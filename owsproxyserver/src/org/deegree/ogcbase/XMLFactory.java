// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/XMLFactory.java,v 1.17 2006/07/13 15:15:46 poth Exp $
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

import org.deegree.datatypes.CodeList;
import org.deegree.datatypes.time.TimeDuration;
import org.deegree.datatypes.time.TimePeriod;
import org.deegree.datatypes.time.TimePosition;
import org.deegree.datatypes.time.TimeSequence;
import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.Values;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Factory for creating <code>DOM</code> representations from java objects used to represent OGC
 * related schema types.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.17 $, $Date: 2006/07/13 15:15:46 $
 * 
 * @since 1.1
 */

public class XMLFactory {

 
    protected static final ILogger LOG = LoggerFactory.getLogger( XMLFactory.class );

    protected static final URI OGCNS = CommonNamespaces.OGCNS;

    protected static final URI GMLNS = CommonNamespaces.GMLNS;

    protected static final URI XLNNS = CommonNamespaces.XLNNS;

    /**
     * Appends the <code>DOM</code> representation of a <code>LonLatEnvelope</code> to the
     * passed <code>Element</code>.
     * 
     * @param root
     * @param lonLatEnvelope
     * @param namespaceURI
     */
    protected static void appendLonLatEnvelope( Element root, LonLatEnvelope lonLatEnvelope,
                                               URI namespaceURI ) {
        Element lonLatEnvelopeElement = XMLTools.appendElement( root, namespaceURI,
            "lonLatEnvelope" );
        lonLatEnvelopeElement.setAttribute( "srsName", "WGS84(DD)" );
        String min = lonLatEnvelope.getMin().getX()
            + " " + lonLatEnvelope.getMin().getY();
        Element elem = XMLTools.appendElement( lonLatEnvelopeElement, GMLNS, "gml:pos", min );
        elem.setAttribute( "dimension", "2" );
        String max = lonLatEnvelope.getMax().getX()
            + " " + lonLatEnvelope.getMax().getY();
        elem = XMLTools.appendElement( lonLatEnvelopeElement, GMLNS, "gml:pos", max );
        elem.setAttribute( "dimension", "2" );
        TimePosition[] tpos = lonLatEnvelope.getTimePositions();
        if ( tpos != null ) {
            for (int i = 0; i < tpos.length; i++) {
                appendTimePosition( lonLatEnvelopeElement, tpos[i] );
            }
        }
    }

