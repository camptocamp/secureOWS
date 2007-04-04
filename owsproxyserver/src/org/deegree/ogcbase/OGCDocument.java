// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/OGCDocument.java,v 1.21 2006/07/12 16:57:57 poth Exp $
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.CodeList;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.time.TimeDuration;
import org.deegree.datatypes.time.TimePeriod;
import org.deegree.datatypes.time.TimePosition;
import org.deegree.datatypes.time.TimeSequence;
import org.deegree.datatypes.values.Closure;
import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.Values;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcs.describecoverage.InvalidCoverageDescriptionExcpetion;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.21 $, $Date: 2006/07/12 16:57:57 $
 * 
 * @since 1.1
 */
public abstract class OGCDocument extends XMLFragment {

    protected static final URI GMLNS = CommonNamespaces.GMLNS;

    /**
     * creates a <tt>LonLatEnvelope</tt> object from the passed element
     * 
     * @param element
     * @return created <tt>LonLatEnvelope</tt>
     * @throws XMLParsingException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    protected LonLatEnvelope parseLonLatEnvelope( Element element )
                            throws XMLParsingException, OGCWebServiceException {

        String srs = XMLTools.getRequiredAttrValue( "srsName", null, element );
        if ( !"WGS84(DD)".equals( srs ) ) {
            throw new OGCWebServiceException( "srsName must be WGS84(DD) for lonLatEnvelope." );
        }

        ElementList el = XMLTools.getChildElements( "pos", GMLNS, element );
        if ( el == null || el.getLength() != 2 ) {
            throw new OGCWebServiceException( "A lonLatEnvelope must contain two gml:pos elements" );
        }

        Point min = GMLDocument.parsePos( el.item( 0 ) );
        Point max = GMLDocument.parsePos( el.item( 1 ) );

        el = XMLTools.getChildElements( "timePosition", GMLNS, element );
        TimePosition[] timePositions = parseTimePositions( el );

        return new LonLatEnvelope( min, max, timePositions, "WGS84(DD)" );
    }

    /**
     * creates an array of <tt>TimePosition</tt> s from the passed element
     * 
     * @param el
     * @return created array of <tt>TimePosition</tt> s
     * @throws XMLParsingException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    protected TimePosition[] parseTimePositions( ElementList el )
                            throws XMLParsingException, OGCWebServiceException {
        TimePosition[] timePos = new TimePosition[el.getLength()];
        for ( int i = 0; i < timePos.length; i++ ) {
            timePos[i] = GMLDocument.parseTimePosition( el.item( i ) );
        }
        return timePos;
    }

    /**
     * Creates an array of <code>Keywords</code> from the passed list of <code>keyword</code>
     * -elements.
     * 
     * This appears to be pretty superfluous (as one <code>keywords</code>- element may contain
     * several <code>keyword</code> -elements). However, the schema in the OGC document "Web
     * Coverage Service (WCS), Version 1.0.0", contains the following line (in the definition of the
     * CoverageOfferingBriefType):
     * 
     * <code>&lt;xs:element ref="keywords" minOccurs="0" maxOccurs="unbounded"/&gt;</code>
     * 
     * @param el
     * @return created array of <tt>Keywords</tt>
     * @throws XMLParsingException
     */
    protected Keywords[] parseKeywords( ElementList el, URI namespaceURI ) {
        Keywords[] kws = new Keywords[el.getLength()];
        for ( int i = 0; i < kws.length; i++ ) {
            kws[i] = parseKeywords( el.item( i ), namespaceURI );
        }
        return kws;
    }

    /**
     * Creates a <code>Keywords</code> instance from the given <code>keywords</code> -element.
     * 
     * @param element
     * @param namespaceURI
     * @return created <code>Keywords</code>
     * @throws XMLParsingException
     */
    protected Keywords parseKeywords( Element element, URI namespaceURI ) {
        ElementList el = XMLTools.getChildElements( "keyword", namespaceURI, element );
        String[] kws = new String[el.getLength()];
        for ( int i = 0; i < kws.length; i++ ) {
            kws[i] = XMLTools.getStringValue( el.item( i ) );
        }
        return new Keywords( kws );
    }

