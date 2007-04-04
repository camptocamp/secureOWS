//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/sos/configuration/ConfigurationFactory.java,v 1.5 2006/08/29 19:54:14 poth Exp $

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
package org.deegree.portal.standard.sos.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.deegree.framework.util.Debug;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:che@wupperverband.de.de">Christian Heier</a>
 * @version 0.1
 */
public class ConfigurationFactory {
    private static final URI SOSNS = CommonNamespaces
        .buildNSURI( "http://www.deegree.org/sosclient" );

    /**
     * 
     * 
     * @param confFile
     * 
     * @return
     * 
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public static SOSClientConfiguration createConfiguration( String confFile )
        throws SAXException,
            IOException,
            Exception {
        Debug.debugMethodBegin();

        Reader reader = new FileReader( confFile );
        SOSClientConfiguration conf = createConfiguration( reader );
        reader.close();
        Debug.debugMethodEnd();
        return conf;
    }

    /**
     * 
     * 
     * @param reader
     * 
     * @return SOSClientConfiguration
     * 
     * @throws SAXException
     * @throws IOException
     * @throws Exception
     */
    public static SOSClientConfiguration createConfiguration( Reader reader )
        throws SAXException,
            IOException,
            Exception {
        Debug.debugMethodBegin();

        Document doc = XMLTools.parse( reader );

        // sos descriptions
        Element element = doc.getDocumentElement();
        ElementList el = XMLTools.getChildElements( "sos", SOSNS, element );
        HashMap sosServers = createSOSDesc( el );

        SOSClientConfiguration conf = new SOSClientConfiguration( sosServers );

        Debug.debugMethodEnd();
        return conf;
    }

    /**
     * creates a map of sos names and associated online resources
     */
    private static HashMap createSOSDesc( ElementList nl ) throws MalformedURLException {
        Debug.debugMethodBegin();

        HashMap sosServers = new HashMap();

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = nl.item( i );
            Node node = element.getElementsByTagNameNS( SOSNS.toString(), "name" ).item( 0 );
            String name = node.getFirstChild().getNodeValue();
            node = element.getElementsByTagNameNS( SOSNS.toString(), "onlineResource" ).item( 0 );

            String tmp = XMLTools.getStringValue( node );
            sosServers.put( name, new URL( tmp ) );
        }

        Debug.debugMethodEnd();
        return sosServers;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: ConfigurationFactory.java,v $
 * Changes to this class. What the people have been up to: Revision 1.5  2006/08/29 19:54:14  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/04/06 20:25:33  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/04/04 20:39:44  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/03/30 21:20:29  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/02/05 09:30:12  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2005/11/16 13:45:01  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1.2.1  2005/11/07 13:09:26  deshmukh
 * Changes to this class. What the people have been up to: Switched namespace definitions in "CommonNamespaces" to URI.
 * Changes to this class. What the people have been up to:
 * Revision 1.1 2005/08/26 10:12:40 taddei first upload of C. Heyer's client code
 * 
 **************************************************************************************************/
