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
package org.deegree.graphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.JDBCConnection;
import org.deegree.io.oraclegeoraster.GeoRasterDescription;
import org.deegree.model.coverage.grid.FormatIm;
import org.deegree.model.coverage.grid.GridCoverageExchangeIm;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wcs.configuration.Directory;
import org.deegree.ogcwebservices.wcs.configuration.DirectoryResolution;
import org.deegree.ogcwebservices.wcs.configuration.Extension;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.configuration.FileResolution;
import org.deegree.ogcwebservices.wcs.configuration.OracleGeoRasterResolution;
import org.deegree.ogcwebservices.wcs.configuration.Resolution;
import org.deegree.ogcwebservices.wcs.configuration.Shape;
import org.deegree.ogcwebservices.wcs.configuration.ShapeResolution;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * 
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/11/27 09:07:52 $
 *
 * @since 2.0
 */
public class LazyRasterLayer extends AbstractLayer {

    private ILogger LOG = LoggerFactory.getLogger( LazyRasterLayer.class );

    private Extension resource;

    private CoverageOffering coverageOffering;

    /**
     * 
     * @param name
     * @param resource
     * @param coverageOffering
     * @throws Exception
     */
    public LazyRasterLayer( String name, CoverageOffering coverageOffering ) throws Exception {
        super( name );
        this.coverageOffering = coverageOffering;
        resource = coverageOffering.getExtension();
    }

    /**
     * 
     * @param name
     * @param crs
     * @param resource
     * @param coverageOffering
     * @throws Exception
     */
    public LazyRasterLayer( String name, CoordinateSystem crs, CoverageOffering coverageOffering )
                            throws Exception {
        super( name, crs );
        this.coverageOffering = coverageOffering;
        resource = coverageOffering.getExtension();
    }

    /**
     * 
     */
    public void setCoordinatesSystem( CoordinateSystem crs )
                            throws Exception {
        // not supported yet
    }
    
    

    @Override
    public Envelope getBoundingBox() {
        return coverageOffering.getDomainSet().getSpatialDomain().getEnvelops()[0];
    }

    /**
     * 
     * @param envelope
     * @return
     * @throws IOException 
     * @throws InvalidParameterValueException 
     */
    public GridCoverage getRaster( Envelope envelope, double resolution )
                            throws InvalidParameterValueException, IOException {

        Resolution[] resolutions = resource.getResolutions( resolution );
        
        String nativeCRS = coverageOffering.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        CoordinateSystem crs;
        try {
            crs = CRSFactory.create( nativeCRS );
        } catch ( UnknownCRSException e ) {
            throw new InvalidParameterValueException( e );
        }
        envelope = GeometryFactory.createEnvelope( envelope.getMin(), envelope.getMax(), crs );
        
        GridCoverageReader reader = null;
        if ( resolutions[0] instanceof FileResolution ) {
            reader = getFileReader( resolutions, envelope );
        } else if ( resolutions[0] instanceof ShapeResolution ) {
            reader = getShapeReader( resolutions, envelope );
        } else if ( resolutions[0] instanceof DirectoryResolution ) {
            reader = getDirectoryReader( resolutions, envelope );
        } else if ( resolutions[0] instanceof OracleGeoRasterResolution ) {
            reader = getOracleGeoRasterReader( resolutions, envelope );
        } else {
            throw new InvalidParameterValueException( "not supported coverage resolution: " + 
                                                      resolutions[0].getClass().getName() );
        }
        return reader.read( null );
    }

    /**
     * 
     * @param resolutions
     * @param env
     * @return 
     * @throws IOException
     * @throws InvalidParameterValueException
     */
    private GridCoverageReader getDirectoryReader( Resolution[] resolutions, Envelope env )
                            throws IOException, InvalidParameterValueException {

        LOG.logInfo( "reading coverage from directories" );
        
        Directory[] dirs = ( (DirectoryResolution) resolutions[0] ).getDirectories( env );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( coverageOffering.getSupportedFormats().getNativeFormat() );

        return gce.getReader( dirs, coverageOffering, env, format );
    }

    /**
     * 
     * @param resolutions
     * @param env
     * @return
     * @throws IOException
     * @throws InvalidParameterValueException
     */
    private GridCoverageReader getFileReader( Resolution[] resolutions, Envelope env )
                            throws IOException, InvalidParameterValueException {

        LOG.logInfo( "reading coverage from files" );
        
        File[] files = ( (FileResolution) resolutions[0] ).getFiles();
        List list = new ArrayList();
        for ( int i = 0; i < files.length; i++ ) {
            Envelope fileEnv = files[i].getEnvelope();
            if ( fileEnv.intersects( env ) ) {
                list.add( files[i] );
            }
        }
        files = (File[]) list.toArray( new File[list.size()] );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( coverageOffering.getSupportedFormats().getNativeFormat() );

        return gce.getReader( files, coverageOffering, env, format );
    }

    /**
     * 
     * @param resolutions
     * @param env
     * @return
     * @throws InvalidParameterValueException
     * @throws IOException
     */
    private GridCoverageReader getOracleGeoRasterReader( Resolution[] resolutions, Envelope env )
                            throws InvalidParameterValueException, IOException {

        LOG.logInfo( "reading coverage from oracle georaster" );
        
        JDBCConnection jdbc = ( (OracleGeoRasterResolution) resolutions[0] ).getJDBCConnection();
        String table = ( (OracleGeoRasterResolution) resolutions[0] ).getTable();
        String rdtTable = ( (OracleGeoRasterResolution) resolutions[0] ).getRdtTable();
        String column = ( (OracleGeoRasterResolution) resolutions[0] ).getColumn();
        String identification = ( (OracleGeoRasterResolution) resolutions[0] ).getIdentification();
        int level = ( (OracleGeoRasterResolution) resolutions[0] ).getLevel();
        GeoRasterDescription grd = new GeoRasterDescription( jdbc, table, rdtTable, column,
                                                             identification, level );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( coverageOffering.getSupportedFormats().getNativeFormat() );

        return gce.getReader( grd, coverageOffering, env, format );

    }

    /**
     * 
     * @param resolutions
     * @param env
     * @return
     * @throws IOException
     * @throws InvalidParameterValueException
     */
    private GridCoverageReader getShapeReader( Resolution[] resolutions, Envelope env )
                            throws IOException, InvalidParameterValueException {

        LOG.logInfo( "reading coverage from shapes" );
        
        Shape shape = ( (ShapeResolution) resolutions[0] ).getShape();

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( coverageOffering.getSupportedFormats().getNativeFormat() );
        return gce.getReader( shape, coverageOffering, env, format );

    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: LazyRasterLayer.java,v $
 Revision 1.4  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.3  2006/05/31 17:53:33  poth
 bug fix

 Revision 1.2  2006/05/31 17:23:59  poth
 first implementation of LazyRasterLayer

 Revision 1.1  2006/05/24 08:05:03  poth
 initial load up


 ********************************************************************** */