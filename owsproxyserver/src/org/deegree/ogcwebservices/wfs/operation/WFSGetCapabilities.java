//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/WFSGetCapabilities.java,v 1.20 2006/10/27 13:26:33 poth Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import java.util.Map;

import org.deegree.framework.util.KVP2Map;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.ogcwebservices.wfs.WFService;
import org.w3c.dom.Element;

/**
 * Represents a GetCapabilities request to a web feature service.
 * <p>
 * The GetCapabilities request is used to query a capabilities document from a web feature
 * service.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.20 $, $Date: 2006/10/27 13:26:33 $
 */
public class WFSGetCapabilities extends GetCapabilities {

    private static final long serialVersionUID = 3581485156939911513L;

    /**
     * Creates a new <code>WFSGetCapabilities</code> instance.
     * 
     * @param id request identifier
     * @param updateSeq
     * @param acceptVersions
     * @param sections
     * @param acceptFormats
     * @param vendoreSpec
     */
    WFSGetCapabilities( String id, String updateSeq, String[] acceptVersions, String[] sections,
                       String[] acceptFormats, Map<String,String> vendoreSpec ) {
        super( id, WFService.VERSION, updateSeq, acceptVersions, sections, acceptFormats, vendoreSpec );
    }

    /**
     * Creates a <code>WFSGetCapabilities</code> instance from a document that contains the DOM
     * representation of the request.
     * 
     * @param id
     * @param root element that contains the DOM representation of the request
     * @return transaction instance
     * @throws OGCWebServiceException
     */
    public static WFSGetCapabilities create( String id, Element root )
                            throws OGCWebServiceException {
        WFSGetCapabilitiesDocument doc = new WFSGetCapabilitiesDocument();
        doc.setRootElement( root );
        WFSGetCapabilities request;
        try {
            request = doc.parse(id);
        } catch ( Exception e ) {
            throw new OGCWebServiceException ("WFSGetCapabilities", e.getMessage());
        }
        return request;
    }

    /**
     * Creates a new <code>WFSGetCapabilities</code> instance from the given key-value pair
     * encoded request.
     * 
     * @param id request identifier
     * @param request
     * @return new <code>WFSGetCapabilities</code> request
     * @throws InvalidParameterValueException 
     * @throws MissingParameterValueException 
     */
    public static WFSGetCapabilities create( String id, String request )
                            throws InvalidParameterValueException, MissingParameterValueException {
        Map<String, String> map = KVP2Map.toMap( request );
        map.put( "ID", id );
        return create( map );
    }

    /**
     * Creates a new <code>WFSGetCapabilities</code> request from the given map.
     * 
     * @param request
     * @return new <code>WFSGetCapabilities</code> request
     * @throws InvalidParameterValueException 
     * @throws MissingParameterValueException 
     */
    public static WFSGetCapabilities create( Map<String, String> request )
                            throws InvalidParameterValueException, MissingParameterValueException {

        String service = getRequiredParam( "SERVICE", request );
        if ( !service.equals( "WFS" ) ) {
            throw new InvalidParameterValueException( "WFSGetCapabilities",
                                                      "Parameter 'service' must be 'WFS'." );
        }

        String[] acceptVersions = getParamValues( "ACCEPTVERSIONS", request, WFService.VERSION );
        String[] sections = getParamValues( "SECTIONS", request, "" );
        String updateSequence = getParam( "UPDATESEQUENCE", request, "" );
        String[] acceptFormats = getParamValues( "ACCEPTFORMATS", request, "text/xml" );

        // TODO generate unique request id
        String id = null;
        return new WFSGetCapabilities( id, updateSequence, acceptVersions, sections, 
                                       acceptFormats, request );
    }

    /**
     * Returns the service name (WFS).
     * 
     * @return the service name (WFS).
     */
    public String getServiceName() {
        return "WFS";
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: WFSGetCapabilities.java,v $
 * Revision 1.20  2006/10/27 13:26:33  poth
 * support for vendorspecific parameters added
 *
 * Revision 1.19  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.18  2006/07/21 14:10:24  mschneider
 * Javadoc fixes.
 *
 * Revision 1.17  2006/06/06 17:06:21  mschneider
 * Reworked and checked against spec.
 *
 * Revision 1.16  2006/05/16 16:29:18  mschneider
 * Improved javadoc. Fixed footer + header.
 *
 * Revision 1.15  2006/04/23 10:45:45  poth
 * *** empty log message ***
 *
 * Revision 1.14  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.13  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.12  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.11  2005/12/04 14:45:45  poth
 * no message
 *
 * Revision 1.10  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.9.2.3  2005/11/15 14:36:19  deshmukh
 * QualifiedName modifications
 * Revision 1.9.2.2 2005/11/07 16:25:57 deshmukh NodeList to List
 * Revision 1.9.2.1 2005/11/07 15:38:04 mschneider Refactoring: use NamespaceContext instead of Node
 * for namespace bindings.
 * 
 * Revision 1.9 2005/09/27 19:53:19 poth no message
 * 
 * Revision 1.8 2005/08/26 21:11:29 poth no message
 * 
 * Revision 1.2 2005/06/06 10:02:56 poth no message
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.6 2005/03/09 11:55:47 mschneider *** empty log message ***
 * 
 * Revision 1.5 2005/03/01 14:39:08 mschneider *** empty log message *** Revision 1.4 2005/02/28
 * 07:43:40 poth no message
 * 
 * Revision 1.3 2005/02/26 16:57:29 poth no message
 * 
 * Revision 1.2 2005/02/21 13:53:48 poth no message
 * 
 * Revision 1.1 2005/02/21 11:24:33 poth no message
 * 
 * Revision 1.4 2005/02/18 20:54:18 poth no message
 * 
 * Revision 1.3 2005/02/07 07:56:57 poth no message
 * 
 * Revision 1.2 2005/02/03 21:35:08 poth no message
 * 
 * Revision 1.4 2004/08/24 11:48:26 tf no message Revision 1.3 2004/06/18 06:18:46 ap no message
 * 
 * Revision 1.2 2004/06/14 15:14:08 ap no message
 * 
 * Revision 1.1 2004/06/08 14:47:14 tf refactored
 * 
 * Revision 1.1 2004/06/07 13:38:34 tf code adapted to wfs1 refactoring
 * 
 * Revision 1.6 2004/05/14 07:48:22 poth no message
 * 
 * Revision 1.5 2004/03/12 15:56:49 poth no message
 * 
 * Revision 1.4 2004/01/26 08:10:37 poth no message
 * 
 * Revision 1.3 2003/11/11 17:12:56 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:54 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:26 poth no message
 * 
 * Revision 1.6 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.5 2002/05/14 14:39:51 ap no message
 * 
 * Revision 1.4 2002/05/13 16:11:02 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 ********************************************************************** */