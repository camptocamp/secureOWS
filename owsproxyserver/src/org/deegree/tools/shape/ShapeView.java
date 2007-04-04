/*

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

Application to index a shape file, using the R-tree algorithm.
Copyright (C) May 2003 by IDgis BV, The Netherlands - www.idgis.nl
*/

package org.deegree.tools.shape;

import java.io.File;

import javax.swing.filechooser.FileView;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/12 14:46:14 $
 *
 * @since 2.0
 */
public class ShapeView extends FileView
{
	public String getName(File f)
	{
		String name = f.getName();

		if(f.isDirectory())
			return null;

		return name.substring(0, name.length() - 4);
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapeView.java,v $
Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
