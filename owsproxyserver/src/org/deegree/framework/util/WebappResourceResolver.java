// $Id$
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

package org.deegree.framework.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.deegree.framework.log.ILogger;

public class WebappResourceResolver {

    /**
     * 'Heuristical' method to retrieve the <code>URL</code> for a file referenced from an
     * init-param of a webapp config file which may be:
     * <ul>
     * <li>a (absolute) <code>URL</code></li>
     * <li>a file location</li>
     * <li>a (relative) URL which in turn is resolved using <code>ServletContext.getRealPath</code>
     * </li>
     * </ul>
     * 
     * @param location
     * @param context
     * @param log the log with which erros will be logged. cannot be null
     * @return
     * @throws MalformedURLException
     */
    public static URL resolveFileLocation( String location, ServletContext context, ILogger log )
        throws MalformedURLException {
        URL serviceConfigurationURL = null;

        log.logDebug( "Resolving configuration file location: '"
            + location + "'..." );
        try {
            serviceConfigurationURL = new URL( location );
        } catch (MalformedURLException e) {
            log.logDebug( "No valid (absolute) URL. Trying context.getRealPath() now." );
            String realPath = context.getRealPath( location );
            if ( realPath == null ) {
                log.logDebug( "No 'real path' available. Trying to parse as a file location now." );
                serviceConfigurationURL = new File( location ).toURL();
            } else {
                try {
                    // realPath may either be a URL or a File
                    serviceConfigurationURL = new URL( realPath );
                } catch (MalformedURLException e2) {
                    log.logDebug( "'Real path' cannot be parsed as URL. "
                        + "Trying to parse as a file location now." );
                    serviceConfigurationURL = new File( realPath ).toURL();
                }
            }
        }
        return serviceConfigurationURL;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log$
Revision 1.7  2006/08/29 19:54:14  poth
footer corrected

Revision 1.6  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
