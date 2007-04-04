//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/OGCCapabilities.java,v 1.6 2006/04/06 20:25:25 poth Exp $
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

import java.io.Serializable;

/**
 * Abstract base class for capabilities of any OGC service instance.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/04/06 20:25:25 $
 * 
 * @since 2.0
 *  
 */
public abstract class OGCCapabilities implements Serializable {

    private String version;

    private String updateSequence;

    /**
     * Constructor to be used in the constructor of subclasses.
     * 
     * @param version
     * @param updateSequence
     */
    public OGCCapabilities(String version, String updateSequence) {
        this.version = version;
        this.updateSequence = updateSequence;
    }

    /**
     * Returns the updateSequence.
     * 
     * @return
     * 
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Sets the updateSequence parameter.
     * 
     * @param updateSequence
     * 
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * Returns the version.
     * 
     * @return
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version parameter.
     * 
     * @param version
     * 
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * Capabilities.java,v $ Revision 1.4 2004/06/23 11:55:40 mschneider Changed
 * hierarchy in org.deegree.ogcwebservices.getcapabilities: -
 * OGCCommonCapabilities are derived for Capabilities according to the OGCCommon
 * Implementation Specification 0.2 - OGCStandardCapabilities are derived for
 * Capabilities prior to the OGCCommon Implementation Specification 0.2
 * 
 * Revision 1.3 2004/05/27 10:15:28 tf initial checkin
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:38 ap no message
 * 
 *  
 ******************************************************************************/
