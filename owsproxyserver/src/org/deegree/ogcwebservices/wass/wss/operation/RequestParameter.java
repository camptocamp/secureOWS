//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/wss/operation/RequestParameter.java,v 1.4 2006/08/24 06:42:16 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
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
 E-Mail: jens.fitzke@uni-bonn.de

 ---------------------------------------------------------------------------*/

package org.deegree.ogcwebservices.wass.wss.operation;

/**
 * Encapsulate the requestparameter of a DoServiceRequest.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/08/24 06:42:16 $
 * 
 * @since 2.0
 */

public class RequestParameter {
    private String parameter = null;

    private String id = null;

    /**
     * @param parameter
     * @param id
     */
    public RequestParameter( String parameter, String id ) {
        this.parameter = parameter;
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Returns the parameter.
     */
    public String getParameter() {
        return parameter;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: RequestParameter.java,v $
 * Changes to this class. What the people have been up to: Revision 1.4  2006/08/24 06:42:16  poth
 * Changes to this class. What the people have been up to: File header corrected
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/20 15:31:04  bezema
 * Changes to this class. What the people have been up to: It looks like the completion of wss. was needs further checking in a tomcat environment. The Strings must still be externalized. Logging is done, so is the formatting.
 * Changes to this class. What the people have been up to: Revision
 * 1.2 2006/05/30 10:12:02 bezema Putting the cvs asci option to -kkv which will update the
 * $revision$ $author$ and $date$ variables in a cvs commit
 * 
 * Revision 1.1 2006/05/29 12:00:58 bezema Refactored the security and authentication webservices
 * into one package WASS (Web Authentication -and- Security Services), also created a common package
 * and a saml package which could be updated to work in the future.
 * 
 * Revision 1.1 2006/05/23 15:22:02 bezema Added configuration files to the wss and wss is able to
 * parse a DoService Request in xml
 * 
 * 
 **************************************************************************************************/