//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/ViewPoint.java,v 1.13 2006/12/04 17:06:43 bezema Exp $
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
package org.deegree.ogcwebservices.wpvs.j3d;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.WKTAdapter;
import org.deegree.ogcwebservices.wpvs.operation.GetView;

/**
 * This class represents the view point for a WPVS request. That is, it represents the point where
 * the observer is at, and looking to a target point. An angle of view must be also given.
 * 
 * @author <a href="mailto:lupp@lat-lon.de">Katharina Lupp</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: bezema $
 * @version $Revision: 1.13 $ $Date: 2006/12/04 17:06:43 $
 */
public class ViewPoint {

    private static final ILogger LOG = LoggerFactory.getLogger( ViewPoint.class );

    private static final double rad0 = Math.toRadians( 0 );

    private static final double rad90 = Math.toRadians( 90 );

    private static final double rad180 = Math.toRadians( 180 );

    private static final double rad270 = Math.toRadians( 270 );

    private static final double rad360 = Math.toRadians( 360 );

    private CoordinateSystem crs;

    private Point3d observerPosition;

    private Point3d pointOfInterest;

    private Point3d[] footprint;

    private double angleOfView = 0;

    private double yaw = 0;

    private double pitch = 0;

    private double terrainDistanceToSeaLevel = 0;

    private double viewerToPOIDistance = 0;

    private double farClippingPlane = 0;

    private Transform3D simpleTransform = null;

    private Transform3D viewMatrix = null;

    /**
     * Creates a new instance of ViewPoint_Impl
     * 
     * @param yaw
     *            rotation on the Z-Axis in radians of the viewer
     * @param pitch
     *            rotation on the X-Axis in radians
     * @param viewerToPOIDistance
     *            from the point of interest to the viewersposition
     * @param pointOfInterest
     *            the point of interest
     * @param angleOfView
     * @param farClippingPlane
     *            where the view ends
     * @param distanceToSealevel
     * @param crs
     *            The Coordinatesystem in which the given reside
     */
    public ViewPoint( double yaw, double pitch, double viewerToPOIDistance,
                     Point3d pointOfInterest, double angleOfView, double farClippingPlane,
                     double distanceToSealevel, CoordinateSystem crs ) {
        this.yaw = yaw;
        this.pitch = pitch;
        // pitch and aov can not have the same value (the footprint will be one) therefor a little
        // check and correction        
        if ( Math.abs( pitch - angleOfView ) <= 0.01 )
            this.pitch -= Math.toRadians( 0.5 );
        this.angleOfView = angleOfView;
        this.pointOfInterest = pointOfInterest;

        this.viewerToPOIDistance = viewerToPOIDistance;

        this.farClippingPlane = farClippingPlane;

        this.terrainDistanceToSeaLevel = distanceToSealevel;

        this.crs = crs;

        simpleTransform = new Transform3D();

        viewMatrix = new Transform3D();
        observerPosition = new Point3d();

        footprint = new Point3d[4];

        calcObserverPosition();

    }

    /**
     * @param request
     *            a server request.
     */
    public ViewPoint( GetView request )  {
        this( request.getYaw(), request.getPitch(), request.getDistance(),
              request.getPointOfInterest(), request.getAngleOfView(),
              request.getFarClippingPlane(), 0, request.getCrs() );
    }

    /**
     * @param request
     *            a server request.
     * @param distanceToSeaLevel
     */
    public ViewPoint( GetView request, double distanceToSeaLevel ) {
        this( request.getYaw(), request.getPitch(), request.getDistance(),
              request.getPointOfInterest(), request.getAngleOfView(),
              request.getFarClippingPlane(), distanceToSeaLevel,
              request.getCrs() );
    }

