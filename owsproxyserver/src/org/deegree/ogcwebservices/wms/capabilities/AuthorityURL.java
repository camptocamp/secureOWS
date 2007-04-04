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
package org.deegree.ogcwebservices.wms.capabilities;

import java.net.URL;

import org.deegree.ogcbase.BaseURL;

/**
 * AuthorityURL encloses an <OnlineResource>element which states the URL of a document defining the
 * meaning of the Identifier values.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.12 $
 */
public class AuthorityURL extends BaseURL {

    private String name = null;

    /**
     * constructor initializing the class with the authorityURL
     * @param name 
     * @param onlineResource 
     */
    public AuthorityURL( String name, URL onlineResource ) {
        super( null, onlineResource );
        setName( name );
    }

    /**
     * @return the name of the authority
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the authority
     * @param name 
     */
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        String ret = null;
        ret = "name = "
            + name + "\n";
        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AuthorityURL.java,v $
Revision 1.12  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.11  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
