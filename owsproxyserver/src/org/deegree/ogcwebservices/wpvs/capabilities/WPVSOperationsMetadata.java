//$$Header$
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

package org.deegree.ogcwebservices.wpvs.capabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.com110.OWSDomainType110;
import org.deegree.owscommon.com110.Operation110;

/**
 * FIXME check spec number! add "Function" to table.
 * 
 * Represents the <code>OperationMetadata</code> part in the capabilities document of a WPVS
 * according to the <code>Web Perspective View Service Implementation Specification 0.0.0</code>.
 * 
 * In addition to the <code>GetCapabilities</code> operation that all <code>OWS 0.3</code> 
 * compliant services must implement, it may define some or all of the following operations: 
 * <table border="1">
 *  <tr>
 *      <th>Name</th>
 *      <th>Mandatory?</th>
 *      <th>Function</th>
 *  <tr>
 *  <tr>
 *      <td><code>GetView</code></td>
 *      <td align="center">yes</td>
 *      <td>&nbsp;</td>
 *  <tr>
 *  <tr>
 *      <td><code>GetDescription</code></td>
 *      <td align="center">&nbsp;</td>
 *      <td>&nbsp;</td>
 *  <tr>
 *  <tr>
 *      <td><code>GetLegendGraphic</code></td>
 *      <td align="center">no</td>
 *      <td>&nbsp;</td>
 *  <tr>
 * </table>
 *  
 * @see org.deegree.ogcwebservices.getcapabilities.OperationsMetadata
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author$
 * 
 * @version 2.0, $Revision$, $Date$
 * 
 * @since 2.0
 */
public class WPVSOperationsMetadata extends OperationsMetadata {

	public static final String GET_VIEW_NAME = "GetView";
	public static final String GET_CAPABILITIES_NAME = "GetCapabilities";
	public static final String GET_DESCRIPTION_NAME = "GetDescription";
	public static final String GET_LEGEND_GRAPHIC_NAME = "GetLegendGraphic";
	
	private Operation110 getCapabilities;
	private Operation110 getView;
	private Operation110 getDescription;
	private Operation110 getLegendGraphic;
	
	private Object extendedCapabilities;
	
	// keys are Strings (the names of the parameters), values are OWSDomainType110 - instances.
	private Map parameters;
    // keys are Strings (the names of constrained domains), values are OWSDomainType110 - instances.
	private Map constraints; 
	
	
	/**
	 * Creates a new <code>WPVSOperationsMetadata</code> instance from the given parameters.
	 * 
	 * @param getView
	 * 				mandatory operation
	 * @param getCapabilities
	 * 				mandatory operation
	 * @param getDescription
	 * 				optional operation; may be null
	 * @param getLegendGraphic
	 * 				optional operation; may be null
	 * @param parameters
	 * 				optional unordered list of parameter valid domains 
	 * 				that each apply to one or more operations which this server interface implements.
	 * @param constraints
	 * 				optional unordered list of valid domain constraints on non-parameter quantaties 
	 * 				that each apply to this server.
	 * @param extendedCapabilities
	 * 				optional; metadata about any additional server abilities.
	 */
	public WPVSOperationsMetadata( Operation110 getCapabilities, Operation110 getView, 
								   Operation110 getDescription, Operation110 getLegendGraphic, 
								   OWSDomainType110[] parameters, OWSDomainType110[] constraints, 
								   Object[] extendedCapabilities ) {
		
		//FIXME this is ugly
		super( null, null, null );
		
		this.getCapabilities = getCapabilities;
		this.getView = getView;
		this.getDescription = getDescription;
		this.getLegendGraphic = getLegendGraphic;
		
		this.extendedCapabilities = extendedCapabilities;
		
		setParameters110( parameters );
        setConstraints110( constraints );
	}
	
	/**
     * Returns all <code>Operations</code> known to the WPVS.
     * 
     * @return
     */
    public Operation[] getAllOperations() {
    	
        List list = new ArrayList( 10 );
        
        list.add( getCapabilities );
        list.add( getView );
        if ( getDescription != null ) {
            list.add( getDescription );
        }
        if ( getLegendGraphic != null ) {
            list.add( getLegendGraphic );
        }

        Operation110 [] ops = new Operation110 [list.size()];
        return (Operation110 []) list.toArray( ops );
    }
	
	/**
	 * @return Returns the <code>GetCapabilities</code> -operation.
	 */
	public Operation getGetCapabilities() {
		return getCapabilities;
	}
	
