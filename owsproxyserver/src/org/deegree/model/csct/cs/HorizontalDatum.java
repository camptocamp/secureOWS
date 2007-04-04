/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon GmbH
 http://www.lat-lon.de

 It has been implemented within SEAGIS - An OpenSource implementation of OpenGIS specification
 (C) 2001, Institut de Recherche pour le D�veloppement (http://sourceforge.net/projects/seagis/)
 SEAGIS Contacts:  Surveillance de l'Environnement Assist�e par Satellite
 Institut de Recherche pour le D�veloppement / US-Espace
 mailto:seasnet@teledetection.fr


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

 Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: klaus.greve@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.csct.cs;

// OpenGIS dependencies
import java.util.Map;

import org.deegree.model.csct.resources.Utilities;

/**
 * Procedure used to measure positions on the surface of the Earth.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.cs.CS_HorizontalDatum
 */
public class HorizontalDatum extends Datum {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -1424482162002300865L;

    /**
     * The default WGS 1984 datum.
     */
    public static final HorizontalDatum WGS84 = (HorizontalDatum) pool.intern( new HorizontalDatum(
                                                                                                    "WGS84",
                                                                                                    DatumType.GEOCENTRIC,
                                                                                                    Ellipsoid.WGS84,
                                                                                                    null ) );

    /**
     * The ellipsoid for this datum.
     */
    private final Ellipsoid ellipsoid;

    /**
     * Preferred parameters for a Bursa Wolf transformation.
     */
    private final WGS84ConversionInfo parameters;

    /**
     * Creates horizontal datum from an ellipsoid. The datum
     * type will be {@link DatumType.Horizontal#OTHER}.
     *
     * @param name      Name to give new object.
     * @param ellipsoid Ellipsoid to use in new horizontal datum.
     */
    public HorizontalDatum( final String name, final Ellipsoid ellipsoid ) {
        this( name, DatumType.Horizontal.OTHER, ellipsoid, null );
    }

    /**
     * Creates horizontal datum from ellipsoid and Bursa-Wolf parameters.
     *
     * @param name      Name to give new object.
     * @param type      Type of horizontal datum to create.
     * @param ellipsoid Ellipsoid to use in new horizontal datum.
     * @param parameters   Suggested approximate conversion from new datum to WGS84,
     *                  or <code>null</code> if there is none.
     *
     */
    public HorizontalDatum( final String name, final DatumType.Horizontal type,
                           final Ellipsoid ellipsoid, final WGS84ConversionInfo parameters ) {
        super( name, type );
        this.ellipsoid = ellipsoid;
        this.parameters = ( parameters != null ) ? (WGS84ConversionInfo) parameters.clone() : null;
        ensureNonNull( "ellipsoid", ellipsoid );
    }

    /**
     * Creates horizontal datum from ellipsoid and Bursa-Wolf parameters.
     *
     * @param properties The set of properties (see {@link Info}).
     * @param type       Type of horizontal datum to create.
     * @param ellipsoid  Ellipsoid to use in new horizontal datum.
     * @param parameters   Suggested approximate conversion from new datum to WGS84,
     *                   or <code>null</code> if there is none.
     */
    HorizontalDatum( final Map properties, final DatumType type, final Ellipsoid ellipsoid,
                    final WGS84ConversionInfo parameters ) {
        super( properties, type );
        this.ellipsoid = ellipsoid;
        this.parameters = parameters;
        // Accept null values.
    }

    /**
     * Gets the type of the datum as an enumerated code.
     *
     * Note: return type will be changed to {@link DatumType.Horizontal}
     *       when we will be able to use generic types (with JDK 1.5).
     *
     * @see org.opengis.cs.CS_HorizontalDatum#getDatumType()
     */
    public DatumType/*.Horizontal*/getDatumType() {
        return (DatumType.Horizontal) super.getDatumType();
    }

    /**
     * Returns the ellipsoid.
     *
     * @see org.opengis.cs.CS_HorizontalDatum#getEllipsoid()
     */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Gets preferred parameters for a Bursa Wolf transformation into WGS84.
     * The 7 returned values correspond to (dx,dy,dz) in meters, (ex,ey,ez)
     * in arc-seconds, and scaling in parts-per-million.  This method will
     * always returns <code>null</code> for horizontal datums with type
     * {@link DatumType.Horizontal#OTHER}. This method may also returns
     * <code>null</code> if no suitable transformation is available.
     *
     * @see org.opengis.cs.CS_HorizontalDatum#getWGS84Parameters()
     */
    public WGS84ConversionInfo getWGS84Parameters() {
        return ( parameters != null ) ? (WGS84ConversionInfo) parameters.clone() : null;
    }

    /**
     * Fill the part inside "[...]".
     * Used for formatting Well Know Text (WKT).
     */
    String addString( final StringBuffer buffer ) {
        super.addString( buffer );
        buffer.append( ", " );
        buffer.append( ellipsoid );
        if ( parameters != null ) {
            buffer.append( ", " );
            buffer.append( parameters );
        }
        return "DATUM";
    }

    /**
     * Compares the specified object
     * with this datum for equality.
     */
    public boolean equals( final Object object ) {
        if ( super.equals( object ) ) {
            final HorizontalDatum that = (HorizontalDatum) object;
            return Utilities.equals( this.ellipsoid, that.ellipsoid )
                   && Utilities.equals( this.parameters, that.parameters );
        }
        return false;
    }

}
