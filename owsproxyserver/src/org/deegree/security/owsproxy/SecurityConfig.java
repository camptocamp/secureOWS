/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/deegree/
lat/lon GmbH
http://www.lat-lon.de

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Contact:

Andreas Poth
lat/lon GmbH
Aennchenstr. 19
53115 Bonn
Germany
E-Mail: poth@lat-lon.de

Prof. Dr. Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: greve@giub.uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.security.owsproxy;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.5 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @since 1.1
 *  
 */
public class SecurityConfig {

    private int readWriteTimeout = 0;
    private String registryClass = null;
    private RegistryConfig registryConfig = null;
    private AuthentificationSettings authsettings = null;
	
    /**
     * @param readWriteTimeout
     * @param registryClass
     * @param registryConfig
     * @param authSet
     */
    public SecurityConfig(String registryClass, int readWriteTimeout, 
                          RegistryConfig registryConfig, 
                          AuthentificationSettings authSet) {
        this.readWriteTimeout = readWriteTimeout;
        this.registryClass = registryClass;
        this.registryConfig = registryConfig;
        this.authsettings = authSet;
    }
    
    /**
     * 
     * @return
     */
	public int getReadWriteTimeout(){
		return readWriteTimeout;
	}

	/**
	 * @param readWriteTimeout
	 * 
	 */
	public void setReadWriteTimeout(int readWriteTimeout){
	    this.readWriteTimeout = readWriteTimeout;
	}

	/**
	 * 
	 * @return
	 */
	public String getRegistryClass(){
		return registryClass;
	}

	/**
	 * @param registryClass
	 * 
	 */
	public void setRegistryClass(String registryClass){
	    this.registryClass = registryClass;
	}
	/**
	 * 
	 * @return
	 */
	public RegistryConfig getRegistryConfig(){
		return registryConfig;
	}

	/**
	 * @param registryConfig
	 * 
	 */
	public void setRegistryConfig(RegistryConfig registryConfig){
	    this.registryConfig = registryConfig;
	}

    /**
     * 
     * @return
     */
    public AuthentificationSettings getAuthsettings() {
        return authsettings;
    }
    
    /**
     * 
     * @param authsettings
     */
    public void setAuthsettings(AuthentificationSettings authsettings) {
        this.authsettings = authsettings;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SecurityConfig.java,v $
Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
