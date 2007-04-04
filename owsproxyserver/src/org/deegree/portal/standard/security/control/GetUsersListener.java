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
package org.deegree.portal.standard.security.control;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.model.User;

/**
 * This <code>Listener</code> reacts on RPC-GetUsers events, extracts the
 * submitted letters and passes the users that begin with one of the letters on
 * to the JSP.
 * <p>
 * The internal "SEC_ADMIN" user is sorted out from the USERS parameter.
 * </p>
 * <p>
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * </p>
 * TODO message text must be translated into english and be moved to
 *      a properties file 
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class GetUsersListener extends AbstractListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( GetUsersListener.class );

    public void actionPerformed(FormEvent event) {
       
        try {
            // perform access check
            SecurityAccess access = SecurityHelper.acquireAccess(this);
            SecurityHelper.checkForAdminRole(access);

            String regex = null;

            if (event instanceof RPCWebEvent) {
                RPCWebEvent ev = (RPCWebEvent) event;
                RPCMethodCall rpcCall = ev.getRPCMethodCall();
                RPCParameter[] params = rpcCall.getParameters();
                if (params.length != 1
                        || !(params[0].getValue() instanceof String)) {
                    throw new RPCException(
                            "Invalid RPC. Exactly one param element (containing a regular expression) must be given.");
                }
                regex = (String) params[0].getValue();
            }

            User[] users = access.getAllUsers();
            ArrayList filteredUsers = new ArrayList(1000);
            Pattern pattern = Pattern.compile(regex);

            // include all users which match the submitte regular expression
            for (int i = 0; i < users.length; i++) {
                if (users[i].getID() != User.ID_SEC_ADMIN) {
                    String name = users[i].getName();
                    LOG.logDebug( "Does '" + name + "' match '" + regex + "'? ");
                    if (pattern.matcher(name).matches()) {
                        LOG.logDebug( "Yes." );
                        filteredUsers.add(users[i]);
                    } else {
                        LOG.logDebug( "No." );
                    }
                }
            }

            User[] us = (User[]) filteredUsers.toArray( new User[filteredUsers.size()] );
            getRequest().setAttribute( "USERS", us );
        } catch (PatternSyntaxException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest() .setAttribute( "MESSAGE", "Die Benutzer konnten nicht ermittelt werden, "
                                    + "da der reguläre Ausdruck in Ihrer Anfrage fehlerhaft war.<br><br>"
                                    + "Die Fehlermeldung lautet: <code>"
                                    + e.getMessage() + "</code>");
            setNextPage("error.jsp");
            e.printStackTrace();
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE", "Die Benutzer konnten nicht ermittelt werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE", "Die Benutzer konnten nicht ermittelt werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
            e.printStackTrace();
        }
        
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetUsersListener.java,v $
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
