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
package org.deegree.security.owsrequestvalidator.csw;

import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.csw.discovery.DescribeRecord;
import org.deegree.ogcwebservices.csw.discovery.GetRecordById;
import org.deegree.ogcwebservices.csw.discovery.GetRecords;
import org.deegree.ogcwebservices.csw.manager.Transaction;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.OWSValidator;
import org.deegree.security.owsrequestvalidator.Policy;


/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.3 $, $Date: 2006/10/04 10:21:29 $ *  * @since 1.1
 */

public class CSWValidator extends OWSValidator {

    private static final String MS_INVALIDREQUEST = Messages.getString("CSW_INVALIDREQUEST"); 

    private static CSWValidator self = null;
    private GetRecordsRequestValidator getRecordValidator;
    //private DescribeRecordTypeRequestValidator describeRecordTypeValidator;
    private TransactionValidator transactionValidator;
    private GetRecordByIdRequestValidator byIdValidator;

    /**
     * @param getFeatureInfoValidator
     * @param describeFeatureTypeValidator
     */
    public CSWValidator(Policy policy, String proxyURL) {
        super(policy, proxyURL);
        this.getRecordValidator = new GetRecordsRequestValidator(policy);
        //this.describeRecordTypeValidator = new DescribeRecordTypeRequestValidator( policy );
        this.transactionValidator = new TransactionValidator( policy );
        this.byIdValidator = new GetRecordByIdRequestValidator( policy );
    }

    /**
     * returns an instance of <tt>WFSPolicyValidator</tt> --> singleton
     * <p>
     * before this method cann be called, WFSPolicyValidator.create(URL) must be called to
     * intialize the <tt>WFSPolicyValidator</tt> otherwise this method returns
     * <tt>null</tt>
     * 
     * @return
     */
    public static CSWValidator getInstance() {
        return self;
    }

    /**
     * validates the passed <tt>OGCWebServiceRequest</tt> if it is valid against the
     * defined conditions for WFS requests
     * 
     * @param reqName
     * @throws InvalidParameterValueException
     */
    public void validateRequest(OGCWebServiceRequest request, User user)
                                    throws InvalidParameterValueException,
                                            UnauthorizedException {
        
        if ( request instanceof GetCapabilities ) { 
            getCapabilitiesValidator.validateRequest( request, user );
        } else if ( request instanceof GetRecords ) { 
            getRecordValidator.validateRequest( request, user );
        } else if ( request instanceof GetRecordById ) { 
            byIdValidator.validateRequest( request, user );
        } else if ( request instanceof DescribeRecord ) { 
            
        } else if ( request instanceof Transaction ) { 
            transactionValidator.validateRequest( request, user );
        } else {
            throw new InvalidParameterValueException(MS_INVALIDREQUEST + request.getClass().getName() );
        }
    }

    /**
     * @see org.deegree_impl.security.OWSPolicyValidator#validateResponse(java.lang.Object,
     *      java.lang.String)
     */
    public byte[] validateResponse(OGCWebServiceRequest request, byte[] response,
                                   String mime, User user) 
    							   throws InvalidParameterValueException, 
                                          UnauthorizedException {
        
        if ( request instanceof GetCapabilities ) { 
            response = getCapabilitiesValidatorR.validateResponse( "CSW", response, mime, user ); 
        }
        // TODO responses to other requests 
        return response;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWValidator.java,v $
Revision 1.3  2006/10/04 10:21:29  poth
bug fix - handling GetRecordById

Revision 1.2  2006/08/03 07:37:44  poth
changes required because of renaming GetRecordRequestValidator to GetRecordsRequestValidator

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.2  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
