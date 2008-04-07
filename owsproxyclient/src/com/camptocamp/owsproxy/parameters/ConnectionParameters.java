package com.camptocamp.owsproxy.parameters;

import java.net.URL;

import com.camptocamp.owsproxy.Translations;

public class ConnectionParameters {
	public String server;
	public String username;
	public String password;
	public String proxyHost;
	public int proxyPort;
	public String proxyUsername;
	public String proxyPassword;

	public ConnectionParameters(String server, String username,
			String password, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUsername = proxyUsername;
		this.proxyPassword = proxyPassword;
		}

	public ConnectionParameters copy() {
		return new ConnectionParameters(server, username, password, proxyHost, proxyPort, proxyUsername, proxyPassword);
	}
	
	/**
	 * Returns true if the configuration is legal
	 * 
	 * @throws IllegalArgumentException
	 */
	public void checkConfiguration() throws IllegalArgumentException {
		if (username == null) {
			throw new IllegalArgumentException(Translations.getString("ConnectionParameters.userRequired")); //$NON-NLS-1$
		}
		if (!validURL(server)) {
			throw new IllegalArgumentException(
					Translations.getString("ConnectionParameters.MalformedURL")); //$NON-NLS-1$
		}

		if ((proxyHost != null && proxyPort == -1)
				|| (proxyHost == null && proxyPort != -1)) {
			throw new IllegalArgumentException(
					Translations.getString("ConnectionParameters.bothHostAndPort")); //$NON-NLS-1$
		}
		
		if( proxyUsername!=null && proxyHost==null ){
			throw new IllegalArgumentException(Translations.getString("ConnectionParameters.UserRequiresHost")); //$NON-NLS-1$
		}
		
		if( proxyPassword!=null && proxyUsername==null ){
			throw new IllegalArgumentException(Translations.getString("ConnectionParameters.PasswordRequiresUsername")); //$NON-NLS-1$
		}
		
		
	}
	
	private static boolean validURL(String url) {
		try {
			new URL(url);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}