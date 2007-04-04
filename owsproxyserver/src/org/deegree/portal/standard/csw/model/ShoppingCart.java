//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/model/ShoppingCart.java,v 1.2 2006/06/23 13:37:36 mays Exp $
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

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

package org.deegree.portal.standard.csw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/06/23 13:37:36 $
 * 
 * @since 2.0
 */
public class ShoppingCart {
    
    
    /**
     * A List of SessionRecord elements.
     */
    private List<SessionRecord> contents;

    public ShoppingCart() {
        this.contents = new ArrayList<SessionRecord>(10);
    }
    
    /**
     * @param contents A List of SessionRecord elements.
     */
    public ShoppingCart( List contents ) {
        this();        
        setContents( contents );
    }
    
    /**
     * @param sr A SessionRecord to add to the ShoppingCart contents. Cannot be null.
     */
    public void add( SessionRecord sr ) {
        if( sr == null ){
            throw new NullPointerException( "I told you sr cannot be null." );
        }
        if ( ! this.contents.contains( sr ) ) {
            this.contents.add( sr );
        }
    }
    
    /**
     * @param sr
     */
    public void remove( SessionRecord sr ) {
        this.contents.remove( sr );
    }
    
    /**
     * @param sessionRecords
     */
    public void removeAll( List sessionRecords ) {
        this.contents.removeAll( sessionRecords );
    }
    
    /**
     * 
     */
    public void clear() {
        this.contents.clear();
    }

    /**
     * @return Returns the contents of the ShoppingCart.
     */
    public List getContents() {
        return contents;
    }

    /**
     * @param contents The contents to set.
     * @throws RuntimeException if the passed List contains elements other than SessionRecord.
     */
    public void setContents( List contents ) {
        
        for( int i = 0; i < contents.size(); i++ ) {
            if ( contents.get(i) instanceof SessionRecord ) {
                add( (SessionRecord)contents.get(i) );
            } else {
                throw new RuntimeException( "The list passed to the constructor contains elements " +
                                            "that are not of the type SessionRecord." );
            }
        }
        
    }
    
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShoppingCart.java,v $
Revision 1.2  2006/06/23 13:37:36  mays
add/update csw model files

********************************************************************** */
