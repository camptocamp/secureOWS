//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/idgenerator/ParentIDGenerator.java,v 1.7 2006/09/26 16:43:22 mschneider Exp $
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

import java.util.Properties;

import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeatureType;

/**
 * {@link IdGenerator} that takes the {@link FeatureId} of the parent feature. 
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/09/26 16:43:22 $
 */
public class ParentIDGenerator extends IdGenerator {

    /**
     * Creates a new <code>ParentIDGenerator</code> instance.
     * 
     * @param params
     *            configuration parameters
     */    
    public ParentIDGenerator (Properties params) {
        super (params);
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
    public String getNewId( DatastoreTransaction ta ) throws IdGenerationException {
        throw new UnsupportedOperationException(
            "ParentIDGenerator cannot be used to generate primary keys (that are no feature ids)." );
    }
    
    /**
     * Returns a new id for a feature of the given type.
     * 
     * @param ft
     *            (mapped) feature type (irrelevant for this generator)
     * @param ta
     *            datastore transaction (context)
     * @return a new feature id.
     * @throws IdGenerationException 
     */
    @Override
    public FeatureId getNewId(  MappedFeatureType ft, DatastoreTransaction ta ) throws IdGenerationException {
        throw new UnsupportedOperationException(
            "ParentIDGenerator cannot be used to generate feature ids (without information on the parent feature." );    
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ParentIDGenerator.java,v $
Revision 1.7  2006/09/26 16:43:22  mschneider
Javadoc corrections + fixed warnings.

Revision 1.6  2006/08/06 20:24:42  poth
never thrown exception removed from constructor

Revision 1.5  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.4  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.2  2006/03/28 13:35:36  mschneider
Changed getNewId() so transaction context (DatastoreTransaction) and thus JDBC connection is accessible in the method.

Revision 1.1  2006/02/23 15:28:36  mschneider
Added ParentIDGenerator.

********************************************************************** */