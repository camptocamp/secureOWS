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

import org.deegree.portal.PortalException;



/**
 * 
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class LayerList  {
    private HashMap layers = new HashMap();
    private List list = new ArrayList();

    /**
     * Creates a new LayerList object.
     *
     * @param layers 
     */
    public LayerList( Layer[] layers ) {
        setLayers( layers );
    }

    /**
     * returns a layer identifies by its name
     *
     * @param name name ofthe layer
     *
     * @return 
     */
    public Layer getLayer( String name ) {
        return (Layer)layers.get( name );
    }
    
    /**
     * returns a layer identifies by its name
     *
     * @param name name ofthe layer
     * @param serverAddress address of the server which servers the layer
     *
     * @return 
     */
    public Layer getLayer( String name, String serverAddress ) {
        Layer layer = null;
        int i = 0;
        while ( i < list.size() && layer == null ) {
            Layer tmp = (Layer)list.get( i );
            String s = tmp.getServer().getOnlineResource().toExternalForm();
            if ( tmp.getName().equals( name ) && s.equals( serverAddress ) ) {
                layer = tmp;
            }
            i++;
        }
        return layer;
    }

    /**
     * returns all layers of the web map context
     *
     * @return 
     */
    public Layer[] getLayers() {
        Layer[] cl = new Layer[list.size()];
        return (Layer[])list.toArray( cl );
    }

    /**
     * sets all layers of the web map context
     *
     * @param layers 
     */
    public void setLayers( Layer[] layers ) {
        this.layers.clear();
        this.list.clear();

        if ( layers != null ) {
            for ( int i = 0; i < layers.length; i++ ) {
                this.layers.put( layers[i].getName(), layers[i] );
                list.add( layers[i] );
            }
        }
    }

    /**
     * adds one layer to the the web map context. If a layer with the same
     * name as the passed layer already exits it will be overwritten
     *
     * @param layer 
     */
    public void addLayer( Layer layer ) {
        this.layers.put( layer.getName(), layer );
        list.add( layer );
    }

    /**
     * removes a layer identified by its name from the web map context
     *
     * @param name name of the layer to be removed
     *
     * @return 
     */
    public Layer removeLayer( String name ) {
        Layer layer = (Layer)this.layers.remove( name );
        list.remove( layer );
        return layer;
    }
    
    /**
     * moves a layer within the layer list up or down
     * @param layer layer to be moved
     * @param up if true the layer will be moved up otherwise 
     *           it will be moved down
     * @throws PortalException will be thrown if the layer is not known 
     *                         by the layer list.
     */
    public void move(Layer layer, boolean up)  {
        int i = 0;
        Layer target = null;
        while ( i < list.size() && target == null ) {
            Layer tmp = (Layer)list.get( i );
            if ( tmp.getName().equals( layer.getName() ) &&
                 tmp.getServer().getOnlineResource().equals( layer.getServer().getOnlineResource() ) ) {
                target = tmp;
            }
            i++;
        }
        i--;
        if ( i > 0 && up ) {
            Object o = list.get( i );
            list.set( i, list.get( i-1 ) );
            list.set( i-1, o );
        } else if ( i < list.size()-1 && !up ) {
            Object o = list.get( i );
            list.set( i, list.get( i+1 ) );
            list.set( i+1, o );
        }
    }

    /**
     * removes all layers from the web map context
     */
    public void clear() {
        this.layers.clear();
        list.clear();
    }
    
   
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LayerList.java,v $
Revision 1.9  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
