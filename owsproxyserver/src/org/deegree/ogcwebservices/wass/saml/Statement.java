//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/saml/Statement.java,v 1.3 2006/06/19 12:47:09 schmitz Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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

package org.deegree.ogcwebservices.wass.saml;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import org.deegree.datatypes.QualifiedName;

/**
 * Encapsulated data: Statement elements
 * 
 * Namespace: http://urn:oasis:names:tc.SAML:1.0:assertion
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: schmitz $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/19 12:47:09 $
 * 
 * @since 2.0
 */
public class Statement {

    private Subject subject = null;

    private URI authenticationMethod = null;

    private Date authenticationInstant = null;

    private String ip = null;

    private String dns = null;

    private QualifiedName kind = null;

    private URI location = null;

    private URI binding = null;

    private ArrayList<String> actions = null;

    private ArrayList<URI> actionNamespaces = null;

    private ArrayList<Assertion> assertions = null;

    private String[] assertionIDs = null;

    private URI resource = null;

    private String decision = null;

    private ArrayList<String> attributeNames = null;

    private ArrayList<URI> attributeNamespaces = null;

    private ArrayList<String[]> attributeValues = null;

    /**
     * @param subject
     * @param authenticationMethod
     * @param authenticationInstant
     */
    public Statement( Subject subject, URI authenticationMethod, Date authenticationInstant ) {
        this.subject = subject;
        this.authenticationMethod = authenticationMethod;
        this.authenticationInstant = authenticationInstant;
    }

    /**
     * @param subject
     * @param actions
     * @param actionNamespaces
     * @param assertions
     * @param assertionIDs
     * @param resource
     * @param decision
     */
    public Statement( Subject subject, ArrayList<String> actions, ArrayList<URI> actionNamespaces,
                     ArrayList<Assertion> assertions, String[] assertionIDs, URI resource,
                     String decision ) {
        this.subject = subject;
        this.actions = actions;
        this.actionNamespaces = actionNamespaces;
        this.assertions = assertions;
        this.assertionIDs = assertionIDs;
        this.resource = resource;
        this.decision = decision;
    }

    /**
     * @param subject
     * @param attributeNames
     * @param attributeNamespaces
     * @param attributeValues
     */
    public Statement( Subject subject, ArrayList<String> attributeNames,
                     ArrayList<URI> attributeNamespaces, ArrayList<String[]> attributeValues ) {
        this.subject = subject;
        this.attributeNames = attributeNames;
        this.attributeNamespaces = attributeNamespaces;
        this.attributeValues = attributeValues;
    }

    /**
     * @return true, if the encapsulated data is an AuthenticationStatement
     */
    public boolean isAuthenticationStatement() {
        return ( authenticationMethod != null ) && ( authenticationInstant != null );
    }

    /**
     * @return true, if the encapsulated data is an AuthorizationDecisionStatement
     */
    public boolean isAuthorizationDecisionStatement() {
        return ( actions != null ) && ( actionNamespaces != null ) && ( assertions != null )
               && ( assertionIDs != null ) && ( resource != null ) && ( decision != null );
    }

    /**
     * @return true, if the encapsulated data is an AttributeStatement
     */
    public boolean isAttributeStatement() {
        return ( attributeNames != null ) && ( attributeNamespaces != null )
               && ( attributeValues != null );
    }

    /**
     * @param ip
     */
    public void setIP( String ip ) {
        this.ip = ip;
    }

    /**
     * @param dns
     */
    public void setDNS( String dns ) {
        this.dns = dns;
    }

    /**
     * @param kind
     * @param location
     * @param binding
     */
    public void setAuthorityBinding( QualifiedName kind, URI location, URI binding ) {
        this.kind = kind;
        this.location = location;
        this.binding = binding;
    }

    /**
     * @return Returns the actionNamespaces.
     */
    public ArrayList<URI> getActionNamespaces() {
        return actionNamespaces;
    }

    /**
     * @return Returns the actions.
     */
    public ArrayList<String> getActions() {
        return actions;
    }

    /**
     * @return Returns the assertionIDs.
     */
    public String[] getAssertionIDs() {
        return assertionIDs;
    }

    /**
     * @return Returns the assertions.
     */
    public ArrayList<Assertion> getAssertions() {
        return assertions;
    }

    /**
     * @return Returns the attributeNames.
     */
    public ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    /**
     * @return Returns the attributeNamespaces.
     */
    public ArrayList<URI> getAttributeNamespaces() {
        return attributeNamespaces;
    }

    /**
     * @return Returns the attributeValues.
     */
    public ArrayList<String[]> getAttributeValues() {
        return attributeValues;
    }

    /**
     * @return Returns the authenticationInstant.
     */
    public Date getAuthenticationInstant() {
        return authenticationInstant;
    }

    /**
     * @return Returns the authenticationMethod.
     */
    public URI getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * @return Returns the binding.
     */
    public URI getBinding() {
        return binding;
    }

    /**
     * @return Returns the decision.
     */
    public String getDecision() {
        return decision;
    }

    /**
     * @return Returns the dns.
     */
    public String getDns() {
        return dns;
    }

    /**
     * @return Returns the ip.
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return Returns the kind.
     */
    public QualifiedName getKind() {
        return kind;
    }

    /**
     * @return Returns the location.
     */
    public URI getLocation() {
        return location;
    }

    /**
     * @return Returns the resource.
     */
    public URI getResource() {
        return resource;
    }

    /**
     * @return Returns the subject.
     */
    public Subject getSubject() {
        return subject;
    }

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: Statement.java,v $
 * Changes to this class. What the people have been up to: Revision 1.3  2006/06/19 12:47:09  schmitz
 * Changes to this class. What the people have been up to: Updated the documentation, fixed the warnings and implemented logging everywhere.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.2  2006/05/29 16:24:59  bezema
 * Changes to this class. What the people have been up to: Rearranging the layout of the wss and creating the doservice classes. The WSService class is implemented as well
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58
 * bezema Refactored the security and authentication webservices into one package WASS (Web
 * Authentication -and- Security Services), also created a common package and a saml package which
 * could be updated to work in the future.
 * 
 * Revision 1.2 2006/05/15 12:39:31 bezema Completed parsing the SAML Assertions spec.
 * 
 * Revision 1.1 2006/05/15 09:54:16 bezema New approach to the nrw:gdi specs. Including ows_1_0 spec
 * and saml spec
 * 
 * 
 **************************************************************************************************/