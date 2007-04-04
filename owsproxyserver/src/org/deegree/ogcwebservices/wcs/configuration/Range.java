// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/Range.java,v 1.7 2006/04/06 20:25:27 poth Exp $
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
package org.deegree.ogcwebservices.wcs.configuration;

import java.util.Arrays;
import java.util.List;

/**
 * A <tt>Range</tt> defines the range of variable values like time or elevation
 * for which the coverages assigned to a <tt>Range</tt> are valid. The valid
 * values are given by the <tt>Axis</tt> of a <tt>Range</tt>. A <tt>Range</tt>
 * can have as much <tt>Axis</tt> and so as much filter dimensions as desired.
 * If a <tt>Range</tt> doesn't have explicit <tt>Axis</tt> they are implicit 
 * coded in the assigned <tt>Directory</tt> or </tt>File</tt> name property.   
 *
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public class Range {

    private String name = null;
    private List axis = null;
   
    /**
     * @param name name of the <tt>Range</tt> 
     */
    public Range(String name) {
        this.name = name;
    }
    
    /**
     * @param name name of the <tt>Range</tt>
     * @param axis list of <tt>Axis</tt> (filter dimensions) assigned to the 
     *             <tt>Range</tt>   
     */
    public Range(String name, Axis[] axis) {
        this.name = name;
        setAxis(axis);
    }

    /**
     * returns the list of <tt>Axis</tt> (filter dimensions) assigned to the 
     * <tt>Range</tt>
     * @return Returns the axis.
     * 
     * @uml.property name="axis"
     */
    public Axis[] getAxis() {
        return (Axis[]) axis.toArray(new Axis[axis.size()]);
    }


    /**
     * sets the list of <tt>Axis</tt> (filter dimensions) assigned to the 
     * <tt>Range</tt>
     * @param axis The axis to set.
     */
    public void setAxis(Axis[] axis) {
        this.axis = Arrays.asList(axis);
    }
    
    /**
     * adds an <tt>Axis</tt> to the Range
     * @param axis
     */
    public void addAxis(Axis axis) {
        this.axis.add( axis );
    }
    
    /**
     * removes an <tt>Axis</tt> from the Range
     * @param axis
     */
    public void removeAxis(Axis axis) {
        this.axis.remove( axis );
    }

    /**
     * @return Returns the name.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Range.java,v $
   Revision 1.7  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.6  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.5  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.4  2006/01/16 20:36:39  poth
   *** empty log message ***

   Revision 1.3  2005/02/13 21:34:58  friebe
   fix javadoc errors

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