    /**
     * creates an <tt>TimeSequence</tt> from the passed element
     * 
     * @param element
     * @return created <tt>TimeSequence</tt>
     * @throws XMLParsingException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    protected TimeSequence parseTimeSequence( Element element, URI namespaceURI )
                            throws XMLParsingException, OGCWebServiceException {
        ElementList el = XMLTools.getChildElements( "timePerdiod", namespaceURI, element );
        TimePeriod[] timePerdiods = parseTimePeriods( el, namespaceURI );
        el = XMLTools.getChildElements( "timePosition", GMLNS, element );
        TimePosition[] timePositions = parseTimePositions( el );

        return new TimeSequence( timePerdiods, timePositions );
    }

    /**
     * creates an array of <tt>TimePeriod</tt> s from the passed element
     * 
     * @param el
     * @return created array of <tt>TimePeriod</tt> s
     * @throws XMLParsingException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    protected TimePeriod[] parseTimePeriods( ElementList el, URI namespaceURI )
                            throws XMLParsingException, OGCWebServiceException {
        TimePeriod[] timePeriods = new TimePeriod[el.getLength()];
        for ( int i = 0; i < timePeriods.length; i++ ) {
            timePeriods[i] = parseTimePeriod( el.item( i ), namespaceURI );
        }
        return timePeriods;
    }

    /**
     * creates a <tt>TimePeriod</tt> from the passed element
     * 
     * @param element
     * @return created <tt>TimePeriod</tt>
     * @throws XMLParsingException
     * @throws InvalidCoverageDescriptionExcpetion
     */
    protected TimePeriod parseTimePeriod( Element element, URI namespaceURI )
                            throws XMLParsingException, OGCWebServiceException {
        try {
            Element begin = XMLTools.getRequiredChildElement( "beginPosition", namespaceURI,
                                                              element );
            TimePosition beginPosition = GMLDocument.parseTimePosition( begin );
            Element end = XMLTools.getRequiredChildElement( "endPosition", namespaceURI, element );
            TimePosition endPosition = GMLDocument.parseTimePosition( end );
            String dur = XMLTools.getRequiredStringValue( "timeResolution", namespaceURI, element );
            TimeDuration resolution = TimeDuration.createTimeDuration( dur );

            return new TimePeriod( beginPosition, endPosition, resolution );
        } catch ( InvalidGMLException e ) {
            String s = e.getMessage() + "\n" + StringTools.stackTraceToString( e );
            throw new OGCWebServiceException( s );
        }

    }

