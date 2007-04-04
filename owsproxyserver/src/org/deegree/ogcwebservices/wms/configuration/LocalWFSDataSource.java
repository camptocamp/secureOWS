//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wms/configuration/LocalWFSDataSource.java,v 1.19 2006/11/24 09:47:18 schmitz Exp $
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
package org.deegree.ogcwebservices.wms.configuration;

import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wms.capabilities.ScaleHint;

/**
 * Data source description for a LOCALWFS datasource
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @version $Revision: 1.19 $, $Date: 2006/11/24 09:47:18 $
 */
public class LocalWFSDataSource extends AbstractDataSource {

    private Query query = null;

    private QualifiedName geometryProperty = null;

    /**
     * Creates a new DataSource object.
     * @param querable 
     * @param failOnException 
     * 
     * @param name
     *            name of the featuretype to access
     * @param type
     *            type of the data source (REMOTEWCS, LOCALWCS)
     * @param geometryProperty 
     * @param ows
     *            <tt>OGCWebService</tt> instance for accessing the data source
     * @param capabilitiesURL 
     * @param scaleHint
     *            filter condition
     * @param validArea 
     * @param query 
     * @param featureInfoTransform 
     * @param reqTimeLimit 
     */
    public LocalWFSDataSource( boolean querable, boolean failOnException, QualifiedName name,
                              int type, QualifiedName geometryProperty, OGCWebService ows,
                              URL capabilitiesURL, ScaleHint scaleHint, Geometry validArea,
                              Query query, URL featureInfoTransform, int reqTimeLimit ) {
        super( querable, failOnException, name, type, ows, capabilitiesURL, scaleHint, validArea,
               featureInfoTransform, reqTimeLimit );
        this.query = query;
        this.geometryProperty = geometryProperty;
    }

    /**
     * @return the WFS Query that describes the access/filtering to the data source.
     */
    public Query getQuery() {
        return query;
    }

    /**
     * @return the name of the geometry property in case the datasource is of type LOCALWFS /
     * REMOTEWFS.
     * <p>
     * 
     */
    public QualifiedName getGeometryProperty() {
        return geometryProperty;
    }

    /**
     * Returns an instance of the <tt>OGCWebService</tt> that represents the datasource.
     * 
     * TODO if more than one layer uses data that are offered by the same OWS the deegree WMS shall
     * just use one instance for accessing the OWS.
     */
    @Override
    public OGCWebService getOGCWebService() throws OGCWebServiceException {
        // not sure why new services are recreated always
        return WFServiceFactory.createInstance( (WFSConfiguration) ( (WFService) ows ).getCapabilities() );
//        return ows;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: LocalWFSDataSource.java,v $
 * Changes to this class. What the people have been up to: Revision 1.19  2006/11/24 09:47:18  schmitz
 * Changes to this class. What the people have been up to: Not sure whether WFService is thread safe.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.18  2006/11/24 09:33:13  schmitz
 * Changes to this class. What the people have been up to: Fixed a bug concerning layer specific scale hints.
 * Changes to this class. What the people have been up to: Using the central i18n mechanism.
 * Changes to this class. What the people have been up to: Changed the localwfs mechanism to just use one WFS and not recreate them.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.17  2006/09/08 08:42:01  schmitz
 * Changes to this class. What the people have been up to: Updated the WMS to be 1.1.1 conformant once again.
 * Changes to this class. What the people have been up to: Cleaned up the WMS code.
 * Changes to this class. What the people have been up to: Added cite WMS test data.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.16  2006/07/27 13:08:46  poth
 * Changes to this class. What the people have been up to: support for request time limit added for each datasource added
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.15  2006/04/25 19:28:52  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.14  2006/04/25 06:50:27  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/04/06 20:25:25  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12  2006/04/04 20:39:41  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.11  2006/03/30 21:20:25  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.10  2005/12/21 17:30:10  poth
 * Changes to this class. What the people have been up to: no message
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2005/11/22 17:19:13  poth
 * Changes to this class. What the people have been up to: no message
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2005/11/16 13:45:00  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7.2.1  2005/11/08 16:37:47  mschneider
 * Changes to this class. What the people have been up to: More refactoring to make it compile again.
 * Changes to this class. What the people have been up to:
 * Revision 1.7 2005/08/26 21:13:02 poth no message
 * 
 * Revision 1.6 2005/08/09 15:48:24 poth no message
 * 
 * Revision 1.5 2005/08/05 09:42:20 poth no message
 * 
 * Revision 1.4 2005/08/04 08:56:58 poth no message
 * 
 * Revision 1.3 2005/07/22 20:51:54 poth no message
 * 
 * Revision 1.2 2005/06/28 15:58:11 poth no message
 * 
 * Revision 1.1 2005/06/22 15:33:00 poth no message
 * 
 * 
 * 
 **************************************************************************************************/
