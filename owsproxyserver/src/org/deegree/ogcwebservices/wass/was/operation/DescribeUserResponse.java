//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/operation/DescribeUserResponse.java,v 1.5 2006/10/11 11:23:42 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wass.was.operation;

import java.io.IOException;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.security.drm.model.User;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * <code>DescribeUserResponse</code> is an XML document class used as response object
 * for the DescribeUser operation.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/10/11 11:23:42 $
 * 
 * @since 2.0
 */

public class DescribeUserResponse extends XMLFragment {

    private static final long serialVersionUID = -4883638653028678703L;

    protected static final String XML_TEMPLATE = "describeusertemplate.xml";
    
    /**
     * Constructs a new response document.
     * 
     * @param user the user object to extract the response values from.
     * @param sessionID the user's session ID
     * @throws IOException 
     * @throws SAXException 
     * @throws XMLParsingException 
     */
    public DescribeUserResponse( User user, String sessionID ) throws IOException, SAXException,
                                                  XMLParsingException {
        super( DescribeUserResponse.class.getResource( XML_TEMPLATE ) );
        
        NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();
        Element root = getRootElement();
        root.setAttribute( "id", sessionID );

        Element userName = (Element) XMLTools.getRequiredNode( root, "UserName", nsContext );
        Element firstName = (Element) XMLTools.getRequiredNode( root, "FirstName", nsContext );
        Element lastName = (Element) XMLTools.getRequiredNode( root, "LastName", nsContext );
        Element password = (Element) XMLTools.getRequiredNode( root, "Password", nsContext );
        Element email = (Element) XMLTools.getRequiredNode( root, "EMailAddress", nsContext );
        
        userName.setTextContent( user.getName() );
        firstName.setTextContent( user.getFirstName() );
        lastName.setTextContent( user.getLastName() );
        password.setTextContent( user.getPassword() );
        email.setTextContent( user.getEmailAddress() );
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeUserResponse.java,v $
Revision 1.5  2006/10/11 11:23:42  poth
first name and last name added to user description instead of name

Revision 1.4  2006/09/18 10:57:13  poth
bug fix - creating userName from DescribeUser response

Revision 1.3  2006/09/12 14:51:15  poth
bug fix - response XML for DescribeUser

Revision 1.2  2006/08/24 06:42:17  poth
File header corrected

Revision 1.1  2006/08/11 08:58:50  schmitz
WAS implements the DescribeUser operation.



********************************************************************** */