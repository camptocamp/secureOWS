package com.camptocamp.owsproxy.parameters;

import java.util.logging.Level;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.logging.OWSLogger;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class DevLogLevelParameter extends Parameter {

	public DevLogLevelParameter() {
		super("-devLogLevel");
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		Level level = Level.parse(nextParam.toUpperCase());
		OWSLogger.DEV.logger().setLevel(level);
		return true;
	}

}
