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
 * This is the basis of all symbolizers. It defines the method * <tt>getGeometry</tt> that's common to all symbolizers. * <p>----------------------------------------------------------------------</p> * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:14 $
 */

public interface Symbolizer {

    /**
     * The ScaleDenominator-information is optional and determines whether
     * a rule (and thus a Symbolizer) is a to be applied at a certain scale.
     * @return the MinScaleDenominator
     */
    double getMinScaleDenominator();

    /**
     * Sets the MinScaleDenominator 
     * @param minScaleDenominator the MinScaleDenominator
     */
    void setMinScaleDenominator(double minScaleDenominator);

    /**
     * The ScaleDenominator-information is optional and determines whether
     * a rule (and thus a Symbolizer) is a to be applied at a certain scale.
     * @return the MaxScaleDenominator
     */
    double getMaxScaleDenominator();

    /**
     * Sets the MaxScaleDenominator 
     * @param maxScaleDenominator the MaxScaleDenominator
     */
    void setMaxScaleDenominator(double maxScaleDenominator);

    /**
     * The Geometry element is optional and if it is absent then the default
     * geometry property of the feature type that is used in the  containing
     * FeatureStyleType is used. The precise meaning of default geometry property
     * is system-dependent. Most frequently, feature types will have only a single
     * geometry property. 
     * @return the geometry of the symbolizer
     */
    Geometry getGeometry();

    /**
     * Sets the Geometry.
     * @param geometry the geometry of the symbolizer
     * 
     */
    void setGeometry(Geometry geometry);

    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Symbolizer.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
