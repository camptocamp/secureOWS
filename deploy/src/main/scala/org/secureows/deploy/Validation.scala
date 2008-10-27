package org.secureows.deploy

import scala.xml._
import java.io.{File,PrintStream}

abstract class Result{val msg:String;val id:Int}
case class Error(msg:String) extends Result{val id=0}
case class Warning(msg:String) extends Result{val id=1}
object Good extends Result{val msg="good";val id=2}

object Validation {
  var log:PrintStream = System.out

  // returns errors,warnings
  def validate(webapp:File):Seq[Result]={
    val webinf = new File(webapp,"WEB-INF")
    val servicesFile = new File(webinf,"services.xml")
    
    try{
	   val servicesXML = XML.loadFile(servicesFile)
	 
	   val services = servicesXML \ "service"
		 
       val validateSpecs = for( service <- services ) yield { 
	       if ( (service \\ "role").isEmpty ) {
           List(Error("At least one role must be defined"))
         } else {
		       val serviceSpec = new File(webinf,"wmsPolicy_"+(service\"@serviceId")+".xml")
	           checkServiceSpec(serviceSpec)
		 } 
      }
		    
      validateSpecs.flatMap( e => e ).toSeq
     }catch{
      case e:org.xml.sax.SAXParseException => List(Error(servicesFile+" is not valid XML\n\t"+e.getMessage))
      case e:Throwable => List(Error("An error occurred while validating "+servicesFile+"\n\t"+e.getMessage))
     }
  }
  
  private[deploy] def checkServiceSpec( xmlFile:File ):Seq[Result]={
    def testTags()={
      val xml = XML.loadFile(xmlFile)    
      val tags = Map( "Security"->true, 
           "Requests"->true,
           "GetMap"->false,
           "GetFeatureInfo"->false,
           "GetCapabilities"->false,
           "GetLegendGraphic"->false)
           
     for( (tag,required) <- tags ) yield {
       val result:Result = if ( (xml \\ tag ).isEmpty ) {
         if( required ) {
             Error("The "+tag+" is required but is missing")
         } else {
           val warning = ">  WARNING: the "+tag+" tag is missing\n"
           log.print (warning);
           Warning(warning)
         }
       } else Good
       
       result
     }
    }

    try{
      if( xmlFile.exists() ){
        testTags().toSeq
      } else {
        List(Error(xmlFile+" is missing.  It is a the definition for a service defined in the services.xml file")) 
      }
    }catch{
      case e => List(Error(xmlFile+" is not valid XML:\n\t"+e.getMessage))
    }
  }
}


