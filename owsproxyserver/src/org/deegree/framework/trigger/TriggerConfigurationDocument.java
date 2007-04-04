//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/trigger/TriggerConfigurationDocument.java,v 1.7 2006/10/10 11:25:11 poth Exp $
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
package org.deegree.framework.trigger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.i18n.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/10 11:25:11 $
 *
 * @since 2.0
 */
public class TriggerConfigurationDocument extends XMLFragment {

    private ILogger LOG = LoggerFactory.getLogger( TriggerConfigurationDocument.class );

    private static NamespaceContext nsc = new NamespaceContext();
    static {
        try {
            nsc.addNamespace( "dgTr", new URI( "http://www.deegree.org/trigger" ) );
        } catch ( URISyntaxException e ) {
            // should never happen
            e.printStackTrace();
        }
    }
    
    
    /**
     * default constructor
     */
    public TriggerConfigurationDocument() {   
        
    }

    /**
     * initialized the class by assigning a XML file
     * @param file
     * @throws IOException
     * @throws SAXException
     */
    public TriggerConfigurationDocument( File file ) throws IOException, SAXException {
        super( file.toURL() );        
    }

    /**
     * 
     * @return
     * @throws XMLParsingException 
     * @throws TriggerException 
     */
    public TriggerCapabilities parseTriggerCapabilities()
                            throws XMLParsingException, TriggerException {

        List list = XMLTools.getNodes( getRootElement(), "dgTr:class", nsc );
        Map<String,TargetClass> targetClasses = new HashMap<String,TargetClass>( list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            TargetClass tc = parserTargetClass( (Element) list.get( i ) );
            targetClasses.put( tc.getName(), tc );
        }

        return new TriggerCapabilities( targetClasses );
    }

    /**
     * 
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws TriggerException 
     */
    private TargetClass parserTargetClass( Element element )
                            throws XMLParsingException, TriggerException {

        String clName = XMLTools.getRequiredNodeAsString( element, "dgTr:name/text()", nsc );

        List list = XMLTools.getNodes( element, "dgTr:method", nsc );
        Map<String,TargetMethod> targetMethods = new HashMap<String,TargetMethod>( list.size() );
        for ( int i = 0; i < list.size(); i++ ) {            
            TargetMethod tm = parseTargetMethod( (Element) list.get( i ) );
            targetMethods.put( tm.getName(), tm );
        }

        return new TargetClass( clName, targetMethods );
    }

    /**
     * 
     * @param element
     * @return
     * @throws XMLParsingException 
     * @throws TriggerException 
     */
    private TargetMethod parseTargetMethod( Element element )
                            throws XMLParsingException, TriggerException {

        String mName = XMLTools.getRequiredNodeAsString( element, "dgTr:name/text()", nsc );

        TriggerCapability preTrigger = null;
        TriggerCapability postTrigger = null;

        //it is possible that no trigger is assigned to a method
        // in this case the present of a method just indicates that
        // it that Triggers can be assigned to it
        Node node = XMLTools.getNode( element, "dgTr:preTrigger/dgTr:trigger", nsc );
        if ( node != null ) {
            preTrigger = parseTriggerCapability( (Element) node );
        }
        node = XMLTools.getNode( element, "dgTr:postTrigger/dgTr:trigger", nsc );
        if ( node != null ) {
            postTrigger = parseTriggerCapability( (Element) node );
        }

        return new TargetMethod( mName, preTrigger, postTrigger );
    }

