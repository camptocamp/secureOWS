/*$************************************************************************************************
 **
 ** $Id: SpatialTemporalExtent.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/SpatialTemporalExtent.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;


/**
 * Extent with respect to date/time and spatial boundaries.
 *
 * @UML abstract EX_SpatialTemporalExtent
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface SpatialTemporalExtent extends TemporalExtent {
    /**
     * Returns the spatial extent component of composite
     * spatial and temporal extent.
     *
     * @return The list of geographic extents (never <code>null</code>).
     * @UML mandatory spatialExtent
     */
    public GeographicExtent[] getSpatialExtent();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SpatialTemporalExtent.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
