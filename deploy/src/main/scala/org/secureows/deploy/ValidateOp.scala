package org.secureows.deploy
import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

object ValType extends Enumeration("install","tmp"){
    val install,tmp=Value
    
    def apply(name:String)={
      val found = filter( name.toLowerCase == _.toString)
      if( !found.hasNext ) throw new IllegalArgumentException("The first argument must be one of "+toString)
      else found.next
    }
}
  
object ValidateOp {
    def run(args:Seq[String],config:Configuration):Option[String]={
      // options are tmp or install indicating that the installed app should be validated 
      // or the temporary working directory should be validated
      val valType = ValType(args(0))
      val aliases = args.drop(1)
      
      if( aliases.isEmpty ) throw new IllegalArgumentException("Validate requires at least one alias to validate")
      
      val nonAliases = aliases.filter( arg => !(config.aliases.contains(arg)) )
      
      if( !nonAliases.isEmpty ) throw new IllegalArgumentException("The following aliases are not defined in the configuration file: "+nonAliases.mkString)
      
      config.distributeJars(aliases)
      
      val results = for( alias <- aliases ) yield { 
        println("\nVALIDATING: "+alias)
        val validationResult = if( config.isLocalhost(alias)) localValidate(valType,alias,config)
                               else remoteValidate(valType,alias,config)
        validationResult.toList.sort( (l,r)=> l.id<r.id).head match {
          case Error(s) => s
          case _ => ""
        }
      }
      val failures = results.filter( _.length > 0)

      if( failures.isEmpty ) None
      else Some(failures.mkString(","))
    }
    
    def remoteValidate(valType:ValType.Value,alias:String,config:Configuration)={
      val appJar = config.tmpDir(alias)+config.deployApp.getName
      val configFile = config.tmpDir(alias)+config.configFile.getName
      val javaCMD = "java -cp "+appJar+" org.secureows.deploy.Main -v -c "+configFile+" -j "+appJar+" "+valType+" "+alias
      Remoting.runRemote(alias,javaCMD,config)
    }
    
    def localValidate(valType:ValType.Value,alias:String,config:Configuration)={
      val dir = valType match {
        case ValType.install => config.installWebapp(alias)
        case ValType.tmp => config.tmpWebapp(alias)
      }
      Validation.validate(new File(dir) )
    }

}
