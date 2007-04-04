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
package org.deegree.framework.xml.schema;

import org.deegree.framework.xml.XMLParsingException;

/**
 * This exception is thrown when a syntactic or semantic error has been encountered during the
 * parsing or the processing of an XML Schema document.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class XMLSchemaException extends XMLParsingException {

    private static final long serialVersionUID = 3787417943058189973L;

    /**
     * Constructs an instance of <code>SchemaException</code> with the specified detail message.
     * 
     * @param msg
     *            the detail message
     */
    public XMLSchemaException( String msg ) {
        super( msg );
    }

    /**
     * Constructs an instance of <code>SchemaException</code> with the specified cause.
     * 
     * @param cause
     *            the Throwable that caused this SchemaException
     * 
     */
    public XMLSchemaException( Throwable cause ) {
        super( cause );
    }

    /**
     * Constructs an instance of <code>SchemaException</code> with the specified detail message
     * and cause.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the Throwable that caused this SchemaException
     */
    public XMLSchemaException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.9  2006/08/29 19:54:14  poth
footer corrected

Revision 1.8  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */