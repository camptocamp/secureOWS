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
 

 
/**
*
* This Interface defines the Aggregation of Surfaces. The implementing
* class should capsulate a java.util.Vector or a comarative data
* structure.
*
* <p>-----------------------------------------------------</p>
*
* @author Andreas Poth
* @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:15 $
* <p>
*/

public interface MultiSurface extends MultiPrimitive {
    /**
     * @link aggregation 
     * @clientCardinality 0..*
     */
    
   /**
    * adds an Surface to the aggregation 
    */	
    public void addSurface(Surface gms);
    
   /**
    * inserts a Surface in the aggregation. all elements with an index 
    * equal or larger index will be moved. if index is
    * larger then getSize() - 1 or smaller then 0 or gms equals null 
    * an exception will be thrown.
    *
    * @param gms Surface to insert.     
    * @param index position where to insert the new Surface
    */ 
    public void insertSurfaceAt(Surface gms, int index) throws GeometryException;
    
   /**
    * sets the submitted Surface at the submitted index. the element
    * at the position <code>index</code> will be removed. if index is
    * larger then getSize() - 1 or smaller then 0 or gms equals null 
    * an exception will be thrown.
    *
    * @param gms Surface to set.     
    * @param index position where to set the new Surface
    */ 
    public void setSurfaceAt(Surface gms, int index) throws GeometryException;
    
   /**
    * removes the submitted Surface from the aggregation
    *
    * @return the removed Surface
    */ 
    public Surface removeSurface(Surface gms) ;
    
   /**
    * removes the Surface at the submitted index from the aggregation.
    * if index is larger then getSize() - 1 or smaller then 0 
    * an exception will be thrown.
    *
    * @return the removed Surface
    */ 
    public Surface removeSurfaceAt(int index) throws GeometryException;      
    
   /**
    * returns the Surface at the submitted index.
    */ 
    public Surface getSurfaceAt(int index);
    
   /**
    * returns all Surfaces as array
    */ 
    public Surface[] getAllSurfaces();       
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MultiSurface.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
