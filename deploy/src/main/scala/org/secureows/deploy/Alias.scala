package org.secureows.deploy

import org.secureows.deploy.validation.Validator
import java.net.URL

case class Alias( alias:String,
                  url:String,
                  configSvnApp:String,
                  configSvnConf:String,
                  downloadUrl:String,
                  installWebapp:String,
                  installConfig:String,
                  tmpDir:String,
                  backupDir:String,
                  username:String,
                  tmpAppDir:String,
                  tmpWar:String,
                  tmpWebapp:String,
                  tmpConfigDir:String,
                  config:Configuration         
) {
  def asURL = new URL("http://"+url)
  def isLocalhost = config.isLocalhost(alias)
  val maxBackups:Int = config.maxBackups
  val validators:Seq[Validator] = config.validators
}
