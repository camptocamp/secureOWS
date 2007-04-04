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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * sends a request to a WFS and checks the result for errors
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 *  
 */

public class WFSRequester {
	
	private static ILogger LOG = LoggerFactory.getLogger(WFSRequester.class);

    /**
     * 
     * @param request
     * @param href
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     */
	public static Document sendWFSrequest(Document request, OGCWebService ows) throws Exception {

        GetFeature getFeature = 
            GetFeature.create( "ID-" + System.currentTimeMillis(), request.getDocumentElement() );
        FeatureResult result = (FeatureResult)ows.doService( getFeature );
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        
        new GMLFeatureAdapter().export( (FeatureCollection)result.getResponse(), bos );
        
		Document resultDoc = XMLTools.parse( new ByteArrayInputStream( bos.toByteArray() ) );
        
		String nn = resultDoc.getDocumentElement().getLocalName();

		if ( "FeatureCollection".equals( nn ) ) {
			return resultDoc;
		} 
		LOG.logDebug("error: invalid wfs result");
        throw new XMLParsingException( "returned document doesn't contain a valid " +
                                       "WFS GetFeature response" );
	

	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WFSRequester.java,v $
Revision 1.12  2006/08/24 06:42:16  poth
File header corrected

Revision 1.11  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
