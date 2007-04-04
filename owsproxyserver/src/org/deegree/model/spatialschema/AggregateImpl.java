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
import java.util.ArrayList;
import java.util.Iterator;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;



/**
* default implementierung of the Aggregate interface 
*
* ------------------------------------------------------------
* @version 8.6.2001
* @author Andreas Poth href="mailto:poth@lat-lon.de"
*/
abstract class AggregateImpl extends GeometryImpl implements Aggregate, Serializable {
    
    private static ILogger LOG = LoggerFactory.getLogger( AggregateImpl.class );
    
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 1161164609227432958L;
    protected ArrayList aggregate = new ArrayList( 500 );
    

    /**
     * Creates a new AggregateImpl object.
     *
     * @param crs 
     */
    public AggregateImpl( CoordinateSystem crs ) {
        super( crs );
    }

    /**
     * Creates a new AggregateImpl object.
     */
    private AggregateImpl() {
        super( null );
    }        

    /**
     * returns the number of Geometry within the aggregation
     */
    public int getSize() {
        return aggregate.size();
    }

    /**
     * merges this aggregation with another one 
     *
     * @exception GeometryException a GeometryException will be thrown if the submitted
     *             isn't the same type as the recieving one.
     */
    public void merge( Aggregate aggregate ) throws GeometryException {
        if ( !this.getClass().getName().equals( aggregate.getClass().getName() ) ) {
            throw new GeometryException( "Aggregations are not of the same type!" );
        }

        for ( int i = 0; i < this.getSize(); i++ ) {
            this.add( aggregate.getObjectAt( i ) );
        }

        setValid( false );
    }

    /**
     * adds an Geometry to the aggregation 
     */
    public void add( Geometry gmo ) {
        aggregate.add( gmo );

        setValid( false );
    }

    /**
     * inserts a Geometry in the aggregation. all elements with an index 
     * equal or larger index will be moved. if index is
     * larger then getSize() - 1 or smaller then 0 or gmo equals null 
     * an exception will be thrown.
     *
     * @param gmo Geometry to insert.     
     * @param index position where to insert the new Geometry
     */
    public void insertObjectAt( Geometry gmo, int index ) throws GeometryException {
        if ( ( index < 0 ) || ( index > this.getSize() - 1 ) ) {
        	throw new GeometryException( "invalid index/position: " + index +
        							" to insert a geometry!" );
        } 

        if ( gmo == null ) {
            throw new GeometryException( "gmo == null. it isn't possible to insert a value" + 
                                    " that equals null!" );
        }

        aggregate.add( index, gmo );

        setValid( false );
    }

    /**
     * sets the submitted Geometry at the submitted index. the element
     * at the position <code>index</code> will be removed. if index is
     * larger then getSize() - 1 or smaller then 0 or gmo equals null 
     * an exception will be thrown.
     *
     * @param gmo Geometry to set.     
     * @param index position where to set the new Geometry
     */
    public void setObjectAt( Geometry gmo, int index ) throws GeometryException {
        if ( ( index < 0 ) || ( index > this.getSize() - 1 ) ) {
        	throw new GeometryException( "invalid index/position: " + index +
        							" to set a geometry!" );
        }

        if ( gmo == null ) {
            throw new GeometryException( "gmo == null. it isn't possible to set a value" + 
                                    " that equals null!" );
        }

        aggregate.set( index, gmo );

        setValid( false );
    }

    /**
     * removes the submitted Geometry from the aggregation
     *
     * @return the removed Geometry
     */
    public Geometry removeObject( Geometry gmo ) {
        if ( gmo == null ) {
            return null;
        }

        int i = aggregate.indexOf( gmo );

        Geometry gmo_ = null;

        try {
            gmo_ = removeObjectAt( i );
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }

        setValid( false );

        return gmo_;
    }

