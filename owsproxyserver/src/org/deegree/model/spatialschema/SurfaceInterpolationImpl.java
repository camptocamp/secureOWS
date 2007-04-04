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
package org.deegree.model.spatialschema;

import java.io.Serializable;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;



/**
* default implementation of the SurfaceInterpolation interface from the
* package jago.model. 
*
* ------------------------------------------------------------
* @version 11.6.2001
* @author Andreas Poth
*/
public class SurfaceInterpolationImpl implements SurfaceInterpolation, Serializable {
    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = -3728721225837686088L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( SurfaceInterpolationImpl.class );
    
    private int surfaceInterpolation = NONE;

    /**
     * Creates a new SurfaceInterpolationImpl object.
     */
    public SurfaceInterpolationImpl() {
    }

    /**
     * Creates a new SurfaceInterpolationImpl object.
     *
     * @param surfaceInterpolation 
     *
     * @throws GeometryException 
     */
    public SurfaceInterpolationImpl( int surfaceInterpolation ) throws GeometryException {
        if ( ( surfaceInterpolation > TRIANGULATEDSOLINE ) || ( surfaceInterpolation < NONE ) ) {
            throw new GeometryException( "invalid surface interpolation" );
        }
    }

    /**
     *
     *
     * @return 
     */
    public int getValue() {
        return surfaceInterpolation;
    }

    /**
    * returns a deep copy of the geometry
    */
    public Object clone() {
        SurfaceInterpolation si = null;

        try {
            si = new SurfaceInterpolationImpl( getValue() );
        } catch ( Exception ex ) {
            LOG.logError( "SurfaceInterpolationImpl.clone: ", ex );
        }

        return si;
    }

    /**
    * checks if this surface is completly equal to the submitted geometry.
    */
    public boolean equals( Object other ) {
        return ( other instanceof SurfaceInterpolationImpl ) && 
               ( ( (SurfaceInterpolation)other ).getValue() == surfaceInterpolation );
    }
} /* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SurfaceInterpolationImpl.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
