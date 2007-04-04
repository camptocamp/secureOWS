// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/CatalogClientException.java,v 1.8 2006/06/23 13:39:01 mays Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de 

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
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

package org.deegree.portal.standard.csw;

import org.deegree.framework.util.StringTools;
import org.deegree.portal.PortalException;

/**
 * A <code>${type_name}</code> class.<br/>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mays $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/06/23 13:39:01 $
 *
 * @since 1.1
 */
public class CatalogClientException extends PortalException {
        
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>CatalogClientException</code> without detail message.
     */
    public CatalogClientException() {
        st = "CatalogClientException";
    }
     
    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CatalogClientException(String msg) {
        super( msg );
    }
    
    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified detail message.
     * 
     * @param msg the detail message.
     * @param e
     */
    public CatalogClientException(String msg, Exception e) {
        this( msg );
        st = StringTools.stackTraceToString( e.getStackTrace() );
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogClientException.java,v $
Revision 1.8  2006/06/23 13:39:01  mays
add/update csw files

********************************************************************** */