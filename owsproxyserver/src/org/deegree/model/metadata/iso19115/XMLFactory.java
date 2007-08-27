//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/metadata/iso19115/XMLFactory.java,v 1.6 2006/09/12 09:24:44 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.model.metadata.iso19115;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.deegree.datatypes.Code;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.security.owsrequestvalidator.wms.WMSValidator;
import org.w3c.dom.Element;

/**
 * <code>XMLFactory</code> with append methods for the various ISO 19115 elements
 * as specified in the OWS common specification 1.0.0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/09/12 09:24:44 $
 * 
 * @since 2.0
 */

public class XMLFactory extends org.deegree.ogcbase.XMLFactory {

    private static URI OWS = CommonNamespaces.OWSNS;
    
    private static String POWS = CommonNamespaces.OWS_PREFIX + ":";
    
    private static String PXLINK = CommonNamespaces.XLINK_PREFIX + ":";
    
    /**
     * Appends the given <code>CitedResponsibleParty</code> object as XML.
     * 
     * @param root
     * @param party
     */
    public static void appendCitedResponsibleParty( Element root, CitedResponsibleParty party ) {
        Element elem = XMLTools.appendElement( root, OWS, POWS + "ResponsiblePartySubsetType" );
        
        String[] individualNames = party.getIndividualName();
        if( ( individualNames != null ) && ( individualNames.length != 0 ) && ( individualNames[0] != null ) )
            XMLTools.appendElement( elem, OWS, POWS + "IndividualName", individualNames[0] );

        String[] positionNames = party.getPositionName();
        if( ( positionNames != null ) && ( positionNames.length != 0 ) && ( positionNames[0] != null ) )
            XMLTools.appendElement( elem, OWS, POWS + "PositionName", positionNames[0] );
        
        RoleCode[] roles = party.getRoleCode();
        if( ( roles != null ) && ( roles.length != 0 ) && ( roles[0] != null ) )
            XMLTools.appendElement( elem, OWS, POWS + "Role", roles[0].getValue() );
        
        ContactInfo[] contactInfos = party.getContactInfo();
        if( ( contactInfos != null ) && ( contactInfos.length != 0 ) && ( contactInfos[0] != null ) )
            appendContactInfo( elem, contactInfos[0] );
    }
    
    /**
     * Appends the contact info.
     * 
     * @param root
     * @param contactInfo
     */
    public static void appendContactInfo( Element root, ContactInfo contactInfo ) {
        Element elem = XMLTools.appendElement( root, OWS, POWS + "ContactInfo" );
        
        appendPhone( elem, contactInfo.getPhone() );
        appendAddress( elem, contactInfo.getAddress() );
        appendOnlineResource( elem, contactInfo.getOnLineResource() );
        
        String hours = contactInfo.getHoursOfService();
        String instructions = contactInfo.getContactInstructions();

        if( hours != null ) XMLTools.appendElement( elem, OWS, POWS + "HoursOfService", hours );
        if( instructions != null ) XMLTools.appendElement( elem, OWS, POWS + "ContactInstructions",
                                                           instructions );
    }
    
    /**
     * Appends the phone data.
     * 
     * @param root
     * @param phone
     */
    public static void appendPhone( Element root, Phone phone ) {
        if( phone == null ) return;
        
        Element elem = XMLTools.appendElement( root, OWS, POWS + "Phone" );
        
        String[] voice = phone.getVoice();
        for( String number : voice ) XMLTools.appendElement( elem, OWS, POWS + "Voice", number );
        
        String[] facsimile = phone.getFacsimile();
        for( String number : facsimile ) XMLTools.appendElement( elem, OWS, POWS + "Facsimile", number );
    }
    
    /**
     * Appends the address data.
     * 
     * @param root
     * @param address
     */
    public static void appendAddress( Element root, Address address ) {
        if( address == null ) return;
        
        Element elem = XMLTools.appendElement( root, OWS, POWS + "Address" );
        
        String[] deliveryPoint = address.getDeliveryPoint();
        for( String point : deliveryPoint ) XMLTools.appendElement( elem, OWS, POWS + "DeliveryPoint", point );
        
        String city = address.getCity();
        if( city != null ) XMLTools.appendElement( elem, OWS, POWS + "City", city );
        
        String adminArea = address.getAdministrativeArea();
        if( adminArea != null ) XMLTools.appendElement( elem, OWS, POWS + "AdministrativeArea", adminArea );
        
        String postalCode = address.getPostalCode();
        if( postalCode != null ) XMLTools.appendElement( elem, OWS, POWS + "PostalCode", postalCode );
        
        String country = address.getCountry();
        if( country != null ) XMLTools.appendElement( elem, OWS, POWS + "Country", country );
     
        String[] email = address.getElectronicMailAddress();
        for( String mail : email ) XMLTools.appendElement( elem, OWS, POWS + "ElectronicMailAddress", mail );
    }
    
