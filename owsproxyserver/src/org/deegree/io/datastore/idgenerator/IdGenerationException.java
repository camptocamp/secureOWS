//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/idgenerator/IdGenerationException.java,v 1.4 2006/04/06 20:25:32 poth Exp $
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
package org.deegree.io.datastore.idgenerator;

import org.deegree.io.datastore.DatastoreException;

/**
 * Marks that the generation of an id failed. 
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.4 $, $Date: 2006/04/06 20:25:32 $
 * 
 * @since 2.0
 */
public class IdGenerationException extends DatastoreException {

    private static final long serialVersionUID = -5894784744855230735L;

    /**
     * Creates a new IdGenerationException instance.
     * 
     * @param msg
     *            detail msg
     * @param cause
     *            causing exception
     */
    public IdGenerationException (String msg, Throwable cause) {
        super (msg, cause);        
    }

    /**
     * Creates a new IdGenerationException instance.
     * 
     * @param msg
     *            detail msg
     */    
    public IdGenerationException( String msg ) {
        super (msg);
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IdGenerationException.java,v $
Revision 1.4  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.3  2006/04/04 20:39:44  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.1  2006/02/04 20:07:30  mschneider
Initial version.

********************************************************************** */