//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/servlet/SOAPFacadeServletFilter.java,v 1.8 2006/06/19 21:02:37 poth Exp $
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
package org.deegree.enterprise.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/06/19 21:02:37 $
 * 
 * @since 2.0
 */
public class SOAPFacadeServletFilter implements Filter {

    private static ILogger LOG = LoggerFactory.getLogger( SOAPFacadeServletFilter.class );

    private static String SOAPNS = "http://www.w3.org/2003/05/soap-envelope";

    private static NamespaceContext nsContext = new NamespaceContext();

    static {
        try {
            nsContext.addNamespace( "csw", CommonNamespaces.CSWNS );
            nsContext.addNamespace( "soap", new URI( SOAPNS ) );
        } catch ( URISyntaxException e ) {
            BootLogger.logError( "Error initializing namespace node.", e );
        }
    }

    /**
     * @param filterConfig
     */
    public void init( FilterConfig filterConfig )
                            throws ServletException {
        // nothing to do
    }

    /**
     * 
     */
    public void destroy() {
        // nothing to do
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
                            throws IOException, ServletException {
        LOG.entering();

        if ( ( (HttpServletRequest) request ).getMethod().equalsIgnoreCase( "GET" ) ) {
            chain.doFilter( request, response );
        } else {
            ServletRequestWrapper reqWrapper = 
                new ServletRequestWrapper( (HttpServletRequest) request );

            XMLFragment xml = new XMLFragment();
            try {
                xml.load( reqWrapper.getInputStream(), XMLFragment.DEFAULT_URL );
            } catch ( XMLException e ) {
                LOG.logError( "parsing request as XML", e );
                throw new ServletException( StringTools.stackTraceToString( e ) );
            } catch ( SAXException e ) {
                LOG.logError( "parsing request as XML", e );
                throw new ServletException( StringTools.stackTraceToString( e ) );
            }
            String s = xml.getRootElement().getNamespaceURI();
            // checking if the root elements node name equals the root name of
            // a SOAP message document. If so the SOAP body must be accessed
            // to be forwarded to the the filter/servlet
            if ( s.equals( SOAPNS ) ) {
                LOG.logDebug( "handle SOAP request" );
                try {
                    handleSOAPRequest( reqWrapper, (HttpServletResponse) response, chain, xml );
                } catch ( Exception e ) {
                    LOG.logError( "handling SOAP request", e );
                    throw new ServletException( StringTools.stackTraceToString( e ) );
                }
            } else {
                LOG.logDebug( "just forward request to next filter or servlet" );
                chain.doFilter( reqWrapper, response );
            }
        }

        LOG.exiting();
    }

    /**
     * handles a SOAP request. It is assumed that SOAP messaging has been used and the request to be
     * performed against a OWS is wrapped within the SOAPBody.
     * 
     * @param request
     * @param response
     * @param chain
     * @param xmlReq
     * @throws IOException
     * @throws ServletException
     * @throws SAXException
     * @throws XMLParsingException
     */
    private void handleSOAPRequest( HttpServletRequest request, HttpServletResponse response,
                                   FilterChain chain, XMLFragment xmlReq )
                            throws IOException, ServletException, SAXException, XMLParsingException {
        LOG.entering();
        
        XMLFragment sm = null;
        if ( hasMandatoryHeader( xmlReq ) ) {
            sm = handleMustUnderstandFault();
        } else {
            String s = "soap:Body/csw:GetRecords";
            Element elem = (Element) XMLTools.getRequiredNode( xmlReq.getRootElement(), s, nsContext );
    
            // extract SOAPBody and wrap it into a ServletWrapper
            XMLFragment xml = new XMLFragment( elem );
    
            ByteArrayOutputStream bos = new ByteArrayOutputStream( 50000 );
            xml.write( bos );
    
            ServletRequestWrapper forward = new ServletRequestWrapper( request );
            forward.setInputStreamAsByteArray( bos.toByteArray() );
            bos.close();
            ServletResponseWrapper resWrapper = new ServletResponseWrapper( response );
            chain.doFilter( forward, resWrapper );
            
            OutputStream os = resWrapper.getOutputStream();
            byte[] b = ( (ServletResponseWrapper.ProxyServletOutputStream) os ).toByteArray();
            os.close();
            
            sm = createResponseMessage( b );
        }

        // write into stream to calling client
        OutputStream os = response.getOutputStream();
        sm.write( os );
        os.close();

        LOG.exiting();
    }

    private XMLFragment handleMustUnderstandFault() throws SAXException, IOException {
        String s  = StringTools.concat( 300, "<?xml version='1.0' encoding='UTF-8'?>",
                "<soapenv:Envelope xmlns:soapenv='" + SOAPNS + "'><soapenv:Body>",
                        "<soapenv:Fault><soapenv:Code><soapenv:Value>soapenv:MustUnderstand",
                        "</soapenv:Value></soapenv:Code><soapenv:Reason><soapenv:Text ",
                        "xml:lang='en'>One or more mandatory SOAP header blocks not ",
                        "understood</soapenv:Text></soapenv:Reason></soapenv:Fault>",
                        "</soapenv:Body></soapenv:Envelope>" );
        StringReader sr = new StringReader( s );
        return new XMLFragment( sr, XMLFragment.DEFAULT_URL );
    }

    /**
     * returns true if the passed SOAP meassage contains a header that must
     * be understood by a handling node
     * 
     * @param xmlReq
     * @return
     * @throws XMLParsingException 
     */
    private boolean hasMandatoryHeader( XMLFragment xmlReq ) throws XMLParsingException {
        List list = XMLTools.getNodes(xmlReq.getRootElement(), "soap:Header", nsContext );
        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            Element element = (Element) iter.next();
            NodeList nl = element.getChildNodes();
            for ( int i = 0; i < nl.getLength(); i++ ) {
                if ( nl.item( i ) instanceof Element ) {
                    Element el = (Element)nl.item( i ); 
                    if ( XMLTools.getNode( el, "@soap:mustUnderstand", nsContext ) != null ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     private Map collectNamespaces( Element node, Map xmlns ) {

     NamedNodeMap nnm = node.getAttributes();
     for (int i = 0; i < nnm.getLength(); i++) {
     Node nd = nnm.item( i );
     if ( node.getNodeName().startsWith( "xmlns" ) ) {
     xmlns.put( node.getNodeName(), nd.getNodeValue() );
     }
     }
     NodeList nl = node.getChildNodes();
     for (int i = 0; i < nl.getLength(); i++) {
     if ( nl.item( i ) instanceof Element ) {
     xmlns = collectNamespaces( (Element) nl.item( i ), xmlns );
     }
     }

     return xmlns;
     }
     */

    /**
     * creates a SOAP message where the response of a OWS call is wrapped whithin the SOAPBody
     * 
     * @param b
     *            response to embed into the body of the SOAP response message
     * @return SOAP response message
     * @throws SOAPException
     */
    private XMLFragment createResponseMessage( byte[] b )
                            throws IOException, SAXException {
        LOG.entering();

        XMLFragment xml = new XMLFragment();
        xml.load( new ByteArrayInputStream( b ), XMLFragment.DEFAULT_URL );

        String s = StringTools.concat( 200, "<?xml version='1.0' encoding='UTF-8'?>",
                                       "<soapenv:Envelope xmlns:soapenv='", SOAPNS,
                                       "'><soapenv:Body></soapenv:Body></soapenv:Envelope>" );
        StringReader sr = new StringReader( s );
        XMLFragment message = new XMLFragment( sr, XMLFragment.DEFAULT_URL );
        XMLTools.insertNodeInto( xml.getRootElement(), message.getRootElement().getFirstChild() );

        /*
         can not be used for CSW because CSW DE-profile requires SOAP 1.2
         MessageFactory factory = MessageFactory.newInstance();
         SOAPMessage message = factory.createMessage();
         message.getSOAPBody().addDocument( doc );
         */

        LOG.exiting();
        return message;
    }

}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: SOAPFacadeServletFilter.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/19 21:02:37  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.7  2006/04/06 20:25:23  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.6  2006/03/30 21:20:24  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.5  2006/01/25 10:09:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2005/11/17 08:18:35  deshmukh
 * Renamed nsNode to nsContext
 * Changes to this class. What the people have been up to:
 * Revision 1.3  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Revision 1.2 2005/11/14 12:47:56 poth no message
 * 
 * Revision 1.1 2005/10/03 12:55:39 poth no message
 * 
 * 
 ************************************************************************************************* */
