package org.secureows.deploy

import org.secureows.deploy.validation._
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
      
      Remoting.distributeJars(aliases,config)
      
      val results = for( aliasName <- aliases ) yield {
        val alias = config.alias(aliasName) 
        val validationResult = if( alias.isLocalhost) localValidate(valType,alias)
                               else remoteValidate(valType,aliasName,config)
        validationResult.toList.sort( (l,r)=> l.id<r.id).head match {
          case Error(s) => s
          case _ => println("Validation successful");""
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
      Remoting.runRemote(config.alias(alias),javaCMD)
    }
    
    def localValidate(valType:ValType.Value,alias:Alias)={
      println("\nVALIDATING: "+alias)
      val dirs = valType match {
        case ValType.install => alias.webapps.map ( _ + alias.installWebappBaseDir)
        case ValType.tmp => alias.webapps.map ( _+alias.tmpWebappBaseDir)
      }
      val results = for ( dir <- dirs ) yield {
        validate(alias, new File(dir))
      }
      results.flatMap(r => r)
    }
    
    def validate(alias:Alias, dir:File) = { 
      val result = alias.validators.filter( _.validFor(dir) ).flatMap( _.validate(dir) )
      if (result.isEmpty) Array(Good)
      else result
    }

}
