//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/datatypes/QualifiedName.java,v 1.11 2006/09/27 16:47:15 poth Exp $
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
package org.deegree.datatypes;

import java.net.URI;

import org.deegree.framework.util.StringTools;

/**
 * This class represent a qualified name for something. A name is thought to be built from an
 * optional prefix and/or a local name E.g.: <BR>- deegree - pre:deegree <BR>
 * a name may be located within a namespace assigned to the names prefix (or as default namespace if
 * the name has not prefix).
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/09/27 16:47:15 $
 * 
 * @since 2.0
 */
public class QualifiedName {

    private String prefix = null;
    private String localName = null;
    private URI namespace = null;
    private String s = null;

    /**
     * @param localName
     *            local name/simple (without prefix)
     */
    public QualifiedName( String name ) {
        if ( name.indexOf( ':' ) > -1 ) {
            String[] tmp = StringTools.toArray( name, ":", false );
            prefix = tmp[0];
            this.localName = tmp[1];
        } else {
            this.localName = name;
        }
        buildString();
    }

    /**
     * @param name
     *            complete name including a prefix
     * @param namespace
     *            namespace the name is located within
     */
    public QualifiedName( String name, URI namespace ) {
        if ( name.indexOf( ':' ) > -1 ) {
            String[] tmp = StringTools.toArray( name, ":", false );
            prefix = tmp[0];
            this.localName = tmp[1];
        } else {
            this.localName = name;
        }
        this.namespace = namespace;
        buildString();
    }

    /**
     * @param prefix
     * @param localName
     *            local/simple name (e.g. deegree)
     * @param namespace
     *            namespace the name is located within
     */
    public QualifiedName( String prefix, String localName, URI namespace ) {
        this.prefix = prefix;
        this.localName = localName;
        this.namespace = namespace;
        buildString();
    }

    private void buildString() {
        StringBuffer sb = new StringBuffer( 50 );
        if ( prefix != null && prefix.length() != 0) {
            sb.append( prefix ).append( ':' );
        }
        sb.append( localName );
        s = sb.toString();
    }

    /**
     * returns a string representation of a QualifiedName. prefix and local name are separated by
     * ':'
     * 
     * @return
     */
    public String getAsString() {
        return s;
    }

    /**
     * returns the names prefix
     * 
     * @return
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * returns the local part of the name
     * 
     * @return
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * returns the namespace the name is located within (may be null)
     * 
     * @return
     */
    public URI getNamespace() {
        return namespace;
    }
    
    public boolean isInNamespace (URI ns) {
        return ns.equals(this.namespace);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return string representation of the object
     */    
    public String toString() {
        if ( this.prefix == null || this.prefix.length() == 0 ) {
            return this.s;
        }
        return this.s
            + " (" + this.prefix + "=" + this.namespace + ")";
    }

    /**
     * Returns a hash code value for the object.
     * 
     * @return a hash code value for the object
     */    
    public int hashCode() {
        return ( this.namespace + this.localName ).hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @return true if this object is the same as the obj argument; false otherwise
     */    
    public boolean equals( Object o ) {
        // return false in the case that the object is null
        // or isn't an instance of QualifiedName
        if ( o == null || !( o instanceof QualifiedName ) ) {
            return false;
        }

        QualifiedName other = (QualifiedName) o;        
        if ( localName.equals( other.getLocalName() ) ) {            
            if ( ( namespace != null && namespace.equals( other.getNamespace() ) )
                || ( namespace == null && other.getNamespace() == null ) ) {
                return true;
            }
        }
        return false;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: QualifiedName.java,v $
 * Changes to this class. What the people have been up to: Revision 1.11  2006/09/27 16:47:15  poth
 * Changes to this class. What the people have been up to: footer corrected
 * Changes to this class. What the people have been up to:
 *  Revision 1.10  2006/07/06 17:34:06  mschneider
 *  Added handling for empty string prefices (default namespace).
 * 
 *  Revision 1.9  2006/05/01 20:15:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.8  2006/04/06 20:25:31  poth
 *  *** empty log message ***
 * 
 *  Revision 1.7  2006/03/30 21:20:28  poth
 *  *** empty log message ***
 * 
 *  Revision 1.6  2006/01/05 10:13:49  poth
 *  *** empty log message ***
 * 
 *  Revision 1.5  2005/11/16 13:44:59  mschneider
 *  Merge of wfs development branch.
 * 
 *  Revision 1.3.2.3  2005/11/15 16:55:09  mschneider
 *  Improved javadoc.
 * 
 *  Revision 1.3.2.2  2005/11/09 18:02:29  mschneider
 *  More refactoring.
 * 
 *  Revision 1.3.2.1  2005/10/31 19:13:48  mschneider
 *  Added convenience method 'isInNamespace()'.
 * 
 *  Revision 1.3  2005/09/27 19:53:18  poth
 *  no message
 * 
 *  Revision 1.2  2005/08/29 17:13:07  mschneider
 *  Declared class final.
 * 
 *  Revision 1.1  2005/08/24 16:05:56  mschneider
 *  Renamed GenericName to QualifiedName.
 *  Revision
 * 1.13 2005/08/23 13:38:00 mschneider Improved toString().
 * 
 * Revision 1.12 2005/07/22 16:19:56 poth no message
 * 
 * Revision 1.11 2005/04/25 14:04:24 poth no message
 * 
 * Revision 1.10 2005/04/25 12:20:55 friebe add hash method Revision 1.6 2005/03/01 09:52:49 poth no
 * message
 * 
 * Revision 1.5 2005/02/28 16:14:32 poth no message
 * 
 * Revision 1.4 2005/02/28 14:14:05 poth no message
 * 
 * Revision 1.3 2005/02/28 13:34:57 poth no message
 * 
 * Revision 1.2 2005/01/19 17:22:26 poth no message
 * 
 * Revision 1.1 2005/01/19 16:32:10 poth no message
 * 
 *  
 **************************************************************************************************/