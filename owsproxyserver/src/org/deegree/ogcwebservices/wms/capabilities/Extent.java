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
package org.deegree.ogcwebservices.wms.capabilities;



/**
 * The Extent element indicates what _values_ along a dimension are valid.
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.11 $
 */
public class Extent {
    private String default_ = null;
    private String name = null;
    private boolean useNearestValue = false;
    private String value = null;

    /**
    * constructor initializing the class with the <Extent>
    */
    Extent( String name, String default_, boolean useNearestValue ) {
        setName( name );
        setDefault( default_ );
        setUseNearestValue( useNearestValue );
    }
    
    /**
     * constructor initializing the class with the <Extent>
     * @param name 
     * @param default_ 
     * @param useNearestValue 
     * @param value 
     */
    public Extent( String name, String default_, boolean useNearestValue, String value ) {
         this( name, default_, useNearestValue );
         this.value = value;
     }

    /**
     * @return the name of the extent
     */
    public String getName() {
        return name;
    }

    /**
    * sets the name of the extent
     * @param name 
    */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return the default extent
     */
    public String getDefault() {
        return default_;
    }

    /**
    * sets the default extent
     * @param default_ 
    */
    public void setDefault( String default_ ) {
        this.default_ = default_;
    }

    /**
     * @return true if a WMS should use the extent that is nearest to 
     * the requested level.
     */
    public boolean useNearestValue() {
        return useNearestValue;
    }

    /**
    * sets true if a WMS should use the extent that is nearest to 
    * the requested level.
     * @param useNearestValue 
    */
    public void setUseNearestValue( boolean useNearestValue ) {
        this.useNearestValue = useNearestValue;
    }
    
    /**
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @param value
     */
    public void setValue( String value ) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        String ret = null;
        ret = "name = " + name + "\n";
        ret += ( "default_ = " + default_ + "\n" );
        ret += ( "useNearestValue = " + useNearestValue + "\n" );
        return ret;
    }
   
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Extent.java,v $
Revision 1.11  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.10  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
