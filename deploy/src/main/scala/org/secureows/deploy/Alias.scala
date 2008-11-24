package org.secureows.deploy

import org.secureows.deploy.validation.Validator
import org.secureows.deploy.fetch.FetchStrategy
import java.net.URL

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
                  config:Configuration         
) {
  def asURL = new URL("http://"+url)
  def isLocalhost = config.isLocalhost(name)
  val maxBackups:Int = config.maxBackups
  lazy val validators:Seq[Validator] = config.validators
  lazy val fetchStrategy:FetchStrategy = config.fetchStrategy
  
  override def toString = name
}
