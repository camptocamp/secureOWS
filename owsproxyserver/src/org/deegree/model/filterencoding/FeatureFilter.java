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
package org.deegree.model.filterencoding;

import java.util.ArrayList;

import org.deegree.model.feature.Feature;

/**
 * Encapsulates the information of a <Filter>element that consists of a number
 * of FeatureId constraints (only) (as defined in the FeatureId DTD).
 * 
 * @author Markus Schneider
 * @version 06.08.2002
 */
public class FeatureFilter extends AbstractFilter {

    /** FeatureIds the FeatureFilter is based on */
    private ArrayList<FeatureId> featureIds = new ArrayList<FeatureId>();

    /** Adds a FeatureId constraint. 
     * @param featureId 
     * 
     */
    public void addFeatureId(FeatureId featureId) {
        featureIds.add(featureId);
    }

    /**
     * @return the contained FeatureIds.
     * 
     * @uml.property name="featureIds"
     */
    public ArrayList<FeatureId> getFeatureIds() {
        return featureIds;
    }

    /**
     * Calculates the <tt>FeatureFilter</tt>'s logical value based on the ID
     * of the given <tt>Feature</tt>. FIXME!!! Use a TreeSet (or something)
     * to speed up comparison.
     * 
     * @param feature
     *            that determines the Id
     * @return true, if the <tt>FeatureFilter</tt> evaluates to true, else
     *         false
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public boolean evaluate(Feature feature) throws FilterEvaluationException {
        String id = feature.getId();
        for (int i = 0; i < featureIds.size(); i++) {
            FeatureId featureId = featureIds.get(i);
            if (id.equals(featureId.getValue())) return true;
        }
        return false;
    }

    /** Produces an indented XML representation of this object. */
    @Override
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer(500);
        sb.append("<ogc:Filter xmlns:ogc='http://www.opengis.net/ogc'>");
        for (int i = 0; i < featureIds.size(); i++) {
            FeatureId fid = featureIds.get(i);
            sb.append(fid.toXML());
        }
        sb.append("</ogc:Filter>");
        return sb;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureFilter.java,v $
Revision 1.7  2006/11/23 09:30:14  bezema
added generic and javadoc

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
