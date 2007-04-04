//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/shape/ShapeDatastore.java,v 1.50 2006/11/16 08:55:32 mschneider Exp $
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

package org.deegree.io.datastore.shape;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.dbaseapi.DBaseException;
import org.deegree.io.dbaseapi.DBaseFile;
import org.deegree.io.shpapi.HasNoDBaseFileException;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.FilterTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.w3c.dom.Element;

/**
 * {@link Datastore} implementation that allows read access to ESRI shape files.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.50 $, $Date: 2006/11/16 08:55:32 $
 */
public class ShapeDatastore extends Datastore {

    private static final ILogger LOG = LoggerFactory.getLogger( ShapeDatastore.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    // keys: FeatureTypes, values: Property-To-column mappings
    private Map<FeatureType, Map> ftMappings = new HashMap<FeatureType, Map>();

    // NOTE: this is equal for all bound schemas
    private URL shapeFileURL;

    private String srsName;

    /**
     * Adds the given GML application schema to the set of schemas that are handled by this
     * datastore instance.
     * <p>
     * Note that this method may be called several times for every GML schema that uses this
     * datastore instance.
     * 
     * @param schema
     *            GML application schema to bind
     * @throws DatastoreException
     */
    @Override
    public void bindSchema( MappedGMLSchema schema )
                            throws DatastoreException {
        super.bindSchema( schema );
        validate( schema );
        srsName = schema.getDefaultSRS().toString();
    }

    /**
     * Performs a {@link Query} against the datastore.
     * 
     * @param query
     *            query to be performed
     * @param ft
     *            the root feature type that is queried
     * @return requested feature instances
     * @throws DatastoreException
     */
    @Override
    public FeatureCollection performQuery( Query query, MappedFeatureType ft )
                            throws DatastoreException {

        query = transformQuery( query );

        FeatureCollection result = null;
        ShapeFile shapeFile = null;
        int startPosition = -1;
        int maxFeatures = -1;

        int record = -1;
        try {
            LOG.logDebug( "Opening shapefile '" + shapeFileURL.getFile() + ".shp'." );
            shapeFile = new ShapeFile( shapeFileURL.getFile() );
            startPosition = query.getStartPosition();
            maxFeatures = query.getMaxFeatures();
            Filter filter = query.getFilter();
            Envelope bbox = null;
            if ( filter instanceof ComplexFilter ) {
                Object[] objects = null;
                try {
                    objects = FilterTools.extractFirstBBOX( (ComplexFilter) filter );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( "DATASTORE_EXTRACTBBOX", record );
                    throw new DatastoreException( msg, e );
                }
                bbox = (Envelope) objects[0];
                filter = (Filter) objects[1];
            }
            if ( bbox == null ) {
                bbox = shapeFile.getFileMBR();
            }

            shapeFile.setFeatureType( ft, ftMappings.get( ft ) );

            int[] idx = shapeFile.getGeoNumbersByRect( bbox );
            //id=identity required
            IDGenerator idg = IDGenerator.getInstance();
            String id = ft.getName().getLocalName();
            id += idg.generateUniqueID();
            if ( idx != null ) {
                // check parameters for sanity
                if ( startPosition < 1 ) {
                    startPosition = 1;
                }
                if ( ( maxFeatures < 0 ) || ( maxFeatures >= idx.length ) ) {
                    maxFeatures = idx.length;
                }
                LOG.logDebug( "Generating ID '" + id + "' for the FeatureCollection." );
                result = FeatureFactory.createFeatureCollection( id, idx.length );

                // TODO: respect startposition
                
                CoordinateSystem crs = CRSFactory.create( srsName );
                for ( int i = 0; i < maxFeatures; i++ ) {
                    record = idx[i];
                    Feature feat = shapeFile.getFeatureByRecNo( idx[i] );
                    if ( filter == null || filter.evaluate( feat ) ) {
                        LOG.logDebug( "Adding feature '" + feat.getId() + "' to FeatureCollection." );
                        GeometryImpl geom = (GeometryImpl) feat.getDefaultGeometryPropertyValue();
                        geom.setCoordinateSystem( crs );
                        result.add( feat );
                    }
                }
            } else {
                result = FeatureFactory.createFeatureCollection( id, 1 );
            }

        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = Messages.getMessage( "DATASTORE_READINGFROMDBF", record );
            throw new DatastoreException( msg, e );
        } catch ( DBaseException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = Messages.getMessage( "DATASTORE_READINGFROMDBF", record );
            throw new DatastoreException( msg, e );
        } catch ( HasNoDBaseFileException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = Messages.getMessage( "DATASTORE_NODBASEFILE", record );
            throw new DatastoreException( msg, e );
        } catch ( FilterEvaluationException e ) {
            throw new DatastoreException( e.getMessage(), e );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String msg = Messages.getMessage( "DATASTORE_READINGFROMDBF", record );
            throw new DatastoreException( msg, e );
        } finally {
            LOG.logDebug( "Closing shapefile." );
            try {
                shapeFile.close();
            } catch ( Exception e ) {
                String msg = Messages.getMessage( "DATASTORE_ERROR_CLOSING_SHAPEFILE",
                                                  this.shapeFileURL.getFile() );
                throw new DatastoreException( msg );
            }
        }

        // transform result to queried srs if necessary
        String targetSrsName = query.getSrsName();
        if (targetSrsName != null && !targetSrsName.equals(this.srsName)) {
            result = transformResult( result, query.getSrsName() );            
        }

        return result;
    }

    /**
     * Performs a {@link Query} against the datastore (in the given transaction context).
     * <p>
     * NOTE: In ShapeDatastore, this method behaves exactly the same as
     * #performQuery(Query,MappedFeatureType,DatastoreTransaction).
     * 
     * @param query
     *            query to be performed
     * @param rootFeatureType
     *            the root feature type that is queried
     * @param context
     *            context (used to specify the JDBCConnection, for example)
     * @return requested feature instances
     * @throws DatastoreException
     */
    @Override
    public FeatureCollection performQuery( final Query query,
                                           final MappedFeatureType rootFeatureType,
                                           final DatastoreTransaction context )
                            throws DatastoreException {
        return performQuery( query, rootFeatureType );
    }

    /**
     * Validates the given {@link MappedGMLSchema} against the available columns in the
     * referenced shape file.
     * 
     * @param schema
     * @throws DatastoreException
     */
    private void validate( MappedGMLSchema schema )
                            throws DatastoreException {

        Set<String> columnNames = determineShapeFileColumns( schema );

        FeatureType[] featureTypes = schema.getFeatureTypes();
        for ( int i = 0; i < featureTypes.length; i++ ) {
            Map<PropertyType, String> ftMapping = getFTMapping( featureTypes[i], columnNames );
            ftMappings.put( featureTypes[i], ftMapping );
        }
    }

    private Map<PropertyType, String> getFTMapping( FeatureType ft, Set<String> columnNames )
                            throws DatastoreException {
        Map<PropertyType, String> ftMapping = new HashMap<PropertyType, String>();
        PropertyType[] properties = ft.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            MappedPropertyType pt = (MappedPropertyType) properties[i];
            if ( pt instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
                if ( !( content instanceof MappingField ) ) {
                    String msg = Messages.getMessage( "DATASTORE_UNSUPPORTED_CONTENT", pt.getName() );
                    throw new DatastoreException( msg );
                }
                String field = ( (MappingField) content ).getField();
                if ( !columnNames.contains( field ) ) {
                    String msg = Messages.getMessage( "DATASTORE_FIELDNOTFOUND", field,
                                                      pt.getName(), shapeFileURL.getFile(),
                                                      columnNames );
                    throw new DatastoreException( msg );
                }
                ftMapping.put( pt, field );
            } else if ( pt instanceof MappedGeometryPropertyType ) {
                // nothing to do
            } else {
                String msg = Messages.getMessage( "DATASTORE_NO_NESTED_FEATURE_TYPES", pt.getName() );
                throw new DatastoreException( msg );
            }
        }
        return ftMapping;
    }

    private Set<String> determineShapeFileColumns( MappedGMLSchema schema )
                            throws DatastoreException {

        Set<String> columnNames = new HashSet<String>();
        DBaseFile dbfFile = null;

        try {
            Element schemaRoot = schema.getDocument().getRootElement();
            String shapePath = XMLTools.getNodeAsString(
                                                         schemaRoot,
                                                         "xs:annotation/xs:appinfo/deegreewfs:File/text()",
                                                         nsContext, null );
            shapeFileURL = schema.getDocument().resolve( shapePath );
            LOG.logDebug( "Opening dbf file '" + shapeFileURL + "'." );
            dbfFile = new DBaseFile( shapeFileURL.getFile() );
            String[] columns = dbfFile.getProperties();
            for ( int i = 0; i < columns.length; i++ ) {
                columnNames.add( columns[i] );
            }
            String s = "Successfully opened dbf file '" + shapeFileURL.getFile()
                       + "' and retrieved the property columns.";
            LOG.logInfo( s );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DatastoreException( Messages.getMessage( "DATASTORE_DBACCESSERROR" ) );
        } finally {
            if ( dbfFile != null ) {
                dbfFile.close();
            }
        }

        return columnNames;
    }

    /**
     * Closes the datastore so it can free dependent resources.
     * 
     * @throws DatastoreException
     */
    @Override
    public void close()
                            throws DatastoreException {
        // TODO
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ShapeDatastore.java,v $
 Revision 1.50  2006/11/16 08:55:32  mschneider
 Moved exceptions for unimplemented methods to abstract Datastore class.

 Revision 1.49  2006/11/15 12:56:13  schmitz
 Fixed nullpointerexception.

 Revision 1.48  2006/11/09 17:38:38  mschneider
 Added check if transformation is necessary.

 Revision 1.47  2006/10/10 15:52:25  mschneider
 Added comment because of startPosition parameter.

 Revision 1.46  2006/09/27 20:30:20  mschneider
 Activated transformation of query and result SRS.

 Revision 1.45  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.44  2006/09/05 17:42:56  mschneider
 Added @Override annotations. Fixed logging output of filenames.

 Revision 1.43  2006/08/30 18:08:26  mschneider
 Fixed exception chaining.

 Revision 1.42  2006/08/30 17:40:44  mschneider
 Removed System.out.println-calls.

 Revision 1.41  2006/08/30 17:01:19  mschneider
 Major cleanup and javadoc corrections.

 Revision 1.40  2006/07/10 21:07:31  mschneider
 Removed System.out.println's.

 Revision 1.39  2006/06/07 14:55:58  poth
 *** empty log message ***

 Revision 1.38  2006/06/06 10:33:28  poth
 bug fix

 Revision 1.37  2006/06/06 07:57:27  poth
 bug fix reading feature collection outside a shapes MBR / performance enhancement

 Revision 1.36  2006/05/31 07:05:44  taddei
 chnaged log level fom info to debug

 Revision 1.35  2006/05/30 08:08:36  taddei
 bug fix: datastore was returning no props, instead of all (when props not specified)

 Revision 1.34  2006/05/29 20:42:42  poth
 some code enhancements

 Revision 1.33  2006/05/29 16:15:14  poth
 bug fix - dbase file closed after reading column definitions

 Revision 1.32  2006/05/29 12:31:57  taddei
 fix for missing prefix

 Revision 1.31  2006/05/26 14:52:17  taddei
 bug fixes (srs, wfs:prefix)

 Revision 1.30  2006/05/24 13:50:24  poth
 bug fix creating feature
 ********************************************************************** */