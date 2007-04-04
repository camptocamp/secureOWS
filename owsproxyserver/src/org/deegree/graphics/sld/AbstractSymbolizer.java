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
package org.deegree.graphics.sld;

/**
 * This is the basis of all symbolizers. It defines the method <tt>getGeometry</tt> * that's common to all symbolizers. * <p>----------------------------------------------------------------------</p> *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:14 $
 */

public abstract class AbstractSymbolizer implements Symbolizer {
    protected double maxDenominator = 9E99;
    protected double minDenominator = 0;

    protected Geometry geometry = null;

    protected String responsibleClass = null;

    /**
     * default constructor
     */
    AbstractSymbolizer() {
    }

    /**
     * constructor initializing the class with the <Symbolizer>
     */
    AbstractSymbolizer( Geometry geometry ) {
        setGeometry( geometry );
    }
    
    /**
     * constructor initializing the class with the <Symbolizer>
     */
    AbstractSymbolizer( Geometry geometry, String resonsibleClass ) {
        setGeometry( geometry );
        setResponsibleClass( resonsibleClass );
    }

    /**
     * The Geometry element is optional and if it is absent then the default
     * geometry property of the feature type that is used in the  containing
     * FeatureStyleType is used. The precise meaning of default geometry property
     * is system-dependent. Most frequently, feature types will have only a single
     * geometry property.
     * @return the geometry of the symbolizer
     * 
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * sets the <Geometry>
     * @param geometry the geometry of the symbolizer
     * 
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }


    /**
     * @return the MinScaleDenominator
     */
    public double getMinScaleDenominator() {
        return minDenominator;
    }

    /**
     * @param minDenominator the MinScaleDenominator
     */
    public void setMinScaleDenominator( double minDenominator ) {
        this.minDenominator = minDenominator;
    }

    /**  
     * @return the MaxScaleDenominator
     */
    public double getMaxScaleDenominator() {
        return maxDenominator;
    }

    /**     
     * @param maxDenominator the MaxScaleDenominator
     */
    public void setMaxScaleDenominator( double maxDenominator ) {
        this.maxDenominator = maxDenominator;
    }

    /**
     * returns the name of a class that will be used for rendering the current 
     * symbolizer. This enables a user to define his own rendering class 
     * (DisplayElement) for a symbolizer to realize styles/renderings that can't 
     * be defined using SLD at the moment.<BR>
     * The returned class must extend 
     * org.deegree_impl.graphics.displayelements.GeometryDisplayElement_Impl<BR>
     * For default the method returns the deegree default class name for
     * rendering the current symbolizer. 
     * 
     * @return
     * 
     */
    public String getResponsibleClass() {
        return responsibleClass;
    }

    /**
     * sets a class that will be used for rendering the current symbolizer.
     * This enables a user to define his own rendering class (DisplayElement)
     * for a symbolizer to realize styles/renderings that can't be defined
     * using SLD at the moment.<BR>
     * The passed class must extend 
     * org.deegree_impl.graphics.displayelements.GeometryDisplayElement_Impl
     * 
     * @param responsibleClass
     * 
     */
    public void setResponsibleClass(String responsibleClass) {
        this.responsibleClass = responsibleClass;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractSymbolizer.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