	/**
     * Sets the configuration for the <code>GetCapabilities</code> -operation.
     * 
     * @param getCapabilitiesOperation
     *            configuration for the <code>GetCapabilities</code> -operation to be set.
     */
    public void setGetCapabilities( Operation110 getCapabilities ) {
        this.getCapabilities = getCapabilities;
    }
    
    /**
	 * @return Returns the getView <code>Operation</code>.
	 */
	public Operation getGetView() {
		return getView;
	}
	
	/**
	 * @param getView The getView to set.
	 */
	public void setGetView( Operation110 getView ) {
		this.getView = getView;
	}
	
	/**
	 * @return Returns the getDescription <code>Operation</code>.
	 */
	public Operation getGetDescription() {
		return getDescription;
	}

	/**
	 * @param getDescription The getDescription to set.
	 */
	public void setGetDescription( Operation110 getDescription ) {
		this.getDescription = getDescription;
	}

	/**
	 * @return Returns the getLegendGraphic <code>Operation</code>.
	 */
	public Operation getGetLegendGraphic() {
		return getLegendGraphic;
	}
	
	/**
	 * @param getLegendGraphic The getLegendGraphic to set.
	 */
	public void setGetLegendGraphic( Operation110 getLegendGraphic ) {
		this.getLegendGraphic = getLegendGraphic;
	}

	/**
	 * @return Returns the extendedCapabilities.
	 */
	public Object getExtendedCapabilities() {
		return extendedCapabilities;
	}

	/**
	 * @param extendedCapabilities The extendedCapabilities to set.
	 */
	public void setExtendedCapabilities( Object extendedCapabilities ) {
		this.extendedCapabilities = extendedCapabilities;
	}
	
	/**
     * Returns a list of parameters assigned directly to the WPVSOperationsMetadata.
     * 
     * @return
     */
    public OWSDomainType110[] getParameters110() {
        OWSDomainType110[] op = new OWSDomainType110[ parameters.size() ];
        return (OWSDomainType110[]) parameters.values().toArray(op);
    }

    /**
     * Adds a parameter to the WPVSOperationsMetadata.
     * 
     * @param parameter
     */
    public void addParameter110( OWSDomainType110 parameter ) {
        parameters.put(parameter.getName(), parameter);
    }

    /**
     * Removes a parameter from the WPVSOperationsMetadata.
     * 
     * @param parameter
     */
    public OWSDomainType110 removeParameter110( String name ) {
        return ( OWSDomainType110 )parameters.remove( name );
    }

    /**
     * Sets a complete list of parameters to the WPVSOperationMetadata.
     * 
     * @param parameters
     */
    public void setParameters110( OWSDomainType110[] parameters ) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        } else {
            this.parameters.clear();
        }
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                addParameter110(parameters[i]);
            }
        }
    }
    
    /**
     * @return Returns the constraints.
     */
    public OWSDomainType110[] getConstraints110() {
    	OWSDomainType110[] op = new OWSDomainType110[ constraints.size() ];
    	return ( OWSDomainType110[] ) constraints.values().toArray( op );
    }
	
    /**
     * Adds a constraint.
     * 
	 * @param constraint of OWSDomainType110
	 */
	private void addConstraints110( OWSDomainType110 constraint ) {
		constraints.put( constraint.getName(), constraint );
	}
	
	/**
     * Sets the constraints of the <code>WPVSOperationMetadata</code>.
     * 
     * @param constraints
     *            may be null
     */
	private void setConstraints110( OWSDomainType110[] constraints ) {
        if ( this.constraints == null ) {
            this.constraints = new HashMap();
        } else {
            this.constraints.clear();
        }
        if ( constraints != null ) {
            for ( int i = 0; i < constraints.length; i++ ) {
                addConstraints110( constraints[i] );
            }
        }
	}
	
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.9  2006/08/29 19:54:14  poth
footer corrected

Revision 1.8  2006/08/24 06:42:15  poth
File header corrected

Revision 1.7  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.6  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.5  2005/12/20 13:15:32  mays
clean up

Revision 1.4  2005/12/20 10:00:30  mays
minor changes in method names and calls

Revision 1.3  2005/12/19 10:10:15  mays
update of imports

Revision 1.2  2005/12/16 15:29:38  mays
necessary changes due to new definition of ows:OperationsMetadata

Revision 1.1  2005/12/09 14:05:05  mays
first implementation of new class

********************************************************************** */
