package org.secureows.deploy
import java.io.{File,InputStream}
import java.text.MessageFormat.format
import scalax.io.{InputStreamResource}

object PushOp {

  def run(args:Seq[String],config:Configuration):Option[String]={
    assert( args.length > 1, "Two parameters required: <from> <to>" )
    val sourceAlias = args(0)
    val remoteAlias = args(1)
    val force = args.length>2 && args(2).equals("force")
    
    //assert(config.isLocalhost(sourceAlias), "The from alias must be on the localhost")
    
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
    def run = if(config.isLocalhost(remoteAlias)) pushLocal(sourceAlias,remoteAlias,config)
	          else pushRemote(sourceAlias,remoteAlias,config)
    
    if( force ) run
    else {
     val results = ValidateOp.localValidate(ValType.install,sourceAlias,config) 
        
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
  
  def pushLocal(sourceAlias:String,remoteAlias:String,config:Configuration) = {
    error("Local to Local push not implemented")
    None
  }
  def pushRemote(sourceAlias:String,remoteAlias:String,config:Configuration) = {
    val sourceWebAppDir = config.installWebapp(sourceAlias)
    val sourceConfig = config.installConfig(sourceAlias)
    
    val destWebAppDir = new File(config.tmpWebapp(remoteAlias))
    val destConfig = new File(config.tmpConfigDir(remoteAlias))
    
    val login = config.username(remoteAlias)+"@"+config.url(remoteAlias)
    
    println("Starting to copy the application to the remote server")
    println("  Webapp from "+sourceWebAppDir+" to "+destWebAppDir)
    println("  Tomcat config from "+sourceConfig+" to "+destConfig)
    
    val pattern =  
      """ssh {0} "rm -rf {1}"
        |ssh {0} "mkdir -p {1}"
        |ssh {0} "mkdir -p {2}"
        |scp -r {3} {0}:{5}
        |scp -r {4} {0}:{6}""".stripMargin
    
    val scpScript = format( pattern, Array(login, destWebAppDir.getPath, destConfig.getPath, sourceWebAppDir, 
                                           sourceConfig, destWebAppDir.getParent, destConfig.getParent))
    
    var error = false
    def errors(in:InputStreamResource[InputStream]){
      val lines = in.lines.toList
      error = !lines.isEmpty
      if(!lines.isEmpty) System.err.println(lines.mkString("\n"))
    }
    def output(in:InputStreamResource[InputStream]){
      in.lines.foreach(l=>print("."))
    }
    
    ProcessRunner("").error(errors _ ).output(output _).script("/bin/sh",scpScript)
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
  
  def installRemote(remoteAlias:String,config:Configuration) = {
      println("Validation passed.  Backing up and installing copied application")
      println("Final installation directory is: "+config.installWebapp(remoteAlias))
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
