package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object Parser extends CommandLineParser {
  val testing = new Flag('t',"testing","This flag is for testing the commandline parser") with AllowAll
  val config = new StringOption('c',"config","The path to the configuration file") with AllowAll
  val jar = new StringOption('j',"jar","The path to the jar file being executed") with AllowAll
  val fetch = new Flag('f',"fetch","Indicates that the fetch behaviour will be performed") with AllowAllButSelf
  val validate = new Flag('v',"validate","Indicates that the validate behaviour will be performed") with AllowAllButSelf
  val push = new Flag('p',"push","Indicates that the push behaviour will be performed") with AllowAllButSelf
  
  override def helpHeader = """
         |Deployment Tool v1.0
         |""".stripMargin

}

object Main {
  def main(args:Array[String]) {
    Parser.parseOrHelp(args) { parsedArgs =>
       
       val configFile = parsedArgs(Parser.config) match {
         case Some(f) => new File(f)
         case None => throw new IllegalArgumentException("The configuration file must be specified")
       }
       
       if(!configFile.exists) throw new IllegalArgumentException(configFile+" does not exist")
       
       val deployApp = new File(parsedArgs(Parser.jar).get)
       val otherArgs = parsedArgs.nonOptions
       val config = new Configuration(configFile,otherArgs,deployApp)
       
       def run( run:Boolean, action: ()=> Option[String])={
         if( run && !parsedArgs(Parser.testing) ){
           action()
         }else None
       }
       
       val fetchResult = run(parsedArgs(Parser.fetch), doFetch(config)_)
       if( fetchResult.isDefined ){
         println("Fetch FAILED: "+fetchResult.get)
         return
       }
       val validateResult = run(parsedArgs(Parser.validate), doValidate(config)_)
       if( validateResult.isDefined ){
         println("Validate FAILED: "+validateResult.get)
         return
       }
       val pushResult = run(parsedArgs(Parser.push), doPush(config)_)
       if( pushResult.isDefined ){
         println("Push FAILED: "+pushResult.get)
         return
       }
       
     }
  }
    
    def doFetch(config:Configuration)():Option[String]={
      if(config.arguments.length != 2) throw new IllegalArgumentException("fetch requires two arguments")
      
      val alias = config.arguments(1)
      
      if(! config.isLocalhost(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not on the localhost")
      
      val configurationDirs = config.configDirs(alias)++Array(config.installDir(alias)+"WEB-INF")
      val args:Seq[String] = Array("svn","st")++configurationDirs
      
      var modified = false
      
      def handler(stdOut:InputStreamResource[java.io.InputStream]){
        val lines = stdOut.lines.filter(line => line.contains("M ") || line.contains("? ") || line.contains("! ")).toList
        modified = !lines.isEmpty
        println(lines.mkString("\n"))
      }
      val svnSt = ProcessRunner(args:_*)
      svnSt.output(handler _).run
        
      if( modified ) return Some("There are uncommitted changes in the installation directory")
      else return None
        
    }
    
    def doValidate(config:Configuration)():Option[String]={
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
    
    def doPush(config:Configuration)():Option[String]={
      null
    }
    
}
