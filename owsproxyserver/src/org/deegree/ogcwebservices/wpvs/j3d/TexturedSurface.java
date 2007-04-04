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
import java.io.IOException;
import java.net.URL;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Vector4f;

import org.deegree.framework.util.ImageUtils;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * 
 * 
 * 
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/11/27 16:56:00 $
 * 
 * @since 2.0
 */
public class TexturedSurface extends ColoredSurface {

    private Texture texture = null;

    private BufferedImage textureImg = null;

    private float[][] textureCoords = null;
    
    /**
     * creates a TexturedSurface from a geometry, color informations and a texture image. Since a
     * texture image be somehow transparent it is useful to be able to define a surfaces color. This
     * constructor will use default coordinates to adjust a texture onto a surface.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param red
     * @param green
     * @param blue
     * @param transparency
     * @param textureImg
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, float red,
                           float green, float blue, float transparency, BufferedImage textureImg ) {
        super( objectID, parentID, surface, red, green, blue, transparency );
        this.textureImg = textureImg;
        createTexture( textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, Material and a texture image. Since a texture
     * image be somehow transparent it is useful to be able to define a surfaces color. This
     * constructor will use default coordinates to adjust a texture onto a surface.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param material
     * @param transparency
     * @param textureImg
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, Material material,
                           float transparency, BufferedImage textureImg ) {
        super( objectID, parentID, surface, material, transparency );
        this.textureImg = textureImg;
        createTexture( textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, color informations and a reference to a texture
     * image. Since a texture image be somehow transparent it is useful to be able to define a
     * surfaces color. This constructor will use default coordinates to adjust a texture onto a
     * surface.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param red
     * @param green
     * @param blue
     * @param transparency
     * @param textureImg
     * @throws IOException
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, float red,
                           float green, float blue, float transparency, URL textureImg )
                            throws IOException {
        super( objectID, parentID, surface, red, green, blue, transparency );
        this.textureImg = ImageUtils.loadImage( textureImg );
        createTexture( this.textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, Material and a reference to a texture image. Since
     * a texture image be somehow transparent it is useful to be able to define a surfaces color.
     * This constructor will use default coordinates to adjust a texture onto a surface.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param material
     * @param transparency
     * @param textureImg
     * @throws IOException
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, Material material,
                           float transparency, URL textureImg ) throws IOException {
        super( objectID, parentID, surface, material, transparency );

        this.textureImg = ImageUtils.loadImage( textureImg );
        createTexture( this.textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, color informations and a texture image. Since a
     * texture image be somehow transparent it is useful to be able to define a surfaces color.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param red
     * @param green
     * @param blue
     * @param transparency
     * @param textureImg
     * @param textureCoords
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, float red,
                           float green, float blue, float transparency, BufferedImage textureImg,
                           float[][] textureCoords ) {
        super( objectID, parentID, surface, red, green, blue, transparency );

        this.textureImg = textureImg;
        this.textureCoords = textureCoords;
        createTexture( textureImg );
        setAppearance( createAppearance() );

    }

    /**
     * creates a TexturedSurface from a geometry, Material and a texture image. Since a texture
     * image be somehow transparent it is useful to be able to define a surfaces color.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param material
     * @param transparency
     * @param textureImg
     * @param textureCoords
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, Material material,
                           float transparency, BufferedImage textureImg, float[][] textureCoords ) {
        super( objectID, parentID, surface, material, transparency );

        this.textureImg = textureImg;
        this.textureCoords = textureCoords;
        createTexture( textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, color informations and a reference to a texture
     * image. Since a texture image be somehow transparent it is useful to be able to define a
     * surfaces color.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param red
     * @param green
     * @param blue
     * @param transparency
     * @param textureImg
     * @param textureCoords
     * @throws IOException
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, float red,
                           float green, float blue, float transparency, URL textureImg,
                           float[][] textureCoords ) throws IOException {
        super( objectID, parentID, surface, red, green, blue, transparency );

        this.textureImg = ImageUtils.loadImage( textureImg );
        this.textureCoords = textureCoords;
        createTexture( this.textureImg );
        setAppearance( createAppearance() );
    }

    /**
     * creates a TexturedSurface from a geometry, Material and a reference to a texture image. Since
     * a texture image be somehow transparent it is useful to be able to define a surfaces color.
     * 
     * @param objectID
     *            an Id for this Surface, for example a db primary key
     * @param parentID
     *            an Id for the parent of this Surface, for example if this is a wall the parent is
     *            the building.
     * @param surface
     *            the ogc:geometry surface which holds the point references of a polygon, not to be
     *            confused with a j3d Object which this class represents.
     * @param material
     * @param transparency
     * @param textureImg
     * @param textureCoords
     * @throws IOException
     */
    public TexturedSurface( String objectID, String parentID, Surface surface, Material material,
                           float transparency, URL textureImg, float[][] textureCoords )
                            throws IOException {
        super( objectID, parentID, surface, material, transparency );
        this.textureImg = ImageUtils.loadImage( textureImg );
        this.textureCoords = textureCoords;
        createTexture( this.textureImg );
        setAppearance( createAppearance() );
    }

