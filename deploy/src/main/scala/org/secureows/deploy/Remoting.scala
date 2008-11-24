package org.secureows.deploy

import org.secureows.deploy.validation._
import scalax.io.InputStreamResource

object Remoting {
    def distributeJars(destinations:Iterable[String],config:Configuration) {
    val destAliases = destinations.filter( alias => (!config.isLocalhost(alias) &&  config.aliases.keys.contains(alias)))
    if( !destAliases.isEmpty ){
      val script = destAliases.flatMap[String] (a=> copyJarScript(a,config) ).mkString("\n")
      ProcessRunner.script("/bin/sh",script)
    }
  }
    
  def runRemote(alias:String,cmd:String,config:Configuration) = {
      val login = config.username(alias)+"@"+config.url(alias)
      var result:List[Result] = Nil
      def handler (stream:InputStreamResource[java.io.InputStream]) {
        val lines = stream.lines.toList
        val tmp = for( l <- lines ) yield {
          if(l.contains("FAILED") || l.contains("Exception in thread")) Error(lines.mkString("\n"))
          else if(l.contains("WARNING")) Warning(lines.mkString("\n"))
          else Good
          } 
        result = tmp ::: result 
        println(lines.mkString("\n"))
      }

      val process = ProcessRunner("ssh",login,cmd).
        output(handler _).
        error(handler _)
      
      process.run

      result
  }
  
    
  private[this] def copyJarScript(alias:String,config:Configuration)={
      val server = config.username(alias)+"@"+config.url(alias)
      val tmp = server+":"+config.tmpDir(alias)
      val destJar = tmp +config.deployApp.getName
      val destConfig = tmp+config.configFile.getName

      val mkDir = "ssh "+server+" \"mkdir -p "+config.tmpDir(alias)+"\""
      val copyJar = "scp "+config.deployApp.getAbsolutePath+" "+tmp
      val copyConfig = "scp "+config.configFile.getAbsolutePath+" "+destConfig
      List(mkDir,copyJar,copyConfig)
    }

}
