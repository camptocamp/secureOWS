package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object FetchOp {
  val TMP_CONF = "conf"
  val TMP_APPS = "apps"
  
  def run(args:Seq[String], config:Configuration):Option[String]={
      if(args.length != 1) throw new IllegalArgumentException("fetch requires exactly one argument, the alias. Arguments passed to program were: "+args.mkString(","))

      val alias = config.alias(args(0)) 
      
      if(!alias.isLocalhost ) throw new IllegalArgumentException("Alias "+alias.name+" is not on the localhost")
      
      val localAppDir = new File(alias.tmpAppDir)
      val localConfDir = new File(alias.tmpConfigDir)
      val webapp = new File(alias.tmpWebappBaseDir)
      val configDir = localAppDir/"configuration"
      
      val changes = checkConfigModifications(alias)
      if( changes.isDefined ) return changes

      if(webapp.exists)      webapp.deleteRecursively
      assert( webapp.mkdirs, "Unable to make directory: "+webapp+" forced to abort")
      
      alias.fetchStrategy.downloadApp(alias)

      checkoutConfigFiles(alias.name,configDir,config)
      
      if( (configDir/TMP_CONF).listFiles.length == 0 ){
        print("There are no configuration files for the web server.\nCheck "+alias.configSvnConf+"\nAre you sure you want to continue?(y/n) ")
        if( readChar != 'y'  ) return Some("Cancelled by user")
      }
      Utils.replaceTree(configDir/TMP_CONF, localConfDir)

      val showMissingConfigQuery = "true".equals(alias.findOrElse("warnWhenMissingConfFiles", {"true"}))
      for( app <- alias.webapps; configFiles=configDir/TMP_APPS/app; if (configFiles.exists) ) {
        if( showMissingConfigQuery && configFiles.listFiles.length==0 ){
          print("There are no configuration files for the owsproxyserver.\nCheck "+alias.configSvnApp+"\nAre you sure you want to continue?(y/n) ")
          if( readChar != 'y' ) return Some("Cancelled by user")
        }
        println(Utils.replaceTree(configDir/TMP_APPS/app, webapp/app)+" files configuration files were copied for the "+app+" web application")
      }
      
      alias.fetchStrategy.finalConfiguration(alias)
      
      InstallOp.run(Array(alias.name),config)
  }
  

    private def checkoutConfigFiles(aliasName:String, configDir:File,config:Configuration) = {
      println("Checking out configuration files")
      
      Utils.doCheckout(configDir/TMP_CONF,config.configSvnConf(aliasName))
      Utils.doCheckout(configDir/TMP_APPS,config.configSvnApp(aliasName))
    }
    
    private def checkConfigModifications(alias:Alias):Option[String] = {
      val webappBase = alias.installWebappBaseDir
      val configurationDirs = alias.webapps.map( webappBase+_ ) ++ Array(alias.installConfig)
      val processArgs:Seq[String] = Array("svn","st")++configurationDirs.toArray.filter(_.length>0)
      
      println("checking for uncommitted modifications in the configurations");

      var modified = false
      
      def handler(stdOut:InputStreamResource[java.io.InputStream]){
        val test = (line:String) => {
          (line.contains(".xml") && 
             !line.contains("tomcat-users.xml") &&
             !line.contains("WEB-INF/web.xml") &&
             !line.contains("services_test.xml")) && 
            (  line.contains("M ") || 
               line.contains("? ") || 
               line.contains("A ") || 
               line.contains("C ") || 
               line.contains("R ") || 
               line.contains("~ ") || 
               line.contains(" L ") || 
                 line.contains("! "))
        }
        val lines = stdOut.lines.filter(test).toList
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
        if( result.toLowerCase != 'y'){
          return Some("There are uncommitted changes in the installation directory")
        } else None
      } else None
    } 
}
