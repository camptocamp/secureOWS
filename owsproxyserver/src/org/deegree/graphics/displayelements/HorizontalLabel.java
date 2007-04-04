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
package org.deegree.graphics.displayelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.GraphicFill;
import org.deegree.graphics.sld.Halo;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * This is a horizontal label with style information and screen * coordinates, ready to be rendered to the view. * <p> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.9 $ $Date: 2006/07/12 14:46:16 $
 */

class HorizontalLabel implements Label {
    
    private static final ILogger LOG = LoggerFactory.getLogger( HorizontalLabel.class );

	private String caption;

	private int [] xpoints = new int [4];
	private int [] ypoints = new int [4];

	// width and height of the caption
	private int w;
	// width and height of the caption
	private int h;

	private Color color;
	private Font font;
	private int descent;
	private int ascent;
    private Halo halo;
    private Feature feature;


	HorizontalLabel (String caption, Font font, Color color, LineMetrics metrics,
				  	   		Feature feature, Halo halo, int x, int y, int w, int h, 
				       		double anchorPoint [], double [] displacement) {

		this.caption = caption;
		this.font = font;
		this.color = color;
		this.descent = (int) metrics.getDescent ();
		this.ascent  = (int) metrics.getAscent ();
		this.feature = feature;
		this.halo = halo;

		this.w = w;
		this.h = h;

		int dx = (int) (-anchorPoint [0] * w + displacement [0] + 0.5);
		int dy = (int) ( anchorPoint [1] * h - displacement [1] + 0.5);
	
		// vertices of label boundary
		xpoints [0] = x + dx;
		ypoints [0] = y + dy;
		xpoints [1] = x + w + dx;
		ypoints [1] = y + dy;
		xpoints [2] = x + w + dx;
		ypoints [2] = y - h + dy;
		xpoints [3] = x + dx;
		ypoints [3] = y - h + dy;
	}

    /**
     *   
     */
    public String getCaption() {
        return caption;
    }

   
	public void paintBoundaries (Graphics2D g) {
		setColor (g, new Color (0x888888), 0.5);		
		g.fillPolygon (xpoints, ypoints, xpoints.length);
		g.setColor (Color.BLACK);

		// render the text
		//g.drawString( caption, xpoints [0], ypoints [0] - descent);
	}

	/**
	 * Renders the label (including halo) to the submitted
	 * <tt>Graphics2D</tt> context.
	 * <p>
	 * @param g <tt>Graphics2D</tt> context to be used
	 */
	public void paint (Graphics2D g) {

		// render the halo (only if specified)
		if ( halo != null ) {
			try {
				paintHalo( g, halo, xpoints [0], ypoints [0] - descent);
			} catch (FilterEvaluationException e) {
				e.printStackTrace ();
			}
		}

		// render the text
		setColor( g, color, 1.0 );
		g.setFont (font);                
		g.drawString( caption, xpoints [0], ypoints [0] - descent );
	}

	/**
	 * Renders the label's halo to the submitted <tt>Graphics2D</tt> context.
	 * <p> 
	 * @param g <tt>Graphics2D</tt> context to be used
	 * @param halo <tt>Halo</tt> from the SLD
	 * @param x x-coordinate of the label
	 * @param y y-coordinate of the label
	 *
	 * @throws FilterEvaluationException if the evaluation of a
	 *         <tt>ParameterValueType</tt> fails
	 */
	private void paintHalo( Graphics2D g, Halo halo, int x, int y)
		throws FilterEvaluationException {

		int radius = (int)halo.getRadius( feature );

		// only draw filled rectangle or circle, if Fill-Element is given
		Fill fill = halo.getFill();

		if ( fill != null ) {
			GraphicFill gFill = fill.getGraphicFill();

			if ( gFill != null ) {
				BufferedImage texture = gFill.getGraphic().getAsImage( feature );
				Rectangle anchor = new Rectangle( 0, 0, texture.getWidth( null ), 
												  texture.getHeight( null ) );
				g.setPaint( new TexturePaint( texture, anchor ) );
			} else {
				double opacity = fill.getOpacity( feature );
				Color color = fill.getFill( feature );
				setColor( g, color, opacity );
			}
		} else {
			g.setColor( Color.white );
		}

		// radius specified -> draw circle
		if ( radius > 0 ) {
			g.fillOval( ( x + ( w >> 1 ) ) - radius, 
						y - ( ascent >> 1 ) - radius, radius << 1, 
						radius << 1 );
		}
		// radius unspecified -> draw rectangle
		else {
			g.fillRect( x - 1, y - ascent - 1, w + 2, h + 2 );
		}

		// only stroke outline, if Stroke-Element is given
		org.deegree.graphics.sld.Stroke stroke = halo.getStroke();

		if ( stroke != null ) {
			double opacity = stroke.getOpacity( feature );

			if ( opacity > 0.01 ) {
				Color color = stroke.getStroke( feature );
				int alpha = (int)Math.round( opacity * 255 );
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				color = new Color( red, green, blue, alpha );
				g.setColor( color );

				float[] dash = stroke.getDashArray( feature );

				// use a simple Stroke if dash == null or dash length < 2
				BasicStroke bs = null;
				float strokeWidth = (float)stroke.getWidth( feature );

				if ( ( dash == null ) || ( dash.length < 2 ) ) {
					bs = new BasicStroke( strokeWidth );
				} else {
					bs = new BasicStroke( strokeWidth, stroke.getLineCap( feature ), 
										  stroke.getLineJoin( feature ), 10.0f, dash, 
										  stroke.getDashOffset( feature ) );
					bs = new BasicStroke( strokeWidth, stroke.getLineCap( feature ), 
										  stroke.getLineJoin( feature ), 1.0f, dash, 1.0f );
				}
				g.setStroke( bs );

				// radius specified -> draw circle
				if ( radius > 0 ) {
					g.drawOval( ( x + ( w >> 1 ) ) - radius, 
								y - ( ascent >> 1 ) - radius, radius << 1, 
								radius << 1 );
				}// radius unspecified -> draw rectangle
				else {
					g.drawRect( x - 1, y - ascent - 1, w + 2, h + 2 );
				}
			}
		}
	}

