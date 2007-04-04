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
package org.deegree.portal.portlet.modules.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.portal.context.ContextException;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.WebMapContextFactory;
import org.xml.sax.SAXException;

/**
 * This class can be used within listener classes that will be
 * called if a user logs in into a portal. It reads the context
 * documents from the users context directory that are storing
 * the state of the maps when the users has logged out the last
 * time. <BR>
 * At the moment a concrete listener is available for Jetspeed 
 * 1.6 @see org.deegree.portal.portlet.modules.actions.IGeoJetspeed16LoginUser 
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/23 14:54:23 $
 *
 * @since 2.0
 */
class LoginUser {
    
    /**
     * validates if a WMC directory for the passed user is already 
     * available and creates it if not. The user's WMC directory will
     * be returned as in instance of @see File 
     * @param user
     * @param data
     * @return
     */
    File ensureDirectory( ServletContext sc, String user ) {

        File dir = new File( sc.getRealPath( "WEB-INF/wmc/" + user ) );
        if ( !dir.exists() ) {
            dir.mkdir();
        }

        return dir;
    }

    
    /**
     * 
     * @param dir
     * @param user
     * @param ses
     * @throws MalformedURLException
     * @throws IOException
     * @throws XMLParsingException
     * @throws ContextException
     * @throws SAXException
     */
    void readContextDocuments( File dir, HttpSession ses ) {    	
        File[] files = dir.listFiles();
        // we have to look for all files stored in the user's WMC
        // directory and read those which can be identified as stored
        // when the has been logged out the last time. These files will
        // read and stored in the users session where the file name
        // ( without extension ) is the attributes key and will
        // be used by the portlets to access the assigned WMC           
        for ( int i = 0; i < files.length; i++ ) {
            String name = files[i].getName();
            if ( name.endsWith( AbstractPortletPerform.CURRENT_WMC + ".xml") ) {

                int pos = name.lastIndexOf( '.' );
                name = name.substring( 0, pos );
                ViewContext vc = null;
                try {
                	vc = WebMapContextFactory.createViewContext( files[i].toURL(), null, null );
				} catch (Exception e) {
					e.printStackTrace();
				}
                ses.setAttribute( name, vc );

            }
        }
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LoginUser.java,v $
Revision 1.8  2006/10/23 14:54:23  poth
never thrown exceptions removed

Revision 1.7  2006/10/20 15:45:09  plum
deleted systemout

Revision 1.6  2006/10/20 15:27:10  plum
ap: bug fix after API change for automatic context loading

Revision 1.5  2006/10/20 12:44:00  poth
*** empty log message ***

Revision 1.4  2006/10/12 15:46:19  poth
adaption required to avoid allocating larger amounts of memory for each user session when using several WMC documents

Revision 1.3  2006/08/24 12:15:52  poth
Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)

Revision 1.2  2006/07/12 14:33:27  poth
comment added

Revision 1.1  2006/07/12 14:15:27  poth
initial login of basis class for handle portal/portlet logins


********************************************************************** */