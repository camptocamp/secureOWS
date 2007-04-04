//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wfsconf/WFSConfOperation.java,v 1.7 2006/08/24 06:38:30 poth Exp $
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
package org.deegree.conf.services.wfsconf;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sncho
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WFSConfOperation {

    protected WFSConfConfiguration config;
    
    /**
     * @param config
     */
    public void setConfiguration( WFSConfConfiguration config ){
        if ( config == null ){
            throw new NullPointerException( "config cannot be null." );
        }
        this.config = config;
    }
    
    
	 public abstract String performOperation( HttpServletRequest request) throws Exception;
	 public abstract String getOperationName();

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WFSConfOperation.java,v $
Revision 1.7  2006/08/24 06:38:30  poth
File header corrected

Revision 1.6  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.4  2006/01/16 20:36:40  poth
*** empty log message ***

Revision 1.3  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.2  2005/12/08 15:34:10  taddei
refactoring

Revision 1.1  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */