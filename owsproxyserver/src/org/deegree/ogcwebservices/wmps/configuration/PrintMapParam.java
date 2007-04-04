//$ Header: Exp $
/*---------------- FILE HEADER ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Dr. Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
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

//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/configuration/PrintMapParam.java,v 1.7 2006/11/02 21:06:03 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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

 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 Germany
 E-Mail: fitzke@lat-lon.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wmps.configuration;

/**
 * This class is a container to store the print map parameters used to access the (jasper reports)
 * template and output directory parameters.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/11/02 21:06:03 $
 * 
 * @since 2.0
 */

public class PrintMapParam {

    private String format;

    private String templateDirectory;

    private String onlineResource;

    private String plotDirectory;

    private String plotImageDir;

    private String adminMailAddress;

    private String mailHost;
    
    private String mailTextTemplate;
   
    private int targetResolution = 300;

    /**
     * Create a new PrintMapParam instance.
     * 
     * @param format
     * @param templateDirectory
     * @param onlineResource
     * @param plotDirectory
     * @param plotImageDir
     * @param adminMailAddress
     * @param mailHost
     */
    public PrintMapParam( String format, String templateDirectory, String onlineResource,
                         String plotDirectory, String plotImageDir, String adminMailAddress,
                         String mailHost, String mailTextTemplate, int targetResolution) {
        this.format = format;
        this.templateDirectory = templateDirectory;
        this.onlineResource = onlineResource;
        this.plotDirectory = plotDirectory;
        this.plotImageDir = plotImageDir;
        this.adminMailAddress = adminMailAddress;
        this.mailHost = mailHost;
        this.mailTextTemplate = mailTextTemplate;
        this.targetResolution = targetResolution;
    }

    /**
     * @return Returns the format. default: pdf
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * @return Returns the plotDirectory.
     */
    public String getPlotDirectory() {
        return this.plotDirectory;
    }

    /**
     * @return Returns the adminMailAddress.
     */
    public String getAdminMailAddress() {
        return this.adminMailAddress;
    }

    /**
     * @return Returns the mailHost.
     */
    public String getMailHost() {
        return this.mailHost;
    }

    /**
     * @return Returns the plotImgDir.
     */
    public String getPlotImageDir() {
        return this.plotImageDir;
    }

    /**
     * @return Returns the templateDirectory.
     */
    public String getTemplateDirectory() {
        return this.templateDirectory;
    }

    /**
     * @return Returns the onlineResource.
     */
    public String getOnlineResource() {
        return this.onlineResource;
    }

    /**
     * returns the template (text) for creating a mail to inform a user
     * where to access the result of a PrintMap request
     * @return
     */
    public String getMailTextTemplate() {
        return mailTextTemplate;
    }

    /**
     * returns the resolution of the print target in DPI 
     * @return
     */
    public int getTargetResolution() {
        return targetResolution;
    }
    
  
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: PrintMapParam.java,v $
 * Revision 1.7  2006/11/02 21:06:03  poth
 * bug fix - scale calculation
 *
 * Revision 1.6  2006/10/02 06:30:36  poth
 * bug fixes
 * Changes to this class. What the people have been up to:
 * Revision 1.5  2006/08/31 10:29:37  deshmukh
 * renamed variable
 * Changes to
 * this class. What the people have been up to: Revision 1.4 2006/08/24 06:42:17 poth Changes to
 * this class. What the people have been up to: File header corrected Changes to this class. What
 * the people have been up to: Revision 1.3
 * 2006/08/10 07:11:35 deshmukh WMPS has
 * been modified to support the new configuration changes and the excess code not needed has been
 * replaced. Changes to this class. What the
 * people have been up to: Revision 1.2 2006/08/01 14:20:10 deshmukh Changes to this class. What the
 * people have been up to: The wmps configuration has been modified and extended. Also fixed the
 * javadoc. Changes to this class. What the
 * people have been up to: Revision 1.1 2006/07/31 11:21:07 deshmukh Changes to this class. What the
 * people have been up to: wmps implemention... Changes to this class. What the people have been up
 * to:
 **************************************************************************************************/
