//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/xml/InvalidConfigurationException.java,v 1.8 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.framework.xml;

/**
 * Indicates that a configuration (or a fragment of it) does not match the
 * expected format. 
 *
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</A>
 *
 * @author last edited by: $Author: poth $
 *
 * @version $Revision: 1.8 $, $Date: 2006/11/27 09:07:53 $
 */
public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException (String msg) {
        super (msg);
    }

    /**
     * @param string
     * @param string2
     */
    public InvalidConfigurationException(String source, String message) {
           super(message+" in "+source);
    }
    
    /**
     * 
     * @param msg
     * @param e
     */
    public InvalidConfigurationException (String msg, Throwable e) {
        super (msg, e);
    }

   
    public InvalidConfigurationException( Throwable arg0 ) {
        super( arg0 );
    }
    
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InvalidConfigurationException.java,v $
Revision 1.8  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.7  2006/11/07 11:07:30  mschneider
Fixed footer formatting.

Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */