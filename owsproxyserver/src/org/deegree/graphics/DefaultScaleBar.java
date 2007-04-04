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
package org.deegree.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/08/11 09:59:56 $
 *
 * @since 2.0
 */
public class DefaultScaleBar implements ScaleBar {
    private Color barColor;

    private Color labelColor;

    private Color bgColor;

    private Font barFont;

    private String barStyle;

    private String unit;

    private double scale;

    private int bottomLabel;

    private double scaleDenominator;

    private int topLabel;

    private NumberFormat numberFormat;

    /**
     * Constructor with all Parameters
     *
     * @param topLabel type of the label on top of the scale bar. Chose L_NONE 
     *                 or no label, L_SCALE for scale on top and L_SCALEDENOMIATOR for 
     *                 scaledenominator on top.
     * @param bottomLabel the same as above but below the scalebar.
     * @param scale the scale to be displayed. For a value of e.g. 1000 there will 
     *                  be 1:1000 displayed
     * @param scaleDenominator the scaledenominator to be displayed
     * @param units the units the scaledenominator is in. Possible values are 
     *                  inch, Meter, Mile, Kilometer...
     * @param labelColor the Color the label has to be in (and of course the text below and above)
     * @param allgColor not used so far
     * @param barStyle the style the bar appears in. Currently just "default" is supported.
     * @param barFont the font the text above and below the scale bar appears in.
     *
     */
    public DefaultScaleBar( int topLabel, int bottomLabel, double scale, double scaleDenominator,
                           String units, Color labelColor, Color barColor, Color allgColor,
                           String barStyle, Font barFont, NumberFormat numberFormat ) {
        setTopLabel( topLabel );
        setBottomLabel( bottomLabel );
        setScale( scale );
        setScaleDenominator( scaleDenominator );
        setColor( allgColor );
        setBarColor( barColor );
        setLabelColor( labelColor );
        setStyle( barStyle );
        setFont( barFont );
        setUnits( units );
        setNumberFormat( numberFormat );
    }

    /**
     *
     * Constructor just using defaults
     *
     */
    public DefaultScaleBar() {
        this( L_SCALEDENOMINATOR, L_SCALE, 40000, 100, "Meter", Color.GREEN, Color.BLUE,
              Color.BLACK, "default", new Font( "default", Font.PLAIN, 12 ),
              new DecimalFormat( "###,###,###" ) );
    }

    /**
     * will paint the scale bar to the passed graphic context
     *
     * @param g graphic context
     */
    public void paint( Graphics g ) { //throws Exception {

        Rectangle rect = g.getClipBounds();
        if ( rect != null && rect.getWidth() > 0 && rect.getHeight() > 0 ) {
            g.setColor( bgColor );
            g.fillRect( 0, 0, (int) rect.getWidth(), (int) rect.getHeight() );
        }

        // if ( toplabel + bottomlabel < 1 ) throw Exception
        g.setColor( barColor );

        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        g.setFont( barFont );

        int laenge;

        if ( barStyle.equals( "default" ) ) {
            g.drawLine( 0, ( height / 2 ) + 1, width - 1, ( height / 2 ) + 1 );
            g.drawLine( 0, height / 2, width - 1, height / 2 );
            g.drawLine( 0, ( height / 2 ) - 1, width - 1, ( height / 2 ) - 1 );
            g.drawLine( 0, ( height / 2 ) + 10, 0, ( height / 2 ) - 10 );
            g.drawLine( width - 1, ( height / 2 ) + 10, width - 1, ( height / 2 ) - 10 );
        }

        String strScale = numberFormat.format( scale );
        String strScaleDen = numberFormat.format( scaleDenominator );

        g.setColor( labelColor );
        switch ( topLabel ) {
        case -1:
            break;
        case 0:
            laenge = g.getFontMetrics().stringWidth( strScale + " " + unit );
            g.drawString( strScale + " " + unit, ( width - laenge ) / 2, ( height / 2 ) - 6 );
            break;
        case 1:
            laenge = g.getFontMetrics().stringWidth( "1 : " + strScaleDen );
            g.drawString( "1 : " + strScaleDen, ( width - laenge ) / 2, ( height / 2 ) - 6 );
            break;
        }

        switch ( bottomLabel ) {
        case -1:
            break;
        case 0:
            laenge = g.getFontMetrics().stringWidth( strScale + " " + unit );
            g.drawString( strScale + " " + unit, ( width - laenge ) / 2, ( height / 2 ) + 1
                                                                         + barFont.getSize() );
            break;
        case 1:
            laenge = g.getFontMetrics().stringWidth( "1 : " + strScaleDen );
            g.drawString( "1 : " + strScaleDen, ( width - laenge ) / 2, ( height / 2 ) + 1
                                                                        + barFont.getSize() );
            break;
        }
    }

