//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/j3d/RenderingEngine.java,v 1.4 2006/04/06 20:25:28 poth Exp $
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

package org.deegree.ogcwebservices.wpvs.j3d;

/**
 * Interface for common WPVS rendering engines. This interface is adapted from the one
 * available in deegree 1.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/04/06 20:25:28 $
 * 
 * @since 2.0
 */
public interface RenderingEngine {

    /**
     * Renders the scene and returns its rendered representation
     *
     * @return rendered scene
     */
    Object renderScene();

    /**
     * sets the scenes back clip distance
     *
     * @param distance 
     */
    void setBackClipDistance( float distance );

    /**
     * sets the scenes front clip distance
     *
     * @param distance 
     */
    void setFrontClipDistance( float distance );
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RenderingEngine.java,v $
Revision 1.4  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.2  2006/03/29 15:04:12  taddei
sz

Revision 1.1  2005/12/21 13:50:03  taddei
first check in of old but good WTS classes


********************************************************************** */