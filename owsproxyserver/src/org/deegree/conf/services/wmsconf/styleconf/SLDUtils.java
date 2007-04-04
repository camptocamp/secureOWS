/*
 * Created on 07.12.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.deegree.conf.services.wmsconf.styleconf;

import java.util.List;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @deprecated
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class SLDUtils {	
    
    private XMLFragment stylesDocument;    
    
    /**
     * 
     */
    public SLDUtils( XMLFragment stylesDocument) {
        this.stylesDocument = stylesDocument;
    }
    
	/**
	 * Adds a styleFragment as a child of the namedLayernode to the this document. 
	 * @param styleFragment te sld fragment to be added
	 * @param namedLayer the namedLayer Element, under which the fragment will be added
	 * @param replaceAll if true, all other styles will be removed
	 * @throws XMLParsingException
	 * @throws Exception
	 */
    public void addStyle( XMLFragment styleFragment, boolean replaceAll ) 
		throws XMLParsingException, Exception {

		//String xpath = " //sld:NamedLayer[ child::sld:Name[ .='" + namedLayer +"']]";	
        String xpath = " //sld:NamedLayer";	
		
		Element root = stylesDocument.getRootElement();
		
		Node namedLayerNode = getNode( xpath, root ) ;
	
		if ( namedLayerNode != null ) {	
			
		    if ( replaceAll ){
		        
		        List children = XMLTools.getNodes( namedLayerNode, "./sld:UserStyle", CommonNamespaces.getNamespaceContext()); 
	
		        for (int i = 0; i < children.size(); i++) {
		            namedLayerNode.removeChild( (Node)children.get( i ) );
	            }
		        
		    }
		    
		    XMLTools.insertNodeInto( styleFragment.getRootElement( ), namedLayerNode );
			
		}  else {
				
			throw new Exception( " The top SLd element does not exist in the styles " );
		}		
	}    
    
	/**
	 * Remove a style named styleName from this SLD document.
	 * @param styleName
	 * @throws Exception
	 */
	public void removeStyle( String styleName ) throws Exception {
	    
	    String xpath = " //sld:NamedLayer/sld:UserStyle[ child::sld:Name[ .='" + styleName +"']]";	
	    
	    Element root = stylesDocument.getRootElement();
		
		Node styleNode = getNode( xpath, root );

		if ( styleNode != null ){
		    
		    styleNode.getParentNode().removeChild( styleNode );
		    
		}  else {
			throw new Exception( " The style '" + styleName + "' has not been found!" );
		}	

	}
	
	/**
	 * Convenience method for the very lazy ;-)
	 * @param xpath
	 * @param root the actual wmsCapabilities document
	 * @return
	 * @throws Exception
	 */
	private Node getNode(String xpath, Element root) throws Exception {		
		return XMLTools.getNode( root, xpath, CommonNamespaces.getNamespaceContext());		
	}
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SLDUtils.java,v $
Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.4  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
