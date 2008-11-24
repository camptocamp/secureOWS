package org.secureows.deploy

import org.secureows.deploy.validation._
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object InstallOp {

  // args is just an alias if local if remote there must be a second arg that is remote.  This is
  // to determine if there should be interaction
  def run(args:Seq[String], config:Configuration):Option[String] = {
    val alias = config.alias(args(0))
    val remote = args.length==2 && args(1)=="remote"
    
    val tmpConf = new File(alias.tmpConfigDir)
    val results = ValidateOp.localValidate( ValType.install, alias )
        println( "Validating the new configuration" )
        val mostSevere = results.toList.sort( (l,r)=> l.id<r.id).head
        mostSevere match {
    	  case Error(s) => Some(s)
    	  case Warning(s) if(!remote)=> {
    	      print("There were warnings in the configuration.  Do you wish to install?(y/n) ")
              if ( readChar == 'y'){
                doInstall(alias, tmpConf,config)
              }else{
                Some("Aborted by user")
              }
    	  }
          case _ => doInstall(alias, tmpConf,config)
        }
  }
  private def doInstall(alias:Alias, tmpConf:File,config:Configuration):Option[String]={
    
    val fromAppBaseDir = new File(alias.tmpWebappBaseDir)
    val installAppsBaseDir=new File(alias.installWebappBaseDir)
    val installConf = new File(alias.installConfig)
    val bin = installConf / "../bin"
    val env = Map("JAVA_HOME"->System.getProperty("java.home"))
    
    println( "Backing up old version" )
    BackupOp.run( Array(alias.name),config )
    println("Shutting down server")
    try{
      ProcessRunner(bin.getAbsolutePath+"/shutdown.sh").error(_.lines.toList).output(_.lines.toList).env(env).run
    }catch{
      case _ => // ignore
    }
    Thread.sleep(3000)
    
    Utils.replaceTree(tmpConf,installConf)
    
    for( app <- alias.webapps ) {
      println("Copying temporary "+app+" installation to active installation")
      val appDir = installAppsBaseDir/app
      appDir.deleteRecursively
      Utils.replaceTree(fromAppBaseDir/app,appDir)
    }	
    println("Starting server")
    try{
      ProcessRunner(bin.getAbsolutePath+"/startup.sh").env(env).run
    }catch{
      case _ => println("WARNING:  There was an error starting the server, you may be required to do so manually" )
    }
    None
  } 		
}	
