package org.secureows.deploy
import java.io.{FileInputStream,File}
import java.net.{URL,InetAddress}
import scalax.io.Implicits._
import java.text.MessageFormat.format

class Configuration(val configFile:File, val arguments:Seq[String], val deployApp:File) {
  
  private[this] val elements = Map[String,String]( loadProperties.toSeq:_* )
  val aliases = createAliases()
  private[this] val localhost = findLocalHost()
  // ----  End of construction ----
  
  // ---- Start of API
  val maxBackups = elements("maxBackups")
  def apply(name:String)= elements(name)
  def url(name:String) = aliases(name)
  def asURL(name:String) = new URL("http://"+aliases(name))
  
  def installDir(alias:String) = assureDir( find(alias,"InstallDir") )
  def tmpDir(alias:String) = assureDir( find(alias,"TmpDir") )
  def backupDir(alias:String) = assureDir( find(alias,"BackupDir") )
  def username(alias:String) = find(alias,"Username")
  def configDirs(alias:String) = find(alias,"ConfigDirs").split(",").map( path => assureDir(path.trim) ).toSeq
  def isLocalhost(alias:String) = localhost.contains(alias)
  def distributeJars(destinations:Iterable[String]) {
    val destAliases = destinations.filter( alias => (!isLocalhost(alias) &&  aliases.keys.contains(alias)))
    if( !destAliases.isEmpty ){
      val script = destAliases.flatMap[String] ( copyJarScript(_) ).mkString("\n")
      ProcessRunner.script("/bin/sh",script)
    }
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
    if( elements.contains(alias+base)) elements(alias+base)
    else elements(base.take(1).toLowerCase+base.drop(1))
  }

  private[this] def copyJarScript(alias:String)={
      val server = username(alias)+"@"+url(alias)
      val tmp = server+":"+tmpDir(alias)
      val destJar = tmp +deployApp.getName
      val destConfig = tmp+configFile.getName

      val copyJar = "scp "+deployApp.getAbsolutePath+" "+tmp
      val copyConfig = "scp "+configFile.getAbsolutePath+" "+destConfig
      
      List(copyJar,copyConfig)
    }

}
