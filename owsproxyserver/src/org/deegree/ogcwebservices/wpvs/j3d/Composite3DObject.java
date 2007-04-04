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

import java.util.List;

import javax.media.j3d.Group;
import javax.media.j3d.Node;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * $Revision: 1.2 $, $Date: 2006/11/27 16:56:39 $
 *
 */
public class Composite3DObject extends Group {

    private ILogger LOG = LoggerFactory.getLogger( Composite3DObject.class );

    private String id = null;

    /**
     * 
     * @param id
     */
    public Composite3DObject( String id ) {
        this.id = id;
    }

    /**
     * constructs a Composite3DObject from the passed list of
     * SimpleSurfaces
     * @param id The id of this 3DObject
     *  
     * @param surfaces
     */
    public Composite3DObject( String id, List<DefaultSurface> surfaces ) {
        this( id );
        for ( int i = 0; i < surfaces.size(); i++ ) {
            addChild( surfaces.get( i ) );
        }
        LOG.logDebug( "number of surfaces: ", surfaces.size() );
    }

    /**
     * returns the ID of the composite
     * @return the ID of the composite
     */
    public String getId() {
        return id;
    }

    @Override
    public void addChild( Node node ) {
        if ( !( node instanceof DefaultSurface ) ) {
            throw new IllegalArgumentException( "passed node must be an instance of "
                                                + DefaultSurface.class.getName() );
        }
        super.addChild( node );
    }

    @Override
    public void insertChild( Node node, int index ) {
        if ( !( node instanceof DefaultSurface ) ) {
            throw new IllegalArgumentException( "passed node must be an instance of "
                                                + DefaultSurface.class.getName() );
        }
        super.insertChild( node, index );
    }

    @Override
    public void setChild( Node node, int index ) {
        if ( !( node instanceof DefaultSurface ) ) {
            throw new IllegalArgumentException( "passed node must be an instance of "
                                                + DefaultSurface.class.getName() );
        }
        super.setChild( node, index );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Composite3DObject.java,v $
 Revision 1.2  2006/11/27 16:56:39  bezema
 added a logger

 Revision 1.1  2006/11/23 11:46:40  bezema
 The initial version of the new wpvs

 Revision 1.1  2006/10/23 09:01:25  ap
 *** empty log message ***


 ********************************************************************** */