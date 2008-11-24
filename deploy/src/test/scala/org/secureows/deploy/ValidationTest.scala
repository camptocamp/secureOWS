package org.secureows.deploy

class ValidationTest extends org.specs.runner.JUnit4(ValidationSpec){}

import org.secureows.deploy.validation._
import org.specs._
import org.specs.matcher.Matcher
import SpecUtil._
import java.io._

object ValidationSpec extends Specification{

  val validator = new SecOwsValidator()
  
  "corrupt service spec should be detected" in {
    val testFile = file(this, "policyFiles/corrupt.xml")
    val result = validator.checkServiceSpec(testFile)
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("not valid XML")
  }
  
  "good service spec should pass" in {
    val testFile = file(this, "policyFiles/good.xml")
    val result = validator.checkServiceSpec(testFile)
    result.filter(_!=Good) must beEmpty
  }
  
  
  "tag with wrong capitalization should raise a warning" in {
    val testFile = file(this, "policyFiles/capitalizationMistakes.xml")
    val result = validator.checkServiceSpec(testFile)
   
    result mustExist(c => c.isInstanceOf[Warning])
    val msg = result.elements.next.toString 
    val error = result.find(_.isInstanceOf[Warning]).get.msg
    error must include("Preconditions")
  }
  
  "missing security tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_security.xml")
    val result = validator.checkServiceSpec(testFile)
    result mustExist(c => c.isInstanceOf[Error])
    val error = result.find(_.isInstanceOf[Error]).get.msg
    error must include("Security")
    error must include ("missing")
  }
  "missing request tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_requests.xml")
    val result = validator.checkServiceSpec(testFile)    
    result mustExist(c => c.isInstanceOf[Error])
    val msg = result.elements.next.toString 
    val error = result.find(_.isInstanceOf[Error]).get.msg
    error must include("Requests")
    error must include("missing")
  }
  "optional missing request tag should pass with warning" in {
    doAfter { validator.log = System.out }
    val byteStream = new ByteArrayOutputStream()
    validator.log = new PrintStream(byteStream)
    
    val testFile = file(this, "policyFiles/missing_optional.xml")
    val result = validator.checkServiceSpec(testFile)
    result mustExist( _.isInstanceOf[Warning])
    byteStream.toString must include("WARNING")
  }
  "a good service.xml should pass" in {
    validator.validate(file(this,"goodDefinition")).filter(_!=Good) must beEmpty
  }
  "corrupt services.xml should be detected" in {
    val result = validator.validate(file(this,"corruptXML"))
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("not valid XML")
  }
  "missing service spec should be detected" in {
    val result = validator.validate(file(this,"missingServiceSpec"))
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("geoserver")
  }
  "service.xml with no role tags should be detected" in {
    val result = validator.validate(file(this,"norole"))
    result mustExist(c => c.isInstanceOf[Error])
    result.filter( _.isInstanceOf[Error] ).length mustBe 1
    result.elements.next.toString must include("There are no roles defined in services.xml")
  }
  
}
