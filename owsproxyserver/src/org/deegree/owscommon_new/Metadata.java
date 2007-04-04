//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/Metadata.java,v 1.2 2006/08/24 06:43:04 poth Exp $
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

import java.net.URI;

import org.deegree.datatypes.xlink.SimpleLink;

/**
 * <code>Metadata</code> encapsulates generic meta data according to the OWS common
 * specification 1.0.0.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
 * 
 * @since 2.0
 */

public class Metadata {

    private SimpleLink link;
    
    private URI about;
    
    // not sure if this makes actual sense
    private Object metadata;
    
    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param link
     * @param about
     * @param metadata
     */
    public Metadata( SimpleLink link, URI about, Object metadata ) {
        this.link = link;
        this.about = about;
        this.metadata = metadata;
    }
    
    /**
     * @return Returns the about.
     */
    public URI getAbout() {
        return about;
    }

    /**
     * @return Returns the link.
     */
    public SimpleLink getLink() {
        return link;
    }

    /**
     * @return Returns the metadata.
     */
    public Object getMetadata() {
        return metadata;
    }

}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Metadata.java,v $
Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.1  2006/08/08 10:21:52  schmitz
Parser is finished, as well as the iso XMLFactory.



********************************************************************** */