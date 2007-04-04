//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/TerrainModel.java,v 1.3 2006/11/30 11:26:27 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wpvs.j3d;

import java.awt.image.BufferedImage;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * The <code>TerrainModel</code> class TODO add documentation here
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version $Revision: 1.3 $, $Date: 2006/11/30 11:26:27 $
 * 
 */

public abstract class TerrainModel extends Shape3D {

    private BufferedImage textureImage = null;

    /**
     * Creates a TerrainModel with a default Appearance set.
     * <p>
     * The default apearance of this terrain is defined as:
     * <ul>
     * <li> specularColor = new Color3f( 0.7f, 0.7f, 0.7f )</li>
     * <li> ambientColor = white </li>
     * <li> diffuseColor = white </li>
     * <li> shininess = 75f </li>
     * <li> lighting is enabled </li>
     * <li> matieral is writable (Material.ALLOW_COMPONENT_WRITE) </li>
     * </ul>
     * </p>
     */
    protected TerrainModel() {
        setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
        setAppearance( createDefaultApperance() );
    }

    protected TerrainModel( BufferedImage texture ) {
        super();
        this.textureImage = texture;
    }

    /**
     * This method implements all the necessary steps to generate a Shape3D Terrain (Elevation
     * model). Before rendering this Class this method should therefor be called prior.
     */
    public abstract void createTerrain();

    /**
     * Creates a J3D Appearance for the surface
     * 
     * @return a new Appearance object
     */
    private Appearance createDefaultApperance() {

        Color3f specular = new Color3f( 0.7f, 0.7f, 0.7f );
        Color3f white = new Color3f( 1, 1, 1 );

        // Now the geometry. Let's just add a couple of the basic primitives
        // for testing.
        Material targetMaterial = new Material();
        targetMaterial.setAmbientColor( white );
        targetMaterial.setDiffuseColor( white );
        targetMaterial.setSpecularColor( specular );
        targetMaterial.setShininess( 75.0f );
        targetMaterial.setLightingEnable( true );
        targetMaterial.setCapability( Material.ALLOW_COMPONENT_WRITE );

        
        Appearance appearance = new Appearance();
        appearance.setMaterial( targetMaterial );
        
        PolygonAttributes targetPolyAttr = new PolygonAttributes();
        targetPolyAttr.setPolygonMode( PolygonAttributes.POLYGON_FILL);
        targetPolyAttr.setCullFace( PolygonAttributes.CULL_NONE );
        appearance.setPolygonAttributes( targetPolyAttr );

        return appearance;
    }

    /**
     * @param textureImage
     *            An other texture value.
     */
    public void setTexture( BufferedImage textureImage ) {
        if ( textureImage != null ) {
            this.textureImage = textureImage;
            Appearance appearance = getAppearance();

            //                               | PolygonAttributes.ALLOW_CULL_FACE_WRITE;
//            int capabilities = PolygonAttributes.ALLOW_MODE_WRITE
//                               | PolygonAttributes.ALLOW_NORMAL_FLIP_WRITE
//                               | 
//                               | PolygonAttributes.CULL_NONE
//                               | PolygonAttributes.ALLOW_NORMAL_FLIP_READ;

//            appearance.setTextureAttributes( new TextureAttributes( TextureAttributes.MODULATE, 
//                                                                    new Transform3D(),
//                                                                    new Color4f( 1, 1, 1, 1 ), 
//                                                                    TextureAttributes.NICEST ) );
            Texture texture = new TextureLoader( textureImage, TextureLoader.GENERATE_MIPMAP ).getTexture();
            texture.setEnable( true );
            texture.setAnisotropicFilterMode( Texture.ANISOTROPIC_SINGLE_VALUE );
            //System.out.println( "\t\tNumber Of MIPMAPS->" + texture.numMipMapLevels() );
            texture.setCapability( Texture.ALLOW_ENABLE_WRITE | Texture.ALLOW_MIPMAP_MODE_READ | Texture.NICEST | Texture.CLAMP_TO_EDGE );
            appearance.setTexture( texture );
            setAppearance( appearance );
        }

    }

    /**
     * @return the BufferedImage which can be used as a texture or <tt>null</tt>if no texture was
     *         defined.
     */
    public BufferedImage getTexture() {
        return this.textureImage;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: TerrainModel.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/11/30 11:26:27  bezema
 * Changes to this class. What the people have been up to: working on the raster heightmap elevationmodel
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/

