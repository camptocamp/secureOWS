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
import org.deegree.security.drm.model.User;

/**
 * This <code>Listener</code> reacts on RPC-StoreGroups events, extracts the
 * contained user/group relations and updates the <code>SecurityManager</code>
 * accordingly.
 * 
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class StoreGroupsListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {
        
        SecurityAccessManager manager = null;
        SecurityTransaction transaction = null;

        // values are Integers (groupIds) or Strings (groupNames)
        Object[] groups = null;
        // values of the ArrayLists are Integers (groupIds)
        ArrayList[] userMembersIds = null;
        // values of the ArrayLists are Integers (userIds)
        ArrayList[] groupMembersIds = null;

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            groups = new Object[params.length];
            userMembersIds = new ArrayList[params.length];
            groupMembersIds = new ArrayList[params.length];

            for (int i = 0; i < params.length; i++) {
                ArrayList userMemberList = new ArrayList(200);
                ArrayList groupMemberList = new ArrayList(200);
                userMembersIds[i] = userMemberList;
                groupMembersIds[i] = groupMemberList;
                if (!(params[0].getValue() instanceof RPCStruct)) {
                    throw new RPCException(
                            "Invalid RPC. Param elements must contain 'struct'-values.");
                }
                RPCStruct struct = (RPCStruct) params[i].getValue();

                // extract group-id / group-name
                RPCMember groupId = struct.getMember("groupId");
                RPCMember groupName = struct.getMember("groupName");
                if ((groupId == null && groupName == null)
                        || (groupId != null && groupName != null)) {
                    throw new RPCException(
                            "Invalid RPC. Every group must either have a 'groupId' or a 'groupName'.");
                }
                if (groupId != null) {
                    if (!(groupId.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'groupId' members must be 'string'-values.");
                    }
                    try {
                        groups[i] = (new Integer((String) groupId.getValue()));
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. 'groupId' members must be valid integer values.");
                    }
                } else {
                    if (!(groupName.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'groupName' members must be 'string'-values.");
                    }

                    groups[i] = ((String) groupName.getValue());
                }

                // extract user members
                RPCMember userMembers = struct.getMember("userMembers");
                if (!(userMembers.getValue() instanceof RPCParameter[])) {
                    throw new RPCException(
                            "Invalid RPC. 'userMembers'-members must contain an 'array'.");
                }
                RPCParameter[] memberArray = (RPCParameter[]) userMembers
                        .getValue();
                for (int j = 0; j < memberArray.length; j++) {
                    if (!(memberArray[j].getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. The 'userMembers' array must contain 'string'-values.");
                    }
                    try {
                        userMemberList.add(new Integer((String) memberArray[j]
                                .getValue()));
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. The values in 'userMembers' arrays must be valid integer values.");
                    }
                }

                // extract group members
                RPCMember groupMembers = struct.getMember("groupMembers");
                if (!(groupMembers.getValue() instanceof RPCParameter[])) {
                    throw new RPCException(
                            "Invalid RPC. 'groupMembers'-members must contain an 'array'.");
                }
                memberArray = (RPCParameter[]) groupMembers.getValue();
                for (int j = 0; j < memberArray.length; j++) {
                    if (!(memberArray[j].getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. The 'groupMembers' array must contain 'string'-values.");
                    }
                    try {
                        groupMemberList.add(new Integer((String) memberArray[j]
                                .getValue()));
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. The values in 'groupMembers' arrays must be valid integer values.");
                    }
                }

            }

            // get Transaction and perform access check
            manager = SecurityAccessManager.getInstance();
            transaction = SecurityHelper.acquireTransaction(this);
            SecurityHelper.checkForAdminRole(transaction);

            // remove deleted groups
            Group[] oldGroups = transaction.getAllGroups();
            for (int i = 0; i < oldGroups.length; i++) {
                if (oldGroups[i].getID() != Group.ID_SEC_ADMIN) {
                    boolean deleted = true;
                    for (int j = 0; j < groups.length; j++) {
                        if (groups[j] instanceof Integer) {
                            if (((Integer) groups[j]).intValue() == oldGroups[i]
                                    .getID()) {
                                deleted = false;
                            }
                        }
                    }
                    if (deleted) {
                        transaction.deregisterGroup(oldGroups[i]);
                    }
                }
            }

            // save all submitted groups (and their members)
            for (int i = 0; i < groups.length; i++) {
                Group group;
                if (groups[i] instanceof Integer) {
                    group = transaction.getGroupById(((Integer) groups[i])
                            .intValue());
                } else {
                    group = transaction.registerGroup((String) groups[i],
                            (String) groups[i]);
                }

                // set user members
                User[] userMembers = new User[userMembersIds[i].size()];
                for (int j = 0; j < userMembersIds[i].size(); j++) {
                    int userId = ((Integer) userMembersIds[i].get(j))
                            .intValue();
                    userMembers[j] = transaction.getUserById(userId);
                }
                transaction.setUsersInGroup(group, userMembers);

                // set group members
                Group[] groupMembers = new Group[groupMembersIds[i].size()];
                for (int j = 0; j < groupMembersIds[i].size(); j++) {
                    int groupId = ((Integer) groupMembersIds[i].get(j))
                            .intValue();
                    groupMembers[j] = transaction.getGroupById(groupId);
                }
                transaction.setGroupsInGroup(group, groupMembers);
            }
            Group[] cycle = transaction.findGroupCycle();
            manager.commitTransaction(transaction);
            transaction = null;

            StringBuffer sb = new StringBuffer(200);
            sb.append("Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert.<BR/>"
                            + "<BR/><p><a href='javascript:initGroupEditor()'>--> zurück zum"
                            + " Gruppen-Editor</a></p>");

            if (cycle != null) {
                sb
                        .append("<br><p><h4>Hinweis: In der Gruppenstruktur befindet sich (mindestens) ein Zyklus:<br><code>");
                for (int i = 0; i < cycle.length; i++) {
                    sb.append(cycle[i].getName());
                    if (i != cycle.length - 1) {
                        sb.append(" -> ");
                    }
                }
                sb.append("</code></h4></p>");
            }

            getRequest().setAttribute("MESSAGE", sb.toString());
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute(
                    "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute(
                    "MESSAGE",
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
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: StoreGroupsListener.java,v $
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
