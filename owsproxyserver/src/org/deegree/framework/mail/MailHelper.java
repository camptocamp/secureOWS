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
package org.deegree.framework.mail;

// J2EE 1.2
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;

/**
 * A helper class to create and send mail.
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.8 $,$Date: 2006/07/12 14:46:19 $
 * 
 * @see javax.mail.Message
 * @see javax.mail.internet.MimeMessage
 */

public final class MailHelper {

    private static final ILogger logger = LoggerFactory.getLogger( MailHelper.class );

    private static final String CHARSET = "iso-8859-1";

    /**
     * Creates a mail helper to send a message.
     * 
     */
    public MailHelper() {
    }

    /**
     * This method creates an email message and sends it using the J2EE mail services
     * 
     * @param eMess
     *            a email message
     * @param mailHost
     *            name of the SMTP host
     * 
     * @throws SendMailException
     *             an exception if the message is undeliverable
     */
    public static void createAndSendMail( MailMessage eMess, String mailHost )
        throws SendMailException {
        Properties p = System.getProperties();
        p.put( "mail.smtp.host", mailHost );
        new MailHelper().createAndSendMail( eMess, Session.getDefaultInstance( p ) );
    }

    /**
     * This method creates an email message and sends it using the J2EE mail services
     * 
     * @param eMess
     *            a email message
     * 
     * @throws SendMailException
     *             an exception if the message is undeliverable
     * 
     * @see javax.mail.Transport
     * @see <a href="http://java.sun.com/j2ee/tutorial/1_3-fcs/doc/Resources4.html#63060">J2EE Mail
     *      Session connection </a>
     */
    public void createAndSendMail( MailMessage eMess, Session session ) throws SendMailException {
        if ( eMess == null || !eMess.isValid() ) {
            throw new SendMailException( "Not a valid e-mail!" );
        }
        try {
            int k = eMess.getMessageBody().length() > 60 ? 60 : eMess.getMessageBody().length() - 1;
            logger.logDebug( StringTools.concat( 500, "Sending message, From: ", 
                    eMess.getSender(),"\nTo: ", eMess.getReceiver(), "\nSubject: ",
                    eMess.getSubject(), "\nContents: ",  
                    eMess.getMessageBody().substring( 0, k ), "..." ) );
            // construct the message
            MimeMessage msg = new MimeMessage( session );
            msg.setFrom( new InternetAddress( eMess.getSender() ) );
            msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse(
                eMess.getReceiver(), false ) );
            msg.setSubject( eMess.getSubject(), MailHelper.CHARSET );
            if ( eMess.getMimeType().equals( MailMessage.PLAIN_TEXT ) ) {
                msg.setText( eMess.getMessageBody(), MailHelper.CHARSET );
            } else {
                msg.setContent( eMess.getMessageBody(), eMess.getMimeType() );
            }
            msg.setHeader( "X-Mailer", "JavaMail" );
            msg.setSentDate( new Date() );
            // send the mail off
            Transport.send( msg );
            logger.logDebug( "Mail sent successfully! Header=", eMess.getHeader() );
        } catch (Exception e) {
            throw new SendMailException( "Error while sending message:"
                + eMess.getHeader(), e );
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MailHelper.java,v $
Revision 1.8  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
