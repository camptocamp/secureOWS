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
package org.deegree.graphics.transformation;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;

/**
 * <code>GeoTransformInterface</code> declares the methods which have to * be implemented by each class that executes a geographical coordinat * transformation. *  * @author Andreas Poth poth@lat-lon.de * @version 28.12.2000
 */

public interface GeoTransform {
    /**
     *
     *
     * @param xdest 
     *
     * @return 
     */
    public double getSourceX( double xdest );

    /**
     *
     *
     * @param xsource 
     *
     * @return 
     */
    public double getDestX( double xsource );

    /**
     *
     *
     * @param ydest 
     *
     * @return 
     */
    public double getSourceY( double ydest );

    /**
     *
     *
     * @param ysource 
     *
     * @return 
     */
    public double getDestY( double ysource );

    /**
     * @param rect
     * 
     * @uml.property name="sourceRect"
     */
    public void setSourceRect(Envelope rect);


    /**
     *
     *
     * @param xMin 
     * @param yMin 
     * @param xMax 
     * @param yMax 
     */
    public void setSourceRect( double xMin, double yMin, double xMax, double yMax );

    /**
     * @return
     * 
     * @uml.property name="sourceRect"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    public Envelope getSourceRect();

    /**
     * @param rect
     * 
     * @uml.property name="destRect"
     */
    public void setDestRect(Envelope rect);


    /**
     *
     *
     * @param xMin 
     * @param yMin 
     * @param xMax 
     * @param yMax 
     */
    public void setDestRect( double xMin, double yMin, double xMax, double yMax );

    /**
     * @return
     * 
     * @uml.property name="destRect"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    public Envelope getDestRect();

    /**
     *
     *
     * @param point 
     *
     * @return 
     */
    public Position getSourcePoint( Position point );

    /**
     *
     *
     * @param point 
     *
     * @return 
     */
    public Position getDestPoint( Position point );
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeoTransform.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
