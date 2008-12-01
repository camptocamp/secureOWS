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
/*
  "handle camel case id in block" in {
          val data =
        "<daemon>\n" +
        "    useLess = 3\n" +
        "</daemon>\n"
      val exp =
        "{: daemon={daemon: useless=\"3\" } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.useLess", "14") mustEqual "3"
  }

  "handle dash block" in {
  val data =
        "<daemon>\n" +
        "    <base-dat>\n" +
        "        ulimit_fd = 32768\n" +
        "    </base-dat>\n" +
        "</daemon>\n"
   val exp =
        "{: daemon={daemon: base-dat={daemon.base-dat: ulimit_fd=\"32768\" } } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.base-dat.ulimit_fd", "14") mustEqual "32768"
  }
  
  "handle assignment after block" in {
  val data =
        "<daemon>\n" +
        "    <base>\n" +
        "        ulimit_fd = 32768\n" +
        "    </base>\n" +
        "    useless = 3\n" +
        "</daemon>\n"
   val exp =
        "{: daemon={daemon: base={daemon.base: ulimit_fd=\"32768\" } useless=\"3\" } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.useless", "14") mustEqual "3"
      a.getString("daemon.base.ulimit_fd", "14") mustEqual "32768"
  }
  */
  "handle camelcase block" in {
  val data =
        "<daemon>\n" +
        "    <baseDat>\n" +
        "        ulimit_fd = 32768\n" +
        "    </baseDat>\n" +
        "    useless = 3\n" +
        "</daemon>\n"
   val exp =
        "{: daemon={daemon: basedat={daemon.baseDat: ulimit_fd=\"32768\" } useless=\"3\" } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.useless", "14") mustEqual "3"
      a.getString("daemon.baseDat.ulimit_fd", "14") mustEqual "32768"
  }
  
    "two consecutive groups" in {
      val data =
        "<daemon>\n" +
        "    useless = 3\n" +
        "</daemon>\n" +
        "\n" +
        "<upp inherit=\"daemon\">\n" +
        "    uid = 16\n" +
        "</upp>\n"
      val exp =
        "{: daemon={daemon: useless=\"3\" } " +
        "upp={upp (inherit=daemon): uid=\"16\" } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.useless", "14") mustEqual "3"
      a.getString("upp.uid", "1") mustEqual "16"
    }
  
   
    "handle a complex case" in {
      val data =
        "<daemon>\n" +
        "    useless = 3\n" +
        "    <base>\n" +
        "        ulimit_fd = 32768\n" +
        "    </base>\n" +
        "</daemon>\n" +
        "\n" +
        "<upp inherit=\"daemon.base\">\n" +
        "    uid = 16\n" +
        "    <alpha inherit=\"upp\">\n" +
        "        name=\"alpha\"\n" +
        "    </alpha>\n" +
        "    <beta inherit=\"daemon\">\n" +
        "        name=\"beta\"\n" +
        "    </beta>\n" +
        "</upp>\n"
      val exp =
        "{: daemon={daemon: base={daemon.base: ulimit_fd=\"32768\" } useless=\"3\" } " +
        "upp={upp (inherit=daemon.base): alpha={upp.alpha (inherit=upp): name=\"alpha\" } " +
        "beta={upp.beta (inherit=daemon): name=\"beta\" } uid=\"16\" } }"
      val a = parse(data)
      a.toString mustEqual exp
      a.getString("daemon.useless", "14") mustEqual "3"
      a.getString("upp.uid", "1") mustEqual "16"
      a.getString("upp.ulimit_fd", "1024") mustEqual "32768"
      a.getString("upp.name", "23") mustEqual "23"
      a.getString("upp.alpha.name", "") mustEqual "alpha"
      a.getString("upp.beta.name", "") mustEqual "beta"
      a.getString("upp.alpha.ulimit_fd", "") mustEqual "32768"
      a.getString("upp.beta.useless", "") mustEqual "3"
      a.getString("upp.alpha.useless", "") mustEqual ""
      a.getString("upp.beta.ulimit_fd", "") mustEqual ""
    }
  
}
