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
package org.deegree.portal.common.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperRunManager;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BasicUUIDFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.Encoders;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.portal.PortalException;
import org.deegree.portal.PortalUtils;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.Style;
import org.deegree.portal.context.ViewContext;
import org.deegree.security.drm.model.User;
import org.xml.sax.SAXException;

/**
 * performs a print request/event by creating a PDF document from 
 * the current map. For this JASPER is used. Well known parameters
 * that can be passed to a jaser report are:<br>
 * <ul>
 *  <li>MAP</li>
 *  <li>LEGEND</li>
 *  <li>DATE</li>
 *  <li>MAPSCALE</li>
 * </ul>
 * <br>Additionaly parameters named 'TA:XXXX' can be used. deegree
 * will create a k-v-p for each TA:XXXX passed as part of RPC.
 * 
 *
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/11/27 09:07:53 $
 *
 * @since 2.0
 */
public abstract class AbstractSimplePrintListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( AbstractSimplePrintListener.class );

    private static final double DEFAULT_PIXEL_SIZE = 0.00028;

    /**
     * @param e 
     */
    public void actionPerformed( FormEvent e ) {
        RPCWebEvent rpc = (RPCWebEvent) e;
        try {
            validate( rpc );
        } catch ( Exception ex ) {
            LOG.logError( ex.getMessage(), ex );
            gotoErrorPage( ex.getMessage() );
        }

        ViewContext vc = getViewContext( rpc );
        if ( vc == null ) {
            LOG.logError( "no valid ViewContext available; maybe your session has reached timeout limit" ); //$NON-NLS-1$
            gotoErrorPage( Messages.getString( "AbstractSimplePrintListener.MISSINGCONTEXT" ) );
            setNextPage( "igeoportal/error.jsp" );
            return;
        }
        try {
            printMap( vc, rpc );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            LOG.logError( ex.getMessage(), ex );
            gotoErrorPage( ex.getMessage() );
            setNextPage( "igeoportal/error.jsp" );
        }
    }

    /**
     * 
     * @param vc
     * @param rpc
     * @throws PortalException
     * @throws IOException 
     * @throws SAXException 
     * @throws XMLParsingException 
     * @throws InconsistentRequestException 
     * @throws UnknownCRSException 
     */
    private void printMap( ViewContext vc, RPCWebEvent rpc )
                            throws PortalException, IOException, InconsistentRequestException,
                            XMLParsingException, SAXException, UnknownCRSException {

        List<String> getMap = createGetMapRequests( vc );
        String image = performGetMapRequests( getMap );

        String legend = accessLegend( createLegendURLs( vc ) );

        String format = (String) rpc.getRPCMethodCall().getParameters()[0].getValue();

        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[1].getValue();
        String printTemplate = (String) struct.getMember( "TEMPLATE" ).getValue();
        ServletContext sc = ( (HttpServletRequest) getRequest() ).getSession( true ).getServletContext();
        String path = sc.getRealPath( "/WEB-INF/igeoportal/print" ) + '/' + printTemplate
                      + ".jasper";
        String pathx = sc.getRealPath( "/WEB-INF/igeoportal/print" ) + '/' + printTemplate
                       + ".jrxml";

        Map parameter = new HashMap();
        parameter.put( "MAP", image );
        parameter.put( "LEGEND", legend );
        SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault() );
        // TODO deprecated - will be remove in future versions
        parameter.put( "DATUM", sdf.format( new GregorianCalendar().getTime() ) );
        //--------------------------------------------------------
        parameter.put( "DATE", sdf.format( new GregorianCalendar().getTime() ) );
        double scale = calcScale( pathx, getMap.get( 0 ) );
        parameter.put( "MAPSCALE", "" + Math.round( scale ) );
        LOG.logDebug( "print map scale: ", scale );
        // set text area values
        RPCMember[] members = struct.getMembers();
        for ( int i = 0; i < members.length; i++ ) {
            if ( members[i].getName().startsWith( "TA:" ) ) {
                String s = members[i].getName().substring( 3, members[i].getName().length() );
                String val = (String) members[i].getValue();
                if ( val != null ) {
                    val = new String( val.getBytes(), "UTF-8" );
                }
                LOG.logDebug( "text area name: ", s );
                LOG.logDebug( "text area value: ", val );
                parameter.put( s, val );
            }
        }

        if ( "application/pdf".equals( format ) ) {

            // create the pdf
            Object result = null;
            try {
                JRDataSource ds = new JREmptyDataSource();
                result = JasperRunManager.runReportToPdf( path, parameter, ds );
            } catch ( JRException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new PortalException(
                                           Messages.getString( "AbstractSimplePrintListener.REPORTCREATION" ) );
            } finally {
                File file = new File( image );
                file.delete();
                file = new File( legend );
                file.delete();
            }

            forwardPDF( result );

        } else if ( "image/png".equals( format ) ) {

            // create the image
            Image result = null;
            try {
                JRDataSource ds = new JREmptyDataSource();
                JasperPrint prt = JasperFillManager.fillReport( path, parameter, ds );
                result = JasperPrintManager.printPageToImage( prt, 0, 1 );
            } catch ( JRException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new PortalException(
                                           Messages.getString( "AbstractSimplePrintListener.REPORTCREATION" ) );
            } finally {
                File file = new File( image );
                file.delete();
                file = new File( legend );
                file.delete();
            }

            forwardImage( result, format );

        }
    }

    private double calcScale( String path, String getmap )
                            throws InconsistentRequestException, XMLParsingException, IOException,
                            SAXException, UnknownCRSException {
        Map model = KVP2Map.toMap( getmap );
        model.put( "ID", "22" );
        GetMap gm = GetMap.create( model );

        File file = new File( path );
        XMLFragment xml = new XMLFragment( file.toURL() );

        String xpathW = "detail/band/image/reportElement[./@key = 'image-1']/@width";
        String xpathH = "detail/band/image/reportElement[./@key = 'image-1']/@height";

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        int w = XMLTools.getRequiredNodeAsInt( xml.getRootElement(), xpathW, nsc );
        int h = XMLTools.getRequiredNodeAsInt( xml.getRootElement(), xpathH, nsc );

        CoordinateSystem crs = CRSFactory.create( gm.getSrs() );
        return MapUtils.calcScale( w, h, gm.getBoundingBox(), crs, DEFAULT_PIXEL_SIZE );
    }

    /**
     * accesses the Legende URLs passed, draws the result onto an image that
     * are stored in a temporary file. The name of the file will be returned.
     * @param name
     * @return
     */
    private String accessLegend( List<String[]> legends )
                            throws IOException {
        int width = Integer.parseInt( getInitParameter( "LEGENDWIDTH" ) );
        int height = Integer.parseInt( getInitParameter( "LEGENDHEIGHT" ) );
        BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics g = bi.getGraphics();
        g.setColor( Color.BLACK );
        int k = 10;

        for ( int i = 0; i < legends.size(); i++ ) {
            String[] s = legends.get( i );
            if ( s[1] != null ) {
                LOG.logDebug( "reading legend: " + s[1] );
                Image img = ImageUtils.loadImage( new URL( s[1] ) );
                if ( img.getWidth( null ) < 50 ) {
                    // it is assumed that no label iss assigned
                    g.drawImage( img, 0, k, null );
                    g.drawString( s[0], img.getWidth( null ) + 10, k + img.getHeight( null ) / 2 );
                } else {
                    g.drawImage( img, 0, k, null );
                }
                k = k + img.getHeight( null ) + 10;
            } else {
                g.drawString( "- " + s[0], 0, k + 10 );
                k = k + 20;
            }
        }
        g.dispose();

        return storeImage( bi );
    }

    /**
     * performs the GetMap requests passed, draws the result onto an image that
     * are stored in a temporary file. The name of the file will be returned.
     * @param list
     * @return
     * @throws PortalException 
     * @throws IOException 
     */
    private String performGetMapRequests( List<String> list )
                            throws PortalException, IOException {

        Map map = KVP2Map.toMap( list.get( 0 ) );
        map.put( "ID", "ww" );
        GetMap getMap = null;
        try {
            getMap = GetMap.create( map );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = Messages.format( "AbstractSimplePrintListener.GETMAPCREATION", list.get( 0 ) );
            throw new PortalException( s );
        }
        BufferedImage bi = new BufferedImage( getMap.getWidth(), getMap.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics g = bi.getGraphics();
        for ( int i = 0; i < list.size(); i++ ) {
            URL url = new URL( list.get( i ) );
            Image img = ImageUtils.loadImage( url );
            g.drawImage( img, 0, 0, null );
        }
        g.dispose();
        return storeImage( bi );
    }

    /**
     * stores the passed image in the defined temporary directory and returns
     * the dynamicly created filename
     * @param bi
     * @return
     * @throws IOException
     */
    private String storeImage( BufferedImage bi )
                            throws IOException {
        BasicUUIDFactory fac = new BasicUUIDFactory();
        String s = fac.createUUID().toANSIidentifier();
        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();
        String fileName = StringTools.concat( 300, sc.getRealPath( tempDir ), '/', s, ".png" );

        FileOutputStream fos = new FileOutputStream( new File( fileName ) );
        Encoders.encodePng( fos, bi );
        fos.close();

        return fileName;
    }

    private void forwardPDF( Object result )
                            throws PortalException {
        // must be a byte array
        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        BasicUUIDFactory fac = new BasicUUIDFactory();
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();

        String fileName = fac.createUUID().toANSIidentifier();
        String s = StringTools.concat( 200, sc.getRealPath( tempDir ), '/', fileName, ".pdf" );
        try {
            RandomAccessFile raf = new RandomAccessFile( s, "rw" );
            raf.write( (byte[]) result );
            raf.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "could not write temporary pdf file: " + s, e );
            throw new PortalException( Messages.format( "AbstractSimplePrintListener.PDFCREATION",
                                                        s ), e );
        }

        getRequest().setAttribute( "PDF", StringTools.concat( 200, tempDir, fileName, ".pdf" ) );
    }

    private void forwardImage( Image result, String format )
                            throws PortalException {

        format = format.substring( format.indexOf( '/' ) + 1 );

        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith( "/" ) ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith( "/" ) ) {
            tempDir = tempDir.substring( 1, tempDir.length() );
        }
        BasicUUIDFactory fac = new BasicUUIDFactory();
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();

        String fileName = fac.createUUID().toANSIidentifier();
        String s = StringTools.concat( 200, sc.getRealPath( tempDir ), "/", fileName, ".", format );
        try {
            // make sure we have a BufferedImage
            if ( !( result instanceof BufferedImage ) ) {
                BufferedImage img = new BufferedImage( result.getWidth( null ),
                                                       result.getHeight( null ),
                                                       BufferedImage.TYPE_INT_ARGB );

                Graphics g = img.getGraphics();
                g.drawImage( result, 0, 0, null );
                g.dispose();
                result = img;
            }

            ImageUtils.saveImage( (BufferedImage) result, s, 1 );
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "could not write temporary pdf file: " + s, e );
            throw new PortalException( Messages.format( "AbstractSimplePrintListener.PDFCREATION",
                                                        s ), e );
        }

        getRequest().setAttribute( "PDF", StringTools.concat( 200, tempDir, fileName, ".", format ) );
    }

    /**
     * fills the passed PrintMap request template with required values
     * @param vc
     * @param rpc
     * @param template
     * @return
     * @throws PortalException 
     */
    private List<String> createGetMapRequests( ViewContext vc ) {

        User user = getUser();

        // set boundingbox/envelope
        Point[] points = vc.getGeneral().getBoundingBox();

        int width = Integer.parseInt( getInitParameter( "WIDTH" ) );
        int height = Integer.parseInt( getInitParameter( "HEIGHT" ) );

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "&BBOX=" ).append( points[0].getX() ).append( ',' );
        sb.append( points[0].getY() ).append( ',' ).append( points[1].getX() );
        sb.append( ',' ).append( points[1].getY() ).append( "&WIDTH=" );
        sb.append( width ).append( "&HEIGHT=" ).append( height );
        if ( user != null ) {
            sb.append( "&user=" ).append( user.getName() );
            sb.append( "&password=" ).append( user.getPassword() );
        }
        String[] reqs = PortalUtils.createBaseRequests( vc );
        List<String> list = new ArrayList<String>( reqs.length );
        for ( int i = 0; i < reqs.length; i++ ) {
            list.add( reqs[i] + sb.toString() );
            LOG.logDebug( "GetMap request:", reqs[i] + sb.toString() );
        }

        return list;
    }

    /**
     * returns <code>null</code> and should be overwirtten by an extending class 
     * @return
     */
    protected User getUser() {
        return null;
    }

    /**
     * reads the view context to print from the users session 
     * @param rpc
     * @return
     */
    abstract protected ViewContext getViewContext( RPCWebEvent rpc );

    /**
     * returns legend access URLs for all visible layers of the passed
     * view context. If a visible layer does not define a LegendURL
     * @param vc
     * @return
     */
    private List<String[]> createLegendURLs( ViewContext vc ) {
        Layer[] layers = vc.getLayerList().getLayers();
        List<String[]> list = new ArrayList<String[]>();
        for ( int i = 0; i < layers.length; i++ ) {
            if ( !layers[i].isHidden() ) {
                Style style = layers[i].getStyleList().getCurrentStyle();
                String[] s = new String[2];
                s[0] = layers[i].getTitle();
                if ( style.getLegendURL() != null ) {
                    s[1] = style.getLegendURL().getOnlineResource().toExternalForm();
                }
                list.add( s );
            }
        }
        return list;
    }

    /**
     * validates the incoming request/RPC if conatins all required elements
     * @param rpc
     * @throws PortalException
     */
    protected void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[1].getValue();
        if ( struct.getMember( "TEMPLATE" ) == null ) {
            throw new PortalException( Messages.getString( "portal.common.control.VALIDATIONERROR" ) );
        }

        if ( rpc.getRPCMethodCall().getParameters()[0].getValue() == null ) {
            throw new PortalException( Messages.getString( "portal.common.control.VALIDATIONERROR" ) );
        }
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: AbstractSimplePrintListener.java,v $
 Revision 1.11  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.10  2006/11/16 14:15:40  schmitz
 Added png download support.

 Revision 1.9  2006/10/17 20:31:20  poth
 *** empty log message ***

 Revision 1.8  2006/09/25 12:47:00  poth
 bug fixes - map scale calculation

 Revision 1.7  2006/08/04 09:24:35  poth
 map scale rounded to integer and passed as string to reports

 Revision 1.6  2006/07/25 06:23:16  poth
 support for scale added (will be passed as MAPSCALE) to the jasper report

 Revision 1.5  2006/07/21 09:32:02  poth
 *** empty log message ***

 Revision 1.4  2006/07/14 14:31:39  poth
 logging and URL decoding added

 Revision 1.3  2006/07/12 10:50:30  taddei
 substituted "mm" by "MM" for Date (by HP)

 Revision 1.2  2006/07/12 10:25:35  poth
 fix for date format / date now is available in jasper templates through parameter DATUM (marked as deprecated) and DATE

 Revision 1.1  2006/05/21 11:59:36  poth
 initial load up

 Revision 1.2  2006/05/20 15:55:53  poth
 bug fix / support for legend visualization

 Revision 1.1  2006/05/19 15:13:57  poth
 simple printlistener initial loadup


 ********************************************************************** */