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

import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.pt.PT_Envelope;

/**
 * A <tt>RasterLayer</tt> represent a layer which data are contained within one  * single <tt>Image</tt>. The image/raster is geo-referenced by a <tt>Envelope</tt> * that is linked to it. *  * <p>------------------------------------------------------------------------</p> *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @version $Revision: 1.8 $ $Date: 2006/05/31 12:06:31 $
 */

class RasterLayer extends AbstractLayer {

    protected GridCoverage raster = null;

    /**
     * Creates a new AbstractLayer object.
     *
     * @param name  
     * @param raster
     *
     * @throws Exception 
     */
    RasterLayer(String name, GridCoverage raster) 
                     throws Exception {
        super( name, raster.getCoordinateReferenceSystem() );
        setRaster( raster );
    }   

    /**
     * sets the coordinate reference system of the MapView. If a new crs is set
     * all geometries of GeometryFeatures will be transformed to the
     * new coordinate reference system.
     */
    public void setCoordinatesSystem( CoordinateSystem crs ) throws Exception {
        //throw new NoSuchMethodError( "not implemented yet" );
    }

    /**
     * returns the image/raster that represents the layers data
     */
    public GridCoverage getRaster() {
        return raster;
    }

    /**
     * sets the image/raster that represents the layers data
     */
    public void setRaster(GridCoverage raster) throws Exception {
        this.raster = raster;
        PT_Envelope pt = raster.getEnvelope();
        Envelope env = GeometryFactory.createEnvelope( pt.minCP.ord[0], pt.minCP.ord[1],  
                                                       pt.maxCP.ord[0], pt.maxCP.ord[1], null );
        this.boundingbox = env;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RasterLayer.java,v $
Revision 1.8  2006/05/31 12:06:31  poth
bug fix - setting boundingbox when a raster is set

Revision 1.7  2006/05/24 08:04:48  poth
*** empty log message ***


********************************************************************** */