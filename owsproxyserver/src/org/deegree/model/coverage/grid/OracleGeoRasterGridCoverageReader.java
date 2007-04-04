//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/coverage/grid/OracleGeoRasterGridCoverageReader.java,v 1.16 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.model.coverage.grid;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.util.Locale;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.deegree.datatypes.CodeList;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.oraclegeoraster.GeoRasterDescription;
import org.deegree.io.oraclegeoraster.GeoRasterReader;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.OperationParameter;
import org.opengis.parameter.ParameterNotFoundException;

/**
 * Reader for Coverages stored in Oracle 10g GeoRaster format 
 * 
 *
 * @version $Revision: 1.16 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.16 $, $Date: 2006/11/27 09:07:52 $
 *
 * @since 2.0
 */
public class OracleGeoRasterGridCoverageReader extends AbstractGridCoverageReader {
    
    private static final ILogger LOG = 
        LoggerFactory.getLogger( OracleGeoRasterGridCoverageReader.class );
    
    
    /**
     * 
     * @param jdbc description of the JDBC connection to a Orable DB
     * @param table name of the table storing the GeoRaster
     * @param column name of the column the target GeoRaster is stored
     * @param identification a SQL where clause that identifies the target 
     *                      georaster
     * @param description
     * @param envelope
     * @param format
     */
    public OracleGeoRasterGridCoverageReader(GeoRasterDescription grDesc, 
                                             CoverageOffering description,
                                             Envelope envelope, Format format) {
        super( grDesc, description, envelope, format );    
    }

    public void dispose() throws IOException {
        
    }

    /**
     * reads a GridCoverage from a Oracle 10g GeoRaster
     * @param parameters -
     */
    public GridCoverage read(GeneralParameterValue[] parameters) throws 
                                                            InvalidParameterNameException, 
                                                            InvalidParameterValueException, 
                                                            ParameterNotFoundException, 
                                                            IOException {
        
        float width = -1;
        float height = -1;
        for (int i = 0; i < parameters.length; i++) {
            OperationParameter op = (OperationParameter)parameters[i].getDescriptor();
            String name = op.getName( Locale.getDefault() );
            if ( name.equalsIgnoreCase( "WIDTH" ) ) {
                Object o = op.getDefaultValue();
                width = ((Integer)o).intValue();
            } else if ( name.equalsIgnoreCase( "HEIGHT" ) ) {
                Object o = op.getDefaultValue();
                height = ((Integer)o).intValue();
            } 
        }

        // get the region of the georaster that intersects with the requested
        // envelope. First field of the returned array contains the intersection
        // envelope in the rasters native CRS; second field contains the 
        // corresponding LonLatEnvelope
        Object[] o = getImageRegion();
     
        GeoRasterDescription grDesc = (GeoRasterDescription)getSource();
        RenderedImage img = null;
        try {            
            LOG.logDebug( "reading GeoRaster from Oracle DB" );
            img = GeoRasterReader.exportRaster( grDesc, (Envelope)o[0] );
        } catch (Exception e) {
            LOG.logError( "could not read GeoRaster: ", e );
            throw new IOException( "could not read GeoRaster: " + e.getMessage() );
        }
        
        ParameterBlock pb = new ParameterBlock();
        pb.addSource( img );
        pb.add( width / img.getWidth() ); // The xScale
        pb.add( height / img.getHeight() ); // The yScale
        pb.add( 0.0F ); // The x translation
        pb.add( 0.0F ); // The y translation
        pb.add( new InterpolationNearest() ); // The interpolation
        // Create the scale operation
        RenderedOp ro = JAI.create( "scale", pb, null );
        try {
            img = ro.getAsBufferedImage();
        } catch (Exception e) {
            LOG.logError( "could not rescale image", e );
            throw new IOException( "could not rescale image" + e.getMessage() );
        }
        
        CoverageOffering co = (CoverageOffering)description.clone();
        co.setLonLatEnvelope( (LonLatEnvelope)o[1] );
        
        return new ImageGridCoverage( co, (Envelope)o[0], (BufferedImage)img );
                
    }
    
    /**
     * returns the region of the source image that intersects with the
     * GridCoverage to be created as Rectange as well as the Envelope 
     * of the region in the native CRS and the LonLatEnvelope of this region.   
     * @return
     */
    private Object[] getImageRegion() {
     
        CodeList[] cl = description.getSupportedCRSs().getNativeSRSs();
        String code = cl[0].getCodes()[0];
        
        LonLatEnvelope lle = description.getLonLatEnvelope(); 
        Envelope tmp = 
            GeometryFactory.createEnvelope(lle.getMin().getX(), lle.getMin().getY(),
                                           lle.getMax().getX(), lle.getMax().getY(), 
                                           null );  
        try {
            if ( !code.equals( "EPSG:4326" ) ) {
                IGeoTransformer trans = new GeoTransformer( code );
                tmp = trans.transform( tmp, "EPSG:4326" );              
            }
        } catch (Exception e) {
            LOG.logError( StringTools.stackTraceToString(e) );
        }
                
        // calculate envelope of the part of the grid coverage that intersects
        // within the image       
        Envelope env = envelope.createIntersection( tmp );
        LonLatEnvelope lonLatEnvelope = calcLonLatEnvelope(env, code);        

        return new Object[] { env, lonLatEnvelope };
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OracleGeoRasterGridCoverageReader.java,v $
Revision 1.16  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.15  2006/11/17 08:58:01  poth
bug fixes - setting correct interpolation

Revision 1.14  2006/11/08 17:13:46  poth
*** empty log message ***

Revision 1.13  2006/09/27 16:46:41  poth
transformation method signature changed

Revision 1.12  2006/08/29 19:54:14  poth
footer corrected

Revision 1.11  2006/06/30 14:16:18  poth
comment corrected

Revision 1.10  2006/05/03 20:09:52  poth
*** empty log message ***

Revision 1.9  2006/05/01 20:15:27  poth
*** empty log message ***


********************************************************************** */