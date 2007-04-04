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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/08/17 13:25:22 $
 *
 * @since 2.0
 */
public class ClassifierFactory {

    /**
     * creates a classifier using @see RangeRule instances. <code>values</code> -1
     * rules will be created by the following method:<br>
     * range 1 = values[0] ... values[1]<br>
     * range 2 = values[1] ... values[2]<br>
     * ...
     * range n = values[values.length-2] ... values[values.length-1]  
     *   
     * @param values
     * @return
     * @throws ClassificationException
     */
    public static AbstractClassifier createRangeClassifier( double... values )
                            throws ClassificationException {

        List<ClassificationRule> rules = new ArrayList<ClassificationRule>( values.length );
        for ( int i = 0; i < values.length - 1; i++ ) {
            double[] repValue = new double[] { ( values[i] + values[i + 1] ) / 2d };
            ClassificationRule rule = new RangeRule( i + 1, "-", repValue,
                                                     new double[] { values[i] },
                                                     new double[] { values[i + 1] } );
            rules.add( rule );
        }

        return new SimpleClassifier( rules );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ClassifierFactory.java,v $
 Revision 1.1  2006/08/17 13:25:22  poth
 initial check in


 ********************************************************************** */