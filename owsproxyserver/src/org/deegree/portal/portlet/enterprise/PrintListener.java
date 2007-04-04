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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BasicUUIDFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wmps.WMPService;
import org.deegree.ogcwebservices.wmps.WMPServiceFactory;
import org.deegree.ogcwebservices.wmps.configuration.WMPSConfiguration;
import org.deegree.ogcwebservices.wmps.configuration.WMPSConfigurationDocument;
import org.deegree.ogcwebservices.wmps.operation.PrintMap;
import org.deegree.ogcwebservices.wmps.operation.PrintMapResponse;
import org.deegree.portal.PortalException;
import org.deegree.portal.context.Layer;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * performs a print request/event by creating a PDF document from 
 * the current map
 * 
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/10/12 15:46:19 $
 *
 * @since 2.0
 */
public class PrintListener extends AbstractListener {

    private static ILogger LOG = LoggerFactory.getLogger( PrintListener.class );

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
            LOG.logError( "no valid ViewContext available; maybe your session has reached tieout limit" );
            gotoErrorPage("no valid ViewContext available; maybe your session has reached tieout limit" );
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
     */
    private void printMap( ViewContext vc, RPCWebEvent rpc )
                            throws PortalException {

        String template = readRequestTemplate();
        
        template = fillTemplate( vc, rpc, template );
        
        StringReader sr = new StringReader( template );
        XMLFragment xml = new XMLFragment();
        try {
            xml.load( sr, XMLFragment.DEFAULT_URL );
        } catch ( Exception e ) {
           LOG.logError( e.getMessage(), e );
           throw new PortalException( "could not create a DOM object from PrintMap request Template", e );
        } 
        
        PrintMap printMap = null; 
        try {
            printMap =PrintMap.create( xml.getRootElement() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not parse PrintMap request.", e );
        } 
        
        if ( !WMPServiceFactory.isInitialized() ) {
            WMPSConfiguration wmpsConf = readWMPSConfiguration();
            WMPServiceFactory.setConfiguration( wmpsConf );
        }
        WMPService wmps = WMPServiceFactory.getService();
        Object result = null;
        try {
            result = wmps.doService( printMap );
        } catch ( OGCWebServiceException e ) {
            e.printStackTrace();
            LOG.logError(  "could not perform PrintMap request ", e );
            throw new PortalException( "could not perform PrintMap request ", e );
        }
        if ( result instanceof PrintMapResponse ) {
            // TODO
            // handle result from asynchronous request processing
        } else {
            forwardPDF( result );            
        }
        
    }

    private void forwardPDF( Object result )
                            throws PortalException {
        // must be a byte array
        String tempDir = getInitParameter( "TEMPDIR" );
        if ( !tempDir.endsWith("/") ) {
            tempDir = tempDir + '/';
        }
        if ( tempDir.startsWith("/") ) {
            tempDir =  tempDir.substring( 1, tempDir.length() );
        }
        BasicUUIDFactory fac = new BasicUUIDFactory();
        ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();
        
        String s = StringTools.concat( 200, sc.getRealPath( tempDir ), '/', 
                                       fac.createUUID().toANSIidentifier(), ".pdf" );
        try {
            RandomAccessFile raf = new RandomAccessFile( s, "rw" );
            raf.write( (byte[])result );
            raf.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "could not write temporary pdf file: " + s, e );
            throw new PortalException( "could not write temporary pdf file: " + s, e );
        }
        
        getRequest().setAttribute( "PDF", StringTools.concat( 200, tempDir, 
                                       fac.createUUID().toANSIidentifier(), ".pdf" ) );
    }

    /**
     * reads WMPS configuration from the source defined in the listeners 
     * init parameters ( 'WMPSCONFIG' )
     * 
     * @return
     * @throws PortalException
     */
    private WMPSConfiguration readWMPSConfiguration()
                            throws PortalException {
        String config = getInitParameter( "WMPSCONFIG" );
        if ( config == null ) {
            throw new PortalException( "no WMPS configuration defined for PrintListener" );
        }
        File file = new File( config );
        if ( !file.isAbsolute() ) {
            ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();
            file = new File( sc.getRealPath( config ) );
        }        
        WMPSConfiguration wmpsConf = null;
        try {
            WMPSConfigurationDocument wmpsConfDoc = new WMPSConfigurationDocument();
            wmpsConfDoc.load( file.toURL() );
            wmpsConf = wmpsConfDoc.parseConfiguration();
        } catch ( Exception e ) {
            LOG.logError( "could not read/create WMPSConfiguration: " + file, e );
            throw new PortalException( "could not read/create WMPSConfiguration: " + file, e ); 
        }
        return wmpsConf;
    }

