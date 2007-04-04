//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/MappedFeatureType.java,v 1.21 2006/11/27 09:07:51 poth Exp $
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
package org.deegree.io.datastore.schema;

import org.deegree.datatypes.QualifiedName;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.DefaultFeatureType;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * Represents a {@link FeatureType} with mapping (persistence) information.
 * <p>
 * The mapping information describe how the {@link FeatureType} is mapped in the database
 * backend.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.21 $, $Date: 2006/11/27 09:07:51 $
 */
public class MappedFeatureType extends DefaultFeatureType {

    private static final long serialVersionUID = -6091409034103779707L;

    private String table;

    private MappedGMLId gmlId;

    private MappedGMLSchema schema;

    private Datastore datastore;

    private boolean isVisible;

    private boolean isUpdatable;

    private boolean isDeletable;

    private boolean isInsertable;

    /**
     * Creates a new instance of <code>MappedFeatureType</code> from the given parameters.
     * 
     * @param name
     * @param isAbstract
     * @param properties
     * @param table
     * @param gmlId
     * @param schema
     * @param isVisible
     * @param isUpdatable
     * @param isDeletable
     * @param isInsertable
     */
    MappedFeatureType( QualifiedName name, boolean isAbstract, PropertyType[] properties,
                      String table, MappedGMLId gmlId, MappedGMLSchema schema, boolean isVisible,
                      boolean isUpdatable, boolean isDeletable, boolean isInsertable ) {
        super( name, isAbstract, properties );
        this.table = table;
        this.gmlId = gmlId;
        this.schema = schema;
        this.datastore = schema.getDatastore();
        this.isVisible = isVisible;
        this.isUpdatable = isUpdatable;
        this.isDeletable = isDeletable;
        this.isInsertable = isInsertable;
    }

    /**
     * Returns the name of the (database) table where the feature type is stored.
     * 
     * @return name of the associated table
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Returns the mapping information for the "gml:Id" attribute.
     * 
     * @return mapping information for the "gml:Id" attribute
     */
    public MappedGMLId getGMLId() {
        return this.gmlId;
    }

    /**
     * Generates a new and unique feature identifier.
     * 
     * @param ta
     * @return a new and unique feature identifier.
     * @throws IdGenerationException 
     */
    public FeatureId generateFid( DatastoreTransaction ta )
                            throws IdGenerationException {
        return this.gmlId.generateFid( this, ta );
    }

    /**
     * Returns the GML Application schema that defines this feature type.
     * 
     * @return GML Application schema that defines this feature type
     */
    public MappedGMLSchema getGMLSchema() {
        return this.schema;
    }

    /**
     * Returns whether the persistent feature type is visible (e.g. queryable in the WFS).
     * 
     * @return true, if the persistent feature type is visible.
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Returns whether update operations may be performed on the persistent feature type. 
     * 
     * @return true, if update operations may be performed, false otherwise.
     */
    public boolean isUpdatable() {
        return this.isUpdatable;
    }

    /**
     * Returns whether delete operations may be performed on the persistent feature type. 
     * 
     * @return true, if delete operations may be performed, false otherwise.
     */
    public boolean isDeletable() {
        return this.isDeletable;
    }

    /**
     * Returns whether insert operations may be performed on the persistent feature type. 
     * 
     * @return true, if insert operations may be performed, false otherwise.
     */
    public boolean isInsertable() {
        return this.isInsertable;
    }

    /**
     * Performs the given <code>Query</code>.
     * <p>
     * All members of the resulting <code>FeatureCollection</code> have this
     * <code>MappedFeatureType</code>.
     * 
     * @param query
     *            Query to be performed
     * @return FeatureCollection with members that have this type
     * @throws DatastoreException
     * @throws UnknownCRSException
     */
    public FeatureCollection performQuery( Query query )
                            throws DatastoreException, UnknownCRSException {
        return this.datastore.performQuery( query, this );
    }

    /**
     * Performs the given <code>Query</code> <i>inside</i> the given transaction context.
     * <p>
     * All members of the resulting <code>FeatureCollection</code> have this
     * <code>MappedFeatureType</code>.
     * 
     * @param query
     *            Query to be performed
     * @param context
     *            transaction context (used to specify the JDBCConnection, for example)
     * @return FeatureCollection with members that have this type
     * @throws DatastoreException
     * @throws UnknownCRSException 
     */
    public FeatureCollection performQuery( Query query, DatastoreTransaction context )
                            throws DatastoreException, UnknownCRSException {
        return this.datastore.performQuery( query, this, context );
    }

    /**
     * Retrieves a transaction object for this feature type.
     * 
     * @return a transaction object for this feature type
     * @throws DatastoreException
     *             if transaction could not be acquired
     */
    public DatastoreTransaction acquireTransaction()
                            throws DatastoreException {
        return this.datastore.acquireTransaction();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MappedFeatureType.java,v $
Revision 1.21  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.20  2006/08/28 16:42:57  mschneider
Javadoc fixes.

Revision 1.19  2006/08/24 06:40:05  poth
File header corrected

Revision 1.18  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */