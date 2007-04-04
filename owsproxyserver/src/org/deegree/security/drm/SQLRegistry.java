package org.deegree.security.drm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

import oracle.jdbc.OracleConnection;
import oracle.sql.CLOB;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.DataBaseIDGenerator;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.IDGeneratorFactory;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterConstructionException;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.model.Group;
import org.deegree.security.drm.model.Privilege;
import org.deegree.security.drm.model.Right;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.SecurableObject;
import org.deegree.security.drm.model.SecuredObject;
import org.deegree.security.drm.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is an implementation of a <code>Registry</code> using an SQL-Database (via JDBC) as
 * backend.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.23 $
 */
public final class SQLRegistry implements SecurityRegistry {
    
    private static final ILogger LOG = LoggerFactory.getLogger( SQLRegistry.class );

    private String dbDriver;
    private String dbName;
    private String dbUser;
    private String dbPassword;

    /** Exclusive connection for a transaction (only one at a time). */
    private Connection transactionalConnection = null;

    public void clean( SecurityTransaction transaction ) throws GeneralSecurityException {

        PreparedStatement pstmt = null;
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( SQLRegistry.class
                .getResourceAsStream( "clean.sql" ) ) );
            StringBuffer sb = new StringBuffer( 5000 );
            String line = null;
            while (( line = reader.readLine() ) != null) {
                sb.append( line );
            }
            String tmp = sb.toString();
            String[] commands = StringTools.toArray( tmp, ";", false );
            for (int i = 0; i < commands.length; i++) {
                String command = commands[i].trim();
                if ( !command.equals( "" ) ) {
                    pstmt = transactionalConnection.prepareStatement( command );
                    pstmt.executeUpdate();
                    closeStatement( pstmt );
                    pstmt = null;
                }
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException( "SQLRegistry.clean() failed. Rollback performed. "
                + "Error message: " + e.getMessage() );
        } catch (IOException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException(
                "SQLRegistry.clean() failed. Problem reading sql command file. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Adds a new user account to the <code>Registry</code>.
     * 
     * @param transaction
     * @param name
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the group already existed
     */
    public User registerUser( SecurityTransaction transaction, String name, String password,
                             String lastName, String firstName, String emailAddress )
        throws GeneralSecurityException {
        try {
            getUserByName( transaction, name );
            throw new DuplicateException( "Registration of user '"
                + name + "' failed! A user with " + "this name already exists." );
        } catch (UnknownException e) {
        }

        User user = new User( getID( transaction, "SEC_SECURABLE_OBJECTS" ), name, password,
            firstName, lastName, emailAddress, this );
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_SECURABLE_OBJECTS (ID,NAME,TITLE) VALUES (?,?,?)" );
            pstmt.setInt( 1, user.getID() );
            pstmt.setString( 2, user.getName() );
            pstmt.setString( 3, user.getTitle() );
            pstmt.executeUpdate();
            closeStatement( pstmt );
            pstmt = null;

            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_USERS (ID,PASSWORD,FIRSTNAME,LASTNAME,EMAIL) VALUES (?,?,?,?,?)" );
            pstmt.setInt( 1, user.getID() );
            pstmt.setString( 2, password );
            pstmt.setString( 3, user.getFirstName() );
            pstmt.setString( 4, user.getLastName() );
            pstmt.setString( 5, user.getEmailAddress() );
            pstmt.executeUpdate();
            closeStatement( pstmt );
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerUser() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
        return user;
    }

    /**
     * Removes an existing <code>User<code> from the <code>Registry</code>
     * (including its relations).
     * 
     * @param transaction
     * @param user
     * @throws GeneralSecurityException
     */
    public void deregisterUser( SecurityTransaction transaction, User user )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_GROUPS WHERE FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_ROLES WHERE FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection.prepareStatement( "DELETE FROM SEC_USERS WHERE ID=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_SECURABLE_OBJECTS=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_SECURABLE_OBJECTS WHERE ID=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterUser() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Updates the metadata (name, email, etc.) of a <code>User</code> in the
     * <code>Registry</code>.
     * 
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if a user with the new name already
     *             existed
     */
    public void updateUser( SecurityTransaction transaction, User user )
        throws GeneralSecurityException {

        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "UPDATE SEC_SECURABLE_OBJECTS SET NAME=?,TITLE=? WHERE ID=?" );
            pstmt.setString( 1, user.getName() );
            pstmt.setString( 2, user.getTitle() );
            pstmt.setInt( 3, user.getID() );
            pstmt.executeUpdate();
            closeStatement( pstmt );
            pstmt = null;

            pstmt = transactionalConnection
                .prepareStatement( "UPDATE SEC_USERS SET PASSWORD=?,FIRSTNAME=?,LASTNAME=?,EMAIL=? WHERE ID=?" );
            pstmt.setString( 1, user.getPassword() );
            pstmt.setString( 2, user.getFirstName() );
            pstmt.setString( 3, user.getLastName() );
            pstmt.setString( 4, user.getEmailAddress() );
            pstmt.setInt( 5, user.getID() );
            pstmt.executeUpdate();
            closeStatement( pstmt );
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerUser() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>User</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the user is not known to the
     *             <code>Registry</code>
     * 
     */
    public User getUserByName( SecurityAccess securityAccess, String name )
        throws GeneralSecurityException {
        User user = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_USERS.ID,SEC_USERS.PASSWORD,SEC_USERS.FIRSTNAME,SEC_USERS.LASTNAME,SEC_USERS.EMAIL "
                    + "FROM SEC_USERS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_USERS.ID=SEC_SECURABLE_OBJECTS.ID AND "
                    + "SEC_SECURABLE_OBJECTS.NAME=?" );
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                user = new User( rs.getInt( 1 ), name, rs.getString( 2 ), rs.getString( 3 ), rs
                    .getString( 4 ), rs.getString( 5 ), this );
            } else {
                throw new UnknownException( "Lookup of user '"
                    + name + "' failed! A user with this name does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }

        return user;
    }

    /**
     * Retrieves a <code>User</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param id
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the user is not known to the
     *             <code>Registry</code>
     */
    public User getUserById( SecurityAccess securityAccess, int id )
        throws GeneralSecurityException {
        User user = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_SECURABLE_OBJECTS.NAME," +
                    "SEC_USERS.PASSWORD,SEC_USERS.FIRSTNAME,SEC_USERS.LASTNAME," +
                    "SEC_USERS.EMAIL FROM SEC_USERS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=? AND SEC_USERS.ID=SEC_SECURABLE_OBJECTS.ID" );
            pstmt.setInt( 1, id );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                user = new User( id, rs.getString( 1 ), rs.getString( 2 ), rs.getString( 3 ), rs
                    .getString( 4 ), rs.getString( 5 ), this );
            } else {
                throw new UnknownException( "Lookup of user with id: "
                    + id + " failed! A user with this id does not exist." );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return user;
    }

    /**
     * Retrieves all <code>User</code> s from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public User[] getAllUsers( SecurityAccess securityAccess ) throws GeneralSecurityException {
        ArrayList users = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_USERS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_USERS.PASSWORD,SEC_USERS.FIRSTNAME,SEC_USERS.LASTNAME,SEC_USERS.EMAIL "
                    + "FROM SEC_USERS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_USERS.ID=SEC_SECURABLE_OBJECTS.ID" );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add( new User( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), rs
                    .getString( 4 ), rs.getString( 5 ), rs.getString( 6 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (User[]) users.toArray( new User[users.size()] );
    }

    /**
     * Retrieves all <code>Users</code> s from the <code>Registry</code> that are associated
     * DIRECTLY (i.e. not via group memberships) with a given <code>Role</code>.
     * 
     * @param securityAccess
     * @param role
     * @throws GeneralSecurityException
     */
    public User[] getUsersWithRole( SecurityAccess securityAccess, Role role )
        throws GeneralSecurityException {
        ArrayList users = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_USERS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_USERS.PASSWORD,SEC_USERS.FIRSTNAME,SEC_USERS.LASTNAME,"
                    + "SEC_USERS.EMAIL "
                    + "FROM SEC_USERS,SEC_SECURABLE_OBJECTS,SEC_JT_USERS_ROLES "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=SEC_USERS.ID AND SEC_JT_USERS_ROLES.FK_USERS=SEC_USERS.ID"
                    + " AND SEC_JT_USERS_ROLES.FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add( new User( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), rs
                    .getString( 4 ), rs.getString( 5 ), rs.getString( 6 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (User[]) users.toArray( new User[users.size()] );
    }

    /**
     * Retrieves all <code>User</code> s from the <code>Registry</code> that belong to the given
     * <code>Group</code> DIRECTLY (i.e. not via inheritance).
     * 
     * @param securityAccess
     * @param group
     * @throws GeneralSecurityException
     */
    public User[] getUsersInGroup( SecurityAccess securityAccess, Group group )
        throws GeneralSecurityException {
        ArrayList users = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_USERS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_USERS.PASSWORD,SEC_USERS.FIRSTNAME,SEC_USERS.LASTNAME,"
                    + "SEC_USERS.EMAIL "
                    + "FROM SEC_USERS,SEC_SECURABLE_OBJECTS,SEC_JT_USERS_GROUPS "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=SEC_USERS.ID AND SEC_JT_USERS_GROUPS.FK_USERS=SEC_USERS.ID"
                    + " AND SEC_JT_USERS_GROUPS.FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add( new User( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), rs
                    .getString( 4 ), rs.getString( 5 ), rs.getString( 6 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (User[]) users.toArray( new User[users.size()] );
    }

    /**
     * Adds a new group account to the <code>Registry</code>.
     * 
     * 
     * @param transaction
     * @param name
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the group already existed
     */
    public Group registerGroup( SecurityTransaction transaction, String name, String title )
        throws GeneralSecurityException {
        try {
            getGroupByName( transaction, name );
            throw new DuplicateException( "Registration of group '"
                + name + "' failed! A group with " + "this name already exists." );
        } catch (UnknownException e) {
        }

        Group group = new Group( getID( transaction, "SEC_SECURABLE_OBJECTS" ), name, title, this );
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_SECURABLE_OBJECTS (ID,NAME,TITLE) VALUES (?,?,?)" );
            pstmt.setInt( 1, group.getID() );
            pstmt.setString( 2, group.getName() );
            pstmt.setString( 3, group.getTitle() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_GROUPS (ID) VALUES (?)" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
        return group;
    }

    /**
     * Removes an existing <code>Group</code> from the <code>Registry</code> (including its
     * relations).
     * 
     * @param transaction
     * @param group
     * @throws GeneralSecurityException
     */
    public void deregisterGroup( SecurityTransaction transaction, Group group )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_GROUPS WHERE FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_GROUPS WHERE FK_GROUPS=? OR FK_GROUPS_MEMBER=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.setInt( 2, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_ROLES WHERE FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection.prepareStatement( "DELETE FROM SEC_GROUPS WHERE ID=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_SECURABLE_OBJECTS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_SECURABLE_OBJECTS WHERE ID=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>Group</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the group is not known to the
     *             <code>Registry</code>
     */
    public Group getGroupByName( SecurityAccess securityAccess, String name )
        throws GeneralSecurityException {
        Group group = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.TITLE "
                + "FROM SEC_GROUPS,SEC_SECURABLE_OBJECTS "
                + "WHERE SEC_GROUPS.ID=SEC_SECURABLE_OBJECTS.ID AND "
                + "SEC_SECURABLE_OBJECTS.NAME=?" );
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                group = new Group( rs.getInt( 1 ), name, rs.getString( 2 ), this );
            } else {
                throw new UnknownException( "Lookup of group '"
                    + name + "' failed! A group with this name does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return group;
    }

    /**
     * Retrieves a <code>Group</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param id
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the group is not known to the
     *             <code>Registry</code>
     */
    public Group getGroupById( SecurityAccess securityAccess, int id )
        throws GeneralSecurityException {
        Group group = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_GROUPS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=? AND SEC_GROUPS.ID=SEC_SECURABLE_OBJECTS.ID" );
            pstmt.setInt( 1, id );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                group = new Group( id, rs.getString( 1 ), rs.getString( 2 ), this );
            } else {
                throw new UnknownException( "Lookup of group with id: "
                    + id + " failed! A group with this id does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return group;
    }

    /**
     * Retrieves all <code>Group</code> s from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public Group[] getAllGroups( SecurityAccess securityAccess ) throws GeneralSecurityException {
        ArrayList groups = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_GROUPS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_GROUPS.ID=SEC_SECURABLE_OBJECTS.ID" );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups
                    .add( new Group( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Group[]) groups.toArray( new Group[groups.size()] );
    }

    /**
     * Adds a new role to the <code>Registry</code>.
     * 
     * @param transaction
     * @param name
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the role already existed
     */
    public Role registerRole( SecurityTransaction transaction, String name )
        throws GeneralSecurityException {
        try {
            getRoleByName( transaction, name );
            throw new DuplicateException( "Registration of role '"
                + name + "' failed! A role with " + "this name already exists." );
        } catch (UnknownException e) {
        }

        Role role = new Role( getID( transaction, "SEC_SECURABLE_OBJECTS" ), name, this );
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_SECURABLE_OBJECTS (ID,NAME,TITLE) VALUES (?,?,?)" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setString( 2, role.getName() );
            pstmt.setString( 3, role.getTitle() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_ROLES (ID) VALUES (?)" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerRole() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
        return role;
    }

    /**
     * Removes an existing <code>Role</code> from the <code>Registry</code> (including its
     * relations).
     * 
     * @param transaction
     * @param role
     * @throws GeneralSecurityException
     */
    public void deregisterRole( SecurityTransaction transaction, Role role )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_ROLES WHERE FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_ROLES WHERE FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_ROLES=? OR FK_SECURABLE_OBJECTS=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setInt( 2, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection.prepareStatement( "DELETE FROM SEC_ROLES WHERE ID=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_SECURABLE_OBJECTS WHERE ID=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterRole() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>Role</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the role is not known to the
     *             <code>Registry</code>
     */
    public Role getRoleByName( SecurityAccess securityAccess, String name )
        throws GeneralSecurityException {
        Role role = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_ROLES.ID "
                + "FROM SEC_ROLES,SEC_SECURABLE_OBJECTS "
                + "WHERE SEC_ROLES.ID=SEC_SECURABLE_OBJECTS.ID AND "
                + "SEC_SECURABLE_OBJECTS.NAME=?" );
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                role = new Role( rs.getInt( 1 ), name, this );
            } else {
                throw new UnknownException( "Lookup of role '"
                    + name + "' failed! A role with this name does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return role;
    }

    /**
     * Retrieves all <code>Roles</code> s from the <code>Registry</code> that have a certain
     * namespace.
     * 
     * @param securityAccess
     * @param ns
     *            null for default namespace
     * @throws GeneralSecurityException
     */
    public Role[] getRolesByNS( SecurityAccess securityAccess, String ns )
        throws GeneralSecurityException {
        ArrayList roles = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if ( ns != null
                && ( !ns.equals( "" ) ) ) {
                pstmt = con
                    .prepareStatement( "SELECT SEC_ROLES.ID,SEC_SECURABLE_OBJECTS.NAME "
                        + "FROM SEC_ROLES,SEC_SECURABLE_OBJECTS "
                        + "WHERE SEC_SECURABLE_OBJECTS.ID=SEC_ROLES.ID AND SEC_SECURABLE_OBJECTS.NAME LIKE ?" );
                pstmt.setString( 1, ns
                    + ":%" );
            } else {
                pstmt = con.prepareStatement( "SELECT SEC_ROLES.ID,SEC_SECURABLE_OBJECTS.NAME "
                    + "FROM SEC_ROLES,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=SEC_ROLES.ID AND "
                    + "SEC_SECURABLE_OBJECTS.NAME NOT LIKE '%:%'" );
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                roles.add( new Role( rs.getInt( 1 ), rs.getString( 2 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Role[]) roles.toArray( new Role[roles.size()] );
    }

    /**
     * Retrieves a <code>Role</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param id
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the role is not known to the
     *             <code>Registry</code>
     */
    public Role getRoleById( SecurityAccess securityAccess, int id )
        throws GeneralSecurityException {
        Role role = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_SECURABLE_OBJECTS.NAME "
                + "FROM SEC_ROLES,SEC_SECURABLE_OBJECTS "
                + "WHERE SEC_SECURABLE_OBJECTS.ID=? AND SEC_ROLES.ID=SEC_SECURABLE_OBJECTS.ID" );
            pstmt.setInt( 1, id );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                role = new Role( id, rs.getString( 1 ), this );
            } else {
                throw new UnknownException( "Lookup of role with id: "
                    + id + " failed! A role with this id does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return role;
    }

    /**
     * Retrieves all <code>Role</code> s from the <code>Registry</code>, except those that are
     * only used internally (these have namespaces that begin with $).
     * 
     * @param securityAccess
     * @throws GeneralSecurityException
     */
    public Role[] getAllRoles( SecurityAccess securityAccess ) throws GeneralSecurityException {
        ArrayList roles = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_ROLES.ID,SEC_SECURABLE_OBJECTS.NAME "
                + "FROM SEC_ROLES,SEC_SECURABLE_OBJECTS "
                + "WHERE SEC_ROLES.ID=SEC_SECURABLE_OBJECTS.ID AND "
                + "SEC_SECURABLE_OBJECTS.NAME NOT LIKE '$%:%'" );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                roles.add( new Role( rs.getInt( 1 ), rs.getString( 2 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Role[]) roles.toArray( new Role[roles.size()] );
    }

    /**
     * Adds a new <code>SecuredObject</code> to the <code>Registry</code>.
     * 
     * @param transaction
     * @param type
     * @param name
     * @param title
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the object already existed
     */
    public SecuredObject registerSecuredObject( SecurityTransaction transaction, String type,
                                               String name, String title )
        throws GeneralSecurityException {
        try {
            getSecuredObjectByName( transaction, name, type );
            throw new DuplicateException( "Registration of secured object '"
                + name + "' with type '" + type
                + "' failed! A secured object with this name and type " + "already exists." );
        } catch (UnknownException e) {
        }

        PreparedStatement pstmt = null;
        SecuredObject object = null;
        ResultSet rs = null;

        try {
            // check for ID of object type (add type if necessary)
            int typeId = 0;
            pstmt = transactionalConnection
                .prepareStatement( "SELECT ID FROM SEC_SECURED_OBJECT_TYPES WHERE NAME=?" );
            pstmt.setString( 1, type );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                typeId = rs.getInt( 1 );
                rs.close();
                rs = null;
                pstmt.close();
                pstmt = null;
            } else {
                typeId = getID( transaction, "SEC_SECURED_OBJECT_TYPES" );
                rs.close();
                rs = null;
                pstmt.close();
                pstmt = null;
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_SECURED_OBJECT_TYPES (ID,NAME) VALUES (?,?)" );
                pstmt.setInt( 1, typeId );
                pstmt.setString( 2, type );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }

            // insert securable object part
            object = new SecuredObject( getID( transaction, "SEC_SECURABLE_OBJECTS" ), typeId,
                name, title, this );
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_SECURABLE_OBJECTS (ID,NAME,TITLE) VALUES (?,?,?)" );
            pstmt.setInt( 1, object.getID() );
            pstmt.setString( 2, object.getName() );
            pstmt.setString( 3, object.getTitle() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            // insert secured object
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_SECURED_OBJECTS (ID, FK_SECURED_OBJECT_TYPES) VALUES (?,?)" );
            pstmt.setInt( 1, object.getID() );
            pstmt.setInt( 2, typeId );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeResultSet( rs );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerSecuredObject() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
        return object;
    }

    /**
     * Removes an existing <code>SecuredObject</code> from the <code>Registry</code> (including
     * its associations).
     * 
     * @param transaction
     * @param object
     * @throws GeneralSecurityException
     */
    public void deregisterSecuredObject( SecurityTransaction transaction, SecuredObject object )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_SECURED_OBJECTS WHERE ID=?" );
            pstmt.setInt( 1, object.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_SECURABLE_OBJECTS=?" );
            pstmt.setInt( 1, object.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_SECURABLE_OBJECTS WHERE ID=?" );
            pstmt.setInt( 1, object.getID() );
            pstmt.executeUpdate();
            pstmt = null;
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterSecuredObject() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>SecuredObject</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the object is not known to the
     *             <code>Registry</code>
     */
    public SecuredObject getSecuredObjectByName( SecurityAccess securityAccess, String name,
                                                String type ) throws GeneralSecurityException {
        SecuredObject object = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_SECURED_OBJECTS.ID,SEC_SECURED_OBJECT_TYPES.ID, "
                    + "SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURED_OBJECTS,SEC_SECURED_OBJECT_TYPES,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURED_OBJECTS.FK_SECURED_OBJECT_TYPES=SEC_SECURED_OBJECT_TYPES.ID AND "
                    + "SEC_SECURED_OBJECTS.ID=SEC_SECURABLE_OBJECTS.ID AND SEC_SECURABLE_OBJECTS.NAME LIKE ? AND "
                    + "SEC_SECURED_OBJECT_TYPES.NAME=?" );
            pstmt.setString( 1, name );
            pstmt.setString( 2, type );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                object = new SecuredObject( rs.getInt( 1 ), rs.getInt( 2 ), name,
                    rs.getString( 3 ), this );
            } else {
                throw new UnknownException( "Lookup of secured object '"
                    + name + "' with type '" + type + "' failed! A secured object with this "
                    + "name and type does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return object;
    }

    /**
     * Retrieves a <code>SecuredObject</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param id
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the object is not known to the
     *             <code>Registry</code>
     */
    public SecuredObject getSecuredObjectById( SecurityAccess securityAccess, int id )
        throws GeneralSecurityException {
        SecuredObject object = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_SECURED_OBJECTS.FK_SECURED_OBJECT_TYPES,SEC_SECURABLE_OBJECTS.NAME,"
                    + "SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURED_OBJECTS,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURED_OBJECTS.ID=SEC_SECURABLE_OBJECTS.ID AND SEC_SECURABLE_OBJECTS.ID=?" );
            pstmt.setInt( 1, id );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                object = new SecuredObject( id, rs.getInt( 1 ), rs.getString( 2 ),
                    rs.getString( 3 ), this );
            } else {
                throw new UnknownException( "Lookup of secured object with id: "
                    + id + " failed! A secured object with this id does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return object;
    }

    /**
     * Retrieves all <code>SecuredObject</code> s from the <code>Registry</code> that have a
     * certain namespace.
     * 
     * @param securityAccess
     * @param ns
     *            null for default namespace
     * @param type
     * @throws GeneralSecurityException
     */
    public SecuredObject[] getSecuredObjectsByNS( SecurityAccess securityAccess, String ns,
                                                 String type ) throws GeneralSecurityException {
        ArrayList objects = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if ( ns != null
                && ( !ns.equals( "" ) ) ) {
                pstmt = con
                    .prepareStatement( "SELECT SEC_SECURED_OBJECTS.ID,SEC_SECURED_OBJECT_TYPES.ID, "
                        + "SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                        + "FROM SEC_SECURED_OBJECTS,SEC_SECURED_OBJECT_TYPES,SEC_SECURABLE_OBJECTS "
                        + "WHERE SEC_SECURED_OBJECTS.FK_SECURED_OBJECT_TYPES=SEC_SECURED_OBJECT_TYPES.ID AND "
                        + "SEC_SECURABLE_OBJECTS.ID=SEC_SECURED_OBJECTS.ID AND SEC_SECURED_OBJECT_TYPES.NAME=? "
                        + "AND SEC_SECURABLE_OBJECTS.NAME LIKE ?" );
                pstmt.setString( 1, type );
                pstmt.setString( 2, ns
                    + ":%" );
            } else {
                pstmt = con
                    .prepareStatement( "SELECT SEC_SECURED_OBJECTS.ID,SEC_SECURED_OBJECT_TYPES.ID, "
                        + "SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                        + "FROM SEC_SECURED_OBJECTS,SEC_SECURED_OBJECT_TYPES,SEC_SECURABLE_OBJECTS "
                        + "WHERE SEC_SECURED_OBJECTS.FK_SECURED_OBJECT_TYPES=SEC_SECURED_OBJECT_TYPES.ID AND "
                        + "SEC_SECURABLE_OBJECTS.ID=SEC_SECURED_OBJECTS.ID AND SEC_SECURED_OBJECT_TYPES.NAME=? "
                        + "AND SEC_SECURABLE_OBJECTS.NAME NOT LIKE '%:%'" );
                pstmt.setString( 1, type );
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                objects.add( new SecuredObject( rs.getInt( 1 ), rs.getInt( 2 ), rs.getString( 3 ),
                    rs.getString( 4 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (SecuredObject[]) objects.toArray( new SecuredObject[objects.size()] );
    }

    /**
     * Retrieves all <code>SecuredObject</code> s with the given type from the
     * <code>Registry</code>.
     * 
     * @param securityAccess
     * @param type
     * @throws GeneralSecurityException
     */
    public SecuredObject[] getAllSecuredObjects( SecurityAccess securityAccess, String type )
        throws GeneralSecurityException {
        ArrayList objects = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_SECURED_OBJECTS.ID,SEC_SECURED_OBJECT_TYPES.ID, "
                    + "SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURED_OBJECTS,SEC_SECURED_OBJECT_TYPES,SEC_SECURABLE_OBJECTS "
                    + "WHERE SEC_SECURED_OBJECTS.FK_SECURED_OBJECT_TYPES=SEC_SECURED_OBJECT_TYPES.ID AND "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_SECURED_OBJECTS.ID AND SEC_SECURED_OBJECT_TYPES.NAME=?" );
            pstmt.setString( 1, type );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                objects.add( new SecuredObject( rs.getInt( 1 ), rs.getInt( 2 ), rs.getString( 3 ),
                    rs.getString( 4 ), this ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (SecuredObject[]) objects.toArray( new SecuredObject[objects.size()] );
    }

    /**
     * Adds a new <code>Privilege</code> to the <code>Registry</code>.
     * 
     * @param transaction
     * @param name
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the <code>Privilege</code>
     *             already existed
     */
    public Privilege registerPrivilege( SecurityTransaction transaction, String name )
        throws GeneralSecurityException {
        try {
            getPrivilegeByName( transaction, name );
            throw new DuplicateException( "Registration of privilege '"
                + name + "' failed! A privilege with " + "this name already exists." );
        } catch (UnknownException e) {
        }

        int id = getID( transaction, "SEC_PRIVILEGES" );
        Privilege privilege = new Privilege( id, name );
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_PRIVILEGES (ID, NAME) VALUES (?,?)" );
            pstmt.setInt( 1, id );
            pstmt.setString( 2, name );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.registerPrivilege() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
        return privilege;
    }

    /**
     * Removes an existing</code> Privilege</code> from the <code>Registry </code> (including its
     * relations).
     * 
     * @param transaction
     * @param privilege
     * @throws GeneralSecurityException
     */
    public void deregisterPrivilege( SecurityTransaction transaction, Privilege privilege )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_PRIVILEGES WHERE FK_PRIVILEGES=?" );
            pstmt.setInt( 1, privilege.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_PRIVILEGES WHERE ID=?" );
            pstmt.setInt( 1, privilege.getID() );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterPrivilege() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>Privilege</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the privilege is not known to the
     *             <code>Registry</code>
     */
    public Privilege getPrivilegeByName( SecurityAccess securityAccess, String name )
        throws GeneralSecurityException {
        Privilege privilege = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT ID FROM SEC_PRIVILEGES WHERE NAME=?" );
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                privilege = new Privilege( rs.getInt( 1 ), name );
            } else {
                throw new UnknownException( "Lookup of privilege '"
                    + name + "' failed! A privilege with this name does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return privilege;
    }

    /**
     * Retrieves all <code>Privileges</code> s from the <code>Registry</code> that are
     * associated DIRECTLY (i.e. not via group memberships) with a given <code>Role</code>.
     * 
     * @param securityAccess
     * @param role
     * @throws GeneralSecurityException
     */
    public Privilege[] getPrivilegesForRole( SecurityAccess securityAccess, Role role )
        throws GeneralSecurityException {
        ArrayList privileges = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_PRIVILEGES.ID,SEC_PRIVILEGES.NAME "
                + "FROM SEC_JT_ROLES_PRIVILEGES, SEC_PRIVILEGES WHERE "
                + "SEC_JT_ROLES_PRIVILEGES.FK_ROLES=? AND "
                + "SEC_JT_ROLES_PRIVILEGES.FK_PRIVILEGES=SEC_PRIVILEGES.ID" );
            pstmt.setInt( 1, role.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                privileges.add( new Privilege( rs.getInt( 1 ), rs.getString( 2 ) ) );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Privilege[]) privileges.toArray( new Privilege[privileges.size()] );
    }

    /**
     * Sets all <code>Privilege</code> s that are associated with a given <code>Role</code>.
     * 
     * @param transaction
     * @param role
     * @param privileges
     * @throws GeneralSecurityException
     */
    public void setPrivilegesForRole( SecurityTransaction transaction, Role role,
                                     Privilege[] privileges ) throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_PRIVILEGES WHERE FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < privileges.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_ROLES_PRIVILEGES (FK_ROLES, FK_PRIVILEGES) VALUES (?,?)" );
                pstmt.setInt( 1, role.getID() );
                pstmt.setInt( 2, privileges[i].getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setPrivilegesForRols() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Adds a new <code>Right</code> to the <code>Registry</code>.
     * 
     * @param transaction
     * @param name
     * @throws GeneralSecurityException
     *             this is a <code>DuplicateException</code> if the <code>Right</code> already
     *             existed
     */
    public RightType registerRightType( SecurityTransaction transaction, String name )
        throws GeneralSecurityException {
        try {
            getRightTypeByName( transaction, name );
            throw new DuplicateException( "Registration of right '"
                + name + "' failed! A right with " + "this name already exists." );
        } catch (UnknownException e) {
        }

        int id = getID( transaction, "SEC_RIGHTS" );
        RightType right = new RightType( id, name );
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "INSERT INTO SEC_RIGHTS (ID, NAME) VALUES (?,?)" );
            pstmt.setInt( 1, id );
            pstmt.setString( 2, name );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException( "SQLRegistry.registerRight() failed. Rollback " +
                    "performed. Error message: " + e.getMessage() );
        }
        return right;
    }

    /**
     * Removes an existing <code>RightType</code> from the <code>Registry</code> (including its
     * relations).
     * 
     * @param transaction
     * @param type
     * @throws GeneralSecurityException
     */
    public void deregisterRightType( SecurityTransaction transaction, RightType type )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_RIGHTS=?" );
            pstmt.setInt( 1, type.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
            pstmt = transactionalConnection.prepareStatement( "DELETE FROM SEC_RIGHTS WHERE ID=?" );
            pstmt.setInt( 1, type.getID() );
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.deregisterRight() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves a <code>Right</code> from the <code>Registry</code>.
     * 
     * @param securityAccess
     * @param name
     * @throws GeneralSecurityException
     *             this is an <code>UnknownException</code> if the <code>Right</code> is not
     *             known to the <code>Registry</code>
     */
    public RightType getRightTypeByName( SecurityAccess securityAccess, String name )
        throws GeneralSecurityException {
        RightType right = null;
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT ID FROM SEC_RIGHTS WHERE NAME=?" );
            pstmt.setString( 1, name );
            rs = pstmt.executeQuery();
            if ( rs.next() ) {
                right = new RightType( rs.getInt( 1 ), name );
            } else {
                throw new UnknownException( "Lookup of right '"
                    + name + "' failed! A right with this name does not exist." );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return right;
    }

    /**
     * Retrieves the <code>Rights</code> from the <code>Registry</code> that are associated with
     * a given <code>Role</code> and a <code>SecurableObject</code>.
     * 
     * @param securityAccess
     * @param object
     * @param role
     * @throws GeneralSecurityException
     */
    public Right[] getRights( SecurityAccess securityAccess, SecurableObject object, Role role )
        throws GeneralSecurityException {
        ArrayList rights = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement( "SELECT SEC_RIGHTS.ID,SEC_RIGHTS.NAME,"
                + "SEC_JT_ROLES_SECOBJECTS.CONSTRAINTS FROM SEC_JT_ROLES_SECOBJECTS,"
                + "SEC_RIGHTS WHERE SEC_JT_ROLES_SECOBJECTS.FK_ROLES=? AND "
                + "SEC_JT_ROLES_SECOBJECTS.FK_SECURABLE_OBJECTS=? AND "
                + "SEC_JT_ROLES_SECOBJECTS.FK_RIGHTS=SEC_RIGHTS.ID" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setInt( 2, object.getID() );
            rs = pstmt.executeQuery();
            ResultSetMetaData metadata = rs.getMetaData();
            int constraintType = metadata.getColumnType( 3 );

            while (rs.next()) {
                Right right = null;
                RightType type = new RightType( rs.getInt( 1 ), rs.getString( 2 ) );
                String constraints = null;
                Object o = rs.getObject( 3 );
                if ( o != null ) {
                    if ( constraintType == Types.CLOB ) {
                        Reader reader = ( (Clob) o ).getCharacterStream();
                        StringBuffer sb = new StringBuffer( 2000 );
                        int c;
                        try {
                            while (( c = reader.read() ) > -1) {
                                sb.append( (char) c );
                            }
                            reader.close();
                        } catch (IOException e) {
                            throw new GeneralSecurityException(
                                "Error converting CLOB to constraint string: "
                                    + e.getMessage() );
                        }
                        constraints = sb.toString();
                    } else {
                        constraints = o.toString();
                    }
                }

                // check if the right has constraints
                if ( constraints != null && constraints.length() > 3 ) {              
                    right = new Right( object, type, buildFilter( constraints ) );
                } else {
                    right = new Right( object, type, null );
                }

                rights.add( right );
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Right[]) rights.toArray( new Right[rights.size()] );
    }

    /**
     * Sets the <code>Rights</code> to be associated with a given <code>Role</code> and
     * <code>SecurableObject</code>.
     * 
     * @param transaction
     * @param object
     * @param role
     * @param rights
     * @throws GeneralSecurityException
     */
    public void setRights( SecurityTransaction transaction, SecurableObject object, Role role,
                          Right[] rights ) throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_ROLES=? AND FK_SECURABLE_OBJECTS=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setInt( 2, object.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            
            for (int i = 0; i < rights.length; i++) {
                
                String constraints = null;
                if ( rights[i].getConstraints() != null ) {
                    constraints = rights[i].getConstraints().toXML().toString();
                }
                LOG.logDebug( "constraints to add: ", constraints );
                if ( transactionalConnection instanceof OracleConnection ) {
                    handleOracle( object, role, rights[i], constraints );
                } else {
                    pstmt = transactionalConnection.prepareStatement( "INSERT INTO SEC_JT_ROLES_SECOBJECTS (FK_ROLES, FK_SECURABLE_OBJECTS, FK_RIGHTS,CONSTRAINTS) VALUES (?,?,?,?)" );
                    pstmt.setInt( 1, role.getID() );
                    pstmt.setInt( 2, object.getID() );
                    pstmt.setInt( 3, rights[i].getType().getID() );
                    pstmt.setString( 4, constraints );
                    pstmt.executeUpdate();
                    pstmt.close();
                }
                                
            }
        } catch (SQLException e) {
            LOG.logError( e.getMessage(), e );
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException( "SQLRegistry.setRights() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }
      
    private void handleOracle( SecurableObject object, Role role, Right right, String constraints )
                            throws SQLException {
        
        PreparedStatement pstmt;
        pstmt = transactionalConnection.prepareStatement( "INSERT INTO SEC_JT_ROLES_SECOBJECTS (FK_ROLES, FK_SECURABLE_OBJECTS, FK_RIGHTS, CONSTRAINTS) VALUES (?,?,?, EMPTY_CLOB() )" );
        pstmt.setInt( 1, role.getID() );
        pstmt.setInt( 2, object.getID() );
        pstmt.setInt( 3, right.getType().getID() );
        pstmt.executeUpdate();
        pstmt.close();
        transactionalConnection.commit();
        
        if ( constraints != null ) {
            pstmt = transactionalConnection.prepareStatement( "select CONSTRAINTS from SEC_JT_ROLES_SECOBJECTS where FK_ROLES = ? and FK_SECURABLE_OBJECTS = ? and FK_RIGHTS = ? FOR UPDATE" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setInt( 2, object.getID() );
            pstmt.setInt( 3, right.getType().getID() );
            ResultSet rs = pstmt.executeQuery();
            rs.next();
                            
            CLOB clob = (oracle.sql.CLOB)rs.getClob( 1 );
            try {
                //clob.getAsciiOutputStream().write( constraints.getBytes() );
                OutputStream os = clob.getAsciiOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                
                //use that output stream to write character data to the Oracle data store
                osw.write( constraints.toCharArray() );
                //write data and commit
                osw.flush();      
                osw.close();
                os.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        pstmt.close();
    }

    /**
     * Sets one <code>Right</code> to be associated with a given <code>Role</code> and all given
     * <code>SecurableObjects</code>.
     * 
     * @param transaction
     * @param objects
     * @param role
     * @param right
     * @throws GeneralSecurityException
     */
    public void setRights( SecurityTransaction transaction, SecurableObject[] objects, Role role,
                          Right right ) throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_ROLES_SECOBJECTS WHERE FK_ROLES=? AND FK_RIGHTS=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.setInt( 2, right.getType().getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < objects.length; i++) {
                String constraints = null;
                if ( right.getConstraints() != null ) {
                    constraints = right.getConstraints().toXML().toString();
                }
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_ROLES_SECOBJECTS (FK_ROLES, FK_SECURABLE_OBJECTS, FK_RIGHTS, CONSTRAINTS) VALUES (?,?,?,?)" );
                pstmt.setInt( 1, role.getID() );
                pstmt.setInt( 2, objects[i].getID() );
                pstmt.setInt( 3, right.getType().getID() );
                pstmt.setString( 4, constraints );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException( "SQLRegistry.setRights() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Retrieves all <code>Group</code> s from the <code>Registry</code> that the given
     * <code>User</code> is a DIRECT (i.e. not via inheritance) member of.
     * 
     * @param securityAccess
     * @param user
     * @throws GeneralSecurityException
     */
    public Group[] getGroupsForUser( SecurityAccess securityAccess, User user )
        throws GeneralSecurityException {
        ArrayList groups = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURABLE_OBJECTS,SEC_GROUPS,SEC_JT_USERS_GROUPS WHERE "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_GROUPS.ID AND "
                    + "SEC_JT_USERS_GROUPS.FK_GROUPS=SEC_GROUPS.ID AND "
                    + "SEC_JT_USERS_GROUPS.FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups
                    .add( new Group( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Group[]) groups.toArray( new Group[groups.size()] );
    }

    /**
     * Retrieves all <code>Groups</code> s from the <code>Registry</code> that are members of
     * another <code>Group</code> DIRECTLY (i.e. not via inheritance).
     * 
     * @param securityAccess
     * @param group
     * @throws GeneralSecurityException
     */
    public Group[] getGroupsInGroup( SecurityAccess securityAccess, Group group )
        throws GeneralSecurityException {
        ArrayList groups = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_GROUPS,SEC_SECURABLE_OBJECTS,SEC_JT_GROUPS_GROUPS "
                    + "WHERE SEC_SECURABLE_OBJECTS.ID=SEC_GROUPS.ID"
                    + " AND SEC_JT_GROUPS_GROUPS.FK_GROUPS_MEMBER=SEC_GROUPS.ID"
                    + " AND SEC_JT_GROUPS_GROUPS.FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups
                    .add( new Group( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Group[]) groups.toArray( new Group[groups.size()] );
    }

    /**
     * Retrieves all <code>Group</code> s from the <code>Registry</code> that the given
     * <code>Group</code> is a DIRECT member (i.e. not via inheritance) of.
     * 
     * @param securityAccess
     * @param group
     * @throws GeneralSecurityException
     */
    public Group[] getGroupsForGroup( SecurityAccess securityAccess, Group group )
        throws GeneralSecurityException {
        ArrayList groups = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURABLE_OBJECTS,SEC_GROUPS,SEC_JT_GROUPS_GROUPS WHERE "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_GROUPS.ID AND "
                    + "SEC_JT_GROUPS_GROUPS.FK_GROUPS=SEC_GROUPS.ID AND "
                    + "SEC_JT_GROUPS_GROUPS.FK_GROUPS_MEMBER=?" );
            pstmt.setInt( 1, group.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups
                    .add( new Group( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Group[]) groups.toArray( new Group[groups.size()] );
    }

    /**
     * Retrieves all <code>Group</code> s from the <code>Registry</code> that are associated
     * with a given <code>Role</code> DIRECTLY (i.e. not via inheritance).
     * 
     * @param securityAccess
     * @param role
     * @throws GeneralSecurityException
     */
    public Group[] getGroupsWithRole( SecurityAccess securityAccess, Role role )
        throws GeneralSecurityException {
        ArrayList groups = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_GROUPS.ID,SEC_SECURABLE_OBJECTS.NAME,SEC_SECURABLE_OBJECTS.TITLE "
                    + "FROM SEC_SECURABLE_OBJECTS,SEC_GROUPS,SEC_JT_GROUPS_ROLES WHERE "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_GROUPS.ID AND "
                    + "SEC_JT_GROUPS_ROLES.FK_GROUPS=SEC_GROUPS.ID AND "
                    + "SEC_JT_GROUPS_ROLES.FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                groups
                    .add( new Group( rs.getInt( 1 ), rs.getString( 2 ), rs.getString( 3 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Group[]) groups.toArray( new Group[groups.size()] );
    }

    /**
     * Retrieves all <code>Role</code> s from the <code>Registry</code> that are associated with
     * a given <code>User</code> DIRECTLY (i.e. not via group memberships).
     * 
     * @param securityAccess
     * @param user
     * @throws GeneralSecurityException
     */
    public Role[] getRolesForUser( SecurityAccess securityAccess, User user )
        throws GeneralSecurityException {
        ArrayList roles = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_ROLES.ID,SEC_SECURABLE_OBJECTS.NAME "
                    + "FROM SEC_SECURABLE_OBJECTS,SEC_ROLES,SEC_JT_USERS_ROLES WHERE "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_ROLES.ID AND SEC_JT_USERS_ROLES.FK_ROLES=SEC_ROLES.ID "
                    + "AND SEC_JT_USERS_ROLES.FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                roles.add( new Role( rs.getInt( 1 ), rs.getString( 2 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Role[]) roles.toArray( new Role[roles.size()] );
    }

    /**
     * Retrieves all <code>Role</code> s from the <code>Registry</code> that are associated with
     * a given <code>Group</code> DIRECTLY (i.e. not via inheritance).
     * 
     * @param securityAccess
     * @param group
     * @throws GeneralSecurityException
     */
    public Role[] getRolesForGroup( SecurityAccess securityAccess, Group group )
        throws GeneralSecurityException {
        ArrayList roles = new ArrayList();
        Connection con = acquireLocalConnection( securityAccess );
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con
                .prepareStatement( "SELECT SEC_ROLES.ID,SEC_SECURABLE_OBJECTS.NAME "
                    + "FROM SEC_SECURABLE_OBJECTS,SEC_ROLES,SEC_JT_GROUPS_ROLES WHERE "
                    + "SEC_SECURABLE_OBJECTS.ID=SEC_ROLES.ID AND SEC_JT_GROUPS_ROLES.FK_ROLES=SEC_ROLES.ID "
                    + "AND SEC_JT_GROUPS_ROLES.FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            rs = pstmt.executeQuery();
            while (rs.next()) {
                roles.add( new Role( rs.getInt( 1 ), rs.getString( 2 ), this ) );
            }
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            closeResultSet( rs );
            closeStatement( pstmt );
            releaseLocalConnection( securityAccess, con );
        }
        return (Role[]) roles.toArray( new Role[roles.size()] );
    }

    /**
     * Sets the <code>Group</code> s that a given <code>User</code> is member of DIRECTLY (i.e.
     * not via inheritance).
     * 
     * @param transaction
     * @param user
     * @param groups
     * @throws GeneralSecurityException
     */
    public void setGroupsForUser( SecurityTransaction transaction, User user, Group[] groups )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_GROUPS WHERE FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < groups.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_USERS_GROUPS (FK_USERS, FK_GROUPS) VALUES (?,?)" );
                pstmt.setInt( 1, user.getID() );
                pstmt.setInt( 2, groups[i].getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setGroupsForUser() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>Group</code> s that a given <code>Group</code> is member of DIRECTLY (i.e.
     * not via inheritance).
     * 
     * @param transaction
     * @param group
     * @param groups
     * @throws GeneralSecurityException
     */
    public void setGroupsForGroup( SecurityTransaction transaction, Group group, Group[] groups )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_GROUPS WHERE FK_GROUPS_MEMBER=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < groups.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_GROUPS_GROUPS (FK_GROUPS_MEMBER, FK_GROUPS) VALUES (?,?)" );
                pstmt.setInt( 1, group.getID() );
                pstmt.setInt( 2, groups[i].getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setGroupsForGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>Group</code> s that a given <code>Role</code> is associated to DIRECTLY
     * (i.e. not via inheritance).
     * 
     * @param transaction
     * @param role
     * @param groups
     * @throws GeneralSecurityException
     */
    public void setGroupsWithRole( SecurityTransaction transaction, Role role, Group[] groups )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_ROLES WHERE FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < groups.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_GROUPS_ROLES (FK_GROUPS, FK_ROLES) VALUES (?,?)" );
                pstmt.setInt( 1, groups[i].getID() );
                pstmt.setInt( 2, role.getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setGroupsWithRole() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>User</code> s that a given <code>Role</code> is associated to DIRECTLY
     * (i.e. not via <code>Group</code> membership).
     * 
     * @param transaction
     * @param role
     * @param users
     * @throws GeneralSecurityException
     */
    public void setUsersWithRole( SecurityTransaction transaction, Role role, User[] users )
        throws GeneralSecurityException {

        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_ROLES WHERE FK_ROLES=?" );
            pstmt.setInt( 1, role.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < users.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_USERS_ROLES (FK_USERS, FK_ROLES) VALUES (?,?)" );
                pstmt.setInt( 1, users[i].getID() );
                pstmt.setInt( 2, role.getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setUsersWithRole() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>User</code> s that are members of a given <code>Group</code> DIRECTLY
     * (i.e. not via inheritance).
     * 
     * @param transaction
     * @param group
     * @param users
     * @throws GeneralSecurityException
     */
    public void setUsersInGroup( SecurityTransaction transaction, Group group, User[] users )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_GROUPS WHERE FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < users.length; i++) {
                closeStatement( pstmt );
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_USERS_GROUPS (FK_USERS, FK_GROUPS) VALUES (?,?)" );
                pstmt.setInt( 1, users[i].getID() );
                pstmt.setInt( 2, group.getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setUsersInGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>Groups</code> s that are members of a given <code>Group</code> DIRECTLY
     * (i.e. not via inheritance).
     * 
     * @param transaction
     * @param group
     * @param groups
     * @throws GeneralSecurityException
     */
    public void setGroupsInGroup( SecurityTransaction transaction, Group group, Group[] groups )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_GROUPS WHERE FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < groups.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_GROUPS_GROUPS (FK_GROUPS_MEMBER, FK_GROUPS) VALUES (?,?)" );
                pstmt.setInt( 1, groups[i].getID() );
                pstmt.setInt( 2, group.getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setGroupsInGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>Role</code> s that a given <code>User</code> is directly associated to
     * (i.e. not via <code>Group</code> membership).
     * 
     * @param transaction
     * @param user
     * @param roles
     * @throws GeneralSecurityException
     */
    public void setRolesForUser( SecurityTransaction transaction, User user, Role[] roles )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_USERS_ROLES WHERE FK_USERS=?" );
            pstmt.setInt( 1, user.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < roles.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_USERS_ROLES (FK_USERS, FK_ROLES) VALUES (?,?)" );
                pstmt.setInt( 1, user.getID() );
                pstmt.setInt( 2, roles[i].getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setRolesForUser() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Sets the <code>Role</code> s that a given <code>Group</code> is associated to directly
     * (i.e. not via inheritance).
     * 
     * @param transaction
     * @param group
     * @param roles
     * @throws GeneralSecurityException
     */
    public void setRolesForGroup( SecurityTransaction transaction, Group group, Role[] roles )
        throws GeneralSecurityException {
        PreparedStatement pstmt = null;

        try {
            pstmt = transactionalConnection
                .prepareStatement( "DELETE FROM SEC_JT_GROUPS_ROLES WHERE FK_GROUPS=?" );
            pstmt.setInt( 1, group.getID() );
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            for (int i = 0; i < roles.length; i++) {
                pstmt = transactionalConnection
                    .prepareStatement( "INSERT INTO SEC_JT_GROUPS_ROLES (FK_GROUPS, FK_ROLES) VALUES (?,?)" );
                pstmt.setInt( 1, group.getID() );
                pstmt.setInt( 2, roles[i].getID() );
                pstmt.executeUpdate();
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            closeStatement( pstmt );
            abortTransaction( transaction );
            throw new GeneralSecurityException(
                "SQLRegistry.setRolesForGroup() failed. Rollback performed. "
                    + "Error message: " + e.getMessage() );
        }
    }

    /**
     * Initializes the <code>SQLRegistry</code> -instance according to the contents of the
     * submitted <code>Properties</code>.
     * 
     * @param properties
     * @throws GeneralSecurityException
     */
    public void initialize( Properties properties ) throws GeneralSecurityException {
        this.dbDriver = properties.getProperty( "driver" );
        this.dbName = properties.getProperty( "url" );
        this.dbUser = properties.getProperty( "user" );
        this.dbPassword = properties.getProperty( "password" );
    }

    /**
     * Signals the <code>SQLRegistry</code> that a new transaction begins.
     * 
     * Only one transaction can be active at a time.
     * 
     * 
     * @param transaction
     * @throws GeneralSecurityException
     */
    public synchronized void beginTransaction( SecurityTransaction transaction )
        throws GeneralSecurityException {
        try {
            transactionalConnection = DBConnectionPool.getInstance().acquireConnection( dbDriver,
                dbName, dbUser, dbPassword );
            // transactionalConnection.setAutoCommit(false);
        } catch (Exception e) {
            throw new GeneralSecurityException( e );
        }
    }

    /**
     * Signals the <code>SQLRegistry</code> that the current transaction ends, i.e. the changes
     * made by the transaction are made persistent.
     * 
     * @param transaction
     * @throws GeneralSecurityException
     */
    public void commitTransaction( SecurityTransaction transaction )
        throws GeneralSecurityException {
        try {
            transactionalConnection.commit();
        } catch (SQLException e) {
            throw new GeneralSecurityException( "Committing of transaction failed: "
                + e.getMessage() );
        } finally {
            try {
                DBConnectionPool.getInstance().releaseConnection( transactionalConnection,
                    dbDriver, dbName, dbUser, dbPassword );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Signals the <code>SQLRegistry</code> that the current transaction shall be aborted. Changes
     * made during the transaction are undone.
     * 
     * @param transaction
     * @throws GeneralSecurityException
     */
    public void abortTransaction( SecurityTransaction transaction ) throws GeneralSecurityException {
        try {
             transactionalConnection.rollback();
        } catch (SQLException e) {
            throw new GeneralSecurityException( "Aborting of transaction failed: "
                + e.getMessage() );
        } finally {
            try {
                DBConnectionPool.getInstance().releaseConnection( transactionalConnection,
                    dbDriver, dbName, dbUser, dbPassword );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    /**
     * Acquires a new <code>Connection</code>. If the given securityAccess is the exclusive
     * Read/Write-transaction holder, the transactionalConnection is returned, else a new
     * <code>Connection</code> is taken from the pool.
     * 
     * @param securityAccess
     * @return
     * @throws GeneralSecurityException
     */
    private Connection acquireLocalConnection( SecurityAccess securityAccess )
        throws GeneralSecurityException {

        Connection con = null;

        if ( securityAccess instanceof SecurityTransaction ) {
            con = transactionalConnection;
        } else {
            try {
                con = DBConnectionPool.getInstance().acquireConnection( dbDriver, dbName, dbUser,
                    dbPassword );
                // con.setAutoCommit(false);
            } catch (Exception e) {
                throw new GeneralSecurityException( e );
            }
        }
        return con;
    }

    /**
     * Releases a <code>Connection</code>. If the given securityAccess is the exclusive
     * Read/Write-transaction holder, nothing happens, else it is returned to the pool.
     * 
     * @param securityAccess
     * @param con
     * @return
     * @throws GeneralSecurityException
     */
    private void releaseLocalConnection( SecurityAccess securityAccess, Connection con )
        throws GeneralSecurityException {

        if ( !( securityAccess instanceof SecurityTransaction ) ) {
            if ( con != null ) {
                try {
                    DBConnectionPool.getInstance().releaseConnection( con, dbDriver, dbName,
                        dbUser, dbPassword );
                } catch (Exception e) {
                    throw new GeneralSecurityException( e );
                }
            }
        }
    }

    /**
     * Closes the given <code>Statement</code> if it is not null.
     * 
     * @param stmt
     * @throws GeneralSecurityException
     */
    private void closeStatement( Statement stmt ) throws GeneralSecurityException {
        if ( stmt != null ) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new GeneralSecurityException( e );
            }
        }
    }

    /**
     * Closes the given <code>ResultSet</code> if it is not null.
     * 
     * @param rs
     * @throws GeneralSecurityException
     */
    private void closeResultSet( ResultSet rs ) throws GeneralSecurityException {
        if ( rs != null ) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new GeneralSecurityException( e );
            }
        }
    }

    /**
     * Retrieves an unused PrimaryKey-value for the given table. The table must have its PrimaryKey
     * in an Integer-field named 'ID'.
     * 
     * @param table
     * @return
     */
    private int getID( SecurityTransaction transaction, String table )
        throws GeneralSecurityException {
        int id = 0;
        Connection con = acquireLocalConnection( transaction );

        try {
            DataBaseIDGenerator idGenerator = IDGeneratorFactory.createIDGenerator( con, table,
                "ID" );
            Object o = idGenerator.generateUniqueId();
            if ( !( o instanceof Integer ) ) {
                throw new GeneralSecurityException( "Error generating new PrimaryKey for table '"
                    + table + "'." );
            }
            id = ( (Integer) o ).intValue();
        } catch (SQLException e) {
            throw new GeneralSecurityException( e );
        } finally {
            releaseLocalConnection( transaction, con );
        }
        return id;
    }

    /**
     * Tries to build a <code>ComplexFilter</code> from the given string representation.
     * 
     * @param constraints
     * @return
     * @throws GeneralSecurityException
     */
    private ComplexFilter buildFilter( String constraints ) throws GeneralSecurityException {
        Filter filter = null;
        try {
            Document document = XMLTools.parse( new StringReader( constraints ) );
            Element element = document.getDocumentElement();
            filter = AbstractFilter.buildFromDOM( element );
        } catch (FilterConstructionException e) {
            throw new GeneralSecurityException( "The stored constraint is not a valid filter: "
                + e.getMessage() );
        } catch (Exception e) {
            throw new GeneralSecurityException( "Error parsing the stored constraint: "
                + e.getMessage() );
        }
        if ( !( filter instanceof ComplexFilter ) ) {
            throw new GeneralSecurityException(
                "The stored constraint is not of type 'ComplexFilter'." );
        }
        return (ComplexFilter) filter;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SQLRegistry.java,v $
Revision 1.23  2006/05/16 07:57:50  poth
commentation completed
useless imports removed



********************************************************************** */