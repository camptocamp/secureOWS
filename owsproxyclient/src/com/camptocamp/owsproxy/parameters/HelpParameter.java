package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;

public class HelpParameter extends Parameter {

	public HelpParameter() {
		super("-h"); //$NON-NLS-1$
	}

	@Override
	public boolean match(String param) {
		return super.match(param)||"-help".equalsIgnoreCase(param); //$NON-NLS-1$
	}
	
	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		// just throw exception because when an exception is encountered the help is printed out
		throw new IllegalArgumentException(""); //$NON-NLS-1$
	}

}
