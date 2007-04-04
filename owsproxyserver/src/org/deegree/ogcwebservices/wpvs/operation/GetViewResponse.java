//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/operation/GetViewResponse.java,v 1.8 2006/11/23 11:46:40 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs.operation;

import java.awt.image.BufferedImage;


/**
 * The <code>GetViewResponse</code> encapsulates the result (a BufferedImage)  of a {@link GetView} request.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/23 11:46:40 $
 * 
 * @since 2.0
 */
public class GetViewResponse {

    
    private final BufferedImage output;
    private final String outputformat;

    /**
     * @param output the result of a request
     * @param outputformat specified by the request
     */
    public GetViewResponse( BufferedImage output, String outputformat){
        this.output = output;
        this.outputformat = outputformat;
    }
    /**
     * Gets the format of this response
     * @return a String describing the output format
     */
    public String getOutputFormat() {
        return this.outputformat;
    }

    /**
     * Gets the output of a GetView request
     * @return an Image that is the actual image generated by a GetView request
     */
    public BufferedImage getOutput() {
        return this.output;
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetViewResponse.java,v $
Revision 1.8  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.7  2006/08/24 06:42:16  poth
File header corrected

Revision 1.6  2006/06/27 09:07:56  taddei
added missing implementation

Revision 1.5  2006/04/06 20:25:31  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.3  2006/01/16 20:36:39  poth
*** empty log message ***

Revision 1.2  2005/12/16 15:20:33  taddei
added a couple of methods fr getting the imgae format and the actual image

Revision 1.1  2005/12/15 16:54:28  taddei
added GetViewResponse


********************************************************************** */