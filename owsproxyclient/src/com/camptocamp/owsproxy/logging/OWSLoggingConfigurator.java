/**
 * 
 */
package com.camptocamp.owsproxy.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Configures the LogManager to read properties from the log file specified in the class or the default log file shipped with 
 * the application
 * 
 * @author jeichar
 */
public class OWSLoggingConfigurator {
	{
		try {
			InputStream configurationFile = OWSLogger.class
					.getResourceAsStream("logging.properties");
			LogManager.getLogManager().readConfiguration(configurationFile);
		} catch (Exception e) {
			try {
				System.getProperties().remove("java.util.logging.config.class");
				LogManager.getLogManager().readConfiguration();
			} catch (IOException e1) {
				// can't further handle issue so ignore it
			}
			OWSLogger.DEV.log(Level.SEVERE, "default log configuration is not available!", e);
		}

	}
}
