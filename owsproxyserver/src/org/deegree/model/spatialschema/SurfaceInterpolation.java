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

package org.deegree.model.spatialschema;

/**
*
* Defining the different kind of surface interpolation known by
* the iso geometry modell. 
*
* <p>-----------------------------------------------------</p>
*
* @author Andreas Poth
* @version $Revision: 1.5 $ $Date: 2006/07/12 14:46:15 $
* <p>
*/ 

public interface SurfaceInterpolation {

    public static final int NONE = 0;
    public static final int PLANAR = 1;
    public static final int SPHERICAL = 2;
    public static final int ELLIPTICAL = 3;
    public static final int CONIC = 4;
    public static final int TIN = 5;
    public static final int BILINEAR = 6;
    public static final int BIQUADRATIC = 7;
    public static final int BICUBIC = 8;
    public static final int POLYNOMIALSPLINE = 9;
    public static final int RATIONALSPLINE = 10;
    public static final int TRIANGULATEDSOLINE = 11;

    int getValue();

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SurfaceInterpolation.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
