//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/xml/schema/ComplexTypeDeclaration.java,v 1.8 2006/08/29 19:54:14 poth Exp $
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

import java.util.LinkedHashSet;

import org.deegree.datatypes.QualifiedName;

/**
 * Represents an XML complex type declaration in an {@link XMLSchema}.
 * <p>
 * The following limitations apply:
 * <ul>
 * <li>the type may be defined using 'extension', but must not use 'restriction'</li>
 * <li>the content model must be a sequence</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $, $Date: 2006/08/29 19:54:14 $
 */
public class ComplexTypeDeclaration implements TypeDeclaration {

    private QualifiedName name;

    private TypeReference extensionBaseType;

    private ElementDeclaration[] subElements;
    
    /**
     * Creates a new <code>ComplexTypeDeclaration</code> instance from the given parameters.
     * 
     * @param name
     * @param extensionBaseType
     * @param subElements
     */
    public ComplexTypeDeclaration( QualifiedName name, TypeReference extensionBaseType,
                                  ElementDeclaration[] subElements ) {
        this.name = name;
        this.extensionBaseType = extensionBaseType;
        this.subElements = subElements;
    }

    /**
     * Returns the qualified name of the declared XML type.
     *  
     * @return the qualified name of the declared XML type
     */    
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * Returns a {@link TypeReference} to the XML type that this complex type extends.
     * 
     * @return a TypeReference to the XML type that this complex type extends
     */
    public TypeReference getExtensionBaseType() {
        return this.extensionBaseType;
    }

    /**
     * Returns the {@link ElementDeclaration}s that this {@link ComplexTypeDeclaration}
     * contains, but not the ones that are inherited (from the extended type).
     * 
     * @return the explicit ElementDeclarations in this ComplexTypeDeclaration
     */
    public ElementDeclaration[] getExplicitElements() {
        return this.subElements;
    }

    /**
     * Returns the {@link ElementDeclaration}s in this {@link ComplexTypeDeclaration}
     * contains, this includes the ones that are inherited (from the extended type).
     * 
     * @return the explicit+implicit ElementDeclarations in this ComplexTypeDeclaration
     */
    public ElementDeclaration[] getElements() {
        LinkedHashSet<ElementDeclaration> allElementSet = new LinkedHashSet<ElementDeclaration>();
        addElements( allElementSet );
        return allElementSet.toArray( new ElementDeclaration[allElementSet.size()] );
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */    
    public String toString() {
        return toString( "" );
    }

    /**
     * Returns a string representation of the object (indented for better readablity,
     * as this is a hierarchical structure).
     * 
     * @param indent
     *             current indentation (as a whitespace string)
     * @return an indented string representation of the object
     */    
    public String toString( String indent ) {
        StringBuffer sb = new StringBuffer();
        sb.append( indent );
        sb.append( "- complexType" );
        if ( name != null ) {
            sb.append( " name=\"" );
            sb.append( this.name );
            sb.append( "\"" );
        }
        if ( this.extensionBaseType != null ) {
            sb.append( ", extension base=\"" );
            sb.append( this.extensionBaseType.getName() );
            sb.append( "\"" );
        }
        sb.append( "\n" );
        for (int i = 0; i < subElements.length; i++) {
            sb.append( subElements[i].toString( indent
                + "  " ) );
        }
        return sb.toString();
    }

    /**
     * Recursively collects all <code>ElementDeclaration</code>s that this
     * <code>ComplexType</code> has.
     * <p>
     * Respects order and scope (overwriting) of <code>ElementDeclaration</code>s.
     * 
     * @param elementSet
     *            the inherited (and own) elements are added to this LinkedHashSet
     */
    private void addElements( LinkedHashSet<ElementDeclaration> elementSet ) {
        if ( this.extensionBaseType != null
            && this.extensionBaseType.getTypeDeclaration() != null ) {
            ( (ComplexTypeDeclaration) this.extensionBaseType.getTypeDeclaration() )
                .addElements( elementSet );
        }
        for (int i = 0; i < subElements.length; i++) {
            elementSet.add( this.subElements[i] );
        }
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ComplexTypeDeclaration.java,v $
Revision 1.8  2006/08/29 19:54:14  poth
footer corrected

Revision 1.7  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */