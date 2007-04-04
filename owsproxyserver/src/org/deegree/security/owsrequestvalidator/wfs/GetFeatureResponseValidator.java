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
package org.deegree.security.owsrequestvalidator.wfs;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.StringTools;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsrequestvalidator.Policy;
import org.deegree.security.owsrequestvalidator.ResponseValidator;
import org.deegree.security.owsrequestvalidator.wms.GetMapRequestValidator;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.1 $, $Date: 2006/07/23 08:44:53 $
 * 
 * @since 1.1
 *  
 */
class GetFeatureResponseValidator extends ResponseValidator {

    /**
     * @param policy
     */
    public GetFeatureResponseValidator(Policy policy) {
        super(policy);
    }

    /**
     * validates the passed object as a response to a OWS request. The validity of the
     * response may is assigned to specific user rights. If the passed user is <>null
     * this will be evaluated. <br>
     * the reponse may contain three valid kinds of objects:
     * <ul>
     * <li>a xml encoded exception
     * <li>a GML document
     * <li>a XML document
     * <li>a serialized deegree FeatureCollection
     * <li>any other kind of document that is valid against the formats
     *      defined for GetFeature in the capabilities
     * </ul>
     * Each of these types can be identified by the mime-type of the response that is also
     * passed to the method. <br>
     * If something basic went wrong it is possible that not further specified kind of
     * object is passed as response. In this case the method will throw an
     * <tt>InvalidParameterValueException</tt> to avoid sending bad responses to the
     * client.
     * 
     * @param service service which produced the response (WMS, WFS ...)
     * @param response
     * @param mime mime-type of the response
     * @param user
     * @see GetMapRequestValidator#validateRequest(OGCWebServiceRequest, String)
     */
    public byte[] validateResponse(String service, byte[] response, String mime, User user)
                                    throws InvalidParameterValueException {

        Request req = policy.getRequest(service, "GetFeature");
        // request is valid because no restrictions are made
        if (req.isAny()) return response;
        
        Condition condition = req.getPostConditions();

        Object result = null;
        if (MimeTypeMapper.isKnownOGCType(mime)) {
            // if the mime-type isn't an image type but a known
            // OGC mime-type it must be an XML document.
            // probably it is an exception but it also could be 
            // a GML document
            response = validateXML( response, mime, user);
        } else if ( mime.equals("text/xml") ) {
            // if the mime-type isn't an image type but 'text/xml' 
            // it could be an exception
            response = validateXML( response, mime, user);
        } else if ( mime.equals("application/octet-stream") ) {
            response = validateBinaryResult( response, user);
        } else { 
            throw new InvalidParameterValueException( UNKNOWNMIMETYPE + mime );
        }
        
        return response;
    }
    
    
    /**
     * validates the passed byte array to be valid against the policy
     * @param xml
     * @param mime
     * @param user
     * @throws InvalidParameterValueException
     */
    private byte[] validateXML(byte[] xml, String mime, User user)
    								throws InvalidParameterValueException {
        // TODO
        // define useful post-validation for xml-documents
        // at the moment everything is valid
        return xml;
    }
    
    /**
     * validates the passed byte array to be valid against the policy. At the
     * moment just a org.deegree.model.feature.FeatureCollection is a valid
     * response if mime type is application/octet-stream. This may be enhanced
     * in future versions.
     *  
     * @param objects
     * @param user
     * @throws InvalidParameterValueException
     */
    private byte[] validateBinaryResult(byte[] object, User user)
                                    throws InvalidParameterValueException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream( object );
            ObjectInputStream ois = new ObjectInputStream( bis );
            Object obj = ois.readObject();
            
            if ( !(obj instanceof FeatureCollection) ) {
                throw new InvalidParameterValueException("If result mime type for" +
                        "WFS GetFeature request is application/octet-stream the" +
                        "result must be an instance " +
                        "of org.deegree.model.feature.FeatureCollection");
            }
        } catch (Exception e) {
            throw new InvalidParameterValueException( StringTools.stackTraceToString(e) );
        }
        
        return object;
    }    
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetFeatureResponseValidator.java,v $
Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.8  2006/05/25 09:53:30  poth
adapated to changed/simplified policy xml-schema


********************************************************************** */