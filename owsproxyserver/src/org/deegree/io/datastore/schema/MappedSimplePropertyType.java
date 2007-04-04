//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/MappedSimplePropertyType.java,v 1.14 2006/09/12 14:30:15 mschneider Exp $
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
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.model.feature.schema.SimplePropertyType;

/**
 * Representation of property types that contain primitive data types with mapping (persistence)
 * information.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.14 $, $Date: 2006/09/12 14:30:15 $
 */
public class MappedSimplePropertyType extends SimplePropertyType implements MappedPropertyType {

    private boolean isIdentityPart;

    private TableRelation[] tableRelations;

    private SimpleContent content;

    /**
     * Constructs a new instance of <code>MappedSimplePropertyType</code> from the given
     * parameters.
     * 
     * @param name
     * @param type
     * @param minOccurs
     * @param maxOccurs
     * @param isIdentityPart
     * @param tableRelations
     * @param content
     */
    public MappedSimplePropertyType( QualifiedName name, int type, int minOccurs, int maxOccurs,
                                    boolean isIdentityPart, TableRelation[] tableRelations,
                                    SimpleContent content ) {
        super( name, type, minOccurs, maxOccurs );
        this.isIdentityPart = isIdentityPart;
        this.tableRelations = tableRelations;
        this.content = content;
    }

    /**
     * Returns whether this property has to be considered when two instances of the parent feature
     * are checked for equality.
     * 
     * @return true, if this property is part of the feature's identity
     */
    public boolean isIdentityPart() {
        return this.isIdentityPart;
    }

    /**
     * Returns the path of {@link TableRelation}s that describe how to get to the table
     * where the content is stored.
     * 
     * @return path of TableRelations, may be null
     */
    public TableRelation[] getTableRelations() {
        return this.tableRelations;
    }

    /**
     * Returns the {@link SimpleContent} of the property type.
     * 
     * @return the SimpleContent of the property type
     */
    public SimpleContent getContent() {
        return this.content;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: MappedSimplePropertyType.java,v $
 Revision 1.14  2006/09/12 14:30:15  mschneider
 Improved javadoc.

 Revision 1.13  2006/08/24 06:40:05  poth
 File header corrected

 Revision 1.12  2006/08/23 16:32:18  mschneider
 Contains SimpleContent now (instead of MappingField).

 Revision 1.11  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.10  2006/08/21 15:43:20  mschneider
 Cleanup. Added "content" subpackage. Removed (completely unused and outdated) FeatureArrayPropertyType.

 Revision 1.9  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */