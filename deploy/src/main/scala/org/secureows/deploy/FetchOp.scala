package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object FetchOp {
    def run(config:Configuration)():Option[String]={
      if(config.arguments.length != 2) throw new IllegalArgumentException("fetch requires exactly two arguments, version and alias. Arguments passed to program were: "+config.arguments)

      val alias = config.arguments(1)

      if( !config.aliases.contains(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not defined in the configuration file")
      if(!config.isLocalhost(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not on the localhost")
      
      val configurationDirs = config.configDirs(alias)++Array(config.installDir(alias)+"WEB-INF")
      val args:Seq[String] = Array("svn","st")++configurationDirs.filter(_.length>0)
      
      var modified = false
      
      def handler(stdOut:InputStreamResource[java.io.InputStream]){
        val lines = stdOut.lines.filter(line => line.contains("M ") || line.contains("? ") || line.contains("! ")).toList
        modified = !lines.isEmpty
        println(lines.mkString("\n"))
      }
      val svnSt = ProcessRunner(args:_*)
      svnSt.output(handler _).run
        
      if( modified ) return Some("There are uncommitted changes in the installation directory")

      val appRemoteUrl = appFetchUrl(config.arguments(0))
      val localAppDir = new File(config.tmpDir(alias)) / "app"
      val localWar = localAppDir / "owsproxyserver.war"
      InputStreamResource(appRemoteUrl.openStream).pumpTo(localWar.outputStream.buffered )
      
      val webapp = localAppDir / "owsproxyserver"
      localWar.unzipTo( webapp )
      
      ProcessRunner.run("svn co ")
      None
      
    }

  def appFetchUrl(version:String) = new java.net.URL("http://www.secureows.org/securewms/war/"+version+"/owsproxyserver.war")

}
