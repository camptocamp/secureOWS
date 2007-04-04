// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/xml/XMLFragment.java,v 1.56 2006/10/31 16:51:42 mschneider Exp $
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

package org.deegree.framework.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.xlink.SimpleLink;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.model.feature.Messages;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An instance of <code>XMLFragment</code> encapsulates an underlying {@link Element} which
 * acts as the root element of the document (which may be a fragment or a whole document).
 * <p>
 * Basically, <code>XMLFragment</code> provides easy loading and proper saving (automatically
 * generated CDATA-elements for text nodes that need to be escaped) and acts as base class for all
 * XML parsers in deegree.
 * 
 * TODO: automatically generated CDATA-elements are not implemented yet
 * 
 * <p>
 * Additionally, <code>XMLFragment</code> tries to make the handling of relative paths inside the
 * document's content as painless as possible. This means that after initialization of the
 * <code>XMLFragment</code> with the correct SystemID (i.e. the URL of the document):
 * <ul>
 * <li>external parsed entities (in the DOCTYPE part) can use relative URLs; e.g. &lt;!ENTITY local
 * SYSTEM "conf/wfs/wfs.cfg"&gt;</li>
 * <li>application specific documents which extend <code>XMLFragment</code> can resolve relative
 * URLs during parsing by calling the <code>resolve()</code> method</li>
 * </ul>
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.56 $, $Date: 2006/10/31 16:51:42 $
 * 
 * @see org.deegree.framework.xml.XMLTools
 */

public class XMLFragment implements Serializable {

    private static final long serialVersionUID = 8984447437613709386L;

    protected static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    protected static final URI XLNNS = CommonNamespaces.XLNNS;

    protected static final ILogger LOG = LoggerFactory.getLogger( XMLFragment.class );

    /**
     * Use this URL as SystemID only if an <code>XMLFragment</code> cannot be pinpointed to a URL -
     * in this case it may not use any relative references!
     */
    public static final String DEFAULT_URL = "http://www.deegree.org";

    private URL systemId;

    private Element rootElement;

    private static final String PRETTY_PRINTER_RESOURCE = "PrettyPrinter.xsl";

    private static XSLTDocument PRETTY_PRINTER_XSLT = new XSLTDocument();

    static {
        LOG.logDebug( "DOM implementation in use (DocumentBuilderFactory): "
                      + DocumentBuilderFactory.newInstance().getClass().getName() );
        try {
            LOG.logDebug( "DOM implementation in use (DocumentBuilder): "
                          + DocumentBuilderFactory.newInstance().newDocumentBuilder().getClass().getName() );
        } catch ( Exception e ) {
            BootLogger.logError( "Error creating test DocumentBuilder instance.", e );
        }
        try {
            URL url = XMLFragment.class.getResource( PRETTY_PRINTER_RESOURCE );
            if ( url == null ) {
                throw new IOException( "The resource '" + PRETTY_PRINTER_RESOURCE
                                       + " could not be found." );
            }
            PRETTY_PRINTER_XSLT.load( url );
        } catch ( Exception e ) {
            BootLogger.logError( "Error loading PrettyPrinter-XSLT document: " + e.getMessage(), e );
        }
    }

    /**
     * Creates a new <code>XMLFragment</code> which is not initialized.
     */
    public XMLFragment() {
        // nothing to do
    }

    /**
     * Creates a new <code>XMLFragment</code> which is loaded from the given <code>URL</code>.
     * 
     * @param url
     * @throws IOException
     * @throws SAXException
     */
    public XMLFragment( URL url ) throws IOException, SAXException {
        load( url );
    }

    /**
     * Creates a new <code>XMLFragment</code> which is loaded from the given <code>Reader</code>.
     * 
     * @param reader
     * @param systemId
     * @throws SAXException
     * @throws IOException
     */
    public XMLFragment( Reader reader, String systemId ) throws SAXException, IOException {
        load( reader, systemId );
    }

    /**
     * Creates a new <code>XMLFragment</code> instance based on the submitted
     * <code>Document</code>.
     * 
     * @param doc
     * @param systemId
     * @throws MalformedURLException
     *             if systemId is no valid and absolute <code>URL</code>
     */
    public XMLFragment( Document doc, String systemId ) throws MalformedURLException {
        setRootElement( doc.getDocumentElement() );
        setSystemId( systemId );
    }

