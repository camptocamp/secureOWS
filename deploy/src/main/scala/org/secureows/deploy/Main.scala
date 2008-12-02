package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object Parser extends CommandLineParser {
  val testing = new Flag('t',"testing","This flag is for testing the commandline parser") with AllowAll
  val config = new StringOption('c',"config","The path to the configuration file") with AllowAll
  val jar = new StringOption('j',"jar","The path to the jar file being executed") with AllowAll

  var ops:List[(Flag,(Seq[String],Configuration)=>Option[String])] = Nil

  // operations in the reverse order that they will be executed if all are present
  val push = addOp('p',"push","Indicates that the push behaviour will be performed",PushOp.run _)
  val install = addOp('i',"install","Indicates that the push behaviour will be performed",InstallOp.run _)
  val backup = addOp('b',"backup","Indicates that the push behaviour will be performed", BackupOp.run _)
  val validate = addOp('v',"validate","Indicates that the validate behaviour will be performed",ValidateOp.run _)
  val fetch = addOp('f',"fetch","Indicates that the fetch behaviour will be performed", FetchOp.run _)
  val clean = addOp('C',"clean","cleans out the last install", CleanOp.run _)
  val run = addOp('R',"run", "Run a function defined in the properties.  The first parameter must be the name of the property which identifies a Function[Alias,Option[String]]", RunOp.run _)
  
  def addOp(short:Char, long:String, desc:String, op:(Seq[String],Configuration)=>Option[String]) = {
    val flag = new Flag( short,long,desc ) with AllowAllButSelf
    ops = (flag,op) :: ops
    flag
  }
  
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
       val config = new Configuration(configFile,deployApp)
       
       val testing = parsedArgs(Parser.testing)
       
       var failure = false 
       for{
         (flag,op) <- Parser.ops
         if (parsedArgs(flag) && !failure && !testing) 
       }{
         val result = op( otherArgs, config )
         if( result.isDefined ){
           val name = flag.longName.take(1).toUpperCase+flag.longName.drop(1)
           println(name+" FAILED:\n  "+result.get.replaceAll("[\\n\\r]","\n>  "))
           failure = true
         }
       }
       
     }
    
  }
    
    def doPush(config:Configuration)():Option[String]={
      null
    }
    
}
