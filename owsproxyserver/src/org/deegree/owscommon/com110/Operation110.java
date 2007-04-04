//$Header$
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

package org.deegree.owscommon.com110;

import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.owscommon.OWSMetadata;

/**
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class Operation110 extends Operation {

	private OWSDomainType110[] parameters;
	private OWSDomainType110[] constraints;
	private OWSMetadata[] metadata;

	/**
     * Creates a new <code>Operation110</code> object with information on 
     * <code>DHCP</code>, <code>Parameter</code> and <code>Constraint</code>.
     * 
	 * @param name
	 * @param dcps
	 * @param parameters
	 * @param constraints
	 * @param metadata
	 */
	public Operation110( String name, DCPType[] dcps, OWSDomainType110[] parameters, 
						 OWSDomainType110[] constraints, OWSMetadata[] metadata ) {
		
		super( name, dcps );
		
		if ( parameters == null ) {
			this.parameters = new OWSDomainType110[0];
		} else {
			this.parameters = parameters;
		}
		
		if ( constraints == null ) {
			this.constraints = new OWSDomainType110[0];
		} else {
			this.constraints = constraints;
		}
		
		if ( metadata == null ) {
			this.metadata = new OWSMetadata[0];
		} else {
			this.metadata = metadata;
		}
	}

	/**
	 * Returns an array of all the parameter objects. 
	 * If parameters is null, an array of size 0 is returned.
	 * 
	 * @return Returns all the parameters.
	 */
	public OWSDomainType110[] getParameters110() {
		return parameters;
	}
	
	/**
	 * @param name
	 * @return Returns the parameter object to the given name. 
	 */
	public OWSDomainType110 getParameter110( String name ) {
		
		for ( int i = 0; i < parameters.length; i++ ) {
            if ( name.equals( parameters[i].getName() ) ) {
                return parameters[i];
            }
        }
		return null;
	}
	
	/**
	 * Returns an array of all the constraint objects. 
	 * If constraints is null, an array of size 0 is returned.
	 * 
	 * @return Returns all the constraints.
	 */
	public OWSDomainType110[] getConstraints110() {
		return constraints;
	}
	
	/**
	 * @param name
	 * @return Returns the constraint object to the given name.
	 */
	public OWSDomainType110 getConstraint110( String name ) {
		
		for ( int i = 0; i < constraints.length; i++ ) {
            if ( name.equals( constraints[i].getName() ) ) {
                return constraints[i];
            }
        }
		return null;
	}
	
	/**
	 * Returns an array of all the metadata objects. 
	 * If metadata is null, an array of size 0 is returned.
	 *  
	 * @return Returns the metadata.
	 */
	public OWSMetadata[] getMetadata110() {
		return metadata;
	}	

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/08/24 06:42:48  poth
File header corrected

Revision 1.4  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.2  2005/12/20 10:03:19  mays
change constructor to always create arrays

Revision 1.1  2005/12/19 10:11:25  mays
move class from package ../ogcwebservices/wpvs/operation to package ../owscommon/com110

Revision 1.1  2005/12/16 15:29:38  mays
necessary changes due to new definition of ows:OperationsMetadata

********************************************************************** */
