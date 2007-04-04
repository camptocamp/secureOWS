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
package org.deegree.conf.services.wmsconf.layerconf;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei </a>
 *  
 */
public class WMSCapabilitiesUtils {

    //TODO chnage into capablities doc?
	private XMLFragment wmsCapabilities;

	/**
	 * Create a WMSCapabilitiesUtil from a given WMS Capabilities represented by
	 * wmsCapabilities
	 * 
	 * @param wmsCapabilities
	 *            the XMl representign the WMS capabilities
	 */
	public WMSCapabilitiesUtils( XMLFragment wmsCapabilities ) {
		this.wmsCapabilities = wmsCapabilities;
	}

	/**
	 * Appends an XmlFragment of a Layer to a node to be idetified by
	 * parentLayerTitle
	 * 
	 * @param layerFragment
	 *            the Fragment containing the Layer
	 * @param parentLayerName
	 *            the title of the parent layer, to which the layerFragment will
	 *            be appended
	 * @throws XMLParsingException parsing error or wrong XML document
	 * @throws Exception parentLayerTitle not found
	 */
	public void appendLayer( XMLFragment layerFragment, String parentLayerName ) 
		throws XMLParsingException, Exception {
		
	
	    if ( layerNameAlreadyExists( layerFragment ) ) {
	        throw new Exception("LayerName already exists");
	    }
	    
		Element root = wmsCapabilities.getRootElement() ;
		Node fragmentNode = layerFragment.getRootElement( );
		
		String xpath = "//Layer/Name[.='" + parentLayerName +"']";							
		Node node = getNode( xpath, root ) ;
		
		if ( node != null ) {
		    
		    Node parentLayer = getNode( xpath, root ).getParentNode();
			parentLayer = XMLTools.insertNodeInto( fragmentNode, parentLayer ) ;
			
		} else {// Parent is null: append to the end of layer tree
			
		    xpath = "/WMT_MS_Capabilities/Capability/Layer";		    
		    Node topLayer = getNode( xpath, root );
			XMLTools.insertNodeInto( fragmentNode, topLayer ) ;
			
		}		
			
	}
	
	
	/**
	 * Removes the Layer idetified by layerTitle from a capabilities Document 
	 * 	 
	 * @param layerName
	 *            the title of the layer to be removed
	 * @throws XMLParsingException parsing error or wrong XML document
	 * @throws Exception parentLayerTitle not found
	 */
	public void removeLayer( String layerName ) throws XMLParsingException, Exception {
		
		String xpath = "//Layer[child::Name[.='" + layerName+"']]" ;
		Element root = wmsCapabilities.getRootElement( ) ;
		Node layer = getNode( xpath, root ) ;
		
		if ( layer != null ) {	
			layer.getParentNode().removeChild( layer ) ;
			
		} else {
			
			throw new Exception( " The layer '" + layerName + 
								 "' does not exist in the Capabilities " );
		}		
	}

	/**
	 * Checks whether the a layer Name in the layerFragmnet already exists in 
	 * the capabilities document
	 * 
	 * @param name
	 * @return
	 */
	private boolean layerNameAlreadyExists(XMLFragment layerFragment) throws Exception{
	    	    	    	   
	    Node layer = null;			
	    String xpath = ".//Name/text()";	
	    Node node = getNode( xpath, layerFragment.getRootElement() ) ;	
	    String name = node.getNodeValue();
	    xpath = " //Layer[ child::Name [ .='" + name +"']]";		
	    Element root = wmsCapabilities.getRootElement() ;
	    layer = getNode( xpath, root ) ;
	    	return layer != null ;
	
	}	
	
	/**
	 * 
	 * @param xpath
	 * @param root the actual wmsCapabilities document
	 * @return
	 * @throws Exception
	 */
	private Node getNode(String xpath, Element root) throws Exception {		
		return XMLTools.getNode( root, xpath, CommonNamespaces.getNamespaceContext());		
	}
	
	/**
	 * Returns the actual actual wmsCapabilities document
	 * 
	 * @return
	 */
	public XMLFragment getWMSCapabilities() {
		return wmsCapabilities;
	}
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSCapabilitiesUtils.java,v $
Revision 1.1  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.10  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.9  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.8  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.7  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.6  2005/12/12 13:08:35  ncho
Sn
layerNameAlreadyExists
insert layer at end of top Level Layer

Revision 1.5  2005/12/07 14:50:59  taddei
search for layer name and not title (for removal)

Revision 1.4  2005/12/05 12:06:04  ncho
SN
implememented removeLayer()

Revision 1.3  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.2  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */