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
package org.deegree.graphics.sld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * A Graphic is a "graphic symbol" with an inherent shape, color, and size.
 * Graphics can either be referenced from an external URL in a common format
 * (such as GIF or SVG) or may be derived from a Mark. Multiple external URLs
 * may be referenced with the semantic that they all provide the same graphic in
 * different formats. The "hot spot" to use for rendering at a point or the
 * start and finish handle points to use for rendering a graphic along a line
 * must either be inherent in the external format or are system- dependent. The
 * default size of an image format (such as GIF) is the inherent size of the
 * image. The default size of a format without an inherent size is 16 pixels in
 * height and the corresponding aspect in width. If a size is specified, the
 * height of the graphic will be scaled to that size and the corresponding
 * aspect will be used for the width. The default if neither an ExternalURL nor
 * a Mark is specified is to use the default Mark with a size of 6 pixels. The
 * size is in pixels and the rotation is in degrees clockwise, with 0 (default)
 * meaning no rotation. In the case that a Graphic is derived from a font-glyph
 * Mark, the Size specified here will be used for the final rendering. Allowed
 * CssParameters are "opacity", "size", and "rotation".
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.11 $ $Date: 2006/10/11 07:58:41 $
 */

public class Graphic implements Marshallable {

    // default values
    public static final double OPACITY_DEFAULT = 1.0;

    public static final double SIZE_DEFAULT = -1;

    public static final double ROTATION_DEFAULT = 0.0;

    private ArrayList marksAndExtGraphics = new ArrayList();

    private BufferedImage image = null;

    private ParameterValueType opacity = null;

    private ParameterValueType rotation = null;

    private ParameterValueType size = null;

    /**
     * Creates a new <tt>Graphic</tt> instance.
     * <p>
     * 
     * @param marksAndExtGraphics
     *            the image will be based upon these
     * @param opacity
     *            opacity that the resulting image will have
     * @param size
     *            image height will be scaled to this value, respecting the
     *            proportions
     * @param rotation
     *            image will be rotated clockwise for positive values, negative
     *            values result in anti-clockwise rotation
     */
    protected Graphic( Object[] marksAndExtGraphics, ParameterValueType opacity,
                      ParameterValueType size, ParameterValueType rotation ) {
        setMarksAndExtGraphics( marksAndExtGraphics );
        this.opacity = opacity;
        this.size = size;
        this.rotation = rotation;
    }

    /**
     * Creates a new <tt>Graphic</tt> instance based on the default
     * <tt>Mark</tt>: a square.
     * <p>
     * 
     * @param opacity
     *            opacity that the resulting image will have
     * @param size
     *            image height will be scaled to this value, respecting the
     *            proportions
     * @param rotation
     *            image will be rotated clockwise for positive values, negative
     *            values result in anti-clockwise rotation
     */
    protected Graphic( ParameterValueType opacity, ParameterValueType size,
                      ParameterValueType rotation ) {
        Mark[] marks = new Mark[1];
        marks[0] = new Mark( "square", null, null );
        setMarksAndExtGraphics( marks );
        this.opacity = opacity;
        this.size = size;
        this.rotation = rotation;
    }

    /**
     * returns the ParameterValueType representation of opacity 
     * @return
     */
    public ParameterValueType getOpacity() {
        return opacity;
    }

    /**
     * returns the ParameterValueType representation of rotation
     * @return
     */
    public ParameterValueType getRotation() {
        return rotation;
    }

    /**
     * returns the ParameterValueType representation of size
     * @return
     */
    public ParameterValueType getSize() {
        return size;
    }

    /**
     * Creates a new <tt>Graphic</tt> instance based on the default
     * <tt>Mark</tt>: a square.
     */
    protected Graphic() {
        this( null, null, null );
    }

    /**
     * Returns an object-array that enables the access to the stored
     * <tt>ExternalGraphic</tt> and <tt>Mark</tt> -instances.
     * <p>
     * 
     * @return contains <tt>ExternalGraphic</tt> and <tt>Mark</tt> -objects
     * 
     * @uml.property name="marksAndExtGraphics"
     */
    public Object[] getMarksAndExtGraphics() {
        Object[] objects = new Object[marksAndExtGraphics.size()];
        return marksAndExtGraphics.toArray( objects );
    }

    /**
     * Sets the <tt>ExternalGraphic</tt>/
     * <tt>Mark<tt>-instances that the image
     * will be based on.
     * <p>
     * @param object to be used as basis for the resulting image
     */
    public void setMarksAndExtGraphics( Object[] object ) {
        image = null;
        this.marksAndExtGraphics.clear();

        if ( object != null ) {
            for ( int i = 0; i < object.length; i++ ) {
                marksAndExtGraphics.add( object[i] );
            }
        }
    }

    /**
     * Adds an Object to an object-array that enables the access to the stored
     * <tt>ExternalGraphic</tt> and <tt>Mark</tt> -instances.
     * <p>
     * 
     * @param object
     *            to be used as basis for the resulting image
     */
    public void addMarksAndExtGraphic( Object object ) {
        marksAndExtGraphics.add( object );
    }

    /**
     * Removes an Object from an object-array that enables the access to the
     * stored <tt>ExternalGraphic</tt> and <tt>Mark</tt> -instances.
     * <p>
     * 
     * @param object
     *            to be used as basis for the resulting image
     */
    public void removeMarksAndExtGraphic( Object object ) {
        marksAndExtGraphics.remove( marksAndExtGraphics.indexOf( object ) );
    }

    /**
     * The Opacity element gives the opacity to use for rendering the graphic.
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluation fails or the value is invalid
     */
    public double getOpacity( Feature feature )
                            throws FilterEvaluationException {
        double opacityVal = OPACITY_DEFAULT;

        if ( opacity != null ) {
            String value = opacity.evaluate( feature );

            try {
                opacityVal = Double.parseDouble( value );
            } catch ( NumberFormatException e ) {
                throw new FilterEvaluationException( "Given value for parameter 'opacity' ('"
                                                     + value + "') has invalid format!" );
            }

            if ( ( opacityVal < 0.0 ) || ( opacityVal > 1.0 ) ) {
                throw new FilterEvaluationException( "Value for parameter 'opacity' (given: '"
                                                     + value + "') must be between 0.0 and 1.0!" );
            }
        }

        return opacityVal;
    }

    /**
     * The Opacity element gives the opacity of to use for rendering the
     * graphic.
     * <p>
     * 
     * @param opacity
     *            Opacity to be set for the graphic
     */
    public void setOpacity( double opacity ) {
        ParameterValueType pvt = null;
        pvt = StyleFactory.createParameterValueType( "" + opacity );
        this.opacity = pvt;
    }

    /**
     * The Size element gives the absolute size of the graphic in pixels encoded
     * as a floating-point number. This element is also used in other contexts
     * than graphic size and pixel units are still used even for font size. The
     * default size for an object is context-dependent. Negative values are not
     * allowed.
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluation fails or the value is invalid
     */
    public double getSize( Feature feature )
                            throws FilterEvaluationException {
        double sizeVal = SIZE_DEFAULT;

        if ( size != null ) {
            String value = size.evaluate( feature );

            try {
                sizeVal = Double.parseDouble( value );
            } catch ( NumberFormatException e ) {
                throw new FilterEvaluationException( "Given value for parameter 'size' ('" + value
                                                     + "') has invalid format!" );
            }

            if ( sizeVal <= 0.0 ) {
                throw new FilterEvaluationException( "Value for parameter 'size' (given: '" + value
                                                     + "') must be greater than 0!" );
            }
        }

        return sizeVal;
    }

    /**
     * @see org.deegree.graphics.sld.Graphic#getSize(Feature)
     *      <p>
     * @param size
     *            size to be set for the graphic
     */
    public void setSize( double size ) {
        ParameterValueType pvt = null;
        pvt = StyleFactory.createParameterValueType( "" + size );
        this.size = pvt;
    }

    /**
     * The Rotation element gives the rotation of a graphic in the clockwise
     * direction about its center point in radian, encoded as a floating- point
     * number. Negative values mean counter-clockwise rotation. The default
     * value is 0.0 (no rotation).
     * <p>
     * 
     * @param feature
     *            specifies the <tt>Feature</tt> to be used for evaluation of
     *            the underlying 'sld:ParameterValueType'
     * @return the (evaluated) value of the parameter
     * @throws FilterEvaluationException
     *             if the evaluation fails or the value is invalid
     */
    public double getRotation( Feature feature )
                            throws FilterEvaluationException {
        double rotVal = ROTATION_DEFAULT;

        if ( rotation != null ) {
            String value = rotation.evaluate( feature );

            try {
                rotVal = Double.parseDouble( value );
            } catch ( NumberFormatException e ) {
                throw new FilterEvaluationException( "Given value for parameter 'rotation' ('"
                                                     + value + "') has invalid format!" );
            }
        }

        return rotVal;
    }

    /**
     * @see org.deegree.graphics.sld.Graphic#getRotation(Feature)
     *      <p>
     * @param rotation
     *            rotation to be set for the graphic
     */
    public void setRotation( double rotation ) {
        ParameterValueType pvt = null;
        pvt = StyleFactory.createParameterValueType( "" + rotation );
        this.rotation = pvt;
    }

