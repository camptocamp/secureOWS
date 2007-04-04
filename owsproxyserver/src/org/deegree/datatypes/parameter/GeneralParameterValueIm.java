// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/datatypes/parameter/GeneralParameterValueIm.java,v 1.8 2006/04/06 20:25:32 poth Exp $
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
package org.deegree.datatypes.parameter;

import java.io.Serializable;

import org.opengis.parameter.GeneralOperationParameter;
import org.opengis.parameter.GeneralParameterValue;

/**
 * @version $Revision: 1.8 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.8 $, $Date: 2006/04/06 20:25:32 $ *  * @since 2.0
 */

public class GeneralParameterValueIm implements GeneralParameterValue, Serializable {

  
    private static final long serialVersionUID = 5138443095346081064L;
    private GeneralOperationParameter descriptor = null;
   

    /**
     * @param descriptor
     */
    public GeneralParameterValueIm(GeneralOperationParameter descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return Returns the descriptor.
     * 
     */
    public GeneralOperationParameter getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor The descriptor to set.
     * 
     */
    public void setDescriptor(GeneralOperationParameter descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * Creates and returns a copy of this object.
     * The precise meaning of "copy" may depend on the class of the object.
     *
     * @return A clone of this instance.
     * @see Object#clone
     */
    public Object clone() {
        return new GeneralParameterValueIm( descriptor );
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GeneralParameterValueIm.java,v $
   Revision 1.8  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.7  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.6  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.5  2006/03/02 11:06:04  poth
   *** empty log message ***

   Revision 1.4  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.3.2.1  2005/11/14 11:34:03  deshmukh
   inserted: serialVersionID

   Revision 1.3  2005/02/22 16:56:11  mschneider
   Removed references to ParameterValue. Replaced by new Bean OWSDomainType.

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.2  2004/08/16 06:23:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:01  ap
   no message


********************************************************************** */
