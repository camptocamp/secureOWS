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
package org.deegree.processing.raster.classification;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/08/17 12:20:37 $
 *
 * @since 2.0
 */
public class FixedValuesRule extends AbstractClassificationRule {

    private double[] values;

    private double[] tolerance;

    private double accuracy = 0;
    
    /**
     * 
     * @param values
     * @param classId
     * @param description
     * @param representativeValue
     */
    public FixedValuesRule( double[] values, int classId, String description, 
                            double[] representativeValue) {
        super ( classId, description, representativeValue );
        this.values = values;
        this.tolerance = new double[values.length];        
    }
    
    /**
     * 
     * @param values
     * @param tolerance
     * @param classId
     * @param description
     * @param representativeValue
     * @throws ClassificationException
     */
    public FixedValuesRule( double[] values, double[] tolerance, int classId, String description, 
                            double[] representativeValue) throws ClassificationException {
        super ( classId, description, representativeValue );
        this.values = values;
        this.tolerance = tolerance;
        if ( values.length != tolerance.length ) {
            throw new ClassificationException( Messages.getString("FixedValuesRule.ARRAYLENGTHERROR") ); //$NON-NLS-1$
        }
    }

    /**
     * @param vector
     * @throws ClassificationException
     */
    public boolean matches( double[] vector )
                            throws ClassificationException {
        if ( vector.length != values.length ) {
            throw new ClassificationException( "RangeRule.VECTORLENGTHERROR" );
        }
        accuracy = 0;
        boolean matches = true;
        for ( int i = 0; i < vector.length; i++ ) {
            if ( vector[i] < ( values[i] - tolerance[i] ) || 
                 vector[i] > ( values[i] + tolerance[i] ) ) {
                matches = false;
            }
            accuracy += Math.pow( vector[i] - values[i], 2 ); 
        }
        accuracy = Math.sqrt( accuracy );
        return matches;
    }
    
    /**
     * returns a value indicating the accuracy a classified vector matches
     * a rule. For @see FixedValuesRule accuracy is the euklidian distance
     * between the values array and the classified vector.
     * 
     * @return
     */
    public double getAccuracy() {
        return accuracy;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FixedValuesRule.java,v $
 Revision 1.2  2006/08/17 12:20:37  poth
 implementation completed

 Revision 1.1  2006/08/16 21:00:11  poth
 initial check in


 ********************************************************************** */