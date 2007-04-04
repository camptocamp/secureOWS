//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/GMLFeatureAdapter.java,v 1.73 2006/11/09 17:45:53 mschneider Exp $
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
 Aennchenstraße 19
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
package org.deegree.model.feature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.DOMPrinter;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Exports feature instances to their GML representation.
 * <p>
 * Has support for XLink output and to disable XLink output (which is generally not feasible).
 * 
 * TODO Handle FeatureCollections like ordinary Features (change model).
 * TODO Separate cycle check (for suppressXLinkOutput).
 * TODO Use a more straight-forward approach to export DOM representations. 
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.73 $, $Date: 2006/11/09 17:45:53 $
 */
public class GMLFeatureAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( GMLFeatureAdapter.class );

    // values: feature ids of already exported features (for XLinks)
    private Set<String> exportedFeatures = new HashSet<String>();

    // values: feature ids of all (sub-) features in a feature (to find cyclic features)    
    private Set<String> localFeatures = new HashSet<String>();

    private boolean suppressXLinkOutput;

    private String schemaURL;

    /**
     * Creates a new <code>GMLFeatureAdapter</code> instance with enabled XLink output.
     */
    public GMLFeatureAdapter() {
        this.suppressXLinkOutput = false;
    }

    /**
     * Creates a new <code>GMLFeatureAdapter</code> instance with enabled XLink output and schema
     * reference.
     * 
     * @param schemaURL
     *           URL of schema document (used as xsi:schemaLocation attribute in XML output) 
     */
    public GMLFeatureAdapter( String schemaURL ) {
        this.suppressXLinkOutput = false;
        if ( schemaURL != null ) {
            this.schemaURL = StringTools.replace( schemaURL, "&", "&amp;", true );
        }
    }

    /**
     * Creates a new instance <code>GMLFeatureAdapter</code> with configurable XLink output.
     * 
     * @param suppressXLinkOutput
     *            set to true, if no XLinks shall be used
     */
    public GMLFeatureAdapter( boolean suppressXLinkOutput ) {
        this.suppressXLinkOutput = suppressXLinkOutput;
    }

    /**
     * Creates a new instance <code>GMLFeatureAdapter</code> with configurable XLink output.
     * 
     * @param suppressXLinkOutput
     *            set to true, if no XLinks shall be used
     * @param schemaURL
     *           URL of schema document (used as xsi:schemaLocation attribute in XML output)
     */
    public GMLFeatureAdapter( boolean suppressXLinkOutput, String schemaURL ) {
        this.suppressXLinkOutput = suppressXLinkOutput;
        if ( schemaURL != null ) {
            this.schemaURL = StringTools.replace( schemaURL, "&", "&amp;", true );
        }
    }

    /**    
     * Appends the DOM representation of the given feature to the also given <code>Node</code>.
     * <p>
     * TODO do this a better way (append nodes directly without serializing to string and
     * parsing it again)
     *  
     * @param root
     * @param feature
     * @throws FeatureException
     * @throws IOException
     * @throws SAXException
     */
    public void append( Element root, Feature feature )
                            throws FeatureException, IOException, SAXException {

        GMLFeatureDocument doc = export( feature );
        XMLTools.insertNodeInto( doc.getRootElement(), root );
    }

    /**
     * Export a <code>Feature</code> to it's XML representation.
     * 
     * @param feature feature to export
     * @return XML representation of feature
     * @throws IOException
     * @throws FeatureException
     * @throws XMLException
     * @throws SAXException
     */
    public GMLFeatureDocument export( Feature feature )
                            throws IOException, FeatureException, XMLException, SAXException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream( 20000 );
        export( feature, bos );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        bos.close();

        GMLFeatureDocument doc = new GMLFeatureDocument();
        doc.load( bis, XMLFragment.DEFAULT_URL );
        return doc;
    }

    /**    
     * Appends the DOM representation of the given <code>FeatureCollection</code> to the
     * also given <code>Node</code>.
     * <p>
     * TODO do this a better way (append nodes directly without serializing to string and
     * parsing it again)
     *  
     * @param root
     * @param fc
     * @throws FeatureException
     * @throws IOException
     * @throws SAXException
     */
    public void append( Element root, FeatureCollection fc )
                            throws FeatureException, IOException, SAXException {

        GMLFeatureCollectionDocument doc = export( fc );
        XMLTools.insertNodeInto( doc.getRootElement(), root );
    }

    /**
     * Export a <code>FeatureCollection</code> to it's XML representation.
     * 
     * @param fc feature collection
     * @return XML representation of feature collection
     * @throws IOException
     * @throws FeatureException
     * @throws XMLException
     * @throws SAXException
     */
    public GMLFeatureCollectionDocument export( FeatureCollection fc )
                            throws IOException, FeatureException, XMLException, SAXException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream( 20000 );
        export( fc, bos );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        bos.close();

        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        doc.load( bis, XMLFragment.DEFAULT_URL );
        return doc;
    }

    /**
     * Exports an instance of a <code>FeatureCollection</code> to the passed
     * <code>OutputStream</code> formatted as GML. Uses the deegree system character set for the
     * XML header encoding information.
     * 
     * @param fc
     *            feature collection to export
     * @param os
     *            output stream to write to
     * 
     * @throws IOException
     * @throws FeatureException
     */
    public void export( FeatureCollection fc, OutputStream os )
                            throws IOException, FeatureException {
        export( fc, os, CharsetUtils.getSystemCharset() );
    }

    /**
     * Exports a <code>FeatureCollection</code> instance to the passed <code>OutputStream</code>
     * formatted as GML.
     * 
     * @param fc
     *            feature collection to export
     * @param os
     *            output stream to write to
     * @param charsetName
     *            name of the used charset/encoding (for the XML header)
     * 
     * @throws IOException
     * @throws FeatureException
     */
    public void export( FeatureCollection fc, OutputStream os, String charsetName )
                            throws IOException, FeatureException {

        PrintWriter pw = new PrintWriter( new OutputStreamWriter( os, charsetName ) );
        pw.println( "<?xml version=\"1.0\" encoding=\"" + charsetName + "\"?>" );
        exportRootCollection( fc, pw );
        pw.close();
    }

    /**
     * Exports a <code>FeatureCollection</code> instance to the passed <code>OutputStream</code>
     * formatted as GML.
     * 
     * @param fc
     *            feature collection to print/export
     * @param pw
     *            target of the printing/export
     * @throws FeatureException 
     */
    private void exportRootCollection( FeatureCollection fc, PrintWriter pw )
                            throws FeatureException {

        if ( fc.getId() != null && !"".equals( fc.getId() ) ) {
            this.exportedFeatures.add( fc.getId() );
        }

        // open the feature collection element
        pw.print( "<" );
        pw.print( fc.getName().getAsString() );

        Map<String, String> attributes = fc.getAttributes();
        for ( Iterator iterator = attributes.keySet().iterator(); iterator.hasNext(); ) {
            String name = (String) iterator.next();
            String value = attributes.get( name );
            pw.print( ' ' );
            pw.print( name );
            pw.print( "='" );
            pw.print( value );
            pw.print( "'" );
        }

        // determine and add namespace bindings
        Map<String, URI> nsBindings = determineUsedNSBindings( fc );
        nsBindings.put( "gml", CommonNamespaces.GMLNS );
        nsBindings.put( "xlink", CommonNamespaces.XLNNS );
        if ( this.schemaURL != null ) {
            nsBindings.put( "xsi", CommonNamespaces.XSINS );
        }
        appendNSBindings( nsBindings, pw );

        // add schema reference (if available)
        if ( this.schemaURL != null ) {
            pw.print( " xsi:schemaLocation=\"http://www.deegree.org/app " );
            pw.print( this.schemaURL + "\"" );
        }
        pw.print( '>' );

        Envelope env;
        try {
            env = fc.getBoundedBy();
        } catch ( GeometryException e ) {
            throw new FeatureException( "Error getting BBOX of feature collection: "
                                        + e.getMessage(), e );
        }
        if ( env != null ) {
            pw.print( "<gml:boundedBy><gml:Envelope" );
            if ( env.getCoordinateSystem() != null ) {
                pw.print( " srsName='" + env.getCoordinateSystem().getAsString() + "'" );
            }
            pw.print( "><gml:pos srsDimension='2'>" );
            pw.print( env.getMin().getX() );
            pw.print( ' ' );
            pw.print( env.getMin().getY() );
            pw.print( "</gml:pos><gml:pos srsDimension='2'>" );
            pw.print( env.getMax().getX() );
            pw.print( ' ' );
            pw.print( env.getMax().getY() );
            pw.print( "</gml:pos></gml:Envelope></gml:boundedBy>" );
        }

        // export all contained features
        for ( int i = 0; i < fc.size(); i++ ) {
            Feature feature = fc.getFeature( i );
            String fid = feature.getId();
            if ( fid != null && !fid.equals( "" ) && this.exportedFeatures.contains( fid )
                 && !this.suppressXLinkOutput ) {
                pw.print( "<gml:featureMember xlink:href=\"#" );
                pw.print( fid );
                pw.print( "\"/>" );
            } else {
                pw.print( "<gml:featureMember>" );
                export( feature, pw );
                pw.print( "</gml:featureMember>" );
            }
        }

        // close the feature collection element
        pw.print( "</" );
        pw.print( fc.getName().getAsString() );
        pw.print( '>' );

    }

    /**
     * Determines the namespace bindings that are used in the feature collection.
     * <p>
     * NOTE: Currently only the bindings for the feature collection's root element and the contained
     * features are considered. If a subfeature uses another bindings, this binding will be missing
     * in the XML.
     * 
     * @param fc
     *            feature collection
     * @return the namespace bindings.
     */
    private Map<String, URI> determineUsedNSBindings( FeatureCollection fc ) {

        Map<String, URI> nsBindings = new HashMap<String, URI>();

        // process feature collection element
        QualifiedName name = fc.getName();
        nsBindings.put( name.getPrefix(), name.getNamespace() );

        // process contained features
        for ( int i = 0; i < fc.size(); i++ ) {
            name = fc.getFeature( i ).getName();
            nsBindings.put( name.getPrefix(), name.getNamespace() );
        }

        return nsBindings;
    }

    /**
     * Appends the given namespace bindings to the PrintWriter.
     * 
     * @param bindings
     *            namespace bindings to append
     * @param pw
     *            PrintWriter to write to
     */
    private void appendNSBindings( Map<String, URI> bindings, PrintWriter pw ) {

        Iterator<String> prefixIter = bindings.keySet().iterator();
        while ( prefixIter.hasNext() ) {
            String prefix = prefixIter.next();
            URI nsURI = bindings.get( prefix );
            pw.print( " xmlns:" );
            pw.print( prefix );
            pw.print( "=\"" );
            pw.print( nsURI );
            pw.print( '\"' );
        }
    }

    /**
     * Exports an instance of a <code>Feature</code> to the passed <code>OutputStream</code>
     * formatted as GML. Uses the deegree system character set for the XML header encoding
     * information.
     * 
     * @param feature
     *            feature to export
     * @param os
     *            output stream to write to
     * 
     * @throws IOException
     * @throws FeatureException
     */
    public void export( Feature feature, OutputStream os )
                            throws IOException, FeatureException {
        export( feature, os, CharsetUtils.getSystemCharset() );
    }

    /**
     * Exports a <code>Feature</code> instance to the passed <code>OutputStream</code>
     * formatted as GML.
     * 
     * @param feature
     *            feature to export
     * @param os
     *            output stream to write to
     * @param charsetName
     *            name of the used charset/encoding (for the XML header)
     * @throws IOException
     * @throws FeatureException
     */
    public void export( Feature feature, OutputStream os, String charsetName )
                            throws IOException, FeatureException {

        PrintWriter pw = new PrintWriter( new OutputStreamWriter( os, charsetName ) );
        pw.println( "<?xml version=\"1.0\" encoding=\"" + charsetName + "\"?>" );
        export( feature, pw );
        pw.close();
    }

    /**
     * Exports a <code>Feature</code> instance to the passed <code>PrintWriter</code> as GML.
     * 
     * @param feature
     *            feature to export
     * @param pw
     *            PrintWriter to write to
     * @throws FeatureException
     */
    private void export( Feature feature, PrintWriter pw )
                            throws FeatureException {

        QualifiedName ftName = feature.getName();
        String fid = feature.getId();

        if ( this.suppressXLinkOutput && fid != null && !"".equals( fid ) ) {
            if ( this.localFeatures.contains( fid ) ) {
                String msg = Messages.format( "ERROR_CYLIC_FEATURE", fid );
                throw new FeatureException( msg );
            }
            this.localFeatures.add( fid );
        }

        // open feature element (add gml:id attribute if feature has an id)
        pw.print( '<' );
        pw.print( ftName.getAsString() );
        if ( fid != null ) {
            this.exportedFeatures.add( fid );
            pw.print( " gml:id=\"" );
            pw.print( fid );
            pw.print( '\"' );
        }
        pw.print( '>' );

        try {
            Envelope env = null;
            if ( ( env = feature.getBoundedBy() ) != null ) {
                pw.print( "<gml:boundedBy><gml:Envelope" );
                if ( env.getCoordinateSystem() != null ) {
                    pw.print( " srsName='" + env.getCoordinateSystem().getAsString() + "'" );
                }
                pw.print( "><gml:pos srsDimension='2'>" );                
                pw.print( env.getMin().getX() );
                pw.print( ' ' );
                pw.print( env.getMin().getY() );
                pw.print( "</gml:pos><gml:pos srsDimension=\"2\">" );
                pw.print( env.getMax().getX() );
                pw.print( ' ' );
                pw.print( env.getMax().getY() );
                pw.print( "</gml:pos></gml:Envelope></gml:boundedBy>" );
            }
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }

        // export all properties of the feature
        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            if ( properties[i] != null && properties[i].getValue() != null ) {
                exportProperty( properties[i], pw );
            }
        }

        // close feature element        
        pw.print( "</" );
        pw.print( ftName.getAsString() );
        pw.println( '>' );

        if ( this.suppressXLinkOutput || fid != null ) {
            this.localFeatures.remove( fid );
        }

    }

    /**
     * Exports a <code>FeatureProperty</code> instance to the passed <code>PrintWriter</code> as
     * GML.
     * 
     * @param property
     *            property to export
     * @param pw
     *            PrintWriter to write to
     * @throws FeatureException
     */
    private void exportProperty( FeatureProperty property, PrintWriter pw )
                            throws FeatureException {

        QualifiedName propertyName = property.getName();

        if ( property instanceof XLinkedFeatureProperty && !this.suppressXLinkOutput ) {
            pw.print( '<' );
            pw.print( propertyName.getAsString() );
            pw.print( " xlink:href=\"#" );
            pw.print( ( (Feature) property.getValue() ).getId() );
            pw.print( "\"/>" );
        } else {
            pw.print( '<' );
            pw.print( propertyName.getAsString() );
            pw.print( '>' );
            Object value = property.getValue();
            if ( value != null ) {
                exportPropertyValue( value, pw );
            }
            pw.print( "</" );
            pw.print( propertyName.getAsString() );
            pw.print( '>' );
        }
    }

    /**
     * Exports the value of a property to the passed <code>PrintWriter</code> as GML.
     * 
     * TODO Handle date
     * 
     * @param value
     *            property value to export
     * @param pw
     *            PrintWriter to write to
     * @throws FeatureException
     */
    private void exportPropertyValue( Object value, PrintWriter pw )
                            throws FeatureException {
        if ( value instanceof Feature ) {
            export( (Feature) value, pw );
        } else if ( value instanceof Feature[] ) {
            Feature[] features = (Feature[]) value;
            for ( int i = 0; i < features.length; i++ ) {
                export( features[i], pw );
            }
        } else if ( value instanceof Envelope ) {
            exportEnvelope( (Envelope) value, pw );
        } else if ( value instanceof FeatureCollection ) {
            export( (FeatureCollection) value, pw );
        } else if ( value instanceof Geometry ) {
            exportGeometry( (Geometry) value, pw );
        } else if ( value instanceof Date ) {
            pw.print( ( (Date) value ).toString() );
            // pw.print( TimeTools.getISOFormattedTime( (Date) value ) );
        } else if ( value instanceof Calendar ) {
            pw.print( ( (Calendar) value ).getTime().toString() );
        } else if ( value instanceof Integer || value instanceof Long || value instanceof Float
                    || value instanceof Double || value instanceof BigDecimal ) {
            pw.print( value.toString() );
        } else if ( value instanceof String ) {
            StringBuffer sb = DOMPrinter.validateCDATA( (String) value );
            pw.print( sb );
        } else if ( value instanceof Boolean ) {
            pw.print( value );
        } else {
            LOG.logInfo( "Unhandled property class '" + value.getClass()
                         + "' in GMLFeatureAdapter." );
            StringBuffer sb = DOMPrinter.validateCDATA( value.toString() );
            pw.print( sb );
        }
    }

    /**
     * prints the passed geometry to the also passed PrintWriter formatted as GML
     * 
     * @param geo
     *            geometry to print/extport
     * @param pw
     *            target of the printing/export
     * @throws FeatureException
     */
    private void exportGeometry( Geometry geo, PrintWriter pw )
                            throws FeatureException {
        LOG.entering();
        try {
            pw.print( GMLGeometryAdapter.export( geo ) );
        } catch ( Exception e ) {
            LOG.logError( "", e );
            throw new FeatureException( "Could not export geometry to GML: " + e.getMessage(), e );
        }
        LOG.exiting();
    }

    /**
     * prints the passed geometry to the also passed PrintWriter formatted as GML
     * 
     * @param geo
     *            geometry to print/extport
     * @param pw
     *            target of the printing/export
     * @throws FeatureException
     */
    private void exportEnvelope( Envelope geo, PrintWriter pw )
                            throws FeatureException {
        LOG.entering();
        try {
            pw.print( GMLGeometryAdapter.exportAsBox( geo ) );
        } catch ( Exception e ) {
            throw new FeatureException( "Could not export envelope to GML: " + e.getMessage(), e );
        }
        LOG.exiting();
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: GMLFeatureAdapter.java,v $
 Revision 1.73  2006/11/09 17:45:53  mschneider
 Added srsName attribute in Envelopes.

 Revision 1.72  2006/10/17 20:31:19  poth
 *** empty log message ***

 Revision 1.71  2006/10/17 15:21:57  mschneider
 Bug fix: use of XLinked root feature members was not possible.

 Revision 1.70  2006/10/11 20:31:45  mschneider
 Added encoding of & (to &amp;) in schemaURL, because it is used in xsi:schemaLocation attribute.

 Revision 1.69  2006/10/11 18:00:01  mschneider
 Added constructors that allow to place a schema reference in generated feature collections.

 Revision 1.68  2006/08/23 16:37:35  mschneider
 Fixed output of " in 'gml:Envelope' elements.

 Revision 1.67  2006/08/11 09:29:40  poth
 strings replace by chars where possible / add srsDimension for global Envelope

 Revision 1.66  2006/08/01 10:41:10  mschneider
 Added checks for empty fids ("").

 Revision 1.65  2006/07/25 15:52:17  mschneider
 Improved javadoc.

 Revision 1.64  2006/06/21 10:24:13  mschneider
 Added methods to export to DOM.

 Revision 1.63  2006/06/15 18:30:48  poth
 *** empty log message ***

 Revision 1.62  2006/05/09 15:51:37  poth
 *** empty log message ***

 Revision 1.61  2006/04/27 09:44:59  poth
 *** empty log message ***

 Revision 1.60  2006/04/26 13:34:09  poth
 *** empty log message ***

 Revision 1.59  2006/04/24 09:34:31  poth
 *** empty log message ***

 Revision 1.58  2006/04/18 18:22:55  poth
 *** empty log message ***

 Revision 1.26  2006/04/18 18:20:46  poth
 *** empty log message ***

 Revision 1.25  2006/04/18 14:40:32  mschneider
 Removed unnecessary whitespace on attribute output.

 Revision 1.24  2006/04/11 15:12:31  poth
 *** empty log message ***

 Revision 1.23  2006/04/07 17:14:34  mschneider
 Added workaround for XLinkedFeatureProperties.

 Revision 1.22  2006/04/06 20:25:27  poth
 *** empty log message ***

 Revision 1.21  2006/04/04 20:39:42  poth
 *** empty log message ***

 Revision 1.20  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.19  2006/03/18 22:47:19  mschneider
 Added handling of Long properties to exportPropertyValue.

 Revision 1.18  2006/02/26 21:30:42  poth
 *** empty log message ***

 Revision 1.17  2006/02/21 23:57:15  mschneider
 Removed output of empty (null valued) features.

 Revision 1.16  2006/02/09 10:36:03  mschneider
 Improved javadoc.

 Revision 1.15  2006/02/09 09:53:44  mschneider
 Implemented suppressXLinkOutput.

 Revision 1.14  2006/02/08 17:43:17  mschneider
 Major cleanup.

 Revision 1.13  2006/02/05 21:21:13  mschneider
 Began implementation of option to suppress xlink output.

 Revision 1.12  2006/02/05 00:18:29  mschneider
 Initial version. Necessary for XLink handling.

 ********************************************************************** */