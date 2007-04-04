/*$************************************************************************************************
 **
 ** $Id: GeographicBoundingBox.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/GeographicBoundingBox.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;


/**
 * Geographic position of the dataset. This is only an approximate
 * so specifying the co-ordinate reference system is unnecessary.
 *
 * @UML abstract EX_GeographicBoundingBox
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface GeographicBoundingBox extends GeographicExtent {
    /**
     * Returns the western-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @return The western-most longitude between -180 and +180�.
     * @UML mandatory westBoundLongitude
     * @unitof Angle
     */
    public double getWestBoundLongitude();

    /**
     * Returns the eastern-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @return The eastern-most longitude between -180 and +180�.
     * @UML mandatory eastBoundLongitude
     * @unitof Angle
     */
    public double getEastBoundLongitude();

    /**
     * Returns the southern-most coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @return The southern-most latitude between -90 and +90�.
     * @UML mandatory southBoundLatitude
     * @unitof Angle
     */
    public double getSouthBoundLatitude();

    /**
     * Returns the northern-most, coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @return The northern-most latitude between -90 and +90�.
     * @UML mandatory northBoundLatitude
     * @unitof Angle
     */
    public double getNorthBoundLatitude();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeographicBoundingBox.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
