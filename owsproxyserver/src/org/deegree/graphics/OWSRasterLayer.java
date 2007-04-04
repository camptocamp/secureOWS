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
package org.deegree.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.opengis.coverage.grid.GridCoverage;

/**
 * 
 * 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: taddei $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/09/07 13:26:20 $
 *
 * @since 2.0
 */
public class OWSRasterLayer extends AbstractLayer {

    private ILogger LOG = LoggerFactory.getLogger( OWSRasterLayer.class );

    private URL baseURL;
    private Envelope envelope;

    /**
     * 
     * @param name
     * @param resource
     * @param coverageOffering
     * @throws Exception
     */
    public OWSRasterLayer( String name, URL baseURL ) throws Exception {
        super( name );
        this.baseURL = baseURL;
    }

    /**
     * 
     */
    public void setCoordinatesSystem( CoordinateSystem crs )
                            throws Exception {
        // not supported yet
    }

    @Override
    public Envelope getBoundingBox() {
        return envelope;
    }

    /**
     * 
     * @param envelope
     * @param w width of image
     * @param w height of image
     * @return
     * @throws IOException 
     * @throws InvalidParameterValueException 
     */
    public GridCoverage getRaster( Envelope envelope, double w, double h ) throws IOException {
        
        this.envelope = envelope;
        StringBuffer sb = new StringBuffer( 150 );
        sb.append( baseURL );
        appendBoxParamtersToBuffer( sb, envelope, (int)w, (int)h );
        
        URL u = new URL( sb.toString()  );
                
        GridCoverage gc = null;
        try {
            BufferedImage img = ImageUtils.loadImage( u );
            
            gc = new ImageGridCoverage( null, envelope, img );
            
        } catch ( Exception e ) {
            LOG.logDebug( "Error getting raster data: " +  e.getMessage() );
        }
        return gc;
    }

    private void appendBoxParamtersToBuffer( StringBuffer sb , Envelope env, int w, int h ){
        
        sb.append( "&BBOX=" ) 
            .append( env.getMin().getX() ).append( "," )
            .append( env.getMin().getY() ).append( "," )
            .append( env.getMax().getX() ).append( "," )
            .append( env.getMax().getY() )
            .append( "&WIDTH=" ).append( w ) 
            .append( "&HEIGHT=" ).append( h ); 
    }
    
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: OWSRasterLayer.java,v $
 Revision 1.3  2006/09/07 13:26:20  taddei
 handling/ignoring exceptions in OWSRasterlayer; and using img dimension

 Revision 1.2  2006/08/25 14:11:21  taddei
 implemented getRaster

 Revision 1.1  2006/08/24 09:57:58  poth
 initial check in

 
 ********************************************************************** */