/*
 * ---------------- FILE HEADER ------------------------------------------
 * 
 * This file is part of deegree. Copyright (C) 2001-2006 by: EXSE, Department of
 * Geography, University of Bonn http://www.giub.uni-bonn.de/deegree/ lat/lon
 * Fitzke/Fretter/Poth GbR http://www.lat-lon.de
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * Andreas Poth lat/lon GmbH Aennchenstr. 19 53115
 * Bonn Germany E-Mail: poth@lat-lon.de
 * 
 * Prof. Dr. Klaus Greve Department of Geography University of Bonn Meckenheimer Allee 166
 * 53115 Bonn Germany E-Mail: greve@giub.uni-bonn.de
 * 
 * 
 * ---------------------------------------------------------------------------
 */

package org.deegree.model.metadata.iso19115;

import java.io.Serializable;

/**
 * iso 19115 OnlineResource
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/07/12 16:57:22 $
 *
 * @since 2.0
 */
public class OnlineResource implements Serializable {

    private String applicationprofile = null;
    private FunctionCode functioncode = null;
    private Linkage linkage = null;
    private String onlineresourcedescription = null;
    private String onlineresourcename = null;
    private String protocol = null;
    
    /**
     * @param linkage
     */
    public OnlineResource(Linkage linkage) {
        this(null, null, linkage, null, null, linkage.getHref().getProtocol());
        
    }
    /** 
     * Creates a new instance of OnLineResource
     * 
     * @param applicationprofile
     * @param functioncode
     * @param linkage
     * @param onlineresourcedescription
     * @param onlineresourcename
     * @param protocol
     */
    public OnlineResource(String applicationprofile, FunctionCode functioncode,
            Linkage linkage, String onlineresourcedescription,
            String onlineresourcename, String protocol) {

        this.applicationprofile = applicationprofile;
        this.functioncode = functioncode;
        this.linkage = linkage;
        this.onlineresourcedescription = onlineresourcedescription;
        this.onlineresourcename = onlineresourcename;
        this.protocol = protocol;
    }

    /**
     * minOccurs="0"
     * @return  
     */
    public String getApplicationProfile() {
        return applicationprofile;
    }

    /**
     * minOccurs="0"
     * @return  
     */
    public FunctionCode getFunctionCode() {
        return functioncode;
    }
   
    /**
     * @return
     * 
     * @uml.property name="linkage"
     */
    public Linkage getLinkage() {
        return linkage;
    }

    /**
     * minOccurs="0"
     * @return  
     */
    public String getOnlineResourceDescription() {
        return onlineresourcedescription;
    }

    /**
     * minOccurs="0"
     * @return  
     */
    public String getOnlineResourceName() {
        return onlineresourcename;
    }

  
    /**
     * minOccurs="0"
     * @return
     * 
     * @uml.property name="protocol"
     */
    public String getProtocol() {
        return protocol;
    }


    /**
     * to String method
     */
    public String toString() {
        String ret = null;
        ret = "applicationprofile = " + applicationprofile + "\n";
        ret += "functioncode = " + functioncode + "\n";
        ret += "linkage = " + linkage + "\n";
        ret += "onlineresourcedescription = " + onlineresourcedescription
                + "\n";
        ret += "onlineresourcename = " + onlineresourcename + "\n";
        ret += "protocol = " + protocol;
        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OnlineResource.java,v $
Revision 1.1  2006/07/12 16:57:22  poth
mutator methods removed

Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
