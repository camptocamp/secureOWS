//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/wherebuilder/WhereBuilder.java,v 1.44 2006/11/29 16:59:54 mschneider Exp $
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
package org.deegree.io.datastore.sql.wherebuilder;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.io.datastore.schema.MappedFeaturePropertyType;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.TableRelation;
import org.deegree.io.datastore.schema.content.ConstantContent;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.SQLFunctionCall;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.datastore.sql.VirtualContentProvider;
import org.deegree.model.filterencoding.ArithmeticExpression;
import org.deegree.model.filterencoding.ComparisonOperation;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.ExpressionDefines;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Function;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsBetweenOperation;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyIsNullOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;

/**
 * Creates SQL-WHERE clauses from OGC filter expressions (to restrict SQL statements to all stored
 * features that match a given filter).
 * <p>
 * Also handles the creation of ORDER-BY clauses.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.44 $, $Date: 2006/11/29 16:59:54 $
 */
public class WhereBuilder {

    protected static final ILogger LOG = LoggerFactory.getLogger( WhereBuilder.class );

    // database specific SRS code for unspecified SRS
    protected static final int SRS_UNDEFINED = -1;    
    
    /** Targeted feature type. */
    protected MappedFeatureType rootFeatureType;

    /** {@link Filter} for which the corresponding WHERE-clause will be generated. */
    protected Filter filter;

    protected SortProperty[] sortProperties;

    protected VirtualContentProvider vcProvider;

    protected QueryTableTree queryTableTree;

    protected List<PropertyPath> filterPropertyPaths = new ArrayList<PropertyPath>();

    protected List<PropertyPath> sortPropertyPaths = new ArrayList<PropertyPath>();

    private Hashtable<String, String> functionMap = new Hashtable<String, String>();

    /**
     * Creates a new <code>WhereBuilder</code> instance.
     * 
     * @param rootFeatureType
     * @param filter
     * @param sortProperties
     * @param aliasGenerator
     * @param vcProvider
     * @throws DatastoreException
     */
    public WhereBuilder( MappedFeatureType rootFeatureType, Filter filter,
                        SortProperty[] sortProperties, TableAliasGenerator aliasGenerator,
                        VirtualContentProvider vcProvider ) throws DatastoreException {

        this.rootFeatureType = rootFeatureType;
        this.queryTableTree = new QueryTableTree( rootFeatureType, aliasGenerator );

        // add filter properties to the QueryTableTree
        this.filter = filter;
        if ( filter != null ) {
            assert filter instanceof ComplexFilter || filter instanceof FeatureFilter;
            buildFilterPropertyNameMap();
            for ( PropertyPath property : this.filterPropertyPaths ) {
                this.queryTableTree.addFilterProperty( property );
            }
            fillFunctionNameMap();
        }

        // add sort properties to the QueryTableTree
        this.sortProperties = sortProperties;
        if ( sortProperties != null ) {
            for ( SortProperty property : sortProperties ) {
                this.sortPropertyPaths.add( property.getSortProperty() );
                this.queryTableTree.addSortProperty( property.getSortProperty() );
            }
        }

        this.vcProvider = vcProvider;

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            LOG.logDebug( "QueryTableTree:\n" + this.queryTableTree );
        }
    }

    /**
     * Returns the alias used for the root table.
     * 
     * @return the alias used for the root table
     */
    public String getRootTableAlias() {
        return this.queryTableTree.getRootNode().getAlias();
    }

    /**
     * Returns the associated <code>Filter</code> instance.
     * 
     * @return the associated <code>Filter</code> instance
     */
    public Filter getFilter() {
        return this.filter;
    }

    protected MappedGeometryPropertyType getGeometryProperty ( PropertyName propName ) {
        PropertyPath propertyPath = propName.getValue();
        PropertyNode propertyNode = this.queryTableTree.getPropertyNode( propertyPath );
        assert propertyNode != null;
        assert propertyNode instanceof GeometryPropertyNode;
        return (MappedGeometryPropertyType) propertyNode.getProperty();
    }
    
