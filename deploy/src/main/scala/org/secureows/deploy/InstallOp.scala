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
    
    val webapp = new File(alias.tmpWebapp)
    val tmpConf = new File(alias.tmpConfigDir)

    println( "Validating the new configuration" )
    val results = ValidateOp.validate( alias, webapp )
    val mostSevere = results.toList.sort( (l,r)=> l.id<r.id).head
    mostSevere match {
	  case Error(s) => Some(s)
	  case Warning(s) if(!remote)=> {
	      print("There were warnings in the configuration.  Do you wish to install?(y/n) ")
          if ( readChar == 'y'){
            doInstall(alias, webapp, tmpConf,config)
          }else{
            Some("Aborted by user")
          }
	  }
      case _ => doInstall(alias, webapp, tmpConf,config)
    }
  }
  private def doInstall(alias:Alias, webapp:File, tmpConf:File,config:Configuration):Option[String]={
    
	    val installation=new File(alias.installWebapp)
	    val installConf = new File(alias.installConfig)
	    val bin = installConf / "../bin"
	    val env = Map("JAVA_HOME"->System.getProperty("java.home"))
	      
	    println( "Backing up old version" )
	    BackupOp.run( Array(alias.alias),config )
	    println("Shutting down server")
        try{
		    ProcessRunner(bin.getAbsolutePath+"/shutdown.sh").error(_.lines.toList).output(_.lines.toList).env(env).run
	    }catch{
	       case _ => // ignore
	    }
	    Thread.sleep(3000)
	      
	    println("Copying temporary installation to active installation")
	    installation.deleteRecursively
	    installConf.deleteRecursively
	    Utils.replaceTree(webapp,installation)
	    Utils.replaceTree(tmpConf,installConf)
	
	    println("Starting server")
        try{
          ProcessRunner(bin.getAbsolutePath+"/startup.sh").env(env).run
	    }catch{
	       case _ => println("WARNING:  There was an error starting the server, you may be required to do so manually" )
	    }
	    None
  } 
}
