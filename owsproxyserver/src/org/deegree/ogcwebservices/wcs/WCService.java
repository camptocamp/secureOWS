// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/WCService.java,v 1.27 2006/12/03 21:20:35 poth Exp $
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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wcs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.parameter.GeneralParameterValueIm;
import org.deegree.datatypes.parameter.OperationParameterIm;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.JDBCConnection;
import org.deegree.io.oraclegeoraster.GeoRasterDescription;
import org.deegree.model.coverage.grid.AbstractGridCoverage;
import org.deegree.model.coverage.grid.FormatIm;
import org.deegree.model.coverage.grid.GridCoverageExchangeIm;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wcs.configuration.Directory;
import org.deegree.ogcwebservices.wcs.configuration.DirectoryResolution;
import org.deegree.ogcwebservices.wcs.configuration.Extension;
import org.deegree.ogcwebservices.wcs.configuration.File;
import org.deegree.ogcwebservices.wcs.configuration.FileResolution;
import org.deegree.ogcwebservices.wcs.configuration.OracleGeoRasterResolution;
import org.deegree.ogcwebservices.wcs.configuration.Resolution;
import org.deegree.ogcwebservices.wcs.configuration.Shape;
import org.deegree.ogcwebservices.wcs.configuration.ShapeResolution;
import org.deegree.ogcwebservices.wcs.configuration.WCSConfiguration;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.describecoverage.DescribeCoverage;
import org.deegree.ogcwebservices.wcs.describecoverage.InvalidCoverageDescriptionExcpetion;
import org.deegree.ogcwebservices.wcs.getcapabilities.ContentMetadata;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSGetCapabilities;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSRequestValidator;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.ResultCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.SpatialSubset;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.OperationParameter;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.27 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.27 $, $Date: 2006/12/03 21:20:35 $
 * 
 * @since 2.0
 */

public class WCService implements OGCWebService {

    private static final ILogger LOG = LoggerFactory.getLogger( WCService.class );

    /**
     * 
     */
    private WCSConfiguration configuration = null;

    /**
     * creates a WCService from a configuration
     * 
     * @param configuration
     */
    public WCService( WCSConfiguration configuration ) {
        this.configuration = configuration;
    }

    /**
     * returns the capabilities of the WCS
     * 
     * @return capabilities of the WCS
     */
    public OGCCapabilities getCapabilities() {
        return configuration;
    }

    /**
     * @param request
     * @return
     */
    public CoverageDescription describeCoverage( DescribeCoverage request )
                            throws OGCWebServiceException {
        
        WCSRequestValidator.validate( configuration, request );
        CoverageOffering[] co = null;
        try {
            co = getCoverageOfferings( request );
        } catch ( Exception e ) {
            LOG.logError( StringTools.stackTraceToString( e ) );
            throw new OGCWebServiceException( StringTools.stackTraceToString( e ) );
        }
        CoverageDescription cd = new CoverageDescription( co, request.getVersion() );
        
        return cd;
    }

    /**
     * @param request
     * @return
     */
    public Coverage getCoverage( GetCoverage request )
                            throws OGCWebServiceException {
        
        WCSRequestValidator.validate( configuration, request );
        Coverage cov = null;
        if ( request.getOutput().getFormat().getCode().equals( "GML" ) ) {
            CoverageOffering co;
            try {
                co = getCoverageOffering( request );
            } catch ( InvalidCoverageDescriptionExcpetion e ) {
                LOG.logError( "CoverageDescription is not valid", e );
                throw new OGCWebServiceException( getClass().getName(),
                                                  "CoverageDescription is not valid: "
                                                                          + e.getMessage() );
            } catch ( IOException e ) {
                LOG.logError( "could not read CoverageDescription", e );
                throw new OGCWebServiceException( getClass().getName(),
                                                  "could not read CoverageDescription: "
                                                                          + e.getMessage() );
            } catch ( SAXException e ) {
                LOG.logError( "could not parse CoverageDescription", e );
                throw new OGCWebServiceException( getClass().getName(),
                                                  "could not parse CoverageDescription: "
                                                                          + e.getMessage() );
            }
            Envelope env = request.getDomainSubset().getSpatialSubset().getEnvelope();
            BufferedImage bi = new BufferedImage( 2, 2, BufferedImage.TYPE_INT_ARGB );
            cov = new ImageGridCoverage( co, env, bi );
        } else {
            cov = readCoverage( request );
        }
        
        return cov;
    }

