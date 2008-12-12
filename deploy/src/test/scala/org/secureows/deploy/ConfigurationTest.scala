package org.secureows.deploy

class ConfigurationTest extends org.specs.runner.JUnit4(ConfigurationSpec){}

import org.specs._
import SpecUtil._
import java.io._
import net.lag.configgy._

object ConfigurationSpec extends Specification{

    def beA(url:String) = beSome[String].which( _ == url)
    val config = Config.fromResource("org/secureows/deploy/testPushJarConfig.properties")
    "configgy works as I expect " in {
        config.getInt("maxBackups") must beSome[Int].which( _ == 5)
        config.getString("aliases.local.url") must beA("localhost")
        val aliases = config.getConfigMap("aliases")
        aliases  must beSome[net.lag.configgy.ConfigMap]
        aliases.get.getString("local.url") must beA("localhost")
        aliases.get.getString("remote.url") must beA("www.secureows.org")
        aliases.get.getString("other_local.url") must beA("127.0.0.1")
        aliases.get.getString("other_local.userName") must beA("camptocamp")
        aliases.get.getString("under_group.url_url") must beA("foo")
    }

    "localhost contains all aliases that map to localhost and 127.0.0.1" in {
        val configFile = SpecUtil.file(this,"testPushJarConfig.properties")
        val jarFile = SpecUtil.file(this,"testPushJar.jar")
        val aliases = "localhost"::Nil
        val config = new Configuration(configFile, jarFile)
        config.isLocalhost("local") mustBe true
        config.isLocalhost("other_local") mustBe true
    }

    "all required elements are available" in {
        println(config.asMap.mkString(","))
        config.getBool("aliases.local.innerGroup.innerprop", false) mustBe true
        val toolConfig = config.getConfigMap("tool_config")
        toolConfig must beSome[ConfigMap]
        println(toolConfig.elements.mkString("[",",","]"))
        config.getString("tool_config.fetchStrategy") must beA("fetcher")
        config.getList("tool_config.postAction") must containInOrder( List("postAction","second") )
        config.getString("tool_config.validators.validator.class") must beA("validator")
        config.getBool("aliases.default.warnWhenMissingConfFiles") must beSome[Boolean].which( _ == false )
    }
}
