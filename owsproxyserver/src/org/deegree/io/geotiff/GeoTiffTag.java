//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/geotiff/GeoTiffTag.java,v 1.4 2006/04/06 20:25:29 poth Exp $
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
 * This class represents the possible GeoTIFF tags (from 256 to 34737)
 * 
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer</A>
 * @author last edited by: $Author: poth $
 * @version 2.0. $Revision: 1.4 $, $Date: 2006/04/06 20:25:29 $
 * @since 
 */
public class GeoTiffTag {

    // general tiff tags
    public static final int ImageWidth = 256;
    public static final int ImageLength = 257;
    public static final int BitsPerSample = 258;
    public static final int Compression = 259;
    public static final int PhotometricInterpretation = 262;
    public static final int FillOrder = 266;
    public static final int DocumentName = 269;
    public static final int ImageDescription = 270;
    public static final int StripOffsets = 273;
    public static final int Orientation = 274;
    public static final int SamplesPerPixel = 277;
    public static final int RowsPerStrip = 278;
    public static final int StripByteCounts = 279;
    public static final int XResolution = 282;
    public static final int YResolution = 283;
    public static final int PlanarConfiguration = 284;
    public static final int ResolutionUnit = 296;
    public static final int Software = 305;
    public static final int ColorMap = 320;
    public static final int TileWidth = 322;
    public static final int TileLength = 323;
    public static final int TileOffsets = 324;
    public static final int TileByteCounts = 325;
    public static final int SampleFormat = 339;
    public static final int SMinSampleValue = 340;
    public static final int SMaxSampleValue = 341;

    // tiff tags used for geotiff
    public static final int ModelPixelScaleTag = 33550;
    public static final int IntergraphMatrixTag = 33920;
    public static final int ModelTiepointTag = 33922;
    public static final int ModelTransformationTag = 34264;
    public static final int GeoKeyDirectoryTag = 34735;
    public static final int GeoDoubleParamsTag = 34736;
    public static final int GeoAsciiParamsTag = 34737;

    /**
     * private default constructor prevents instantiation
     */
    private GeoTiffTag() {
    }
}

/*
 * ****************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: GeoTiffTag.java,v $
 * Revision 1.4  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.1.1.1  2005/01/05 10:36:35  poth
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