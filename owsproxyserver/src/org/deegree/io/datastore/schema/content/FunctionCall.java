//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/FunctionCall.java,v 1.4 2006/11/09 17:38:00 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.io.datastore.schema.content;

import java.util.ArrayList;
import java.util.List;

import org.deegree.io.datastore.schema.MappedSimplePropertyType;

/**
 * Abstract content class for {@link MappedSimplePropertyType}s that describes a call to a
 * function as the origin of the property value.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/11/09 17:38:00 $
 */
public abstract class FunctionCall extends VirtualContent {

    private List<FunctionParam> params;

    /**
     * Initializes a newly created <code>FunctionCall</code> with the given {@link FunctionParam}s.
     * 
     * @param params
     */
    public FunctionCall( List<FunctionParam> params ) {
        this.params = params;
    }

    /**
     * Initializes a newly created <code>FunctionCall</code> with the given {@link FunctionParam}s.
     * 
     * @param params
     */
    public FunctionCall( FunctionParam...params ) {
        this.params = new ArrayList<FunctionParam> (params.length);
        for ( FunctionParam param : params ) {
            this.params.add(param);
        }
    }    

    /**
     * Returns the {@link FunctionParam}s.
     * 
     * @return the parameters of the function call
     */
    public List<FunctionParam> getParams () {
        return this.params;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FunctionCall.java,v $
 Revision 1.4  2006/11/09 17:38:00  mschneider
 Added constructor that accepts arrays.

 Revision 1.3  2006/09/19 14:52:58  mschneider
 Added abstract base class VirtualContent.

 Revision 1.2  2006/09/13 18:22:39  mschneider
 Improved javadoc.

 Revision 1.1  2006/09/05 17:42:08  mschneider
 Initial version.

 ********************************************************************** */