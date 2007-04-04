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
package org.deegree.graphics.sld;

import org.deegree.framework.xml.Marshallable;


/**
 * 
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.8 $ $Date: 2006/07/29 08:51:12 $
 */
public class Extent implements Marshallable {
    private String name = null;
    private String value = null;

    /**
    * default constructor
    */
    Extent() {
    }

    /**
    * constructor initializing the class with the <Extent>
    */
    Extent( String value, String name ) {
        setName( name );
        setValue( value );
    }

    /**
     * returns the name of the extent
     * @return the name of the extent
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the extent
     * @param name the name of the extent
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the value of the extent
     * @return the value of the extent
     * 
     * @uml.property name="value"
     */
    public String getValue() {
        return value;
    }

    /**
     * sets the value of the extent
     * @param value the value of the extent
     * 
     * @uml.property name="value"
     */
    public void setValue(String value) {
        this.value = value;
    }

    
    /**
     * exports the content of the FeatureTypeConstraint as XML formated String
     *
     * @return xml representation of the FeatureTypeConstraint
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<Extent>" );
        sb.append( "<Name>" ).append( name ).append( "</Name>" );
        sb.append( "<Value>" ).append( name ).append( "</Value>" );
        sb.append( "</Extent>" );
        
        return sb.toString();
    }  
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Extent.java,v $
Revision 1.8  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
