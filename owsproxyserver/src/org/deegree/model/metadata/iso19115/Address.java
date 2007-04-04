/*
 ----------------    FILE HEADER  ------------------------------------------
 
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

package org.deegree.model.metadata.iso19115;

import java.util.ArrayList;

/**
 * Address object.
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </a>
 * @version $Revision: 1.8 $ $Date: 2006/07/12 14:46:18 $
 */
public class Address {

    private String administrativearea = null;
    private String city = null;
    private String country = null;
    private ArrayList deliverypoint = null;
    private ArrayList electronicmailaddress = null;
    private String postalcode = null;

    /**
     * Address 
     * 
     * @param administrativearea
     * @param city
     * @param country
     * @param deliverypoint
     * @param electronicmailaddress
     * @param postalcode
     */
    public Address(String administrativearea, String city, String country,
            String[] deliverypoint, String[] electronicmailaddress,
            String postalcode) {

        this.deliverypoint = new ArrayList();
        this.electronicmailaddress = new ArrayList();

        setAdministrativeArea(administrativearea);
        setCity(city);
        setCountry(country);
        setDeliveryPoint(deliverypoint);
        setElectronicMailAddress(electronicmailaddress);
        setPostalCode(postalcode);
    }

    /**
     * minOccurs="0"
     *  
     */
    public String getAdministrativeArea() {
        return administrativearea;
    }

    /**
     * @see Address#getAdministrativeArea()
     */
    public void setAdministrativeArea(String administrativearea) {
        this.administrativearea = administrativearea;
    }

    /**
     * minOccurs="0"/>
     * 
     * @uml.property name="city"
     */
    public String getCity() {
        return city;
    }

    /**
     * @see Address#getCity()
     * 
     * @uml.property name="city"
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * minOccurs="0"
     * 
     * @uml.property name="country"
     */
    public String getCountry() {
        return country;
    }

    /**
     * @see Address#getCountry()
     * 
     * @uml.property name="country"
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     *  
     */
    public String[] getDeliveryPoint() {
        return (String[]) deliverypoint
                .toArray(new String[deliverypoint.size()]);
    }

    /**
     * @see Address#getDeliveryPoint()
     */
    public void addDeliveryPoint(String deliverypoint) {
        this.deliverypoint.add(deliverypoint);
    }

    /**
     * @see Address#getDeliveryPoint()
     */
    public void setDeliveryPoint(String[] deliverypoint) {
        this.deliverypoint.clear();
        for (int i = 0; i < deliverypoint.length; i++) {
            this.deliverypoint.add(deliverypoint[i]);
        }
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     */
    public String[] getElectronicMailAddress() {
        return (String[]) electronicmailaddress
                .toArray(new String[electronicmailaddress.size()]);
    }

    /**
     * @see Address#getElectronicMailAddress()
     */
    public void addElectronicMailAddress(String electronicmailaddress) {
        this.electronicmailaddress.add(electronicmailaddress);
    }

    /**
     * @see Address#getElectronicMailAddress()
     */
    public void setElectronicMailAddress(String[] electronicmailaddress) {
        this.electronicmailaddress.clear();
        for (int i = 0; i < electronicmailaddress.length; i++) {
            this.electronicmailaddress.add(electronicmailaddress[i]);
        }
    }

    /**
     * minOccurs="0"
     *  
     */
    public String getPostalCode() {
        return postalcode;
    }

    /**
     * @see Address#getPostalCode()
     */
    public void setPostalCode(String postalcode) {
        this.postalcode = postalcode;
    }

    /**
     * tpString method
     */
    public String toString() {
        String ret = "administrativearea = " + administrativearea + "\n"
                + "city = " + city + "\n" + "country = " + country + "\n"
                + "deliverypoint = " + deliverypoint + "\n"
                + "electronicmailaddress = " + electronicmailaddress + "\n"
                + "postalcode =" + postalcode + "\n";
        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Address.java,v $
Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
