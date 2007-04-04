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
package org.deegree.framework.mail;

/**
 * A UnknownMimetypeException is thrown if the MIME type is not
 * supported.
 *
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A>
 *
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.6 $, $Date: 2006/07/12 14:46:19 $
 */
public class UnknownMimeTypeException extends Exception {

    private String mimeType;

    /**
     * Creates a exception with the given message and MIME type
     */
    public UnknownMimeTypeException(String message, String mimeType) {
        super(message +" : Unknown MIME Type :" + mimeType );
        this.mimeType = mimeType;
    }

    /**
     * @return the name of the unknown mime type
     * 
     * @uml.property name="mimeType"
     */
    public String getMimeType() {
        return this.mimeType;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: UnknownMimeTypeException.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
