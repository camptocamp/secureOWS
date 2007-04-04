//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/operation/GetView.java,v 1.24 2006/11/27 15:42:55 bezema Exp $
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
 Aennchenstraße 19
 53177 Bonn
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

package org.deegree.ogcwebservices.wpvs.operation;

import java.awt.Color;
import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.StringTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * This Class handles a kvp request from a client and stores it's values.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.24 $, $Date: 2006/11/27 15:42:55 $
 * 
 * @since 2.0
 */
public class GetView extends WPVSRequestBase {

    /**
     * 
     */
    private static final long serialVersionUID = 3147456903146907261L;

    private static final ILogger LOG = LoggerFactory.getLogger( GetView.class );

    private final List<String> datasets;

    private final float quality;

    /**
     * using deegree's Position
     */
    private final Point3d pointOfInterest;

    private final float pitch;

    private final float yaw;

    private final float roll;

    private final float distance;

    private final float angleOfView;

    private final boolean transparent;

    private final Dimension imageDimension;

    private final String outputFormat;

    private final Color backgroundColor;

    private final String exceptionFormat;

    private final String elevationModel;

    private final Envelope boundingBox;

    private final CoordinateSystem crs;

    private double farClippingPlane;

    private GetView( String version, String id, List<String> datasets, String elevationModel,
                    float quality, Position pointOfInterest, Envelope bbox, CoordinateSystem crs,
                    float pitch, float yaw, float roll, float distance, float angleOfView,
                    String outputFormat, Color backgroundColor, boolean transparent,
                    Dimension imageDimension, String exceptionFormat, double farClippingPlane,
                    Map<String, String> vendorSpecificParameter ) {
        this(
              version,
              id,
              datasets,
              elevationModel,
              quality,
              new Point3d( pointOfInterest.getX(), pointOfInterest.getY(), pointOfInterest.getZ() ),
              bbox, crs, pitch, yaw, roll, distance, angleOfView, outputFormat, backgroundColor,
              transparent, imageDimension, exceptionFormat, farClippingPlane,
              vendorSpecificParameter );
    }

    /**
     * Trusted constructor. No parameter validity is performed. This is delegated to the factory
     * method createGeMap.
     * 
     * TODO the list of pars is too long, should break up into smaller classes, e.g. pars for
     * perspective output, etc.
     * 
     * @param version
     * @param id
     * @param datasets
     * @param elevationModel
     * @param quality
     * @param pointOfInterest
     * @param bbox
     * @param crs
     * @param pitch
     * @param yaw
     * @param roll
     * @param distance
     * @param angleOfView
     * @param outputFormat
     * @param backgroundColor
     * @param transparent
     * @param imageDimension
     * @param exceptionFormat
     * @param farClippingPlane
     * @param vendorSpecificParameter
     */
    private GetView( String version, String id, List<String> datasets, String elevationModel,
                    float quality, Point3d pointOfInterest, Envelope bbox, CoordinateSystem crs, float pitch,
                    float yaw, float roll, float distance, float angleOfView, String outputFormat,
                    Color backgroundColor, boolean transparent, Dimension imageDimension,
                    String exceptionFormat, double farClippingPlane,
                    Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
        this.datasets = datasets;
        this.elevationModel = elevationModel;

        this.quality = quality;
        this.pointOfInterest = pointOfInterest;
        this.boundingBox = bbox;
        this.crs = crs;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.distance = distance;
        this.angleOfView = angleOfView;
        this.outputFormat = outputFormat;
        this.backgroundColor = backgroundColor;
        this.transparent = transparent;
        this.imageDimension = imageDimension;
        this.exceptionFormat = exceptionFormat;
        this.farClippingPlane = farClippingPlane;

    }

