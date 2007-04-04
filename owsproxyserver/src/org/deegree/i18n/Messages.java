//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/i18n/Messages.java,v 1.7 2006/09/23 09:04:04 poth Exp $
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
package org.deegree.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import org.deegree.framework.util.BootLogger;

/**
 * Responsible for the access to messages that are visible to the user.
 * <p>
 * Messages are read from the properties file <code>messages_LANG.properties</code> (LANG is always a
 * lowercased ISO 639 code), so internationalization is supported. If a certain property (or the
 * property file) for the specific default language of the system is not found, the message is taken
 * from <code>messages_en.properties</code>.
 *
 * @see java.util.Locale#getLanguage()
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.7 $, $Date: 2006/09/23 09:04:04 $
 */
public class Messages {

    /* This definition allows Eclipse to display the content of referenced message keys. */
    @SuppressWarnings("unused")
    private static final String BUNDLE_NAME = "org.deegree.i18n.messages_en";

    private static Properties props = new Properties();

    /**
     * Initialization done at class loading time.
     */
    static {
        try {
            // load all messages from default file ("org/deegree/i18n/message_en.properties")
            String fileName = "messages_en.properties";
            InputStream is = Messages.class.getResourceAsStream( fileName );
            if ( is == null ) {
                BootLogger.log( "Error while initializing " + Messages.class.getName() + " : "
                                + " default message file: '" + fileName + " not found." );
            }
            is = Messages.class.getResourceAsStream( fileName );
            props.load( is );
            is.close();

            // override messages using file "/message_en.properties"
            fileName = "/messages_en.properties";
            overrideMessages(fileName);
           
            String lang = Locale.getDefault().getLanguage();
            if (!"".equals (lang) && !"en".equals(lang)) {
                // override messages using file "org/deegree/i18n/message_LANG.properties"                
                fileName = "messages_" + lang + ".properties";
                overrideMessages(fileName);
                // override messages using file "/message_LANG.properties"
                fileName = "/messages_" + lang + ".properties";
                overrideMessages(fileName);
            }
        } catch ( IOException e ) {
            BootLogger.logError( "Error while initializing " + Messages.class.getName() + " : "
                                 + e.getMessage(), e );
        }
    }

    private static void overrideMessages( String propertiesFile ) throws IOException {
        InputStream is = Messages.class.getResourceAsStream( propertiesFile );
        if ( is != null ) {
            // override default messages 
            Properties overrideProps = new Properties();
            overrideProps.load( is );
            is.close();
            Iterator iter = overrideProps.keySet().iterator();
            while ( iter.hasNext() ) {
                String key = (String) iter.next();
                props.put( key, overrideProps.get( key ) );
            }
        }
    }

    /**
     * Returns the message assigned to the passed key. If no message is assigned, an error message
     * will be returned that indicates the missing key. 
     *  
     * @param key
     * @param arguments
     * @return the message assigned to the passed key
     */
    public static String getMessage( String key, Object... arguments ) {
        String s = props.getProperty( key );
        if ( s != null  ) {
            return MessageFormat.format( s, arguments );
        } 
        
        // to avoid NPEs
        return "$Message with key: " + key + " not found$";
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Messages.java,v $
 Revision 1.7  2006/09/23 09:04:04  poth
 System.out removed

 Revision 1.6  2006/09/20 11:34:43  mschneider
 Fixed bug in the overriding strategy.

 Revision 1.5  2006/09/20 10:04:52  mschneider
 Improved message that is displayed when key is not found.

 Revision 1.4  2006/09/20 09:49:17  mschneider
 Added javadoc.

 Revision 1.3  2006/09/19 18:50:36  poth
 *** empty log message ***

 Revision 1.2  2006/09/19 18:50:22  poth
 remove depencies to class instances

 Revision 1.1  2006/09/19 15:10:51  poth
 initial check in

 
 ********************************************************************** */