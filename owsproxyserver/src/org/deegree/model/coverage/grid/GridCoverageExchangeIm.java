/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree
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
 E-Mail: klaus.greve@giub.uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.model.coverage.grid;

import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.oraclegeoraster.GeoRasterDescription;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wcs.configuration.Directory;
import org.deegree.ogcwebservices.wcs.configuration.Extension;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.configuration.GridDirectory;
import org.deegree.ogcwebservices.wcs.configuration.Shape;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverageExchange;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridCoverageWriter;

/**
 * Support for creation of grid coverages from persistent formats as well as exporting a grid
 * coverage to a persistent formats. For example, it allows for creation of grid coverages from the
 * GeoTIFF Well-known binary format and exporting to the GeoTIFF file format. Basic implementations
 * only require creation of grid coverages from a file format or resource. More sophesticated
 * implementations may extract the grid coverages from a database. In such case, a
 * <code>GridCoverageExchange</code> instance will hold a connection to a specific database and
 * the {@link #dispose} method will need to be invoked in order to close this connection.
 * <p>
 * 
 * @author Andreas Poth
 * @version 1.0
 * @since 2.0
 */
public class GridCoverageExchangeIm implements GridCoverageExchange {

    private static final ILogger LOG = LoggerFactory.getLogger( GridCoverageExchangeIm.class );

    private static final URI DEEGREEAPP = CommonNamespaces.buildNSURI( "http://www.deegree.org/app" );

    private static final String APP_PREFIX = "app";

    public static final String SHAPE_IMAGE_FILENAME = "FILENAME";

    public static final String SHAPE_DIR_NAME = "FOLDER";

    private List formats = null;

    /**
     * @param formats
     */
    public GridCoverageExchangeIm( Format[] formats ) {
        setFormats( formats );
    }

    /**
     * @param formats The formats to set.
     */
    public void setFormats( Format[] formats ) {
        if ( formats != null ) {
            this.formats = Arrays.asList( formats );
        } else {
            this.formats = new ArrayList();
        }
    }

    /**
     * Retrieve information on file formats or resources available with the
     * <code>GridCoverageExchange</code> implementation.
     * 
     * @return Information on file formats or resources available with the
     *         <code>GridCoverageExchange</code> implementation.
     */
    public Format[] getFormats() {
        return (Format[]) formats.toArray( new Format[formats.size()] );
    }

