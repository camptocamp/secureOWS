//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/AbstractOWServiceHandler.java,v 1.9 2006/08/24 06:39:04 poth Exp $
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
package org.deegree.enterprise.servlet;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.ExceptionReport;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.owscommon.XMLFactory;

/**
 * This class provides methods that are common to all services that comply to the OWS Common
 * Implementation Specification 0.3.0.
 * <p>
 * At the moment, the only implemented functionality allows the sending of exception reports to the
 * client, but in the future this may be extended by providing a method that sends responses to
 * GetCapabilities requests.
 * </p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/08/24 06:39:04 $
 * 
 * @since 2.0
 */
abstract class AbstractOWServiceHandler implements ServiceDispatcher {

    //private static final String RESPONSE_TYPE = "application/vnd.ogc.se_xml";
    private static final String RESPONSE_TYPE = "text/xml";

    private static ILogger LOG = LoggerFactory.getLogger( AbstractOWServiceHandler.class );

    /**
     * Sends an exception report to the client. The exception report complies to the OWS Common
     * Implementation Specification 0.3.0.
     * 
     * @param httpResponse
     * @param serviceException
     */
    public void sendException( HttpServletResponse httpResponse,
                                     OGCWebServiceException serviceException ) {
        LOG.entering();
        ExceptionReport report = new ExceptionReport(
            new OGCWebServiceException[] { serviceException } );
        try {
            httpResponse.setContentType( RESPONSE_TYPE );
            XMLFragment reportDocument = XMLFactory.export( report );
            OutputStream os = httpResponse.getOutputStream();
            reportDocument.write( os );
        } catch (Exception e) {
            LOG.logError( "Error sending exception report: "
                + StringTools.stackTraceToString( e ) );
        }
        LOG.exiting();
    }
    
    /**
     * Sends an exception report to the client. The exception report complies to the OWS Common
     * Implementation Specification 0.3.0.
     * 
     * @param httpResponse
     * @param serviceException
     */
    public void sendException( HttpServletResponse httpResponse,
                                      Exception serviceException ) {
        LOG.entering();
        
        OGCWebServiceException ogc = new OGCWebServiceException( serviceException.getMessage() );
        
        ExceptionReport report = new ExceptionReport( new OGCWebServiceException[] { ogc } );
        try {
            httpResponse.setContentType( RESPONSE_TYPE );
            XMLFragment reportDocument = XMLFactory.export( report );
            OutputStream os = httpResponse.getOutputStream();
            reportDocument.write( os );
        } catch (Exception e) {
            LOG.logError( "Error sending exception report: "
                + StringTools.stackTraceToString( e ) );
        }
        LOG.exiting();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractOWServiceHandler.java,v $
Revision 1.9  2006/08/24 06:39:04  poth
File header corrected

Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
