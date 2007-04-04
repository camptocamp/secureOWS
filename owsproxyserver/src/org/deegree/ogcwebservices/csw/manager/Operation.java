//$Header$
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
package org.deegree.ogcwebservices.csw.manager;

/**
 * 
 * 
 * 
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 2.0
 */
public abstract class Operation {

    private String handle = null;

    private String name = null;

    public Operation( String name, String handle ) {
        super();
        this.handle = handle;
        this.name = name;
    }

    /**
     * The handle attribute is an additional parameter not defined in the general model. It is used
     * in the XML encoding to associate a mnemonic name with each action contained in a Transaction
     * for the purpose of error handling. If a CSW encounters an error processing a transaction
     * request and the handle attribute is defined, the CSW can localize the source of the problem
     * for the client by specifying the handle in the exception response.
     * 
     * @return
     */
    public String getHandle() {
        return handle;
    }

    /**
     * returns the name of the operation
     * 
     * @return
     */
    public String getName() {
        return name;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log$
 * Changes to this class. What the people have been up to: Revision 1.6  2006/08/29 19:54:14  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2006/04/06 20:25:27  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4  2006/04/04 20:39:43  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to: Revision 1.3 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/17 16:28:12 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/