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
public class Request {

    private Condition preConditions;
	private Condition postConditions;
	private String service;
	private String name;
	private boolean any;
    
    /**
     * @param service
     * @param name
     */
    public Request(String service, String name) {
        this.service = service;
        this.name = name;
    }
    
    /**
     * @param service
     * @param name
     */
    public Request(String service, String name, boolean all) {
        this.service = service;
        this.name = name;
        this.any = all;
    }
    
    /**
     * @param preCondition
     * @param postCondition
     * @param service
     * @param name
     */
    public Request(String service, String name, Condition preConditions, 
                   Condition postConditions) {
        super();
        this.preConditions = preConditions;
        this.postConditions = postConditions;
        this.service = service;
        this.name = name;
    }
    
    /**
     * returns the name of the service a <tt>Request</tt> is assigned to
     */
	public String getService(){
		return service;
	}
	
	/**
	 * returns the name request
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * returns the pre-condition assigned to a request. This method may
	 * returns <tt>null</tt> which means no pre conditions are defined
	 * and all http-requests validated against the conditions of this
	 * request will fail. 
	 * @return
	 */
	public Condition getPreConditions(){
		return preConditions;
	}

	/**
	 * sets the pre-condition of a <tt>Request</tt>
	 * @see #getPreConditions()
	 * @param condition
	 */
	public void setPreConditions(Condition condition){
	    this.preConditions = condition;
	}

	/**
	 * returns the post-condition assigned to a request. This method may
	 * returns <tt>null</tt> which means no post-conditions are defined
	 * and all responses to http-requests validated against the conditions 
	 * of this request will fail. 
	 * @return
	 */
	public Condition getPostConditions(){
		return postConditions;
	}

	/**
	 * sets the post-condition of a <tt>Request</tt>
	 * @see #getPostConditions()
	 * @param condition
	 */
	public void setPostConditions(Condition condition){
	    this.postConditions = condition;
	}
	
	/**
     * @return Returns the all.
     */
    public boolean isAny() {
        return any;
    }
    /**
     * @param all The all to set.
     */
    public void setAny(boolean any) {
        this.any = any;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Request.java,v $
Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
