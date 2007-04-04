//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/metadata/iso19115/Constraints.java,v 1.2 2006/08/24 06:40:27 poth Exp $
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
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.model.metadata.iso19115;

import java.util.Date;
import java.util.List;

/**
 * <code>Constraints</code> is a class that encapsulates metadata about various
 * constraints that can be applied to an OGC web service. The stored data is
 * more general than the simple access constraints specified in the OWS common
 * specification version 1.0.0 and therefore includes a lot more fields.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:40:27 $
 * 
 * @since 2.0
 */

public class Constraints {
    
    private String fees = null;
    
    private Date plannedAvailableDateTime = null;
    
    private String orderingInstructions = null;
    
    private String turnaround = null;
    
    private List<String> useLimitations = null;
    
    private List<String> accessConstraints = null;
    
    private List<String> useConstraints = null;
    
    private List<String> otherConstraints = null;

    
    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param fees
     * @param plannedAvailableDateTime
     * @param orderingInstructions
     * @param turnaround
     * @param useLimitations
     * @param accessConstraints
     * @param useConstraints
     * @param otherConstraints
     */
    public Constraints( String fees, Date plannedAvailableDateTime,
                        String orderingInstructions, String turnaround,
                        List<String> useLimitations, List<String> accessConstraints,
                        List<String> useConstraints, List<String> otherConstraints ) {
        this.fees = fees;
        this.plannedAvailableDateTime = plannedAvailableDateTime;
        this.orderingInstructions = orderingInstructions;
        this.turnaround = turnaround;
        this.useLimitations = useLimitations;
        this.accessConstraints = accessConstraints;
        this.useConstraints = useConstraints;
        this.otherConstraints = otherConstraints;
    }
    
    /**
     * @return Returns the accessConstraints.
     */
    public List<String> getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * @return Returns the fees.
     */
    public String getFees() {
        return fees;
    }

    /**
     * @return Returns the orderingInstructions.
     */
    public String getOrderingInstructions() {
        return orderingInstructions;
    }

    /**
     * @return Returns the otherConstraints.
     */
    public List<String> getOtherConstraints() {
        return otherConstraints;
    }

    /**
     * @return Returns the plannedAvailableDateTime.
     */
    public Date getPlannedAvailableDateTime() {
        return plannedAvailableDateTime;
    }

    /**
     * @return Returns the turnaround.
     */
    public String getTurnaround() {
        return turnaround;
    }

    /**
     * @return Returns the useConstraints.
     */
    public List<String> getUseConstraints() {
        return useConstraints;
    }

    /**
     * @return Returns the useLimitations.
     */
    public List<String> getUseLimitations() {
        return useLimitations;
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Constraints.java,v $
Revision 1.2  2006/08/24 06:40:27  poth
File header corrected

Revision 1.1  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.



********************************************************************** */