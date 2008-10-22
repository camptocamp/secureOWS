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
       
       val fetchResult = run(parsedArgs(Parser.fetch), FetchOp.run(config)_)
       if( fetchResult.isDefined ){
         println("Fetch FAILED: "+fetchResult.get)
         return
       }
       val validateResult = run(parsedArgs(Parser.validate), ValidateOp.run(config)_)
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
    
    def doPush(config:Configuration)():Option[String]={
      null
    }
    
}
