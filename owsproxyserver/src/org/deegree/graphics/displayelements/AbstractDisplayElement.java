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
package org.deegree.graphics.displayelements;

import java.io.Serializable;

import org.deegree.model.feature.Feature;

/**
 * Basic interface of all display elements. A <tt>DisplayElement</tt> is
 * associated to one feature that may have a geometry property or not
 * (usually it has).
 * <p>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:16 $
 */

abstract class AbstractDisplayElement implements DisplayElement, Serializable {

    /** Use serialVersionUID for interoperability. */
    private final static long serialVersionUID = 1226236249388451855L;

    protected Feature feature = null;

    private boolean highlighted = false;

    private boolean selected = false;

    /**
     * 
     */
    AbstractDisplayElement() {
    }

    /**
     * 
     *
     * @param feature 
     */
    AbstractDisplayElement( Feature feature ) {
        if ( feature != null ) {
            setFeature( feature );
        }
    }

    /**
     * Returns the associated <tt>Feature</tt>.
     * 
     */
    public Feature getFeature() {
        return feature;
    }

    /**
     * sets the feature encapsulated by a DisplayElement
     * @param feature
     */
    public void setFeature( Feature feature ) {
       this.feature = new ScaledFeature( feature, -1 );
    }

    /**
     * returns the id of the feature that's associated with the
     * DisplayElement
     */
    public String getAssociateFeatureId() {
        return feature.getId();
    }

    /**
     * marks a <tt>DisplayElement</tt> as selected or not
     * 
     */
    public void setSelected( boolean selected ) {
        this.selected = selected;
    }

    /**
     * returns if the <tt>DisplayElement</tt> is selected or not
     * 
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * marks the <tt>DisplayElement</tt> as highlighted or not
     * 
     */
    public void setHighlighted( boolean highlighted ) {
        this.highlighted = highlighted;
    }

    /**
     * returns if the <tt>DisplayElement</tt> is highlighted or not.
     * 
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Returns if the <tt>DisplayElement</tt> should be painted at the
     * current scale or not.
     */
    public boolean doesScaleConstraintApply( double scale ) {
        return true;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractDisplayElement.java,v $
Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
