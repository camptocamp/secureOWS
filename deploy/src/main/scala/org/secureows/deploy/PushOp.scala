package org.secureows.deploy

import org.secureows.deploy.validation._
import java.io.{File,InputStream}
import java.text.MessageFormat.format
import scalax.io.{InputStreamResource}
import scalax.io.Implicits._

object PushOp {

  def run(args:Seq[String],config:Configuration):Option[String]={
    assert( args.length > 1 && args.length < 4, "Two parameters required: <from> <to>" )
    val sourceAlias = args(0)
    val remoteAlias = args(1)
    val force = args.length>2 && args(2).equals("force")
    
    if( config.isLocalhost(sourceAlias)) doPush(sourceAlias, remoteAlias, force, config) 
    else runRemote(sourceAlias, remoteAlias, config)
  }
  
  def runRemote(sourceAlias:String, remoteAlias:String, config:Configuration):Option[String]={
    
    def run = {
	    val appJar = config.tmpDir(sourceAlias)+config.deployApp.getName
	    val configFile = config.tmpDir(sourceAlias)+config.configFile.getName
	    val cmd= "java -cp "+appJar+" org.secureows.deploy.Main -p -c "+configFile+" -j "+appJar+" "+sourceAlias+" "+remoteAlias+" force"
	      
	    val results = Remoting.runRemote(sourceAlias,cmd,config)
	    results.toList.sort( (l,r)=> l.id<r.id).head match {
	      case Error(s) => Some(s)
	      case _ => None
	    }
    }
    
     val results = ValidateOp.remoteValidate(ValType.install,sourceAlias,config) 
        
      results.toList.sort( (l,r)=> l.id<r.id).head match {
	  case Error(s) => Some(s)
	  case Warning(s)  => {
	      print("There were warnings in the configuration.  Do you wish to install?(y/n) ")
          if ( readChar == 'y'){
            run
          }else{
            Some("Aborted by user")
          }
	  }
      case _ => run
      }
    
  }
  
  def doPush(sourceAlias:String, remoteAlias:String, force:Boolean,config:Configuration):Option[String]={
    def run = if(config.isLocalhost(remoteAlias)) pushLocal(config.alias(sourceAlias),config.alias(remoteAlias),config)
	          else pushRemote(sourceAlias,remoteAlias,config)
    
    if( force ) run
    else {
     val results = ValidateOp.localValidate(ValType.install,config.alias(sourceAlias)) 
        
      results.toList.sort( (l,r)=> l.id<r.id).head match {
	  case Error(s) => Some(s)
	  case Warning(s)  => {
	      print("There were warnings in the configuration.  Do you wish to install?(y/n) ")
          if ( readChar == 'y'){
            run
          }else{
            Some("Aborted by user")
          }
	  }
      case _ => run
      }
    }
  }
  
  def pushLocal(from:Alias,to:Alias,config:Configuration) = {
    BackupOp.run(Array(to.name), config)
    val baseFrom = from.installWebappBaseDir 
    val baseTo = from.installWebappBaseDir 

    val results = for( app <- from.webapps ) yield {
      val fromDir = new File(baseFrom+app)
      val toDir = new File(baseTo+app)
      toDir.deleteRecursively
      Utils.copyTree(fromDir,toDir)
      None
    }

    Utils.copyTree(new File(from.installConfig),new File(to.installConfig))
    results.find( _.isDefined ).getOrElse( None )
  }
  def pushRemote(sourceAlias:String,remoteAlias:String,config:Configuration) = {
    val sourceWebAppBaseDir = config.installWebappBaseDir(sourceAlias)
    val sourceConfig = config.installConfig(sourceAlias)
    
    val destWebAppBaseDir = new File(config.tmpWebappBaseDir(remoteAlias))
    val destConfig = new File(config.tmpConfigDir(remoteAlias))
    
    val login = config.username(remoteAlias)+"@"+config.url(remoteAlias)
    println("Starting to copy the application to the remote server")
    
    
    val pattern =  
      """ssh {0} "rm -rf {1}"
        |ssh {0} "mkdir -p {1}"
        |scp -r {2} {0}:{3}
""".stripMargin
        
    var error = false
    def errors(in:InputStreamResource[InputStream]){
      val lines = in.lines.toList
      error = !lines.isEmpty
      if(!lines.isEmpty) System.err.println(lines.mkString("\n"))
    }
    def output(in:InputStreamResource[InputStream]){
      in.lines.foreach(l=>print("."))
    }
    
    for( app <- config.webapps; if(!error) ) {
      val sourceWebAppDir = new File(sourceWebAppBaseDir,app)
      val destWebAppDir = new File(destWebAppBaseDir,app)
      
      println("  Webapp from "+sourceWebAppDir+" to "+destWebAppDir)
      val scpScript = format( pattern, login, destWebAppDir.getPath, sourceWebAppDir, 
                              destWebAppDir.getParent)
      ProcessRunner("").error(errors _ ).output(output _).script("/bin/sh",scpScript)
    }

    if( error ){
      Some("Error occurred while copying files to remote server:"+remoteAlias)
    } else{
      println("  Tomcat config from "+sourceConfig+" to "+destConfig)
      val copyConfigScript = format( pattern, login, destConfig.getPath, 
                                     sourceConfig, destConfig.getParent)

      ProcessRunner("").error(errors _ ).output(output _).script("/bin/sh",copyConfigScript)
      if( error ){
        Some("Error occurred while copying files to remote server:"+remoteAlias)
      }else{
        println ("Done copying to remote server... starting to validate the remote server")
        Remoting.distributeJars(Array(remoteAlias),config)
        
        val results = ValidateOp.remoteValidate(ValType.tmp,remoteAlias,config)   
        results.toList.sort( (l,r)=> l.id<r.id).head match {
          case Error(s) => Some("Error validating configuration copied to remote server: \n  "+s) 
          case _ => installRemote(remoteAlias,config)
        }
      }
    }
  }
  
  def installRemote(remoteAlias:String,config:Configuration) = {
      println("Validation passed.  Backing up and installing copied application")
      println("Final installation directory is: "+config.installWebappBaseDir(remoteAlias))
      println(" and "+config.installConfig(remoteAlias))
      val appJar = config.tmpDir(remoteAlias)+config.deployApp.getName
      val configFile = config.tmpDir(remoteAlias)+config.configFile.getName
      val javaCMD = "java -cp "+appJar+" org.secureows.deploy.Main -i -c "+configFile+" -j "+appJar+" "+remoteAlias+" remote"
      val results = Remoting.runRemote(remoteAlias, javaCMD,config)
      results.toList.sort( (l,r)=> l.id<r.id).head match {
        case Error(s) => Some(s) 
        case _ => println("Installation succeeded"); None
      }
  }
  
}
