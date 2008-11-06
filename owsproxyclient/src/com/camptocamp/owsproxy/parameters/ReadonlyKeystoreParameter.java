package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class ReadonlyKeystoreParameter extends Parameter {

	public ReadonlyKeystoreParameter() {
		super("-readonlyKeystore"); //$NON-NLS-1$
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			int port = Integer.parseInt(nextParam);
			client.getParams().proxyPort = port;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					Translations.getString("ProxyPortParameter.portNotInteger")); //$NON-NLS-1$
		}

		return true;
	}

}
