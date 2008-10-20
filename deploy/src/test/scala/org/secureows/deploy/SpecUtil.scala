package org.secureows.deploy
import java.io.File
import java.net.URL

object SpecUtil {
  def file( base:Object, name:String ) = new File( url(base,name).getFile )
  def url( base:Object, name:String ) = base.getClass.getResource(name)
}
