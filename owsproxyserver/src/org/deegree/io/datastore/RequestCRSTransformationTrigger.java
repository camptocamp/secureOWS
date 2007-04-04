//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/RequestCRSTransformationTrigger.java,v 1.4 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.io.datastore;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.Trigger;
import org.deegree.framework.trigger.TriggerException;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterTools;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * Trigger implementation for transformation of geometries being
 * part of a WFS request into the CRS of the comparsion geometries
 * at the physical datasource.
 * <br>
 * At th moment just support for GetFeature is implemented
 * TODO
 * support for update and delete requests
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.4 $, $Date: 2006/11/27 09:07:53 $
 */
public class RequestCRSTransformationTrigger implements Trigger {

    private static final ILogger LOG = LoggerFactory.getLogger( RequestCRSTransformationTrigger.class );

    private String name;

    /**
     * @param caller calling instance
     * @param values parameter values passed from the caller. Because
     *        RequestCRSTransformationTrigger is intented to be used as a 
     *        preTrigger this will be all parameters passed to the calling
     *        method   
     */
    public Object[] doTrigger( Object caller, Object... values ) {

        Datastore ds = (Datastore) caller;
        Query query = (Query) values[0];

        Filter filter = query.getFilter();

        if ( filter instanceof ComplexFilter ) {
            // transformations are just required if a filter is complex
            // because FeatureID filters does not contain spatial operations
            ComplexFilter cFilter = (ComplexFilter) filter;
            SpatialOperation[] so = FilterTools.extractSpatialFilter( cFilter );
            QualifiedName[] qns = query.getTypeNames();
            IGeoTransformer gt = null;
            for ( int i = 0; i < qns.length; i++ ) {
                MappedFeatureType mft = ( (Datastore) caller ).getFeatureType( qns[i] );
                CoordinateSystem targetCRS = mft.getGMLSchema().getDefaultCS();
                for ( int j = 0; j < so.length; j++ ) {
                    CoordinateSystem sourceCRS = so[i].getGeometry().getCoordinateSystem();
                    if ( sourceCRS != null && !targetCRS.equals( sourceCRS ) ) {
                        try {
                            if ( gt == null ) {
                                gt = new GeoTransformer( targetCRS );
                            }
                            so[j].setGeometry( gt.transform( so[i].getGeometry() ) );
                        } catch ( CRSTransformationException e ) {
                            LOG.logError( e.getMessage(), e );
                            throw new TriggerException( e );
                        } 
                    }
                }
            }

        }

        return values;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override    
    public String toString() {
        return "Trigger name: " + name;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: RequestCRSTransformationTrigger.java,v $
 Revision 1.4  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.3  2006/11/09 17:37:01  mschneider
 Fixed formatting.

 Revision 1.2  2006/09/28 09:46:01  poth
 implementation completed

 Revision 1.1  2006/09/27 20:08:17  poth
 first check in

 ********************************************************************** */