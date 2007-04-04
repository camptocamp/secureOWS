//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/GetRecordByIdDocument.java,v 1.9 2006/10/04 20:42:04 poth Exp $
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
 Aennchenstr. 19
 53115 Bonn
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
package org.deegree.ogcwebservices.csw.discovery;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/10/04 20:42:04 $
 * 
 * @since 2.0
 */
public class GetRecordByIdDocument extends OGCDocument {

    private static final long serialVersionUID = 2796229558893029054L;

    private static final ILogger LOG = LoggerFactory.getLogger( GetRecordByIdDocument.class );

    private static final String XML_TEMPLATE = "GetRecordByIdTemplate.xml";

    /**
     * 
     * @param id
     * @return
     */
    GetRecordById parseGetRecordById( String id ) throws OGCWebServiceException {
        LOG.entering();

        String version = null;
        Map vendorSpecificParameters = null;
        String[] ids = null;
        String elementSetName = null;

        try {
            // '<csw:GetRecords>'-element (required)
            Node contextNode = XMLTools.getRequiredNode( this.getRootElement(),
                "self::csw:GetRecordById", nsContext );

            // 'service'-attribute (required, must be CSW)
            String service = XMLTools.getRequiredNodeAsString( contextNode, "@service", nsContext );
            if ( !service.equals( "CSW" ) ) {
                ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
                throw new InvalidParameterValueException( "GetRecordById",
                    "'service' must be 'CSW'", code );
            }

            // 'version'-attribute (required)
            version = XMLTools.getRequiredNodeAsString( contextNode, "@version", nsContext );

            // '<csw:ResponseHandler>'-elements (optional)
            ids = XMLTools.getNodesAsStrings( contextNode, "csw:Id", nsContext );

            // '<csw:ElementSetName>'-element (optional)
            Node elementSetNameElement = XMLTools.getNode( contextNode, "csw:ElementSetName",
                nsContext );

            if ( elementSetNameElement != null ) {
                // must contain one of the values 'brief', 'summary' or
                // 'full'
                elementSetName = XMLTools.getRequiredNodeAsString( elementSetNameElement, "text()",
                    nsContext, new String[] { "brief", "summary", "full" } );

            } else {
                elementSetName = "summary";
            }

        } catch (Exception e) {
            ExceptionCode code = ExceptionCode.INVALID_FORMAT;
            throw new OGCWebServiceException( "CatalogGetCapabilities", StringTools
                .stackTraceToString( e ), code );
        }

        LOG.exiting();

        return new GetRecordById( id, version, vendorSpecificParameters, ids, elementSetName );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.framework.xml.XMLFragment#createEmptyDocument()
     */
    void createEmptyDocument() throws IOException, SAXException {
        URL url = GetRecordByIdDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '"
                + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

}

/* *********************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GetRecordByIdDocument.java,v $
 * Revision 1.9  2006/10/04 20:42:04  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/07/11 07:10:11  poth
 * footer added/corrected
 *
 * Revision 1.7  2006/04/06 20:25:25  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/03/30 21:20:25  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.5  2006/02/26 21:30:42  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2005/11/17 08:18:35  deshmukh
 * Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Revision 1.3  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Revision 1.2.2.2  2005/11/07 15:38:04  mschneider
 * Refactoring: use NamespaceContext instead of Node for namespace bindings.
 * Changes to this class. What the people have been up to:
 * Revision 1.2.2.1 2005/11/07 13:09:26 deshmukh Switched namespace definitions in
 * "CommonNamespaces" to URI.
 * 
 * Revision 1.2 2005/09/27 19:53:18 poth no message
 * 
 * Revision 1.1 2005/09/09 19:43:46 poth no message
 * 
 * 
 ********************************************************************************************** */