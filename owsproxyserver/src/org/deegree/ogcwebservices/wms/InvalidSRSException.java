//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/InvalidSRSException.java,v 1.3 2006/11/22 15:38:31 schmitz Exp $
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
package org.deegree.ogcwebservices.wms;

import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.InconsistentRequestException;

/**
 * <code>InvalidSRSException</code> is true to its name and uses the InvalidSRS exception code.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/11/22 15:38:31 $
 * 
 * @since 2.0
 */

public class InvalidSRSException extends InconsistentRequestException {

    private static final long serialVersionUID = -2954976910068274489L;

    /**
     * @param message
     */
    public InvalidSRSException( String message ) {
        super( message );
        code = ExceptionCode.INVALID_SRS;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: InvalidSRSException.java,v $
 Revision 1.3  2006/11/22 15:38:31  schmitz
 Fixed more exception handling, especially for the GetFeatureInfo request.



 ********************************************************************** */