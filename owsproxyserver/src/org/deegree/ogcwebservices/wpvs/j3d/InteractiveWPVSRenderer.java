//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/InteractiveWPVSRenderer.java,v 1.12 2006/11/27 09:07:52 poth Exp $

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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Light;
import javax.media.j3d.Locale;
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;


/**
 * This class sill/shoud provide the ability to render a scence object (s. 
 * com.sun.j3d.loaders.Scene) or a Canvas3D. It's currently used for testing. 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.12 $ $Date: 2006/11/27 09:07:52 $
 */
public class InteractiveWPVSRenderer extends Abstract3DRenderingEngine {     
    
    private int width  = 800;
    
    private int height = 600;
    
    private Canvas3D offScreenCanvas3D;
    
    private View view;  
    
    private boolean newSize;
    
    /**
     * initialzies the render class with a default width and height (800x600)
     */
    public InteractiveWPVSRenderer(WPVSScene scene)
    {        
        this( scene, 801, 601 );          
    }
    
    /**
     * initialzies the render class with the submitted width and height 
     */
    public InteractiveWPVSRenderer(WPVSScene scene, int width, int height)
    {        
        super( scene );
        this.width = width;
        this.height = height;
        view = new View();        
    }
    
    public void setScene(WPVSScene scene) {
        this.scene = scene;
    }
    
    /**Create the VirtualUniverse for the application.*/
    protected VirtualUniverse createVirtualUniverse() {
        return new VirtualUniverse();
    }
    
    /**
     * Simple utility method that creates a Locale for the
     * VirtualUniverse
     */
    protected Locale createLocale( VirtualUniverse u ) {
        return new Locale( u );
    }
    
    /**
     * returns the width of the offscreen rendering target
     */
    public int getWidth() {
        return width;
    }
    
    public void setWidth( int width ) {   
        newSize = true;
        this.width = width;
    }
    
    /**
     * returns the height of the offscreen rendering target
     */
    public int getHeight() {
        return height;
    }
    
    public void setHeight( int height ) {
        newSize = true;
        this.height = height;  
    }
    
    /** renders the scene to an <tt>BufferedImage</tt>
     * @return a <tt>BufferedImage</tt> where the scene has been rendered to
     */
    public Object renderScene() {
        if ( newSize ) {
            view.removeCanvas3D( offScreenCanvas3D );

            offScreenCanvas3D = createOffscreenCanvas3D();

            newSize = false;
        }
        view.addCanvas3D( offScreenCanvas3D );     
        view.startView();
        
        // The viewGroup contains nodes necessary for rendering, viewing, etc
        // View, ViewPlatform, Canvas3D, PhysBody, PhysEnviron, and Lights
        BranchGroup viewGroup = new BranchGroup();
        
        // The sceneGroup conatins obejcts of the scene graph
        BranchGroup sceneGroup = new BranchGroup();
        
        setView( view,  viewGroup );
//        createMouseBehaviours( viewGroup );
        
        OrderedGroup terrainGroup = new OrderedGroup();
        
        addBackground( scene.getViewPoint(), terrainGroup, scene.getBackground() );
        
        // add the lights to the view
        Light[] lights = scene.getLights();
        for (int i = 0; i < lights.length; i++) {
            viewGroup.addChild( lights[i] );
        }
        /*
        // add the terrain to the view
        Shape3D terrain[] = scene.getTerrain();
        for (int i = terrain.length-1; i >= 0; i--) {
            terrainGroup.addChild( terrain[i] );
        }        
        sceneGroup.addChild( terrainGroup );
        
        // add the features to the view
        Group[] features = scene.getFeatures();
        for (int i = 0; i < features.length; i++) {   
            sceneGroup.addChild( features[i] );
        }                
        */
        sceneGroup.compile();
        
        viewGroup.compile();
        
        VirtualUniverse universe = createVirtualUniverse();
        Locale locale = createLocale( universe );  

        locale.addBranchGraph(sceneGroup);
        locale.addBranchGraph(viewGroup);
        
        BranchGroup mainGroup = new BranchGroup();
//        mainGroup.addChild( sceneGroup );
//        mainGroup.addChild( viewGroup );
//        mainGroup.addChild( bg[0] );
        
        return offScreenCanvas3D;
    }
    
    /**
     * creates and returns a canvas for offscreen rendering
     */
    protected Canvas3D createOffscreenCanvas3D()
    {	
        Canvas3D offScreenCanvas3D = createCanvas( false );
        
//        offScreenCanvas3D.getScreen3D().setSize( width, height );
        
        offScreenCanvas3D.getScreen3D().setPhysicalScreenHeight( 0.0254/90 * height );
        offScreenCanvas3D.getScreen3D().setPhysicalScreenWidth( 0.0254/90 * width );

        BufferedImage renderedImage = 
            new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );        

        ImageComponent2D imageComponent = 
            new ImageComponent2D( ImageComponent.FORMAT_RGB8, renderedImage );

        imageComponent.setCapability( ImageComponent.ALLOW_IMAGE_READ );

//        offScreenCanvas3D.setOffScreenBuffer( imageComponent );
        
        return offScreenCanvas3D;
    }
    
    private void createMouseBehaviours( Group scene  ){
        Point3d origin = new Point3d(-2584400.880145242, 528.7904086212667, 5615449.9824785);  
        BoundingSphere bounds = new BoundingSphere(origin, 250000);
//        TransformGroup viewTrans = vp.getViewPlatformTransform();
            
//      Create the rotate behavior node
        MouseRotate behavior1 = new MouseRotate(offScreenCanvas3D);
        scene.addChild(behavior1);
        behavior1.setSchedulingBounds(bounds);

        // Create the zoom behavior node
        MouseZoom behavior2 = new MouseZoom(offScreenCanvas3D);
        scene.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate(offScreenCanvas3D);
        scene.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);
    }
    
    /**
     * Called to render the scene into the offscreen Canvas3D
     */
    protected RenderedImage getImage(Canvas3D offScreenCanvas3D)
    {        
        offScreenCanvas3D.renderOffScreenBuffer();
        offScreenCanvas3D.waitForOffScreenRendering();
        
        ImageComponent2D imageComponent = offScreenCanvas3D.getOffScreenBuffer();
        
        return imageComponent.getImage();
            
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InteractiveWPVSRenderer.java,v $
Revision 1.12  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.11  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.10  2006/07/05 15:59:13  poth
comments corrected

Revision 1.9  2006/06/20 07:45:59  taddei
removed println

Revision 1.8  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.6  2006/02/24 11:42:41  taddei
refactoring (background)

Revision 1.5  2006/02/21 14:02:39  taddei
better positioning of background

Revision 1.4  2006/02/09 15:47:24  taddei
bug fixes, refactoring and javadoc

Revision 1.3  2006/01/26 14:26:57  taddei
changed background code to match latest changes in superclass

Revision 1.2  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.1  2005/12/23 11:56:44  taddei
first checkin of interactive renderer (used for testing)

Revision 1.1  2005/12/21 13:50:03  taddei
first check in of old but good WTS classes


********************************************************************** */