    /**
     * Returns a <tt>BufferedImage</tt> representing this object. The image
     * respects the 'Opacity', 'Size' and 'Rotation' parameters. If the
     * 'Size'-parameter is omitted, the height of the first
     * <tt>ExternalGraphic</tt> is used. If there is none, the default value
     * of 6 pixels is used.
     * <p>
     * 
     * @return the <tt>BufferedImage</tt> ready to be painted
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public BufferedImage getAsImage( Feature feature )
                            throws FilterEvaluationException {
        int intSizeX = (int) getSize( feature );
        int intSizeY = intSizeX;

        //calculate the size of the first ExternalGraphic
        int intSizeImgX = -1;
        int intSizeImgY = -1;
        for ( int i = 0; i < marksAndExtGraphics.size(); i++ ) {
            Object o = marksAndExtGraphics.get( i );
            if ( o instanceof ExternalGraphic ) {
                BufferedImage extImage = ( (ExternalGraphic) o ).getAsImage( intSizeX, intSizeY,
                                                                             feature );
                intSizeImgX = extImage.getWidth();
                intSizeImgY = extImage.getHeight();
                break;
            }
        }

        if ( intSizeX < 0 ) {
            // if size is unspecified
            if ( intSizeImgX < 0 ) {
                // if there are no ExternalGraphics, use default value of 6 pixels
                intSizeX = 6;
                intSizeY = 6;
            } else {
                // if there are ExternalGraphics, use width and height of the first
                intSizeX = intSizeImgX;
                intSizeY = intSizeImgY;
            }
        } else {
            //if size is specified
            if ( intSizeImgX < 0 ) {
                // if there are no ExternalGraphics, use default intSizeX
                intSizeY = intSizeX;
            } else {
                // if there are ExternalGraphics, use the first to find the height
                intSizeY = (int) Math.round( ( ( (double) intSizeImgY ) / ( (double) intSizeImgX ) )
                                             * intSizeX );
            }
        }

        image = new BufferedImage( intSizeX, intSizeY, BufferedImage.TYPE_INT_ARGB );

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.rotate( Math.toRadians( getRotation( feature ) ), intSizeX >> 1, intSizeY >> 1 );

        for ( int i = 0; i < marksAndExtGraphics.size(); i++ ) {
            Object o = marksAndExtGraphics.get( i );
            BufferedImage extImage = null;

            if ( o instanceof ExternalGraphic ) {
                extImage = ( (ExternalGraphic) o ).getAsImage( intSizeX, intSizeY, feature );
            } else {
                extImage = ( (Mark) o ).getAsImage( feature, intSizeX );
            }

            g.drawImage( extImage, 0, 0, intSizeX, intSizeY, null );
        }

        // use the default Mark if there are no Marks / ExternalGraphics
        // specified at all
        if ( marksAndExtGraphics.size() == 0 ) {
            Mark mark = new Mark();
            BufferedImage extImage = mark.getAsImage( feature, intSizeX );
            g.drawImage( extImage, 0, 0, intSizeX, intSizeY, null );
        }

        return image;
    }

    /**
     * Sets a <tt>BufferedImage</tt> representing this object. The image
     * respects the 'Opacity', 'Size' and 'Rotation' parameters.
     * <p>
     * 
     * @param bufferedImage
     *            BufferedImage to be set
     */
    public void setAsImage( BufferedImage bufferedImage ) {
        image = bufferedImage;
    }

    /**
     * exports the content of the Graphic as XML formated String
     * 
     * @return xml representation of the Graphic
     */
    public String exportAsXML() {

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<Graphic>" );
        for ( int i = 0; i < marksAndExtGraphics.size(); i++ ) {
            sb.append( ( (Marshallable) marksAndExtGraphics.get( i ) ).exportAsXML() );
        }
        if ( opacity != null ) {
            sb.append( "<Opacity>" );
            sb.append( ( (Marshallable) opacity ).exportAsXML() );
            sb.append( "</Opacity>" );
        }
        if ( size != null ) {
            sb.append( "<Size>" );
            sb.append( ( (Marshallable) size ).exportAsXML() );
            sb.append( "</Size>" );
        }
        if ( rotation != null ) {
            sb.append( "<Rotation>" );
            sb.append( ( (Marshallable) rotation ).exportAsXML() );
            sb.append( "</Rotation>" );
        }
        sb.append( "</Graphic>" );

        return sb.toString();
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Graphic.java,v $
 Revision 1.11  2006/10/11 07:58:41  poth
 getter for ParameterValueType representations of rotation, opacity and size added

 Revision 1.10  2006/07/29 08:51:12  poth
 references to deprecated classes removed

 Revision 1.9  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
