//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sde/SDEUpdateHandler.java,v 1.8 2006/08/29 15:53:57 mschneider Exp $
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
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGeometryPropertyType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.io.datastore.sql.TableAliasGenerator;
import org.deegree.io.sdeapi.SDEAdapter;
import org.deegree.io.sdeapi.SDEConnection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.PropertyPath;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeState;
import com.esri.sde.sdk.client.SeUpdate;

/**
 * Handler for <code>Update</code> operations contained in <code>Transaction</code> requests.
 *
 * @author <a href="mailto:cpollmann@moss.de">Christoph Pollmann</a>
 *
 * @author last edited by: $Author: mschneider $
 *
 * @version 2.0, $Revision: 1.8 $
 *
 * @since 2.0
 */
public class SDEUpdateHandler extends AbstractSDERequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( SDEUpdateHandler.class );

    /**
     * Creates a new <code>UpdateHandler</code> from the given parameters.
     *
     * @param dsTa
     * @param aliasGenerator
     * @param conn
     * @throws DatastoreException
     */
    public SDEUpdateHandler( SDETransaction dsTa, TableAliasGenerator aliasGenerator,
                            SDEConnection conn ) {
        super( dsTa.getDatastore(), aliasGenerator, conn );
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

        // only for statistics???
        //FeatureId[] fids = determineAffectedFIDs( mappedFeatureType, filter );

        // process properties list
        // TODO: has to take care about properties in related tables
        ArrayList list = new ArrayList();
        for ( PropertyPath path : properties.keySet() ) {
            QualifiedName qn = path.getStep( path.getSteps() - 1 ).getPropertyName();
            MappedPropertyType pt = (MappedPropertyType) ft.getProperty( qn );
            if ( pt == null ) {
                String msg = "Internal error: unknown property type " + qn;
                LOG.logDebug( msg );
                throw new DatastoreException( msg );
            }
            if ( 0 < pt.getTableRelations().length ) {
                String msg = "Update of properties of related tables is not implemented yet " + qn;
                LOG.logDebug( msg );
                throw new DatastoreException( msg );
            }
            list.add( pt );
        }
        MappedPropertyType[] mappedPType = (MappedPropertyType[]) list.toArray( new MappedPropertyType[list.size()] );

        // prepare update
        SDEWhereBuilder whereBuilder = datastore.getWhereBuilder( ft, filter, aliasGenerator );
        Map columnsMap = buildColumnsMap( ft, mappedPType, false );
        String[] columns = (String[]) columnsMap.keySet().toArray( new String[columnsMap.size()] );
        Map mappingFieldsMap = buildMappingFieldMap( columns, columnsMap );
        StringBuffer whereCondition = new StringBuffer();
        whereBuilder.appendWhereCondition( whereCondition );

        try {
            SeUpdate updater = new SeUpdate( conn.getConnection() );
            updater.setState( conn.getState().getId(), new SeObjectId( SeState.SE_NULL_STATE_ID ),
                              SeState.SE_STATE_DIFF_NOCHECK );
            updater.toTable( ft.getTable(), columns, whereCondition.toString() );
            updater.setWriteMode( true );
            SeRow row = updater.getRowToSet();
            if ( row.hasColumns() ) {
                SeColumnDefinition[] cd = row.getColumns();
                for ( int k = 0; k < cd.length; k++ ) {
                    LOG.logDebug( "*** col[" + k + "] name=" + cd[k].getName() + " type="
                                  + cd[k].getType() + "/" + typeName( cd[k].getType() ) );
                }
            } else {
                LOG.logDebug( "*** no column definitions!!!" );
            }

            for ( PropertyPath path : properties.keySet() ) {
                QualifiedName qn = path.getStep( path.getSteps() - 1 ).getPropertyName();
                MappedPropertyType pt = (MappedPropertyType) ft.getProperty( qn );
                if ( pt instanceof MappedSimplePropertyType ) {
                    SimpleContent content = ((MappedSimplePropertyType) pt).getContent();
                    if (content instanceof MappingField) {                      
                        MappingField field = (MappingField) content;
                        Integer resultPos = (Integer) mappingFieldsMap.get( field );
                        Object value = properties.get( path );
                        SDEAdapter.setRowValue( row, resultPos.intValue(), value,
                                                SDEAdapter.mapSQL2SDE( field.getType() ) );                        
                    }
                } else if ( pt instanceof MappedGeometryPropertyType ) {
                    MappingGeometryField field = ( (MappedGeometryPropertyType) pt ).getMappingField();
                    Integer resultPos = (Integer) mappingFieldsMap.get( field );
                    SeShape value = (SeShape) datastore.convertDegreeToDBGeometry( (Geometry) properties.get( path ) );
                    row.setShape( resultPos.intValue(), value );
                }
            }

            updater.execute();
            updater.close();
        } catch ( SeException e ) {
            e.printStackTrace();
            throw new DatastoreException( "update failed", e );
        }

        // return fids.length;
        return 1; // don't know, how many rows are affected and don't want to query it only for statistics
    }

    public static String typeName( int type ) {
        switch ( type ) {
        case SeColumnDefinition.TYPE_BLOB:
            return "BLOB";
        case SeColumnDefinition.TYPE_CLOB:
            return "CLOB";
        case SeColumnDefinition.TYPE_DATE:
            return "DATE";
        case SeColumnDefinition.TYPE_FLOAT32:
            return "FLOAT32";
        case SeColumnDefinition.TYPE_FLOAT64:
            return "FLOAT64";
        case SeColumnDefinition.TYPE_INT16:
            return "INT16";
        case SeColumnDefinition.TYPE_INT32:
            return "INT32";
        case SeColumnDefinition.TYPE_INT64:
            return "INT64";
        case SeColumnDefinition.TYPE_NCLOB:
            return "NCLOB";
        case SeColumnDefinition.TYPE_NSTRING:
            return "NSTRING";
        case SeColumnDefinition.TYPE_RASTER:
            return "RASTER";
        case SeColumnDefinition.TYPE_SHAPE:
            return "SHAPE";
        case SeColumnDefinition.TYPE_STRING:
            return "STRING";
        case SeColumnDefinition.TYPE_UUID:
            return "UUID";
        case SeColumnDefinition.TYPE_XML:
            return "XML";
        default:
            return "???";
        }
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: SDEUpdateHandler.java,v $
 * Revision 1.8  2006/08/29 15:53:57  mschneider
 * Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable().
 *
 * Revision 1.7  2006/08/23 16:34:06  mschneider
 * Added handling of virtual properties. Needs testing.
 *
 * Revision 1.6  2006/08/22 18:14:42  mschneider
 * Refactored due to cleanup of org.deegree.io.datastore.schema package.
 *
 * Revision 1.5  2006/08/21 16:42:36  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.4  2006/08/21 15:44:38  mschneider
 * Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.
 *
 * Revision 1.3  2006/08/06 20:38:52  poth
 * never thrown exceptions and never read variables removed
 *
 * Revision 1.2  2006/07/10 21:07:31  mschneider
 * Removed System.out.println's.
 *
 * Revision 1.1  2006/05/21 19:06:21  poth
 * initial load up
 *
 * Revision 1.2  2006/05/12 21:17:22  polli
 * update and delete handler impledmented
 *
 * Revision 1.1  2006/05/09 14:51:52  polli
 * no message
 *
 **************************************************************************************************/

