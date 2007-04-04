// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/Extension.java,v 1.6 2006/04/06 20:25:27 poth Exp $
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

import org.opengis.parameter.ParameterValueGroup;

/**
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public interface Extension {
    
    final String FILEBASED = "file";
    final String NAMEINDEXED = "nameIndexed";
    final String SHAPEINDEXED = "shapeIndexed";
    final String ORACLEGEORASTER = "OracleGeoRaster";
    
    /**
     * returns the type of the coverage source that is described be
     * an extension
     * @return
     */
    String getType();
    
    /**
     * returns the minimum scale of objects that are described by an
     * <tt>Extension</tt> object
     * 
     * @return
     */
    double getMinScale();
    
    /**
     * returns the maximum scale of objects that are described by an
     * <tt>Extension</tt> object
     * 
     * @return
     */
    double getMaxScale();
    
    /**
     * returns all <tt>Resolution</tt>s. If no
     * <tt>Resolution</tt> can be found for the passed scale an empty
     * array will be returned.
     * 
     * @param scale scale the returned resolutions must fit
     * 
     * @return <tt>Resolution</tt>s matching the passed scale
     */
    Resolution[] getResolutions();
    
    /**
     * returns the <tt>Resolution</tt>s matching the passed scale. If no
     * <tt>Resolution</tt> can be found for the passed scale an empty
     * array will be returned.
     * 
     * @param scale scale the returned resolutions must fit
     * 
     * @return <tt>Resolution</tt>s matching the passed scale
     */
    Resolution[] getResolutions(double scale);
    
    /**
     * returns the <tt>Resolution</tt>s matching the passed parameters. 
     * If no <tt>Resolution</tt> can be found for the passed parameter 
     * an empty array will be returned.
     * 
     * @return <tt>Resolution</tt>s matching the passed scale
     */
    Resolution[] getResolutions(ParameterValueGroup parameter);
    
    /**
     * @param resolution
     */
    void addResolution(Resolution resolution);

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Extension.java,v $
   Revision 1.6  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2006/02/23 07:45:24  poth
   *** empty log message ***

   Revision 1.2  2006/02/05 20:33:09  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:32:33  poth
   no message

   Revision 1.6  2004/07/19 06:20:01  ap
   no message

   Revision 1.5  2004/07/14 06:52:48  ap
   no message

   Revision 1.4  2004/07/05 06:15:00  ap
   no message

   Revision 1.3  2004/05/28 06:02:57  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
