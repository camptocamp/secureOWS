
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

package org.deegree.io.shpapi;

/**
 * Class representing basic Geometry for geometries read from a shape-file.  * <p> *  * <B>Last changes<B>:<BR> * 12.01.2000 ap: constructor re-declared<BR> * 13.01.2000 ap: all methods removed<BR> * 13.01.2000 ap: all variables except reBuffer removed<BR> * 16.08.2000 ap: field enevlope added<BR> * 16.08.2000 ap: method getEnvelope() added<BR> *  * <!----------------------------------------------------------------------------> * @author Andreas Poth * @version 16.08.2000
 */




public class SHPGeometry {

    protected byte[] recBuffer = null;

    /**
     * 
     * @uml.property name="envelope"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    protected SHPEnvelope envelope = null;


    public SHPGeometry () {}

    public SHPGeometry (byte[] recBuf) {

         /* private copy of record Buffer */
        recBuffer = recBuf;
 
    } // end of constructor

    /**
     * returns the minimum boundary rectangle of the geomertry<BR>
     * 
     * @uml.property name="envelope"
     */
    public SHPEnvelope getEnvelope() {
        return envelope;
    }

} // end of class SHPGeometry
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SHPGeometry.java,v $
Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