    /**
     * creates a <tt>Values</tt> object from the passed element
     * 
     * @param element
     * @return created <tt>Values</tt>
     * @throws XMLParsingException
     */
    protected Values parseValues( Element element, URI namespaceURI )
                            throws XMLParsingException {

        String type = XMLTools.getAttrValue( element, namespaceURI, "type" );
        String semantic = XMLTools.getAttrValue( element, namespaceURI, "semantic" );

        ElementList el = XMLTools.getChildElements( "interval", namespaceURI, element );
        Interval[] intervals = new Interval[el.getLength()];
        for ( int i = 0; i < intervals.length; i++ ) {
            intervals[i] = parseInterval( el.item( i ), namespaceURI );
        }

        el = XMLTools.getChildElements( "singleValue", namespaceURI, element );
        TypedLiteral[] singleValues = new TypedLiteral[el.getLength()];
        for ( int i = 0; i < singleValues.length; i++ ) {
            singleValues[i] = parseTypedLiteral( el.item( i ) );
        }

        Element elem = XMLTools.getChildElement( "default", namespaceURI, element );
        TypedLiteral def = null;
        if ( elem != null ) {
            def = parseTypedLiteral( elem );
        }

        try {
            URI sem = null;
            if ( semantic != null )
                sem = new URI( semantic );
            URI tp = null;
            if ( type != null )
                tp = new URI( type );
            return new Values( intervals, singleValues, tp, sem, def );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "couldn't parse URI from valuesl\n"
                                           + StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * creates an <tt>Interval</tt> object from the passed element
     * 
     * @param element
     * @return created <tt>Interval</tt>
     * @throws XMLParsingException
     */
    protected Interval parseInterval( Element element, URI namespaceURI )
                            throws XMLParsingException {

        try {
            String tmp = XMLTools.getAttrValue( element, namespaceURI, "type" );
            URI type = null;
            if ( tmp != null )
                type = new URI( tmp );
            String semantic = XMLTools.getAttrValue( element, namespaceURI, "semantic" );
            tmp = XMLTools.getAttrValue( element, "atomic" );
            boolean atomic = "true".equals( tmp ) || "1".equals( tmp );
            String clos = XMLTools.getAttrValue( element, namespaceURI, "closure" );

            Closure closure = new Closure( clos );

            Element elem = XMLTools.getRequiredChildElement( "min", namespaceURI, element );
            TypedLiteral min = parseTypedLiteral( elem );

            elem = XMLTools.getRequiredChildElement( "min", namespaceURI, element );
            TypedLiteral max = parseTypedLiteral( elem );

            elem = XMLTools.getRequiredChildElement( "res", namespaceURI, element );
            TypedLiteral res = parseTypedLiteral( elem );

            URI sem = null;
            if ( semantic != null )
                sem = new URI( semantic );

            return new Interval( min, max, type, sem, atomic, closure, res );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "couldn't parse URI from interval\n"
                                           + StringTools.stackTraceToString( e ) );
        }

    }

    /**
     * creates a <tt>TypedLiteral</tt> from the passed element
     * 
     * @param element
     * @return created <tt>TypedLiteral</tt>
     * @throws XMLParsingException
     */
    protected TypedLiteral parseTypedLiteral( Element element )
                            throws XMLParsingException {
        try {
            String tmp = XMLTools.getStringValue( element );
            String mtype = XMLTools.getAttrValue( element, "type" );
            URI mt = null;
            if ( mtype != null )
                mt = new URI( mtype );
            return new TypedLiteral( tmp, mt );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "couldn't parse URI from typedLiteral\n"
                                           + StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * creates an array of <tt>CodeList</tt> objects from the passed element list
     * 
     * @param el
     * @return created array of <tt>CodeList</tt>
     * @throws XMLParsingException
     */
    protected CodeList[] parseCodeListArray( ElementList el )
                            throws XMLParsingException {
        CodeList[] cl = new CodeList[el.getLength()];
        for ( int i = 0; i < cl.length; i++ ) {
            cl[i] = parseCodeList( el.item( i ) );
        }
        return cl;
    }

    /**
     * creates a <tt>CodeList</tt> object from the passed element
     * 
     * @param element
     * @return created <tt>CodeList</tt>
     * @throws XMLParsingException
     */
    protected CodeList parseCodeList( Element element )
                            throws XMLParsingException {
        try {
            String tmp = XMLTools.getAttrValue( element, "codeSpace" );
            URI codeSpace = null;
            if ( tmp != null ) {
                codeSpace = new URI( tmp );
            }
            tmp = XMLTools.getStringValue( element );
            String[] ar = StringTools.toArray( tmp, " ,;", true );
            return new CodeList( element.getNodeName(), ar, codeSpace );
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "couldn't parse URI from CodeList\n"
                                           + StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * Creates an <tt>OnLineResource</tt> instance from the passed element. The element contains
     * an OnlineResourse as it is used in the OGC Web XXX CapabilitiesService specifications.
     * 
     * TODO Compare with XMLFragment#parseSimpleLink
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    protected OnlineResource parseOnLineResource( Element element )
                            throws XMLParsingException {

        OnlineResource olr = null;
        String attrValue = XMLTools.getRequiredAttrValue( "href", XLNNS, element );
        URL href = null;
        try {
            href = resolve( attrValue );
        } catch ( MalformedURLException e ) {
            throw new XMLParsingException( "Given value '" + attrValue + "' in attribute 'href' "
                                           + "(namespace: " + XLNNS + ") of element '"
                                           + element.getLocalName() + "' (namespace: "
                                           + element.getNamespaceURI() + ") is not a valid URL." );
        }
        Linkage linkage = new Linkage( href, Linkage.SIMPLE );
        String title = XMLTools.getAttrValue( element, XLNNS, "title" );
        olr = new OnlineResource( null, null, linkage, null, title, href.getProtocol() );
        return olr;
    }

    /**
     * Creates a new instance of <code>PropertyPath</code> from the given text node.
     * <p>
     * NOTE: Namespace prefices used in the property path must be bound using XML namespace
     * mechanisms (i.e. using xmlns attributes in the document).
     * 
     * @param textNode
     *            string representation of the property path
     * @return new PropertyPath instance
     * @see PropertyPath
     */
    public static PropertyPath parsePropertyPath( Text textNode )
                            throws XMLParsingException {

        String path = XMLTools.getStringValue( textNode );
        String[] steps = StringTools.toArray( path, "/", false );
        List<PropertyPathStep> propertyPathSteps = new ArrayList<PropertyPathStep>( steps.length );

        for ( int i = 0; i < steps.length; i++ ) {
            PropertyPathStep propertyStep = null;
            QualifiedName propertyName = null;
            String step = steps[i];
            boolean isAttribute = false;
            boolean isIndexed = false;
            int selectedIndex = -1;

            // check if step begins with '@' -> must be the final step then
            if ( step.startsWith( "@" ) ) {
                if ( i != steps.length - 1 ) {
                    String msg = "PropertyName '" + path
                                 + "' is illegal: the attribute specifier may only "
                                 + "be used for the final step.";
                    throw new XMLParsingException( msg );
                }
                step = step.substring( 1 );
                isAttribute = true;
            }

            // check if the step ends with brackets ([...])
            if ( step.endsWith( "]" ) ) {
                if ( isAttribute ) {
                    String msg = "PropertyName '" + path
                                 + "' is illegal: if the attribute specifier ('@') is used, "
                                 + "index selection ('[...']) is not possible.";
                    throw new XMLParsingException( msg );
                }
                int bracketPos = step.indexOf( '[' );
                if ( bracketPos < 0 ) {
                    String msg = "PropertyName '" + path
                                 + "' is illegal. No opening brackets found for step '" + step
                                 + "'.";
                    throw new XMLParsingException( msg );
                }
                try {
                    selectedIndex = Integer.parseInt( step.substring( bracketPos + 1,
                                                                      step.length() - 1 ) );
                } catch ( NumberFormatException e ) {
                    String msg = "PropertyName '" + path + "' is illegal. Specified index '"
                                 + step.substring( bracketPos + 1, step.length() - 1 )
                                 + "' is not a number.";
                    throw new XMLParsingException( msg );
                }
                step = step.substring( 0, bracketPos );
                isIndexed = true;
            }

            // determine namespace prefix and binding (if any)
            int colonPos = step.indexOf( ':' );
            if ( colonPos < 0 ) {
                propertyName = new QualifiedName( step );
            } else {
                String prefix = step.substring( 0, colonPos );
                step = step.substring( colonPos + 1 );
                URI namespace = null;
                try {
                    namespace = XMLTools.getNamespaceForPrefix( prefix, textNode );
                } catch ( URISyntaxException e ) {
                    throw new XMLParsingException( "Error parsing PropertyName: " + e.getMessage() );
                }
                if ( namespace == null ) {
                    throw new XMLParsingException( "PropertyName '" + path
                                                   + "' uses an unbound namespace prefix: "
                                                   + prefix );
                }
                propertyName = new QualifiedName( prefix, step, namespace );
            }

            if ( isAttribute ) {
                propertyStep = PropertyPathFactory.createAttributePropertyPathStep( propertyName );
            } else if ( isIndexed ) {
                propertyStep = PropertyPathFactory.createPropertyPathStep( propertyName,
                                                                           selectedIndex );
            } else {
                propertyStep = PropertyPathFactory.createPropertyPathStep( propertyName );
            }
            propertyPathSteps.add( propertyStep );
        }
        return PropertyPathFactory.createPropertyPath( propertyPathSteps );
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: OGCDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.21  2006/07/12 16:57:57  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.20  2006/07/06 17:34:55  mschneider
 * Changes to this class. What the people have been up to: Fixed handling of unbound namespace prefices in parsePropertyPath().
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.19  2006/04/06 20:25:22  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.18  2006/04/04 20:39:40  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.17  2006/04/04 10:33:06  mschneider
 * Changes to this class. What the people have been up to: Adapted to updated PropertyPath classes.
 * Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.16 2006/03/30 21:20:24 poth Changes to
 * this class. What the people have been up to: *** empty log message *** Changes to this class.
 * What the people have been up to: Changes to this class. What the people have been up to: Revision
 * 1.15 2006/03/21 21:38:40 poth Changes to this class. What the people have been up to: *** empty
 * log message *** Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.14 2006/01/11 16:57:15 poth Changes to this class.
 * What the people have been up to: *** empty log message *** Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.13 2005/11/22
 * 18:05:17 deshmukh Changes to this class. What the people have been up to: java split method
 * replaced with StringTools.toArray() Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.12 2005/11/16 13:45:00
 * mschneider Changes to this class. What the people have been up to: Merge of wfs development
 * branch. Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.11.2.3 2005/11/15 17:01:04 mschneider Changes to this class.
 * What the people have been up to: Fixed off-by one error in parsePropertyPath(). Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.11.2.2 2005/11/10 15:24:44 mschneider Changes to this class. What the people have been
 * up to: Refactoring: use "PropertyPath" in "org.deegree.model.filterencoding.PropertyName".
 * Changes to this class. What the people have been up to: Revision 1.2 2005/03/09 11:55:46
 * mschneider *** empty log message ***
 * 
 * Revision 1.1.1.1 2005/01/05 10:33:22 poth no message
 * 
 * Revision 1.21 2004/08/26 15:43:31 tf no message
 * 
 * 
 * Revision 1.9 2004/06/30 15:16:05 mschneider Refactoring of XMLTools.
 * 
 * Revision 1.8 2004/06/28 06:40:04 ap no message
 * 
 * Revision 1.7 2004/06/23 14:55:14 ap no message
 * 
 * Revision 1.6 2004/06/23 14:01:04 tf add getCapabilities() method
 * 
 * Revision 1.5 2004/06/23 11:48:38 tf javadoc updated
 * 
 * Revision 1.4 2004/06/21 08:05:49 ap no message
 * 
 * Revision 1.3 2004/06/02 14:10:34 ap no message
 * 
 * Revision 1.2 2004/06/02 07:01:41 ap no message
 * 
 * Revision 1.1 2004/05/31 07:37:45 ap no message
 * 
 **************************************************************************************************/
