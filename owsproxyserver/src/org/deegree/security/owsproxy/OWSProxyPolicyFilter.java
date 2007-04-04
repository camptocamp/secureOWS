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
package org.deegree.security.owsproxy;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.capabilities.CatalogueGetCapabilities;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecord;
import org.deegree.ogcwebservices.csw.discovery.GetRecordById;
import org.deegree.ogcwebservices.csw.discovery.GetRecords;
import org.deegree.ogcwebservices.wcs.describecoverage.DescribeCoverage;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSGetCapabilities;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetStyles;
import org.deegree.ogcwebservices.wms.operation.PutStyles;
import org.deegree.ogcwebservices.wms.operation.WMSGetCapabilities;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsrequestvalidator.GeneralPolicyValidator;
import org.deegree.security.owsrequestvalidator.OWSValidator;
import org.deegree.security.owsrequestvalidator.csw.CSWValidator;
import org.deegree.security.owsrequestvalidator.wfs.WFSValidator;
import org.deegree.security.owsrequestvalidator.wms.WMSValidator;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.19 $, $Date: 2006/10/22 20:19:52 $
 * 
 * @since 1.1
 *  
 */
class OWSProxyPolicyFilter {
    
    private Map validators = null;
    
    /**
     * if this constructor is used the OWSProxyPolicyFilter
     * doesn't constain a Validator. Validators must be set using the
     * addValidator(OWSPolicyValidator) method   
     */
    OWSProxyPolicyFilter() {
        validators = new HashMap();
    }
    
    /**
     * @param owsValidators
     */
    OWSProxyPolicyFilter(OWSValidator[] owsValidators) {
        this();
        for (int i = 0; i < owsValidators.length; i++) {
            addValidator( owsValidators[i] );
        }
    }
    
    /**
     * adds a <tt>OWSPolicyValidator</tt> to the <tt>OWSProxyPolicyFilter</tt>
     * @param validator
     */
    public void addValidator(OWSValidator validator) {
        if ( validator instanceof WMSValidator ) {
            validators.put("WMS", validator);
        } else if ( validator instanceof WFSValidator ) {
            validators.put("WFS", validator);
        } else if ( validator instanceof CSWValidator ) {
            validators.put("CSW", validator);
        }           
    }
    
    /**
     * validate the passed <tt>OGCWebServiceRequest</tt> againsted the Policy
     * encapsulated by the <tt>OWSProxyPolicyFilter</tt> 
     * 
     * @param request
     * @param length length (characters) of the request
     * @param user
     */
    public void validateGeneralConditions(HttpServletRequest request, int length, User user) 
    								throws InvalidParameterValueException {

        Object o = validators.keySet().iterator().next();
        OWSValidator validator = (OWSValidator)validators.get(o);
        // create GeneralPolicyValidatora and perform validation of 
        // general request parameters
        GeneralPolicyValidator gpValidator = 
            new GeneralPolicyValidator( validator.getGeneralCondtion() );
        validateGeneralConditions( gpValidator, request,length, user);
    }

    /**
     * validate the passed <tt>OGCWebServiceRequest</tt> againsted the Policy
     * encapsulated by the <tt>OWSProxyPolicyFilter</tt> 
     * 
     * @param request
     * @param user
     */
    public void validate(OGCWebServiceRequest request, User user) throws  
    											InvalidParameterValueException, 
                                                UnauthorizedException {

        String service = getService( request );
        // get validator assigned to the requested service
        OWSValidator validator = (OWSValidator)validators.get( service );
        if ( validator == null ) {
            throw new InvalidParameterValueException( "No Validator registered for service: " + service );
        }                
        // validate the OWS request
        validator.validateRequest( request, user );

    }
    
    /**
     * validates the general conditions of a Http request. validated are:
     * <ul>
     *  <li>content length
     *  <li>request method
     *  <li>header fields
     * </ul>
     * @param gpValidator
     * @param request
     * @param length length (characters) of the request
     * @param user
     * @throws InvalidParameterValueException
     */
    private void validateGeneralConditions(GeneralPolicyValidator gpValidator,
                                           HttpServletRequest request, 
                                           int length, User user) 
    				throws InvalidParameterValueException {
        
        gpValidator.validateRequestMethod( request.getMethod() );
        if ( request.getContentLength() > 0) length = request.getContentLength();
        if ( request.getMethod().equalsIgnoreCase("GET") ) {            
            gpValidator.validateGetContentLength( length );
        } else {
            gpValidator.validatePostContentLength( length );
        }
        Enumeration iterator = request.getHeaderNames();
        Map header = new HashMap();
        while ( iterator.hasMoreElements() ) {
            String key = (String)iterator.nextElement();
            Object value = request.getHeaders( key );
            header.put( key, value );
        }
        gpValidator.validateHeader( header );            
    }

    

    /**
     * @param target
     * @param user
     *  
     */
    public byte[] validate(OGCWebServiceRequest request, byte[] data, String mime, 
                           User user) throws InvalidParameterValueException,
                                               UnauthorizedException{
        String service = getService( request );       
        // get validator assigned to the requested service
        OWSValidator validator = (OWSValidator)validators.get( service );
        if ( validator == null ) {
            throw new InvalidParameterValueException( "No Validator registered for " +
            										  "service: " + service );
        }            
        // validate the OWS request
        return validator.validateResponse(request, data, mime, user);
        
    }

    /**
     * determine requested service type
     * @param request
     * @return
     */
    private String getService(OGCWebServiceRequest request) {
        String service = null;
        if ( request instanceof GetMap || 
             request instanceof GetFeatureInfo ||
             request instanceof GetLegendGraphic ||
             request instanceof WMSGetCapabilities ||
             request instanceof GetStyles ||
             request instanceof PutStyles ) {
            service = "WMS";
        } else if ( request instanceof WFSGetCapabilities || 
                    request instanceof GetFeature ||
                    request instanceof Transaction ||
                    request instanceof DescribeFeatureType ) {
            service = "WFS";
        } else if ( request instanceof GetRecordById || 
                    request instanceof GetRecords ||
                    request instanceof CatalogueGetCapabilities ||
                    request instanceof org.deegree.ogcwebservices.csw.manager.Transaction ||
                    request instanceof DescribeRecord ) {
            service = "CSW";
        } else if ( request instanceof GetCoverage || 
                    request instanceof DescribeCoverage ||
                    request instanceof WCSGetCapabilities ) {
            service = "WCS";
        }
        return service;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSProxyPolicyFilter.java,v $
Revision 1.19  2006/10/22 20:19:52  poth
support for vendor specific operation getScaleBar removed

Revision 1.18  2006/08/08 15:48:55  poth
useless parameters removed

Revision 1.17  2006/08/02 14:11:47  poth
support for CSW added

Revision 1.16  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.15  2006/07/03 15:36:17  poth
bug fix - handling case where a request has no content (avoid NPE) / correction of comments

Revision 1.14  2006/05/23 09:31:48  poth
bug fix service determination

********************************************************************** */