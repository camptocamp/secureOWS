//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/MappedFeatureTypeReference.java,v 1.8 2006/08/24 06:40:05 poth Exp $
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

/**
 * Represents a reference to a {@link MappedFeatureType}.
 * <p>
 * The reference may be resolved or not. If it is resolved, the referenced
 * {@link MappedFeatureType} is accessible, otherwise only the name of the type is available.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/08/24 06:40:05 $
 */
public class MappedFeatureTypeReference {

    private QualifiedName featureTypeName;

    private MappedFeatureType featureType;

    private boolean isResolved;

    /**
     * Creates an unresolved <code>MappedFeatureTypeReference</code>.
     * 
     * @param featureTypeName
     */
    public MappedFeatureTypeReference( QualifiedName featureTypeName ) {
        this.featureTypeName = featureTypeName;
    }

    /**
     * Returns the name of the referenced {@link MappedFeatureType}.
     * 
     * @return the name of the referenced feature type
     */
    public QualifiedName getName() {
        return this.featureTypeName;
    }

    /**
     * Returns true, if the reference has been resolved.
     * <p>
     * If this method returns true, {@link #getFeatureType()} will return the correct
     * {@link MappedFeatureType} instance. 
     * 
     * @return true, if the reference has been resolved, false otherwise
     */    
    public boolean isResolved() {
        return this.isResolved;
    }

    /**
     * Returns the referenced {@link MappedFeatureType}.
     * <p>
     * This method will only return the correct {@link MappedFeatureType} instance, if
     * the reference has been resolved by a call to {@link #resolve(MappedFeatureType)}.
     * 
     * @return the referenced feature type, or null if it has not been resolved
     */
    public MappedFeatureType getFeatureType() {
        return this.featureType;
    }

    /**
     * Sets the referenced {@link MappedFeatureType} instance.
     * 
     * @param featureType
     * @throws RuntimeException
     *             if the reference has been resolved already
     */
    public void resolve( MappedFeatureType featureType ) {
        if ( isResolved() ) {
            throw new RuntimeException( "MappedFeatureTypeReference to feature type '"
                + featureTypeName + "' has already been resolved." );
        }
        this.featureType = featureType;
        this.isResolved = true;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MappedFeatureTypeReference.java,v $
Revision 1.8  2006/08/24 06:40:05  poth
File header corrected

Revision 1.7  2006/08/21 16:40:27  mschneider
Javadoc improvements.

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */