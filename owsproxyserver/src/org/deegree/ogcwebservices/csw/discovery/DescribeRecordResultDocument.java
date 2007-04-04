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

import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.xml.sax.SAXException;

/**
 * Represents an XML DescribeRecordResponse document of an OGC CSW 2.0 compliant service.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.18 $, $Date: 2006/10/18 17:00:55 $
 * 
 * @since 2.0
 */

public class DescribeRecordResultDocument extends OGCDocument {
   
    private static final String XML_TEMPLATE = "DescribeRecordResponseTemplate.xml";

    /**
     * Generates a <code>DescribeRecordResponse</code> representation of this object.
     * 
     * @param request
     *            
     * @return
     * @throws MissingParameterValueException
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     */
    public DescribeRecordResult parseDescribeRecordResponse( DescribeRecord request ) {

        String version = null;
        SchemaComponent[] schemaComponents = null;

        // TODO: Implement me!

        return new DescribeRecordResult( request, version, schemaComponents );
    }

    /**
     * @see org.deegree.framework.xml.XMLFragment#createEmptyDocument()
     */
    public void createEmptyDocument() throws IOException, SAXException {
        URL url = DescribeRecordResultDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '"
                + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeRecordResultDocument.java,v $
Revision 1.18  2006/10/18 17:00:55  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.17  2006/08/07 10:46:56  poth
never thrown exception removed

Revision 1.16  2006/07/11 07:10:11  poth
footer added/corrected


********************************************************************** */