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

import java.util.Properties;

/**
 * This interface specifies the log worker services. *  * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A> *  * @author last edited by: $Author: bezema $ *  * @version 2.0, $Revision: 1.8 $, $Date: 2006/09/12 12:49:47 $ *   * @since 2.0
 */

public interface ILogger {
    
    /**
     * 
     * @param props
     */
    void init(Properties props);

    /**
     * 
     * @param name
     */
    void bindClass(String name);    
    
    /**
     * 
     * @param name
     */
    void bindClass(Class name);
    
    /**
     * 
     * @param message
     */
    void logDebug(String message);
    
    /**
     * 
     * @param message
     */
    void logInfo(String message);
    
    /**
     * 
     * @param message
     */
    void logWarning(String message);
    
   /**
    * Log error message.
    *
    * @param message  the log message
    */
    void logError(String message);

    /**
     * 
     * @param message
     * @param e
     */
    void logDebug(String message, Throwable e);
    
    /**
     * 
     * @param message
     * @param e
     */
    void logInfo(String message, Throwable e);
    
    /**
     * 
     * @param message
     * @param e
     */
    void logWarning(String message, Throwable e);
    
    /**
     * Log error with exception
     *
     * @param message  the log message
     * @param e        the exception to be logged
     */
    void logError(String message, Throwable e);
    
    /**
     * 
     * @param message
     * @param tracableObject
     */
    void logDebug(String message, Object tracableObject);
    
    /**
     * 
     * @param message
     * @param tracableObject
     */
    void logInfo(String message, Object tracableObject);
    
    /**
     * 
     * @param priority
     * @param message
     * @param ex
     */
    void log(int priority, String message, Throwable ex);
    
    /**
     * 
     * @param priority
     * @param message
     * @param source
     * @param ex
     */
    void log(int priority, String message, Object source, Throwable ex);
    
    /**
     * Log a method entry.
     *
     */
    void entering();
    
    /**
     * Log a method entry.
     * 
     * @param sourceClass
     * @param sourceMethod
     */
    void entering(String sourceClass, String sourceMethod); 
    
    /**
     * Log a method entry with parameters.
     * 
     * @param sourceClass
     * @param sourceMethod
     * @param arguments the parameters to be given
     */
    void entering(String sourceClass, String sourceMethod, Object[] arguments);
    
    /**
     * Log a method return.
     * 
     * @param sourceClass
     * @param sourceMethod
     */
    void exiting();
    
    /**
     * Log a method return.
     * 
     * @param sourceClass
     * @param sourceMethod
     */
    void exiting(String sourceClass, String sourceMethod);
    
    /**
     * Log a method return with parameters.
     * 
     * @param sourceClass
     * @param sourceMethod
     * @param arguments
     */
    void exiting(String sourceClass, String sourceMethod, Object[] arguments);

    /**
     * sets the debug level
     * @param level
     * 
     * @uml.property name="level"
     */
    void setLevel(int level);

    /**
     * @return the debug level
     * 
     * @uml.property name="level"
     */
    int getLevel();
    
    /**
     * Debugging log is enabled.
     * 
     * @return <code>true</code> if the log level is DEBUG, otherwise <code>false</code>
     */
    boolean isDebug();

    
    /** Debug log level */
    int      LOG_DEBUG    = 0;
    /** Info log level */
    int      LOG_INFO     = 1;
    /** Warning log level */
    int      LOG_WARNING  = 2;
    /** Fatal error log level */
    int      LOG_ERROR    = 3;
  }

/* *****************************************************************************
   Changes to this class. What the people have been up to:
   $Log: ILogger.java,v $
   Revision 1.8  2006/09/12 12:49:47  bezema
   Adding documentation and @overides annotations

   Revision 1.7  2006/07/26 18:52:24  mschneider
   Fixed problem that caused error message because of missing ResourceBundle.

   Revision 1.6  2006/04/06 20:25:28  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:43  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:27  poth
   *** empty log message ***

   Revision 1.3  2005/08/08 21:30:39  friebe
   add method isDebug()

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/08/09 06:42:38  ap
   exiting() method add to logger framework

   Revision 1.3  2004/07/09 07:01:33  ap
   no message

   Revision 1.2  2004/06/15 14:58:19  tf
   refactored Logger and add new methods

   Revision 1.1  2004/05/14 15:26:38  tf
   initial checkin


 **************************************************************************** */