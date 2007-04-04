/*$************************************************************************************************
 **
 ** $Id: GeographicExtent.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/GeographicExtent.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;


/**
 * Base interface for geographic area of the dataset.
 *
 * @UML abstract EX_GeographicExtent
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface GeographicExtent {
    /**
     * Indication of whether the bounding polygon encompasses an area covered by the data
     * (<cite>inclusion</cite>) or an area where data is not present (<cite>exclusion</cite>).
     *
     * @return <code>true</code> for inclusion, or <code>false</code> for exclusion.
     * @UML optional extentTypeCode
     */
    public boolean isInclusion();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeographicExtent.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
