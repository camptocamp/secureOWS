package org.secureows.deploy

import org.secureows.deploy.validation.Validator
import org.secureows.deploy.fetch.FetchStrategy
import net.lag.logging.Logger
import java.net.URL
import java.io.File

case class Alias( name:String,
                 url:String,
                 configSvnApp:String,
                 configSvnConf:String,
                 downloadUrl:String,
                 installWebappBaseDir:String,
                 webapps:Iterable[String],
                 installConfig:String,
                 tmpDir:String,
                 backupDir:String,
                 username:String,
                 tmpAppDir:String,
                 tmpWebappBaseDir:String,
                 tmpConfigDir:String,
                 shutdown:String,
                 startup:String,
                 config:Configuration
) {
    def apply(key:String) = config.find(name,key)
    def findOrElse(key:String,elseAction: =>String):String={
        try{
            apply(key)
        }catch{
            case e:IllegalArgumentException => elseAction
        }
    }
    def asURL = new URL("http://"+url)
    def isLocalhost = config.isLocalhost(name)
    val maxBackups:Int = config.maxBackups
    lazy val validators:Seq[Validator] = config.validators
    lazy val fetchStrategy:FetchStrategy = config.fetchStrategy
    lazy val postAction:Option[Function[Alias,Option[String]]] = config.postAction
  
    override def toString = name

    lazy val logger:Logger = {
        val logConfig =config.config.configMap("aliases."+name+".log")
        println(logConfig)
        val file = new java.io.File(logConfig("filename"))
        file.getParentFile.mkdirs
        Logger.configure(logConfig, false, false)
    }

}


object Alias {
    class Converter(filename:String){
        def asFile=new File(filename)
    }

    implicit def toConverter(filename:String) = new Converter(filename)
}