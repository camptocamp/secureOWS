package org.secureows.deploy

import scala.xml._
import java.io.{File,PrintStream}

object Validation {
  var log:PrintStream = System.out

  def validate(webapp:File):Option[String]={
    val webinf = new File(webapp,"WEB-INF")
    val servicesFile = new File(webinf,"services.xml")
    
    try{
	   val servicesXML = XML.loadFile(servicesFile)
	 
	   val services = servicesXML \ "service"
		 
       val validateSpecs = for( service <- services ) yield { 
	       if ( (service \\ "role").isEmpty ) {
           Some("At least one role must be defined")
         } else {
		       val serviceSpec = new File(webinf,"wmsPolicy_"+(service\"@serviceId")+".xml")
	         checkServiceSpec(serviceSpec)
		    } 
      }
		    
      validateSpecs.find( !_.isEmpty ).getOrElse(None)
     }catch{
      case e:org.xml.sax.SAXParseException => Some(servicesFile+" is not valid XML"+e.getMessage)
     }
  }
  
  private[deploy] def checkServiceSpec( xmlFile:File ):Option[String]={
    def testTags()={
      val xml = XML.loadFile(xmlFile)    
      val tags = Map( "Security"->true, 
           "Requests"->true,
           "GetMap"->false,
           "GetFeatureInfo"->false,
           "GetCapabilities"->false,
           "GetLegendGraphic"->false)
           
     for( (tag,required) <- tags ) yield {
      if ( (xml \\ tag ).isEmpty ) {
        if( required ) Some( "The "+tag+" is required but is missing")
          else {log.printf ("WARNING: the %s tag is missing\n", Array(tag)); None}
        }else None
     }
    }

    try{
      if( xmlFile.exists() ){
        testTags().find( !_.isEmpty ).getOrElse(None)
      } else {
        Some(xmlFile+" is missing.  It is a the definition for a service defined in the services.xml file") 
      }
    }catch{
      case e => Some(xmlFile+" is not valid XML"+e.getMessage)
    }
  }
}


