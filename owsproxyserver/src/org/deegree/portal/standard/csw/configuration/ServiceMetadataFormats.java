//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/configuration/ServiceMetadataFormats.java,v 1.3 2006/06/23 13:37:06 mays Exp $
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

package org.deegree.portal.standard.csw.configuration;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/06/23 13:37:06 $
 * 
 * @since 2.0
 */
public class ServiceMetadataFormats {
    private static final String BUNDLE_NAME = 
        "org.deegree.portal.standard.csw.configuration.servicemetadataformats"; 

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private ServiceMetadataFormats() {
    }

    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ServiceMetadataFormats.java,v $
 Revision 1.3  2006/06/23 13:37:06  mays
 add/update csw configuration files

 ********************************************************************** */
