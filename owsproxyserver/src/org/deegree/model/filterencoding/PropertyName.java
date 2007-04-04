//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/filterencoding/PropertyName.java,v 1.23 2006/11/29 11:01:47 mschneider Exp $
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

package org.deegree.model.filterencoding;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Encapsulates the information of a PropertyName element.
 * 
 * @author Markus Schneider
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.23 $, $Date: 2006/11/29 11:01:47 $
 */
public class PropertyName extends Expression {

    private static final ILogger LOG = LoggerFactory.getLogger( PropertyName.class );

    /** the PropertyName's value (as an XPATH expression). */
    private PropertyPath propertyPath;

    /**
     * Creates a new instance of <code>PropertyName</code>.
     * 
     * @deprecated use #PropertyName(QualifiedName) instead
     */
    public PropertyName( String value ) {
        this( new QualifiedName( value ) );
    }

    /**
     * Creates a new instance of <code>PropertyName</code>.
     * 
     * @param elementName
     */
    public PropertyName( QualifiedName elementName ) {
        this( PropertyPathFactory.createPropertyPath( elementName ) );
    }

    /**
     * Creates a new instance of <code>PropertyName</code>.
     * 
     * @param value
     */
    public PropertyName( PropertyPath value ) {
        id = ExpressionDefines.PROPERTYNAME;
        setValue( value );
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object is built.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Expression buildFromDOM( Element element )
                            throws FilterConstructionException {
        // check if root element's name equals 'PropertyName'
        if ( !element.getLocalName().toLowerCase().equals( "propertyname" ) ) {
            throw new FilterConstructionException( "Name of element does not equal "
                                                   + "'PropertyName'!" );
        }
        PropertyPath propertyPath;
        try {
            Text node = (Text) XMLTools.getRequiredNode( element, "text()",
                                                         CommonNamespaces.getNamespaceContext() );
            propertyPath = OGCDocument.parsePropertyPath(node);
        } catch ( XMLParsingException e ) {
            throw new FilterConstructionException( e.getMessage() );
        }
        return new PropertyName( propertyPath );
    }

    /**
     * Returns the PropertyName's value.
     */
    public PropertyPath getValue() {
        return this.propertyPath;
    }

    /**
     * @see org.deegree.model.filterencoding.PropertyName#getValue()
     */
    public void setValue( PropertyPath value ) {
        this.propertyPath = value;
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer( 200 );
        sb.append( "<ogc:PropertyName" );

        // TODO use methods from XMLTools
        Map namespaceMap = this.propertyPath.getNamespaceContext().getNamespaceMap();
        Iterator prefixIter = namespaceMap.keySet().iterator();
        while ( prefixIter.hasNext() ) {
            String prefix = (String) prefixIter.next();
            if ( !CommonNamespaces.XMLNS_PREFIX.equals( prefix ) ) {
                URI namespace = (URI) namespaceMap.get( prefix );
                sb.append( " xmlns:" );
                sb.append( prefix );
                sb.append( "=" );
                sb.append( "\"" );
                sb.append( namespace );
                sb.append( "\"" );
            }
        }
        sb.append( ">" ).append( propertyPath ).append( "</ogc:PropertyName>" );
        return sb;
    }

    /**
     * Returns the <tt>PropertyName</tt>'s value (to be used in the evaluation of a complexer
     * <tt>Expression</tt>). If the value is a geometry, an instance of <tt>Geometry</tt> is
     * returned, if it appears to be numerical, a <tt>Double</tt>, else a <tt>String</tt>.
     * <p>
     * TODO: Improve datatype handling.
     * <p>
     * 
     * @param feature
     *            that determines the value of this <tt>PropertyName</tt>
     * @return the resulting value
     * @throws FilterEvaluationException
     *             if the <Feature>has no <tt>Property</tt> with a matching name
     */
    public Object evaluate( Feature feature )
                            throws FilterEvaluationException {

        FeatureProperty property = null;
        try {
            property = feature.getDefaultProperty( this.propertyPath );
        } catch ( PropertyPathResolvingException e ) {
            e.printStackTrace();
            throw new FilterEvaluationException( e.getMessage() );
        }
        FeatureType ft = feature.getFeatureType();
        if ( property == null
             && ft.getProperty( this.propertyPath.getStep( 0 ).getPropertyName() ) == null ) {
            throw new FilterEvaluationException( "Feature '" + feature.getFeatureType().getName()
                                                 + "' has no property identified by '"
                                                 + propertyPath + "'!" );
        }

        if ( property == null || property.getValue() == null ) {
            return null;
        }
        Object object = property.getValue();
        if ( object instanceof Number || object instanceof Geometry ) {
            return object;
        }
        return object.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @return <code>true</code> if this object is the same as the obj argument;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof PropertyName ) ) {
            return false;
        }
        return propertyPath.equals( ( (PropertyName) other ).getValue() );
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override    
    public String toString () {
        return this.propertyPath.getAsString();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: PropertyName.java,v $
 Revision 1.23  2006/11/29 11:01:47  mschneider
 Added #toString().

 Revision 1.22  2006/08/30 18:09:16  mschneider
 Fixed #evalute(). Returns geometries as objects now.

 Revision 1.21  2006/07/26 16:11:50  mschneider
 Removed obsessive logging.

 Revision 1.20  2006/07/04 19:10:49  poth
 comments corrected - code formatation

 ********************************************************************** */