    /**
     * removes the Geometry at the submitted index from the aggregation.
     * if index is larger then getSize() - 1 or smaller then 0 
     * an exception will be thrown.
     *
     * @return the removed Geometry
     */
    public Geometry removeObjectAt( int index ) throws GeometryException {
        if ( index < 0 ) {
            return null;
        }

        if ( index > ( this.getSize() - 1 ) ) {
            throw new GeometryException( "invalid index/position: " + index +
            						" to remove a geometry!" );
        }

        Geometry gmo = (Geometry)aggregate.remove( index );

        setValid( false );

        return gmo;
    }

    /**
     * removes all Geometry from the aggregation. 
     */
    public void removeAll() {
        aggregate.clear();
        envelope = null;
        setValid( false );
    }

    /**
     * returns the Geometry at the submitted index. if index is
     * larger then getSize() - 1 or smaller then 0 
     * an exception will be thrown.
     */
    public Geometry getObjectAt( int index ) {
        return (Geometry)aggregate.get( index );
    }

    /**
     * returns all Geometries as array
     */
    public Geometry[] getAll() {
        Geometry[] gmos = new Geometry[this.getSize()];

        return (Geometry[])aggregate.toArray( gmos );
    }

    /**
     * returns true if the submitted Geometry is within the aggregation
     */
    public boolean isMember( Geometry gmo ) {
        return aggregate.contains( gmo );
    }

    /**
     * returns the aggregation as an iterator
     */
    public Iterator getIterator() {
        return aggregate.iterator();
    }

    /**
     * returns true if no geometry stored
     * within the collection.
     */
    public boolean isEmpty() {
        return ( getSize() == 0 );
    }

    /**
     * sets the spatial reference system
     *
     * @param crs new spatial reference system
     */
    public void setCoordinateSystem( CoordinateSystem crs ) {
        super.setCoordinateSystem( crs );

        if ( aggregate != null ) {
            for ( int i = 0; i < aggregate.size(); i++ ) {
                ( (GeometryImpl)getObjectAt( i ) ).setCoordinateSystem( crs );
            }
            setValid( false );
        }
    }    

    /**
     * translate the point by the submitted values. the <code>dz</code>-
     * value will be ignored.
     */
    public void translate( double[] d ) {
        try {
            for ( int i = 0; i < getSize(); i++ ) {
                Geometry gmo = getObjectAt( i );
                gmo.translate( d );
            }
            setValid( false );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
        setValid( false );
    }

    /**
     *
     *
     * @param other 
     *
     * @return 
     */
    public boolean equals( Object other ) {
    	if ( envelope == null ) {
    		calculateParam();
    	}
        if ( !super.equals( other ) || !( other instanceof AggregateImpl ) || 
                 !envelope.equals( ( (Geometry)other ).getEnvelope() ) || 
                 ( getSize() != ( (Aggregate)other ).getSize() ) ) {
            return false;
        }

        try {
            for ( int i = 0; i < getSize(); i++ ) {
                Object o1 = getObjectAt( i );
                Object o2 = ( (Aggregate)other ).getObjectAt( i );

                if ( !o1.equals( o2 ) ) {
                    return false;
                }
            }
        } catch ( Exception ex ) {
            return false;
        }

        return true;
    }

    /**
     * The Boolean valued operation "intersects" shall return TRUE if this Geometry
     * intersects another Geometry. Within a Complex, the Primitives do not
     * intersect one another. In general, topologically structured data uses shared
     * geometric objects to capture intersection information.
     */
    public boolean intersects( Geometry gmo ) {
        boolean inter = false;

        try {
            for ( int i = 0; i < aggregate.size(); i++ ) {
                if ( this.getObjectAt( i ).intersects( gmo ) ) {
                    inter = true;
                    break;
                }
            }
        } catch ( Exception e ) {
        }

        return inter;
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = null;
        ret = "aggregate = " + aggregate + "\n";
        ret += ( "envelope = " + envelope + "\n" );
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AggregateImpl.java,v $
Revision 1.7  2006/11/27 09:07:51  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
