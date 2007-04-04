//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/GetFeatureWithLock.java,v 1.17 2006/10/12 16:24:00 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import java.util.Map;


/**
 * Represents a <code>GetFeatureWithLock</code> request to a web feature service.
 * <p>
 * This is identical to a {@link GetFeature} request, except that the features matching the
 * request will be locked.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.17 $
 */
public class GetFeatureWithLock extends GetFeature {
    
    private static final long serialVersionUID = 8885456550385437651L;

    /**
     * Creates a new <code>GetFeatureWithLock</code> instance.
     *
     * @param version 
     * @param id 
     * @param handle 
     * @param vendorSpecificParameter 
     * @param resultType 
     * @param outputFormat 
     * @param maxFeatures 
     * @param startPosition 
     * @param traverseXLinkDepth 
     * @param traverseXLinkExpiry 
     * @param queries 
     */
    GetFeatureWithLock(String version, String id, String handle, 
                       RESULT_TYPE resultType, String outputFormat, 
                       int maxFeatures, int startPosition, int traverseXLinkDepth, 
                       int traverseXLinkExpiry,  Query[] queries,
                       Map<String,String> vendorSpecificParameter ) {
        super(version, id, handle, resultType, outputFormat, maxFeatures,  
              startPosition, traverseXLinkDepth, traverseXLinkExpiry, 
              queries, vendorSpecificParameter);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String ret = this.getClass().getName();
        ret += super.toString();
        return ret;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetFeatureWithLock.java,v $
Revision 1.17  2006/10/12 16:24:00  mschneider
Javadoc + compiler warning fixes.

Revision 1.16  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */