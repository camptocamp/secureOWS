//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/OffScreenWPVSRenderer.java,v 1.11 2006/12/04 17:06:43 bezema Exp $
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.Locale;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;

import org.deegree.framework.util.MapUtils;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;

import com.sun.j3d.utils.universe.LocaleFactory;


/**
 * The class provides the capabilitiy for rendering a <tt>WPVSScene</tt> to an
 * offscreen graphic context that is represent by a <tt>BufferedImage</tt>.
 * <p>-----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.11 $ $Date: 2006/12/04 17:06:43 $
 */
public class OffScreenWPVSRenderer extends Abstract3DRenderingEngine {     
    
    private int width  = 800;
    
    private int height = 600;
    
    private Canvas3D offScreenCanvas3D;
    
    private BranchGroup sceneGroup;
    
    //private OrderedGroup terrainGroup;
    private BranchGroup lightGroup;
    
    private VirtualUniverse universe; 
    
    private Locale locale;
    
    /**
     * initialzies the render class with a default width and height (800x600)
     * @param scene to render
     */
    public OffScreenWPVSRenderer(WPVSScene scene)
    {        
        this( scene, 801, 601 );          
    }
    
    /**
     * initialzies the render class with the submitted width and height 
     * @param scene to render
     * @param width of the resulting image
     * @param height of the resulting image
     */
    public OffScreenWPVSRenderer(WPVSScene scene, int width, int height)
    {        
        super( scene );
        this.width = width;
        this.height = height;

        this.offScreenCanvas3D = createOffscreenCanvas3D();
        sceneGroup = scene.getScene();

        lightGroup = new BranchGroup();

        //terrainGroup = new OrderedGroup();
        
        universe = new VirtualUniverse();
        
        locale = new Locale( universe );
        //locale = 
        
        //addBackground( scene.getViewPoint(), terrainGroup, scene.getBackground() );
        addBackground( scene.getViewPoint(), sceneGroup, scene.getBackground() );
        // add the terrain to the view
  
        
        //sceneGroup.addChild( terrainGroup );
        
        /**
         * Showing the boundingbox
         */
        float[][] triangles = new float[4][3];
        Envelope env = scene.getViewPoint().getVisibleArea().getEnvelope();
        System.out.println( "envelope: " + env );
        Position pMin = env.getMin();
        Position pMax = env.getMax();
        
        float zValue = (float)scene.getViewPoint().getPointOfInterest().z;
        
        triangles[0][0] = (float)pMin.getX();
        triangles[0][1] = (float)pMin.getY();
        triangles[0][2] = zValue;

        triangles[1][0] = (float)pMin.getX();
        triangles[1][1] = (float)pMax.getY();
        triangles[1][2] = zValue;

        triangles[2][0] = (float)pMax.getX();
        triangles[2][1] = (float)pMax.getY();
        triangles[2][2] = zValue;

        triangles[3][0] = (float)pMax.getX();
        triangles[3][1] = (float)pMin.getY();
        triangles[3][2] = zValue;
        
        int vertexFormat = GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.NORMALS;
        LineArray la = new LineArray( 4, vertexFormat );
        for( int i = 0; i< 4 ; ++i ){
            System.out.println( i+") "+triangles[i][0] + ", " + triangles[i][1] + ", " + triangles[i][2]);
            la.setCoordinates(i, triangles[i] );
            la.setColor( i, new Color3f( Color.RED ));
            la.setNormal( i, new float[]{0, 0 , -1} );
        }
        
        Shape3D tinObject = new Shape3D();
        tinObject.setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
        tinObject.setGeometry( la );
        
        sceneGroup.addChild( tinObject );
        

        PointArray pa = new PointArray( 2, vertexFormat );
        pa.setCoordinates(0, triangles[0] );
        pa.setColor( 0, new Color3f( Color.BLUE ));
        pa.setNormal( 0, new float[]{0, 0 , -1} );
        
        tinObject = new Shape3D();
        tinObject.setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
        tinObject.setGeometry( pa );
        sceneGroup.addChild( tinObject );

        
        pa.setCoordinates(1, triangles[2] );
        pa.setColor( 1, new Color3f( Color.GREEN ));
        pa.setNormal( 1, new float[]{0, 0 , 1} );
        
        tinObject = new Shape3D();
        tinObject.setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
        tinObject.setGeometry( pa );
        sceneGroup.addChild( tinObject );
       
        
//        for ( Group buildings : scene.getFeatures() ) {               
//            sceneGroup.addChild( buildings );  
//        }                
               
        sceneGroup.compile();
        locale.addBranchGraph( sceneGroup );
        
        
        Light[] lights = scene.getLights();
        for (int i = 0; i < lights.length; i++) {
              lightGroup.addChild( lights[i].cloneTree() );
        }
       
        lightGroup.compile();
        locale.addBranchGraph( lightGroup );
    }

    
    /**
     * @param scene the Scene to render
     */
    public void setScene(WPVSScene scene) {
        this.scene = scene;
    }
  
