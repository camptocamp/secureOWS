/*

This file is part of deegree.

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

Copyright (C) May 2003 by IDgis BV, The Netherlands - www.idgis.nl
*/

package org.deegree.io.dbaseapi;

public abstract class DBaseIndexException extends Exception
{
	private Comparable key;

    /**
     * 
     * @uml.property name="index"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private DBaseIndex index;



	public DBaseIndexException(String error, Comparable key, DBaseIndex index)
	{
		super(error);

		this.key = key;
		this.index = index;
	}

    /**
     * 
     * @uml.property name="key"
     */
    public Comparable getKey() {
        return key;
    }

    /**
     * 
     * @uml.property name="index"
     */
    public DBaseIndex getIndex() {
        return index;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBaseIndexException.java,v $
Revision 1.3  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
