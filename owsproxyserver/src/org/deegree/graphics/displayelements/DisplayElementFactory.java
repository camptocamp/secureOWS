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
package org.deegree.graphics.displayelements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.Geometry;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiPrimitive;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.opengis.coverage.grid.GridCoverage;

/**
 * Factory class for the different kinds of <tt>DisplayElement</tt>s.
 * <p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.20 $ $Date: 2006/07/29 08:51:12 $
 */
public class DisplayElementFactory {
    
    private static final ILogger LOG = LoggerFactory.getLogger( DisplayElementFactory.class );
    
    /**
     * returns the display elements associated to a feature
     * @throws PropertyPathResolvingException 
     */
    public DisplayElement[] createDisplayElement( Object o, UserStyle[] styles )
                                            throws ClassNotFoundException,
                                                IllegalAccessException,
                                                InstantiationException,
                                                NoSuchMethodException,
                                                InvocationTargetException,
                                                GeometryException, PropertyPathResolvingException {

        ArrayList list = new ArrayList( 20 );

        if ( o instanceof Feature ) {
            Feature feature = (Feature) o;

            try {
                String featureTypeName = feature.getFeatureType().getName().getAsString();

                for (int i = 0; i < styles.length; i++) {

                    if ( styles[i] == null ) {
                        // create display element from default style
                        DisplayElement de = buildDisplayElement( feature );
                        if ( de != null ) {
                            list.add( de );
                        }
                    } else {
                        FeatureTypeStyle[] fts = styles[i].getFeatureTypeStyles();
                        for (int k = 0; k < fts.length; k++) {
                            if ( fts[k].getFeatureTypeName() == null
                                || featureTypeName.equals( fts[k].getFeatureTypeName() ) ) {
                                Rule[] rules = fts[k].getRules();                                
                                for (int n = 0; n < rules.length; n++) {
                                    // does the filter rule apply?
                                    Filter filter = rules[n].getFilter();
                                    if ( filter != null ) {
                                        try {
                                            if ( !filter.evaluate( feature ) ) {
                                                continue;
                                            }
                                        } catch (FilterEvaluationException e) {
                                            LOG.logError( "Error evaluating filter: ", e );
                                            continue;
                                        }
                                    }
                                    
                                    // Filter expression is true for this feature, so a
                                    // corresponding DisplayElement has to be added to the
                                    // list
                                    Symbolizer[] symbolizers = rules[n].getSymbolizers();

                                    for (int u = 0; u < symbolizers.length; u++) {

                                        DisplayElement displayElement = DisplayElementFactory
                                            .buildDisplayElement( feature, symbolizers[u] );
                                        if ( displayElement != null ) {
                                            list.add( displayElement );                                            
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (IncompatibleGeometryTypeException e) {
                e.printStackTrace();
            }
        } else {
            RasterSymbolizer symbolizer = new RasterSymbolizer();
            list.add( buildRasterDisplayElement( (GridCoverage) o, symbolizer ) );
        }

        DisplayElement[] de = new DisplayElement[list.size()];
        return (DisplayElement[]) list.toArray( de );
    }

    /**
     * Builds a <tt>DisplayElement</tt> using the given <tt>Feature</tt> or raster and
     * <tt>Symbolizer</tt>.
     * <p>
     * 
     * @param o
     *            contains the geometry or raster information (Feature or raster)
     * @param symbolizer
     *            contains the drawing (style) information and selects the geometry property of the
     *            <tt>Feature</tt> to be drawn
     * @throws IncompatibleGeometryTypeException
     *             if the selected geometry of the <tt>Feature</tt> is not compatible with the
     *             <tt>Symbolizer</tt>
     * @return constructed <tt>DisplayElement</tt>
     * @throws PropertyPathResolvingException 
     */
    public static DisplayElement buildDisplayElement( Object o, Symbolizer symbolizer )
                                                    throws IncompatibleGeometryTypeException,
                                                        ClassNotFoundException,
                                                        IllegalAccessException,
                                                        InstantiationException,
                                                        NoSuchMethodException,
                                                        InvocationTargetException,
                                                        GeometryException, 
                                                        PropertyPathResolvingException {
        DisplayElement displayElement = null;

        if ( o instanceof Feature ) {
            Feature feature = (Feature) o;

            // determine the geometry property to be used
            org.deegree.model.spatialschema.Geometry geometry = null;
            Geometry symbolizerGeometry = symbolizer.getGeometry();

            if ( symbolizerGeometry != null ) {                
                FeatureProperty property  = 
                    feature.getDefaultProperty( symbolizerGeometry.getPropertyPath() );
                if ( property != null ) {
                    geometry = (org.deegree.model.spatialschema.Geometry) property.getValue();
                }
            } else {
                geometry = feature.getDefaultGeometryPropertyValue();
            }

            // if the geometry is null, do not build a DisplayElement
            if ( geometry == null ) {
                return null;
            }
            // PointSymbolizer
            if ( symbolizer instanceof PointSymbolizer ) {
                displayElement = buildPointDisplayElement( feature, geometry,
                    (PointSymbolizer) symbolizer );
            } // LineSymbolizer
            else if ( symbolizer instanceof LineSymbolizer ) {
                displayElement = buildLineStringDisplayElement( feature, geometry,
                    (LineSymbolizer) symbolizer );
            } // PolygonSymbolizer
            else if ( symbolizer instanceof PolygonSymbolizer ) {
                displayElement = buildPolygonDisplayElement( feature, geometry,
                    (PolygonSymbolizer) symbolizer );
            } else if ( symbolizer instanceof TextSymbolizer ) {
                displayElement = buildLabelDisplayElement( feature, geometry,
                    (TextSymbolizer) symbolizer );
            }
        } else {
            if ( symbolizer instanceof RasterSymbolizer ) {
                displayElement = buildRasterDisplayElement( (GridCoverage) o,
                    (RasterSymbolizer) symbolizer );
            }
        }

        return displayElement;
    }

    /**
     * Builds a <tt>DisplayElement</tt> using the given <tt>Feature</tt> or Raster and a default
     * <tt>Symbolizer</tt>.
     * <p>
     * 
     * @param o
     *            contains the geometry or raster information (Feature or raster)
     * @throws IncompatibleGeometryTypeException
     *             if the selected geometry of the <tt>Feature</tt> is not compatible with the
     *             <tt>Symbolizer</tt>
     * @return constructed <tt>DisplayElement</tt>
     */
    public static DisplayElement buildDisplayElement( Object o )
        throws IncompatibleGeometryTypeException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException,
            GeometryException {

        DisplayElement displayElement = null;

        if ( o instanceof GridCoverage ) {
            RasterSymbolizer symbolizer = new RasterSymbolizer();
            displayElement = buildRasterDisplayElement( (GridCoverage) o, symbolizer );
        } else {
            Feature feature = (Feature) o;
            // determine the geometry property to be used
            org.deegree.model.spatialschema.Geometry geoProperty = feature
                .getDefaultGeometryPropertyValue();

            // if the geometry property is null, do not build a DisplayElement
            if ( geoProperty == null ) {
                return null;
            }
            // PointSymbolizer
            if ( geoProperty instanceof Point
                || geoProperty instanceof MultiPoint ) {
                PointSymbolizer symbolizer = new PointSymbolizer();
                displayElement = buildPointDisplayElement( feature, geoProperty, symbolizer );
            } // LineSymbolizer
            else if ( geoProperty instanceof Curve
                || geoProperty instanceof MultiCurve ) {
                LineSymbolizer symbolizer = new LineSymbolizer();
                displayElement = buildLineStringDisplayElement( feature, geoProperty, symbolizer );
            } // PolygonSymbolizer
            else if ( geoProperty instanceof Surface
                || geoProperty instanceof MultiSurface ) {
                PolygonSymbolizer symbolizer = new PolygonSymbolizer();
                displayElement = buildPolygonDisplayElement( feature, geoProperty, symbolizer );
            } else {
                throw new IncompatibleGeometryTypeException( "not a valid geometry type" );
            }
        }

        return displayElement;
    }

    /**
     * Creates a <tt>PointDisplayElement</tt> using the given geometry and style information.
     * <p>
     * 
     * @param feature
     *            associated <tt>Feature<tt>
     * @param geom geometry information
     * @param sym style information
     * @return constructed <tt>PointDisplayElement</tt>
     */
    public static PointDisplayElement buildPointDisplayElement(
                                                               Feature feature,
                                                               org.deegree.model.spatialschema.Geometry geom,
                                                               PointSymbolizer sym )
        throws ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {

        // if the geometry is not a point geometry, the centroid(s) of the
        // geometry will be used
        PointDisplayElement displayElement = null;
        String className = sym.getResponsibleClass();
        Class clss = Class.forName( className );
        Object[] values = new Object[] { feature, geom, sym };
        if ( geom instanceof Point ) {
            Class[] param = new Class[] { Feature.class, Point.class, PointSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (PointDisplayElement) constructor.newInstance( values );
        } else if ( geom instanceof MultiPoint ) {
            Class[] param = new Class[] { Feature.class, MultiPoint.class, PointSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (PointDisplayElement) constructor.newInstance( values );
        } else if ( geom instanceof MultiPrimitive ) {
            // Primitive[] primitives = ( (MultiPrimitive) geom ).getAllPrimitives();
            // Point[] centroids = new Point[primitives.length];
            Point[] centroids = new Point[1];
            centroids[0] = geom.getCentroid();

            // for ( int i = 0; i < primitives.length; i++ ) {
            // centroids[i] = primitives[i].getCentroid();
            // }

            try {
                Class[] param = new Class[] { Feature.class, MultiPoint.class,
                                             PointSymbolizer.class };
                Constructor constructor = clss.getConstructor( param );
                values[1] = GeometryFactory.createMultiPoint( centroids );
                displayElement = (PointDisplayElement) constructor.newInstance( values );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class[] param = new Class[] { Feature.class, Point.class, PointSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            values[1] = geom.getCentroid();
            displayElement = (PointDisplayElement) constructor.newInstance( values );
        }

        return displayElement;
    }

    /**
     * Creates a <tt>LineStringDisplayElement</tt> using the given geometry and style information.
     * <p>
     * 
     * @param feature
     *            associated <tt>Feature<tt>
     * @param geom geometry information
     * @param sym style information
     * @throws IncompatibleGeometryTypeException if the geometry property is not
     *         a <tt>Curve</tt> or a <tt>MultiCurve</tt>
     * @return constructed <tt>LineStringDisplayElement</tt>
     */
    public static LineStringDisplayElement buildLineStringDisplayElement(Feature feature,
                                                                         org.deegree.model.spatialschema.Geometry geom,
                                                                         LineSymbolizer sym )
                                                    throws IncompatibleGeometryTypeException,
                                                        ClassNotFoundException,
                                                        IllegalAccessException,
                                                        InstantiationException,
                                                        NoSuchMethodException,
                                                        InvocationTargetException,
                                                        GeometryException {
        LineStringDisplayElement displayElement = null;

        String className = sym.getResponsibleClass();
        Class clss = Class.forName( className );
        Object[] values = new Object[] { feature, geom, sym };

        if ( geom instanceof Curve ) {
            Class[] param = new Class[] { Feature.class, Curve.class, LineSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (LineStringDisplayElement) constructor.newInstance( values );
        } else if ( geom instanceof MultiCurve ) {
            Class[] param = new Class[] { Feature.class, MultiCurve.class, LineSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (LineStringDisplayElement) constructor.newInstance( values );
        } else if ( geom instanceof Surface ) {
            // according to OGC SLD specification it is possible to assign a
            // LineSymbolizer to a polygon. To handle this the border of the
            // polygon will be transformed into a lines (curves)
            MultiCurve mc = surfaceToCurve( (Surface) geom );
            displayElement = buildLineStringDisplayElement( feature, mc, sym );
        } else if ( geom instanceof MultiSurface ) {
            // according to OGC SLD specification it is possible to assign a
            // LineSymbolizer to a multipolygon. To handle this the borders of the
            // multipolygons will be transformed into a lines (curves)
            MultiSurface ms = (MultiSurface) geom;
            List list = new ArrayList( 500 );
            for (int i = 0; i < ms.getSize(); i++) {
                MultiCurve mc = surfaceToCurve( ms.getSurfaceAt( i ) );
                for (int j = 0; j < mc.getSize(); j++) {
                    list.add( mc.getCurveAt( j ) );
                }
            }
            Curve[] curves = (Curve[]) list.toArray( new Curve[list.size()] );
            MultiCurve mc = GeometryFactory.createMultiCurve( curves );
            displayElement = buildLineStringDisplayElement( feature, mc, sym );
        } else {
            throw new IncompatibleGeometryTypeException(
                "Tried to create a LineStringDisplayElement from a geometry with "
                    + "an incompatible / unsupported type: '" + geom.getClass().getName() + "'!" );
        }

        return displayElement;
    }

    /**
     * transforms a Surface into a (Multi)Curve
     * 
     * @param geom
     * @return
     * @throws Exception
     */
    private static MultiCurve surfaceToCurve( Surface geom ) throws GeometryException {
        List list = new ArrayList( 100 );
        int k = geom.getNumberOfSurfacePatches();
        for (int i = 0; i < k; i++) {
            Position[] pos = geom.getSurfacePatchAt( i ).getExteriorRing();
            Curve curve = GeometryFactory.createCurve( pos, geom.getCoordinateSystem() );
            list.add( curve );
            Position[][] inn = geom.getSurfacePatchAt( i ).getInteriorRings();
            if ( inn != null ) {
                for (int j = 0; j < inn.length; j++) {
                    curve = GeometryFactory.createCurve( inn[j], geom.getCoordinateSystem() );
                    list.add( curve );
                }
            }
        }
        Curve[] curves = (Curve[]) list.toArray( new Curve[list.size()] );
        MultiCurve mc = GeometryFactory.createMultiCurve( curves );
        return mc;
    }

    /**
     * Creates a <tt>PolygonDisplayElement</tt> using the given geometry and style information.
     * <p>
     * 
     * @param feature
     *            associated <tt>Feature<tt>
     * @param gmObject geometry information
     * @param sym style information
     * @throws IncompatibleGeometryTypeException if the geometry property is not
     *         a <tt>Surface</tt> or a <tt>MultiSurface</tt>
     * @return constructed <tt>PolygonDisplayElement</tt>
     */
    public static PolygonDisplayElement buildPolygonDisplayElement(
                                                                   Feature feature,
                                                                   org.deegree.model.spatialschema.Geometry gmObject,
                                                                   PolygonSymbolizer sym )
        throws IncompatibleGeometryTypeException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {
        PolygonDisplayElement displayElement = null;

        String className = sym.getResponsibleClass();
        Class clss = Class.forName( className );
        Object[] values = new Object[] { feature, gmObject, sym };
        if ( gmObject instanceof Surface ) {
            Class[] param = new Class[] { Feature.class, Surface.class, PolygonSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (PolygonDisplayElement) constructor.newInstance( values );
        } else if ( gmObject instanceof MultiSurface ) {
            Class[] param = new Class[] { Feature.class, MultiSurface.class,
                                         PolygonSymbolizer.class };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (PolygonDisplayElement) constructor.newInstance( values );
        } else {
            throw new IncompatibleGeometryTypeException(
                "Tried to create a LineStringDisplayElement from a geometry with "
                    + "an incompatible / unsupported type: '" + gmObject.getClass().getName()
                    + "'!" );
        }

        return displayElement;
    }

    /**
     * Creates a <tt>LabelDisplayElement</tt> using the given geometry and style information.
     * <p>
     * 
     * @param feature
     *            <tt>Feature</tt> to be used (necessary for evaluation of the label expression)
     * @param gmObject
     *            geometry information
     * @param sym
     *            style information
     * @throws IncompatibleGeometryTypeException
     *             if the geometry property is not a <tt>Point</tt>, a <tt>Surface</tt> or
     *             <tt>MultiSurface</tt>
     * @return constructed <tt>PolygonDisplayElement</tt>
     */
    public static LabelDisplayElement buildLabelDisplayElement(
                                                               Feature feature,
                                                               org.deegree.model.spatialschema.Geometry gmObject,
                                                               TextSymbolizer sym )
        throws IncompatibleGeometryTypeException,
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {

        LabelDisplayElement displayElement = null;

        if ( gmObject instanceof Point
            || gmObject instanceof MultiPoint || gmObject instanceof Surface
            || gmObject instanceof MultiSurface || gmObject instanceof Curve
            || gmObject instanceof MultiCurve ) {
            String className = sym.getResponsibleClass();
            Class clss = Class.forName( className );
            Class[] param = new Class[] { Feature.class,
                                         org.deegree.model.spatialschema.Geometry.class,
                                         TextSymbolizer.class };
            Object[] values = new Object[] { feature, gmObject, sym };
            Constructor constructor = clss.getConstructor( param );
            displayElement = (LabelDisplayElement) constructor.newInstance( values );
        } else {
            throw new IncompatibleGeometryTypeException(
                "Tried to create a LabelDisplayElement from a geometry with "
                    + "an incompatible / unsupported type: '" + gmObject.getClass().getName()
                    + "'!" );
        }

        return displayElement;
    }

    /**
     * Creates a <tt>RasterDisplayElement</tt> from the submitted image. The submitted
     * <tt>Envelope</tt> holds the bounding box of the imgae/raster data.
     * 
     * @param gc
     *            grid coverage
     * @param sym
     *            raster symbolizer
     * 
     * @return RasterDisplayElement
     */
    public static RasterDisplayElement buildRasterDisplayElement( GridCoverage gc,
                                                                 RasterSymbolizer sym ) {
        return new RasterDisplayElement( gc, sym );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DisplayElementFactory.java,v $
Revision 1.20  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.19  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
