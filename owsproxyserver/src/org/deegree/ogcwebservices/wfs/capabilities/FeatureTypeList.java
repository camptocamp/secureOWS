//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/FeatureTypeList.java,v 1.10 2006/10/02 16:52:44 mschneider Exp $
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
 Aennchenstr. 19
 53115 Bonn
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

package org.deegree.ogcwebservices.wfs.capabilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;

/**
 * This section defines the list of feature types (and the available operations on each feature
 * type) that are served by a web feature server. It's used in responses to {@link WFSGetCapabilities}
 * requests.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.10 $ $Date: 2006/10/02 16:52:44 $
 */
public class FeatureTypeList {

    private Operation[] globalOperations;

    private Map<QualifiedName, WFSFeatureType> featureTypes = new HashMap<QualifiedName, WFSFeatureType>();

    /**
     * Creates a new <code>FeatureTypeList</code> instance.
     * 
     * @param globalOperations
     * @param featureTypes
     */
    public FeatureTypeList( Operation[] globalOperations, Collection<WFSFeatureType> featureTypes ) {
        this.globalOperations = globalOperations;
        for ( WFSFeatureType ft : featureTypes ) {
            this.featureTypes.put( ft.getName(), ft );
        }
    }

    /**
     * Returns all served feature types.
     *
     * @return all served feature types
     */
    public WFSFeatureType[] getFeatureTypes() {
        return this.featureTypes.values().toArray( new WFSFeatureType[this.featureTypes.size()] );
    }

    /**
     * Returns the feature type with the given name.
     * 
     * @param name
     *            name of the feature type to look up
     * @return the feature type with the given name
     */
    public WFSFeatureType getFeatureType( QualifiedName name ) {
        return this.featureTypes.get( name );
    }

    /**
     * Adds the given feature type to the list of served feature types.
     * 
     * @param featureType
     *            feature type to be added
     */
    public void addFeatureType( WFSFeatureType featureType ) {
        this.featureTypes.put( featureType.getName(), featureType );
    }

    /**
     * Removes the given feature type from the list of served feature types.
     * 
     * @param featureType
     *            feature type to be removed
     */
    public void removeFeatureType( WFSFeatureType featureType ) {
        this.featureTypes.remove( featureType.getName() );
    }

    /**
     * Returns the {@link Operation}s that are available on all served feature types.
     * 
     * @return the {@link Operation}s that are available on all served feature types
     */
    public Operation[] getGlobalOperations() {
        return globalOperations;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureTypeList.java,v $
 Revision 1.10  2006/10/02 16:52:44  mschneider
 Some cleanup. Javadoc fixes.

 Revision 1.9  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */