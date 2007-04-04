/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001 by:
EXSE, Department of Geography, University of Bonn
http://www.giub.uni-bonn.de/exse/
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
E-Mail: klaus.greve@uni-bonn.de

                 
 ---------------------------------------------------------------------------*/
package org.deegree.io.shpapi;

import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.CurveSegment;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class SHPPolygon3D extends SHPPolygon {
    
    boolean[] outer = null;
    
    /**
     * @param recBuf
     */
    public SHPPolygon3D(byte[] recBuf) {
        
        super( recBuf );
        
        envelope = ShapeUtils.readBox(recBuf,4);
        
        rings = new SHPPolyLine3D(recBuf);
        
        numPoints = rings.numPoints;
        numRings = rings.numParts;
              
    }

    /**
     * @param surface
     */
    public SHPPolygon3D(Surface[] surface) {
        super( surface );
        try {
            int count = 0;
            
            for (int i = 0; i < surface.length; i++) {
                // increment for exterior ring
                count++;
                // increment for inner rings
                Ring[] rings = surface[i].getSurfaceBoundary().getInteriorRings();
                if ( rings != null ) {                    
                    count += rings.length;
                }
            }
            
            Curve[] curves = new Curve[count];
            outer = new boolean[count]; 
            
            count = 0;
            for (int i = 0; i < surface.length; i++) {
                
                CurveSegment cs =
                surface[i].getSurfaceBoundary().getExteriorRing().getAsCurveSegment();
                outer[count] = true;
                curves[count++] = GeometryFactory.createCurve( cs );
                
                Ring[] rings = surface[i].getSurfaceBoundary().getInteriorRings();
                if ( rings != null ) {
                    for (int j = 0; j < rings.length; j++) {
                        cs =rings[j].getAsCurveSegment();
                        outer[count] = false;                        
                        curves[count++] = GeometryFactory.createCurve( cs );
                    }
                }
            }
            rings = new SHPPolyLine3D(curves);
            
            envelope = rings.envelope;
            
            numPoints = rings.numPoints;
            numRings = rings.numParts;
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public SHPPolygon3D( SHPPolyLine3D rings) {
        super( (Surface[])null );
        this.rings = rings;
        numRings = rings.numParts;
    }   
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SHPPolygon3D.java,v $
Revision 1.1  2006/06/05 15:21:53  poth
support for polygonz type added


********************************************************************** */