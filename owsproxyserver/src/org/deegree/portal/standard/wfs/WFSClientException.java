//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/wfs/WFSClientException.java,v 1.1 2006/11/03 08:36:34 mays Exp $
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

package org.deegree.portal.standard.wfs;

import org.deegree.framework.util.StringTools;
import org.deegree.portal.PortalException;

/**
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version $Revision: 1.1 $, $Date: 2006/11/03 08:36:34 $
 */
public class WFSClientException extends PortalException {

    private static final long serialVersionUID = -104053648764163087L;

    /**
     * Creates a new instance of <code>WFSClientException</code> without a detailed message.
     */
    public WFSClientException() {
        st = "WFSClientException";
    }

    /**
     * Constructs an instance of <code>WFSClientException</code> with the specified message.
     * @param message The detailed message
     */
    public WFSClientException( String message ) {
        super( message );
    }

    /**
     * Constructs an instance of <code>CatalogClientException</code> with the specified message.
     * @param message The detailed message
     * @param e
     */
    public WFSClientException( String message, Exception e ) {
        this( message );
        st = StringTools.stackTraceToString( e.getStackTrace() );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:

 $Log: WFSClientException.java,v $
 Revision 1.1  2006/11/03 08:36:34  mays
 add new listener for wfs gazetteer dlient (to be tested)

 ********************************************************************** */
