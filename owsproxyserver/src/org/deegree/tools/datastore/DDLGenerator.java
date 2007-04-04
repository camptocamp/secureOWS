//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/datastore/DDLGenerator.java,v 1.25 2006/12/04 18:23:52 mschneider Exp $
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
package org.deegree.tools.datastore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGMLSchemaDocument;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.xml.sax.SAXException;

/**
 * Abstract base class for DDL generation from annotated GML schema files.
 * <p>
 * This abstract base class only implements the functionality needed to retrieve the necessary
 * tables and columns used in an annotated GML schema. DDL generation is dependent on the specific
 * SQL backend to be used, so this is implemented in concrete extensions of this class.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.25 $, $Date: 2006/12/04 18:23:52 $
 */
public abstract class DDLGenerator {

    protected static final String FT_PREFIX = "FT_";

    protected static final int FEATURE_TYPE_TABLE = 0;

    protected static final int JOIN_TABLE = 1;

    protected static final int MULTI_PROPERTY_TABLE = 2;

    protected MappedGMLSchema schema;

    // key type: String (table names), value type: TableDefinition
    protected Map<String, TableDefinition> tables = new HashMap<String, TableDefinition>();

    /**
     * Creates a new instance of <code>DDLGenerator</code> from the given parameters.
     * 
     * @param schemaURL
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     * @throws XMLSchemaException
     * @throws UnknownCRSException 
     */
    protected DDLGenerator( URL schemaURL ) throws MalformedURLException, IOException,
                            SAXException, XMLParsingException, XMLSchemaException, UnknownCRSException {

        System.out.println( Messages.format( "LOADING_SCHEMA_FILE", schemaURL ) );
        MappedGMLSchemaDocument schemaDoc = new MappedGMLSchemaDocument();
        schemaDoc.load( schemaURL );
        schema = schemaDoc.parseMappedGMLSchema();
        FeatureType[] featureTypes = schema.getFeatureTypes();
        int concreteCount = 0;
        for ( int i = 0; i < featureTypes.length; i++ ) {
            if ( !featureTypes[i].isAbstract() ) {
                concreteCount++;
            }
        }
        System.out.println( Messages.format( "SCHEMA_INFO", new Integer( featureTypes.length ),
                                             new Integer( featureTypes.length - concreteCount ),
                                             new Integer( concreteCount ) ) );
        System.out.println( Messages.getString( "RETRIEVING_TABLES" ) );
        buildTableMap();
    }

    /**
     * Returns all table definitions of the given type.
     * 
     * @param type
     *            FEATURE_TYPE_TABLE, JOIN_TABLE or MULTI_PROPERTY_TABLE
     * @return all table definitions of the given type.
     */
    protected TableDefinition[] getTables( int type ) {
        Collection<TableDefinition> tableList = new ArrayList<TableDefinition>();
        Iterator iter = this.tables.keySet().iterator();
        while ( iter.hasNext() ) {
            String tableName = (String) iter.next();
            TableDefinition table = this.tables.get( tableName );
            if ( table.getType() == type ) {
                tableList.add( table );
            }
        }
        return tableList.toArray( new TableDefinition[tableList.size()] );
    }

    /**
     * Returns the table definition for the table with the given name. If no such definition exists,
     * a new table definition is created and added to the internal <code>tables</code> map.
     * 
     * @param tableName
     *            table definition to look up
     * @param type
     *            type of the table (only respected, if a new TableDefinition instance is created)
     * @return the table definition for the table with the given name.
     */
    private TableDefinition lookupTableDefinition( String tableName, int type ) {
        TableDefinition table = this.tables.get( tableName );
        if ( table == null ) {
            table = new TableDefinition( tableName, type );
            this.tables.put( tableName, table );
        }
        return table;
    }

