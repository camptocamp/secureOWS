package org.secureows.deploy


import Remoting.{handleErrors,error}
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
    
        println( "Backing up old version" )
        BackupOp.run( Array(alias.name),config )
        stopServer(alias)
    
        Utils.replaceTree(tmpConf,installConf)
    
        for( app <- alias.webapps ) {
            println("Copying temporary "+app+" installation to active installation")
            val appDir = installAppsBaseDir/app
            val warFile = installAppsBaseDir/(app+".war")
            assert (!appDir.exists || appDir.deleteRecursively(), "unable to delete"+warFile)
            assert (!warFile.exists || warFile.delete(), "unable to delete "+warFile)

            Utils.zipDir(fromAppBaseDir/app, warFile)
        }

        startServer(alias)

        performPostActions(alias)
    }

    def performPostActions(alias:Alias):Option[String]={
        alias.logger.debug("postActions to run: "+alias.postAction.mkString)
        alias.postAction.foldLeft(None:Option[String]){
            (error,func) =>
                error match {

                case None =>alias.logger.debug("Running postAction function "+func);func(alias)
                case some => some
            }
        }
    }

    lazy val env = Map("JAVA_HOME"->System.getProperty("java.home"))
 
    def stopServer(alias:Alias) {
        println("Shutting down server")
        controlServer(alias, alias.shutdown)
        Thread.sleep(alias.findOrElse("serverShutdownWait", "10000").toInt)
    }

    private[this] def controlServer(alias:Alias, cmd:String):Option[String] ={
        error = null
        try{
            val runner = ProcessRunner().error(handleErrors _).log
            val params = if( alias.isLocalhost ) cmd.split(" ")
            else{
                val login = alias.username+"@"+alias.url
                Array("ssh",login)++cmd.split(" ")
            }
            if (runner(params:_*) != 0 ){
                error = "Unexpected error occurred while running: "+params.mkString(", ")
            }
        }catch{
            case e => error = "Unexpected error occurred while running: "+cmd+": "+e
        }
    
        if(error!=null) Some(error)
        else None

    }
  
    def startServer(alias:Alias):Option[String]={
        println("Starting server")
        val ret=controlServer(alias, alias.startup)
        Thread.sleep(alias.findOrElse("serverStartupWait", "10000").toInt)

        ret
    }
}
