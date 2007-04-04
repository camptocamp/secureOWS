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

import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;

/**
 *
 * @author Katharina Lupp <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.7 $ $Date: 2006/07/28 08:01:27 $
 */
public class GetFeatureInfoResult extends DefaultOGCWebServiceResponse {
    
    private String featureInfo;
    
    /**
     * constructor initializing the class with the &lt;WMSFeatureInfoResponse&gt;
     */
     GetFeatureInfoResult( OGCWebServiceRequest request, OGCWebServiceException exception) {
         super( request, exception );
         setFeatureInfo( featureInfo );
     }

    /**
     * constructor initializing the class with the &lt;WMSFeatureInfoResponse&gt;
     */
    GetFeatureInfoResult( OGCWebServiceRequest request, String featureInfo ) {
        super( request );
        setFeatureInfo( featureInfo );
    }

    /**
     * @return the feature info the corresponds to an feature info request. The
     * format of the result is determined by the <tt>INFO_FORMAT</tt> parameter
     * of the request. If an excption raised during the processing of the request
     * or the request has been invald <tt>null</tt> will be returned.
     */
    public String getFeatureInfo() {
        return featureInfo;
    }

    /**
     * sets the feature info the corresponds to an feature info request.
     * @param featureInfo 
     */
    public void setFeatureInfo( String featureInfo ) {
        this.featureInfo = featureInfo;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetFeatureInfoResult.java,v $
Revision 1.7  2006/07/28 08:01:27  schmitz
Updated the WMS for 1.1.1 compliance.
Fixed some documentation.

Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
