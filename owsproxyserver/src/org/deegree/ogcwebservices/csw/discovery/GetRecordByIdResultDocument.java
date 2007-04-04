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

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/10/18 17:00:56 $
 *
 * @since 2.0
 */
public class GetRecordByIdResultDocument extends XMLFragment {

    private static final long serialVersionUID = 2796229558893029054L;

    /**
     * parses a GetRecordById response XML document and maps it to its 
     * corresponding java/deegree class
     * @param request
     * @return
     * @throws XMLParsingException
     */
    public GetRecordByIdResult parseGetRecordByIdResponse( GetRecordById request )
                            throws XMLParsingException {

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        Element root = getRootElement();
        Element record = (Element) XMLTools.getNode( root, "./child::*[1]", nsc );

        return new GetRecordByIdResult( request, record );
    }

    /**
     * creates an empty GetRecordByIdResponse document
     *
     */
    public void createEmptyDocument() {
        Document doc = XMLTools.create();
        Element root = doc.createElementNS( CommonNamespaces.CSWNS.toASCIIString(),
                                            "csw:GetRecordByIdResponse" );
        setRootElement( root );
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GetRecordByIdResultDocument.java,v $
 Revision 1.6  2006/10/18 17:00:56  poth
 made DefaultOGCWebServiceResponse base type for all webservice responses

 Revision 1.5  2006/07/11 07:10:11  poth
 footer added/corrected


 ********************************************************************** */