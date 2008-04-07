package com.camptocamp.owsproxy.parameters;

import java.io.FileNotFoundException;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;

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
			throw new IllegalArgumentException(Translations.getString("cantCreateFile", nextParam));
		}
		return true;
	}

}
