//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/GetCapabilities.java,v 1.9 2006/10/27 13:26:13 poth Exp $
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

import java.util.Map;

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * Each OGC Web Service must be able to describe its capabilities. This abstract
 * base class defines the structure intended to convey general information about the
 * service itself, and summary information about the available data.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.9 $
 */
public abstract class GetCapabilities extends AbstractOGCWebServiceRequest {

    private String updateSequence;

    private String[] sections;

    private String[] acceptVersions;

    private String[] acceptFormats;

    /**
     * @param id 
     * @param service
     * @param version
     * @param updateSequence
     * @param acceptVersions 
     * @param sections
     * @param acceptFormats 
     */
    protected GetCapabilities( String id, String version, String updateSequence,
                              String[] acceptVersions, String[] sections, 
                              String[] acceptFormats, Map<String, String> vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
        this.updateSequence = updateSequence;
        this.sections = sections;
        this.acceptVersions = acceptVersions;
        this.acceptFormats = acceptFormats;
    }

    /**
     * @return Returns the capabilitiesSection.
     * 
     */
    public String[] getSections() {
        return sections;
    }

    /**
     * @return
     */
    public String[] getAcceptVersions() {
        return acceptVersions;
    }

    /**
     * @return
     */
    public String[] getAcceptFormats() {
        return acceptFormats;
    }

    /**
     * @return Returns the updateSequence.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

}

/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * GetCapabilities.java,v $ Revision 1.8 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.7 2004/06/21 06:44:57 ap no message
 * 
 * Revision 1.6 2004/06/17 15:43:12 ap no message
 * 
 * Revision 1.5 2004/06/14 15:14:08 ap no message
 * 
 * Revision 1.4 2004/06/14 14:54:13 ap no message
 * 
 * Revision 1.3 2004/06/09 09:30:42 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:38 ap no message
 * 
 *  
 ******************************************************************************/