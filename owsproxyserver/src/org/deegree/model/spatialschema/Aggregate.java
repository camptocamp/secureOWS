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


import java.util.Iterator;

/**
*
* This interface defines the basis functionallity of all geometry
* aggregations. it will be specialized for the use of primitive,
* and solid geometries.
*
* <p>-----------------------------------------------------</p>
*
* @author Andreas Poth
* @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:15 $
* <p>
*/

public interface Aggregate extends Geometry {
	
   /**
    * returns the number of Geometry within the aggregation
    */	
	int getSize();
	
   /**
    * merges two aggregation.
    *
    * @exception GeometryException a GeometryException will be thrown if the submitted
    *			 isn't the same type as the recieving one.
    */	
	void merge(Aggregate aggregate) throws GeometryException;
	
   /**
    * adds an Geometry to the aggregation 
    */	
    void add(Geometry gmo);    
    
   /**
    * inserts a Geometry in the aggregation. all elements with an index 
    * equal or larger index will be moved. if index is
    * larger then getSize() - 1 an exception will be thrown.
    *
    * @param gmo Geometry to insert.     
    * @param index position where to insert the new Geometry
    */ 
    void insertObjectAt(Geometry gmo, int index) throws GeometryException;
    
   /**
    * sets the submitted Geometry at the submitted index. the element
    * at the position <code>index</code> will be removed. if index is
    * larger then getSize() - 1 an exception will be thrown.
    *
    * @param gmo Geometry to set.     
    * @param index position where to set the new Geometry
    */ 
    void setObjectAt(Geometry gmo, int index) throws GeometryException;
    
   /**
    * removes the submitted Geometry from the aggregation
    *
    * @return the removed Geometry
    */ 
    Geometry removeObject(Geometry gmo);
    
   /**
    * removes the Geometry at the submitted index from the aggregation.
    * if index is larger then getSize() - 1 an exception will be thrown.
    *
    * @return the removed Geometry
    */ 
    Geometry removeObjectAt(int index) throws GeometryException;
    
   /**
    * removes all Geometry from the aggregation. 
    */ 
    void removeAll();
    
   /**
    * returns the Geometry at the submitted index. 
    */ 
    Geometry getObjectAt(int index);
    
   /**
    * returns all Geometries as array
    */ 
    Geometry[] getAll();
    
   /**
    * returns true if the submitted Geometry is within the aggregation
    */
    boolean isMember(Geometry gmo); 
    
   /**
    * returns the aggregation as an iterator
    */ 
    Iterator getIterator();       
     
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Aggregate.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
