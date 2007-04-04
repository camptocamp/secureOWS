//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/demo/DemoMapView.java,v 1.34 2006/11/27 09:07:52 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
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
 Meckenheimer Allee 176
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
package org.deegree.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.Layer;
import org.deegree.graphics.LazyRasterLayer;
import org.deegree.graphics.MapFactory;
import org.deegree.graphics.MapView;
import org.deegree.graphics.Theme;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGMLSchemaDocument;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescriptionDocument;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.opengis.coverage.grid.GridCoverage;
import org.w3c.dom.Document;

/**
 * Sample application to demonstrate deegree's mechanisms for reading vector and raster data from the
 * following sources:
 * <ul>
 *   <li>ESRI shape file</li>
 *   <li>GML file</li>
 *   <li>deegree feature persistence layer (datastore)</li>
 *   <li>Coverages</li>
 * </ul> 
 * <p>
 * The data is displayed in an interactive map.
 * <p>
 * Saving of vector data as either shape or GML is also demonstrated.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0, $Revision: 1.34 $, $Date: 2006/11/27 09:07:52 $
 */
public class DemoMapView extends JFrame {

    private static final long serialVersionUID = -1665647914613642719L;

    private static enum TYPE {
        SHAPE, GML, RASTER, DATASTORE
    };
    
    //private static String INPUT = "E:\\deegree2_masterCD\\apache-tomcat-5.5.12\\webapps\\deegree2demo\\WEB-INF\\conf\\wfs\\featuretypes\\CountyBoundaries2003.xsd";
   private static String INPUT = "D:\\java\\projekte\\testwcs\\data\\tiles\\wcs_utah_configuration.xml";
   //private static String INPUT = "file:///e:/temp/wf.xml";
    //private static String INPUT = "e:/temp/Mo-Usg-extrem";
   // private static String INPUT = "E:\\deegree2_masterCD\\apache-tomcat-5.5.12\\webapps\\deegree2demo\\WEB-INF\\data\\utah\\vector\\admin\\SGID500_ZipCodes";
    //private static String INPUT = "E:/temp/schema.xsd";

    private static String OUTPUT = "e:\\temp\\output";

    //private static String CRS = "epsg:4326";
    //private static String CRS = "epsg:26912";
    //private static String CRS = "epsg:2152";
    //private static String CRS = "epsg:31466";
    private static String CRS = "epsg:4326";

    private MapView mapView;

    private TYPE dataType = TYPE.RASTER;

