//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/sos/Constants.java,v 1.5 2006/08/29 19:54:14 poth Exp $

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
package org.deegree.portal.standard.sos;

/**
 * Constants for SOSClient.
 * 
 * @author <a href="mailto:che@wupperverband.de.de">Christian Heier</a>
 * @version 0.1
 */
public class Constants {

    private Constants(){
        // prevent instantiation
    }
    
    /**
     * Comment for <code>TYPENAME</code>
     */
    public static final String TYPENAME = "TypeName";

    /**
     * Comment for <code>PLATFORMDESCRIPTION</code>
     */
    public static final String PLATFORMDESCRIPTION = "PlatformDescription";

    /**
     * Comment for <code>PLATFORMDESCRIPTION</code>
     */
    public static final String SENSORDESCRIPTION = "SensorDescription";

    public static final String OBSERVATION_DATA = "ObservationData";
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Constants.java,v $
Revision 1.5  2006/08/29 19:54:14  poth
footer corrected

Revision 1.4  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.3  2006/04/04 20:39:41  poth
*** empty log message ***

Revision 1.2  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.1  2006/02/05 09:30:12  poth
*** empty log message ***

Revision 1.4  2005/09/06 18:05:51  taddei
added more constants

Revision 1.3  2005/09/05 15:25:35  taddei
added constant

Revision 1.2  2005/08/31 13:23:36  taddei
Made interface a (non-instatianble) class

Revision 1.1  2005/08/26 10:12:40  taddei
first upload of C. Heyer's client code

********************************************************************** */