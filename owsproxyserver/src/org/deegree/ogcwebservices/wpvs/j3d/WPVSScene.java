//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/WPVSScene.java,v 1.11 2006/11/23 11:46:40 bezema Exp $
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * This class represents the basic class for creation of a 3D perspective views as specified in the
 * OGC Web Perpective View Service specification. A WPVS scene is defined by a scene model and a
 * date determining the light conditions. Additional elements are 3D or 2.5D-features that are
 * placed into the scene, atmospheric conditions influencing the light and visibility (e.g. fog,
 * rain etc., but currently not implemented) and additional light placed into the scene (e.g. street
 * lights, spots, lighted windows etc.).
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:lupp@lat-lon.de">Katharina Lupp</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.11 $ $Date: 2006/11/23 11:46:40 $
 */
public class WPVSScene {

    private Calendar calendar;

    private BranchGroup scene;

    private List<Light> lights;

    private ViewPoint viewPoint;

    private Node background;

    /**
     * Creates a new instance of WPVScene
     * 
     * @param scene
     *            java3D representation of the scene.
     * @param viewPoint
     *            object that describes the viewers position and the looking direction
     * @param calendar
     *            describtion of the date and time for which the scene shall be rendered --> light
     *            conditions
     * @param lights
     *            lights in addition to sun and ambient light (e.g. street lights, spots etc.)
     * @param background
     *            scene background; have to be a <tt>Shape3D</tt> or a <tt>Background</tt>
     */
    public WPVSScene( BranchGroup scene, ViewPoint viewPoint, Calendar calendar,
                     List<Light> lights, Node background ) {
        if ( lights != null ) {
            this.lights = lights;
        } else {
            this.lights = new ArrayList<Light>();
        }
        this.scene = scene;
        this.viewPoint = viewPoint;
        this.calendar = calendar;
        this.background = background;
    }

    /**
     * creates the light that results from the sun (direct light) and the ambient of the sky.
     */
    private void createDayLight() {

        int latitute = 50;
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int date = calendar.get( Calendar.DAY_OF_MONTH );
        int hour = calendar.get( Calendar.HOUR_OF_DAY );
        int minute = calendar.get( Calendar.MINUTE );

        Vector3f vec = SunLight.calculateSunlight( latitute, year, month, date, hour, minute, 0 );
        double vPos = SunPosition.calcVerticalSunposition( latitute, year, month, date, hour,
                                                           minute );
        double hPos = SunPosition.calcHorizontalSunPosition( hour, minute );

        Color3f white = new Color3f( vec );

        ViewPoint vp = getViewPoint();
        Point3d p = vp.getObserverPosition();
        Point3d origin = new Point3d( p.x, p.y, p.z );
        BoundingSphere light_bounds = new BoundingSphere( origin, 250000 );

        // Directional Light: A DirectionalLight node defines an oriented light with an origin at
        // infinity.
        DirectionalLight headlight = new DirectionalLight();
        headlight.setInfluencingBounds( light_bounds );
        headlight.setColor( white );
        headlight.setDirection( (float) Math.sin( hPos ), -(float) Math.sin( vPos ),
                                (float) Math.abs( Math.cos( hPos ) ) );
        lights.add( headlight );
        // Ambient Light: Ambient light is that light that seems to come from all directions.
        // Ambient light has only an ambient reflection component.
        // It does not have diffuse or specular reflection components.
        AmbientLight al = new AmbientLight();
        al.setInfluencingBounds( light_bounds );
        al.setColor( new Color3f( 0.5f * vec.x, 0.5f * vec.y, 0.5f * vec.z ) );

        lights.add( al );
    }

    /**
     * @return the background object of the scene.
     */
    public Node getBackground() {
        return background;
    }

    /**
     * @param background
     *            sets the <tt>Background</tt> object of the scene
     */
    public void setBackground( Node background ) {
        this.background = background;
    }


    /**
     * get the date and the time for determining time depending the light conditions of the scene
     * 
     * @return describtion of the date and time for which the scene shall be rendered --> light
     *         conditions
     */
    public Calendar getDate() {
        return calendar;
    }

    /**
     * set the date and the time for determining time depending the light conditions of the scene
     * 
     * @param calendar
     *            describtion of the date and time for which the scene shall be rendered --> light
     *            conditions
     */
    public void setDate( Calendar calendar ) {
        if ( calendar == null ) {
            calendar = new GregorianCalendar(  );
        }
        this.calendar = calendar;
    }

    /**
     * @return Java3D representation of the scene.
     */
    public BranchGroup getScene() {
        return scene;
    }

    /**
     * gets the position of the viewer, the directions he looks and his field of view in radians
     * 
     * @return object that describes the viewers position and the point he looks at
     */
    public ViewPoint getViewPoint() {
        return viewPoint;
    }

    /**
     * defines the position of the viewer and the point he looks at.
     * 
     * @param viewPoint
     *            object that describes the viewers position and the point he looks at
     */
    public void setViewPoint( ViewPoint viewPoint ) {
        this.viewPoint = viewPoint;
    }

    /**
     * adds a light to the scene. this can be ambient, directional and point light.
     * 
     * @param light
     *            a light in addition to sun and basic ambient light (e.g. street lights, spots
     *            etc.)
     */
    public void addLight( Light light ) {
        this.lights.add( light );
    }

    /**
     * returns the lights of the scene
     * 
     * @return lights including sun and basic ambient light (e.g. street lights, spots etc.)
     */
    public Light[] getLights() {
        return lights.toArray( new Light[lights.size()] );
    }

    /**
     * sets the lights of the scene. this can be ambient, directional and point light.
     * 
     * @param lights
     *            lights in addition to sun and basic ambient light (e.g. street lights, spots etc.)
     */
    public void setLights( Light[] lights ) {
        this.lights.clear();
        setDate( calendar );
        createDayLight();
        if ( lights != null ) {
            for ( int i = 0; i < lights.length; i++ ) {
                addLight( lights[i] );
            }
        }
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WPVSScene.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision 1.10
 * 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.9 2006/06/29 19:12:20 poth ** empty log message ***
 * 
 * Revision 1.8 2006/04/06 20:25:28 poth ** empty log message ***
 * 
 * Revision 1.7 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.6 2006/02/24 11:42:41 taddei refactoring (background)
 * 
 * Revision 1.5 2006/02/21 14:02:39 taddei better positioning of background
 * 
 * Revision 1.4 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.3 2006/01/26 14:28:57 taddei uses now a background for the background (no more images
 * strings)
 * 
 * Revision 1.2 2005/12/23 11:58:08 taddei chageds due to refactoring
 * 
 * Revision 1.1 2005/12/21 13:50:03 taddei first check in of old but good WTS classes
 * 
 * 
 **************************************************************************************************/