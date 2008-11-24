package org.secureows.deploy.fetch

import scalax.io.{CommandLineParser,InputStreamResource}
import scalax.io.Implicits._
import scalax.data.Positive
import java.text.MessageFormat.format
import java.io.File

class OwsProxyServerWarStrategy extends FetchStrategy {
  def downloadApp(alias:Alias) = {
      
      val webapp = new File(alias.tmpWebappBaseDir+"owsproxyserver")
      val appRemoteUrl = appFetchUrl(alias)
      val localWar = new File(alias.tmpAppDir + "owsproxyserver.war")
      
      println("Downloading "+appRemoteUrl)
      InputStreamResource(appRemoteUrl.openStream).pumpTo(localWar.outputStream.buffered )

      println("decompressing "+localWar.getName)
      ProcessRunner("unzip",localWar.getAbsolutePath, "-d", webapp.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
      
      (webapp/"WEB-INF").listFiles.filter( _.isFile).filter( _.getName.endsWith(".xml")).foreach( _.delete)
            
  }

  def finalConfiguration(alias:Alias) = {
    val webapp = new File(alias.tmpWebappBaseDir+"owsproxyserver")
    ProcessRunner("java","-cp","WEB-INF/classes","OwsAdmin", "./WEB-INF/").dir(webapp).run
  }  
  private def appFetchUrl(alias:Alias) = new java.net.URL(alias.downloadUrl)

}
