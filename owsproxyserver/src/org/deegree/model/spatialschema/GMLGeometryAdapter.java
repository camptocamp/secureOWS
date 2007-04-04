//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/spatialschema/GMLGeometryAdapter.java,v 1.40 2006/11/27 09:07:51 poth Exp $
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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.spatialschema;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.InvalidGMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Adapter class for converting GML geometries to deegree geometries and vice versa. Some logical
 * problems results from the fact that an envelope isn't a geometry according to ISO 19107 (where
 * the deegree geometry model is based on) but according to GML2/3 specification it is.<br>
 * So if the wrap(..) method is called with an envelope a <tt>Surface</tt> will be returned
 * representing the envelops shape. To export an <tt>Envelope</tt> to a GML box/envelope two
 * specialized export methods are available.<BR>
 * The export method(s) doesn't return a DOM element as one may expect but a <tt>StringBuffer</tt>.
 * This is done because the transformation from deegree geometries to GML mainly is required when a
 * GML representation of a geometry shall be serialized to a file or to a network connection. For
 * both cases the string representation is required. and it is simply faster to create the string
 * directly instead of first creating a DOM tree that after this must be serialized to a string.<BR>
 * In future version geometries will be serialized to a stream.
 * 
 * @version $Revision: 1.40 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.40 $, $Date: 2006/11/27 09:07:51 $
 * 
 * @since 2.0
 */

