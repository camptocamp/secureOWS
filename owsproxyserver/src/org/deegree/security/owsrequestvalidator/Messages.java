//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/security/owsrequestvalidator/Messages.java,v 1.7 2006/10/04 20:42:21 poth Exp $
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
package org.deegree.security.owsrequestvalidator;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/04 20:42:21 $
 *
 * @since 2.0
 */
public class Messages {
    private static final String BUNDLE_NAME = "org.deegree.security.owsrequestvalidator.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = 
        ResourceBundle.getBundle( BUNDLE_NAME );

    private Messages() {
    }

    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString( key );
        } catch ( MissingResourceException e ) {
            return '!' + key + '!';
        }
    }
    
    public static String format (String key, Object... arguments) {
        return MessageFormat.format(getString (key), arguments);
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Messages.java,v $
 Revision 1.7  2006/10/04 20:42:21  poth
 *** empty log message ***

 Revision 1.6  2006/05/23 06:54:29  poth
 format methods added

 Revision 1.5  2006/04/06 20:25:26  poth
 *** empty log message ***

 Revision 1.4  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.3  2006/01/19 21:24:49  poth
 *** empty log message ***

 Revision 1.2  2005/04/07 09:23:17  poth
 no message

 Revision 1.1  2005/02/03 15:56:16  poth
 no message


 ********************************************************************** */