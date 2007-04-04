//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sde/SDEQueryHandler.java,v 1.11 2006/08/23 16:34:06 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2006 by: M.O.S.S. Computer Grafik Systeme GmbH
 Hohenbrunner Weg 13
 D-82024 Taufkirchen
 http://www.moss.de/

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

 ---------------------------------------------------------------------------*/
package org.deegree.io.datastore.sde;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.PropertyPathResolver;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.sdeapi.SDEConnection;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.XLinkedFeatureProperty;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;

import com.esri.sde.sdk.client.SeFilter;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeSqlConstruct;
import com.esri.sde.sdk.client.SeState;

/**
 * Special <code>QueryHandler</code> implementation for the <code>SDEDatastore</code>.
 *
 * @author <a href="mailto:cpollmann@moss.de">Christoph Pollmann</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.11 $
 */
public class SDEQueryHandler extends AbstractSDERequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( SDEQueryHandler.class );

    // requested feature type
    protected MappedFeatureType rootFeatureType;

    // requested properties of the feature type
    protected PropertyPath[] propertyNames;

    // used to build the initial SELECT (especially the WHERE-clause)
    protected SDEWhereBuilder whereBuilder;

    // key: feature id of features that are generated or are in generation
    protected Set<FeatureId> featuresInGeneration = new HashSet<FeatureId>();

    // key: feature id value, value: Feature
    protected Map<String, Feature> featureMap = new HashMap<String, Feature>( 1000 );

    // value: XLinkedFeatureProperty
    private Collection<XLinkedFeatureProperty> xlinkProperties = new ArrayList<XLinkedFeatureProperty>();

    private Query query = null;

    /**
     * Creates a new instance of <code>SDEQueryHandler</code> from the given parameters.
     *
     * @param datastore
     *            datastore that spawned this QueryHandler
     * @param aliasGenerator
     *            used to generate unique aliases for the tables in the SELECT statements
     * @param conn
     *            SDEConnection to execute the generated SELECT statements against
     * @param rootFeatureType
     *            queried feature type
     * @param query
     *            Query to perform
     * @throws DatastoreException
     */
    public SDEQueryHandler( SDEDatastore datastore, TableAliasGenerator aliasGenerator,
                           SDEConnection conn, MappedFeatureType rootFeatureType, Query query )
                            throws DatastoreException {
        super( datastore, aliasGenerator, conn );
        this.rootFeatureType = rootFeatureType;
        this.propertyNames = PropertyPathResolver.normalizePropertyPaths( rootFeatureType,
                                                                          query.getPropertyNames() );
        this.whereBuilder = this.datastore.getWhereBuilder( rootFeatureType, query.getFilter(),
                                                            aliasGenerator );
        this.aliasGenerator = aliasGenerator;
        this.query = query;
    }

    /**
     * Performs the associated <code>Query</code> against the datastore.
     *
     * @return collection of requested features
     * @throws DatastoreException
     */
    public FeatureCollection performQuery()
                            throws DatastoreException {

        FeatureCollection result = null;
        if ( this.query.getResultType() == RESULT_TYPE.HITS ) {
            // TODO
        } else {
            result = performContentQuery();
        }

        return result;
    }

    /**
     * Performs a query for the feature instances that match the query constraints. This corresponds
     * to a query with resultType=RESULTS.
     *
     * @return a feature collection containing the features that match the query constraints
     * @throws PropertyPathResolvingException
     * @throws DatastoreException
     */
    private FeatureCollection performContentQuery()
                            throws PropertyPathResolvingException, DatastoreException {

        FeatureCollection result = FeatureFactory.createFeatureCollection( "ID", 10000 );
        String[] columns;
        Map<MappedPropertyType, Collection<PropertyPath>> requestedPropertyMap = PropertyPathResolver.determineFetchProperties(
                                                                                                                                this.rootFeatureType,
                                                                                                                                this.propertyNames );
        MappedPropertyType[] requestedProperties = new MappedPropertyType[requestedPropertyMap.size()];
        requestedProperties = requestedPropertyMap.keySet().toArray( requestedProperties );

        Map<String, List<MappingField>> columnsMap = buildColumnsMap( this.rootFeatureType,
                                                                      requestedProperties, true );
        columns = columnsMap.keySet().toArray( new String[columnsMap.size()] );
        Map mappingFieldsMap = buildMappingFieldMap( columns, columnsMap );

        SeQuery stmt = buildInitialSelect( columns );
        Object[] resultValues = new Object[columns.length];

        // necessary to handle that a feature may occur several times in result set
        Set<FeatureId> rootFeatureIds = new HashSet<FeatureId>();

        try {
            int maxFeatures = this.query.getMaxFeatures();
            int startPosition = this.query.getStartPosition();
            int rowCount = 0;
            stmt.execute();
            SeRow row = null;
            if ( maxFeatures != -1 ) {
                if ( startPosition < 0 ) {
                    startPosition = 0;
                }
            }
            for ( ;; ) {
                try {
                    row = stmt.fetch();
                } catch ( Exception e ) {
                    row = null;
                }
                if ( null == row )
                    break;
                rowCount++;
                if ( rowCount < startPosition )
                    continue;
                // collect result values
                for ( int i = 0; i < resultValues.length; i++ ) {
                    try {
                        resultValues[i] = row.getObject( i );
                    } catch ( Exception e ) {
                    }
                }
                FeatureId fid = extractFeatureId( this.rootFeatureType, mappingFieldsMap,
                                                  resultValues );

                // skip it if this root feature has already been fetched
                if ( !rootFeatureIds.contains( fid ) ) {

                    // feature also may have been fetched already as subfeature
                    Feature feature = this.featureMap.get( fid );
                    if ( feature == null ) {
                        feature = extractFeature( fid, this.rootFeatureType, requestedPropertyMap,
                                                  mappingFieldsMap, resultValues );
                    }
                    result.add( feature );
                }
            }
        } catch ( Exception exc ) {
        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
            } catch ( Exception exc2 ) {
            }
        }
        resolveXLinks();
        result.setAttribute( "numberOfFeatures", "" + result.size() );
        return result;
    }

    protected void resolveXLinks()
                            throws DatastoreException {
        Iterator iter = this.xlinkProperties.iterator();
        while ( iter.hasNext() ) {
            XLinkedFeatureProperty property = (XLinkedFeatureProperty) iter.next();
            Feature feature = this.featureMap.get( property.getTargetFeatureId() );
            if ( feature == null ) {
                throw new DatastoreException( "Internal error in QueryHandler." );
            }
            property.setValue( feature );
        }
    }

    /**
     * Builds the initial SELECT statement.
     * <p>
     * This statement determines all feature ids that are affected by the filter, but also SELECTs
     * all properties that are stored in the root feature type's table (to improve efficiency).
     * </p>
     * <p>
     * The statement is structured like this:
     * <ul>
     * <li><code>SELECT</code></li>
     * <li>comma-separated list of selected qualified fields</li>
     * <li><code>FROM</code></li>
     * <li>comma-separated list of tables and their aliases (this is needed to constrain the paths
     * to selected XPath-PropertyNames)</li>
     * <li><code>WHERE</code></li>
     * <li>SQL representation of the Filter expression</li>
     * <li><code>ORDER BY</code></li>
     * <li>qualified sort criteria fields</li>
     * </ul>
     * </p>
     *
     * @param columns
     * @return
     */
    protected SeQuery buildInitialSelect( String[] columns ) {
        SeQuery query = null;
        try {
            StringBuffer whereCondition = new StringBuffer();
            whereBuilder.appendWhereCondition( whereCondition );
            SeSqlConstruct constr = new SeSqlConstruct( rootFeatureType.getTable(),
                                                        whereCondition.toString() );
            query = new SeQuery( getConnection().getConnection(), columns, constr );
            query.setState( getConnection().getState().getId(),
                            new SeObjectId( SeState.SE_NULL_STATE_ID ),
                            SeState.SE_STATE_DIFF_NOCHECK );
            if ( this.query.getFilter() instanceof ComplexFilter ) {
                // There is NO chance, to make a new SeCoordinateReference equal to the existing crs of the requested layer.
                // So, we give it a chance, by passing the layer definitions (and its associated crs) to the whereBuilder method
                List layers = getConnection().getConnection().getLayers();
                SeFilter[] spatialFilter = whereBuilder.buildSpatialFilter(
                                                                            (ComplexFilter) this.query.getFilter(),
                                                                            layers );
                if ( null != spatialFilter && 0 < spatialFilter.length ) {
                    query.setSpatialConstraints( SeQuery.SE_OPTIMIZE, false, spatialFilter );
                }
            }
            query.prepareQuery();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // append sort criteria	(simple implementation)
        // TODO implement sort as comparison operator in feature collection
        // because fetching of subfeatures can not be handled here
        // sort only provides ascendig sorting at the moment!
        /* TODO!!!!
         SortProperty[] sortProperties = query.getSortProperties();
         if (null != sortProperties && 0 < sortProperties.length) {
         String[] sortColumns = new String[sortProperties.length];
         for ( int i = 0; i < sortProperties.length; i++ ) {
         PropertyPath pp = sortProperties[i].getSortProperty();
         PropertyPath npp = PropertyPathResolver.normalizePropertyPath( rootFeatureType, pp );
         QualifiedName propertyName = npp.getStep( 1 ).getPropertyName();
         PropertyType property = rootFeatureType.getProperty( propertyName );
         PropertyContent[] contents = ( (MappedPropertyType) property ).getContents();
         if ( contents[0] instanceof SimplePropertyContent ) {
         sortColumns[i] = ( (SimplePropertyContent) contents[0] ).getMappingField().getField();
         }
         }
         querybuf.append(" ORDER BY ");
         appendColumnsList( querybuf, sortColumns );
         }
         */
        return query;
    }

    /**
     * Builds a SELECT statement to fetch features / properties that are stored in a related table.
     *
     * @param columns
     *            table column names to fetch
     * @param relations
     *            table relations that lead to the table where the property is stored
     * @param resultValues
     *            all retrieved columns from one result set row
     * @param mappingFieldMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @return the statement or null if the keys in resultValues contain NULL values
     */
    private SeQuery buildSubsequentSelect( String[] columns, TableRelation[] relations,
                                          Object[] resultValues, Map mappingFieldMap ) {
        SeQuery query = null;
        try {
            StringBuffer whereCondition = new StringBuffer();

            // joins can't be used in versioned SDEs (so only one join level possible)

            // append key constraints
            MappingField[] fromFields = relations[0].getFromFields();
            MappingField[] toFields = relations[0].getToFields();
            for ( int i = 0; i < fromFields.length; i++ ) {
                Integer resultPos = (Integer) mappingFieldMap.get( fromFields[i] );
                Object keyValue = resultValues[resultPos.intValue()];
                if ( keyValue == null ) {
                    return null;
                }
                whereCondition.append( toFields[i].getField() );
                whereCondition.append( "='" + keyValue.toString() + "'" );
                if ( i != fromFields.length - 1 ) {
                    whereCondition.append( " AND " );
                }
            }
            SeSqlConstruct constr = new SeSqlConstruct( relations[0].getToTable(),
                                                        whereCondition.toString() );

            query = new SeQuery( getConnection().getConnection(), columns, constr );
            query.setState( getConnection().getState().getId(),
                            new SeObjectId( SeState.SE_NULL_STATE_ID ),
                            SeState.SE_STATE_DIFF_NOCHECK );
            query.prepareQuery();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return query;
    }

    /**
     * Extracts a feature from the values of a result set row.
     *
     * @param fid
     *            feature id of the feature
     * @param featureType
     *            feature type of the feature to be extracted
     * @param requestedPropertyMap
     *            requested <code>MappedPropertyType</code>s mapped to <code>Collection</code>
     *            of <code>PropertyPath</code>s
     * @param mappingFieldsMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return the extracted feature
     * @throws GeometryException
     * @throws DatastoreException
     */
    protected Feature extractFeature(
                                     FeatureId fid,
                                     MappedFeatureType featureType,
                                     Map<MappedPropertyType, Collection<PropertyPath>> requestedPropertyMap,
                                     Map mappingFieldsMap, Object[] resultValues )
                            throws DatastoreException {

        this.featuresInGeneration.add( fid );

        // extract the requested properties of the feature
        List<FeatureProperty> propertyList = new ArrayList<FeatureProperty>();
        Iterator propertyIter = requestedPropertyMap.keySet().iterator();
        while ( propertyIter.hasNext() ) {
            MappedPropertyType requestedProperty = (MappedPropertyType) propertyIter.next();
            // PropertyPath[] requestingPaths = PropertyPathResolver.determineSubPropertyPaths
            // (requestedProperty, propertyPaths);
            Collection<FeatureProperty> props = extractProperties( requestedProperty,
                                                                   mappingFieldsMap, resultValues );
            propertyList.addAll( props );
        }
        FeatureProperty[] properties = propertyList.toArray( new FeatureProperty[propertyList.size()] );
        Feature feature = FeatureFactory.createFeature( fid.getAsString(), featureType, properties );

        this.featureMap.put( fid.getAsString(), feature );
        return feature;
    }

    /**
     * Extracts the feature id from the values of a result set row.
     *
     * @param featureType
     *            feature type for which the id shall be extracted
     * @param mappingFieldMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return the feature id
     */
    protected FeatureId extractFeatureId( MappedFeatureType featureType, Map mappingFieldMap,
                                         Object[] resultValues ) {
        MappingField[] idFields = featureType.getGMLId().getIdFields();
        Object[] idValues = new Object[idFields.length];
        for ( int i = 0; i < idFields.length; i++ ) {
            Integer resultPos = (Integer) mappingFieldMap.get( idFields[i] );
            idValues[i] = resultValues[resultPos.intValue()];
        }
        return new FeatureId( featureType.getGMLId(), idValues );
    }

    /**
     * Extracts the properties of the given property type from the values of a result set row. If
     * the property is stored in related table, only the key values are present in the result set
     * row and more SELECTs are built and executed to build the property.
     * <p>
     * FYI: If the property is not stored in a related table, only one FeatureProperty is built,
     * otherwise the number of properties depends on the multiplicity of the relation.
     *
     * @param propertyType
     *            the mapped property type to be extracted
     * @param mappingFieldMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return Collection of FeatureProperty instances
     * 
     * @throws DatastoreException
     */
    private Collection<FeatureProperty> extractProperties(
                                                          MappedPropertyType propertyType,
                                                          Map<MappingField, Integer> mappingFieldMap,
                                                          Object[] resultValues )
                            throws DatastoreException {
        Collection<FeatureProperty> result = null;

        TableRelation[] tableRelations = propertyType.getTableRelations();
        if ( tableRelations != null && tableRelations.length != 0 ) {
            LOG.logDebug( "Fetching related properties: '" + propertyType.getName() + "'..." );
            result = fetchRelatedProperties( propertyType.getName(), propertyType, mappingFieldMap,
                                             resultValues );
        } else {
            Object propertyValue = null;
            if ( propertyType instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) propertyType ).getContent();
                if ( content instanceof MappingField ) {
                    MappingField field = (MappingField) content;
                    Integer resultPos = mappingFieldMap.get( field );
                    propertyValue = resultValues[resultPos.intValue()];
                }
            } else if ( propertyType instanceof MappedGeometryPropertyType ) {
                MappingField field = ( (MappedGeometryPropertyType) propertyType ).getMappingField();
                Integer resultPos = mappingFieldMap.get( field );
                propertyValue = resultValues[resultPos.intValue()];
                propertyValue = this.datastore.convertDBToDegreeGeometry( propertyValue );
            } else {
                String msg = "Unsupported property type: '" + propertyType.getClass().getName()
                             + "' in QueryHandler.extractProperties(). ";
                LOG.logError( msg );
                throw new IllegalArgumentException( msg );
            }
            FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                             propertyType.getName(),
                                                                             propertyValue );
            result = new ArrayList<FeatureProperty>();
            result.add( property );
        }
        return result;
    }

    /**
     *
     * @param propertyName
     * @param pt
     * @param mappingFieldMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return Collection of FeatureProperty instances
     */
    private Collection<FeatureProperty> fetchRelatedProperties( QualifiedName propertyName,
                                                               MappedPropertyType pt,
                                                               Map mappingFieldMap,
                                                               Object[] resultValues ) {
        Collection<FeatureProperty> result = new ArrayList<FeatureProperty>( 100 );
        SeQuery stmt = null;
        SeRow row = null;
        try {
            if ( pt instanceof MappedSimplePropertyType ) {

                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
                if ( content instanceof MappingField ) {
                    String column = ((MappingField) content).getField();
                    stmt = buildSubsequentSelect( new String[] { column }, pt.getTableRelations(),
                                                  resultValues, mappingFieldMap );
                    if ( stmt != null ) {
                        stmt.execute();
                        for ( ;; ) {
                            try {
                                row = stmt.fetch();
                            } catch ( Exception e ) {
                                row = null;
                            }
                            if ( null == row )
                                break;
                            Object propertyValue = row.getObject( 0 );
                            FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                                             propertyName,
                                                                                             propertyValue );
                            result.add( property );
                        }
                    }
                }
            } else if ( pt instanceof MappedGeometryPropertyType ) {
                String column = ( (MappedGeometryPropertyType) pt ).getMappingField().getField();

                stmt = buildSubsequentSelect( new String[] { column }, pt.getTableRelations(),
                                              resultValues, mappingFieldMap );
                if ( stmt != null ) {
                    stmt.execute();
                    for ( ;; ) {
                        try {
                            row = stmt.fetch();
                        } catch ( Exception e ) {
                            row = null;
                        }
                        if ( null == row )
                            break;
                        Object value = row.getObject( 0 );
                        Geometry geometry = this.datastore.convertDBToDegreeGeometry( value );
                        FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                                         propertyName,
                                                                                         geometry );
                        result.add( property );
                    }
                }
            } else {
                String msg = "Unsupported content type: '" + pt.getClass().getName()
                             + "' in QueryHandler.fetchRelatedProperties().";
                LOG.logError( msg );
                throw new DatastoreException( msg );
            }
        } catch ( Exception exc ) {
        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
            } catch ( Exception exc2 ) {
            }
        }
        return result;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: SDEQueryHandler.java,v $
 * Revision 1.11  2006/08/23 16:34:06  mschneider
 * Added handling of virtual properties. Needs testing.
 *
 * Revision 1.10  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.9  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.8  2006/08/21 15:44:38  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.7  2006/08/15 17:38:43  mschneider
 * Improved javadoc.
 *
 * Revision 1.6  2006/08/06 20:38:51  poth
 * never thrown exceptions and never read variables removed
 *
 * Revision 1.5  2006/07/10 21:07:31  mschneider
 * Removed System.out.println's.
 *
 * Revision 1.4  2006/06/01 15:21:33  mschneider
 * Renamed PropertyPathResolver.determineRequestedProperties() to PropertyPathResolver.determineFetchProperties().
 *
 * Revision 1.3  2006/06/01 13:09:49  mschneider
 * Added use of Generics for type safety.
 *
 * Revision 1.2  2006/05/26 09:42:58  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 * Revision 1.1  2006/05/21 19:06:21  poth
 * initial load up
 *
 * Revision 1.1  2006/05/09 14:51:52  polli
 * no message
 *
 **************************************************************************************************/