    /**
     * fills the passed PrintMap request template with required values
     * @param vc
     * @param rpc
     * @param template
     * @return
     */
    private String fillTemplate( ViewContext vc, RPCWebEvent rpc, String template ) {
        // set boundingbox/envelope
        Point[] points = vc.getGeneral().getBoundingBox();
        Envelope env = GeometryFactory.createEnvelope( points[0].getX(), points[0].getY(),
                                                       points[1].getX(), points[1].getY(),
                                                       points[0].getCoordinateSystem() );
        String envS = GMLGeometryAdapter.exportAsEnvelope( env ).toString();        
        template = StringTools.replace( template, "$ENV$", envS, false );
        
        // set layers
        StringBuffer lys = new StringBuffer( 1000 );
        Layer[] layers = vc.getLayerList().getLayers();
        for ( int i = 0; i < layers.length; i++ ) {
            if ( !layers[i].isHidden() ) {
                lys.append( "<sld:NamedLayer>" );
                lys.append( "<sld:Named>" );
                lys.append( layers[i] );
                lys.append( "</sld:Named>" );
                lys.append( "<sld:NamedStyle>" );
                lys.append( "<sld:Named>" );
                lys.append( layers[i].getStyleList().getCurrentStyle().getName() );
                lys.append( "</sld:Named>" );
                lys.append( "</sld:NamedStyle>" );
                lys.append( "</sld:NamedLayer>" );
            }
        }
        template = StringTools.replace( template, "$LAYERS$", lys.toString(), false );
        
        // set print template
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[0].getValue();
        String printTemplate = (String)struct.getMember( "TEMPLATE" ).getValue();
        template = StringTools.replace( template, "$TEMPLATE$", printTemplate, false );
        
        // set text area values
        StringBuffer ta = new StringBuffer( 1000 );
        RPCMember[] members = struct.getMembers();
        for ( int i = 0; i < members.length; i++ ) {
            if ( members[i].getName().startsWith( "TA:" ) ) {
                ta.append( "<TextArea>" );
                ta.append( "<Name>" );
                ta.append( members[i].getName().substring( 3, members[i].getName().length() ) );
                ta.append( "</Name>" );
                ta.append( "<Text>" );
                ta.append( members[i].getValue() );
                ta.append( "</Text>" );
                ta.append( "</TextArea>" );
            }
        }
        template = StringTools.replace( template, "$TEXTAREAS$", ta.toString(), false );
        return template;
    }

    /**
     * read PrintMap request template from the source defined in the listeners 
     * init parameters ( 'PRINTMAPTEMPLATE' )
     * @return
     * @throws PortalException
     */
    private String readRequestTemplate()
                            throws PortalException {
        String reqTemplate = getInitParameter( "PRINTMAPTEMPLATE" );
        if ( reqTemplate == null ) {
            throw new PortalException( "not PrintMap request template defined for PrintListener" );
        }
        File file = new File( reqTemplate );
        if ( !file.isAbsolute() ) {
            ServletContext sc = ( (HttpServletRequest) this.getRequest() ).getSession( true ).getServletContext();         
            file = new File( sc.getRealPath( reqTemplate ) );
        }
        byte[] b;
        try {
            RandomAccessFile raf = new RandomAccessFile( file, "r" );
            b = new byte[(int) raf.length()];
            raf.read( b );
            raf.close();
        } catch ( IOException e ) {
            String s = "Could not read PrintMap request template: " + file;
            LOG.logError( s, e );
            throw new PortalException( s );
        }
        return new String( b );
    }

    /**
     * reads the view context to print from the users session 
     * @param rpc
     * @return
     */
    private ViewContext getViewContext( RPCWebEvent rpc ) {
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[0].getValue();
        String mmid = (String) struct.getMember( "MAPMODELID" ).getValue();
        HttpSession session = ( (HttpServletRequest) getRequest() ).getSession();
        return IGeoPortalPortletPerform.getCurrentViewContext( session, mmid );
    }

    /**
     * validates the incoming request/RPC if conatins all required elements
     * @param rpc
     * @throws PortalException
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCStruct struct = (RPCStruct) rpc.getRPCMethodCall().getParameters()[0].getValue();
        if ( struct.getMember( "TEMPLATE" ) == null ) {
            throw new PortalException( "struct member: 'TEMPLATE' must be set" );
        }
        if ( struct.getMember( "MAPMODELID" ) == null ) {
            throw new PortalException( "struct member: 'MAPMODELID' must be set" );
        }        
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PrintListener.java,v $
Revision 1.9  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.8  2006/06/07 12:37:51  deshmukh
Reset the changes made to the WMPS.

Revision 1.6  2006/05/19 07:23:56  poth
bug fixes

Revision 1.5  2006/05/18 16:50:04  poth
*** empty log message ***

Revision 1.4  2006/05/17 17:05:44  poth
implementation completed (not tested)



********************************************************************** */