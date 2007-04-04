//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogueCapabilities.java,v 1.6 2006/07/12 14:46:16 poth Exp $
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
package org.deegree.ogcwebservices.csw.capabilities;

import java.io.IOException;
import java.net.URL;

import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.owscommon.OWSCommonCapabilities;
import org.xml.sax.SAXException;

/**
 * Represents the capabilities for an OGC-CSW 2.0.0 compliant service instance.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/07/12 14:46:16 $
 * 
 * @since 2.0
 */

public class CatalogueCapabilities extends OWSCommonCapabilities {


    private FilterCapabilities filterCapabilities;

    /**
     * Creates catalog capabilities from a URL.
     * 
     * @param url
     *            location of the capabilities file
     * @return catalog capabilities
     */
    public static OGCCapabilities createCapabilities(URL url)
            throws IOException, SAXException, InvalidCapabilitiesException {
        OGCCapabilities capabilities = null;
        CatalogueCapabilitiesDocument capabilitiesDoc = new CatalogueCapabilitiesDocument();
        capabilitiesDoc.load(url);
        capabilities = capabilitiesDoc.parseCapabilities();
        return capabilities;
    }

    /**
     * Generates a new CatalogCapabilities instance from the given parameters.
     * 
     * @param version
     * @param updateSequence
     * @param operationsMetadata
     * @param contents
     */
    public CatalogueCapabilities(String version, String updateSequence,
            ServiceIdentification serviceIdentification,
            ServiceProvider serviceProvider,
            OperationsMetadata operationsMetadata, Contents contents,
            FilterCapabilities filterCapabilities) {
        super(version, updateSequence, serviceIdentification, serviceProvider,
                operationsMetadata, contents);
        this.filterCapabilities = filterCapabilities;
    }

    /**
     * Returns the FilterCapabilites section of the capabilities.
     * 
     * @return
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueCapabilities.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
