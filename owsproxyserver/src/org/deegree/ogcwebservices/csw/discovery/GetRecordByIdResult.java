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
package org.deegree.ogcwebservices.csw.discovery;

import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;
import org.w3c.dom.Element;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/10/18 17:00:55 $
 *
 * @since 2.0
 */
public class GetRecordByIdResult extends DefaultOGCWebServiceResponse {
    
    private Element record = null;

    /**
     * 
     * @param request
     * @param record result node
     */
    public GetRecordByIdResult(GetRecordById request,  Element record) {
        super( request);
        this.record = record;
    }

    /**
     * @return the record node that is the result content of a 
     *          GetRecordById request 
     */
    public Element getRecord() {
        return record;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetRecordByIdResult.java,v $
Revision 1.6  2006/10/18 17:00:55  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.5  2006/07/11 07:10:11  poth
footer added/corrected


********************************************************************** */