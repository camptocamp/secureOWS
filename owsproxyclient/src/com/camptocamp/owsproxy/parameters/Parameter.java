package com.camptocamp.owsproxy.parameters;

import com.camptocamp.owsproxy.OWSHeadlessClient;

/**
 * An object for use by {@link OWSHeadlessClient} for matching the different flags and performing the action that is required for
 * the flag.  This is typically configuring the {@link OWSHeadlessClient} object
 * 
 * 
 * @author jeichar
 */
public abstract class Parameter {

	private final String flag;

	public Parameter(String flag) {
		this.flag=flag;
	}
	
	public boolean match(String param){
		return param.equalsIgnoreCase(flag);
	}
	
	/**
	 * Performs the action for the parameter.  Typically configures the client.  
	 * 
	 * @param nextParam the next parameter in the list of parameters.  Often a parameter is a flag and a value.  this would be the value
	 * in this circumstance.
	 * 
	 * @param client the client to configure
	 * 
	 * @return true if nextParam was the value and therefore should not be considered in the future
	 */
	public abstract boolean performAction( String nextParam, OWSHeadlessClient client );

}
