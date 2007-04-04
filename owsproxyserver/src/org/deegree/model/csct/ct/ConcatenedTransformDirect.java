/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
lat/lon GmbH
http://www.lat-lon.de

It has been implemented within SEAGIS - An OpenSource implementation of OpenGIS specification
(C) 2001, Institut de Recherche pour le D�veloppement (http://sourceforge.net/projects/seagis/)
SEAGIS Contacts:  Surveillance de l'Environnement Assist�e par Satellite
                  Institut de Recherche pour le D�veloppement / US-Espace
                  mailto:seasnet@teledetection.fr


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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree.model.csct.ct;

import org.deegree.model.csct.pt.CoordinatePoint;


/**
 * Concatened transform where the transfert dimension
 * is the same than source and target dimension. This
 * fact allows some optimizations, the most important
 * one being the possibility to avoid the use of an
 * intermediate buffer.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 */
class ConcatenedTransformDirect extends ConcatenedTransform {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3568975979013908920L;

    /**
     * Construct a concatenated transform.
     */
    public ConcatenedTransformDirect( final MathTransformFactory provider, 
                                      final MathTransform transform1, 
                                      final MathTransform transform2 ) {
        super( provider, transform1, transform2 );
    }

    /**
     * Check if transforms are compatibles
     * with this implementation.
     */
    protected boolean isValid() {
        return super.isValid() && 
               ( transform1.getDimSource() == transform1.getDimTarget() ) && 
               ( transform2.getDimSource() == transform2.getDimTarget() );
    }

    /**
     * Transforms the specified <code>ptSrc</code> and stores the result in <code>ptDst</code>.
     */
    public CoordinatePoint transform( final CoordinatePoint ptSrc, CoordinatePoint ptDst )
                              throws TransformException {        
        ptDst = transform1.transform( ptSrc, ptDst );
        return transform2.transform( ptDst, ptDst );
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    public void transform( final double[] srcPts, final int srcOff, final double[] dstPts, 
                           final int dstOff, final int numPts ) throws TransformException {
        
        transform1.transform( srcPts, srcOff, dstPts, dstOff, numPts );
        transform2.transform( dstPts, dstOff, dstPts, dstOff, numPts );
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    public void transform( final float[] srcPts, final int srcOff, final float[] dstPts, 
                           final int dstOff, final int numPts ) throws TransformException {
        
        transform1.transform( srcPts, srcOff, dstPts, dstOff, numPts );
        transform2.transform( dstPts, dstOff, dstPts, dstOff, numPts );
    }
}