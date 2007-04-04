/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 University of Bonn
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
 E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.tools.raster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.CodeList;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.wcs.configuration.Resolution;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;

/**
 * utility program to group several independ groups of images tiles
 * into one WCS coverage
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.15 $, $Date: 2006/11/27 09:07:53 $
 * 
 * @since 1.1
 * 
 * @deprecated use @see org.deegree.tools.raster.RasterTreeBuilder instead; this class
 * will be removed from deegree at the end of 2007
 */
public class GroupTiles {

    private static final ILogger LOG = LoggerFactory.getLogger( GroupTiles.class );

    private String outDir = null;

    private int count = 0;

    public static String CONFIG_TEMPLATE = "template_wcs_configuration.xml";

    public static String CONFIG_XSL = "updateConfig.xsl";

    /**
     * 
     * 
     * @param rootDir directory containing the image tiles
     * @param outDir directory where to store the result
     * @param name desired name of the covrage
     * @param label desired label of the covrage
     * @throws Exception
     */
    public GroupTiles( String rootDir, String outDir ) throws Exception {
        LOG.logInfo( "output directory: " + outDir );

        this.outDir = outDir;
        count = 0;
        List confFiles = getFiles( rootDir, "XML" );
        count = 0;
        List shpFiles = getFiles( rootDir, "SHP" );
        String tmp = (String) confFiles.get( 0 );
        if ( tmp.startsWith( "/" ) ) {
            tmp = tmp.substring( 1, tmp.length() );
        }
        CoverageDescription desc = CoverageDescription.createCoverageDescription( new URL(
                                                                                           "file:///"
                                                                                                                   + tmp ) );
        String nativeCRS = getNativeCRS( desc );
        Envelope bbox = calcNewBBOX( shpFiles, nativeCRS );
        Envelope llBbox = calcLLBBOX( nativeCRS, bbox );
        String resolutions = getResolutions( confFiles );
        mergeShapes( shpFiles );
        CoverageOffering co = desc.getCoverageOfferings()[0];
        createConfigurationFile( co.getName(), co.getDescription(), resolutions,
                                 co.getSupportedFormats().getFormats()[0].getCodes()[0], nativeCRS,
                                 llBbox, bbox );
        LOG.logInfo( "finished!" );
    }

    /**
     * walk in a recursion through all directories under the passed one
     * and returns the names/pathes of all contained XML-files
     * 
     * @param dir directory to start from
     * @return
     */
    private List getFiles( String dir, String extension ) {
        List list = new ArrayList( 200 );
        File file = new File( dir );
        String[] lst = file.list( new DFileFilter( extension ) );
        for ( int i = 0; i < lst.length; i++ ) {
            File fl = new File( dir + '/' + lst[i] );
            if ( fl.isDirectory() ) {
                List l = getFiles( dir + '/' + lst[i], extension );
                list.addAll( l );
            } else {
                count++;
                if ( count % 100 == 0 ) {
                    System.out.println( "tiles processed: " + count );
                }
                list.add( dir + '/' + lst[i] );
            }
        }
        return list;
    }

    /**
     * calculates the bounding box of the grouped tiles by evaluating the
     * MBRs of all involved shape files.
     * 
     * @param shpFiles
     * @param crs
     * @return
     * @throws IOException
     * @throws UnknownCRSException 
     */
    private Envelope calcNewBBOX( List shpFiles, String crs )
                            throws IOException, UnknownCRSException {
        double minx = 9E9;
        double miny = 9E9;
        double maxx = -9E9;
        double maxy = -9E9;
        for ( int i = 0; i < shpFiles.size(); i++ ) {
            String tmp = (String) shpFiles.get( i );
            int pos = tmp.lastIndexOf( '.' );
            tmp = tmp.substring( 0, pos );
            ShapeFile shp = new ShapeFile( tmp );
            Envelope env = shp.getFileMBR();
            if ( env.getMin().getX() < minx ) {
                minx = env.getMin().getX();
            }
            if ( env.getMin().getY() < miny ) {
                miny = env.getMin().getY();
            }
            if ( env.getMax().getX() > maxx ) {
                maxx = env.getMax().getX();
            }
            if ( env.getMax().getY() > maxy ) {
                maxy = env.getMax().getY();
            }
            shp.close();
        }
        CoordinateSystem cs = CRSFactory.create( crs );
        return GeometryFactory.createEnvelope( minx, miny, maxx, maxy, cs );
    }

    /**
     * transforms the all over bbox of the tiles to a latlon bounding box
     * 
     * @param nativeCRS
     * @param bbox
     * @return
     * @throws Exception
     */
    private Envelope calcLLBBOX( String nativeCRS, Envelope bbox )
                            throws Exception {
        IGeoTransformer trans = new GeoTransformer("EPSG:4326" );
        return trans.transform( bbox, nativeCRS);
    }

    /**
     * returns the name of the native CRS of the coverage
     * @param desc
     * @return
     */
    private String getNativeCRS( CoverageDescription desc ) {
        CoverageOffering co = desc.getCoverageOfferings()[0];
        CodeList[] cl = co.getSupportedCRSs().getNativeSRSs();
        if ( cl != null && cl.length > 0 ) {
            LOG.logInfo( "CRS: " + cl[0].getCodes()[0] );
            return cl[0].getCodes()[0];
        }
        Envelope env = co.getDomainSet().getSpatialDomain().getEnvelops()[0];
        return env.getCoordinateSystem().getName();

    }

    /**
     * returns a comma seperated list of all available coverage source
     * resolutions
     * 
     * @param desc
     * @return
     */
    private String getResolutions( List confFiles )
                            throws Exception {

        List reso = new ArrayList();
        for ( int i = 0; i < confFiles.size(); i++ ) {
            String tmp = (String) confFiles.get( i );
            if ( tmp.startsWith( "/" ) ) {
                tmp = tmp.substring( 1, tmp.length() );
            }
            CoverageDescription desc = CoverageDescription.createCoverageDescription( new URL(
                                                                                               "file:///"
                                                                                                                       + tmp ) );
            CoverageOffering co = desc.getCoverageOfferings()[0];
            Resolution[] res = co.getExtension().getResolutions();

            for ( int j = 0; j < res.length; j++ ) {
                Double d = new Double( res[res.length - j - 1].getMinScale() );
                if ( !reso.contains( d ) ) {
                    reso.add( d );
                }
            }
        }

        Double[] doubs = (Double[]) reso.toArray( new Double[reso.size()] );
        Arrays.sort( doubs );
        String rs = "";
        for ( int i = 0; i < doubs.length; i++ ) {
            rs += String.valueOf( doubs[doubs.length - i - 1] );
            if ( i < ( doubs.length - 1 ) ) {
                rs += ",";
            }
        }

        return rs;
    }

    /**
     * mergers the shape files for each resolution level into a new one
     * @param shpFiles
     */
    private void mergeShapes( List shpFiles )
                            throws Exception {
        LOG.logInfo( "mergin shapes ..." );
        String fs = "/";
        while ( shpFiles.size() > 0 ) {
            String tmp = (String) shpFiles.get( 0 );
            int pos = tmp.lastIndexOf( "." );
            tmp = tmp.substring( 0, pos );
            pos = tmp.lastIndexOf( fs );
            String res = tmp.substring( pos + 1, tmp.length() );
            LOG.logInfo( "processing resolution level: " + res );
            FeatureCollection fc = FeatureFactory.createFeatureCollection( "fc" + res, 1000 );

            String[] shapes = (String[]) shpFiles.toArray( new String[shpFiles.size()] );
            for ( int i = shapes.length; i > 0; i-- ) {
                tmp = (String) shpFiles.get( i - 1 );
                if ( tmp.indexOf( res ) > 0 ) {
                    shpFiles.remove( i - 1 );
                    pos = tmp.lastIndexOf( '.' );
                    tmp = tmp.substring( 0, pos );
                    ShapeFile shp = new ShapeFile( tmp );
                    int cnt = shp.getRecordNum();
                    for ( int j = 0; j < cnt; j++ ) {
                        Feature feat = shp.getFeatureByRecNo( j + 1 );
                        fc.add( feat );
                    }
                    shp.close();
                }
            }
            if ( fc.size() > 0 ) {
                ShapeFile shp = new ShapeFile( outDir + "/" + res, "rw" );
                shp.writeShape( fc );
                shp.close();
            }
        }
    }

