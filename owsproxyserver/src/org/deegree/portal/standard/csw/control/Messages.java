package org.deegree.portal.standard.csw.control;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "org.deegree.portal.standard.csw.control.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

    private Messages() {
    }

    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString( key );
        } catch ( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Messages.java,v $
 Revision 1.1  2006/06/30 12:47:49  mays
 started to externalize messages

 ********************************************************************** */
