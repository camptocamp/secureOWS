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
package org.deegree.model.feature;

import org.deegree.framework.util.StringTools;

/**
 *
 * @author  Administrator
 */
public class FeatureException extends java.lang.Exception {
        
    private String st = "org.deegree.model.feature.FeatureException";
    
    /**
     * Creates a new instance of <code>FeatureException</code> without detail message.
     */
    public FeatureException() {
    }
    
     
    /**
     * Constructs an instance of <code>FeatureException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FeatureException(String msg) {
        super( msg );
    }
       
    /**
     * Constructs an instance of <code>FeatureException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FeatureException(String msg, Exception e) {
        this( msg );
        st = StringTools.stackTraceToString( e.getStackTrace() );
    }
    
    public String toString() {
        return super.toString() + "\n" + st;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureException.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
