//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/Insert.java,v 1.3 2006/10/12 16:24:00 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;

/**
 * Represents an <code>Insert</code> operation as a part of a {@link Transaction} request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.3 $, $Date: 2006/10/12 16:24:00 $
 */
public class Insert extends TransactionOperation {

    /**
     * Generation strategies for feature ids.
     */
    public static enum ID_GEN {
        
        /** Use provided feature ids. */
        USE_EXISTING,
        
        /** Use provided feature ids, generate new id if feature with same id already exists. */
        REPLACE_DUPLICATE,
        
        /** Always generate new feature ids. */
        GENERATE_NEW
    }

    /** Use provided feature ids. */
    public static final String ID_GEN_USE_EXISTING_STRING = "UseExisting";

    /** Use provided feature ids, generate new id if feature with same id already exists. */
    public static final String ID_GEN_REPLACE_DUPLICATE_STRING = "ReplaceDuplicate";

    /** Always generate new feature ids. */
    public static final String ID_GEN_GENERATE_NEW_STRING = "GenerateNew";

    private ID_GEN idGenMode;

    private URI srsName;

    private FeatureCollection fc;

    /**
     * Creates a new <code>Insert</code> instance.
     * 
     * @param handle
     *            optional identifier for the operation (for error messsages)
     * @param idGenMode
     *            mode for feature id generation
     * @param srsName
     *            name of the spatial reference system
     * @param fc
     *            feature instances to be inserted, wrapped in a <code>FeatureCollection</code>
     */
    public Insert( String handle, ID_GEN idGenMode, URI srsName, FeatureCollection fc ) {
        super( handle );
        this.idGenMode = idGenMode;
        this.srsName = srsName;
        this.fc = fc;
    }

    /**
     * Returns the mode for id generation.
     * <p>
     * Must be one of the following:
     * <ul>
     * <li>ID_GEN.USE_EXISTING</li>
     * <li>ID_GEN.REPLACE_DUPLICATE</li>
     * <li>ID_GEN.GENERATE_NEW</li>
     * </ul>
     * 
     * @return the mode for id generation
     */
    public ID_GEN getIdGen() {
        return this.idGenMode;
    }

    /**
     * Returns the asserted SRS of the features to be inserted.
     * 
     * @return the asserted SRS of the features
     */
    public URI getSRSName() {
        return this.srsName;
    }

    /**
     * Returns the feature instances to be inserted.
     * 
     * @return the feature instances to be inserted
     */
    public FeatureCollection getFeatures() {
        return this.fc;
    }

    /**
     * Returns the names of the feature types that are affected by the operation.
     * 
     * @return the names of the affected feature types
     */
    @Override
    public List<QualifiedName> getAffectedFeatureTypes() {
        Set<QualifiedName> featureTypeSet = new HashSet<QualifiedName>();
        for ( int i = 0; i < this.fc.size(); i++ ) {
            Feature feature = this.fc.getFeature( i );
            featureTypeSet.add( feature.getName() );
        }
        List<QualifiedName> featureTypes = new ArrayList<QualifiedName>( featureTypeSet );
        return featureTypes;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Insert.java,v $
 Revision 1.3  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.2  2006/09/14 00:01:20  mschneider
 Little corrections + javadoc fixes.

 Revision 1.1  2006/05/16 16:25:30  mschneider
 Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.

 ********************************************************************** */