//    /**
//     * Returns the SRS of the {@link MappedGeometryPropertyType} that is identified by the given
//     * {@link PropertyPath}.
//     * 
//     * @param propertyPath
//     * @return the default SRS of the geometry property type
//     */
//    protected String getSrs( PropertyPath propertyPath ) {
//        PropertyNode propertyNode = this.queryTableTree.getPropertyNode( propertyPath );
//        assert propertyNode != null;
//        assert propertyNode instanceof GeometryPropertyNode;
//        MappedGeometryPropertyType geoProp = (MappedGeometryPropertyType) propertyNode.getProperty();
//        return geoProp.getSRS().toString();
//    }
//
//    /**
//     * Returns the internal Srs of the {@link MappedGeometryPropertyType} that is identified by the
//     * given {@link PropertyPath}.
//     * 
//     * @param propertyPath
//     * @return the default SRS of the geometry property type
//     */
//    protected int getInternalSrsCode( PropertyPath propertyPath ) {
//        PropertyNode propertyNode = this.queryTableTree.getPropertyNode( propertyPath );
//        assert propertyNode != null;
//        assert propertyNode instanceof GeometryPropertyNode;
//        MappedGeometryPropertyType geoProp = (MappedGeometryPropertyType) propertyNode.getProperty();
//        return geoProp.getMappingField().getSRS();
//    }

    protected int getPropertyNameSQLType( PropertyName propertyName ) {

        PropertyPath propertyPath = propertyName.getValue();
        PropertyNode propertyNode = this.queryTableTree.getPropertyNode( propertyPath );
        assert propertyNode != null;

        MappedPropertyType propertyType = propertyNode.getProperty();
        if ( !( propertyType instanceof MappedSimplePropertyType ) ) {
            String msg = "Error in WhereBuilder: cannot compare against properties of type '"
                         + propertyType.getClass() + "'.";
            LOG.logError( msg );
            throw new RuntimeException( msg );
        }

        SimpleContent content = ( (MappedSimplePropertyType) propertyType ).getContent();
        if ( !( content instanceof MappingField ) ) {
            String msg = "Virtual properties are currently ignored in WhereBuilder#getPropertyNameSQLType(PropertyName).";
            LOG.logError( msg );
            return Types.VARCHAR;
        }

        int targetSqlType = ( (MappingField) content ).getType();
        return targetSqlType;
    }

    protected void buildFilterPropertyNameMap()
                            throws PropertyPathResolvingException {
        if ( this.filter instanceof ComplexFilter ) {
            buildPropertyNameMapFromOperation( ( (ComplexFilter) this.filter ).getOperation() );
        } else if ( this.filter instanceof FeatureFilter ) {
            // TODO
            // throw new PropertyPathResolvingException( "FeatureFilter not implemented yet." );
        }
    }

    private void buildPropertyNameMapFromOperation( Operation operation )
                            throws PropertyPathResolvingException {
        switch ( OperationDefines.getTypeById( operation.getOperatorId() ) ) {
        case OperationDefines.TYPE_SPATIAL: {
            registerPropertyName( ( (SpatialOperation) operation ).getPropertyName() );
            break;
        }
        case OperationDefines.TYPE_COMPARISON: {
            buildPropertyNameMap( (ComparisonOperation) operation );
            break;
        }
        case OperationDefines.TYPE_LOGICAL: {
            buildPropertyNameMap( (LogicalOperation) operation );
            break;
        }
        default: {
            break;
        }
        }
    }

    private void buildPropertyNameMap( ComparisonOperation operation )
                            throws PropertyPathResolvingException {
        switch ( operation.getOperatorId() ) {
        case OperationDefines.PROPERTYISEQUALTO:
        case OperationDefines.PROPERTYISLESSTHAN:
        case OperationDefines.PROPERTYISGREATERTHAN:
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO: {
            buildPropertyNameMap( ( (PropertyIsCOMPOperation) operation ).getFirstExpression() );
            buildPropertyNameMap( ( (PropertyIsCOMPOperation) operation ).getSecondExpression() );
            break;
        }
        case OperationDefines.PROPERTYISLIKE: {
            registerPropertyName( ( (PropertyIsLikeOperation) operation ).getPropertyName() );
            break;
        }
        case OperationDefines.PROPERTYISNULL: {
            buildPropertyNameMap( ( (PropertyIsNullOperation) operation ).getPropertyName() );
            break;
        }
        case OperationDefines.PROPERTYISBETWEEN: {
            buildPropertyNameMap( ( (PropertyIsBetweenOperation) operation ).getLowerBoundary() );
            buildPropertyNameMap( ( (PropertyIsBetweenOperation) operation ).getUpperBoundary() );
            registerPropertyName( ( (PropertyIsBetweenOperation) operation ).getPropertyName() );
            break;
        }
        default: {
            break;
        }
        }
    }

    private void buildPropertyNameMap( LogicalOperation operation )
                            throws PropertyPathResolvingException {
        List operationList = operation.getArguments();
        Iterator it = operationList.iterator();
        while ( it.hasNext() ) {
            buildPropertyNameMapFromOperation( (Operation) it.next() );
        }
    }

    private void buildPropertyNameMap( Expression expression )
                            throws PropertyPathResolvingException {
        switch ( expression.getExpressionId() ) {
        case ExpressionDefines.PROPERTYNAME: {
            registerPropertyName( (PropertyName) expression );
            break;
        }
        case ExpressionDefines.ADD:
        case ExpressionDefines.SUB:
        case ExpressionDefines.MUL:
        case ExpressionDefines.DIV: {
            buildPropertyNameMap( ( (ArithmeticExpression) expression ).getFirstExpression() );
            buildPropertyNameMap( ( (ArithmeticExpression) expression ).getSecondExpression() );
            break;
        }
        case ExpressionDefines.FUNCTION: {
            // TODO: What about PropertyNames used here?
            break;
        }
        case ExpressionDefines.EXPRESSION:
        case ExpressionDefines.LITERAL: {
            break;
        }
        }
    }

    private void registerPropertyName( PropertyName propertyName ) {
        this.filterPropertyPaths.add( propertyName.getValue() );
    }

    /**
     * Appends the alias-qualified, comma separated list of tables to be joined. This includes the
     * join conditions, which are generated in ANSI-SQL left outer join style.
     * 
     * @param query
     *            the list is appended to this <code>SQLStatement</code>
     */
    public void appendJoinTableList( StatementBuffer query ) {

        FeatureTypeNode root = this.queryTableTree.getRootNode();
        query.append( root.getTable() );
        query.append( ' ' );
        query.append( root.getAlias() );
        Stack<PropertyNode> propertyNodeStack = new Stack<PropertyNode>();
        PropertyNode[] propertyNodes = root.getPropertyNodes();
        for ( int i = 0; i < propertyNodes.length; i++ ) {
            propertyNodeStack.push( propertyNodes[i] );
        }

        while ( !propertyNodeStack.isEmpty() ) {
            PropertyNode currentNode = propertyNodeStack.pop();
            String fromAlias = currentNode.getParent().getAlias();
            TableRelation[] tableRelations = currentNode.getPathFromParent();
            if ( tableRelations != null && tableRelations.length != 0 ) {
                String[] toAliases = currentNode.getTableAliases();
                appendOuterJoins( tableRelations, fromAlias, toAliases, query );

            }
            if ( currentNode instanceof FeaturePropertyNode ) {
                FeaturePropertyNode featurePropertyNode = (FeaturePropertyNode) currentNode;
                FeatureTypeNode[] childNodes = ( (FeaturePropertyNode) currentNode ).getFeatureTypeNodes();
                for ( int i = 0; i < childNodes.length; i++ ) {
                    String toTable = childNodes[i].getTable();
                    String toAlias = childNodes[i].getAlias();
                    String[] pathAliases = featurePropertyNode.getTableAliases();
                    if ( pathAliases.length == 0 ) {
                        fromAlias = featurePropertyNode.getParent().getAlias();
                    } else {
                        fromAlias = pathAliases[pathAliases.length - 1];
                    }
                    MappedFeaturePropertyType content = (MappedFeaturePropertyType) featurePropertyNode.getProperty();
                    TableRelation[] relations = content.getTableRelations();
                    TableRelation relation = relations[relations.length - 1];
                    appendOuterJoin( relation, fromAlias, toAlias, toTable, query );
                    propertyNodes = childNodes[i].getPropertyNodes();
                    for ( int j = 0; j < propertyNodes.length; j++ ) {
                        propertyNodeStack.push( propertyNodes[j] );
                    }
                }
            }
        }
    }

    private void appendOuterJoins( TableRelation[] tableRelation, String fromAlias,
                                  String[] toAliases, StatementBuffer query ) {
        for ( int i = 0; i < toAliases.length; i++ ) {
            String toAlias = toAliases[i];
            appendOuterJoin( tableRelation[i], fromAlias, toAlias, query );
            fromAlias = toAlias;
        }
    }

    private void appendOuterJoin( TableRelation tableRelation, String fromAlias, String toAlias,
                                 StatementBuffer query ) {

        query.append( " LEFT OUTER JOIN " );
        query.append( tableRelation.getToTable() );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );

        MappingField[] fromFields = tableRelation.getFromFields();
        MappingField[] toFields = tableRelation.getToFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            if ( toAlias.equals( "" ) ) {
                toAlias = tableRelation.getToTable();
            }
            query.append( toAlias );
            query.append( "." );
            query.append( toFields[i].getField() );
            query.append( "=" );
            if ( fromAlias.equals( "" ) ) {
                fromAlias = tableRelation.getFromTable();
            }
            query.append( fromAlias );
            query.append( "." );
            query.append( fromFields[i].getField() );
            if ( i != fromFields.length - 1 ) {
                query.append( " AND " );
            }
        }
    }

    private void appendOuterJoin( TableRelation tableRelation, String fromAlias, String toAlias,
                                 String toTable, StatementBuffer query ) {

        query.append( " LEFT OUTER JOIN " );
        query.append( toTable );
        query.append( " " );
        query.append( toAlias );
        query.append( " ON " );

        MappingField[] fromFields = tableRelation.getFromFields();
        MappingField[] toFields = tableRelation.getToFields();
        for ( int i = 0; i < fromFields.length; i++ ) {
            if ( toAlias.equals( "" ) ) {
                toAlias = toTable;
            }
            query.append( toAlias );
            query.append( "." );
            query.append( toFields[i].getField() );
            query.append( "=" );
            if ( fromAlias.equals( "" ) ) {
                fromAlias = tableRelation.getFromTable();
            }
            query.append( fromAlias );
            query.append( "." );
            query.append( fromFields[i].getField() );
            if ( i != fromFields.length - 1 ) {
                query.append( " AND " );
            }
        }
    }

    /**
     * Appends an SQL WHERE-condition corresponding to the <code>Filter</code> to the given SQL
     * statement.
     * 
     * @param query
     * @throws DatastoreException 
     */
    public final void appendWhereCondition( StatementBuffer query )
                            throws DatastoreException {
        if ( filter instanceof ComplexFilter ) {
            query.append( " WHERE " );
            appendComplexFilterAsSQL( query, (ComplexFilter) filter );
        } else if ( filter instanceof FeatureFilter ) {
            FeatureFilter featureFilter = (FeatureFilter) filter;
            if ( featureFilter.getFeatureIds().size() > 0 ) {
                query.append( " WHERE " );
                appendFeatureFilterAsSQL( query, featureFilter );
            }
        } else {
            assert false : "Unexpected filter type: " + filter.getClass();
        }
    }

    /**
     * Appends an SQL "ORDER BY"-condition that corresponds to the sort properties of the query to
     * the given SQL statement.
     * 
     * @param query
     * @throws DatastoreException
     */
    public void appendOrderByCondition( StatementBuffer query )
                            throws DatastoreException {

        // ignore properties that are unsuitable as sort criteria (like constant properties)
        List<SortProperty> sortProps = new ArrayList<SortProperty>();

        if ( this.sortProperties != null && this.sortProperties.length != 0 ) {
            for ( int i = 0; i < this.sortProperties.length; i++ ) {
                SortProperty sortProperty = this.sortProperties[i];
                PropertyPath path = sortProperty.getSortProperty();
                PropertyNode propertyNode = this.queryTableTree.getPropertyNode( path );
                MappedPropertyType pt = propertyNode.getProperty();
                if ( !( pt instanceof MappedSimplePropertyType ) ) {
                    String msg = Messages.getMessage( "DATASTORE_INVALID_SORT_PROPERTY",
                                                      pt.getName() );
                    throw new DatastoreException( msg );
                }
                SimpleContent content = ( (MappedSimplePropertyType) pt ).getContent();
                if ( content.isSortable() ) {
                    sortProps.add( sortProperty );
                } else {
                    String msg = "Ignoring sort criterion - property '" + path.getAsString()
                                 + "' is not suitable for sorting.";
                    LOG.logDebug( msg );
                }
            }
        }

        if ( sortProps.size() > 0 ) {
            query.append( " ORDER BY " );
        }

        for ( int i = 0; i < sortProps.size(); i++ ) {
            SortProperty sortProperty = sortProps.get( i );
            PropertyPath path = sortProperty.getSortProperty();
            appendPropertyPathAsSQL( query, path );
            if ( !sortProperty.getSortOrder() ) {
                query.append( " DESC" );
            }
            if ( i != sortProps.size() - 1 ) {
                query.append( ',' );
            }
        }
    }

    /**
     * Appends an SQL fragment for the given object.
     * 
     * @param query
     * @param filter
     * @throws DatastoreException 
     */
    protected void appendComplexFilterAsSQL( StatementBuffer query, ComplexFilter filter )
                            throws DatastoreException {
        appendOperationAsSQL( query, filter.getOperation() );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     * @throws DatastoreException 
     */
    protected void appendOperationAsSQL( StatementBuffer query, Operation operation )
                            throws DatastoreException {

        switch ( OperationDefines.getTypeById( operation.getOperatorId() ) ) {
        case OperationDefines.TYPE_SPATIAL: {
            appendSpatialOperationAsSQL( query, (SpatialOperation) operation );
            break;
        }
        case OperationDefines.TYPE_COMPARISON: {
            appendComparisonOperationAsSQL( query, (ComparisonOperation) operation );
            break;
        }
        case OperationDefines.TYPE_LOGICAL: {
            appendLogicalOperationAsSQL( query, (LogicalOperation) operation );
            break;
        }
        default: {
            break;
        }
        }
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     */
    protected void appendComparisonOperationAsSQL( StatementBuffer query,
                                                  ComparisonOperation operation ) {
        switch ( operation.getOperatorId() ) {
        case OperationDefines.PROPERTYISEQUALTO:
        case OperationDefines.PROPERTYISLESSTHAN:
        case OperationDefines.PROPERTYISGREATERTHAN:
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO: {
            appendPropertyIsCOMPOperationAsSQL( query, (PropertyIsCOMPOperation) operation );
            break;
        }
        case OperationDefines.PROPERTYISLIKE: {
            appendPropertyIsLikeOperationAsSQL( query, (PropertyIsLikeOperation) operation );
            break;
        }
        case OperationDefines.PROPERTYISNULL: {
            appendPropertyIsNullOperationAsSQL( query, (PropertyIsNullOperation) operation );
            break;
        }
        case OperationDefines.PROPERTYISBETWEEN: {
            appendPropertyIsBetweenOperationAsSQL( query, (PropertyIsBetweenOperation) operation );
            break;
        }
        }
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     */
    protected void appendPropertyIsCOMPOperationAsSQL( StatementBuffer query,
                                                      PropertyIsCOMPOperation operation ) {
        Expression firstExpr = operation.getFirstExpression();
        if ( !( firstExpr instanceof PropertyName ) ) {
            throw new IllegalArgumentException( "First expression in a comparison must "
                                                + "always be a 'PropertyName' element." );
        }
        int targetSqlType = getPropertyNameSQLType( (PropertyName) firstExpr );
        if ( operation.isMatchCase() ) {
            appendExpressionAsSQL( query, firstExpr, targetSqlType );
        } else {
            List<Expression> list = new ArrayList<Expression>();
            list.add( firstExpr );
            Function func = new Function( getFunctionName( "LOWER" ), list );
            appendFunctionAsSQL( query, func, targetSqlType );
        }
        switch ( operation.getOperatorId() ) {
        case OperationDefines.PROPERTYISEQUALTO: {
            query.append( " = " );
            break;
        }
        case OperationDefines.PROPERTYISLESSTHAN: {
            query.append( " < " );
            break;
        }
        case OperationDefines.PROPERTYISGREATERTHAN: {
            query.append( " > " );
            break;
        }
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO: {
            query.append( " <= " );
            break;
        }
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO: {
            query.append( " >= " );
            break;
        }
        }
        if ( operation.isMatchCase() ) {
            appendExpressionAsSQL( query, operation.getSecondExpression(), targetSqlType );
        } else {
            List<Expression> list = new ArrayList<Expression>();
            list.add( operation.getSecondExpression() );
            Function func = new Function( getFunctionName( "LOWER" ), list );
            appendFunctionAsSQL( query, func, targetSqlType );
        }
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement. Replacing and escape
     * handling is based on a finite automaton with 2 states:
     * <p>
     * (escapeMode)
     * <ul>
     * <li>' is appended as \', \ is appended as \\</li>
     * <li>every character (including the escapeChar) is simply appended</li>
     * <li>- unset escapeMode</li>
     * (escapeMode is false)
     * </ul>
     * <ul>
     * <li>' is appended as \', \ is appended as \\</li>
     * <li>escapeChar means: skip char, set escapeMode</li>
     * <li>wildCard means: append %</li>
     * <li>singleChar means: append _</li>
     * </ul>
     * </p>
     * 
     * TODO This method needs extensive testing with different DBMS and combinations of escapeChar,
     * wildCard and singleChar - especially problematic seems escapeChar = \
     * 
     * NOTE: Currently, the method uses a quirk and appends the generated argument inline, i.e. not
     * using query.addArgument(). This is because of a problem that occurred for example in
     * Postgresql; the execution of the inline version is *much* faster (at least with version 8.0).
     * 
     * @param query
     * @param operation
     */
    protected void appendPropertyIsLikeOperationAsSQL( StatementBuffer query,
                                                      PropertyIsLikeOperation operation ) {

        String literal = operation.getLiteral().getValue();
        char escapeChar = operation.getEscapeChar();
        char wildCard = operation.getWildCard();
        char singleChar = operation.getSingleChar();
        boolean escapeMode = false;
        int length = literal.length();
        int targetSqlType = getPropertyNameSQLType( operation.getPropertyName() );
        if ( operation.isMatchCase() ) {
            appendPropertyNameAsSQL( query, operation.getPropertyName() );
        } else {
            List<Expression> list = new ArrayList<Expression>();
            list.add( operation.getPropertyName() );
            Function func = new Function( getFunctionName( "LOWER" ), list );
            appendFunctionAsSQL( query, func, targetSqlType );
        }
        query.append( " LIKE '" );
        StringBuffer parameter = new StringBuffer();
        for ( int i = 0; i < length; i++ ) {
            char c = literal.charAt( i );
            if ( escapeMode ) {
                if ( c == '\'' ) {
                    // ' must be converted to escapeChar + '
                    parameter.append( escapeChar );
                    parameter.append( '\'' );
                } else if ( c == '%' ) {
                    // % must be converted to escapeChar + %
                    parameter.append( escapeChar );
                    parameter.append( '%' );
                } else if ( c == '_' ) {
                    // _ must be converted to escapeChar + _
                    parameter.append( escapeChar );
                    parameter.append( '_' );
                } else if ( c == '\\' ) {
                    // \ must be converted to escapeChar + \
                    parameter.append( escapeChar );
                    parameter.append( '\\' );
                } else if ( c == escapeChar ) {
                    // escapeChar must be converted to escapeChar + escapeChar
                    parameter.append( escapeChar );
                    parameter.append( escapeChar );
                } else {
                    parameter.append( c );
                }
                escapeMode = false;
            } else {
                // escapeChar means: switch to escapeMode
                if ( c == escapeChar ) {
                    escapeMode = true;
                } else if ( c == wildCard ) {
                    // wildCard must be converted to %                    
                    parameter.append( '%' );
                    // singleChar must be converted to ?
                } else if ( c == singleChar ) {
                    parameter.append( '_' );
                } else if ( c == '\'' ) {
                    // ' must be converted to escapeChar + '                    
                    parameter.append( escapeChar );
                    parameter.append( '\'' );
                } else if ( c == '%' ) {
                    // % must be converted to escapeChar + %                    
                    parameter.append( escapeChar );
                    parameter.append( '%' );
                } else if ( c == '_' ) {
                    // _ must be converted to escapeChar + _
                    parameter.append( escapeChar );
                    parameter.append( '_' );
                } else if ( c == '\\' ) {
                    // \ must (even in escapeMode) be converted to escapeChar + \                    
                    parameter.append( escapeChar );
                    parameter.append( '\\' );
                } else {
                    parameter.append( c );
                }
            }
        }
        if ( operation.isMatchCase() ) {
            query.append( parameter );
        } else {
            query.append( parameter.toString().toLowerCase() );
        }
        query.append( "\' ESCAPE '" );
        if ( escapeChar == '\\' || escapeChar == '\'' ) {
            query.append( '\\' );
        }
        query.append( escapeChar );
        query.append( "'" );
        // query.addArgument( parameter.toString() );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     */
    protected void appendPropertyIsNullOperationAsSQL( StatementBuffer query,
                                                      PropertyIsNullOperation operation ) {
        appendPropertyNameAsSQL( query, operation.getPropertyName() );
        query.append( " IS NULL" );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     */
    protected void appendPropertyIsBetweenOperationAsSQL( StatementBuffer query,
                                                         PropertyIsBetweenOperation operation ) {

        PropertyName propertyName = operation.getPropertyName();
        int targetSqlType = getPropertyNameSQLType( propertyName );
        appendExpressionAsSQL( query, operation.getLowerBoundary(), targetSqlType );
        query.append( " <= " );
        appendPropertyNameAsSQL( query, propertyName );
        query.append( " AND " );
        appendPropertyNameAsSQL( query, propertyName );
        query.append( " <= " );
        appendExpressionAsSQL( query, operation.getUpperBoundary(), targetSqlType );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param expression
     * @param targetSqlType
     *            sql type code to be used for literals at the bottom of the expression tree
     */
    protected void appendExpressionAsSQL( StatementBuffer query, Expression expression,
                                         int targetSqlType ) {
        switch ( expression.getExpressionId() ) {
        case ExpressionDefines.PROPERTYNAME: {
            appendPropertyNameAsSQL( query, (PropertyName) expression );
            break;
        }
        case ExpressionDefines.LITERAL: {
            appendLiteralAsSQL( query, (Literal) expression, targetSqlType );
            break;
        }
        case ExpressionDefines.FUNCTION: {
            Function function = (Function) expression;
            appendFunctionAsSQL( query, function, targetSqlType );
            break;
        }
        case ExpressionDefines.ADD:
        case ExpressionDefines.SUB:
        case ExpressionDefines.MUL:
        case ExpressionDefines.DIV: {
            appendArithmeticExpressionAsSQL( query, (ArithmeticExpression) expression,
                                             targetSqlType );
            break;
        }
        case ExpressionDefines.EXPRESSION:
        default: {
            throw new IllegalArgumentException( "Unexpected expression type: "
                                                + expression.getExpressionName() );
        }
        }
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param literal
     * @param targetSqlType
     */
    protected void appendLiteralAsSQL( StatementBuffer query, Literal literal, int targetSqlType ) {
        query.append( '?' );
        query.addArgument( literal.getValue(), targetSqlType );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param propertyName
     */
    protected void appendPropertyNameAsSQL( StatementBuffer query, PropertyName propertyName ) {

        PropertyPath propertyPath = propertyName.getValue();
        appendPropertyPathAsSQL( query, propertyPath );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param propertyPath
     */
    protected void appendPropertyPathAsSQL( StatementBuffer query, PropertyPath propertyPath ) {

        LOG.logDebug( "Looking up '" + propertyPath + "' in the query table tree." );
        MappingField mappingField = null;
        PropertyNode propertyNode = this.queryTableTree.getPropertyNode( propertyPath );
        assert ( propertyNode != null );
        if ( propertyNode instanceof SimplePropertyNode ) {
            SimpleContent content = ( (MappedSimplePropertyType) ( propertyNode.getProperty() ) ).getContent();
            if ( !( content instanceof MappingField ) ) {
                if ( content instanceof ConstantContent ) {
                    query.append( "'" + ( (ConstantContent) content ).getValue() + "'" );
                    return;
                } else if ( content instanceof SQLFunctionCall ) {
                    SQLFunctionCall call = (SQLFunctionCall) content;
                    String tableAlias = null;
                    String[] tableAliases = propertyNode.getTableAliases();
                    if ( tableAliases == null || tableAliases.length == 0 ) {
                        tableAlias = propertyNode.getParent().getAlias();
                    } else {
                        tableAlias = tableAliases[tableAliases.length - 1];
                    }
                    this.vcProvider.appendSQLFunctionCall( query, tableAlias, call );
                    return;
                }
                String msg = "Virtual properties are currently ignored in WhereBuilder#appendPropertyPathAsSQL(StatementBuffer,PropertyPath).";
                LOG.logError( msg );
                assert false;
            }
            mappingField = (MappingField) content;
        } else if ( propertyNode instanceof GeometryPropertyNode ) {
            mappingField = ( (MappedGeometryPropertyType) propertyNode.getProperty() ).getMappingField();
        } else {
            String msg = "Internal error in WhereBuilder: unhandled PropertyNode type: '"
                         + propertyNode.getClass().getName() + "'.";
            LOG.logError( msg );
            throw new RuntimeException( msg );
        }
        String tableAlias = null;
        String[] tableAliases = propertyNode.getTableAliases();
        if ( tableAliases == null || tableAliases.length == 0 ) {
            tableAlias = propertyNode.getParent().getAlias();
        } else {
            tableAlias = tableAliases[tableAliases.length - 1];
        }
        if ( tableAlias != "" ) {
            query.append( tableAlias );
            query.append( '.' );
        } else {
            query.append( mappingField.getTable() );
            query.append( '.' );
        }
        query.append( mappingField.getField() );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param expression
     * @param targetSqlType
     */
    protected void appendArithmeticExpressionAsSQL( StatementBuffer query,
                                                   ArithmeticExpression expression,
                                                   int targetSqlType ) {
        query.append( '(' );
        appendExpressionAsSQL( query, expression.getFirstExpression(), targetSqlType );
        switch ( expression.getExpressionId() ) {
        case ExpressionDefines.ADD: {
            query.append( '+' );
            break;
        }
        case ExpressionDefines.SUB: {
            query.append( '-' );
            break;
        }
        case ExpressionDefines.MUL: {
            query.append( '*' );
            break;
        }
        case ExpressionDefines.DIV: {
            query.append( '/' );
            break;
        }
        }
        appendExpressionAsSQL( query, expression.getSecondExpression(), targetSqlType );
        query.append( ')' );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param function
     * @param targetSqlType
     */
    protected void appendFunctionAsSQL( StatementBuffer query, Function function, int targetSqlType ) {
        query.append( function.getName() );
        query.append( " (" );
        List list = function.getArguments();
        for ( int i = 0; i < list.size(); i++ ) {
            Expression expression = (Expression) list.get( i );
            appendExpressionAsSQL( query, expression, targetSqlType );
            if ( i != list.size() - 1 )
                query.append( ", " );
        }
        query.append( ")" );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * @param query
     * @param operation
     * @throws DatastoreException 
     */
    protected void appendLogicalOperationAsSQL( StatementBuffer query, LogicalOperation operation )
                            throws DatastoreException {
        List argumentList = operation.getArguments();
        switch ( operation.getOperatorId() ) {
        case OperationDefines.AND: {
            for ( int i = 0; i < argumentList.size(); i++ ) {
                Operation argument = (Operation) argumentList.get( i );
                query.append( '(' );
                appendOperationAsSQL( query, argument );
                query.append( ')' );
                if ( i != argumentList.size() - 1 )
                    query.append( " AND " );
            }
            break;
        }
        case OperationDefines.OR: {
            for ( int i = 0; i < argumentList.size(); i++ ) {
                Operation argument = (Operation) argumentList.get( i );
                query.append( '(' );
                appendOperationAsSQL( query, argument );
                query.append( ')' );
                if ( i != argumentList.size() - 1 )
                    query.append( " OR " );
            }
            break;
        }
        case OperationDefines.NOT: {
            Operation argument = (Operation) argumentList.get( 0 );
            query.append( "NOT (" );
            appendOperationAsSQL( query, argument );
            query.append( ')' );
            break;
        }
        }
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement.
     * 
     * TODO Handle compound primary keys correctly.
     * 
     * @param query
     * @param filter
     */
    protected void appendFeatureFilterAsSQL( StatementBuffer query, FeatureFilter filter ) {

        // List list = filter.getFeatureIds();
        // Iterator it = list.iterator();
        // while (it.hasNext()) {
        // FeatureId fid = (FeatureId) it.next();
        // MappingField mapping = null;
        // DatastoreMapping mapping = featureType.getFidDefinition().getFidFields()[0];
        // query.append( ' ' );
        // query.append( this.joinTableTree.getAlias() );
        // query.append( "." );
        // query.append( mapping.getField() );
        // query.append( "=?" );
        // query.addArgument( fid.getValue() );
        // if ( it.hasNext() ) {
        // query.append( " OR" );
        // }
        // }
        ArrayList list = filter.getFeatureIds();
        MappingField mapping = rootFeatureType.getGMLId().getIdFields()[0];
        query.append( ' ' );
        String tbl = getRootTableAlias();
        if ( null != tbl && 0 < tbl.length() ) {
            query.append( tbl );
            query.append( "." );
        }
        query.append( mapping.getField() );
        try {
            for ( int i = 0; i < list.size(); i++ ) {
                if ( 0 == i )
                    query.append( " IN (?" );
                else
                    query.append( ",?" );
                String fid = ( (org.deegree.model.filterencoding.FeatureId) list.get( i ) ).getValue();
                Object fidValue = org.deegree.io.datastore.FeatureId.removeFIDPrefix(
                                                                                      fid,
                                                                                      rootFeatureType.getGMLId() );
                query.addArgument( fidValue, mapping.getType() );
            }
        } catch ( Exception e ) {
            LOG.logError( "Error converting feature id", e );
        }
        query.append( ")" );
    }

    /**
     * Appends an SQL fragment for the given object to the given sql statement. As this depends on
     * the handling of geometry data by the concrete database in use, this method must be
     * overwritten by any datastore implementation that has spatial capabilities.
     * 
     * @param query
     * @param operation
     * @throws DatastoreException
     */
    protected void appendSpatialOperationAsSQL( @SuppressWarnings("unused")
    StatementBuffer query, @SuppressWarnings("unused")
    SpatialOperation operation )
                            throws DatastoreException {
        String msg = "Spatial operations are not supported by the WhereBuilder implementation in use: '"
                     + getClass() + "'";
        throw new DatastoreException( msg );
    }

    /**
     * Prepares the function map for functions with implementation specific names, e.g. upper case
     * conversion in ORACLE = UPPER(string); POSTGRES = UPPER(string), and MS Access =
     * UCase(string). Default SQL-function name map function 'UPPER' is 'UPPER'. If this function
     * shall be used with user databases e.g. SQLServer a specialized WhereBuilder must override
     * this method.
     */
    protected void fillFunctionNameMap() {
        functionMap.clear();
        functionMap.put( "LOWER", "LOWER" );
    }

    /**
     * Get the function with the specified name.
     * 
     * @param name
     *            the function name
     * @return the mapped function name
     */
    protected String getFunctionName( String name ) {
        String f = functionMap.get( name );
        if ( null == f )
            f = name;
        return f;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: WhereBuilder.java,v $
 Revision 1.44  2006/11/29 16:59:54  mschneider
 Improved handling of native coordinate transformation.

 Revision 1.43  2006/11/15 18:38:18  mschneider
 Changed signatures to allow the correct chaining of DatastoreExceptions.

 Revision 1.42  2006/11/09 23:17:52  mschneider
 PropertyIsLike-Fix: escapeChar in escapeMode (must be escapeChar + escapeChar).

 Revision 1.41  2006/11/09 22:54:18  mschneider
 escapeChar was not used for escaping everywhere.

 Revision 1.40  2006/11/09 22:40:04  mschneider
 Fixed some problems with PropertyIsLike.

 Revision 1.39  2006/11/09 17:44:54  mschneider
 Removed #getInternalSRS(SpatialOperation). Added #getSrs(PropertyPath) + #getInternalSrsCode(PropertyPath).

 Revision 1.38  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.37  2006/09/19 14:56:29  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 Revision 1.36  2006/09/13 18:24:12  mschneider
 Adapted (not fully) to complexer FunctionParam hierarchy.

 Revision 1.35  2006/09/11 15:06:28  mschneider
 Usage of SQLFunctionCall-mapped properties in filters is possible now.

 Revision 1.34  2006/09/05 14:44:54  mschneider
 Adapted to changes in QueryTableTree.

 Revision 1.33  2006/09/04 14:16:46  mschneider
 Adapted due to changes in QueryTableTree.

 Revision 1.32  2006/09/04 13:57:01  mschneider
 Added debug output.

 Revision 1.31  2006/09/04 13:30:14  mschneider
 Removed System.out.println ().

 Revision 1.30  2006/08/29 15:54:35  mschneider
 Added usage of SimpleContent#isSortable().

 Revision 1.29  2006/08/28 16:46:52  mschneider
 Fixed NullPointerException in ArrayList creation.

 Revision 1.28  2006/08/28 16:40:10  mschneider
 Fixed warnings. Added quirk that allows virtual (constant) properties to be used in filter conditions.

 Revision 1.27  2006/08/23 16:36:59  mschneider
 Added handling of virtual properties. More work is needed for virtual properties used in filter conditions and as sort criteria.

 Revision 1.26  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.25  2006/08/21 16:42:36  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.24  2006/08/21 15:47:18  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.23  2006/08/15 17:42:00  mschneider
 Implemented #appendOrderByCondition( StatementBuffer ). Cleanup.

 Revision 1.22  2006/08/14 16:49:59  mschneider
 Changed to respect (optional) SortProperties.

 Revision 1.21  2006/07/25 12:10:12  mschneider
 Javadoc corrections.

 Revision 1.20  2006/07/23 10:08:21  poth
 support for feature filter added

 Revision 1.19  2006/06/25 08:00:14  poth
 bug fix - using uppercase characters for case insensitive searches caused problems with '�' so now lower case characters are used

 Revision 1.18  2006/06/01 12:20:11  mschneider
 Fixed footer.

 ********************************************************************** */