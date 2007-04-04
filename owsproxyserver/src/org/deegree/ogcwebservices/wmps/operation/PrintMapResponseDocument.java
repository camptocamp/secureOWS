//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/operation/PrintMapResponseDocument.java,v 1.11 2006/08/29 19:54:14 poth Exp $
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

package org.deegree.ogcwebservices.wmps.operation;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Represents an Initial Response document for a WMPS 1.1.0 compliant web service.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/08/29 19:54:14 $
 * 
 * @since 2.0
 */

public class PrintMapResponseDocument extends DefaultOGCWebServiceResponse {

    private static final ILogger LOG = LoggerFactory.getLogger( PrintMapResponseDocument.class );

    protected static final URI WMPSNS = CommonNamespaces.WMPSNS;

    private static final String XML_TEMPLATE = "WMPSInitialResponseTemplate.xml";

    private Element root;

    /**
     * @param request
     */
    public PrintMapResponseDocument( OGCWebServiceRequest request ) {
        super( request );
    }

    /**
     * Creates a skeleton response document that contains the mandatory elements only.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument()
                            throws IOException, SAXException {
        LOG.entering();
        URL url = PrintMapResponseDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '" + XML_TEMPLATE + " could not be found." );
        }
        XMLFragment fragment = new XMLFragment();
        fragment.load( url );
        this.root = fragment.getRootElement();
        LOG.exiting();
    }

    /**
     * Get Root Element of the document.
     * 
     * @return Element
     */
    public Element getRootElement() {
        return this.root;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PrintMapResponseDocument.java,v $
Revision 1.11  2006/08/29 19:54:14  poth
footer corrected

Revision 1.10  2006/08/24 06:42:17  poth
File header corrected

Revision 1.9  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
