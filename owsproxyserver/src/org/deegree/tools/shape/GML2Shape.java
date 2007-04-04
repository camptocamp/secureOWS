// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/shape/GML2Shape.java,v 1.10 2006/07/12 14:46:14 poth Exp $
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
package org.deegree.tools.shape;

import java.io.File;
import java.io.FileReader;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;

/**
 * 
 *
 * @version $Revision: 1.10 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.10 $, $Date: 2006/07/12 14:46:14 $
 *
 * @since 1.1
 */
public class GML2Shape {
    
    private static final ILogger LOG = LoggerFactory.getLogger( GML2Shape.class );
    
    private FeatureCollection fc = null;
    private String inName = null;
    private String outName = null;

    /**
     * 
     */
    public GML2Shape(String inName, String outName) {       
        this.inName = inName;
        this.outName = outName;
    }
    
    public void read() throws Exception {
        LOG.logInfo( "reading " + inName + " ... ");
        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument ();
        doc.load (new FileReader( inName ), new File (inName).toURL().toString());
        fc = doc.parse();
    }
    
    public void write() throws Exception {
        LOG.logInfo( "writing " + outName + " ... ");
        ShapeFile sf = new ShapeFile( outName, "rw");
        sf.writeShape(fc);
        sf.close();
        LOG.logInfo( "finish");
    }

    public static void main(String[] args) throws Exception {
                
        if ( args == null || args.length < 2 ) {
            System.out.println("Two arguments - input file and output file - are required!");
            System.exit(1);
        }
        
        GML2Shape rws = 
            new GML2Shape( args[0] , args[1]);
        rws.read();
        rws.write();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GML2Shape.java,v $
Revision 1.10  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
