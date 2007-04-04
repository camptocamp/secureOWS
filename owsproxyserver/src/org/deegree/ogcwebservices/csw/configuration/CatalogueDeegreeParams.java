//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/configuration/CatalogueDeegreeParams.java,v 1.10 2006/07/12 16:59:32 poth Exp $
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
package org.deegree.ogcwebservices.csw.configuration;

import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.enterprise.DeegreeParams;
import org.deegree.io.JDBCConnection;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * Represents the specific <code>deegreeParams</code> section of the configuration for a deegree
 * CSW 2.0 instance. This class encapsulates the deegree CWS specific parameters and inherits the
 * parameters from the <code>DeegreeParams</code> class.
 * <p>
 * It adds the following elements to the common <code>deegreeParams<code>:<table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Mandatory</th>
 * <th>Function</th>
 * </tr>
 * <tr>
 * <td>WFSResource</td>
 * <td align="center">-</td>
 * <td>Resource location of the capabilities of the WFS responsible for data
 * access, default: file:///$RootDirectory$/WEB-INF/xml/wfs_capabilities.xml.
 * </td>
 * </tr>
 * <tr>
 * <td>CatalogAddresses</td>
 * <td align="center">-</td>
 * <td>Addresses of remote catalogs to be used to realize a cascading catalog.
 * </td>
 * </tr>
 * <tr>
 * <td>HarvestRepository</td>
 * <td align="center">-</td>
 * <td>Information concerning services that are harvestable.</td>
 * </tr>
 * </table>
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.10 $, $Date: 2006/07/12 16:59:32 $
 * 
 * @see org.deegree.enterprise.DeegreeParams
 * 
 * @since 2.0
 */

public class CatalogueDeegreeParams extends DeegreeParams {

    private static final long serialVersionUID = -2923926335089417513L;
    
    private SimpleLink wfsResource;
    private OnlineResource[] catalogAddresses;
    private JDBCConnection harvestRepository;
    private String defaultOutputSchema = null;
    private OnlineResource transInXslt = null;
    private OnlineResource transOutXslt = null;

    /**
     * Creates a new CatalogDeegreeParams instance.
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param wfsResource
     * @param catalogAddresses
     * @param harvestRepository
     */
    public CatalogueDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                                int requestTimeLimit, String characterSet, SimpleLink wfsResource,
                                OnlineResource[] catalogAddresses,
                                JDBCConnection harvestRepository,
                                String defaultOutputSchema, OnlineResource trans_in_xslt, 
                                OnlineResource trans_out_xslt) {
        super( defaultOnlineResource, cacheSize, requestTimeLimit, characterSet );
        this.wfsResource = wfsResource;
        this.catalogAddresses = catalogAddresses;
        this.harvestRepository = harvestRepository;
        this.defaultOutputSchema = defaultOutputSchema;
        this.transInXslt = trans_in_xslt;
        this.transOutXslt = trans_out_xslt;
    }

    /**
     * @return Returns the catalogAddresses.
     */
    public OnlineResource[] getCatalogAddresses() {
        return catalogAddresses;
    }

    /**
     * @param catalogAddresses
     *            The catalogAddresses to set.
     */
    public void setCatalogAddresses( OnlineResource[] catalogAddresses ) {
        this.catalogAddresses = catalogAddresses;
    }

    /**
     * @return Returns the harvestRepository.
     */
    public JDBCConnection getHarvestRepository() {
        return harvestRepository;
    }

    /**
     * @param harvestRepository
     *            The harvestRepository to set.
     */
    public void setHarvestRepository( JDBCConnection harvestRepository ) {
        this.harvestRepository = harvestRepository;
    }

    /**
     * @return Returns the wfsResource.
     */
    public SimpleLink getWfsResource() {
        return wfsResource;
    }

    /**
     * @param wfsResource
     *            The wfsResource to set.
     */
    public void setWfsResource( SimpleLink wfsResource ) {
        this.wfsResource = wfsResource;
    }

    /**
     * 
     * @return returns the default output schema 
     */
    public String getDefaultOutputSchema() {
        return defaultOutputSchema;
    }
    
    /**
     * @param defaultOutputSchema default output schema
     */
    public void setDefaultOutputSchema( String defaultOutputSchema ) {
        this.defaultOutputSchema = defaultOutputSchema;
    }
    
    public OnlineResource getTransformationInputXSLT() {
        return transInXslt;
    }
    
    public void setTransformationInputXSLT(OnlineResource xslt) {
        this.transInXslt = xslt;
    }
    
    public OnlineResource getTransformationOutputXSLT() {
        return transOutXslt;
    }
    
    public void setTransformationOutputXSLT(OnlineResource xslt) {
        this.transOutXslt = xslt;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueDeegreeParams.java,v $
Revision 1.10  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
