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
package org.deegree.portal.context;

import java.util.HashMap;



/**
 * encapsulates a FormatList as defined by the OGC Web Map Context specification
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class FormatList {
    private HashMap formats = new HashMap();
    private Format current = null;

    /**
     * Creates a new FormatList object.
     *
     * @param formats 
     */
    public FormatList( Format[] formats ) throws ContextException {
        setFormats( formats );
    }

    /**
     *
     *
     * @return 
     */
    public Format[] getFormats() {
        Format[] fr = new Format[formats.size()];
        return (Format[])formats.values().toArray( fr );
    }

    /**
     *
     *
     * @param formats 
     *
     * @throws ContextException 
     */
    public void setFormats( Format[] formats ) throws ContextException {
        if ( ( formats == null ) || ( formats.length == 0 ) ) {
            throw new ContextException( "at least one format must be defined for a layer" );
        }

        this.formats.clear();

        for ( int i = 0; i < formats.length; i++ ) {
            if ( formats[i].isCurrent() ) {
                current = formats[i];
            }
            this.formats.put( formats[i].getName(), formats[i] );
        }
    }
    
    /**
     * @return
     */
    public Format getCurrentFormat() {
        return current;
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public Format getFormat( String name ) {
        return (Format)formats.get( name );
    }

    /**
     *
     *
     * @param format 
     */
    public void addFormat( Format format ) {
        if ( format.isCurrent() ) {
            current.setCurrent( false );
            current = format;
        }
        formats.put( format.getName(), format );
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public Format removeFormat( String name ) {
        return (Format)formats.remove( name );
    }

    /**
     *
     */
    public void clear() {
        formats.clear();
    }
    
   
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FormatList.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
