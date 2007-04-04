// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/getcapabilities/Contents.java,v
// 1.1 2004/06/23 11:55:40 mschneider Exp $
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
package org.deegree.model.filterencoding.capabilities;

/**
 * FilterCapabilitiesBean used to represent
 * <code>Filter<code> expressions according to the
 * 1.0.0 as well as the 1.1.1 <code>Filter Encoding Implementation Specification</code>.
 * 
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe</a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.6 $, $Date: 2006/07/12 14:46:16 $
 * 
 * @since 2.0
 */

public class FilterCapabilities {

    public static final String VERSION_100 = "1.0.0";
    public static final String VERSION_110 = "1.1.0";
    
    /**
     * 
     * @uml.property name="scalarCapabilities"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ScalarCapabilities scalarCapabilities;

    /**
     * 
     * @uml.property name="spatialCapabilities"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private SpatialCapabilities spatialCapabilities;

    private IdCapabilities idCapabilities;

    private String version;
    
    /**
     * Constructs a new <code>FilterCapabilities</code> -instance with the
     * given parameters. Used for filter expressions according to the 1.0.0
     * specification that don't have an <code>Id_Capabilities</code> section.
     * 
     * @param scalarCapabilities
     * @param spatialCapabilities
     */
    public FilterCapabilities(ScalarCapabilities scalarCapabilities,
            SpatialCapabilities spatialCapabilities) {
        this.scalarCapabilities = scalarCapabilities;
        this.spatialCapabilities = spatialCapabilities;
        this.version = VERSION_100;
    }

    /**
     * Constructs a new <code>FilterCapabilities</code> -instance with the
     * given parameters. Used for filter expressions according to the 1.1.0
     * specification that have an <code>Id_Capabilities</code> section.
     * 
     * @param scalarCapabilities
     * @param spatialCapabilities
     * @param idCapabilities
     */
    public FilterCapabilities(ScalarCapabilities scalarCapabilities,
            SpatialCapabilities spatialCapabilities,
            IdCapabilities idCapabilities) {
        this.scalarCapabilities = scalarCapabilities;
        this.spatialCapabilities = spatialCapabilities;
        this.idCapabilities = idCapabilities;
        this.version = VERSION_110;
    }

    /**
     * @return
     * 
     * @uml.property name="scalarCapabilities"
     */
    public ScalarCapabilities getScalarCapabilities() {
        return scalarCapabilities;
    }

    /**
     * @return
     * 
     * @uml.property name="spatialCapabilities"
     */
    public SpatialCapabilities getSpatialCapabilities() {
        return spatialCapabilities;
    }

    /**
     * @param capabilities
     * 
     * @uml.property name="scalarCapabilities"
     */
    public void setScalarCapabilities(ScalarCapabilities capabilities) {
        scalarCapabilities = capabilities;
    }

    /**
     * @param capabilities
     * 
     * @uml.property name="spatialCapabilities"
     */
    public void setSpatialCapabilities(SpatialCapabilities capabilities) {
        spatialCapabilities = capabilities;
    }
    
    /**
     * @return Returns the idCapabilities.
     */
    public IdCapabilities getIdCapabilities() {
        return idCapabilities;
    }
    /**
     * @param idCapabilities The idCapabilities to set.
     */
    public void setIdCapabilities(IdCapabilities idCapabilities) {
        this.idCapabilities = idCapabilities;
    }
    
    
    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FilterCapabilities.java,v $
Revision 1.6  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
