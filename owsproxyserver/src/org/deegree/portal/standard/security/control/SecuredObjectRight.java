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
53177 Bonn
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
package org.deegree.portal.standard.security.control;

import java.util.Map;

import org.deegree.security.drm.model.SecuredObject;

/**
 * Container that encapsulates information about a certain <code>Right</code>
 * on a certain <code>SecuredObject</code>.
 */
public class SecuredObjectRight {

    private boolean accessible;

    private SecuredObject securedObject;

    private Map constraints;

    public SecuredObjectRight(boolean accessible, SecuredObject securedObject,
            Map constraints) {
        this.accessible = accessible;
        this.securedObject = securedObject;
        this.constraints = constraints;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public SecuredObject getSecuredObject() {
        return securedObject;
    }

    public Map getConstraints() {
        return constraints;
    }

    public String toString() {
        return "SecuredObjectRight: accessible=" + accessible
                + ", securedObject=" + securedObject + " , constraints="
                + constraints;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SecuredObjectRight.java,v $
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
