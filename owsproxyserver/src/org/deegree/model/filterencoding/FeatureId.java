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

import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * Encapsulates the information of a <FeatureId>element as defined in the
 * FeatureId DTD. The <FeatureId>element is used to encode the unique
 * identifier for any feature instance. Within a filter expression, the
 * <FeatureId>is used as a reference to a particular feature instance.
 * 
 * @author Markus Schneider
 * @version 06.08.2002
 */
public class FeatureId {

    /** The FeatureId's value. */
    private String value;

    /** Constructs a new FeatureId. */
    public FeatureId( String value ) {
        this.value = value;
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static FeatureId buildFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name equals 'FeatureId'
        if ( !element.getLocalName().toLowerCase().equals( "featureid" ) )
            throw new FilterConstructionException( "Name of element does not equal 'FeatureId'!" );

        // determine the value of the FeatureId
        String fid = element.getAttribute( "fid" );
        if ( fid == null || "".equals( fid ) )
            throw new FilterConstructionException( "<FeatureId> requires 'fid'-attribute!" );

        return new FeatureId( fid );
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static FeatureId buildGMLIdFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name equals 'GmlObjectId'
        if ( !element.getLocalName().equals( "GmlObjectId" ) )
            throw new FilterConstructionException( "Name of element does not equal 'GmlObjectId'!" );

        // determine the requested id
        String gmlId = element.getAttributeNS( CommonNamespaces.GMLNS.toString(), "id" );
        if ( gmlId == null || "".equals( gmlId ) )
            throw new FilterConstructionException( "<GmlObjectId> requires 'gml:id'-attribute!" );

        return new FeatureId( gmlId );
    }

    /**
     * Returns the feature id. A feature id is built from it's feature type name
     * and it's id separated by a ".". e.g. Road.A565
     * 
     * @uml.property name="value"
     */
    public String getValue() {
        return value;
    }

    /**
     * @see org.deegree.model.filterencoding.FeatureId#getValue()
     * 
     * @uml.property name="value"
     */
    public void setValue( String value ) {
        this.value = value;
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append( "<ogc:FeatureId fid=\"" ).append( value ).append( "\"/>" );
        return sb;
    }
}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureId.java,v $
 Revision 1.8  2006/07/25 12:27:30  mschneider
 Fixed buildGMLIdFromDOM(). Uses value of attribute "gml:id" now, not "id".

 Revision 1.7  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
