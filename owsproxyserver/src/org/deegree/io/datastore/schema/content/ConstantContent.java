//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/ConstantContent.java,v 1.4 2006/09/19 14:52:58 mschneider Exp $
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

import org.deegree.io.datastore.schema.MappedSimplePropertyType;

/**
 * Special content class for {@link MappedSimplePropertyType}s that contain constant string
 * values.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/09/19 14:52:58 $
 */
public class ConstantContent extends VirtualContent implements FunctionParam {

    private String constant;

    /**
     * Initializes a newly created <code>ConstantContent</code> object so that it represents
     * the given {@link String} value.
     * 
     * @param constant
     */
    public ConstantContent( String constant ) {
        this.constant = constant;
    }

    /**
     * Returns false, because it makes no sense to use a constant as a sort criterion. 
     * 
     * @return false, because it makes no sense to use a constant as a sort criterion.
     */    
    public boolean isSortable () {
        return false;
    }    
    
    /**
     * Returns the constant {@link String} that this object contains.
     * 
     * @return constant String that this object contains
     */
    public String getValue () {
        return this.constant;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ConstantContent.java,v $
 Revision 1.4  2006/09/19 14:52:58  mschneider
 Added abstract base class VirtualContent.

 Revision 1.3  2006/09/13 18:22:55  mschneider
 Fixed FunctionParam hierarchy.

 Revision 1.2  2006/08/29 15:53:18  mschneider
 Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable(). Added #isSortable().

 Revision 1.1  2006/08/23 16:30:45  mschneider
 Initial version.

 ********************************************************************** */
