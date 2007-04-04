//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/geotiff/GeoTiffException.java,v 1.5 2006/04/06 20:25:29 poth Exp $
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
package org.deegree.io.geotiff;

/**
 * The GeoTIFF exception. Thrown in the context of this package of the GeoTIFF
 * Writer and Reader.
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </A>
 * @author last edited by: $Author: poth $
 * @version 2.0. $Revision: 1.5 $, $Date: 2006/04/06 20:25:29 $
 * @since
 */
public class GeoTiffException extends Exception {

    private String message = "GeoTiffException";

    /**
     * 
     * @param message
     */
    public GeoTiffException(String message) {
        this.message = message;
    }

    /**
     * 
     * @uml.property name="message"
     */
    public String getMessage() {
        return this.message;
    }

    /**
     *  
     */
    public String toString() {
        return message + "\n" + getLocalizedMessage();
    }

}

/*
 * ****************************************************************************
 * Changes to this class. What the people have been up to:
 * 
 * $Log: GeoTiffException.java,v $
 * Revision 1.5  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.2  2005/01/18 22:08:54  poth
 * no message
 *
 * Revision 1.1  2004/07/16 07:03:39  ap
 * no message
 *
 * Revision 1.2  2004/07/15 09:57:23  axel_schaefer
 * no message
 *
 * ****************************************************************************
 */