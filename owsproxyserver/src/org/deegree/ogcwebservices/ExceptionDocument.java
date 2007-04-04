// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/ExceptionDocument.java,v 1.15 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.ogcwebservices;

import java.net.MalformedURLException;
import java.net.URI;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.15 $, $Date: 2006/11/27 09:07:53 $
 * 
 * @since 2.0
 */
public class ExceptionDocument extends XMLFragment {

    private static final String POGC = CommonNamespaces.OGC_PREFIX + ":";

    private static final URI OGCNS = CommonNamespaces.OGCNS;

    /**
     * Creates new document without namespace binding.
     */
    public void createEmptyDocument() {
        Document doc = XMLTools.create();
        // note: removed the namespace in order to ensure WMS 1.1.1 certifiability
        Element root = doc.createElement( "ServiceExceptionReport" );
        doc.appendChild( root );
        setRootElement( root );
    }

    /**
     * Creates new document within the OGC namespace.
     */
    public void createEmptyDocumentNS() {
        Document doc = XMLTools.create();
        Element root;
        try {
            root = doc.createElementNS( OGCNS.toURL().toExternalForm(), POGC
                                                                        + "ServiceExceptionReport" );
            doc.appendChild( root );
            setRootElement( root );
        } catch ( MalformedURLException e ) {
            // cannot happen. Really.
        }
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * ExceptionDocument.java,v $ Revision 1.2 2005/02/10 17:17:24 mschneider
 * Corrected usage of XmlNode + XmlDocument.
 * 
 * Revision 1.1.1.1 2005/01/05 10:30:46 poth no message
 * 
 * Revision 1.3 2004/06/16 09:46:02 ap no message
 * 
 *  
 ******************************************************************************/
