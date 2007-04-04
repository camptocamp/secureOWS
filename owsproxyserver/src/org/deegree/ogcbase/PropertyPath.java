//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/PropertyPath.java,v 1.11 2006/08/02 12:05:47 poth Exp $
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
package org.deegree.ogcbase;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.NamespaceContext;

/**
 * Represents a subset of the XPath expression language as described in section 7.4.2 of the Web
 * Feature Implementation Specification 1.1.0 (but is used by other OGC specifications as well).
 * <p>
 * This specification does not require a WFS implementation to support the full XPath language. In
 * order to keep the implementation entry cost as low as possible, this specification mandates that
 * a WFS implementation <b>must</b> support the following subset of the XPath language:
 * <ol>
 * <li>A WFS implementation <b>must</b> support <i>abbreviated relative location</i> paths.</li>
 * <li>Relative location paths are composed of one or more <i>steps</i> separated by the path
 * separator '/'.</li>
 * <li>The first step of a relative location path <b>may</b> correspond to the root element of the
 * feature property being referenced <b>or</b> to the root element of the feature type with the
 * next step corresponding to the root element of the feature property being referenced</li>
 * <li>Each subsequent step in the path <b>must</b> be composed of the abbreviated form of the
 * <i>child::</i> axis specifier and the name of the feature property encoded as the principal node
 * type of <i>element</i>. The abbreviated form of the <i>child::</i> axis specifier is to simply
 * omit the specifier from the location step.</li>
 * <li>Each step in the path may optionally contain a predicate composed of the predicate
 * delimiters '[' and ']' and a number indicating which child of the context node is to be selected.
 * This allows feature properties that may be repeated to be specifically referenced.</li>
 * <li>The final step in a path may optionally be composed of the abbreviated form of the
 * <i>attribute::</i> axis specifier, '@', and the name of a feature property encoded as the
 * principal node type of <i>attribute::</i>.</li>
 * </ol>
 * <p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/08/02 12:05:47 $
 * 
 * @since 2.0
 * 
 * @see PropertyPathStep
 */
public class PropertyPath {

    private List<PropertyPathStep> steps;
   
    /**
     * Creates a new instance of <code>PropertyPath</code> with the specified steps.
     * 
     * @param steps
     *            property path steps, may not be null
     */
    public PropertyPath( List<PropertyPathStep> steps ) {
        if (steps.size() < 1) {
            throw new IllegalArgumentException ("PropertyPath must contain at least one step.");
        }
        this.steps = steps;
    }

    /**
     * Returns the namespace bindings for the prefices that are used by this property path.
     * 
     * @return the namespace bindings
     */
    public NamespaceContext getNamespaceContext() {
        NamespaceContext nsContext = new NamespaceContext();
        for (PropertyPathStep step : steps) {
            QualifiedName elementName = step.getPropertyName();
            if ( elementName.getPrefix() != null && elementName.getNamespace() != null ) {
                nsContext.addNamespace( elementName.getPrefix(), elementName.getNamespace() );
            }
        }        
        return nsContext;
    }

    /**
     * Returns the number of steps.
     * 
     * @return the number of steps.
     */
    public int getSteps() {
        return this.steps.size();
    }
    
    /**
     * returns the string representation of a SortProperty
     * @return string representation of a PropertyPath
     */
    public String getAsString() {        
        StringBuffer sb = new StringBuffer( 500 );
        for ( int i = 0; i < steps.size(); i++ ) {
            sb.append( steps.get(i).getPropertyName().getAsString() );
            if ( i < steps.size()-1 ) {
                sb.append( '/' );
            }
        }
        return sb.toString();
    }

    /**
     * Returns the <code>PropertyPathStep</code> at the given index.
     * 
     * @return the <code>PropertyPathStep</code> at the given index.
     */    
    public PropertyPathStep getStep( int i ) {
        return this.steps.get( i );
    }

    /**
     * Returns all steps of the <code>PropertyPath</code>.
     * 
     * @return all steps of the <code>PropertyPath</code>.
     */
    public List<PropertyPathStep> getAllSteps() {
        return this.steps;
    }    
    
    /**
     * Adds the given <code>PropertyPathStep</code> to the end of the path.
     * 
     * @param last <code>PropertyPathStep</code> to add
     */
    public void append (PropertyPathStep last) {
        this.steps.add( last );
    }    
    
    /**
     * Adds the given <code>PropertyPathStep</code> to the beginning of the path.
     * 
     * @param first <code>PropertyPathStep</code> to add
     */
    public void prepend (PropertyPathStep first) {
        this.steps.add (0, first);
    }    
    
    /**
     * Returns a hash code value for the object.
     * 
     * @return a hash code value for the object
     */
    public int hashCode() {
        int hashCode = 0;
        for (PropertyPathStep step : steps) {
            hashCode += step.hashCode();
        }
        return hashCode;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @return true if this object is the same as the obj argument; false otherwise
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof PropertyPath ) ) {
            return false;
        }
        PropertyPath that = (PropertyPath) obj;
        if (this.getSteps() != that.getSteps()) {
            return false;
        }
        for (int i = 0; i < this.getSteps(); i++) {
            if (!this.getStep (i).equals (that.getStep(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return string representation of the object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getSteps(); i++) {
            sb.append( getStep(i) );
            if ( i != getSteps() - 1 ) {
                sb.append( "/" );
            }
        }
        return sb.toString();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PropertyPath.java,v $
Revision 1.11  2006/08/02 12:05:47  poth
methdod getAsString added

Revision 1.10  2006/06/01 15:19:38  mschneider
Fixed footer.

Revision 1.9  2006/04/06 20:25:22  poth
*** empty log message ***

Revision 1.8  2006/04/04 20:39:40  poth
*** empty log message ***

Revision 1.7  2006/04/04 10:34:10  mschneider
Added handling of attributes to PropertyPaths.

Revision 1.6  2006/03/30 21:20:24  poth
*** empty log message ***

Revision 1.5  2006/03/29 14:56:31  mschneider
Fixed header.

********************************************************************** */