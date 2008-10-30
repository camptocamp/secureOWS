package org.secureows.deploy

class ValidationTest extends org.specs.runner.JUnit4(ValidationSpec){}

import org.specs._
import org.specs.matcher.Matcher
import SpecUtil._
import java.io._

object ValidationSpec extends Specification{

  "corrupt service spec should be detected" in {
    val testFile = file(this, "policyFiles/corrupt.xml")
    val result = Validation.checkServiceSpec(testFile)
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("not valid XML")
  }
  
  "good service spec should pass" in {
    val testFile = file(this, "policyFiles/good.xml")
    val result = Validation.checkServiceSpec(testFile)
    result.filter(_!=Good) must beEmpty
  }
  
  
  "tag with wrong capitalization should raise a warning" in {
    val testFile = file(this, "policyFiles/capitalizationMistakes.xml")
    val result = Validation.checkServiceSpec(testFile)
   
    result mustExist(c => c.isInstanceOf[Warning])
    val msg = result.elements.next.toString 
    val error = result.find(_.isInstanceOf[Warning]).get.msg
    error must include("Preconditions")
  }
  
  "missing security tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_security.xml")
    val result = Validation.checkServiceSpec(testFile)
    result mustExist(c => c.isInstanceOf[Error])
    val error = result.find(_.isInstanceOf[Error]).get.msg
    error must include("Security")
    error must include ("missing")
  }
  "missing request tag spec should be detected" in {
    val testFile = file(this, "policyFiles/missing_requests.xml")
    val result = Validation.checkServiceSpec(testFile)    
    result mustExist(c => c.isInstanceOf[Error])
    val msg = result.elements.next.toString 
    val error = result.find(_.isInstanceOf[Error]).get.msg
    error must include("Requests")
    error must include("missing")
  }
  "optional missing request tag should pass with warning" in {
    doAfter { Validation.log = System.out }
    val byteStream = new ByteArrayOutputStream()
    Validation.log = new PrintStream(byteStream)
    
    val testFile = file(this, "policyFiles/missing_optional.xml")
    val result = Validation.checkServiceSpec(testFile)
    result mustExist( _.isInstanceOf[Warning])
    byteStream.toString must include("WARNING")
  }
  "a good service.xml should pass" in {
    Validation.validate(file(this,"goodDefinition")).filter(_!=Good) must beEmpty
  }
  "corrupt services.xml should be detected" in {
    val result = Validation.validate(file(this,"corruptXML"))
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("not valid XML")
  }
  "missing service spec should be detected" in {
    val result = Validation.validate(file(this,"missingServiceSpec"))
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("geoserver")
  }
  "service.xml with no role tags should be detected" in {
    val result = Validation.validate(file(this,"norole"))
    result mustExist(c => c.isInstanceOf[Error])
    result.elements.next.toString must include("At least one role ")
  }
  
}
