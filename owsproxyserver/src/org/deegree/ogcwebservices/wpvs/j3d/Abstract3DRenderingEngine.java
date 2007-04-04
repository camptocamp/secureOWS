//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Abstract3DRenderingEngine.java,v 1.12 2006/12/04 17:06:44 bezema Exp $
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

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;

/**
 * 
 * This class serves a a superclass for all rendering engines of the WPV Service.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.12 $ $Date: 2006/12/04 17:06:44 $
 */
abstract public class Abstract3DRenderingEngine implements RenderingEngine {

    protected WPVSScene scene;

    protected float backClipping;

    protected float frontClipping;

    /**
     * Creates a new Abstract3DRenderEngine object.
     * 
     * @param scene
     */
    public Abstract3DRenderingEngine( WPVSScene scene ) {
        this.scene = scene;
        // clipping default
        // backClipping = (float)scene.getViewPoint().getFarClippingPlane();
        backClipping = 15000f;
        frontClipping = 5f;
    }

    /**
     * Creates a new canvas each time this is called.
     * 
     * The Canvas3D class provides a drawing canvas for 3D rendering. The Canvas3D object extends
     * the Canvas object to include 3D-related information such as the size of the canvas in pixels,
     * the Canvas3D's location, also in pixels, within a Screen3D object, and whether or not the
     * canvas has stereo enabled. Because all Canvas3D objects contain a reference to a Screen3D
     * object and because Screen3D objects define the size of a pixel in physical units, Java 3D can
     * convert a Canvas3D size in pixels to a physical world size in meters. It can also determine
     * the Canvas3D's position and orientation in the physical world.
     * 
     * @param offscreen
     *            true if the canvas3D is an offsreen canvas.
     * 
     * @return A new canvas instance or <tt>null</tt> if no GraphicsEnvironment was found.
     */
    protected Canvas3D createCanvas( boolean offscreen ) {
        // This class is used to obtain a valid GraphicsConfiguration that can be used by Java 3D.
        // It instantiates objects and then sets all non-default attributes as desired.
        GraphicsDevice[] gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        GraphicsConfigTemplate3D gc3D = new GraphicsConfigTemplate3D();
        gc3D.setSceneAntialiasing( GraphicsConfigTemplate.PREFERRED );
        gc3D.setDoubleBuffer( GraphicsConfigTemplate.REQUIRED );

        if ( gd != null && gd.length > 0 ) {
            Canvas3D canvas = new Canvas3D( gd[0].getBestConfiguration( gc3D ), offscreen );
            return canvas;
        }
        return null;
    }

    /**
     * A transform group is aplied as the transformation to the branches
     * 
     * @return the transformgroup with ALLOW_TRANSFORM_READ, ALLOW_TRANSFORM_WRITE and
     *         ALLOW_LOCAL_TO_VWORLD_READ set.
     */
    protected TransformGroup createTransformGroup() {
        // creates the TransformGroup
        // The TransformGroup node specifies a single spatial transformation, via a Transform3D
        // object,
        // that can position, orient, and scale all of its children.
        TransformGroup viewTG = new TransformGroup();

        // Specifies that the node allows access to its object's transform information.
        viewTG.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );

        // Specifies that the node allows writing its object's transform information.
        viewTG.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );

        // Specifies that this Node allows read access to its local coordinates to virtual world
        // (Vworld) coordinates transform.
        viewTG.setCapability( Node.ALLOW_LOCAL_TO_VWORLD_READ );

        return viewTG;
    }

    /**
     * sets/defines the <tt>View</tt> of the scene and adds it to the submitted
     * <tt>BranchGroup</tt>
     * 
     * @param view
     *            the scenes view
     * @param viewGroup
     */
    protected void setView( View view, BranchGroup viewGroup ) {
        ViewPoint viewPoint = scene.getViewPoint();

        // The ViewPatform class is used to set up the "view" side of a Java 3D scene graph.
        ViewPlatform camera = new ViewPlatform();

        // RELATIVE_TO_FIELD_OF_VIEW tells Java 3D that it should modify the eyepoint position so it
        // is located
        // at the appropriate place relative to the window to match the specified field of view.
        // This implies that the view frustum will change whenever the application changes the field
        // of view.
        camera.setViewAttachPolicy( View.RELATIVE_TO_FIELD_OF_VIEW );
        camera.setViewAttachPolicy(View.NOMINAL_SCREEN );


        
        view.setFieldOfView( viewPoint.getAngleOfView() /** 0.5*/ );
        view.setWindowEyepointPolicy( View.RELATIVE_TO_FIELD_OF_VIEW );
        
        //set view parameters
        view.setUserHeadToVworldEnable( true );
        view.setSceneAntialiasingEnable( true );
 
        // The View object contains all parameters needed in rendering a three dimensional scene
        // from one viewpoint.
        view.setBackClipDistance( backClipping );
        view.setFrontClipDistance( frontClipping );

        // creates the PhysicalBody and PhysicalEnvironment for the View
        // and attachs it to the View
        view.setPhysicalEnvironment( new PhysicalEnvironment() );
        PhysicalBody pb = new PhysicalBody( scene.getViewPoint().getObserverPosition(), scene.getViewPoint().getObserverPosition());
        
        view.setPhysicalBody( pb );

        
        // attach the View to the ViewPlatform
        view.attachViewPlatform( camera );

        TransformGroup viewTG = createTransformGroup();
        viewTG.addChild( camera );
        viewTG.setTransform( viewPoint.getViewMatrix() );
        viewGroup.addChild( viewTG );
 //       viewGroup.addChild( camera );
    }

    /**
     * adds a background to the scene
     * 
     * @param vp
     *            viewpoint
     * @param background
     *            the node to render in the background
     * @param worldGroup
     *            to add the Background to
     */
    protected void addBackground( @SuppressWarnings("unused")
    ViewPoint vp, Group worldGroup, Node background ) {
        // Point3d pp = vp.getObserverPosition();
        // Point3d origin = new Point3d( pp.x, pp.y, pp.z );
        // Bounds bounds = new BoundingSphere( origin, backClipping );
        // ExponentialFog fog = new ExponentialFog();
        // fog.setColor( new Color3f( 0.7f, 0.7f, 0.7f ) );
        // fog.setDensity( 0.001f );
        // LinearFog fog = new LinearFog();
        // fog.setColor( new Color3f( 0.7f, 0.7f, 0.7f ) );
        // fog.setFrontDistance( 0 );
        // fog.setBackDistance( 2000 );
        // fog.setInfluencingBounds( bounds );
        // worldGroup.addChild( fog );

        worldGroup.addChild( background );

    }

    /**
     * sets the scenes back clip distance. default is 15000
     * 
     * @param distance
     */
    public void setBackClipDistance( float distance ) {
        backClipping = distance;
    }

    /**
     * sets the scenes front clip distance. default is 2
     * 
     * @param distance
     */
    public void setFrontClipDistance( float distance ) {
        frontClipping = distance;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Abstract3DRenderingEngine.java,v $
 * Changes to this class. What the people have been up to: Revision 1.12  2006/12/04 17:06:44  bezema
 * Changes to this class. What the people have been up to: enhanced dgm from wcs support
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 * Revision 1.10 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.9 2006/04/06 20:25:28 poth ** empty log message ***
 * 
 * Revision 1.8 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.7 2006/03/02 15:28:38 taddei �, and fog
 * 
 * Revision 1.6 2006/02/24 11:42:41 taddei refactoring (background)
 * 
 * Revision 1.5 2006/02/21 14:02:39 taddei better positioning of background
 * 
 * Revision 1.4 2006/01/26 13:55:50 taddei changes to backgorund code
 * 
 * Revision 1.3 2006/01/18 08:56:46 taddei added comment to possible bug fix
 * 
 * Revision 1.2 2005/12/23 11:56:24 taddei background test code included
 * 
 * Revision 1.1 2005/12/21 13:50:03 taddei first check in of old but good WTS classes
 * 
 * 
 **************************************************************************************************/