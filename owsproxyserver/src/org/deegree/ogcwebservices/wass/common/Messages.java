//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/Messages.java,v 1.2 2006/08/24 06:42:16 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wass.common;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A <code>Messages</code> class which loads string resources from the properties-file and formats
 * them according to MessageFormat.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class Messages {
    // The name of the resource file
    private static final String BUNDLE_NAME = "org.deegree.ogcwebservices.wass.common.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

    private Messages() {
        // private constructors can not be instantiated.
    }

    /**
     * @param key
     *            to look for in the resource bundle
     * @return the message identified by key
     */
    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString( key );
        } catch ( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }

    /**
     * @param key
     *            to look for in the resource bundle
     * @param arg0
     *            the variable to put inside the String at key defined with "{0}"
     * @return the message with variable arg0 inserted.
     */
    public static String format( String key, Object arg0 ) {
        return format( key, new Object[] { arg0 } );
    }

    /**
     * @param key
     *            to look for in the resource bundle
     * @param arg0
     *            the first variable to put inside the String at key defined with "{0}"
     * @param arg1
     *            the second variable to put inside the String at key defined with "{1}"
     * @return the message with variable arg0, arg1 inserted.
     */
    public static String format( String key, Object arg0, Object arg1 ) {
        return format( key, new Object[] { arg0, arg1 } );
    }

    /**
     * @param key
     *            to look for in the resource bundle
     * @param arg0
     *            the first variable to put inside the String at key defined with "{0}"
     * @param arg1
     *            the second variable to put inside the String at key defined with "{1}"
     * @param arg2
     *            the third variable to put inside the String at key defined with "{2}"
     * @return the message with variable arg0, arg1, arg2 inserted.
     */
    public static String format( String key, Object arg0, Object arg1, Object arg2 ) {
        return format( key, new Object[] { arg0, arg1, arg2 } );
    }

    /**
     * @param key
     *            to look for in the resource bundle
     * @param arguments
     *            the different variables to insert into the result string.
     * @return the message with variables arguments inserted.
     */
    public static String format( String key, Object[] arguments ) {
        return MessageFormat.format( getString( key ), arguments );
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Messages.java,v $
 * Changes to this class. What the people have been up to: Revision 1.2  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1  2006/06/19 13:16:41  bezema
 * Changes to this class. What the people have been up to: The localized messages base class added
 * Changes to this class. What the people have been up to:
 **************************************************************************************************/

