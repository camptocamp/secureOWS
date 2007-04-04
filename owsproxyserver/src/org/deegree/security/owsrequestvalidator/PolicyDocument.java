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
package org.deegree.security.owsrequestvalidator;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.BaseURL;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.security.SecurityConfigurationException;
import org.deegree.security.owsproxy.AuthentificationSettings;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.DefaultDBConnection;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.RegistryConfig;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsproxy.SecurityConfig;
import org.deegree.security.owsproxy.UsersOperationParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.deegree.security.owsproxy.UsersOperationParameter.OpParameterRoleEntry;


/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.18 $, $Date: 2006/08/02 14:14:42 $
 * 
 * @since 1.1
 * 
 */
public class PolicyDocument {

    private ILogger LOG = LoggerFactory.getLogger( PolicyDocument.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private Document doc = null;

    private String service = null;

    /**
     * @param url
     * @throws SecurityConfigurationException
     */
    public PolicyDocument( URL url ) throws SecurityConfigurationException {
        try {
            Reader reader = new InputStreamReader( url.openStream() );
            doc = XMLTools.parse( reader );
            service = XMLTools.getRequiredAttrValue( "service", null, doc.getDocumentElement() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * @param doc
     *            document containing a policy document
     */
    public PolicyDocument( Document doc ) throws SecurityConfigurationException {
        this.doc = doc;
        try {
            service = XMLTools.getRequiredAttrValue( "service", null, doc.getDocumentElement() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
    }

    /**
     * returns the <tt>Policy</tt> created from the encapsulated DOM abject.
     * 
     * @return
     */
    public Policy getPolicy()
                            throws SecurityConfigurationException, XMLParsingException {
        Condition general = getGeneralCondition();
        SecurityConfig sc = null;
        List nl = XMLTools.getNodes( doc, "/dgsec:OWSPolicy/dgsec:Security", nsContext );
        if ( nl.size() > 0 ) {
            sc = getSecuityConfig();
        }
        Request[] requests = getRequests();
        return new Policy( sc, general, requests );
    }

    /**
     * @return Returns the generalCondition.
     */
    private Condition getGeneralCondition()
                            throws SecurityConfigurationException {
        Condition condition = null;
        try {
            OperationParameter[] op = new OperationParameter[4];
            String xpath = "/dgsec:OWSPolicy/dgsec:GeneralConditions/dgsec:Conditions/dgsec:Parameter[@name = 'getContentLength']";
            op[0] = getOperationParameter( "getContentLength", xpath );
            xpath = "/dgsec:OWSPolicy/dgsec:GeneralConditions/dgsec:Conditions/dgsec:Parameter[@name = 'postContentLength']";
            op[1] = getOperationParameter( "postContentLength", xpath );
            xpath = "/dgsec:OWSPolicy/dgsec:GeneralConditions/dgsec:Conditions/dgsec:Parameter[@name = 'httpHeader']";
            op[2] = getOperationParameter( "httpHeader", xpath );
            xpath = "/dgsec:OWSPolicy/dgsec:GeneralConditions/dgsec:Conditions/dgsec:Parameter[@name = 'requestMethod']";
            op[3] = getOperationParameter( "requestType", xpath );
            condition = new Condition( op );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
        return condition;
    }

    /**
     * @return Returns the secuityConfig.
     */
    private SecurityConfig getSecuityConfig()
                            throws SecurityConfigurationException {
        SecurityConfig securityConfig = null;
        try {
            String xpath = null;
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:RegistryClass";
            String regClass = XMLTools.getNodeAsString( doc, xpath, nsContext, 
                                                        "org.deegree.security.drm.SQLRegistry" );
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:ReadWriteTimeout";
            String tmp = XMLTools.getNodeAsString( doc, xpath, nsContext, "300" );
            int readWriteTimeout = Integer.parseInt( tmp );
            RegistryConfig registryConfig = getRegistryConfig();
            AuthentificationSettings authSet = getAuthentificationSettings();
            securityConfig = new SecurityConfig( regClass, readWriteTimeout, registryConfig,
                                                 authSet );

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }

        return securityConfig;
    }

    /**
     * returns the configuration of the used rights management registry
     * 
     * @return
     * @throws SecurityConfigurationException
     */
    private RegistryConfig getRegistryConfig()
                            throws SecurityConfigurationException {
        RegistryConfig registryConfig = null;
        try {
            String xpath = null;
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:RegistryConfig/dgjdbc:JDBCConnection/dgjdbc:Driver";
            String driver = XMLTools.getNodeAsString( doc, xpath, nsContext, null );
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:RegistryConfig/dgjdbc:JDBCConnection/dgjdbc:Url";
            String logon = XMLTools.getNodeAsString( doc, xpath, nsContext, null );
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:RegistryConfig/dgjdbc:JDBCConnection/dgjdbc:User";
            String user = XMLTools.getNodeAsString( doc, xpath, nsContext, null );
            xpath = "/dgsec:OWSPolicy/dgsec:Security/dgsec:RegistryConfig/dgjdbc:JDBCConnection/dgjdbc:Password";
            String password = XMLTools.getNodeAsString( doc, xpath, nsContext, null );
            if ( driver != null && logon != null ) {
                DefaultDBConnection con = new DefaultDBConnection( driver, logon, user, password );
                registryConfig = new RegistryConfig( con );
            } else if ( (driver != null && logon == null) || (driver == null && logon != null) ) {
                throw new SecurityConfigurationException( Messages.getString( "PolicyDocument.DatabaseConnection" ) );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
        return registryConfig;
    }

    /**
     * returns the settings for accessing the authentification authority
     * 
     * @return
     * @throws SecurityConfigurationException
     */
    private AuthentificationSettings getAuthentificationSettings()
                            throws SecurityConfigurationException {
        AuthentificationSettings authSet = null;

        try {
            StringBuffer xpath = new StringBuffer( "/dgsec:OWSPolicy/dgsec:Security/" );
            xpath.append( "dgsec:AuthentificationSettings/dgsec:AuthentificationService" );
            xpath.append( "/dgsec:OnlineResource/@xlink:href" );
            String onlineRes = XMLTools.getNodeAsString( doc, xpath.toString(), nsContext, null );
            if ( onlineRes != null ) {
                BaseURL baseURL = new BaseURL( null, new URL( onlineRes ) );
                authSet = new AuthentificationSettings( baseURL );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }

        return authSet;
    }

    /**
     * @return returns the requests described by the policy document
     */
    private Request[] getRequests()
                            throws SecurityConfigurationException {
        Request[] requests = null;
        try {
            List nl = XMLTools.getNodes( doc, "/dgsec:OWSPolicy/dgsec:Requests/*", nsContext );
            requests = new Request[nl.size()];
            for ( int i = 0; i < requests.length; i++ ) {
                requests[i] = getRequest( (Element) nl.get( i ) );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
        return requests;
    }

    /**
     * returns the requests described by the passed <tt>Element</tt>
     * 
     * @param element
     * @return created <tt>Request</tt>
     * @throws SecurityConfigurationException
     */
    private Request getRequest( Element element )
                            throws SecurityConfigurationException {
        String name = element.getLocalName();
        Request request = null;
        Condition preCon = null;
        Condition postCon = null;

        try {
            List nl = XMLTools.getNodes( element, "./dgsec:PreConditions/dgsec:Parameter",
                                         nsContext );
            OperationParameter[] op = new OperationParameter[nl.size()];
            for ( int i = 0; i < nl.size(); i++ ) {
                op[i] = getOperationParameter( (Element) nl.get( i ) );
            }
            preCon = new Condition( op );

            nl = XMLTools.getNodes( element, "./dgsec:PostConditions/dgsec:Parameter", nsContext );
            op = new OperationParameter[nl.size()];
            for ( int i = 0; i < nl.size(); i++ ) {
                op[i] = getOperationParameter( (Element) nl.get( i ) );
            }
            postCon = new Condition( op );
            request = new Request( service, name, preCon, postCon );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }

        return request;
    }

    /**
     * creates an <tt>OperationParameter</tt> with the passed name from the also passed root
     * XPath. A root XPath is an expression to the desired parameter node.
     * 
     * @param name
     *            name of the OperationParameter
     * @param xpathRoot
     * @return
     * @throws SecurityConfigurationException
     */
    private OperationParameter getOperationParameter( String name, String xpathRoot )
                            throws SecurityConfigurationException {
        OperationParameter op = null;
        try {
            if ( XMLTools.getNodes( doc, xpathRoot, nsContext ).size() == 0 ) {
                // return OperationParameter that denies any access
                return new OperationParameter( name, false );
            }
            // is parameter coupled to user specific rights
            String tmp = XMLTools.getRequiredNodeAsString( doc, xpathRoot + "/@userCoupled",
                                                           nsContext ).toLowerCase();
            boolean userCoupled = tmp.equals( "true" ) || tmp.equals( "1" );

            // is any? -> no restrictions
            tmp = XMLTools.getNodeAsString( doc, xpathRoot + "/dgsec:Any", nsContext, "false" );
            boolean any = !tmp.equals( "false" );

            if ( !any ) {
                // get values if not 'any'
                String[] values = XMLTools.getNodesAsStrings( doc, xpathRoot + "/dgsec:Value",
                                                              nsContext );
                op = new OperationParameter( name, values, userCoupled );
            } else {
                op = new OperationParameter( name, any );
            }

        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new SecurityConfigurationException( StringTools.stackTraceToString( e ) );
        }
        return op;
    }

    /**
     * creates an <tt>OperationParameter</tt> from the passed element.
     * 
     * @param element
     *            encapsulating a parameter
     * @return created <tt>OperationParameter</tt>
     * @throws XMLParsingException
     */
    // XXXsyp This is unused
    private OperationParameter getOperationParameterNested( Element element )
                            throws XMLParsingException {
        OperationParameter op = null;
        String name = XMLTools.getRequiredAttrValue( "name", null, element );
        String uc = XMLTools.getRequiredAttrValue( "userCoupled", null, element );
        boolean userCoupled = uc.equals( "true" ) || uc.equals( "1" );
        boolean any = XMLTools.getNode( element, "dgsec:Any", nsContext ) != null;
        if ( !any ) {
            List list = XMLTools.getNodes( element, "dgsec:Value", nsContext );
            String[] values = new String[list.size()];
            for ( int j = 0; j < values.length; j++ ) {
                values[j] = XMLTools.getStringValue( (Element) list.get( j ) );
            }

            op = new OperationParameter( name, values, userCoupled );
        } else {
            op = new OperationParameter( name, any );
        }

        return op;
    }
    
    /**
     * creates an <tt>OperationParameter</tt> from the passed element.
     * 
     * @param element
     *            encapsulating a parameter
     * @return created <tt>OperationParameter</tt>
     * @throws XMLParsingException
     */
    private OperationParameter getOperationParameter( Element element )
                            throws XMLParsingException {
        
        OperationParameter op = null;
        String name = XMLTools.getRequiredAttrValue( "name", null, element );
        String uc = XMLTools.getRequiredAttrValue( "userCoupled", null, element );
        boolean userCoupled = uc.equals( "true" ) || uc.equals( "1" );

        // ********

        //XMLTools.getNodes(contextNode, xPathQuery, nsContext)
        
        // XXXsyp rename role
        List roles = XMLTools.getNodes( element, "dgsec:Role", nsContext );
        if (roles.size() > 0) {
            System.out.println("has roles");
            
            //Map<String,OperationParameter> ops = new HashMap<String,OperationParameter>();
            
            List ops = new ArrayList();
            
            for ( int i = 0; i < roles.size(); i++ ) {
                Element el = (Element) roles.get( i );
                
                String roleName = el.getAttribute("name");
                
                OperationParameter _op = getOperationParameterInternal(el, name, userCoupled);
                /*
                List list = XMLTools.getNodes( el, "dgsec:Value", nsContext );
                String[] values = new String[list.size()];
                for ( int j = 0; j < values.length; j++ ) {
                    values[j] = XMLTools.getStringValue( (Element) list.get( j ) );
                }
                */
                System.out.println(_op);
                
                // XXX continue there
                /*
                OperationParameter uop;
                if (_op.isAny()) {
                    uop = new UsersOperationParameter( name, _op.isAny() );
                } else {
                    uop = new UsersOperationParameter( name, (String[])_op.getValues().toArray(new String[] {}), _op.isUserCoupled() );
                }
                return uop;
                */
                //ops.put(roleName, _op);
                // TODO: multiple roles
                ops.add(new OpParameterRoleEntry(new String[] {roleName}, _op));
            }
            return new UsersOperationParameter(name, ops, false);
            
        }
        // ********
        
        op = getOperationParameterInternal(element, name, userCoupled);

        return op;
    }

    private OperationParameter getOperationParameterInternal(Element element, String name, boolean userCoupled) throws XMLParsingException {
        OperationParameter op;
        boolean any = XMLTools.getNode( element, "dgsec:Any", nsContext ) != null;
        if ( !any ) {
            List list = XMLTools.getNodes( element, "dgsec:Value", nsContext );
            String[] values = new String[list.size()];
            for ( int j = 0; j < values.length; j++ ) {
                values[j] = XMLTools.getStringValue( (Element) list.get( j ) );
            }

            op = new OperationParameter( name, values, userCoupled );
        } else {
            op = new OperationParameter( name, any );
        }
        return op;
    }    

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PolicyDocument.java,v $
Revision 1.18  2006/08/02 14:14:42  poth
made JDBC connection for security manager optional

Revision 1.17  2006/05/25 09:53:30  poth
adapated to changed/simplified policy xml-schema


********************************************************************** */