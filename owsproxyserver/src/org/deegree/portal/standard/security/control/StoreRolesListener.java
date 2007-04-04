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

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.model.Group;
import org.deegree.security.drm.model.Right;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;

/**
 * This <code>Listener</code> reacts on RPC-StoreRoles events, extracts the
 * submitted role/group relations and updates the
 * <code>SecurityAccessManager</code> accordingly.
 *
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-rol are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class StoreRolesListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {
        
        // contains the data from the RPC, values of the ArrayLists
        // are Integers (one roleId followed by several groupIds; the
        // first value is a String in case of a new role)
        ArrayList[] roles = null;

        SecurityAccessManager manager = null;
        SecurityTransaction transaction = null;

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            roles = new ArrayList[params.length];
            for (int i = 0; i < params.length; i++) {
                ArrayList list = new ArrayList();
                roles[i] = list;
                if (!(params[0].getValue() instanceof RPCStruct)) {
                    throw new RPCException(
                            "Invalid RPC. Param elements must contain 'struct'-values.");
                }
                RPCStruct struct = (RPCStruct) params[i].getValue();

                // extract role-id / role-name
                RPCMember roleId = struct.getMember("roleId");
                RPCMember roleName = struct.getMember("roleName");
                if ((roleId == null && roleName == null)
                        || (roleId != null && roleName != null)) {
                    throw new RPCException(
                            "Invalid RPC. Every role must either have a 'roleId' or a 'roleName'.");
                }
                if (roleId != null) {
                    if (!(roleId.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'roleId' members must be 'string'-values.");
                    }
                    try {
                        list.add(new Integer((String) roleId.getValue()));
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. 'roleId' members must be valid integer values.");
                    }
                } else {
                    if (!(roleName.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'roleName' members must be 'string'-values.");
                    }
                    list.add( roleName.getValue() );
                }

                // extract groups
                RPCMember groups = struct.getMember("groups");
                if (!(groups.getValue() instanceof RPCParameter[])) {
                    throw new RPCException(
                            "Invalid RPC. 'groups'-members must contain an 'array'.");
                }
                RPCParameter[] groupArray = (RPCParameter[]) groups.getValue();
                for (int j = 0; j < groupArray.length; j++) {
                    if (!(groupArray[j].getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. The 'groups' array must contain 'string'-values.");
                    }
                    try {
                        list
                                .add(new Integer((String) groupArray[j]
                                        .getValue()));
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. Values of 'groups' arrays must be valid integer values.");
                    }
                }
            }

			// get Transaction
			manager = SecurityAccessManager.getInstance();
            transaction = SecurityHelper.acquireTransaction (this);
            SecurityHelper.checkForAdminRole(transaction);

            // perform access check (and get admin/subadmin role)
            Role subadminRole = SecurityHelper
                    .checkForAdminOrSubadminRole(transaction);

            // remove deleted roles
            Role[] oldRoles = transaction.getAllRoles();
            for (int i = 0; i < oldRoles.length; i++) {
                if (!oldRoles[i].getName().startsWith("SUBADMIN:")) {
                    boolean deleted = true;
                    for (int j = 0; j < roles.length; j++) {
                        ArrayList list = roles[j];
                        if (list.get(0) instanceof Integer) {
                            if (((Integer) list.get(0)).intValue() == oldRoles[i]
                                    .getID()) {
                                deleted = false;
                            }
                        }
                    }
                    if (deleted) {
                        // deregister Role
                        transaction.deregisterRole(oldRoles[i]);
                    }
                }
            }

            // store all submitted roles (and their groups)
            for (int i = 0; i < roles.length; i++) {
                Role role = null;

                ArrayList list = roles[i];
                if (list.get(0) instanceof Integer) {
                    role = transaction.getRoleById(((Integer) list.get(0))
                            .intValue());

                    // only modify role if editor has the right to grant the
                    // role
                    if (!transaction.getUser().hasRight(transaction, "grant", role)) {
                        continue;
                    }
                } else {
                    // only add role if editor has the privilege to do so
                    if (transaction.getUser().hasPrivilege(transaction, "addrole")) {
                        role = transaction.registerRole((String) list.get(0));
                        if (subadminRole.getID() != Role.ID_SEC_ADMIN) {
                            transaction.setRights(role, subadminRole,
                                    new Right[] {
                                    	new Right (role, RightType.DELETE),
                                    	new Right (role, RightType.UPDATE),
                                    	new Right (role, RightType.GRANT)});
                        }
                    }
                }
                // set groups to be associated with the role
                Group[] groups = new Group[list.size() - 1];
                for (int j = 1; j < list.size(); j++) {
                    int groupId = ((Integer) list.get(j)).intValue();
                    groups[j - 1] = transaction.getGroupById(groupId);
                }
                transaction.setGroupsWithRole(role, groups);
            }
            manager.commitTransaction(transaction);
            transaction = null;

            getRequest().setAttribute( "MESSAGE",
                            "Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert."
                                    + "<BR/><BR/><p><a href='javascript:initRoleEditor()'>--> zurück "
                                    + "zum Rollen-Editor</a></p>");
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute("MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } catch (GeneralSecurityException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } finally {
            if (manager != null && transaction != null) {
                try {
                    manager.abortTransaction(transaction);
                } catch (GeneralSecurityException ex) {
                    ex.printStackTrace();
                }
            }
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
