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
package org.deegree.portal.context;




/**
 * this class encapsulates the description of the front end of a GUI setting up
 * on a web map context. this is a deegree specific form of description. beside
 * some
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public abstract class AbstractFrontend implements Frontend {
    
    private GUIArea center = null;
    private GUIArea east = null;
    private GUIArea north = null;
    private GUIArea south = null;
    private GUIArea west = null;
    private String controller = null;
    
    /**
     * Creates a new Frontend object.
     *
     * @param controller 
     * @param west 
     * @param east 
     * @param south 
     * @param north 
     * @param center 
     */
    public AbstractFrontend(String controller, GUIArea west, GUIArea east, 
                            GUIArea south, GUIArea north, GUIArea center) {
        setController( controller );
        setWest( west );
        setEast( east );
        setSouth( south );
        setNorth( north );
        setCenter( center );
    }

    /**
     * returns the name of the central controller of the front end. depending
     * on the implementation this may be the name of a HTML/JSP-page a java class
     * or something else.
     *
     * @return 
     */
    public String getController() {
        return controller;
    }

    /**
     * returns the description of the west GUI area
     *
     * @return 
     */
    public GUIArea getWest() {
        return west;
    }

    /**
     * returns the description of the east GUI area
     *
     * @return 
     */
    public GUIArea getEast() {
        return east;
    }

    /**
     * returns the description of the south GUI area
     *
     * @return 
     */
    public GUIArea getSouth() {
        return south;
    }

    /**
     * returns the description of the north GUI area
     *
     * @return 
     */
    public GUIArea getNorth() {
        return north;
    }

    /**
     * returns the description of the central GUI area
     *
     * @return 
     */
    public GUIArea getCenter() {
        return center;
    }

    /**
     * sets the name of the central controller of the front end. depending
     * on the implementation this may be the name of a HTML/JSP-page a java class
     * or something else. 
     *
     * @param controller 
     */
    public void setController( String controller ) {
        this.controller = controller;
    }

    /**
     * sets the description of the west GUI area
     *
     * @param west 
     */
    public void setWest( GUIArea west ) {
        this.west = west;
    }

    /**
     * sets the description of the east GUI area
     *
     * @param east 
     */
    public void setEast( GUIArea east ) {
        this.east = east;
    }

    /**
     * sets the description of the south GUI area
     *
     * @param south 
     */
    public void setSouth( GUIArea south ) {
        this.south = south;
    }

    /**
     * sets the description of the north GUI area
     *
     * @param north 
     */
    public void setNorth( GUIArea north ) {
        this.north = north;
    }

    /**
     * sets the description of the central GUI area
     *
     * @param center 
     */
    public void setCenter( GUIArea center ) {
        this.center = center;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractFrontend.java,v $
Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
