//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/MappedGeometryPropertyType.java,v 1.14 2006/11/27 09:07:51 poth Exp $
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

import java.net.URI;

import org.deegree.datatypes.QualifiedName;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.MappingGeometryField;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.GeometryPropertyType;

/**
 * Representation of property types that contain spatial data with mapping (persistence)
 * information.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.14 $, $Date: 2006/11/27 09:07:51 $
 */
public class MappedGeometryPropertyType extends GeometryPropertyType implements MappedPropertyType {

    private boolean isIdentityPart;

    private URI srs;

    private CoordinateSystem cs;    

    private TableRelation[] tableRelations;

    private MappingGeometryField mappingField;

    /**
     * Constructs a new instance of <code>MappedGeometryPropertyType</code> from the given
     * parameters.
     * 
     * @param name
     * @param typeName
     * @param type
     * @param minOccurs
     * @param maxOccurs
     * @param isIdentityPart
     * @param srs 
     * @param tableRelations 
     * @param mappingField 
     * @throws UnknownCRSException 
     */
    public MappedGeometryPropertyType( QualifiedName name, QualifiedName typeName, int type,
                                      int minOccurs, int maxOccurs, boolean isIdentityPart,
                                      URI srs, TableRelation[] tableRelations,
                                      MappingGeometryField mappingField ) throws UnknownCRSException {
        super( name, typeName, type, minOccurs, maxOccurs );
        this.isIdentityPart = isIdentityPart;
        this.srs = srs;
        this.srs = srs;
        // TODO always check if this worked as expected
        this.cs = CRSFactory.create( srs.toString() );        
        this.tableRelations = tableRelations;
        this.mappingField = mappingField;
    }

    /**
     * Returns whether this property has to be considered when two instances of the parent feature
     * are checked for equality.
     * 
     * @return true, if this property is part of the feature's identity, false otherwise
     */
    public boolean isIdentityPart() {
        return this.isIdentityPart;
    }    
    
    /**
     * Returns the SRS of the property's geometry content.
     * 
     * @return the SRS of the property's geometry content
     */
    public URI getSRS() {
        return this.srs;
    }

    /**
     * Returns the {@link CoordinateSystem} of the property's geometry content.
     * 
     * @return the coordinate system of the property's geometry content
     */
    public CoordinateSystem getCS() {
        return this.cs;
    }

    /**
     * Returns the path of <code>TableRelation</code>s that describe how to get to the table
     * where the content is stored.
     * 
     * @return path of TableRelations, may be null
     */
    public TableRelation[] getTableRelations() {
        return this.tableRelations;
    }    
    
    /**
     * Returns the {@link MappingField} that stores the geometry information.
     * 
     * @return the MappingField that stores the geometry information
     */
    public MappingGeometryField getMappingField() {
        return this.mappingField;
    }    
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: MappedGeometryPropertyType.java,v $
 Revision 1.14  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.13  2006/08/24 06:40:05  poth
 File header corrected

 Revision 1.12  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.11  2006/08/21 15:43:20  mschneider
 Cleanup. Added "content" subpackage. Removed (completely unused and outdated) FeatureArrayPropertyType.

 Revision 1.10  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
