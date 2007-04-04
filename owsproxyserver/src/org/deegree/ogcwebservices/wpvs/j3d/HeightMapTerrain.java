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
import java.awt.Color;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleFanArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import org.j3d.geom.GeometryData;
import org.j3d.geom.UnsupportedTypeException;
import org.j3d.geom.terrain.ColorRampGenerator;
import org.j3d.geom.terrain.ElevationGridGenerator;

/**
 * 
 * 
 * 
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/12/04 17:06:43 $
 * 
 * @since 2.0
 */
public class HeightMapTerrain extends TerrainModel {

    /** Width of the terrain to generate */
    private float terrainWidth;

    /** Depth of the terrain to generate */
    private float terrainDepth;

    /** The last generated terrain heights */
    private float[][] terrainHeights;

    /** coloring informations */
    private ColorRampGenerator colorGenerator;

    private int geometryType;

    private boolean centerTerrain;

    private Vector3f translation;

    /**
     * 
     * @param width
     *            width of the terrains bbox
     * @param depth
     *            depth/height of the terrains bbox
     * @param heights
     *            terrain data; heights
     * @param translation
     *            of the lowerleft point of the heightmap to the lowerleftpoint of the Java3D model.
     * @param geometryType
     *            defines the type/format of the used GeometryArray. supported are:
     *            <ul>
     *            <li>GeometryData.TRIANGLES
     *            <li>GeometryData.QUADS
     *            <li>GeometryData.INDEXED_QUADS
     *            <li>GeometryData.INDEXED_TRIANGLES
     *            <li>GeometryData.TRIANGLE_STRIPS
     *            <li>GeometryData.TRIANGLE_FANS
     *            <li>GeometryData.INDEXED_TRIANGLE_STRIPS:
     *            <li>GeometryData.INDEXED_TRIANGLE_FANS
     *            </ul>
     * @param centerTerrain
     */
    public HeightMapTerrain( float width, float depth, float[][] heights, Vector3f translation,
                            int geometryType, boolean centerTerrain ) {
        terrainWidth = width;
        terrainDepth = depth;
        this.terrainHeights = heights;
        this.translation = translation;
        colorGenerator = createDefaultColorGenerator();
        this.geometryType = geometryType;
        this.centerTerrain = centerTerrain;
    }

    /**
     * 
     * @param width
     *            width of the terrains bbox
     * @param depth
     *            depth/height of the terrains bbox
     * @param heights
     *            terrain data; heights
     * @param geometryType
     *            for a description see
     *            {@link HeightMapTerrain#HeightMapTerrain(float, float, float[][], Vector3f, int, boolean)}
     * @param centerTerrain
     * @param colorGenerator
     */
    public HeightMapTerrain( float width, float depth, float[][] heights, int geometryType,
                            boolean centerTerrain, ColorRampGenerator colorGenerator ) {

        terrainWidth = width;
        terrainDepth = depth;
        this.terrainHeights = heights;
        this.colorGenerator = colorGenerator;
        if ( this.colorGenerator == null )
            this.colorGenerator = createDefaultColorGenerator();
        this.geometryType = geometryType;
        this.centerTerrain = centerTerrain;
    }

    /**
     * Generate height values only based on the current configuration.
     * 
     * @return The last generated height values
     */
    public float[][] getTerrainHeights() {
        return terrainHeights;
    }

    /**
     * Must be called before rendering the terrain!!i<br>
     * 
     * 
     */
    @Override
    public void createTerrain() {

        ElevationGridGenerator gridGenerator = new ElevationGridGenerator(
                                                                           terrainWidth,
                                                                           terrainDepth,
                                                                           terrainHeights[0].length,
                                                                           terrainHeights.length,
                                                                           translation,
                                                                           centerTerrain );

        // set the terrain into the elevation grid handler
        gridGenerator.setTerrainDetail( terrainHeights, 0 );

        GeometryData data = new GeometryData();
        data.geometryType = geometryType;
        data.geometryComponents = GeometryData.NORMAL_DATA;

        try {
            gridGenerator.generate( data );
        } catch ( UnsupportedTypeException ute ) {
            System.out.println( "Geometry type is not supported" );
        }

        int format = GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3;
        colorGenerator.generate( data );

        GeometryArray geom = createGeometryArray( data, format );

        geom.setCoordinates( 0, data.coordinates );
        geom.setNormals( 0, data.normals );
        geom.setColors( 0, data.colors );

        setGeometry( geom );
    }

    /**
     * @return the terrainDepth value.
     */
    public float getTerrainDepth() {
        return terrainDepth;
    }

    /**
     * @return the terrainWidth value.
     */
    public float getTerrainWidth() {
        return terrainWidth;
    }

    /**
     * @return true if the Terrain should be centered.
     */
    public boolean isTerrainCentered() {
        return centerTerrain;
    }

    /**
     * @return the geometryType. For a description of possible values see
     *         {@link HeightMapTerrain#HeightMapTerrain(float, float, float[][], Vector3f, int, boolean)}
     */
    public int getGeometryType() {
        return geometryType;
    }

    /**
     * Methodfor creating a GeometryArray depending on the passed array format. supported are:
     * 
     * @param data
     *            actual geometry
     * @param format
     *            the Internal formats the GeometryArray should have see
     *            {@link GeometryArray#GeometryArray(int, int)};
     * @return a GeometryArray instantiated according to the GeometryType in the GeometryData.
     */
    protected GeometryArray createGeometryArray( GeometryData data, int format ) {
        GeometryArray geom = null;
        IndexedGeometryArray i_geom;

        switch ( data.geometryType ) {
        case GeometryData.TRIANGLES:
            geom = new TriangleArray( data.vertexCount, format );
            break;

        case GeometryData.QUADS:
            geom = new QuadArray( data.vertexCount, format );
            break;

        case GeometryData.INDEXED_QUADS:

            i_geom = new IndexedQuadArray( data.vertexCount, format, data.indexesCount );
            i_geom.setCoordinateIndices( 0, data.indexes );
            i_geom.setColorIndices( 0, data.indexes );
            i_geom.setNormalIndices( 0, data.indexes );
            geom = i_geom;
            break;
        case GeometryData.INDEXED_TRIANGLES:

            i_geom = new IndexedTriangleArray( data.vertexCount, format, data.indexesCount );
            i_geom.setCoordinateIndices( 0, data.indexes );
            i_geom.setColorIndices( 0, data.indexes );
            i_geom.setNormalIndices( 0, data.indexes );
            geom = i_geom;
            break;

        case GeometryData.TRIANGLE_STRIPS:
            geom = new TriangleStripArray( data.vertexCount, format, data.stripCounts );
            break;

        case GeometryData.TRIANGLE_FANS:
            geom = new TriangleFanArray( data.vertexCount, format, data.stripCounts );
            break;

        case GeometryData.INDEXED_TRIANGLE_STRIPS:
            i_geom = new IndexedTriangleStripArray( data.vertexCount, format, data.indexesCount,
                                                    data.stripCounts );
            i_geom.setCoordinateIndices( 0, data.indexes );
            i_geom.setColorIndices( 0, data.indexes );
            i_geom.setNormalIndices( 0, data.indexes );
            geom = i_geom;
            break;

        case GeometryData.INDEXED_TRIANGLE_FANS:
            i_geom = new IndexedTriangleFanArray( data.vertexCount, format, data.indexesCount,
                                                  data.stripCounts );
            i_geom.setCoordinateIndices( 0, data.indexes );
            i_geom.setColorIndices( 0, data.indexes );
            i_geom.setNormalIndices( 0, data.indexes );
            geom = i_geom;
            break;
        }
        return geom;
    }

    private ColorRampGenerator createDefaultColorGenerator() {
        Color3f[] colors = new Color3f[2];
        colors[0] = new Color3f( Color.WHITE );
        colors[1] = new Color3f( Color.BLACK );
        float[] heights = new float[2];
        heights[0] = -150;
        heights[1] = 1500;
        return new ColorRampGenerator( heights, colors );
    }

    /**
     * @return the translation value.
     */
    public Vector3f getTranslation() {
        return translation;
    }

}
