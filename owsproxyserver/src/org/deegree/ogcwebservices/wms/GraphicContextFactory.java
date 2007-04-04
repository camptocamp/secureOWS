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
53115 Bonn
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
package org.deegree.ogcwebservices.wms;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


/**
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class GraphicContextFactory {
    
    /**
     * creates a graphic target object for the passed mime type. The target will
     * be the object where to render the to. A <tt>BufferedImage</tt> for raster
     * image mime types and a DOM <tt>Document</tt> for SVG.
     *
     * @param mimeType mime type to create a object for 
     * @param width width of the desired target object
     * @param height height of the desired target object 
     *
     * @return object to render to
     */
    public static synchronized Object createGraphicTarget( String mimeType, int width, int height ) {
        
        Object o = null;
        if ( mimeType.equals( "image/jpg" ) || mimeType.equals( "image/jpeg" ) || 
             mimeType.equals( "image/bmp" ) || mimeType.equals( "image/tif" ) || 
             mimeType.equals( "image/tiff" ) ) { 
            o = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        } else if ( mimeType.equals( "image/gif" ) || mimeType.equals( "image/png" ) ) {
            o = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        } else if ( mimeType.equals( "image/svg+xml" ) ||
                    mimeType.equals( "image/svg xml" ) ) {
            // Get a DOMImplementation
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            o = domImpl.createDocument( null, "svg", null );
        }

        return o;
    }

    /**
     * creates a graphic context for the passed target considering the passed
     * mime type
     *
     * @param mimeType mime type of the graphic (target)
     * @param target object to render to.
     *
     * @return graphic context of the target
     */
    public static synchronized Graphics createGraphicContext( String mimeType, Object target ) {
        Graphics g = null;

        if ( mimeType.equals( "image/jpg" ) || mimeType.equals( "image/jpeg" ) || 
             mimeType.equals( "image/bmp" ) || mimeType.equals( "image/tif" ) || 
             mimeType.equals( "image/tiff" ) || mimeType.equals( "image/gif" ) || 
             mimeType.equals( "image/png" ) ) {
            g = ( (BufferedImage)target ).getGraphics();
        } else if ( mimeType.equals( "image/svg+xml" ) ||
                    mimeType.equals( "image/svg xml" ) ) {
            // Create an instance of the SVG Generator
            g = new SVGGraphics2D( (Document)target );
        }         

        return g;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GraphicContextFactory.java,v $
Revision 1.7  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
