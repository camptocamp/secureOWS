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

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.modules.actions.JLoginUser;
import org.apache.turbine.util.RunData;

/**
 * 
 * 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: plum $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/10/20 15:27:10 $
 *
 * @since 2.0
 */
public class IGeoJetspeed16LoginUser extends JLoginUser {

    @Override
    public void doPerform( RunData data )
                            throws Exception {
        super.doPerform( data );
        String user = data.getUser().getUserName(); 
        HttpSession ses = data.getRequest().getSession( false );
        if ( ses != null ) {
            LoginUser liu = new LoginUser();            
            File dir = liu.ensureDirectory( data.getServletContext(), user );
            liu.readContextDocuments( dir, ses );
        }

    }
    
    
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: IGeoJetspeed16LoginUser.java,v $
 Revision 1.3  2006/10/20 15:27:10  plum
 ap: bug fix after API change for automatic context loading

 Revision 1.2  2006/07/21 07:39:34  poth
 check for invalid user session added

 Revision 1.1  2006/07/12 14:14:35  poth
 initial checkin of Login listener for Jetspeed 1.6

 Revision 1.1  2006/07/10 20:13:34  poth
 *** empty log message ***


 ********************************************************************** */