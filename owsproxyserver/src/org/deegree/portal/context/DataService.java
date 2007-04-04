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



/**
 * describes the service lying behind a WMS layer. This can be a WFS, a WCS or
 * a cascaded WMS. If the dataservice is a WFS an instance of this class also
 * provides informations about the geometry type delivered by the WFS for this
 * assigned feature type. If the service is a WCS the geometry type attribute
 * contains the type of coverage assigned to the layer (Grid, TIN, Thiessen 
 * polygon ...)
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class DataService  {
    private Server server = null;
    private String featureType = null;
    private String geometryType = null;

    /**
     * Creates a new DataService object.
     *
     * @param server server description
     * @param featureType feature type provided by the server if it's a WFS
     * @param geometryType geometry type or coverage type if the server is a
     *                     WFS or a WCS
     */
    public DataService( Server server, String featureType, String geometryType ) {
        setServer( server );
        setFeatureType( featureType );
        setGeometryType( geometryType );
    }

    /**
     * returns the an instance of an object describing the service/server behind
     * a WMS layer
     *
     * @return instance of <tt>Server</tt>
     */
    public Server getServer() {
        return server;
    }

    /**
     * sets the an instance of an object describing the service/server behind
     * a WMS layer
     *
     * @param server server description
     */
    public void setServer( Server server ) {
        this.server = server;
    }
    
    /**
     * returns the featuretype assigned to the WMS layer if the server behind it
     * is a WFS
     *
     * @return 
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * sets the featuretype assigned to the WMS layer if the server behind it
     * is a WFS
     *
     * @param featureType 
     */
    public void setFeatureType( String featureType ) {
        this.featureType = featureType;
    }

    /**
     * returns the geometry type or coverage type provided by the server behind 
     * a WMS layer if the server is a WFS or a WCS
     *
     * @return 
     */
    public String getGeometryType() {
        return geometryType;
    }

    /**
     * sets the geometry type or coverage type provided by the server behind 
     * a WMS layer if the server is a WFS or a WCS
     *
     * @param geometryType 
     */
    public void setGeometryType( String geometryType ) {
        this.geometryType = geometryType;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DataService.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
