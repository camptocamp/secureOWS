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
	
       val roles = servicesXML \ "roles" \ "role" 
       
       
       val validateSpecs = for( service <- services ) yield { 
	     if ( (service \\ "role").isEmpty ) {
           List(Error("There are no roles defined in services.xml for service: '"+service \ "@serviceId"+"'.\n  This means this service is inaccessible"))
         } else {
		       val serviceSpec = new File(webinf,"wmsPolicy_"+(service\"@serviceId")+".xml")
	           checkServiceSpec(serviceSpec)
		 } 
      }
      
      if( roles.isEmpty ) validateSpecs.flatMap( e => e ).toSeq
      else {
        Warning("There is a roles tag that is not a child of a service.  This is ignored") :: 
        validateSpecs.flatMap( e => e ).toList
      }
      
     }catch{
      case e:org.xml.sax.SAXParseException => List(Error(servicesFile+" is not valid XML\n\t"+e.getMessage))
      case e:Throwable => List(Error("An error occurred while validating "+servicesFile+"\n\t"+e.getMessage))
     }
  }
  
  private[deploy] def checkServiceSpec( xmlFile:File ):Seq[Result]={
    val tags = Map( "Security"->REQUIRED, 
           "Requests"->REQUIRED,
           "OWSPolicy"->REQUIRED,
           
           "GetMap"->RECOMMENDED,
           "GetFeatureInfo"->RECOMMENDED,
           "GetCapabilities"->RECOMMENDED,
           "GetLegendGraphic"->RECOMMENDED,

           "PreConditions"->OPTIONAL,
           "Parameter"->OPTIONAL,
           "Role"->OPTIONAL,
           "Value"->OPTIONAL,
           "PostConditions"->OPTIONAL,
           "Any"->OPTIONAL,
           "#PCDATA" -> OPTIONAL
      )
    def isValid(node:Node):Iterable[Result]={
      if ( !tags.keys.contains(node.label) ){
        val warning = ">  WARNING: <"+node.label+"> is not a recognized tag.  Check Spelling and capitalization\n"
        log.print (warning);
        List(Warning(warning))
      }else if(!node.child.isEmpty){
        val results = for(child <- node.child) yield {
          isValid(child)
        }
        results.flatMap( e => e )
      } else {
        List(Good)
      }
    }
    def testTags()={
      val xml = scala.xml.Utility.trim(XML.loadFile(xmlFile))
                 
     val required = for( (tag,required) <- tags if (required != OPTIONAL) ) yield {
       if ( (xml \\ tag ).isEmpty ) {
         required match {
           case REQUIRED => Error("The "+tag+" is required but is missing")
           case _ => {
             val warning = ">  WARNING: the "+tag+" tag is missing\n"
             log.print (warning);
             Warning(warning)
           }
         }
       } else Good
     }
     
     required ++ isValid(xml)
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

sealed abstract class Requirement
case object REQUIRED extends Requirement
case object RECOMMENDED extends Requirement
case object OPTIONAL extends Requirement