    /**
     * Creates a new <code>XMLFragment</code> instance based on the submitted <code>Element</code>.
     * 
     * @param element
     */
    public XMLFragment( Element element ) {
        setRootElement( element );
    }

    /**
     * Returns the systemId (the URL of the <code>XMLFragment</code>).
     * 
     * @return the systemId
     */
    public URL getSystemId() {
        return systemId;
    }

    /**
     * @param systemId
     *            The systemId (physical location) to set (may be null).
     * @throws MalformedURLException
     */
    public void setSystemId( String systemId )
                            throws MalformedURLException {
        if ( systemId != null ) {
            this.systemId = new URL( systemId );
        }
    }

    /**
     * @param systemId
     *            The systemId (physical location) to set.
     */
    public void setSystemId( URL systemId ) {
        this.systemId = systemId;
    }

    /**
     * Returns whether the document has a schema reference.
     * 
     * @return true, if the document has a schema reference, false otherwise
     */
    public boolean hasSchema() {
        if ( this.rootElement.getAttribute( "xsi:schemaLocation" ) != null ) {
            return true;
        }
        return false;
    }

    /**
     * Determines the namespace <code>URI</code>s and the bound schema <code>URL</code>s from
     * the 'xsi:schemaLocation' attribute of the document element.
     * 
     * @return keys are URIs (namespaces), values are URLs (schema locations)
     * @throws XMLParsingException
     */
    public Map<URI, URL> getAttachedSchemas()
                            throws XMLParsingException {

        Map<URI, URL> schemaMap = new HashMap<URI, URL>();

        NamedNodeMap attrMap = rootElement.getAttributes();
        Node schemaLocationAttr = attrMap.getNamedItem( "xsi:schemaLocation" );
        if ( schemaLocationAttr == null ) {
            return schemaMap;
        }

        String target = schemaLocationAttr.getNodeValue();
        StringTokenizer tokenizer = new StringTokenizer( target );

        while ( tokenizer.hasMoreTokens() ) {
            URI nsURI = null;
            String token = tokenizer.nextToken();
            try {
                nsURI = new URI( token );
            } catch ( URISyntaxException e ) {
                String msg = "Invalid 'xsi:schemaLocation' attribute: namespace " + token
                             + "' is not a valid URI.";
                LOG.logError( msg );
                throw new XMLParsingException( msg );
            }

            URL schemaURL = null;
            try {
                token = tokenizer.nextToken();
                schemaURL = resolve( token );
            } catch ( NoSuchElementException e ) {
                String msg = "Invalid 'xsi:schemaLocation' attribute: namespace '" + nsURI
                             + "' is missing a schema URL.";
                LOG.logError( msg );
                throw new XMLParsingException( msg );
            } catch ( MalformedURLException ex ) {
                String msg = "Invalid 'xsi:schemaLocation' attribute: '" + token
                             + "' for namespace '" + nsURI + "' could not be parsed as URL.";
                throw new XMLParsingException( msg );
            }
            schemaMap.put( nsURI, schemaURL );
        }
        return schemaMap;
    }

    /**
     * Initializes the <code>XMLFragment</code> with the content from the given <code>URL</code>.
     * Sets the SystemId, too.
     * 
     * @param url
     * @throws IOException
     * @throws SAXException
     */
    public void load( URL url )
                            throws IOException, SAXException {
        String uri = url.toString();
        DocumentBuilder builder = XMLTools.getDocumentBuilder();
        Document doc = builder.parse( uri );
        setRootElement( doc.getDocumentElement() );
        setSystemId( uri );
    }

    /**
     * Initializes the <code>XMLFragment</code> with the content from the given
     * <code>InputStream</code>. Sets the SystemId, too.
     * 
     * @param istream
     * @param systemId
     *            cannot be null
     * @throws SAXException
     * @throws IOException
     * @throws XMLException
     * @throws NullPointerException
     */
    public void load( InputStream istream, String systemId )
                            throws SAXException, IOException, XMLException {
        InputStreamReader isr = new InputStreamReader( istream, CharsetUtils.getSystemCharset() );
        load( isr, systemId );
    }

