//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/geotiff/GeoTiffWriter.java,v 1.11 2006/08/06 20:56:50 poth Exp $
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;

import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;

/**
 * This class is for writing GeoTIFF files from any java.awt.image. At that
 * time, only writing the Bounding Box is available.
 * 
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </A>
 * @author last edited by: $Author: poth $
 * @version 2.0. $Revision: 1.11 $, $Date: 2006/08/06 20:56:50 $
 * @since 2.0
 */
public class GeoTiffWriter {

    private ArrayList tiffields = null;
    private HashMap geoKeyDirectoryTag = null;
    private BufferedImage bi = null;

    /**
     * private constructor. called from any other public constructor
     */
    private GeoTiffWriter(Envelope envelope, double resx, double resy, CoordinateSystem crs)  {
        this.tiffields = new ArrayList();
        this.geoKeyDirectoryTag = new HashMap();
        int[] header = { 1, 2, 0 };
        // sets the header. this key must be overwritten in the write-method.
        addKeyToGeoKeyDirectoryTag(1, header);
        // sets the boundingbox (with envelope and resolution)
        setBoxInGeoTIFF(envelope, resx, resy);
        // sets the CoordinateSystem
        // TODO
        //setCoordinateSystem(crs);
    }

    /**
     * creates an GeoTiffWriter instance from an java.awt.image.
     * 
     * 
     * @param image
     *            the image, to be transformed to a GeoTIFF.
     * @param envelope
     *            the BoundingBox, the GeoTIFF should have
     * @param resx
     *            The X-Resolution
     * @param resy
     *            The Y-Resolution
     */
    public GeoTiffWriter(BufferedImage image, Envelope envelope,
            double resx, double resy, CoordinateSystem crs) {
        this(envelope, resx, resy, crs);
        this.bi = image;
    }

    /**
     * creates an GeoTiffWriter instance. TODO
     * 
     * @param img
     * @param envelope
     * @param resx
     * @param resy
     */
    public GeoTiffWriter(byte[][] img, Envelope envelope, double resx,
            double resy, CoordinateSystem crs) {
        // TODO constructor byte[][]
        this(envelope, resx, resy, crs);        
    }

    /**
     * creates an GeoTiffWriter instance. TODO
     * 
     * @param img
     * @param envelope
     * @param resx
     * @param resy
     */
    public GeoTiffWriter(short[][] img, Envelope envelope, double resx,
            double resy, CoordinateSystem crs)  {
        this(envelope, resx, resy, crs);
    }

    /**
     * creates an GeoTiffWriter instance. TODO
     * 
     * @param img
     * @param envelope
     * @param resx
     * @param resy
     */
    public GeoTiffWriter(float[][] img, Envelope envelope, double resx,
            double resy, CoordinateSystem crs) {
        // TODO constructor float[][]
        this(envelope, resx, resy, crs);

    }

    /**
     * returns the GeoKeys as an array of Tiff Fields.
     * 
     * @return an array of TIFFFields
     */
    private TIFFField[] getGeoTags() {
        TIFFField[] extraFields = null;

        if (this.tiffields != null && this.tiffields.size() > 0) {
            extraFields = new TIFFField[this.tiffields.size()];
            for (int i = 0; i < extraFields.length; i++) {
                extraFields[i] = (TIFFField) this.tiffields.get(i);
            }
        }
        return extraFields;
    }

    /**
     * gets the GeoKeyDirectoryTag as a chararrary.
     * 
     * @return
     */
    private char[] getGeoKeyDirectoryTag() {
        char[] ch = null;

        // check, if it contains more fields than the header
        if (this.geoKeyDirectoryTag.size() > 1) {
            ch = new char[this.geoKeyDirectoryTag.size() * 4];
            Set set = this.geoKeyDirectoryTag.keySet();
            Object[] o = set.toArray();

            Integer keyID = null;
            int[] temparray = new int[3];

            // o.length is equals this.geoKeyDirectoryTag.size()
            for (int i = 0; i < o.length; i++) {
                // get the key-ID from the ObjectArray 'o'
                keyID = (Integer) o[i];
                // get the values of the HashMap (int[]) at the key keyID
                temparray = (int[]) this.geoKeyDirectoryTag.get(keyID);
                ch[i * 4] = (char) keyID.intValue();
                ch[i * 4 + 1] = (char) temparray[0];
                ch[i * 4 + 2] = (char) temparray[1];
                ch[i * 4 + 3] = (char) temparray[2];
            }
        }

        return ch;
    }

    /**
     * 
     * @param key
     * @param values
     */
    private void addKeyToGeoKeyDirectoryTag(int key, int[] values) {
        this.geoKeyDirectoryTag.put(new Integer(key), values);
    }

    /**
     * Writes the GeoTIFF as a BufferedImage to an OutputStream. The
     * OutputStream isn't closed after the method.
     * 
     * @param os
     *            the output stream, which has to be written.
     * @throws IOException
     */
    public void write(OutputStream os) throws IOException {
        if (this.geoKeyDirectoryTag.size() > 1) {
            // overwriter header with *real* size of GeoKeyDirectoryTag
            int[] header = { 1, 2, this.geoKeyDirectoryTag.size() - 1 };
            addKeyToGeoKeyDirectoryTag(1, header);

            char[] ch = getGeoKeyDirectoryTag();

            // int tag, int type, int count, java.lang.Object data
            TIFFField geokeydirectorytag = 
                new TIFFField( GeoTiffTag.GeoKeyDirectoryTag, TIFFField.TIFF_SHORT, ch.length, ch);
            this.tiffields.add( geokeydirectorytag );
        }

        // get the geokeys
        TIFFField[] tiffields_array = getGeoTags();

        TIFFEncodeParam encodeParam = new TIFFEncodeParam();
        if (tiffields_array != null && tiffields_array.length > 0) {
            encodeParam.setExtraFields(tiffields_array);
        }
        TIFFImageEncoder encoder = new TIFFImageEncoder(os, encodeParam);

        // void encoder( java.awt.image.RenderedImage im )
        encoder.encode(bi);
    }

    // ************************************************************************
    // BoundingBox
    // ************************************************************************
    /**
     * description: Extracts the GeoKeys of the GeoTIFF. The Following Tags will
     * be extracted(http://www.remotesensing.org/geotiff/spec/geotiffhome.html):
     * <ul>
     * <li>ModelPixelScaleTag = 33550 (SoftDesk)
     * <li>ModelTiepointTag = 33922 (Intergraph)
     * </ul>
     * implementation status: working
     */
    private void setBoxInGeoTIFF(Envelope envelope, double resx, double resy) {

        double[] resolution = { resx, resy, 0.0 };
        // ModelPixelScaleTag:
        // Tag = 33550
        // Type = DOUBLE (IEEE Double precision)
        // N = 3
        // Owner: SoftDesk
        TIFFField modelPixelScaleTag = 
            new TIFFField( GeoTiffTag.ModelPixelScaleTag, TIFFField.TIFF_DOUBLE, 3, resolution);

        this.tiffields.add(modelPixelScaleTag);

        // ModelTiepointTag:
        // calculate the first points for the upper-left corner {0,0,0} of the
        // tiff
        double tp_01x = 0.0; // (0, val1)
        double tp_01y = 0.0; // (1, val2)
        double tp_01z = 0.0; // (2) z-value. not needed

        // the real-world coordinates for the upper points (tp_01.)
        // these are the unknown variables which have to be calculated.
        double tp_02x = 0.0; // (3, val4)
        double tp_02y = 0.0; // (4, val5)
        double tp_02z = 0.0; // (5) z-value. not needed

        double xmin = envelope.getMin().getX();
        double ymax = envelope.getMax().getY();

        // transform this equation: xmin = ?[val4] - ( tp_01x * resx )
        tp_02x = xmin + (tp_01x * resx);

        // transform this equation: ymax = ?[val5] + ( tp_01y * resy )
        tp_02y = ymax + (tp_01y * resy);

        double[] tiepoint = { tp_01x, tp_01y, tp_01z, tp_02x, tp_02y, tp_02z };

        // ModelTiepointTag:
        // Tag = 33922 (8482.H)
        // Type = DOUBLE (IEEE Double precision)
        // N = 6*K, K = number of tiepoints
        // Alias: GeoreferenceTag
        // Owner: Intergraph
        TIFFField modelTiepointTag = 
            new TIFFField(GeoTiffTag.ModelTiepointTag, TIFFField.TIFF_DOUBLE, 6, tiepoint);

        this.tiffields.add(modelTiepointTag);
    }

    // ************************************************************************
    // CoordinateSystem
    // ************************************************************************
    /**
     *  
     */
    private void setCoordinateSystem(CoordinateSystem crs) throws GeoTiffException {
/*
            // add GTModelTypeGeoKey
            int[] values_GTModelTypeGeoKey = { 0, 1, 2 };
            addKeyToGeoKeyDirectoryTag(GeoTiffKey.GTModelTypeGeoKey,
                    values_GTModelTypeGeoKey);

            try {             
                String horizontalDatum = crs.getHorizontalDatum().getName();

                if ( Geographic_CS_Codes.contains_Geodectic_Datum_Code(horizontalDatum ) ) {

                    int[] geographictypegeokey = { 0, 1,
                            Geographic_CS_Codes.getGeogrpahicCSTypeCode(horizontalDatum) };
                    addKeyToGeoKeyDirectoryTag(GeoTiffKey.GeographicTypeGeoKey,
                            geographictypegeokey);

                } else {
                    throw new GeoTiffException( "Error in determining Horizontal Datum Name:\n"
                                    + ' ' + horizontalDatum
                                    + " ist not registered in Geodetic Datum Codes.");
                }

            } catch (Exception e) {
                throw new GeoTiffException("RemoteException: " + e.getMessage());
            }
*/            

    }

}

/*
 * ****************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: GeoTiffWriter.java,v $
 * Revision 1.11  2006/08/06 20:56:50  poth
 * TODO set
 *
 * Revision 1.10  2006/07/26 10:25:58  poth
 * never thrown exceptions removed
 *
 * Revision 1.9  2006/05/01 20:15:27  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/05 17:41:07  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/01/25 10:39:39  poth
 * *** empty log message ***
 *
 * Revision 1.3  2005/12/06 13:45:20  poth
 * System.out.println substituted by logging api
 *
 * Revision 1.2  2005/01/18 22:08:54  poth
 * no message
 *
 * Revision 1.2  2004/07/20 15:34:30  ap
 * no message
 *
 * Revision 1.6  2004/07/16 08:48:01  axel_schaefer
 * Wrong check of GeoKeyDirectoryTag size.
 * Revision 1.5 2004/07/16 07:04:53 poth no message
 * 
 * Revision 1.4 2004/07/15 15:33:43 axel_schaefer no message Revision 1.3
 * 2004/07/15 09:57:10 axel_schaefer secure saving at noontime Revision 1.2
 * 2004/07/12 08:55:48 poth no message
 * 
 * Revision 1.1 2004/07/08 15:35:22 axel_schaefer first version
 * 
 * ****************************************************************************
 */