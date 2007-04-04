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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * this interface describes the content of an area of a GUI. a GUI area contains
 * zero ... n modules described by the <tt>Module</tt> interface. A GUI area may
 * be can be switched to be invisible. indicated by the hidden attribute.
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class GUIArea  {
    
    public static final int WEST = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int NORTH = 3;
    public static final int CENTER = 4;
    
    private HashMap modules = new HashMap();
    private boolean hidden = false;
    private int area = 0;
    private List list = new ArrayList();

    /**
     * Creates a new GUIArea_Impl object.
     *
     * @param area 
     * @param hidden 
     * @param modules 
     */
    public GUIArea( int area, boolean hidden, Module[] modules ) {
        setArea( area );
        setHidden( hidden );
        setModules( modules );
    }

    /**
     * returns area (north, west, east ...)  assigned to an instance
     *
     * @return 
     */
    public int getArea() {
        return area;
    }

    /**
     * sets the name of a module
     *
     * @param area 
     */
    public void setArea( int area ) {
        this.area = area;
    }

    /**
     * returns true if the GUIArea is hidden. 
     *
     * @return 
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * sets the GUIArea to be hidden or visible. 
     *
     * @param hidden 
     */
    public void setHidden( boolean hidden ) {
        this.hidden = hidden;
    }

    /**
     * returns a module identified by its name
     *
     * @param name 
     *
     * @return 
     */
    public Module getModule( String name ) {
        return (Module)modules.get( name );
    }

    /**
     * returns all modules of a GUIArea
     *
     * @return 
     */
    public Module[] getModules() {
        Module[] cl = new Module[list.size()];
        return (Module[])list.toArray( cl );

    }

    /**
     * sets the modules of a GUIArea
     *
     * @param modules 
     */
    public void setModules( Module[] modules ) {
        this.modules.clear();
        this.list.clear();
        
        if ( modules != null ) {
            for ( int i = 0; i < modules.length; i++ ) {
                this.modules.put( modules[i].getName(), modules[i] );
                list.add( modules[i] );
            }
        }
    }

    /**
     * adds a module to a GUIArea
     *
     * @param module 
     */
    public void addModul( Module module ) {
        modules.put( module.getName(), module );
        list.add( module );
    }

    /**
     * reomes a module identified by its name from the GUIArea
     *
     * @param name 
     *
     * @return 
     */
    public Module removeModule( String name ) {
        Module module = (Module)modules.remove( name );
        list.remove( module );
        return module;
    }
    
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GUIArea.java,v $
Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