    /**
     * Factory method to create an instance of GetView from teh parameters in <code>model</code>
     * 
     * @param model
     *            a map containing request parameters and values
     * @return a new instance of GetView
     * @throws InconsistentRequestException
     *             if a mandatory parameter is missing
     * @throws InvalidParameterValueException
     *             if a parameter has an illegal value
     */
    public static GetView create( Map<String, String> model )
                            throws InconsistentRequestException, InvalidParameterValueException {

        // TODO throw a proper exception, the InconsistentRequestException doesn't cover all cases

        LOG.entering();

        // not needed anymore
        model.remove( "REQUEST" );

        String id = model.remove( "ID" );

        /*
         * TODO check if this is right WPVSConfiguration configuration = (WPVSConfiguration)
         * model.remove( "CAPABILITIES" ); if ( configuration == null ){ throw new RuntimeException (
         * "Working site: you forgot to add config to model -> " + "see how this is done in wms" ); }
         */

        String version = model.remove( "VERSION" );
        if ( version == null ) {
            throw new InconsistentRequestException( "'VERSION' value must be set" );
        }

        // FORMAT
        String format = model.remove( "OUTPUTFORMAT" );
        if ( format == null ) {
            throw new InconsistentRequestException( "OUTPUTFORMAT value must be set" );
        }
        try {
            format = URLDecoder.decode( format, CharsetUtils.getSystemCharset() );
        } catch ( UnsupportedEncodingException e1 ) {
            LOG.logError( e1.getLocalizedMessage(), e1 );
        }
        if ( !MimeTypeMapper.isKnownImageType( format ) ) {
            throw new InvalidParameterValueException(
                                                      StringTools.concat( 50, format,
                                                                          " is not a valid image/result format" ) );
        }

        // TRANSPARENCY
        boolean transparency = false;
        String tp = model.remove( "TRANSPARENT" );
        if ( tp != null ) {
            transparency = tp.toUpperCase().trim().equals( "TRUE" );
        }

        if ( transparency
             && ( format.equals( "image/jpg" ) || format.equals( "image/jpeg" )
                  || format.equals( "image/bmp" ) || format.equals( "image/tif" ) || format.equals( "image/tiff" ) ) ) {

            throw new InconsistentRequestException(
                                                    StringTools.concat(
                                                                        100,
                                                                        "TRANSPARENCY=true is inconsistent with OUTPUTFORMAT=",
                                                                        format,
                                                                        ".Valid transparent formats are 'image/gif' ",
                                                                        "and 'image/png'." ) );
        }

        // width
        String tmp = model.remove( "WIDTH" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'WIDTH' value must be set" );
        }
        int width = 0;
        try {
            width = Integer.parseInt( tmp );
        } catch ( NumberFormatException e ) {
            throw new InconsistentRequestException( "WIDTH must be a valid integer number" );
        }

        tmp = model.remove( "HEIGHT" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'HEIGHT' value must be set" );
        }
        int height = 0;
        try {
            height = Integer.parseInt( tmp );
        } catch ( NumberFormatException e ) {
            throw new InconsistentRequestException( "HEIGHT must be a valid integer number" );
        }

        if ( width < 0 || height < 0 ) {
            throw new InconsistentRequestException( "WIDTH and HEIGHT must be >= 0" );
        }
        Dimension imgDimension = new Dimension( width, height );

        Color bgColor = Color.white;

        tmp = model.remove( "BACKGROUNDCOLOR" );
        if ( tmp != null ) {
            try {
                bgColor = Color.decode( tmp );
            } catch ( NumberFormatException e ) {
                throw new InconsistentRequestException(
                                                        StringTools.concat(
                                                                            100,
                                                                            "The BACKGROUNDCOLOR '",
                                                                            tmp,
                                                                            "' does not denote a valid hexadecimal color." ) );
            }
        }

        String elevModel = model.remove( "ELEVATIONMODEL" );
        /*
         * if ( elevModel == null ) { throw new InconsistentRequestException( "'ELEVATIONMODEL'
         * value must be set" ); }
         */
        if ( elevModel != null ) {
            elevModel = elevModel.trim();
            if ( elevModel.length() == 0 ) {
                throw new InconsistentRequestException(
                                                        "ELEVATIONMODEL cannot contain space characters only or be empty" );
            }
        }

        tmp = model.remove( "AOV" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'AOV' value must be set" );
        }

        float aov = 0;
        try {
            aov = (float) Math.toRadians( Float.parseFloat( tmp ) );
            /**
             * checking for > 0 || < 180
             */
            if ( ( aov <= 0 ) || ( aov >= 3.141592653 ) ) {
                throw new InvalidParameterValueException(
                                                          "AOV value must be a number between 0° and 180°" );
            }
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException( "AOV couldn't parse the aov value" );
        }

        tmp = model.remove( "ROLL" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'ROLL' value must be set" );
        }
        final float roll;
        try {
            /**
             * checking for > 360 && < 360
             */
            float value = Float.parseFloat( tmp ) % 360;
            if ( value < 0 )
                value += 360;
            roll = (float) Math.toRadians( value );
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException( "ROLL value must be a number" );
        }

        tmp = model.remove( "DISTANCE" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'DISTANCE' value must be set." );
        }

        final float distance;
        String mesg = "DISTANCE must be a number >= 0.";
        try {
            distance = Float.parseFloat( tmp );
            if ( distance < 0 ) {
                throw new InvalidParameterValueException( mesg );
            }
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException( mesg );
        }

        tmp = model.remove( "PITCH" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'PITCH' value must be set." );
        }
        float pitch = 0;
        try {
            pitch = (float) Math.toRadians( Float.parseFloat( tmp ) );
            if ( ( pitch < -1.570796327 ) || ( pitch > 1.570796327 ) ) {
                throw new InvalidParameterValueException(
                                                          "PITCH value must be a number between -90° and 90°" );
            }
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException(
                                                      "PITCH value must be a number between -90° and 90°" );
        }
        // pitch and aov can not have the same value (the footprint will be qaudratic) therefor a
        // little check and correction.
        if ( Math.abs( pitch - aov ) <= 0.01 ) {
            pitch -= 0.5f;
        }

        tmp = model.remove( "YAW" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "'YAW' value must be set." );
        }
        final float yaw;
        try {
            float tmpYaw = Float.parseFloat( tmp ) % 360;
            if ( tmpYaw < 0 )
                tmpYaw += 360;
            // YAW == 270 -> OutOfMem Error
            // if ( tmpYaw > 89.5 && tmpYaw < 90.5 ) {
            // tmpYaw = 91;
            // } else if ( tmpYaw > 269.5 && tmpYaw < 270.5 ) {
            // tmpYaw = 271;
            // }
            // [UT] 06.06.2005 splitter doesn't work fine for 0 (or 360) and 180
            // if ( tmpYaw % 180 == 0 ) {
            // tmpYaw += 0.5;
            // }
            yaw = (float) Math.toRadians( tmpYaw );

        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException( "YAW value must be a number" );
        }

        tmp = model.remove( "POI" );
        if ( tmp == null ) {
            throw new InconsistentRequestException( "POI value is missing." );
        }
        mesg = "POI value must denote a number tuple with valid x,y,z values, for example '123.45,678.90,456.123'";

        try {
            tmp = URLDecoder.decode( tmp, CharsetUtils.getSystemCharset() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InconsistentRequestException( e.getLocalizedMessage() );
        }
        String[] xyz = tmp.split( "," );
        if ( xyz.length != 3 ) {
            throw new InvalidParameterValueException( mesg );
        }
        Position poi;
        double[] p = new double[3];
        try {
            p[0] = Double.parseDouble( xyz[0] );
            p[1] = Double.parseDouble( xyz[1] );
            p[2] = Double.parseDouble( xyz[2] );

        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidParameterValueException( mesg );
        }
        poi = GeometryFactory.createPosition( p );

        String crsString = model.remove( "CRS" );
        CoordinateSystem crs = null;
        if ( crsString == null ) {
            throw new InconsistentRequestException( "CRS parameter is missing." );
        }
        try {
            crsString = URLDecoder.decode( crsString, CharsetUtils.getSystemCharset() );
            crs = CRSFactory.create( crsString );
        } catch ( UnsupportedEncodingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
        } catch ( UnknownCRSException ucrse ) {
            LOG.logError( ucrse.getLocalizedMessage(), ucrse );
            throw new InvalidParameterValueException( ucrse.getMessage() );
        }

        String datasetsString = model.remove( "DATASETS" );
        if ( datasetsString == null ) {
            throw new InconsistentRequestException( "'DATASETS' value must be set" );
        }

        datasetsString = datasetsString.trim();
        if ( datasetsString.length() == 0 ) {
            throw new InconsistentRequestException(
                                                    "'DATASETS' cannot contain space characters only or be empty" );
        }

        String[] datasets = datasetsString.split( "," );
        if ( datasetsString.length() == 0 ) {
            throw new InconsistentRequestException(
                                                    "'DATASETS' must contain at least one dataset name" );
        }

        List<String> datasetList = Arrays.asList( datasets );

        String boxstring = model.remove( "BOUNDINGBOX" );
        Envelope boundingBox = null;
        if ( boxstring == null ) {
            throw new InconsistentRequestException( "BOUNDINGBOX value must be set" );
        }

        try {
            boxstring = URLDecoder.decode( boxstring, CharsetUtils.getSystemCharset() );
        } catch ( UnsupportedEncodingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InconsistentRequestException(
                                                    StringTools.concat(
                                                                        100,
                                                                        "Cannot decode BOUNDINGBOX: '",
                                                                        boxstring,
                                                                        " using ",
                                                                        CharsetUtils.getSystemCharset() ) );
        }

        String[] tokens = boxstring.split( "," );
        if ( tokens.length != 4 ) {
            throw new InconsistentRequestException(
                                                    "BOUNDINGBOX value must have a value such as xmin,ymin,xmax,ymax" );
        }

        double minx;
        double maxx;
        double miny;
        double maxy;
        try {
            minx = Double.parseDouble( tokens[0] );
            miny = Double.parseDouble( tokens[1] );
            maxx = Double.parseDouble( tokens[2] );
            maxy = Double.parseDouble( tokens[3] );
        } catch ( NumberFormatException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InconsistentRequestException( "BOUNDINGBOX has an illegal value: "
                                                    + e.getMessage() );
        }

        if ( minx >= maxx ) {
            throw new InvalidParameterValueException( "minx must be less than maxx" );
        }

        if ( miny >= maxy ) {
            throw new InvalidParameterValueException( "miny must be less than maxy" );
        }

        boundingBox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, crs);

