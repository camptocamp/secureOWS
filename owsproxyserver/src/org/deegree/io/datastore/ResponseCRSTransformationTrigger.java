//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/ResponseCRSTransformationTrigger.java,v 1.4 2006/11/27 09:07:53 poth Exp $
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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.trigger.Trigger;
import org.deegree.framework.trigger.TriggerException;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.GeometryException;

/**
 * Trigger implementation for transforming the geometries being part
 * of a resposne of a Datastore into the desired target CRS.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.4 $, $Date: 2006/11/27 09:07:53 $
 */
public class ResponseCRSTransformationTrigger implements Trigger {
    
    private static final ILogger LOG = LoggerFactory.getLogger( ResponseCRSTransformationTrigger.class );
    
    private String name;

    /**
     * @param caller calling instance
     * @param values values passed from the calling method
     */
    public Object[] doTrigger( Object caller, Object... values ) {
        FeatureCollection fc = (FeatureCollection)values[0];
        String targetCRS = (String)values[1];
   
        if ( targetCRS != null && fc != null ) {
            try {
                IGeoTransformer gt = new GeoTransformer( targetCRS );
                fc = gt.transform( fc );
            } catch ( CRSTransformationException e ) {
                LOG.logError( e.getMessage(), e );
                throw new TriggerException( e );
            } catch ( GeometryException e ) {
                LOG.logError( e.getMessage(), e );
                throw new TriggerException( e );
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
$Log: ResponseCRSTransformationTrigger.java,v $
Revision 1.4  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/11/09 17:37:30  mschneider
Javadoc fixes.

Revision 1.2  2006/09/28 09:46:01  poth
implementation completed

Revision 1.1  2006/09/27 20:08:17  poth
first check in

********************************************************************** */