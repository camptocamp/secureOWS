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

// J2EE 1.3
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;

import org.deegree.framework.jndi.JndiUtils;
import org.deegree.framework.mail.EMailMessage;
import org.deegree.framework.mail.MailHelper;
import org.deegree.framework.mail.MailMessage;
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.version.Version;

/**
 * The Logger is used to log messages to files. This service will use a logging
 * service provided by the application server or a 3rd party logging service
 * such as Apache Log4J to enable asychronous call of the method log(). The log
 * server is configured by a set of Properties which are provided to the class
 * init.
 * <p>
 * There are some global properties as well: <BR>
 * <UL>
 * <LI><B>log.class </B>: the logging class.
 * <LI><B>log.active </B>: to enable or disabel the logging service
 * <LI><B>log.mail.active </B>: to activate the email notification
 * <LI><B>log.mail.to </B>: the mail address
 * <LI><B>log.mail.session </B>: the mail session used to send mail
 * </UL>
 * <P>
 * Messages are logged using log(). If an error occurs during creation or
 * logging, the message will be written to the server BootLogger.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: UID=$Author: poth $
 * 
 * @version $Revision: 1.10 $, $Date: 2006/10/17 20:31:19 $
 * 
 * @see LoggerFactory
 * @see org.deegree.framework.util.BootLogger
 */
abstract class LoggerService implements ILogger {

    protected static String defaultChannelName;

    private static String hostAddress;

    private static String hostName;

    private static String mailNotificationAddress;

    private static String mailSessionName;

    private static Session mailSession;

    private static String mailHostName;

    private static boolean sendMailIsActive;

    private static DateFormat df;

    private static final String ENTERING_METHOD = "-> {1}#{0}() with arguments {2}";

    private static final String EXITING_METHOD = "<- {1}#{0}()";
   
    public void init (Properties props) {

        df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        try {

            // fetch all configuration parameters
            LoggerService.defaultChannelName = props.getProperty("log.channel.name");
            LoggerService.mailNotificationAddress = props.getProperty("log.mail.to");
            String mailerActive = props.getProperty("log.mail.active");
            LoggerService.mailSessionName = props.getProperty("log.mail.session");
            LoggerService.mailHostName = props.getProperty("log.mail.smtphost");

            // set defaults if not set
            if (mailerActive == null || mailerActive.equalsIgnoreCase("false")) {
                LoggerService.sendMailIsActive = false;
            }
            if (defaultChannelName == null) {
                LoggerService.defaultChannelName = LoggerService.class
                        .getName();
            }
            if (mailNotificationAddress == null) {
                LoggerService.mailNotificationAddress = "UNKNOWN@"
                        + LoggerService.mailHostName;
            }

            hostAddress = InetAddress.getLocalHost().getHostAddress();
            hostName = InetAddress.getLocalHost().getHostName();

        } catch (UnknownHostException ex) {
            BootLogger.log("Unable to determine host: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            BootLogger.log("Error while initializing "
                    + LoggerService.class.getName() + " : " + ex.getMessage());
        } finally {
            BootLogger.log("Using: defaultChannelName=" + defaultChannelName
                    + ", mailNotificationAddress=" + mailNotificationAddress
                    + ", mailSessionName=" + mailSessionName
                    + ", log.mail.active=" + LoggerService.sendMailIsActive);
        }
    }
    
    /**
     * Create logger instance
     *  
     */
    protected LoggerService() {
        //only callable for the package.
    }

    /**
     * Log error with exception
     * 
     * @param message
     *            the log message
     * @param e
     *            the exception to be logged
     * @param properties a given Property file in which specific email notification possibilities are saved.
     */
    public final void logError(String message, Throwable e, Map properties) {
        this.logError(message, e);
        if (sendMailIsActive) {
            this.sendMail(message, e, properties);
        }
    }

    /**
     * Log warning message
     * 
     * @param message
     *            the log message
     */
    public abstract void logWarning(String message);

    /**
     * Log warning message with exception
     * 
     * @param message
     *            the log message
     * @param e
     *            the exception to be logged
     */
    public abstract void logWarning(String message, Throwable e);

    /**
     * Log info message
     * 
     * @param message
     *            the log message
     */
    public abstract void logInfo(String message);

    /**
     * Log info message
     * 
     * @param message
     *            the log message
     * @param e
     *            the exception to be logged
     */
    public abstract void logInfo(String message, Throwable e);

    /**
     * Log debug message
     * 
     * @param message
     *            the log message
     */
    public abstract void logDebug(String message);

    /**
     * Log debug message.
     * 
     * @param message
     *            the log message
     * @param e
     *            the exception to be logged
     */
    public abstract void logDebug(String message, Throwable e);

