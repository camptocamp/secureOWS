//$Header$
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
package org.deegree.framework.xml;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.deegree.ogcbase.CommonNamespaces;

/**
 * Implementation of Jaxen's (http://jaxen.codehaus.org) <code>NamespaceContext</code> interface.
 * <p>
 * NOTE: This should be used everywhere inside deegree, don't use
 * <code>org.jaxen.SimpleNamespaceContext</code> -- this prevents unnecessary binding to Jaxen.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class NamespaceContext implements org.jaxen.NamespaceContext {

    // keys: prefices (String), values: namespaces (URI)
    private Map<String,URI> namespaceMap = new HashMap<String,URI>();

    /**
     * Creates a new instance of <code>NamespaceContext</code> with only the prefix 'xmlns:' being
     * bound.
     */
    public NamespaceContext() {
        this.namespaceMap.put( CommonNamespaces.XMLNS_PREFIX, CommonNamespaces.XMLNS );
    }

    public Map getNamespaceMap() {
        return this.namespaceMap;
    }

    public void addNamespace( String prefix, URI namespace ) {
        this.namespaceMap.put( prefix, namespace );
    }

    public String translateNamespacePrefixToUri( String prefix ) {
        URI namespaceURI = this.namespaceMap.get( prefix );
        return namespaceURI == null ? null : namespaceURI.toString();
    }

    public URI getURI( String prefix ) {
        return this.namespaceMap.get( prefix );
    }    
    
    public String toString() {
        return namespaceMap.toString();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.12  2006/09/26 12:44:00  poth
use of generics added

Revision 1.11  2006/08/29 19:54:14  poth
footer corrected

Revision 1.10  2006/08/24 06:39:17  poth
File header corrected

Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
