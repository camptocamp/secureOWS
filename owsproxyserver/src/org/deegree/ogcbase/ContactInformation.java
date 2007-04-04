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
package org.deegree.ogcbase;


/**
 * Identification of, and means of communication with a person and/or * organization associated with the service/resource. based on ISO 19115. *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.6 $, $Date: 2006/07/12 14:46:14 $ * @since 1.0
 */

public class ContactInformation {

    private ContactAddress contactAddress = null;
    private ContactPersonPrimary contactPersonPrimary = null;
    private String contactElectronicMailAddress = null;
    private String contactFacsimileTelephone = null;
    private String contactPosition = null;
    private String contactVoiceTelephone = null;

    /**
     * constructor initializing the class with ContactInformation Strings
     */
    public ContactInformation(String contactPosition,
            String contactVoiceTelephone, String contactFacsimileTelephone,
            String contactElectronicMailAddress,
            ContactPersonPrimary contactPersonPrimary,
            ContactAddress contactAddress) {
        setContactPosition(contactPosition);
        setContactVoiceTelephone(contactVoiceTelephone);
        setContactFacsimileTelephone(contactFacsimileTelephone);
        setContactElectronicMailAddress(contactElectronicMailAddress);
        setContactPersonPrimary(contactPersonPrimary);
        setContactAddress(contactAddress);
    }

    /**
     * returns a datastructure that contains the name of the contact person and
     * the organization he works for.
     */
    public ContactPersonPrimary getContactPersonPrimary() {
        return contactPersonPrimary;
    }

    /**
     * sets a datastructure that contains the name of the contact person and
     * the organization he works for.
     */
    public void setContactPersonPrimary(
        ContactPersonPrimary contactPersonPrimary) {
        this.contactPersonPrimary = contactPersonPrimary;
    }

    /**
     * returns the positon of the contact person within its organization
    */
    public String getContactPosition() {
        return contactPosition;
    }

    /**
     * sets the positon of the contact person within its organization
    */
    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    /**
     * returns the address where to reach to contact person
    */
    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    /**
     * sets the address where to reach to contact person
     */
    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    /**
     * returns the voice Telephone number of the contact person
      */
    public String getContactVoiceTelephone() {
        return contactVoiceTelephone;
    }

    /**
     * sets the voice Telephone number of the contact person
     */
    public void setContactVoiceTelephone(String contactVoiceTelephone) {
        this.contactVoiceTelephone = contactVoiceTelephone;
    }

    /**
     * returns the facsimile Telephone number of the contact person
    */
    public String getContactFacsimileTelephone() {
        return contactFacsimileTelephone;
    }

    /**
     * sets the facsimile Telephone number of the contact person
    */
    public void setContactFacsimileTelephone(String contactFacsimileTelephone) {
        this.contactFacsimileTelephone = contactFacsimileTelephone;
    }

    /**
     * returns the email address of the contact person
     */
    public String getContactElectronicMailAddress() {
        return contactElectronicMailAddress;
    }

    /**
     * sets the email address of the contact person
     */
    public void setContactElectronicMailAddress(
        String contactElectronicMailAddress) {
        this.contactElectronicMailAddress = contactElectronicMailAddress;
    }

   
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ContactInformation.java,v $
Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
