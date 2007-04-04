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
package org.deegree.portal.portlet.enterprise;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.ogcbase.ImageURL;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.Format;
import org.deegree.portal.context.FormatList;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.Server;
import org.deegree.portal.context.Style;
import org.deegree.portal.context.StyleList;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.standard.wms.control.GetWMSLayerListener;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/08/20 20:53:54 $
 *
 * @since 2.0
 */
public class AddWMSListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( GetWMSLayerListener.class );

    /**
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( Exception e ) {
            gotoErrorPage( "Not a valid RPC for AddWMSListener\n" + e.getMessage() );
        }

        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession();
        Enumeration en = session.getAttributeNames();
        try {
            while ( en.hasMoreElements() ) {
                String key = (String) en.nextElement();
                Object o = session.getAttribute( key );
                if ( o != null && o instanceof ViewContext ) {
                    appendWMS( rpc, (ViewContext) o );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            e.printStackTrace();
        }
    }

    /**
     * appends the selected layers of a WMS to the passed <code>ViewContext</code> 
     * 
     * @param context
     * @throws ContextException 
     * @throws MalformedURLException 
     * @throws PortalException 
     * @throws InvalidCapabilitiesException 
     */
    private void appendWMS( RPCWebEvent rpc, ViewContext context )
                            throws MalformedURLException, ContextException, PortalException,
                            InvalidCapabilitiesException {

        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[0].getValue();
        URL url = new URL( (String) struct.getMember( "WMSURL" ).getValue() );
        String name = (String) struct.getMember( "WMSNAME" ).getValue();
        String version = (String) struct.getMember( "WMSVERSION" ).getValue();
        String layers = (String) struct.getMember( "LAYERS" ).getValue();
        String formatName = (String) struct.getMember( "FORMAT" ).getValue();
        List<String> list = StringTools.toList( layers, ";", true );

        WMSCapabilitiesDocument capa = new WMSCapabilitiesDocument();
        try {
            
            capa.load( new URL( url.toExternalForm() + "?request=GetCapabilities&service=WMS" ) );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not load WMS capabilities from: " + 
                                       new URL( url.toExternalForm() + 
                                               "?request=GetCapabilities&service=WMS" ) + 
                                       "; reason: " + e.getMessage() );
        }
        WMSCapabilities capalilities = (WMSCapabilities) capa.parseCapabilities();

        Layer layer = context.getLayerList().getLayers()[0];
        for ( int i = 0; i < list.size(); i++ ) {
            String[] lay = StringTools.toArray( list.get( i ), "|", false );
            Server server = new Server( name, version, "OGC:WMS", url, capalilities );
            String srs = context.getGeneral().getBoundingBox()[0].getCoordinateSystem().getAsString();
            Format format = new Format( formatName, true );
            FormatList fl = new FormatList( new Format[] { format } );
            // read available styles from WMS capabilities and add them
            // to the WMC layer 
            org.deegree.ogcwebservices.wms.capabilities.Layer wmslay = capalilities.getLayer( lay[0] );
            org.deegree.ogcwebservices.wms.capabilities.Style[] wmsstyles = wmslay.getStyles();
            Style[] styles = null;
            if ( wmsstyles == null || wmsstyles.length == 0 ) {
                // a wms capabilities layer may offeres one or more styles for
                // a layer but it don't have to. But WMC must have at least one
                // style for each layer; So we set a default style in the case
                // a wms does not declares one
                styles = new Style[1];
                styles[0] = new Style( "", "default", "", null, true );
            } else {
                styles = new Style[wmsstyles.length];
                for ( int j = 0; j < styles.length; j++ ) {
                    boolean isDefault = wmsstyles[j].getName().toLowerCase().indexOf( "default" ) > -1 || 
                                        wmsstyles[j].getName().trim().length() == 0;
                    ImageURL legendURL = null;
                    LegendURL[] lUrl = wmsstyles[j].getLegendURL();
                    if ( lUrl != null && lUrl.length > 0 ) {
                        legendURL = new ImageURL( lUrl[0].getWidth(), lUrl[0].getHeight(),
                                                  lUrl[0].getFormat(), lUrl[0].getOnlineResource() );
                    }                
                    styles[j] = new Style( wmsstyles[j].getName(), wmsstyles[j].getTitle(),
                                           wmsstyles[j].getAbstract(), legendURL, isDefault );
                }
            }

            StyleList styleList = new StyleList( styles );
            Layer newLay = new Layer( server, lay[0], lay[1], null, new String[] { srs }, null,
                                      null, fl, styleList, true, false, layer.getExtension() );

            context.getLayerList().addLayer( newLay );
        }

    }

    /**
     * validates the incomming RPC
     * @param rpc
     */
    private void validate( RPCWebEvent rpc ) {
        // TODO Auto-generated method stub

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AddWMSListener.java,v $
Revision 1.6  2006/08/20 20:53:54  poth
changes rquired as a consequence of bug fix in wmc implementation/handling. Instead of determining the correct URL as given in a services capabilities deegree always has used the base URL which is just guarenteed to be valid for GetCapabilities requests.

Revision 1.5  2006/06/09 15:17:21  poth
bug fix - handling layers with no style definition

Revision 1.4  2006/06/08 20:27:12  poth
bug fix - handling wms style elements with empty Name and Title element

Revision 1.3  2006/06/08 11:33:30  poth
*** empty log message ***

Revision 1.2  2006/05/21 12:01:15  poth
bug fix reading styles from WMS capabilities


********************************************************************** */