    /**
     * Collects the referenced tables and their columns from the input schema. Builds the member map
     * <code>tables</code> from this data.
     */
    private void buildTableMap() {
        FeatureType[] featureTypes = schema.getFeatureTypes();
        for ( int i = 0; i < featureTypes.length; i++ ) {
            if ( !featureTypes[i].isAbstract() ) {
                buildTableMap( (MappedFeatureType) featureTypes[i] );
            }
        }
    }

    /**
     * Collects the tables and their columns used in the annotation of the given feature type.
     * Builds the member map <code>tables</code> from this data.
     * 
     * @param featureType
     *            feature type to process
     */
    private void buildTableMap( MappedFeatureType featureType ) {
        TableDefinition table = lookupTableDefinition( featureType.getTable(), FEATURE_TYPE_TABLE );

        addGMLIdColumns( featureType.getGMLId(), table );

        PropertyType[] properties = featureType.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            MappedPropertyType property = (MappedPropertyType) properties[i];
            if ( property instanceof MappedSimplePropertyType ) {
                buildTableMap( (MappedSimplePropertyType) property, table );
            } else if ( property instanceof MappedGeometryPropertyType ) {
                buildTableMap( (MappedGeometryPropertyType) property, table );
            } else if ( property instanceof MappedFeaturePropertyType ) {
                buildTableMap( (MappedFeaturePropertyType) property, table );
            } else {
                throw new RuntimeException( Messages.format( "ERROR_UNEXPECTED_PROPERTY_TYPE",
                                                             property.getClass().getName() ) );
            }
        }
    }

    /**
     * Adds the columns used in the given <code>MappedGMLId</code> to the also given
     * <code>TableDefinition</code>.
     * 
     * @param gmlId
     *            columns are taken from this gmlId mapping
     * @param table
     *            columns are added to this table definition
     */
    private void addGMLIdColumns( MappedGMLId gmlId, TableDefinition table ) {
        MappingField[] idFields = gmlId.getIdFields();
        for ( int i = 0; i < idFields.length; i++ ) {
            ColumnDefinition column = new ColumnDefinition( idFields[i].getField(),
                                                            idFields[i].getType(), false, true );
            table.addColumn( column );
        }
    }

    /**
     * Collects the tables and their columns used in the annotation of the given simple property
     * type. Builds the <code>table</code> member map from this data.
     * <p>
     * If the data for the property is stored in a related table, the table and column information
     * used on the path to this table is also added to the <code>tables</code> member map.
     * 
     * @param simpleProperty
     *            simple property type to process
     * @param table
     *            table definition associated with the property definition
     */
    private void buildTableMap( MappedSimplePropertyType simpleProperty, TableDefinition table ) {
        Collection<ColumnDefinition> newColumns = new ArrayList<ColumnDefinition>();
        // array must always have length 1
        TableRelation[] relations = simpleProperty.getTableRelations();
        if ( simpleProperty.getMaxOccurs() != 1 && ( relations == null || relations.length < 1 ) ) {
            throw new RuntimeException( Messages.format( "ERROR_INVALID_PROPERTY_DEFINITION",
                                                         simpleProperty.getName() ) );
        }

        SimpleContent content = simpleProperty.getContent();
        if ( content instanceof MappingField ) {
            MappingField mf = (MappingField) content;
            if ( relations == null || relations.length == 0 ) {
                newColumns.add( new ColumnDefinition( mf.getField(), mf.getType(),
                                                      simpleProperty.getMinOccurs() == 0, false ) );
            } else {
                TableRelation firstRelation = relations[0];
                MappingField[] fromFields = firstRelation.getFromFields();
                for ( int i = 0; i < fromFields.length; i++ ) {
                    MappingField fromField = fromFields[i];
                    newColumns.add( new ColumnDefinition( fromField.getField(),
                                                          fromField.getType(), false, false ) );
                }
                buildTableMap( relations, mf );
            }
        } else {
            String msg = "Ignoring property '" + simpleProperty + "' - has virtual content.";
            System.out.println( msg );
        }
        table.addColumns( newColumns );
    }

    /**
     * Collects the tables and their columns used in the annotation of the given geometry property
     * type. Builds the <code>table</code> member map from this data.
     * <p>
     * If the geometry for the property is stored in a related table, the table and column
     * information used on the path to this table is also added to the <code>tables</code> member
     * map.
     * 
     * @param geometryProperty
     *            feature property type to process
     * @param table
     *            table definition associated with the property definition
     */
    private void buildTableMap( MappedGeometryPropertyType geometryProperty, TableDefinition table ) {
        Collection<ColumnDefinition> newColumns = new ArrayList<ColumnDefinition>();
        TableRelation[] relations = geometryProperty.getTableRelations();
        if ( geometryProperty.getMaxOccurs() != 1 && ( relations == null || relations.length < 1 ) ) {
            throw new RuntimeException( Messages.format( "ERROR_INVALID_PROPERTY_DEFINITION",
                                                         geometryProperty.getName() ) );
        }
        if ( relations == null || relations.length == 0 ) {
            newColumns.add( new ColumnDefinition( geometryProperty.getMappingField().getField(),
                                                  geometryProperty.getMappingField().getType(),
                                                  geometryProperty.getMinOccurs() == 0, true ) );
        } else {
            TableRelation firstRelation = relations[0];
            MappingField[] fromFields = firstRelation.getFromFields();
            for ( int i = 0; i < fromFields.length; i++ ) {
                MappingField fromField = fromFields[i];
                newColumns.add( new ColumnDefinition( fromField.getField(), fromField.getType(),
                                                      false, true ) );
            }
            buildTableMap( relations, geometryProperty.getMappingField() );
        }
        table.addColumns( newColumns );
    }

    /**
     * Collects the tables and their columns used in the annotation of the given feature property
     * type. Builds the <code>table</code> member map from this data.
     * <p>
     * The table and column information used on the path to the table of the feature type is also
     * added to the <code>tables</code> member map.
     * 
     * @param featureProperty
     *            feature property type to process
     * @param table
     *            table definition associated with the property definition
     */
    private void buildTableMap( MappedFeaturePropertyType featureProperty, TableDefinition table ) {
        Collection<ColumnDefinition> newColumns = new ArrayList<ColumnDefinition>();

        // array must always have length 1
        TableRelation[] relations = featureProperty.getTableRelations();

        // target feature type table must always be accessed via 'Relation'-elements
        if ( relations == null || relations.length < 1 ) {
            throw new RuntimeException(
                                        Messages.format(
                                                         "ERROR_INVALID_FEATURE_PROPERTY_DEFINITION_1",
                                                         featureProperty.getName() ) );
        }

        // maxOccurs > 1: target feature type table must be accessed via join table
        if ( featureProperty.getMaxOccurs() != 1 && ( relations.length < 2 ) ) {
            throw new RuntimeException(
                                        Messages.format(
                                                         "ERROR_INVALID_FEATURE_PROPERTY_DEFINITION_2",
                                                         featureProperty.getName() ) );
        }

        // add this feature type's key columns to current table
        TableRelation firstRelation = relations[0];
        MappingField[] fromFields = firstRelation.getFromFields();
        boolean isNullable = featureProperty.getMinOccurs() == 0 && relations.length == 1;
        for ( int i = 0; i < fromFields.length; i++ ) {
            MappingField fromField = fromFields[i];
            newColumns.add( new ColumnDefinition( fromField.getField(), fromField.getType(),
                                                  isNullable, false ) );
        }
        table.addColumns( newColumns );

        MappedFeatureType contentType = featureProperty.getFeatureTypeReference().getFeatureType();
        buildTableMap( relations, featureProperty, contentType );
    }

    /**
     * Collects the tables and their columns used in the relation tables from a simple/geometry
     * property to it's content table. Builds the <code>table</code> member map from this data.
     * 
     * @param relations
     *            relation tables from annotation of property type
     * @param targetField
     *            holds the properties data
     */
    private void buildTableMap( TableRelation[] relations, MappingField targetField ) {

        // process tables used in 'To'-element of each 'Relation'-element
        for ( int i = 0; i < relations.length; i++ ) {
            String tableName = relations[i].getToTable();
            TableDefinition table = lookupTableDefinition( tableName, MULTI_PROPERTY_TABLE );
            MappingField[] toFields = relations[i].getToFields();
            for ( int j = 0; j < toFields.length; j++ ) {
                ColumnDefinition column = new ColumnDefinition( toFields[j].getField(),
                                                                toFields[j].getType(), false, false );
                table.addColumn( column );
            }
        }

        // process table used in 'To'-element of last 'Relation'-element (targetField refers to
        // this)
        ColumnDefinition column = new ColumnDefinition( targetField.getField(),
                                                        targetField.getType(), false, false );
        TableDefinition table = lookupTableDefinition(
                                                       relations[relations.length - 1].getToTable(),
                                                       MULTI_PROPERTY_TABLE );
        table.addColumn( column );
    }

    /**
     * Collects the tables and their columns used in the relation tables from a feature property to
     * it's content feature type. Builds the <code>table</code> member map from this data.
     * 
     * @param relations
     *            relation tables from annotation of feature property type
     * @param property
     * @param targetType
     *            type contained in the feature property
     */
    private void buildTableMap( TableRelation[] relations, MappedPropertyType property,
                               MappedFeatureType targetType ) {

        TableDefinition table = lookupTableDefinition( relations[0].getFromTable(),
                                                       FEATURE_TYPE_TABLE );

        // process tables used in 'To'-element of each 'Relation'-element (except the last)
        for ( int i = 0; i < relations.length - 1; i++ ) {
            String tableName = relations[i].getToTable();
            table = lookupTableDefinition( tableName, JOIN_TABLE );
            MappingField[] toFields = relations[i].getToFields();
            for ( int j = 0; j < toFields.length; j++ ) {
                ColumnDefinition column = new ColumnDefinition( toFields[j].getField(),
                                                                toFields[j].getType(), false, false );
                table.addColumn( column );
            }
        }

        // process table used in 'To'-element of last 'Relation'-element
        FeatureType[] concreteTypes = targetType.getGMLSchema().getSubstitutions( targetType );
        MappingField[] toFields = relations[relations.length - 1].getToFields();

        // if it refers to several target tables (target feature type is abstract), an additional
        // column is needed (which determines the target feature type)        
        if ( concreteTypes.length > 1 ) {
            String typeColumn = "featuretype";
            if ( relations.length == 1 ) {
                typeColumn = FT_PREFIX + property.getName().getLocalName().toUpperCase();
            }
            ColumnDefinition column = new ColumnDefinition( typeColumn, Types.VARCHAR, false, false );
            table.addColumn( column );
        }
        for ( int i = 0; i < concreteTypes.length; i++ ) {
            MappedFeatureType concreteType = (MappedFeatureType) concreteTypes[i];
            String tableName = concreteType.getTable();
            table = lookupTableDefinition( tableName, FEATURE_TYPE_TABLE );
            for ( int j = 0; j < toFields.length; j++ ) {
                ColumnDefinition column = new ColumnDefinition( toFields[j].getField(),
                                                                toFields[j].getType(), false, false );
                table.addColumn( column );
            }
        }

        // process tables used in 'From'-element of each 'Relation'-element (except the first)
        for ( int i = 1; i < relations.length; i++ ) {
            String tableName = relations[i].getFromTable();
            if ( i != relations.length - 1 ) {
                table = lookupTableDefinition( tableName, JOIN_TABLE );
            } else {
                table = lookupTableDefinition( tableName, FEATURE_TYPE_TABLE );
            }
            MappingField[] fromFields = relations[i].getFromFields();
            for ( int j = 0; j < fromFields.length; j++ ) {
                ColumnDefinition column = new ColumnDefinition( fromFields[j].getField(),
                                                                fromFields[j].getType(), false,
                                                                false );
                table.addColumn( column );
            }
        }
    }

    /**
     * Generates the DDL statements that can be used to build a relational schema that backs the
     * GML schema.
     * 
     * @param outputFile
     * @throws IOException
     */
    public void generateCreateScript( String outputFile )
                            throws IOException {
        PrintWriter writer = new PrintWriter( new FileWriter( outputFile ) );
        TableDefinition[] tables = getTables( FEATURE_TYPE_TABLE );
        System.out.println( Messages.format( "CREATE_FEATURE_TYPE", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateCreateStatements( tables[i] ) );
        }
        tables = getTables( JOIN_TABLE );
        System.out.println( Messages.format( "CREATE_JOIN_TABLES", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateCreateStatements( tables[i] ) );
        }
        tables = getTables( MULTI_PROPERTY_TABLE );
        System.out.println( Messages.format( "CREATE_PROPERTY_TABLES", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateCreateStatements( tables[i] ) );
        }
        writer.close();
    }

    /**
     * Generates the DDL statements necessary for the creation of the given table definition. Must
     * be overwritten by the concrete implementation.
     * 
     * @param table
     * @return the DDL statements necessary for the creation of the given table definition
     */
    protected abstract StringBuffer generateCreateStatements( TableDefinition table );

    /**
     * Generates the DDL statements that can be used to remove the relational schema again.
     * 
     * @param outputFile
     * @throws IOException
     */
    public void generateDropScript( String outputFile )
                            throws IOException {
        PrintWriter writer = new PrintWriter( new FileWriter( outputFile ) );
        TableDefinition[] tables = getTables( FEATURE_TYPE_TABLE );
        System.out.println( Messages.format( "DROP_FEATURE_TYPE", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateDropStatements( tables[i] ) );
        }
        tables = getTables( JOIN_TABLE );
        System.out.println( Messages.format( "DROP_JOIN_TABLES", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateDropStatements( tables[i] ) );
        }
        tables = getTables( MULTI_PROPERTY_TABLE );
        System.out.println( Messages.format( "DROP_PROPERTY_TABLES", new Integer( tables.length ) ) );
        for ( int i = 0; i < tables.length; i++ ) {
            writer.println( generateDropStatements( tables[i] ) );
        }
        writer.close();
    }

    /**
     * Generates the DDL statements necessary for the removal of the given table definition. Must
     * be overwritten by the concrete implementation.
     * 
     * @param table
     * @return the DDL statements necessary for the removal of the given table definition
     */
    protected abstract StringBuffer generateDropStatements( TableDefinition table );

    /**
     * @param args
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     * @throws XMLSchemaException
     * @throws UnknownCRSException 
     */
    public static void main( String[] args )
                            throws IOException, SAXException, XMLParsingException,
                            XMLSchemaException, UnknownCRSException {

        if ( args.length != 4 ) {
            System.out.println( "Usage: DDLGenerator [FLAVOUR] <input.xsd> <create.sql> <drop.sql>" );
            System.exit( 0 );
        }

        String flavour = args[0];
        String schemaFile = args[1];
        String createFile = args[2];
        String dropFile = args[3];

        DDLGenerator generator = null;
        if ( "POSTGIS".equals( flavour ) ) {
            generator = new PostGISDDLGenerator( new File( schemaFile ).toURL() );
        } else {
            System.out.println( Messages.format( "ERROR_UNSUPPORTED_FLAVOUR", flavour ) );
        }

        generator.generateCreateScript( createFile );
        generator.generateDropScript( dropFile );
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( Messages.getString( "RELATIONAL_SCHEMA" ) );
        sb.append( '\n' );

        TableDefinition[] tables = getTables( FEATURE_TYPE_TABLE );
        sb.append( '\n' );
        sb.append( tables.length );
        sb.append( " feature type tables\n\n" );
        for ( int i = 0; i < tables.length; i++ ) {
            sb.append( tables[i] );
            sb.append( '\n' );
        }

        sb.append( '\n' );
        tables = getTables( JOIN_TABLE );
        sb.append( tables.length );
        sb.append( " join tables\n\n" );
        for ( int i = 0; i < tables.length; i++ ) {
            sb.append( tables[i] );
            sb.append( '\n' );
        }

        sb.append( '\n' );
        tables = getTables( MULTI_PROPERTY_TABLE );
        sb.append( tables.length );
        sb.append( " property tables\n\n" );
        for ( int i = 0; i < tables.length; i++ ) {
            sb.append( tables[i] );
            sb.append( '\n' );
        }
        return sb.toString();
    }

    class TableDefinition {

        private int type;

        private String tableName;

        private Map<String, ColumnDefinition> columnsMap = new LinkedHashMap<String, ColumnDefinition>();

        TableDefinition( String tableName, int type ) {
            this.type = type;
            this.tableName = tableName;
        }

        String getName() {
            return this.tableName;
        }

        int getType() {
            return this.type;
        }

        ColumnDefinition[] getColumns() {
            Collection<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
            Iterator iter = columnsMap.keySet().iterator();
            while ( iter.hasNext() ) {
                String columnName = (String) iter.next();
                columns.add( columnsMap.get( columnName ) );
            }
            return columns.toArray( new ColumnDefinition[columns.size()] );
        }

        ColumnDefinition[] getPKColumns() {
            Collection<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
            Iterator iter = columnsMap.keySet().iterator();
            while ( iter.hasNext() ) {
                String columnName = (String) iter.next();
                ColumnDefinition column = columnsMap.get( columnName );
                if ( column.isPartOfPK() ) {
                    columns.add( columnsMap.get( columnName ) );
                }
            }
            return columns.toArray( new ColumnDefinition[columns.size()] );
        }

        ColumnDefinition getColumn( String name ) {
            return columnsMap.get( name );
        }

        void addColumn( ColumnDefinition column ) {
            ColumnDefinition oldColumn = columnsMap.get( column.getName() );
            if ( oldColumn != null ) {
                if ( !( column.getType() == oldColumn.getType() ) ) {
                    String msg = Messages.format( "ERROR_COLUMN_DEFINITION_TYPES",
                                                  column.getName(),
                                                  oldColumn.isNullable() ? "NULLABLE"
                                                                        : "NOT NULLABLE",
                                                  column.isNullable() ? "NULLABLE" : "NOT NULLABLE" );
                    throw new RuntimeException( msg );

                }
                if ( oldColumn.isPartOfPK() ) {
                    column = oldColumn;
                }
            }
            columnsMap.put( column.getName(), column );
        }

        void addColumns( Collection columns ) {
            Iterator iter = columns.iterator();
            while ( iter.hasNext() ) {
                ColumnDefinition column = (ColumnDefinition) iter.next();
                addColumn( column );
            }
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append( Messages.format( "TABLE", this.tableName ) );
            sb.append( Messages.getString( "PRIMARY_KEY" ) );
            ColumnDefinition[] pkColumns = getPKColumns();
            for ( int i = 0; i < pkColumns.length; i++ ) {
                sb.append( '"' );
                sb.append( pkColumns[i].getName() );
                sb.append( '"' );
                if ( i != pkColumns.length - 1 ) {
                    sb.append( ", " );
                }
            }
            sb.append( '\n' );
            Iterator columnNameIter = this.columnsMap.keySet().iterator();
            while ( columnNameIter.hasNext() ) {
                String columnName = (String) columnNameIter.next();
                ColumnDefinition column = this.columnsMap.get( columnName );
                try {
                    sb.append( Messages.format(
                                                "COLUMN",
                                                columnName,
                                                Types.getTypeNameForSQLTypeCode( column.getType() ),
                                                new Boolean( column.isNullable() ) ) );
                } catch ( UnknownTypeException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sb.append( '\n' );
            }
            return sb.toString();
        }
    }

    class ColumnDefinition {

        private String columnName;

        private int type;

        private boolean isNullable;

        private boolean isGeometryColumn;

        private boolean isPartOfPK;

        ColumnDefinition( String columnName, int type, boolean isNullable, boolean isGeometryColumn ) {
            this.columnName = columnName;
            this.type = type;
            this.isNullable = isNullable;
            this.isGeometryColumn = isGeometryColumn;
        }

        ColumnDefinition( String columnName, int type, boolean isNullable, boolean isPartOfPK,
                         boolean isGeometryColumn ) {
            this( columnName, type, isNullable, isGeometryColumn );
            this.isPartOfPK = isPartOfPK;
        }

        String getName() {
            return this.columnName;
        }

        int getType() {
            return this.type;
        }

        boolean isNullable() {
            return this.isNullable;
        }

        boolean isGeometry() {
            return this.isGeometryColumn;
        }

        boolean isPartOfPK() {
            return this.isPartOfPK;
        }
    }
}

