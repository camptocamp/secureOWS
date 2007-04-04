/*$************************************************************************************************
 **
 ** $Id: GeographicDescription.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/metadata/extent/Attic/GeographicDescription.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.metadata.extent;

// OpenGIS direct dependencies
import org.opengis.metadata.Identifier;


/**
 * Description of the geographic area using identifiers.
 *
 * @UML abstract EX_GeographicDescription
 * @author ISO 19115
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 5.0
 */
public interface GeographicDescription extends GeographicExtent {
    /**
     * Returns the identifier used to represent a geographic area.
     *
     * @UML mandatory geographicIdentifier
     */
    public Identifier getGeographicIdentifier();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeographicDescription.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
