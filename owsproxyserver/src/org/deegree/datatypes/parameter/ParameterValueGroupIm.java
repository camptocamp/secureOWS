// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/datatypes/parameter/ParameterValueGroupIm.java,v 1.7 2006/04/06 20:25:32 poth Exp $
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.opengis.parameter.GeneralOperationParameter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.ParameterValueGroup;

/**
 *  A group of related parameter values. The same group can be repeated more 
 *  than once in an  
 *  or higher level <code>ParameterValueGroup</code>, if those instances 
 *  contain different values of one or more which suitably
 *  distinquish among those groups.
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/04/06 20:25:32 $
 *
 * @since 2.0
 * @deprecated Not required. Will be deleted.   
 */
public class ParameterValueGroupIm extends GeneralParameterValueIm 
                                   implements ParameterValueGroup, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Map values = new HashMap();
    

    /**
     * initializes a new <tt>ParameterValueGroup</tt>. The implementation
     * of the org.opengis.parameter.ParameterValueGroup interfaces adds
     * mutator methods.
     * 
     * @param descriptor descriptor of this ParameterValueGroup
     * @param values values contaied in this group
     */
    public ParameterValueGroupIm(GeneralOperationParameter descriptor, 
                                 GeneralParameterValue[] values) {
        super(descriptor);
        setValues(values);
    }
    
    /**
     * @see org.opengis.parameter.ParameterValueGroup#getValue(java.lang.String)
     */
    public GeneralParameterValue getValue(String name) throws InvalidParameterNameException {
        return (GeneralParameterValue)values.get( name );
    }

    /**
     * @see org.opengis.parameter.ParameterValueGroup#getValues()
     * 
     * @uml.property name="values"
     */
    public GeneralParameterValue[] getValues() {
        GeneralParameterValue[] gpv = new GeneralParameterValue[values.size()];
        return (GeneralParameterValue[]) values.values().toArray(gpv);
    }

    
    /**
     * @see #getValues()
     */
    public void setValues(GeneralParameterValue[] values) {
        this.values.clear();
        for (int i = 0; i < values.length; i++) {
            this.values.put( values[i].getDescriptor().getName( Locale.getDefault() ), values[i]);
        }
    }
    
    /**
     * @see #getValues()
     */
    public void addValue(GeneralParameterValue value) {
        values.put( value.getDescriptor().getName( Locale.getDefault() ), value );
    }

    /**
     * @param name
     */
    public void removeValue(String name) {
        values.remove(name);
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ParameterValueGroupIm.java,v $
   Revision 1.7  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.6  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.5  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.4  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.3.2.1  2005/11/14 11:34:03  deshmukh
   inserted: serialVersionID

   Revision 1.3  2005/02/22 16:56:11  mschneider
   Removed references to ParameterValue. Replaced by new Bean OWSDomainType.

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/08/16 06:23:33  ap
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:01  ap
   no message


********************************************************************** */
