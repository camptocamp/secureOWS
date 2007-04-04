// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/ServiceIdentification.java,v 1.9 2006/11/07 11:09:54 mschneider Exp $
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
package org.deegree.ogcwebservices.getcapabilities;

import org.deegree.datatypes.Code;
import org.deegree.model.metadata.iso19115.Keywords;

/**
 * Represents the <code>ServiceIdentification</code> section of the * capabilities of an OGC compliant web service according to the * <code>OGC Common Implementation Specification 0.2</code>. This section * corresponds to and expands the SV_ServiceIdentification class in ISO 19119. * <p> * It consists of the following elements: <table border="1"> * <tr> * <th>Name</th> * <th>Occurences</th> * <th>Function</th> * </tr> * <tr> * <td>ServiceType</td> * <td align="center">1</td> * <td>Useful to provide service type name useful for machine-to-machine * communication</td> * </tr> * <tr> * <td>ServiceTypeVersion</td> * <td align="center">1-*</td> * <td>Useful to provide list of server-supported versions.</td> * </tr> * <tr> * <td>Title</td> * <td align="center">1</td> * <td>Useful to provide a server title for display to a human.</td> * </tr> * <tr> * <td>Abstract</td> * <td align="center">0|1</td> * <td>Usually useful to provide narrative description of server, useful for * display to a human.</td> * </tr> * <tr> * <td>Keywords</td> * <td align="center">0-*</td> * <td>Often useful to provide keywords useful for server searching.</td> * </tr> * <tr> * <td>Fees</td> * <td align="center">0|1</td> * <td>Usually useful to specify fees, or NONE if no fees.</td> * </tr> * <tr> * <td>AccessConstraints</td> * <td align="center">0-*</td> * <td>Usually useful to specify access constraints, or NONE if no access * constraints.</td> * </tr> * </table> *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @author last edited by: $Author: mschneider $ *  * @version $Revision: 1.9 $ */
public class ServiceIdentification {

    private String name;
    
    private Code serviceType;
    
	private String[] serviceTypeVersions;
    
	private String title;
    
	private String serviceAbstract;
    
    private Keywords[] keywords;
    
	private String fees;
    
	private String[] accessConstraints;

	/**
	 * Constructs a new ServiceIdentification object.
	 * 
	 * @param serviceType
	 * @param serviceTypeVersions
	 * @param title
	 * @param serviceAbstract
	 *            may be null
	 * @param keywords
	 *            may be an empty array or null
	 * @param fees
	 *            may be null
	 * @param accessConstraints
	 *            may be an empty array or null
	 */
	public ServiceIdentification( String name, Code serviceType,
			String[] serviceTypeVersions, String title, String serviceAbstract,
			Keywords[] keywords, String fees, String[] accessConstraints) {
        this.name = name;
		this.serviceType = serviceType;
		this.serviceTypeVersions = serviceTypeVersions;
		this.title = title;
		this.serviceAbstract = serviceAbstract;
		this.keywords = keywords;
		this.fees = fees;
		this.accessConstraints = accessConstraints;
	}

    /**
     * Constructs a new ServiceIdentification object.
     * 
     * @param serviceType
     * @param serviceTypeVersions
     * @param title
     * @param serviceAbstract
     *            may be null
     * @param keywords
     *            may be an empty array or null
     * @param fees
     *            may be null
     * @param accessConstraints
     *            may be an empty array or null
     */
    public ServiceIdentification( Code serviceType,
            String[] serviceTypeVersions, String title, String serviceAbstract,
            Keywords[] keywords, String fees, String[] accessConstraints) {
        this.name = title;
        this.serviceType = serviceType;
        this.serviceTypeVersions = serviceTypeVersions;
        this.title = title;
        this.serviceAbstract = serviceAbstract;
        this.keywords = keywords;
        this.fees = fees;
        this.accessConstraints = accessConstraints;
    }

    /**
     * Returns the java representation of the ServiceType-element. In the XML
     * document, this element has the type ows:CodeType.
     * 
     * @return
     * 
     */
    public Code getServiceType() {
        return serviceType;
    }

    /**
     * Returns the java representation of the ServiceTypeVersion-elements. In
     * the XML document, these elements have the type ows:VersionType.
     * 
     * @return
     */
    public String[] getServiceTypeVersions() {
        return serviceTypeVersions;
    }

    /**
     * Returns the java representation of the Title-element. In the XML
     * document, this element has the type string.
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }


	/**
	 * Returns the java representation of the Abstract-element. In the XML
	 * document, this element has the type string.
	 * 
	 * @return
	 */
	public String getAbstract() {
		return serviceAbstract;
	}

    /**
     * Returns the java representation of the Keywords-elements. In the XML
     * document, these elements have the type ows:Keyword.
     * 
     * @return
     */
    public Keywords[] getKeywords() {
        return keywords;
    }

    /**
     * Returns the java representation of the AccessConstraints-elements. In the
     * XML document, these elements have the type string.
     * 
     * @return
     */
    public String getFees() {
        return fees;
    }

    /**
     * Returns the java representation of the AccessConstraints-elements. In the
     * XML document, these elements have the type string.
     * 
     * @return
     */
    public String[] getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}

/* ******************************************************************************
 * $Log: ServiceIdentification.java,v $
 * Revision 1.9  2006/11/07 11:09:54  mschneider
 * Fixed footer formatting.
 *
 * Revision 1.8  2006/08/01 11:46:07  schmitz
 * Added data classes for the new OWS common capabilities framework
 * according to the OWS 1.0.0 common specification.
 * Added name to service identification.
 *
 * Revision 1.7  2006/07/12 14:46:16  poth
 * comment footer added
 *
 * Revision 1.6  2006/04/06 20:25:25  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:41  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:25  poth
 * *** empty log message ***
 *
 * Revision 1.3  2005/06/08 15:13:55  poth
 * no message
 *
 * Revision 1.2  2005/01/18 22:08:55  poth
 * no message
 *
 * Revision 1.4  2004/07/12 13:03:21  mschneider
 * More work on the CatalogConfiguration and capabilities framework.
 * 
 * Revision 1.3 2004/06/30 15:16:05 mschneider
 * Refactoring of XMLTools.
 * 
 * Revision 1.2 2004/06/28 15:40:13 mschneider
 * Finished the generation of the ServiceIdentification part of the Capabilities from DOM, added functionality to the XMLTools helper class.
 *  
 ***************************************************************************** */