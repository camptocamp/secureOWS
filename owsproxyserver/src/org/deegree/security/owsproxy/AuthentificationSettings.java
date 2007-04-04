/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.security.owsproxy;

import org.deegree.ogcbase.BaseURL;


/**
 * This exception shall be thrown when a session(ID) will be used that
 * has been expired.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.4 $, $Date: 2006/07/12 14:46:18 $
 * 
 * @since 1.1
 */
public class AuthentificationSettings {
    
    private BaseURL authentificationURL = null;   

    /**
     * @param authentificationURL
     */
    public AuthentificationSettings(BaseURL authentificationURL) {
        this.authentificationURL = authentificationURL;
    }
    
    public BaseURL getAuthentificationURL() {
        return authentificationURL;
    }
    
    public void setAuthentificationURL(BaseURL authentificationURL) {
        this.authentificationURL = authentificationURL;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AuthentificationSettings.java,v $
Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
