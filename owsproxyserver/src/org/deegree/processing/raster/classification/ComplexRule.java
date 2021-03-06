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

import java.util.List;

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
public class ComplexRule extends AbstractClassificationRule {

    private List<ClassificationRule> rules;
    private double accuracy = 0;

    /**
     * 
     * @param classId
     * @param description
     * @param representativeValue
     * @param rules
     */
    public ComplexRule( int classId, String description, double[] representativeValue,
                        List<ClassificationRule> rules ) {
        super ( classId, description, representativeValue );
        this.rules = rules;
    }

    /**
     * returns true if the passed vector matches all encapsulated rules
     * 
     * @param vector
     * @throws ClassificationException
     */
    public boolean matches( double[] vector )
                            throws ClassificationException {
        accuracy = 0;
        boolean matches = true;
        for ( int i = 0; i < rules.size(); i++ ) {
            if ( !rules.get( i ).matches( vector ) ) {
                matches = false;
            }
            accuracy += rules.get( i ).getAccuracy();
        }
        accuracy /= rules.size();
        return matches;
    }
    
    /**
     * returns a value indicating the accuracy a classified vector matches
     * a rule. For @see ComplexRule accuracy value will be calculated as
     * arithmetic mean of accuracies of the encapsulated rules 
     * 
     * @return
     */
    public double getAccuracy() {
        return accuracy;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ComplexRule.java,v $
 Revision 1.2  2006/08/17 12:20:37  poth
 implementation completed

 Revision 1.1  2006/08/16 21:00:11  poth
 initial check in


 ********************************************************************** */