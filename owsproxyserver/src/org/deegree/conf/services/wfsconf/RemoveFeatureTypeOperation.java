//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wfsconf/RemoveFeatureTypeOperation.java,v 1.15 2006/10/17 20:31:19 poth Exp $
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.conf.services.wfsconf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveFeatureTypeOperation extends WFSConfOperation {
	
	private String featureTypeName;
    
    public static final String REQUEST_NAME = "RemoveFeatureType"; 
    
    private RemoveFeatureTypeOperation( String featureTypeName ) {
        this.featureTypeName = featureTypeName;
        
    }
    public static RemoveFeatureTypeOperation create(HttpServletRequest request) 
    	throws Exception {
        
        Map map = KVP2Map.toMap( request );
        
        String nameParameter = (String)map.get( "NAME" );

        if ( nameParameter == null) {
            throw new InvalidParameterValueException( "Parameter 'NAME' is missing." );
        }

        return new RemoveFeatureTypeOperation( nameParameter );
    }

    
	/* (non-Javadoc)
	 * @see org.deegree.conf.services.WFSConf.FeatureTypeOperation#performOperation(javax.servlet.http.HttpServletRequest)
	 */
	public String performOperation(HttpServletRequest request) throws Exception {
	    
	    File datastoreFile = new File( config.getWFSDataSourceDirectory() + File.separator 
	        + this.featureTypeName + "." +  ConfigUtils.DATASOURCE_EXTENSION );
	    
        removeFeatureType( datastoreFile, "_c" );
        removeFeatureType( datastoreFile, "_s" );
        removeFeatureType( datastoreFile, "_l" );
        removeFeatureType( datastoreFile, "_p" );
        removeFeatureType( datastoreFile, "_t" );
        
        return "Removed feature type " + this.featureTypeName;
	}
	
	/**
     * @param datastoreFile
     * @param feat
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XMLParsingException
     * @throws Exception
     * @throws SAXException
     */
    private void removeFeatureType( File datastoreFile, String extension ) 
                                    throws FileNotFoundException, IOException, 
                                    XMLParsingException, Exception, SAXException {

        if ( datastoreFile.exists() ){
            datastoreFile.delete();
        }
        config.deregisterFeatureType( "app:" + this.featureTypeName + extension );
        removeFeatureTypeFromCaps( "app:" + this.featureTypeName + extension, 
                                   config.getWfsCapabilities() );
        config.saveCapabilites();
        
    }
    public void removeFeatureTypeFromCaps( String featureTypeName, WFSConfigurationDocument wfsConfigDoc ) 
		throws XMLParsingException, Exception {
	
	    String xpath = 
	        " /wfs:WFS_Capabilities/wfs:FeatureTypeList/wfs:FeatureType[ child::wfs:Name[ .='" 
	        + featureTypeName +"']]";	
	
	    Element root = wfsConfigDoc.getRootElement();
	
	    Node featureTypeElement = 
	        XMLTools.getNode( root, xpath, CommonNamespaces.getNamespaceContext() );
	    
	    if ( featureTypeElement != null ) {	
	        
	        featureTypeElement.getParentNode().removeChild( featureTypeElement );
		
	    }  else {
	        //TODO log messages
	        System.out.println( "No FeatureType '" + featureTypeName + "' found. Ignoring" );
	        
	    }		

	}	
    /**
     * 
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoveFeatureTypeOperation.java,v $
Revision 1.15  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.14  2006/08/24 06:38:30  poth
File header corrected

Revision 1.13  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.12  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.11  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.10  2005/12/21 19:20:43  poth
no message

Revision 1.9  2005/12/21 17:30:10  poth
no message

Revision 1.8  2005/12/12 11:34:56  taddei
corrected namespaces (wfs:)

Revision 1.7  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.6  2005/12/09 16:48:47  taddei
operations change capabilities too

Revision 1.5  2005/12/08 15:33:44  taddei
refactoring

Revision 1.4  2005/12/07 14:45:45  taddei
completed imlementation

Revision 1.3  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */