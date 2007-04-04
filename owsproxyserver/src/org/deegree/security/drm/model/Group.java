package org.deegree.security.drm.model;

import java.util.HashSet;

import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.SecurityRegistry;

/**
 * Implementation of group-objects. <code>Groups</code> s can be members of
 * other <code>Groups</code> and have associated <code>Role</code>s.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.3 $
 */
public class Group extends SecurableObject {

    public final static int ID_SEC_ADMIN = 2;

    /**
     * Creates a new <code>Group</code> -instance.
     * 
     * @param id
     * @param name
     * @param title
     * @param registry
     */
    public Group(int id, String name, String title, SecurityRegistry registry) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.registry = registry;
    }

    /**
     * Returns the <code>User</code> s that are DIRECT (i.e. not via group
     * membership) members of this group.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public User[] getUsers(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        return registry.getUsersInGroup(securityAccess, this);
    }

    /**
     * Returns the <code>Groups</code> s that are DIRECT members (i.e. not via
     * inheritance) of this group.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public Group[] getGroups(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        return registry.getGroupsInGroup(securityAccess, this);
    }

    /**
     * Returns <code>Role</code> s that this group is associated with
     * directly.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public Role[] getRoles(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        return registry.getRolesForGroup(securityAccess, this);
    }

    /**
     * Returns the <code>Privileges</code> that the <code>Group</code> has.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public Privilege[] getPrivileges(SecurityAccess securityAccess)
            throws GeneralSecurityException {
        Role[] roles = securityAccess.getAllRolesForGroup(this);
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
     * Returns the rights that this <code>Group</code> has on the given
     * <code>SecurableObject</code>.
     * 
     * @param securityAccess
     */
    public RightSet getRights(SecurityAccess securityAccess,
            SecurableObject object) throws GeneralSecurityException {
        Role[] roles = securityAccess.getAllRolesForGroup(this);
        RightSet rights = null;
        for (int i = 0; i < roles.length; i++) {
            Right[] roleRights = registry.getRights(securityAccess, object,
                    roles[i]);
            switch (i) {
            case 0: {
                rights = new RightSet(roleRights);
                break;
            }
            default: {
                rights.merge(new RightSet(roleRights));
            }
            }
        }
        return rights;
    }

    /**
     * Returns a <code>String</code> representation of this object.
     */
    public String toString(SecurityAccess securityAccess) {
        StringBuffer sb = new StringBuffer("Name: ").append(name);

        try {
            sb.append(", Users (Members): [");
            User[] users = getUsers(securityAccess);
            for (int i = 0; i < users.length; i++) {
                sb.append(users[i].getName());
                if (i != users.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");

            sb.append(", Groups (Members): [");
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
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Group.java,v $
Revision 1.3  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
