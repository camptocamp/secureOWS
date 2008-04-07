package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class ProxyUserParameter extends Parameter {

	public ProxyUserParameter() {
		super("-proxyUser"); //$NON-NLS-1$
	}
	
	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		client.getParams().proxyUsername = parseUsername(nextParam);
		client.getParams().proxyPassword = parsePassword(nextParam);
		return true;
	}

	/**
	 * Parses out the username from the username:password string
	 */
	public static String parseUsername(String string) {
		if (string.contains(":")) { //$NON-NLS-1$
			String[] split = string.split(":"); //$NON-NLS-1$
			if (split.length != 2) {
				throw new IllegalArgumentException(
						Translations.getString("ProxyUserParameter.illegalArg")); //$NON-NLS-1$
			}
	
			return split[0];
		} else {
			return string;
		}
	}

	/**
	 * Parses out the password from the username:password string
	 */
	public static String parsePassword(String string) {
		// note: size of string after : is checked in parseUsername
		String[] split = string.split(":"); //$NON-NLS-1$
		if (split.length > 1) {
			return split[1];
		}
		return null;
	}

}
