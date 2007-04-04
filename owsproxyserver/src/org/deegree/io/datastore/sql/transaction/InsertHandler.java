//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/transaction/Attic/InsertHandler.java,v 1.36 2006/10/05 14:17:56 poth Exp $
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.TransactionException;
import org.deegree.io.datastore.idgenerator.FeatureIdAssigner;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.io.datastore.idgenerator.ParentIDGenerator;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.AbstractRequestHandler;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.datastore.sql.transaction.InsertRow.InsertField;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeaturePropertyType;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.SimplePropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;

/**
 * Handler for {@link Insert} operations (usually contained in {@link Transaction}
 * requests).
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.36 $, $Date: 2006/10/05 14:17:56 $
 */
public class InsertHandler extends AbstractRequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( InsertHandler.class );

    // features that are currently being processed
    private Map<FeatureId, FeatureRow> featuresInInsertion = new HashMap<FeatureId, FeatureRow>();

    // contains only property rows and join table rows (but no feature rows)
    private List<InsertRow> insertRows = new ArrayList<InsertRow>();

    private SQLTransaction dsTa;

    /**
     * Creates a new <code>InsertHandler</code> from the given parameters.
     *
     * @param dsTa
     * @param aliasGenerator
     * @param conn
     */
    public InsertHandler( SQLTransaction dsTa, TableAliasGenerator aliasGenerator, Connection conn ) {
        super( dsTa.getDatastore(), aliasGenerator, conn );
        this.dsTa = dsTa;
    }

    /**
     * Inserts the given feature instance into the datastore.
     *
     * @param features
     *            (which have a MappedFeatureType as feature type)
     * @return feature ids of inserted (root) feature instances
     * @throws DatastoreException
     *             if the insert could not be performed
     */
    public List<FeatureId> performInsert( List<Feature> features )
                            throws DatastoreException {

        List<FeatureId> fids = new ArrayList<FeatureId>();

        for ( int i = 0; i < features.size(); i++ ) {
            Feature feature = features.get( i );

            MappedFeatureType ft = (MappedFeatureType) feature.getFeatureType();
            if ( feature.getId().startsWith( FeatureIdAssigner.EXIST_PREFIX ) ) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_EXISTS", feature.getName(),
                                              feature.getId().substring( 1 ) );
                throw new TransactionException( msg );
            }
            LOG.logDebug( "Inserting root feature '" + feature.getId() + "'..." );
            insertFeature( feature );
            FeatureId fid = new FeatureId( ft.getGMLId(), feature.getId() );
            fids.add( fid );

        }

        // merge inserts rows that are identical (except their pks)
        this.insertRows = mergeInsertRows( this.insertRows );

        // add featureRows to insertRows
        Iterator<FeatureRow> iter = this.featuresInInsertion.values().iterator();
        while ( iter.hasNext() ) {
            this.insertRows.add( iter.next() );
        }

        // check for cyclic fk constraints
        Collection<InsertRow> cycle = InsertRow.findCycle( this.insertRows );
        if ( cycle != null ) {
            Iterator<InsertRow> cycleIter = cycle.iterator();
            StringBuffer sb = new StringBuffer();
            while ( cycleIter.hasNext() ) {
                sb.append( cycleIter.next() );
                if ( cycle.iterator().hasNext() ) {
                    sb.append( " -> " );
                }
            }
            String msg = Messages.getMessage( "DATASTORE_FK_CYCLE", sb.toString() );
            throw new TransactionException( msg );
        }

        // sort the insert rows topologically
        List<InsertRow> sortedInserts = InsertRow.sortInsertRows( this.insertRows );

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            Iterator<InsertRow> iter2 = sortedInserts.iterator();
            LOG.logDebug( sortedInserts.size() + " rows to be inserted: " );
            while ( iter2.hasNext() ) {
                LOG.logDebug( iter2.next().toString() );
            }
        }

        executeInserts( sortedInserts );
        return fids;
    }

    /**
     * Builds the <code>InsertRows</code> that are necessary to insert the given feature instance
     * (including all properties + subfeatures).
     *
     * @param feature
     * @return InsertRows that are necessary to insert the given feature instance
     * @throws TransactionException
     */
    private FeatureRow insertFeature( Feature feature )
                            throws TransactionException {

        MappedFeatureType ft = (MappedFeatureType) feature.getFeatureType();
        if ( !ft.isInsertable() ) {
            String msg = Messages.getMessage( "DATASTORE_FT_NOT_INSERTABLE", ft.getName() );
            throw new TransactionException( msg );
        }

        LOG.logDebug( "Creating InsertRow for feature with type '" + ft.getName() + "' and id: '"
                      + feature.getId() + "'." );

        // extract feature id column value
        MappingField[] fidFields = ft.getGMLId().getIdFields();
        if ( fidFields.length > 1 ) {
            throw new TransactionException( "Insertion of features with compound feature "
                                            + "ids is not supported." );
        }
        FeatureId fid = null;
        try {
            fid = new FeatureId( ft.getGMLId(), feature.getId() );
        } catch ( IdGenerationException e ) {
            throw new TransactionException( e.getMessage(), e );
        }

        // check if the feature id is already being inserted (happens for cyclic features)
        FeatureRow insertRow = this.featuresInInsertion.get( fid );
        if ( insertRow != null ) {
            return insertRow;
        }

        insertRow = new FeatureRow( ft.getTable() );
        this.featuresInInsertion.put( fid, insertRow );

        // add column value for fid (primary key)
        String fidColumn = fidFields[0].getField();
        insertRow.setColumn( fidColumn, fid.getValue( 0 ),
                             ft.getGMLId().getIdFields()[0].getType(), true );

        // process properties
        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            FeatureProperty property = properties[i];
            MappedPropertyType propertyType = (MappedPropertyType) ft.getProperty( property.getName() );
            if ( propertyType == null ) {
                String msg = Messages.getMessage( "DATASTORE_PROPERTY_TYPE_NOT_KNOWN", property.getName() );
                LOG.logDebug( msg );
                throw new TransactionException( msg );
            }
            insertProperty( ft, property, propertyType, insertRow );
        }
        return insertRow;
    }

    /**
     * Builds the <code>InsertRow</code>s that are necessary to insert the given property instance
     * (including all it's subfeatures).
     *
     * @param featureType
     *            parent feature type (that the property belongs to)
     * @param property
     *            property instance to be inserted
     * @param propertyType
     *            property type of the property
     * @param featureRow
     *            table row of the parent feature instance
     * @throws TransactionException
     */
    private void insertProperty( MappedFeatureType featureType, FeatureProperty property,
                                MappedPropertyType propertyType, InsertRow featureRow )
                            throws TransactionException {

        if ( propertyType instanceof SimplePropertyType ) {
            LOG.logDebug( "- Simple property '" + propertyType.getName() + "', value='"
                          + getPropertyValue( property ) + "'." );
            insertProperty( (MappedSimplePropertyType) propertyType, property, featureRow );
        } else if ( propertyType instanceof GeometryPropertyType ) {
            LOG.logDebug( "- Geometry property: '" + propertyType.getName() + "'" );
            insertProperty( (MappedGeometryPropertyType) propertyType, property, featureRow );
        } else if ( propertyType instanceof FeaturePropertyType ) {
            LOG.logDebug( "- Feature property: '" + propertyType.getName() + "'" );
            insertProperty( (MappedFeaturePropertyType) propertyType, property, featureRow,
                            featureType.getGMLSchema() );
        } else {
            throw new TransactionException( "Unhandled property type '"
                                            + propertyType.getClass().getName() + "'." );
        }
    }

    /**
     * Inserts the given simple property (stored in feature table or in related table).
     *
     * @param pt
     * @param property
     * @param featureRow
     * @throws TransactionException
     */
    private void insertProperty( MappedSimplePropertyType pt, FeatureProperty property,
                                InsertRow featureRow )
                            throws TransactionException {

        SimpleContent content = pt.getContent();
        if ( content.isUpdateable() ) {
            if (content instanceof MappingField) {
                MappingField mf = (MappingField) content;
                String propertyColumn = mf.getField();
                Object propertyValue = property.getValue();
                int propertyType = mf.getType();
                TableRelation[] relations = pt.getTableRelations();
                insertProperty( propertyColumn, propertyValue, propertyType, relations, featureRow );                
            }
        }
    }

    /**
     * Inserts the given geometry property (stored in feature table or in related table).
     *
     * @param pt
     * @param property
     * @param featureRow
     * @throws TransactionException
     */
    private void insertProperty( MappedGeometryPropertyType pt, FeatureProperty property,
                                InsertRow featureRow )
                            throws TransactionException {

        String propertyColumn = pt.getMappingField().getField();
        MappingGeometryField dbField = pt.getMappingField();
        Geometry deegreeGeometry = (Geometry) property.getValue();
        Object dbGeometry;

        try {
            dbGeometry = this.datastore.convertDeegreeToDBGeometry( deegreeGeometry,
                                                                    dbField.getSRS(), this.conn );
        } catch ( DatastoreException e ) {
            throw new TransactionException( e.getMessage(), e );
        }

        int propertyType = pt.getMappingField().getType();

        // TODO remove this Oracle hack
        if ( this.datastore.getClass().getName().contains( "OracleDatastore" ) ) {
            propertyType = Types.STRUCT;
        }

        TableRelation[] relations = pt.getTableRelations();
        insertProperty( propertyColumn, dbGeometry, propertyType, relations, featureRow );
    }

    /**
     * Inserts the given simple / geometry property (stored in feature table or in related table).
     *
     * @param propertyColumn
     * @param propertyValue
     * @param propertyType
     * @param featureRow
     * @throws TransactionException
     */
    private void insertProperty( String propertyColumn, Object propertyValue, int propertyType,
                                TableRelation[] relations, InsertRow featureRow )
                            throws TransactionException {

        if ( relations == null || relations.length == 0 ) {
            // property is stored in feature table
            featureRow.setColumn( propertyColumn, propertyValue, propertyType, false );
        } else {
            // property is stored in related table
            if ( relations.length > 1 ) {
                throw new TransactionException( Messages.getMessage( "DATASTORE_SIMPLE_PROPERTY_JOIN" ) );
            }

            if ( !relations[0].isFromFK() ) {
                // fk is in property table
                MappingField[] pkFields = relations[0].getFromFields();
                MappingField[] fkFields = relations[0].getToFields();

                for ( int i = 0; i < pkFields.length; i++ ) {
                    InsertField pkField = featureRow.getColumn( pkFields[i].getField() );
                    if ( pkField == null ) {
                        String msg = Messages.getMessage( "DATASTORE_NO_FK_VALUE", pkField.getColumnName(),
                                                      pkFields[i].getTable() );
                        throw new TransactionException( msg );
                    }
                    int pkColumnType = pkField.getSQLType();
                    int fkColumnType = fkFields[i].getType();
                    if ( pkColumnType != fkColumnType ) {
                        String fkType = "" + fkColumnType;
                        String pkType = "" + pkColumnType;
                        try {
                            fkType = Types.getTypeNameForSQLTypeCode( fkColumnType );
                            pkType = Types.getTypeNameForSQLTypeCode( pkColumnType );
                        } catch ( UnknownTypeException e ) {
                            LOG.logError( e.getMessage(), e );
                        }
                        Object[] params = new Object[] { relations[0].getToTable(),
                                                        fkFields[i].getField(), fkType,
                                                        featureRow.getTable(),
                                                        pkFields[i].getField(), pkType };
                        String msg = Messages.getMessage( "DATASTORE_FK_PK_TYPE_MISMATCH", params );
                        throw new TransactionException( msg );
                    }
                    InsertRow insertRow = new InsertRow( relations[0].getToTable() );
                    insertRow.linkColumn( fkFields[i].getField(), pkField );
                    insertRow.setColumn( propertyColumn, propertyValue, propertyType, false );
                    this.insertRows.add( insertRow );
                }
            } else {
                // fk is in feature table
                MappingField[] pkFields = relations[0].getToFields();
                MappingField[] fkFields = relations[0].getFromFields();

                // generate necessary primary key value
                InsertField pkField = null;
                try {
                    Object pk = null;
                    // TODO remove hack!!!
                    if ( relations[0].getIdGenerator() instanceof ParentIDGenerator ) {
                        InsertField field = featureRow.getColumn( "ID" );
                        if ( field == null ) {
                            throw new TransactionException( "No value for ID available!" );
                        }
                        pk = field.getValue();
                    } else {
                        pk = relations[0].getNewPK( this.dsTa );
                    }
                    InsertRow insertRow = findOrCreateRow( relations[0].getToTable(),
                                                           pkFields[0].getField(), pk );
                    pkField = insertRow.setColumn( pkFields[0].getField(), pk,
                                                   pkFields[0].getType(), true );
                    insertRow.setColumn( propertyColumn, propertyValue, propertyType, false );
                } catch ( IdGenerationException e ) {
                    throw new TransactionException( e.getMessage(), e );
                }
                featureRow.linkColumn( fkFields[0].getField(), pkField );
            }
        }
    }

    /**
     * Inserts the given feature property.
     *
     * @param pt
     * @param property
     * @param featureRow
     * @throws TransactionException
     */
    private void insertProperty( MappedFeaturePropertyType pt, FeatureProperty property,
                                InsertRow featureRow, MappedGMLSchema schema )
                            throws TransactionException {

        // find (concrete) subfeature type for the given property instance
        MappedFeatureType propertyFeatureType = pt.getFeatureTypeReference().getFeatureType();
        FeatureType[] substitutions = schema.getSubstitutions( propertyFeatureType );
        Feature subFeature = (Feature) property.getValue();
        MappedFeatureType subFeatureType = null;
        for ( int i = 0; i < substitutions.length; i++ ) {
            if ( substitutions[i].getName().equals( subFeature.getName() ) ) {
                subFeatureType = (MappedFeatureType) substitutions[i];
                break;
            }
        }
        if ( subFeatureType == null ) {
            String msg = Messages.getMessage( "DATASTORE_FEATURE_NOT_SUBSTITUTABLE",
                                          propertyFeatureType.getName(), subFeature.getName() );
            throw new TransactionException( msg );
        }
        boolean ftIsAbstract = propertyFeatureType.isAbstract();

        TableRelation[] relations = pt.getTableRelations();
        if ( relations == null || relations.length < 1 ) {
            throw new TransactionException(
                                            "Invalid feature property definition, feature property "
                                                                    + "mappings must use at least one 'TableRelation' element." );
        }

        // workaround for links to dummy InsertRows (of already stored features)
        boolean cutLink = subFeature.getId().startsWith( FeatureIdAssigner.EXIST_PREFIX );
        InsertRow subFeatureRow = null;
        if ( cutLink ) {
            try {
                Object fidValue = FeatureId.removeFIDPrefix( subFeature.getId().substring( 1 ),
                                                             subFeatureType.getGMLId() );
                subFeatureRow = new FeatureRow( subFeatureType.getTable() );
                // add column value for fid (primary key)
                String fidColumn = subFeatureType.getGMLId().getIdFields()[0].getField();
                subFeatureRow.setColumn( fidColumn, fidValue,
                                         subFeatureType.getGMLId().getIdFields()[0].getType(), true );
            } catch ( DatastoreException e ) {
                throw new TransactionException( e );
            }
        } else {
            // insert sub feature (if it is not already stored)
            subFeatureRow = insertFeature( subFeature );
        }

        if ( relations.length == 1 ) {
            if ( relations[0].isFromFK() ) {
                // fk is in feature table
                MappingField[] pkFields = relations[0].getToFields();
                MappingField[] fkFields = relations[0].getFromFields();

                for ( int i = 0; i < pkFields.length; i++ ) {
                    InsertField pkField = subFeatureRow.getColumn( pkFields[i].getField() );
                    if ( pkField == null ) {
                        String msg = Messages.getMessage( "DATASTORE_NO_FK_VALUE", pkField.getColumnName(),
                                                      pkField.getTable() );
                        throw new TransactionException( msg );
                    }
                    int pkColumnType = pkField.getSQLType();
                    int fkColumnType = fkFields[i].getType();
                    if ( pkColumnType != fkColumnType ) {
                        String fkType = "" + fkColumnType;
                        String pkType = "" + pkColumnType;
                        try {
                            fkType = Types.getTypeNameForSQLTypeCode( fkColumnType );
                            pkType = Types.getTypeNameForSQLTypeCode( pkColumnType );
                        } catch ( UnknownTypeException e ) {
                            LOG.logError( e.getMessage(), e );
                        }
                        Object[] params = new Object[] { featureRow.getTable(),
                                                        fkFields[i].getField(), fkType,
                                                        subFeatureRow.getTable(),
                                                        pkFields[i].getField(), pkType };
                        String msg = Messages.getMessage( "DATASTORE_FK_PK_TYPE_MISMATCH", params );
                        throw new TransactionException( msg );
                    }

                    if ( !cutLink ) {
                        featureRow.linkColumn( fkFields[i].getField(), pkField );
                    } else {
                        featureRow.setColumn( fkFields[i].getField(), pkField.getValue(),
                                              pkField.getSQLType(), false );
                    }
                }

                if ( ftIsAbstract ) {
                    String typeField = FT_PREFIX + relations[0].getToTable();
                    featureRow.setColumn( typeField, subFeatureType.getName().getLocalName(),
                                          Types.VARCHAR, false );
                }
            } else {
                // fk is in subfeature table
                MappingField[] pkFields = relations[0].getFromFields();
                MappingField[] fkFields = relations[0].getToFields();

                InsertField pkField = featureRow.getColumn( pkFields[0].getField() );

                if ( pkField == null ) {
                    String msg = Messages.getMessage( "DATASTORE_NO_FK_VALUE", pkField.getColumnName(),
                                                  pkField.getTable() );
                    throw new TransactionException( msg );
                }
                int pkColumnType = pkField.getSQLType();
                int fkColumnType = fkFields[0].getType();
                if ( pkColumnType != fkColumnType ) {
                    String fkType = "" + fkColumnType;
                    String pkType = "" + pkColumnType;
                    try {
                        fkType = Types.getTypeNameForSQLTypeCode( fkColumnType );
                        pkType = Types.getTypeNameForSQLTypeCode( pkColumnType );
                    } catch ( UnknownTypeException e ) {
                        LOG.logError( e.getMessage(), e );
                    }
                    Object[] params = new Object[] { subFeatureRow.getTable(),
                                                    fkFields[0].getField(), fkType,
                                                    featureRow.getTable(), pkField.getColumnName(),
                                                    pkType };
                    String msg = Messages.getMessage( "DATASTORE_FK_PK_TYPE_MISMATCH", params );
                    throw new TransactionException( msg );
                }

                if ( !cutLink ) {
                    subFeatureRow.linkColumn( fkFields[0].getField(), pkField );
                } else {
                    subFeatureRow.setColumn( fkFields[0].getField(), pkField.getValue(),
                                             pkField.getSQLType(), false );
                }
            }
        } else if ( relations.length == 2 ) {

            // insert into join table
            String joinTable = relations[0].getToTable();
            MappingField[] leftKeyFields = relations[0].getToFields();
            MappingField[] rightKeyFields = relations[1].getFromFields();

            InsertRow jtRow = new InsertRow( joinTable );
            if ( ftIsAbstract ) {
                jtRow.setColumn( FT_COLUMN, subFeatureType.getName().getLocalName(), Types.VARCHAR,
                                 false );
            }

            if ( !relations[0].isFromFK() ) {
                // left key field in join table is fk
                MappingField[] pkFields = relations[0].getFromFields();
                InsertField pkField = featureRow.getColumn( pkFields[0].getField() );
                if ( pkField == null ) {
                    throw new TransactionException(
                                                    "Insertion of feature property using join table failed: "
                                                                            + "no value for join table key column '"
                                                                            + pkField.getColumnName()
                                                                            + "'." );
                }
                jtRow.linkColumn( leftKeyFields[0].getField(), pkField );
            } else {
                // left key field in join table is pk
                MappingField[] pkFields = relations[0].getToFields();
                // generate necessary primary key value
                InsertField pkField = null;
                try {
                    Object pk = relations[0].getNewPK( this.dsTa );
                    pkField = jtRow.setColumn( pkFields[0].getField(), pk, pkFields[0].getType(),
                                               true );
                } catch ( IdGenerationException e ) {
                    throw new TransactionException( e.getMessage(), e );
                }
                featureRow.linkColumn( relations[0].getFromFields()[0].getField(), pkField );
            }

            if ( relations[1].isFromFK() ) {
                // right key field in join table is fk
                MappingField[] pkFields = relations[1].getToFields();
                InsertField pkField = subFeatureRow.getColumn( pkFields[0].getField() );
                if ( pkField == null ) {
                    throw new TransactionException(
                                                    "Insertion of feature property using join table failed: "
                                                                            + "no value for join table key column '"
                                                                            + pkField.getColumnName()
                                                                            + "'." );
                }
                if ( !cutLink ) {
                    jtRow.linkColumn( rightKeyFields[0].getField(), pkField );
                } else {
                    jtRow.setColumn( rightKeyFields[0].getField(), pkField.getValue(),
                                     pkField.getSQLType(), false );
                }
            } else {
                // right key field in join table is pk
                MappingField[] pkFields = relations[1].getFromFields();
                // generate necessary primary key value
                InsertField pkField = null;
                try {
                    Object pk = relations[1].getNewPK( this.dsTa );
                    pkField = jtRow.setColumn( pkFields[0].getField(), pk, pkFields[0].getType(),
                                               true );
                } catch ( IdGenerationException e ) {
                    throw new TransactionException( e.getMessage(), e );
                }
                if ( !cutLink ) {
                    subFeatureRow.linkColumn( relations[1].getToFields()[0].getField(), pkField );
                }
            }
            this.insertRows.add( jtRow );
        } else {
            throw new TransactionException(
                                            "Insertion of feature properties stored in related tables "
                                                                    + "connected via more than one join table is not supported." );
        }
    }

    /**
     * Checks whether the feature that corresponds to the given FeatureRow is already stored in the
     * database.
     *
     * @param featureRow
     * @return true, if feature is already stored, false otherwise
     * @throws DatastoreException
     */
    private boolean doesFeatureExist( FeatureRow featureRow )
                            throws DatastoreException {

        boolean exists = false;

        InsertField pkField = featureRow.getPKColumn();

        StatementBuffer query = buildFeatureSelect( pkField.getColumnName(), pkField.getSQLType(),
                                                    pkField.getValue(), featureRow.getTable() );
        LOG.logDebug( "Feature existence query: '" + query + "'" );

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.datastore.prepareStatement( this.conn, query );
            rs = stmt.executeQuery();
            if ( rs.next() ) {
                exists = true;
            }
            if ( rs.next() ) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_QUERY_MORE_THAN_ONE_RESULT",
                                              query.getQueryString() );
                LOG.logError( msg );
                throw new TransactionException( msg );
            }
        } catch ( SQLException e ) {
            throw new TransactionException( e );
        } finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
            } catch ( SQLException e ) {
                throw new TransactionException( e );
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        throw new TransactionException( e );
                    }
                }
            }
        }
        return exists;
    }

    /**
     * Builds a SELECT statement that checks for the existence of a feature with the given id.
     *
     * @param fidColumn
     * @param typeCode
     * @param fidValue
     * @param table
     * @return the statement
     */
    private StatementBuffer buildFeatureSelect( String fidColumn, int typeCode, Object fidValue,
                                               String table ) {

        StatementBuffer query = new StatementBuffer();
        query.append( "SELECT * FROM " );
        query.append( table );
        query.append( " WHERE " );

        // append feature id constraints
        query.append( fidColumn );
        query.append( "=?" );
        query.addArgument( fidValue, typeCode );
        return query;
    }

    private InsertRow findOrCreateRow( String table, String pkColumn, Object value ) {
        Iterator<InsertRow> rowIter = this.insertRows.iterator();
        boolean found = false;
        InsertRow row = null;
        while ( rowIter.hasNext() ) {
            row = rowIter.next();
            if ( row.getTable().equals( table ) ) {
                InsertField field = row.getColumn( pkColumn );
                if ( value.equals( field.getValue() ) ) {
                    found = true;
                    LOG.logDebug( "Found matching row " + row );
                    break;
                }
            }
        }
        if ( !found ) {
            row = new InsertRow( table );
            this.insertRows.add( row );
        }
        return row;
    }

    private String getPropertyValue( FeatureProperty property ) {
        Object value = property.getValue();
        StringBuffer sb = new StringBuffer();
        if ( value instanceof Object[] ) {
            Object[] objects = (Object[]) value;
            for ( int i = 0; i < objects.length; i++ ) {
                sb.append( objects[i] );
            }
        } else {
            sb.append( value );
        }
        return sb.toString();
    }

    /**
     * Transforms the given <code>List</code> of <code>InsertRows</code> into SQL INSERT
     * statements and executes them using the underlying JDBC connection.
     *
     * @param inserts
     * @throws DatastoreException
     */
    private void executeInserts( List<InsertRow> inserts )
                            throws DatastoreException {

        PreparedStatement stmt = null;

        for ( InsertRow row : inserts ) {
            if ( row instanceof FeatureRow ) {
                if ( doesFeatureExist( (FeatureRow) row ) ) {
                    LOG.logDebug( "Skipping feature row. Already present in db." );
                    continue;
                }
            }
            try {
                stmt = null;
                StatementBuffer insert = createStatementBuffer( row );
                LOG.logDebug( insert.toString() );
                stmt = this.datastore.prepareStatement( this.conn, insert );
                stmt.execute();
            } catch ( SQLException e ) {
                String msg = "Error performing insert: " + e.getMessage();
                LOG.logError( msg, e );
                throw new TransactionException( msg, e );
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch ( SQLException e ) {
                        String msg = "Error closing statement: " + e.getMessage();
                        LOG.logError( msg, e );
                    }
                }
            }
        }
    }

    private StatementBuffer createStatementBuffer( InsertRow row ) {
        StatementBuffer insert = new StatementBuffer();
        insert.append( "INSERT INTO " );
        insert.append( row.table );
        insert.append( " (" );
        Iterator<InsertField> columnsIter = row.getColumns().iterator();
        while ( columnsIter.hasNext() ) {
            insert.append( columnsIter.next().getColumnName() );
            if ( columnsIter.hasNext() ) {
                insert.append( ',' );
            }
        }
        insert.append( ") VALUES(" );
        columnsIter = row.getColumns().iterator();
        while ( columnsIter.hasNext() ) {
            InsertField field = columnsIter.next();
            insert.append( '?' );
            insert.addArgument( field.getValue(), field.getSQLType() );
            if ( columnsIter.hasNext() ) {
                insert.append( ',' );
            }
        }
        insert.append( ")" );
        return insert;
    }

    /**
     * Merges the given <code>InsertRow</code>s by eliminating rows that have identical content
     * (except for their primary keys).
     * <p>
     * This only applies to non-FeatureRows: there are never two FeatureRows that may be treated as
     * identical, because unique feature ids have been assigned to them before.
     *
     * @see FeatureIdAssigner
     *
     * @param insertRows
     * @return merged List of InsertRows
     */
    private List<InsertRow> mergeInsertRows( List<InsertRow> insertRows ) {

        List<InsertRow> result = new ArrayList<InsertRow>();

        // keys: table names, values: inserts into the table
        Map<String, Collection<InsertRow>> tableMap = new HashMap<String, Collection<InsertRow>>();

        // build table lookup map
        Iterator<InsertRow> iter = insertRows.iterator();
        while ( iter.hasNext() ) {
            InsertRow insertRow = iter.next();
            Collection<InsertRow> tableInserts = tableMap.get( insertRow.getTable() );
            if ( tableInserts == null ) {
                tableInserts = new ArrayList<InsertRow>();
                tableMap.put( insertRow.getTable(), tableInserts );
            }
            tableInserts.add( insertRow );
        }

        iter = insertRows.iterator();
        while ( iter.hasNext() ) {
            InsertRow insertRow = iter.next();
            boolean insert = true;
            if ( !( insertRow instanceof FeatureRow ) ) {
                Collection<InsertRow> tableInserts = tableMap.get( insertRow.getTable() );
                Iterator<InsertRow> candidatesIter = tableInserts.iterator();
                while ( candidatesIter.hasNext() ) {
                    InsertRow candidate = candidatesIter.next();
                    if ( insertRow != candidate ) {
                        if ( compareInsertRows( insertRow, candidate ) ) {
                            LOG.logDebug( "Removing InsertRow: " + insertRow.hashCode() + " "
                                          + insertRow + " - duplicate of: " + candidate );
                            replaceInsertRow( insertRow, candidate );
                            insert = false;
                            tableInserts.remove( insertRow );
                            break;
                        }
                    }
                }
            }
            if ( insert ) {
                result.add( insertRow );
            }
        }
        return result;
    }

    private boolean compareInsertRows( InsertRow row1, InsertRow row2 ) {
        Collection<InsertField> fields1 = row1.getColumns();
        Iterator<InsertField> iter = fields1.iterator();
        while ( iter.hasNext() ) {
            InsertField field1 = iter.next();
            if ( !field1.isPK() ) {
                InsertField field2 = row2.getColumn( field1.getColumnName() );
                Object value1 = field1.getValue();
                Object value2 = null;
                if ( field2 != null )
                    value2 = field2.getValue();
                if ( value1 == null ) {
                    if ( value2 == null ) {
                        continue;
                    }
                    return false;
                }
                if ( !value1.equals( value2 ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    private void replaceInsertRow( InsertRow oldRow, InsertRow newRow ) {

        Collection<InsertField> oldFields = oldRow.getColumns();
        for ( InsertField field : oldFields ) {
            InsertField toField = field.getReferencedField();
            if ( toField != null ) {
                LOG.logDebug( "Removing reference to field '" + toField + "'" );
                toField.removeReferencingField( field );
            }
        }

        Collection<InsertField> referencingFields = oldRow.getReferencingFields();
        for ( InsertField fromField : referencingFields ) {
            LOG.logDebug( "Replacing reference for field '" + fromField + "'" );
            InsertField field = newRow.getColumn( fromField.getReferencedField().getColumnName() );
            LOG.logDebug( "" + field );
            fromField.relinkField( field );
        }
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: InsertHandler.java,v $
 * Revision 1.36  2006/10/05 14:17:56  poth
 * debug statement added
 *
 * Revision 1.35  2006/09/26 16:45:24  mschneider
 * Refactored because of moving of TransactionException.
 *
 * Revision 1.34  2006/09/20 11:35:41  mschneider
 * Merged datastore related messages with org.deegree.18n.
 *
 * Revision 1.33  2006/09/05 14:43:24  mschneider
 * Adapted due to merging of messages.
 *
 * Revision 1.32  2006/08/29 15:53:57  mschneider
 * Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable().
 *
 * Revision 1.31  2006/08/23 16:35:20  mschneider
 * Added handling of virtual properties. Virtual properties are skipped on insert.
 *
 * Revision 1.30  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.29  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.28  2006/08/21 15:46:53  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.27  2006/07/26 18:56:50  mschneider
 * Fixed spelling in method name.
 *
 * Revision 1.26  2006/07/23 10:07:58  poth
 * *** empty log message ***
 *
 * Revision 1.25  2006/07/10 14:23:23  poth
 * footer corrected
 *
 *  Revision 1.24  2006/07/10 08:26:43  poth
 *  footer comment corrected
 *
 *  Revision 1.23  2006/05/17 18:28:22  mschneider
 *  Removed compile time dependency to  OracleDatastore.
 *
 *  Revision 1.22  2006/04/26 13:34:09  poth
 *  *** empty log message ***
 *
 *  Revision 1.21  2006/04/19 18:29:56  mschneider
 *  Fixed problem that occured when a feature contained a feature property where the fk was stored in the inner feature (fk column was erroneously set in the table of the outer feature). This only happened if the subfeature instance was already stored in the database.
 *
 *  Revision 1.20  2006/04/18 12:47:02  mschneider
 *  Adapted to cope with DatastoreException from AbstractSQLDatastore.prepareStatement().
 *
 *  Revision 1.19  2006/04/10 16:37:35  mschneider
 *  Added local message file.
 *
 *  Revision 1.18  2006/04/07 17:13:48  mschneider
 *  Added several hacks to make it work. Needs some serious love, though.
 *
 *  Revision 1.17  2006/04/06 20:25:27  poth
 *  *** empty log message ***
 *
 *  Revision 1.16  2006/04/04 17:50:59  mschneider
 *  Fixed replaceInsertRow().
 *
 *  Revision 1.15  2006/04/04 10:22:02  poth
 *  *** empty log message ***
 *
 *  Revision 1.14  2006/03/29 14:55:34  mschneider
 *  Removed System.out.printlns.
 *
 *  Revision 1.13  2006/03/28 13:39:19  mschneider
 *  Refactored to provide the DatastoreTransaction on id generation calls.
 *
 *  Revision 1.12  2006/03/15 13:50:03  poth
 *  *** empty log message ***
 *
 *  Revision 1.11  2006/03/09 13:08:36  mschneider
 *  Fixed regression - Philosopher insert example didn't work anymore.
 *
 *  Revision 1.10  2006/03/03 13:35:58  mschneider
 *  Added findOrCreateRow().
 *
 *  Revision 1.9  2006/03/02 18:03:51  poth
 *  *** empty log message ***
 *
 *  Revision 1.8  2006/02/24 13:29:11  mschneider
 *  Simplified insertFeature() as all Feature instances to be inserted must have MappedFeatureTypes now.
 *
 *  Revision 1.7  2006/02/23 15:29:44  mschneider
 *  Reactivated merging of InsertRows.
 *
 *  Revision 1.6  2006/02/23 13:05:19  mschneider
 *  Changed parameters of performInsert().
 *
 *  Revision 1.5  2006/02/22 02:33:34  mschneider
 *  Added merging of "equal" InsertRows.
 *  Revision 1.4
 * 2006/02/21 23:59:58 mschneider Former InsertHandlerNew. Works so far...
 *
 * Revision 1.2 2006/02/20 16:33:30 mschneider More work on Insert functionality.
 *
 * Revision 1.1 2006/02/17 15:01:26 mschneider Work in progress.
 *
 * Revision 1.2 2006/02/17 14:40:54 mschneider Began reimplementation to support more complex
 * schemas.
 *
 * Revision 1.1 2006/02/13 17:54:55 mschneider Initial version.
 *
 ************************************************************************************************* */
