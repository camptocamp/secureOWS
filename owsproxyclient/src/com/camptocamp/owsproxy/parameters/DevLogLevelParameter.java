package com.camptocamp.owsproxy.parameters;

import java.util.logging.Level;

import com.camptocamp.owsproxy.OWSHeadlessClient;
import com.camptocamp.owsproxy.Translations;
import com.camptocamp.owsproxy.logging.OWSLogger;

/**
 * Parameter for configuring the ProxyHost URL
 * 
 * @author jeichar
 */
public class DevLogLevelParameter extends Parameter {

	public DevLogLevelParameter() {
		super("-devLogLevel"); //$NON-NLS-1$
	}

	@Override
	public boolean performAction(String nextParam, OWSHeadlessClient client) {
		try {
		Level level = Level.parse(nextParam.toUpperCase());
		OWSLogger.DEV.logger().setLevel(level);
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(Translations.getString("DevLogLevelParamter.badLevel", nextParam)); //$NON-NLS-1$
		}
		return true;
	}

}
