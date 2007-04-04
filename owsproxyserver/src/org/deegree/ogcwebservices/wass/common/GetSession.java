//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/common/GetSession.java,v 1.7 2006/08/09 14:07:50 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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

package org.deegree.ogcwebservices.wass.common;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.w3c.dom.Element;

/**
 * Encapsulated data: GetSession element
 * 
 * Namespace: http://www.gdi-nrw.de/session
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/08/09 14:07:50 $
 * 
 * @since 2.0
 */

public class GetSession extends AbstractRequest {

    private static final long serialVersionUID = 2437405317472796643L;
    
    /**
     * The logger enhances the quality and simplicity of Debugging within the deegree2 framework
     */
    private static final ILogger LOG = LoggerFactory.getLogger( GetSession.class );

    private AuthenticationData authenticationData = null;

    /**
     * Constructs new one from the given values.
     *
     * @param id the request id
     * @param service
     * @param version
     * @param authenticationData
     *
     */
    public GetSession( String id, String service, String version, AuthenticationData authenticationData ) {
        super( id, version, service, "GetSession" );
        this.authenticationData = authenticationData;
    }

    /**
     * Constructs new one from the given key-value-pairs.
     * 
     * @param id the request id
     * @param kvp
     *            the map
     */
    public GetSession( String id, Map<String, String> kvp  ) {
        super( id, kvp );
        URN method = new URN( kvp.get( "AUTHMETHOD" ) );
        String cred = kvp.get( "CREDENTIALS" );
        authenticationData = new AuthenticationData( method, cred );
    }

    /**
     * @return Returns the authenticationData.
     */
    public AuthenticationData getAuthenticationData() {
        return authenticationData;
    }

    /**
     * @param id
     * @param documentElement
     * @return a new instance of this class
     * @throws OGCWebServiceException 
     */
    public static OGCWebServiceRequest create( String id, Element documentElement ) throws OGCWebServiceException {
        try {
            return new SessionOperationsDocument( ).parseGetSession( id, documentElement );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( e.getMessage() );
        }
    }

    /**
     * @param id
     * @param kvp
     * @return a new instance of this class
     */
    public static OGCWebServiceRequest create( String id, Map<String, String> kvp ) {
        return new GetSession( id, kvp );
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GetSession.java,v $
 * Revision 1.7  2006/08/09 14:07:50  poth
 * footer corrected
 *
 * Revision 1.6  2006/06/23 10:23:50  schmitz
 * Completed the WAS, GetSession and CloseSession work.
 * Changes to this class. What the people have been up to:
 * Revision 1.5  2006/06/19 15:34:04  bezema
 * changed wass to handle things the right way
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2006/06/19 12:47:26  schmitz
 * Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this
 * class. What the people have been up to: Revision 1.3 2006/06/13 15:16:18 bezema Changes to this
 * class. What the people have been up to: DoService Test seems to work Changes to this class. What
 * the people have been up to: Revision 1.2
 * 2006/05/30 11:44:51 schmitz Updated the
 * documentation, fixed some warnings. Changes to this class. What the people have been up to:
 * Revision 1.1 2006/05/29 12:00:58 bezema
 * Refactored the security and
 * authentication webservices into one package WASS (Web Authentication -and- Security Services),
 * also created a common package and a saml package which could be updated to work in the future.
 * Changes to this class. What the people
 * have been up to: Revision 1.3 2006/05/26 14:38:32 schmitz Changes to this class. What the people
 * have been up to: Added some KVP constructors to WAS operations. Changes to this class. What the
 * people have been up to: Added some comments, updated the plan. Changes to this class. What the
 * people have been up to: Restructured WAS operations by adding an AbstractRequest base class.
 * Changes to this class. What the people
 * have been up to: Revision 1.2 2006/05/19 15:35:35 schmitz Changes to this class. What the people
 * have been up to: Updated the documentation, added the GetCapabilities operation and implemented a
 * rough WAService outline. Fixed some warnings. Changes to this class. What the people have been up
 * to: Revision 1.1 2006/05/16 14:45:07
 * bezema getsession and close session can
 * now be parses Changes to this class. What the people have been up to:
 * 
 **************************************************************************************************/
