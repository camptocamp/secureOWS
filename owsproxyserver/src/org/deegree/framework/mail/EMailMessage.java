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

/**
 * This class encapsulates all the info need to send an email message. This object is passed to the
 * MailerEJB sendMail(...) method.
 * 
 * @testcase
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.7 $,$Date: 2006/07/12 14:46:19 $
 */
public class EMailMessage implements java.io.Serializable, MailMessage {

    private String sender;

    private String subject; 

    private String htmlContents;

    private String emailReceiver;

    private String mimeType;

    /**
     * Creates an empty email message with the default MIME type plain text.
     */
    private EMailMessage() {
        try {
            this.setMimeType( MailMessage.PLAIN_TEXT );
        } catch (Exception ex) {
            // nothing to do
        }
    }

    /**
     * Creates a new mail message with MIME type text/plain.
     * 
     * @param from
     *            the sender
     * @param to
     *            the receiver list
     * @param subject
     *            the subject
     * @param messageBody
     *            the content of the message
     */
    public EMailMessage( String from, String to, String subject, String messageBody ) {
        this();

        this.setSender( from );
        this.setReceiver( to );
        this.setSubject( subject );
        this.setMessageBody( messageBody );
    }

    /**
     * Creates a new mail message with the given MIME type.
     * 
     * @param from
     *            the sender
     * @param to
     *            the receiver list
     * @param subject
     *            the subject
     * @param messageBody
     *            the content of the message
     * @param mimeType
     *            the MIME type of the message body
     * @throws UnknownMimeTypeException
     *             if the given mimeType is not supported
     */
    public EMailMessage( String from, String to, String subject, String messageBody, String mimeType )
        throws UnknownMimeTypeException {
        this( from, to, subject, messageBody );
        this.setMimeType( mimeType );
    }

    /**
     * Returns the state of this message. If sender and receiver are unequal null then this message
     * is valid otherwise invalid.
     * 
     * @return validation state, <code>true</code> if sender and receiver are not
     *         <code>null</code>, otherwise <code>false</code>
     */
    public boolean isValid() {
        if ( this.getSender() != null
            && this.getReceiver() != null ) {
            return true;
        }
        return false;
    }

    /**
     * Return mail header including sender, receiver and subject.
     * 
     * @return string with sender, receiver and subject
     */
    public String getHeader() {
        return ( "From:"
            + this.getSender() + ", To:" + this.getReceiver() + ", Subject:" + this.getSubject() );
    }

    /**
     * Description of the Method
     * 
     * @return Description of the Return Value
     */
    public String toString() {
        return ( "From:"
            + this.getSender() + ", To:" + this.getReceiver() + ", Subject:" + this.getSubject()
            + ",Body: " + this.getMessageBody() );
    }

    /**
     * Method declaration
     * 
     * @return
     * 
     * @uml.property name="sender"
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Method declaration
     * 
     * @return The messageBody value
     */
    public String getMessageBody() {
        return this.htmlContents;
    }

    /**
     * Method declaration
     * 
     * @return
     */
    public String getReceiver() {
        return this.emailReceiver;
    }

    /**
     * Method declaration
     * 
     * @param to
     */
    public void setReceiver( String to ) {
        this.emailReceiver = to;
    }

    /**
     * Method declaration
     * 
     * @param message
     */
    public void setMessageBody( String message ) {
        this.htmlContents = message;
    }

    /**
     * Method declaration
     * 
     * @param from
     * 
     * @uml.property name="sender"
     */
    public void setSender( String from ) {
        this.sender = from;
    }

    /**
     * Method declaration
     * 
     * @param title
     * 
     * @uml.property name="subject"
     */
    public void setSubject( String title ) {
        this.subject = title;
    }

    /**
     * Gets the subject attribute of the EMailMessage object
     * 
     * @return The subject value
     * 
     * @uml.property name="subject"
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the mimeType attribute of the EMailMessage object
     * 
     * @param mimeType
     *            The new mimeType value
     * @throws UnknownMimeTypeException
     *             if the given MIME type is not supported
     * 
     * @uml.property name="mimeType"
     */
    public void setMimeType( String mimeType ) throws UnknownMimeTypeException {
        if ( mimeType.equalsIgnoreCase( MailMessage.PLAIN_TEXT ) ) {
            this.mimeType = mimeType;
        } else if ( mimeType.equalsIgnoreCase( MailMessage.TEXT_HTML ) ) {
            this.mimeType = mimeType;
        } else {
            throw new UnknownMimeTypeException( getClass().getName(), mimeType );
        }
    }

    /**
     * Gets the mimeType attribute of the EMailMessage object
     * 
     * @return The mimeType value
     * 
     * @uml.property name="mimeType"
     */
    public String getMimeType() {
        return this.mimeType;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: EMailMessage.java,v $
Revision 1.7  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