/***************************************************************************************************
 * $Log: DDLGenerator.java,v $
 * Revision 1.25  2006/12/04 18:23:52  mschneider
 * Changed spelling for feature type disambiguation columns (FT_XYZ) to uppercase.
 *
 * Revision 1.24  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.23  2006/11/23 15:25:03  mschneider
 * Javadoc fixed.
 *
 * Revision 1.22  2006/08/31 14:59:09  mschneider
 * Javadoc fixes.
 *
 * Revision 1.21  2006/08/24 06:43:54  poth
 * File header corrected
 *
 * Revision 1.20  2006/08/23 16:37:52  mschneider
 * Added handling of virtual properties. Needs testing.
 *
 * Revision 1.19  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.18  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.17  2006/08/21 15:49:59  mschneider
 * Changes due to removing of (unused + outdated) FeatureArrayPropertyType.
 *
 * Revision 1.16  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.15  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.14  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.13  2006/01/31 16:27:14  mschneider
 * Changes due to refactoring of org.deegree.model.feature package.
 *
 * Revision 1.12  2006/01/18 19:20:35  mschneider
 * Adapted to type code for MappingFields (instead of typeName).
 *
 * Revision 1.11  2005/12/29 10:55:58  mschneider
 * Cleanup. Moved WhereBuilder specific classes to own package.
 *
 * Revision 1.10  2005/12/22 02:16:19  mschneider
 * Changed name of featuretype columns.
 *
 * Revision 1.9  2005/12/20 14:50:27  mschneider
 * Renamed #getFeatureType() to #getFeatureTypeReference().
 *
 * Revision 1.8  2005/12/13 23:14:36  mschneider
 * Added extraction of table definitions need for feature properties.
 *
 * Revision 1.7  2005/12/12 22:46:32  mschneider
 * Cleanup, javadoc, extraction of messages to ResourceBundle.
 *
 * Revision 1.6  2005/12/12 17:10:09  mschneider
 * Moving common functionality to DDLGenerator.
 * 
 * Revision 1.5 2005/12/09 14:52:55  mschneider
 * Added creation of Drop-Statements to remove tables again. Cleaned up output.
 * 
 * Revision 1.4 2005/12/08 22:24:24  mschneider
 * Added support for feature type fields, so ambigous foreign keys can be resolved properly.
 * 
 * Revision 1.3 2005/12/08 21:45:10  mschneider
 * Style fixes.
 * 
 * Revision 1.2 2005/12/08 20:49:28  mschneider 
 * Fixed error message.
 *
 * Revision 1.1 2005/12/08 20:48:51  mschneider 
 * Initial version.
 **************************************************************************************************/