//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/filterencoding/AbstractFilter.java,v 1.11 2006/07/26 16:10:40 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.model.filterencoding;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract superclass representing <code>Filter</code> elements (as defined in the Filter DTD). A
 * <code>Filter</code> element either consists of (one or more) FeatureId-elements or one
 * operation-element. This is reflected in the two implementations FeatureFilter and ComplexFilter.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.11 $, $Date: 2006/07/26 16:10:40 $
 */
public abstract class AbstractFilter implements Filter {

    /**
     * Given a DOM-fragment, a corresponding Filter-object is built. This method recursively calls
     * other buildFromDOM () - methods to validate the structure of the DOM-fragment.
     *
     * @param element 
     * @return corresponding Filter-object
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Filter buildFromDOM( Element element ) throws FilterConstructionException {
        Filter filter = null;

        // check if root element's name equals 'filter'
        if ( !element.getLocalName().equals( "Filter" ) ) {
            throw new FilterConstructionException( "Name of element does not equal 'Filter'!" );
        }

        // determine type of Filter (FeatureFilter / ComplexFilter)
        Element firstElement = null;
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if ( children.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
                firstElement = (Element) children.item( i );
            }
        }
        if ( firstElement == null ) {
            throw new FilterConstructionException( "Filter node is empty!" );
        }

        if ( firstElement.getLocalName().equals( "FeatureId" ) ) {
            // must be a FeatureFilter
            FeatureFilter fFilter = new FeatureFilter();
            children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if ( children.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
                    Element fid = (Element) children.item( i );
                    if ( !fid.getLocalName().equals( "FeatureId" ) )
                        throw new FilterConstructionException( "Unexpected element encountered: "
                            + fid.getLocalName() );
                    fFilter.addFeatureId( FeatureId.buildFromDOM( fid ) );
                }
            }
            filter = fFilter;
        } else if ( firstElement.getLocalName().equals( "GmlObjectId" ) ) {
            // must be a FeatureFilter
            FeatureFilter fFilter = new FeatureFilter();
            children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if ( children.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
                    Element fid = (Element) children.item( i );
                    if ( !fid.getLocalName().equals( "GmlObjectId" ) )
                        throw new FilterConstructionException( "Unexpected element encountered: "
                            + fid.getLocalName() );
                    fFilter.addFeatureId( FeatureId.buildGMLIdFromDOM( fid ) );
                }
            }
            filter = fFilter;
        } else {
            // must be a ComplexFilter
            children = element.getChildNodes();
            boolean justOne = false;
            for (int i = 0; i < children.getLength(); i++) {
                if ( children.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
                    Element operator = (Element) children.item( i );
                    if ( justOne )
                        throw new FilterConstructionException( "Unexpected element encountered: "
                            + operator.getLocalName() );
                    ComplexFilter cFilter = 
                        new ComplexFilter( AbstractOperation.buildFromDOM( operator ) );
                    filter = cFilter;
                    justOne = true;
                }
            }
        }
        return filter;
    }

    /**
     * Produces an XML representation of this object.
     *  
     * @return an XML representation of this object
     */
    public abstract StringBuffer toXML();
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractFilter.java,v $
Revision 1.11  2006/07/26 16:10:40  mschneider
Fixed header. Improved javadoc.

Revision 1.10  2006/07/25 12:28:17  mschneider
Fixed spelling in exception messages.

Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */