//$Header$
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
package org.deegree.enterprise.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Node;

/**
 * <p>The class or in detail its method @see #mapPropertyValue(Node) can be used 
 * by a XSLT script to map a node value (key) to another,
 * corresponding value (value). The mapping will be defined by a properties
 * file containing key-value-pairs as can be read by @see #java.util.Properties.
 * as default org.deegree.enterprise.servlet.wfsrequestmapping.properties
 * will be read (which per default is empty). If no matching value for a key
 * is defined in the properties file the key will be returned instead.</p>
 * <p>The node passed to this method must include a text and no other nodes.
 * E.g. &lt;PropertyName&gt;/MyProperty/value&lt;/PropertyName&gt;</p>
 * <p>If special behaviors are needed by an deegree WFS instance and/or
 * you do not want to edit the default properties and use your own one
 * you should write a class that extends this or it as pattern. 
 * 
 *
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 *
 * @version 1.0. $Revision$, $Date$
 *
 * @since 2.0
 */
public class WFSRequestMapping {
    
    private static ILogger LOG = LoggerFactory.getLogger( WFSRequestMapping.class );

    protected static String propertiesFile = "wfsrequestmapping.properties";
    private static Properties mapping = null;
    static {
        mapping = new Properties();
        try {
            InputStream is = 
                WFSRequestMapping.class.getResourceAsStream( "wfsrequestmapping.properties" );
            InputStreamReader isr = new InputStreamReader( is );
            BufferedReader br = new BufferedReader( isr );
            String line = null;
            while ( (line = br.readLine() ) != null ) {
                if ( !line.trim().startsWith("#") ) {
                String[] tmp = StringTools.toArray( line, "=", false );
                mapping.put( tmp[0], tmp[1] );
                }
            }            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static NamespaceContext nsc = CommonNamespaces.getNamespaceContext(); 

    /**
     * The method is used by a XSLT script to map a node value (key) to another,
     * corresponding value (value). The mapping will be defined by a properties
     * file containing key-value-pairs as can be read by @see #java.util.Properties.
     * as default org.deegree.enterprise.servlet.wfsrequestmapping.properties
     * will be read (which per default is empty). If no matching value for a key
     * is defined in the properties file the key will be returned instead.
     * <p>The node passed to this method must include a text and no other nodes.
     * E.g. &lt;PropertyName&gt;/MyProperty/value&lt;/PropertyName&gt;</p>
     *  
     * @param node
     * @return
     * @throws XMLParsingException
     */
    public static String mapPropertyValue( Node node ) {

        String nde = null;
        String key = null;
        try {            
            nde = XMLTools.getNodeAsString( node, ".", nsc, null );
            if ( nde.startsWith( "/" ) ) {
                key = '.' + nde;
            } else if ( nde.startsWith( "." ) ) {
                key = nde;
            } else {
                key = "./" + nde;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        if ( mapping.getProperty( key ) != null ) {
            nde = mapping.getProperty( key );
        }
        LOG.logDebug( "mapped property: " + nde );
        return nde;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/04/06 20:25:23  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:24  poth
*** empty log message ***

Revision 1.3  2006/03/30 07:15:36  poth
*** empty log message ***

Revision 1.2  2006/02/16 08:26:48  poth
*** empty log message ***

Revision 1.1  2006/02/10 08:56:54  poth
*** empty log message ***

Revision 1.1  2006/01/09 07:47:09  ap
*** empty log message ***


********************************************************************** */