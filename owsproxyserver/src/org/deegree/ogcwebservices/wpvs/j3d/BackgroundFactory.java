//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/BackgroundFactory.java,v 1.8 2006/11/27 09:07:52 poth Exp $
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ImageComponent2D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * Factory for creating different types of Java3D objects to be used as background objects.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
public class BackgroundFactory {

	private BackgroundFactory() {
		// nothing to do
	}
	
	/**
	 * Creates Java 3D <code>background</code> object, drawn at infinity. This method is used
	 * for "box" views and/or when the users wants a fixed background image. Not the in the latter
	 * case for optimal results, the image should be of same dimensions as the <code>GetView</code>
	 * image 
	 * @param vp the ViewPoint defining the observer's position
	 * @param color the background color (seen when image is null or doesn't cover the whole view) 
	 * @param imgURL a URL pointing to an image
	 * @return
	 * @throws IOException 
	 */
	public static Background createSimpleBackgroundGroup( ViewPoint vp, Color color, URL imgURL, Dimension reqImgDimension ) throws IOException{
        
		Point3d ftPrint = vp.getFootprint()[0];

		Background bg = new Background( new Color3f( color ) );
        Point3d origin = new Point3d( ftPrint.x, ftPrint.y, ftPrint.z );  
        BoundingSphere bounds = new BoundingSphere(origin, ftPrint.x );

        bg.setApplicationBounds( bounds );
        
        if( imgURL != null ) {
            BufferedImage buffImg = ImageIO.read( imgURL );
            
            // scale image to fill the whole bakground
            BufferedImage tmpImg = new BufferedImage( reqImgDimension.width, reqImgDimension.height, buffImg.getType() );
            Graphics g = tmpImg.getGraphics();
            g.drawImage( buffImg, 0, 0, tmpImg.getWidth() - 1 ,tmpImg.getHeight() - 1, null );
            g.dispose();
            
            ImageComponent2D img = new TextureLoader( tmpImg ).getImage();
            bg.setImage( img );      
        }
        
        return bg;
	}
	
    
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: BackgroundFactory.java,v $
Revision 1.8  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.6  2006/07/05 15:59:13  poth
comments corrected

Revision 1.5  2006/07/05 11:23:07  taddei
background img is scaled to fit req dimensions

Revision 1.4  2006/06/20 10:16:01  taddei
clean up and javadoc

Revision 1.3  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.1  2006/02/24 11:42:41  taddei
refactoring (background)


********************************************************************** */