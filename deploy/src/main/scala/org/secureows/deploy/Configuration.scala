package org.secureows.deploy
import java.io.{FileInputStream,File}
import java.net.{URL,InetAddress}
import scalax.io.Implicits._
import java.text.MessageFormat.format
import org.secureows.deploy.validation.Validator
import org.secureows.deploy.fetch.FetchStrategy

class Configuration(val configFile:File, val deployApp:File) {
  
  private[deploy] val elements = Map[String,String]( loadProperties.toSeq:_* )
  val aliases = createAliases()
  private[this] val localhost = findLocalHost()
  // ----  End of construction ----
  
  // ---- Start of API
  val maxBackups = {
    val backups = elements("maxBackups")
    if( backups == null ) throw new IllegalArgumentException("maxBackups is not defined in the configuration file")
    if( backups.toInt < 0 ) throw new IllegalArgumentException("maxBackups must be a positive number")
    backups.toInt
  } 
  
  def alias(alias:String) = {
    if( !aliases.contains(alias) ) throw new IllegalArgumentException("Alias "+alias+" is not defined in the configuration file")

    Alias(alias, url(alias), configSvnApp(alias), configSvnConf(alias), downloadUrl(alias),
          installWebappBaseDir(alias), webapps, installConfig(alias), tmpDir(alias),
          backupDir(alias), username(alias), tmpAppDir(alias), tmpWebappBaseDir(alias),
          tmpConfigDir(alias), shutdown(alias), startup(alias),this)
  }
  
  def asURL(name:String) = new URL("http://"+aliases(name))

  def configSvnApp(alias:String):String = find(alias, "configSvnApp")
  def configSvnConf(alias:String):String = find(alias, "configSvnConf")
  def downloadUrl(alias:String):String = find(alias,"downloadUrl")
  def url(name:String):String = aliases(name)
  def installConfig(alias:String):String = assureDir( find(alias,"installConfig") )
  def tmpDir(alias:String):String = assureDir( find(alias,"tmpDir") )
  def backupDir(alias:String):String = assureDir( find(alias,"backupDir") )
  def username(alias:String):String = find(alias,"username")
  def tmpAppDir(alias:String) = tmpDir(alias) + "server/"
  def tmpWebappBaseDir(alias:String):String = assureDir(tmpAppDir(alias) + "tomcat/webapps/")
  def tmpConfigDir(alias:String):String = tmpAppDir(alias) + "tomcat/conf/"
  def installWebappBaseDir(alias:String):String = assureDir( find(alias,"installWebapp") )
  def shutdown(alias:String) = find(alias,"shutdown")
  def startup(alias:String) = find(alias,"startup")
  
  def webapps:Iterable[String] =  {
    val decl = elements("apps")
    assert (decl != null, "the configurations file must declare a 'apps' property")
    decl.split(",")
  }
  def isLocalhost(alias:String) = localhost.contains(alias)
  
  lazy val validators:Seq[Validator] = {
    val asString = elements("validators")
    if( asString == null || asString.trim().length==0 ){
      Seq[Validator]()
    } else {
      val classNames = asString.split(",").map( _.trim )
      for( name <- classNames ) yield {
        val c = classOf[Configuration].getClassLoader.loadClass( name )
        c.newInstance().asInstanceOf[Validator]
      }
    }
  }

  lazy val fetchStrategy:FetchStrategy = {
    val asString = elements("fetchStrategy")
    assert( asString != null && asString.trim().length>0, "the 'fetchStrategy' property is not defined" )
      val c = classOf[Configuration].getClassLoader.loadClass( asString )
      c.newInstance().asInstanceOf[FetchStrategy]
  }

  lazy val postAction:Option[Function[Alias,Option[String]]] = function("postAction")

  def function(name:String):Option[Function[Alias,Option[String]]] = {
    if( elements.contains(name)){
        val asString = elements(name)
        assert( asString.trim().length>0, "the '"+name+"' property is defined but empty" )
        val c = classOf[Configuration].getClassLoader.loadClass( asString )

     Some(c.newInstance().asInstanceOf[Function[Alias,Option[String]]])
    }
    else None
  }

  // ---- End of API
    private[this] def findLocalHost() = {
    val local = new URL("http://"+InetAddress.getLocalHost().getHostName())
    val localhost = new URL("http://localhost")
    val localIp = new URL("http://127.0.0.1")
    
    aliases.keys.filter{ alias => 
      val aliasUrl = new URL("http://"+url(alias))
      aliasUrl== local || aliasUrl == localIp || aliasUrl == localhost
    }.toList
  }
  private[this] def createAliases() = {
    val aliases = elements("aliases")
    val parts = aliases.split(",")
    val entries = for( alias <- parts ) yield  {
      val parts = alias.split("->")
      if( parts.length != 2 ) {
        throw new IllegalArgumentException("'"+aliases+"' is not correctly formatted.  It must be alias -> url, alias2 -> url2, etc..." )
      }
      
      (parts(0).trim,parts(1).trim)
    }
    
    Map(entries.toSeq:_*)
    
  }
  
  private[this] def loadProperties = {
    
    def resolve(map:Map[String,String]):Map[String,String] = {
        val patternString = ".*\\$\\{([^\\}]+)\\}.*"
	    val replacePattern = java.util.regex.Pattern.compile(patternString)
	    val result = for( (key,value) <- map ) yield {
	            val matcher = replacePattern.matcher( value )
	
	            if( matcher.matches ){
	              val substitute = map.getOrElse( matcher.group(1), 
                         {
                            throw new IllegalArgumentException(
                              "Property "+key+
                                " contains ${"+matcher.group(1)+
                                "} which cannot be resolved")
                            ""
                         } )
               
	              val newVal = value.replace( "${"+matcher.group(1)+"}", substitute )
	              (key, newVal)
	            }else{
	              (key,value)
	            }
	            
	      }
        if( result.exists( e=> e._2.matches(patternString)) ) resolve(Map(result.toSeq:_*))
        else Map(result.toSeq:_*)
	    
	  }
    
    import scala.collection.jcl.Conversions._
    val props = new java.util.Properties()
    props.load(new FileInputStream(configFile))
    
    
    resolve(Map(props.map( e=> (e._1.asInstanceOf[String],e._2.asInstanceOf[String])).toSeq:_*) )
  }
  
  private[deploy] def assureDir(dir:String) = if(dir.endsWith("/")||dir.length==0) dir else dir+"/"
  
  private[deploy] def find(alias:String,base:String) = {
    if(!aliases.contains(alias)) throw new IllegalArgumentException(alias+" is not a listed alias in the configuration file")
    
    val defaultKey = base.take(1).toLowerCase+base.drop(1)
    val extensionKey = base.take(1).toUpperCase+base.drop(1)
    
    if( elements.contains(alias+extensionKey)) elements(alias+extensionKey)
    else if(elements.contains(defaultKey)) elements(defaultKey)
    else throw new IllegalArgumentException("There is no value for "+defaultKey+" or "+alias+extensionKey+" in the configuration file")
  }

}