    /**
     * Returns a grid coverage reader that can manage the specified source
     * 
     * @param source
     *            An object that specifies somehow the data source. Can be a
     *            {@link java.lang.String}, an {@link java.io.InputStream}, a
     *            {@link java.nio.channels.FileChannel}, whatever. It's up to the associated grid
     *            coverage reader to make meaningful use of it.
     * @return The grid coverage reader.
     * @throws IOException
     *             if an error occurs during reading.
     * 
     * @revisit We need a mechanism to allow the right GridCoverageReader Something like an SPI.
     *          What if we can't find a GridCoverageReader? Do we return null or throw an Exception?
     */
    public GridCoverageReader getReader( Object source )
                            throws IOException {
        LOG.entering();
        if ( !( source instanceof InputStream ) ) {
            throw new IOException( "source parameter must be an instance of InputStream" );
        }
        LOG.exiting();
        return null;
    }

    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt>
     * class/interface as defined by GeoAPI. Returns a grid coverage reader that can manage the
     * specified source
     * 
     * @param source
     *            An object that specifies somehow the data source.
     * @param description
     *            an object describing the grid coverage and the access to avaiable metadata
     * @param envelope
     * @param format
     * @return The grid coverage reader.
     * @throws IOException
     *             if an error occurs during reading.
     * 
     * @revisit We need a mechanism to allow the right GridCoverageReader Something like an SPI.
     *          What if we can't find a GridCoverageReader? Do we return null or throw an Exception?
     */
    public GridCoverageReader getReader( InputStream source, CoverageOffering description,
                                         Envelope envelope, Format format )
                            throws IOException {
        LOG.entering();
        GridCoverageReader gcr = null;
        Extension ext = description.getExtension();
        String type = ext.getType();
        if ( type.equals( Extension.FILEBASED ) ) {
            if ( format.getName().toUpperCase().indexOf( "GEOTIFF" ) > -1 ) {
                gcr = new GeoTIFFGridCoverageReader( source, description, envelope, format );
            } else if ( isImageFormat( format ) ) {
                gcr = new ImageGridCoverageReader( source, description, envelope, format );
            } else {
                throw new IOException( "not supported file format: " + format.getName() );
            }
        } else {
            throw new IOException( "coverage storage type: " + type
                                   + " is not supported with method: getReader(InputStream, "
                                   + "CoverageOffering, Envelope, Format )" );
        }
        LOG.exiting();
        return gcr;
    }

    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt>
     * class/interface as defined by GeoAPI. Returns a grid coverage reader that can manage the
     * specified source
     * 
     * @param resource
     *            a string that specifies somehow the data source (e.g. a file).
     * @param description
     *            an object describing the grid coverage and the access to avaiable metadata
     * @param envelope
     * @param format
     * 
     * @return The grid coverage reader.
     * @throws IOException
     *             if an error occurs during reading.
     * 
     * @revisit We need a mechanism to allow the right GridCoverageReader Something like an SPI.
     *          What if we can't find a GridCoverageReader? Do we return null or throw an Exception?
     */
    public GridCoverageReader getReader( Object resource, CoverageOffering description,
                                         Envelope envelope, Format format )
                            throws IOException, InvalidParameterValueException {
        LOG.entering();

        GridCoverageReader gcr = null;
        Extension ext = description.getExtension();
        String type = ext.getType();
        if ( type.equals( Extension.FILEBASED ) ) {
            File file = new File( null, (String) resource, envelope );
            if ( format.getName().toUpperCase().indexOf( "GEOTIFF" ) > -1 ) {
                LOG.logInfo( "creating GeoTIFFGridCoverageReader" );
                gcr = new GeoTIFFGridCoverageReader( file, description, envelope, format );
            } else if ( isImageFormat( format ) ) {
                LOG.logInfo( "creating ImageGridCoverageReader" );
                gcr = new ImageGridCoverageReader( file, description, envelope, format );
            } else {
                throw new IOException( "not supported file format: " + format.getName() );
            }
        } else if ( type.equals( Extension.NAMEINDEXED ) ) {
            LOG.logInfo( "creating nameIndexed CompoundGridCoverageReader" );
            Directory[] dirs = new Directory[] { (Directory) resource };
            gcr = getReader( dirs, description, envelope, format );
        } else if ( type.equals( Extension.SHAPEINDEXED ) ) {
            LOG.logInfo( "creating shapeIndexed CompoundGridCoverageReader" );
            File[] files = null;
            try {
                files = getFilesFromShape( (Shape) resource, envelope, description );
            } catch ( UnknownCRSException e ) {
                throw new InvalidParameterValueException( e );
            }
            gcr = getReader( files, description, envelope, format );
        } else if ( type.equals( Extension.ORACLEGEORASTER ) ) {
            LOG.logInfo( "creating OracleGeoRasterGridCoverageReader" );
            gcr = createOracleGeoRasterGridCoverageReader( (GeoRasterDescription) resource,
                                                           description, envelope, format );
        } else {
            throw new IOException( "coverage storage type: " + type + " is not supported" );
        }

        LOG.exiting();
        return gcr;
    }

