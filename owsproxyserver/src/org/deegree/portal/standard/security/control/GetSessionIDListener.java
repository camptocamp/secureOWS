/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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
package org.deegree.portal.standard.security.control;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;

/**
 * Listener to retrieve the (deegree managed) sessionID of a user. 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.7 $, $Date: 2006/08/29 19:54:14 $
 * 
 * @since 1.1
 */
public class GetSessionIDListener extends AbstractListener {
        
    private static String userDir = "WEB-INF/conf/igeoportal/users/"; 
    
    /** 
     * performs a login request. the passed event contains a RPC method call
     * containing user name and password. 
     */
    public void actionPerformed(FormEvent event) {   
        
        RPCWebEvent re = (RPCWebEvent)event;
        
        if ( !validateRequest( re ) ) {
            gotoErrorPage( Messages.getString("GetSessionIDListener.1") ); 
            return;
        }
        
        try {
            // write request parameter into session to reconstruct the search form
            HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );        
            getRequest().setAttribute( "SESSIONID", session.getAttribute( "SESSIONID" ) );
            getRequest().setAttribute( "STARTCONTEXT", getUsersStartContext( re ) ); 
        } catch (IOException e) {
            e.printStackTrace();
            gotoErrorPage( Messages.getString("GetSessionIDListener.5") ); 
        }
        
    }
    
    /**
     * validates the passed event to be valid agaist the requirements of the 
     * listener (contains user name )
     * 
     * @param event
     * @return
     */
    private boolean validateRequest(RPCWebEvent event) {
        RPCMethodCall mc = event.getRPCMethodCall();
        if ( mc.getParameters().length == 0 ) {
            return false;
        }
        RPCStruct struct = (RPCStruct)mc.getParameters()[0].getValue();
        if ( struct.getMember("user") == null ) { 
            return false;
        }
        
        return true;
    }
    
    /**
     * returns the name of the users start context. If the user does not 
     * own an individual start context the name of the default start context 
     * for all users will be returned.
     * @param event
     * @return
     */
    private String getUsersStartContext(RPCWebEvent event) throws IOException {
        RPCMethodCall mc = event.getRPCMethodCall();
        RPCStruct struct = (RPCStruct)mc.getParameters()[0].getValue();
        String userName = (String)struct.getMember("user").getValue(); 
        StringBuffer sb = new StringBuffer( 300 );
        sb.append( getHomePath() ).append( userDir  ).append( userName );
        sb.append( "/context.properties" ); 
        File file = new File( sb.toString() );        
        if ( !file.exists() ) {            
            sb.delete( 0, sb.length() );
            sb.append( getHomePath() ).append( userDir  ).append( "context.properties" ); 
            file = new File( sb.toString() );
        } 
        Properties prop = new Properties();
        InputStream is = file.toURL().openStream();
        prop.load( is );
        is.close();
        StringBuffer dir = new StringBuffer( "users/" ).append( userName ).append( '/' ); 
        return dir.append( prop.getProperty( "STARTCONTEXT" ) ).toString(); 
    }
 
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetSessionIDListener.java,v $
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/08/29 12:06:18  poth
*** empty log message ***

Revision 1.5  2006/08/28 15:26:41  poth
comments corrected

Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
