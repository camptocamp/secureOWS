//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/XLinkedFeatureProperty.java,v 1.6 2006/10/16 09:34:59 poth Exp $
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
 Aennchenstraße 19
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
package org.deegree.model.feature;

import org.deegree.datatypes.QualifiedName;

/**
 * Feature property instance that does not specify it's content inline, but by referencing
 * a feature instance. 
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/10/16 09:34:59 $
 * 
 * @since 2.0
 */
public class XLinkedFeatureProperty implements FeatureProperty {

    private QualifiedName name;    
    
    private String targetFeatureId;
    
    private Feature targetFeature;    

    /**
     * Creates a new instance of <code>XLinkedFeatureProperty</code> from the given parameters.
     * <p>
     * NOTE: After creating, this property has no value. The reference to the target feature has to
     * be resolved first by calling #setValue(java.lang.Object).
     * 
     * @see #setValue(java.lang.Object)
     * 
     * @param name
     *            feature name
     * @param targetFeatureId
     *            id of the feature that this property contains
     */    
    public XLinkedFeatureProperty (QualifiedName name, String targetFeatureId) {
        this.name = name;
        this.targetFeatureId = targetFeatureId;
    }       

    /**
     * Returns the name of the property.
     * 
     * @return the name of the property.
     */    
    public QualifiedName getName() {
        return this.name;
    }
    
    /**
     * Returns the value of the property.
     * 
     * @return the value of the property.
     */     
    public Object getValue() {
        checkResolved();
        return this.targetFeature;
    }
    
    /**
     * Returns the value of the property.
     * 
     * @return the value of the property.
     */     
    public Object getValue(Object defaultValue) {
        checkResolved();
        if ( this.targetFeature == null ) {
            return defaultValue;
        }
        return this.targetFeature;
    }

    /**
     * Sets the target feature instance that this feature property refers to.
     * 
     * @param targetFeature
     *            feature instance that this feature property refers to.
     * @throws RuntimeException
     *             if the reference has already been resolved
     */    
    public void setValue( Object value ) {
        if (this.targetFeature != null) {
            String msg = Messages.format("ERROR_REFERENCE_ALREADY_RESOLVED", this.targetFeatureId);
            throw new RuntimeException (msg);            
        }
        this.targetFeature = (Feature) value;        
    }

    /* (non-Javadoc)
     * @see org.deegree.model.feature.FeatureProperty#getOwner()
     */
    public Feature getOwner() {
        return null;
    }

    /**
     * Returns the feature id of the target feature.
     * 
     * @return the feature id of the target feature.
     */
    public String getTargetFeatureId() {
        return this.targetFeatureId;
    }            
    
    /**
     * Ensures that the reference to the target feature has been resolved.
     * 
     * @throws RuntimeException
     *             if the reference has not been resolved
     */
    private void checkResolved () {
        if (this.targetFeature == null) {
            String msg = Messages.format("ERROR_XLINK_NOT_RESOLVED", this.targetFeatureId);
            throw new RuntimeException (msg);
        }
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XLinkedFeatureProperty.java,v $
Revision 1.6  2006/10/16 09:34:59  poth
enbaled default value return for Feature.getDefaultProperty and FeatureProperty.getValue

Revision 1.5  2006/04/07 17:13:11  mschneider
Improved javadoc.

Revision 1.4  2006/04/06 20:25:27  poth
*** empty log message ***

Revision 1.3  2006/04/04 20:39:42  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.1  2006/01/20 18:13:16  mschneider
Initial version.

********************************************************************** */