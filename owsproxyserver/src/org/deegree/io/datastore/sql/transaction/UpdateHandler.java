//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/UpdateHandler.java,v 1.25 2006/09/20 11:35:41 mschneider Exp $
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
package org.deegree.io.datastore.sql.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.idgenerator.FeatureIdAssigner;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.TableRelation.FK_INFO;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.AbstractRequestHandler;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.FeaturePropertyType;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wfs.operation.transaction.Update;

/**
 * Handler for {@link Update} operations (usually contained in {@link Transaction}
 * requests).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.25 $, $Date: 2006/09/20 11:35:41 $
 */
public class UpdateHandler extends AbstractRequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( UpdateHandler.class );

    private SQLTransaction dsTa;

    /**
     * Creates a new <code>UpdateHandler</code> from the given parameters.
     * 
     * @param dsTa
     * @param aliasGenerator
     * @param conn
     */
    public UpdateHandler( SQLTransaction dsTa, TableAliasGenerator aliasGenerator, Connection conn ) {
        super( dsTa.getDatastore(), aliasGenerator, conn );
        this.dsTa = dsTa;
    }

    /**
     * Performs an update operation against the associated datastore.
     * 
     * @param ft
     * @param properties
     * @param filter
     * @return number of updated (root) feature instances
     * @throws DatastoreException
     */
    public int performUpdate( MappedFeatureType ft, Map<PropertyPath, Object> properties,
                             Filter filter )
                            throws DatastoreException {

        List<FeatureId> fids = determineAffectedFIDs( ft, filter );

        LOG.logDebug( "Updating: " + ft );
        for ( PropertyPath property : properties.keySet() ) {
            Object propertyValue = properties.get( property );
            for ( FeatureId fid : fids ) {
                LOG.logDebug( "Updating feature: " + fid );
                performUpdate( fid, ft, property, propertyValue );
            }
        }
        return fids.size();
    }

    /**
     * Performs an update operation against the associated datastore.
     * <p>
     * The filter must match exactly one feature instance (or none) which is then replaced
     * by the specified replacement feature.
     * 
     * @param mappedFeatureType
     * @param replacementFeature
     * @param filter
     * @return number of updated (root) feature instances (0 or 1)
     * @throws DatastoreException
     */
    public int performUpdate( MappedFeatureType mappedFeatureType, Feature replacementFeature,
                             Filter filter )
                            throws DatastoreException {

        LOG.logDebug( "Updating (replace): " + mappedFeatureType );
        if ( filter != null ) {
            LOG.logDebug( " filter: " + filter.toXML() );
        }

        List<FeatureId> fids = determineAffectedFIDs( mappedFeatureType, filter );

        if ( fids.size() > 1 ) {
            String msg = Messages.getMessage( "DATASTORE_MORE_THAN_ONE_FEATURE" );
            throw new DatastoreException( msg );
        }
        DeleteHandler deleteHandler = new DeleteHandler( this.dsTa, this.aliasGenerator, this.conn );
        deleteHandler.performDelete( mappedFeatureType, filter );

        // identify stored subfeatures / assign feature ids
        FeatureIdAssigner fidAssigner = new FeatureIdAssigner( Insert.ID_GEN.GENERATE_NEW );
        fidAssigner.assignFID( replacementFeature, this.dsTa );
        // TODO remove this hack        
        fidAssigner.markStoredFeatures();

        InsertHandler insertHandler = new InsertHandler( this.dsTa, this.aliasGenerator, this.conn );
        List<Feature> features = new ArrayList<Feature>();
        features.add( replacementFeature );
        insertHandler.performInsert( features );

        return fids.size();
    }

    /**
     * Performs the update (replacing of a property) of the given feature instance.
     * <p>
     * If the selected property is a direct property of the feature, the root feature
     * is updated, otherwise the targeted subfeatures have to be determined first.
     * 
     * @param fid
     * @param ft
     * @param propertyName
     * @param replacementValue
     * @throws DatastoreException
     */
    private void performUpdate( FeatureId fid, MappedFeatureType ft, PropertyPath propertyName,
                               Object replacementValue )
                            throws DatastoreException {

        LOG.logDebug( "Updating fid: " + fid + ", propertyName: " + propertyName + " -> "
                      + replacementValue );

        int steps = propertyName.getSteps();
        QualifiedName propName = propertyName.getStep( steps - 1 ).getPropertyName();
        if ( steps > 2 ) {
            QualifiedName subFtName = propertyName.getStep( steps - 2 ).getPropertyName();
            MappedFeatureType subFt = this.datastore.getFeatureType( subFtName );
            MappedPropertyType pt = (MappedPropertyType) subFt.getProperty( propName );
            List<TableRelation> tablePath = getTablePath( ft, propertyName );
            List<FeatureId> subFids = determineAffectedFIDs( fid, subFt, tablePath );
            for ( FeatureId subFid : subFids ) {
                updateProperty( subFid, subFt, pt, replacementValue );
            }
        } else {
            MappedPropertyType pt = (MappedPropertyType) ft.getProperty( propName );
            updateProperty( fid, ft, pt, replacementValue );
        }
    }

    /**
     * Determines the subfeature instances that are targeted by the given PropertyName.
     * 
     * @param fid
     * @param subFt
     * @param propertyName
     * @return the matched feature ids
     * @throws DatastoreException
     */
    private List<FeatureId> determineAffectedFIDs( FeatureId fid, MappedFeatureType subFt,
                                                  List<TableRelation> path )
                            throws DatastoreException {

        List<FeatureId> subFids = new ArrayList<FeatureId>();

        this.aliasGenerator.reset();
        String[] tableAliases = this.aliasGenerator.generateUniqueAliases( path.size() + 1 );
        String toTableAlias = tableAliases[tableAliases.length - 1];
        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        appendFeatureIdColumns( subFt, toTableAlias, query );
        query.append( " FROM " );
        query.append( path.get( 0 ).getFromTable() );
        query.append( " " );
        query.append( tableAliases[0] );
        // append joins
        for ( int i = 0; i < path.size(); i++ ) {
            query.append( " JOIN " );
            query.append( path.get( i ).getToTable() );
            query.append( " " );
            query.append( tableAliases[i + 1] );
            query.append( " ON " );
            MappingField[] fromFields = path.get( i ).getFromFields();
            MappingField[] toFields = path.get( i ).getToFields();
            for ( int j = 0; j < fromFields.length; j++ ) {
                query.append( tableAliases[i] );
                query.append( '.' );
                query.append( fromFields[j].getField() );
                query.append( '=' );
                query.append( tableAliases[i + 1] );
                query.append( '.' );
                query.append( toFields[j].getField() );
            }
        }
        query.append( " WHERE " );
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( tableAliases[0] );
            query.append( '.' );
            query.append( fidFields[i].getField() );
            query.append( "=?" );
            query.addArgument( fid.getValue( i ), fidFields[i].getType() );
            if ( i != fidFields.length - 1 ) {
                query.append( " AND " );
            }
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            rs = stmt.executeQuery();
            subFids = extractFeatureIds( rs, subFt );
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in determineAffectedFIDs(): " + e.getMessage() );
        } finally {
            try {
                if ( rs != null ) {
                    try {
                        rs.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing result set: '" + e.getMessage() + "'.", e );
                    }
                }
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
        }
        return subFids;
    }

    /**
     * Returns the relations (the "path") that lead from the feature type's table to the
     * subfeature table which is targeted by the specified property name.
     * 
     * @param ft source feature type
     * @param path property name
     * @return relations that lead from the feature type's table to the subfeature table
     */
    private List<TableRelation> getTablePath( MappedFeatureType ft, PropertyPath path ) {
        List<TableRelation> relations = new ArrayList<TableRelation>();
        for ( int i = 1; i < path.getSteps() - 2; i += 2 ) {
            QualifiedName propName = path.getStep( i ).getPropertyName();
            MappedFeaturePropertyType pt = (MappedFeaturePropertyType) ft.getProperty( propName );
            TableRelation[] tableRelations = pt.getTableRelations();
            for ( int j = 0; j < tableRelations.length; j++ ) {
                relations.add( tableRelations[j] );
            }
            ft = pt.getFeatureTypeReference().getFeatureType();
        }
        return relations;
    }

    /**
     * Replaces the specified feature's property with the given value.
     * 
     * @param fid
     * @param ft
     * @param pt
     * @param replacementValue
     * @throws DatastoreException
     */
    private void updateProperty( FeatureId fid, MappedFeatureType ft, MappedPropertyType pt,
                                Object replacementValue )
                            throws DatastoreException {
        LOG.logDebug( "Updating property '" + pt.getName() + "' of feature '" + fid + "'." );

        if ( !ft.isUpdatable() ) {
            String msg = Messages.getMessage( "DATASTORE_FT_NOT_UPDATABLE", ft.getName() );
            throw new DatastoreException( msg );
        }
        TableRelation[] tablePath = pt.getTableRelations();
        if ( pt instanceof MappedSimplePropertyType ) {
            SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
            if ( content.isUpdateable() ) {
                if ( content instanceof MappingField ) {
                    updateProperty( fid, tablePath, (MappingField) content, replacementValue );
                }
            } else {
                LOG.logInfo( "Ignoring property '" + pt.getName() + "' in update - is virtual." );
            }
        } else if ( pt instanceof MappedGeometryPropertyType ) {
            MappingGeometryField dbField = ( (MappedGeometryPropertyType) pt ).getMappingField();
            Object dbGeometry = this.datastore.convertDeegreeToDBGeometry(
                                                                           (Geometry) replacementValue,
                                                                           dbField.getSRS(),
                                                                           this.conn );
            // TODO remove this Oracle hack
            if ( this.datastore.getClass().getName().contains( "OracleDatastore" ) ) {
                dbField = new MappingGeometryField( dbField.getTable(), dbField.getField(),
                                                    Types.STRUCT, dbField.getSRS() );
            }
            updateProperty( fid, tablePath, dbField, dbGeometry );
        } else if ( pt instanceof FeaturePropertyType ) {
            updateProperty( fid, ft, (MappedFeaturePropertyType) pt, (Feature) replacementValue );
        } else {
            throw new DatastoreException( "Internal error: Properties with type '" + pt.getClass()
                                          + "' are not handled in UpdateHandler." );
        }
    }

    /**
     * Updates a simple / geometry property of the specified feature.
     * <p>
     * Three cases have to be distinguished (which all have to be handled differently):
     * <ol>
     * <li>property value stored in feature table</li>
     * <li>property value stored in property table, fk in property table</li>
     * <li>property value stored in property table, fk in feature table</li>
     * </ol>  
     * 
     * @param fid
     * @param tablePath
     * @param dbField
     * @param replacementValue
     * @throws DatastoreException 
     */
    private void updateProperty( FeatureId fid, TableRelation[] tablePath, MappingField dbField,
                                Object replacementValue )
                            throws DatastoreException {

        if ( tablePath.length == 0 ) {
            updateProperty( fid, dbField, replacementValue );
        } else if ( tablePath.length == 1 ) {
            TableRelation relation = tablePath[0];
            if ( tablePath[0].getFKInfo() == FK_INFO.fkIsToField ) {
                Object[] keyValues = determineKeyValues( fid, relation );
                if ( keyValues != null ) {
                    deletePropertyRows( relation, keyValues );
                }
                insertPropertyRow( relation, keyValues, dbField, replacementValue );
            } else {
                Object[] oldKeyValues = determineKeyValues( fid, relation );
                Object[] newKeyValues = findOrInsertPropertyRow( relation, dbField,
                                                                 replacementValue );
                updateFeatureRow( fid, relation, newKeyValues );
                if ( oldKeyValues != null ) {
                    deleteOrphanedPropertyRows( relation, oldKeyValues );
                }
            }
        } else {
            throw new DatastoreException( "Updating of properties that are stored in "
                                          + "related tables using join tables is not "
                                          + "supported." );
        }
    }

    private void updateFeatureRow( FeatureId fid, TableRelation relation, Object[] newKeyValues )
                            throws DatastoreException {

        StatementBuffer query = new StatementBuffer();
        query.append( "UPDATE " );
        query.append( relation.getFromTable() );
        query.append( " SET " );
        MappingField[] fromFields = relation.getFromFields();
        for ( int i = 0; i < newKeyValues.length; i++ ) {
            query.append( fromFields[i].getField() );
            query.append( "=?" );
            query.addArgument( newKeyValues[i], fromFields[i].getType() );
        }
        query.append( " WHERE " );
        appendFIDWhereCondition( query, fid );

        LOG.logDebug( "Performing update: " + query.getQueryString() );

        PreparedStatement stmt = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            stmt.execute();
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in performUpdate(): " + e.getMessage() );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                }
            }
        }

    }

    /**
     * Updates a simple / geometry property of the specified feature.
     * <p>
     * This method handles the case where the property is stored in the feature table itself, so
     * a single UPDATE statement is sufficient.  
     * 
     * @param fid
     * @param dbField
     * @param replacementValue
     * @throws DatastoreException 
     */
    private void updateProperty( FeatureId fid, MappingField dbField, Object replacementValue )
                            throws DatastoreException {

        StatementBuffer query = new StatementBuffer();
        query.append( "UPDATE " );
        query.append( dbField.getTable() );
        query.append( " SET " );
        query.append( dbField.getField() );
        query.append( "=?" );
        query.addArgument( replacementValue, dbField.getType() );
        query.append( " WHERE " );
        appendFIDWhereCondition( query, fid );

        LOG.logDebug( "Performing update: " + query.getQueryString() );

        PreparedStatement stmt = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            stmt.execute();
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in performUpdate(): " + e.getMessage() );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                }
            }
        }
    }

    /**
     * Determines the values for the key columns that are referenced by the given table
     * relation (as from fields). 
     *  
     * @param fid
     * @param relation
     * @return the values for the key columns
     * @throws DatastoreException
     */
    private Object[] determineKeyValues( FeatureId fid, TableRelation relation )
                            throws DatastoreException {

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT " );
        MappingField[] fromFields = relation.getFromFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            query.append( fromFields[i].getField() );
            if ( i != fromFields.length - 1 ) {
                query.append( ',' );
            }
        }
        query.append( " FROM " );
        query.append( relation.getFromTable() );
        query.append( " WHERE " );
        appendFIDWhereCondition( query, fid );

        Object[] keyValues = new Object[fromFields.length];
        LOG.logDebug( "determineKeyValues: " + query.getQueryString() );
        PreparedStatement stmt = null;
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            ResultSet rs = stmt.executeQuery();
            if ( rs.next() ) {
                for ( int i = 0; i < keyValues.length; i++ ) {
                    Object value = rs.getObject( i + 1 );
                    if ( value != null ) {
                        keyValues[i] = value;
                    } else {
                        keyValues = null;
                        break;
                    }
                }
            } else {
                LOG.logError( "Internal error. Result is empty (no rows)." );
                throw new SQLException();
            }
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in performUpdate(): " + e.getMessage() );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                }
            }
        }
        return keyValues;
    }

    private void deletePropertyRows( TableRelation relation, Object[] keyValues )
                            throws DatastoreException {

        StatementBuffer query = new StatementBuffer();
        query.append( "DELETE FROM " );
        query.append( relation.getToTable() );
        query.append( " WHERE " );
        MappingField[] toFields = relation.getToFields();
        for ( int i = 0; i < toFields.length; i++ ) {
            query.append( toFields[i].getField() );
            query.append( "=?" );
            query.addArgument( keyValues[i], toFields[i].getType() );
            if ( i != toFields.length - 1 ) {
                query.append( " AND " );
            }
        }

        PreparedStatement stmt = null;
        LOG.logDebug( "deletePropertyRows: " + query.getQueryString() );
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            stmt.execute();
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in performUpdate(): " + e.getMessage() );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                }
            }
        }
    }

    private void insertPropertyRow( TableRelation relation, Object[] keyValues,
                                   MappingField dbField, Object replacementValue )
                            throws DatastoreException {

        if ( keyValues == null ) {
            if ( relation.getFromFields().length > 1 ) {
                throw new DatastoreException( "Key generation for compound keys is not supported." );
            }
            // generate new primary key
            keyValues = new Object[1];
            keyValues[0] = relation.getIdGenerator().getNewId( dsTa );
        }

        StatementBuffer query = new StatementBuffer();
        query.append( "INSERT INTO " );
        query.append( relation.getToTable() );
        query.append( " (" );
        MappingField[] toFields = relation.getToFields();
        for ( int i = 0; i < toFields.length; i++ ) {
            query.append( toFields[i].getField() );
            query.append( ',' );
        }
        query.append( dbField.getField() );
        query.append( ") VALUES (" );
        for ( int i = 0; i < toFields.length; i++ ) {
            query.append( '?' );
            query.addArgument( keyValues[i], toFields[i].getType() );
            query.append( ',' );
        }
        query.append( "?)" );
        query.addArgument( replacementValue, dbField.getType() );

        PreparedStatement stmt = null;
        LOG.logDebug( "insertPropertyRow: " + query.getQueryString() );
        try {
            stmt = this.datastore.prepareStatement( conn, query );
            stmt.execute();
        } catch ( SQLException e ) {
            throw new DatastoreException( "Error in performUpdate(): " + e.getMessage() );
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e ) {
                    LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                }
            }
        }
    }

    /**
     * Returns the foreign key value(s) for the row that stores the given property.
     * <p>
     * If the row already exists, the existing key is returned, otherwise a new row for the 
     * property is inserted first.
     *  
     * @param relation
     * @param dbField
     * @param replacementValue
     * @return foreign key value(s) for the row that stores the given property
     * @throws DatastoreException
     */
    private Object[] findOrInsertPropertyRow( TableRelation relation, MappingField dbField,
                                             Object replacementValue )
                            throws DatastoreException {

        Object[] keyValues = null;

        if ( dbField.getType() != Types.GEOMETRY ) {
            StatementBuffer query = new StatementBuffer();
            query.append( "SELECT " );
            MappingField[] toFields = relation.getToFields();
            for ( int i = 0; i < toFields.length; i++ ) {
                query.append( toFields[i].getField() );
                if ( i != toFields.length - 1 ) {
                    query.append( ',' );
                }
            }
            query.append( " FROM " );
            query.append( relation.getToTable() );
            query.append( " WHERE " );
            query.append( dbField.getField() );
            query.append( "=?" );
            query.addArgument( replacementValue, dbField.getType() );

            PreparedStatement stmt = null;
            LOG.logDebug( "findOrInsertPropertyRow: " + query.getQueryString() );
            try {
                stmt = this.datastore.prepareStatement( conn, query );
                ResultSet rs = stmt.executeQuery();
                if ( rs.next() ) {
                    keyValues = new Object[toFields.length];
                    for ( int i = 0; i < toFields.length; i++ ) {
                        keyValues[i] = rs.getObject( i + 1 );
                    }
                }
            } catch ( SQLException e ) {
                throw new DatastoreException( "Error in findOrInsertPropertyRow(): "
                                              + e.getMessage() );
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        LOG.logError( "Error closing statement: '" + e.getMessage() + "'.", e );
                    }
                }
            }
            if ( keyValues != null ) {
                return keyValues;
            }
        }

        if ( relation.getToFields().length > 1 ) {
            throw new DatastoreException( "Key generation for compound keys is not supported." );
        }

        // property does not yet exist (or it's a geometry)
        keyValues = new Object[1];
        // generate new PK
        keyValues[0] = relation.getNewPK( this.dsTa );
        insertPropertyRow( relation, keyValues, dbField, replacementValue );

        return keyValues;
    }

    private void deleteOrphanedPropertyRows( TableRelation relation, Object[] keyValues )
                            throws DatastoreException {
        Map<String, Object> keyColumns = new HashMap<String, Object>();
        for ( int i = 0; i < keyValues.length; i++ ) {
            keyColumns.put( relation.getToFields()[i].getField(), keyValues[i] );
        }
        DeleteNode node = new DeleteNode( relation.getToTable(), keyColumns );
        DeleteHandler deleteHandler = new DeleteHandler( this.dsTa, this.aliasGenerator, this.conn );
        if ( deleteHandler.getReferencingRows( node ).size() == 0 ) {
            try {
                deleteHandler.performDelete( node );
            } catch ( SQLException e ) {
                throw new DatastoreException( e.getMessage() );
            }
        }
    }

    private void updateProperty( @SuppressWarnings("unused")
    FeatureId fid, @SuppressWarnings("unused")
    MappedFeatureType ft, @SuppressWarnings("unused")
    MappedFeaturePropertyType pt, @SuppressWarnings("unused")
    Feature replacementFeature ) {
        throw new UnsupportedOperationException(
                                                 "Updating of feature properties is not implemented yet." );
    }

    private void appendFIDWhereCondition( StatementBuffer query, FeatureId fid ) {
        MappingField[] fidFields = fid.getFidDefinition().getIdFields();
        for ( int i = 0; i < fidFields.length; i++ ) {
            query.append( fidFields[i].getField() );
            query.append( "=?" );
            query.addArgument( fid.getValue( i ), fidFields[i].getType() );
            if ( i != fidFields.length - 1 ) {
                query.append( " AND " );
            }
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: UpdateHandler.java,v $
 Revision 1.25  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.24  2006/09/19 14:57:01  mschneider
 Fixed warnings, improved javadoc.

 Revision 1.23  2006/09/05 14:43:24  mschneider
 Adapted due to merging of messages.

 Revision 1.22  2006/08/29 15:53:57  mschneider
 Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable().

 Revision 1.21  2006/08/23 16:35:58  mschneider
 Added handling of virtual properties. Virtual properties are skipped on update.

 Revision 1.20  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.19  2006/08/21 16:42:36  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.18  2006/08/21 15:46:53  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.17  2006/08/06 20:49:30  poth
 UnsupportedOperationException instead of datastoreException thrown

 Revision 1.16  2006/07/26 18:57:29  mschneider
 Fixed spelling in method name.

 Revision 1.15  2006/06/29 10:26:20  mschneider
 Moved identifying of stored features / assigning of feature ids from TransactionHandler here.

 Revision 1.14  2006/06/28 08:53:35  poth
 *** empty log message ***

 Revision 1.13  2006/06/27 08:14:55  poth
 bug fix - just log filter if available

 Revision 1.12  2006/06/01 16:01:19  mschneider
 Added exception that is thrown in case of FeatureProperty updates.

 Revision 1.11  2006/05/30 17:04:06  mschneider
 Update on geometry properties should work now.

 Revision 1.10  2006/05/29 16:39:24  mschneider
 Update on simple properties should work now.

 Revision 1.9  2006/05/24 15:25:03  mschneider
 Update for simple / geometry properties should work now (only when they are stored in the feature table).

 Revision 1.8  2006/05/23 22:40:57  mschneider
 "Standard" update throws Exception now (to let the user know that it is not fully implemented yet).

 Revision 1.7  2006/05/23 16:06:34  mschneider
 Changed signature of performUpdate().

 Revision 1.6  2006/05/18 15:48:02  mschneider
 Added handling of non-standard update (feature replace).

 Revision 1.5  2006/05/17 18:28:49  mschneider
 Initial work on Update.

 Revision 1.4  2006/05/16 16:20:10  mschneider
 Added update method for deegree specific update operation.

 Revision 1.3  2006/04/18 12:47:40  mschneider
 Improved javadoc.

 Revision 1.2  2006/04/06 20:25:27  poth
 *** empty log message ***

 Revision 1.1  2006/02/13 17:54:55  mschneider
 Initial version.

 ********************************************************************** */
