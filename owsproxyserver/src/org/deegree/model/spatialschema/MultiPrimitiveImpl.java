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
package org.deegree.model.spatialschema;

import java.io.Serializable;

import org.deegree.model.crs.CoordinateSystem;


/**
 * default implementation of the MultiPrimitive interface of
 * package jago.model.
 *
 * <p>------------------------------------------------------------</p>
 * @version 5.6.2001
 * @author Andreas Poth
 * <p>
 */
class MultiPrimitiveImpl extends AggregateImpl implements MultiPrimitive,
                                                                           Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 7228377539686274411L;

    /**
     * Creates a new MultiPrimitiveImpl object.
     *
     * @param crs 
     */
    public MultiPrimitiveImpl( CoordinateSystem crs ) {
        super( crs );
    }

    /**
     * merges this aggregation with another one
     *
     * @exception GeometryException will be thrown if the submitted
     *             isn't the same type as the recieving one.
     */
    public void merge( Aggregate aggregate ) throws GeometryException {
        if ( !( aggregate instanceof MultiPrimitive ) ) {
            throw new GeometryException( "The submitted aggregation isn't a MultiPrimitive" );
        }

        super.merge( aggregate );
    }

    /**
     * returns the Primitive at the submitted index.
     */
    public Primitive getPrimitiveAt( int index ) {
        return (Primitive)super.getObjectAt( index );
    }

    /**
     * returns all Primitives as array
     */
    public Primitive[] getAllPrimitives() {
        Primitive[] gmos = new Primitive[this.getSize()];

        return (Primitive[])aggregate.toArray( gmos );
    }
    
    protected void calculateParam() {
    }
    
    public int getCoordinateDimension() {
        return -1;
    }
    
    public int getDimension() {
        return 2;
    }
    
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MultiPrimitiveImpl.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
