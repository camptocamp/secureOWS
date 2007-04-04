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
package org.deegree.ogcwebservices.wms.capabilities;



/**
 * A Map Server may use zero or more Identifier elements to list ID numbers
 * or labels defined by a particular Authority. For example, the Global Change
 * Master Directory (gcmd.gsfc.nasa.gov) defines a DIF_ID label for every
 * dataset. The authority name and explanatory URL are defined in a separate
 * AuthorityURL element, which may be defined once and inherited by subsidiary
 * layers. Identifiers themselves are not inherited.
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.10 $
 */
public class Identifier  {
    private String authority = null;
    private String value = null;
  

    /**
    * constructor initializing the class with the <Identifier>
     * @param value 
     * @param authority 
    */
    public Identifier( String value, String authority ) {
        setValue( value );
        setAuthority( authority );
    }

    /**
     * @return the value of the identifier. that may be a ID, a label
     * or something comparable
     */
    public String getValue() {
        return value;
    }

    /**
    * sets the value of the identifier. that may be a ID, a label
    * or something comparable
     * @param value 
    */
    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * @return the name of the authority that defines the identifier
     */
    public String getAuthority() {
        return authority;
    }

    /**
    * sets the name of the authority that defines the identifier
     * @param authority 
    */
    public void setAuthority( String authority ) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        String ret = null;
        ret = "value = " + value + "\n";
        ret += ( "authority = " + authority + "\n" );
        return ret;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Identifier.java,v $
Revision 1.10  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.9  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
