// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/security/DRMAccess.java,v 1.13 2006/10/22 20:32:08 poth Exp $
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

package org.deegree.tools.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.WrongCredentialsException;
import org.deegree.security.drm.model.Group;
import org.deegree.security.drm.model.Right;
import org.deegree.security.drm.model.RightSet;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.SecuredObject;
import org.deegree.security.drm.model.User;
import org.w3c.dom.Document;


/**
 * tool class to handle deegree sercurity administration using 
 * commandline calls:
 * <pre>
 *  general definitions:
 *  -driver JDBC driver (e.g. sun.jdbc.odbc.JdbcOdbcDriver for ODBC databases)
 *  -logon jdbc:odbc:security logon to database (e.g. ODBC name)
 *  -user user name (optional)
 *  -password users password (optional)
 *  
 *  possible actions:
 *  -action (addUser, addGroup, addRole, addUserToGroup, assignRoleWithGroup, addSecuredObject, assignRights, clean)
 *  defines the action be performed. possible actions are listed inn brackets.
 *  
 *  action = addUser -> adds a user to the right management
 *  -name users login name
 *  -password users password
 *  -firstName the first name of the user
 *  -lastName the last name of the user
 *  -emal email address of the user
 *  
 *  action = removeUser -> removes a user to the right management
 *  -name users login name
 *  
 *  action = addGroup -> adds a group to the right management system
 *  -name name of the group
 *  -title title of the group
 *  
 *  action = removeGroup -> removes a group to the right management
 *  -name groups login name
 *  
 *  action = addRole -> adds a role to the right management system
 *  -name name of the role
 *  
 *  action = addUserToGroup -> adds a user to a named group
 *  -userName name of the user
 *  -groupName name of the group
 *  
 *  action = addUserToGroup -> assignes a group with a role
 *  -groupName name of the group
 *  -roleName name of the role
 *  
 *  action = addSecuredObject -> adds a new secured object to the right management system
 *  -soType type of the secured object (e.g. Layer, FeatureType, Coverage ...)
 *  -soName name of the secured object
 *  -soTitle title of the secured object
 *  
 *  action = removeSecuredObject -> removes a new secured object from the right management system
 *  -soType type of the secured object (e.g. Layer, FeatureType, Coverage ...)
 *  -soName name of the secured object
 *  
 *  action = assignRights -> assigns rights on a named secured object to a role
 *  -constraints comma seperated list of absolut pathes to filter encoding files
 *  -rights comma seperated list of rights to assign. the number of rights must be equest to the number constraints
 *  -soName name of the secured object
 *  -soType type of the secured object
 *  -role name of the role the rights shall be given to
 *  
 *  action = removeRights removes rights on a named secured object to a role
 *  -rights comma seperated list of rights to remove.
 *  -soName name of the secured object
 *  -soType type of the secured object
 *  -role name of the role the rights shall be given to
 *  
 *  action = clean -> cleans the complete right management system database by deleting all entries!
 *  </pre>
 * 
 *
 * @version $Revision: 1.13 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.13 $, $Date: 2006/10/22 20:32:08 $
 *
 * @since 2.0
 */
public class DRMAccess  {
    
    private static String secAdminPassword = "JOSE67";

    private SecurityAccessManager manager;

    private SecurityTransaction transaction;
    
    public DRMAccess() throws IOException {
    	InputStream is = DRMAccess.class.getResourceAsStream("sec.properties");
    	Properties prop = new Properties();
    	prop.load( is );
    	is.close();
    	secAdminPassword = prop.getProperty("adminpass");
    }

   
    protected void setUp(String driver, String logon, String user, String password) 
                                                throws Exception {
        Properties properties = new Properties();
        properties.setProperty("driver", driver);
        properties.setProperty("url", logon);
        if ( user == null ) user = ""; 
        properties.setProperty("user", user);
        if ( password == null ) password = "";
        properties.setProperty("password", password);        
        try {
            manager = SecurityAccessManager.getInstance();
        } catch (GeneralSecurityException e) {
            try {
                SecurityAccessManager.initialize("org.deegree.security.drm.SQLRegistry",
                                properties, 60 * 1000);
                manager = SecurityAccessManager.getInstance();
                
            } catch (GeneralSecurityException e1) {
                e1.printStackTrace();
            }
        }        
    }

    public void GetUsers() {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            User[] users = transaction.getAllUsers();
            for (int i = 0; i < users.length; i++) {
                System.out.println("User " + i + ": " + users[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param name
     * @param password
     * @param firstName
     * @param lastName
     * @param email
     */
    public void addUser(String name, String password, String firstName,
                         String lastName, String email) {
        try {        
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            transaction.registerUser(name, password, firstName,
                    lastName, email);

            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
            
        }
    }
    
    /**
     * 
     * @param name
     */
    public void removeUser(String name) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            user = transaction.getUserByName(name);            
            transaction.deregisterUser( user );
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param name
     * @param password
     * @return
     */
    public User login(String name, String password) {
        User user = null;
        try {
            user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            user = transaction.getUserByName(name);
            try {
                user.authenticate(password);
            } catch (WrongCredentialsException e) {
                System.out.println("failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
        }
        return user;
    }

    /**
     * 
     * @param name
     * @param title
     */
    public Group addGroup(String name, String title) {
        Group humans = null;
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            humans = transaction.registerGroup(name, title);
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
        }
        return humans;
    }
    
    /**
     * 
     * @param name
     * @param title
     */
    public void removeGroup(String name) {

        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            Group group = transaction.getGroupByName(name);
            transaction.deregisterGroup( group );
            manager.commitTransaction( transaction );
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param name
     */
    public Role addRole(String name) {
        Role role = null;
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            role = transaction.registerRole(name);
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }            
        }
        return role;
    }
    
    /**
     * 
     * @param name
     */
    public void removeRole(String name) {

        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            Role role = transaction.getRoleByName(name);
            transaction.deregisterRole( role );
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }            
        }

    }

    /**
     * 
     * @param user
     * @param group
     */
    public void setGroupMemberships(String userName, String group) {

        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            User jon = transaction.getUserByName(userName);
            Group humans = transaction.getGroupByName(group);
            User[] users = humans.getUsers(transaction);
            List list = Arrays.asList( users );
            ArrayList aList = new ArrayList( list );
            aList.add( jon );
            users = (User[])aList.toArray( new User[aList.size()] );
            transaction.setUsersInGroup(humans, users);

            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
            
        }
    }

    /**
     * 
     * @param role
     * @param group
     */
    public void setRoleAssociation(String role, String group) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            
            Group humans = transaction.getGroupByName(group);
            Role canOpener = transaction.getRoleByName(role);
            Group[] groups = canOpener.getGroups(transaction);
            List list = Arrays.asList( groups );
            ArrayList aList = new ArrayList( list );
            aList.add( humans );
            groups = (Group[])aList.toArray( new Group[aList.size()] );
            transaction.setGroupsWithRole(canOpener, groups );            
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
            
        }
    }

    /**
     * 
     * @param type
     * @param name
     * @param title
     */
    public void addSecuredObject(String type, String name,
                                 String title) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            transaction.registerSecuredObject(type, name, title);
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
            
        }
    }
    
    /**
     * 
     * @param type
     * @param name
     * @param title
     */
    public void removeSecuredObject(String type, String name) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            SecuredObject so = transaction.getSecuredObjectByName(name, type);
            transaction.deregisterSecuredObject(so);
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
            
        }
    }

    /**
     * 
     * @param filter
     * @param secObj
     * @param soType
     * @param role
     */
    public void assignRights(String[] filter, String secObj, String soType, 
                             String role, String[] rights) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
                        
            SecuredObject so = transaction.getSecuredObjectByName(secObj, soType);
                     
            Right[] rs = new Right[rights.length];
            for (int i = 0; i < rs.length; i++) {
                Filter constraints = null;       
                if ( filter[i] != null ) {
                    Document doc = XMLTools.parse(new StringReader(filter[i]));
                    constraints = AbstractFilter.buildFromDOM(doc.getDocumentElement());
                }                
                if ( rights[i].equalsIgnoreCase("getmap") ) {
                    rs[i] = new Right( so, RightType.GETMAP, constraints );
                } else if ( rights[i].equalsIgnoreCase("getfeatureinfo") ) {
                    rs[i] = new Right( so, RightType.GETFEATUREINFO, constraints );
                } else if ( rights[i].equalsIgnoreCase("getlegendgraphic") ) {
                    rs[i] = new Right( so, RightType.GETLEGENDGRAPHIC, constraints);
                } else if ( rights[i].equalsIgnoreCase("getfeature") ) {
                    rs[i] = new Right( so, RightType.GETFEATURE, constraints);
                } else if ( rights[i].equalsIgnoreCase("describefeaturetype") ) {
                    rs[i] = new Right( so, RightType.DESCRIBEFEATURETYPE, constraints);
                } else if ( rights[i].equalsIgnoreCase("getcoverage") ) {
                    rs[i] = new Right( so, RightType.GETCOVERAGE, constraints);
                } else if ( rights[i].equalsIgnoreCase("describecoverage") ) {
                    rs[i] = new Right( so, RightType.DESCRIBECOVERAGE, constraints);
                } else if ( rights[i].equalsIgnoreCase("delete") ) {
                    rs[i] = new Right( so, RightType.DELETE, constraints);
                } else if ( rights[i].equalsIgnoreCase("update") ) {
                    rs[i] = new Right( so, RightType.UPDATE, constraints);
                } else if ( rights[i].equalsIgnoreCase("insert") ) {
                    rs[i] = new Right( so, RightType.INSERT, constraints);
                } else {
                    System.out.println("unknown right: " + rights[i]);
                }
            }                        
            
            transaction.addRights( so, transaction.getRoleByName(role), rs );
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
        }
    }
    
    /**
     * @param secObj
     * @param soType
     * @param role
     * @param rights
     */
    public void removeRights(String secObj, String soType, String role, String[] rights) {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
                        
            SecuredObject so = transaction.getSecuredObjectByName(secObj, soType);
            
            RightType [] rs = new RightType[rights.length];
            for (int i = 0; i < rs.length; i++) {
                rs [i] = transaction.getRightByName(rights [i]);
            }                        
            
            transaction.removeRights( so, transaction.getRoleByName(role),rs);
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                manager.abortTransaction(transaction);
            } catch (GeneralSecurityException me) {
                me.printStackTrace();
            }
        }
    }
        
    /**
     * 
     */
    public void clean() {
        try {
            User user = manager.getUserByName("SEC_ADMIN");
            user.authenticate(DRMAccess.secAdminPassword);
            transaction = manager.acquireTransaction(user);
            transaction.clean();
            manager.commitTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void hasRight(String user, String password, String securedObject, 
                         String type, String right) {
        try {
            SecurityAccessManager sam = SecurityAccessManager.getInstance();
            User usr = sam.getUserByName( user );
            usr.authenticate( password );            
            SecurityAccess access = sam.acquireAccess( usr );
            SecuredObject secObj = access.getSecuredObjectByName(securedObject,type);
            if ( !usr.hasRight(access, right, secObj) ) {
                System.out.println( "You try to access a feature/resource on a " +
                        "securedObject you are not allowed to: " + securedObject);
            } else {
                System.out.println("the user '" + user +"' has the requested right");
            }
        } catch (WrongCredentialsException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private String fillString (String begin, int length) {
        StringBuffer sb = new StringBuffer ();
        for (int i = 0; i < length - begin.length (); i++) {
            sb.append (' ');
        }
        return begin + sb;
    }
    
    public void printRights( String userName, String secObjectType ) {
        try {
            User secAdminUser = manager.getUserByName("SEC_ADMIN");
            secAdminUser.authenticate(DRMAccess.secAdminPassword);
            SecurityAccess access = manager.acquireAccess(secAdminUser);

            User user = access.getUserByName(userName);
            SecuredObject [] secObjects = access.getAllSecuredObjects(secObjectType);
            Role [] roles = user.getRoles(access);

            System.out.println ("ROLE                    SEC_OBJECT                RIGHT            CONSTRAINTS\n");            
            for (int i = 0; i < roles.length; i++) {
                String roleString = fillString (roles [i].getName(), 24);
                for (int j = 0; j < secObjects.length; j++) {
                    String secObjectString = fillString (secObjects [j].getName(), 26);
                    RightSet rightSet = roles [i].getRights(access, secObjects [j]);
                    Right [] rights = rightSet.toArray(secObjects [j]);
                    for (int k = 0; k < rights.length; k++) {
                        String rightString = fillString(rights [k].getType().getName(), 16);
                        Filter filter = rights [k].getConstraints();
                        String constraintsString = " 0";
                        if (filter != null) {                            
                            constraintsString = " 1";
                        }
                        System.out.println (roleString + secObjectString + rightString + constraintsString);    
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    /**
     * 
     *
     */
    private static void printHelp() {        
        System.out.println("general definitions:");
        System.out.println("-driver JDBC driver (e.g. sun.jdbc.odbc.JdbcOdbcDriver for ODBC databases)");
        System.out.println("-logon jdbc:odbc:security logon to database (e.g. ODBC name)");
        System.out.println("-user user name (optional)");
        System.out.println("-pw users password (optional)");
        System.out.println();        
        System.out.println("possible actions:");
        System.out.println("-action (addUser, addGroup, addRole, addUserToGroup, assignRoleWithGroup, addSecuredObject, assignRights, clean)");
        System.out.println("defines the action be performed. possible actions are listed inn brackets.");
        System.out.println();
        System.out.println("action = addUser -> adds a user to the right management");
        System.out.println("-name users login name");
        System.out.println("-password users password");
        System.out.println("-firstName the first name of the user");
        System.out.println("-lastName the last name of the user");
        System.out.println("-emal email address of the user");
        System.out.println();
        System.out.println("action = removeUser -> removes a user to the right management");
        System.out.println("-name users login name");
        System.out.println();
        System.out.println("action = addGroup -> adds a group to the right management system");
        System.out.println("-name name of the group");
        System.out.println("-title title of the group");
        System.out.println();
        System.out.println("action = removeGroup -> removes a group to the right management");
        System.out.println("-name groups login name");
        System.out.println();
        System.out.println("action = addRole -> adds a role to the right management system");
        System.out.println("-name name of the role");
        System.out.println();
        System.out.println("action = addUserToGroup -> adds a user to a named group");
        System.out.println("-userName name of the user");
        System.out.println("-groupName name of the group");
        System.out.println();
        System.out.println("action = addUserToGroup -> assignes a group with a role");
        System.out.println("-groupName name of the group");
        System.out.println("-roleName name of the role");
        System.out.println();
        System.out.println("action = addSecuredObject -> adds a new secured object to the right management system");
        System.out.println("-soType type of the secured object (e.g. Layer, FeatureType, Coverage ...)");
        System.out.println("-soName name of the secured object");
        System.out.println("-soTitle title of the secured object");
        System.out.println();
        System.out.println("action = removeSecuredObject -> removes a new secured object from the right management system");
        System.out.println("-soType type of the secured object (e.g. Layer, FeatureType, Coverage ...)");
        System.out.println("-soName name of the secured object");
        System.out.println();
        System.out.println("action = assignRights -> assigns rights on a named secured object to a role");
        System.out.println("-constraints comma seperated list of absolut pathes to filter encoding files");
        System.out.println("-rights comma seperated list of rights to assign. the number of rights must be equest to the number constraints");
        System.out.println("-soName name of the secured object");
        System.out.println("-soType type of the secured object");
        System.out.println("-role name of the role the rights shall be given to");        
        System.out.println();
        System.out.println("action = removeRights removes rights on a named secured object to a role");
        System.out.println("-rights comma seperated list of rights to remove.");
        System.out.println("-soName name of the secured object");
        System.out.println("-soType type of the secured object");
        System.out.println("-role name of the role the rights shall be given to");        
        System.out.println();
        System.out.println("action = printRights -> print roles and associated rights of a user");
        System.out.println("-userName name of the user");
        System.out.println("-soType type of the secured object");        
        System.out.println();
        System.out.println("action = clean -> cleans the complete right management system " +
                           "database by deleting all entries!");        
    }

   
   
    public static void main(String[] args) {
        try {
            HashMap map = new HashMap();

            for ( int i = 0; i < args.length; i += 2 ) {
                if ( args.length >= i + 2 ) {
                    map.put( args[i], args[i + 1] );
                } else {
                    map.put( args[i], "" );
                }
            }
            
            if ( map.containsKey("-help") || map.containsKey("-h") ||
                 map.containsKey("-?") ) {
                printHelp();
            }
 
            String driver = (String)map.get("-driver");
            String logon = (String)map.get("-logon");
            String user = (String)map.get("-user");
            String password = (String)map.get("-pw");
            
            DRMAccess sac = new DRMAccess();
            sac.setUp(driver, logon, user, password);
            
            String action = (String)map.get("-action");
            
            if ( action.equals("addUser") ) {
                sac.addUser((String)map.get("-name"), 
                            (String)map.get("-password"), 
                            (String)map.get("-firstName"), 
                            (String)map.get("-lastName"), 
                            (String)map.get("-email"));
            } else if ( action.equals("removeUser") ) {
                sac.removeUser((String)map.get("-name") );
            } else if ( action.equals("addGroup") ) {
                sac.addGroup((String)map.get("-name"), 
                             (String)map.get("-title"));
            } else if ( action.equals("removeGroup") ) {
                sac.removeGroup((String)map.get("-name"));
            } else if ( action.equals("addRole") ) {
                sac.addRole((String)map.get("-name"));
            } else if ( action.equals("removeRole") ) {
                sac.removeRole((String)map.get("-name"));
            } else if ( action.equals("addUserToGroup") ) {
                sac.setGroupMemberships((String)map.get("-userName"),
                                        (String)map.get("-groupName"));
            } else if ( action.equals("assignRoleWithGroup") ) {
                sac.setRoleAssociation((String)map.get("-roleName"),
                                       (String)map.get("-groupName"));
            } else if ( action.equals("addSecuredObject") ) {
                sac.addSecuredObject((String)map.get("-soType"), 
                                     (String)map.get("-soName"), 
                                     (String)map.get("-soTitle") );
            } else if ( action.equals("removeSecuredObject") ) {
                sac.removeSecuredObject((String)map.get("-soType"), 
                                     (String)map.get("-soName") );
            } else if ( action.equals("assignRights") ) {
                String[] filter = StringTools.toArray((String)map.get("-constraints"),",;",false);
                for (int i = 0; i < filter.length; i++) {
                    if ( filter[i] != null && !filter[i].trim().equals("-") && !filter[i].trim().equals(".") ) {
                        RandomAccessFile raf = new RandomAccessFile(filter[i],"r");
                        long l = raf.length();
                        byte[] b = new byte[(int)l];
                        raf.read(b);
                        raf.close();
                        filter[i] = new String(b);
                    } else {
                        filter[i] = null;
                    }
                }
                String[] rights = StringTools.toArray((String)map.get("-rights"),",:;",false);
                sac.assignRights( filter, (String)map.get("-soName"), 
                                  (String)map.get("-soType"), 
                                  (String)map.get("-role"),
                                  rights);
            } else if ( action.equals("removeRights") ) {            
                String[] rights = StringTools.toArray((String)map.get("-rights"),",",false);
                sac.removeRights( (String)map.get("-soName"), 
                                  (String)map.get("-soType"), 
                                  (String)map.get("-role"),
                                  rights);
            }  else if ( action.equals("hasRight") ) {
                sac.hasRight( (String)map.get("-userName"),
                              (String)map.get("-password"),
                              (String)map.get("-soName"),
                              (String)map.get("-soType"),
                              (String)map.get("-right") );                             
            }  else if ( action.equals("clean") ) {
                sac.clean();
            } else if (action.equals("printRights")) {
                sac.printRights ((String)map.get("-userName"), (String)map.get("-soType"));
            }
                    
            try {
                Thread.sleep(100);
            } catch (Exception e) {}        
            System.out.println("finished");
            System.exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DRMAccess.java,v $
Revision 1.13  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.12  2006/08/08 15:53:49  poth
never called privte method removed

Revision 1.11  2006/08/01 16:35:38  poth
*** empty log message ***

Revision 1.9  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
