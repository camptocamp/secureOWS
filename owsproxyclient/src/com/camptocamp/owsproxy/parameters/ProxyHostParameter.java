package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class ProxyHostParameter extends Parameter {

	public ProxyHostParameter() {
		super("-proxyHost");
	}
	
	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		client.getParams().proxyHost = nextParam;
		return true;
	}

}