    /**
     * 
     * @param name
     * @param description
     * @param resolutions
     * @param format
     * @param crs
     * @param llBbox
     * @param bbox
     */
    private void createConfigurationFile( String name, String description, String resolutions,
                                         String format, String crs, Envelope llBbox, Envelope bbox ) {
        URL configURL = AutoTiler.class.getResource( GroupTiles.CONFIG_TEMPLATE );
        URL configXSL = AutoTiler.class.getResource( GroupTiles.CONFIG_XSL );

        if ( configURL == null || configXSL == null ) {
            LOG.logError( "Unable to make configuration file" );
            System.exit( 1 );
        } else {

            XSLTDocument xslt = new XSLTDocument();

            try {
                if ( description == null ) {
                    description = "";
                }
                xslt.load( configXSL );
                Map map = new HashMap();
                map.put( "upperleftll", llBbox.getMin().getX() + " " + llBbox.getMin().getY() );
                map.put( "lowerrightll", llBbox.getMax().getX() + " " + llBbox.getMax().getY() );
                map.put( "upperleft", bbox.getMin().getX() + " " + bbox.getMin().getY() );
                map.put( "lowerright", bbox.getMax().getX() + " " + bbox.getMax().getY() );
                map.put( "dataDir", outDir );
                map.put( "label", name );
                map.put( "name", name );
                map.put( "description", description );
                map.put( "keywords", "deegree" );
                map.put( "resolutions", resolutions );
                map.put( "mimeType", format );
                map.put( "srs", crs );

                XMLFragment xml = new XMLFragment();
                xml.load( configURL );
                xml = xslt.transform( xml, XMLFragment.DEFAULT_URL, null, map );

                // write the result
                String out = outDir + "/wcs_configuration.xm";
                FileOutputStream fos = new FileOutputStream( out );
                xml.write( fos );
                fos.close();
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }

        }

    }

    public static void printHelp() {
        System.out.println( "-i root directory" );
        System.out.println( "-o output directory (optional)" );
        System.out.println( "-h print this help" );
    }

    /**
     * @version $Revision: 1.15 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
     */
    private class DFileFilter implements FilenameFilter {

        private String extend = null;

        public DFileFilter( String extend ) {
            this.extend = extend;
        }

        /**
         * @return
         */
        public boolean accept( File f, String name ) {
            int pos = name.lastIndexOf( "." );
            String ext = name.substring( pos + 1 );
            System.out.println( name );
            return ext.toUpperCase().equals( extend ) || name.indexOf( '.' ) < 0;
        }
    }

    public static void main( String[] args ) {

        //        if ( args == null || args.length < 2) {
        //            GroupTiles.printHelp();
        //            System.exit(1);
        //        }
        //        
        HashMap map = new HashMap();
        for ( int i = 0; i < args.length; i += 2 ) {
            if ( args[i].trim().equals( "-h" ) ) {
                GroupTiles.printHelp();
                System.exit( 1 );
            }
            map.put( args[i], args[i + 1] );
        }

        String rootDir = (String) map.get( "-i" );
        //rootDir = "D:/java/projekte/ostwuerttemberg/resources/tk25/tiles";
        String outDir = (String) map.get( "-o" );
        if ( outDir == null ) {
            outDir = rootDir;
        }

        try {
            new GroupTiles( rootDir, outDir );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GroupTiles.java,v $
Revision 1.15  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.14  2006/09/27 16:46:41  poth
transformation method signature changed

Revision 1.13  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
