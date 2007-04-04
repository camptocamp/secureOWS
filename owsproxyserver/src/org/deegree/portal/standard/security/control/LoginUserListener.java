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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.UnknownException;
import org.deegree.security.drm.WrongCredentialsException;
import org.deegree.security.drm.model.User;

/**
 * This <code>Listener</code> reacts on RPC-LoginUser events, extracts the
 * submitted username + password and tries to authenticate the user against
 * the rights management subsystem.
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class LoginUserListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {
        
        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            if (params.length != 2) {
                throw new RPCException(
                        "Invalid RPC. Exactly two 'param'-elements below 'params' are required.");
            }
            if (params[0].getType() != String.class
                    || params[1].getType() != String.class) {
                throw new RPCException(
                        "Invalid RPC. 'param'-elements below 'params' must contain strings.");
            }
            String userName = (String) params[0].getValue();
            String password = (String) params[1].getValue();
            
            // login user to SecurityAccessManager
            SecurityAccessManager manager = SecurityAccessManager.getInstance();
            User user = manager.getUserByName(userName);
            user.authenticate(password);

            // set USERNAME and PASSWORD in HttpSession
            HttpSession session = ((HttpServletRequest) getRequest()).getSession(true);
            session.setAttribute(ClientHelper.KEY_USERNAME, userName);
            session.setAttribute(ClientHelper.KEY_PASSWORD, password);
        } catch (UnknownException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute("MESSAGE", "Benutzername / Passwort inkorrekt.");
            setNextPage("index.jsp");
        } catch (WrongCredentialsException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute("MESSAGE", "Benutzername / Passwort inkorrekt.");
            setNextPage("index.jsp");
        } catch (Exception e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                            "Der Login konnte aufgrund eines internen Fehlers nicht korrekt durchgeführt werden:<br><br><code>"
                                    + e.getMessage() + "</code>");
            setNextPage("index.jsp");
            e.printStackTrace();
        }

    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log $
Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
