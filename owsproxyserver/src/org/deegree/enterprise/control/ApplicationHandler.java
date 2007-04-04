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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.enterprise.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;

import org.deegree.datatypes.parameter.GeneralOperationParameterIm;
import org.deegree.datatypes.parameter.ParameterValueIm;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralOperationParameter;
import org.opengis.parameter.ParameterValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handler for all web events.
 *
 * @author  <a href="mailto:tfriebe@gmx.net">Torsten Friebe</a>
 * @author  <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 *
 * @version $Revision: 1.16 $
 *
 */
public class ApplicationHandler implements WebListener {

    private static final ILogger LOG = LoggerFactory.getLogger( ApplicationHandler.class );

    private static final HashMap handler = new HashMap();

    private static final HashMap handlerNext = new HashMap();

    private static final HashMap handlerANext = new HashMap();

    private static final HashMap handlerParam = new HashMap();

    private static final String EVENT = "event";

    private static final String NAME = "name";

    private static final String CLASS = "class";

    private static final String NEXT = "next";

    private static final String ALTERNATIVENEXT = "alternativeNext";

    /**
     * Creates a new ApplicationHandler object.
     *
     * @param configFile 
     */
    public ApplicationHandler( String configFile ) throws Exception {
        ApplicationHandler.initHandler( configFile );
    }

    /**
     * Handles all web action events. Calls the specified listener using
     * the mapping defined in control.xml file.
     *
     * @param e   the action event generated out of the incoming http POST event.
     */
    public void actionPerformed( FormEvent e ) {
        Object source = e.getSource();

        if ( source instanceof ServletRequest ) {
            ServletRequest request = (ServletRequest) source;

            String actionName = request.getParameter( "action" );

            if ( actionName != null ) {
                // handle simple KVP encoded request
                try {
                    if ( "version".equalsIgnoreCase( actionName ) ) {
                        this.showVersion( request );
                    } else {
                        try {
                            this.delegateToHelper( actionName, e );
                        } catch ( Exception ex ) {
                            ex.printStackTrace();
                            LOG.logError( "Action " + actionName + " is unknown!" );
                        }
                    }
                } catch ( Exception ex ) {
                    request.setAttribute( "next", "error.jsp" );
                    request.setAttribute( "javax.servlet.jsp.jspException", ex );
                }
            } else {
                // handle RPC encoded request
                try {
                    RPCMethodCall mc = getMethodCall( request );
                    e = new RPCWebEvent( e, mc );
                    this.delegateToHelper( mc.getMethodName(), e );
                } catch ( RPCException re ) {
                    re.printStackTrace();
                    request.setAttribute( "next", "error.jsp" );
                    request.setAttribute( "javax.servlet.jsp.jspException", re );
                } catch ( Exception ee ) {
                    ee.printStackTrace();
                    request.setAttribute( "next", "error.jsp" );
                    request.setAttribute( "javax.servlet.jsp.jspException", ee );
                }
            }
        }
    }

