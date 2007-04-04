//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/idgenerator/DBMaxIdGenerator.java,v 1.2 2006/09/19 14:54:46 mschneider Exp $
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
package org.deegree.io.datastore.sql.idgenerator;

import java.sql.Connection;
import java.util.Properties;

import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.io.datastore.idgenerator.IdGenerator;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.sql.AbstractSQLDatastore;
import org.deegree.io.datastore.sql.transaction.SQLTransaction;

/**
 * Feature id generator that uses a maximum+1 integer value to create new values.
 *
 * @author <a href="mailto:cpollmann@moss.de">Christoph Pollmann</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version $Revision: 1.2 $, $Date: 2006/09/19 14:54:46 $
 */
public class DBMaxIdGenerator extends IdGenerator {

    private String tableName;

    private String columnName;

    /**
     * Creates a new <code>DBMaxIdGenerator</code> instance.
     * <p>
     * Supported configuration parameters:
     * <table>
     *   <tr><th>Name</th><th>optional?</th><th>Usage</th></tr>
     *   <tr><td>sequence</td><td>no</td><td>name of the SQL sequence to be used</td></tr>
     * </table>
     *
     * @param params
     *            configuration parameters
     * @throws IdGenerationException
     */
    public DBMaxIdGenerator( Properties params ) throws IdGenerationException {
        super( params );
        this.tableName = params.getProperty( "table" );
        this.columnName = params.getProperty( "column" );
        if ( this.tableName == null || this.columnName == null ) {
            throw new IdGenerationException( "DBMaxIdGenerator requires 'sequence' parameter." );
        }
    }

    /**
     * Returns a new primary key.
     *
     * @param ta
     *            datastore transaction (context)
     * @return a new primary key.
     * @throws IdGenerationException
     *             if the generation of the id could not be performed
     */
    @Override
    public Object getNewId( DatastoreTransaction ta )
                            throws IdGenerationException {
        if ( !( ta instanceof SQLTransaction ) ) {
            throw new IllegalArgumentException(
                                                "DBSeqIdGenerator can only be used with SQL datastores." );
        }

        Object pk;
        try {
            AbstractSQLDatastore ds = (AbstractSQLDatastore) ta.getDatastore();
            Connection conn = ( (SQLTransaction) ta ).getConnection();
            pk = ds.getMaxNextVal( conn, this.tableName, this.columnName );
        } catch ( DatastoreException e ) {
            throw new IdGenerationException( e.getMessage(), e );
        }
        return pk;
    }

    /**
     * Returns a new id for a feature of the given type.
     *
     * @param ft
     *            (mapped) feature type
     * @return a new feature id.
     * @throws IdGenerationException
     *             if the generation of the id could not be performed
     */
    @Override
    public FeatureId getNewId( MappedFeatureType ft, DatastoreTransaction ta )
                            throws IdGenerationException {

        MappedGMLId fidDefinition = ft.getGMLId();
        if ( fidDefinition.getKeySize() != 1 ) {
            throw new IdGenerationException( "Cannot generate feature ids that are mapped to "
                                             + fidDefinition.getKeySize() + " columns." );
        }

        Datastore ds = ft.getGMLSchema().getDatastore();
        if ( !( ds instanceof AbstractSQLDatastore ) ) {
            throw new IllegalArgumentException(
                                                "DBSeqIdGenerator can only be used with SQL datastores." );
        }

        Object fidNucleus;
        try {
            fidNucleus = ( (AbstractSQLDatastore) ds ).getMaxNextVal(
                                                                      ( (SQLTransaction) ta ).getConnection(),
                                                                      this.tableName,
                                                                      this.columnName );
        } catch ( DatastoreException e ) {
            throw new IdGenerationException( e.getMessage(), e );
        }

        FeatureId fid = new FeatureId( fidDefinition, new Object[] { fidNucleus } );
        return fid;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DBMaxIdGenerator.java,v $
 Revision 1.2  2006/09/19 14:54:46  mschneider
 Fixed warnings.

 Revision 1.1  2006/05/08 09:58:45  poth
 *** empty log message ***

 ********************************************************************** */