        /**
         * Doing some checking of the given request parameters.
         */

        if ( !boundingBox.contains( poi ) ) {
            throw new InconsistentRequestException( "POI (" + poi
                                                    + " )must be inside the Bounding box ("
                                                    + boundingBox + ")" );
        }

        tmp = model.remove( "FARCLIPPINGPLANE" );
        double farClippingPlane = 15000;
        if ( tmp != null ) {
            try {
                farClippingPlane = Double.parseDouble( tmp );
            } catch ( NumberFormatException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new InvalidParameterValueException( "FarClippingPlane must be a number" );
            }
        }

        tmp = model.remove( "QUALITY" );
        float quality = 1f;
        if ( tmp != null ) {
            try {
                quality = Float.parseFloat( tmp );
            } catch ( NumberFormatException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new InvalidParameterValueException( "QUALITY must have a value between [0,1]" );
            }
        }

        String exceptions = model.remove( "EXCEPTIONFORMAT" );
        if ( exceptions == null ) {
            exceptions = "XML";
        }

        // Shouldn't this be checked for the right value ???
        tmp = model.remove( "DATETIME" );
        if ( tmp == null ) {
            tmp = "2006-01-18T16:15:00";
        }
        // org.deegree.framework.util.TimeTools.createCalendar( tmp );

        model.put( "DATETIME", tmp );

        tmp = model.remove( "SCALE" );
        if ( tmp != null ) {
            try {
                Float.parseFloat( tmp );
                model.put( "SCALE", tmp );
            } catch ( NumberFormatException e ) {
                LOG.logError( e.getLocalizedMessage(), e );
                throw new InvalidParameterValueException( e );
            }
        }

        LOG.exiting();

