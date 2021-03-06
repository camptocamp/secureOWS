//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/shape/AVLPointSymbolCodeList.java,v 1.12 2006/09/18 14:08:06 poth Exp $$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

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
package org.deegree.tools.shape;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.12 $, $Date: 2006/09/18 14:08:06 $
 * 
 * @since 1.1
 */
class AVLPointSymbolCodeList {
    
    private static final String CODELIST = "AVLPointSymbolCodeList.xml";
    private Map map = new HashMap();

    /**
     * 
     */
    public AVLPointSymbolCodeList() throws SAXException, IOException,  XMLParsingException {
    	
    	InputStream in = 
            AVLPointSymbolCodeList.class.getResourceAsStream(CODELIST);
        Document doc = XMLTools.parse( in );
      
        /* ************************* OLD **********************************/
        //Node nsNode = XMLTools.getNamespaceNode( new String[] {} );
        //NodeList nl = XMLTools.getXPath( "Symbols", element, nsNode );
        
    	NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();           
    	List nl = XMLTools.getNodes( doc, "Symbols" , nsContext ); 
    	
    	for (Object o : nl) {
    		if ( o instanceof Node ){
    			Node n = (Node)o;
    			String code = XMLTools.getRequiredNodeAsString( n, "@code", nsContext );
    			String sym = XMLTools.getRequiredNodeAsString( n, "@symbol", nsContext );
    			map.put( code, sym );
    		}
        }
    }
    
    public String getSymbol(String code) {
        return (String)map.get( code );
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$$Log: AVLPointSymbolCodeList.java,v $
$Revision 1.12  2006/09/18 14:08:06  poth
$useless code removed
$
$Revision 1.11  2006/08/24 06:43:54  poth
$File header corrected
$
$Revision 1.10  2006/08/16 14:29:13  ncho
$ported from deegree1 not yet tested
$$
********************************************************************** */
