package org.secureows.deploy

import org.specs._
import SpecUtil._
import java.io._

object OnlineSpecs extends Specification{
  /*"test ProcessRunner" in {
    ProcessRunner("ssh","camptocamp@www.secureows.org","java -cp /tmp/tmp/deploy.jar org.secureows.deploy.Main -v -c /tmp/tmp/testing.properties -j /tmp/tmp/deploy.jar website").run
    val configFile = SpecUtil.file(this,"testPushJarConfig.properties")
    val jarFile = SpecUtil.file(this,"testPushJar.jar")
    val aliases = "remote"::Nil
    Remoting.distributeJars(aliases,new Configuration(configFile, aliases, jarFile))
    ProcessRunner.script("/bin/sh","ssh camptocamp@www.secureows.org \"java -cp /tmp/tmp/deploy.jar org.secureows.deploy.Main -v -c /tmp/tmp/testing.properties -j /tmp/tmp/deploy.jar website\"")
  }

  "copy can copy to computer" in {
    val configFile = SpecUtil.file(this,"testPushJarConfig.properties")
    val jarFile = SpecUtil.file(this,"testPushJar.jar")
    val aliases = "remote"::Nil
    Remoting.distributeJars(aliases,new Configuration(configFile, aliases, jarFile))
  }
   
  "can run validate on a remote computer" in {
    Main.main(Array("-v","-j","target/deploy-1.1/lib/deploy.jar","-c","testing.properties","install","website"))
  }

   * "can run validate on local computer" in {
    Main.main(Array("-v","-j","target/deploy-1.1/lib/deploy.jar","-c","testing.properties","install","home"))
  }

 "fetch" in {
    Main.main(Array("-f","-j","target/deploy-1.1/lib/deploy.jar","-c","geonetwork.properties","from"))
  }
*/

  "push" in {
    Main.main(Array("-p","-j","target/deploy-1.1/lib/deploy.jar","-c","geonetwork.properties","from","francois"))
  }
}