        return new GetView( version, id, datasetList, elevModel, quality, poi, boundingBox, crs,
                            pitch, yaw, roll, distance, aov, format, bgColor, transparency,
                            imgDimension, exceptions, farClippingPlane, model );
    }

    /**
     * @return the requested angleOfView
     */
    public float getAngleOfView() {
        return angleOfView;
    }

    /**
     * @return the requested distance to the poi
     */
    public float getDistance() {
        return distance;
    }

    /**
     * @return the requested dimension of the resultimage
     */
    public Dimension getImageDimension() {
        return imageDimension;
    }

    /**
     * @return the requested pitch (rotation around the x-axis)
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * 
     * @return the point of interest as Point3d
     */
    public Point3d getPointOfInterest() {
        return pointOfInterest;
    }

    /**
     * @return the quality of the textures
     */
    public float getQuality() {
        return quality;
    }

    /**
     * @return the requested roll (rotation around the y-axis)
     */
    public float getRoll() {
        return roll;
    }

    /**
     * @return if the resultimage should be transparent
     */
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * @return the requested yaw (rotation around the z-axis)
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * @return the requested datasets (e.g. layers or features etc.)
     */
    public List<String> getDatasets() {
        return datasets;
    }

    /**
     * @return the requested color of the background
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the requested format of thrown exceptions
     */
    public String getExceptionFormat() {
        return exceptionFormat;
    }

    /**
     * @return the mimetype of the resultimage
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @return the boundingbox of the request
     */
    public Envelope getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return the Coordinate System of the request
     */
    public CoordinateSystem getCrs() {
        return crs;
    }

    /**
     * @return the elevationmodel to be used.
     */
    public String getElevationModel() {
        return elevationModel;
    }

    /**
     * @return Returns the farClippingPlane.
     */
    public double getFarClippingPlane() {
        return farClippingPlane;
    }
    
    /**
     * @param farClippingPlane another clippingPlane  distance.
     */
    public void setFarClippingPlane(double farClippingPlane) {
        this.farClippingPlane = farClippingPlane;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetView.java,v $
 * Changes to this class. What the people have been up to: Revision 1.24  2006/11/27 15:42:55  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.23  2006/11/27 11:45:54  bezema
 * Changes to this class. What the people have been up to: Enabling setting the farclipping plane
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.22  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.21  2006/11/23 11:47:50  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.18
 * 2006/06/05 09:59:25 poth method for conversation from java.awt.Color to its Hex code
 * representation centralized in framework.ColorUtil
 * 
 * Revision 1.17 2006/04/26 12:12:40 taddei bugfix due to change in time tools; some clean up
 * 
 * Revision 1.16 2006/04/06 20:25:31 poth ** empty log message ***
 * 
 * Revision 1.15 2006/03/30 21:20:29 poth ** empty log message ***
 * 
 * Revision 1.14 2006/03/16 11:37:31 taddei added javadoc
 * 
 * Revision 1.13 2006/03/09 09:02:33 taddei elevation model may be null
 * 
 * Revision 1.12 2006/03/07 08:47:19 taddei fixed typo in exception mesg
 * 
 * Revision 1.11 2006/03/02 15:27:58 taddei using now StringTools, exception -> exceptionformat
 * 
 * Revision 1.10 2006/02/24 14:57:27 taddei scale par is aok
 * 
 * Revision 1.9 2006/02/22 17:15:41 taddei utf8
 * 
 * Revision 1.8 2006/02/22 13:34:36 taddei added check if bbox contains poi
 * 
 * Revision 1.7 2006/01/26 14:31:10 taddei added missing parameters, and small bug-fixes
 * 
 * Revision 1.6 2006/01/18 10:21:07 taddei putting wfs service to work
 * 
 * Revision 1.5 2006/01/16 20:36:39 poth ** empty log message ***
 * 
 * Revision 1.4 2005/12/23 12:00:20 taddei fixed typo
 * 
 * Revision 1.3 2005/12/16 15:19:48 taddei better messages, added lost elevationModel
 * 
 * Revision 1.2 2005/12/15 16:54:55 taddei small bugs fixes
 * 
 * Revision 1.1 2005/12/15 15:22:33 taddei added WPVSRequestBase and GetView
 * 
 * 
 **************************************************************************************************/
