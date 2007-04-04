//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/configuration/WFSDeegreeParams.java,v 1.9 2006/10/12 16:24:00 mschneider Exp $
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
 Aennchenstraße 19
 53177 Bonn
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
package org.deegree.ogcwebservices.wfs.configuration;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.wfs.WFService;

/**
 * Represents the specific <code>deegreeParams</code> section of the configuration document for a
 * {@link WFService} instance. This class encapsulates the deegree WFS specific parameters and
 * inherits the parameters from the {@link DeegreeParams} class.
 * <p>
 * It adds the following elements to the common <code>deegreeParams<code>:
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Mandatory</th>
 * <th>Function</th>
 * </tr>
 * <tr>
 * <td>DataDirectoryList/td>
 * <td align="center">+</td>
 * <td>List of directories to be scanned for featuretypes/datastores to be served by the WFS.
 * </td>
 * </tr>
 * </table> 
 * 
 * @see org.deegree.enterprise.DeegreeParams
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.9 $, $Date: 2006/10/12 16:24:00 $
 */
public class WFSDeegreeParams extends DeegreeParams {

    private static final long serialVersionUID = 2425998383206611958L;

    private String[] dataDirectories;

    /**
     * Creates a new <code>WFSDeegreeParams</code> instance.
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param dataDirectories
     */
    WFSDeegreeParams(OnlineResource defaultOnlineResource, int cacheSize,
            int requestTimeLimit, String characterSet, String[] dataDirectories) {
        super(defaultOnlineResource, cacheSize,
                requestTimeLimit, characterSet);
        this.dataDirectories = dataDirectories;
    }

    /**
     * Returns the resolved (absolute) data directory paths.
     * 
     * @return the resolved (absolute) data directory paths
     */
    public String[] getDataDirectories() {
        return dataDirectories;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WFSDeegreeParams.java,v $
Revision 1.9  2006/10/12 16:24:00  mschneider
Javadoc + compiler warning fixes.

Revision 1.8  2006/08/24 06:42:17  poth
File header corrected

Revision 1.7  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */