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

import java.net.URL;


/**
 * This class encapsulates a reference to a Web Map Context document as defined
 * int the OGC Web Map Context specification
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class ViewContextReference  {
    private String title = null;
    private URL contextURL = null;

    /**
     * Creates a new ViewContextReference object.
     *
     * @param title title of the context
     * @param contextURL URL where to access the context
     *
     * @throws ContextException 
     */
    public ViewContextReference( String title, URL contextURL ) throws ContextException {
        setTitle( title );
        setContextURL( contextURL );
    }

    /**
     *
     *
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     *
     * @param title 
     *
     * @throws ContextException 
     */
    public void setTitle( String title ) throws ContextException {
        if ( title == null ) {
            throw new ContextException( "title isn't allowed to be null" );
        }

        this.title = title;
    }

    /**
     *
     *
     * @return 
     */
    public URL getContextURL() {
        return contextURL;
    }

    /**
     *
     *
     * @param contextURL 
     *
     * @throws ContextException 
     */
    public void setContextURL( URL contextURL ) throws ContextException {
        if ( contextURL == null ) {
            throw new ContextException( "contextURL isn't allowed to be null" );
        }

        this.contextURL = contextURL;
    }

   
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ViewContextReference.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
