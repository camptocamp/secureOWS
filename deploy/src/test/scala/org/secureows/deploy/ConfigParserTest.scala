package org.secureows.deploy

class ConfigParserTest extends org.specs.runner.JUnit4(ConfigParserSpec){}

import org.specs._
import SpecUtil._
import java.io._
import net.lag.configgy._

object ConfigParserSpec extends Specification{

  class FakeImporter extends Importer {
    def importFile(filename: String): String = {
      filename match {
        case "test1" =>
          "staff = \"weird skull\"\n"
        case "test2" =>
          "<inner>\n" +
          "    cat=\"meow\"\n" +
          "    include \"test3\"\n" +
          "    dog ?= \"blah\"\n" +
          "</inner>"
        case "test3" =>
          "dog=\"bark\"\n" +
          "cat ?= \"blah\"\n"
        case "test4" =>
          "cow=\"moo\"\n"
      }
    }
  }

  def parse(in: String) = {
    val attr = new Config
    attr.importer = new FakeImporter
    attr.load(in)
    attr
  }
  "handle camelCase lists" in {
          val data =
        "<daemon>\n" +
        "    useLess = [\"one\",\"two\"]\n" +
        "</daemon>\n"
      val a = parse(data)
      a.getList("daemon.useLess") must containInOrder( List( "one","two"))
  }
  
}
