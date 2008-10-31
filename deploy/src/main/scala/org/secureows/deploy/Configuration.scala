package org.secureows.deploy
import java.io.{FileInputStream,File}
import java.net.{URL,InetAddress}
import scalax.io.Implicits._
import java.text.MessageFormat.format

class Configuration(val configFile:File, val deployApp:File) {
  
  private[this] val elements = Map[String,String]( loadProperties.toSeq:_* )
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
  def asURL(name:String) = new URL("http://"+aliases(name))

  def configSvnApp(alias:String):String = find(alias, "configSvnApp")
  def configSvnConf(alias:String):String = find(alias, "configSvnConf")
  def downloadUrl(alias:String):String = find(alias,"downloadUrl")
  def url(name:String):String = aliases(name)
  def installWebapp(alias:String):String = assureDir( find(alias,"installWebapp") )
  def installConfig(alias:String):String = assureDir( find(alias,"installConfig") )
  def tmpDir(alias:String):String = assureDir( find(alias,"tmpDir") )
  def backupDir(alias:String):String = assureDir( find(alias,"backupDir") )
  def username(alias:String):String = find(alias,"username")
  def tmpAppDir(alias:String) = tmpDir(alias) + "server/"
  def tmpWar(alias:String):String = tmpAppDir(alias) + "owsproxyserver.war"
  def tmpWebapp(alias:String):String = tmpAppDir(alias) + "tomcat/webapps/owsproxyserver/"
  def tmpConfigDir(alias:String):String = tmpAppDir(alias) + "tomcat/conf/"

  
  def isLocalhost(alias:String) = localhost.contains(alias)

  
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
    val props = new java.util.Properties()
    props.load(new FileInputStream(configFile))
    
    import scala.collection.jcl.Conversions.convertMap
    for( (key,value) <- convertMap(props) ) yield {
      ( key.asInstanceOf[String], value.asInstanceOf[String])
    }
  }
  
  private[this] def assureDir(dir:String) = if(dir.endsWith("/")||dir.length==0) dir else dir+"/"
  
  private[this] def find(alias:String,base:String) = {
    if(!aliases.contains(alias)) throw new IllegalArgumentException(alias+" is not a listed alias in the configuration file")
    
    val defaultKey = base.take(1).toLowerCase+base.drop(1)
    val extensionKey = base.take(1).toUpperCase+base.drop(1)
    
    if( elements.contains(alias+extensionKey)) elements(alias+extensionKey)
    else if(elements.contains(defaultKey)) elements(defaultKey)
    else throw new IllegalArgumentException("There is no default value for "+defaultKey+" in the configuration file")
  }

}
