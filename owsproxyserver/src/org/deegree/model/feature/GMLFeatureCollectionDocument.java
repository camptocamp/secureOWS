//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/GMLFeatureCollectionDocument.java,v 1.13 2006/09/13 23:59:33 mschneider Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.util.IDGenerator;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Parser and wrapper class for GML feature collections.
 * <p>
 * Extends {@link GMLFeatureDocument}, as a feature collection is a feature in the GML
 * type hierarchy.
 * <p>
 * 
 * TODO Remove hack for xlinked feature members (should be easy after fixing model package).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.13 $, $Date: 2006/09/13 23:59:33 $
 * 
 * @see GMLFeatureDocument
 */
public class GMLFeatureCollectionDocument extends GMLFeatureDocument {

    private static final long serialVersionUID = -6923435144671685710L;

    private Collection<String> xlinkedMembers = new ArrayList<String>();

    /**
     * Creates a new instance of <code>GMLFeatureCollectionDocument</code>.
     * <p>
     * Simple types encountered during parsing are "guessed", i.e. the parser tries to convert
     * the values to double, integer, calendar, etc. However, this may lead to unwanted results,
     * e.g. a property value of "054604" is converted to "54604". 
     */
    public GMLFeatureCollectionDocument() {
        super();
    }

    /**
     * Creates a new instance of <code>GMLFeatureCollectionDocument</code>.
     * <p> 
     * @param guessSimpleTypes
     *            set to true, if simple types should be "guessed" during parsing
     */
    public GMLFeatureCollectionDocument( boolean guessSimpleTypes ) {
        super( guessSimpleTypes );
    }

    /**
     * Returns the object representation of the underlying feature collection document.
     * 
     * @return object representation of the underlying feature collection document.
     * @throws XMLParsingException
     */
    public FeatureCollection parse()
                            throws XMLParsingException {
        FeatureCollection fc = parse( this.getRootElement() );
        resolveXLinkReferences();
        addXLinkedMembers( fc );
        return fc;
    }

    /**
     * Ugly hack that adds the "xlinked" feature members to the feature collection.
     * 
     * TODO remove this
     * 
     * @param fc
     * @throws XMLParsingException
     */
    private void addXLinkedMembers( FeatureCollection fc )
                            throws XMLParsingException {
        Iterator<String> iter = this.xlinkedMembers.iterator();
        while ( iter.hasNext() ) {
            String fid = iter.next();
            Feature feature = this.featureMap.get( fid );
            if ( feature == null ) {
                String msg = Messages.format( "ERROR_XLINK_NOT_RESOLVABLE", fid );
                throw new XMLParsingException( msg );
            }
            fc.add( feature );
        }
    }

    /**
     * Returns the object representation for the given feature collection element.
     * 
     * @return object representation for the given feature collection element.
     * @throws XMLParsingException
     */
    private FeatureCollection parse( Element element )
                            throws XMLParsingException {

        String fcId = parseFeatureId( element );
        // generate id if necessary (use feature type name + a unique number as id)
        if ( "".equals( fcId ) ) {
            fcId = element.getLocalName();
            fcId += IDGenerator.getInstance().generateUniqueID();
        }

        ElementList el = XMLTools.getChildElements( element );
        List<Feature> list = new ArrayList<Feature>( el.getLength() );

        for ( int i = 0; i < el.getLength(); i++ ) {
            Feature feature = null;
            Element propertyElement = el.item( i );
            String propertyName = propertyElement.getNodeName();

            if ( !propertyName.endsWith( "boundedBy" ) && !propertyName.endsWith( "name" )
                 && !propertyName.endsWith( "description" ) ) {
                // the first child of a feature member must always be a feature
                Element featureElement = XMLTools.getChildElements( el.item( i ) ).item( 0 );
                if ( featureElement == null ) {
                    // check if feature content is xlinked
                    // TODO remove this ugly hack
                    Text xlinkHref = (Text) XMLTools.getNode( propertyElement,
                                                              "@xlink:href/text()", nsContext );
                    if ( xlinkHref == null ) {
                        String msg = Messages.format( "ERROR_INVALID_FEATURE_PROPERTY",
                                                      propertyName );
                        throw new XMLParsingException( msg );
                    }
                    String href = xlinkHref.getData();
                    if ( !href.startsWith( "#" ) ) {
                        String msg = Messages.format( "ERROR_EXTERNAL_XLINK_NOT_SUPPORTED", href );
                        throw new XMLParsingException( msg );
                    }
                    String fid = href.substring( 1 );
                    this.xlinkedMembers.add( fid );
                } else {
                    try {
                        feature = parseFeature( featureElement );
                        list.add( feature );
                    } catch ( Exception e ) {
                        throw new XMLParsingException(
                                                       "Error creating feature instance from element '"
                                                                               + featureElement.getLocalName()
                                                                               + "': "
                                                                               + e.getMessage() );
                    }
                }
            }
        }

        Feature[] features = list.toArray( new Feature[list.size()] );
        FeatureCollection fc = FeatureFactory.createFeatureCollection( fcId, features );
        return fc;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GMLFeatureCollectionDocument.java,v $
 Revision 1.13  2006/09/13 23:59:33  mschneider
 Fixed exception chaining.

 Revision 1.12  2006/08/31 15:00:26  mschneider
 Added second constructor that allows to disable the guessing of simple types. Javadoc fixes.

 Revision 1.11  2006/07/25 15:52:52  mschneider
 gml:Id attribute is respected now (if present).

 Revision 1.10  2006/06/04 17:21:52  poth
 useage of deprecated methods replaced

 Revision 1.9  2006/04/06 20:25:27  poth
 *** empty log message ***

 Revision 1.8  2006/04/04 20:39:42  poth
 *** empty log message ***

 Revision 1.7  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.6  2006/03/09 12:55:40  mschneider
 Improved javadoc.

 Revision 1.5  2006/02/05 18:52:35  mschneider
 Added hack to allow xlink featureMember properties.

 Revision 1.4  2006/02/04 22:49:57  mschneider
 Fixed imports.

 Revision 1.3  2006/01/30 16:20:26  mschneider
 Moved resolveXLinkReferences() here.

 Revision 1.2  2006/01/20 18:13:47  mschneider
 Moved parsing functionality from GMLFeatureAdapter here.

 Revision 1.1  2006/01/19 16:18:14  mschneider
 Initial version.

 ********************************************************************** */