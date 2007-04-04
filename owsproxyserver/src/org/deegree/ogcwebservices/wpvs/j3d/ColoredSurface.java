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

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/11/27 16:57:02 $
 *
 * @since 2.0
 */
public class ColoredSurface extends DefaultSurface {
    
    private Appearance defaultAppearance;
    
    /**
     * 
     * @param objectID an Id for this Surface, for example a db primary key
     * @param parentId an Id for the parent of this Surface, for example if this is a wall the parent is the building.
     * @param surface
     * @param red
     * @param green
     * @param blue
     * @param transparency
     */
    public ColoredSurface( String objectID, String parentId, Surface surface, float red, float green, float blue, 
                           float transparency ) {
        super( objectID, parentId, surface );
        Material material = createMaterial( red, green, blue );
        setAppearance( createAppearance( material, transparency ) );
    }
    
    /**
     * 
     * @param objectID an Id for this Surface, for example a db primary key
     * @param parentId an Id for the parent of this Surface, for example if this is a wall the parent is the building.
     * @param surface
     * @param material
     * @param transparency
     */
    public ColoredSurface( String objectID, String parentId, Surface surface, Material material, float transparency ) {
       super( objectID, parentId, surface );
       setAppearance( createAppearance( material, transparency ) );
    }
    
    /**
     * 
     * @param objectID an Id for this Surface, for example a db primary key
     * @param parentId an Id for the parent of this Surface, for example if this is a wall the parent is the building.
     * @param surface
     * @param app
     */
    public ColoredSurface( String objectID, String parentId, Surface surface, Appearance app ) {
       super( objectID, parentId, surface );
       defaultAppearance = app;
    }
    
    @Override 
    public Appearance getAppearance() {
        return defaultAppearance;
    }

    @Override
    public void setAppearance( Appearance appearance ) {
        this.defaultAppearance = appearance;
        super.setAppearance( appearance );
    }
   

    /**
     * create a simple colored Material
     * @param red
     * @param green
     * @param blue
     * @return a new Material with specular and ambient and diffuse color.
     */
    private Material createMaterial(float red, float green, float blue) {
        Color3f color = new Color3f( red, green, blue );
        Material targetMaterial = new Material();
        
        targetMaterial.setAmbientColor( color );
        targetMaterial.setDiffuseColor( color );
        targetMaterial.setSpecularColor( color );
        targetMaterial.setShininess( 75.0f );
        targetMaterial.setLightingEnable( true );
        targetMaterial.setCapability( Material.ALLOW_COMPONENT_WRITE );
        return targetMaterial;
    }
    
    /**
     * create Appearence from a material and a opacity value
     * @param material
     * @param transparency
     * @return a default appearance created with the material properties
     */
    private Appearance createAppearance(Material material, float transparency) {
        ColoringAttributes ca = new ColoringAttributes();
        ca.setShadeModel( ColoringAttributes.SHADE_GOURAUD );
        
        Appearance appearance = new Appearance();
        material.setAmbientColor( new Color3f( Color.RED) );
        material.setDiffuseColor( new Color3f( Color.RED) );
        appearance.setMaterial( material );
        appearance.setColoringAttributes(ca);

        PolygonAttributes targetPolyAttr = new PolygonAttributes();
        targetPolyAttr.setCapability( PolygonAttributes.ALLOW_MODE_WRITE );
        targetPolyAttr.setCapability( PolygonAttributes.ALLOW_CULL_FACE_WRITE );
        targetPolyAttr.setCapability( PolygonAttributes.ALLOW_NORMAL_FLIP_WRITE );
        targetPolyAttr.setBackFaceNormalFlip( true );
        targetPolyAttr.setPolygonMode( PolygonAttributes.POLYGON_FILL );
        targetPolyAttr.setCullFace( PolygonAttributes.CULL_FRONT );
        //pa.setPolygonMode( PolygonAttributes.POLYGON_LINE );
        appearance.setPolygonAttributes( targetPolyAttr );
        
        if ( transparency != 0f ) {
            TransparencyAttributes transpAtt = 
                new TransparencyAttributes( TransparencyAttributes.BLENDED, transparency );
            appearance.setTransparencyAttributes( transpAtt );

        }
        
        return appearance;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ColoredSurface.java,v $
Revision 1.2  2006/11/27 16:57:02  bezema
added a javadoc

Revision 1.1  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.1  2006/10/23 09:01:25  ap
*** empty log message ***


********************************************************************** */