    /**
     * sets the type of the label above the scale bar
     * 
     * @param labelType lable type
     * 
     */
    public void setTopLabel( int labelType ) {
        switch ( labelType ) {
        case -1:
            topLabel = -1;
            break;
        case 0:
            topLabel = 0;
            break;
        case 1:
            topLabel = 1;
            break;
        }
    }

    /**
     * sets the type of the label below the scale bar
     * 
     * @param labelType lable type
     * 
     */
    public void setBottomLabel( int labelType ) {
        switch ( labelType ) {
        case -1:
            bottomLabel = -1;
            break;
        case 0:
            bottomLabel = 0;
            break;
        case 1:
            bottomLabel = 1;
            break;
        }
    }

    /**
     * sets the scale as defined in the OGC WMS 1.1.1 specification. Scale is
     * defined as the diagonal size of a pixel in the center of a map measured
     * in meter. The setting of the scale will affect the value of the scale 
     * denominator
     * 
     * @parameter scale map scale
     * 
     */
    public void setScale( double scale ) {
        this.scale = scale;
    }

    /**
     * sets the scale denominator for the scale bar. The scale denominator is
     * the scale expression as we know it for printed maps (e.g. 1:10000 1:5000).
     * The passed value is expressed in meters.
     * The setting of the scale denominator will affect the value of the scale
     * 
     * @param scaleDen scale denominator value
     * 
     */
    public void setScaleDenominator( double scaleDen ) {
        scaleDenominator = scaleDen;
    }

    /**
     * sets the units the scale and the scale denominater will be expressed at. 
     * Settings other than meter will cause that the passed values for scale
     * and scale denominater will be recalculated for painting. it depends on
     * the implementation what units are supported.
     *
     * @param units name units (meter, miles, feet etc.)
     */
    public void setUnits( String units ) {
        unit = units;
    }

    /**
     * sets the front color of the scale bar
     */
    public void setColor( Color color ) {
        bgColor = color;
    }

    /**
     * sets the label color of the scale bar
     * 
     */
    public void setLabelColor( Color color ) {
        labelColor = color;
    }

    /**
     * sets the bar color of the scale bar
     * 
     */
    public void setBarColor( Color color ) {
        barColor = color;
    }

    /**
     * sets the style of the scale bar. default style is |--------|
     * the list of known styles depends on the implementation
     *
     * @param style style name
     */
    public void setStyle( String style ) {
        barStyle = style;
    }

    /**
     * sets the font for label rendering
     *
     * @param font awt font object
     */
    public void setFont( Font font ) {
        barFont = font;
    }

    /* (non-Javadoc)
     * @see org.deegree.graphics.ScaleBar#setBackgroundColor(java.awt.Color)
     */
    public void setBackgroundColor( Color color ) {
        this.bgColor = color;

    }

    /**
     * sets the number format for a scale bar
     * @param
     */
    public void setNumberFormat( NumberFormat numberFormat ) {
        this.numberFormat = numberFormat;
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DefaultScaleBar.java,v $
 Revision 1.8  2006/08/11 09:59:56  poth
 number formating added / code formated

 Revision 1.7  2006/08/06 21:03:18  poth
 bug fix - recognition for background color added

 Revision 1.6  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
