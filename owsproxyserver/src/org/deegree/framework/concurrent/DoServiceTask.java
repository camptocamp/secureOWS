//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/concurrent/DoServiceTask.java,v 1.1 2006/11/22 14:05:06 schmitz Exp $
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
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstrasse 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.framework.concurrent;

import java.util.concurrent.Callable;

import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceRequest;

/**
 * <code>DoServiceTask</code> is the Callable class that should be used by all services to invoke
 * other services.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.1 $, $Date: 2006/11/22 14:05:06 $
 * @param <T> 
 * 
 * @since 2.0
 */

public class DoServiceTask<T> implements Callable<T> {

    OGCWebService webService;

    OGCWebServiceRequest request;

    /**
     * @param webService
     * @param request
     */
    public DoServiceTask( OGCWebService webService, OGCWebServiceRequest request ) {
        this.webService = webService;
        this.request = request;
    }

    /**
     * @return the result of the execution
     */
    public T call()
                            throws Exception {
        return (T)this.webService.doService( request );
    }
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DoServiceTask.java,v $
Revision 1.1  2006/11/22 14:05:06  schmitz
Moved some createGetMapRequest methods to GetMap.
Added a generic DoServiceTask.



********************************************************************** */