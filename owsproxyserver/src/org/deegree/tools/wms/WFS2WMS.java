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
package org.deegree.tools.wms;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.graphics.sld.AbstractLayer;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.w3c.dom.Node;

/**
 * creates a deegree WMS configuration document or a Layer section from
 * a WFS capabilities document. The datasource type for each layer will
 * be LOCALWFS. Also a style with random color(s) will be created, assigned
 * to the layers and stored in a xml document named $OUTFILE_BASE$_styles.xml. 
 * TODO
 * support for usage of an already existing WMS configuration document
 * TODO
 * determine geometry types of the feature types registered within the
 * wfs capabilities document 
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/10/17 20:31:18 $
 *
 * @since 2.0
 */
public class WFS2WMS {

    private static NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    private ILogger LOG = LoggerFactory.getLogger( WFS2WMS.class );

    private URL xsl = WFS2WMS.class.getResource( "wfs2wms.xsl" );

    private Properties prop = null;

    public WFS2WMS( Properties prop ) {
        this.prop = prop;
    }

    public static double getMinX( Node node ) {
        double v = -180;
        try {
            String s = XMLTools.getNodeAsString( node, ".", nsc, "-180 -90" );
            String[] t = s.split( " " );
            v = Double.parseDouble( t[0] );
        } catch ( XMLParsingException e ) {
            e.printStackTrace();
        }
        return v;
    }

    public static double getMinY( Node node ) {
        double v = -90;
        try {
            String s = XMLTools.getNodeAsString( node, ".", nsc, "-180 -90" );
            String[] t = s.split( " " );
            v = Double.parseDouble( t[1] );
        } catch ( XMLParsingException e ) {
            e.printStackTrace();
        }
        return v;
    }

    public static double getMaxX( Node node ) {
        double v = 180;
        try {
            String s = XMLTools.getNodeAsString( node, ".", nsc, "180 90" );
            String[] t = s.split( " " );
            v = Double.parseDouble( t[0] );
        } catch ( XMLParsingException e ) {
            e.printStackTrace();
        }
        return v;
    }