    /**
     * extracts the RPC method call from the 
     */
    private RPCMethodCall getMethodCall( ServletRequest request )
                            throws RPCException {

        String s = request.getParameter( "rpc" );
        LOG.logDebug( "RPC", s );
        try {
            if ( s == null ) {
                StringBuffer sb = new StringBuffer( 1000 );
                try {
                    BufferedReader br = request.getReader();
                    String line = null;
                    while ( ( line = br.readLine() ) != null ) {
                        sb.append( line );
                    }
                    br.close();
                } catch ( Exception e ) {
                    throw new RPCException( "Error reading stream from servlet\n" + e.toString() );
                }

                s = sb.toString();
                s = URLDecoder.decode( s, CharsetUtils.getSystemCharset() );
                int pos1 = s.indexOf( "<methodCall>" );
                int pos2 = s.indexOf( "</methodCall>" );
                if ( pos1 < 0 ) {
                    throw new RPCException( "request doesn't contain a RPC methodCall" );
                }
                s = s.substring( pos1, pos2 + 13 );
            } else {
                s = URLDecoder.decode( s, CharsetUtils.getSystemCharset() );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new RPCException( e.toString() );
        }

        StringReader reader = new StringReader( s );
        RPCMethodCall mc = RPCFactory.createRPCMethodCall( reader );

        return mc;
    }

    /**
     *
     *
     * @param action 
     * @param e 
     *
     * @throws Exception 
     */
    protected void delegateToHelper( String action, FormEvent e )
                            throws Exception {
        action = action.trim();

        LOG.logInfo( "action: " + action );
        Class cls = (Class) ApplicationHandler.handler.get( action );
        AbstractListener helper = (AbstractListener) cls.newInstance();
        helper.setNextPage( (String) handlerNext.get( action ) );
        helper.setDefaultNextPage( (String) handlerNext.get( action ) );
        helper.setAlternativeNextPage( (String) handlerANext.get( action ) );
        helper.setInitParameterList( (List) handlerParam.get( action ) );
        helper.handle( e );
    }

    /**
     *
     *
     * @param request 
     */
    protected void showVersion( ServletRequest request ) {
        request.setAttribute( "next", "snoopy.jsp" );
    }

    /**
     *
     *
     * @param configFile 
     *
     * @throws IOException 
     * @throws SAXException 
     */
    private static void initHandler( String configFile )
                            throws IOException, MalformedURLException, SAXException {
        LOG.logInfo( "Reading event handler configuration file:" + configFile );
        /*
         * Read resource into Document...
         */
        URL url = new File( configFile ).toURL();
        Reader reader = new InputStreamReader( url.openStream() );
        Document doc = XMLTools.parse( reader );
        /*
         * Read and create page elements
         */
        NodeList nodes = doc.getElementsByTagName( EVENT );

        for ( int i = 0; i < nodes.getLength(); i++ ) {
            String name = XMLTools.getAttrValue( nodes.item( i ), NAME );
            String cls = XMLTools.getAttrValue( nodes.item( i ), CLASS );
            String nextPage = XMLTools.getAttrValue( nodes.item( i ), NEXT );
            String anextPage = XMLTools.getAttrValue( nodes.item( i ), ALTERNATIVENEXT );

            if ( anextPage == null ) {
                anextPage = nextPage;
            }

            Class clscls = null;
            try {
                clscls = Class.forName( cls );
                handler.put( name.trim(), clscls );
                handlerNext.put( name.trim(), nextPage );
                handlerANext.put( name.trim(), anextPage );
                List<ParameterValue> pvList = parseParameters( nodes.item( i ) );
                handlerParam.put( name.trim(), pvList );
                LOG.logInfo( "Handler '" + clscls + "' bound to event '" + name + "'" );
            } catch ( Exception ex ) {
                ex.printStackTrace();
                LOG.logError( "No handler '" + cls + "' specified for event '" + name + "'", ex );
                throw new SAXException( "No handler class specified for event:" + name + " " + cls
                                        + "\n" + ex );
            }
        }
    }

    /**
     * several para,eters can be passed to each Listener by adding 
     * <pre>
     *   <parameter>
     *       <name>aName</name>
     *       <value>aValue</value>
     *   </parameter>
     * </pre>
     * sections to the corresponding &lt;event&gt; element. 
     * @param node
     * @return
     * @throws XMLParsingException
     */
    private static List<ParameterValue> parseParameters( Node node )
                            throws XMLParsingException {

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        List nodes = XMLTools.getNodes( node, "parameter", nsc );
        List<ParameterValue> pvs = new ArrayList<ParameterValue>();
        for ( int i = 0; i < nodes.size(); i++ ) {
            Element element = (Element) nodes.get( i );
            String name = XMLTools.getRequiredNodeAsString( element, "name", nsc );
            String value = XMLTools.getRequiredNodeAsString( element, "value", nsc );
            GeneralOperationParameter descriptor = new GeneralOperationParameterIm(
                                                                                    new Identifier[0],
                                                                                    name, null, 1,
                                                                                    1 );
            pvs.add( new ParameterValueIm( descriptor, value ) );
        }

        return pvs;
    }

}

/*
 * Changes to this class. What the people haven been up to:
 *
 * $Log: ApplicationHandler.java,v $
 * Revision 1.16  2006/10/17 20:31:19  poth
 * *** empty log message ***
 *
 * Revision 1.15  2006/07/25 14:15:51  poth
 * stupid logging code remove; useless Debug references removed; code formatting
 *
 * Revision 1.14  2006/07/22 15:15:29  poth
 * bug fix - URI decoding
 *
 * Revision 1.13  2006/05/22 19:04:46  poth
 * *** empty log message ***
 *
 * Revision 1.12  2006/05/16 15:09:42  poth
 * add support of using init parameter for action listeners
 *
 * Revision 1.11  2006/05/15 18:21:31  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/05/15 06:55:37  poth
 * *** empty log message ***
 *
 * Revision 1.9  2006/04/06 20:25:30  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/04/04 20:39:44  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/02/05 20:33:09  poth
 * *** empty log message ***
 *
 * Revision 1.5  2005/12/06 13:45:19  poth
 * System.out.println substituted by logging api
 *
 * Revision 1.4  2005/03/09 11:55:47  mschneider
 * *** empty log message ***
 *
 * Revision 1.3  2005/02/25 11:19:16  poth
 * no message
 *
 * Revision 1.2  2005/02/11 10:44:29  friebe
 * fit for java 1.5
 *
 * Revision 1.1.1.1  2005/01/05 10:38:58  poth
 * no message
 *
 * Revision 1.1  2004/06/08 13:03:20  tf
 * refactor to org.deegree.enterprise
 *
 * Revision 1.1  2004/05/24 07:00:03  ap
 * no message
 *
 * Revision 1.13  2004/04/27 06:40:43  poth
 * no message
 *
 * Revision 1.12  2004/02/27 15:56:58  mrsnyder
 * Removed hard-coded catalog name.
 *
 * Revision 1.11  2004/02/19 10:08:56  poth
 * no message
 *
 * Revision 1.10  2004/01/03 13:46:45  poth
 * no message
 *
 * Revision 1.9  2003/12/31 16:13:59  poth
 * no message
 *
 * Revision 1.8  2003/12/12 16:48:24  poth
 * no message
 *
 * Revision 1.7  2003/11/28 11:35:56  poth
 * no message
 *
 * Revision 1.6  2003/11/16 10:59:37  poth
 * no message
 *
 * Revision 1.5  2003/11/14 08:22:26  poth
 * no message
 *
 * Revision 1.4  2003/11/10 07:57:38  poth
 * no message
 *
 * Revision 1.3  2003/10/31 16:11:45  poth
 * no message
 *
 * Revision 1.2  2003/10/30 16:59:04  poth
 * no message
 *
 * Revision 1.1  2003/07/11 12:47:10  poth
 * no message
 *
 * Revision 1.1.1.1  2002/03/22 15:23:10  ap
 * no message
 *
 */
