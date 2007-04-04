// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/WCSRequestBase.java,v 1.7 2006/04/25 19:28:52 poth Exp $
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
package org.deegree.ogcwebservices.wcs;

import java.util.HashMap;

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;

/**
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/04/25 19:28:52 $
 *
 * @since 2.0
 */
public class WCSRequestBase extends AbstractOGCWebServiceRequest {

    /**
     * @param request
     * @param version
     * @param id
     */
    public WCSRequestBase(String id, String version)  {        
        super(version, id, null);
    }
    
    /**
     * @param id
     * @param request
     * @param version
     * @param vendorSpecificParameter
     */
    public WCSRequestBase(String id, String version, 
                          HashMap vendorSpecificParameter) {
        super(version, id, vendorSpecificParameter);
    }
    
    /**
     * returns WCS as service name
     */
    public String getServiceName() {
        return "WCS";
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: WCSRequestBase.java,v $
   Revision 1.7  2006/04/25 19:28:52  poth
   *** empty log message ***

   Revision 1.6  2006/04/06 20:25:31  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:28  poth
   *** empty log message ***

   Revision 1.3  2005/04/06 12:02:08  poth
   no message

   Revision 1.2  2005/02/21 11:24:33  poth
   no message

   Revision 1.4  2004/07/12 06:12:11  ap
   no message

   Revision 1.3  2004/06/21 08:05:49  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
