// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/AbstractResolution.java,v 1.6 2006/04/06 20:25:27 poth Exp $
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
 * A concrete Resolution must implement accessor methods for either Shape, 
 * Directory or File (or additional descriptions available in future) which
 * will be used is no Range is persent.
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
abstract class AbstractResolution implements Resolution {
    
    private double minScale = 0;
    private double maxScale = 9E99;
    private List ranges = null;        
    

    /**
     * @param minScale
     * @param maxScale
     * @param range
     */
    public AbstractResolution(double minScale, double maxScale, Range[] range) 
                                throws IllegalArgumentException {
        if ( minScale >= maxScale ) {
            throw new IllegalArgumentException( "minScale must be > maxScale" );
        }
        this.minScale = minScale;
        this.maxScale = maxScale;
        setRange(range);
    }

    /**
     * @see #getMaxScale()
     * @param maxScale The maxScale to set.
     * 
     * @uml.property name="maxScale"
     */
    public void setMaxScale(double maxScale) throws IllegalArgumentException {
        if (minScale >= maxScale) {
            throw new IllegalArgumentException("minScale must be > maxScale");
        }
        this.maxScale = maxScale;
    }

    /**
     * @see #getMinScale()
     * @param minScale The minScale to set.
     * 
     * @uml.property name="minScale"
     */
    public void setMinScale(double minScale) throws IllegalArgumentException {
        if (minScale >= maxScale) {
            throw new IllegalArgumentException("minScale must be > maxScale");
        }
        this.minScale = minScale;
    }


    /**
     * @see #getRanges()
     * @param ranges The range to set.
     */
    public void setRange(Range[] ranges) {
        this.ranges = Arrays.asList(ranges);
    }
    
    /**
     * @see #getRanges()
     * @param range
     */
    public void addRange(Range range) {
        ranges.add( range );
    }
    
    /**
     * removes a range from a <tt>Resolution</tt>
     * @param range
     */
    public void removeRange(Range range) {
        ranges.remove(range);
    }

    /**
     * returns the minimum scale (inculding) the <tt>Resolution</tt>
     * is valid for.
     * @return
     * 
     * @uml.property name="minScale"
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * returns the maximum scale (exculding) the <tt>Resolution</tt>
     * is valid for.
     * @return
     * 
     * @uml.property name="maxScale"
     */
    public double getMaxScale() {
        return maxScale;
    }

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
     * 
     * @uml.property name="ranges"
     */
    public Range[] getRanges() {
        return (Range[]) ranges.toArray(new Range[ranges.size()]);
    }

    
    /**
     * minScale is used for comparing. If this.minScale < o.getMinScale -1 will 
     * be ruturned; vice versa 1 will be returned. only is this.minScale ==
     * o.minScale 0 will be returned. 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) throws IllegalArgumentException {
        if ( !(o instanceof Resolution ) ) {
            throw new IllegalArgumentException("o must be an instance of Resolution");
        }
        Resolution res = (Resolution)o;
        if ( getMinScale() < res.getMinScale() ) {
            return -1;
        }
        if ( getMinScale() > res.getMinScale() ) {
            return 1;
        }
        return 0;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: AbstractResolution.java,v $
   Revision 1.6  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2006/01/16 20:36:39  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
