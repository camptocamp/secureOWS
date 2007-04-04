//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/Attic/AppearanceFactory.java,v 1.18 2006/11/27 09:07:52 poth Exp $
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
 * ... 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.18 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
//public class AppearanceFactory {
//    
//    private static final Color3f AMBIENT_COLOR;
//    private static final Color3f DIFFUSE_COLOR;
//    private static final Color3f SPECULAR_COLOR;
//    private static final Color3f EMISSIVE_COLOR;
//
//
//    static {
//        
//        //default
//        float a = 0.7f;
//        float d = 0.7f;
//        float s = 0.7f;
//        float e = 0.0f;
//        
//        try {
//            a = Float.parseFloat( MaterialConstants.getString("AppearanceFactory.AMBIENT_COLOR" ) ); //$NON-NLS-1$
//            d = Float.parseFloat( MaterialConstants.getString("AppearanceFactory.DIFFUSE_COLOR" ) ); //$NON-NLS-1$
//            s = Float.parseFloat( MaterialConstants.getString("AppearanceFactory.SPECULAR_COLOR" ) ); //$NON-NLS-1$
//            e = Float.parseFloat( MaterialConstants.getString("AppearanceFactory.EMISSIVE_COLOR" ) ); //$NON-NLS-1$
//            
//        } catch ( Exception ex ) {
//            //TODO log this
//            ex.printStackTrace();
//        }
//        
//        AMBIENT_COLOR = new Color3f( a, a, a );
//        DIFFUSE_COLOR = new Color3f( d, d, d );
//        SPECULAR_COLOR = new Color3f( s, s, s );
//        EMISSIVE_COLOR = new Color3f( e, e, e );
//         
//    }
//    
//	
//    private AppearanceFactory(){
//        //no instantiation
//    }
//    
//    /**
//     * Creates a Java3D <code>Appearance</code> object using <code>images</code> as texture.
//     * FIXME singleImage par is misleading. should have another method for handling
//     * single images, because the behaviour is completely different
//     * @param images images for the individual TIN tiles
//     * @return a new <code>Appearance</code> object 
//     */
//    public synchronized static Appearance createAppearance( BufferedImage[] images ) {
//            
//        Appearance app = new Appearance();
//            
//        app.setMaterial( createStandardMaterial() );
//        
//        applyTransparentAttributes( app, images);
//	    
//        
//        PolygonAttributes pa = new PolygonAttributes();
//        pa.setCullFace(PolygonAttributes.CULL_NONE);
//        pa.setBackFaceNormalFlip( true );
//
////        pa.setPolygonMode( PolygonAttributes.POLYGON_LINE );
//        app.setPolygonAttributes( pa );
//        
//        return app;
//    }
//
//    private static final Material createStandardMaterial(){
//    	
//        Material material = new Material();
//        material.setAmbientColor( AMBIENT_COLOR  );
//        material.setDiffuseColor( DIFFUSE_COLOR );
//        material.setSpecularColor( SPECULAR_COLOR );
//        material.setEmissiveColor( EMISSIVE_COLOR );
//        material.setShininess( 1f );
//        material.setLightingEnable( true );
//        
//        return material;
//    }
//    
//    
//    /* applies TransparenyAttributes and Texture to an Appearance, in order
//     * to make it partially transparent
//     */
//    private static void applyTransparentAttributes( Appearance app, 
//    		BufferedImage[] images ){
//
//    	TextureUnitState[] textureUnit = new TextureUnitState[ images.length ];  
//    	
//        for (int i = 0; i < textureUnit.length; i++) {
//
//            Texture texture = new TextureLoader( images[ i ] ).getTexture();
//            texture.setBoundaryModeS( Texture.CLAMP );
//            texture.setBoundaryModeT( Texture.CLAMP );
//            texture.setBoundaryColor( 0f,0f, 0f,0.00f);
//            
//            
//            TextureAttributes texAttr = new TextureAttributes();
//            texAttr.setTextureMode(TextureAttributes.DECAL);
//            
//            textureUnit[i] = new TextureUnitState( texture, texAttr, null);
//            textureUnit[i].setCapability(TextureUnitState.ALLOW_STATE_WRITE);
//            
//        }
//        
//        app.setTextureUnitState( textureUnit );
//    	
//    }    
//    
//}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AppearanceFactory.java,v $
Revision 1.18  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.17  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.16  2006/07/18 15:14:21  taddei
changes in DEM (WCS) geometry, and cleanup

Revision 1.14  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.13  2006/05/10 15:01:49  taddei
parameters now are equal to wts

Revision 1.12  2006/05/05 12:42:09  taddei
added properties for material constants

Revision 1.11  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.10  2006/04/05 08:57:30  taddei
added a bit of e color

Revision 1.8  2006/03/29 15:02:48  taddei
removed unused constants

Revision 1.7  2006/03/09 08:57:37  taddei
clean up

Revision 1.6  2006/03/02 15:32:52  taddei
using different standard material, transparency atts is now DECAL

Revision 1.5  2006/02/24 11:42:41  taddei
refactoring (background)

Revision 1.4  2006/02/21 12:58:17  taddei
commented out (still) unused code

Revision 1.3  2006/02/21 09:26:52  taddei
appearance now includes bg color

Revision 1.2  2006/02/09 15:47:24  taddei
bug fixes, refactoring and javadoc

Revision 1.1  2006/01/26 14:25:34  taddei
added class for TIN appearance


********************************************************************** */