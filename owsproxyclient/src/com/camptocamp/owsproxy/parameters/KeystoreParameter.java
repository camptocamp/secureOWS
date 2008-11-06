package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class KeystoreParameter extends Parameter {

	public KeystoreParameter() {
		super("-keystore"); //$NON-NLS-1$
	}
	
	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		client.getParams().keystore = parsekeystoreFile(nextParam);
		client.getParams().keystorePass= parsePassword(nextParam);
		return true;
	}

	/**
	 * Parses out the username from the username:password string
	 */
	public static String parsekeystoreFile(String string) {
		String[] split = string.split(":"); //$NON-NLS-1$
		if (split.length != 2) {
			throw new IllegalArgumentException(
					Translations.getString("ProxyUserParameter.illegalArg")); //$NON-NLS-1$
		}

		return split[0];
	}

	/**
	 * Parses out the password from the username:password string
	 */
	public static String parsePassword(String string) {
		String[] split = string.split(":"); //$NON-NLS-1$
		return split[1];
	}

}