    /**
     * @return the width of the offscreen rendering target
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @param width of the image
     */
    public void setWidth( int width ) {   
        this.width = width;
        this.offScreenCanvas3D = createOffscreenCanvas3D();
    }
    
    /**
     * @return the height of the offscreen rendering target
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * @param height of the image
     */
    public void setHeight( int height ) {
        this.height = height;  
        this.offScreenCanvas3D = createOffscreenCanvas3D();
    }
    
    /** renders the scene to a <tt>BufferedImage</tt>
     * @return a <tt>BufferedImage</tt> where the scene has been rendered to
     */
    public BufferedImage renderScene() {
        View view = new View();   

        System.out.println( "read fov1: " + Math.toDegrees( view.getFieldOfView() ));
        view.setFieldOfView( scene.getViewPoint().getAngleOfView() /** 0.5*/ );
        view.addCanvas3D( offScreenCanvas3D );   
        System.out.println( "read fov1: " + Math.toDegrees( view.getFieldOfView() ));
        BranchGroup viewGroup = new BranchGroup();      
        setView( view,  viewGroup );            
        viewGroup.setCapability( BranchGroup.ALLOW_DETACH );
        viewGroup.compile();
        
        locale.addBranchGraph(viewGroup);

        offScreenCanvas3D.renderOffScreenBuffer();    
        offScreenCanvas3D.waitForOffScreenRendering();

        ImageComponent2D imageComponent = offScreenCanvas3D.getOffScreenBuffer();
        
        locale.removeBranchGraph( viewGroup );

        view.removeCanvas3D( offScreenCanvas3D );
        return imageComponent.getImage();
    }
    
    
    /**
     * creates and returns a canvas for offscreen rendering
     * @return a offscreen Canvas3D on which the the scene will be rendered.
     */
    protected Canvas3D createOffscreenCanvas3D()
    {	
        Canvas3D offScreenCanvas3D = createCanvas( true );
        
        offScreenCanvas3D.getScreen3D().setSize( width, height );
        
        offScreenCanvas3D.getScreen3D().setPhysicalScreenHeight( MapUtils.DEFAULT_PIXEL_SIZE * height );
        offScreenCanvas3D.getScreen3D().setPhysicalScreenWidth( MapUtils.DEFAULT_PIXEL_SIZE * width );

        BufferedImage renderedImage = 
            new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );        

        ImageComponent2D imageComponent = 
            new ImageComponent2D( ImageComponent.FORMAT_RGB, renderedImage );
//        new ImageComponent2D( ImageComponent.FORMAT_RGB, renderedImage );

        imageComponent.setCapability( ImageComponent.ALLOW_IMAGE_READ );

        offScreenCanvas3D.setOffScreenBuffer( imageComponent );
        
        return offScreenCanvas3D;
    }
    
    /**
     * Called to render the scene into the offscreen Canvas3D
     * @param offScreenCanvas3D to be rendered into
     * @return a buffered image as a result of the Rendering.
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
$Log: OffScreenWPVSRenderer.java,v $
Revision 1.11  2006/12/04 17:06:43  bezema
enhanced dgm from wcs support

Revision 1.10  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.9  2006/07/05 15:59:13  poth
comments corrected

Revision 1.8  2006/06/29 16:50:09  poth
*** empty log message ***

Revision 1.7  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.6  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.5  2006/02/24 11:42:41  taddei
refactoring (background)

Revision 1.4  2006/02/21 14:02:39  taddei
better positioning of background

Revision 1.3  2006/01/26 14:27:26  taddei
changed background code to match latest changes in superclass

Revision 1.2  2005/12/23 11:57:22  taddei
fixed typo

Revision 1.1  2005/12/21 13:50:03  taddei
first check in of old but good WTS classes


********************************************************************** */