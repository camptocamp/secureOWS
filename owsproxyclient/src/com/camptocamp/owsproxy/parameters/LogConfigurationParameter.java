package com.camptocamp.owsproxy.parameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.LogManager;

import com.camptocamp.owsproxy.OWSHeadlessClient;

public class LogConfigurationParameter extends Parameter {

	public LogConfigurationParameter() {
		super("-logConf");
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream(nextParam));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Logging configuration file cannot be read");
		} catch (IOException e) {
			throw new IllegalArgumentException("Logging configuration file cannot be read");
		}
		return true;
	}

}