    /**
     * Creates a new instance of <code>DemoMapView</code>.
     *    
     */
    public DemoMapView(  ) {

       
        setSize( 750, 960 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        getContentPane().setLayout( null );
        this.addKeyListener( new MyKeyListener() );

        setVisible( true );
    }

    /**
     * Runs the application.
     */
    public void run()
                            throws Exception {

        List<Theme> themes = new ArrayList<Theme>();
        if ( dataType == TYPE.GML ) {
            FeatureCollection fc = loadGML();
            saveAsGML( fc );
            themes.add( createTheme( fc ) );
        } else if ( dataType == TYPE.SHAPE ) {
            FeatureCollection fc = loadShape();
            saveAsShape( fc );
            themes.add( createTheme( fc ) );
        } else if ( dataType == TYPE.RASTER ) {
            CoverageOffering co = loadRaster();
            themes.add( createTheme( co ) );
        } else if ( dataType == TYPE.DATASTORE ) {
            FeatureCollection fc = loadFromDatastore();
            saveAsGML( fc );
            themes.add( createTheme( fc ) );
        }

        createMap( themes );

        repaint();
    }

    /**
     * Loads a shape file and returns the content as deegree <code>FeatureCollection</code>.
     *  
     * @see FeatureCollection
     * @return shape file content
     */
    private FeatureCollection loadShape()
                            throws Exception {
        ShapeFile sf = new ShapeFile( INPUT );
        FeatureCollection fc = 
            FeatureFactory.createFeatureCollection( "id", sf.getRecordNum() );

        CoordinateSystem crs = CRSFactory.create( CRS );
        int k = 0;
        System.out.println( "No. of features: " + sf.getRecordNum() );
        for ( int i = 0; i < sf.getRecordNum(); i++ ) {
            Feature feat = sf.getFeatureByRecNo( i + 1 );
            GeometryImpl geom = (GeometryImpl) feat.getDefaultGeometryPropertyValue();
            k = countVertices( k, geom );
            geom.setCoordinateSystem( crs );
            fc.add( feat );
        }
        System.out.println( "No. of vertices: " + k );
        sf.close();
        return fc;
    }

    /**
     * Loads a GML file and returns the content as deegree <code>FeatureCollection</code>.
     *  
     * @see FeatureCollection
     * @return GML file content
     */
    private FeatureCollection loadGML()
                            throws Exception {
        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        doc.load( new URL( INPUT ) );
        FeatureCollection fc = doc.parse();
        System.out.println( "No. of features: " + fc.size() );
        int k = 0;
        for ( int i = 0; i < fc.size(); i++ ) {
            Geometry geom = fc.getFeature( i ).getDefaultGeometryPropertyValue();
            k = countVertices( k, geom );
        }
        System.out.println( "No. of vertices: " + k );
        return fc;
    }

    /**
     * Loads a <code>FeatureCollection</code> using deegree's feature persistence layer.
     * <p>
     * This method uses a GML application schema with persistence annotations.
     * <p>
     * Currently, the following backends are implemented:
     * <ul>
     *   <li>PostGIS (reading + transactions)</li>
     *   <li>Oracle spatial (reading + transactions)</li>
     *   <li>Generic SQL DBs (reading)</li>
     *   <li>ArcSDE (reading + transactions)</li>
     *   <li>ESRI ShapeFiles (reading) </li>
     *   <li>...</li>
     * </ul>
     * 
     * @return selected features as a FeatureCollection
     * @throws Exception
     * @see org.deegree.io.datastore.Datastore 
     */
    private FeatureCollection loadFromDatastore()
                            throws Exception {

        // read annotated GML application schema
        MappedGMLSchemaDocument schemaDoc = new MappedGMLSchemaDocument();
        schemaDoc.load( new File( INPUT ).toURL() );
        MappedGMLSchema schema = schemaDoc.parseMappedGMLSchema();

        // get example feature type (Counties2003)
        QualifiedName ftName = new QualifiedName( "app", "Counties2003",
                                                  new URI( "http://www.deegree.org/app" ) );
        MappedFeatureType ft = (MappedFeatureType) schema.getFeatureType( ftName );

        // query the feature type
        Query query = buildExampleQuery1( ftName );
        FeatureCollection fc = ft.performQuery( query );
        System.out.println (fc.getBoundedBy());

        return fc;
    }

    /**
     * Builds a simple example query that selects all features of the feature type.
     * 
     * @param ftName name of the feature type to query
     * @return query
     */
    private Query buildExampleQuery1( QualifiedName ftName ) {
        Query query = Query.create( ftName );
        return query;
    }

    /**
     * Builds a simple example query that selects only features of the feature type that
     * match certain criteria.
     * 
     * @param ftName name of the feature type to query
     * @return query
     * @throws Exception
     */
    private Query buildExampleQuery2( QualifiedName ftName ) throws Exception {
        String filterString =
            "<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:app=\"http://www.deegree.org/app\">" +
            "  " +
            "    <PropertyIsEqualTo>" +
            "      <PropertyName>app:name</PropertyName>" +
            "      <Literal>Washington</Literal>" +
            "    </PropertyIsEqualTo>" +
            "  " +
            "</Filter>";
        Document doc = XMLTools.parse(new StringReader(filterString));
        Filter filter = AbstractFilter.buildFromDOM(doc.getDocumentElement());
        Query query = Query.create( ftName, filter );
        return query;
    }    

    /**
     * Builds a simple example query that selects only features of the feature type that
     * match certain criteria.
     * 
     * @param ftName name of the feature type to query
     * @return query
     * @throws Exception
     */
    private Query buildExampleQuery3( QualifiedName ftName ) throws Exception {
        String filterString =
            "<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:app=\"http://www.deegree.org/app\" xmlns:gml=\"http://www.opengis.net/gml\">" +
            "  <Or>" +
            "    <PropertyIsEqualTo>" +
            "      <PropertyName>app:name</PropertyName>" +
            "      <Literal>Washington</Literal>" +
            "    </PropertyIsEqualTo>" +
            "    <Disjoint>" +
            "      <PropertyName>app:geom</PropertyName>" +
            "      <gml:Box>" +
            "        <gml:coord>" +
            "          <gml:X>450000</gml:X>" +
            "          <gml:Y>4450000</gml:Y>" +
            "        </gml:coord>" +
            "        <gml:coord>" + 
            "          <gml:X>460000</gml:X>" +
            "          <gml:Y>4460000</gml:Y>" +
            "        </gml:coord>" +
            "      </gml:Box>" +
            "    </Disjoint>" +
            "  </Or>" +
            "</Filter>";
        Document doc = XMLTools.parse(new StringReader(filterString));
        Filter filter = AbstractFilter.buildFromDOM(doc.getDocumentElement());
        Query query = Query.create( ftName, filter );
        return query;
    }     

    /**
     * Loads a raster dataset using a GridCoverage description.
     * 
     * @return
     * @throws Exception
     * @see GridCoverage
     */
    private CoverageOffering loadRaster()
                            throws Exception {

        CoverageDescriptionDocument conf = new CoverageDescriptionDocument();
        conf.load( new File( INPUT ).toURL() );
        return conf.getCoverageOfferings()[0];
    }

    /**
     * Counts the number of vertices in a Geometry.
     * 
     * @param k
     * @param geom
     * @return
     * @throws GeometryException
     */
    private int countVertices( int k, Geometry geom )
                            throws GeometryException {
        if ( geom instanceof Surface ) {
            Surface s = (Surface) geom;
            k += s.getSurfaceBoundary().getExteriorRing().getPositions().length;
            Ring[] r = s.getSurfaceBoundary().getInteriorRings();
            if ( r != null ) {
                for ( int j = 0; j < r.length; j++ ) {
                    k += r[j].getPositions().length;
                }
            }
        } else if ( geom instanceof MultiSurface ) {
            MultiSurface ms = (MultiSurface) geom;
            Surface[] s = ms.getAllSurfaces();
            for ( int g = 0; g < s.length; g++ ) {
                k += s[g].getSurfaceBoundary().getExteriorRing().getPositions().length;
                Ring[] r = s[g].getSurfaceBoundary().getInteriorRings();
                if ( r != null ) {
                    for ( int j = 0; j < r.length; j++ ) {
                        k += r[j].getPositions().length;
                    }
                }
            }
        } else if ( geom instanceof Curve ) {
            Curve c = (Curve) geom;
            k += c.getAsLineString().getNumberOfPoints();
        } else if ( geom instanceof MultiCurve ) {
            MultiCurve mc = (MultiCurve) geom;
            Curve[] c = mc.getAllCurves();
            for ( int j = 0; j < c.length; j++ ) {
                k += c[j].getAsLineString().getNumberOfPoints();
            }
        } else if ( geom instanceof MultiPoint ) {
            MultiPoint mp = (MultiPoint) geom;
            k += mp.getAllPoints().length;
        }
        return k;
    }

    /**
     * Saves the passed <code>FeatureCollection</code> as a GML file.
     * 
     * @param fc FeatureCollection to save
     * @throws Exception
     * @see FeatureCollection
     */
    private void saveAsGML( FeatureCollection fc )
                            throws Exception {
        FileOutputStream fos = new FileOutputStream( OUTPUT );
        GMLFeatureAdapter adapter = new GMLFeatureAdapter();
        adapter.export( fc, fos );
        fos.close();
    }

    /**
     * Saves the passed <code>FeatureCollection</code> as a shape file.
     * 
     * @param fc FeatureCollection to save
     * @throws Exception
     * @see FeatureCollection
     */
    private void saveAsShape( FeatureCollection fc )
                            throws Exception {
        ShapeFile sf = new ShapeFile( OUTPUT, "rw" );
        sf.writeShape( fc );
        sf.close();
    }

    /**
     * Creates a map from the passed <code>CoverageOffering</code> (raster data).
     * 
     * @param co
     * @throws Exception
     * @see CoverageOffering 
     */
    private Theme createTheme( CoverageOffering co )
                            throws Exception {

        Layer layer = new LazyRasterLayer( "raster", co );

        return MapFactory.createTheme( "MyTheme", layer );
    }

    /**
     * Creates a map from the passed <code>Theme</code>s (vector data + styles).
     * 
     * @param themes
     * @throws Exception
     */
    public void createMap( List<Theme> themes )
                            throws Exception {
        // add a small buffer around the datas bbox to ensure
        // that everything will be visible 
        Envelope bb = themes.get( 0 ).getLayer().getBoundingBox();
        bb = bb.getBuffer( bb.getWidth() / 10d );
        CoordinateSystem crs = CRSFactory.create( CRS );
        // transform bbox into desired map CRS
        //GeoTransformer tr = new GeoTransformer( "EPSG:4314" );
        //bb = tr.transformEnvelope( bb, CRS );
        
        // create MapView using defined bbox and Themes 
        mapView = MapFactory.createMapView( "MyMap", bb, crs,
                                  themes.toArray( new Theme[themes.size()] ) );
    }

    /**
     * Returns the geometrytype read from a data file<br>
     * <ul>
     *  <li>1 = point or multi point
     *  <li>2 = curve or multi curve
     *  <li>3 = surface or multi surface 
     * </ul>
     * @param fc
     * @return
     */
    private int getGeometryType( FeatureCollection fc ) {
        Geometry geom = fc.getFeature( 0 ).getDefaultGeometryPropertyValue();
        if ( geom instanceof Point || geom instanceof MultiPoint ) {
            return 1;
        } else if ( geom instanceof Curve || geom instanceof MultiCurve ) {
            return 2;
        } else if ( geom instanceof Surface || geom instanceof MultiSurface ) {
            return 3;
        }
        return -1;
    }

    /**
     * Creates a <code>Theme</code> from the passed <code>FeatureCollection</code>.
     * 
     * @param fc
     * @see FeatureCollection
     */
    private Theme createTheme( FeatureCollection fc )
                            throws Exception {

        UserStyle st = null;
        int geomType = getGeometryType( fc );
        // create a style depending on geometry type
        switch ( geomType ) {
        case 1:
            st = (UserStyle) StyleFactory.createPointStyle( "square", Color.PINK, Color.BLACK, 1,
                                                            1, 10, 10, 0, Double.MAX_VALUE );
            break;
        case 2:
            st = (UserStyle) StyleFactory.createLineStyle( Color.PINK, 4, 1, 0, Double.MAX_VALUE );
            break;
        case 3:
            st = (UserStyle) StyleFactory.createPolygonStyle( Color.BLUE, 1,
                                                              new Color( 22, 22, 22 ), 4, 1, 0,
                                                              Double.MAX_VALUE );
            break;
        }
        
        UserStyle[] us = new UserStyle[] { st };

        // create feature layer/theme using the datas CRS
        CoordinateSystem cs = CRSFactory.create( CRS );
        org.deegree.graphics.Layer layer = 
            MapFactory.createFeatureLayer( "MyLayer", cs, fc );
        return MapFactory.createTheme( fc.getId(), layer, us );
    }

    /**
     * overrides the paint method of @see JFrame
     */
    public void paint( Graphics g ) {
        if ( mapView != null ) {
            try {
                g.setColor( Color.WHITE );
                g.fillRect( 0, 0, getWidth(), getHeight() );
                long l = System.currentTimeMillis();
                System.out.println( "pixel resolution: " + mapView.getBoundingBox().getWidth()
                                    / getWidth() );
                mapView.paint( g );
                System.out.println( "time for rendering: " + ( System.currentTimeMillis() - l ) );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {

        DemoMapView test = new DemoMapView( );
        test.run();
        test.repaint();
    }

    /**
     * Handler for key events:
     * <ul>
     * <li>'i': zoom in</li>
     * <li>'o': zoom out</li>
     * <li>cursor up: scroll up</li>
     * <li>cursor down: scroll down</li>
     * <li>cursor left: scroll left</li>
     * <li>cursor right: scroll right</li>
     * </ul> 
     */
    class MyKeyListener implements KeyListener {

        public void keyTyped( KeyEvent arg0 ) {
        }

        public void keyPressed( KeyEvent arg0 ) {
        }

        public void keyReleased( KeyEvent key ) {

            Envelope env = mapView.getBoundingBox();
            if ( key.getKeyCode() == KeyEvent.VK_I ) {
                double w = env.getWidth();
                double h = env.getHeight();
                double mx = env.getMin().getX() + w / 2d;
                double my = env.getMin().getY() + h / 2d;
                w = w - ( env.getWidth() / 10d );
                h = h - ( env.getHeight() / 10d );
                env = GeometryFactory.createEnvelope( mx - w / 2d, my - h / 2d, mx + w / 2d, my + w
                                                                                             / 2d,
                                                      env.getCoordinateSystem() );
                mapView.setBoundingBox( env );
                repaint();
            } else if ( key.getKeyCode() == KeyEvent.VK_O ) {
                double w = env.getWidth();
                double h = env.getHeight();
                double mx = env.getMin().getX() + w / 2d;
                double my = env.getMin().getY() + h / 2d;
                w = w + ( env.getWidth() / 10d );
                h = h + ( env.getHeight() / 10d );
                env = GeometryFactory.createEnvelope( mx - w / 2d, my - h / 2d, mx + w / 2d, my + w
                                                                                             / 2d,
                                                      env.getCoordinateSystem() );

                mapView.setBoundingBox( env );
                repaint();
            } else if ( key.getKeyCode() == KeyEvent.VK_RIGHT ) {
                double d = env.getWidth() / 20d;
                mapView.setBoundingBox( env.translate( -d, 0 ) );
                repaint();
            } else if ( key.getKeyCode() == KeyEvent.VK_LEFT ) {
                double d = env.getWidth() / 20d;
                mapView.setBoundingBox( env.translate( d, 0 ) );
                repaint();
            } else if ( key.getKeyCode() == KeyEvent.VK_UP ) {
                double d = env.getWidth() / 20d;
                mapView.setBoundingBox( env.translate( 0, -d ) );
                repaint();
            } else if ( key.getKeyCode() == KeyEvent.VK_DOWN ) {
                double d = env.getWidth() / 20d;
                mapView.setBoundingBox( env.translate( 0, d ) );
                repaint();
            }
        }
    }
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DemoMapView.java,v $
 Revision 1.34  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.33  2006/11/16 10:04:03  poth
 *** empty log message ***

 Revision 1.32  2006/10/17 15:00:37  poth
 *** empty log message ***

 Revision 1.31  2006/08/06 20:11:00  poth
 *** empty log message ***

 Revision 1.30  2006/08/06 20:06:55  poth
 useless parameters removed

 Revision 1.29  2006/08/02 18:50:40  poth
 *** empty log message ***

 Revision 1.28  2006/06/12 08:16:59  poth
 *** empty log message ***

 Revision 1.27  2006/06/08 17:59:59  poth
 *** empty log message ***

 Revision 1.26  2006/06/08 09:38:23  mschneider
 Implemented loadFromDatastore(). Improved documentation.

 Revision 1.25  2006/06/07 17:12:02  mschneider
 Improved javadoc.

 Revision 1.24  2006/06/03 12:23:48  poth
 *** empty log message ***

 Revision 1.23  2006/05/31 13:20:03  poth
 *** empty log message ***

 Revision 1.22  2006/05/31 13:00:32  poth
 support for raster data (GridCoverages) as well as for zoom and pan (using keyboard) added

 Revision 1.21  2006/05/31 08:43:11  poth
 method for counting vertices added

 Revision 1.20  2006/05/26 07:21:50  poth
 some enhancements

 Revision 1.19  2006/05/25 16:37:17  poth
 *** empty log message ***

 Revision 1.18  2006/05/25 16:17:03  poth
 some code cleanups

 Revision 1.17  2006/05/16 07:33:20  poth
 commentation completed

 ********************************************************************** */