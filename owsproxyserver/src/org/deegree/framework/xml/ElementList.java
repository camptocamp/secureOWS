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

package org.deegree.framework.xml;

import java.util.ArrayList;

import org.w3c.dom.Element;

/**
 * Convenience class for easy handling of
 * <code>NodeLists<code> containing only objects of
 * type org.w3c.dom.Element.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/12 14:46:16 $
 */
public class ElementList {
	ArrayList elements = new ArrayList(100);

	public void addElement(Element element) {
		elements.add(element);
	}

	public int getLength() {
		return elements.size();
	}

	public Element item(int i) {
		if (i < 0 || i > elements.size() - 1)
			return null;
		return (Element) elements.get(i);
	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ElementList.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
