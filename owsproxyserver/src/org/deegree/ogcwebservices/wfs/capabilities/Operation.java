//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/Operation.java,v 1.8 2006/11/16 08:53:21 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.capabilities;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import org.deegree.i18n.Messages;

/**
 * Represents an element of type 'wfs:OperationType'.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @version $Revision: 1.8 $ $Date: 2006/11/16 08:53:21 $
 */
public class Operation {

    public static final String INSERT = "Insert";

    public static final String UPDATE = "Update";

    public static final String DELETE = "Delete";

    public static final String QUERY = "Query";

    public static final String LOCK = "Lock";

    public static final String GET_GML_OBJECT = "GetGMLObject";

    private static final Set<String> VALID_OPERATIONS = new HashSet<String>();

    static {
        VALID_OPERATIONS.add( INSERT );
        VALID_OPERATIONS.add( UPDATE );
        VALID_OPERATIONS.add( DELETE );
        VALID_OPERATIONS.add( QUERY );
        VALID_OPERATIONS.add( LOCK );
        VALID_OPERATIONS.add( GET_GML_OBJECT );
    }

    private String operation;

    /**
     * Returns the type of the operation as a <code>String</code>.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Constructs a new OperationType.
     * 
     * @param operation
     * @throws InvalidParameterException
     */
    public Operation( String operation ) throws InvalidParameterException {
        if ( VALID_OPERATIONS.contains( operation ) ) {
            this.operation = operation;
        } else {
            String msg = Messages.getMessage( "WFS_INVALID_OPERATION_TYPE", operation );
            throw new InvalidParameterException( msg );
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Operation.java,v $
 Revision 1.8  2006/11/16 08:53:21  mschneider
 Merged messages from org.deegree.ogcwebservices.wfs and its subpackages.

 Revision 1.7  2006/05/16 16:23:17  mschneider
 Added type safety (Generics). Fixed header + footer.

 ********************************************************************** */