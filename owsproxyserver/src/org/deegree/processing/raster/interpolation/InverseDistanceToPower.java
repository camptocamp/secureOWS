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
package org.deegree.processing.raster.interpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.Values;
import org.deegree.io.quadtree.IndexException;
import org.deegree.io.quadtree.Quadtree;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * Class for interpolating a set of data tuples (x, y, value) onto
 * a grid using Inverse Distance to Power algorithm
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: schmitz $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/11/17 13:59:47 $
 *
 * @since 2.0
 */
public class InverseDistanceToPower extends Interpolation {

    private double power = 2;

    /**
     * 
     * @param data
     * @param power
     */
    public InverseDistanceToPower( Quadtree data, double power ) {
        super( data );
        this.power = power;
    }

    /**
     * 
     * @param data
     * @param ignoreValues
     * @param power
     */
    public InverseDistanceToPower( Quadtree data, Values ignoreValues, double power ) {
        super( data, ignoreValues );
        this.power = power;
    }

    /**
     * 
     * @param data
     * @param ignoreValues
     * @param searchRadius1
     * @param searchRadius2
     * @param searchRadiusAngle
     * @param minData
     * @param maxData
     * @param noValue
     * @param autoincreaseSearchRadius1 
     * @param autoincreaseSearchRadius2 
     * @param power
     */
    public InverseDistanceToPower( Quadtree data, Values ignoreValues, double searchRadius1,
                                  double searchRadius2, double searchRadiusAngle, int minData,
                                  int maxData, double noValue, double autoincreaseSearchRadius1,
                                  double autoincreaseSearchRadius2, double power ) {
        super( data, ignoreValues, searchRadius1, searchRadius2, searchRadiusAngle, minData,
               maxData, noValue, autoincreaseSearchRadius1, autoincreaseSearchRadius2 );
        this.power = power;
    }

    /**
     * calculates the interpolated value for a position defined by x and y
     * 
     * @param x 
     * @param y 
     * @return the interpolated value
     * @throws InterpolationException
     */
    @Override
    public double calcInterpolatedValue( double x, double y )
                            throws InterpolationException {
        try {
            Envelope searchRadius = GeometryFactory.createEnvelope( x - searchRadius1,
                                                                    y - searchRadius2,
                                                                    x + searchRadius1,
                                                                    y + searchRadius2, 
                                                                    null );
            List foundValues = data.query( searchRadius );

            List<DataTuple> values = new ArrayList<DataTuple>();
            for ( Object obj : foundValues ) {
                DataTuple tuple = (DataTuple) obj;

                boolean ignore = false;

                if ( ignoreValues != null && ignoreValues.getInterval().length > 0 ) {
                    for ( Interval interval : ignoreValues.getInterval() ) {
                        double min = Double.parseDouble( interval.getMin().getValue() );
                        double max = Double.parseDouble( interval.getMax().getValue() );
                        if ( tuple.value > min && tuple.value < max ) {
                            ignore = true;
                        }
                    }
                }

                if ( !ignore ) {
                    double dx = Math.abs( tuple.x - x );
                    double dy = Math.abs( tuple.y - y );
                    /*
                    if ( dx == 0 && dy == 0 ) {
                        //                    System.out.println( "Call with already existing value!" );
                        return tuple.value;
                    }
                    */
                    double dist = Math.sqrt( dx * dx + dy * dy );
                    double weight = Math.pow( dist, power );
                    values.add( new DataTuple( dist, tuple.value, weight ) );
                }
            }

            if ( values.size() < minData ) {
                if ( autoincreaseSearchRadius1 == 0 && autoincreaseSearchRadius2 == 0 ) {
                    return noValue;
                }

                searchRadius1 += autoincreaseSearchRadius1;
                searchRadius2 += autoincreaseSearchRadius2;
                System.out.println( "Warning: not enough data to interpolate point."
                                    + " Increasing the search radius to " + searchRadius1 + " and "
                                    + searchRadius2 );
                return calcInterpolatedValue( x, y );
            }

            if ( values.size() > maxData ) {
                Collections.sort( values );
                values = values.subList( 0, maxData );
            }

            double valueSum = 0;

            double weightSum = 0;

            for ( Object obj : values ) {
                // in the data tuple, x is interpreted as the distance, y is the value and
                // "value" is the weight
                DataTuple tuple = (DataTuple) obj;
                valueSum += ( tuple.y / tuple.value );
                weightSum += ( 1 / tuple.value );
            }

            double result = ( valueSum / weightSum );

            if ( Double.isInfinite( result ) ) {
                return noValue;
            }

            if ( Double.isNaN( result ) ) {
                return noValue;
            }

            return result;
        } catch ( IndexException e ) {
            throw new InterpolationException( e );
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: InverseDistanceToPower.java,v $
 Revision 1.6  2006/11/17 13:59:47  schmitz
 Fixed distance calculation.

 Revision 1.5  2006/11/16 21:03:44  poth
 *** empty log message ***

 Revision 1.4  2006/11/16 17:11:17  poth
 bug fix - calculation of weight

 Revision 1.3  2006/10/25 11:59:04  schmitz
 Text2Tiff is unfinished due to problems with geotiff format.
 The rest of the interpolation/Text2Tiff should work fine now.

 Revision 1.2  2006/10/20 14:57:08  schmitz
 Added a memory point quadtree implementation.
 Used the quadtree for interpolation.
 Updated the text2tiff tool to use quadtree and interpolation.

 Revision 1.1  2006/10/12 15:44:57  poth
 initial checkin


 ********************************************************************** */