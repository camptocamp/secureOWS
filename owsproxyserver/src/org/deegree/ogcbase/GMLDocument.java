// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/GMLDocument.java,v 1.17 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.ogcbase;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import org.deegree.datatypes.time.TimeIndeterminateValue;
import org.deegree.datatypes.time.TimePosition;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.coverage.grid.Grid;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @version $Revision: 1.17 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.17 $, $Date: 2006/11/27 09:07:53 $
 * 
 * @since 1.1
 */
public class GMLDocument extends XMLFragment {
   
    private static URI GMLNS = CommonNamespaces.GMLNS;

    /**
     * creates a <tt>Point</tt> from the passed <pos> element containing a GML pos.
     * 
     * @param element
     * @return created <tt>Point</tt>
     * @throws XMLParsingException
     * @throws InvalidGMLException
     */
    public static Point parsePos( Element element ) throws InvalidGMLException {
        String tmp = XMLTools.getAttrValue( element, "dimension" );
        int dim = 0;
        if ( tmp != null ) {
            dim = Integer.parseInt( tmp );
        }
        tmp = XMLTools.getStringValue( element );
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

        return GeometryFactory.createPoint( pos, null );
    }

    /**
     * creates a <tt>Envelope</tt> from the passed element. Because deegree geometry
     * implementation doesn't use CRS for envelopes the srsName attribute of the passed element is
     * ignored.
     * 
     * @param element
     * @return created <tt>Envelope</tt>
     * @throws XMLParsingException
     * @throws InvalidGMLException
     * @throws UnknownCRSException 
     */
    public static Envelope parseEnvelope( Element element )
                            throws InvalidGMLException, UnknownCRSException {        
        
        String srs = XMLTools.getAttrValue( element, "srsName" );
        CoordinateSystem crs = null;
        if ( srs != null ) {
            crs = CRSFactory.create( srs );
        }

        ElementList el = XMLTools.getChildElements( "pos", GMLNS, element );
        if ( el == null || el.getLength() != 2 ) {
            throw new InvalidGMLException( "A lonLatEnvelope must contain "
                                           + "two gml:pos elements" );
        }
        Point min = parsePos( el.item( 0 ) );
        Point max = parsePos( el.item( 1 ) );

        return GeometryFactory.createEnvelope( min.getPosition(), max.getPosition(), crs );
    }

    /**
     * creates a <tt>TimePosition</tt> object from the passed element.
     * 
     * @param element
     * @return created <tt>TimePosition</tt>
     * @throws XMLParsingException
     * @throws InvalidGMLException
     */
    public static TimePosition parseTimePosition( Element element )
                            throws XMLParsingException, InvalidGMLException {
        try {
            String calendarEraName = XMLTools.getRequiredAttrValue( "calendarEraName", null,
                                                                    element );
            URI frame = new URI( XMLTools.getRequiredAttrValue( "frame", null, element ) );
            String indeterminatePosition = XMLTools.getRequiredAttrValue( "indeterminatePosition",
                                                                          null, element );
            TimeIndeterminateValue tiv = new TimeIndeterminateValue( indeterminatePosition );
            String tmp = XMLTools.getStringValue( element );
            Calendar cal = null;

            if ( !frame.toString().equals( "#ISO-8601" ) ) {
                throw new InvalidGMLException( "just #ISO-8601 is supported as "
                                               + "frame for TimePosition." );
            }

            cal = TimeTools.createCalendar( tmp );

            return new TimePosition( tiv, calendarEraName, frame, cal );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "couldn't parse timePosition frame\n"
                                           + StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * creates a <tt>Grid</tt> instance from the passed <tt>Element</tt>
     * 
     * @param element
     * @return instance of <tt>Grid</tt>
     * @throws XMLParsingException
     * @throws InvalidGMLException
     */
    public static Grid parseGrid( Element element )
                            throws InvalidGMLException {
        Grid grid = null;
        try {
            String path = "gml:limits/gml:GridEnvelope/gml:low";
            String lo = XMLTools.getRequiredNodeAsString( element, path, nsContext );
            double[] low = StringTools.toArrayDouble( lo, " ,;" );
            path = "gml:limits/gml:GridEnvelope/gml:high";
            String hi = XMLTools.getRequiredNodeAsString( element, path, nsContext );
            double[] high = StringTools.toArrayDouble( hi, " ,;" );
            Position posLo = GeometryFactory.createPosition( low );
            Position posHi = GeometryFactory.createPosition( high );
            Envelope env = GeometryFactory.createEnvelope( posLo, posHi, null );
            String[] axis = XMLTools.getNodesAsStrings( element, "axisName/text()", nsContext );
            grid = new Grid( env, axis );
        } catch ( Exception e ) {
            throw new InvalidGMLException( e.getMessage() );
        }
        return grid;
    }

}
/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: GMLDocument.java,v $
 * Revision 1.17  2006/11/27 09:07:53  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.16  2006/07/13 14:55:04  poth
 * never thrown exceptions removed from throws clause / footer corrected
 *
 * Revision 1.15  2006/05/01 20:15:26  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.14  2006/04/19 14:58:35  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.13  2006/04/06 20:25:22  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.12  2006/04/04 20:39:40  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.11  2006/03/30 21:20:24  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.10  2005/11/17 08:18:35  deshmukh
 * Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Revision 1.9  2005/11/16 13:45:00  mschneider
 * Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Revision 1.8.2.3  2005/11/07 16:22:23  mschneider
 * Switch from NodeList to List.
 * Changes to
 * this class. What the people have been up to: Revision 1.8.2.2 2005/11/07 15:38:04 mschneider
 * Refactoring: use NamespaceContext instead
 * of Node for namespace bindings. Revision
 * 1.8.2.1 2005/11/07 13:09:26 deshmukh Switched namespace definitions in "CommonNamespaces" to URI.
 * 
 * Revision 1.8 2005/09/27 19:53:18 poth no message
 * 
 * Revision 1.7 2005/04/15 10:04:44 poth no message
 * 
 * Revision 1.6 2005/03/16 16:22:59 mschneider ** empty log message ***
 * 
 * Revision 1.5 2005/03/14 15:13:00 mschneider ** empty log message ***
 * 
 * Revision 1.4 2005/03/09 11:55:46 mschneider ** empty log message ***
 * 
 * Revision 1.3 2005/03/01 14:39:08 mschneider ** empty log message ***
 * 
 * Revision 1.2 2005/02/10 17:17:24 mschneider Corrected usage of XmlNode + XmlDocument.
 * 
 * Revision 1.1.1.1 2005/01/05 10:33:20 poth no message
 * 
 * Revision 1.4 2004/07/12 13:03:21 mschneider More work on the CatalogConfiguration and
 * capabilities framework.
 * 
 * Revision 1.3 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.2 2004/06/28 06:40:04 ap no message
 * 
 * Revision 1.1 2004/05/31 07:37:45 ap no message
 * 
 ********************************************************************************************* */
