package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object ValidateOp {

    def run(config:Configuration)():Option[String]={
      val aliases = config.arguments
      
      if( aliases.isEmpty ) throw new IllegalArgumentException("Validate requires at least one alias to validate")
      
      val nonAliases = aliases.filter( arg => !(config.aliases.contains(arg)) )
      
      if( !nonAliases.isEmpty ) throw new IllegalArgumentException("The following aliases are not defined in the configuration file: "+nonAliases.mkString)
      
      config.distributeJars(aliases)
      
      val results = for( alias <- aliases) yield { 
        println("Validating alias: "+alias)
        if( config.isLocalhost(alias)) localValidate(alias,config)
        else remoteValidate(alias,config)
      }
      results.find( _.isDefined ) match {
        case Some(s) => s
        case None => None
      }
    }
    
    def remoteValidate(alias:String,config:Configuration)={
      val login = config.username(alias)+"@"+config.url(alias)
      val appJar = config.tmpDir(alias)+config.deployApp.getName
      val configFile = config.tmpDir(alias)+config.configFile.getName
      val javaCMD = "java -cp "+appJar+" org.secureows.deploy.Main -v -c "+configFile+" -j "+appJar+" "+alias

      var result:Option[String] = None
      def handler (stream:InputStreamResource[java.io.InputStream]) {
        val lines = stream.lines.toList
        if(lines.find( l => l.contains("FAILED")).isDefined) result = Some(lines.mkString("\n"))
      }

      val process = ProcessRunner("ssh",login,javaCMD).
        output(handler _).
        error(handler _)
      process.run

      result
    }
    
    def localValidate(alias:String,config:Configuration)={
        Validation.validate(new File(config.installDir(alias)) )
    }
    
}
