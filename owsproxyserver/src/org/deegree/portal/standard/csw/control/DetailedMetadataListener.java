//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/DetailedMetadataListener.java,v 1.12 2006/07/31 11:02:44 mays Exp $
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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.portal.standard.csw.control;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;

/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/07/31 11:02:44 $
 * 
 * @since 2.0
 */
public class DetailedMetadataListener extends OverviewMetadataListener {
// extends OverviewMetadataListener --> SimpleSearchListener --> AbstractListener
    
    private static final ILogger LOG = LoggerFactory.getLogger( DetailedMetadataListener.class );
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering();
        
        // get Metadata from the users sesssion
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        config = (CSWClientConfiguration)session.getAttribute( Constants.CSW_CLIENT_CONFIGURATION );
        
        nsContext = CommonNamespaces.getNamespaceContext();
        
//        try {
//            nsNode = XMLTools.getNamespaceNode( config.getNamespaceBindings() );
//        } catch( ParserConfigurationException e ) {
//            e.printStackTrace();
//            gotoErrorPage( "Could not create namespace node for SimpleSearchListener." 
//                           + e.getMessage() );
//            LOG.exiting();
//            return;
//        }

        // get transformation file name
        String fileName = "metaDetails2html.xsl"; // default value
        // FIXME replace format with current value
        String format = "Profiles.ISO19115";
        HashMap xslMap = config.getProfileXSL( format );
        if ( xslMap != null ) {
            if ( xslMap.get( "full" ) != null ) {
                fileName = (String)xslMap.get( "full" );
            }
        }
        String pathToXslFile = "file:" + getHomePath() + "WEB-INF/conf/igeoportal/"+ fileName; 
        
        Object o = session.getAttribute( SESSION_METADATA );
        if ( o != null ) {
            try {
                handleResult( o, pathToXslFile, "detailed" );
            } catch ( Exception e ) {
                e.printStackTrace();
                gotoErrorPage( "Error handling result: \n" + e.getMessage() );
                LOG.exiting();
                return;
            }
        } else {
            // create error message if no metadata object is contained in the users session
            setNextPage( "error.jsp" );
            try {
                getRequest().setAttribute( Constants.MESSAGE, 
                                           "detailed search: no metadata object in user session." );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        LOG.exiting();
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DetailedMetadataListener.java,v $
Revision 1.12  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.11  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.10  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
