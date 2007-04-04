//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wmsconf/layerconf/AddLayerOperation.java,v 1.16 2006/10/17 20:31:17 poth Exp $
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
package org.deegree.conf.services.wmsconf.layerconf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.ConfigUtils;
import org.deegree.conf.services.wmsconf.WMSConfOperation;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddLayerOperation extends WMSConfOperation {

    public static final String REQUEST_NAME = "AddLayer";

    private String name;
    private String title;
    private String abstract_;
    private String[] styleList;
    private String queryable;
    private String parentLayer;
    private String[] crsList;
    private String[] featureTypeList;
    private Envelope bbox;
    
   
    
    public static AddLayerOperation create(HttpServletRequest request) throws Exception {
        
        Map map = KVP2Map.toMap( request );
        
        String name = (String)map.get("NAME");    
        //String crsParameter = (String)map.get("CRS");        
        String bboxParameter = (String)map.get("BBOX");        
        //String templateParameter = (String)map.get("TEMPLATE");        
        String titleParameter = (String)map.get("TITLE");        
        String abstractParameter = (String)map.get("ABSTRACT");
        String featureTypeList = (String)map.get("FEATURETYPES");
        String[] ftList = StringTools.toArray( featureTypeList, ",", false );
        String queryable = (String)map.get("QUERYABLE");
        
        Envelope box = ConfigUtils.createSafeBBOX( bboxParameter );
        
        String style = (String)map.get("STYLES");
        String[] styleList = StringTools.toArray( style, ",", false );
        String parentLayer = (String)map.get("PARENT");
        
        
        return new AddLayerOperation( name, titleParameter, abstractParameter,parentLayer,
            null, box, ftList, queryable, styleList );
    }
    
    
    
    public static AddLayerOperation create( String name, String title, String abstract_, 
                                            String parentLayer, String[] crsList, Envelope bbox, 
                                            String[] featureTypeList, String queryable, 
                                            String[] styleList )  {
        
        //TODO validation
        
        return new AddLayerOperation( name, title, abstract_, parentLayer, crsList, bbox, 
            featureTypeList, queryable ,styleList );

    }    
    
    /**
     * 
     */
    public AddLayerOperation( String name, String title, String abstract_, String parentLayer, 
                              String[] crsList, Envelope bbox, String[] featureTypeList, 
                              String queryable, String[] styleList) {
        this.name = name;
        this.title = title;
        this.abstract_ = abstract_;
        this.parentLayer = parentLayer;
        this.crsList = crsList;
        this.featureTypeList = featureTypeList;
        this.queryable = queryable;
        this.styleList = styleList;
        this.bbox = bbox;
    }
    
    public String performOperation(  HttpServletRequest request, HttpServletResponse response ) 
    	throws Exception {
        
        XMLFragment f = createLayerFragment();
        
        WMSCapabilitiesDocument wmsCaps = config.getWmsCapabilities();
        
        
        WMSCapabilitiesUtils wmsUtils = new WMSCapabilitiesUtils( wmsCaps );
        wmsUtils.appendLayer( f, parentLayer );
        
        config.saveCapabilites();
        
        return "Added layer '" + this.name + "'";
    }
    
    private XMLFragment createLayerFragment(){
        
        Document doc = XMLTools.create();
        Element layer = doc.createElement( "Layer" );
        
        if ( queryable == null || queryable.equals( "1" ) || 
             queryable.equalsIgnoreCase( "true") ) {
            layer.setAttribute( "queryable", "1" );
        } else {
            layer.setAttribute( "queryable", "0" );
        }
        layer.setAttribute( "noSubsets", "0");
        layer.setAttribute( "fixedWidth", "0");
        layer.setAttribute( "fixedHeight", "0");
        
        Element e = doc.createElement( "Name" );
        Text t = doc.createTextNode( name );
        e.appendChild( t );
        layer.appendChild( e );
        
        if ( title != null ){
            e = doc.createElement( "Title" );
            t = doc.createTextNode( title );
            e.appendChild( t );
            layer.appendChild( e );
        }
        
        if ( this.crsList != null ){
            for (int i = 0; i < this.crsList.length; i++) {
                e = doc.createElement( "SRS" );
                t = doc.createTextNode( this.crsList[i] );
                e.appendChild( t );
                layer.appendChild( e );
            }
        }
        
        e = doc.createElement( "LatLonBoundingBox" );
        e.setAttribute( "minx", String.valueOf( this.bbox.getMin().getX() ) );
        e.setAttribute( "miny", String.valueOf( this.bbox.getMin().getY() ) );
        e.setAttribute( "maxx", String.valueOf( this.bbox.getMax().getX() ) );
        e.setAttribute( "maxy", String.valueOf( this.bbox.getMax().getY() ) );
        layer.appendChild( e );
        
        for (int i = 0; i < featureTypeList.length; i++) {
            Element ds = XMLTools.appendElement( layer, CommonNamespaces.DEEGREEWMS, 
                                                 "deegree:DataSource" );
            ds.setAttribute( "failOnException", "0" );
            ds.setAttribute( "queryable", "1" );            
            XMLTools.appendElement( ds, CommonNamespaces.DEEGREEWMS, "deegree:Name",
                                    "app:" + featureTypeList[i] );
            
        }
        
        
        if ( this.styleList != null ){
            
            for (int i = 0; i < styleList.length; i++) {
                if ( styleList[i] != null ){
                    Element style = doc.createElement( "Style" );
                    
                    e = doc.createElement( "Name" );
                    t = doc.createTextNode( styleList[i] );
                    e.appendChild( t );
                    style.appendChild( e );
                    
                    e = doc.createElement( "Title" );
                    t = doc.createTextNode( styleList[i] );
                    e.appendChild( t );
                    style.appendChild( e );
                    
					layer.appendChild( style );
                }
            }
        }
        
        
        return new XMLFragment( layer );
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
$Log: AddLayerOperation.java,v $
Revision 1.16  2006/10/17 20:31:17  poth
*** empty log message ***

Revision 1.15  2006/08/24 06:38:30  poth
File header corrected

Revision 1.14  2006/08/07 06:49:04  poth
not used private method removed

Revision 1.13  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.12  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.11  2005/12/22 20:12:32  poth
no message

Revision 1.10  2005/12/21 19:20:43  poth
no message

Revision 1.9  2005/12/21 17:30:10  poth
no message

Revision 1.8  2005/12/12 09:39:49  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.7  2005/12/09 16:49:04  taddei
bug fix

Revision 1.6  2005/12/08 15:33:07  taddei
bbox uses method from util class

Revision 1.5  2005/12/07 14:49:55  taddei
completed imlementation

Revision 1.3  2005/12/01 14:14:38  taddei
corrected styling to match deegree style guide, code clean up

Revision 1.2  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */