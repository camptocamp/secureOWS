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

import org.deegree.framework.xml.Marshallable;


/**
 * The LayerFeatureConstraints element is optional in a NamedLayer and allows the
 * user to specify constraints on what features of what feature types are to be
 * selected by the named-layer reference. It is essentially a filter that allows
 * the selection of fewer features than are present in the named layer.
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.9 $ $Date: 2006/11/27 09:07:52 $
 */
public class LayerFeatureConstraints implements  Marshallable {
    private ArrayList featureTypeConstraint = null;

    /**
    * constructor initializing the class with the <LayerFeatureConstraints>
    */
    LayerFeatureConstraints( FeatureTypeConstraint[] featureTypeConstraint ) {
        this.featureTypeConstraint = new ArrayList();
        setFeatureTypeConstraint( featureTypeConstraint );
    }

    /**
     * A FeatureTypeConstraint element is used to identify a feature type by a
     * well-known name, using the FeatureTypeName element.
     * @return the FeatureTypeConstraints as Array
     * 
     * @uml.property name="featureTypeConstraint"
     */
    public FeatureTypeConstraint[] getFeatureTypeConstraint() {
        return (FeatureTypeConstraint[]) featureTypeConstraint
            .toArray(new FeatureTypeConstraint[featureTypeConstraint.size()]);
    }

   /** 
    * sets the <FeatureTypeConstraint>
    * @param featureTypeConstraint the <FeatureTypeConstraint>
    */
    public void setFeatureTypeConstraint( FeatureTypeConstraint[] featureTypeConstraint ) {
        this.featureTypeConstraint.clear();

        if ( featureTypeConstraint != null ) {
            for ( int i = 0; i < featureTypeConstraint.length; i++ ) {
                this.featureTypeConstraint.add( featureTypeConstraint[i] );
            }
        }
    }
    
    /**
    * adds the <FeatureTypeConstraint>
    * @param featureTypeConstraint the <FeatureTypeConstraint>
    */
    public void addFeatureTypeConstraint( FeatureTypeConstraint featureTypeConstraint ) {
        this.featureTypeConstraint.add( featureTypeConstraint );
    }    

   /**
    * Removes a FeatureTypeConstraint.
    * @param featureTypeConstraint the <FeatureTypeConstraint>
    */
    public void removeFeatureTypeConstraint(FeatureTypeConstraint featureTypeConstraint) {
        this.featureTypeConstraint.remove( this.featureTypeConstraint.indexOf(featureTypeConstraint) );
    }
    
    /**
     * returns the LayerFeatureConstraints as String.
     * @return the LayerFeatureConstraints as String
     */
    public String toString() {
        String ret = getClass().getName() + "\n";
        ret = "featureTypeConstraint = " + featureTypeConstraint + "\n";

        return ret;
    }
    
    /**
     * exports the content of the Font as XML formated String
     *
     * @return xml representation of the Font
     */
    public String exportAsXML() {
        
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<LayerFeatureConstraints>" );
        for (int i = 0; i <  featureTypeConstraint.size(); i++) {
            sb.append( ((Marshallable)featureTypeConstraint.get( i )).exportAsXML() );
        }
        sb.append( "</LayerFeatureConstraints>" );
        
        
        return sb.toString();
    }

    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LayerFeatureConstraints.java,v $
Revision 1.9  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.8  2006/07/29 08:51:11  poth
references to deprecated classes removed

Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
