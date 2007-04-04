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
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/08/17 12:20:37 $
 *
 * @since 2.0
 */
abstract class AbstractClassificationRule implements ClassificationRule {
    
    private String description;
    private double[] representativeValue;
    private int classId;
    
    /**
     * 
     * @param classId
     * @param description
     * @param representativeValue
     */
    public AbstractClassificationRule( int classId, String description, double[] representativeValue) {
        this.description = description;
        this.representativeValue = representativeValue;
        this.classId = classId;
    }

    /**
     * returns a vector representative for a rule 
     * 
     * @return
     */
    public int getClassId() {
        return classId;
    }

    /**
     * returns a description of a rule
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * returns a vector representative for a rule 
     * 
     * @return
     */
    public double[] getRepresentativeValue() {
        return representativeValue;
    }
    
    
    

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractClassificationRule.java,v $
Revision 1.1  2006/08/17 12:20:37  poth
implementation completed


********************************************************************** */