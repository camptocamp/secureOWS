//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/model/DataSessionRecord.java,v 1.3 2006/07/31 09:29:10 mays Exp $
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

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/

package org.deegree.portal.standard.csw.model;

import java.io.Serializable;

import org.deegree.model.spatialschema.Envelope;

/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.3 $, $Date: 2006/07/31 09:29:10 $
 * 
 * @since 2.0
 */
public class DataSessionRecord extends SessionRecord implements Serializable {

    private static final long serialVersionUID = -1816508996710579386L;
    private ServiceSessionRecord[] services;
    private Envelope bbox;

    /**
     * @param identifier
     * @param catalogName
     * @param title
     */
    public DataSessionRecord( String identifier, String catalogName, String title ) {
        super( identifier, catalogName, title );
        this.services = null;
        this.bbox = null;
    }

    /**
     * @param identifier
     * @param catalogName
     * @param title
     * @param services 
     * @param bbox 
     */
    public DataSessionRecord( String identifier, String catalogName, String title, 
                              ServiceSessionRecord[] services, Envelope bbox ) {
        super( identifier, catalogName, title );
        this.services = services;
        this.bbox = bbox;
    }

    /**
     * @param dsr
     */
    public DataSessionRecord( DataSessionRecord dsr ) {
        super( dsr );
        this.services = dsr.getServices();
        this.bbox = dsr.getBoundingBox();        
    }
    
    
    /**
     * @return Returns the bbox.
     */
    public Envelope getBoundingBox() {
        return bbox;
    }

    /**
     * @param bbox The bbox to set.
     */
    public void setBoundingBox( Envelope bbox ) {
        this.bbox = bbox;
    }

    /**
     * @return Returns the services.
     */
    public ServiceSessionRecord[] getServices() {
        return services;
    }

    /**
     * @param services The services to set.
     */
    public void setServices( ServiceSessionRecord[] services ) {
        this.services = services;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DataSessionRecord.java,v $
Revision 1.3  2006/07/31 09:29:10  mays
class implements serializable

Revision 1.2  2006/06/23 13:37:36  mays
add/update csw model files

********************************************************************** */
