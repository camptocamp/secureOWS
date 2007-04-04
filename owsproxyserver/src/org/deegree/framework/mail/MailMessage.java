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
 * Interface of a email message. *  * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A> *  * @author last edited by: $Author: poth $ *  * @version $Revision: 1.6 $, $Date: 2006/07/12 14:46:19 $
 */

public interface MailMessage
{

    /**
     * Returns the subject.
     * 
     * 
     * @return the subjec string
     * 
     * @uml.property name="subject"
     */
    String getSubject();

    /**
     * Returns the sender
     * 
     * 
     * @return the sender mail address
     * 
     * @uml.property name="sender"
     */
    String getSender();

    /**
     * Returns the message body
     * 
     * @return string representation of the message body
     * 
     * @uml.property name="messageBody"
     */
    String getMessageBody();

    /**
     * Returns the receiver mail address
     * 
     * 
     * @return the mail address of the receiver
     * 
     * @uml.property name="receiver"
     */
    String getReceiver();

    /**
     * Sets the receiver mail address
     * 
     * 
     * @param to the receiver mail address
     * 
     * @uml.property name="receiver"
     */
    void setReceiver(String to);

    /**
     * Sets the message body
     * 
     * @param message the string representation of the message body
     * 
     * @uml.property name="messageBody"
     */
    void setMessageBody(String message);

    /**
     * Sets the sender mail address.
     * 
     * 
     * @param from the sender mail address
     * 
     * @uml.property name="sender"
     */
    void setSender(String from);

    /**
     * Sets the subject.
     * 
     * @param title the message subject
     * 
     * @uml.property name="subject"
     */
    void setSubject(String title);


   /**
    * Return mail header including sender, receiver and subject.
    *
    * @return string with sender, receiver and subject
    */
    String getHeader();

    /**
     * Sets the MIME type of the message
     * 
     * @param mimeType the MIME type as string
     * 
     * @see #PLAIN_TEXT
     * @see #TEXT_HTML
     * @see <a href="http://www.ietf.org/rfc/rfc1341.txt">IETF MIME RFC</a>
     * @see javax.mail.Part#setContent(java.lang.Object, java.lang.String)
     * @see javax.mail.Part#setText
     * 
     * @uml.property name="mimeType"
     */
    void setMimeType(String mimeType) throws UnknownMimeTypeException;

    /**
     * Returns the MIME type of the message body.
     * 
     * @return  the MIME type as a string
     * 
     * @see javax.mail.Part#getContentType
     * 
     * @uml.property name="mimeType"
     */
    String getMimeType();

  /**
   * Validates the message.
   *
   * @return <code>true</code> - if the message is complete, otherwise false.
   */
   boolean isValid();

   /**
    * MIME type <code>text/plain</code>
    */
   String PLAIN_TEXT = "text/plain";

   /**
    * MIME type <code>text/html</code>
    */
   String TEXT_HTML = "text/html";

   /**
    * HTML mulitpart message with inline elements
    *
    * @see javax.mail.Part
    */
   short PART_INLINE = 0;

   /**
    * Mulitpart message with references
    *
    * @see javax.mail.Part
    */
   short PART_REF = 1;
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MailMessage.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
