//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/generic/GenericSQLWhereBuilder.java,v 1.21 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.io.datastore.sql.generic;

import java.sql.Types;
import java.util.List;

import org.deegree.i18n.Messages;
import org.deegree.io.JDBCConnection;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.sql.StatementBuffer;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.datastore.sql.VirtualContentProvider;
import org.deegree.io.datastore.sql.wherebuilder.WhereBuilder;
import org.deegree.io.quadtree.DBQuadtree;
import org.deegree.io.quadtree.DBQuadtreeManager;
import org.deegree.io.quadtree.IndexException;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.SortProperty;

/**
 * {@link WhereBuilder} implementation for the {@link GenericSQLDatastore}.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.21 $, $Date: 2006/11/27 09:07:52 $
 */
class GenericSQLWhereBuilder extends WhereBuilder {

    private final static String SQL_TRUE = "1=1";

    private final static String SQL_FALSE = "1!=1";

    private JDBCConnection jdbc;

    /**
     * Creates a new instance of <code>GenericSQLWhereBuilder</code> from the given parameters.
     * 
     * @param ft
     *           requested feature type
     * @param filter
     *           filter that restricts the matched features
     * @param sortProperties
     *           sort criteria for the result, may be null or empty
     * @param aliasGenerator
     *           used to generate unique table aliases
     * @param vcProvider
     * @param jdbc
     * @throws DatastoreException
     */
    public GenericSQLWhereBuilder( MappedFeatureType ft, Filter filter,
                                  SortProperty[] sortProperties,
                                  TableAliasGenerator aliasGenerator,
                                  VirtualContentProvider vcProvider, JDBCConnection jdbc )
                            throws DatastoreException {
        super( ft, filter, sortProperties, aliasGenerator, vcProvider );
        this.jdbc = jdbc;
    }

    /**
     * Generates an SQL-fragment for the given object.
     * 
     * TODO: Implement BBOX faster using explicit B0X-constructor
     * 
     * @throws DatastoreException
     */
    @Override
    protected void appendSpatialOperationAsSQL( StatementBuffer query, SpatialOperation operation )
                            throws DatastoreException {

        switch ( operation.getOperatorId() ) {
        case OperationDefines.BBOX: {
            appendBBOXOperationAsSQL( query, operation );
            break;
        }
        case OperationDefines.DISJOINT:
        case OperationDefines.CROSSES:
        case OperationDefines.EQUALS:
        case OperationDefines.WITHIN:
        case OperationDefines.OVERLAPS:
        case OperationDefines.TOUCHES:
        case OperationDefines.CONTAINS:
        case OperationDefines.INTERSECTS:
        case OperationDefines.DWITHIN:
        case OperationDefines.BEYOND: {
            query.append( SQL_TRUE );
            break;
        }
        default: {
            String msg = Messages.getMessage(
                                              "DATASTORE_UNKNOWN_SPATIAL_OPERATOR",
                                              OperationDefines.getNameById( operation.getOperatorId() ) );
            throw new DatastoreException( msg );
        }
        }
    }

    /**
     * Appends a constraint (FEATURE_ID IN (...)) to the given {@link StatementBuffer} which
     * is generated by using the associated {@link DBQuadtree} index. 
     * 
     * @param query
     * @param operation
     * @throws DatastoreException
     */
    private void appendBBOXOperationAsSQL( StatementBuffer query, SpatialOperation operation )
                            throws DatastoreException {

        Envelope env = operation.getGeometry().getEnvelope();

        try {
            DBQuadtreeManager qtm = new DBQuadtreeManager( jdbc, this.rootFeatureType.getTable(),
                                                       "geometry", null );
            Envelope qtEnv = qtm.getQuadtree().getRootBoundingBox();
            if ( qtEnv.intersects( env ) ) {
                // check if features within this bbox are available
                // if not -> return an empty list
                List ids = qtm.getQuadtree().query( env );
                if ( ids.size() > 0 ) {
                    int dataType = Types.VARCHAR;
                    if ( ids.get( 0 ) instanceof Integer ) {
                        dataType = Types.INTEGER;
                    }
    
                    MappingField[] idFields = this.rootFeatureType.getGMLId().getIdFields();
                    if ( idFields.length > 1 ) {
                        String msg = "GenericSQLDatastore cannot handle composite feature ids.";
                        throw new DatastoreException( msg );
                    }
    
                    query.append( getRootTableAlias() + '.' + idFields[0].getField() + " IN (" );
                    for ( int i = 0; i < ids.size() - 1; i++ ) {
                        query.append( "?," );
                        if ( dataType == Types.VARCHAR ) {
                            query.addArgument( ( (String) ids.get( i ) ).trim(), Types.VARCHAR );
                        } else {
                            query.addArgument( ids.get( i ), Types.INTEGER );
                        }
                    }
                    if ( dataType == Types.VARCHAR ) {
                        query.addArgument( ( (String) ids.get( ids.size() - 1 ) ).trim(), Types.VARCHAR );
                    } else {
                        query.addArgument( ids.get( ids.size() - 1 ), Types.INTEGER );
                    }
                    query.append( "?)" );
                } else {
                    query.append( SQL_FALSE );    
                }
            } else {
                query.append( SQL_FALSE );
            }
        } catch ( IndexException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DatastoreException( "Could not initialize Quadtree: " + e.getMessage(), e );
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GenericSQLWhereBuilder.java,v $
 Revision 1.21  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.20  2006/10/20 08:02:11  poth
 required changes resulting from generalizing qudtree implementation

 Revision 1.19  2006/10/11 15:52:00  mschneider
 Add dummy condition if no feature ids match.

 Revision 1.18  2006/10/11 15:31:16  poth
 bug fix - ensure that at least one feature_ID has been retrieved from the quadtree before construction SQL where clause from it

 Revision 1.17  2006/09/27 17:35:54  mschneider
 Outfactored string constants.

 Revision 1.16  2006/09/27 16:09:11  mschneider
 Moved BBOX evaluation to GenericSQLWhereBuilder.

 Revision 1.15  2006/09/19 14:54:12  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 Revision 1.14  2006/08/15 17:40:45  mschneider
 Javadoc fixes.

 Revision 1.13  2006/08/14 16:50:55  mschneider
 Changed to respect (optional) SortProperties.

 Revision 1.12  2006/07/22 15:15:53  poth
 performance enhancement

 Revision 1.11  2006/06/01 12:40:56  mschneider
 Added footer. Eliminated warnings.

 ********************************************************************** */