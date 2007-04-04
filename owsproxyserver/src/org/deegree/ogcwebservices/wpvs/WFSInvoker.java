//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/WFSInvoker.java,v 1.35 2006/11/27 15:43:34 bezema Exp $
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
 Aennchenstraße 19
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

package org.deegree.ogcwebservices.wpvs;

import java.io.StringReader;
import java.util.ArrayList;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.FeatureId;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.configuration.LocalWFSDataSource;
import org.deegree.ogcwebservices.wpvs.j3d.DefaultSurface;
import org.deegree.ogcwebservices.wpvs.j3d.Object3DFactory;
import org.deegree.ogcwebservices.wpvs.j3d.PointsToPointListFactory;
import org.deegree.ogcwebservices.wpvs.util.ResolutionStripe;
import org.w3c.dom.Document;

/**
 * Invoker for a Web Feature Service.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.35 $, $Date: 2006/11/27 15:43:34 $
 * 
 * @since 2.0
 */
public class WFSInvoker extends GetViewServiceInvoker {

    private static final ILogger LOG = LoggerFactory.getLogger( WFSInvoker.class );

    /* whether the returned data is a 3D object or data for the elevation model */
    private final boolean isElevationModelRequest;

    private int id;

    /**
     * Creates a new instance of this class.
     * 
     * @param owner
     *            the ResolutionStripe that calls this invoker
     * @param id
     * @param isElevationModelRequest
     */
    public WFSInvoker( ResolutionStripe owner, int id, boolean isElevationModelRequest ) {
        super( owner );
        this.id = id;
        this.isElevationModelRequest = isElevationModelRequest;
    }

    @Override
    public void invokeService( AbstractDataSource dataSource ) {

        LOG.entering();

        if ( !( dataSource instanceof LocalWFSDataSource ) ) {
            LOG.logError( "The given AbstractDataSource is no WFSDataSource instance. It is needed for a WFSInvoker" );
            throw new RuntimeException( "DataSource should be a WFS-instance for a WFCSInvoker" );
        }

        OGCWebService service = dataSource.getOGCWebService();
        if ( service == null ) {
            LOG.logError( "The given AbstractDataSource is no WFSDataSource instance. It is needed for a WFSInvoker" );
            throw new RuntimeException( "DataSource should be a WFS-instance for a WFCSInvoker" );
        }

        Object response = null;
        try {
            GetFeature getFeature = createGetFeatureRequest( (LocalWFSDataSource) dataSource );
            response = service.doService( getFeature );
        } catch ( OGCWebServiceException ogcwe ) {
            LOG.logError( "Exception when performing GetFeature: ", ogcwe );
            ogcwe.printStackTrace();
        } catch ( GeometryException ge ) {
            LOG.logError( "Exception when creating GetFeature request: ", ge );
            ge.printStackTrace();
        }

        if ( response != null && response instanceof FeatureResult ) {
            FeatureCollection result = (FeatureCollection) ( (FeatureResult) response ).getResponse();
            if ( result != null ) {
                LOG.logDebug( "RESULT: " + result.size() );

                if ( isElevationModelRequest ) {
//                    PointListFactory adapter = (PointListFactory) ( (LocalWFSDataSource) dataSource ).getFeatureCollectionAdapter();
                    PointsToPointListFactory ptpFac = new PointsToPointListFactory();
                    resolutionStripe.setElevationModelFromMeassurePoints( ptpFac.createFromFeatureCollection( result ) );
                } else {
                    Object3DFactory o3DFactory = new Object3DFactory();
                    for ( int i = 0; i < result.size(); ++i ) {
                        Feature feature = result.getFeature( i );
                        createSurfaces( o3DFactory, feature );
                    }
                }
            }
        }
        LOG.exiting();
    }

    /**
     * This method recursively constructs all the surfaces contained in the given feature. If the
     * Feature contains a PropertyType of {@link Types#FEATURE} this Feature will also be traversed, if it
     * contains a {@link Types#GEOMETRY} a {@link DefaultSurface} will be created.
     * 
     * @param o3DFactory the Factory to create the defaultservice
     * @param feature the feature to traverse.
     */
    private void createSurfaces( Object3DFactory o3DFactory, Feature feature ) {

        FeatureType ft = feature.getFeatureType();
        PropertyType[] propertyTypes = ft.getProperties();

        for ( PropertyType pt : propertyTypes ) {
            if ( pt.getType() == Types.FEATURE ) {
                FeatureProperty[] fp = feature.getProperties( pt.getName() );
                if ( fp != null ) {
                    for ( int i = 0; i < fp.length; i++ ) {
                        createSurfaces( o3DFactory, (Feature) fp[i].getValue() );
                    }
                }
            } else if ( pt.getType() == Types.GEOMETRY ) {
                DefaultSurface ds = o3DFactory.createSurface( feature );
                if ( ds != null ) {
                    resolutionStripe.addFeature( id + "_" + ds.getDefaultSurfaceID(), ds );
                }
            }
        }

    }

