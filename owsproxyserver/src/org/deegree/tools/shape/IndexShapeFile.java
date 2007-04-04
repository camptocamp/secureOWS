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

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
<p>IndexShapeFile is an application that can be used to index an ESRI ShapeFiles(tm).
It indexes both the geometry and the alphanumeric attributes</p>

<p>The application shows a file chooser with which the user can select a file. When a file
is choosen the application opens it and shows the attributes. The user can now select the
attributes that has to be indexed. Already indexed attributes are already selected and can
be deselected. For alphanumeric attributes the user can indicate if the attribute is unique or not.</p>

<p>After selecting the attributes the application creates the needed indexes and loops over all the
features in the shape file. For every feature the application inserts the attributes in the right index.
After looping over the features the application closes the shapefile and the created indexes and removes the indexes
that are no longer needed (eg. the index for the attributes that are deselected).</p>

<p>It is not possible to transform a unique index to a non-unique index or back.</p>
*/
public class IndexShapeFile
{
	public static void main(String[] args)	 throws Exception
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new ShapeFilter());
		fileChooser.setFileView(new ShapeView());
		if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			IndexFrame indexFrame = new IndexFrame(fileChooser.getSelectedFile());
			indexFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			indexFrame.show();
		}
		else
			System.exit(0);
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IndexShapeFile.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
