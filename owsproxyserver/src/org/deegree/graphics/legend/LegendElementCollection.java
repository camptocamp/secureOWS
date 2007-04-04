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
package org.deegree.graphics.legend;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * <tt>LegendElementCollection</tt> is a collection of <tt>LegendElement</tt>s 
 * and is a <tt>LegendElement</tt> too. It can be used to group elements or to 
 * create more complex elements.<p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.8 $ $Date: 2006/11/29 11:02:21 $
 */
public class LegendElementCollection extends LegendElement {

    ArrayList collection = null;

    String title = "";

    /**
     * empty constructor
     *
     */
    public LegendElementCollection() {
        super();
        this.collection = new ArrayList();
    }

    /**
     * empty constructor
     *
     */
    public LegendElementCollection( String title ) {
        super();
        this.collection = new ArrayList();
        this.title = title;
    }

    /**
     * adds a <tt>LegendElement</tt> to the collection.
     *
     * @param legendElement to add
     */
    public void addLegendElement( LegendElement legendElement ) {
        this.collection.add( legendElement );
    }

    /**
     * 
     * @return
     */
    public LegendElement[] getLegendElements() {
        return (LegendElement[]) collection.toArray( new LegendElement[collection.size()] );
    }

    /**
     * sets the title of the <tt>LegendElement</tt>. The title will be displayed
     * on top of the <tt>LegendElementCollection</tt>
     * 
     * @param title title of the <tt>LegendElement</tt>
     * 
     * @uml.property name="title"
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * returns the title of the <tt>LegendElement</tt>
     * 
     * @return
     * 
     * @uml.property name="title"
     */
    public String getTitle() {
        return title;
    }

    /**
     * returns the number of legend elements as an int
     * @return number of legend elements
     */
    public int getSize() {
        return this.collection.size();
    }

    /**
     * @return
     */
    public BufferedImage exportAsImage( String mime )
                            throws LegendException {

        int[] titleFontMetrics;
        int titleheight = 0; // height of the title (default: 0, none)

        int maxheight = 0; // maximum width of resulting Image
        int maxwidth = 0; // maximum height of resulting Image
        int buffer = 10; // bufferspace between LegendElements and Title (eventually)

        LegendElement[] le = getLegendElements();
        BufferedImage[] imagearray = new BufferedImage[le.length];
        BufferedImage bi = null;

        for ( int i = 0; i < le.length; i++ ) {
            imagearray[i] = le[i].exportAsImage( mime );
            maxheight += ( imagearray[i].getHeight() + buffer );
            if ( maxwidth < imagearray[i].getWidth() ) {
                maxwidth = imagearray[i].getWidth();
            }
        }

        // printing the title (or not)
        Graphics g = null;
        if ( getTitle() != null && getTitle().length() > 0 ) {
            titleFontMetrics = calculateFontMetrics( getTitle() );
            titleheight = titleFontMetrics[1] + titleFontMetrics[2];
            maxheight += titleheight;

            // is title wider than the maxwidth?
            if ( maxwidth <= titleFontMetrics[0] ) {
                maxwidth = titleFontMetrics[0];
            }

            bi = new BufferedImage( maxwidth, maxheight, BufferedImage.TYPE_INT_ARGB );
            g = bi.getGraphics();
            g.setColor( java.awt.Color.BLACK );
            g.drawString( getTitle(), 0, 0 + titleheight );
        } else {
            bi = new BufferedImage( maxwidth, maxheight, BufferedImage.TYPE_INT_ARGB );
            g = bi.getGraphics();
        }

        for ( int j = 0; j < imagearray.length; j++ ) {
            g.drawImage( imagearray[j], 0, ( imagearray[j].getHeight() + buffer ) * j + titleheight
                                           + buffer, null );
        }

        return bi;
    }
}

/*
 *****************************************************************************
 Changes to this class. What the people have been up to:
 $Log: LegendElementCollection.java,v $
 Revision 1.8  2006/11/29 11:02:21  poth
 code formatting

 Revision 1.7  2006/07/29 08:51:12  poth
 references to deprecated classes removed

 Revision 1.6  2006/04/06 20:25:31  poth
 *** empty log message ***

 Revision 1.5  2006/04/04 20:39:44  poth
 *** empty log message ***

 Revision 1.4  2006/03/30 21:20:28  poth
 *** empty log message ***

 Revision 1.3  2005/02/18 20:54:18  poth
 no message

 Revision 1.2  2005/01/18 22:08:54  poth
 no message

 Revision 1.2  2004/06/24 06:22:06  ap
 no message

 Revision 1.2  2004/05/24 07:11:45  ap
 no message

 Revision 1.1  2004/05/22 10:02:55  ap
 no message

 Revision 1.6  2004/04/07 10:58:46  axel_schaefer
 bugfix



 **************************************************************************** */