public class GMLGeometryAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( GMLGeometryAdapter.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static Map crsMap = new HashMap();

    private static final String COORD = CommonNamespaces.GML_PREFIX + ":coord";

    private static final String COORDINATES = CommonNamespaces.GML_PREFIX + ":coordinates";

    private static final String POS = CommonNamespaces.GML_PREFIX + ":pos";

    private static final String POSLIST = CommonNamespaces.GML_PREFIX + ":posList";

    /**
     * Creates a GML representation from the passed <code>Geometry<code>
     *  
     * @param geometry
     * @param target 
     * @throws GeometryException
     */
    public static PrintWriter export( Geometry geometry, OutputStream target )
                            throws GeometryException {

        PrintWriter printwriter = new PrintWriter( target );

        if ( geometry instanceof SurfacePatch ) {
            geometry = new SurfaceImpl( (SurfacePatch) geometry );
        } else if ( geometry instanceof LineString ) {
            geometry = new CurveImpl( (LineString) geometry );
        }
        // create geometries from the wkb considering the geomerty typ
        if ( geometry instanceof Point ) {
            exportPoint( (Point) geometry, printwriter );
        } else if ( geometry instanceof Curve ) {
            exportCurve( (Curve) geometry, printwriter );
        } else if ( geometry instanceof Surface ) {
            exportSurface( (Surface) geometry, printwriter );
        } else if ( geometry instanceof MultiPoint ) {
            exportMultiPoint( (MultiPoint) geometry, printwriter );
        } else if ( geometry instanceof MultiCurve ) {
            exportMultiCurve( (MultiCurve) geometry, printwriter );
        } else if ( geometry instanceof MultiSurface ) {
            exportMultiSurface( (MultiSurface) geometry, printwriter );
        }

        printwriter.flush();
        return printwriter;

    }

    /**
     * creates a GML representation from the passed <tt>Geometry</tt>
     * 
     * @param geometry
     * @return
     * @throws GeometryException
     */
    public static StringBuffer export( Geometry geometry )
                            throws GeometryException {

        if ( geometry instanceof SurfacePatch ) {
            geometry = new SurfaceImpl( (SurfacePatch) geometry );
        } else if ( geometry instanceof LineString ) {
            geometry = new CurveImpl( (LineString) geometry );
        }

        StringBuffer sb = null;
        // create geometries from the wkb considering the geomerty typ
        if ( geometry instanceof Point ) {
            sb = exportPoint( (Point) geometry );
        } else if ( geometry instanceof Curve ) {
            sb = exportCurve( (Curve) geometry );
        } else if ( geometry instanceof Surface ) {
            sb = exportSurface( (Surface) geometry );
        } else if ( geometry instanceof MultiPoint ) {
            sb = exportMultiPoint( (MultiPoint) geometry );
        } else if ( geometry instanceof MultiCurve ) {
            sb = exportMultiCurve( (MultiCurve) geometry );
        } else if ( geometry instanceof MultiSurface ) {
            sb = exportMultiSurface( (MultiSurface) geometry );
        }

        return sb;
    }

    /**
     * creates a GML representation from the passed <tt>Envelope</tt>. This method is required
     * because in ISO 19107 Envelops are no geometries.
     * 
     * @param envelope
     * @return
     * @throws GeometryException
     */
    public static StringBuffer exportAsBox( Envelope envelope ) {

        StringBuffer sb = new StringBuffer( "<gml:Box xmlns:gml='http://www.opengis.net/gml'>" );
        sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );
        sb.append( envelope.getMin().getX() ).append( ',' );
        sb.append( envelope.getMin().getY() );
        int dim = envelope.getMax().getCoordinateDimension();
        if ( dim == 3 ) {
            sb.append( ',' ).append( envelope.getMin().getZ() );
        }
        sb.append( ' ' ).append( envelope.getMax().getX() );
        sb.append( ',' ).append( envelope.getMax().getY() );
        if ( dim == 3 ) {
            sb.append( ',' ).append( envelope.getMax().getZ() );
        }
        sb.append( "</gml:coordinates></gml:Box>" );

        return sb;
    }

    /**
     * creates a GML representation from the passed <tt>Envelope</tt>. This method is required
     * because in ISO 19107 Envelops are no geometries.
     * 
     * @param envelope
     * @return
     * @throws GeometryException
     */
    public static StringBuffer exportAsEnvelope( Envelope envelope ) {

        StringBuffer sb = new StringBuffer( "<gml:Envelope " );
        sb.append( "xmlns:gml='http://www.opengis.net/gml'>" );
        sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );
        sb.append( envelope.getMin().getX() ).append( ',' );
        sb.append( envelope.getMin().getY() );
        int dim = envelope.getMax().getCoordinateDimension();
        if ( dim == 3 ) {
            sb.append( ',' ).append( envelope.getMin().getZ() );
        }
        sb.append( ' ' ).append( envelope.getMax().getX() );
        sb.append( ',' ).append( envelope.getMax().getY() );
        if ( dim == 3 ) {
            sb.append( ',' ).append( envelope.getMax().getZ() );
        }
        sb.append( "</gml:coordinates></gml:Envelope>" );

        return sb;
    }

    /**
     * Converts the string representation of a GML geometry object to a corresponding
     * <tt>Geometry</tt>. Notice that GML Boxes will be converted to Surfaces because in ISO
     * 19107 Envelops are no geometries.
     * 
     * @param gml
     * @return
     * @throws GeometryException
     * @throws XMLParsingException
     */
    public static Geometry wrap( String gml )
                            throws GeometryException, XMLParsingException {
        StringReader sr = new StringReader( gml );
        Document doc = null;
        try {
            doc = XMLTools.parse( sr );
        } catch ( Exception e ) {
            LOG.logError( "could not parse: '" + gml + "' as GML/XML", e );
            throw new XMLParsingException( "could not parse: '" + gml + "' as GML/XML: "
                                           + e.getMessage() );
        }
        return wrap( doc.getDocumentElement() );
    }

    /**
     * Converts a GML geometry object to a corresponding <tt>Geometry</tt>. Notice that GML Boxes
     * will be converted to Surfaces because in ISO 19107 Envelops are no geometries.
     * <p>
     * Currently, the following conversions are supported:
     * <ul>
     * <li>GML Point -> Point
     * <li>GML MultiPoint -> MultiPoint
     * <li>GML LineString -> Curve
     * <li>GML MultiLineString -> MultiCurve
     * <li>GML Polygon -> Surface
     * <li>GML MultiPolygon -> MultiSurface
     * <li>GML Box -> Surface
     * <li>GML Curve -> Curve
     * <li>GML Surface -> Surface
     * <li>GML MultiCurve -> MultiCurve
     * <li>GML MultiSurface -> MultiSurface
     * </ul>
     * <p>
     * 
     * @param gml
     * @return the corresponding <tt>Geometry</tt>
     * @throws GeometryException
     *             if type unsupported or conversion failed
     */
    public static Geometry wrap( Element gml )
                            throws GeometryException {

        Geometry geometry = null;
        try {
            String name = gml.getLocalName();
            if ( ( name.equals( "Point" ) ) || ( name.equals( "Center" ) ) ) {
                geometry = wrapPoint( gml );
            } else if ( name.equals( "LineString" ) ) {
                geometry = wrapLineString( gml );
            } else if ( name.equals( "Polygon" ) ) {
                geometry = wrapPolygon( gml );
            } else if ( name.equals( "MultiPoint" ) ) {
                geometry = wrapMultiPoint( gml );
            } else if ( name.equals( "MultiLineString" ) ) {
                geometry = wrapMultiLineString( gml );
            } else if ( name.equals( "MultiPolygon" ) ) {
                geometry = wrapMultiPolygon( gml );
            } else if ( name.equals( "Box" ) || name.equals( "Envelope" ) ) {
                geometry = wrapBoxAsSurface( gml );
            } else if ( name.equals( "Curve" ) ) {
                geometry = wrapCurveAsCurve( gml );
            } else if ( name.equals( "Surface" ) ) {
                geometry = wrapSurfaceAsSurface( gml );
            } else if ( name.equals( "MultiCurve" ) ) {
                geometry = wrapMultiCurveAsMultiCurve( gml );
            } else if ( name.equals( "MultiSurface" ) ) {
                geometry = wrapMultiSurfaceAsMultiSurface( gml );
            } else if ( name.equals( "CompositeSurface" ) ) {
                geometry = wrapCompositeSurface( gml );
            } else {
                new GeometryException( "Not a supported geometry type: " + name );
            }
        } catch ( Exception e ) {
            throw new GeometryException( StringTools.stackTraceToString( e ) );
        }

        return geometry;
    }

    /**
     * Returns an instance of a CompositeSurface created from the passed <gml:CompositeSurface> TODO
     * 
     * @param gml
     * @return CompositeSurface
     * @throws GeometryException
     */
    private static CompositeSurface wrapCompositeSurface( Element gml ) {
        throw new UnsupportedOperationException( "wrapCompositeSurface( Element) not "
                                                 + "implemented as yet. Work in Progress." );
    }

    /**
     * Returns an instance of a Curve created from the passed <gml:Curve>
     * 
     * @param element
     * @return Curve
     * @throws XMLParsingException
     * @throws GeometryException
     * @throws UnknownCRSException 
     */
    protected static Curve wrapCurveAsCurve( Element curve )
                            throws XMLParsingException, GeometryException, UnknownCRSException {

        String srs = XMLTools.getAttrValue( curve, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        Element segment = (Element) XMLTools.getRequiredNode( curve, "gml:segments", nsContext );

        List list = XMLTools.getNodes( segment, "gml:LineStringSegment", nsContext );

        CurveSegment[] segments = new CurveSegment[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            Element lineStringSegment = (Element) list.get( i );
            Position[] pos = null;
            try {
                pos = createPositions( lineStringSegment, srs );
                segments[i] = GeometryFactory.createCurveSegment( pos, crs );
            } catch ( Exception e ) {
                throw new GeometryException(
                                             "Error creating segments for the element LineStringSegment." );
            }
        }

        return GeometryFactory.createCurve( segments );
    }

    /**
     * Returns an instance of a Surface created from the passed <gml:Surface>
     * 
     * @param gml
     * @return Surface
     * @throws XMLParsingException
     * @throws GeometryException
     */
    protected static Surface wrapSurfaceAsSurface( Element surfaceElement )
                            throws XMLParsingException, GeometryException {

        CoordinateSystem crs = null;

        Element patches = extractPatches( surfaceElement );

        List polygonList = extractPolygons( patches );

        Polygon[] polygons = new Polygon[polygonList.size()];

        for ( int i = 0; i < polygonList.size(); i++ ) {

            Element polygon = (Element) polygonList.get( i );
            try {

                Element exterior = (Element) XMLTools.getNode( polygon, "gml:exterior", nsContext );

                Position[] exteriorRing = null;

                if ( exterior != null ) {
                    Element linearRing = (Element) XMLTools.getRequiredNode( exterior,
                                                                             "gml:LinearRing",
                                                                             nsContext );

                    String srs = XMLTools.getAttrValue( linearRing, "srsName" );
                    if ( srs != null ) {
                        crs = getCRS( srs );
                    }
                    exteriorRing = createPositions( linearRing, srs );
                }

                List interiorList = XMLTools.getNodes( polygon, "gml:interior", nsContext );

                Position[][] interiorRings = null;
                if ( interiorList != null && interiorList.size() > 0 ) {

                    interiorRings = new Position[interiorList.size()][];

                    for ( int j = 0; j < interiorRings.length; j++ ) {

                        Element interior = (Element) interiorList.get( j );

                        Element linearRing = (Element) XMLTools.getRequiredNode( interior,
                                                                                 "gml:LinearRing",
                                                                                 nsContext );

                        String srs = XMLTools.getAttrValue( linearRing, "srsName" );
                        if ( srs != null ) {
                            crs = getCRS( srs );
                        }
                        interiorRings[j] = createPositions( interior, srs );
                    }
                }
                SurfaceInterpolation si = new SurfaceInterpolationImpl();
                polygons[i] = (Polygon) GeometryFactory.createSurfacePatch( exteriorRing,
                                                                            interiorRings, si, crs );
            } catch ( Exception e ) {
                throw new XMLParsingException( "Error parsing the polygon element '"
                                               + polygon.getNodeName()
                                               + "' to create a surface geometry." );
            }
        }
        Surface surface = null;
        try {
            surface = GeometryFactory.createSurface( polygons );
        } catch ( GeometryException e ) {
            throw new GeometryException( "Error creating a surface from '" + polygons.length
                                         + "' polygons." );
        }
        return surface;
    }

    /**
     * Extract the list <gml:Polygon> elements from <gml:patches>.
     * 
     * @param patch
     * @return List
     * @throws XMLParsingException
     */
    private static List extractPolygons( Element patch )
                            throws XMLParsingException {
        List polygonList = null;
        try {
            polygonList = XMLTools.getRequiredNodes( patch, "gml:Polygon", nsContext );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException(
                                           "Error retrieving the list of polygon element(s) from the patches element." );
        }
        return polygonList;
    }

    /**
     * Extract the <gml:patches> node from a <gml:Surface> element.
     * 
     * @param surface
     * @return Element
     * @throws XMLParsingException
     */
    private static Element extractPatches( Element surface )
                            throws XMLParsingException {
        Element patches = null;
        try {
            patches = (Element) XMLTools.getRequiredNode( surface, "gml:patches", nsContext );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException(
                                           "Error retrieving the patches element from the surface element." );
        }
        return patches;
    }

    /**
     * Returns an instance of a MultiCurve created from the passed <gml:MultiCurve> element.
     * 
     * @param multiCurveElement
     * @return MultiCurve
     * @throws XMLParsingException
     * @throws GeometryException
     * @throws UnknownCRSException 
     */
    protected static MultiCurve wrapMultiCurveAsMultiCurve( Element multiCurveElement )
                            throws XMLParsingException, GeometryException, UnknownCRSException {

        MultiCurve multiCurve = null;
        try {
            // gml:curveMember
            List listCurveMember = XMLTools.getNodes( multiCurveElement, "gml:curveMember",
                                                      nsContext );
            if ( listCurveMember.size() > 0 ) {
                Curve[] curves = new Curve[listCurveMember.size()];
                for ( int i = 0; i < listCurveMember.size(); i++ ) {
                    Element curveMember = (Element) listCurveMember.get( i );
                    Element curve = (Element) XMLTools.getRequiredNode( curveMember, "gml:Curve",
                                                                        nsContext );
                    curves[i] = wrapCurveAsCurve( curve );
                }
                multiCurve = GeometryFactory.createMultiCurve( curves );
            } else {
                // gml:curveMembers
                Element curveMembers = (Element) XMLTools.getRequiredNode( multiCurveElement,
                                                                           "gml:curveMembers",
                                                                           nsContext );
                List listCurves = XMLTools.getRequiredNodes( curveMembers, "gml:Curve", nsContext );
                Curve[] curves = new Curve[listCurves.size()];
                for ( int i = 0; i < listCurves.size(); i++ ) {
                    Element curve = (Element) listCurves.get( i );
                    curves[i] = wrapCurveAsCurve( curve );
                }
                multiCurve = GeometryFactory.createMultiCurve( curves );
            }
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException(
                                           "Error parsing <gml:curveMember> elements. Please check the xml document." );
        } catch ( GeometryException e ) {
            throw new GeometryException(
                                         "Error creating a curve from the curve element. Please check the GML specifications "
                                                                 + "for correct element declaration." );
        }

        return multiCurve;
    }

    /**
     * Returns an instance of a MultiSurface created from the passed <gml:MultiSurface> element.
     * 
     * @param multiSurfaceElement
     * @return MultiSurface
     * @throws XMLParsingException
     * @throws GeometryException
     * @throws InvalidGMLException
     * @throws UnknownCRSException 
     */
    protected static MultiSurface wrapMultiSurfaceAsMultiSurface( Element multiSurfaceElement )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {

        MultiSurface multiSurface = null;
        try {
            // gml:surfaceMember
            List listSurfaceMember = XMLTools.getNodes( multiSurfaceElement, "gml:surfaceMember",
                                                        nsContext );
            if ( listSurfaceMember.size() > 0 ) {
                Surface[] surfaces = new Surface[listSurfaceMember.size()];
                for ( int i = 0; i < listSurfaceMember.size(); i++ ) {
                    Element surfaceMember = (Element) listSurfaceMember.get( i );
                    Element surface = (Element) XMLTools.getNode( surfaceMember, "gml:Surface",
                                                                  nsContext );
                    if ( surface != null ) {
                        surfaces[i] = wrapSurfaceAsSurface( surface );
                    } else {
                        surface = (Element) XMLTools.getRequiredNode( surfaceMember, "gml:Polygon",
                                                                      nsContext );
                        surfaces[i] = wrapPolygon( surface );
                    }
                }
                multiSurface = GeometryFactory.createMultiSurface( surfaces );
            } else {
                // gml:surfaceMembers
                Element surfaceMembers = (Element) XMLTools.getRequiredNode( multiSurfaceElement,
                                                                             "gml:surfaceMembers",
                                                                             nsContext );
                List listSurfaces = XMLTools.getNodes( surfaceMembers, "gml:Surface", nsContext );
                Surface[] surfaces = null;
                if ( listSurfaces != null && listSurfaces.size() > 0 ) {
                    surfaces = new Surface[listSurfaces.size()];
                    for ( int i = 0; i < listSurfaces.size(); i++ ) {
                        Element surface = (Element) listSurfaces.get( i );
                        surfaces[i] = wrapSurfaceAsSurface( surface );
                    }
                } else {
                    listSurfaces = XMLTools.getRequiredNodes( surfaceMembers, "gml:Polygon",
                                                              nsContext );
                    surfaces = new Surface[listSurfaces.size()];
                    for ( int i = 0; i < listSurfaces.size(); i++ ) {
                        Element surface = (Element) listSurfaces.get( i );
                        surfaces[i] = wrapPolygon( surface );
                    }
                }
                multiSurface = GeometryFactory.createMultiSurface( surfaces );
            }
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException(
                                           "Error parsing <gml:surfaceMember> elements. Please check "
                                                                   + "the xml document." );
        } catch ( GeometryException e ) {
            throw new GeometryException(
                                         "Error creating a curve from the curve element. Please check "
                                                                 + "the GML specifications for correct element declaration." );
        }

        return multiSurface;
    }

    /**
     * returns an instance of CS_CoordinateSystem corrsponding to the passed crs name
     * 
     * @param name
     *            name of the crs
     * 
     * @return CS_CoordinateSystem
     * @throws UnknownCRSException 
     */
    private static CoordinateSystem getCRS( String name )
                            throws UnknownCRSException {

        if ( ( name != null ) && ( name.length() > 2 ) ) {
            if ( name.startsWith( "http://www.opengis.net/gml/srs/" ) ) {
                // as declared in the GML 2.1.1 specification
                // http://www.opengis.net/gml/srs/epsg.xml#4326
                int p = name.lastIndexOf( "/" );

                if ( p >= 0 ) {
                    name = name.substring( p, name.length() );
                    p = name.indexOf( "." );

                    String s1 = name.substring( 1, p ).toUpperCase();
                    p = name.indexOf( "#" );

                    String s2 = name.substring( p + 1, name.length() );
                    name = s1 + ":" + s2;
                }
            }
        }

        CoordinateSystem crs = (CoordinateSystem) crsMap.get( name );

        if ( crs == null ) {
            crs = CRSFactory.create( name );
            crsMap.put( name, crs );
        }

        return crs;
    }

    /**
     * returns an instance of a point created from the passed <gml:Point>
     * 
     * @param element
     *            <gml:Point>
     * 
     * @return instance of Point
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static Point wrapPoint( Element element )
                            throws XMLParsingException, InvalidGMLException, UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        Position[] bb = null;
        List nl = XMLTools.getNodes( element, COORD, nsContext );
        if ( nl != null && nl.size() > 0 ) {
            bb = new Position[1];
            bb[0] = createPositionFromCoord( (Element) nl.get( 0 ) );
        } else {
            nl = XMLTools.getNodes( element, COORDINATES, nsContext );
            if ( nl != null && nl.size() > 0 ) {
                bb = createPositionFromCoordinates( (Element) nl.get( 0 ) );
            } else {
                nl = XMLTools.getNodes( element, POS, nsContext );
                bb = new Position[1];
                bb[0] = createPositionFromPos( (Element) nl.get( 0 ) );
            }
        }

        Point point = GeometryFactory.createPoint( bb[0], crs );

        return point;
    }

    /**
     * returns an instance of a curve created from the passed <gml:LineString>
     * 
     * @param element
     *            <gml:LineString>
     * 
     * @return instance of Curve
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static Curve wrapLineString( Element element )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        Position[] pos = createPositions( element, srs );

        Curve curve = GeometryFactory.createCurve( pos, crs );

        return curve;
    }

    /**
     * returns an instance of a surface created from the passed <gml:Polygon>
     * 
     * @param element
     *            <gml:Polygon>
     * 
     * @return instance of Surface
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static Surface wrapPolygon( Element element )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        List nl = XMLTools.getNodes( element, CommonNamespaces.GML_PREFIX + ":outerBoundaryIs",
                                     nsContext );
        if ( nl == null || nl.size() == 0 ) {
            nl = XMLTools.getRequiredNodes( element, CommonNamespaces.GML_PREFIX + ":exterior",
                                            nsContext );
        }
        Element outs = (Element) nl.get( 0 );
        nl = XMLTools.getRequiredNodes( outs, CommonNamespaces.GML_PREFIX + ":LinearRing",
                                        nsContext );
        Element ring = (Element) nl.get( 0 );
        nl = XMLTools.getNodes( ring, COORDINATES, nsContext );
        Position[] outterRing = createPositions( ring, srs );

        Position[][] innerRings = null;
        List inns = XMLTools.getNodes( element, CommonNamespaces.GML_PREFIX + ":innerBoundaryIs",
                                       nsContext );
        if ( inns == null || inns.size() == 0 ) {
            inns = XMLTools.getNodes( element, CommonNamespaces.GML_PREFIX + ":interior", nsContext );
        }
        if ( inns != null && inns.size() > 0 ) {
            innerRings = new Position[inns.size()][];
            for ( int i = 0; i < innerRings.length; i++ ) {

                nl = XMLTools.getRequiredNodes( (Node) inns.get( i ), CommonNamespaces.GML_PREFIX
                                                                      + ":LinearRing", nsContext );

                ring = (Element) nl.get( 0 );
                innerRings[i] = createPositions( ring, srs );
            }
        }

        SurfaceInterpolation si = new SurfaceInterpolationImpl();
        Surface surface = GeometryFactory.createSurface( outterRing, innerRings, si, crs );

        return surface;
    }

    /**
     * returns an instance of a multi point created from the passed <gml:MultiPoint>
     * 
     * @param element
     *            <gml:MultiPoint>
     * 
     * @return instance of MultiPoint
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static MultiPoint wrapMultiPoint( Element element )
                            throws XMLParsingException, InvalidGMLException, UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        ElementList el = XMLTools.getChildElements( "pointMember", CommonNamespaces.GMLNS, element );
        Point[] points = new Point[el.getLength()];
        for ( int i = 0; i < points.length; i++ ) {
            points[i] = wrapPoint( XMLTools.getFirstChildElement( el.item( i ) ) );
            ( (GeometryImpl) points[i] ).setCoordinateSystem( crs );
        }

        MultiPoint mp = GeometryFactory.createMultiPoint( points );

        return mp;
    }

    /**
     * returns an instance of a multi point created from the passed <gml:MultiLineString>
     * 
     * @param element
     *            <gml:MultiLineString>
     * 
     * @return instance of MultiCurve
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static MultiCurve wrapMultiLineString( Element element )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        ElementList el = XMLTools.getChildElements( "lineStringMember", CommonNamespaces.GMLNS,
                                                    element );
        Curve[] curves = new Curve[el.getLength()];
        for ( int i = 0; i < curves.length; i++ ) {
            curves[i] = wrapLineString( XMLTools.getFirstChildElement( el.item( i ) ) );
            ( (GeometryImpl) curves[i] ).setCoordinateSystem( crs );
        }

        MultiCurve mp = GeometryFactory.createMultiCurve( curves );

        return mp;
    }

    /**
     * returns an instance of a multi point created from the passed <gml:MultiLineString>
     * 
     * @param element
     *            <gml:MultiLineString>
     * 
     * @return instance of MultiCurve
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static MultiSurface wrapMultiPolygon( Element element )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }

        ElementList el = XMLTools.getChildElements( "polygonMember", CommonNamespaces.GMLNS,
                                                    element );
        Surface[] surfaces = new Surface[el.getLength()];
        for ( int i = 0; i < surfaces.length; i++ ) {
            surfaces[i] = wrapPolygon( XMLTools.getFirstChildElement( el.item( i ) ) );
            ( (GeometryImpl) surfaces[i] ).setCoordinateSystem( crs );
        }

        MultiSurface mp = GeometryFactory.createMultiSurface( surfaces );

        return mp;
    }

    /**
     * returns a Envelope created from Box element
     * 
     * @param element
     *            <boundedBy>
     * 
     * @return instance of <tt>Envelope</tt>
     * 
     * @throws XMLParsingException
     * @throws InvalidGMLException
     * @throws UnknownCRSException 
     */
    public static Envelope wrapBox( Element element )
                            throws XMLParsingException, InvalidGMLException, UnknownCRSException {

        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = getCRS( srs );
        }
        Position[] bb = null;
        List nl = XMLTools.getNodes( element, COORD, nsContext );
        if ( nl != null && nl.size() > 0 ) {
            bb = new Position[2];
            bb[0] = createPositionFromCoord( (Element) nl.get( 0 ) );
            bb[1] = createPositionFromCoord( (Element) nl.get( 1 ) );
        } else {
            nl = XMLTools.getNodes( element, COORDINATES, nsContext );
            if ( nl != null && nl.size() > 0 ) {
                bb = createPositionFromCoordinates( (Element) nl.get( 0 ) );
            } else {
                nl = XMLTools.getNodes( element, POS, nsContext );
                if ( nl != null && nl.size() > 0 ) {
                    bb = new Position[2];
                    bb[0] = createPositionFromPos( (Element) nl.get( 0 ) );
                    bb[1] = createPositionFromPos( (Element) nl.get( 1 ) );
                } else {
                    Element lowerCorner = (Element) XMLTools.getRequiredNode( element,
                                                                              "gml:lowerCorner",
                                                                              nsContext );
                    Element upperCorner = (Element) XMLTools.getRequiredNode( element,
                                                                              "gml:upperCorner",
                                                                              nsContext );
                    bb = new Position[2];
                    bb[0] = createPositionFromCorner( lowerCorner );
                    bb[1] = createPositionFromCorner( upperCorner );
                }
            }
        }

        Envelope box = GeometryFactory.createEnvelope( bb[0], bb[1], crs );

        return box;
    }

    private static Position createPositionFromCorner( Element corner )
                            throws InvalidGMLException {

        String tmp = XMLTools.getAttrValue( corner, "dimension" );
        int dim = 0;
        if ( tmp != null ) {
            dim = Integer.parseInt( tmp );
        }
        tmp = XMLTools.getStringValue( corner );
        double[] vals = StringTools.toArrayDouble( tmp, ", " );
        if ( dim != 0 ) {
            if ( vals.length != dim ) {
                throw new InvalidGMLException( "dimension must be equal to the number of "
                                               + "coordinate values defined in pos element." );
            }
        } else {
            dim = vals.length;
        }

        Position pos = null;
        if ( dim == 3 ) {
            pos = GeometryFactory.createPosition( vals[0], vals[1], vals[2] );
        } else {
            pos = GeometryFactory.createPosition( vals[0], vals[1] );
        }

        return pos;

    }

    /**
     * returns a Surface created from Box element. This method is useful because an Envelope that
     * normaly should be created from a Box isn't a geometry in context of ISO 19107
     * 
     * @param element
     *            <boundedBy>
     * 
     * @return instance of <tt>Surface</tt>
     * 
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private static Surface wrapBoxAsSurface( Element element )
                            throws XMLParsingException, GeometryException, InvalidGMLException,
                            UnknownCRSException {
        Envelope env = wrapBox( element );
        return GeometryFactory.createSurface( env, env.getCoordinateSystem() );
    }

    /**
     * returns an instance of Position created from the passed coord
     * 
     * @param element
     *            <coord>
     * 
     * @return instance of <tt>Position</tt>
     * 
     * @throws XMLParsingException
     */
    private static Position createPositionFromCoord( Element element )
                            throws XMLParsingException {

        Position pos = null;
        Element elem = XMLTools.getRequiredChildElement( "X", CommonNamespaces.GMLNS, element );
        double x = Double.parseDouble( XMLTools.getStringValue( elem ) );
        elem = XMLTools.getRequiredChildElement( "Y", CommonNamespaces.GMLNS, element );
        double y = Double.parseDouble( XMLTools.getStringValue( elem ) );
        elem = XMLTools.getChildElement( "Z", CommonNamespaces.GMLNS, element );

        if ( elem != null ) {
            double z = Double.parseDouble( XMLTools.getStringValue( elem ) );
            pos = GeometryFactory.createPosition( new double[] { x, y, z } );
        } else {
            pos = GeometryFactory.createPosition( new double[] { x, y } );
        }

        return pos;
    }

    /**
     * returns an array of Positions created from the passed coordinates
     * 
     * @param element
     *            <coordinates>
     * 
     * @return instance of <tt>Position[]</tt>
     * 
     * @throws XMLParsingException
     */
    private static Position[] createPositionFromCoordinates( Element element ) {

        Position[] points = null;
        String ts = XMLTools.getAttrValue( element, "ts" );
        if ( ts == null ) {
            ts = " ";
        }
        String ds = XMLTools.getAttrValue( element, "decimal" );
        if ( ds == null ) {
            ds = ".";
        }
        String cs = XMLTools.getAttrValue( element, "cs" );
        if ( cs == null ) {
            cs = ",";
        }
        String value = XMLTools.getStringValue( element );

        // first tokenizer, tokens the tuples
        StringTokenizer tuple = new StringTokenizer( value, ts );
        points = new Position[tuple.countTokens()];
        int i = 0;
        while ( tuple.hasMoreTokens() ) {
            String s = tuple.nextToken();
            // second tokenizer, tokens the coordinates
            StringTokenizer coort = new StringTokenizer( s, cs );
            double[] p = new double[coort.countTokens()];

            for ( int k = 0; k < p.length; k++ ) {
                p[k] = Double.parseDouble( coort.nextToken() );
            }

            points[i++] = GeometryFactory.createPosition( p );
        }

        return points;
    }

    /**
     * creates a <tt>Point</tt> from the passed <pos> element containing a GML pos.
     * 
     * @param element
     * @return created <tt>Point</tt>
     * @throws XMLParsingException
     * @throws InvalidGMLException
     */
    private static Position createPositionFromPos( Element element )
                            throws InvalidGMLException {

        String tmp = XMLTools.getAttrValue( element, "dimension" );
        int dim = 0;
        if ( tmp != null ) {
            dim = Integer.parseInt( tmp );
        }
        tmp = XMLTools.getStringValue( element );
        double[] vals = StringTools.toArrayDouble( tmp, "\t\n\r\f ," );
        if ( dim != 0 ) {
            if ( vals.length != dim ) {
                throw new InvalidGMLException( "dimension must be equal to the number of "
                                               + "coordinate values defined in pos element." );
            }
        } else {
            dim = vals.length;
        }

        Position pos = null;
        if ( dim == 3 ) {
            pos = GeometryFactory.createPosition( vals[0], vals[1], vals[2] );
        } else {
            pos = GeometryFactory.createPosition( vals[0], vals[1] );
        }

        return pos;
    }

    /**
     * 
     * @param element
     * @return Position
     * @throws InvalidGMLException
     */
    private static Position[] createPositionFromPosList( Element element, String srsName )
                            throws InvalidGMLException {

        if ( srsName == null ) {
            srsName = XMLTools.getAttrValue( element, "srsName" );
        }

        String srsDimension = XMLTools.getAttrValue( element, "srsDimension" );
        int dim = 0;
        if ( srsDimension != null ) {
            dim = Integer.parseInt( srsDimension );
        }
        if ( dim == 0 ) {
            // TODO
            // determine dimension from CRS
            // default dimension set.
            dim = 2;

        }

        String axisLabels = XMLTools.getAttrValue( element, "gml:axisAbbrev" );

        String uomLabels = XMLTools.getAttrValue( element, "uomLabels" );

        if ( srsName == null ) {
            if ( srsDimension != null ) {
                throw new InvalidGMLException( "Attribute srsDimension cannot be defined "
                                               + "unless attribute srsName has been defined." );
            }
            if ( axisLabels != null ) {
                throw new InvalidGMLException( "Attribute axisLabels cannot be defined "
                                               + "unless attribute srsName has been defined." );
            }

        }
        if ( axisLabels == null ) {
            if ( uomLabels != null ) {
                throw new InvalidGMLException( "Attribute uomLabels cannot be defined "
                                               + "unless attribute axisLabels has been defined." );
            }
        }
        String tmp = XMLTools.getStringValue( element );
        double[] values = StringTools.toArrayDouble( tmp, "\t\n\r\f ," );
        int size = values.length / dim;
        if ( values.length < 4 ) {
            throw new InvalidGMLException( "A point list must have minimum 2 coordinate tuples. "
                                           + "Here only '" + size + "' are defined." );
        }
        double positions[][] = new double[size][dim];
        int a = 0, b = 0;
        for ( int i = 0; i < values.length; i++ ) {
            if ( b == dim ) {
                a++;
                b = 0;
            }
            positions[a][b] = values[i];
            b++;
        }

        Position[] position = new Position[positions.length];
        for ( int i = 0; i < positions.length; i++ ) {
            double[] vals = positions[i];
            if ( dim == 3 ) {
                position[i] = GeometryFactory.createPosition( vals[0], vals[1], vals[2] );
            } else {
                position[i] = GeometryFactory.createPosition( vals[0], vals[1] );
            }
        }

        return position;

    }

    /**
     * creates an array of <tt>Position</tt>s from the <coordinates> or <pos> Elements located as
     * children under the passed parent element.
     * <p>
     * example:<br>
     * 
     * <pre>
     *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           &lt;gml:Box&gt;
     *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 &lt;gml:coordinates cs=&quot;,&quot; decimal=&quot;.&quot; ts=&quot; &quot;&gt;0,0 4000,4000&lt;/gml:coordinates&gt;
     *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           &lt;/gml:Box&gt;
     * </pre>
     * 
     * </p>
     * 
     * @param parent
     * @param srsName
     * @return
     * @throws XMLParsingException
     * @throws InvalidGMLException
     */
    private static Position[] createPositions( Element parent, String srsName )
                            throws XMLParsingException, InvalidGMLException {

        List nl = XMLTools.getNodes( parent, COORDINATES, nsContext );
        Position[] pos = null;
        if ( nl != null && nl.size() > 0 ) {
            pos = createPositionFromCoordinates( (Element) nl.get( 0 ) );
        } else {
            nl = XMLTools.getNodes( parent, POS, nsContext );
            if ( nl != null && nl.size() > 0 ) {
                pos = new Position[nl.size()];
                for ( int i = 0; i < pos.length; i++ ) {
                    pos[i] = createPositionFromPos( (Element) nl.get( i ) );
                }
            } else {
                Element posList = (Element) XMLTools.getRequiredNode( parent, POSLIST, nsContext );
                if ( posList != null ) {
                    pos = createPositionFromPosList( posList, srsName );
                }
            }
        }

        return pos;
    }

    /**
     * creates a GML expression of a point geometry
     * 
     * @param o
     *            point geometry
     * 
     * @return
     */
    private static StringBuffer exportPoint( Point o ) {

        StringBuffer sb = new StringBuffer( 200 );
        String crs = null;
        if ( o.getCoordinateSystem() != null ) {
            crs = o.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        if ( crs != null ) {
            sb.append( "<gml:Point srsName=\"" ).append( crs ).append( "\">" );
        } else {
            sb.append( "<gml:Point>" );
        }
        sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );
        sb.append( o.getX() ).append( ',' ).append( o.getY() );
        if ( o.getCoordinateDimension() == 3 ) {
            sb.append( "," + o.getZ() );
        }
        sb.append( "</gml:coordinates>" );
        sb.append( "</gml:Point>" );

        return sb;
    }

    /**
     * creates a GML expression of a curve geometry
     * 
     * @param o
     *            curve geometry
     * 
     * @return
     * 
     * @throws GeometryException
     */
    private static StringBuffer exportCurve( Curve o )
                            throws GeometryException {

        Position[] p = o.getAsLineString().getPositions();

        StringBuffer sb = new StringBuffer( p.length * 40 );

        String crs = null;
        if ( o.getCoordinateSystem() != null ) {
            crs = o.getCoordinateSystem().getName().replace( ' ', ':' );
        }

        if ( crs != null ) {
            sb.append( "<gml:LineString srsName=\"" + crs + "\">" );
        } else {
            sb.append( "<gml:LineString>" );
        }

        sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

        for ( int i = 0; i < ( p.length - 1 ); i++ ) {
            sb.append( p[i].getX() ).append( ',' ).append( p[i].getY() );
            if ( o.getCoordinateDimension() == 3 ) {
                sb.append( ',' ).append( p[i].getZ() ).append( ' ' );
            } else {
                sb.append( ' ' );
            }
        }

        sb.append( p[p.length - 1].getX() ).append( ',' ).append( p[p.length - 1].getY() );
        if ( o.getCoordinateDimension() == 3 ) {
            sb.append( ',' ).append( p[p.length - 1].getZ() );
        }
        sb.append( "</gml:coordinates></gml:LineString>" );

        return sb;
    }

    /**
     * @param sur
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static StringBuffer exportSurface( Surface sur )
                            throws GeometryException {

        StringBuffer sb = new StringBuffer( 5000 );

        String crs = null;
        if ( sur.getCoordinateSystem() != null ) {
            crs = sur.getCoordinateSystem().getName().replace( ' ', ':' );
        }

        if ( crs != null ) {
            sb.append( "<gml:Polygon srsName=\"" + crs + "\">" );
        } else {
            sb.append( "<gml:Polygon>" );
        }

        SurfacePatch patch = sur.getSurfacePatchAt( 0 );

        // exterior ring
        sb.append( "<gml:outerBoundaryIs><gml:LinearRing>" );
        sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

        Position[] p = patch.getExteriorRing();

        for ( int i = 0; i < ( p.length - 1 ); i++ ) {
            sb.append( p[i].getX() ).append( ',' ).append( p[i].getY() );
            if ( sur.getCoordinateDimension() == 3 ) {
                sb.append( ',' ).append( p[i].getZ() ).append( ' ' );
            } else {
                sb.append( ' ' );
            }
        }

        sb.append( p[p.length - 1].getX() ).append( ',' ).append( p[p.length - 1].getY() );
        if ( sur.getCoordinateDimension() == 3 ) {
            sb.append( ',' ).append( p[p.length - 1].getZ() );
        }
        sb.append( "</gml:coordinates>" );
        sb.append( "</gml:LinearRing></gml:outerBoundaryIs>" );

        // interior rings
        Position[][] ip = patch.getInteriorRings();

        if ( ip != null ) {
            for ( int j = 0; j < ip.length; j++ ) {
                sb.append( "<gml:innerBoundaryIs><gml:LinearRing>" );
                sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

                for ( int i = 0; i < ( ip[j].length - 1 ); i++ ) {
                    sb.append( ip[j][i].getX() ).append( ',' ).append( ip[j][i].getY() );
                    if ( sur.getCoordinateDimension() == 3 ) {
                        sb.append( ',' ).append( ip[j][i].getZ() ).append( ' ' );
                    } else {
                        sb.append( ' ' );
                    }
                }

                sb.append( ip[j][ip[j].length - 1].getX() ).append( ',' );
                sb.append( ip[j][ip[j].length - 1].getY() );
                if ( sur.getCoordinateDimension() == 3 ) {
                    sb.append( ',' ).append( ip[j][ip[j].length - 1].getZ() );
                }
                sb.append( "</gml:coordinates>" );
                sb.append( "</gml:LinearRing></gml:innerBoundaryIs>" );
            }
        }

        sb.append( "</gml:Polygon>" );

        return sb;
    }

    /**
     * @param mp
     * @return
     * @throws RemoteException
     */
    private static StringBuffer exportMultiPoint( MultiPoint mp ) {

        StringBuffer sb = new StringBuffer( mp.getSize() * 35 );
        String srsName = "";

        String crs = null;
        if ( mp.getCoordinateSystem() != null ) {
            crs = mp.getCoordinateSystem().getName().replace( ' ', ':' );
        }

        if ( crs != null ) {
            srsName = " srsName=\"" + crs + "\"";
        }

        sb.append( "<gml:MultiPoint" ).append( srsName ).append( ">" );

        for ( int i = 0; i < mp.getSize(); i++ ) {
            sb.append( "<gml:pointMember>" );
            sb.append( "<gml:Point" ).append( srsName ).append( ">" );

            sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );
            sb.append( mp.getPointAt( i ).getX() ).append( ',' ).append( mp.getPointAt( i ).getY() );
            if ( mp.getPointAt( i ).getCoordinateDimension() == 3 ) {
                sb.append( ',' ).append( mp.getPointAt( i ).getZ() );
            }
            sb.append( "</gml:coordinates>" );
            sb.append( "</gml:Point>" );
            sb.append( "</gml:pointMember>" );
        }

        sb.append( "</gml:MultiPoint>" );

        return sb;
    }

    /**
     * @param mp
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static StringBuffer exportMultiCurve( MultiCurve mp )
                            throws GeometryException {

        StringBuffer sb = new StringBuffer( 50000 );
        String srsName = "";
        String crs = null;
        if ( mp.getCoordinateSystem() != null ) {
            crs = mp.getCoordinateSystem().getName().replace( ' ', ':' );
        }

        if ( crs != null ) {
            srsName = " srsName=\"" + crs + "\"";
        }

        sb.append( "<gml:MultiLineString" ).append( srsName ).append( ">" );

        for ( int j = 0; j < mp.getSize(); j++ ) {
            sb.append( "<gml:lineStringMember>" );
            sb.append( "<gml:LineString" ).append( srsName ).append( ">" );

            sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

            Position[] p = mp.getCurveAt( j ).getAsLineString().getPositions();

            for ( int i = 0; i < ( p.length - 1 ); i++ ) {
                sb.append( p[i].getX() ).append( ',' ).append( p[i].getY() );
                if ( mp.getCoordinateDimension() == 3 ) {
                    sb.append( ',' ).append( p[i].getZ() ).append( ' ' );
                } else {
                    sb.append( ' ' );
                }
            }

            sb.append( p[p.length - 1].getX() ).append( ',' ).append( p[p.length - 1].getY() );
            if ( mp.getCoordinateDimension() == 3 ) {
                sb.append( ',' ).append( p[p.length - 1].getZ() );
            }
            sb.append( "</gml:coordinates>" );
            sb.append( "</gml:LineString>" );
            sb.append( "</gml:lineStringMember>" );
        }

        sb.append( "</gml:MultiLineString>" );

        return sb;
    }

    /**
     * @param mp
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static StringBuffer exportMultiSurface( MultiSurface mp )
                            throws GeometryException {

        StringBuffer sb = new StringBuffer( 50000 );
        String srsName = "";
        String crs = null;
        if ( mp.getCoordinateSystem() != null ) {
            crs = mp.getCoordinateSystem().getName().replace( ' ', ':' );
        }

        if ( crs != null ) {
            srsName = " srsName=\"" + crs + "\"";
        }

        sb.append( "<gml:MultiPolygon" ).append( srsName ).append( ">" );

        for ( int k = 0; k < mp.getSize(); k++ ) {
            sb.append( "<gml:polygonMember>" );
            sb.append( "<gml:Polygon" ).append( srsName ).append( ">" );

            Surface sur = mp.getSurfaceAt( k );
            SurfacePatch patch = sur.getSurfacePatchAt( 0 );

            // exterior ring
            sb.append( "<gml:outerBoundaryIs><gml:LinearRing>" );
            sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

            Position[] p = patch.getExteriorRing();

            for ( int i = 0; i < ( p.length - 1 ); i++ ) {
                sb.append( p[i].getX() ).append( ',' ).append( p[i].getY() );
                if ( mp.getCoordinateDimension() == 3 ) {
                    sb.append( ',' ).append( p[i].getZ() ).append( ' ' );
                } else {
                    sb.append( ' ' );
                }
            }

            sb.append( p[p.length - 1].getX() ).append( ',' ).append( p[p.length - 1].getY() );
            if ( mp.getCoordinateDimension() == 3 ) {
                sb.append( ',' ).append( p[p.length - 1].getZ() );
            }
            sb.append( "</gml:coordinates>" );
            sb.append( "</gml:LinearRing></gml:outerBoundaryIs>" );

            // interior rings
            Position[][] ip = patch.getInteriorRings();

            if ( ip != null ) {
                for ( int j = 0; j < ip.length; j++ ) {
                    sb.append( "<gml:innerBoundaryIs><gml:LinearRing>" );
                    sb.append( "<gml:coordinates cs=\",\" decimal=\".\" ts=\" \">" );

                    for ( int i = 0; i < ( ip[j].length - 1 ); i++ ) {
                        sb.append( ip[j][i].getX() ).append( ',' ).append( ip[j][i].getY() );
                        if ( mp.getCoordinateDimension() == 3 ) {
                            sb.append( ',' ).append( ip[j][i].getZ() ).append( ' ' );
                        } else {
                            sb.append( ' ' );
                        }
                    }

                    sb.append( ip[j][ip[j].length - 1].getX() ).append( ',' );
                    sb.append( ip[j][ip[j].length - 1].getY() );
                    if ( mp.getCoordinateDimension() == 3 ) {
                        sb.append( ',' ).append( ip[j][ip[j].length - 1].getZ() );
                    }
                    sb.append( "</gml:coordinates>" );
                    sb.append( "</gml:LinearRing></gml:innerBoundaryIs>" );
                }
            }

            sb.append( "</gml:Polygon>" );
            sb.append( "</gml:polygonMember>" );
        }

        sb.append( "</gml:MultiPolygon>" );

        return sb;
    }

    /**
     * creates a GML expression of a point geometry
     * 
     * @param point
     *            point geometry
     * 
     * @return
     */
    private static void exportPoint( Point point, PrintWriter pw ) {

        String crs = null;
        if ( point.getCoordinateSystem() != null ) {
            crs = point.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:Point srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:Point>";
        }
        pw.println( srs );

        int dim = point.getCoordinateDimension();
        if ( dim != 0 ) {
            String dimension = "<gml:pos dimension=\"" + dim + "\">";
            pw.print( dimension );
        } else {
            pw.print( "<gml:pos>" );
        }

        String coordinates = point.getX() + " " + point.getY();
        if ( point.getCoordinateDimension() == 3 ) {
            coordinates = coordinates + " " + point.getZ();
        }
        pw.print( coordinates );
        pw.println( "</gml:pos>" );
        pw.print( "</gml:Point>" );

    }

    /**
     * creates a GML expression of a curve geometry
     * 
     * @param o
     *            curve geometry
     * 
     * @return
     * 
     * @throws GeometryException
     */
    private static void exportCurve( Curve o, PrintWriter pw )
                            throws GeometryException {

        String crs = null;
        if ( o.getCoordinateSystem() != null ) {
            crs = o.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:Curve srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:Curve>";
        }
        pw.println( srs );
        pw.println( "<gml:segments>" );

        int curveSegments = o.getNumberOfCurveSegments();
        for ( int i = 0; i < curveSegments; i++ ) {
            pw.print( "<gml:LineStringSegment>" );
            CurveSegment segment = o.getCurveSegmentAt( i );
            Position[] p = segment.getAsLineString().getPositions();
            pw.print( "<gml:posList>" );
            for ( int j = 0; j < ( p.length - 1 ); j++ ) {
                pw.print( p[j].getX() + " " + p[j].getY() );
                if ( o.getCoordinateDimension() == 3 ) {
                    pw.print( ' ' );
                    pw.print( p[j].getZ() );
                    pw.print( ' ' );
                } else {
                    pw.print( ' ' );
                }
            }
            pw.print( p[p.length - 1].getX() + " " + p[p.length - 1].getY() );
            if ( o.getCoordinateDimension() == 3 ) {
                pw.print( " " + p[p.length - 1].getZ() );
            }
            pw.println( "</gml:posList>" );
            pw.println( "</gml:LineStringSegment>" );
        }
        pw.println( "</gml:segments>" );
        pw.print( "</gml:Curve>" );

    }

    /**
     * @param sur
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static void exportSurface( Surface surface, PrintWriter pw )
                            throws GeometryException {

        String crs = null;
        if ( surface.getCoordinateSystem() != null ) {
            crs = surface.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:Surface srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:Surface>";
        }
        pw.println( srs );
        int patches = surface.getNumberOfSurfacePatches();
        pw.println( "<gml:patches>" );
        for ( int i = 0; i < patches; i++ ) {
            pw.println( "<gml:Polygon>" );
            SurfacePatch patch = surface.getSurfacePatchAt( i );

            printExteriorRing( surface, pw, patch );
            printInteriorRing( surface, pw, patch );
            pw.println( "</gml:Polygon>" );
        }
        pw.println( "</gml:patches>" );
        pw.print( "</gml:Surface>" );

    }

    /**
     * @param surface
     * @param pw
     * @param patch
     */
    private static void printInteriorRing( Surface surface, PrintWriter pw, SurfacePatch patch ) {
        // interior rings
        Position[][] ip = patch.getInteriorRings();
        if ( ip != null ) {
            for ( int j = 0; j < ip.length; j++ ) {
                pw.println( "<gml:interior>" );
                pw.println( "<gml:LinearRing>" );
                pw.print( "<gml:posList>" );

                for ( int k = 0; k < ( ip[k].length - 1 ); k++ ) {
                    pw.print( ip[j][k].getX() + " " + ip[j][k].getY() );
                    if ( surface.getCoordinateDimension() == 3 ) {
                        pw.print( " " + ip[j][k].getZ() + " " );
                    } else {
                        pw.print( " " );
                    }
                }
                pw.print( ip[j][ip[j].length - 1].getX() + " " + ip[j][ip[j].length - 1].getY() );
                if ( surface.getCoordinateDimension() == 3 ) {
                    pw.print( " " + ip[j][ip[j].length - 1].getZ() );
                }
                pw.println( "</gml:posList>" );
                pw.println( "</gml:LinearRing>" );
                pw.println( "</gml:interior>" );
            }
        }
    }

    /**
     * @param surface
     * @param pw
     * @param patch
     */
    private static void printExteriorRing( Surface surface, PrintWriter pw, SurfacePatch patch ) {
        // exterior ring
        pw.println( "<gml:exterior>" );
        pw.println( "<gml:LinearRing>" );
        pw.println( "<gml:posList>" );
        Position[] p = patch.getExteriorRing();
        for ( int j = 0; j < ( p.length - 1 ); j++ ) {
            pw.print( p[j].getX() + " " + p[j].getY() );
            if ( surface.getCoordinateDimension() == 3 ) {
                pw.print( " " + p[j].getZ() + " " );
            } else {
                pw.print( ' ' );
            }
        }
        pw.print( p[p.length - 1].getX() + " " + p[p.length - 1].getY() );
        if ( surface.getCoordinateDimension() == 3 ) {
            pw.print( " " + p[p.length - 1].getZ() );
        }
        pw.println( "</gml:posList>" );
        pw.println( "</gml:LinearRing>" );
        pw.println( "</gml:exterior>" );
    }

    /**
     * @param mp
     * @return
     * @throws RemoteException
     */
    private static void exportMultiPoint( MultiPoint mp, PrintWriter pw ) {

        String crs = null;
        if ( mp.getCoordinateSystem() != null ) {
            crs = mp.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:MultiPoint srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:MultiPoint>";
        }
        pw.println( srs );
        pw.println( "<gml:pointMembers>" );
        for ( int i = 0; i < mp.getSize(); i++ ) {

            pw.println( "<gml:Point>" );
            pw.print( "<gml:pos>" );
            pw.print( mp.getPointAt( i ).getX() + " " + mp.getPointAt( i ).getY() );
            if ( mp.getPointAt( i ).getCoordinateDimension() == 3 ) {
                pw.print( " " + mp.getPointAt( i ).getZ() );
            }
            pw.println( "</gml:pos>" );
            pw.println( "</gml:Point>" );
        }
        pw.println( "</gml:pointMembers>" );
        pw.print( "</gml:MultiPoint>" );

    }

    /**
     * @param multiCurve
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static void exportMultiCurve( MultiCurve multiCurve, PrintWriter pw )
                            throws GeometryException {

        String crs = null;
        if ( multiCurve.getCoordinateSystem() != null ) {
            crs = multiCurve.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:MultiCurve srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:MultiCurve>";
        }
        pw.println( srs );

        Curve[] curves = multiCurve.getAllCurves();
        pw.println( "<gml:curveMembers>" );
        for ( int i = 0; i < curves.length; i++ ) {
            Curve curve = curves[i];
            pw.println( "<gml:Curve>" );
            pw.println( "<gml:segments>" );
            pw.println( "<gml:LineStringSegment>" );
            int numberCurveSegments = curve.getNumberOfCurveSegments();
            for ( int j = 0; j < numberCurveSegments; j++ ) {
                CurveSegment curveSegment = curve.getCurveSegmentAt( j );
                Position[] p = curveSegment.getAsLineString().getPositions();
                pw.print( "<gml:posList>" );
                for ( int k = 0; k < ( p.length - 1 ); k++ ) {
                    pw.print( p[k].getX() + " " + p[k].getY() );
                    if ( curve.getCoordinateDimension() == 3 ) {
                        pw.print( " " + p[k].getZ() + " " );
                    } else {
                        pw.print( " " );
                    }
                }
                pw.print( p[p.length - 1].getX() + " " + p[p.length - 1].getY() );
                if ( curve.getCoordinateDimension() == 3 ) {
                    pw.print( " " + p[p.length - 1].getZ() );
                }
                pw.println( "</gml:posList>" );
            }
            pw.println( "</gml:LineStringSegment>" );
            pw.println( "</gml:segments>" );
            pw.println( "</gml:Curve>" );
        }
        pw.println( "</gml:curveMembers>" );
        pw.print( "</gml:MultiCurve>" );

    }

    /**
     * @param multiSurface
     * @return
     * @throws RemoteException
     * @throws GeometryException
     */
    private static void exportMultiSurface( MultiSurface multiSurface, PrintWriter pw )
                            throws GeometryException {

        String crs = null;
        if ( multiSurface.getCoordinateSystem() != null ) {
            crs = multiSurface.getCoordinateSystem().getName().replace( ' ', ':' );
        }
        String srs = null;
        if ( crs != null ) {
            srs = "<gml:MultiSurface srsName=\"" + crs + "\">";
        } else {
            srs = "<gml:MultiSurface>";
        }
        pw.println( srs );

        Surface[] surfaces = multiSurface.getAllSurfaces();
        pw.println( "<gml:surfaceMembers>" );
        for ( int i = 0; i < surfaces.length; i++ ) {
            Surface surface = surfaces[i];
            pw.println( "<gml:Surface>" );
            pw.println( "<gml:patches>" );
            pw.println( "<gml:Polygon>" );
            int numberSurfaces = surface.getNumberOfSurfacePatches();
            for ( int j = 0; j < numberSurfaces; j++ ) {
                SurfacePatch surfacePatch = surface.getSurfacePatchAt( j );
                printExteriorRing( surface, pw, surfacePatch );
                printInteriorRing( surface, pw, surfacePatch );
            }
            pw.println( "</gml:Polygon>" );
            pw.println( "</gml:patches>" );
            pw.println( "</gml:Surface>" );
        }
        pw.println( "</gml:surfaceMembers>" );
        pw.print( "</gml:MultiSurface>" );

    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GMLGeometryAdapter.java,v $
 * Revision 1.40  2006/11/27 09:07:51  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.39  2006/11/13 21:24:42  poth
 * bug fix - wraping MultiSurfaces
 *
 * Revision 1.38  2006/09/27 16:47:50  poth
 * bug fix parsing poslist
 * Changes to this class. What the people have been up to:
 * Revision 1.37  2006/08/08 09:14:54  poth
 * wrapCompositeSurface( Element ) marked as unsupported operation / never read variable removed
 * Changes to this class. What the people have been up to:
 * Revision 1.36  2006/05/15 10:09:02  mschneider
 * Renamed Position.getDimension() to Position.getCoordinateDimension() for consistency.
 * Changes to this class. What the people have been up to:
 * Revision 1.35  2006/05/03 17:26:26  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.34  2006/05/01 20:15:25  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.33  2006/04/26 13:34:09  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.32  2006/04/06 20:25:21  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.31  2006/03/30 21:20:23  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.30  2006/03/28 13:40:55  mschneider
 * Changed delimiters in Pos / PosList elements. Now all whitespaces can act as delimiters.
 * Changes to this class. What the people have been up to:
 * Revision 1.29  2006/03/07 21:40:20  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.28  2006/01/25 10:42:02  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.27  2006/01/06 16:06:34  deshmukh
 * New Service WMPS handling PrintMap requests
 * Changes
 * to this class. What the people have been up to: Revision 1.26 2005/12/18 19:06:30 poth Changes to
 * this class. What the people have been up to: no message Changes to this class. What the people
 * have been up to: Revision 1.25 2005/12/05
 * 09:25:17 deshmukh *** empty log message
 * *** Changes to this class. What the
 * people have been up to: Revision 1.24 2005/12/01 14:18:37 deshmukh Changes to this class. What
 * the people have been up to: TODO fixed Changes to this class. What the people have been up to:
 * Revision 1.23 2005/12/01 13:33:32
 * deshmukh Modified Polygon wrapping
 * functionality to support GML3.1 Changes
 * to this class. What the people have been up to: Revision 1.21 2005/12/01 10:34:59 deshmukh
 * *** empty log message *** Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.20 2005/12/01 09:09:55 deshmukh Changes to this class. What the people have been up
 * to: *** empty log message *** Changes to
 * this class. What the people have been up to: Revision 1.19 2005/11/30 08:20:38 deshmukh Changes
 * to this class. What the people have been up to: *** empty log message *** Changes to this class.
 * What the people have been up to: Revision
 * 1.18 2005/11/29 17:17:26 deshmukh ***
 * empty log message *** Changes to this
 * class. What the people have been up to: Revision 1.17 2005/11/25 14:14:36 deshmukh Changes to
 * this class. What the people have been up to: *** empty log message *** Changes to this class.
 * What the people have been up to: Revision
 * 1.16 2005/11/25 07:48:00 deshmukh Changes to this class. What the people have been up to:
 * wrapCurveAsCurve, wrapSurfaceAsSurface method implementation added Changes to this class. What
 * the people have been up to: Revision 1.15
 * 2005/11/17 08:18:35 deshmukh Renamed
 * nsNode to nsContext Changes to this
 * class. What the people have been up to: Revision 1.14 2005/11/16 13:45:01 mschneider Changes to
 * this class. What the people have been up to: Merge of wfs development branch. Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.13.2.2 2005/11/07 16:22:23 mschneider Changes to this class. What the people have been
 * up to: Switch from NodeList to List. Changes to this class. What the people have been up to:
 * Revision 1.13.2.1 2005/11/07 15:38:04
 * mschneider Refactoring: use
 * NamespaceContext instead of Node for namespace bindings. Changes to this class. What the people
 * have been up to: Revision 1.13 2005/10/20 19:03:10 poth no message
 * 
 * Revision 1.12 2005/09/27 19:53:18 poth no message
 * 
 * Revision 1.11 2005/08/05 09:42:20 poth no message
 * 
 * Revision 1.10 2005/04/06 13:44:06 poth no message
 * 
 * Revision 1.9 2005/03/09 11:55:46 mschneider ** empty log message ***
 * 
 * Revision 1.8 2005/03/01 14:39:08 mschneider ** empty log message ***
 * 
 * Revision 1.7 2005/02/14 11:31:48 ncho bug fixes by AP [SN]
 * 
 * Revision 1.6 2005/01/30 22:22:02 poth no message
 * 
 * Revision 1.5 2005/01/19 17:22:26 poth no message
 * 
 * Revision 1.4 2005/01/18 22:08:54 poth no message
 * 
 * Revision 1.3 2005/01/16 22:30:33 poth no message
 * 
 * Revision 1.2 2005/01/06 17:51:46 poth no message
 * 
 * Revision 1.6 2004/08/24 07:31:33 ap no message
 * 
 * Revision 1.5 2004/07/16 10:24:00 ap no message
 * 
 * Revision 1.4 2004/07/14 06:52:48 ap no message
 * 
 * Revision 1.3 2004/07/13 06:12:53 ap no message
 * 
 * Revision 1.2 2004/07/12 14:25:35 ap no message
 * 
 * Revision 1.1 2004/07/12 14:21:25 ap no message
 * 
 * 
 **************************************************************************************************/
