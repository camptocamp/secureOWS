//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/security/owsproxy/DefaultDBConnection.java,v 1.6 2006/07/12 14:46:18 poth Exp $
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
 * @version 1.1, $Revision: 1.6 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @since 1.1
 *  
 */
public class DefaultDBConnection  {
	
	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;
	
	/**
	 * @param driver
	 * @param logon
	 * @param user
	 * @param password
	 */
	public DefaultDBConnection(String driver, String url, String user,
			String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * @return
	 */
	public String getDirver() {
		return driver;
	}

	/**
	 * @param driver
	 * 
	 */
	public void setDriver(String driver){
		this.driver = driver;
	}

	/**
	 * @return
	 */
	public String getUrl(){
		return url;
	}

	/**
	 * @param logon
	 * 
	 */
	public void setUrl(String url){
		this.url = url;
	}

	/**
	 * @return
	 */
	public String getUser(){
		return user;
	}

	/**
	 * @param String
	 * 
	 */
	public void setUser(String user){
		this.user = user;
	}

	/**
	 * @return
	 */
	public String getPassword(){
		return password;
	}

	/**
	 * @param String
	 * 
	 */
	public void setPassword(String password){
		this.password = password;
	}

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DefaultDBConnection.java,v $
Revision 1.6  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
