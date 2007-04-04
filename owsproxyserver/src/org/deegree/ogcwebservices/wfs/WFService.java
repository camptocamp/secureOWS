//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/WFService.java,v 1.45 2006/11/09 17:46:36 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs;

import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.trigger.TriggerProvider;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.operation.DescribeFeatureType;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.GetFeatureWithLock;
import org.deegree.ogcwebservices.wfs.operation.LockFeature;
import org.deegree.ogcwebservices.wfs.operation.WFSGetCapabilities;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;

/**
 * This class implements access to the methods defined in the OGC WFS 1.1.0 specification.
 * Requests are passed through to both doService(..) methods defined in
 * <code>org.deegree.ogcwebservices.OGCWebService</code>. (the first one 'doService(
 * OGCWebServiceEvent )' acts in an asynchronous way. The second one acts synchronously.
 * If the used backend does not support atomic transactions, it is possible that one part fails
 * while another works well. Depending on definitions made in the OGC WFS 1.1.1 specification in
 * this case it is possible that even if a sub part of the request fails no exception will be
 * thrown. In this case the result objects contains informations which parts of the request worked
 * and which didn't.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.45 $, $Date: 2006/11/09 17:46:36 $
 *  
 * @see OGCWebService
 */
public class WFService implements OGCWebService {

    /** Only OGC standard version currently implemented by this service. */
    public static final String VERSION = "1.1.0";

    private static final TriggerProvider TP = TriggerProvider.create( WFService.class );

    private WFSConfiguration configuration;

    /**
     * Creates a new instance of <code>WFService</code> with the given configuration.
     * <p>
     * Note that the configuration must already be validated.
     * 
     * @param configuration
     */
    WFService( WFSConfiguration configuration ) {
        this.configuration = configuration;
    }

    /**
     * Returns the capabilities of the <code>WFService</code>.
     * 
     * @return the capabilities, this is actually a <code>WFSConfiguration</code> instance
     */
    public WFSCapabilities getCapabilities() {
        return this.configuration;
    }

    /**
     * Performs the handling of the passed OGCWebServiceEvent directly and returns the result to the
     * calling class/ method.
     * 
     * @param request
     *            WFS request to perform
     * 
     * @throws OGCWebServiceException
     */
    public Object doService( OGCWebServiceRequest request )
                            throws OGCWebServiceException {

        request = (OGCWebServiceRequest) TP.doPreTrigger( this, request )[0];

        Object response = null;
        if ( request instanceof WFSGetCapabilities ) {
            // TODO implement partial responses (if only certain sections are requested)
            response = configuration;
        } else if ( request instanceof GetFeature ) {
            GetFeatureHandler gfh = new GetFeatureHandler( this );
            response = gfh.handleRequest( (GetFeature) request );
        } else if ( request instanceof DescribeFeatureType ) {
            DescribeFeatureTypeHandler dfth = new DescribeFeatureTypeHandler( this );
            response = dfth.handleRequest( (DescribeFeatureType) request );
        } else if ( request instanceof Transaction ) {
            TransactionHandler th = new TransactionHandler( this, (Transaction) request );
            response = th.handleRequest();
        } else if ( request instanceof GetFeatureWithLock ) {
            String msg = "GetFeatureWithLock operation is not supported yet.";
            throw new OGCWebServiceException( getClass().getName(), msg );
        } else if ( request instanceof LockFeature ) {
            String msg = "LockFeature operation is not supported yet.";
            throw new OGCWebServiceException( getClass().getName(), msg );
        } else {
            String msg = "Unknown request type: " + request.getClass().getName();
            throw new OGCWebServiceException( getClass().getName(), msg );
        }

        return TP.doPostTrigger( this, response )[0];
    }

    /**
     * Returns a clone of the  <code>WFService</code> instance.
     * <p>
     * Note that the configuration of the new service will refer to the same instance.
     */
    @Override
    public Object clone() {
        return new WFService( configuration );
    }

    /**
     * Returns the <code>MappedFeatureType</code> with the given name.
     * 
     * @param name
     *            name of the feature type
     * @return the mapped feature type with the given name, or null if it is not known to this
     *         WFService instance
     */
    public MappedFeatureType getMappedFeatureType( QualifiedName name ) {
        return this.configuration.getMappedFeatureTypes().get( name );
    }

    /**
     * Returns a <code>Map</code> of the feature types that this WFS serves.
     * 
     * @return keys: feature type names, values: mapped feature types
     */
    public Map<QualifiedName, MappedFeatureType> getMappedFeatureTypes() {
        return this.configuration.getMappedFeatureTypes();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFService.java,v $
 Revision 1.45  2006/11/09 17:46:36  mschneider
 Improved indenting.

 Revision 1.44  2006/11/07 11:09:36  mschneider
 Added exceptions in case anything other than the 1.1.0 format is requested.

 Revision 1.43  2006/10/01 11:15:43  poth
 trigger points for doService methods defined

 Revision 1.42  2006/09/05 17:44:01  mschneider
 Fixed javadoc version information.

 Revision 1.41  2006/07/21 14:09:06  mschneider
 Improved javadoc.

 Revision 1.40  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */