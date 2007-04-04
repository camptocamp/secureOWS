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
package org.deegree.portal.standard.wms.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.Debug;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.Constants;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.IOSettings;
import org.deegree.portal.context.ViewContext;

/**
 * will be called if the client forces a print action. 
 * 
 * @deprecated use @see org.deegree.portal.common.control.AbstractSimplePrintListener
 *
 * @author <a href="mailto:lupp@lat-lon.de">Katharina Lupp</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.18 $ $Date: 2006/12/04 10:02:46 $
 */
public class PrintListener extends AbstractMapListener {

    private static final ILogger LOG = LoggerFactory.getLogger( PrintListener.class );

    /*
     * the method will be called if a print action/event occurs.
     * 
     * (non-Javadoc)
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    @Override
    public void actionPerformed( FormEvent event ) {
        Debug.debugMethodBegin();
        super.actionPerformed( event );

        RPCWebEvent rpc = (RPCWebEvent) event;
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter[] para = mc.getParameters();
        RPCStruct struct = (RPCStruct) para[0].getValue(); // paperFormat, resolution, orientation, format
        RPCStruct struct2 = (RPCStruct) para[1].getValue(); // wmsRequest_0 .. wmsRequest_i

        HttpSession session = ( (HttpServletRequest) this.getRequest() ).getSession( true );
        ViewContext vc = null;
        if ( session.getAttribute( Constants.CURRENTMAPCONTEXT ) != null ) {
            vc = (ViewContext) session.getAttribute( Constants.CURRENTMAPCONTEXT );
        } else {
            vc = (ViewContext) session.getAttribute( "DefaultMapContext" );
        }

        HashMap[] model = createWMSRequestModel( struct2 );
        int[] imageSize = calcImageSize( struct, model[0] );
        int width = imageSize[0];
        //int height = imageSize[1];
        int xmap = imageSize[2];
        int ymap = imageSize[3];
        int mapStart = imageSize[4];

        try {
            GetLegendGraphic legendParam = getLegendRequestParameter();
            //HashMap with legend
            HashMap symbols = getLegend( struct, legendParam, model );
            Rectangle rectLegend = calcLegendSize( struct, symbols );
            Rectangle rectMap = calcMapSize( model, rectLegend, width );
            //sets new size of map und creates the adequate BufferedImage
            model = modifyModelSize( rectMap, model, xmap, ymap );
            HashMap[] copy = createCopy( model );
            BufferedImage biReq = getMap( copy );

            //creates BufferedImage with required size and creates graphic
            BufferedImage bi = createBackgroundImage( rectMap, rectLegend );

            bi = drawMapToBI( biReq, bi, mapStart );
            bi = drawLegendToBI( symbols, bi, ( mapStart + rectMap.width ) );

            saveImage( vc, struct, bi );

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        Debug.debugMethodEnd();
    }

    /**
     * creates a background BufferedImage for the attributes (map, legend, scalebar)
     *
     * @param rectMap
     * @param rectLegend
     * @return
     */
    private BufferedImage createBackgroundImage( Rectangle rectMap, Rectangle rectLegend ) {
        BufferedImage bi = null;

        if ( rectMap.height > rectLegend.height ) {
            bi = new BufferedImage( rectMap.width + rectLegend.width + 40, rectMap.height + 100,
                                    BufferedImage.TYPE_INT_RGB );
        } else {
            bi = new BufferedImage( rectMap.width + rectLegend.width + 40, rectLegend.height + 100,
                                    BufferedImage.TYPE_INT_RGB );
        }

        Graphics g = bi.getGraphics();
        g.setColor( Color.WHITE );
        g.fillRect( 1, 1, bi.getWidth() - 2, bi.getHeight() - 2 );
        g.dispose();

        return bi;
    }

    /**
     * creates copy of the model
     *
     * @param model
     * @return
     */
    private HashMap[] createCopy( HashMap[] model ) {
        HashMap[] copy = new HashMap[model.length];
        synchronized ( this ) {
            for ( int i = 0; i < model.length; i++ ) {
                copy[i] = (HashMap) model[i].clone();
            }
        }

        return copy;
    }

    /**
     * creates model with WMS request from the RPCStruct request parameter.
     *
     * @param struc
     * @return
     */
    private HashMap[] createWMSRequestModel( RPCStruct struc ) {

        RPCMember[] member = struc.getMembers();
        HashMap[] getMR = new HashMap[member.length];

        String request = "";
        //for ( int i = 0; i < member.length; i++ ) {
        for ( int i = member.length - 1; i >= 0; i-- ) {
            request = (String) member[i].getValue();
            getMR[i] = toMap( request );
            StringTokenizer st = new StringTokenizer( request, "?" );
            getMR[i].put( "URL", st.nextToken() );
        }
        return getMR;
    }

    /**
     * calculates images size in dependency on chosen paperFormat, resolution 
     * and orientation.
     *
     * @param struct
     * @param model
     * @return
     */
    private int[] calcImageSize( RPCStruct struct, HashMap model ) {
        double width = 0;
        double height = 0;
        double mapSize = 0;

        double mapWI = Double.parseDouble( (String) model.get( "WIDTH" ) );
        double mapHE = Double.parseDouble( (String) model.get( "HEIGHT" ) );

        String paperFormat = (String) struct.getMember( "paperFormat" ).getValue();
        String resolution = (String) struct.getMember( "resolution" ).getValue();
        String orientation = (String) struct.getMember( "orientation" ).getValue();

        if ( paperFormat.equals( "A4" ) ) {
            if ( orientation.equals( "hoch" ) ) {
                width = 8.2;
                height = 11.6;
                mapSize = 6.5; //legend beside
            } else {
                width = 11.6;
                height = 8.2;
                mapSize = 9.5;
            }
        } else if ( paperFormat.equals( "A3" ) ) {
            if ( orientation.equals( "hoch" ) ) {
                width = 11.6;
                height = 16.5;
                mapSize = 9.8; //legend beside
            } else {
                width = 16.5;
                height = 11.6;
                mapSize = 14;
            }
        } else if ( paperFormat.equals( "A5" ) ) {
            if ( orientation.equals( "hoch" ) ) {
                width = 5.8;
                height = 8.2;
                mapSize = 4.1; //legend beside
            } else {
                width = 8.2;
                height = 5.8;
                mapSize = 6.5;
            }
        }

        width = width * Double.parseDouble( resolution );
        height = height * Double.parseDouble( resolution );
        mapSize = mapSize * Double.parseDouble( resolution );

        int xmap = (int) Math.round( mapSize );
        double fac = mapWI / mapHE;
        int ymap = (int) Math.round( mapSize / fac );
        int mapStart = 45;
        int[] imagesize = new int[] { (int) Math.round( width ), (int) Math.round( height ), xmap,
                                      ymap, mapStart };

        return imagesize;
    }

    /**
     * calculates size (width and height) of legend depending on 
     * chosen paperFormat and resolution.
     *
     * @param struct
     * @param map
     * @return
     */
    private Rectangle calcLegendSize( RPCStruct struct, HashMap map ) {

        String paperFormat = (String) struct.getMember( "paperFormat" ).getValue();
        String resolution = (String) struct.getMember( "resolution" ).getValue();

        String[] layers = (String[]) map.get( "NAMES" );
        BufferedImage[] legs = (BufferedImage[]) map.get( "IMAGES" );

        int w = 0;
        int h = 0;
        int size = 12;
        double tmph1 = 0;
        double tmph2 = 0;

        if ( paperFormat.equals( "A4" ) ) {
            tmph1 = size / 3d;
        } else if ( paperFormat.equals( "A3" ) ) {
            tmph1 += 2 * size / 3;
        }

        double res = Double.parseDouble( resolution );
        tmph2 = ( ( res - 150 ) / 150d ) * size;
        size += (int) Math.round( tmph2 + tmph1 );
        for ( int i = 0; i < layers.length; i++ ) {
            h += ( legs[i].getHeight() + 6 );

            Graphics g = legs[i].getGraphics();
            Font f = new Font( g.getFont().getFontName(), g.getFont().getStyle(), size );
            g.setFont( f );
            Rectangle2D rect = g.getFontMetrics().getStringBounds( layers[i], g );
            g.dispose();

            if ( rect.getWidth() > w ) {
                w = (int) rect.getWidth();
            }
        }
        w += 150;

        return new Rectangle( w, h );
    }

    /**
     * calculates map size in dependency of the chosen paperFormat and resolution. 
     *
     * @param model
     * @param rectLegend
     * @param width
     * @return
     */
    private Rectangle calcMapSize( HashMap[] model, Rectangle rectLegend, int width ) {
        int w = width - rectLegend.width - 20;
        double wi = Double.parseDouble( (String) model[0].get( "WIDTH" ) );
        double he = Double.parseDouble( (String) model[0].get( "HEIGHT" ) );
        double fac = he / wi;
        int h = (int) Math.round( fac * w );

        return new Rectangle( w, h );
    }

    /**
     * draws legend with symbols and name of the layers to the background BufferedImage.
     *
     * @param map
     * @param bi
     * @param start
     * @return
     */
    private BufferedImage drawLegendToBI( HashMap map, BufferedImage bi, int start ) {

        int h = 5;
        Graphics g = bi.getGraphics();
        g.setColor( Color.WHITE );
        String[] layers = (String[]) map.get( "NAMES" );
        BufferedImage[] legs = (BufferedImage[]) map.get( "IMAGES" );

        for ( int i = layers.length - 1; i >= 0; i-- ) {
            g.drawImage( legs[i], start + 10, h, null );
            g.setColor( Color.BLACK );

            if ( legs[i].getHeight() < 50 ) {
                g.drawString( layers[i], start + 25 + legs[i].getWidth(),
                              h + (int) ( legs[i].getHeight() / 1.2 ) );
            }

            h += ( legs[i].getHeight() + 5 );
        }

        g.dispose();
        return bi;
    }

    /**
     * modifies width and height in the WMSGetMapRequest
     * in dependency of the chosen paperFormat and resolution. 
     * New width and height are calculated in the method "calcMapSize".
     *
     * @param rectMap
     * @param model
     * @param width
     * @param height
     * @return
     */
    private HashMap[] modifyModelSize( Rectangle rectMap, HashMap[] model, int width, int height ) {
        int w = rectMap.width;
        double fac = (double) height / (double) width;
        double h = fac * w;

        for ( int i = 0; i < model.length; i++ ) {
            model[i].put( "HEIGHT", "" + (int) Math.round( h ) );
            model[i].put( "WIDTH", "" + w );
        }

        return model;
    }

    /**
     * gets the map with corresponding mapsizes built-in the urls. 
     * @throws UnknownCRSException 
     *
     * @param model
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws XMLParsingException
     * @throws InconsistentRequestException
     * @throws OGCWebServiceException
     * @throws UnknownCRSException
     */
    private BufferedImage getMap( HashMap[] model )
                            throws MalformedURLException, IOException, XMLParsingException,
                            InconsistentRequestException, OGCWebServiceException,
                            UnknownCRSException {
        int w = Integer.parseInt( (String) model[0].get( "WIDTH" ) );
        int h = Integer.parseInt( (String) model[0].get( "HEIGHT" ) );
        BufferedImage bJ = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
        Graphics g = bJ.getGraphics();
        g.setColor( Color.WHITE );
        g.fillRect( 1, 1, bJ.getWidth() - 2, bJ.getHeight() - 2 );
        g.dispose();
        GetMap gmr = null;

        for ( int i = model.length - 1; i >= 0; i-- ) {
            String urls = OWSUtils.validateHTTPGetBaseURL( (String) model[i].remove( "URL" ) );
            String s = URLDecoder.decode( (String) model[i].get( "FORMAT" ),
                                          CharsetUtils.getSystemCharset() );
            model[i].put( "FORMAT", s );
            model[i].put( "ID", "1.1.1" );
            gmr = GetMap.create( model[i] );
            /*
             if ( sessionid != null ) {
                urls = StringTools.concat( 1000, urls, "&sessionID=", sessionid.getValue(), '&' );
             } 
             */
            URL url = new URL( urls + gmr.getRequestParameter() );
            InputStream is = url.openStream();
            BufferedImage tmp = ImageIO.read( is );
            is.close();
            g = bJ.getGraphics();
            g.drawImage( tmp, 0, 0, null );
            g.dispose();
        }

        return bJ;
    }

    /**
     * draws the map to the background BufferedImage
     *
     * @param reqIm
     * @param bi
     * @param mapStart
     * @return
     */
    private BufferedImage drawMapToBI( BufferedImage reqIm, BufferedImage bi, int mapStart ) {
        Graphics g = bi.getGraphics();
        g.drawImage( reqIm, mapStart, mapStart, null );
        g.dispose();

        return bi;
    }

    /**
     * saves image with BufferedImage including map, legend and scalebar in 
     * chosen image format and directory set in mapcontext.xml file.
     * Via the attribute "ImageSource" the image can be called for.
     *
     * @param vc
     * @param struct
     * @param bg
     */
    private void saveImage( ViewContext vc, RPCStruct struct, BufferedImage bg ) {
        String format = (String) struct.getMember( "format" ).getValue();
        format = format.substring( format.indexOf( '/' ) + 1, format.length() );
        GeneralExtension ge = vc.getGeneral().getExtension();
        IOSettings ios = ge.getIOSettings();
        String dir = ios.getPrintDirectory().getDirectoryName();

        long l = IDGenerator.getInstance().generateUniqueID() % 100;
        String file = "Map" + l + '.' + format;

        try {
            String outPut = new URL( dir ).getFile() + '/' + file;
            File f = new File( outPut );
            f.deleteOnExit();
            FileOutputStream fos = new FileOutputStream( f );
            ImageUtils.saveImage( bg, fos, format, 1 );
        } catch ( Exception e ) {
            LOG.logError( "Error occurred in saving image_: ", e );
        }

        String access = ios.getPrintDirectory().getOnlineResource().toExternalForm() + '/' + file;

        this.getRequest().setAttribute( "ImageSource", access );
        this.getRequest().setAttribute( "ImageWidth", new Integer( bg.getWidth() ) );
        this.getRequest().setAttribute( "ImageHeight", new Integer( bg.getHeight() ) );

    }

    /**
     * gets parameters of the LegendRequest
     *
     * @return
     * @throws InconsistentRequestException
     */
    private GetLegendGraphic getLegendRequestParameter()
                            throws InconsistentRequestException {
        HashMap legend = toMap( "VERSION=1.1.1&REQUEST=GetLegendGraphic&FORMAT=image/png&WIDTH=50&HEIGHT=50&"
                                + "EXCEPTIONS=application/vnd.ogc.se_inimage&LAYER=europe:major_rivers&STYLE=default&"
                                + "SLD=file:///C:/Projekte/UmweltInfo/deegreewms/WEB-INF/xml/styles.xml" );
        GetLegendGraphic legendReq = GetLegendGraphic.create( legend );

        return legendReq;
    }

    /**
     * gets legend with layers and styles. The size of the symbols is adjusted to the
     * chosen paperFomrat and resolution.
     *
     * @param struct
     * @param glr
     * @param model
     * @return
     */
    private HashMap getLegend( RPCStruct struct, GetLegendGraphic glr, HashMap[] model ) {

        Debug.debugMethodBegin( this, "setScaleBarURL" );

        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();

        String format = glr.getFormat();
        if ( format.equals( "image/jpg" ) )
            format = "image/jpeg";

        String legendURL = "";

        String paperFormat = (String) struct.getMember( "paperFormat" ).getValue();
        String resolution = (String) struct.getMember( "resolution" ).getValue();
        int h = 15;
        int w = 15;
        double tmph1 = 0;
        double tmpw1 = 0;
        double tmph2 = 0;
        double tmpw2 = 0;

        if ( paperFormat.equals( "A4" ) ) {
            tmph1 = h / 3d;
            tmpw1 = w / 3d;
        } else if ( paperFormat.equals( "A3" ) ) {
            tmph1 = 2 * h / 3d;
            tmpw1 = 2 * w / 3d;
        }

        double res = Double.parseDouble( resolution );
        tmph2 = ( ( res - 150 ) / 150d ) * h;
        tmpw2 = ( ( res - 150 ) / 150d ) * w;

        w += (int) Math.round( tmpw1 + tmpw2 );
        h += (int) Math.round( tmph1 + tmph2 );

        for ( int i = 0; i < model.length; i++ ) {
            if ( model[i].get( "SLD" ) != null ) {
                continue;
            }

            String[] lays = StringTools.toArray( (String) model[i].get( "LAYERS" ), ",", false );

            String style = (String) model[i].get( "STYLES" );
            String[] styles = new String[20];
            for ( int j = 0; j < styles.length; j++ ) {
                styles[j] = "default";
            }
            if ( style != null && !style.trim().equals( "" ) ) {

                //styles = StringTools.toArray(style,",",false);
                if ( ( StringTools.toArray( style, ",", false ) ).length != 0 ) {
                    styles = StringTools.toArray( style, ",", false );
                }

            } else {
                styles = new String[lays.length];
                for ( int j = 0; j < styles.length; j++ ) {
                    styles[j] = "default";
                }
            }
            if ( styles == null ) {
                styles = new String[20];
                for ( int j = 0; j < styles.length; j++ ) {
                    styles[j] = "default";
                }
            }

            for ( int j = 0; j < lays.length; j++ ) {
                String layer = lays[j];
                legendURL = createLegendURL( w, h, layer, styles[j], format, glr, model[i] );
                BufferedImage legendGraphic = null;
                try {
                    InputStream is = new URL( legendURL ).openStream();
                    legendGraphic = ImageIO.read( is );
                    is.close();
                } catch ( Exception e ) {
                    System.out.println( "\n\nLegend graphic for layer '" + layer
                                        + "' is not available. Skipping.\n\n" );
                    legendGraphic = null;
                    e.printStackTrace();
                }
                if ( legendGraphic != null ) {
                    list1.add( layer );
                    list2.add( legendGraphic );
                }
            }
        }

        Debug.debugMethodEnd();
        String[] layers = (String[]) list1.toArray( new String[list1.size()] );
        BufferedImage[] legs = (BufferedImage[]) list2.toArray( new BufferedImage[list2.size()] );
        HashMap map = new HashMap();
        map.put( "NAMES", layers );
        map.put( "IMAGES", legs );

        return map;
    }

    /**
     * creates legend URL with corresponding key values.
     * 
     * @param w
     * @param h
     * @param layer
     * @param style
     * @param format
     * @param glr
     * @param model
     * @return
     */
    private String createLegendURL( int w, int h, String layer, String style, String format,
                                   GetLegendGraphic glr, HashMap model ) {

        String s = null;
        try {
            if ( "default".equals( style ) ) {
                style = "";
            }
            String url = OWSUtils.validateHTTPGetBaseURL( (String) model.get( "URL" ) );
            s = StringTools.concat( 500, url, "service=WMS", "&VERSION=", glr.getVersion(),
                                    "&REQUEST=GetLegendGraphic", "&FORMAT=", format, "&WIDTH=", w,
                                    "&HEIGHT=", h,
                                    "&EXCEPTIONS=application/vnd.ogc.se_inimage&LAYER=",
                                    URLDecoder.decode( layer, CharsetUtils.getSystemCharset() ),
                                    "&STYLE=", style );
            // insufficient information available for user restrictions
            // TODO  
//            HttpServletRequest req = (HttpServletRequest) getRequest();
//            if ( req.getUserPrincipal() != null ) {
//                String user = req.getUserPrincipal().getName();
//                if ( user.indexOf( "\\" ) > 1 ) {
//                    String[] us = StringTools.toArray( user, "\\", false );
//                    user = us[us.length - 1];
//                }
//                s = StringTools.concat( 500, s, "&user=", user );
//            }

        } catch ( UnsupportedEncodingException e ) {
            // should never happen
            e.printStackTrace();
        }
        return s;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 
 $Log: PrintListener.java,v $
 Revision 1.18  2006/12/04 10:02:46  mays
 remove user info from GetLegendGraphic request

 Revision 1.17  2006/12/01 14:31:36  mays
 clean up file header

 Revision 1.16  2006/12/01 14:39:08  mays
 try to use current map context. only if this is not available, use default map context;
 clean up javadoc
 
 Revision 1.10  2006/08/07 14:39:08  poth
 never used methods removed / class marked as deprecated

 Revision 1.9  2006/06/29 10:29:37  poth
 *** empty log message ***

 Revision 1.8  2006/06/25 20:35:42  poth
 support for forwarding UserPrincipal as part of GetCapabilitities, GetMap and GetLegendGraphicRequests

 Revision 1.7  2006/04/20 12:15:12  ncho
 SN: added line to avoid ArrayIndexOutOfBoundsException

 Revision 1.6  2006/04/19 09:06:04  ncho
 SN: fixed URL access to print

 Revision 1.5  2006/04/07 09:30:00  taddei
 sn fix for release 2006-04

 Revision 1.1  2006/02/05 09:30:12  poth
 *** empty log message ***


 Revision 1.16  2005/12/06 13:45:20  poth
 System.out.println substituted by logging api

 Revision 1.15  2005/09/27 19:53:19  poth
 no message

 Revision 1.14  2005/04/06 13:44:07  poth
 no message

 Revision 1.13  2005/03/18 16:31:32  poth
 no message

 Revision 1.12  2005/03/12 10:45:03  poth
 no message

 Revision 1.11  2005/03/09 11:55:46  mschneider
 *** empty log message ***

 Revision 1.10  2005/02/25 19:03:02  poth
 no message

 Revision 1.9  2005/02/25 11:19:16  poth
 no message

 Revision 1.8  2005/02/24 20:04:04  poth
 no message

 Revision 1.7  2005/02/21 11:24:33  poth
 no message

 Revision 1.6  2005/02/18 20:54:18  poth
 no message

 Revision 1.5  2005/02/17 16:54:51  poth
 no message

 Revision 1.4  2005/02/17 11:52:11  poth
 no message

 Revision 1.3  2005/02/16 13:12:18  poth
 no message

 Revision 1.2  2005/02/16 12:39:32  poth
 no message

 Revision 1.1.1.1  2005/01/05 10:30:00  poth
 no message

 Revision 1.1  2004/08/18 06:37:01  taddei
 Listener with minor bug fixes: catching exceptions etc...

 ********************************************************************** */