    public static double getMaxY( Node node ) {
        double v = 90;
        try {
            String s = XMLTools.getNodeAsString( node, ".", nsc, "180 90" );
            String[] t = s.split( " " );
            v = Double.parseDouble( t[1] );
        } catch ( XMLParsingException e ) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * append all required namespace definition to the root element 
     * @param xml
     * @return
     * @throws InvalidCapabilitiesException
     */
    private XMLFragment addNamespaces( XMLFragment wms, WFSCapabilities capa ) {
        WFSFeatureType[] fts = capa.getFeatureTypeList().getFeatureTypes();
        for ( int i = 0; i < fts.length; i++ ) {
            QualifiedName qn = fts[i].getName();
            XMLTools.appendNSBinding( wms.getRootElement(), qn.getPrefix(), qn.getNamespace() );
        }
        return wms;
    }

    /**
     * creates a style for each feature type registered in a WFS capabilities
     * document
     * 
     * @param wfs
     * @return
     */
    private List<UserStyle> createStyles( WFSCapabilities capa ) {
        List<UserStyle> styles = new ArrayList<UserStyle>();
        Map<QualifiedName, Integer> types = determineGeometryType( capa );

        WFSFeatureType[] fts = capa.getFeatureTypeList().getFeatureTypes();

        UserStyle st = null;
        for ( int i = 0; i < fts.length; i++ ) {
            QualifiedName qn = fts[i].getName();
            int type = types.get( qn );
            switch ( type ) {
            case 1: {
                st = (UserStyle) StyleFactory.createPointStyle( "square", ColorUtils.getRandomColor( false ), 
                                                                ColorUtils.getRandomColor( false ), 1,
                                                                1, 10, 10, 0, Double.MAX_VALUE );
                break;
            }
            case 2: {
                st = (UserStyle) StyleFactory.createLineStyle( ColorUtils.getRandomColor( false ),
                                                               4, 1, 0, Double.MAX_VALUE );
                break;
            }
               
            case 3: {
                st = (UserStyle) StyleFactory.createPolygonStyle( ColorUtils.getRandomColor( false ),
                                                                  1,
                                                                  ColorUtils.getRandomColor( false ),
                                                                  1, 1, 0, Double.MAX_VALUE );
                break;
            }
            }
            st.setName( "default:" + qn.getAsString() );
            styles.add( st );
        }

        return styles;
    }

    /**
     * 
     * @param styles
     * @return
     */
    private StyledLayerDescriptor createSLD( List<UserStyle> styles ) {

        UserStyle[] us = styles.toArray( new UserStyle[styles.size()] );
        NamedLayer nl = new NamedLayer( "defaultstyle", null, us );
        return new StyledLayerDescriptor( new AbstractLayer[] { nl }, "1.0.0" );

    }

    /**
     * returns the geometrytype of each feature type registered within the
     * passed WFS capabilities document<br>
     * <ul>
     *  <li>1 = point or multi point
     *  <li>2 = curve or multi curve
     *  <li>3 = surface or multi surface 
     * </ul>
     * @param capa
     * @return
     */
    private Map<QualifiedName, Integer> determineGeometryType( WFSCapabilities capa ) {

        Map<QualifiedName, Integer> types = new HashMap<QualifiedName, Integer>();

        WFSFeatureType[] fts = capa.getFeatureTypeList().getFeatureTypes();
        for ( int i = 0; i < fts.length; i++ ) {
            QualifiedName qn = fts[i].getName();
            // TODO
            // get real geometry type
            types.put( qn, 3 );
        }

        return types;
    }

    /**
     * method the starts running the conversation
     * 
     * @throws Exception
     */
    public void run() throws Exception {
        
        String out = prop.getProperty( "-outFile" );
        File file = new File( out );
        int pos = file.getName().lastIndexOf('.');
        String styleDoc = file.getName().substring( 0, pos ) + "_styles.xml";
        
        HashMap param = new HashMap();
        param.put( "PARENTLAYER", prop.getProperty( "-parentLayer" ) );
        param.put( "MINX", prop.getProperty( "-minx" ) );
        param.put( "MINY", prop.getProperty( "-miny" ) );
        param.put( "MAXX", prop.getProperty( "-maxx" ) );
        param.put( "MAXY", prop.getProperty( "-maxy" ) );
        param.put( "SRS", prop.getProperty( "-srs" ) );
        if ( "true".equals( prop.getProperty( "-full" ) ) ) {
            param.put( "WMSCAPS", "1" );
        }
        param.put( "STYLEDOC", styleDoc );        

        LOG.logInfo( "XSLT-parameter: ", param );

        XSLTDocument outXSLSheet = new XSLTDocument();
        outXSLSheet.load( xsl );

        file = new File( prop.getProperty( "-wfsCaps" ) );
        param.put( "WFSCAPS", file.toURL().toExternalForm() );
        XMLFragment doc = new XMLFragment();                
        doc.load( file.toURL() );

        XMLFragment resultDocument = outXSLSheet.transform( doc, null, null, param );

        WFSCapabilitiesDocument wfsdoc = new WFSCapabilitiesDocument();
        wfsdoc.setRootElement( doc.getRootElement() );
        WFSCapabilities capa = (WFSCapabilities) wfsdoc.parseCapabilities();

        resultDocument = addNamespaces( resultDocument, capa );

        List styles = createStyles( capa );
        StyledLayerDescriptor sld = createSLD( styles );
        
        String s = prop.getProperty( "-outFile" ).replace( ".xml", "_styles.xml" );
        file = new File( s );
        FileOutputStream fos = new FileOutputStream( file );
        fos.write( sld.exportAsXML().getBytes() );
        fos.close();

        file = new File( prop.getProperty( "-outFile" ) );
        fos = new FileOutputStream( file );
        resultDocument.write( fos );
        fos.close();
    }

    private static void validate( Properties map )
                            throws Exception {
        if ( map.get( "-parentLayer" ) == null ) {
            throw new Exception( Messages.getString( "WFS2WMS.validate_2" ) );
        }
        if ( map.get( "-minx" ) != null ) {
            Double.parseDouble( map.getProperty( "-minx" ) );
        } else {
            map.put( "-minx", "-180" );
        }
        if ( map.get( "-miny" ) != null ) {
            Double.parseDouble( map.getProperty( "-miny" ) );
        } else {
            map.put( "-miny", "-90" );
        }
        if ( map.get( "-maxx" ) != null ) {
            Double.parseDouble( map.getProperty( "-maxx" ) );
        } else {
            map.put( "-maxx", "180" );
        }
        if ( map.get( "-maxy" ) != null ) {
            Double.parseDouble( map.getProperty( "-maxy" ) );
        } else {
            map.put( "-maxy", "90" );
        }
        if ( map.get( "-srs" ) == null ) {
            map.put( "-srs", "EPSG:4326" );
        }
        if ( map.get( "-wfsCaps" ) == null ) {
            throw new Exception( Messages.getString( "WFS2WMS.validate_0" ) );
        }
        if ( map.get( "-outFile" ) == null ) {
            throw new Exception( Messages.getString( "WFS2WMS.validate_1" ) );
        }
    }

    /**
     * @param args
     */
    public static void main( String[] args )
                            throws Exception {

        Properties map = new Properties();
        for ( int i = 0; i < args.length; i += 2 ) {
            System.out.println( args[i + 1] );
            map.put( args[i], args[i + 1] );
        }

        try {
            validate( map );
        } catch ( Exception e ) {
            System.out.println( "!!! E R R O R !!!" );
            System.out.println( e.getMessage() );
            System.out.println( "----------------------------------------------------" );
            System.out.println( Messages.getString( "WFS2WMS.parentLayer" ) );
            System.out.println( Messages.getString( "WFS2WMS.wfsCaps" ) );
            System.out.println( Messages.getString( "WFS2WMS.outFile" ) );
            return;
        }

        WFS2WMS wfs2wms = new WFS2WMS( map );
        wfs2wms.run();

    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFS2WMS.java,v $
 Revision 1.4  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.3  2006/06/05 19:27:36  poth
 support for automatic style creation (just polygons) and explict defintion of wfs capabilities source in result Layer/datasource elements added

 Revision 1.2  2006/06/05 15:22:52  poth
 *** empty log message ***

 Revision 1.1  2006/06/04 19:16:33  poth
 initial load up (not tested)


 ********************************************************************** */