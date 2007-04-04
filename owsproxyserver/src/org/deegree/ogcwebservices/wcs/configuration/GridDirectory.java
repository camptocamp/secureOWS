// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/GridDirectory.java,v 1.6 2006/05/01 20:15:26 poth Exp $
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

import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;

/**
 * An instance of <tt>GridDirectory</tt> describes a directory in the file
 * system containing grid coverages within the envelope assigned to the 
 * <tt>Directory</tt>. The name of the <tt>Directory</tt> may is build
 * from variable indicated by a leadin '$'
 * (e.g. C:/rasterdata/luftbilder/775165/$YEAR/$MONTH/$DAY/$ELEVATION/l0.5)
 * in this case the variable parts of the name can be replaced by
 * an application with concrete values. It is in the responsibility of
 * the application to use valid values for the variables. Known variable
 * names are:
 * <ul>
 *  <li>$YEAR
 *  <li>$MONTH
 *  <li>$DAY
 *  <li>$HOUR
 *  <li>$MINUTE
 *  <li>$ELEVATION
 * </ul>
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/05/01 20:15:26 $
 *
 * @since 2.0
 */
public class GridDirectory extends Directory {

   private double tileWidth = 0;
   private double tileHeight = 0;
    
    
    /**
     * file extentions will be empty. this will cause that all files in the
     * directory will be recognized
     * @param name name of the directory
     * @param envelope enclosing envelope of the tiles in the directory
     * @param crs CRS of the data
     * @param tileWidth width (pixels) of the files in the directory
     * @param tileHeight height (pixels) of the files in the directory
     */
    public GridDirectory(String name, Envelope envelope, CoordinateSystem crs,  
    					 double tileWidth, double tileHeight) {
        super(name, envelope, crs);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }
    
    
    /**
     * @param name name of the directory
     * @param envelope enclosing envelope of the tiles in the directory
     * @param crs CRS of the data
     * @param fileExtensions list of reconized file extensions
     * @param tileWidth width (pixels) of the files in the directory
     * @param tileHeight height (pixels) of the files in the directory
     */
    public GridDirectory(String name, Envelope envelope, CoordinateSystem crs,  
                         String[] fileExtensions, double tileWidth, double tileHeight) {
        super(name, envelope, crs, fileExtensions);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * @return Returns the tileHeight.
     * 
     * @uml.property name="tileHeight"
     */
    public double getTileHeight() {
        return tileHeight;
    }

    /**
     * @param tileHeight The tileHeight to set.
     * 
     * @uml.property name="tileHeight"
     */
    public void setTileHeight(double tileHeight) {
        this.tileHeight = tileHeight;
    }

    /**
     * @return Returns the tileWidth.
     * 
     * @uml.property name="tileWidth"
     */
    public double getTileWidth() {
        return tileWidth;
    }

    /**
     * @param tileWidth The tileWidth to set.
     * 
     * @uml.property name="tileWidth"
     */
    public void setTileWidth(double tileWidth) {
        this.tileWidth = tileWidth;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GridDirectory.java,v $
   Revision 1.6  2006/05/01 20:15:26  poth
   *** empty log message ***

   Revision 1.5  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.4  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.5  2004/08/12 10:39:44  ap
   no message

   Revision 1.4  2004/08/09 06:43:50  ap
   no message

   Revision 1.3  2004/06/28 06:26:52  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
