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
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/08/17 13:25:36 $
 *
 * @since 2.0
 */
public class ClassificationResult {

    private int width = 0;
    private int height = 0;
    private int[] classIds = null;
    private double[][] repValue = null;
    private double[] accuracies = null;
    
    /**
     * 
     * @param width width of the classified raster
     * @param height height of the classified raster
     */
    public ClassificationResult(int width, int height) {
        this.width = width;
        this.height = height;
        classIds = new int[ width * height ];        
        repValue = new double[ width * height ][];
        accuracies = new double[ width * height ];
    }

    /**
     * returns the width of the result raster
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * returns the height of the result raster
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * @param i index position in x-direction
     * @param j index position in y-direction
     * @param classId 
     * @param representativeValue
     * @param accuracy
     */
    public void setResult( int i, int j, int classId, double[] representativeValue, double accuracy ) {
        int index = (j * width) + i;         
        classIds[ index ] = classId ;
        repValue[ index ] = representativeValue;
        accuracies[ index ] = accuracy;
    }
    
    /**
     * returns the classId for the passed index position. If the raster cell
     * at the passed position is not assigned to a class (did not match a rule)
     * -9999 will be returned. 
     * 
     * @param i
     * @param j
     * @return
     */
    public int getClassId(int i, int j) {
        int index = (j * width) + i;
        return classIds[ index ];
    }
    
    /**
     * returns the representative value for the raster cell at the
     * passed index position. If the raster cell
     * at the passed position is not assigned to a class (did not match a rule)
     * <code>null</code> will be returned. 
     * 
     * @param i
     * @param j
     * @return
     */
    public double[] getRepresentativeValueId(int i, int j) {
        int index = (j * width) + i;
        return repValue[ index ];
    }
    
    /**
     * returns the accuracy for the passed index position. If the raster cell
     * at the passed position is not assigned to a class (did not match a rule)
     * -9999 will be returned.
     * 
     * @param i
     * @param j
     * @return
     */
    public double getAccuracy(int i, int j) {
        int index = (j * width) + i;
        return accuracies[ index ];
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ClassificationResult.java,v $
Revision 1.2  2006/08/17 13:25:36  poth
bug fixes

Revision 1.1  2006/08/17 12:20:37  poth
implementation completed


********************************************************************** */