    /**
     * method for event based request procrssing
     * 
     * @param request
     *            object containing the request.
     * @return
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {
        
        Object response = null;
        if ( request instanceof WCSGetCapabilities ) {
            WCSRequestValidator.validate( configuration, request );
            response = getCapabilities();
        } else if ( request instanceof GetCoverage ) {
            Coverage cov = getCoverage( (GetCoverage) request );
            response = new ResultCoverage( cov, cov.getClass(),
                                           ( (GetCoverage) request ).getOutput().getFormat(),
                                           (GetCoverage) request );
        } else if ( request instanceof DescribeCoverage ) {
            response = describeCoverage( (DescribeCoverage) request );
        }
        
        return response;
    }

    /**
     * returns the <tt>CoverageOffering</tt> s according to the coverages
     * names contained in the passed request. If the request doesn't contain one
     * or more named coverage <tt>CoverageOffering</tt> s for all coverages
     * known by the WCS will be returned.
     * 
     * @param request
     *            DescribeCoverage request
     * @return @throws
     *         IOException
     * @throws SAXException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    private CoverageOffering[] getCoverageOfferings( DescribeCoverage request )
                            throws IOException, SAXException, InvalidCoverageDescriptionExcpetion {
        
        String[] coverages = request.getCoverages();
        CoverageOffering[] co = null;
        ContentMetadata cm = configuration.getContentMetadata();
        if ( coverages.length == 0 ) {
            // get descriptions of all coverages
            CoverageOfferingBrief[] cob = cm.getCoverageOfferingBrief();
            co = new CoverageOffering[cob.length];
            for ( int i = 0; i < cob.length; i++ ) {
                URL url = cob[i].getConfiguration();
                CoverageDescription cd = CoverageDescription.createCoverageDescription( url );
                co[i] = cd.getCoverageOffering( cob[i].getName() );
            }
        } else {
            // get descriptions of all requested coverages
            co = new CoverageOffering[coverages.length];
            for ( int i = 0; i < coverages.length; i++ ) {
                CoverageOfferingBrief cob = cm.getCoverageOfferingBrief( coverages[i] );
                URL url = cob.getConfiguration();
                CoverageDescription cd = CoverageDescription.createCoverageDescription( url );
                co[i] = cd.getCoverageOffering( cob.getName() );
            }
        }
        
        return co;
    }

    /**
     * The method reads and returns the coverage described by the passed
     * request.
     * 
     * @param request
     * @return @throws
     *         OGCWebServiceException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    private Coverage readCoverage( GetCoverage request )
                            throws OGCWebServiceException, InvalidCoverageDescriptionExcpetion,
                            InvalidParameterValueException {
        
        Coverage result = null;
        try {
            CoverageOffering co = getCoverageOffering( request );
            Resolution[] resolutions = getResolutions( co, request );
            if ( resolutions == null || resolutions.length == 0 ) {
                throw new InvalidParameterValueException( "No data source defined the requested combination of spatial resolution and ranges" );
            }
            GridCoverageReader reader = null;
            LOG.logInfo( "getting responsible GridCoverageReader" );
            if ( resolutions[0] instanceof FileResolution ) {
                reader = getFileReader( resolutions, co, request );
            } else if ( resolutions[0] instanceof ShapeResolution ) {
                reader = getShapeReader( resolutions, co, request );
            } else if ( resolutions[0] instanceof DirectoryResolution ) {
                reader = getDirectoryReader( resolutions, co, request );
            } else if ( resolutions[0] instanceof OracleGeoRasterResolution ) {
                reader = getOracleGeoRasterReader( resolutions, co, request );
            }
            List<GeneralParameterValue> list = new ArrayList<GeneralParameterValue>( 10 );
            Envelope size = (Envelope) request.getDomainSubset().getSpatialSubset().getGrid();
            OperationParameter op = 
                new OperationParameterIm( "width", null, new Integer( (int) size.getWidth() + 1 ) );
            list.add( new GeneralParameterValueIm( op ) );
            op = new OperationParameterIm( "height", null, new Integer( (int) size.getHeight() + 1 ) );
            list.add( new GeneralParameterValueIm( op ) );
            GeneralParameterValue[] gpvs = new GeneralParameterValue[list.size()];
            result = reader.read( list.toArray( gpvs ) );
            
            // transform Coverage into another CRS if required
            String crs = request.getOutput().getCrs().getCode();
            if ( crs == null ) {
                crs = request.getDomainSubset().getRequestSRS().getCode();
            }
            if ( !crs.equalsIgnoreCase( co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0] ) ) {
                LOG.logDebug( "transforming coverage to " + crs );
                GeoTransformer gt = new GeoTransformer( crs );
                result = gt.transform( (AbstractGridCoverage)result, 5, 3, null );
            }
            
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( StringTools.stackTraceToString( e ) );
            throw new OGCWebServiceException( StringTools.stackTraceToString( e ) );
        }
        
        return result;
    }

    /**
     * returns the <tt>CoverageOffering</tt> describing the access to the data
     * sources behind the requested coverage
     * 
     * @param request
     *            GetCoverage request
     * @return @throws
     *         IOException
     * @throws SAXException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    private CoverageOffering getCoverageOffering( GetCoverage request )
                            throws IOException, SAXException, InvalidCoverageDescriptionExcpetion {
        
        ContentMetadata cm = configuration.getContentMetadata();
        CoverageOfferingBrief cob = cm.getCoverageOfferingBrief( request.getSourceCoverage() );
        URL url = cob.getConfiguration();
        CoverageDescription cd = CoverageDescription.createCoverageDescription( url );
        CoverageOffering co = cd.getCoverageOffering( request.getSourceCoverage() );
        
        return co;
    }

    /**
     * returns the <tt>Resolution</tt> s matching the scale, region and range
     * parameters of the passed request
     * 
     * @param co
     * @param request
     * @return
     */
    private Resolution[] getResolutions( CoverageOffering co, GetCoverage request ) {
        
        Extension extension = co.getExtension();
        SpatialSubset sps = request.getDomainSubset().getSpatialSubset();
        // determenine resolution of the requested coverage
        Envelope env = sps.getEnvelope();
        Envelope grid = (Envelope) sps.getGrid();
        double qx = env.getWidth() / grid.getWidth();
        double qy = env.getHeight() / grid.getHeight();
        double reso = qx;
        // if x- and y-direction has different resolution in the GetCoverage
        // request use the finest
        if ( qy < qx ) {
            reso = qy;
        }
        Resolution[] res = extension.getResolutions( reso );
        
        return res;
    }

