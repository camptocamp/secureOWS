//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/getcoverage/ResultCoverage.java,v 1.8 2006/05/18 16:50:04 poth Exp $
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
package org.deegree.ogcwebservices.wcs.getcoverage;

import org.deegree.datatypes.Code;

/**
 * Encapsulates the result of a GetCoverage request. In addition to the * data/coverage itself informations about the desired output format * and the type of the coverage are included. *  * @version $Revision: 1.8 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.8 $, $Date: 2006/05/18 16:50:04 $ *  * @since 2.0
 */

public class ResultCoverage {
    
    private Object coverage = null;
    private Class coverageType = null;
    private Code desiredOutputFormat = null;
    private GetCoverage request = null;

    
    /**
     * @param coverage
     * @param coverageType
     * @param desiredOutputFormat
     */
    public ResultCoverage(Object coverage, Class coverageType,
                          Code desiredOutputFormat, GetCoverage request) {
        super();
        this.coverage = coverage;
        this.coverageType = coverageType;
        this.desiredOutputFormat = desiredOutputFormat;
        this.request = request;
    }
    
    

    public GetCoverage getRequest() {
        return request;
    }


    /**
     * @return Returns the coverage.
     * 
     */
    public Object getCoverage() {
        return coverage;
    }

    /**
     * @return Returns the coverageType.
     * 
     */
    public Class getCoverageType() {
        return coverageType;
    }

    /**
     * @return Returns the desiredOutputFormat.
     * 
     */
    public Code getDesiredOutputFormat() {
        return desiredOutputFormat;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ResultCoverage.java,v $
Revision 1.8  2006/05/18 16:50:04  poth
*** empty log message ***

Revision 1.7  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.6  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.4  2006/03/02 11:06:04  poth
*** empty log message ***

Revision 1.3  2005/04/06 10:58:15  poth
no message

Revision 1.2  2005/01/18 22:08:55  poth
no message

Revision 1.2  2004/07/14 15:34:24  ap
no message


********************************************************************** */