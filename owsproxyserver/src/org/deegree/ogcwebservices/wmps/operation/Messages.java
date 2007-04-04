//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/operation/Messages.java,v 1.3 2006/08/24 06:42:17 poth Exp $
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
package org.deegree.ogcwebservices.wmps.operation;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages class provides the functionality to read response, exception messages from a properties
 * file.
 * 
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/08/24 06:42:17 $
 * 
 * @since 2.0
 */
public class Messages {

    private static final String BUNDLE_NAME = "org.deegree.ogcwebservices.wmps.operation.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

    /**
     * Create a new Messages instance.
     */
    private Messages() {
    }

    /**
     * Get Message
     * 
     * @param key
     * @return String
     */
    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString( key );
        } catch ( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }

    /**
     * Get Message along with the appropriate replacement for the object.
     * 
     * @param key
     * @param arg0
     * @return String
     */
    public static String format( String key, Object arg0 ) {
        return format( key, new Object[] { arg0 } );
    }

    /**
     * Get Message along with the appropriate replacement for the objects.
     * 
     * @param key
     * @param arg0
     * @param arg1
     * @return String
     */
    public static String format( String key, Object arg0, Object arg1 ) {
        return format( key, new Object[] { arg0, arg1 } );
    }

    /**
     * Get Message along with the appropriate replacement for the objects.
     * 
     * @param key
     * @param arg0
     * @param arg1
     * @param arg2
     * @return String
     */
    public static String format( String key, Object arg0, Object arg1, Object arg2 ) {
        return format( key, new Object[] { arg0, arg1, arg2 } );
    }

    /**
     * Get Message along with the appropriate replacement for the object(s).
     * 
     * @param key
     * @param arguments
     * @return String
     */
    public static String format( String key, Object[] arguments ) {
        return MessageFormat.format( getString( key ), arguments );
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $$ Log: $$
 **************************************************************************************************/
