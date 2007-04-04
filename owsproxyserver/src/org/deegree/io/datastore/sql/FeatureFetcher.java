//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/FeatureFetcher.java,v 1.6 2006/11/29 16:59:54 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.io.datastore.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.PropertyPathResolver;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.ConstantContent;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SQLFunctionCall;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.XLinkedFeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * The only implementation of this abstract class is the {@link QueryHandler} class.
 * <p>
 * While the {@link QueryHandler} class performs the initial SELECT, {@link FeatureFetcher}
 * is responsible for any subsequent SELECTs that may be necessary. 
 * 
 * @see QueryHandler
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.6 $, $Date: 2006/11/29 16:59:54 $
 */
abstract class FeatureFetcher extends AbstractRequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureFetcher.class );

    // key: feature id of features that are generated or are in generation
    protected Set<FeatureId> featuresInGeneration = new HashSet<FeatureId>();

    // key: feature id value, value: Feature
    protected Map<String, Feature> featureMap = new HashMap<String, Feature>( 1000 );

    // provides virtual content (constants, sql functions, ...)
    protected VirtualContentProvider vcProvider;

    // value: XLinkedFeatureProperty
    protected Collection<XLinkedFeatureProperty> xlinkProperties = new ArrayList<XLinkedFeatureProperty>();

    protected Query query;

    // TODO do this a better way!!!
    private Map<MappingGeometryField, SimpleContent> geometryFieldMap = new HashMap<MappingGeometryField, SimpleContent>();

    FeatureFetcher( AbstractSQLDatastore datastore, TableAliasGenerator aliasGenerator,
                   Connection conn, Query query ) {
        super( datastore, aliasGenerator, conn );
        this.query = query;
    }

    /**
     * Builds a SELECT statement to fetch features / properties that are stored in a related table.
     *
     * @param fetchContents
     *            table columns / functions to fetch
     * @param relations
     *            table relations that lead to the table where the property is stored
     * @param resultValues
     *            all retrieved columns from one result set row
     * @param resultPosMap
     *            key class: SimpleContent, value class: Integer (this is the associated index in
     *            resultValues)
     * @return the statement or null if the keys in resultValues contain NULL values
     */
    private StatementBuffer buildSubsequentSelect( List<List<SimpleContent>> fetchContents,
                                                  TableRelation[] relations, Object[] resultValues,
                                                  Map<SimpleContent, Integer> resultPosMap ) {

        this.aliasGenerator.reset();
        String[] tableAliases = this.aliasGenerator.generateUniqueAliases( relations.length );

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        appendQualifiedContentList( query, tableAliases[tableAliases.length - 1], fetchContents );
        query.append( " FROM " );
        query.append( relations[0].getToTable() );
        query.append( " " );
        query.append( tableAliases[0] );

        // append joins
        for ( int i = 1; i < relations.length; i++ ) {
            query.append( " JOIN " );
            query.append( relations[i].getToTable() );
            query.append( " " );
            query.append( tableAliases[i] );
            query.append( " ON " );
            MappingField[] fromFields = relations[i].getFromFields();
            MappingField[] toFields = relations[i].getToFields();
            for ( int j = 0; j < fromFields.length; j++ ) {
                query.append( tableAliases[i - 1] );
                query.append( '.' );
                query.append( fromFields[j].getField() );
                query.append( '=' );
                query.append( tableAliases[i] );
                query.append( '.' );
                query.append( toFields[j].getField() );
            }
        }

        // append key constraints
        query.append( " WHERE " );
        MappingField[] fromFields = relations[0].getFromFields();
        MappingField[] toFields = relations[0].getToFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            int resultPos = resultPosMap.get( fromFields[i] );
            Object keyValue = resultValues[resultPos];
            if ( keyValue == null ) {
                return null;
            }
            query.append( tableAliases[0] );
            query.append( '.' );
            query.append( toFields[i].getField() );
            query.append( "=?" );
            query.addArgument( keyValue, toFields[i].getType() );
            if ( i != fromFields.length - 1 ) {
                query.append( " AND " );
            }
        }
        return query;
    }

    /**
     * Builds a SELECT statement to fetch the feature ids and the (concrete) feature types of feature properties
     * that are stored in a related table (currently limited to *one* join table).
     * <p>
     * This is only necessary for feature properties that contain feature types with more than one possible
     * substitution.
     *
     * @param relation1
     *            first table relation that leads to the join table
     * @param relation2
     *            second table relation that leads to the table where the property is stored
     * @param resultValues
     *            all retrieved columns from one result set row
     * @param mappingFieldMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @return the statement or null if the keys in resultValues contain NULL values
     */
    private StatementBuffer buildFeatureTypeSelect( TableRelation relation1,
                                                   TableRelation relation2, Object[] resultValues,
                                                   Map mappingFieldMap ) {
        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        // append feature type column
        query.append( FT_COLUMN );
        // append feature id columns
        MappingField[] fidFields = relation2.getFromFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( ',' );
            query.append( fidFields[i].getField() );
        }
        query.append( " FROM " );
        query.append( relation1.getToTable() );
        query.append( " WHERE " );
        // append key constraints
        MappingField[] fromFields = relation1.getFromFields();
        MappingField[] toFields = relation1.getToFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            Integer resultPos = (Integer) mappingFieldMap.get( fromFields[i] );
            Object keyValue = resultValues[resultPos.intValue()];
            if ( keyValue == null ) {
                return null;
            }
            query.append( toFields[i].getField() );
            query.append( "=?" );
            query.addArgument( keyValue, toFields[i].getType() );
            if ( i != fromFields.length - 1 ) {
                query.append( " AND " );
            }
        }
        return query;
    }

    /**
     * Builds a SELECT statement that fetches one feature and it's properties.
     *
     * @param fid
     *            id of the feature to fetch
     * @param table
     *            root table of the feature
     * @param fetchContents
     * @return the statement or null if the keys in resultValues contain NULL values
     */
    private StatementBuffer buildFeatureSelect( FeatureId fid, String table,
                                               List<List<SimpleContent>> fetchContents ) {

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        appendQualifiedContentList( query, table, fetchContents );
        query.append( " FROM " );
        query.append( table );
        query.append( " WHERE " );

        // append feature id constraints
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( fidFields[i].getField() );
            query.append( "=?" );
            query.addArgument( fid.getValue( i ), fidFields[i].getType() );
            if ( i != fidFields.length - 1 ) {
                query.append( " AND " );
            }
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
     * @param resultPosMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return the extracted feature
     * @throws SQLException
     *             if a JDBC related error occurs
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    protected Feature extractFeature(
                                     FeatureId fid,
                                     MappedFeatureType featureType,
                                     Map<MappedPropertyType, Collection<PropertyPath>> requestedPropertyMap,
                                     Map<SimpleContent, Integer> resultPosMap, Object[] resultValues )
                            throws SQLException, DatastoreException, UnknownCRSException {

        this.featuresInGeneration.add( fid );

        // extract the requested properties of the feature
        List<FeatureProperty> propertyList = new ArrayList<FeatureProperty>();
        for ( MappedPropertyType requestedProperty : requestedPropertyMap.keySet() ) {
            Collection<PropertyPath> propertyPaths = requestedPropertyMap.get( requestedProperty );
            // PropertyPath[] requestingPaths = PropertyPathResolver.determineSubPropertyPaths
            // (requestedProperty, propertyPaths);
            Collection<FeatureProperty> props = extractProperties( requestedProperty,
                                                                   propertyPaths, resultPosMap,
                                                                   resultValues );
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
     * @param mfMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return the feature id
     */
    protected FeatureId extractFeatureId( MappedFeatureType featureType,
                                         Map<SimpleContent, Integer> mfMap, Object[] resultValues ) {
        MappingField[] idFields = featureType.getGMLId().getIdFields();
        Object[] idValues = new Object[idFields.length];
        for ( int i = 0; i < idFields.length; i++ ) {
            Integer resultPos = mfMap.get( idFields[i] );
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
     * @param pt
     *            the mapped property type to be extracted
     * @param propertyPaths
     *            property paths that refer to the property to be extracted
     * @param resultPosMap
     *            key class: SimpleContent, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return Collection of FeatureProperty instances
     * @throws SQLException
     *             if a JDBC related error occurs
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    private Collection<FeatureProperty> extractProperties( MappedPropertyType pt,
                                                          Collection<PropertyPath> propertyPaths,
                                                          Map<SimpleContent, Integer> resultPosMap,
                                                          Object[] resultValues )
                            throws SQLException, DatastoreException, UnknownCRSException {

        Collection<FeatureProperty> result = null;

        TableRelation[] tableRelations = pt.getTableRelations();
        if ( tableRelations != null && tableRelations.length != 0 ) {
            LOG.logDebug( "Fetching related properties: '" + pt.getName() + "'..." );
            result = fetchRelatedProperties( pt.getName(), pt, propertyPaths, resultPosMap,
                                             resultValues );
        } else {
            Object propertyValue = null;
            if ( pt instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
                if ( content instanceof MappingField ) {
                    Integer resultPos = resultPosMap.get( content );
                    propertyValue = resultValues[resultPos.intValue()];
                } else if ( content instanceof ConstantContent ) {
                    propertyValue = ( (ConstantContent) content ).getValue();
                } else if ( content instanceof SQLFunctionCall ) {
                    Integer resultPos = resultPosMap.get( content );
                    propertyValue = resultValues[resultPos.intValue()];
                }
            } else if ( pt instanceof MappedGeometryPropertyType ) {

                // TODO resultPosMap may contain SQLFunctionCall instead of MappingField!!!
                MappingField field = ( (MappedGeometryPropertyType) pt ).getMappingField();
                SimpleContent content = this.geometryFieldMap.get( field );

                Integer resultPos = resultPosMap.get( content );
                propertyValue = resultValues[resultPos.intValue()];

                CoordinateSystem cs = ( (MappedGeometryPropertyType) pt ).getCS();
                if ( this.query.getSrsName() != null ) {
                    cs = CRSFactory.create( this.query.getSrsName() );
                }

                propertyValue = this.datastore.convertDBToDeegreeGeometry( propertyValue, cs, conn );
            } else {
                String msg = "Unsupported property type: '" + pt.getClass().getName()
                             + "' in QueryHandler.extractProperties(). ";
                LOG.logError( msg );
                throw new IllegalArgumentException( msg );
            }
            FeatureProperty property = FeatureFactory.createFeatureProperty( pt.getName(),
                                                                             propertyValue );
            result = new ArrayList<FeatureProperty>();
            result.add( property );
        }
        return result;
    }

    /**
     * Extracts a {@link FeatureId} from one result set row.
     *
     * @param featureType
     * @param rs
     * @param startIndex
     * @return feature id from result set row
     * @throws SQLException
     */
    private FeatureId extractFeatureId( MappedFeatureType featureType, ResultSet rs, int startIndex )
                            throws SQLException {
        MappedGMLId gmlId = featureType.getGMLId();
        MappingField[] idFields = gmlId.getIdFields();

        Object[] idValues = new Object[idFields.length];
        for ( int i = 0; i < idValues.length; i++ ) {
            idValues[i] = rs.getObject( i + startIndex );
        }
        return new FeatureId( gmlId, idValues );
    }

    /**
     * Determines the columns / functions that have to be fetched from the table of the givens
     * {@link MappedFeatureType} and associates identical columns / functions to avoid that the
     * same column / function is SELECTed more than once. Identical columns are put into the same
     * (inner) list.
     * <p>
     * The following {@link SimpleContent} instances of the {@link MappedFeatureType}s annotation
     * are used to build the list:
     * <ul>
     * <li>MappingFields from the wfs:gmlId - annotation element of the feature type definition</li>
     * <li>MappingFields in the annotations of the property element definitions; if the property's
     * content is stored in a related table, the MappingFields used in the first wfs:Relation
     * element's wfs:From element are considered</li>
     * <li>SQLFunctionCalls in the annotations of the property element definitions</li>
     * </ul>
     *
     * @param ft
     *             feature type for which the content list is built
     * @param requestedProps
     *             requested properties
     * @return key class: String (column names), value class: List (containing MappingField
     *         instances)
     * @throws DatastoreException 
     */
    protected List<List<SimpleContent>> determineFetchContents( MappedFeatureType ft,
                                                               PropertyType[] requestedProps )
                            throws DatastoreException {

        List<List<SimpleContent>> fetchList = new ArrayList<List<SimpleContent>>();

        // helper lookup map (column names -> referencing MappingField instances)
        Map<String, List<SimpleContent>> columnsMap = new HashMap<String, List<SimpleContent>>();

        // add table columns that are necessary to build the feature's gml id
        MappingField[] idFields = ft.getGMLId().getIdFields();
        for ( int i = 0; i < idFields.length; i++ ) {
            List<SimpleContent> mappingFieldList = columnsMap.get( idFields[i].getField() );
            if ( mappingFieldList == null ) {
                mappingFieldList = new ArrayList<SimpleContent>();
            }
            mappingFieldList.add( idFields[i] );
            columnsMap.put( idFields[i].getField(), mappingFieldList );
        }

        // add columns that are necessary to build the requested feature properties
        for ( int i = 0; i < requestedProps.length; i++ ) {
            MappedPropertyType pt = (MappedPropertyType) requestedProps[i];
            TableRelation[] tableRelations = pt.getTableRelations();
            if ( tableRelations != null && tableRelations.length != 0 ) {
                // if property is not stored in feature type's table, retrieve key fields of
                // the first relation's 'From' element
                MappingField[] fields = tableRelations[0].getFromFields();
                for ( int k = 0; k < fields.length; k++ ) {
                    List<SimpleContent> list = columnsMap.get( fields[k].getField() );
                    if ( list == null ) {
                        list = new ArrayList<SimpleContent>();
                    }
                    list.add( fields[k] );
                    columnsMap.put( fields[k].getField(), list );
                }
                // if (content instanceof FeaturePropertyContent) {
                // if (tableRelations.length == 1) {
                // // if feature property contains an abstract feature type, retrieve
                // // feature type as well (stored in column named "FT_fk")
                // MappedFeatureType subFeatureType = ( (FeaturePropertyContent) content )
                // .getFeatureTypeReference().getFeatureType();
                // if (subFeatureType.isAbstract()) {
                // String typeColumn = FT_PREFIX + fields [0].getField();
                // columnsMap.put (typeColumn, new ArrayList ());
                // }
                // }
                // }
            } else {
                String column = null;
                SimpleContent content = null;
                if ( pt instanceof MappedSimplePropertyType ) {
                    content = ( (MappedSimplePropertyType) pt ).getContent();
                    if ( content instanceof MappingField ) {
                        column = ( (MappingField) content ).getField();
                    } else {
                        // ignore virtual properties here
                        continue;
                    }
                } else if ( pt instanceof MappedGeometryPropertyType ) {
                    content = determineFetchContent( (MappedGeometryPropertyType) pt,
                                                     query.getSrsName() );
                    column = ( (MappedGeometryPropertyType) pt ).getMappingField().getField();
                } else {
                    assert false;
                }
                List<SimpleContent> contentList = columnsMap.get( column );
                if ( contentList == null ) {
                    contentList = new ArrayList<SimpleContent>();
                }
                contentList.add( content );
                columnsMap.put( column, contentList );
            }
        }

        fetchList.addAll( columnsMap.values() );

        // add functions that are necessary to build the requested feature properties
        for ( int i = 0; i < requestedProps.length; i++ ) {
            MappedPropertyType pt = (MappedPropertyType) requestedProps[i];
            if ( pt instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
                if ( content instanceof SQLFunctionCall ) {
                    List<SimpleContent> functionCallList = new ArrayList<SimpleContent>( 1 );
                    functionCallList.add( content );
                    fetchList.add( functionCallList );
                } else {
                    // ignore other content types here
                    continue;
                }
            }

        }
        return fetchList;
    }

    /**
     * Determines a {@link SimpleContent} object that represents the queried
     * {@link MappedGeometryProperty} in the requested SRS.
     * <p>
     * <ul>
     * <li>If the query SRS is identical to the geometry field's SRS (and thus the SRS of the
     * stored geometry, the corresponding {@link MappingGeometryField} is returned.</li>
     * <li>If the query SRS differs from the geometry field's SRS (and thus the SRS of the
     * stored geometry, an {@link SQLFunctionCall} is returned that refers to the stored geometry,
     * in the query SRS.</li>
     * </ul>  
     * 
     * @param pt
     *             geometry property
     * @param queriedSRS
     *             name of the queried SRS (may be null)
     * @return a <code>SimpleContent</code> instance that represents the queried geometry property
     * @throws DatastoreException if the transform call cannot be generated
     */
    private SimpleContent determineFetchContent( MappedGeometryPropertyType pt, String queriedSRS )
                            throws DatastoreException {

        SimpleContent content = null;        
        MappingGeometryField field = pt.getMappingField();

        queriedSRS = this.datastore.checkTransformation( pt, queriedSRS );
        if ( queriedSRS != null ) {
            content = this.datastore.buildSRSTransformCall( pt, queriedSRS );
        } else {
            content = field;
        }

        // TODO 
        this.geometryFieldMap.put( field, content );

        return content;
    }

    /**
     * Retrieves the feature with the given (concrete) type and feature id.
     *
     * @param ft
     * @param requestedPaths
     * @param fid
     * @return the feature with the given type and feature id, may be null
     * @throws SQLException
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    private Feature fetchFeature( MappedFeatureType ft, PropertyPath[] requestedPaths, FeatureId fid )
                            throws SQLException, DatastoreException, UnknownCRSException {

        Feature feature = null;
        Map<MappedPropertyType, Collection<PropertyPath>> requestedPropMap = PropertyPathResolver.determineFetchProperties(
                                                                                                                            ft,
                                                                                                                            requestedPaths );
        MappedPropertyType[] requestedProps = requestedPropMap.keySet().toArray(
                                                                                 new MappedPropertyType[requestedPropMap.size()] );

        if ( requestedProps.length > 0 ) {

            // determine contents (fields / functions) that must be SELECTed from root table
            List<List<SimpleContent>> fetchContents = determineFetchContents( ft, requestedProps );
            Map<SimpleContent, Integer> resultPosMap = buildResultPosMap( fetchContents );

            // build feature query
            StatementBuffer query = buildFeatureSelect( fid, ft.getTable(), fetchContents );
            LOG.logDebug( "Feature query: '" + query + "'" );
            Object[] resultValues = new Object[fetchContents.size()];
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                stmt = this.datastore.prepareStatement( this.conn, query );
                rs = stmt.executeQuery();

                if ( rs.next() ) {
                    // collect result values
                    for ( int i = 0; i < resultValues.length; i++ ) {
                        resultValues[i] = rs.getObject( i + 1 );
                    }
                    feature = extractFeature( fid, ft, requestedPropMap, resultPosMap, resultValues );
                } else {
                    String msg = Messages.getMessage( "DATASTORE_FEATURE_QUERY_NO_RESULT",
                                                      query.getQueryString() );
                    LOG.logError( msg );
                    throw new DatastoreException( msg );
                }
                if ( rs.next() ) {
                    String msg = Messages.getMessage(
                                                      "DATASTORE_FEATURE_QUERY_MORE_THAN_ONE_RESULT",
                                                      query.getQueryString() );
                    LOG.logError( msg );
                    throw new DatastoreException( msg );
                }
            } finally {
                try {
                    if ( rs != null ) {
                        rs.close();
                    }
                } finally {
                    if ( stmt != null ) {
                        stmt.close();
                    }
                }
            }
        }
        return feature;
    }

    /**
     *
     * @param propertyName
     * @param pt
     * @param propertyPaths
     *             property paths that refer to the property to be extracted
     * @param resultPosMap
     *            key class: MappingField, value class: Integer (this is the associated index in
     *            resultValues)
     * @param resultValues
     *            all retrieved columns from one result set row
     * @return Collection of FeatureProperty instances
     * @throws SQLException
     *             if a JDBC related error occurs
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    private Collection<FeatureProperty> fetchRelatedProperties(
                                                               QualifiedName propertyName,
                                                               MappedPropertyType pt,
                                                               Collection<PropertyPath> propertyPaths,
                                                               Map<SimpleContent, Integer> resultPosMap,
                                                               Object[] resultValues )
                            throws SQLException, DatastoreException, UnknownCRSException {

        Collection<FeatureProperty> result = new ArrayList<FeatureProperty>( 100 );
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if ( pt instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();

                // TODO check for invalid content types

                List<SimpleContent> fetchContents = new ArrayList<SimpleContent>( 1 );
                List<List<SimpleContent>> fetchContentsList = new ArrayList<List<SimpleContent>>( 1 );
                fetchContents.add( content );
                fetchContentsList.add( fetchContents );

                StatementBuffer query = buildSubsequentSelect( fetchContentsList,
                                                               pt.getTableRelations(),
                                                               resultValues, resultPosMap );
                LOG.logDebug( "Subsequent query: '" + query + "'" );
                if ( query != null ) {
                    stmt = this.datastore.prepareStatement( this.conn, query );
                    rs = stmt.executeQuery();
                    while ( rs.next() ) {
                        Object propertyValue = rs.getObject( 1 );
                        FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                                         propertyName,
                                                                                         propertyValue );
                        result.add( property );
                    }
                }

            } else if ( pt instanceof MappedGeometryPropertyType ) {
                MappingField content = ( (MappedGeometryPropertyType) pt ).getMappingField();
                CoordinateSystem cs = ( (MappedGeometryPropertyType) pt ).getCS();

                List<SimpleContent> fetchContents = new ArrayList<SimpleContent>( 1 );
                List<List<SimpleContent>> fetchContentsList = new ArrayList<List<SimpleContent>>( 1 );
                fetchContents.add( content );
                fetchContentsList.add( fetchContents );

                StatementBuffer query = buildSubsequentSelect( fetchContentsList,
                                                               pt.getTableRelations(),
                                                               resultValues, resultPosMap );
                LOG.logDebug( "Subsequent query: '" + query + "'" );
                if ( query != null ) {
                    stmt = this.datastore.prepareStatement( this.conn, query );
                    rs = stmt.executeQuery();
                    while ( rs.next() ) {
                        Object value = rs.getObject( 1 );
                        Geometry geometry = this.datastore.convertDBToDeegreeGeometry( value, cs,
                                                                                       this.conn );
                        FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                                         propertyName,
                                                                                         geometry );
                        result.add( property );
                    }
                }
            } else if ( pt instanceof MappedFeaturePropertyType ) {
                MappedFeatureType featureType = ( (MappedFeaturePropertyType) pt ).getFeatureTypeReference().getFeatureType();
                FeatureType[] substitutions = featureType.getGMLSchema().getSubstitutions(
                                                                                           featureType );

                if ( substitutions.length > 1 ) {
                    // if feature type has more than one concrete substitution, determine concrete
                    // feature type first
                    LOG.logDebug( "FeatureType '"
                                  + featureType.getName()
                                  + "' has more than one concrete substitution. Need to determine feature type "
                                  + "table first." );
                    TableRelation[] tableRelations = pt.getTableRelations();
                    if ( tableRelations.length == 2 ) {
                        StatementBuffer query = buildFeatureTypeSelect( tableRelations[0],
                                                                        tableRelations[1],
                                                                        resultValues, resultPosMap );
                        LOG.logDebug( "Feature type (and id) query: '" + query + "'" );
                        if ( query != null ) {
                            stmt = this.datastore.prepareStatement( this.conn, query );
                            rs = stmt.executeQuery();
                            while ( rs.next() ) {
                                String featureTypeName = rs.getString( 1 );
                                MappedFeatureType concreteFeatureType = (MappedFeatureType) featureType.getGMLSchema().getFeatureType(
                                                                                                                                       featureTypeName );
                                if ( concreteFeatureType == null ) {
                                    String msg = "Lookup of concrete feature type '"
                                                 + featureTypeName + "' failed: "
                                                 + " Inconsistent featuretype column!?";
                                    LOG.logError( msg );
                                    throw new DatastoreException( msg );
                                }
                                FeatureId fid = extractFeatureId( concreteFeatureType, rs, 2 );
                                LOG.logDebug( "Subfeature '" + fid.getAsString()
                                              + "' has concrete feature type '"
                                              + concreteFeatureType.getName() + "'." );

                                if ( !this.featuresInGeneration.contains( fid ) ) {
                                    PropertyPath[] subPropertyPaths = PropertyPathResolver.determineSubPropertyPaths(
                                                                                                                      concreteFeatureType,
                                                                                                                      propertyPaths );
                                    Feature feature = fetchFeature( concreteFeatureType,
                                                                    subPropertyPaths, fid );
                                    if ( feature != null ) {
                                        FeatureProperty property = FeatureFactory.createFeatureProperty(
                                                                                                         propertyName,
                                                                                                         feature );
                                        result.add( property );
                                    }
                                } else {
                                    XLinkedFeatureProperty property = new XLinkedFeatureProperty(
                                                                                                  propertyName,
                                                                                                  fid.getAsString() );
                                    this.xlinkProperties.add( property );
                                    result.add( property );
                                }
                            }
                        }
                    } else {
                        String msg = "Querying of feature properties with a content type with more "
                                     + "than one concrete substitution is not implemented for "
                                     + tableRelations.length + " TableRelations.";
                        LOG.logError( msg );
                        throw new DatastoreException( msg );
                    }
                } else {
                    // feature type is the only substitutable concrete feature type
                    PropertyPath[] subPropertyPaths = PropertyPathResolver.determineSubPropertyPaths(
                                                                                                      featureType,
                                                                                                      propertyPaths );
                    Map<MappedPropertyType, Collection<PropertyPath>> requestedPropertiesMap = PropertyPathResolver.determineFetchProperties(
                                                                                                                                              featureType,
                                                                                                                                              subPropertyPaths );
                    MappedPropertyType[] requestedProperties = requestedPropertiesMap.keySet().toArray(
                                                                                                        new MappedPropertyType[requestedPropertiesMap.size()] );

                    // determine contents (fields / functions) that needs to be SELECTed from current table
                    List<List<SimpleContent>> fetchContents = determineFetchContents( featureType,
                                                                                      requestedProperties );
                    Map<SimpleContent, Integer> newResultPosMap = buildResultPosMap( fetchContents );

                    StatementBuffer query = buildSubsequentSelect( fetchContents,
                                                                   pt.getTableRelations(),
                                                                   resultValues, resultPosMap );
                    LOG.logDebug( "Subsequent query: '" + query + "'" );

                    if ( query != null ) {
                        Object[] newResultValues = new Object[fetchContents.size()];
                        stmt = this.datastore.prepareStatement( this.conn, query );
                        rs = stmt.executeQuery();
                        while ( rs.next() ) {
                            // cache result values
                            for ( int i = 0; i < newResultValues.length; i++ ) {
                                newResultValues[i] = rs.getObject( i + 1 );
                            }
                            FeatureId fid = extractFeatureId( featureType, newResultPosMap,
                                                              newResultValues );
                            FeatureProperty property = null;
                            Feature feature = null;
                            if ( !this.featuresInGeneration.contains( fid ) ) {
                                feature = extractFeature( fid, featureType, requestedPropertiesMap,
                                                          newResultPosMap, newResultValues );
                                property = FeatureFactory.createFeatureProperty( propertyName,
                                                                                 feature );
                            } else {
                                property = new XLinkedFeatureProperty( propertyName,
                                                                       fid.getAsString() );
                                this.xlinkProperties.add( (XLinkedFeatureProperty) property );
                            }
                            result.add( property );
                        }
                    }
                }
            } else {
                String msg = "Unsupported content type: '" + pt.getClass().getName()
                             + "' in QueryHandler.fetchRelatedProperties().";
                LOG.logError( msg );
                throw new IllegalArgumentException( msg );
            }
        } finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
                if ( stmt != null ) {
                    stmt.close();
                }
            } finally {
                if ( stmt != null ) {
                    stmt.close();
                }
            }
        }
        return result;
    }

    protected void appendQualifiedContentList( StatementBuffer query, String tableAlias,
                                              List<List<SimpleContent>> fetchContents ) {

        for ( int i = 0; i < fetchContents.size(); i++ ) {
            SimpleContent content = fetchContents.get( i ).get( 0 );
            if ( content instanceof MappingField ) {
                appendQualifiedColumn( query, tableAlias, ( (MappingField) content ).getField() );
            } else if ( content instanceof SQLFunctionCall ) {
                this.vcProvider.appendSQLFunctionCall( query, tableAlias, (SQLFunctionCall) content );
            } else {
                assert false;
            }
            if ( i != fetchContents.size() - 1 ) {
                query.append( "," );
            }
        }
    }

    /**
     * Builds a lookup map that allows to find the index (position in the {@link ResultSet}) by
     * the {@link SimpleContent} instance that makes it necessary to fetch it.
     * 
     * @param fetchContents
     * @return key: SimpleContent, value: Integer (position in ResultSet)
     */
    protected Map<SimpleContent, Integer> buildResultPosMap( List<List<SimpleContent>> fetchContents ) {

        Map<SimpleContent, Integer> resultPosMap = new HashMap<SimpleContent, Integer>();
        for ( int i = 0; i < fetchContents.size(); i++ ) {
            for ( SimpleContent content : fetchContents.get( i ) ) {
                resultPosMap.put( content, i );
            }
        }
        return resultPosMap;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureFetcher.java,v $
 Revision 1.6  2006/11/29 16:59:54  mschneider
 Improved handling of native coordinate transformation.

 Revision 1.5  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.4  2006/11/09 17:40:11  mschneider
 Added hack for native coordinate system transformations. Needs review.

 Revision 1.3  2006/11/02 14:29:11  mschneider
 Javadoc / formatting fixes.

 Revision 1.2  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.1  2006/09/19 14:54:02  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 ********************************************************************** */