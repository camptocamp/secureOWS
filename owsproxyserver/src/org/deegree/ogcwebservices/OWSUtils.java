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
package org.deegree.ogcwebservices;

import java.net.URL;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSOperationsMetadata;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.GetFeatureWithLock;
import org.deegree.ogcwebservices.wfs.operation.Lock;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;

/**
 * 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 2.0
 */
public class OWSUtils {
    
    /**
     * according to OGC WMS 1.3 Testsuite a URL to a service operation
     * via HTTPGet must end with '?' or '&'
     * @param href
     * @return the parameter, changed to fulfill the spec
     */
    public static String validateHTTPGetBaseURL(String href) {      
        if ( href.indexOf( '?' ) < 0 ) {
            href = href + '?';
        } else if ( !href.endsWith( "&" ) && !href.endsWith( "?" ) ) {
            href = href + '&';
        }
        return href;
    }
    
    /**
     * the method return the first HTTPGet URL for a Operation within 
     * the pass capabilities document. If No URL can be found (e.g.
     * the service does not support the operation via HTTP Get) 
     * <code>null</code> will be returned
     * 
     * @param capabilities
     * @param clss
     * @return see above
     */
    public synchronized static URL getHTTPGetOperationURL(OGCCapabilities capabilities, Class clss) {
        URL url = null;
        if ( capabilities instanceof WMSCapabilities ) {             
             url = getHTTPGetOperationURL( (WMSCapabilities) capabilities, clss );            
        } else if ( capabilities instanceof WFSCapabilities ) {             
             url = getHTTPGetOperationURL( (WFSCapabilities) capabilities, clss );            
        } else {       
            // TODO
            // support more service types
            // possibly use generic base capabilities to extract it
        }
        return url;
        
    }
        
    
    /**
     * @see #getHTTPGetOperationURL(OGCCapabilities, Class)
     * @param capabilities
     * @param clss
     * @return the first operation URL
     */
    private synchronized static URL getHTTPGetOperationURL(WMSCapabilities capabilities, Class clss) {
        
        OperationsMetadata om = capabilities.getOperationMetadata();
        List<DCP> dcps = null;
        if ( clss.equals( GetMap.class ) ) {
            Operation op = om.getOperation( new QualifiedName( "GetMap" ) );
            if( op == null ) {
                op = om.getOperation( new QualifiedName( "map" ) );
            }
            dcps = op.getDCP();
        } else if ( clss.equals( GetCapabilities.class ) ) {            
            Operation op = om.getOperation( new QualifiedName( "GetCapabilities" ) );
            if( op == null ) {
                op = om.getOperation( new QualifiedName( "capabilities" ) );
            }
            dcps = op.getDCP();
        } else if ( clss.equals( GetFeatureInfo.class ) ) {            
            Operation op = om.getOperation( new QualifiedName( "GetFeatureInfo" ) );
            dcps = op.getDCP();
        } else if ( clss.equals( GetLegendGraphic.class ) ) {            
            Operation op = om.getOperation( new QualifiedName( "GetLegendGraphic" ) );
            dcps = op.getDCP();
        } 

        // search for the first HTTP Get link
        if( dcps == null ) {
            return null;
        }
        for( DCP dcp : dcps ) {
            if( dcp instanceof HTTP ) {
                HTTP http = (HTTP) dcp;
                List<URL> urls = http.getGetOnlineResources();
                if( urls.size() > 0 ) {
                    return urls.get( 0 );
                }
            }
        }
        
        return null;
        
    }
    
    /**
     * @see #getHTTPGetOperationURL(OGCCapabilities, Class)
     * @param capabilities
     * @param clss
     * @return the first operation URL
     */
    private synchronized static URL getHTTPGetOperationURL(WFSCapabilities capabilities, Class clss) {
        
        URL url = null;
        WFSOperationsMetadata om = (WFSOperationsMetadata)capabilities.getOperationsMetadata();
        if ( clss.equals( GetCapabilities.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetCapabilitiesOperation().getDCPs()[0].getProtocol();
            url = http.getGetOnlineResources()[0];    
        } else if ( clss.equals( GetFeature.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetFeature().getDCPs()[0].getProtocol();
            url = http.getGetOnlineResources()[0];    
        } else if ( clss.equals( GetFeatureWithLock.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetFeatureWithLock().getDCPs()[0].getProtocol();
            url = http.getGetOnlineResources()[0];    
        } else if ( clss.equals( Lock.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getLockFeature().getDCPs()[0].getProtocol();
            url = http.getGetOnlineResources()[0];    
        } else if ( clss.equals( Transaction.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getTransaction().getDCPs()[0].getProtocol();
            url = http.getGetOnlineResources()[0];    
        } 
        return url;
    }
    
    /**
     * the method return the first HTTPPost URL for a Operation within 
     * the pass capabilities document. If No URL can be found (e.g.
     * the service does not support the operation via HTTP Get) 
     * <code>null</code> will be returned
     * 
     * @param capabilities
     * @param clss
     * @return
     */
    public synchronized static URL getHTTPPostOperationURL(OGCCapabilities capabilities, Class clss) {
        URL url = null;
        if ( capabilities instanceof WFSCapabilities ) {             
            url = getHTTPPostOperationURL( (WFSCapabilities) capabilities, clss );            
       } else {       
           // TODO
           // support more service types
           // possibly use generic base capabilities to extract it
       }
        return url;
    }
    
    /**
     * @see #getHTTPPostOperationURL(OGCCapabilities, Class)
     * @param capabilities
     * @param clss
     * @return the first operation URL
     */
    private synchronized static URL getHTTPPostOperationURL(WFSCapabilities capabilities, Class clss) {
        
        URL url = null;
        WFSOperationsMetadata om = (WFSOperationsMetadata)capabilities.getOperationsMetadata();
        if ( clss.equals( GetCapabilities.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetCapabilitiesOperation().getDCPs()[0].getProtocol();
            url = http.getPostOnlineResources()[0];    
        } else if ( clss.equals( GetFeature.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetFeature().getDCPs()[0].getProtocol();
            url = http.getPostOnlineResources()[0];    
        } else if ( clss.equals( GetFeatureWithLock.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getGetFeatureWithLock().getDCPs()[0].getProtocol();
            url = http.getPostOnlineResources()[0];    
        } else if ( clss.equals( Lock.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getLockFeature().getDCPs()[0].getProtocol();
            url = http.getPostOnlineResources()[0];    
        } else if ( clss.equals( Transaction.class ) ) {            
            org.deegree.ogcwebservices.getcapabilities.HTTP http =
                (org.deegree.ogcwebservices.getcapabilities.HTTP)
                om.getTransaction().getDCPs()[0].getProtocol();
            url = http.getPostOnlineResources()[0];    
        } 
        return url;
    }


}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSUtils.java,v $
Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.5  2006/09/08 16:25:33  poth
method for getting post addresses from OWS capabilities initialized / two method that do not has to be public set to private

Revision 1.4  2006/08/29 13:02:50  poth
code formating

Revision 1.3  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.2  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.1  2006/08/20 20:51:05  poth
initial check in


********************************************************************** */