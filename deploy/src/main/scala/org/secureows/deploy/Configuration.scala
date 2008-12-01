package org.secureows.deploy
import java.io.{FileInputStream,File}
import java.net.{URL,InetAddress}
import scalax.io.Implicits._
import java.text.MessageFormat.format
import org.secureows.deploy.validation.Validator
import org.secureows.deploy.fetch.FetchStrategy
import net.lag.configgy.Configgy

class Configuration(val configFile:File, val deployApp:File) {
    Configgy.configure(configFile.getAbsolutePath)

    val config = Configgy.config
    val localhosts = findLocalHost
    // ----  End of construction ----
  
    // ---- Start of API
    val maxBackups = {
        val backups = config.getInt("maxBackups")
        if( backups.isEmpty ) throw new IllegalArgumentException("maxBackups is not defined in the configuration file")
        if( backups.get < 0 ) throw new IllegalArgumentException("maxBackups must be a positive number")
        backups.get
    }

    def alias(alias:String) = {
        if( !config.contains("aliases."+alias) ) throw new IllegalArgumentException("Alias "+alias+" is not defined in the configuration file")

        Alias(alias, url(alias), configSvnApp(alias), configSvnConf(alias), downloadUrl(alias),
              installWebappBaseDir(alias), webapps, installConfig(alias), tmpDir(alias),
              backupDir(alias), username(alias), tmpAppDir(alias), tmpWebappBaseDir(alias),
              tmpConfigDir(alias), shutdown(alias), startup(alias),this)
    }
  
    def asURL(name:String) = new URL("http://"+url(name))

    def configSvnApp(alias:String):String = find(alias, "configSvnApp")
    def configSvnConf(alias:String):String = find(alias, "configSvnConf")
    def downloadUrl(alias:String):String = find(alias,"downloadUrl")
    def url(name:String):String = find(name,"url")
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
  
    def webapps:Iterable[String] =  config.getList("apps")

    def isLocalhost(alias:String) = localhosts.contains(alias)
  
    lazy val validators:Seq[Validator] = {
        val validators = config.getConfigMap("tool_config.validators")
        if( validators.isEmpty){
            Seq[Validator]()
        } else {
            val v = for{ key <- validators.get.keys
                        name = validators.get.getString(key)
                        c = classOf[Configuration].getClassLoader.loadClass( name.get )
            } yield {
                c.newInstance().asInstanceOf[Validator]
            }
            v.collect
        }
    }

    lazy val fetchStrategy:FetchStrategy = {
        val asString = config.getString("tool_config.fetchStrategy")
        assert( asString.isDefined && asString.get.trim().length>0, "the 'tool_config.fetchStrategy' property is not defined" )
        val c = classOf[Configuration].getClassLoader.loadClass( asString.get )
        c.newInstance().asInstanceOf[FetchStrategy]
    }

    lazy val postAction:Option[Function[Alias,Option[String]]] = function("tool_config.postAction")

    def function(name:String):Option[Function[Alias,Option[String]]] = {
        val asString = config.getString("tool_config."+name)
        if( asString.isDefined){
            assert( asString.get.trim().length>0, "the '"+name+"' property is defined but empty" )
            val c = classOf[Configuration].getClassLoader.loadClass( asString.get )

            Some(c.newInstance().asInstanceOf[Function[Alias,Option[String]]])
        }
        else None
    }

    // ---- End of API
    private[this] def findLocalHost() = {
        val local = new URL("http://"+InetAddress.getLocalHost().getHostName())
        val localhost = new URL("http://localhost")
        val localIp = new URL("http://127.0.0.1")

        val aliases = config.configMap("aliases")
        aliases.keys.filter{ alias =>
            val urlString = aliases(alias+".url","default")
            val aliasUrl = new URL("http://"+urlString)
            aliasUrl == local || aliasUrl == localIp || aliasUrl == localhost

        }.toList
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
        val key = "aliases."+alias+"."+base
        val data = config.getString( key )
        if(data.isEmpty) throw new IllegalArgumentException("There is no value for "+key+" in the configuration file")
        else data.get
    }

}