    /**
     * {@inheritDoc} 
     */
    public void entering() {
        StackTraceElement[] stack = new Exception().getStackTrace();
        StackTraceElement element = null;
        if (stack.length > 2) {
            element = stack[1];
            this.entering(element.getClassName(), element.getMethodName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void entering(String sourceClass, String sourceMethod,
            Object[] arguments) {
        this.logDebug(this.produceMessage(ENTERING_METHOD, new Object[] {
                sourceMethod, sourceClass, arguments}));
    }

    /**
     * 
     * 
     * 
     */
    public void entering(String sourceClass, String sourceMethod) {
        this.entering(sourceClass, sourceMethod, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree_impl.log.ILogger#exiting(java.lang.String,
     *      java.lang.String, java.lang.Object[])
     */
    public void exiting(String sourceClass, String sourceMethod,
            Object[] arguments) {
        this.logDebug(this.produceMessage(EXITING_METHOD, new Object[] {
                sourceMethod, sourceClass}));
    }
    
    /**
     * {@inheritDoc} 
     */
    public void exiting() {
        StackTraceElement[] stack = new Exception().getStackTrace();
        StackTraceElement element = null;
        if (stack.length > 2) {
            element = stack[1];
            this.exiting(element.getClassName(), element.getMethodName());
        }
    }

    /**
     * {@inheritDoc} 
     */
    public void exiting(String sourceClass, String sourceMethod) {
        this.exiting(sourceClass, sourceMethod, null);
    }

    /**
     * Formats the provided string and the args array into a String using
     * MessageFormat.
     */
    private String produceMessage(String pattern, Object[] args) {
        return new MessageFormat(pattern).format(args);
    }

    /**
     * Sends email with exception string. If mail session or mail notification
     * address is null no email is send.
     * 
     * @param message
     *            message is in mail subject
     * @param ex
     *            full exception is displayed in body
     * @param properties
     *            list of properties listed in body
     */
    protected void sendMail(String message, Throwable ex, Map properties) {
        if (sendMailIsActive && mailSessionName != null
                && mailNotificationAddress != null) {
            this.sendMailNotification(message, ex, properties);
        }
    }

    private void sendMailNotification(String message, Throwable ex,
            Map properties) {
        MailMessage mail;
        StringBuffer emailBody = new StringBuffer();

        emailBody.append("A critical error occured in " + Version.getVersion());
        emailBody.append(" running on " + hostName + "/" + hostAddress);
        emailBody.append(" on "
                + df.format(new Date(System.currentTimeMillis())));
        emailBody.append("\n\n");
        if (message != null) {
            emailBody.append("\n\nThe error message: " + message);
        }
        if (properties != null) {
            emailBody.append("\n\nRequest data:\n--------------\n"
                    + properties.toString());
        }
        if (ex != null) {
            emailBody.append("\n\nException:\n---------\n" + ex.getMessage());
            emailBody.append("\n\nStack Trace:\n------------\n\n"
                    + ex.toString());
        }
        emailBody
                .append("\n\nSystem Info:\n--------------\n" + getSystemInfo());

        String subject = "Critical Error:" + Version.getVersion() + " on "
                + hostName + "/" + hostAddress;

        mail = new EMailMessage("DO_NOT_REPLY@" + hostName,
                mailNotificationAddress, subject, emailBody.toString());
        try {
            if (mailSession == null) {
                mailSession = this.getMailSession();
            }
            new MailHelper().createAndSendMail(mail, mailSession);
        } catch (Exception e) {
            BootLogger.logError("Can't send email notification: "
                    + mail.getHeader(), e);
        }
    }

    /**
     * Return system information (memory and properties) as string.
     * 
     * @return system information (memory and properties) as string.
     */
    private String getSystemInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("Total Memory: " + Runtime.getRuntime().totalMemory() / 1024
                + " Kilobyte\n");
        buf.append("Free Memory: " + Runtime.getRuntime().freeMemory() / 1024
                + " Kilobyte\n");
        java.util.Properties sysprops = System.getProperties();
        for (Enumeration e = sysprops.keys(); e.hasMoreElements();) {
            String key = e.nextElement().toString();
            String value = sysprops.getProperty(key);
            buf.append(key + " : " + value + "\n");
        }
        return buf.toString();
    }

    /**
     * 
     * @uml.property name="mailSession"
     */
    private Session getMailSession() {
        Session session = null;
        try {
            session = (Session) JndiUtils.lookup(mailSessionName, Session.class);
        } catch (Exception ex) {
            BootLogger.logError("Error while initializing "
                + LoggerService.class.getName()
                + " : "
                + ex.getMessage(), ex);
        } finally {
            if (session == null) {
                Properties p = System.getProperties();
                p.put("mail.smtp.host", LoggerService.mailHostName);
                session = Session.getDefaultInstance(p, null);
            }
        }
        return session;
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * LoggerService.java,v $ Revision 1.1 2004/06/15 14:58:19 tf refactored Logger
 * and add new methods
 * 
 * Revision 1.2 2004/05/25 15:39:42 tf add smtp host to resources
 * 
 * Revision 1.1 2004/05/14 15:26:38 tf initial checkin
 *  
 ******************************************************************************/