    private void createTexture( BufferedImage textureImg ) {
        try {
            texture = new TextureLoader( textureImg ).getTexture();
            texture.setEnable( true );
            texture.setCapability( Texture.ALLOW_ENABLE_WRITE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return the texture of this surface.
     */
    public BufferedImage getTexture() {
        return textureImg;
    }

    /**
     * this method must be called before addin the surface to a Group
     */
    @Override
    public void compile() {

        GeometryInfo geometryInfo = new GeometryInfo( GeometryInfo.POLYGON_ARRAY );

        Position[] pos = surface.getSurfaceBoundary().getExteriorRing().getPositions();
        Ring[] innerRings = surface.getSurfaceBoundary().getInteriorRings();
        int k = 1;
        int l = 3 * ( pos.length  );
        if ( innerRings != null ) {
            for ( int i = 0; i < innerRings.length; i++ ) {
                k++;
                l += ( 3 * innerRings[i].getPositions().length );
            }
        }

        float[] coords = new float[l];
        int contourCounts[] = { k };
        int[] stripCounts = new int[k];
        k = 0;
        stripCounts[k++] = pos.length;

        int z = 0;
        for ( int i = 0; i < pos.length; i++ ) {
            coords[z++] = (float) pos[i].getX();
            coords[z++] = (float) pos[i].getY();
            coords[z++] = (float) pos[i].getZ();
        }

        if ( innerRings != null ) {
            for ( int j = 0; j < innerRings.length; j++ ) {
                pos = innerRings[j].getPositions();
                stripCounts[k++] = pos.length;
                for ( int i = 0; i < pos.length; i++ ) {
                    coords[z++] = (float) pos[i].getX();
                    coords[z++] = (float) pos[i].getY();
                    coords[z++] = (float) pos[i].getZ();
                }
            }
        }

        geometryInfo.setCoordinates( coords );
        geometryInfo.setStripCounts( stripCounts );
        geometryInfo.setContourCounts( contourCounts );

        // manually set texture coordinates must be done befor
        // normals all calculated
        if ( textureCoords != null ) {
            geometryInfo.setTextureCoordinateParams( textureCoords.length, 2 );
            for ( int i = 0; i < textureCoords.length; i++ ) {
                geometryInfo.setTextureCoordinates( i, textureCoords[i] );
            }
        }

        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals( geometryInfo );

        setGeometry( geometryInfo.getGeometryArray() );

        setAppearanceOverrideEnable( true );
    }

    private Appearance createAppearance() {
        Appearance ap = getAppearance();
        ap.setTexture( texture );
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode( TextureAttributes.MODULATE );
        ap.setTextureAttributes( texAttr );

        if ( textureCoords == null ) {
            // automatic creation of texture coordinates is behavior of
            // the appearance
            TexCoordGeneration tcg = new TexCoordGeneration(
                                                             TexCoordGeneration.OBJECT_LINEAR,
                                                             TexCoordGeneration.TEXTURE_COORDINATE_2 );
            tcg.setPlaneS( new Vector4f( 1, 1, 0, 0 ) );
            tcg.setPlaneT( new Vector4f( 0, 0, 1, 0 ) );
            ap.setTexCoordGeneration( tcg );
        }
        return ap;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: TexturedSurface.java,v $
 * Changes to this class. What the people have been up to: Revision 1.2  2006/11/27 16:56:00  bezema
 * Changes to this class. What the people have been up to: switched z and y values
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision
 * 1.1 2006/10/23 09:01:25 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/