    /**
     * 
     * @param element
     * @return
     * @throws XMLParsingException 
     * @throws TriggerException 
     */
    private TriggerCapability parseTriggerCapability( Element element )
                            throws XMLParsingException, TriggerException {
        
        TriggerCapability tc = null;
        
        // a node (if not null) may represents a simple Trigger or a 
        // TriggerChain (which is a Trigger too)    
        String trName = XMLTools.getRequiredNodeAsString( element, "dgTr:name/text()", nsc );
        String clName = XMLTools.getRequiredNodeAsString( element, "dgTr:performingClass/text()", nsc );
        Class clss = null;
        try {
            clss = Class.forName( clName );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException( Messages.getMessage( "FRAMEWORK_UNKNOWN_TRIGGERCLASS",
                                                                clName ) );
        }
        
        if ( !Trigger.class.isAssignableFrom( clss ) ) {
            // class read from the configuration must be an implementation
            // of org.deegree.framework.trigger.Trigger
            throw new TriggerException( Messages.getMessage( "FRAMEWORK_INVALID_TRIGGERCLASS",
                                                             clName ) );
        }
        
        Map<String, Class> paramTypes = new HashMap<String, Class>();
        Map<String, Object> paramValues = new HashMap<String, Object>();
        List<String> paramNames = new ArrayList<String>();
        List initParams = XMLTools.getNodes( element, "dgTr:initParam", nsc );
        parseInitParams( paramTypes, paramValues, paramNames, initParams );
        
        // get nested Trigger capabilities if available
        List nested = XMLTools.getNodes( element, "dgTr:trigger/dgTr:trigger", nsc );
        List<TriggerCapability> nestedList = new ArrayList<TriggerCapability>( nested.size() );
        for ( int i = 0; i < nested.size(); i++ ) {
            nestedList.add( parseTriggerCapability( (Element)nested.get( i ) ) );
        }
        
        tc = new TriggerCapability( trName, clss, paramNames, paramTypes, paramValues, nested );
    

        return tc;
    }

    /**
     * 
     * @param paramTypes
     * @param paramValues
     * @param paramNames
     * @param initParams
     * @throws XMLParsingException
     */
    private void parseInitParams( Map<String, Class> paramTypes, Map<String, Object> paramValues, 
                                  List<String> paramNames, List initParams )
                            throws XMLParsingException {
        for ( int i = 0; i < initParams.size(); i++ ) {
            String name = XMLTools.getRequiredNodeAsString( (Node)initParams.get( i ),
                                                            "dgTr:name/text()", nsc );
            paramNames.add( name );
            String tmp = XMLTools.getRequiredNodeAsString( (Node)initParams.get( i ),
                                                            "dgTr:type/text()", nsc );
            Class cl = null;
            try {
                cl = Class.forName( tmp );
            } catch ( ClassNotFoundException e ) {
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( Messages.getMessage( "FRAMEWORK_UNKNOWN_INITPARAMCLASS",
                                                                    tmp ) );
            } 
            tmp = XMLTools.getRequiredNodeAsString( (Node)initParams.get( i ),
                                                    "dgTr:value/text()", nsc );
            Object value = null;
            try {
                value = getValue( cl, tmp );
            } catch ( MalformedURLException e ) {               
                LOG.logError( e.getMessage(), e );
                throw new XMLParsingException( Messages.getMessage( "FRAMEWORK_TRIGGER_INITPARAM_PARSING",
                                                                    tmp, cl.getName() ) );
            }
            paramTypes.put( name, cl );
            paramValues.put( name, value );
        }
    }

    /**
     * 
     * @param type
     * @param tmp
     * @return
     * @throws MalformedURLException
     */
    private Object getValue( Class type, String tmp ) throws MalformedURLException {
        Object value = null;        
        if ( type.equals( Integer.class ) ) {
            value = Integer.parseInt( tmp );
        } else if ( type.equals( Double.class ) ) {
            value = Double.parseDouble( tmp );
        } else  if ( type.equals( Float.class ) ) {
            value = Float.parseFloat( tmp );
        } else if ( type.equals( URL.class ) ) {
            value = new URL( tmp );
        } else if ( type.equals( Date.class ) ) {
            value = TimeTools.createCalendar( tmp ).getTime();
        } else if ( type.equals( String.class ) ) {
            value = tmp;
        }  
        return value;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TriggerConfigurationDocument.java,v $
 Revision 1.7  2006/10/10 11:25:11  poth
 bug fix - reading init params

 Revision 1.6  2006/09/23 09:02:39  poth
 first working implementation

 Revision 1.5  2006/09/22 15:04:58  poth
 ongoing implementation

 Revision 1.4  2006/09/22 13:25:02  poth
 ongoing implementation

 Revision 1.3  2006/09/20 07:12:11  poth
 ongoing implementation

 Revision 1.2  2006/09/19 20:11:02  poth
 ongoing implementation

 Revision 1.1  2006/09/14 16:06:05  poth
 initial check in


 ********************************************************************** */