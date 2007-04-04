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
package org.deegree.security.owsrequestvalidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.11 $, $Date: 2006/08/14 13:38:05 $
 * 
 * @since 1.1
 */

public class GeneralPolicyValidator {

    // known condition parameter
    private static final String GETCONTENTLENGTH = "getContentLength";

    private static final String POSTCONTENTLENGTH = "postContentLength";

    private static final String HTTPHEADER = "httpHeader";

    private static final String REQUESTTYPE = "requestType";

    // message strings
    // TODO: read from resource bundle
    private static final String contentLengthMESSAGE1 = "contentLength condition isn't defined";

    private static final String contentLengthMESSAGE2 = "contentLength exceeds defined maximum length";

    private Condition generalCondition = null;

    /**
     * @param policy
     */
    public GeneralPolicyValidator( Condition generalCondition ) {
        this.generalCondition = generalCondition;
    }

    /**
     * validates if the passed length of a request content doesn't exceeds
     * the defined maximum length. If the OperationParameter indicates that
     * the condition is coupled to specific user rights, these rights will
     * be read from the rights management system 
     * 
     * @param contentLength
     * 
     */
    public void validateGetContentLength( int contentLength )
                            throws InvalidParameterValueException {

        OperationParameter op = generalCondition.getOperationParameter( GETCONTENTLENGTH );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }
        int compareValue = op.getFirstAsInt();
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( compareValue < contentLength ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 );
        }
    }

    /**
     * validates if the passed length of a request content doesn't exceeds
     * the defined maximum length. If the OperationParameter indicates that
     * the condition is coupled to specific user rights, these rights will
     * be read from the rights management system 
     * 
     * @param length
     * 
     */
    public void validatePostContentLength( int contentLength )
                            throws InvalidParameterValueException {
        OperationParameter op = generalCondition.getOperationParameter( POSTCONTENTLENGTH );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }
        int compareValue = op.getFirstAsInt();
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( compareValue < contentLength ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 + ": " + contentLength );
        }
    }

    /**
     * @param headerFields
     * 
     */
    public void validateHeader( Map headerFields )
                            throws InvalidParameterValueException {
        OperationParameter op = generalCondition.getOperationParameter( HTTPHEADER );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted, not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        //TODO

    }

    /**
     * validates if the current request type (e.g. POST, GET ...) is granted
     * to be performed 
     * 
     * @param type
     * 
     */
    public void validateRequestMethod( String type )
                            throws InvalidParameterValueException {
        OperationParameter op = generalCondition.getOperationParameter( REQUESTTYPE );
        if ( op == null ) {
            // if no policy for a value is defined the condition
            // never will be fullfilled --> rights are granted not limited
            throw new InvalidParameterValueException( contentLengthMESSAGE1 );
        }

        String[] tmp = StringTools.toArray( op.getFirstAsString(), ",", true );
        List compareValue = Arrays.asList( tmp );
        if ( op.isUserCoupled() ) {
            // TODO
            // get compareValue from the rights management system
        }
        if ( !compareValue.contains( type ) ) {
            throw new InvalidParameterValueException( contentLengthMESSAGE2 );
        }
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GeneralPolicyValidator.java,v $
 Revision 1.11  2006/08/14 13:38:05  poth
 code formating

 Revision 1.10  2006/08/08 15:49:15  poth
 useless parameters removed

 Revision 1.9  2006/08/02 09:45:09  poth
 changes required as consequence of changing OperationParameter

 Revision 1.8  2006/07/12 14:46:16  poth
 comment footer added

 ********************************************************************** */
