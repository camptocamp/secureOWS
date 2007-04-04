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

/**
 * Thrown when a reference to an XML type is encountered that is unknown in the context (i.e. in the
 * XML Schema document).
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 * @since 2.0
 */
public class UndefinedXSDTypeException extends UnresolvableReferenceException {

    private static final long serialVersionUID = -9138320617620027534L;

    /**
     * Constructs an instance of <code>UndefinedTypeException</code> with the specified detail
     * message.
     * 
     * @param msg
     *            the detail message
     */
    public UndefinedXSDTypeException( String msg ) {
        super( msg );
    }

    /**
     * Constructs an instance of <code>UndefinedTypeException</code> with the specified cause.
     * 
     * @param cause
     *            the Throwable that caused this SchemaException
     * 
     */
    public UndefinedXSDTypeException( Throwable cause ) {
        super( cause );
    }

    /**
     * Constructs an instance of <code>UndefinedTypeException</code> with the specified detail
     * message and cause.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the Throwable that caused this UndefinedTypeException
     * 
     */
    public UndefinedXSDTypeException( String msg, Throwable cause ) {
        super( msg, cause );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.8  2006/08/29 19:54:14  poth
footer corrected

Revision 1.7  2006/08/24 06:39:17  poth
File header corrected

Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
