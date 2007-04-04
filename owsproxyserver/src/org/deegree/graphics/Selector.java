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

package org.deegree.graphics;


import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;

/**
*
* <p>------------------------------------------------------------------------</p>
*
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
* @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:18 $
*/
public interface Selector {
	
   /**
    * adds a Theme to the Selector that shall be notified if something
    * happens.
    */	
	void addTheme(Theme theme);
	
   /**
    * @see Selector#addTheme(Theme)
    */	
	void removeTheme(Theme theme);	

   /**
    * selects all features (display elements) that are located within the
    * submitted bounding box.
    * @return ids of the selected features (display elements)
    */
    String[] select(Envelope boundingbox);

   /**
    * selects all features (display elements) that intersects the submitted
    * point. if a feature is already selected it remains selected.
    * @return ids of the selected features (display elements)
    */
	String[] select(Position position);

   /**
    * selects all features (display elements) that are located within the circle
    * described by the position and the radius. if a feature is already selected
    * it remains selected.
    * @return ids of the selected features (display elements)
    */
	String[] select(Position position, double radius);

   /**
    * selects all features (display elements) that are specified by the submitted
    * ids. if a feature is already selected it remains selected.
    * @return ids of the selected features (display elements)
    */
    String[] select(String[] ids);

   /**
    * invertes the current selection.
    */
	String[] invertSelection();

   /**
    * marks all features as unselected
    */
    void reset();

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Selector.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
