package org.secureows.deploy.fetch

import org.apache.tools.ant.Main
import org.secureows.deploy.{Alias,FetchOp, Utils, ProcessRunner}
import org.secureows.deploy.validation.{Good,Result,Error}
import java.io.File
import scalax.io.Implicits.fileExtras
import scalax.io.InputStreamResource
import scant.Ant
import scala.xml.XML
import java.net.URLClassLoader

class CheckoutAppsStrategy extends FetchStrategy {
    def downloadApp(alias:Alias){

        val localAppDir = new File(alias.tmpAppDir)
        val buildDir = localAppDir/"configuration/.checkoutApp"

        println( "Checking out: " +alias.downloadUrl)
        if( buildDir.exists && "true".equals(alias.findOrElse("CheckoutAppsStrategy.doUpdate", "false")) ){
            ProcessRunner("svn","update",buildDir.getAbsolutePath).run
        }else{
            Utils.doCheckout(buildDir, alias.downloadUrl)
        }
        println("Copying Geocat private files")
        Utils.copyTree(new File(alias("CheckoutAppsStrategy.privateFiles")), buildDir)
        build(buildDir/"jeeves/build.xml")
        build(buildDir/"build.xml")

        runGast(alias, buildDir)

        val webAppDir = new File(alias.tmpWebappBaseDir)
    
        for( app <- alias.webapps; path = webAppDir/app ){
            path.deleteRecursively
            path.mkdirs
            val built = buildDir/"web"/app
      
            built.tree.filter( _.getName.equals(".svn") ).foreach( file => if (file.exists) file.deleteRecursively )
      
            Utils.copyTree(built, path)
            println(app+" webapp has been built")
        }
    }

    def runGast(alias:Alias, buildDir:File){
        InstallOp.stopServer(alias)
        val urls = Array((buildDir/"gast/gast.jar").toURI.toURL)
        val loader = URLClassLoader.newInstance(urls, getClass.getClassLoader)
        System.setProperty("GEONETWORK_HOME",buildDir.getAbsolutePath)
        System.setProperty("java.awt.headless","true")
        val gast = loader.loadClass("org.fao.gast.Gast")
        val constructor = gast.getMethod("main",classOf[Array[String]])

        for( cmd <- alias.getListOrElse("CheckoutAppsStrategy.gastCmds", {List("-setup","-sampleData")}) ){
            println("Running gast "+cmd)
            constructor.invoke(null, Array(Array(cmd)):_*)
        }
        System.setProperty("java.awt.headless","false")
    }
  
    def build( buildFile:File) {
        val properties = new java.util.Properties()
        properties.put("JAVA_HOME", System.getProperty("java.home"))
        new Main(){
            override def exit(exitCode:Int) {
	
                if( exitCode != 0 ){
                    Predef.exit(exitCode)
                }
            }
        }.startAnt(Array("-buildfile",buildFile.getAbsolutePath), properties, getClass().getClassLoader)

    }
    def finalConfiguration(alias:Alias){
        // nothing to do
    }

}
