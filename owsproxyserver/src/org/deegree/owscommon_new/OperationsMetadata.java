//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/OperationsMetadata.java,v 1.3 2006/08/29 13:02:32 poth Exp $
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
package org.deegree.owscommon_new;

import java.util.List;

import org.deegree.datatypes.QualifiedName;

/**
 * <code>OperationsMetadata</code> stores the contents of a OperationsMetadata
 * element according to the OWS common specification version 1.0.0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/08/29 13:02:32 $
 * 
 * @since 2.0
 */

public class OperationsMetadata {

    private List<Parameter> parameters;
    
    private List<DomainType> constraints;
    
    private List<Operation> operations;
    
    private List<Object> operatesOn;

    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param parameters
     * @param constraints
     * @param operations
     * @param operatesOn 
     */
    public OperationsMetadata( List<Parameter> parameters, List<DomainType> constraints,
                               List<Operation> operations, List<Object> operatesOn ) {
        this.parameters = parameters;
        this.constraints = constraints;
        this.operations = operations;
        this.operatesOn = operatesOn;
    }
    
    /**
     * @return Returns the constraints.
     */
    public List<DomainType> getConstraints() {
        return constraints;
    }

    /**
     * @return Returns the operations.
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * @return Returns the parameters.
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
    
    /**
     * @param name
     * @return the <code>DomainType</code> with the specified name or null, if there is no
     * constraint with that name.
     */
    public DomainType getConstraint( QualifiedName name ) {
        for( DomainType constraint : constraints ) {
            if( constraint.getName().equals( name ) ) {
                return constraint;
            }
        }
        
        return null;
    }
    
    /**
     * @param name
     * @return the <code>Parameter</code> with the specified name or null, if there is no
     * parameter with that name. This method only tests Parameters that are <code>DomainType</code>s.
     */
    public Parameter getParameter( QualifiedName name ) {
        for( Parameter parameter : parameters ) {
            if( parameter instanceof DomainType ) {
                if( ( (DomainType) parameter ).getName().equals( name ) ) {
                    return parameter;
                }
            }
        }
        
        return null;
    }

    /**
     * @param name
     * @return the <code>Operation</code> with the specified name or null, if there is no
     * operation with that name.
     */
    public Operation getOperation( QualifiedName name ) {        
        for( Operation operation : operations ) {
            if( operation.getName().equals( name ) ) {
                return operation;
            }
        }
        
        return null;
    }

    /**
     * @return Returns the operatesOn.
     */
    public List<Object> getOperatesOn() {
        return operatesOn;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OperationsMetadata.java,v $
Revision 1.3  2006/08/29 13:02:32  poth
code formating

Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.1  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.



********************************************************************** */