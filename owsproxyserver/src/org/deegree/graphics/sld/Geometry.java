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

import org.deegree.framework.xml.Marshallable;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.PropertyPath;

/**
 * The Geometry element is optional and if it is absent then the default geometry * property of the feature type that is used in the  containing FeatureStyleType * is used. The precise meaning of default geometry property is system-dependent. * Most frequently, feature types will have only a single geometry property. * <p>----------------------------------------------------------------------</p> *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @version $Revision: 1.9 $ $Date: 2006/07/12 14:46:14 $
 */

public class Geometry implements Marshallable {

    private org.deegree.model.spatialschema.Geometry geometry = null;

    private PropertyPath propertyPath = null; 

    Geometry( PropertyPath propertyPath, org.deegree.model.spatialschema.Geometry geometry ) {
        this.propertyPath = propertyPath;
        this.geometry = geometry;
    }
    
    /**
     * returns xpath information of the geometry property
     * @return
     */
    public PropertyPath getPropertyPath() {
        return propertyPath;
    }

    /**
     * In principle, a fixed geometry could be defined using GML or operators
     * could be defined for computing a geometry from references or literals.
     * This enbales the calling client to submitt the geometry to be rendered
     * by the WMS directly. (This is not part of the SLD XML-schema)
     * @return the Geometry
     * 
     */
    public org.deegree.model.spatialschema.Geometry getGeometry() {
        return geometry;
    }
    
    /**
     * exports the content of the Geometry as XML formated String
     *
     * @return xml representation of the Geometry
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<Geometry>" );
        if ( propertyPath != null ) {
            sb.append( "<ogc:PropertyName>" ).append( propertyPath.toString() );
            sb.append( "</ogc:PropertyName>" );
        } else {            
            try {
                sb.append( GMLGeometryAdapter.export(geometry) );
            } catch ( GeometryException e ) {
                e.printStackTrace();
            }
        }
        
        sb.append( "</Geometry>" );
                
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Geometry.java,v $
Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
