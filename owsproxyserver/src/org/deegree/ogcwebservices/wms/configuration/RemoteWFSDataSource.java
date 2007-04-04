//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/RemoteWFSDataSource.java,v 1.16 2006/09/08 08:42:01 schmitz Exp $
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
package org.deegree.ogcwebservices.wms.configuration;

import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.RemoteWFService;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;


/**
 * Data source description for a LOCALWFS datasource 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision: 1.16 $, $Date: 2006/09/08 08:42:01 $
 */
public class RemoteWFSDataSource extends LocalWFSDataSource {
        
   
    /**
     * Creates a new DataSource object.
     * 
     * @param querable
     * @param failOnException
     * @param name
     * @param type
     * @param geometryProperty
     * @param ows
     * @param capabilitiesURL
     * @param scaleHint
     * @param validArea 
     * @param query
     * @param featureInfoTransform 
     * @param reqTimeLimit 
     */
    public RemoteWFSDataSource( boolean querable, boolean failOnException, QualifiedName name, 
                               int type, QualifiedName geometryProperty, OGCWebService ows, 
                               URL capabilitiesURL, ScaleHint scaleHint, Geometry validArea, 
                               Query query, URL featureInfoTransform, int reqTimeLimit) {
        super( querable, failOnException, name, type, geometryProperty, ows, capabilitiesURL, 
               scaleHint, validArea, query, featureInfoTransform, reqTimeLimit );
    }
    
    /**
     * returns an instance of the <tt>OGCWebService</tt> that represents the
     * datasource. Notice: if more than one layer uses data that are offered by
     * the same OWS the deegree WMS shall just use one instance for accessing
     * the OWS
     * @throws OGCWebServiceException 
     *  
     */
    @Override
    public OGCWebService getOGCWebService() throws OGCWebServiceException {
        return new RemoteWFService( (WFSCapabilities)( (WFService) ows ).getCapabilities() );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RemoteWFSDataSource.java,v $
Revision 1.16  2006/09/08 08:42:01  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.15  2006/07/27 13:08:46  poth
support for request time limit added for each datasource added

Revision 1.14  2006/07/11 14:08:37  schmitz
Fixed some documentation warnings.

Revision 1.13  2006/04/25 19:28:52  poth
*** empty log message ***

Revision 1.12  2006/04/25 06:50:27  poth
*** empty log message ***

Revision 1.11  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.10  2006/04/04 20:39:41  poth
*** empty log message ***

Revision 1.9  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.8  2005/12/21 17:30:10  poth
no message

Revision 1.7  2005/11/22 17:19:13  poth
no message

Revision 1.6  2005/08/26 21:13:02  poth
no message

Revision 1.5  2005/08/09 15:48:24  poth
no message

Revision 1.4  2005/08/05 09:42:20  poth
no message

Revision 1.3  2005/08/04 08:56:58  poth
no message

Revision 1.2  2005/07/22 20:51:54  poth
no message

Revision 1.1  2005/06/22 15:33:00  poth
no message



********************************************************************** */