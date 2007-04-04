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

package org.deegree.ogcwebservices.wass.saml;

import java.net.URI;

/**
 * Encapsulated data: Condition element
 * 
 * Namespace: http://urn:oasis:names:tc.SAML:1.0:assertion
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * test
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision: 1.8 $ $Date: 2006/06/19 12:47:09 $
 * 
 */
public class Condition {

    private URI audience = null;

    private boolean doNotCache = false;

    /**
     * @param uri
     */
    public Condition( URI uri ) {
        audience = uri;
    }

    /**
     * @param doNotCache
     */
    public Condition( boolean doNotCache ) {
        this.doNotCache = doNotCache;
    }

    /**
     * @return whether doNotCache is set
     */
    public boolean isDoNotCache() {
        return doNotCache;
    }

    /**
     * @return whether this is an audience restriction
     */
    public boolean isAudienceRestriction() {
        return audience != null;
    }

    /**
     * @return the audience
     */
    public URI getAudience() {
        return audience;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Condition.java,v $
 Revision 1.8  2006/06/19 12:47:09  schmitz
 Updated the documentation, fixed the warnings and implemented logging everywhere.

 Revision 1.7  2006/05/30 09:01:09  deshmukh
 docu test

<<<<<<< Condition.java
=======
 Revision 1.6  2006/05/30 08:59:22  schmitz
 fdtdfg

 Revision 1.5  2006/05/30 08:58:08  deshmukh
 docu test

>>>>>>> 1.6
 Revision 1.4  2006/05/30 08:56:49  deshmukh
 docu test

 Revision 1.3  2006/05/30 08:56:25  deshmukh
 docu test

 Revision 1.2  2006/05/29 16:24:59  bezema
 Rearranging the layout of the wss and creating the doservice classes. The WSService class is implemented as well

 Revision 1.1  2006/05/29 12:00:58  bezema
 Refactored the security and authentication webservices into one package WASS (Web Authentication -and- Security Services), also created a common package and a saml package which could be updated to work in the future.

 Revision 1.1  2006/05/15 09:54:16  bezema
 New approach to the nrw:gdi specs. Including ows_1_0 spec and saml spec


 ********************************************************************** */