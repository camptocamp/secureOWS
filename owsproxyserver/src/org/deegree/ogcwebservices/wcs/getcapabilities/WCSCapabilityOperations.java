// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/getcapabilities/WCSCapabilityOperations.java,v 1.6 2006/04/06 20:25:31 poth Exp $
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
package org.deegree.ogcwebservices.wcs.getcapabilities;

import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;

/**
 * @version $Revision: 1.6 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/06 20:25:31 $ *  * @since 2.0
 */

public class WCSCapabilityOperations extends OperationsMetadata {

    /**
     * 
     * @uml.property name="describeCoverageOperation"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Operation describeCoverageOperation = null;

    /**
     * 
     * @uml.property name="getCoverageOperation"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Operation getCoverageOperation = null;


    
    /**
     * @param capabilitiesOperations
     * @param describeCoverageOperation
     * @param getCoverageOperation
     */
    public WCSCapabilityOperations(Operation capabilitiesOperations, 
                                   Operation describeCoverageOperation, 
                                   Operation getCoverageOperation) {
        super(capabilitiesOperations, null, null);
        this.describeCoverageOperation = describeCoverageOperation;
        this.getCoverageOperation = getCoverageOperation;
    }

    /**
     * @return Returns the describeCoverageOperation.
     * 
     * @uml.property name="describeCoverageOperation"
     */
    public Operation getDescribeCoverageOperation() {
        return describeCoverageOperation;
    }

    /**
     * @param describeCoverageOperation The describeCoverageOperation to set.
     * 
     * @uml.property name="describeCoverageOperation"
     */
    public void setDescribeCoverageOperation(Operation describeCoverageOperation) {
        this.describeCoverageOperation = describeCoverageOperation;
    }

    /**
     * @return Returns the getCoverageOperation.
     * 
     * @uml.property name="getCoverageOperation"
     */
    public Operation getGetCoverageOperation() {
        return getCoverageOperation;
    }

    /**
     * @param getCoverageOperation The getCoverageOperation to set.
     * 
     * @uml.property name="getCoverageOperation"
     */
    public void setGetCoverageOperation(Operation getCoverageOperation) {
        this.getCoverageOperation = getCoverageOperation;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: WCSCapabilityOperations.java,v $
   Revision 1.6  2006/04/06 20:25:31  poth
   *** empty log message ***

   Revision 1.5  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.4  2006/03/30 21:20:28  poth
   *** empty log message ***

   Revision 1.3  2005/02/22 16:56:11  mschneider
   Removed references to ParameterValue. Replaced by new Bean OWSDomainType.

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.4  2004/07/12 06:12:11  ap
   no message

   Revision 1.3  2004/06/22 13:25:14  ap
   no message

   Revision 1.2  2004/06/09 15:44:09  ap
   no message

   Revision 1.1  2004/06/09 15:30:37  ap
   no message

   Revision 1.1  2004/06/03 09:02:20  ap
   no message

   
********************************************************************** */