package com.camptocamp.owsproxy.parameters;

import java.io.FileNotFoundException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.logging.OWSLogHandler;
import com.camptocamp.owsproxy.logging.OWSLogger;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class DevLogFileParameter extends Parameter {

	public DevLogFileParameter() {
		super("-devLogFile");
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
			lookupOWSLogHandler().setDevLogFile(nextParam);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(nextParam
					+ " cannot be created as a file");
		}
		return true;
	}

	/**
	 * Looks for the OWSLogHandler that is being used by the loggers or returns null if non are found
	 */
	public static OWSLogHandler lookupOWSLogHandler() {
		Logger logger = OWSLogger.DEV.logger();
		while (logger != null) {
			Handler[] handlers = logger.getHandlers();

			for (Handler handler : handlers) {
				if (handler instanceof OWSLogHandler) {
					return (OWSLogHandler) handler;
				}
			}
			logger = logger.getParent();
		}
		return null;
	}

}
