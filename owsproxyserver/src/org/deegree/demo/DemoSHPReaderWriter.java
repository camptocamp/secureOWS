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
package org.deegree.demo;

import java.io.FileOutputStream;

import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0, $Revision: 1.16 $, $Date: 2006/07/23 08:47:04 $
 * 
 */
public class DemoSHPReaderWriter {

    public void readShape( String fileRoot ) throws Exception {
        FeatureCollection fc = FeatureFactory.createFeatureCollection( "id", 100 );

        try {
            ShapeFile sf = new ShapeFile( fileRoot );
            System.out.println( sf.getFileMBR() );
            for (int i = 0; i < sf.getRecordNum(); i++) {
                Feature feat = sf.getFeatureByRecNo( i + 1 );
                Geometry object = feat.getDefaultGeometryPropertyValue();
                if ( object instanceof Point ) {
                    System.out.println( "point" );
                } else if ( object instanceof Curve ) {
                    System.out.println( "curve" );
                } else if ( object instanceof Surface ) {
                    // System.out.println ("surface");
                }
                if ( object instanceof MultiPoint ) {
                    System.out.println( "multipoint" );
                } else if ( object instanceof MultiCurve ) {
                    System.out.println( "multicurve" );
                } else if ( object instanceof MultiSurface ) {
                    System.out.println( "multisurface" );
                }
 
                fc.add( feat );

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // stores the content as GML
        this.writeGML( "e:/temp/out.xml", fc );

        // write a new shape
        this.writeShape( "e:/temp/out", fc );
    }

    public void writeShape( String fileRoot, FeatureCollection fc ) throws Exception {
        ShapeFile sf = new ShapeFile( fileRoot, "rw" );
        sf.writeShape( fc );
        sf.close();
    }

    public void writeGML( String file, FeatureCollection fc ) throws Exception {
        FileOutputStream fos = new FileOutputStream( file );
        new GMLFeatureAdapter().export( fc, fos );
        fos.close();
    }

    public static void main( String[] args ) {

        DemoSHPReaderWriter test = new DemoSHPReaderWriter();
        try {
            // read roalds.shp.
            // just the name of the shape is needed.
            // don't use the extension of the shape.
            test.readShape( "C:\\DemoCD_deegree\\deegreewms\\WEB-INF\\data\\osnabrueck\\gruenpolyl" );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DemoSHPReaderWriter.java,v $
Revision 1.16  2006/07/23 08:47:04  poth
*** empty log message ***

********************************************************************** */
