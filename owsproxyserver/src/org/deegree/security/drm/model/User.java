package org.deegree.security.drm.model;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.deegree.model.feature.Feature;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityRegistry;
import org.deegree.security.drm.WrongCredentialsException;

/**
 * Implementation of user-objects. <code>User</code> s can be members of
 * <code>Groups</code> and can be associated with <code>Role</code>s.
 * <p>
 * A user is always in one of two states:
 * <li>
 * <ul>
 * Not authenticated: <code>SecurityManager</code> will not issue
 * <code>SecurityAccess</code> instances for this user
 * <ul>
 * Authenticated: achieved by calling <code>authenticate()</code> and
 * submitting the correct password, afterwards <code>SecurityAccess</code>
 * instances for the user can be issued
 * </ul>
 * </li>
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.7 $
 */
public class User extends SecurableObject {

    public final static int ID_SEC_ADMIN = 1;

    private String password;
    private String firstName;
    private String lastName;
    private String emailAddress;

    private boolean isAuthenticated = false;

    // XXXsyp used for checking roles
    public HttpServletRequest servletRequest;
    
    /**
     * Creates a new <code>User</code> -instance.
     * 
     * @param id
     * @param name
     * @param password
     *            null means that password checking is disabled
     * @param firstName
     * @param lastName
     * @param registry
     */
    public User(int id, String name, String password, String firstName,
            String lastName, String emailAddress, SecurityRegistry registry) {
        this.id = id;
        this.name = name;
        this.password = password;
        if (password == null) {
            isAuthenticated = true;
        }
        if (lastName == null || firstName == null) {
            this.title = name;
        } else if ((lastName == null || lastName.equals(""))
                && (firstName == null || firstName.equals(""))) {
            this.title = name;
        } else if ((!lastName.equals("")) && (!firstName.equals(""))) {
            this.title = lastName + ", " + firstName;
        } else if (lastName.equals("")) {
            this.title = firstName;
        } else {
            this.title = lastName;
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.registry = registry;
    }

    /**
     * 
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * 
     */
    public String getPassword() {
        return password;
    }


    /**
     * Returns the groups that this user belongs to.
     * 
     * @param securityAccess
     */
    public Group[] getGroups(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        return registry.getGroupsForUser(securityAccess, this);
    }

    /**
     * Returns the roles that this is user is associated with (directly and via
     * group memberships).
     * <p>
     * 
     * @param securityAccess
     */
    public Role[] getRoles(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        return securityAccess.getAllRolesForUser(this);
    }

    /**
     * Returns the <code>Privileges</code> that the <code>User</code> has
     * (directly and via group memberships).
     * 
     * @throws GeneralSecurityException
     */
    public Privilege[] getPrivileges(SecurityAccess securityAccess)
            throws GeneralSecurityException {

        Role[] roles = securityAccess.getAllRolesForUser(this);
        HashSet privilegeSet = new HashSet();
        // gather privileges for all associated roles
        for (int i = 0; i < roles.length; i++) {
            Privilege[] rolePrivileges = registry.getPrivilegesForRole(
                    securityAccess, roles[i]);
            for (int j = 0; j < rolePrivileges.length; j++) {
                privilegeSet.add(rolePrivileges[j]);
            }
        }
        return (Privilege[]) privilegeSet.toArray(new Privilege[privilegeSet
                .size()]);
    }

    /**
     * Returns whether the <code>User</code> has a certain
     * <code>Privilege</code> (either directly or via group memberships).
     * 
     * @param privilege
     */
    public boolean hasPrivilege(SecurityAccess securityAccess,
            Privilege privilege) throws GeneralSecurityException {
        Privilege[] privileges = getPrivileges(securityAccess);        
        for (int i = 0; i < privileges.length; i++) {            
            if (privileges[i].equals(privilege)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the <code>User</code> has a certain privilege (either
     * directly or via group memberships).
     * 
     * @param s
     */
    public boolean hasPrivilege(SecurityAccess securityAccess, String s)
            throws GeneralSecurityException {
        Privilege privilege = registry.getPrivilegeByName(securityAccess, s);
        return hasPrivilege(securityAccess, privilege);
    }

    /**
     * Returns the rights that this <code>User</code> has on the given
     * <code>SecurableObject</code> (directly and via group memberships).
     */
    public RightSet getRights(SecurityAccess securityAccess,
            SecurableObject object) throws GeneralSecurityException {
        Role[] roles = securityAccess.getAllRolesForUser(this);
        RightSet rights = new RightSet();

        for (int i = 0; i < roles.length; i++) {
            rights = rights.merge(new RightSet(registry.getRights(
                    securityAccess, object, roles[i])));
        }
        return rights;
    }

    /**
     * Returns whether the <code>User</code> has a certain <code>Right</code>
     * on this <code>SecurableObject</code> (directly or via group
     * memberships).
     */
    public boolean hasRight(SecurityAccess securityAccess, RightType type,
            Feature accessParams, SecurableObject object)
            throws GeneralSecurityException {
        return getRights(securityAccess, object).applies(object, type,
                accessParams);
    }

    /**
     * Returns whether the <code>User</code> has a certain <code>Right</code>
     * on this <code>SecurableObject</code> (directly or via group
     * memberships).
     */
    public boolean hasRight(SecurityAccess securityAccess, RightType type,
            SecurableObject object) throws GeneralSecurityException {
        return getRights(securityAccess, object).applies(object, type);
    }

    /**
     * Returns whether the <code>User</code> has a certain right on this
     * <code>SecurableObject</code> (directly or via group memberships).
     */
    public boolean hasRight(SecurityAccess securityAccess, String s,
            SecurableObject object) throws GeneralSecurityException {
        RightType right = registry.getRightTypeByName(securityAccess, s);
        return hasRight(securityAccess, right, object);
    }

    /**
     * Returns whether the <code>User</code> has already been authenticated by
     * a call to <code>authenticate()</code> with the correct password (or if
     * the <code>user</code>'s password is null).
     * 
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    /**
     * Returns a <code>String</code> representation of this object.
     */
    public String toString(SecurityAccess securityAccess) {
        StringBuffer sb = new StringBuffer("Name: ").append(name).append(
                ", Title: ").append(title);

        try {
            sb.append(", Groups: [");
            Group[] groups = getGroups(securityAccess);
            for (int i = 0; i < groups.length; i++) {
                sb.append(groups[i].getName());
                if (i != groups.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");

            sb.append(", Roles: [");
            Role[] roles = getRoles(securityAccess);
            for (int i = 0; i < roles.length; i++) {
                sb.append(roles[i].getName());
                if (i != roles.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");

            sb.append(", Privileges: [");
            Privilege[] privileges = getPrivileges(securityAccess);
            for (int i = 0; i < privileges.length; i++) {
                sb.append(privileges[i].getName());
                if (i != privileges.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Checks if the submitted password is equal to the one of this user
     * instance and sets the state to "authenticated" in case it is correct.
     * 
     * @param password
     */
    public void authenticate(String password) throws WrongCredentialsException {
        if (this.password == null || "".equals( this.password ) ) {
            isAuthenticated = true;
            return;
        }
        if (!this.password.equals(password)) {
            isAuthenticated = false;
            throw new WrongCredentialsException( "The submitted password is incorrect.");
        }
        isAuthenticated = true;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: User.java,v $
Revision 1.7  2006/09/18 10:56:03  poth
code formating

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