    /**
     * Calculates the observers position for a given pointOfInterest, distance and view direction(
     * as semi polar coordinates, yaw & pitch ). also recalculating the viewmatrix and the
     * footprint, for they are affected by the change of position.
     * 
     */
    private void calcObserverPosition() {

        LOG.entering();
        double z = Math.sin( pitch ) * this.viewerToPOIDistance;

        double groundLength = Math.sqrt( ( viewerToPOIDistance * viewerToPOIDistance ) - ( z * z ) );
        double x = Math.sin( yaw ) * groundLength;
        // -1 if yaw is null, we're looking to the north
        double y = -1 * Math.cos( yaw ) * groundLength;

        LOG.exiting();
        observerPosition.x = pointOfInterest.x + x;
        observerPosition.y = pointOfInterest.y + y;
        observerPosition.z = pointOfInterest.z + z;
        calcFootprint();
        calculateViewMatrix();
    }

    /**
     * calculates the target point of the view for the submitted height
     * 
     * @return the point of interest, given the current viewposition, yaw and pitch.
     */
    @SuppressWarnings("unused")
    private Point3d calcPointOfInterest() {
        LOG.entering();

        double heightAboveGround = observerPosition.z - terrainDistanceToSeaLevel;
        double distance = heightAboveGround / Math.tan( pitch );

        double xDistance = 0; //
        double yDistance = 0; // Math.sqrt( ( distance * distance ) - ( yDistance * yDistance ) );

        Point3d tp = null;

        if ( ( yaw >= rad0 ) && ( yaw <= rad90 ) ) {
            xDistance = Math.sin( yaw ) * distance;
            yDistance = Math.sqrt( ( distance * distance ) - ( xDistance * xDistance ) );
        } else if ( ( yaw > rad90 ) && ( yaw <= rad180 ) ) {
            xDistance = Math.sin( rad90 - yaw ) * distance;
            yDistance = -1 * ( Math.sqrt( ( distance * distance ) - ( xDistance * xDistance ) ) );
        } else if ( ( yaw > rad180 ) && ( yaw <= rad270 ) ) {
            xDistance = -1 * ( Math.sin( rad180 - yaw ) * distance );
            yDistance = -1 * ( Math.sqrt( ( distance * distance ) - ( xDistance * xDistance ) ) );
        } else if ( ( yaw > rad270 ) && ( yaw < rad360 ) ) {
            xDistance = -1 * ( Math.sin( rad270 - yaw ) * distance );
            yDistance = Math.sqrt( ( distance * distance ) - ( xDistance * xDistance ) );
        }
        tp = new Point3d( observerPosition.x + xDistance, observerPosition.y + yDistance,
                          terrainDistanceToSeaLevel );
        LOG.exiting();
        return tp;
    }

    /**
     * Calculates the field of view aka footprint, the corner points of the field of view as
     * follows, <br/>
     * <ul>
     * <li> f[0] = farclippingplane right side fo viewDirection </li>
     * <li> f[1] = farclippingplane left side fo viewDirection </li>
     * <li> f[2] = nearclippingplane right side fo viewDirection, note it can be behind the
     * viewPosition </li>
     * <li> f[3] = nearclippingplane left side fo viewDirection, note it can be behind the
     * viewPosition </li>
     * </ul>
     * <br/> the are rotated and translated according to the simpleTranform
     * 
     */
    private void calcFootprint() {
        LOG.entering();

        // should be read frome the configuration
        // double farClippingPlane = 15000;

        double nearClippingPlane = 0;
        double farClippingPlaneDistance = farClippingPlane;

        double halfAngleOfView = angleOfView * 0.5;

        // double heightAboveGround = observerPosition.y - distanceToSeaLevel;

        double heightAboveGround = observerPosition.z - terrainDistanceToSeaLevel;
        if ( heightAboveGround < 0 ) { // beneath the ground
            LOG.logError( "the Observer is below the terrain" );
            return;
        }

        if ( pitch > 0 ) { // the eye is looking down on the poi
            // pitch equals angle between upper and viewaxis, angleOfView is centered around the
            // viewaxis
            double angleToZ = pitch + halfAngleOfView;
            if ( Math.abs( angleToZ - rad90 ) > 0.00001 ) {
                // footprint front border distance
                if ( angleToZ > rad90 ) {
                    nearClippingPlane = -1 * ( heightAboveGround * Math.tan( angleToZ - rad90 ) );
                } else {
                    nearClippingPlane = heightAboveGround / Math.tan( angleToZ );
                }
            }

            // And the far clippingplane
            angleToZ = pitch - halfAngleOfView;
            if ( angleToZ > 0 ) {
                farClippingPlaneDistance = heightAboveGround / Math.tan( angleToZ );
                if ( farClippingPlaneDistance > farClippingPlane )
                    farClippingPlaneDistance = farClippingPlane;
            }

        } else {
            // TODO looking up to the poi
        }

        double farX = 0;
        if ( Math.abs( ( halfAngleOfView + rad90 ) % rad180 ) > 0.00001 )
            farX = Math.tan( halfAngleOfView ) * farClippingPlaneDistance;
        else {
            // TODO
        }
        // Although currently not supported in the DataBase, the Altitude (z) is set to
        // distanceSeaLevel, who knows what it might bring in the future.
        footprint[0] = new Point3d( farX, farClippingPlaneDistance, terrainDistanceToSeaLevel );
        footprint[1] = new Point3d( -farX, farClippingPlaneDistance, terrainDistanceToSeaLevel );

        double nearX = 0;
        if ( Math.abs( ( halfAngleOfView + rad90 ) % rad180 ) > 0.00001 )
            nearX = Math.tan( halfAngleOfView ) * nearClippingPlane;
        else {
            // TODO
        }
        footprint[2] = new Point3d( nearX, nearClippingPlane, terrainDistanceToSeaLevel );
        footprint[3] = new Point3d( -nearX, nearClippingPlane, terrainDistanceToSeaLevel );

        // System.out.println( "near->" + nearX );
        // System.out.println( "far->" + farX );

        // Looking north means yaw
        simpleTransform.rotZ( yaw );
        // translate to the viewersposition.
        simpleTransform.setTranslation( new Vector3d( observerPosition.x, observerPosition.y,
                                                      this.terrainDistanceToSeaLevel ) );

        for ( Point3d fp : footprint ) {
            simpleTransform.transform( fp );
        }

        LOG.exiting();
    }

    /**
     * Sets the viewMatrix according to the given yaw, pitch and the calculated observerPosition.
     */
    private void calculateViewMatrix() {
        viewMatrix.setIdentity();
        viewMatrix.lookAt( observerPosition, pointOfInterest, new Vector3d( 0, 0, 1 ) );
        viewMatrix.invert();

    }

    /**
     * @return true if the near clippingplane is behind the viewposition.
     */
    public boolean isNearClippingplaneBehindViewPoint() {
        if ( pitch > 0 ) { // the eye is looking down on the poi
            // pitch equals angle between upper and viewaxis, angleOfView is centered around the
            // viewaxis
            double angleToZ = pitch + ( angleOfView * 0.5 );
            if ( Math.abs( angleToZ - rad90 ) > 0.00001 ) {
                // footprint front border distance
                if ( angleToZ > rad90 ) {
                    return true;
                }
            }
        } else {
            // TODO looking up to the poi
        }

        return false;
    }

    /**
     * 
     * @return the field of view of the observer in radians
     */
    public double getAngleOfView() {
        return angleOfView;
    }

    /**
     * @param aov
     *            the field of view of the observer in radians
     */
    public void setAngleOfView( double aov ) {
        this.angleOfView = aov;
        calcFootprint();
    }

    /**
     * 
     * @return the horizontal direction in radians the observer looks
     */
    public double getYaw() {
        return yaw;
    }

    /**
     * 
     * @param yaw
     *            the horizontal direction in radians the observer looks
     */
    public void setYaw( double yaw ) {
        this.yaw = yaw;
        calcObserverPosition();
    }

    /**
     * @return vertical direction in radians the observer looks
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @param pitch
     *            the vertical direction in radians the observer looks
     * 
     */
    public void setPitch( double pitch ) {
        this.pitch = (pitch%rad90);

        // pitch and aov can not have the same value (the footprint will be one) therefor a little
        // check and correction
        if ( Math.abs( pitch - angleOfView ) <= 0.01 )
            this.pitch -= Math.toRadians( 0.5 );
        calcObserverPosition();
    }

    /**
     * @return Returns the distanceToSeaLevel of the terrain beneath the viewpoint.
     */
    public double getTerrainDistanceToSeaLevel() {
        return terrainDistanceToSeaLevel;
    }

    /**
     * @param distanceToSeaLevel
     *            of the terrain beneath the viewpoint
     */
    public void setTerrainDistanceToSeaLevel( double distanceToSeaLevel ) {
        this.terrainDistanceToSeaLevel = distanceToSeaLevel;
        calcFootprint();
    }

    /**
     * @return the position of the observer, the directions he looks and his field of view in
     *         radians
     * 
     */
    public Point3d getObserverPosition() {
        return observerPosition;
    }

    /**
     * @param observerPosition
     *            the position of the observer, the directions he looks and his field of view in
     *            radians
     * 
     */
    public void setObserverPosition( Point3d observerPosition ) {
        this.observerPosition = observerPosition;
        calcFootprint();
        calculateViewMatrix();
    }

    /**
     * @return the point of interest to which the viewer is looking
     */
    public Point3d getPointOfInterest() {
        return pointOfInterest;
    }

    /**
     * @param pointOfInterest
     *            the directions the observer looks and his field of view in radians
     * 
     */
    public void setPointOfInterest( Point3d pointOfInterest ) {
        this.pointOfInterest = pointOfInterest;
        calcObserverPosition();
    }

    /**
     * The footprint in object space: <br/>f[0] = (FarclippingPlaneRight) = angleOfView/2 +
     * viewDirection.x, farclippingplaneDistance, distanceToSealevel <br/>f[1] =
     * (FarclippingPlaneLeft) = angleOfView/2 - viewDirection.x, farclippingplaneDistance,
     * distanceToSealevel <br/>f[2] = (NearclippingPlaneRight) = angleOfView/2 + viewDirection.x,
     * nearclippingplaneDistance, distanceToSealevel <br/>f[3] = (NearclippingPlaneLeft) =
     * angleOfView/2 - viewDirection.x, nearclippingplaneDistance, distanceToSealevel
     * 
     * @return footprint or rather the field of view
     */
    public Point3d[] getFootprint() {
        return footprint;
    }

    /**
     * @param distanceToSeaLevel
     *            the new height for which the footprint should be calculated
     * @return footprint or rather the field of view
     */
    public Point3d[] getFootprint( double distanceToSeaLevel ) {
        this.terrainDistanceToSeaLevel = distanceToSeaLevel;
        calcFootprint();
        return footprint;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "observerPosition: " + observerPosition + "\n" );
        sb.append( "targetPoint: " + pointOfInterest + "\n" );
        sb.append( "footprint: " + "\n" );
        sb.append( footprint[0] + "\n" );
        sb.append( footprint[1] + "\n" );
        sb.append( footprint[2] + "\n" );
        sb.append( footprint[3] + "\n" );
        sb.append( "aov: " + Math.toDegrees( angleOfView ) + "\n" );
        sb.append( "yaw: " + Math.toDegrees( yaw ) + "\n" );
        sb.append( "pitch: " + Math.toDegrees( pitch ) + "\n" );
        sb.append( "distanceToSeaLevel: " + terrainDistanceToSeaLevel + "\n" );
        sb.append( "farClippingPlane: " + farClippingPlane + "\n" );
        return sb.toString();
    }

    /**
     * @return Returns the farClippingPlane.
     */
    public double getFarClippingPlane() {
        return farClippingPlane;
    }

    /**
     * @return Returns a new transform3D Object which contains the transformations to place the
     *         viewers Position and his yaw viewing angle relativ to the 0,0 coordinates and the
     *         poi.
     */
    public Transform3D getSimpleTransform() {
        return new Transform3D( simpleTransform );
    }

    /**
     * @param transform
     *            The transform to set.
     */
    public void setSimpleTransform( Transform3D transform ) {
        this.simpleTransform = transform;
    }

    /**
     * @return Returns the viewMatrix.
     */
    public Transform3D getViewMatrix() {
        return viewMatrix;
    }

    /**
     * @return the viewerToPOIDistance value.
     */
    public double getViewerToPOIDistance() {
        return viewerToPOIDistance;
    }

    /**
     * @param viewerToPOIDistance
     *            An other viewerToPOIDistance value.
     */
    public void setViewerToPOIDistance( double viewerToPOIDistance ) {
        this.viewerToPOIDistance = viewerToPOIDistance;
        calcObserverPosition();
    }

    /**
     * 
     * @return the Footprint as a Surface (bbox)
     */
    public Surface getVisibleArea() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for ( Point3d point : footprint ) {
            if ( point.x < minX )
                minX = point.x;
            if ( point.x > maxX )
                maxX = point.x;
            if ( point.y < minY )
                minY = point.y;
            if ( point.y > maxY )
                maxY = point.y;
        }
        Envelope env = GeometryFactory.createEnvelope( minX, minY, maxX, maxY, crs );
        Surface s = null;
        try {
            s = GeometryFactory.createSurface( env, crs );
        } catch ( GeometryException e ) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * @return A String representation of the Footprint, so that it can be easily used in another
     *         programm e.g. deejump
     * @throws GeometryException
     *             if the footprint could not be transformed to wkt.
     */
    public String getFootPrintAsWellKnownText()
                            throws GeometryException {
        Position[] pos = new Position[footprint.length + 1];

        for ( int i = 0; i < footprint.length; ++i ) {
            Point3d point = footprint[i];
            pos[i] = GeometryFactory.createPosition( point.x, point.y, point.z );
        }
        Point3d point = footprint[0];
        pos[footprint.length] = GeometryFactory.createPosition( point.x, point.y, point.z );

        return WKTAdapter.export( GeometryFactory.createSurface( pos, null, null, crs ) ).toString();
    }

    /**
     * @return the ObserverPosition as a well known text String.
     * @throws GeometryException if the conversion fails.
     */
    public String getObserverPositionAsWKT()
                            throws GeometryException {
        return WKTAdapter.export(
                                  GeometryFactory.createPoint( observerPosition.x,
                                                               observerPosition.y,
                                                               observerPosition.z, crs ) ).toString();
    }

    /**
     * @return the crs value.
     */
    public CoordinateSystem getCrs() {
        return crs;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: ViewPoint.java,v $
 * Changes to this class. What the people have been up to: Revision 1.13  2006/12/04 17:06:43  bezema
 * Changes to this class. What the people have been up to: enhanced dgm from wcs support
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/11/29 16:01:17  bezema
 * Changes to this class. What the people have been up to: bug fixes and added features
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/11/27 15:42:34  bezema
 * Changes to this class. What the people have been up to: Updated the coordinatesystem handling
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.8
 * 2006/07/05 15:59:13 poth comments corrected
 * 
 * Revision 1.7 2006/07/05 11:24:22 taddei removed unused code
 * 
 * Revision 1.6 2006/04/06 20:25:28 poth ** empty log message ***
 * 
 * Revision 1.5 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.4 2006/01/26 14:40:36 taddei added test method
 * 
 * Revision 1.3 2006/01/16 20:36:39 poth ** empty log message ***
 * 
 * Revision 1.2 2005/12/23 11:57:43 taddei added factory method
 * 
 * Revision 1.1 2005/12/21 13:50:03 taddei first check in of old but good WTS classes
 * 
 * 
 **************************************************************************************************/
