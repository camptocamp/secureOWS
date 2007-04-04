//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/getcapabilities/WCSCapabilities.java,v 1.5 2006/04/06 20:25:31 poth Exp $
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
package org.deegree.ogcwebservices.wcs.getcapabilities;

import org.deegree.ogcwebservices.getcapabilities.Capability;
import org.deegree.ogcwebservices.getcapabilities.OGCStandardCapabilities;
import org.deegree.ogcwebservices.getcapabilities.Service;

/**
 * @version $Revision: 1.5 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.5 $, $Date: 2006/04/06 20:25:31 $ *  * @since 2.0
 */

public class WCSCapabilities extends OGCStandardCapabilities {

    /**
     * 
     * @uml.property name="contentMetadata"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ContentMetadata contentMetadata;


    /**
     * @param version
     * @param updateSequence
     * @param service
     * @param capabilitiy
     * @param contentMetadata
     */
    public WCSCapabilities(String version, String updateSequence,
            Service service, Capability capabilitiy,
            ContentMetadata contentMetadata) {
        super(version, updateSequence, service, capabilitiy);
        this.contentMetadata = contentMetadata;
    }

    /**
     * @return Returns the contentMetadata.
     * 
     * @uml.property name="contentMetadata"
     */
    public ContentMetadata getContentMetadata() {
        return contentMetadata;
    }

    /**
     * @param contentMetadata
     *            The contentMetadata to set.
     * 
     * @uml.property name="contentMetadata"
     */
    public void setContentMetadata(ContentMetadata contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * WCSCapabilities.java,v $ Revision 1.3 2004/06/03 09:02:20 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:39 ap no message
 * 
 *  
 ******************************************************************************/