    /**
     * This method is a deegree specific enhancement of the <tt>GridCoverageExchange</tt>
     * class/interface as defined by GeoAPI. Returns a grid coverage reader that can manage the
     * specified source
     * 
     * @param resources
     *            an array strings that specifies somehow the data sources (e.g. some files).
     * @param description
     *            an object describing the grid coverage and the access to avaiable metadata
     * @param envelope
     * @return The grid coverage reader.
     * @throws IOException
     *             if an error occurs during reading.
     * 
     * @revisit We need a mechanism to allow the right GridCoverageReader Something like an SPI.
     *          What if we can't find a GridCoverageReader? Do we return null or throw an Exception?
     */
    public GridCoverageReader getReader( Object[] resources, CoverageOffering description,
                                         Envelope envelope, Format format )
                            throws IOException, InvalidParameterValueException {
        LOG.entering();

        //CS_CoordinateSystem crs = createNativeCRS( description );
        GridCoverageReader gcr = null;
        Extension ext = description.getExtension();
        String type = ext.getType();
        File[] files = null;
        if ( type.equals( Extension.FILEBASED ) ) {
            LOG.logInfo( "creating filebased CompoundGridCoverageReader" );
            files = (File[]) resources;
            gcr = new CompoundGridCoverageReader( files, description, envelope, format );
        } else if ( type.equals( Extension.NAMEINDEXED ) ) {
            LOG.logInfo( "creating nameIndexed CompoundGridCoverageReader" );
            try {
                files = getFilesFromDirectories( (Directory[]) resources, envelope, description );
            } catch ( UnknownCRSException e ) {
               throw new InvalidParameterValueException( e );
            }
            gcr = new CompoundGridCoverageReader( files, description, envelope, format );
        } else if ( type.equals( Extension.SHAPEINDEXED ) ) {
            LOG.logInfo( "creating shapeIndexed CompoundGridCoverageReader" );
            files = (File[]) resources;
            gcr = new CompoundGridCoverageReader( files, description, envelope, format );
        } else if ( type.equals( Extension.ORACLEGEORASTER ) ) {
            LOG.logInfo( "creating OracleGeoRasterGridCoverageReader" );
            gcr = createOracleGeoRasterGridCoverageReader( (GeoRasterDescription) resources[0],
                                                           description, envelope, format );
        } else {
            throw new IOException( "coverage storage type: " + type + " is not supported" );
        }

        LOG.exiting();
        return gcr;
    }

    /**
     * Creates a OracleGeoRasterGridCoverageReader instance from the given parameters.
     * 
     * @param grDesc
     * @param description
     * @param envelope
     * @param format
     * @return
     * @throws IOException
     */
    private GridCoverageReader createOracleGeoRasterGridCoverageReader(
                                                                        GeoRasterDescription grDesc,
                                                                        CoverageOffering description,
                                                                        Envelope envelope,
                                                                        Format format )
                            throws IOException {
        GridCoverageReader gcr = null;
        //      gcr = new OracleGeoRasterGridCoverageReader( (GeoRasterDescription) resource,
        //                                                         description, envelope, format );
        try {
            Class gridCoverageReaderClass = Class.forName( "org.deegree.model.coverage.grid.OracleGeoRasterGridCoverageReader" );

            // get constructor
            Class[] parameterTypes = new Class[] { GeoRasterDescription.class,
                                                  CoverageOffering.class, Envelope.class,
                                                  Format.class };
            Constructor constructor = gridCoverageReaderClass.getConstructor( parameterTypes );

            // call constructor
            Object arglist[] = new Object[] { grDesc, description, envelope, format };
            gcr = (GridCoverageReader) constructor.newInstance( arglist );
        } catch ( ClassNotFoundException e ) {
            throw new IOException( "Cannot find Oracle raster library: " + e.getMessage() );
        } catch ( Exception e ) {
            throw new IOException( e.getMessage() );
        }
        return gcr;
    }

    /**
     * returns true if the passed format is an image format
     * 
     * @param format
     * @return
     */
    private boolean isImageFormat( Format format ) {
        String frmt = format.getName().toUpperCase();
        return frmt.equalsIgnoreCase( "png" ) || frmt.equalsIgnoreCase( "bmp" )
               || frmt.equalsIgnoreCase( "tif" ) || frmt.equalsIgnoreCase( "tiff" )
               || frmt.equalsIgnoreCase( "gif" ) || frmt.equalsIgnoreCase( "jpg" )
               || frmt.equalsIgnoreCase( "jpeg" ) || frmt.indexOf( "ECW" ) > -1;
    }

