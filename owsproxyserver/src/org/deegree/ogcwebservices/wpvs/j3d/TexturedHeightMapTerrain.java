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
 
 This class uses some code fragments taken from J3D.org open source project
 which has been publish under LGPL at www.jd3.org.

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

// Standard imports
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.media.j3d.GeometryArray;
import javax.vecmath.Vector3f;

import org.deegree.framework.util.ImageUtils;
import org.j3d.geom.GeometryData;
import org.j3d.geom.UnsupportedTypeException;
import org.j3d.geom.terrain.ElevationGridGenerator;

/**
 * 
 * 
 * 
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.4 $, $Date: 2006/12/04 17:06:43 $
 */
public class TexturedHeightMapTerrain extends HeightMapTerrain {

    /**
     * @param width
     *            width of the terrains bbox
     * @param depth
     *            depth/height of the terrains bbox
     * @param heights
     *            terrain data; heights
     * @param translation of the lowerleft point to the Java3D model.
     * @param geometryType for a description see {@link HeightMapTerrain#HeightMapTerrain(float, float, float[][], Vector3f, int, boolean)}
     * @param centerTerrain 
     */
    public TexturedHeightMapTerrain( float width, float depth, float[][] heights, Vector3f translation, int geometryType, boolean centerTerrain ) {
        super( width, depth, heights, translation, geometryType, centerTerrain );
    }

    /**
     * No translation of the lowerleft point.
     * @param width
     *            width of the terrains bbox
     * @param depth
     *            depth/height of the terrains bbox
     * @param heights
     *            terrain data; heights
     * @param geometryType for a description see {@link HeightMapTerrain#HeightMapTerrain(float, float, float[][], Vector3f, int, boolean)}
     * @param centerTerrain 
     * @param texture
     */
    public TexturedHeightMapTerrain( float width, float depth, float[][] heights, int geometryType, boolean centerTerrain,
                                  BufferedImage texture ) {

        super( width, depth, heights, new Vector3f( 0, 0, 0 ), geometryType, centerTerrain );
        setTexture( texture );
    }

    /**
     * No translation of the lowerleft point.
     * @param width
     *            width of the terrains bbox
     * @param depth
     *            depth/height of the terrains bbox
     * @param heights
     *            terrain data; heights
     * @param geometryType for a description see {@link HeightMapTerrain#HeightMapTerrain(float, float, float[][], Vector3f, int, boolean)}
     * @param centerTerrain 
     * @param textureFile
     */
    public TexturedHeightMapTerrain( float width, float depth, float[][] heights, int geometryType, boolean centerTerrain, URL textureFile ) {

        super( width, depth, heights, new Vector3f( 0, 0, 0 ), geometryType, centerTerrain );
        try {
            setTexture(ImageUtils.loadImage( textureFile ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Must be called before rendering the terrain!!i<br>
     */
    @Override
    public void createTerrain(  ) {

        ElevationGridGenerator gridGenerator = new ElevationGridGenerator(
                                                                           getTerrainWidth(),
                                                                           getTerrainDepth(),
                                                                           getTerrainHeights()[0].length,
                                                                           getTerrainHeights().length,
                                                                           getTranslation(),
                                                                           isTerrainCentered() );

        // set the terrain into the elevation grid handler
        gridGenerator.setTerrainDetail( getTerrainHeights(), 0 );

        GeometryData data = new GeometryData();
        data.geometryType = getGeometryType();
        data.geometryComponents = GeometryData.NORMAL_DATA;
        if ( getTexture() != null ){
            data.geometryComponents |= GeometryData.TEXTURE_2D_DATA;
        }
        try {
            gridGenerator.generate( data );
        } catch ( UnsupportedTypeException ute ) {
            System.out.println( "Geometry type is not supported" );
        }
        int format = GeometryArray.COORDINATES | GeometryArray.NORMALS;
        if ( getTexture() != null )
            format |= GeometryArray.TEXTURE_COORDINATE_2;
        GeometryArray geom = createGeometryArray( data, format );

        geom.setCoordinates( 0, data.coordinates );
        
//        try {
//            File tmpFile = File.createTempFile( "stripedata", ".txt" );
//            tmpFile.deleteOnExit();
//            PrintStream ps = new PrintStream( new BufferedOutputStream(  new FileOutputStream( tmpFile ) ) );
//            System.out.println( ps + ": outputting coords.");
//            for( int i = 0; i+2<  data.coordinates.length ; i += 3 ){
//                ps.println ( data.coordinates[i] + " " + data.coordinates[i+1] + " " + data.coordinates[i+2] );
//            }
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        geom.setNormals( 0, data.normals );
        if ( getTexture() != null ){
//                File f = File.createTempFile("texcoords", ".txt");
//                f.deleteOnExit();
//                PrintStream ps = new PrintStream( new BufferedOutputStream(  new FileOutputStream( f ) ) );
//                System.out.println( ps + ": outputting texcoord.");
//                for( int i = 0; i<  data.textureCoordinates.length ; ++i ){
//                    ps.println( data.textureCoordinates[i] );
//                }
            geom.setTextureCoordinates( 0, 0, data.textureCoordinates );
        }
        
        setGeometry( geom );
//        addTexture();

    }

//    private void addTexture() {
//        Appearance appearance = getAppearance();
//        appearance.setMaterial( targetMaterial );
//
//        PolygonAttributes targetPolyAttr = new PolygonAttributes();
//        int capabilities = PolygonAttributes.ALLOW_MODE_WRITE | 
//                           PolygonAttributes.ALLOW_CULL_FACE_WRITE |
//                           PolygonAttributes.ALLOW_NORMAL_FLIP_WRITE | 
//                           PolygonAttributes.POLYGON_FILL;
//        targetPolyAttr.setCapability( capabilities );
//        appearance.setPolygonAttributes( targetPolyAttr );
//        if ( getTexture() != null ) {
//            try {
//                Texture texture = new TextureLoader( getTexture() ).getTexture();
//                texture.setEnable( true );
//                texture.setCapability( Texture.ALLOW_ENABLE_WRITE );
//                appearance.setTexture( texture );
//            } catch ( Exception e ) {
//                e.printStackTrace();
//            }
//            setCapability( Shape3D.ALLOW_GEOMETRY_WRITE );
//            setAppearance( appearance );
//        }
//    }

}
