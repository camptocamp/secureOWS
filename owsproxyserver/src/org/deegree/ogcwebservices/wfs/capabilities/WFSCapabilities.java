//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/WFSCapabilities.java,v 1.15 2006/11/09 17:46:54 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.capabilities;

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
 * Represents the capabilities for an OGC-WFS 1.1.0 compliant service instance.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.15 $, $Date: 2006/11/09 17:46:54 $
 */
public class WFSCapabilities extends OWSCommonCapabilities {

    private static final long serialVersionUID = -8126209663124432256L;

    private FeatureTypeList featureTypeList;

    private GMLObject[] servesGMLObjectTypeList;

    private GMLObject[] supportsGMLObjectTypeList;

    private FilterCapabilities filterCapabilities;

    /**
     * Creates WFSCapabilities from a URL.
     * 
     * @param url
     *            location of the capabilities file
     * @return catalog capabilities
     * @throws IOException 
     * @throws SAXException 
     * @throws InvalidCapabilitiesException 
     */
    public static OGCCapabilities createCapabilities(URL url)
            throws IOException, SAXException, InvalidCapabilitiesException {
        OGCCapabilities capabilities = null;
        WFSCapabilitiesDocument capabilitiesDoc = new WFSCapabilitiesDocument();
        capabilitiesDoc.load(url);
        capabilities = capabilitiesDoc.parseCapabilities();
        return capabilities;
    }

    /**
     * Generates a new WFSCapabilities instance from the given parameters.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param featureTypeList
     * @param servesGMLObjectTypeList
     * @param supportsGMLObjectTypeList
     * @param contents
     *            TODO field not verified! Check spec.
     * @param filterCapabilities
     */
    public WFSCapabilities(String version, String updateSequence,
            ServiceIdentification serviceIdentification,
            ServiceProvider serviceProvider,
            OperationsMetadata operationsMetadata,
            FeatureTypeList featureTypeList,
            GMLObject[] servesGMLObjectTypeList,
            GMLObject[] supportsGMLObjectTypeList, Contents contents,
            FilterCapabilities filterCapabilities) {
        super(version, updateSequence, serviceIdentification, serviceProvider,
                operationsMetadata, contents);
        this.featureTypeList = featureTypeList;
        this.servesGMLObjectTypeList = servesGMLObjectTypeList;
        this.supportsGMLObjectTypeList = supportsGMLObjectTypeList;
        this.filterCapabilities = filterCapabilities;
    }

    /**
     * Returns the FilterCapabilites section of the capabilities.
     * 
     * @return
     * 
     */
    public FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * @return Returns the featureTypeList.
     */
    public FeatureTypeList getFeatureTypeList() {
        return this.featureTypeList;
    }

    /**
     * @param featureTypeList
     *            The featureTypeList to set.
     */
    public void setFeatureTypeList(FeatureTypeList featureTypeList) {
        this.featureTypeList = featureTypeList;
    }
   
    /**
     * @return Returns the servesGMLObjectTypeList.
     */
    public GMLObject[] getServesGMLObjectTypeList() {
        return servesGMLObjectTypeList;
    }

    /**
     * @param servesGMLObjectTypeList
     *            The servesGMLObjectTypeList to set.
     */
    public void setServesGMLObjectTypeList(
            GMLObject[] servesGMLObjectTypeList) {
        this.servesGMLObjectTypeList = servesGMLObjectTypeList;
    }

    /**
     * @return Returns the supportsGMLObjectTypeList.
     */
    public GMLObject[] getSupportsGMLObjectTypeList() {
        return supportsGMLObjectTypeList;
    }

    /**
     * @param supportsGMLObjectTypeList
     *            The supportsGMLObjectTypeList to set.
     */
    public void setSupportsGMLObjectTypeList(
            GMLObject[] supportsGMLObjectTypeList) {
        this.supportsGMLObjectTypeList = supportsGMLObjectTypeList;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WFSCapabilities.java,v $
Revision 1.15  2006/11/09 17:46:54  mschneider
Fixed formatting.

Revision 1.14  2006/10/11 18:00:47  mschneider
Javadoc fixes.

Revision 1.13  2006/09/07 08:09:34  poth
comment corrected

Revision 1.12  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */