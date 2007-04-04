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
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLTools;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * transforms the given document by using the given xsl script
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 */

public class XSLTransformer {

    public static Document transformDocument( Document doc, URL sheetURL )
        throws TransformerException,
            IOException,
            SAXException {

        //FIXME use XmlFragment and associated classes to transform
        //		Document transformedDoc = null;

        TransformerFactory transFact = TransformerFactory.newInstance();

        Transformer trans = transFact.newTransformer( new StreamSource( sheetURL.openStream() ) );

        StringWriter sw = new StringWriter();

        StringReader sr = new StringReader( DOMPrinter.nodeToString( doc, CharsetUtils
            .getSystemCharset() ) );
        StreamSource strs = new StreamSource( sr );
        trans.transform( strs, new StreamResult( sw ) );

        return XMLTools.parse( new StringReader( sw.getBuffer().toString() ) );
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XSLTransformer.java,v $
Revision 1.8  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.7  2006/08/24 06:42:16  poth
File header corrected

Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
