package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object FetchOp {
  val TMP_APP = "owsproxyserver"
  val TMP_CONF = "conf"
  
  def run(args:Seq[String], config:Configuration):Option[String]={
      if(args.length != 1) throw new IllegalArgumentException("fetch requires exactly one argument, the alias. Arguments passed to program were: "+args.mkString(","))

      val alias = args(0)

      if( !config.aliases.contains(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not defined in the configuration file")
      if(!config.isLocalhost(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not on the localhost")
      
      
      val appRemoteUrl = appFetchUrl(alias,config)
      val localAppDir = new File(config.tmpAppDir(alias))
      val localConfDir = new File(config.tmpConfigDir(alias))
      val localWar = new File(config.tmpWar(alias))
      val webapp = new File(config.tmpWebapp(alias))
      val configDir = localAppDir/"configuration"

      if(webapp.exists)      webapp.deleteRecursively
      webapp.mkdirs
      
      println("Downloading "+appRemoteUrl)
      InputStreamResource(appRemoteUrl.openStream).pumpTo(localWar.outputStream.buffered )

      println("decompressing "+localWar.getAbsolutePath)
      ProcessRunner("unzip",localWar.getAbsolutePath, "-d", webapp.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
  
      checkoutConfigFiles(alias,configDir,config)
      
      if( (configDir/TMP_CONF).listFiles.isEmpty ){
        print("There are no configuration files for the web server.\nCheck "+config.configSvnConf(alias)+"\nAre you sure you want to continue?(y/n) ")
        if( readChar != 'y'  ) return Some("Cancelled by user")
      }
      if( (configDir/TMP_APP).listFiles.isEmpty ){
        print("There are no configuration files for the owsproxyserver.\nCheck "+config.configSvnApp(alias)+"\nAre you sure you want to continue?(y/n) ")
        if( readChar != 'y' ) return Some("Cancelled by user")
      }

      (webapp/"WEB-INF").listFiles.filter( _.isFile).filter( _.getName.endsWith(".xml")).foreach( _.delete)
      
      Utils.replaceTree(configDir/TMP_CONF, localConfDir)
      Utils.replaceTree(configDir/TMP_APP, webapp)
      
      generateWebXml(webapp)
      
      InstallOp.run(Array(alias),config)
    }
  
  def generateWebXml(webapp:File){
    ProcessRunner("java","-cp","WEB-INF/classes","OwsAdmin", "./WEB-INF/").dir(webapp).run
  }

    private def checkoutConfigFiles(alias:String, configDir:File,config:Configuration) = {
      println("Checking out configuration files")

      def doCheckout(dir:File, url:String){
          dir.deleteRecursively
          dir.mkdirs
	      ProcessRunner("svn","co",url,dir.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
      }
      
      doCheckout(configDir/TMP_CONF,config.configSvnConf(alias))
      doCheckout(configDir/TMP_APP,config.configSvnApp(alias))
    }
    
    private def checkConfigModifications(alias:String, config:Configuration):Option[String] = {
            
      val configurationDirs = Array(config.installWebapp(alias)+"WEB-INF", config.installConfig(alias))
      val processArgs:Seq[String] = Array("svn","st")++configurationDirs.filter(_.length>0)
      
      var modified = false
      
      def handler(stdOut:InputStreamResource[java.io.InputStream]){
        val lines = stdOut.lines.filter(line => (line.contains(".xml") && !line.contains("services_test.xml"))&& (line.contains("M ") || line.contains("? ") || line.contains("! "))).toList
        modified = !lines.isEmpty
        if(modified){
          println("There are uncommitted configuration changes")
          println(lines.mkString("\n"))
        }
      }
      val svnSt = ProcessRunner(processArgs:_*)
      svnSt.output(handler _).run
        
      if( modified ) {
        print ("Do you want to overwrite changes?(y/n) ")
        val result = readChar
        if( result.toLowerCase == 'n'){
          return Some("There are uncommitted changes in the installation directory")
        } else None
      } else None
    } 
    private def appFetchUrl(alias:String, config:Configuration) = new java.net.URL(config.downloadUrl(alias))

}
