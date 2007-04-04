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
package org.deegree.model.crs;

import javax.media.jai.Interpolation;

import org.deegree.model.coverage.grid.AbstractGridCoverage;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.opengis.coverage.grid.GridCoverage;

public interface IGeoTransformer {

    /**
     * transforms the coodinates of a deegree geometry to the target
     * coordinate reference system.
     */
    public abstract Geometry transform( Geometry geo )
                            throws CRSTransformationException;

    /**
     * transfroms a <tt>Envelope</tt> to the target crs of the 
     * <tt>GeoTransformer</tt> instance
     *
     * @param envelope 
     * @param sourceCRS CRS of the envelope
     *
     * @return 
     *
     * @throws Exception 
     */
    public abstract Envelope transform( Envelope envelope, String sourceCRS )
                            throws CRSTransformationException;

    /**
     * transfroms a <tt>Envelope</tt> to the target crs of the 
     * <tt>GeoTransformer</tt> instance
     *
     * @param envelope 
     * @param sourceCRS CRS of the envelope
     *
     * @return 
     * @throws Exception 
     *
     * @throws Exception 
     */
    public abstract Envelope transform( Envelope envelope,
                                       org.deegree.model.crs.CoordinateSystem sourceCRS )
                            throws CRSTransformationException;

    /**
     * transforms all geometries contained within the passed @see FeatureCollection
     * into the target CRS of a GeoTransformer instance
     * 
     * @param fc
     * @return the transformed geometries in the Featurecollection
     * @throws GeometryException 
     * @throws CRSTransformationException 
     */
    public abstract FeatureCollection transform( FeatureCollection fc )
                            throws CRSTransformationException, GeometryException;

    /**
     * transforms all geometries contained within the passed @see Feature
     * into the target CRS of a GeoTransformer instance
     * 
     * @param feature
     * @return the transformed geometries in the given Feauture.
     * @throws GeometryException 
     * @throws CRSTransformationException 
     */
    public abstract Feature transform( Feature feature )
                            throws CRSTransformationException, GeometryException;

    /**
     * transforms a GridCoverage into another coordinate reference system. 
     * 
     * @param coverage grid coverage to transform
     * @param refPointsGridSize size of the grid used to calculate polynoms 
     *                          coefficients. E.g. 2 -&lg; 4 points, 3 -&lg; 9 points ...<br>
     *                          Must be &lg;= 2. Accuracy of coefficients increase with
     *                          size of the grid. Speed decreases with size of the grid. 
     * @param degree The degree of the polynomial is supplied as an argument.
     * @param interpolation interpolation method for warping the passed coverage. 
     *                      Can be <code>null</code>. In this case 'Nearest Neighbor' will
     *                      be used as default
     * @return
     */
    public abstract GridCoverage transform( AbstractGridCoverage coverage, int refPointsGridSize,
                                           int degree, Interpolation interpolation )
                            throws CRSTransformationException;

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: IGeoTransformer.java,v $
 Revision 1.2  2006/12/03 21:20:16  poth
 support for transforming GridCoverages added

 Revision 1.1  2006/11/27 09:07:51  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.


 ********************************************************************** */