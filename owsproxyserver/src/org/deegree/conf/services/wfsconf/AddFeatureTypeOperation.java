//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wfsconf/AddFeatureTypeOperation.java,v 1.16 2006/10/17 20:31:19 poth Exp $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @deprecated
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddFeatureTypeOperation extends WFSConfOperation{
   
    public static final String REQUEST_NAME = "AddFeatureType"; 
    
    private static final String DUMMY_FEATURETYPE_NAME = "DUMMY_FEATURETYPE_NAME"; 
    
    private String featureTypeName;
	private String title;
	private String abstractPar;
	private String crs;
	private Envelope bbox;	
	private String dataStoreName;


	
    /**
     * 
     */
    private AddFeatureTypeOperation( String name, 
    								 String crs,
									 Envelope bbox,
									 String title,
									 String dataStore,
									 String abstractPar ) {
    	this.featureTypeName = name;
    	this.title = title;
    	this.abstractPar = abstractPar;
    	this.crs = crs;
    	this.bbox = bbox;	
    	this.dataStoreName = dataStore; 
    		
		        
    }

    public static AddFeatureTypeOperation create( HttpServletRequest request )  
    	throws Exception {
            	
        Map map = KVP2Map.toMap( request );
        
    	String parameter = "NAME";
        String nameParameter = (String)map.get( parameter );
        
        if ( nameParameter == null || nameParameter.length() == 0 ) {
            throw new InvalidParameterValueException( "Parameter '" + parameter + "' is missing." );
        }
        
        parameter = "TITLE";
        String titleParameter = (String)map.get(parameter);
        
        if ( titleParameter == null) {
            titleParameter = "";
        }

        parameter = "ABSTRACT";
        String abstractParameter = (String)map.get( parameter );
        
        if ( abstractParameter == null ) {
            abstractParameter = "";
    	}
        
        parameter = "CRS";
        String crsParameter = (String)map.get( parameter );
        
        if ( crsParameter == null ) {
            throw new InvalidParameterValueException( "Parameter '" + parameter + "' is missing." );
        }
        
        parameter = "BBOX";
        String bboxParameter = (String)map.get(parameter);
        
        if ( bboxParameter == null) {
            throw new InvalidParameterValueException( "Parameter '" + parameter + "' is missing." );
        }
        
        parameter = "DATASTORE";
        String dataStoreParameter = (String)map.get(parameter);
        
        if ( bboxParameter == null) {
            throw new InvalidParameterValueException( "Parameter '" + parameter + "' is missing." );
        }
       
        Envelope bbox = ConfigUtils.createSafeBBOX( bboxParameter );
        
        return new AddFeatureTypeOperation( nameParameter, crsParameter, bbox,
											titleParameter, dataStoreParameter, abstractParameter );

    }
    
    /* (non-Javadoc)
	 * @see org.deegree.conf.services.WFSConf.FeatureTypeOperation#performOperation(javax.servlet.http.HttpServletRequest)
	 */
	public String performOperation(HttpServletRequest request) throws Exception {
	    
	    // read datastore confi xml
	    File inFile = new File( config.getSourceDataSourceDirectory() + File.separator + 
	        this.dataStoreName + "." + ConfigUtils.DATASOURCE_EXTENSION); 
        
	    // change replacing DUMMY_FT_NAME -> this.ftName 
        createFeatureType( inFile, "_c" );
        createFeatureType( inFile, "_l" );
        createFeatureType( inFile, "_s" );
        createFeatureType( inFile, "_p" );
        createFeatureType( inFile, "_t" );
        
	    return "Added feature type " + this.featureTypeName;
	}
	
	/**
     * @param inFile
     * @throws IOException
     * @throws FileNotFoundException
     * @throws XMLParsingException
     * @throws Exception
     * @throws SAXException
     */
    private void createFeatureType( File inFile, String extension ) throws IOException, 
                                                FileNotFoundException, XMLParsingException,
                                                Exception, SAXException {
        String feat = this.featureTypeName;
	    String datasourceXML = readTemplate( inFile.getAbsolutePath() ); 
	    datasourceXML = datasourceXML.replaceAll( DUMMY_FEATURETYPE_NAME, feat );
	    
	    // save into new dir
	    saveDataSourceXML( datasourceXML );
	    
	    config.registerFeatureType( "app:" +  feat+ extension, this.dataStoreName );
        
	    Document doc = config.getWfsCapabilities().getRootElement().getOwnerDocument();
	    
	    XMLFragment ftFrag = createFeatureTypeFragment( doc,"app:" + feat+ extension, this.title, 
                                                        this.abstractPar, this.crs, 
                                                        this.bbox );
		 
	    addFeatureTypeToCaps( ftFrag, config.getWfsCapabilities() );
	    
	    config.saveCapabilites();
    }

    /**
     * @param string
	 * @throws IOException
     */
    private void saveDataSourceXML( String contentXML ) throws IOException {

        File outFile = new File( config.getWFSDataSourceDirectory() + File.separator 
            + this.dataStoreName + "." + ConfigUtils.DATASOURCE_EXTENSION); 
        
        Writer writer = new FileWriter( outFile );
        writer.write( contentXML );
        writer.close();
       
    }	

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected String readTemplate(String filename ) throws IOException {
		
		StringBuffer sb = new StringBuffer( 1000 );		
		Reader fr = new FileReader( filename );
		BufferedReader br = new BufferedReader( fr );
		String s = null;
        
		while ( ( s = br.readLine() ) != null ) {
		    sb.append( s );
        }
        
        br.close();

		return sb.toString();
	}    
    
	public void addFeatureTypeToCaps( XMLFragment featureTypeFragment, 
	                            WFSConfigurationDocument wfsConfigDoc )
	
		throws XMLParsingException, Exception {

	    String xpath = " /wfs:WFS_Capabilities/wfs:FeatureTypeList";	
	
	    Element root = wfsConfigDoc.getRootElement();
	
	    Node ftList = XMLTools.getNode( root, xpath, CommonNamespaces.getNamespaceContext() );
	    
	    if ( ftList != null ) {	
		
	        XMLTools.insertNodeInto( featureTypeFragment.getRootElement( ), ftList );
		
	    }  else {
	        throw new Exception( "WFS Capabilities not valid. Missing FeatureList element." );
	    }		
	
	}	
	
    private XMLFragment createFeatureTypeFragment( Document doc, String featureTypeName, String title, 
                                                   String anAbstract, String crs, Envelope bbox){
        
        Element ftElement = doc.createElement( "wfs:FeatureType" );
        Element e = doc.createElement( "wfs:Name" );
        Text t = doc.createTextNode( featureTypeName );
        e.appendChild( t );
        ftElement.appendChild( e );
        
        if ( title != null ){
            e = doc.createElement( "wfs:Title" );
            t = doc.createTextNode( title );
            e.appendChild( t );
            ftElement.appendChild( e );
        }
        
        if ( anAbstract!= null ){
            e = doc.createElement( "wfs:Abstract" );
            t = doc.createTextNode( anAbstract );
            e.appendChild( t );
            ftElement.appendChild( e );
        }
        
        if ( crs != null ){
             e = doc.createElement( "wfs:DefaultSRS" );
             t = doc.createTextNode( crs );
             e.appendChild( t );
             ftElement.appendChild( e );
        }
        
        e = doc.createElement( "ows:WGS84BoundingBox" );
        
        Element coords = doc.createElement( "ows:LowerCorner" );
        String coordVals = String.valueOf( bbox.getMin().getX() )  
        	+ " " + String.valueOf( bbox.getMin().getY() );
        t = doc.createTextNode( coordVals );
        coords.appendChild( t );
        e.appendChild( coords );
        
        coords = doc.createElement( "ows:UpperCorner" );
        coordVals = String.valueOf( bbox.getMax().getX() )  
        	+ " " + String.valueOf( bbox.getMax().getY() );
        t = doc.createTextNode( coordVals );
        coords.appendChild( t );
        e.appendChild( coords );
        ftElement.appendChild( e );
        
        
        return new XMLFragment( ftElement );
    }

	
	
	//still needed??
	public WFSConfigurationDocument readWFSConfiguration( URL filename )
		throws Exception {
	    
	    WFSConfigurationDocument wfsConfigDoc = new WFSConfigurationDocument();
	    wfsConfigDoc.load( filename );
	    
	    //TODO replace
	    //xml = xml.replaceAll( "myFeatureTypeName", this.name );
			    
	    DOMPrinter.printNode( wfsConfigDoc.getRootElement(), "-"); 
	    
	    return wfsConfigDoc;
	}
	
    /**
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation#getOperationName()
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AddFeatureTypeOperation.java,v $
Revision 1.16  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.15  2006/08/24 06:38:30  poth
File header corrected

Revision 1.14  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.13  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.12  2005/12/21 19:20:43  poth
no message

Revision 1.11  2005/12/21 17:30:10  poth
no message

Revision 1.10  2005/12/12 11:34:56  taddei
corrected namespaces (wfs:)

Revision 1.9  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.8  2005/12/09 16:48:47  taddei
operations change capabilities too

Revision 1.7  2005/12/08 15:33:32  taddei
refactoring

Revision 1.6  2005/12/07 14:44:06  taddei
completed imlementation

Revision 1.5  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.4  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */