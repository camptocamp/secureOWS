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
 * Specifies the data structure of a address and the access to its components
 * based on ISO 19115.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.6 $, $Date: 2006/07/12 14:46:14 $
 * @since 1.0
 */
public class ContactAddress {

    private String address = null;

    private String addressType = null;

    private String city = null;

    private String country = null;

    private String postCode = null;

    private String stateOrProvince = null;

   

    /**
     * constructor initializing the class with ContactAddress Strings
     */
    public ContactAddress(String addressType, String address, String city,
            String stateOrProvince, String postCode, String country) {
        setAddressType(addressType);
        setAddress(address);
        setCity(city);
        setStateOrProvince(stateOrProvince);
        setPostCode(postCode);
        setCountry(country);
    }

    /**
     * returns the address type. e.g. 'postal'
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * sets the address type. e.g. 'postal'
     */
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * returns the address. usally this is the street and number of a building. It
     * also can be a p.o. box
     */
    public String getAddress() {
        return address;
    }

    /**
     * sets the address. usally this is the street and number of a building. It
     * also can be a p.o. box
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * returns the name of the city
     */
    public String getCity() {
        return city;
    }

    /**
     * sets the name of the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * returns the name of the state or province of the address.
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * sets the name of the state or province of the address.
     */
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * returns the post code. This doesn't contain an abbreviation for the country
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * sets the post code. This doesn't contain an abbreviation for the country
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * returns the name of the country. this should be the complete name and not
     * an abbreviation.
     */
    public String getCountry() {
        return country;
    }

    /**
     * sets the name of the country. this should be the complete name and not
     * an abbreviation.
     */
    public void setCountry(String country) {
        this.country = country;
    }
   
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ContactAddress.java,v $
Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
