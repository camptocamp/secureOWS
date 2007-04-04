//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/Constants.java,v 1.2 2006/07/31 11:02:44 mays Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
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

package org.deegree.portal.standard.csw.control;

/**
 * A <code>${type_name}</code> class.<br/>
 * Constants class for the CSW client based on iGeoPortal
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/07/31 11:02:44 $
 * 
 * @since 2.0
 */
public class Constants implements org.deegree.portal.Constants {

    // please insert new constants in alphabetical order. thanks.

    static final String CONF_DATE = "CONF_DATE";

    static final String CONF_GEOGRAPHICBOX = "CONF_GEOGRAPHICBOX";

    static final String CONF_IDENTIFIER = "CONF_IDENTIFIER";

    static final String CONF_KEYWORDS = "CONF_KEYWORDS";

    static final String CONF_SERVICESEARCH = "CONF_SERVICESEARCH";

    static final String CONF_SIMPLESEARCH = "CONF_SIMPLESEARCH";

    static final String CONF_TOPICCATEGORY = "CONF_TOPICCATEGORY";

    static final String CSW_CLIENT_CONFIGURATION = "CSW_CLIENT_CONFIGURATION";

    static final String RPC_DATEFROM = "RPC_DATEFROM";

    static final String RPC_DATETO = "RPC_DATETO";

    static final String RPC_DAY = "RPC_DAY";

    static final String RPC_IDENTIFIER = "RPC_IDENTIFIER";

    static final String RPC_MONTH = "RPC_MONTH";

    static final String RPC_PROTOCOL = "RPC_PROTOCOL"; // SOAP, POST

    static final String RPC_SERVICESEARCH = "RPC_SERVICESEARCH";

    static final String RPC_YEAR = "RPC_YEAR";

    // needs to be public for jsp pages
    public static final String SESSION_SHOPPINGCART = "SESSION_SHOPPINGCART";

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Constants.java,v $
Revision 1.2  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.1  2006/07/31 09:28:17  mays
move Constants to package control

Revision 1.8  2006/06/23 13:39:01  mays
add/update csw files

********************************************************************** */
