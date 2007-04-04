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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CitedResponsibleParty_Impl.java
 *
 * Created on 16. September 2002, 09:55
 * <p>----------------------------------------------------------------------</p>
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer</a>
 * @version $Revision: 1.7 $ $Date: 2006/08/07 09:42:49 $ *
 */
public class CitedResponsibleParty implements Serializable {

    private static final long serialVersionUID = 5912684530267785339L;

    private List<ContactInfo> contactinfo = null;
    
    private List<String> individualname = null;
    
    private List<String> organisationname = null;
    
    private List<String> positionname = null;
    
    private List<RoleCode> rolecode = null;
    
    /**
     * Creates new instance through <code>List</code>s.
     * 
     * @param contactInfo
     * @param individualName
     * @param organizationName
     * @param positionName
     * @param roleCode
     */
    public CitedResponsibleParty( List<ContactInfo> contactInfo, List<String> individualName,
                                  List<String> organizationName, List<String> positionName,
                                  List<RoleCode> roleCode ) {
        contactinfo = contactInfo;
        individualname = individualName;
        organisationname = organizationName;
        positionname = positionName;
        rolecode = roleCode;
    }
    
    /** Creates a new instance of CitedResponsibleParty_Impl */
    public CitedResponsibleParty(ContactInfo[] contactinfo,
                                      String[] individualname,
                                      String[] organisationname,
                                      String[] positionname,
                                      RoleCode[] rolecode) {

        this.contactinfo = new ArrayList<ContactInfo>();
        this.individualname = new ArrayList<String>();
        this.organisationname = new ArrayList<String>();
        this.positionname = new ArrayList<String>();
        this.rolecode = new ArrayList<RoleCode>();
        
        setContactInfo(contactinfo);
        setIndividualName(individualname);
        setOrganisationName(organisationname);
        setPositionName(positionname);
        setRoleCode(rolecode);
    }
    

    /** minOccurs="0" maxOccurs="unbounded"
     * @return ContactInfo-Array
     *
     */
    public ContactInfo[] getContactInfo() {
        return contactinfo.toArray( new ContactInfo[contactinfo.size()] );
    }
    
    /**
     * @see CitedResponsibleParty#getContactInfo()
     */
    public void addContactInfo(ContactInfo contactinfo) {
        this.contactinfo.add(contactinfo);
    }
    
    /**
     * @see CitedResponsibleParty#getContactInfo()
     */
    public void setContactInfo(ContactInfo[] contactinfo) {
        this.contactinfo = Arrays.asList( contactinfo );
    }
    

    /** minOccurs="0" maxOccurs="unbounded"
     * @return String-Array
     *
     */
    public String[] getIndividualName() {
        return individualname.toArray( new String[individualname.size()] );
    }
    
    /**
     * @see CitedResponsibleParty#getIndividualName()
     */
    public void addIndividualName(String individualname) {
        this.individualname.add(individualname);
    }
    
    /**
     * @see CitedResponsibleParty#getIndividualName()
     */
    public void setIndividualName(String[] individualname) {
        this.individualname = Arrays.asList( individualname );
    }
    

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * @return String-Array
     */
    public String[] getOrganisationName() {
        return organisationname.toArray( new String[organisationname.size()] );
    }
    
    /**
     * @see CitedResponsibleParty#getOrganisationName()
     */
    public void addOrganisationName(String organisationname) {
        this.organisationname.add(organisationname);
    }
    
    /**
     * @see CitedResponsibleParty#getOrganisationName()
     */
    public void setOrganisationName(String[] organisationname) {
        this.organisationname = Arrays.asList( organisationname );
    }
    

    /** minOccurs="0" maxOccurs="unbounded"
     * @return String-Array
     *
     */
    public String[] getPositionName() {
        return positionname.toArray( new String[positionname.size()] );
    }
    
    /**
     * @see CitedResponsibleParty#getPositionName()
     */
    public void addPositionName(String positionname) {
        this.positionname.add(positionname);
    }
    
    /**
     * @see CitedResponsibleParty#getPositionName()
     */
    public void setPositionName(String[] positionname) {
        this.positionname = Arrays.asList( positionname );
    }
    

    /** minOccurs="0" maxOccurs="unbounded"
     * @return RoleCode-Array
     *
     */
    public RoleCode[] getRoleCode() {
        return rolecode.toArray( new RoleCode[rolecode.size()] );
    }
    
    /**
     * @see CitedResponsibleParty#getRoleCode()
     */
    public void addRoleCode(RoleCode rolecode) {
        this.rolecode.add(rolecode);
    }
    
    /**
     * @see CitedResponsibleParty#getRoleCode()
     */
    public void setRoleCode(RoleCode[] rolecode) {
        if( rolecode != null ) {
            this.rolecode = Arrays.asList( rolecode );
        } else this.rolecode.clear();
    }

	/**
     * to String method
     */
	@Override
    public String toString() {
		String ret = null;
		ret = "contactinfo = " + contactinfo + "\n";
		ret += "individualname = " + individualname + "\n";
		ret += "organisationname = " + organisationname + "\n";
		ret += "positionname = " + positionname + "\n";
		ret += "rolecode = " + rolecode + "\n";
		return ret;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CitedResponsibleParty.java,v $
Revision 1.7  2006/08/07 09:42:49  poth
unneccessary type cast removed

Revision 1.6  2006/08/04 15:16:26  schmitz
Half the OWS common 1.0.0 parser is finished. Data classes should be complete.

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
