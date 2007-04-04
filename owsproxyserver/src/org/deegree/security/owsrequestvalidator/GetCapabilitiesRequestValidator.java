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
package org.deegree.security.owsrequestvalidator;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueGetCapabilities;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilities;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.Request;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.16 $, $Date: 2006/08/10 07:17:52 $
 * 
 * @since 1.1
 */
public class GetCapabilitiesRequestValidator extends RequestValidator {
    
    // known condition parameter
    private static final String UPDATESEQUENCE = "updateSequence";     
    
    /**
     * @param policy
     */
    public GetCapabilitiesRequestValidator(Policy policy) {
        super(policy);
    }
    
    /** 
     * validates the incomming GetCapabilities request
     * @param request request to validate
     * @param user name of the user who likes to perform the request 
     * 			   (can be null)
     */
    public void validateRequest(OGCWebServiceRequest request, User user)
                    throws InvalidParameterValueException {
        
        String version = null;
        String updateSeq = null;
        Request req = null;
        
        if ( request instanceof WFSGetCapabilities ) {
            version = ((WFSGetCapabilities)request).getVersion();  
            req = policy.getRequest( "WFS", "GetCapabilities" );  
        } else if ( request instanceof WMSGetCapabilities ) {
            version = ((WMSGetCapabilities)request).getVersion();
            updateSeq = ((WMSGetCapabilities)request).getUpdateSequence();
            req = policy.getRequest( "WMS", "GetCapabilities" );  
        } else if ( request instanceof WCSGetCapabilities ) {
            version = ((WCSGetCapabilities)request).getVersion();
            req = policy.getRequest( "WCS", "GetCapabilities" );  
        } else if ( request instanceof CatalogueGetCapabilities ) {
            version = ((CatalogueGetCapabilities)request).getVersion();
            req = policy.getRequest( "CSW", "GetCapabilities" );  
        }
       
        // request is valid because no restrictions are made
        if ( req.isAny() ) return;
        
        validateVersion( req.getPreConditions(), version );
        validateUpdateSeq( req.getPreConditions(), updateSeq );
        
    }
     
    /**
     * 
     * @param version
     * @param updateSeq
     * @throws InvalidParameterValueException
     */
    private void validateUpdateSeq( Condition condition, String updateSeq ) 
    				throws InvalidParameterValueException {        
//        OperationParameter op = condition.getOperationParameter( UPDATESEQUENCE );
        //version is valid because no restrictions are made
//		  if ( op.isAll() ) return;
//        List list = op.getValues();
//        if ( op.isUserCoupled() ) {
//            //TODO
//            // get comparator list from security registry
//        }
//        if ( !list.contains(version) ) {
//              String s = Messages.getString("GetCapabilitiesRequestValidator.INVALIDUPDATESEQ");
//            throw new InvalidParameterValueException( s );
//        }
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetCapabilitiesRequestValidator.java,v $
Revision 1.16  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.15  2006/08/02 14:14:06  poth
support for CSW added

Revision 1.14  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.13  2006/05/25 09:53:31  poth
adapated to changed/simplified policy xml-schema


********************************************************************** */