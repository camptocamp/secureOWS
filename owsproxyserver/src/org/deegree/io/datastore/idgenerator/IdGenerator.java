//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/idgenerator/IdGenerator.java,v 1.7 2006/09/26 16:43:22 mschneider Exp $
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
package org.deegree.io.datastore.idgenerator;

import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.ResourceBundle;

import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeatureType;

/**
 * Abstract base class for generators that are used to create primary keys (especially
 * {@link FeatureId}s).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/09/26 16:43:22 $
 */
public abstract class IdGenerator {

    /** Default generator type based on UUIDs. */
    public static String TYPE_UUID = "UUID";
    
    private static final String BUNDLE_NAME = "org.deegree.io.datastore.idgenerator.idgenerator";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );
   
    protected Properties params;
    
    protected MappedFeatureType ft;

    /**
     * Creates a new <code>IdGenerator</code> instance.
     * 
     * @param params
     *            configuration parameters
     */
    protected IdGenerator (Properties params) {
        this.params = params;
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
    public abstract Object getNewId( DatastoreTransaction ta ) throws IdGenerationException;    

    /**
     * Returns a new id for a feature of the given type.
     * 
     * @param ft
     *            feature type
     * @param ta
     *            datastore transaction (context)
     * @return a new feature id.
     * @throws IdGenerationException
     */
    public abstract FeatureId getNewId( MappedFeatureType ft, DatastoreTransaction ta )
        throws IdGenerationException;    
    
    /**
     * Returns a concrete <code>IdGenerator</code> instance which is identified by the given type
     * code.
     * 
     * @param type
     *            type code
     * @param params
     *            initialization parameters for the IdGenerator
     * @return concrete IdGenerator instance
     */
    public static final IdGenerator getInstance (String type, Properties params) {
        IdGenerator generator = null;
        try {
            String className = RESOURCE_BUNDLE.getString( type );
            Class idGeneratorClass = Class.forName( className );

            // get constructor
            Class [] parameterTypes = new Class [] { Properties.class };
            Constructor constructor = idGeneratorClass.getConstructor(parameterTypes);

            // call constructor
            Object arglist[] = new Object[] { params };
            generator = (IdGenerator) constructor.newInstance(arglist);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO WTF is error message not propagated here?
            throw new RuntimeException( "Could not instantiate IdGenerator with type '"
                + type + "': " + e.getMessage() );
        }
        return generator;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IdGenerator.java,v $
Revision 1.7  2006/09/26 16:43:22  mschneider
Javadoc corrections + fixed warnings.

Revision 1.6  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.5  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.3  2006/03/28 13:35:36  mschneider
Changed getNewId() so transaction context (DatastoreTransaction) and thus JDBC connection is accessible in the method.

Revision 1.2  2006/02/22 00:21:44  mschneider
Renamed AbstractDatastore to Datastore.

Revision 1.1  2006/02/17 14:33:06  mschneider
Former GMLIdGenerator.

Revision 1.2  2006/02/04 20:06:57  mschneider
Changed return type for getNewId () to FeatureId.

Revision 1.1  2006/02/03 14:40:13  mschneider
Initial version.

********************************************************************** */