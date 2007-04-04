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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/

package org.deegree.portal.standard.context.control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCUtils;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.Debug;
import org.deegree.portal.PortalException;

/**
 * This listener generates a list of availavle ViewContexts.
 * The only parameter passed is the user name.Currently only 
 * .xml files are being accepted as contexts and it's also
 * also assumed that those are available under 
 * WEB-INF/xml/users/some_user user directories
 *  
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 */
public class ContextLoadListener extends AbstractContextListener {

    private static String userDir = "WEB-INF/conf/igeoportal/users/";

    /**
     * @see org.deegree.enterprise.control.WebListener#actionPerformed(org.deegree.enterprise.control.FormEvent)
     */
    public void actionPerformed( FormEvent event ) {

        Debug.debugMethodBegin( this, "actionPerformed" );

        RPCWebEvent rpc = (RPCWebEvent) event;
        try {
            validate( rpc );
        } catch ( PortalException e ) {
            gotoErrorPage( "Not a valid RPC for ContextLoad\n" + e.getMessage() );
            return;
        }

        String userName = null;
        try {
            userName = extractUserName( rpc );
        } catch ( Exception e ) {
            gotoErrorPage( "Couldn't get user name from session ID\n" + e.getMessage() );
            return;
        }
        List contextList = null;
        try {
            contextList = getContextList( userName );
        } catch ( Exception e ) {
            gotoErrorPage( "List of available context documents\n" + e.getMessage() );
            return;
        }

        getRequest().setAttribute( "CONTEXT_LIST", contextList );
        getRequest().setAttribute( "USER", userName );

        Debug.debugMethodEnd();
    }

    /**
     * reads the session ID from the passed RPC and gets the assigned
     * user name from a authentification service
     * @param event
     * @return
     * @throws Exception
     */
    private String extractUserName( RPCWebEvent event )
                            throws Exception {
        RPCMethodCall mc = event.getRPCMethodCall();
        RPCParameter[] pars = mc.getParameters();
        RPCStruct struct = (RPCStruct) pars[0].getValue();

        String name = "default";
        try {
            name = getUserName( RPCUtils.getRpcPropertyAsString( struct, "sessionID" ) );
            if ( name == null ) {
                name = "default";
            }
        } catch ( Exception e ) {
        }
        // get map context value
        return name;
    }

    /**
     * returns a list of all available context documents assigned to the
     * passed user
     * 
     * @param userName
     * @return
     * @throws Exception
     */
    private List getContextList( String userName )
                            throws Exception {

        String path2Dir = getHomePath() + userDir + userName;

        File dir = new File( path2Dir );
        File[] files = dir.listFiles();
        List contextList = new ArrayList();
        for ( int i = 0; i < files.length; i++ ) {
            String s = files[i].getName();
            if ( files[i].isFile() && s.endsWith( ".xml" ) ) {
                contextList.add( files[i].getName() );
            }
        }
        return contextList;
    }

    /**
     * validates if the passed RPC call containes the required variables
     *  
     * @param rpc
     * @throws PortalException
     */
    private void validate( RPCWebEvent rpc )
                            throws PortalException {
        RPCMethodCall mc = rpc.getRPCMethodCall();
        RPCParameter param = mc.getParameters()[0];
        RPCStruct struct = (RPCStruct) param.getValue();
        RPCMember sessionID = struct.getMember( "sessionID" );
        if ( sessionID == null ) {
            throw new PortalException( "missing parameter 'sessionID' in RPC for ContextLoad" );
        }
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ContextLoadListener.java,v $
 Revision 1.7  2006/10/17 20:31:18  poth
 *** empty log message ***

 Revision 1.6  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.5  2006/08/24 16:57:43  poth
 code formating

 Revision 1.4  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */
