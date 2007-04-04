//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/FieldContent.java,v 1.1 2006/09/13 18:22:15 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.io.datastore.schema.content;

import org.deegree.io.datastore.schema.TableRelation;

/**
 * Describes the parameter of a {@link FunctionCall}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/09/13 18:22:15 $
 */
public class FieldContent implements FunctionParam {

    private MappingField field;
    
    private TableRelation[] tablePath;
        
    /**
     * Creates a new <code>FieldContent</code> instance.
     * 
     * @param field
     * @param tablePath
     */
    public FieldContent (MappingField field, TableRelation [] tablePath) {
        this.field = field;
        this.tablePath = tablePath;
    }
    
    /**
     * Returns the {@link MappingField} that is used as the source of the data.
     * 
     * @return the MappingField that is used as the source of the data
     */
    public MappingField getField () {
        return this.field;
    }
    
    /**
     * Returns the {@link TableRelation}s that lead from the table that is associated
     * with the {@link FunctionCall} to the {@link MappingField}.
     * 
     * @return table path from FunctionCall table to MappingField table
     */
    public TableRelation [] getTablePath () {
        return this.tablePath;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FieldContent.java,v $
Revision 1.1  2006/09/13 18:22:15  mschneider
Initial version.

********************************************************************** */