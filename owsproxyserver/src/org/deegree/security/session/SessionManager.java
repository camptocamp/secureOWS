/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.security.session;


/**
 * This exception shall be thrown when a session(ID) will be used that
 * has been expired.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.5 $, $Date: 2006/07/12 14:46:15 $
 * 
 * @since 1.1
 */
public interface SessionManager {
    
    /**
     * returns the session identified by its ID. If no session with the
     * passed ID is known <tt>null</tt> will be returned. If the requested
     * session isn't alive anymore it will be removed from the session
     * manager
     * @param id
     * @return
     */
    public Session getSessionByID(String id) throws SessionStatusException;
    
    /**
     * returns the session assigned to the passed user. If no session is assigend
     * to the passed user <tt>null</tt> will be returned. If the requested
     * session isn't alive anymore it will be removed from the session
     * manager
     * 
     * @param user
     * @return
     */
    public Session getSessionByUser(String user) throws SessionStatusException;
    
    /**
     * adds a session to the session managment. the session will be stored within
     * two lists. one addresses the session with its ID the other with its user
     * name. If the session is anonymous it just will be stored in the first list.  
     * 
     * @param session
     * @throws SessionStatusException
     */
    public void addSession(Session session) throws SessionStatusException;
    
    /**
     * removes a session identified by its ID from the session managment. the 
     * removed session will be returned.
     * 
     * @param id
     * @return
     */
    public Session removeSessionByID(String id);
    
    /**
     * removes all sessions that are expired from the session management
     */
    public void clearExpired();

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SessionManager.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