    /**
     * returns a <tt>GridCoverageReader</tt> for accessing the data source of
     * the target coverage of the passed GetCoverage request. The reader will be
     * constructed from all <tt>File</tt> s matching the filter conditions
     * defined in the passed GeCoverage request. <BR>
     * At the moment just the first field of the passed <tt>Resolution</tt>
     * array will be considered!
     * 
     * @param resolutions
     *            <tT>Resolution</tt> to get a reader for
     * @param co
     *            description of the requested coverage
     * @param request
     * @return <tt>GridCoverageReader</tt>
     * @throws IOException
     */
    private GridCoverageReader getFileReader( Resolution[] resolutions, CoverageOffering co,
                                             GetCoverage request )
                            throws IOException, InvalidParameterValueException {
        

        String nativeCRS = co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        // calculates the envevole to be used by the created GridCoverageReader
        Envelope envelope = calculateRequestEnvelope( request, nativeCRS );

        File[] files = ( (FileResolution) resolutions[0] ).getFiles();
        List list = new ArrayList();
        for ( int i = 0; i < files.length; i++ ) {
            Envelope fileEnv = files[i].getEnvelope();
            if ( fileEnv.intersects( envelope ) ) {
                list.add( files[i] );
            }
        }
        files = (File[]) list.toArray( new File[list.size()] );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( co.getSupportedFormats().getNativeFormat() );
        GridCoverageReader reader = gce.getReader( files, co, envelope, format );
        
        return reader;
    }

    /**
     * returns a <tt>GridCoverageReader</tt> for accessing the data source of
     * the target coverage of the passed GetCoverage request. The reader will be
     * constructed from all <tt>Shape</tt> s matching the filter conditions
     * defined in the passed GeCoverage request. At least this should be just
     * one! <BR>
     * At the moment just the first field of the passed <tt>Resolution</tt>
     * array will be considered!
     * 
     * @param resolutions
     * @param co
     * @param request
     * @return @throws
     *         IOException
     */
    private GridCoverageReader getShapeReader( Resolution[] resolutions, CoverageOffering co,
                                              GetCoverage request )
                            throws IOException, InvalidParameterValueException {
        
        String nativeCRS = co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        // calculates the envevole to be used by the created GridCoverageReader
        Envelope envelope = calculateRequestEnvelope( request, nativeCRS );

        Shape shape = ( (ShapeResolution) resolutions[0] ).getShape();

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( co.getSupportedFormats().getNativeFormat() );
        GridCoverageReader reader = gce.getReader( shape, co, envelope, format );
        
        return reader;
    }

    /**
     * returns a <tt>GridCoverageReader</tt> for accessing the data source of
     * the target coverage of the passed GetCoverage request. The reader will be
     * constructed from all <tt>Directory</tt> s matching the filter
     * conditions defined in the passed GeCoverage request. At least this should
     * be just one! <BR>
     * At the moment just the first field of the passed <tt>Resolution</tt>
     * array will be considered!
     * 
     * @param resolutions
     * @param co
     * @param request
     * @return @throws
     *         IOException
     */
    private GridCoverageReader getDirectoryReader( Resolution[] resolutions, CoverageOffering co,
                                                  GetCoverage request )
                            throws IOException, InvalidParameterValueException {
        

        String nativeCRS = co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        // calculates the envevole to be used by the created GridCoverageReader
        Envelope envelope = calculateRequestEnvelope( request, nativeCRS );

        Directory[] dirs = ( (DirectoryResolution) resolutions[0] ).getDirectories( envelope );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( co.getSupportedFormats().getNativeFormat() );

        GridCoverageReader reader = gce.getReader( dirs, co, envelope, format );

        
        return reader;
    }

    /**
     * returns a <tt>GridCoverageReader</tt> for accessing the data source of
     * the target coverage of the passed GetCoverage request. The reader will be
     * constructed from the JDBCV connnection defined in the CoverageDescription 
     * extension.<BR>
     * At the moment just the first field of the passed <tt>Resolution</tt>
     * array will be considered!
     * 
     * @param resolutions
     * @param co
     * @param request
     * @return
     * @throws InvalidParameterValueException 
     * @throws IOException 
     */
    private GridCoverageReader getOracleGeoRasterReader( Resolution[] resolutions,
                                                        CoverageOffering co, GetCoverage request )
                            throws InvalidParameterValueException, IOException {

        String nativeCRS = co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        // calculates the envevole to be used by the created GridCoverageReader
        Envelope envelope = calculateRequestEnvelope( request, nativeCRS );

        JDBCConnection jdbc = ( (OracleGeoRasterResolution) resolutions[0] ).getJDBCConnection();
        String table = ( (OracleGeoRasterResolution) resolutions[0] ).getTable();
        String rdtTable = ( (OracleGeoRasterResolution) resolutions[0] ).getRdtTable();
        String column = ( (OracleGeoRasterResolution) resolutions[0] ).getColumn();
        String identification = ( (OracleGeoRasterResolution) resolutions[0] ).getIdentification();
        int level = ( (OracleGeoRasterResolution) resolutions[0] ).getLevel();
        GeoRasterDescription grd = new GeoRasterDescription( jdbc, table, rdtTable, column,
                                                             identification, level );

        GridCoverageExchangeIm gce = new GridCoverageExchangeIm( null );
        Format format = new FormatIm( co.getSupportedFormats().getNativeFormat() );

        return gce.getReader( grd, co, envelope, format );

    }

