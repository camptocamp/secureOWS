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

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.User;

/**
 * This <tt>Listener</tt> reacts on 'EditSubadminRole'-events, extracts the
 * submitted role-id and passes the role + known datasets on to the JSP.
 * <p>
 * Access constraints:
 * <ul>
 * <li>only users that have the 'Administrator'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class EditSubadminRoleListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            if (params.length != 1) {
                throw new RPCException(
                        "Invalid RPC. Exactly one 'param'-element below 'params' is required.");
            }
            if (params[0].getType() != String.class) {
                throw new RPCException(
                        "Invalid RPC. 'param'-element below 'params' must contain a 'string'.");
            }
            int roleId = -1;
            try {
                roleId = Integer.parseInt((String) params[0].getValue());
            } catch (NumberFormatException e) {
                throw new RPCException(
                        "Invalid RPC. Role must be specified by a valid integer value.");
            }

            // get user
            SecurityAccessManager manager = SecurityAccessManager.getInstance();
            User user = manager.getUserByName((String) toModel().get(ClientHelper.KEY_USERNAME));
            SecurityAccess access = manager.acquireAccess(user);

            // perform access check
            ClientHelper.checkForAdminRole(access);
            Role role = access.getRoleById(roleId);

            // supply necessary data for the JSP
            getRequest().setAttribute("TOKEN", access);
            getRequest().setAttribute("ROLE", role);
            getRequest().setAttribute("DATASETS",
                    access.getAllSecuredObjects("dataset"));
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                    "Der Rolleneditor konnte nicht aufgerufen werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("admin/admin_error.jsp");
        } catch (GeneralSecurityException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                    "Der Rolleneditor konnte nicht aufgerufen werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("admin/admin_error.jsp");
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
