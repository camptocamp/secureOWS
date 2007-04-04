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
public class SessionStatusException extends Exception {

    /**
     * 
     */
    public SessionStatusException() {
        super();
    }
    /**
     * @param arg0
     */
    public SessionStatusException(String arg0) {
        super(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     */
    public SessionStatusException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
    /**
     * @param arg0
     */
    public SessionStatusException(Throwable arg0) {
        super(arg0);
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SessionStatusException.java,v $
Revision 1.5  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
