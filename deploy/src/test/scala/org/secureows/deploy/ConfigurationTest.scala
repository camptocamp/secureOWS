package org.secureows.deploy

class ConfigurationTest extends org.specs.runner.JUnit4(ConfigurationSpec){}

import org.specs._
import SpecUtil._
import java.io._

object ConfigurationSpec extends Specification{
  "localhost contains all aliases that map to localhost and 127.0.0.1" in {
    val configFile = SpecUtil.file(this,"testPushJarConfig.properties")
    val jarFile = SpecUtil.file(this,"testPushJar.jar")
    val aliases = "localhost"::Nil
    val config = new Configuration(configFile, jarFile)
    config.isLocalhost("local") mustBe true
    config.isLocalhost("otherLocal") mustBe true

  }
}
