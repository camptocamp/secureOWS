/*$************************************************************************************************
 **
 ** $Id: BoundingPolygon.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/BoundingPolygon.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;

// OpenGIS direct dependencies


/**
 * Boundary enclosing the dataset, expressed as the closed set of
 * (<var>x</var>,<var>y</var>) coordinates of the polygon. The last
 * point replicates first point.
 *
 * @UML abstract EX_BoundingPolygon
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface BoundingPolygon extends GeographicExtent {
    /**
     * Returns the sets of points defining the bounding polygon.
     *
     * @UML mandatory polygon
     *
     * @revisit The UML allow an arbitrary number of object to be returned. Should we return
     *          an array? Furthermore, the UML return the most general type ({@link Geometry}).
     *          We could returns an array of some more restrictive type, or allow this method
     *          to returns some aggregate.
     */
    //public Geometry getPolygon();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: BoundingPolygon.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
