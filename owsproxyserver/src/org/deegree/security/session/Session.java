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
package org.deegree.security.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.1, $Revision: 1.6 $, $Date: 2006/07/12 14:46:15 $ *  * @since 1.1
 */

public class Session {

    private SessionID sessionID = null;
    private String user = null;
    private Map attributes = Collections.synchronizedMap(new HashMap());
    
    /**
     * creates a session that never expires for an anonymous user
     * 
     * @param user user the session is assigned to
     */
    public Session() {        
        this.sessionID = new SessionID(-1);
    }
    
    /**
     * creates a session that never expires
     * 
     * @param user user the session is assigned to
     */
    public Session(String user) {
        this.sessionID = new SessionID(-1);
        this.user = user;
    }
    
    /**
     * creates a session with a specific lifetime for an anonymous user. 
     * the expiration date will be updated each time a user accesses 
     * his session
     * @param duration
     * @param user
     */
    public Session(int duration) {
        this(null, duration);
    }
    
    
    /**
     * creates a session with a specific lifetime. the expiration date
     * will be updated each time a uses accesses his session
     * @param duration
     * @param user
     */
    public Session(String user, int duration) {
        this.sessionID = new SessionID(duration);
        this.user = user;
    }
    
    /**
     * creates a session with a specific SessionID for an anonymous user. 
     * the expiration date will be updated each time a uses accesses his session
     * 
     * @param sessionID
     * @param user
     */
    public Session(SessionID sessionID) {
        this(null, sessionID);
    }
    
    /**
     * creates a session with a specific SessionID. the expiration date
     * will be updated each time a uses accesses his session
     * 
     * @param sessionID
     * @param user
     */
    public Session(String user, SessionID sessionID) {
        super();
        this.sessionID = sessionID;
        this.user = user;
    }

    /**
     * returns the name user the user who owns the session. returns null
     * if its a session for an anonymous user
     * @return
     * 
     */
    public String getUser() {
        return user;
    }

    
    /**
     * adds an attribute to the session. calling this method will
     * reset the expiration date of the encapsulated sessionID<br>
     * this method throws an exception if the sessinID has been killed
     * or is alive anymore
     * 
     * @param key
     * @param value
     */
    public void addAttribute(Object key, Object value) throws SessionStatusException {
        sessionID.reset();
        attributes.put(key, value);
    }
    
    /**
     * returns the values of the attribute identified by the passed 
     * key. calling this method will reset the expiration date of the 
     * encapsulated sessionID<br>
     * this method throws an exception if the sessinID has been killed
     * or is alive anymore
     * 
     * @param key
     * @return
     */
    public Object getAttribute(Object key) throws SessionStatusException {
        sessionID.reset();
        return attributes.get(key);
    }
    
    /**
     * removes the attribute identified by the passed key from the 
     * session. calling this method will reset the expiration date of the 
     * encapsulated sessionID<br>
     * this method throws an exception if the sessinID has been killed
     * or is alive anymore
     * 
     * @param key
     * @return
     */
    public Object removeAttribute(Object key) throws SessionStatusException {
        sessionID.reset();
        return attributes.remove(key);
    }
    
    /**
     * returns true if the session is still alive or false if the
     * expiration date of the sessionID has been reached
     * @return
     */
    public boolean isAlive() {
        return sessionID.isAlive();
    }

    /**
     * returns the sessionID encapsulated in this session.
     * @return
     * 
     */
    public SessionID getSessionID() {
        return sessionID;
    }

    
    /**
     * kills a Session by marking the encapsulated SessionID as invalid. 
     * A killed SessionID can't be reseted
     */
    public void close() {
        sessionID.close();
    }
    
    /**
     * resets the expiration date of the session
     * 
     */
    public void reset() throws SessionStatusException {
        sessionID.reset();
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Session.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
