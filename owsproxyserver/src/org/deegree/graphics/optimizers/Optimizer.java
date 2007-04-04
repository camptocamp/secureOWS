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
package org.deegree.graphics.optimizers;

import java.awt.Graphics2D;

import org.deegree.graphics.MapView;
import org.deegree.graphics.Theme;

/**
 * This is the general interface for optimizers that need to alter the
 * contents of <tt>Theme</tt>s before the parent <tt>MapView</tt> object is
 * painted. For example, the placements of <tt>LabelDisplayElement</tt>s
 * in a <tt>Theme</tt> may be optimized to minimize overlapping using the
 * <tt>LabelOptimizer</tt>.
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:19 $
 */
public interface Optimizer {

	/**
	 * Sets the associated <tt>MapView</tt>-instance. Is needed for the scale
	 * and projection information. Called by the <tt>MapView</tt>.
	 * @param mapView
	 */
	public void setMapView (MapView mapView);

	/**
	 * Adds a <tt>Theme</tt> that the <tt>Optimizer</tt> should consider.
	 * @param theme
	 */	
	public void addTheme (Theme theme);
	
	/**
	 * Invokes the optimization process. The <tt>Optimizer</tt> will now
	 * process and modify the contents of the attached <tt>Theme</tt>s.
	 * @param g
	 */	
	public abstract void optimize (Graphics2D g) throws Exception;
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Optimizer.java,v $
Revision 1.7  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
