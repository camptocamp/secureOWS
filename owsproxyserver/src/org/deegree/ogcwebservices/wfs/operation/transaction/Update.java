//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/Update.java,v 1.3 2006/10/12 16:24:00 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.PropertyPath;
import org.w3c.dom.Node;

/**
 * Represents an <code>Update</code> operation as a part of a {@link Transaction} request. 
 * <p> 
 * WFS Specification OBC 04-094 (#12.2.5 Pg.68)
 * <p>
 * The <code>Update</code> element describes one update operation that is to be applied to a
 * <code>Feature</code> or a set of <code>Feature</code>s of a single <code>FeatureType</code>.
 * <p>
 * Multiple <code>Update</code> operations can be contained in a single <code>Transaction</code>
 * request. An <code>Update</code> element contains one or more <b>Property</b> elements that
 * specify the name and replacement value for a property that belongs to the
 * <code>FeatureType</code> specified using the <b>mandatory typeName</b> attribute.
 * <p>
 * Additionally, a deegree specific addition to this specification is supported:<br>
 * Instead of a number of <code>Properties</code>, it is also possible to specify a root feature that
 * will replace the feature that is matched by the filter. In this case, the filter must match exactly
 * one (or zero) feature instances.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.3 $, $Date: 2006/10/12 16:24:00 $
 */
public class Update extends TransactionOperation {

    private QualifiedName typeName;

    private Feature replacementFeature;

    private Map<PropertyPath, Node> rawProperties;

    private Map<PropertyPath, Object> parsedProperties;

    private Filter filter;

    /**
     * Creates a new <code>Update</code> instance.
     * 
     * @param handle
     *            optional identifier for the operation (for error messsages)
     * @param typeName
     *            the name of the targeted feature type
     * @param properties
     *            property names and their replacement values (as Nodes)
     * @param filter
     *            selects the feature instances to be updated
     */
    public Update( String handle, QualifiedName typeName,
                  Map<PropertyPath, Node> properties, Filter filter ) {
        super( handle );
        this.typeName = typeName;
        this.rawProperties = properties;
        this.filter = filter;
    }

    /**
     * Creates a new <code>Update</code> instance.
     * 
     * @param handle
     *            optional identifier for the operation (for error messsages)
     * @param typeName
     *            the name of the targeted feature type
     * @param replacementFeature
     *            property names and their replacement values
     * @param filter
     *            selects the (single) feature instance to be replaced
     */
    public Update( String handle, QualifiedName typeName, Feature replacementFeature, Filter filter ) {
        super( handle );
        this.typeName = typeName;
        this.replacementFeature = replacementFeature;
        this.filter = filter;
    }

    /**
     * Returns the name of the targeted feature type.
     * 
     * @return the name of the targeted feature type.
     */
    public QualifiedName getTypeName() {
        return this.typeName;
    }

    /**
     * Returns the filter that selects the feature instances to be updated.
     * 
     * @return the filter that selects the feature instances to be updated.
     */
    public Filter getFilter() {
        return this.filter;
    }

    /**
     * Returns the feature that will replace the matched feature instance. If the returned value is
     * null, this is a "standard" update operation that updates a number of flat properties instead. 
     * 
     * @return the feature that will replace the (single) matched feature instance.
     */
    public Feature getFeature() {
        return this.replacementFeature;
    }

    /**
     * Return the properties and their replacement values that are targeted by this update operation.
     * 
     * @return the properties and their replacement values (as Nodes).
     */
    public Map<PropertyPath, Node> getRawProperties() {
        return this.rawProperties;
    }

    /**
     * Return the properties and their replacement values that are targeted by this update operation.
     * 
     * @return the properties and their replacement values.
     */
    public Map<PropertyPath, Object> getReplacementProperties() {
        if ( this.parsedProperties == null ) {
            throw new RuntimeException( "Internal error: Properties have not been parsed yet." );
        }
        return this.parsedProperties;
    }

    /**
     * Sets the normalized property paths and their replacement values (parsed).
     * 
     * @param parsedProperties the normalized property paths and their replacement values (parsed).
     */
    public void setParsedProperties( Map<PropertyPath, Object> parsedProperties ) {
        this.parsedProperties = parsedProperties;
    }

    /**
     * Returns the names of the feature types that are affected by the operation.
     * 
     * @return the names of the affected feature types.
     */
    @Override
    public List<QualifiedName> getAffectedFeatureTypes() {
        List<QualifiedName> featureTypes = new ArrayList<QualifiedName>( 1 );
        featureTypes.add( this.typeName );
        return featureTypes;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Update.java,v $
 Revision 1.3  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.2  2006/05/23 16:11:57  mschneider
 Added getter / setter for parsed properties.

 Revision 1.1  2006/05/16 16:25:30  mschneider
 Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.

 ********************************************************************** */