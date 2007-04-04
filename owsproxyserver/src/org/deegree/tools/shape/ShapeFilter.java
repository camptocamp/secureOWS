/*
Coyright 2003 IDgis BV

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
*/

package org.deegree.tools.shape;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * 
 *
 * @version $Revision: 1.4 $
 *
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/07/12 14:46:14 $
 *
 * @since 2.0
 */
public class ShapeFilter extends FileFilter
{
	public boolean accept(File f)
	{
		if(f.isDirectory())
			return true;

		String name = f.getName();
		if(name.length() > 4) {
			return name.substring(name.length() - 4).equalsIgnoreCase(".shp");
        }
		return false;
	}

	public String getDescription()
	{
		return "ESRI ShapeFiles";
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapeFilter.java,v $
Revision 1.4  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
