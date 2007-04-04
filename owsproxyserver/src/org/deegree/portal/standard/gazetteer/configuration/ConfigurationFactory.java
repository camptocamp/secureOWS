// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/configuration/ConfigurationFactory.java,v 1.5 2006/08/29 19:54:13 poth Exp $

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
package org.deegree.portal.standard.gazetteer.configuration;

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
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:13 $
 *
 * @since 1.1
 */
public class ConfigurationFactory {
    private static final URI GZNS = CommonNamespaces.buildNSURI("http://www.deegree.org/gazetteerclient");

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
    public static GazetteerClientConfiguration createConfiguration( String confFile )
                                                      throws SAXException, IOException, Exception {
        Debug.debugMethodBegin( );

        Reader reader = new FileReader( confFile );
        GazetteerClientConfiguration conf = createConfiguration( reader );
        reader.close();
        Debug.debugMethodEnd();
        return conf;
    }

    /**
    *
    *
    * @param reader 
    *
    * @return 
    *
    * @throws SAXException 
    * @throws IOException 
    * @throws Exception 
    */
    public static GazetteerClientConfiguration createConfiguration( Reader reader )
                                                      throws SAXException, IOException, Exception {
        Debug.debugMethodBegin( );

        Document doc = XMLTools.parse( reader );

        // gazetteer descriptions
        Element element = doc.getDocumentElement();
        ElementList el = XMLTools.getChildElements( "gazetteer", GZNS, element );
        HashMap gaze = createGazetteerDesc( el );
        
        GazetteerClientConfiguration conf = new GazetteerClientConfiguration( gaze ); 

        Debug.debugMethodEnd();
        return conf;
    }

    /**
    * creates a map of thesauri names and associated addresses
    */
    private static HashMap createGazetteerDesc( ElementList nl ) throws MalformedURLException {
        Debug.debugMethodBegin( );

        HashMap thes = new HashMap();

        for ( int i = 0; i < nl.getLength(); i++ ) {
            Element element = nl.item( i );
            Node node = element.getElementsByTagNameNS( GZNS.toString(), "name" ).item( 0 );
            String name = node.getFirstChild().getNodeValue();
            node = element.getElementsByTagNameNS( GZNS.toString(), "onlineResource" ).item( 0 );

            String tmp = XMLTools.getStringValue( node );
            thes.put( name, new URL( tmp ) );
        }

        Debug.debugMethodEnd();
        return thes;
    }
        
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ConfigurationFactory.java,v $
   Revision 1.5  2006/08/29 19:54:13  poth
   footer corrected

   Revision 1.4  2006/04/06 20:25:21  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:40  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:23  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:11  poth
   *** empty log message ***

   Revision 1.3  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/07 13:09:27  deshmukh
   Switched namespace definitions in "CommonNamespaces" to URI.

   Revision 1.2  2005/03/09 11:55:46  mschneider
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:30:07  poth
   no message

   Revision 1.4  2004/07/12 13:03:21  mschneider
   More work on the CatalogConfiguration and capabilities framework.

   Revision 1.3  2004/07/09 06:58:04  ap
   no message

   Revision 1.2  2004/05/24 06:58:47  ap
   no message

   Revision 1.1  2004/05/22 09:55:36  ap
   no message

   Revision 1.2  2004/03/24 12:36:22  poth
   no message

   Revision 1.1  2004/03/15 07:38:05  poth
   no message



********************************************************************** */
