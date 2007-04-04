// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/Resolution.java,v 1.4 2006/04/06 20:25:27 poth Exp $
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

/**
 * The interface defines the encpsulating element to coverage data description
 * as used by the deegree WCS CoverageOffering Extension. The interface extends
 * <tt>java.lang.Comparable</tt> to enable a container to sort its <tt>Resolution</tt>s
 * by scale.
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public interface Resolution extends Comparable {
    
    /**
     * returns the minimum scale (inculding) the <tt>Resolution</tt>
     * is valid for.
     * @return
     */
    double getMinScale();
    
    /**
     * returns the maximum scale (exculding) the <tt>Resolution</tt>
     * is valid for.
     * @return
     */
    double getMaxScale();
    
    /**
     * returns the <tt>Range</tt>s included with in resolution. A range is 
     * similar to those defined in OGC WCS 1.0.0 specification for 
     * CoverageOffering. But it is reduced to the elements required for
     * identifying the coverages resources assigned to a specific combination
     * of parameter (values).<p>
     * The return value maybe is <tt>null</tt> if the <tt>Resolution</tt>
     * just describes data from one parameter dimension (missing Range in
     * CoverageOffering). In this case there is direct access to the data
     * source describing element(s). 
     * 
     * @return
     */
    public Range[] getRanges();

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Resolution.java,v $
   Revision 1.4  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:32:36  poth
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
