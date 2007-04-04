package org.deegree.ogcwebservices.wass.exceptions;

import java.security.GeneralSecurityException;

/**
 * A <code>DoServiceException</code> class that can be used to tell the client she has not the
 * right credentials to access the requested service.
 * 
 * @author <a href="mailto:bezema@lat-lon.de>Rutger Bezema</a>
 * 
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/06/19 12:47:09 $
 * 
 * @since 2.0
 */
public class DoServiceException extends GeneralSecurityException {

    private static final long serialVersionUID = -6545217181758230675L;

    /**
     * @param message
     * @param cause
     */
    public DoServiceException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * @param msg
     */
    public DoServiceException( String msg ) {
        super( msg );
    }

    /**
     * @param cause
     */
    public DoServiceException( Throwable cause ) {
        super( cause );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DoServiceException.java,v $
 * Changes to this class. What the people have been up to: Revision 1.2  2006/06/19 12:47:09  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to: Changes
 * to this class. What the people have been up to: Revision 1.1 2006/05/30 15:11:28 bezema Changes
 * to this class. What the people have been up to: Working on the postclient from apachecommons to
 * place a request to the services behind the wss proxy Changes to this class. What the people have
 * been up to:
 * 
 **************************************************************************************************/