    /**
     * reads the names of the grid coverage files intersecting the requested region from the passed
     * shape (name).
     * 
     * @param shape
     * @param envelope
     *            requested envelope
     * @param description
     *            description (metadata) of the source coverage
     * @return
     * @throws IOException
     * @throws UnknownCRSException 
     */
    private File[] getFilesFromShape( Shape shape, Envelope envelope, CoverageOffering description )
                            throws IOException, UnknownCRSException {
        LOG.entering();

        CoordinateSystem crs = createNativeCRS( description );

        String shapeBaseName = StringTools.replace( shape.getRootFileName(), "\\", "/", true );
        String shapeDir = shapeBaseName.substring( 0, shapeBaseName.lastIndexOf( "/" ) + 1 );

        ShapeFile shp = new ShapeFile( shapeBaseName );
        File[] files = null;
        int[] idx = shp.getGeoNumbersByRect( envelope );
        if ( idx != null ) {
            files = new File[idx.length];
            try {
                for ( int i = 0; i < files.length; i++ ) {
                    Feature feature = shp.getFeatureByRecNo( idx[i] );
                    QualifiedName qn = new QualifiedName( APP_PREFIX, SHAPE_IMAGE_FILENAME,
                                                          DEEGREEAPP );
                    String img = (String) feature.getDefaultProperty( qn ).getValue();
                    qn = new QualifiedName( APP_PREFIX, SHAPE_DIR_NAME, DEEGREEAPP );
                    String dir = (String) feature.getDefaultProperty( qn ).getValue();
                    if ( !( new java.io.File( dir ).isAbsolute() ) ) {
                        // solve relative path; it is assumed that the tile directories
                        // are located in the same directory as the shape file
                        dir = shapeDir + dir;
                    }
                    Geometry geom = feature.getGeometryPropertyValues()[0];
                    Envelope env = geom.getEnvelope();
                    env = GeometryFactory.createEnvelope( env.getMin(), env.getMax(), crs );
                    files[i] = new File( crs, dir.concat( "/".concat( img ) ), env );
                }
            } catch ( Exception e ) {
                throw new IOException( e.getMessage() + "\n" + StringTools.stackTraceToString( e ) );
            }
        } else {
            files = new File[0];
        }

        LOG.exiting();
        return files;

    }

    /**
     * reads the names of the grid coverage files intersecting the requested region from raster data
     * files contained in the passed directories
     * 
     * @param directories
     *            list of directories searched for matching raster files
     * @param envelope
     *            requested envelope
     * @param description
     *            description (metadata) of the source coverage
     * @return list of files intersecting the requested envelope
     * @throws UnknownCRSException 
     * @throws IOException
     */
    private File[] getFilesFromDirectories( Directory[] directories, Envelope envelope,
                                            CoverageOffering description ) throws UnknownCRSException {

        CoordinateSystem crs = createNativeCRS( description );

        List<File> list = new ArrayList<File>( 1000 );

        for ( int i = 0; i < directories.length; i++ ) {

            double widthCRS = ( (GridDirectory) directories[i] ).getTileWidth();
            double heightCRS = ( (GridDirectory) directories[i] ).getTileHeight();
            String[] extensions = directories[i].getFileExtensions();
            String dirName = directories[i].getName();

            DFileFilter fileFilter = new DFileFilter( extensions );
            java.io.File iofile = new java.io.File( dirName );
            String[] tiles = iofile.list( fileFilter );
            for ( int j = 0; j < tiles.length; j++ ) {
                int pos1 = tiles[j].indexOf( '_' );
                int pos2 = tiles[j].lastIndexOf( '.' );
                String tmp = tiles[j].substring( 0, pos1 );
                double x1 = Double.parseDouble( tmp ) / 1000d;
                tmp = tiles[j].substring( pos1 + 1, pos2 );
                double y1 = Double.parseDouble( tmp ) / 1000d;
                Envelope env = GeometryFactory.createEnvelope( x1, y1, x1 + widthCRS, y1
                                                                                      + heightCRS,
                                                               crs );
                if ( env.intersects( envelope ) ) {
                    File file = new File( crs, dirName + '/' + tiles[j], env );
                    list.add( file );
                }
            }

        }

        File[] files = list.toArray( new File[list.size()] );

        return files;
    }

