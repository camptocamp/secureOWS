package org.secureows.deploy
import scalax.io.InputStreamResource

object Remoting {
  def runRemote(alias:String,cmd:String,config:Configuration) = {
      val login = config.username(alias)+"@"+config.url(alias)
      var result:List[Result] = Nil
      def handler (stream:InputStreamResource[java.io.InputStream]) {
        val lines = stream.lines.toList
        val tmp = for( l <- lines ) yield {
          if(l.contains("FAILED")) Error(lines.mkString("\n"))
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
}
