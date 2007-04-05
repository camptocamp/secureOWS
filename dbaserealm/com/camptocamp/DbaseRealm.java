package com.camptocamp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.realm.RealmBase;

import com.linuxense.javadbf.DBFReader;

/**
 * Dbase realm for Tomcat
 *
 */
public class DbaseRealm extends RealmBase {

	private Map userToPassword = new HashMap();
	private Map userToGroups = new HashMap();

	final static String DBASE_NAME = "users.dbf";
	
	static class DbasePrincipal implements Principal {

		String username;
		
		public DbasePrincipal(String username) {
			this.username = username;
		}
		
		public String getName() {
			return "Dbase principal for user" + username;
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof DbasePrincipal))
				return false;
			return ((DbasePrincipal)other).username.equals(this.username);
		}

		@Override
		public String toString() {
			return "DbaseRealm_user(" + username + ")";
		}
	}
	
	public DbaseRealm() {

		System.out.println("**************** Init DbaseRealm ******************");

		try {

			InputStream inputStream = this.getClass().getResourceAsStream(DBASE_NAME);

			DBFReader reader = new DBFReader(inputStream);

			Object[] rowObjects;

			while ((rowObjects = reader.nextRecord()) != null) {

				String user = ((String) rowObjects[0]).trim();
				String password = ((String) rowObjects[1]).trim();

				userToPassword.put(user, password);

				String groupsString = ((String) rowObjects[2]).trim();

				String[] groupsRaw = groupsString.split(",");

				String[] groups = new String[groupsRaw.length];

				for (int i = 0; i < groupsRaw.length; i++) {
					groups[i] = groupsRaw[i].trim();
				}

				userToGroups.put(user, groups);

				System.out.println("user: >" + user + "< pw: >***< groups: " + groups);
			}

			inputStream.close();

		} catch (Exception e) {
			System.out.println("Error while reading dbase file:");
			e.printStackTrace();
		}

	}
	
	@Override
	protected String getName() {
		System.out.println("getName");
		return "DbaseRealm";
	}

	@Override
	protected String getPassword(String username) {

		System.out.println("getPassword " + username);

		if (!userToPassword.containsKey(username))
			return null;
			
		String pw = (String)userToPassword.get(username);
		System.out.println("password found");
		return pw;
	}

	@Override
	protected Principal getPrincipal(String username) {

		System.out.println("getPrincipal " + username);

		return new DbasePrincipal(username);
	}
	
	public boolean hasRole(Principal principal, String role) {

		System.out.println("Has role:>" + principal + "< *role* >" + role + "<");

		if (!(principal instanceof DbasePrincipal))
			return false;
		DbasePrincipal p = (DbasePrincipal)principal;
		String user = p.username;
		if (!userToGroups.containsKey(user))
			return false;
		
		String[] groups = (String[])userToGroups.get(user);
		
		for (int i = 0; i < groups.length; i++) {
			if (role.equals(groups[i])) {
				System.out.println("Role found");
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Testing code.
	 */
	public static void main(String[] args) {
		
		System.out.println("hey");
		DbaseRealm r = new DbaseRealm();
		
		System.out.println("password for user1: " + r.getPassword("user1"));
		System.out.println("password for nosuchuser: " + r.getPassword("nosuchuser"));
		
		System.out.println("principal for user1: " + r.getPrincipal("user1"));
		System.out.println("principal for nosuchuser: " + r.getPrincipal("nosuchuser"));
		
		Principal p = r.getPrincipal("user1");
		System.out.println("user1 has role rolea " + r.hasRole(p, "rolea"));
		
		System.out.println("user1 has role nosuchrole " + r.hasRole(p, "nosuchrole"));

	}	
}