    /**
     * Appends an <code>XML/GML Envelope</code> -element to the passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param envelope
     */
    protected static void appendEnvelope( Element xmlNode, Envelope envelope ) {
        Element node = XMLTools.appendElement( xmlNode, GMLNS, "gml:Envelope" );
        CoordinateSystem crs = envelope.getCoordinateSystem();
        if ( crs != null ) {
            try {
                node.setAttribute( "srsName", crs.getName() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Element env = node;

        String min = envelope.getMin().getX() + " " + envelope.getMin().getY();
        node = XMLTools.appendElement( env, GMLNS, "gml:pos", min );
        node.setAttribute( "dimension", "2" );

        String max = envelope.getMax().getX() + " " + envelope.getMax().getY();
        node = XMLTools.appendElement( env, GMLNS, "gml:pos", max );
        node.setAttribute( "dimension", "2" );
    }

    /**
     * Appends the <code>XML/GML</code> representation of a <code>TimePosition</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param tpos
     */
    protected static void appendTimePosition( Element xmlNode, TimePosition tpos ) {
        if ( tpos != null ) {
            String s = TimeTools.getISOFormattedTime( tpos.getTime() );
            Element element = XMLTools.appendElement( xmlNode, GMLNS, "gml:timePosition", s );
            element.setAttribute( "calendarEraName", tpos.getCalendarEraName() );
            element.setAttribute( "frame", tpos.getFrame().toString() );
            element.setAttribute( "indeterminatePosition", tpos.getIndeterminatePosition().value );
        }
    }

    /**
     * Appends a <code>keywords</code> -element for each <code>Keywords</code> object of the
     * passed array to the passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param keywords
     * @param namespaceURI
     */
    protected static void appendKeywords( Element xmlNode, Keywords[] keywords, URI namespaceURI ) {
        if ( keywords != null ) {
            for (int i = 0; i < keywords.length; i++) {
                Element node = XMLTools.appendElement( xmlNode, namespaceURI, "keywords" );
                appendKeywords( node, keywords[i], namespaceURI );
            }
        }
    }

    /**
     * Appends a <code>keywords</code> -element to the passed <code>Element</code> and fills it
     * with the available keywords.
     * 
     * @param xmlNode
     * @param keywords
     * @param namespaceURI
     */
    protected static void appendKeywords( Element xmlNode, Keywords keywords, URI namespaceURI ) {
        if ( keywords != null ) {
            String[] kw = keywords.getKeywords();
            for (int i = 0; i < kw.length; i++) {
                XMLTools.appendElement( xmlNode, namespaceURI, "keyword", kw[i] );
            }
            if ( keywords.getThesaurusName() != null ) {
                XMLTools.appendElement( xmlNode, namespaceURI, "type", keywords.getThesaurusName() );
            }
        }
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>TimeSequence</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param timeSeq
     */
    protected static void appendTemporalDomain( Element xmlNode, TimeSequence timeSeq,
                                               URI namespaceURI ) {
        if ( timeSeq != null ) {
            Element node = XMLTools.appendElement( xmlNode, namespaceURI, "temporalDomain", null );
            TimePeriod[] tPer = timeSeq.getTimePeriod();
            if ( tPer != null ) {
                for (int i = 0; i < tPer.length; i++) {
                    appendTimePeriod( node, tPer[i], namespaceURI );
                }
            }

            TimePosition[] tPos = timeSeq.getTimePosition();
            if ( tPos != null ) {
                for (int i = 0; i < tPos.length; i++) {
                    appendTimePosition( node, tPos[i] );
                }
            }
        }
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>TimePeriod</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param tPeriod
     */
    protected static void appendTimePeriod( Element xmlNode, TimePeriod tPeriod, URI namespaceURI ) {
        Element node = XMLTools.appendElement( xmlNode, namespaceURI, "timePeriod" );
        appendTimePosition( node, tPeriod.getBeginPosition() );
        appendTimePosition( node, tPeriod.getEndPosition() );
        appendTimeResolution( node, tPeriod.getTimeResolution() );
    }

    /**
     * Appends an <code>XML/GML</code> representation of the passed <code>TimeDuration</code> to
     * the passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param duration
     */
    protected static void appendTimeResolution( Element xmlNode, TimeDuration duration ) {
        XMLTools.appendElement( xmlNode, GMLNS, "gml:timeResolution", duration
            .getAsGMLTimeDuration() );
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>Values</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param values
     */
    protected static void appendValues( Element xmlNode, Values values, URI namespaceURI ) {
        Element node = XMLTools.appendElement( xmlNode, namespaceURI, "values" );
        if ( values.getType() != null ) {
            node.setAttribute( "type", values.getType().toString() );
        }
        if ( values.getSemantic() != null ) {
            node.setAttribute( "xmlns:dgr", namespaceURI.toString() );
            node.setAttributeNS( namespaceURI.toString(), "dgr:semantic", 
                                 values.getSemantic().toString() );
        }
        Interval[] intervals = values.getInterval();
        if ( intervals != null ) {
            for (int i = 0; i < intervals.length; i++) {
                appendInterval( node, intervals[i], namespaceURI );
            }
        }
        TypedLiteral[] sVal = values.getSingleValue();
        if ( sVal != null ) {
            for (int i = 0; i < sVal.length; i++) {
                appendTypedLiteral( node, sVal[i], "singleValue", namespaceURI );
            }
        }
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>Interval</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param interval
     */
    protected static void appendInterval( Element xmlNode, Interval interval, URI namespaceURI ) {
        Element node = XMLTools.appendElement( xmlNode, namespaceURI, "interval" );
        Element inter = node;
        if ( interval.getType() != null ) {
            node.setAttribute( "type", interval.getType().toString() );
        }

        if ( interval.getSemantic() != null ) {
            node .setAttribute( "xmlns:dgr", namespaceURI.toString() );
            node .setAttributeNS( namespaceURI.toString(), "dgr:semantic", 
                                  interval.getSemantic().toString() );
        }
        node.setAttribute( "atomic", "" + interval.isAtomic() );
        if ( interval.getClosure() != null
            && interval.getClosure().value != "" && interval.getClosure().value != null ) {
            node .setAttribute( "xmlns:dgr", namespaceURI.toString() );
            node .setAttributeNS( namespaceURI.toString(), "dgr:closure", 
                                  interval.getClosure().value );
        }
        node = XMLTools.appendElement( inter, namespaceURI, "min", interval.getMin().getValue() );
        if ( interval.getMin().getType() != null ) {
            node.setAttribute( "type", interval.getMin().getType().toString() );
        }
        node = XMLTools.appendElement( inter, namespaceURI, "max", interval.getMin().getValue() );
        if ( interval.getMax().getType() != null ) {
            node.setAttribute( "type", interval.getMax().getType().toString() );
        }
        node = XMLTools.appendElement( inter, namespaceURI, "res", interval.getRes().getValue() );
        if ( interval.getRes().getType() != null ) {
            node.setAttribute( "type", interval.getRes().getType().toString() );
        }
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>TypedLiteral</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param singleValue
     */
    protected static void appendTypedLiteral( Element xmlNode, TypedLiteral singleValue,
                                             String name, URI namespaceURI ) {
        Node node = XMLTools.appendElement( xmlNode, namespaceURI, name, singleValue.getValue() );
        if ( singleValue.getType() != null ) {
            ( (Element) node ).setAttribute( "type", singleValue.getType().toString() );
        }
    }

    /**
     * Appends an <code>XML</code> representation of the passed <code>CodeList</code> to the
     * passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param codeList
     */
    protected static void appendCodeList( Element xmlNode, CodeList codeList, URI namespaceURI ) {
        String[] codes = codeList.getCodes();
        String s = StringTools.arrayToString( codes, ' ' );
        Node node = XMLTools.appendElement( xmlNode, namespaceURI, codeList.getName(), s );
        if ( codeList.getCodeSpace() != null ) {
            ( (Element) node ).setAttribute( "codeSpace", codeList.getCodeSpace().toString() );
        }
    }

    /**
     * Appends the XML attributes of the given <code>SimpleLink</code> to the also passed
     * <code>Element</code>.
     * 
     * @param xmlNode
     * @param simpleLink
     * @param propertyName
     * @param href
     */
    protected static void appendSimpleLinkAttributes( Element linkElement, SimpleLink simpleLink ) {

        linkElement.setAttributeNS( XLNNS.toString(), "xlink:type", "simple" );
        if ( simpleLink.getHref() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:href", simpleLink.getHref()
                .toString() );
        }
        if ( simpleLink.getRole() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:role", simpleLink.getRole()
                .toString() );
        }
        if ( simpleLink.getArcrole() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:arcrole", simpleLink.getArcrole()
                .toString() );
        }
        if ( simpleLink.getTitle() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:title", simpleLink.getTitle() );
        }
        if ( simpleLink.getShow() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:show", simpleLink.getShow() );
        }
        if ( simpleLink.getActuate() != null ) {
            linkElement.setAttributeNS( XLNNS.toString(), "xlink:actuate", simpleLink.getActuate() );
        }
    }

    /**
     * Appends the <code>DOM</code> representation of a simple <code>XLink</code> -element to
     * the passed <code>Element</code>.
     * 
     * @param xmlNode
     * @param elementName
     * @param onlineResource
     * @param namespaceURI
     */
    protected static void appendOnlineResource( Element xmlNode, String elementName,
                                               OnlineResource onlineResource, URI namespaceURI ) {
        Element linkElement = XMLTools.appendElement( xmlNode, namespaceURI, elementName );
        linkElement.setAttributeNS( XLNNS.toString(), "xlink:type", "simple" );
        linkElement.setAttributeNS( XLNNS.toString(), "xlink:href", onlineResource.getLinkage()
            .getHref().toString() );
    }

    /**
     * Appends the <code>DOM</code> representation of the given <code>PropertyPath</code>as a
     * new text node to the given element (including necessary namespace bindings).
     * 
     * @param element
     *            Element node where the PropertyPath is appended to
     */
    protected static void appendPropertyPath( Element element, PropertyPath propertyPath ) {
        StringBuffer sb = new StringBuffer();
        sb.append( propertyPath );

        Text textNode = element.getOwnerDocument().createTextNode( sb.toString() );
        element.appendChild( textNode );
        XMLTools.appendNSBindings( element, propertyPath.getNamespaceContext() );
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.17  2006/07/13 15:15:46  poth
useless typecasts removed

Revision 1.16  2006/07/12 16:57:57  poth
*** empty log message ***

Revision 1.15  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
