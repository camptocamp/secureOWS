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
	      
            val results = Remoting.runRemote(config.alias(sourceAlias),cmd)
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
        val source = config.alias(sourceAlias)
        val remote = config.alias(remoteAlias)
        def run = if(config.isLocalhost(remoteAlias)) pushLocal(source,remote,config)
        else pushRemote(source,remote, config)
    
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
        val baseTo = to.installWebappBaseDir
    
        InstallOp.stopServer(to);
    
        val results = for( app <- from.webapps ) yield {
            val fromDir = new File(baseFrom+app)
            val toDir = new File(baseTo+app)
            val warFile = new File(baseTo+app+".war")
      
            println( "Pushing '"+app+"' to '"+from.name+"'")
            toDir.deleteRecursively()
            warFile.delete()
      
            Utils.zipDir(fromDir, warFile)
            None
        }

        Utils.replaceTree(new File(from.installConfig),new File(to.installConfig))
    
        InstallOp.startServer(to);
    
        results.find( _.isDefined ).getOrElse( None )
    }

    def pushRemote(sourceAlias:Alias,remoteAlias:Alias,config:Configuration):Option[String] = {
        InstallOp.stopServer(remoteAlias)

        val result = run(remoteAlias,sourceAlias.installConfig,remoteAlias.installConfig)
        if( result.isDefined) return result

        for{app <- config.webapps}
        {
            Remoting.runRemote(remoteAlias, "rm -rf "+remoteAlias.installWebappBaseDir+app)
            Remoting.runRemote(remoteAlias, "rm -rf "+remoteAlias.installWebappBaseDir+app+".war")
            val result = run(remoteAlias,sourceAlias.installWebappBaseDir+app+".war",remoteAlias.installWebappBaseDir)
            if( result.isDefined) return result
        }
        println("Starting to copy the application to the remote server")
        InstallOp.startServer(remoteAlias)
        None
    }

     private[this] def run(alias:Alias,from:String,to:String)={
        import Remoting.{handleErrors,error}
        error = null
        val login = alias.username+"@"+alias.url
        val params = Array("scp","-r",from,login+":"+to)
        val outputHandler = (stream:InputStreamResource[InputStream])=>println (stream.lines.filter(_.trim().length>0).mkString("\n"))
        ProcessRunner(params:_*).error( handleErrors _ ).output( outputHandler ).log.run
        if( error!=null ) Some(error)
        else None
     }
}