//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/shape/ShapefileMerger.java,v 1.3 2006/08/24 06:43:54 poth Exp $
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: greve@giub.uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree.tools.shape;

import java.io.File;
import java.io.IOException;

import org.deegree.io.dbaseapi.DBaseException;
import org.deegree.io.shpapi.HasNoDBaseFileException;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;


/**
 * ... 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/08/24 06:43:54 $
 * 
 * @since 2.0
 */

public class ShapefileMerger {

    private FeatureCollection mergedFeatures;
    
    private File outputFile;
    
    public ShapefileMerger( String[] args ) throws IOException, HasNoDBaseFileException, DBaseException {

        if( this.mergedFeatures == null ){
            this.mergedFeatures = FeatureFactory.createFeatureCollection( "dummy", 1000 );
        }

        for ( int i = 1; i < args.length; i++ ) {
            
            String s = new File( args[ i ] ).getAbsolutePath();
            ShapeFile shp = new ShapeFile( s );
            System.out.println( "Opened: " + s );

            for ( int j = 0; j < shp.getRecordNum(); j++ ) {
                mergedFeatures.add( shp.getFeatureByRecNo( j + 1 ) );
            }
            shp.close();
        }
        
        this.outputFile = new File( args[0] );
        
    }

    
    public FeatureCollection getMergedFeatures(){
        return this.mergedFeatures;
    }
    
    /**
     * @param args
     */
    public static void main( String[] args ) {
        
        if ( args.length < 3 ){
            System.out.println( "Usage: java -classpath deegree.jar;. org.deegree.tools.shape.ShapefileMerger <out_shapefile> <in_shape_1> <in_shape2> ... <in_shape_n>" );
            System.exit( 0 );
        }

        ShapefileMerger shpMerger = null;
        try {
            shpMerger = new ShapefileMerger( args );
            shpMerger.save();
            
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        } 
        
    }

    private void save() throws IOException {
        
        if( this.mergedFeatures != null ){
            String s = this.outputFile.getAbsolutePath();
            ShapeFile shp = new ShapeFile( s, "rw" );
            try {
                shp.writeShape( this.mergedFeatures );
                System.out.println( "Saved: " + s);
            } catch ( Exception e ) {
                throw new IOException( "Could not save merged FeatureCollection: " 
                                       +  e.getLocalizedMessage() );
            }
            shp.close();
        }
    }
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ShapefileMerger.java,v $
Revision 1.3  2006/08/24 06:43:54  poth
File header corrected

Revision 1.2  2006/07/31 12:33:27  poth
help text corrected

Revision 1.1  2006/05/19 13:19:20  poth
improved program for merging shapes - first load up


********************************************************************** */