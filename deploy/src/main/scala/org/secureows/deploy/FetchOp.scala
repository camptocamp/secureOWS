package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object FetchOp {
    def run(args:Seq[String], config:Configuration):Option[String]={
      if(args.length != 1) throw new IllegalArgumentException("fetch requires exactly one argument, the alias. Arguments passed to program were: "+args.mkString(","))

      val alias = args(0)

      if( !config.aliases.contains(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not defined in the configuration file")
      if(!config.isLocalhost(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not on the localhost")
      
      
      
      val appRemoteUrl = appFetchUrl(alias,config)
      val localAppDir = new File(config.tmpAppDir(alias))
      val localWar = new File(config.tmpWar(alias))
      val webapp = new File(config.tmpWebapp(alias))
      val configDir = localAppDir/"configuration"

      if(webapp.exists)      webapp.deleteRecursively
      webapp.mkdirs
      
      println("Downloading owsproxyserver.war")
      InputStreamResource(appRemoteUrl.openStream).pumpTo(localWar.outputStream.buffered )

      println("decompressing war")
      ProcessRunner("unzip",localWar.getAbsolutePath, "-d", webapp.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
  
      checkoutConfigFiles(alias,configDir,config)
  
      Utils.replaceTree(configDir, localAppDir/"tomcat")
      
      InstallOp.run(Array(alias),config)
    }

    private def checkoutConfigFiles(alias:String, configDir:File,config:Configuration) = {
      println("Checking out configuration files")
      var conflict = false
      
      def conflictHandler(in:InputStreamResource[java.io.InputStream]){
        val lines = in.lines.filter(_.contains("C  ")).toList
        if( !lines.isEmpty ) {
          conflict = true
          println("Conflicts occurred:" + lines.mkString("\n"))
        }
      }
      
      if( (configDir).exists ) ProcessRunner("svn","up",configDir.getAbsolutePath).output(conflictHandler _).error(conflictHandler _).run
      else ProcessRunner("svn","co",config.configSvn(alias),configDir.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
      
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
        println ("Do you want to overwrite changes? (y/n)")
        val result = readChar
        if( result.toLowerCase == 'n'){
          return Some("There are uncommitted changes in the installation directory")
        } else None
      } else None
    } 
    private def appFetchUrl(alias:String, config:Configuration) = new java.net.URL(config.downloadUrl(alias))

}
