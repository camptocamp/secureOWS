//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/quadtree/MemPointNode.java,v 1.4 2006/11/03 08:27:49 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.io.quadtree;

import java.util.ArrayList;
import java.util.List;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * <code>MemPointNode</code> is the node class of a memory based implementation of a quadtree.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/11/03 08:27:49 $
 * 
 * @since 2.0
 */

public class MemPointNode implements Node {

    private final Envelope envelope;

    private final int level;

    private MemPointNode[] subnodes;

    private List<Object> items;

    private List<Envelope> itemsEnvelope;

    private Quadtree owner;

    /**
     * Constructs a new node with the given envelope, object, location and level.
     * 
     * @param owner
     * @param env the envelope
     * @param lvl the level
     */
    public MemPointNode( Quadtree owner, Envelope env, int lvl ) {
        envelope = env;
        level = lvl;
        this.owner = owner;
    }

    /**
     * @return the deepest level of this subtree
     */
    public int getDepth() {
        if ( subnodes == null ) {
            return level;
        }

        int max = 0;
        int d = 0;

        for ( MemPointNode node : subnodes ) {
            if ( node != null ) {
                d = node.getDepth();
                if ( d > max ) {
                    max = d;
                }
            }
        }

        return max;
    }

    /**
     * @return the region of this node
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * This method does not make sense for the memory implementation.
     * 
     * @return null
     */
    public String getId() {
        return null;
    }

    /**
     * Inserts the item into the quadtree.
     * 
     * @param item the item
     * @param itemEnv the envelope of the item
     */
    public void insert( Object item, Envelope itemEnv )
                            throws IndexException {

        if ( !envelope.intersects( itemEnv ) ) {
            throw new IndexException( "Item envelope does not intersect with node envelope!" );
        }

        if ( level < ( (MemPointQuadtree) owner ).maxDepth ) {
            Envelope[] envs = split();

            if ( subnodes == null ) {
                subnodes = new MemPointNode[4];
            }

            for ( int i = 0; i < 4; ++i ) {
                if ( envs[i].intersects( itemEnv ) ) {
                    if ( subnodes[i] == null ) {
                        subnodes[i] = new MemPointNode( owner, envs[i], level + 1 );
                    }
                    subnodes[i].insert( item, itemEnv );
                }
            }
        } else {
            if ( items == null ) {
                items = new ArrayList<Object>( 50 );
                itemsEnvelope = new ArrayList<Envelope>( 50 );
            }
            items.add( item );
            itemsEnvelope.add( itemEnv );
        }

    }

    /**
     * Searches for all items intersecting the search envelope.
     * 
     * @param searchEnv the search envelope
     * @param visitor the resulting list
     * @param level unused by this implementation
     * @return a list with all found items
     */
    public List query( Envelope searchEnv, List visitor, int level )
                            throws IndexException {

        if ( subnodes == null ) {
            return visitor;
        }

        for ( int i = 0; i < 4; ++i ) {
            if ( subnodes[i] != null ) {
                MemPointNode node = subnodes[i];
                if ( node.items != null ) {
                    if ( subnodes[i].envelope.intersects( searchEnv ) ) {
                        for ( int j = 0; j < node.itemsEnvelope.size(); j++ ) {
                            Envelope env = node.itemsEnvelope.get( j );
                            if ( env.intersects( searchEnv ) ) {
                                visitor.addAll( node.items );
                            }
                        }
                    }
                } else {
                    if ( node.envelope.intersects( searchEnv ) ) {
                        node.query( searchEnv, visitor, level );
                    }
                }
            }
        }

        return visitor;
    }

    /**
     * Deletes the item from the quadtree. Untested method!
     * 
     * @param item the item to be deleted
     */
    public void deleteItem( Object item ) {

        if ( subnodes == null ) {
            return;
        }

        for ( int i = 0; i < 4; ++i ) {
            if ( subnodes[i] != null ) {
                MemPointNode node = subnodes[i];
                if ( node.items.contains( item ) ) {
                    node.items.remove( item );
                } else {
                    node.deleteItem( item );
                }
            }
        }

    }

    /**
     * Deletes all items intersecting the envelope. Untested method!
     * 
     * @param envelope
     */
    public void deleteRange( Envelope envelope ) {

        if ( subnodes == null ) {
            return;
        }

        for ( int i = 0; i < 4; ++i ) {
            if ( subnodes[i] != null ) {
                MemPointNode node = subnodes[i];
                if ( node.envelope.intersects( envelope ) ) {
                    subnodes[i] = null;
                } else {
                    if ( node.envelope.intersects( envelope ) ) {
                        node.deleteRange( envelope );
                    }
                }
            }
        }

    }

    // splits the envelope of this node in four pieces
    private Envelope[] split() {
        Envelope[] envs = new Envelope[4];
        double nW = envelope.getWidth() / 2d;
        double nH = envelope.getHeight() / 2d;

        envs[0] = GeometryFactory.createEnvelope( envelope.getMin().getX(),
                                                  envelope.getMin().getY(),
                                                  envelope.getMin().getX() + nW,
                                                  envelope.getMin().getY() + nH, null );
        envs[1] = GeometryFactory.createEnvelope( envelope.getMin().getX() + nW,
                                                  envelope.getMin().getY(),
                                                  envelope.getMin().getX() + ( 2 * nW ),
                                                  envelope.getMin().getY() + nH, null );
        envs[2] = GeometryFactory.createEnvelope( envelope.getMin().getX() + nW,
                                                  envelope.getMin().getY() + nH,
                                                  envelope.getMin().getX() + ( 2 * nW ),
                                                  envelope.getMin().getY() + ( 2 * nH ), null );
        envs[3] = GeometryFactory.createEnvelope( envelope.getMin().getX(),
                                                  envelope.getMin().getY() + nH,
                                                  envelope.getMin().getX() + nW,
                                                  envelope.getMin().getY() + ( 2 * nH ), null );

        return envs;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: MemPointNode.java,v $
 Revision 1.4  2006/11/03 08:27:49  schmitz
 Updated the documentation.

 Revision 1.3  2006/10/30 09:02:38  poth
 implementation changed for optimized memory management for MemPointQuadtree

 Revision 1.2  2006/10/25 11:59:04  schmitz
 Text2Tiff is unfinished due to problems with geotiff format.
 The rest of the interpolation/Text2Tiff should work fine now.

 Revision 1.1  2006/10/20 14:57:08  schmitz
 Added a memory point quadtree implementation.
 Used the quadtree for interpolation.
 Updated the text2tiff tool to use quadtree and interpolation.



 ********************************************************************** */