    /**
     * Initializes the <code>XMLFragment</code> with the content from the given
     * <code>Reader</code>. Sets the SystemId, too.
     * 
     * @param reader
     * @param systemId
     *            can not be null
     * @throws SAXException
     * @throws IOException
     * @throws NullPointerException
     */
    public void load( Reader reader, String systemId )
                            throws SAXException, IOException {

        InputSource source = new InputSource( reader );
        if ( systemId == null ) {
            throw new NullPointerException( "'systemId' must not be null!" );
        }

        DocumentBuilder builder = XMLTools.getDocumentBuilder();
        Document doc = builder.parse( source );
        setRootElement( doc.getDocumentElement() );
    }

    /**
     * @param rootElement
     */
    public void setRootElement( Element rootElement ) {
        this.rootElement = rootElement;
    }

    /**
     * @return the element
     */
    public Element getRootElement() {
        return rootElement;
    }

    /**
     * Resolves the given URL (which may be relative) against the SystemID of the
     * <code>XMLFragment</code> into a <code>URL</code> (which is always absolute).
     * 
     * @param url
     * @return the resolved URL object
     * @throws MalformedURLException
     */
    public URL resolve( String url )
                            throws MalformedURLException {
        LOG.logDebug( StringTools.concat( 200, "Resolving URL '", url, "' against SystemID '",
                                          systemId, "' of XMLFragment" ) );
        // check if url is an absolut path
        File file = new File( url );
        if ( file.isAbsolute() ) {
            return file.toURL();
        }
        // remove leading '/' because otherwise
        // URL resolvedURL = new URL( systemId, url ); will fail
        if ( url.startsWith( "/" ) ) {
            url = url.substring( 1, url.length() );
            LOG.logInfo( "URL has been corrected by removing the leading '/'" );
        }
        URL resolvedURL = new URL( systemId, url );

        LOG.logDebug( StringTools.concat( 100, "-> resolvedURL: '", resolvedURL, "'" ) );
        return resolvedURL;
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>Writer</code> using the
     * default system encoding and adding CDATA-sections in for text-nodes where needed.
     * 
     * TODO: Add code for CDATA safety.
     * 
     * @param writer
     */
    public void write( Writer writer ) {
        Properties properties = new Properties();
        properties.setProperty( OutputKeys.ENCODING, CharsetUtils.getSystemCharset() );
        write( writer, properties );
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>Writer</code> using the
     * specified <code>OutputKeys</code>.
     * 
     * @param writer
     *            cannot be null
     * @param outputProperties
     *            output properties for the <code>Transformer</code> that is used to serialize the
     *            document
     * 
     * see javax.xml.OutputKeys
     */
    public void write( Writer writer, Properties outputProperties ) {
        try {
            Source source = new DOMSource( rootElement );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if ( outputProperties != null ) {
                transformer.setOutputProperties( outputProperties );
            }
            transformer.transform( source, new StreamResult( writer ) );
        } catch ( TransformerConfigurationException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        }
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>OutputStream</code> using
     * the default system encoding and adding CDATA-sections in for text-nodes where needed.
     * 
     * TODO: Add code for CDATA safety.
     * 
     * @param os
     */
    public void write( OutputStream os ) {
        Properties properties = new Properties();
        properties.setProperty( OutputKeys.ENCODING, CharsetUtils.getSystemCharset() );
        write( os, properties );
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>OutputStream</code> using
     * the specified <code>OutputKeys</code> which allow complete control of the generated output.
     * 
     * @param os
     *            cannot be null
     * @param outputProperties
     *            output properties for the <code>Transformer</code> used to serialize the
     *            document
     * 
     * @see javax.xml.transform.OutputKeys
     */
    public void write( OutputStream os, Properties outputProperties ) {
        try {
            Source source = new DOMSource( rootElement );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if ( outputProperties != null ) {
                transformer.setOutputProperties( outputProperties );
            }
            transformer.transform( source, new StreamResult( os ) );
        } catch ( TransformerConfigurationException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        }
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>OutputStream</code> using
     * indentation so it may be read easily.
     * 
     * @param os
     * @throws TransformerException
     */
    public void prettyPrint( OutputStream os )
                            throws TransformerException {
        PRETTY_PRINTER_XSLT.transform( this ).write( os );
    }

    /**
     * Writes the <code>XMLFragment</code> instance to the given <code>Writer</code> using
     * indentation so it may be read easily.
     * 
     * @param writer
     * @throws TransformerException
     */
    public void prettyPrint( Writer writer )
                            throws TransformerException {
        PRETTY_PRINTER_XSLT.transform( this ).write( writer );
    }

    /**
     * Parses the submitted <code>Element</code> as a <code>SimpleLink</code>.
     * <p>
     * Possible escaping of the attributes "xlink:href", "xlink:role" and "xlink:arcrole" is
     * performed automatically.
     * </p>
     * 
     * @param element
     * @return the object representation of the element
     * @throws XMLParsingException
     */
    protected SimpleLink parseSimpleLink( Element element )
                            throws XMLParsingException {

        URI href = null;
        URI role = null;
        URI arcrole = null;
        String title = null;
        String show = null;
        String actuate = null;

        String uriString = null;
        try {
            uriString = XMLTools.getNodeAsString( element, "@xlink:href", nsContext, null );
            if ( uriString != null ) {
                href = new URI( null, uriString, null );
            }
            uriString = XMLTools.getNodeAsString( element, "@xlink:role", nsContext, null );
            if ( uriString != null ) {
                role = new URI( null, uriString, null );
            }
            uriString = XMLTools.getNodeAsString( element, "@xlink:arcrole", nsContext, null );
            if ( uriString != null ) {
                arcrole = new URI( null, uriString, null );
            }
        } catch ( URISyntaxException e ) {
            throw new XMLParsingException( "'" + uriString + "' is not a valid URI." );
        }

        return new SimpleLink( href, role, arcrole, title, show, actuate );
    }

    /**
     * Parses the value of the submitted <code>Node</code> as a <code>QualifiedName</code>.
     * <p>
     * To parse the text contents of an <code>Element</code> node, the actual text node must be
     * given, not the <code>Element</code> node itself.
     * </p>
     * 
     * @param node
     * @return object representation of the element
     * @throws XMLParsingException
     */
    public static QualifiedName parseQualifiedName( Node node )
                            throws XMLParsingException {

        String name = node.getNodeValue().trim();
        QualifiedName qName = null;
        if ( name.indexOf( ':' ) > -1 ) {
            String[] tmp = StringTools.toArray( name, ":", false );
            try {
                qName = new QualifiedName( tmp[0], tmp[1], XMLTools.getNamespaceForPrefix( tmp[0],
                                                                                           node ) );
            } catch ( URISyntaxException e ) {
                throw new XMLParsingException( e.getMessage(), e );
            }
        } else {
            qName = new QualifiedName( name );
        }
        return qName;
    }

    /**
     * Returns the qualified name of the given element.
     * 
     * @param element
     * @return the qualified name of the given element.
     * @throws XMLParsingException
     */
    protected QualifiedName getQualifiedName( Element element )
                            throws XMLParsingException {

        // TODO check if we can use element.getNamespaceURI() instead
        URI nsURI = null;
        String prefix = element.getPrefix();
        try {
            nsURI = XMLTools.getNamespaceForPrefix( prefix, element );
        } catch ( URISyntaxException e ) {
            String msg = Messages.format( "ERROR_NSURI_NO_URI", element.getPrefix() );
            LOG.logError( msg, e );
            throw new XMLParsingException( msg, e );
        }
        QualifiedName ftName = new QualifiedName( prefix, element.getLocalName(), nsURI );

        return ftName;
    }

    /**
     * returns a string representation of the XML Document
     * 
     * @return the string
     */
    public String getAsString() {
        StringWriter writer = new StringWriter( 50000 );
        Source source = new DOMSource( rootElement );
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( source, new StreamResult( writer ) );
        } catch ( Exception e ) {
            LOG.logError( "Error serializing XMLFragment!", e );
        }
        return writer.toString();
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return getAsString();
    }
}

/***********************************************************************************************
 Changes to this class. What the people have been up to:
 $Log: XMLFragment.java,v $
 Revision 1.56  2006/10/31 16:51:42  mschneider
 Fixed encoding handling in #load(URL).

 Revision 1.55  2006/10/17 20:31:19  poth
 *** empty log message ***

 Revision 1.54  2006/09/05 17:41:24  mschneider
 Fixed warnings + footer.

 Revision 1.53  2006/09/05 11:51:21 schmitz
 Added a whitespace trim while parsing qualified names.

 Revision 1.52 2006/07/26 18:53:20 mschneider
 Changed info messages to debug.

 Revision 1.51 2006/07/12 14:46:16 poth
 comment footer added
 
 ***********************************************************************************************/
