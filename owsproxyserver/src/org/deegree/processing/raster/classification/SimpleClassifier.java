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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.processing.raster.DataMatrix;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/08/17 13:25:36 $
 *
 * @since 2.0
 */
public class SimpleClassifier extends AbstractClassifier {
    
    private ILogger LOG = LoggerFactory.getLogger( SimpleClassifier.class );

    /**
     * 
     * @param rules
     */
    public SimpleClassifier( List<ClassificationRule> rules ) {
        super( rules );
    }

    @Override
    public ClassificationResult classify( DataMatrix data ) throws ClassificationException {
        
        int width = data.getWidth();
        int height = data.getHeight();
        ClassificationResult result = new ClassificationResult( width, height );
        for ( int i = 0; i < width; i++ ) {
            for ( int j = 0; j < height; j++ ) {
                double[] vector = data.getCellAt( i, j );
                ClassificationRule rule = findRule( vector );
                if ( rule != null ) {
                    result.setResult( i, j, rule.getClassId(), rule.getRepresentativeValue(),
                                      rule.getAccuracy() );
                    String s = StringTools.concat( 100, "cell at position ", i, ' ', j, 
                                                   " classified as: ", rule.getClassId() );                    
                    LOG.logDebug( s );
                } else {
                    result.setResult( i, j, -9999, null, -9999 );
                    LOG.logDebug( "no matching rule found for position " + i + ' ' + j );
                }
            }
        }
        
        return result;
    }

    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SimpleClassifier.java,v $
Revision 1.2  2006/08/17 13:25:36  poth
bug fixes

Revision 1.1  2006/08/17 12:20:37  poth
implementation completed


********************************************************************** */