    /**
     * Appends the link.
     * 
     * @param root
     * @param link
     */
    public static void appendOnlineResource( Element root, OnlineResource link ) {
        if( link == null ) return;
        appendOnlineResource( root, link.getLinkage().getHref() );
    }
    
    /**
     * Appends the link.
     * 
     * @param root
     * @param link
     */
    public static void appendOnlineResource( Element root, URL link ) {
        if( link == null ) return;
        
        // fix up URL to standard form
        String url = link.toExternalForm();
        
        // OnlineResource overriding
        String vendorOR = (String)WMSValidator.VendorORMapping.get(Thread.currentThread().getId());
        if (vendorOR != null)
            url = vendorOR;
        
        if( ! url.toString().endsWith( "?") ) {
            if( ! url.endsWith( "&" ) ) {
                if( url.indexOf( "?" ) == -1 ) url = url + "?";
                else url = url + "&";
            }
        }
        
        // XMLTools should add this namespace?
        root.setAttribute( "xmlns:" + CommonNamespaces.XLINK_PREFIX, "http://www.w3.org/1999/xlink" );
        root.setAttribute( PXLINK + "href", url );
        root.setAttribute( PXLINK + "type", "simple" );
    }
    
    /**
     * Appends an online resource in a newly created element in the OWS namespace. The new element
     * will be named according to the tagName parameter.
     * 
     * @param root
     * @param link
     * @param tagName
     */
    public static void appendOnlineResource( Element root, OnlineResource link, String tagName ) {
        Element newElem = XMLTools.appendElement( root, OWS, POWS + tagName );
        appendOnlineResource( newElem, link );
    }
    
    /**
     * Appends an online resource in a newly created element in the OWS namespace. The new element
     * will be named according to the tagName parameter.
     * 
     * @param root
     * @param link
     * @param tagName
     */
    public static void appendOnlineResource( Element root, URL link, String tagName ) {
        Element newElem = XMLTools.appendElement( root, OWS, POWS + tagName );
        appendOnlineResource( newElem, link );
    }
    
    /**
     * Appends the access constraint element. Please note that a lot of the information contained
     * within will not be included in the output due to restrictions of the OWS specification.
     * 
     * @param root
     * @param constraints
     */
    public static void appendAccessConstraint( Element root, Constraints constraints ) {
        List<String> constrList = constraints.getUseLimitations();
        String constr = "";
        if( constrList.size() != 0 ) constr = constrList.get( 0 );
        
        XMLTools.appendElement( root, OWS, POWS + "AccessConstraint", constr );
    }
    
    /**
     * Appends an element of type CodeType with the given name and content.
     * 
     * @param root
     * @param tagName
     * @param code
     */
    public static void appendCode( Element root, String tagName, Code code ) {
        Element elem = XMLTools.appendElement( root, OWS, POWS + tagName, code.getCode() );
        URI codeSpace = code.getCodeSpace();
        if( codeSpace != null ) elem.setAttribute( "codeSpace", codeSpace.toString() );
    }
    
    /**
     * Appends the contents of a <code>Keywords</code> object.
     * 
     * @param root
     * @param keywords
     */
    public static void appendKeywords( Element root, Keywords keywords ) {
        Element elem = XMLTools.appendElement( root, OWS, POWS + "Keywords" );
        
        String[] words = keywords.getKeywords();
        for( String word : words ) XMLTools.appendElement( elem, OWS, POWS + "Keyword", word );
        
        TypeCode code = keywords.getTypeCode();
        if( code != null ) appendCode( elem, "Type", code );
    }
 
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.6  2006/09/12 09:24:44  schmitz
Corrected another xmlns:xlink error.

Revision 1.5  2006/09/12 08:39:00  schmitz
Added xmlns:xlink attribute to online resources.

Revision 1.4  2006/09/08 08:42:03  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.3  2006/08/24 06:40:27  poth
File header corrected

Revision 1.2  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.1  2006/08/08 10:21:52  schmitz
Parser is finished, as well as the iso XMLFactory.



********************************************************************** */