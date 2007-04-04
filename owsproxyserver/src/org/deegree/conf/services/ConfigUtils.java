//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/ConfigUtils.java,v 1.2 2006/11/02 10:16:41 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.conf.services;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.owscommon.XMLFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ... 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/11/02 10:16:41 $
 * 
 * @since 2.0
 */
public class ConfigUtils {

    public static final String SQL_EXTENSION = "sql";

    public static final String DATASOURCE_EXTENSION = "xsd";

    public static final String SQL_CREATE = "create.sql";

    public static final String SQL_REMOVE = "remove.sql";
    
    public static final String DATASTORE = "datastore.xsd";
    
    private ConfigUtils(){
        //never instantiate...
    }

    /**
     * Validate the bbox enetered by the user and create an envelope, if the input is correct
     * 
     * @param bboxInput
     * @throws InvalidParameterValueException
     */
    public static Envelope createSafeBBOX(String bboxInput) throws InvalidParameterValueException{
    	
    	String[] splittedBBOX = bboxInput.split(",");		
        
    	if ( splittedBBOX.length != 4 ) {
    	    
        	String message = "Parameter 'BBOX' is not valid" ;
        	throw new InvalidParameterValueException( message ) ;
        	
        }
        
    	double minx = Double.parseDouble( splittedBBOX[0] ) ; 
    	double maxx = Double.parseDouble( splittedBBOX[2] ) ; 
    	
    	if ( minx >= maxx ) {
        
    		String message = "Paremeter 'BBOX' is not valid: ";
        	message = "\n 'minx < maxx' is required , found (" + minx +">=" +maxx+")";
            throw new InvalidParameterValueException( message );
            
        }
    	
    	double miny = Double.parseDouble( splittedBBOX[1] );
    	double maxy = Double.parseDouble( splittedBBOX[3] );
    	
    	if ( miny >= maxy) {
            
    			String message = "Paremeter 'BBOX' is not valid: " 
    			    + "\n 'miny < maxy' is required found (" + miny +">=" +maxy+")";
                throw new InvalidParameterValueException( message );
                
    	}

        Envelope env = GeometryFactory.createEnvelope(minx, miny, maxx, maxy, null);
        return env;
    }

    /* conveniece method to send a message back to the service as XML */    
    public static void sendResponse( String opName, String message, HttpServletResponse response) {
    	 
    	try {
    		    		
    		
            response.setContentType( "application/vnd.ogc.se_xml" );           
            XMLFragment doc = ConfigUtils.createResponseString( opName, message);
            OutputStream os = response.getOutputStream();
            doc.write( os );
            os.close();
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

        
    /* conveniece method to create a message to be sent */    
    public static XMLFragment createResponseString( String opName, String mesg ) 
    	throws Exception{				
    
         Document doc = XMLTools.create();
         Element root = doc.createElement( opName );
         Element summary = doc.createElement( "summary" );
         summary.setAttribute( "result", mesg );
         Element totalInserted = doc.createElement("totalInserted");
         Element totalUpdated = doc.createElement("totalUpdated");
         Element totalDeleted = doc.createElement("totalDeleted");
         totalDeleted.appendChild(doc.createTextNode("0"));
         totalInserted.appendChild(doc.createTextNode("0"));
         totalUpdated.appendChild(doc.createTextNode("0"));
         /*summary.appendChild(totalDeleted);
         summary.appendChild(totalInserted);
         summary.appendChild(totalUpdated);
         */
         root.appendChild(summary);
    	 doc.appendChild(root);	  
    	 
    	 XMLFragment fragment = new XMLFragment(doc.getDocumentElement());
    	 
         return fragment;
    
    }

    /* conveniece method to send an exception back to the service as XML */
        public static void sendException( HttpServletResponse response, Exception e ) {
            
    //        LOG.logInfo( "Sending OGCWebServiceException to client." );
            OGCWebServiceException oe = new OGCWebServiceException( e.getMessage() );
            ExceptionReport report = new ExceptionReport( new OGCWebServiceException[] { oe } );
            e.printStackTrace();
            
            try {
                response.setContentType( "application/vnd.ogc.se_xml" );
                XMLFragment doc = XMLFactory.export( report );
                OutputStream os = response.getOutputStream();
                doc.write( os );
                os.close();
            } catch (Exception ex) {
    //            LOG.logError( "ERROR: " + ex.getMessage(), ex );
            }
        }
    
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ConfigUtils.java,v $
Revision 1.2  2006/11/02 10:16:41  mschneider
Fixed #createSafeBBOX(String) to use crs information.

Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.7  2006/08/24 06:38:30  poth
File header corrected

Revision 1.6  2006/04/06 20:25:33  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.4  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.3  2005/12/21 19:20:43  poth
no message

Revision 1.2  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.1  2005/12/08 15:30:55  taddei
utilities class with common methods/constants


********************************************************************** */