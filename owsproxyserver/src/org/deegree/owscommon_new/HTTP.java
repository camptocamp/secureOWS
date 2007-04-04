//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/HTTP.java,v 1.2 2006/08/24 06:43:04 poth Exp $
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.owscommon_new;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * <code>HTTP</code> describes the distributed computing platform which a service uses.
 * In terms of HTTP: it stores the links where it can be reached.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
 * 
 * @since 2.0
 */

public class HTTP implements DCP {

    private static final long serialVersionUID = 989887096571428263L;
    
    private List<List<DomainType>> constraints;
    
    private List<Type> types;
    
    private List<OnlineResource> links;
    
    /**
     * Standard constructor that initializes all encapsulated data.
     *
     * @param links 
     * @param constraints
     * @param types 
     */
    public HTTP( List<OnlineResource> links, List<List<DomainType>> constraints, List<Type> types ) {
        this.links = links;
        this.constraints = constraints;
        this.types = types;
    }

    /**
     * @return Returns the constraints.
     */
    public List<List<DomainType>> getConstraints() {
        return constraints;
    }
    
    /**
     * @return Returns the types.
     */
    public List<Type> getTypes() {
        return types;
    }

    /**
     * @return the links.
     */
    public List<OnlineResource> getLinks() {
        return links;
    }
    
    /**
     * Enumeration type indicating the used HTTP request method.
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author: poth $
     * 
     * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
     * 
     * @since 2.0
     */
    public enum Type {
        /**
         * The Get HTTP method.
         */
        Get,
        /**
         * The Post HTTP method.
         */
        Post
    }

    /**
     * @return a list of all Get method URLs.
     */
    public List<URL> getGetOnlineResources() {
        List<URL> result = new ArrayList<URL>();
        
        for( int i = 0; i < types.size(); ++i )
            if( types.get( i ) == Type.Get )
                result.add( links.get( i ).getLinkage().getHref() );
        
        return result;
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: HTTP.java,v $
Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:21  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.2  2006/08/22 10:25:01  schmitz
Updated the WMS to use the new OWS common package.
Updated the rest of deegree to use the new data classes returned
by the updated WMS methods/capabilities.

Revision 1.1  2006/08/08 10:21:52  schmitz
Parser is finished, as well as the iso XMLFactory.



********************************************************************** */