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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.User;



/**
 * Helper class that performs common security access tasks and checks used in
 * the <code>Listener</code> classes.
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class ClientHelper {

    public static final String KEY_USERNAME = "USERNAME";
    public static final String KEY_PASSWORD = "PASSWORD";    
    
    public static final String TYPE_LAYER = "Layer";
    public static final String TYPE_FEATURETYPE = "Featuretype";
    public static final String TYPE_METADATASCHEMA = "MetadataSchema";
    
    /**
     * Tries to acquire a <code>SecurityAccess</code> for the credentials
     * (username, password) stored in the associated <code>HttpSesssion</code>
     * of the given <code>AbstractListener</code>.
     * 
     * @param listener
     * @throws GeneralSecurityException
     */
    public static SecurityAccess acquireAccess(AbstractListener listener)
            throws GeneralSecurityException {
        // get USERNAME and PASSWORD from HttpSession
        HttpSession session = ((HttpServletRequest) listener.getRequest())
                .getSession(false);
        if (session == null) {
            throw new UnauthorizedException(
                    "Sie sind nicht korrekt am System angemeldet ("
                    + "wahrscheinlich ist der Timeout für Ihre Sitzung abgelaufen). Bitte melden Sie "
                    + "sich erneut an.<BR/><BR/><p><a href='/index.jsp'>--> zur Anmeldung</a></p>");
        }
        String userName = (String) session.getAttribute(KEY_USERNAME);
        String password = (String) session.getAttribute(KEY_PASSWORD);

        // perform access check
        SecurityAccessManager manager = SecurityAccessManager.getInstance();
        User user = manager.getUserByName(userName);
        user.authenticate(password);
        return manager.acquireAccess(user);
    }

    /**
     * Tries to acquire a <code>SecurityTransaction</code> for the credentials
     * (username, password) stored in the associated <code>HttpSesssion</code>.
     * 
     * @param listener
     * @throws GeneralSecurityException
     */
    public static SecurityTransaction acquireTransaction(
            AbstractListener listener) throws GeneralSecurityException {
        // get USERNAME and PASSWORD from HttpSession
        HttpSession session = ((HttpServletRequest) listener.getRequest())
                .getSession(false);
        String userName = (String) session.getAttribute(KEY_USERNAME);
        String password = (String) session.getAttribute(KEY_PASSWORD);

        // perform access check
        SecurityAccessManager manager = SecurityAccessManager.getInstance();
        User user = manager.getUserByName(userName);
        user.authenticate(password);
        return manager.acquireTransaction(user);
    }

    /**
     * Returns the administrator (the 'Administrator'- or a 'SUBADMIN:'-role)
     * for the given role.
     * 
     * @param access
     * @param role
     * @throws GeneralSecurityException
     */
    public static Role findAdminForRole(SecurityAccess access, Role role)
            throws GeneralSecurityException {
        Role[] allRoles = access.getAllRoles();
        Role admin = access.getRoleById(Role.ID_SEC_ADMIN);
        for (int i = 0; i < allRoles.length; i++) {
            if (allRoles[i].getName().startsWith("SUBADMIN:")) {
                // if a subadmin-role has the update right, it is
                // considered to be administrative for the role
                if (allRoles[i].hasRight(access, RightType.UPDATE, role)) {
                    admin = allRoles[i];
                }
            }
        }
        return admin;
    }

    /**
     * Returns the associated 'Administrator'- or 'SUBADMIN:'-role of the token
     * holder.
     * 
     * @param access
     * @throws GeneralSecurityException
     */
    public static Role checkForAdminOrSubadminRole(SecurityAccess access)
            throws GeneralSecurityException {
        Role adminOrSubadminRole = null;
        Role[] roles = access.getUser().getRoles(access);
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].getID() == Role.ID_SEC_ADMIN
                    || roles[i].getName().startsWith("SUBADMIN:")) {
                if (adminOrSubadminRole == null) {
                    adminOrSubadminRole = roles[i];
                } else {
                    throw new GeneralSecurityException(
                            "Unzulä�ssige Rollenvergabe: Benutzer '"
                                    + access.getUser().getTitle()
                                    + "' hat sowohl die Rolle '"
                                    + adminOrSubadminRole.getTitle()
                                    + "' als auch die Rolle '"
                                    + roles[i].getTitle() + "'.");
                }
            }
        }
        if (adminOrSubadminRole == null) {
            throw new UnauthorizedException(
                    "Sie verfüügen nicht über die für den Zugriff benötigte Administrator- "
                            + "bzw. üüber eine Subadministrator-Rolle.");
        }
        return adminOrSubadminRole;
    }

    /**
     * Tests if the given token is associated with the 'Administrator'-role.
     * 
     * @param token
     * @throws GeneralSecurityException,
     *             this is an UnauthorizedException if the user does not have
     *             the 'Administrator'-role
     */
    public static void checkForAdminRole(SecurityAccess access)
            throws GeneralSecurityException {
        Role[] roles = access.getUser().getRoles(access);
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].getID() == Role.ID_SEC_ADMIN) {
                return;
            }
        }
        throw new UnauthorizedException(
                "Sie verfügen nicht über die für den Zugriff benötigte Administrator-Rolle.");
    }

    /**
     * Tests if the 'SUBADMIN:' and 'Administrator'-roles are all disjoint (so
     * that there are no users that have more than 1 role).
     * 
     * @param access
     * @throws GeneralSecurityException
     *             if there is a user with more than one role
     */
    public static void checkSubadminRoleValidity(SecurityAccess access)
            throws GeneralSecurityException {

        Role[] subadminRoles = access.getRolesByNS("SUBADMIN");
        Set[] rolesAndUsers = new Set[subadminRoles.length + 1];
        String[] roleNames = new String[subadminRoles.length + 1];

        // admin role
        User[] users = access.getRoleById(Role.ID_SEC_ADMIN).getAllUsers(access);
        rolesAndUsers[0] = new HashSet();
        roleNames[0] = "Administrator";
        for (int i = 0; i < users.length; i++) {
            rolesAndUsers[0].add(users[i]);
        }

        // subadmin roles
        for (int i = 1; i < rolesAndUsers.length; i++) {
            users = subadminRoles[i - 1].getAllUsers(access);
            rolesAndUsers[i] = new HashSet();
            roleNames[i] = subadminRoles[i - 1].getTitle();
            for (int j = 0; j < users.length; j++) {
                rolesAndUsers[i].add(users[j]);
            }
        }

        // now check if all usersets are disjoint
        for (int i = 0; i < rolesAndUsers.length - 1; i++) {
            Set userSet1 = rolesAndUsers[i];
            for (int j = i + 1; j < rolesAndUsers.length; j++) {
                Set userSet2 = rolesAndUsers[j];
                Iterator it = userSet2.iterator();
                while (it.hasNext()) {
                    User user = (User) it.next();
                    if (userSet1.contains(user)) {
                        throw new GeneralSecurityException(
                                "Ungültige Subadmin-Rollenvergabe. Benutzer '"
                                        + user.getTitle()
                                        + "' würde sowohl die Rolle '"
                                        + roleNames[i]
                                        + "' als auch die Rolle '"
                                        + roleNames[j] + "' erhalten.");
                    }
                }
            }
        }
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ClientHelper.java,v $
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/08/29 19:18:39  poth
code formating / footer correction

Revision 1.5  2006/08/03 08:01:53  poth
list of right types enhanced / TYPE_METADATAFORMAT renamed to TYPE_METADATASCHEMA

Revision 1.4  2006/07/23 08:45:24  poth
*** empty log message ***

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
