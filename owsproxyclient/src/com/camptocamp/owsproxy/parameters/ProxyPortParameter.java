package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class ProxyPortParameter extends Parameter {

	public ProxyPortParameter() {
		super("-proxyPort");
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			int port = Integer.parseInt(nextParam);
			client.getParams().proxyPort = port;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"The port number must be an integer");
		}

		return true;
	}

}
