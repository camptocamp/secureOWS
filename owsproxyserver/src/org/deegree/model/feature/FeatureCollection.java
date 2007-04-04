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
package org.deegree.model.feature;

import java.util.Iterator;


/**
 * 
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/07/12 14:46:19 $
 *
 * @since 1.0
 */
public interface FeatureCollection extends Feature {
    /**
     * returns the feature at the submitted index
     */
    public Feature getFeature(int index);

    /**
     * returns the feature identified by it's id
     */
    public Feature getFeature(String id);

    /**
     * returns an array of all features
     */
    public Feature[] toArray();
    
    /**
     * returns an <tt>Iterator</tt> on the feature contained in a collection 
     * @return
     */
    public Iterator iterator();

    /**
     * adds a feature to the collection
     */
    public void add(Feature feature);
    
    /**
     * adds a list of features to the collection
     */
    public void addAll(Feature[] feature);
    
    /**
     * adds a list of features to the collection
     */
    public void addAll(FeatureCollection feature);

    /**
     * removes the submitted feature from the collection
     */
    public Feature remove(Feature feature);

    /**
     * removes the feature at the submitted index from the 
     * collection
     */
    public Feature remove(int index);
    
   /**
     * removes the feature that is assigned to the submitted id. The removed
     * feature will be returned. If no valid feature could be found null will
     * be returned
     */
    public Feature remove(String id);
        
   /**
    * returns the number of features within the collection
    */ 
    public int size();


}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureCollection.java,v $
Revision 1.9  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
