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
package org.deegree.model.crs;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Locale;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.WarpPolynomial;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.i18n.Messages;
import org.deegree.model.coverage.grid.AbstractGridCoverage;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.csct.cs.ConvenienceCSFactory;
import org.deegree.model.csct.cs.CoordinateSystem;
import org.deegree.model.csct.ct.CoordinateTransformationFactory;
import org.deegree.model.csct.ct.MathTransform;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.CurveSegment;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfacePatch;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.describecoverage.DomainSet;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.pt.PT_CoordinatePoint;

/**
 * class for transforming deegree geometries to new coordinate
 * reference systems.
 *
 * <p>------------------------------------------------------------</p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.13 $ $Date: 2006/12/03 21:20:15 $
 */
public class GeoTransformer implements IGeoTransformer {

    private ConvenienceCSFactory csFactory = null;

    private CoordinateSystem targetCS = null;

    /**
     * Creates a new GeoTransformer object.
     *
     * @param targetCS 
     *
     * @throws Exception 
     */
    public GeoTransformer( String targetCS ) {
        csFactory = ConvenienceCSFactory.getInstance();
        this.targetCS = csFactory.getCSByName( targetCS );
    }

    /**
     * Creates a new GeoTransformer object.
     * 
     * @param targetCS
     * @throws Exception
     */
    public GeoTransformer( org.deegree.model.crs.CoordinateSystem targetCRS ) {
        csFactory = ConvenienceCSFactory.getInstance();
        this.targetCS = csFactory.getCSByName( targetCRS.getName() );
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(org.deegree.model.spatialschema.Geometry)
     */
    public Geometry transform( Geometry geo )
                            throws CRSTransformationException {

        CoordinateSystem cs = ConvenienceCSFactory.getInstance().getCSByName(
                                                                              geo.getCoordinateSystem().getName() );

        CoordinateTransformationFactory ctfc = CoordinateTransformationFactory.getDefault();
        MathTransform trans = null;

        try {
            trans = ctfc.createFromCoordinateSystems( cs, targetCS ).getMathTransform();
        } catch ( Exception e ) {
            e.printStackTrace();
            String s = Messages.getMessage( "CRS_NOT_SUPPORTED_TRANSFORMATION",
                                            geo.getCoordinateSystem(), targetCS, e.getMessage() );
            throw new CRSTransformationException( s );
        }

        if ( geo instanceof Point ) {
            geo = transform( (Point) geo, trans );
        } else if ( geo instanceof Curve ) {
            geo = transform( (Curve) geo, trans );
        } else if ( geo instanceof Surface ) {
            geo = transform( (Surface) geo, trans );
        } else if ( geo instanceof MultiPoint ) {
            geo = transform( (MultiPoint) geo, trans );
        } else if ( geo instanceof MultiCurve ) {
            geo = transform( (MultiCurve) geo, trans );
        } else if ( geo instanceof MultiSurface ) {
            geo = transform( (MultiSurface) geo, trans );
        }

        return geo;
    }

    /**
     * transforms the submitted point to the target coordinate reference system
     */
    private Geometry transform( Point geo, MathTransform trans )
                            throws CRSTransformationException {

        try {
            double[] din = geo.getAsArray();

            if ( geo.getCoordinateSystem().getUnits().equals( "°" ) ) {
                if ( din[0] <= -179.999 )
                    din[0] = -179.999;
                else if ( din[0] >= 179.999 )
                    din[0] = 179.999;
                if ( din[1] <= -89.999 )
                    din[1] = -89.999;
                else if ( din[1] >= 89.999 )
                    din[1] = 89.999;
            }

            double[] di = new double[] { din[0], din[1] };
            double[] dou = new double[2];

            trans.transform( di, 0, dou, 0, di.length - 1 );

            org.deegree.model.crs.CoordinateSystem crs = CRSFactory.create( targetCS.getName( Locale.getDefault() ) );
            geo = GeometryFactory.createPoint( dou[0], dou[1], din[2], crs );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CRSTransformationException( e );
        }

        return geo;
    }

    /**
     * transforms the submitted curve to the target coordinate reference system
     */
    private Geometry transform( Curve geo, MathTransform trans )
                            throws CRSTransformationException {

        try {
            CurveSegment[] newcus = new CurveSegment[geo.getNumberOfCurveSegments()];

            org.deegree.model.crs.CoordinateSystem crs = CRSFactory.create( targetCS.getName( Locale.getDefault() ) );
            for ( int i = 0; i < geo.getNumberOfCurveSegments(); i++ ) {
                CurveSegment cus = geo.getCurveSegmentAt( i );
                Position[] pos = cus.getPositions();
                pos = transform( pos, trans );
                newcus[i] = GeometryFactory.createCurveSegment( pos, crs );
            }

            geo = GeometryFactory.createCurve( newcus );
        } catch ( Exception e ) {
            throw new CRSTransformationException( e );
        }

        return geo;
    }

    /**
     * transforms the submitted surface to the target coordinate reference system
     */
    private Geometry transform( Surface geo, MathTransform trans )
                            throws CRSTransformationException {

        try {
            int cnt = geo.getNumberOfSurfacePatches();
            SurfacePatch[] patches = new SurfacePatch[cnt];

            org.deegree.model.crs.CoordinateSystem crs = CRSFactory.create( targetCS.getName( Locale.getDefault() ) );
            for ( int i = 0; i < cnt; i++ ) {
                SurfacePatch p = geo.getSurfacePatchAt( i );
                Position[] ex = p.getExteriorRing();
                ex = transform( ex, trans );

                Position[][] in = p.getInteriorRings();
                Position[][] inn = null;

                if ( in != null ) {
                    inn = new Position[in.length][];

                    for ( int k = 0; k < in.length; k++ ) {
                        inn[k] = transform( in[k], trans );
                    }
                }

                patches[i] = GeometryFactory.createSurfacePatch( ex, inn, p.getInterpolation(), crs );
            }

            // at the moment only polygons made of one patch are supported
            geo = GeometryFactory.createSurface( patches[0] );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new CRSTransformationException( e );
        }

        return geo;
    }

    /**
     * transforms the submitted multi point to the target coordinate reference
     * system
     */
    private Geometry transform( MultiPoint geo, MathTransform trans )
                            throws CRSTransformationException {

        try {
            Point[] points = new Point[geo.getSize()];

            for ( int i = 0; i < geo.getSize(); i++ ) {
                points[i] = (Point) transform( geo.getPointAt( i ), trans );
            }

            geo = GeometryFactory.createMultiPoint( points );
        } catch ( Exception e ) {
            throw new CRSTransformationException( e );
        }

        return geo;
    }

    /**
     * transforms the submitted multi curve to the target coordinate reference
     * system
     */
    private Geometry transform( MultiCurve geo, MathTransform trans )
                            throws CRSTransformationException {

        try {
            Curve[] curves = new Curve[geo.getSize()];

            for ( int i = 0; i < geo.getSize(); i++ ) {
                curves[i] = (Curve) transform( geo.getCurveAt( i ), trans );
            }

            geo = GeometryFactory.createMultiCurve( curves );
        } catch ( Exception e ) {
            throw new CRSTransformationException( e );
        }

        return geo;
    }

    /**
     * transforms the submitted multi surface to the target coordinate reference
     * system
     */
    private Geometry transform( MultiSurface geo, MathTransform trans )
                            throws CRSTransformationException {

        Surface[] surfaces = new Surface[geo.getSize()];

        for ( int i = 0; i < geo.getSize(); i++ ) {
            surfaces[i] = (Surface) transform( geo.getSurfaceAt( i ), trans );
        }

        geo = GeometryFactory.createMultiSurface( surfaces );

        return geo;
    }

    /**
     * transfroms an array of Positions to the target coordinate reference
     * system
     */
    private Position[] transform( Position[] pos, MathTransform trans )
                            throws Exception {

        Position[] newpos = new Position[pos.length];

        for ( int k = 0; k < pos.length; k++ ) {
            double[] din = pos[k].getAsArray();
            double[] di = new double[] { din[0], din[1] };
            double[] dou = new double[2];
            trans.transform( di, 0, dou, 0, di.length - 1 );
            newpos[k] = GeometryFactory.createPosition( dou[0], dou[1], din[2] );
        }

        return newpos;
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(org.deegree.model.spatialschema.Envelope, java.lang.String)
     */
    public Envelope transform( Envelope envelope, String sourceCRS )
                            throws CRSTransformationException {

        org.deegree.model.crs.CoordinateSystem crs = null;
        try {
            crs = CRSFactory.create( sourceCRS );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return transform( envelope, crs );
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(org.deegree.model.spatialschema.Envelope, org.deegree.model.crs.CoordinateSystem)
     */
    public Envelope transform( Envelope envelope, org.deegree.model.crs.CoordinateSystem sourceCRS )
                            throws CRSTransformationException {

        Point min = GeometryFactory.createPoint( envelope.getMin().getX(),
                                                 envelope.getMin().getY(), sourceCRS );
        Point max = GeometryFactory.createPoint( envelope.getMax().getX(),
                                                 envelope.getMax().getY(), sourceCRS );
        min = (Point) transform( min );
        max = (Point) transform( max );

        org.deegree.model.crs.CoordinateSystem crs = null;
        try {
            crs = CRSFactory.create( targetCS.getName( Locale.getDefault() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        // create bounding box with coordinates
        return GeometryFactory.createEnvelope( min.getX(), min.getY(), max.getX(), max.getY(), crs );
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(org.deegree.model.feature.FeatureCollection)
     */
    public FeatureCollection transform( FeatureCollection fc )
                            throws CRSTransformationException, GeometryException {

        for ( int i = 0; i < fc.size(); i++ ) {
            transform( fc.getFeature( i ) );
        }

        return fc;
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(org.deegree.model.feature.Feature)
     */
    public Feature transform( Feature feature )
                            throws CRSTransformationException, GeometryException {

        org.deegree.model.crs.CoordinateSystem crs = null;
        try {
            crs = CRSFactory.create( targetCS.getName( Locale.getDefault() ) );
        } catch ( Exception e ) {
        }
        FeatureProperty[] fp = feature.getProperties();
        for ( int i = 0; i < fp.length; i++ ) {

            if ( fp[i].getValue() instanceof Geometry ) {
                Geometry geom = (Geometry) fp[i].getValue();
                if ( !crs.equals( geom.getCoordinateSystem() ) ) {
                    fp[i].setValue( transform( (Geometry) fp[i].getValue() ) );
                }
            } else if ( fp[i].getValue() instanceof Feature ) {
                transform( (Feature) fp[i].getValue() );
            }

        }

        return feature;
    }

    /* (non-Javadoc)
     * @see org.deegree.model.crs.IGeoTransformer#transform(GridCoverage coverage, int refPointsGridSize, int degree)
     */
    public GridCoverage transform( AbstractGridCoverage coverage, int refPointsGridSize,
                                  int degree, Interpolation interpolation )
                            throws CRSTransformationException {

        BufferedImage img = coverage.getAsImage( -1, -1 );
        PT_CoordinatePoint min = coverage.getEnvelope().minCP;
        PT_CoordinatePoint max = coverage.getEnvelope().maxCP;

        // create transformation object to transform reference points
        // from the source (native) CRS to the target CRS 
        org.deegree.model.crs.CoordinateSystem crs = coverage.getCoordinateReferenceSystem();
        Envelope sourceBBOX = GeometryFactory.createEnvelope( min.ord[0], min.ord[1], max.ord[0],
                                                              max.ord[1], crs );
        Envelope targetBBOX = transform( sourceBBOX, crs );

        GeoTransform sourceGT = new WorldToScreenTransform( sourceBBOX.getMin().getX(),
                                                            sourceBBOX.getMin().getY(),
                                                            sourceBBOX.getMax().getX(),
                                                            sourceBBOX.getMax().getY(), 0, 0,
                                                            img.getWidth() - 1, img.getHeight() - 1 );
        GeoTransform targetGT = new WorldToScreenTransform( targetBBOX.getMin().getX(),
                                                            targetBBOX.getMin().getY(),
                                                            targetBBOX.getMax().getX(),
                                                            targetBBOX.getMax().getY(), 0, 0,
                                                            img.getWidth() - 1, img.getHeight() - 1 );

        // create/calculate reference points
        float dx = img.getWidth() / (float) ( refPointsGridSize - 1 );
        float dy = img.getHeight() / (float) ( refPointsGridSize - 1 );
        float[] srcCoords = new float[refPointsGridSize * refPointsGridSize * 2];
        float[] targetCoords = new float[refPointsGridSize * refPointsGridSize * 2];
        int k = 0;
        for ( int i = 0; i < refPointsGridSize; i++ ) {
            for ( int j = 0; j < refPointsGridSize; j++ ) {
                srcCoords[k] = i * dx;
                srcCoords[k + 1] = j * dy;
                double x = sourceGT.getSourceX( srcCoords[k] );
                double y = sourceGT.getSourceY( srcCoords[k + 1] );
                Point point = GeometryFactory.createPoint( x, y, crs );       
                point = (Point) transform( point ); 
                targetCoords[k] = (float) targetGT.getDestX( point.getX() );
                targetCoords[k + 1] = (float) targetGT.getDestY( point.getY() );                
                k += 2;
            }
        }
        
        // create warp object from reference points and desired interpolation
        WarpPolynomial warp = WarpPolynomial.createWarp( srcCoords, 0, targetCoords, 0,
                                                         srcCoords.length, 1f, 1f, 1f, 1f, degree );

        if ( interpolation == null ) {
            interpolation = new InterpolationNearest();
        }
        
        // Create and perform the warp operation.
        ParameterBlock pb = new ParameterBlock();
        pb.addSource( img );
        pb.add( warp );
        pb.add( interpolation );
        
        img = JAI.create( "warp", pb ).getAsBufferedImage();

        // create a new GridCoverage from the warp result.
        // because warping only can be performed on images the
        // resulting GridCoverage will be an instance of ImageGridCoverage
        CoverageOffering oldCO = coverage.getCoverageOffering();
        CoverageOffering coverageOffering = null;
        if ( oldCO != null ) {            
            try {
                DomainSet ds = oldCO.getDomainSet();
                ds.getSpatialDomain().setEnvelops( new Envelope[] { targetBBOX } );
                coverageOffering = new CoverageOffering( oldCO.getName(), oldCO.getLabel(),
                                                         oldCO.getDescription(),
                                                         oldCO.getMetadataLink(),
                                                         oldCO.getLonLatEnvelope(),
                                                         oldCO.getKeywords(), ds,
                                                         oldCO.getRangeSet(), oldCO.getSupportedCRSs(),
                                                         oldCO.getSupportedFormats(),
                                                         oldCO.getSupportedInterpolations(),
                                                         oldCO.getExtension() );
            } catch ( Exception e ) {
                String s = Messages.getMessage( "CRS_CO_CREATION_ERROR", e.getMessage() );
                throw new CRSTransformationException( s );
            }
        }
        return new ImageGridCoverage( coverageOffering, sourceBBOX, img );
    }

}