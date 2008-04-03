package com.camptocamp.owsproxy.parameters;

import java.io.FileNotFoundException;

import com.camptocamp.owsproxy.OWSHeadlessClient;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class UserLogFileParameter extends Parameter {

	public UserLogFileParameter() {
		super("-userLogFile");
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			DevLogFileParameter.lookupOWSLogHandler().setUserLogFile(nextParam);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(nextParam+" cannot be created as a file");
		}
		return true;
	}

}
