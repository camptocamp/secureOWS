// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/DefaultExtension.java,v 1.6 2006/04/06 20:25:27 poth Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.opengis.parameter.ParameterValueGroup;

/**
 * Default implementation of WCS CoverageDescription for handling
 * informations about coverage data backend. 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public class DefaultExtension implements Extension {
    
    protected TreeSet resolutions = null;
    protected double minScale = 0;
    protected double maxScale = 9E99;
    private String type = null;

    /**
     * constructor initializing an empty <tt>Extension</tt>
     */
    public DefaultExtension(String type) throws UnknownCVExtensionException {
        resolutions = new TreeSet();
        setType(type);
    }
    
    /**
     * initializing the <tt>Extension</tt> with the passed <tt>Resolution</tt>s
     * @param resolutions
     */
    public DefaultExtension(String type, Resolution[] resolutions) 
                                                    throws UnknownCVExtensionException  {        
        this( type );
        minScale = 9E99;
        maxScale = 0;
        for (int i = 0; i < resolutions.length; i++) {
            this.resolutions.add( resolutions[i] );
            if ( resolutions[i].getMinScale() < minScale ) {
                minScale = resolutions[i].getMinScale();
            }
            if ( resolutions[i].getMaxScale() > maxScale ) {
                maxScale = resolutions[i].getMaxScale();
            }
        }        
    }

    /**
     * returns the type of the coverage source that is described be
     * an extension
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * returns the type of the coverage source that is described be
     * an extension. Valid types are:
     * <ul>
     *  <li>shapeIndexed
     *  <li>nameIndexed
     *  <li>file
     * </ul>
     * This list may be extended in future versions of deegree
     * @param type
     * @throws UnknownCVExtensionException
     */
    public void setType(String type) throws UnknownCVExtensionException {
        if ( type == null || (!type.equals("shapeIndexed") && 
            !type.equals("nameIndexed") && !type.equals("file") && 
            !type.equals("OracleGeoRaster") ) ) {
            throw new UnknownCVExtensionException("unknown extension type: " + type);
        }
        this.type = type;
    }

    /**
     * returns the minimum scale of objects that are described by an
     * <tt>Extension</tt> object
     * 
     * @return
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * returns the maximum scale of objects that are described by an
     * <tt>Extension</tt> object
     * 
     * @return
     */
    public double getMaxScale() {
        return maxScale;
    }

    /**
     * returns all <tt>Resolution</tt>s . If no
     * <tt>Resolution</tt> can be found for the passed scale an empty
     * array will be returned.
     * 
     * @param scale scale the returned resolutions must fit
     * 
     * @return <tt>Resolution</tt>s matching the passed scale
     */
    public Resolution[] getResolutions() {
        return (Resolution[]) resolutions.toArray(new Resolution[resolutions
            .size()]);
    }

    
    /**
     * returns the <tt>Resolution</tt>s matching the passed scale. If no
     * <tt>Resolution</tt> can be found for the passed scale an empty
     * array will be returned.
     * 
     * @param scale scale the returned resolutions must fit
     * 
     * @return <tt>Resolution</tt>s matching the passed scale
     */
    public Resolution[] getResolutions(double scale) {
        if ( scale < minScale || scale > maxScale ) {
            return new Resolution[0];
        }
        List list = new ArrayList();
        Iterator iterator = resolutions.iterator();
        while (iterator.hasNext()) {
            Resolution res = (Resolution)iterator.next();
            if ( scale >= res.getMinScale() && scale <= res.getMaxScale() ) {                
                list.add( res );
            }
        }
        return (Resolution[])list.toArray( new Resolution[list.size()] );
    }

    /**
     * @see org.deegree.ogcwebservices.wcs.configuration.Extension#getResolutions(org.opengis.parameter.ParameterValueGroup)
     */
    public Resolution[] getResolutions(ParameterValueGroup parameter) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @param resolution
     */
    public void addResolution(Resolution resolution) {
        // TODO Auto-generated method stub
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: DefaultExtension.java,v $
   Revision 1.6  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2006/02/28 09:45:33  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.6  2004/07/19 06:20:01  ap
   no message

   Revision 1.5  2004/07/14 06:52:48  ap
   no message

   Revision 1.4  2004/05/31 07:37:45  ap
   no message

   Revision 1.3  2004/05/28 06:02:57  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
