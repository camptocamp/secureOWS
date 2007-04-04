// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/WCSDeegreeParams.java,v 1.7 2006/07/12 16:59:32 poth Exp $
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
package org.deegree.ogcwebservices.wcs.configuration;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/07/12 16:59:32 $
 *
 * @since 2.0
 */
public class WCSDeegreeParams extends DeegreeParams {
    
    private List directoryList = new ArrayList();

    /**
     * creates an instance of a class containing the deegree specific
     * global configuration parameters for a WCS
     * 
     * @param defaultOnlineResource URL/URI used a default if not specified
     *                              in the Request section of the capabilities
     * @param cacheSize max size of the used cache
     * @param requestTimeLimit maximum time limit (minutes) of request processing
     * @param directoryList list of directories that are scanned for 
     *                      data-configuration files for the WCS 
     */
    public WCSDeegreeParams(OnlineResource defaultOnlineResource, 
                           int cacheSize, int requestTimeLimit, String[] directoryList) {
        super(defaultOnlineResource, cacheSize, requestTimeLimit);
        setDirectoryList(directoryList);
    }

    /**
     * @return Returns the directoryList.
     * 
     * @uml.property name="directoryList"
     */
    public String[] getDirectoryList() {
        String[] s = new String[directoryList.size()];
        return (String[]) directoryList.toArray(s);
    }

    /**
     * @param directoryList The directoryList to set.
     */
    public void setDirectoryList(String[] directoryList) {
        this.directoryList = Arrays.asList( directoryList );        
    }
    
    /**
     * adds a directory to the directory list
     * @param directory
     */
    public void addDirectory(String directory) {
        directoryList.add( directory );
    }
    
    /**
     * removes a directory from the directory list
     * @param directory
     */
    public void removeDirectory(String directory) {
        directoryList.remove(directory);
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: WCSDeegreeParams.java,v $
   Revision 1.7  2006/07/12 16:59:32  poth
   required adaptions according to renaming of OnLineResource to OnlineResource

   Revision 1.6  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.3  2005/09/05 13:29:09  mschneider
   Refactored due to removal of obsolete element "rootDirectory" from "deegreeParams" section.

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.2  2004/07/06 16:44:25  mschneider
   More work on CatalogConfiguration and CatalogConfigurationDocument.
   This includes the hierarchy of these classes.

   Revision 1.1  2004/07/05 13:42:38  mschneider
   Changed deegreeParam to deegreeParams wherever it is used.

   Revision 1.3  2004/06/23 11:55:40  mschneider
   Changed hierarchy in org.deegree.ogcwebservices.getcapabilities:
   - OGCCommonCapabilities are derived for Capabilities according to the OGCCommon Implementation Specification 0.2
   - OGCStandardCapabilities are derived for Capabilities prior to the OGCCommon Implementation Specification 0.2

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
