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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: greve@giub.uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree.portal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.model.spatialschema.Point;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.context.Format;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.LayerList;
import org.deegree.portal.context.ViewContext;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 2.0
 */
public class PortalUtils {
    
    /**
     * returns a comma seperated list of layers marked as sensible for
     * feature info requestes
     * @param vc
     * @return
     */
    public static List getFeatureInfoLayers(ViewContext vc) {        
        List list = new ArrayList();
        LayerList layerList = vc.getLayerList();
        Layer[] layers = layerList.getLayers();
        for (int i = 0; i < layers.length; i++) {
           if ( layers[i].getExtension() != null && layers[i].getExtension().isSelectedForQuery() ) {
               list.add( layers[i].getName() );
           }
        }
        return list;
    }
    
    /**
     * returns a comma seperated list of visible layers 
     * @param vc
     * @return
     */
    public static List getVisibleLayers(ViewContext vc) {        
        List list = new ArrayList();
        LayerList layerList = vc.getLayerList();
        Layer[] layers = layerList.getLayers();
        for (int i = 0; i < layers.length; i++) {
           if ( !layers[i].isHidden() ) {
               String[] s = new String[2];
               s[0] = layers[i].getName();
               s[1] = layers[i].getServer().getOnlineResource().toExternalForm();
               list.add( s );
           }
        }      
        return list;
    }
    
    /**
     * returns true if at least one layer of the passed server is visible
     * @param vc
     * @param serverTitle
     * @return
     */
    public static boolean hasServerVisibleLayers(ViewContext vc, String serverTitle) {
        LayerList layerList = vc.getLayerList();
        Layer[] layers = layerList.getLayers();
        for (int i = 0; i < layers.length; i++) {
           if ( layers[i].getServer().getTitle().equals( serverTitle ) &&
                !layers[i].isHidden() ) {
               return true;
           }
        }
        return false;
    }
    
    /**
     * creates the GetMap basic requests required by the JSP page assigned to
     * the MapViewPortlet.
     * @param vc
     * @return
     */
    public static String[] createBaseRequests(ViewContext vc) {
                
        Layer[] layers = vc.getLayerList().getLayers();
        List list = new ArrayList( layers.length );
        int i = layers.length-1;
        try {
            while ( i >= 0 ) {
                GeneralExtension gExt = vc.getGeneral().getExtension();
                Point[] bbox = vc.getGeneral().getBoundingBox();
                StringBuffer sb = new StringBuffer( 1000 );
                URL url = OWSUtils.getHTTPGetOperationURL( layers[i].getServer().getCapabilities(), 
                                                          GetMap.class );
                if ( url != null ){
                    String href = url.toExternalForm();
                    
                    sb.append( OWSUtils.validateHTTPGetBaseURL( href ) );
                    sb.append( "SRS=" ).append( bbox[0].getCoordinateSystem().getName() );
                    sb.append( "&REQUEST=GetMap&VERSION=" ).append( layers[i].getServer().getVersion() );
                    sb.append( "&transparent=" ).append( true );
                    if ( gExt == null ) {
                        sb.append( "&BGCOLOR=" ).append( "0xFFFFFF" );
                    } else {
                        sb.append( "&BGCOLOR=" ).append( gExt.getBgColor() );
                    }
                    Format format = layers[i].getFormatList().getCurrentFormat();
                    sb.append( "&FORMAT=" ).append( format.getName() );
                    StringBuffer styles = new StringBuffer( 1000 );
                    styles.append( "&STYLES=");
                    StringBuffer lyrs = new StringBuffer( 1000 );
                    lyrs.append( "&LAYERS=" );
                    String title = layers[i].getServer().getTitle();                
                    while ( i >= 0 && title.equals( layers[i].getServer().getTitle() ) ) {
                        if ( !layers[i].isHidden() ) {
                            lyrs.append( layers[i].getName() ).append( ',' );
                            String style = layers[i].getStyleList().getCurrentStyle().getName();
                            if ( style.equalsIgnoreCase( "DEFAULT") ) {
                                style = "";
                            }
                            styles.append( style ).append( ',' );
                        }
                        i--;
                    } 
                    String s1 = lyrs.substring( 0, lyrs.length() - 1 ); 
                    String s2 = styles.substring( 0, styles.length() - 1 );
                    sb.append( s1 ).append( s2 );
                    if ( s1.length() > 7 ) {                    
                        // ensure that a request will just be created if 
                        // at least one layer of a group is visible
                        list.add( sb.toString() );
                    }
                } else {
                    System.out.println( "no service available for layer: " + layers[i--].getName() );
                }
                
            }
        } catch ( Exception shouldneverhappen ) {
            shouldneverhappen.printStackTrace();
        }

        return (String[])list.toArray( new String[list.size()] );
    }
    
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PortalUtils.java,v $
Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.10  2006/08/29 19:17:11  poth
bug fix - add code for catching the case that no service is available for a layer

Revision 1.9  2006/08/20 20:51:43  poth
method validateHTTPGetBaseURL moved to OWSUtil

Revision 1.8  2006/08/18 08:58:50  poth
method added

Revision 1.7  2006/08/17 19:30:35  poth
bug fix - OGC WMS 1.3 specification defines that HTTPGet URLs must end with '&' or '?'

Revision 1.6  2006/06/22 14:42:38  poth
*** empty log message ***

Revision 1.5  2006/06/11 17:59:48  poth
bug fix - marking active featureinfo layer

Revision 1.4  2006/06/08 20:27:12  poth
bug fix - handling wms style elements with empty Name and Title element

Revision 1.3  2006/06/08 11:33:30  poth
*** empty log message ***

Revision 1.2  2006/06/06 13:20:18  poth
some bug fixes

Revision 1.1  2006/05/19 13:13:34  poth
moved to another package


********************************************************************** */