    /**
     * Creates a new <code>GetFeature</code> object from an "XML-String" not nice.
     * 
     * @param ds
     *            the datasource containig service data
     * @param id
     *            the request id
     * @return a new GetFeature request
     * @throws GeometryException
     * @throws OGCWebServiceException
     * @throws Exception
     */
    private GetFeature createGetFeatureRequest( LocalWFSDataSource dataSource /*
                                                                                 * String id,
                                                                                 * Surface[] boxes
                                                                                 */)
                            throws GeometryException, OGCWebServiceException {

        QualifiedName qn = dataSource.getName();

        StringBuffer sb = new StringBuffer( 5000 );
        sb.append( "<?xml version='1.0' encoding='" + CharsetUtils.getSystemCharset() + "'?>" );
        sb.append( "<wfs:GetFeature xmlns:wfs='http://www.opengis.net/wfs' " );
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
        sb.append( "xmlns:" ).append( qn.getPrefix() ).append( '=' );
        sb.append( "'" ).append( qn.getNamespace() ).append( "' " );

        if ( dataSource.getServiceType() == AbstractDataSource.LOCAL_WFS ) {
            sb.append( "outputFormat='FEATURECOLLECTION'>" );
        } else {
            sb.append( "outputFormat='text/xml; subtype=gml/3.1.1'>" );
        }

        /**
         * To make things a little clearer compare with this SQL-Statement: SELECT ( !texture )?
         * geoProperty : * FROM qn.getLocalName() WHERE geoPoperty intersects with
         * resolutionStripe.getSurface() AND FilterConditions.
         */
        PropertyPath geoProperty = dataSource.getGeometryProperty();

        // FROM
        sb.append( "<wfs:Query typeName='" ).append( qn.getPrefix() ).append( ":" );
        sb.append( qn.getLocalName() ).append( "'>" );

        // SELECT
        if ( !isElevationModelRequest ) {
            sb.append( "<wfs:PropertyName>" );
            sb.append( geoProperty.getAsString() );
            sb.append( "</wfs:PropertyName>" );
        }

        StringBuffer sbArea = GMLGeometryAdapter.export( resolutionStripe.getSurface() );

        // WHERE
        sb.append( "<ogc:Filter><ogc:Intersects> " );
        sb.append( "<wfs:PropertyName>" );
        sb.append( geoProperty.getAsString() );
        sb.append( "</wfs:PropertyName>" );
        sb.append( sbArea );
        sb.append( "</ogc:Intersects>" );

        // AND
        Filter filter = dataSource.getFilter();
        if ( filter != null ) {
            if ( filter instanceof ComplexFilter ) {
                sb.append( "<ogc:And>" );
                sb.append( "<ogc:BBOX>" );
                sb.append( "<wfs:PropertyName>" );
                sb.append( dataSource.getGeometryProperty().getAsString() );
                sb.append( "</wfs:PropertyName>" );
                sb.append( GMLGeometryAdapter.exportAsBox( resolutionStripe.getSurface().getEnvelope() ) );
                sb.append( "</ogc:BBOX>" );
                // add filter as defined in the layers datasource description
                // to the filter expression
                org.deegree.model.filterencoding.Operation op = ( (ComplexFilter) filter ).getOperation();
                sb.append( op.toXML() ).append( "</ogc:And>" );
            } else {
                if ( filter instanceof FeatureFilter ) {
                    ArrayList<FeatureId> featureIds = ( (FeatureFilter) filter ).getFeatureIds();
                    if ( featureIds.size() != 0 )
                        sb.append( "<ogc:And>" );
                    for ( FeatureId fid : featureIds ) {
                        sb.append( fid.toXML() );
                    }
                    if ( featureIds.size() != 0 )
                        sb.append( "</ogc:And>" );
                }
            }
        }

        sb.append( "</ogc:Filter></wfs:Query></wfs:GetFeature>" );

        String s = sb.toString();

        Document doc;
        try {
            doc = XMLTools.parse( new StringReader( s ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            String mesg = "Could not parse GetFeature request ";
            LOG.logError( StringTools.concat( s.length() + 100, mesg, sb.toString() ) );
            throw new OGCWebServiceException( mesg );

        }
        LOG.logDebug( "WFS GetFeature: " + s );

        IDGenerator idg = IDGenerator.getInstance();
        return GetFeature.create( String.valueOf( idg.generateUniqueID() ),
                                  doc.getDocumentElement() );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WFSInvoker.java,v $
 * Changes to this class. What the people have been up to: Revision 1.35  2006/11/27 15:43:34  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.34  2006/11/23 11:46:14  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.32 2006/07/20 08:12:21 taddei Changes to this
 * class. What the people have been up to: use of QualiName for geometry property Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.31 2006/07/05 15:57:47 poth Changes to this class. What the people have been up to:
 * useless parameter removed from method signature Changes to this class. What the people have been
 * up to: Changes to this class. What the people have been up to: Revision 1.30 2006/07/05 11:22:10
 * taddei Changes to this class. What the people have been up to: include par to set buildings elev
 * to 0 Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.29 2006/07/04 09:06:22 taddei Changes to this class. What the
 * people have been up to: todo: excp handling Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.28 2006/06/29 16:50:09
 * poth Changes to this class. What the people have been up to: *** empty log message *** Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.27 2006/06/20 10:16:01 taddei Changes to this class. What the people have been up
 * to: clean up and javadoc Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.26 2006/06/20 07:39:59 taddei Changes to this
 * class. What the people have been up to: added parent dataset and change in ft name Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.25 2006/05/12 13:12:45 taddei Changes to this class. What the people have been up
 * to: clean up Changes to this class. What the people have been up to: Changes to this class. What
 * the people have been up to: Revision 1.24 2006/05/10 15:01:19 taddei Changes to this class. What
 * the people have been up to: now collecting boxes into a united geom Changes to this class. What
 * the people have been up to: Revision 1.23 2006/05/05 12:41:02 taddei added to to list
 * 
 * Revision 1.22 2006/04/18 18:20:26 poth ** empty log message ***
 * 
 * Revision 1.21 2006/04/06 20:25:30 poth ** empty log message ***
 * 
 * Revision 1.20 2006/04/06 15:07:59 taddei added support for ValidArea
 * 
 * Revision 1.19 2006/04/05 09:07:03 taddei added code for computing res of different surfs; and fc
 * adapter
 * 
 * Revision 1.17 2006/03/29 15:08:21 taddei with buildings
 * 
 * Revision 1.16 2006/03/10 10:31:40 taddei changes regarding cood sys and scale calculation
 * 
 * Revision 1.15 2006/03/09 08:57:58 taddei debug mesgs
 * 
 * Revision 1.14 2006/03/07 08:49:20 taddei changes due to pts list factories
 * 
 * Revision 1.13 2006/03/02 15:23:52 taddei using now StringTools and StringBuilder
 * 
 * Revision 1.12 2006/02/22 17:12:31 taddei implemented correct drawing order
 * 
 * Revision 1.11 2006/02/22 13:36:02 taddei refactoring: added service, createOGCWebService; also
 * better except handling
 * 
 * Revision 1.10 2006/02/17 13:38:12 taddei bug fix when counting (resol was using wrong dim) and
 * fixed � (sz)
 * 
 * Revision 1.9 2006/02/14 15:21:41 taddei now working with remote WFS
 * 
 * Revision 1.8 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.7 2006/01/30 14:58:37 taddei minor internal refactoring
 * 
 * Revision 1.6 2006/01/27 10:39:13 taddei query optmization
 * 
 * Revision 1.5 2006/01/26 14:42:31 taddei WMS and WFS invokers woring; minor refactoring
 * 
 * Revision 1.4 2006/01/18 10:21:07 taddei putting wfs service to work
 * 
 * Revision 1.3 2006/01/18 08:59:36 taddei commented out (due to wrong refactoring); fix is coming
 * 
 * Revision 1.2 2006/01/18 08:58:00 taddei implementation (WFS)
 * 
 * Revision 1.1 2005/12/16 15:19:11 taddei added DeafultViewHandler and the Invokers
 * 
 * 
 **************************************************************************************************/
