package org.secureows.deploy

class ValidationTest extends org.specs.runner.JUnit4(ValidationSpec){}

import org.specs._
import SpecUtil._
import java.io._

object ValidationSpec extends Specification{
  "corrupt service spec should be detected" in {
    val testFile = file(this, "policyFiles/corrupt.xml")
    val result = Validation.checkServiceSpec(testFile)
    result must beSome[String].which( _.contains("not valid XML"))
  }
  "good service spec should pass" in {
    val testFile = file(this, "policyFiles/good.xml")
    val result = Validation.checkServiceSpec(testFile)
    result must beNone[String]
  }
  "missing security tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_security.xml")
    val result = Validation.checkServiceSpec(testFile)
    result must beSome[String].which( s => s.contains("Security") && s.contains("missing") )
  }
  "missing request tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_requests.xml")
    val result = Validation.checkServiceSpec(testFile)    
    result must beSome[String].which( s => s.contains("Requests") && s.contains("missing") )

  }
  "optional missing request tag should pass with warning" in {
    doAfter { Validation.log = System.out }
    val byteStream = new ByteArrayOutputStream()
    Validation.log = new PrintStream(byteStream)
    
    val testFile = file(this, "policyFiles/missing_optional.xml")
    val result = Validation.checkServiceSpec(testFile)
    result must beNone[String]
    byteStream.toString must include("WARNING")
  }
  "a good service.xml should pass" in {
    Validation.validate(file(this,"goodDefinition")) must beNone[String]
  }
  "corrupt services.xml should be detected" in {
    Validation.validate(file(this,"corruptXML")) must beSome[String].which( _.contains("not valid XML"))
  }
  "missing service spec should be detected" in {
    Validation.validate(file(this,"missingServiceSpec")) must beSome[String].which( _.contains("geoserver"))
  }
  "service.xml with no role tags should be detected" in {
    val result = Validation.validate(file(this,"norole"))
    result must beSome[String]
    result.get must include("At least one role ")
  }
}
