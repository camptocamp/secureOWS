//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/IODocument.java,v 1.9 2006/10/17 20:31:17 poth Exp $
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
package org.deegree.io;

import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.w3c.dom.Element;

/**
 * This class provides method for reading IO configuration elements that are common to several
 * services/applications.
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/10/17 20:31:17 $
 *
 * @since 2.0
 */
public class IODocument extends XMLFragment {

    private static final long serialVersionUID = -8166238044553562735L;

    /**
     * @param element
     */
    public IODocument( Element element ) {
        super( element );
    }

    /**
     * parses a JDBCConnection element and returns the object representation
     *
     * @return a jdbc connection object
     * @throws XMLParsingException
     */
    public JDBCConnection parseJDBCConnection() throws XMLParsingException {
        JDBCConnection connection = null;

        Element connectionElement = this.getRootElement();
        if ( connectionElement != null ) {
            String driver = XMLTools.getRequiredNodeAsString( connectionElement,
                "dgjdbc:Driver/text()", nsContext );
            String logon = XMLTools.getRequiredNodeAsString( connectionElement,
                "dgjdbc:Url/text()", nsContext );
            String user = XMLTools.getNodeAsString( connectionElement, "dgjdbc:User/text()",
                nsContext, null );
            String password = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:Password/text()", nsContext, null );
            String securityConstraints = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:SecurityConstraints/text()", nsContext, null );
            String encoding = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:Encoding/text()", nsContext, CharsetUtils.getSystemCharset() );
            String aliasPrefix = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:AliasPrefix/text()", nsContext, null );
            String sdeDatabase = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:SDEDatabase/text()", nsContext, null );
            String sdeVersion = XMLTools.getNodeAsString( connectionElement,
                "dgjdbc:SDEVersion/text()", nsContext, null );
            connection = new JDBCConnection( driver, logon, user, password, securityConstraints,
                encoding, aliasPrefix, sdeDatabase, sdeVersion );
        }
        return connection;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: IODocument.java,v $
 * Revision 1.9  2006/10/17 20:31:17  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/08/07 14:52:14  poth
 * set reading encoding to optional; use system charset as default
 *
 * Revision 1.7  2006/07/07 15:03:03  schmitz
 * Fixed a few warnings.
 * Added database options to WASS deegree params.
 *
 * Revision 1.6  2006/05/21 19:13:03  poth
 * several changes required by implemented SDEDatastore / adapted to ArcSDE 9 java API
 *
 * Revision 1.2  2006/05/09 14:50:50  polli
 * SDE parameters added
 *
 * Revision 1.1.1.1  2006/04/12 20:37:06  polli
 * no message
 *
 * Revision 1.5  2006/04/06 20:25:31  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:28  poth
 * *** empty log message ***
 *
 * Revision 1.3  2005/11/17 08:18:35  deshmukh
 * Renamed nsNode to nsContext
 *
 * Revision 1.2  2005/11/16 13:45:01  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.1.2.3  2005/11/07 18:28:22  mschneider
 * *** empty log message ***
 *
 * Revision 1.1.2.2 2005/11/07 15:38:04 mschneider
 * Refactoring: use NamespaceContext instead of Node for namespace bindings.
 *
 * Revision 1.1.2.1 2005/11/07 13:09:26 deshmukh
 * Switched namespace definitions in "CommonNamespaces" to URI.
 *
 * Revision 1.1 2005/10/07 10:30:41 poth no message
 *
 **************************************************************************************************/
