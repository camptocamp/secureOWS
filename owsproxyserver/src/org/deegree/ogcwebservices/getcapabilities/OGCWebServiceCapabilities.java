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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.Marshallable;
import org.w3c.dom.Document;

/**
 * The purpose of the GetCapabilities operation is described in the Basic
 * CapabilitiesService Elements section, above. In the particular case of a Web
 * Map CapabilitiesService, the response of a GetCapabilities request is general
 * information about the service itself and specific information about the
 * available maps.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @version 2002-03-01, $Revision: 1.9 $, $Date: 2006/07/12 14:46:16 $
 * @since 1.0
 */

public abstract class OGCWebServiceCapabilities implements Marshallable {

    protected static final ILogger LOG = LoggerFactory.getLogger(OGCWebServiceCapabilities.class);


    private CapabilitiesService service = null;
    private String updateSequence = null;
    private String version = null;

    /**
     * constructor initializing the class with the OGCWebServiceCapabilities
     */
    public OGCWebServiceCapabilities(String version, String updateSequence,
            CapabilitiesService service) {
        setVersion(version);
        setUpdateSequence(updateSequence);
        setService(service);
    }

    /**
     * returns the version of the service
     */
    public String getVersion() {
        return version;
    }

    /**
     * sets the version of the service
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * The UPDATESEQUENCE parameter is for maintaining cache consistency. Its
     * value can be an integer, a timestamp in [ISO 8601:1988(E)] format , or
     * any other number or string. The server may include an UpdateSequence
     * value in its Capabilities XML. If present, this value should be increased
     * when changes are made to the Capabilities (e.g., when new maps are added
     * to the service). The server is the sole judge of lexical ordering
     * sequence. The client may include this parameter in its GetCapabilities
     * request.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * sets the update sequence
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * this returns a general describtion of the service described by the
     * Capabilities XML document.
     */
    public CapabilitiesService getService() {
        return service;
    }

    /**
     * this sets a general describtion of the service described by the
     * Capabilities XML document.
     */
    public void setService(CapabilitiesService service) {
        this.service = service;
    }

    /**
     * Must be overridden by subclass. Replaces abstract method exportAsXML.
     *  
     */
    public Document export() {
        return null;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OGCWebServiceCapabilities.java,v $
Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
