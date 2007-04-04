// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/wms/protocol/WMSProtocolFactory.java,v
// 1.7 2004/07/12 06:12:11 ap Exp $
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
package org.deegree.ogcwebservices.wms.operation;

import org.deegree.framework.util.Debug;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationType;
import org.w3c.dom.Document;

/**
 * Factory that builds the different types of WMS-Requests & Responses.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:wanhoff@uni-bonn.de">Jeronimo Wanhoff </a>
 * @version $Revision: 1.10 $ $Date: 2006/11/27 09:07:52 $
 */
public class WMSProtocolFactory {

    
    /**
     * creates an instance of a <tt>WMSGetCapabilitiesResult</tt> object
     * 
     * @param request
     *            request that lead to the response
     * @param exception
     *            exception if one occuered
     * @param capabilities
     *            WMS capabilities
     * 
     * @return <tt>WMSGetCapabilitiesResult</tt>
     */
    public static WMSGetCapabilitiesResult createGetCapabilitiesResponse(
            OGCWebServiceRequest request, OGCWebServiceException exception,
            WMSConfigurationType capabilities) {

        WMSGetCapabilitiesResult res = null;
        if (exception != null) {
            res = new WMSGetCapabilitiesResult(request, exception);
        } else {
            res = new WMSGetCapabilitiesResult(request, capabilities);
        }

        return res;
    }
    
    /**
     * creates a <tt>WFSGetMapResponse</tt> object
     * 
     * @param request
     *            a copy of the request that leads to this response
     * @param exception
     *            a describtion of an excetion (only if raised)
     * @param response
     *            the response to the request
     * @return the result
     */
    public static GetMapResult createGetMapResponse( OGCWebServiceRequest request, 
                                                     OGCWebServiceException exception,
                                                     Object response) {

        GetMapResult res = null;
        if (exception != null) {
            res = new GetMapResult(request, exception);
        } else {
            res = new GetMapResult(request, response);
        }

        return res;
    }


    /**
     * creates a <tt>WFSGetFeatureInfoResponse</tt> object
     * 
     * @param request
     *            a copy of the request that leads to this response
     * @param exception
     *            a describtion of an excetion (only if raised)
     * @param featureInfo
     * @return the result object
     */
    public static GetFeatureInfoResult createGetFeatureInfoResponse(
            OGCWebServiceRequest request, OGCWebServiceException exception,
            String featureInfo) {

        GetFeatureInfoResult res = null;
        if (exception != null) {
            res = new GetFeatureInfoResult(request, exception);
        } else {
            res = new GetFeatureInfoResult(request, featureInfo);
        }

        return res;
    }
  
    /**
     * @param request
     * @param legendGraphic
     * @return the result object
     */
    public static GetLegendGraphicResult createGetLegendGraphicResponse(
            OGCWebServiceRequest request, Object legendGraphic) {
        return new GetLegendGraphicResult(request, legendGraphic);
    }

    /**
     * @param request
     * @param exception
     * @return the result object
     */
    public static GetLegendGraphicResult createGetLegendGraphicResponse(
            AbstractOGCWebServiceRequest request, Document exception) {
        return new GetLegendGraphicResult(request, exception);
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * WMSProtocolFactory.java,v $ Revision 1.7 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.6 2004/07/05 13:42:38 mschneider Changed deegreeParam to
 * deegreeParams wherever it is used.
 * 
 * Revision 1.5 2004/06/30 15:16:05 mschneider Refactoring of XMLTools.
 * 
 * Revision 1.4 2004/06/28 06:27:05 ap no message
 * 
 * Revision 1.3 2004/06/24 06:22:22 ap no message
 * 
 * Revision 1.2 2004/06/22 13:25:14 ap no message
 * 
 * Revision 1.1 2004/06/21 06:43:29 ap no message
 * 
 * Revision 1.4 2004/06/11 07:27:38 ap no message
 * 
 * Revision 1.3 2004/06/04 14:01:50 tf no message
 * 
 * Revision 1.2 2004/06/04 13:01:06 tf no message
 * 
 * Revision 1.1 2004/05/24 07:09:21 ap no message
 * 
 * Revision 1.59 2004/05/06 12:01:27 poth no message
 * 
 * Revision 1.58 2004/04/14 09:53:10 poth no message
 * 
 * Revision 1.57 2004/04/07 06:43:51 poth no message
 * 
 * Revision 1.56 2004/04/02 06:41:56 poth no message
 * 
 * Revision 1.55 2004/03/31 15:40:20 poth no message
 * 
 * Revision 1.54 2004/03/31 07:12:07 poth no message
 * 
 * Revision 1.53 2004/03/30 07:09:33 poth no message
 * 
 * Revision 1.52 2004/03/26 11:19:32 poth no message
 * 
 * Revision 1.51 2004/03/24 12:36:22 poth no message
 * 
 * 
 *  
 ******************************************************************************/