    /**
     * According to WCS 1.0.0 the CRS of the GetCoverage request BBOX can be
     * different to the desired CRS of the resulting coverage. This method
     * transforms the request CRS to the output CRS if requiered. At the moment
     * deegree WCS doesn't support transformation of grid coverages so the
     * output CRS will always be the native CRS of te data.
     * 
     * @param request
     * @param targetCrs
     * @return @throws
     *         InvalidParameterValueException
     */
    private Envelope calculateRequestEnvelope( GetCoverage request, String targetCrs )
                            throws InvalidParameterValueException {
        

        SpatialSubset spsu = request.getDomainSubset().getSpatialSubset();
        Envelope envelope = spsu.getEnvelope();

        String reqCrs = request.getDomainSubset().getRequestSRS().getCode();

        if ( !reqCrs.equals( targetCrs ) ) {
            IGeoTransformer gt = new GeoTransformer(targetCrs);
            
            try {
                envelope = gt.transform( envelope, reqCrs );
            } catch ( Exception e ) {
                throw new InvalidParameterValueException( "requestCRS couldn't be "
                                                          + "transformed to outputCRS: " + reqCrs );
            }
        }
        
        return envelope;
    }

}
/* ******************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WCService.java,v $
 * Revision 1.27  2006/12/03 21:20:35  poth
 * support for transforming GridCoverages into other CRS  added
 *
 * Revision 1.26  2006/11/27 09:07:53  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.25  2006/09/27 16:46:41  poth
 * transformation method signature changed
 *
 * Revision 1.24  2006/09/14 20:07:35  poth
 * code formatting
 *
 * Revision 1.23  2006/06/12 08:11:21  poth
 * footer comment corrected
 *
 *  Revision 1.22  2006/05/18 15:58:07  poth
 *  *** empty log message ***
 * 
 *  Revision 1.21  2006/05/03 20:09:52  poth
 *  *** empty log message ***
 * 
 *  Revision 1.20  2006/05/01 20:15:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.19  2006/04/06 20:25:31  poth
 *  *** empty log message ***
 * 
 *  Revision 1.18  2006/03/30 21:20:28  poth
 *  *** empty log message ***
 * 
 *  Revision 1.17  2006/03/16 12:22:28  poth
 *  *** empty log message ***
 * 
 *  Revision 1.16  2006/03/16 12:22:01  poth
 *  *** empty log message ***
 * 
 *  Revision 1.15  2006/03/15 22:20:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.14  2006/03/14 08:51:22  poth
 *  *** empty log message ***
 * 
 *  Revision 1.13  2006/03/02 21:39:38  poth
 *  *** empty log message ***
 * 
 *  Revision 1.12  2006/03/02 11:06:04  poth
 *  *** empty log message ***
 * 
 *  Revision 1.11  2006/02/28 17:53:31  poth
 *  *** empty log message ***
 * 
 *  Revision 1.10  2006/02/05 20:33:09  poth
 *  *** empty log message ***
 * 
 *  Revision 1.9  2006/01/11 16:57:02  poth
 *  *** empty log message ***
 * 
 *  Revision 1.8  2005/12/21 17:30:10  poth
 *  no message
 * 
 *  Revision 1.7  2005/11/21 15:03:42  deshmukh
 *  CRS to SRS
 * 
 *  
 ***************************************************************************** */
