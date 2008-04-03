package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;

public class HelpParameter extends Parameter {

	public HelpParameter() {
		super("-h");
	}

	@Override
	public boolean match(String param) {
		return super.match(param)||"-help".equalsIgnoreCase(param);
	}
	
	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		// just throw exception because when an exception is encountered the help is printed out
		throw new IllegalArgumentException("");
	}

}
