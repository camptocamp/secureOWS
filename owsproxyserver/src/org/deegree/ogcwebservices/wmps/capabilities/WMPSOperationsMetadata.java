//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/capabilities/WMPSOperationsMetadata.java,v 1.18 2006/10/22 20:36:05 poth Exp $
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
package org.deegree.ogcwebservices.wmps.capabilities;

import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSDomainType;

/**
 * 
 * Represents the <code>OperationMetadata</code> part in the capabilities document of a WMPS. The
 * <code>GetCapabilities</code> operation may define the following operation:
 * <ul>
 * <li>PrintMap
 * </ul>
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0.
 * 
 */

public class WMPSOperationsMetadata extends OperationsMetadata {

    private static final long serialVersionUID = -3294700326681151524L;

    private static final ILogger LOG = LoggerFactory.getLogger( WMPSOperationsMetadata.class );

    private Operation printMap;

    /**
     * Constructs a new <code>WMPSOperationsMetadata</code> instance from the given parameters.
     * 
     * @param getCapabilities
     * @param printMap
     * @param parameters
     * @param constraints
     */
    public WMPSOperationsMetadata( Operation getCapabilities, Operation printMap,                                  
                                   OWSDomainType[] parameters,  OWSDomainType[] constraints ) {
        super( getCapabilities, parameters, constraints );
        this.printMap = printMap;
    }

    /**
     * Constructs a new <code>WMPSOperationsMetadata</code> instance from the given parameters.
     * 
     * @param getCapabilities
     * @param printMap
     */
    public WMPSOperationsMetadata( Operation getCapabilities, Operation printMap ) {
        super( getCapabilities, null, null );
        this.printMap = printMap;
    }

    /**
     * Returns all <code>Operations</code> known to the WMPS. Currently only
     * <ul>
     * <li>GetCapabilities
     * <li>PrintMap
     * </ul>
     * supported
     * 
     * @return Operation[]
     */
    @Override
    public Operation[] getOperations() {
        LOG.entering();

        LOG.logDebug( "Getting the list of operations known to the WMPS as an array." );
        List<Operation> list = new ArrayList<Operation>( 10 );
        list.add( this.getCapabilitiesOperation );
        if ( this.printMap != null ) {
            list.add( this.printMap );
        }
       
        Operation[] ops = new Operation[list.size()];
        LOG.exiting();
        return list.toArray( ops );
    }

    /**
     * Returns the print map operation.
     * 
     * @return Operation
     */
    public Operation getPrintMap() {
        return this.printMap;
    }

    /**
     * Sets the print map operation
     * 
     * @param printMap
     */
    public void setPrintMap( Operation printMap ) {
        this.printMap = printMap;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: WMPSOperationsMetadata.java,v $
 * Revision 1.18  2006/10/22 20:36:05  poth
 * operations not supported by WMPS removed
 *
 * Revision 1.17  2006/10/22 20:32:08  poth
 * support for vendor specific operation GetScaleBar removed
 * Changes to this class. What the people have been up to:
 * Revision 1.16  2006/08/10 07:11:35  deshmukh
 * WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to:
 * Revision 1.15 2006/08/01 13:41:48
 * deshmukh The wmps configuration has been
 * modified and extended. Also fixed the javadoc. Changes to this class. What the people have been
 * up to: Revision 1.14 2006/07/12 14:46:19 poth comment footer added
 * 
 **************************************************************************************************/
