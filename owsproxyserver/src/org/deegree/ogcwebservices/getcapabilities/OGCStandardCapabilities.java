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

/**
 * Represents the capabilities for an OGC-Webservice <u>prior</u> to the * <code>OWS Common Implementation Specification 0.2</code>. * <p> * It consists of the following parts:<table border="1"> * <tr> * <th>Name</th> * <th>Occurences</th> * <th>Function</th> * </tr> * <tr> * <td>Service</td> * <td align="center">1</td> * <td>Provides metadata of the service.</td> * </tr> * <tr> * <td>Capability</td> * <td align="center">1</td> * <td>Provides properties and capabilities of the service.</td> * </tr> * </table> *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @author last edited by: $Author: poth $ *  * @version 2.0, $Revision: 1.6 $, $Date: 2006/07/12 14:46:16 $ *  * @since 2.0
 */

public abstract class OGCStandardCapabilities extends OGCCapabilities {

    /**
     * 
     * @uml.property name="service"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Service service = null;

    /**
     * 
     * @uml.property name="capabilitiy"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Capability capabilitiy = null;


	/**
	 * @param version
	 * @param updateSequence
	 * @param service
	 * @param capabilitiy
	 */
	public OGCStandardCapabilities(String version, String updateSequence,
			Service service, Capability capabilitiy) {
		super(version, updateSequence);
		this.service = service;
		this.capabilitiy = capabilitiy;
	}

    /**
     * Returns the Capabilitiy part of the configuration.
     * 
     * @return
     * 
     * @uml.property name="capabilitiy"
     */
    public Capability getCapabilitiy() {
        return capabilitiy;
    }

    /**
     * Sets the Capabilitiy part of the configuration.
     * 
     * @param capabilitiy
     * 
     * @uml.property name="capabilitiy"
     */
    public void setCapabilitiy(Capability capabilitiy) {
        this.capabilitiy = capabilitiy;
    }

    /**
     * Returns the Service part of the configuration.
     * 
     * @return
     * 
     * @uml.property name="service"
     */
    public Service getService() {
        return service;
    }

    /**
     * Sets the Service part of the configuration.
     * 
     * @uml.property name="service"
     */
    public void setService(Service service) {
        this.service = service;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OGCStandardCapabilities.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
