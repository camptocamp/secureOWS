//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/DownloadListener.java,v 1.12 2006/07/31 09:33:58 mays Exp $
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

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;


/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/07/31 09:33:58 $
 * 
 * @since 2.0
 */
public class DownloadListener extends AbstractListener {

    private static final ILogger LOG = LoggerFactory.getLogger( DownloadListener.class );
    
    public void actionPerformed( FormEvent event ) {
        // TODO Auto-generated method stub
        LOG.entering();

        getRequest().setAttribute( Constants.MESSAGE, "DownloadListener: Not implemented yet." );
        
        LOG.exiting();
        return;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DownloadListener.java,v $
Revision 1.12  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.11  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
