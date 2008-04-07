package com.camptocamp.owsproxy.parameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;

public class LogConfigurationParameter extends Parameter {

	public LogConfigurationParameter() {
		super("-logConf"); //$NON-NLS-1$
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream(nextParam));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(Translations.getString("LogConfigurationParameter.cantRead")); //$NON-NLS-1$
		} catch (IOException e) {
			throw new IllegalArgumentException(Translations.getString("LogConfigurationParameter.cantRead")); //$NON-NLS-1$
		}
		return true;
	}

}
