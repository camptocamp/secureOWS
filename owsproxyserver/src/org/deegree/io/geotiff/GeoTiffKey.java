//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/geotiff/GeoTiffKey.java,v 1.4 2006/04/06 20:25:29 poth Exp $
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
package org.deegree.io.geotiff;

/**
 * This class represents the possible GeoTIFF keys (from 1024 to 4099) in the
 * GeoKeyDirectoryTag (34735).
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </A>
 * @author last edited by: $Author: poth $
 * @version 2.0. $Revision: 1.4 $, $Date: 2006/04/06 20:25:29 $
 * @since
 */
public class GeoTiffKey {

    public static final int GTModelTypeGeoKey = 1024;
    public static final int GTRasterTypeGeoKey = 1025;
    public static final int GTCitationGeoKey = 1026;
    public static final int GeographicTypeGeoKey = 2048;
    public static final int GeogCitationGeoKey = 2049;
    public static final int GeogGeodeticDatumGeoKey = 2050;
    public static final int GeogPrimeMeridianGeoKey = 2051;
    public static final int GeogLinearUnitsGeoKey = 2052;
    public static final int GeogLinearUnitSizeGeoKey = 2053;
    public static final int GeogAngularUnitsGeoKey = 2054;
    public static final int GeogAngularUnitSizeGeoKey = 2055;
    public static final int GeogEllipsoidGeoKey = 2056;
    public static final int GeogSemiMajorAxisGeoKey = 2057;
    public static final int GeogSemiMinorAxisGeoKey = 2058;
    public static final int GeogInvFlatteningGeoKey = 2059;
    public static final int GeogAzimuthUnitsGeoKey = 2060;
    public static final int GeogPrimeMeridianLongGeoKey = 2061;
    public static final int ProjectedCSTypeGeoKey = 3072;
    public static final int PCSCitationGeoKey = 3073;
    public static final int ProjectionGeoKey = 3074;
    public static final int ProjCoordTransGeoKey = 3075;
    public static final int ProjLinearUnitsGeoKey = 3076;
    public static final int ProjLinearUnitSizeGeoKey = 3077;
    public static final int ProjStdParallel1GeoKey = 3078;
    // public static final int ProjStdParallelGeoKey =$ProjStdParallel1GeoKey;
    public static final int ProjStdParallel2GeoKey = 3079;
    public static final int ProjNatOriginLongGeoKey = 3080;
    // public static final int ProjOriginLongGeoKey =$ProjNatOriginLongGeoKey
    public static final int ProjNatOriginLatGeoKey = 3081;
    // public static final int ProjOriginLatGeoKey =$ProjNatOriginLatGeoKey
    public static final int ProjFalseEastingGeoKey = 3082;
    public static final int ProjFalseNorthingGeoKey = 3083;
    public static final int ProjFalseOriginLongGeoKey = 3084;
    public static final int ProjFalseOriginLatGeoKey = 3085;
    public static final int ProjFalseOriginEastingGeoKey = 3086;
    public static final int ProjFalseOriginNorthingGeoKey = 3087;
    public static final int ProjCenterLongGeoKey = 3088;
    public static final int ProjCenterLatGeoKey = 3089;
    public static final int ProjCenterEastingGeoKey = 3090;
    public static final int ProjCenterNorthingGeoKey = 3091;
    public static final int ProjScaleAtNatOriginGeoKey = 3092;
    // public static final int ProjScaleAtOriginGeoKey
    // =$ProjScaleAtNatOriginGeoKey
    public static final int ProjScaleAtCenterGeoKey = 3093;
    public static final int ProjAzimuthAngleGeoKey = 3094;
    public static final int ProjStraightVertPoleLongGeoKey = 3095;
    public static final int VerticalCSTypeGeoKey = 4096;
    public static final int VerticalCitationGeoKey = 4097;
    public static final int VerticalDatumGeoKey = 4098;
    public static final int VerticalUnitsGeoKey = 4099;

    /**
     * private default constructor prevents instantiation
     */
    private GeoTiffKey() {
    }

}

/*
 * ****************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: GeoTiffKey.java,v $
 * Revision 1.4  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2005/01/05 10:36:34  poth
 * no message
 *
 * Revision 1.1  2004/07/16 07:03:39  ap
 * no message
 *
 * Revision 1.2  2004/07/15 09:57:23  axel_schaefer
 * no message
 *
 * ****************************************************************************
 */