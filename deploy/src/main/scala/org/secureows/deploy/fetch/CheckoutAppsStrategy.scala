package org.secureows.deploy.fetch

import org.apache.tools.ant.Main
import org.secureows.deploy.{Alias,FetchOp, Utils, ProcessRunner}
import org.secureows.deploy.validation.{Good,Result,Error}
import java.io.File
import scalax.io.Implicits.fileExtras
import scalax.io.InputStreamResource
import scant.Ant
import scala.xml.XML

class CheckoutAppsStrategy extends FetchStrategy {
  def downloadApp(alias:Alias){

    val localAppDir = new File(alias.tmpAppDir)
    val configDir = localAppDir/"configuration/.checkoutApp"
    
    println( "Checking out: " +alias.downloadUrl)
    configDir.deleteRecursively
    Utils.doCheckout(configDir, alias.downloadUrl)
    
    build(configDir/"jeeves/build.xml")
    build(configDir/"build.xml")
    
    val webAppDir = new File(alias.tmpWebappBaseDir)
    
    for( app <- alias.webapps; path = webAppDir/app ){
      path.deleteRecursively
      path.mkdirs
      val built = configDir/"web"/app
      
      built.tree.filter( _.getName.equals(".svn") ).foreach( file => if (file.exists) file.deleteRecursively )
      
      Utils.copyTree(built, path)
      
    }
  }
  
  def build( buildFile:File) {
    	new Main(){
      override def exit(exitCode:Int) {
	
        if( exitCode != 0 ){
          Predef.exit(exitCode)
        }
      }
    }.startAnt(Array("-buildfile",buildFile.getAbsolutePath), new java.util.Properties(), getClass().getClassLoader)

  }
  def finalConfiguration(alias:Alias){
    // nothing to do
  }

}