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

import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;

import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * 
 * 
 * 
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
public class DefaultSurface extends Shape3D {

    protected Surface surface;

    private String parentID;

    private String objectID;

    /**
     * 
     * @param objectID
     * @param parentID
     * @param surface
     */
    public DefaultSurface( String objectID, String parentID, Surface surface ) {
        super();
        this.surface = surface;
        this.parentID = parentID;
        this.objectID = objectID;

        /*
        System.out.println( "new Appearance" );
        Color3f specular = new Color3f( 0.7f, 0.7f, 0.7f );
        Color3f white = new Color3f( 0.4f, 1, 0.4f );

        Material targetMaterial = new Material();
        targetMaterial.setAmbientColor( white );
        targetMaterial.setDiffuseColor( white );
        targetMaterial.setSpecularColor( specular );
        targetMaterial.setShininess( 75.0f );
        targetMaterial.setLightingEnable( true );
        targetMaterial.setCapability( Material.ALLOW_COMPONENT_WRITE );
//
        ColoringAttributes ca = new ColoringAttributes();
        ca.setShadeModel( ColoringAttributes.SHADE_GOURAUD );
//
        Appearance defaultAppearance = new Appearance();
        defaultAppearance.setMaterial( targetMaterial );
        defaultAppearance.setColoringAttributes( ca );
//
        PolygonAttributes targetPolyAttr = new PolygonAttributes();
        targetPolyAttr.setCapability( PolygonAttributes.ALLOW_MODE_WRITE );
        targetPolyAttr.setCapability( PolygonAttributes.ALLOW_NORMAL_FLIP_WRITE );
        targetPolyAttr.setPolygonMode( PolygonAttributes.POLYGON_FILL );
        targetPolyAttr.setCullFace( PolygonAttributes.CULL_NONE );
        // pa.setPolygonMode( PolygonAttributes.POLYGON_LINE );
        defaultAppearance.setPolygonAttributes( targetPolyAttr );
        setAppearance( defaultAppearance );
        */
    }
    

    /**
     * @return the ID of the Object this Surface is a part of (e.g. the building id if this is a
     *         wall)
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * @return the objectID value.
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * @return a String composited of the parentID and "_" and the objectID
     */
    public String getDefaultSurfaceID() {
        return parentID + '_' + objectID;
    }

    /**
     * 
     * @return the surface geometry encapsulated
     */
    public Surface getSurfaceGeometry() {
        return surface;
    }

    /**
     * this method must be called before adding the surface to a Group
     */
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

        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals( geometryInfo );

        setGeometry( geometryInfo.getGeometryArray() );

        setAppearanceOverrideEnable( true );
        //setAppearance( getAppearance() );
    }
    
    public String getGeometryAsString( ){
        StringBuffer sb = new StringBuffer( numGeometries() );
        for( int i = 0; i < numGeometries();++i ){
            Geometry ga = getGeometry(i);
            sb.append( ga.toString() );
        }
        return sb.toString();        
    }


}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DefaultSurface.java,v $
 * Changes to this class. What the people have been up to: Revision 1.2  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/11/23 11:46:40  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to: Revision
 * 1.1 2006/10/23 09:01:25 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/