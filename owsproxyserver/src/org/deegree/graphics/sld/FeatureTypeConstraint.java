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

import java.util.ArrayList;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.Marshallable;
import org.deegree.model.filterencoding.Filter;

/**
 * A FeatureTypeConstraint element is used to identify a feature type by * well-known name, using the FeatureTypeName element. *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a> * @version $Revision: 1.10 $ $Date: 2006/11/29 21:28:30 $
 */

public class FeatureTypeConstraint implements Marshallable {

    private ArrayList extents = null;
    
    private Filter filter = null;

    private QualifiedName featureTypeName = null;

    /**
     * constructor initializing the class with the <FeatureTypeConstraint>
     */
    FeatureTypeConstraint(QualifiedName featureTypeName, Filter filter,
            Extent[] extents) {
        this.extents = new ArrayList();
        setFeatureTypeName(featureTypeName);
        setFilter(filter);
        setExtents(extents);
    }

    /**
     * returns the name of the feature type
     * 
     * @return the name of the feature type
     */
    public QualifiedName getFeatureTypeName() {
        return featureTypeName;
    }

    /**
     * sets the name of the feature type
     * 
     * @param featureTypeName
     *            the name of the feature type
     */
    public void setFeatureTypeName(QualifiedName featureTypeName) {
        this.featureTypeName = featureTypeName;
    }

    /**
     * returns a feature-filter as defined in WFS specifications.
     * 
     * @return the filter of the FeatureTypeConstraints
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * sets a feature-filter as defined in WFS specifications.
     * 
     * @param filter
     *            the filter of the FeatureTypeConstraints
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * returns the extent for filtering the feature type
     * 
     * @return the extent for filtering the feature type
     */
    public Extent[] getExtents() {
        return (Extent[]) extents.toArray(new Extent[extents.size()]);
    }

    /**
     * sets the extent for filtering the feature type
     * 
     * @param extents
     *            extents for filtering the feature type
     */
    public void setExtents(Extent[] extents) {
        this.extents.clear();

        if (extents != null) {
            for (int i = 0; i < extents.length; i++) {
                addExtent(extents[i]);
            }
        }
    }

    /**
     * Adds an Extent to the Extent-List of a FeatureTypeConstraint
     * 
     * @param extent
     *            an extent to add
     */
    public void addExtent(Extent extent) {
        extents.add(extent);
    }

    /**
     * Removes an Extent from the Extent-List of a FeatureTypeConstraint
     * 
     * @param extent
     *            an extent to remove
     */
    public void removeExtent(Extent extent) {
        extents.remove(extents.indexOf(extent));
    }

    /**
     * @return the FeatureTypeConstraint as String
     */
    public String toString() {
        String ret = getClass().getName() + "\n";
        ret = "featureTypeName = " + featureTypeName + "\n";
        ret += ("filter = " + filter + "\n");
        ret += ("extents = " + extents + "\n");

        return ret;
    }

    /**
     * exports the content of the FeatureTypeConstraint as XML formated String
     * 
     * @return xml representation of the FeatureTypeConstraint
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append("<FeatureTypeConstraint>");
        sb.append("<FeatureTypeName>").append(featureTypeName);
        sb.append("</FeatureTypeName>");
        if (filter != null) {
            sb.append(filter.toXML());
        }
        if (extents != null) {
            for (int i = 0; i < extents.size(); i++) {
                sb.append(((Marshallable) extents.get(i)).exportAsXML());
            }
        }
        sb.append("</FeatureTypeConstraint>");

        return sb.toString();
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureTypeConstraint.java,v $
Revision 1.10  2006/11/29 21:28:30  poth
bug fixing - SLD GetMap requests containing user layers with featuretypeconstraints

Revision 1.9  2006/11/29 20:20:44  poth
*** empty log message ***

Revision 1.8  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
