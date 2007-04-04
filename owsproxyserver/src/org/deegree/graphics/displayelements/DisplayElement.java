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
package org.deegree.graphics.displayelements;

import java.awt.Graphics;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.feature.Feature;

/**
 * Basic interface of all display elements. A <tt>DisplayElement</tt> is  * associated to one feature that may have a geometry property or not  * (usually it has). * <p> * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.8 $ $Date: 2006/07/12 14:46:16 $
 */

public interface DisplayElement {

    /**
     * Returns the associated <tt>Feature</tt>.
     */
    Feature getFeature ();
    
    /**
     * sets the feature encapsulated by a DisplayElement
     * @param feature
     */
    void setFeature(Feature feature);
    
   /**
    * returns the id of thr feature that's associated with the 
    * DisplayElement
    */	
    String getAssociateFeatureId();
	
   /**
    *  renders the DisplayElement to the submitted graphic context
    */	
    void paint(Graphics g, GeoTransform projection, double scale);

    /**
     * marks a <tt>DisplayElement</tt> as selected or not
     * 
     * @uml.property name="selected"
     */
    void setSelected(boolean selected);

    /**
     * returns if the <tt>DisplayElement</tt> is selected or not
     * 
     * @uml.property name="selected"
     */
    boolean isSelected();

    /**
     * marks the <tt>DisplayElement</tt> as highlighted or not
     * 
     * @uml.property name="highlighted"
     */
    void setHighlighted(boolean highlighted);

    /**
     * returns if the <tt>DisplayElement</tt> is highlighted or not.
     * 
     * @uml.property name="highlighted"
     */
    boolean isHighlighted();

    /**
     * Returns if the <tt>DisplayElement</tt> should be painted at the
     * current scale or not.
     */
    boolean doesScaleConstraintApply (double scale);
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DisplayElement.java,v $
Revision 1.8  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