    /**
     * creates an instance of <tt>CS_CoordinateSystem</tt> from the name of the native CRS of the
     * grid coverage
     * 
     * @param description
     * @return
     * @throws UnknownCRSException 
     */
    private CoordinateSystem createNativeCRS( CoverageOffering description ) throws UnknownCRSException {
        String srs = description.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];

        return CRSFactory.create( srs );
    }

    /**
     * Returns a GridCoverageWriter that can write the specified format. The file format name is
     * determined from the {@link Format} interface. Sample file formats include:
     * 
     * <blockquote><table>
     * <tr>
     * <td>"GeoTIFF"</td>
     * <td>&nbsp;&nbsp;- GeoTIFF</td>
     * </tr>
     * <tr>
     * <td>"PIX"</td>
     * <td>&nbsp;&nbsp;- PCI Geomatics PIX</td>
     * </tr>
     * <tr>
     * <td>"HDF-EOS"</td>
     * <td>&nbsp;&nbsp;- NASA HDF-EOS</td>
     * </tr>
     * <tr>
     * <td>"NITF"</td>
     * <td>&nbsp;&nbsp;- National Image Transfer Format</td>
     * </tr>
     * <tr>
     * <td>"STDS-DEM"</td>
     * <td>&nbsp;&nbsp;- Standard Transfer Data Standard</td>
     * </tr>
     * </table></blockquote>
     * 
     * @param destination
     *            An object that specifies somehow the data destination. Can be a
     *            {@link java.lang.String}, an {@link java.io.OutputStream}, a
     *            {@link java.nio.channels.FileChannel}, whatever. It's up to the associated grid
     *            coverage writer to make meaningful use of it.
     * @param format
     *            the output format.
     * @return The grid coverage writer.
     * @throws IOException
     *             if an error occurs during reading.
     */
    public GridCoverageWriter getWriter( Object destination, Format format )
                            throws IOException {

        LOG.logInfo( "requested format: " + format.getName() );

        GridCoverageWriter gcw = null;
        if ( !isKnownFormat( format ) ) {
            throw new IOException( "not supported Format: " + format );
        }

        if ( format.getName().equalsIgnoreCase( "GEOTIFF" ) ) {
            gcw = new GeoTIFFGridCoverageWriter( destination, null, null, null, format );
        } else if ( isImageFormat( format ) ) {
            gcw = new ImageGridCoverageWriter( destination, null, null, null, format );
        } else if ( format.getName().equalsIgnoreCase( "GML" ) ) {
            gcw = new GMLGridCoverageWriter( destination, null, null, null, format );
        } else {
            throw new IOException( "not supported Format: " + format );
        }

        return gcw;
    }

    /**
     * validates if a passed format is known to an instance of <tt>GridCoverageExchange</tt>
     * 
     * @param format
     * @return
     */
    private boolean isKnownFormat( Format format ) {

        for ( Iterator iter = formats.iterator(); iter.hasNext(); ) {
            Format element = (Format) iter.next();
            if ( element.equals( format ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Allows any resources held by this object to be released. The result of calling any other
     * method subsequent to a call to this method is undefined. Applications should call this method
     * when they know they will no longer be using this <code>GridCoverageExchange</code>,
     * especially if it was holding a connection to a database.
     * 
     * @throws IOException
     *             if an error occured while disposing resources (for example closing a database
     *             connection).
     */
    public void dispose()
                            throws IOException {
        formats = null;
    }

    /**
     * class: official version of a FilenameFilter
     */
    class DFileFilter implements FilenameFilter {

        private Map extensions = null;

        public DFileFilter( String[] extensions ) {
            this.extensions = new HashMap();
            for ( int i = 0; i < extensions.length; i++ ) {
                this.extensions.put( extensions[i].toUpperCase(), extensions[i] );
            }
        }

        public String getDescription() {
            return "*.*";
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept( java.io.File arg0, String name ) {
            int pos = name.lastIndexOf( "." );
            String ext = name.substring( pos + 1 ).toUpperCase();
            return extensions.get( ext ) != null;
        }
    }

}
/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GridCoverageExchangeIm.java,v $
 * Revision 1.25  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.24  2006/08/30 16:59:13  mschneider
 * Moved definitions of DEEGREEAPP and APP_PREFIX here (this are *not* constant bindings).
 *
 * Revision 1.23  2006/05/18 15:56:46  mschneider
 * Removed compile time dependency to OracleGeoRasterGridCoverageReader.
 *
 * Revision 1.22  2006/05/18 15:45:19  mschneider
 * Removed compile time dependency to OracleGeoRasterGridCoverageReader.
 *
 * Revision 1.21  2006/05/18 07:58:22  poth
 * correction of file comment footer
 *
 *  Revision 1.20  2006/05/18 07:57:17  poth
 *  bug fix reading shape indexed files
 * 
 *  Revision 1.19  2006/05/01 20:15:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.18  2006/04/06 20:25:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.17  2006/03/30 21:20:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.16  2006/03/15 22:20:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.15  2006/03/05 17:41:07  poth
 *  *** empty log message ***
 * 
 *  Revision 1.14  2006/03/02 21:39:38  poth
 *  *** empty log message ***
 * 
 *  Revision 1.13  2006/03/02 11:06:04  poth
 *  *** empty log message ***
 * 
 *  Revision 1.12  2006/02/23 07:45:24  poth
 *  *** empty log message ***
 * 
 *  Revision 1.11  2006/02/06 16:37:38  poth
 *  *** empty log message ***
 * 
 *  Revision 1.10  2006/02/05 20:33:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.9  2006/01/29 20:59:08  poth
 *  *** empty log message ***
 * 
 *  Revision 1.8  2005/11/21 18:42:10  mschneider
 *  Refactoring due to changes in Feature class.
 * 
 *  Revision 1.7  2005/11/21 14:58:25  deshmukh
 *  CRS to SRS
 * 
 *  Revision 1.6 2005/11/16 13:45:01
 * mschneider  Merge of wfs development
 * branch.  Changes to this class. What the
 * people have been up to: Revision 1.5.2.1 2005/11/15 13:36:55 deshmukh Changes to this class. What
 * the people have been up to: Modified Object to FeatureProperty Changes to this class. What the
 * people have been up to: Revision 1.5 2005/09/27 19:53:18 poth no message
 * 
 * Revision 1.4 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.3 2005/06/15 16:16:53 poth no message
 * 
 * Revision 1.2 2005/01/18 22:08:54 poth no message
 * 
 * Revision 1.14 2004/08/30 15:44:32 ap no message
 * 
 * Revision 1.13 2004/08/23 06:59:52 ap no message
 * 
 * Revision 1.12 2004/08/12 10:39:32 ap no message
 * 
 * Revision 1.11 2004/08/06 06:41:51 ap grid coverage implementation extension
 * 
 * Revision 1.10 2004/07/20 15:34:30 ap no message
 * 
 * Revision 1.9 2004/07/19 06:20:00 ap no message
 * 
 * Revision 1.8 2004/07/16 06:19:38 ap no message
 * 
 * Revision 1.7 2004/07/15 15:29:42 ap no message
 * 
 * Revision 1.6 2004/07/15 11:31:09 ap no message
 * 
 * 
 * 
 ************************************************************************************************* */
