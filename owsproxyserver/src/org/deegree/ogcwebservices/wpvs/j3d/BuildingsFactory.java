//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/BuildingsFactory.java,v 1.19 2006/11/27 09:07:52 poth Exp $
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


/**
 * Factory class for creating Java3D objects out of CityGMl features. This class is project-specific 
 * and won't be available in future releases of deegree.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.19 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
//public class BuildingsFactory implements ThreeDObjectFactory {
//    
//    private static ILogger LOG = LoggerFactory.getLogger( BuildingsFactory.class );
//
//    private static final Color DEFAULT_BUILDING_COLOR;
//
//    private static final URI APP_URI;
//
//    static {
//        URI tmp = null;
//        try {
//            tmp = new URI( "http://www.deegree.org/app" );
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        } finally {
//            APP_URI = tmp;
//        }
//
//        Color tmpColor = Color.LIGHT_GRAY.brighter();
//        try {
//            tmpColor = Color.decode( BuildingsFactoryProperties.getString( "BuildingsFactory.DEFAULT_BUILDING_COLOR" ) );
//        } catch ( Exception e ) {
//            // TODO log
//            e.printStackTrace();
//        }
//        DEFAULT_BUILDING_COLOR = tmpColor;
//
//    }
//
//    private static final QualifiedName FRONT_TEXT_QNAME = new QualifiedName( "app:front_texture",
//                                                                             APP_URI );
//
//    private static final QualifiedName FRONT_RED_QNAME = new QualifiedName( "app:front_color_red",
//                                                                            APP_URI );
//
//    private static final QualifiedName FRONT_GREEN_QNAME = new QualifiedName(
//                                                                              "app:front_color_green",
//                                                                              APP_URI );
//
//    private static final QualifiedName FRONT_BLUE_QNAME = new QualifiedName(
//                                                                             "app:front_color_blue",
//                                                                             APP_URI );
//
//    private static final QualifiedName FRONT_TEX_QNAME = new QualifiedName(
//                                                                            "app:front_texture_coordinates",
//                                                                            APP_URI );
//
//    private static final QualifiedName FRONT_OPACITY_QNAME = new QualifiedName(
//                                                                                "app:front_opacity",
//                                                                                APP_URI );
//
//    private static Map<URL, Texture> imagesCache;
//
//    /* the minimum altitude, used to put building on the floor */
//    //    private float minimumAltitude;
//    private boolean forceToZeroAltitude;
//
//    /**
//     * Creates a new factory for CityGML buildings
//     *
//     */
//    public BuildingsFactory() {
//        
//        imagesCache = new HashMap<URL, Texture>( 20 );
//        //        minimumAltitude = subtractMinAltitude ? Float.POSITIVE_INFINITY : 0f;
//    }
//    
//
//    public void setForceObjectsToZeroAltitude( boolean force ) {
//        this.forceToZeroAltitude = force;
//    }
//
//    /* (non-Javadoc)
//     * @see org.deegree.ogcwebservices.wpvs.j3d.FeatureCollectionAdapter#createJ3DGeometry()
//     */
//    public TriangleArray createJ3DGeometry() {
//        // TODO Auto-generated method stub
//        return null;
//    }
//    
//    /**
//     * 
//     * @see  ThreeDObjectFactory#create3DObjectsFromFeatureCollection
//     */
//    public List<Group> create3DObjectsFromFeatureCollection( FeatureCollection featureCollection ) {
//
//        List<Group> buildingGroup = new ArrayList<Group>( featureCollection.size() );
//
//        Feature[] features = featureCollection.toArray();
//        Group building = new Group();
//        
//        NormalGenerator normalGenerator = new NormalGenerator();
//        normalGenerator.setCreaseAngle( (float) Math.toRadians( 16 ) );
//
//        for ( Feature feature : features ) {
//
//            List<StyledSurface> list = new ArrayList<StyledSurface>();
//            collectStyledGeometries( feature, list );
//
//            float minimumAltitude = 0;
//
//            if ( forceToZeroAltitude ) { // calculate min of each feature (building)
//                minimumAltitude = Float.MAX_VALUE;
//                for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
//                    StyledSurface stylSurf = (StyledSurface) iter.next();
//                    Geometry geom = stylSurf.getGeom();
//                    float tmp = calcMinAltitude( geom );
//                    minimumAltitude = Math.min( minimumAltitude, tmp );
//                }
//            }
//
//            for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
//                StyledSurface stylSurf = (StyledSurface) iter.next();
//
//                Geometry geom = stylSurf.getGeom();
//
//                float[] coords = toCoords( geom, minimumAltitude );
//
//                if ( coords != null ) {
//                    float[] texCoords = stylSurf.getTexCoords();
//                    if ( texCoords == null ) {
//                        texCoords = toTextCoords( coords );
//                    }
//                    GeometryInfo geomInfo = new GeometryInfo( GeometryInfo.POLYGON_ARRAY );
//
//                    geomInfo.setCoordinates( coords );
//                    geomInfo.setTextureCoordinateParams( 1, 2 );
//                    geomInfo.setTextureCoordinates( 0, texCoords );
//
//                    geomInfo.setContourCounts( new int[] { 1 } );
//                    geomInfo.setStripCounts( new int[] { coords.length / 3 } );
//
//                    normalGenerator.generateNormals( geomInfo );
//                    GeometryArray ga = geomInfo.getGeometryArray();
//
//                    Shape3D shape = new Shape3D( ga );
//                    shape.setAppearance( createBuildingApperance( stylSurf ) );
//
//                    building.addChild( shape );
//
//                }
//            }
//        }
//        buildingGroup.add( building );
//
//        return buildingGroup;
//    }
//
//
//
////    /**
////     * 
////     * @see  ThreeDObjectFactory#create3DObjectsFromFeatureCollection
////     */
////    public List<Group> create3DObjectsFromFeatureCollection( FeatureCollection featureCollection ) {
////
////        List<Group> buildingGroup = new ArrayList<Group>( featureCollection.size() );
////
////        Feature[] features = featureCollection.toArray();
////        Group building = new Group();
////        
////        NormalGenerator normalGenerator = new NormalGenerator();
////        normalGenerator.setCreaseAngle( (float) Math.toRadians( 16 ) );
////
////        for ( Feature feature : features ) {
////
////            List<StyledSurface> list = new ArrayList<StyledSurface>();
////            collectStyledGeometries( feature, list );
////
////            float minimumAltitude = 0;
////
////            if ( forceToZeroAltitude ) { // calculate min of each feature (building)
////                minimumAltitude = Float.MAX_VALUE;
////                for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
////                    StyledSurface stylSurf = (StyledSurface) iter.next();
////                    Geometry geom = stylSurf.getGeom();
////                    float tmp = calcMinAltitude( geom );
////                    minimumAltitude = Math.min( minimumAltitude, tmp );
////                }
////            }
////
////            for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
////                StyledSurface stylSurf = (StyledSurface) iter.next();
////
////                Geometry geom = stylSurf.getGeom();
////
////                float[] coords = toCoords( geom, minimumAltitude );
////
////                if ( coords != null ) {
////                    float[] texCoords = stylSurf.getTexCoords();
////                    if ( texCoords == null ) {
////                        texCoords = toTextCoords( coords );
////                    }
////                    GeometryInfo geomInfo = new GeometryInfo( GeometryInfo.POLYGON_ARRAY );
////
////                    geomInfo.setCoordinates( coords );
////                    geomInfo.setTextureCoordinateParams( 1, 2 );
////                    geomInfo.setTextureCoordinates( 0, texCoords );
////
////                    geomInfo.setContourCounts( new int[] { 1 } );
////                    geomInfo.setStripCounts( new int[] { coords.length / 3 } );
////
////                    normalGenerator.generateNormals( geomInfo );
////                    GeometryArray ga = geomInfo.getGeometryArray();
////
////                    Shape3D shape = new Shape3D( ga );
////                    shape.setAppearance( createBuildingApperance( stylSurf ) );
////
////                    building.addChild( shape );
////
////                }
////            }
////        }
////        buildingGroup.add( building );
////
////        return buildingGroup;
////    }
//
//    /**
//     * Iterates over the properties of the feature and collect all found geometry properties in the list
//     * @param feature the feature from which all geometry objects (be them direct properties or properties
//     * of its properties) will be extracted and collected
//     * @param list the list into which to put the collected geometry properties and its style information
//     */
//    private void collectStyledGeometries( Feature feature, List<StyledSurface> list ) {
//
//        FeatureProperty[] props = feature.getProperties();
//        if ( props != null ) {
//            for ( int i = 0; i < props.length; i++ ) {
//
//                Object value = props[i].getValue();
//                if ( value != null ) {
//
//                    if ( value instanceof DefaultFeature ) {
//                        DefaultFeature newFeature = (DefaultFeature) value;
//
//                        Geometry geom = newFeature.getDefaultGeometryPropertyValue();
//
//                        //calcMinAltitude( geom );
//
//                        Object tex = null;
//                        if ( newFeature != null ) {
//
//                            FeatureProperty featureProperty = newFeature.getDefaultProperty( FRONT_TEXT_QNAME );
//                            if ( featureProperty != null ) {
//                                tex = featureProperty.getValue();
//                            }
//
//                            Object r = null;
//                            featureProperty = newFeature.getDefaultProperty( FRONT_RED_QNAME );
//                            if ( featureProperty != null ) {
//                                r = featureProperty.getValue();
//                            }
//                            Object g = null;
//                            featureProperty = newFeature.getDefaultProperty( FRONT_GREEN_QNAME );
//                            if ( featureProperty != null ) {
//                                g = featureProperty.getValue();
//                            }
//                            Object b = null;
//                            featureProperty = newFeature.getDefaultProperty( FRONT_BLUE_QNAME );
//                            if ( featureProperty != null ) {
//                                b = featureProperty.getValue();
//                            }
//
//                            Object o = null;
//                            float opacity = 1f;
//
//                            featureProperty = newFeature.getDefaultProperty( FRONT_OPACITY_QNAME );
//                            if ( featureProperty != null ) {
//                                o = featureProperty.getValue();
//                                if ( o != null ) {
//                                    opacity = ( (Double) o ).floatValue();
//                                }
//                            }
//
//                            Color c = DEFAULT_BUILDING_COLOR;
//                            if ( r != null && g != null && b != null ) {
//
//                                try {
//                                    c = new Color( ( (Double) r ).floatValue(),
//                                                   ( (Double) g ).floatValue(),
//                                                   ( (Double) b ).floatValue() );
//                                } catch ( Exception e ) {
//                                    LOG.logError( e.getMessage(), e );
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            float[] texCoords = null;
//                            featureProperty = newFeature.getDefaultProperty( FRONT_TEX_QNAME );
//                            if ( featureProperty != null ) {
//                                b = featureProperty.getValue();
//                                if ( b != null ) {
//                                    texCoords = toTextCoords( (Geometry) b );
//                                }
//                            }
//
//                            if ( geom != null ) {
//
//                                list.add( new StyledSurface( geom, c, (String) tex, texCoords,
//                                                             opacity ) );
//                            }
//
//                        }
//                        collectStyledGeometries( newFeature, list );
//
//                    }
//                }
//
//            }
//
//        }
//
//    }
//
//    /**
//     * Creates a J3D Appearance for the styled surface 
//     * @param stylSurf the object containing geometry and style info (texture, colors , etc.) 
//     * @return a new Appearance object
//     */
//    private static Appearance createBuildingApperance( StyledSurface stylSurf ) {
//
//        Appearance app = new Appearance();
//
//        Color3f defaultColor = new Color3f( stylSurf.getForeColor() );
//
//        ColoringAttributes ca = new ColoringAttributes();
//        ca.setShadeModel( ColoringAttributes.SHADE_GOURAUD );
//        app.setColoringAttributes( ca );
//
//        Material material = new Material();
//        material.setAmbientColor( defaultColor );
//        material.setDiffuseColor( defaultColor );
//        float s = 0.0f;
//        material.setSpecularColor( new Color3f( s, s, s ) );
//        material.setShininess( 1f );
//        material.setLightingEnable( true );
//        //        float e = 0.145f;
//        //        material.setEmissiveColor( e, e, e );
//
//        app.setMaterial( material );
//
//        String tex = stylSurf.getTexture();
//        String texUrlString = stylSurf.getTexture();
//        /*
//        if ( tex != null && texUrlString != null && texUrlString.length() != 0 ) {
//
//            TextureAttributes texAttr = new TextureAttributes();
//            texAttr.setTextureMode( TextureAttributes.MODULATE );
//            app.setTextureAttributes( texAttr );
//
//            URL texUrl = null;
//            try {
//
//                texUrl = new URL( texUrlString );
//                Texture texture = imagesCache.get( texUrl );
//                if ( texture == null ) {
//                    texture = new TextureLoader( texUrl, null ).getTexture();
//                }
//                app.setTexture( texture );
//
//            } catch ( Exception e ) {
//                LOG.logError( e.getMessage(), e );
//            }
//
//        }
//        */
//        PolygonAttributes pa = new PolygonAttributes();
//        pa.setCullFace( PolygonAttributes.CULL_NONE );
//        pa.setBackFaceNormalFlip( true );
//        //      pa.setPolygonMode( PolygonAttributes.POLYGON_LINE );
//        app.setPolygonAttributes( pa );
//
//        float transpareny = 1f - stylSurf.getOpacity();
//        if ( transpareny != 0f ) {
//            TransparencyAttributes transpAtt = new TransparencyAttributes(
//                                                                           TransparencyAttributes.BLENDED,
//                                                                           transpareny );
//            app.setTransparencyAttributes( transpAtt );
//
//        }
//
//        return app;
//    }
//
//    /**
//     * Reads the exterior rigns of Surfaces only and transform them into a float array
//     * @param geom the Surface geometry (other types are ignored)
//     * @return a new float array with -x0,z0,y0, -x1,z1,y1, -xn,zn,yn, etc
//     */
//    private float[] toCoords( Geometry geom, float minimumAltitude ) {
//
//        //TODO really check if Polygon???
//        // TODO interior rings...
//
//        float[] coords = null;
//
//        if ( geom instanceof Surface ) {
//
//            Surface p = (Surface) geom;
//            Position[] positions = p.getSurfaceBoundary().getExteriorRing().getPositions();
//            coords = new float[3 * ( positions.length - 1 )];
//
//            for ( int i = 0; i < positions.length - 1; i++ ) {
//
//                int ix = 3 * i;
//                coords[ix] = (float) positions[i].getX();
//                coords[ix + 1] = (float) positions[i].getY();
//                float f = (float) positions[i].getZ() - minimumAltitude;
//                coords[ix + 2] = f;
//
//            }
//
//        }
//        return coords;
//    }
//
//    /**
//     * @see toCoords( Geometry )
//     */
//    private float[] toTextCoords( float[] coords ) {
//
//        float[] texCoords = null;
//
//        if ( coords.length == 12 ) {
//            return new float[] { 0, 1, 1, 1, 1, 0, 0, 0 };
//        }
//        float minX = Float.POSITIVE_INFINITY;
//        float minY = Float.POSITIVE_INFINITY;
//        float maxX = Float.NEGATIVE_INFINITY;
//        float maxY = Float.NEGATIVE_INFINITY;
//
//        for ( int i = 0; i < coords.length; i += 3 ) {
//            float x = -coords[i];
//
//            if ( x < minX ) {
//                minX = x;
//            }
//            if ( x > maxX ) {
//                maxX = x;
//            }
//
//            float y = coords[i + 2];
//            if ( y < minY ) {
//                minY = y;
//            }
//            if ( y > maxY ) {
//                maxY = y;
//            }
//
//        }
//
//        float w = maxX - minX;
//        if ( w == 0 ) {
//            w = 1f;
//        }
//        float h = maxY - minY;
//        if ( h == 0 ) {
//            h = 1f;
//        }
//
//        texCoords = new float[coords.length / 3 * 2];
//
//        int c = 0;
//        for ( int i = 0; i < coords.length; i += 3 ) {
//            texCoords[c] = Math.abs( ( -coords[i] - minX ) / w );
//            texCoords[c + 1] = ( coords[i + 2] - minY ) / h;
//
//            c += 2;
//        }
//
//        return texCoords;
//    }
//
//    private float[] toTextCoords( Geometry texPoly ) {
//
//        float[] coords = null;
//
//        if ( texPoly instanceof Surface ) {
//
//            Surface p = (Surface) texPoly;
//            Position[] positions = p.getSurfaceBoundary().getExteriorRing().getPositions();
//            coords = new float[2 * ( positions.length - 1 )];
//
//            for ( int i = 0; i < positions.length - 1; i++ ) {
//
//                int ix = 2 * i;
//                coords[ix] = (float) positions[i].getX();
//                coords[ix + 1] = (float) positions[i].getY();
//            }
//        }
//        return coords;
//    }
//
//    /**
//     * Iterates over positions of the exterior rig of the geom, assuming it's a Surface,
//     * and update the minimum altitude value of this obejtc.
//     * @param geom the geometry from which minimum altitude value will be calculated
//     */
//    private float calcMinAltitude( Geometry geom ) {
//
//        float minimumAltitude = Float.POSITIVE_INFINITY;
//
//        if ( geom instanceof Surface ) {
//
//            Surface p = (Surface) geom;
//            Position[] positions = p.getSurfaceBoundary().getExteriorRing().getPositions();
//
//            for ( int i = 0; i < positions.length - 1; i++ ) {
//                float z = (float) positions[i].getZ();
//                minimumAltitude = Math.min( minimumAltitude, z );
//
//            }
//        } else {
//            //safer to return 0 because it is used in subtraction ;-)
//            minimumAltitude = 0;
//        }
//
//        return minimumAltitude;
//    }
//
//    /**
//     * 
//     * A convenience class to pack together geometry and style information for buildings. 
//     * 
//     * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
//     * @author last edited by: $Author: poth $
//     * 
//     * @version 2.0, $Revision: 1.19 $, $Date: 2006/11/27 09:07:52 $
//     * 
//     * @since 2.0
//     */
//    private static class StyledSurface {
//
//        private final Geometry geom;
//
//        private final Color foreColor;
//
//        private final String tex;
//
//        private final float[] texCoords;
//
//        private final float opacity;
//
//        /**
//         * Createa new instance of a Styled Suface.
//         * @param g the object geometry
//         * @param foregroundColor the foreground color
//         * @param texture the URL of the texture
//         * @param textureCoords the array with the texture coordinates 
//         * @param opacity the opacity value
//         */
//        StyledSurface( Geometry g, Color foregroundColor, String texture, float[] textureCoords,
//                      float opacity ) {
//            this.geom = g;
//            this.texCoords = textureCoords;
//            this.foreColor = foregroundColor;
//            this.tex = texture;
//            this.opacity = opacity;
//        }
//
//        public Color getForeColor() {
//            return foreColor;
//        }
//
//        public Geometry getGeom() {
//            return geom;
//        }
//
//        public String getTexture() {
//            return tex;
//        }
//
//        public float[] getTexCoords() {
//            return texCoords;
//        }
//
//        public float getOpacity() {
//            return opacity;
//        }
//    }
//}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: BuildingsFactory.java,v $
 Revision 1.19  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.18  2006/11/23 11:46:40  bezema
 The initial version of the new wpvs

 Revision 1.17  2006/07/05 15:58:58  poth
 bug fix - calculating objects minimum z-value

 Revision 1.16  2006/07/05 14:08:04  taddei
 attempt to put budligns at z = 0; buggy for reasons not understood

 Revision 1.15  2006/07/05 11:24:02  taddei
 now sets EM to zero plane (if EM == null)

 Revision 1.14  2006/07/04 09:08:06  taddei
 buildings factory props, and possibility of setting builds to z = 0

 Revision 1.13  2006/06/20 10:16:01  taddei
 clean up and javadoc

 Revision 1.12  2006/06/20 07:45:49  taddei
 removed println

 Revision 1.11  2006/05/12 13:13:16  taddei
 changed light props

 Revision 1.10  2006/05/05 12:42:09  taddei
 added properties for material constants

 Revision 1.9  2006/04/28 15:05:49  taddei
 null checking and clean up

 Revision 1.8  2006/04/18 18:20:26  poth
 *** empty log message ***

 Revision 1.7  2006/04/06 20:25:28  poth
 *** empty log message ***

 Revision 1.6  2006/04/05 08:58:28  taddei
 supports tex now (if not in fc, then create some default)

 Revision 1.4  2006/03/29 15:03:23  taddei
 with + or -  working texs

 Revision 1.3  2006/03/27 14:15:17  taddei
 code for conputing text coords

 Revision 1.2  2006/03/27 08:35:25  taddei
 working: color, normal (no tex yet)

 Revision 1.1  2006/03/24 08:52:21  taddei
 factory for building buildings


 ********************************************************************** */