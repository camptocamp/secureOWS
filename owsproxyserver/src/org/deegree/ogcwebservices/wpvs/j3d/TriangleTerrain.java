//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/TriangleTerrain.java,v 1.1 2006/11/23 11:46:40 bezema Exp $
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
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * The <code>TriangleTerrain</code> class TODO add documentation here
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/11/23 11:46:40 $
 * 
 */

public class TriangleTerrain extends TerrainModel {

    private static final ILogger LOG = LoggerFactory.getLogger( TriangleTerrain.class );

    private Envelope boundingBox;

    private List<float[][]> triangles;

    /**
     * @param triangles
     *            to be connected to a Triangle(Geometry)Array
     * @param bbox
     *            a geometry representation of the BoundingBox of the TriangleArray (needed to scale
     *            the texturecoordinates).
     */
    public TriangleTerrain( List<float[][]> triangles, Envelope bbox ) {
        super( );
        this.triangles = triangles;
        boundingBox = bbox;
    }

    @Override
    public void createTerrain() {
        LOG.entering();
        double widthInv = 1d/boundingBox.getWidth();
        double heightInv = 1d/boundingBox.getHeight();
        Position lowerLeft = boundingBox.getMin();
        
        GeometryInfo geometryInfo = new GeometryInfo( GeometryInfo.TRIANGLE_ARRAY );
        
        BufferedImage texture = getTexture();
        if( texture != null )
            geometryInfo.setTextureCoordinateParams( 1, 2 );

        Point3f[] coordinates = new Point3f[triangles.size()*3];
        TexCoord2f[] texCoords = new TexCoord2f[triangles.size()*3];
        
        int coordNr = 0;
        for ( float[][] triangleCoords : triangles ) {
            for ( int k = 0; k < 3; k++ ) {
//                System.out.println( Thread.currentThread() + "-> coordNR: " + coordNr );
                Point3f modelCoordinate = new Point3f( triangleCoords[k][0], triangleCoords[k][1],
                                                       triangleCoords[k][2] );
                coordinates[coordNr] = modelCoordinate;
                
                if ( texture != null ) {
                    double texCoordX = ( modelCoordinate.x - lowerLeft.getX() ) * widthInv;
                    double texCoordY = ( modelCoordinate.y - lowerLeft.getY() ) * heightInv;
                    texCoords[coordNr] = new TexCoord2f( (float) texCoordX, (float) texCoordY );
                } 
                coordNr++;
            }

        }
        geometryInfo.setCoordinates( coordinates );
        if ( texture != null ){
            geometryInfo.setTextureCoordinates( 0, texCoords );
        }
        geometryInfo.recomputeIndices();
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals( geometryInfo );

        

        
        setGeometry( geometryInfo.getGeometryArray() );
        LOG.exiting();

        return;

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: TriangleTerrain.java,v $
 * Changes to this class. What the people have been up to: Revision 1.1  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/

