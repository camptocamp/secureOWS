/*$************************************************************************************************
 **
 ** $Id: VerticalExtent.java,v 1.3 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/VerticalExtent.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;



/**
 * Vertical domain of dataset.
 *
 * @UML abstract EX_VerticalExtent
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface VerticalExtent {
    /**
     * Returns the lowest vertical extent contained in the dataset.
     *
     * @UML mandatory minimumValue
     */
    public double getMinimumValue();

    /**
     * Returns the highest vertical extent contained in the dataset.
     *
     * @UML mandatory maximumValue
     */
    public double getMaximumValue();

    /**
     * Returns the vertical units used for vertical extent information.
     * Examples: metres, feet, millimetres, hectopascals.
     *
     * @UML mandatory unitOfMeasure
     */
    public String getUnit();

    /**
     * Provides information about the origin from which the
     * maximum and minimum elevation values are measured.
     *
     * @UML mandatory verticalDatum
     */
//    public VerticalDatum getVerticalDatum();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: VerticalExtent.java,v $
Revision 1.3  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
