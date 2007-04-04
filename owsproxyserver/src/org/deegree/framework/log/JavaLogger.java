// $Header: /cvsroot/deegree/src/org/deegree/framework/log/JavaLogger.java,v 1.2
// 2004/06/15 14:58:19 tf Exp $
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

package org.deegree.framework.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.deegree.framework.util.StringTools;

/**
 * Log service provided by Java logging API.<BR/>
 * The log environment is fully configurable using a configuration file.
 * The configuration file name is <code>logging.properties</code>. 
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/09/12 12:49:47 $
 * 
 * @see <a
 *      href="http://java.sun.com/j2se/1.4.2/docs/guide/util/logging/overview.html">Java
 *      Logging Overview </a>
 * 
 * @since 2.0
 */
final class JavaLogger extends LoggerService {

    private java.util.logging.Logger log;

    /**
     * 
     *  
     */
    JavaLogger() {
        super();
    }

    /**
     * @see org.deegree.framework.log.ILogger#bindClass(java.lang.String)
     */
    public void bindClass( String string ) {
        log = java.util.logging.Logger.getLogger( string );

        try {
//            java.util.logging.Handler handler = new FileHandler( "%t" + defaultChannelName
//                                                                 + "_%g.log", 100000, 3, true );
            java.util.logging.Handler handler = new ConsoleHandler();
            handler.setFormatter( new java.util.logging.SimpleFormatter() );
            log.addHandler( handler );
        } catch ( SecurityException e ) {
            log.warning( e.getMessage() );
        } 
    }

    /**
     * @see org.deegree.framework.log.ILogger#bindClass(java.lang.Class)
     */
    public void bindClass( Class class1 ) {
        bindClass( class1.getName() );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logDebug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logDebug( String string, Throwable throwable ) {
        if ( throwable != null )
            log.fine( new StringBuffer().append( string ).append( ":" ).append(
                                                                                throwable.getMessage() ).toString() );

        else
            log.fine( string );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logInfo(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logInfo( String string, Throwable throwable ) {
        if ( throwable != null ) {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( string ).append( ':' ).append( throwable.getMessage() );
            log.info(  sb.toString() );

        } else {
            log.info( string );
        }
    }

    /**
     * @see org.deegree.framework.log.ILogger#logWarning(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logWarning( String string, Throwable throwable ) {
        if ( throwable != null ) {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( string ).append( ':' ).append( throwable.getMessage() );
            log.warning(  sb.toString() );

        } else {
            log.warning( string );
        }
    }

    /**
     * @see org.deegree.framework.log.ILogger#logError(java.lang.String, java.lang.Throwable)
     */
    public void logError( String string, Throwable throwable ) {
        if ( throwable != null )
            log.severe( StringTools.concat( 1000, string, ':',
                                            StringTools.stackTraceToString( throwable ) ) );

        else
            log.severe( string );

        sendMail( string, throwable, null );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logDebug(java.lang.String)
     */
    @Override
    public void logDebug( String string ) {
        logDebug( string, null );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logInfo(java.lang.String)
     */
    @Override
    public void logInfo( String string ) {
        logInfo( string, null );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logWarning(java.lang.String)
     */
    @Override
    public void logWarning( String string ) {
        logWarning( string, null );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logError(java.lang.String)
     */
    public void logError( String string ) {
        logError( string, null );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logInfo(java.lang.String, java.lang.Object)
     */
    public void logInfo( String string, Object object ) {
        if ( object != null )
            logInfo( new StringBuffer().append( string ).append( object.toString() ).toString() );

        else
            logInfo( string );
    }

    /**
     * @see org.deegree.framework.log.ILogger#logDebug(java.lang.String, java.lang.Object)
     */
    public void logDebug( String string, Object object ) {
        if ( object != null )
            logDebug( new StringBuffer().append( string ).append( object.toString() ).toString() );

        else
            logDebug( string );
    }

    /**
     * @see org.deegree.framework.log.ILogger#log(int, java.lang.String, java.lang.Throwable)
     */
    public void log( int i, String string, Throwable throwable ) {
        log.log( Level.INFO, string, throwable );
    }

    /**
     * @see org.deegree.framework.log.ILogger#log(int, java.lang.String, java.lang.Object, java.lang.Throwable)
     */
    public void log( int i, String string, Object object, Throwable throwable ) {
        log.log( Level.INFO,
                 new StringBuffer().append( string ).append( ":" ).append( throwable.getMessage() ).toString(),
                 object );
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ( new StringBuffer().append( "Logging Class: " ).append( log.getClass().getName() ).toString() );
    }

    /**
     * @see org.deegree.framework.log.ILogger#getLevel()
     */
    public int getLevel() {
        return this.getInternalLevel( this.log.getLevel() );
    }

    /**
     * @see org.deegree.framework.log.ILogger#setLevel(int)
     */
    public void setLevel( int level ) {
        this.log.setLevel( this.getJavaLogLevel( level ) );
    }

    private Level getJavaLogLevel( int logLevel ) {
        Level javaloglevel;
        switch ( logLevel ) {
        case ILogger.LOG_DEBUG:
            javaloglevel = Level.FINEST;
            break;
        case ILogger.LOG_INFO:
            javaloglevel = Level.INFO;
            break;
        case ILogger.LOG_WARNING:
            javaloglevel = Level.WARNING;
            break;
        case ILogger.LOG_ERROR:
            javaloglevel = Level.SEVERE;
            break;
        default:
            javaloglevel = Level.INFO;
            break;
        }
        return javaloglevel;
    }

    private int getInternalLevel( Level javaLogLevel ) {
        int intloglevel = ILogger.LOG_INFO;
        if ( Level.FINEST.equals( javaLogLevel ) ) {
            intloglevel = ILogger.LOG_DEBUG;
        } else if ( Level.INFO.equals( javaLogLevel ) ) {
            intloglevel = ILogger.LOG_INFO;
        } else if ( Level.WARNING.equals( javaLogLevel ) ) {
            intloglevel = ILogger.LOG_WARNING;
        } else if ( Level.SEVERE.equals( javaLogLevel ) ) {
            intloglevel = ILogger.LOG_ERROR;
        }
        return intloglevel;
    }

    public boolean isDebug() {
        return ( log.getLevel().intValue() == Level.FINEST.intValue() );
    }

}

/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * JavaLogger.java,v $ Revision 1.2 2004/06/15 14:58:19 tf refactored Logger and
 * add new methods
 * 
 * Revision 1.1 2004/05/14 15:26:38 tf initial checkin
 * 
 *  
 ******************************************************************************/