	public int getX () {
		return xpoints [0];
	}
	public int getY () {
		return ypoints [0];
	}
	public int getMaxX () {
        return xpoints [1];
    }
	public int getMaxY () {
        return ypoints [1];
    }
	public int getMinX () {
        return xpoints [3];
    }
	public int getMinY () {
        return ypoints [3];
    }

	/**
	 * Determines if the label intersects with another label.
	 * <p>
	 * @param that label to test
	 * @return true if the labels intersect
	 */    
	public boolean intersects (Label that) {

		if (! (that instanceof HorizontalLabel)) {
			LOG.logInfo( "Intersection test for rotated labels is not implemented yet!");
			return false;
		}

		// coordinates of this Envelope's BBOX
		double west1  = getMinX ();
		double south1 = getMinY ();
		double east1  = getMaxX ();
		double north1 = getMaxY ();

		// coordinates of the other Envelope's BBOX        
		double west2  = ((HorizontalLabel) that).getMinX();
		double south2 = ((HorizontalLabel) that).getMinY();
		double east2  = ((HorizontalLabel) that).getMaxX();
		double north2 = ((HorizontalLabel) that).getMaxY();

		// special cases: one box lays completly inside the other one
		if ( ( west1 <= west2 ) && ( south1 <= south2 ) && ( east1 >= east2 ) && 
				 ( north1 >= north2 ) ) {
			return true;
		}
		if ( ( west1 >= west2 ) && ( south1 >= south2 ) && ( east1 <= east2 ) && 
				 ( north1 <= north2 ) ) {
			return true;
		}
		// in any other case of intersection, at least one line of the BBOX has
		// to cross a line of the other BBOX
		// check western boundary of box 1
		// "touching" boxes must not intersect
		if ( ( west1 >= west2 ) && ( west1 < east2 ) ) {
			if ( ( south1 <= south2 ) && ( north1 > south2 ) ) {
				return true;
			}

			if ( ( south1 < north2 ) && ( north1 >= north2 ) ) {
				return true;
			}
		}
		// check eastern boundary of box 1
		// "touching" boxes must not intersect
		if ( ( east1 > west2 ) && ( east1 <= east2 ) ) {
			if ( ( south1 <= south2 ) && ( north1 > south2 ) ) {
				return true;
			}

			if ( ( south1 < north2 ) && ( north1 >= north2 ) ) {
				return true;
			}
		}
		// check southern boundary of box 1
		// "touching" boxes must not intersect
		if ( ( south1 >= south2 ) && ( south1 < north2 ) ) {
			if ( ( west1 <= west2 ) && ( east1 > west2 ) ) {
				return true;
			}

			if ( ( west1 < east2 ) && ( east1 >= east2 ) ) {
				return true;
			}
		}
		// check northern boundary of box 1
		// "touching" boxes must not intersect
		if ( ( north1 > south2 ) && ( north1 <= north2 ) ) {
			if ( ( west1 <= west2 ) && ( east1 > west2 ) ) {
				return true;
			}

			if ( ( west1 < east2 ) && ( east1 >= east2 ) ) {
				return true;
			}
		}
		return false;
    }

	private Graphics2D setColor( Graphics2D g2, Color color, double opacity ) {
		if ( opacity < 0.999 ) {
			final int alpha = (int)Math.round( opacity * 255 );
			final int red = color.getRed();
			final int green = color.getGreen();
			final int blue = color.getBlue();
			color = new Color( red, green, blue, alpha );
		}

		g2.setColor( color );
		return g2;
	}
	
	public String toString () {
		return caption;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: HorizontalLabel.java,v $
Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
