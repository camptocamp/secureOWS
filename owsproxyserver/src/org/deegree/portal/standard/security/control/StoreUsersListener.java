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
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.model.User;

/**
 * This <code>Listener</code> reacts on 'storeUsers' events, extracts the
 * contained user definitions and updates the <code>SecurityManager</code>
 * accordingly.
 *
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class StoreUsersListener extends AbstractListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( StoreUsersListener.class );

    public void actionPerformed(FormEvent event) {

        SecurityAccessManager manager = null;
        SecurityTransaction transaction = null;

        User[] users = null;

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            users = new User[params.length];

            for (int i = 0; i < params.length; i++) {
                if (!(params[0].getValue() instanceof RPCStruct)) {
                    throw new RPCException(
                            "Invalid RPC. Param elements must contain 'struct'-values.");
                }
                RPCStruct struct = (RPCStruct) params[i].getValue();

                // extract user details
                RPCMember userIdRPC = struct.getMember("userId");
                RPCMember userNameRPC = struct.getMember("userName");
                RPCMember emailRPC = struct.getMember("email");
                RPCMember passwordRPC = struct.getMember("password");
                RPCMember firstNameRPC = struct.getMember("firstName");
                RPCMember lastNameRPC = struct.getMember("lastName");

                int userId;
                String userName = null;
                String email = null;
                String password = null;
                String firstName = null;
                String lastName = null;

                if (userIdRPC == null) {
                    throw new RPCException(
                            "Invalid RPC. Every user must have a 'userId'.");
                }
                if (!(userIdRPC.getValue() instanceof String)) {
                    throw new RPCException(
                            "Invalid RPC. 'userId' members must be 'string'-values.");
                }
                try {
                    userId = Integer.parseInt(((String) userIdRPC.getValue()));
                } catch (NumberFormatException e) {
                    throw new RPCException(
                            "Invalid RPC. 'userId' members must be valid integer values.");
                }
                // extract userName
                if (userNameRPC != null) {
                    if (!(userNameRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'userName' members must be 'string'-values.");
                    } 
                    userName = (String) userNameRPC.getValue();
                    
                }
                // extract email
                if (emailRPC != null) {
                    if (!(emailRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'email' members must be 'string'-values.");
                    } 
                    email = (String) emailRPC.getValue();
                    
                }
                // extract password
                if (passwordRPC != null) {
                    if (!(passwordRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'password' members must be 'string'-values.");
                    } 
                    password = (String) passwordRPC.getValue();
                    
                }
                // extract firstName
                if (firstNameRPC != null) {
                    if (!(firstNameRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'firstName' members must be 'string'-values.");
                    } 
                    firstName = (String) firstNameRPC.getValue();
                    
                }
                // extract lastName
                if (lastNameRPC != null) {
                    if (!(lastNameRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'lastName' members must be 'string'-values.");
                    } 
                    lastName = (String) lastNameRPC.getValue();
                    
                }

                if (userName == null) {
                    throw new GeneralSecurityException ("Every user must have a name.");
                }
                if (email == null) {
                    throw new GeneralSecurityException ("Every user must have an email address.");
                }                
                
                users[i] = new User(userId, userName, password, firstName,
                        lastName, email, null);
            }

            for (int i = 0; i < users.length; i++) {
                LOG.logInfo( "\nid: " + users[i].getID() );
                LOG.logInfo( "firstName: " + users[i].getFirstName() );
                LOG.logInfo( "lastName: " + users[i].getLastName() );
                LOG.logInfo( "email: " + users[i].getEmailAddress() );
                LOG.logInfo( "password: " + users[i].getPassword() );
            }

			// get Transaction and perform access check
			manager = SecurityAccessManager.getInstance();
            transaction = SecurityHelper.acquireTransaction (this);
            SecurityHelper.checkForAdminRole(transaction);

            // remove deleted users
            User[] oldUsers = transaction.getAllUsers();
            for (int i = 0; i < oldUsers.length; i++) {
                boolean deleted = true;
                for (int j = 0; j < users.length; j++) {
                    if (users[j].equals(oldUsers[i])) {
                        deleted = false;
                    }
                }
                if (oldUsers [i].getID() != User.ID_SEC_ADMIN && deleted) {
                    transaction.deregisterUser(oldUsers[i]);
                }
            }

            // register all new users / update old users
            for (int i = 0; i < users.length; i++) {
                if (users[i].getID() == -1) {
                    transaction.registerUser(
                            users[i].getName (),
                            users[i].getPassword(),
                            users[i].getLastName(),
                            users[i].getFirstName(),
                            users[i].getEmailAddress());
                } else if (users [i].getID() != User.ID_SEC_ADMIN) {
                    transaction.updateUser(users [i]);
                }
            }
            manager.commitTransaction(transaction);
            transaction = null;

            getRequest()
                    .setAttribute(
                            "MESSAGE",
                            "Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert.<BR/>"
                                    + "<BR/><p><a href='javascript:initUserEditor()'>--> zurück zum"
                                    + " Benutzer-Editor</a></p>");
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
$Log: StoreUsersListener.java,v $
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
