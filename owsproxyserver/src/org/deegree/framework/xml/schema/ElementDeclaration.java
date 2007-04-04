//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/xml/schema/ElementDeclaration.java,v 1.9 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.framework.xml.schema;

import org.deegree.datatypes.QualifiedName;

/**
 * Represents an XML element declaration in an {@link XMLSchema}.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.9 $, $Date: 2006/08/29 19:54:14 $
 */
public class ElementDeclaration {

    private QualifiedName name;
    
    private boolean isAbstract;

    private TypeReference type;

    private int minOccurs;

    private int maxOccurs;

    private ElementReference substitutionGroup;
    
    /**
     * Creates a new <code>ElementDeclaration</code> instance from the given parameters.
     * 
     * @param name
     * @param isAbstract
     * @param type
     * @param minOccurs
     * @param maxOccurs
     * @param substitutionGroup
     */
    public ElementDeclaration( QualifiedName name, boolean isAbstract, TypeReference type, int minOccurs, int maxOccurs,
                              QualifiedName substitutionGroup ) {
        this.name = name;
        this.isAbstract = isAbstract;
        this.type = type;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        if ( substitutionGroup != null ) {
            this.substitutionGroup = new ElementReference( substitutionGroup );
        }
    }

    /**
     * Returns the qualified name of the declared XML element.
     * 
     * @return the qualified name of the declared XML element
     */
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * Returns whether the element is declared abstract.
     * 
     * @return true, if the element is abstract, false otherwise
     */
    public boolean isAbstract() {
        return this.isAbstract;
    }    

    /**
     * Returns a {@link TypeReference} that describes the content of the element.
     * 
     * @return a TypeReference that describes the content of the element
     */
    public TypeReference getType() {
        return this.type;
    }

    /**
     * Returns the minimum number of occurences of the element.
     * 
     * @return the minimum number of occurences of the element, -1 if it is unconstrained
     */
    public int getMinOccurs() {
        return this.minOccurs;
    }

    /**
     * Returns the maximum number of occurences of the element.
     * 
     * @return the maximum number of occurences of the element, -1 if it is unconstrained
     */    
    public int getMaxOccurs() {
        return this.maxOccurs;
    }    

    /**
     * Returns an {@link ElementReference} which the element may be substituted for. 
     * 
     * @return an ElementReference which the element may be substituted for
     */    
    public ElementReference getSubstitutionGroup() {
        return this.substitutionGroup;
    }

    /**
     * Returns whether this element is substitutable for the given element name.
     * <p>
     * This is true if the substitutionGroup equals the given element name, or if an element that
     * this element is substitutable for may be substituted for the given element name.
     * 
     * @param substitutionName
     * @return true, if this element declaration is a valid substiution for elements with the
     *             given name
     */
    public boolean isSubstitutionFor( QualifiedName substitutionName ) {
        if ( this.name.equals( substitutionName ) ) {
            return true;
        }
        if ( this.substitutionGroup == null ) {
            return false;
        }
        if ( this.substitutionGroup.getElementDeclaration() == null ) {
            return this.substitutionGroup.getName().equals( substitutionName );
        }
        return this.substitutionGroup.getElementDeclaration().isSubstitutionFor( substitutionName );
    }

    /**
     * Returns a string representation of the object (indented for better readablity,
     * as this is part of a hierarchical structure).
     * 
     * @param indent
     *             current indentation (as a whitespace string)
     * @return an indented string representation of the object
     */    
    public String toString( String indent ) {
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "- element" );
        if ( this.name != null ) {
            sb.append( " name=\"" );
            sb.append( this.name.getLocalName() );
            sb.append( "\"" );
        }
        if ( !this.type.isAnonymous() ) {
            sb.append( " type=\"" );
            sb.append( this.type.getName() );
            sb.append( "\"" );
        } else {
            sb.append( " anonymous type" );
        }
        if ( this.substitutionGroup != null ) {
            sb.append( " substitutionGroup=\"" );
            sb.append( this.substitutionGroup.getName() );
            sb.append( "\"" );
        }
        sb.append( "\n" );
        if ( this.type.isAnonymous() ) {
            sb.append( this.type.getTypeDeclaration().toString( indent
                + "  " ) );
        }
        return sb.toString();
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ElementDeclaration.java,v $
Revision 1.9  2006/08/29 19:54:14  poth
footer corrected

Revision 1.8  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.


Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */