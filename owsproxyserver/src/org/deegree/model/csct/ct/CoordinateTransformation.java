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
package org.deegree.model.csct.ct;

// OpenGIS dependencies
import java.util.Locale;

import org.deegree.model.csct.cs.CoordinateSystem;
import org.deegree.model.csct.cs.Info;
import org.deegree.model.csct.resources.Utilities;
import org.deegree.model.csct.resources.css.ResourceKeys;
import org.deegree.model.csct.resources.css.Resources;

/**
 * Describes a coordinate transformation. A coordinate transformation class establishes
 * an association between a source and a target coordinate reference system, and provides
 * a {@link MathTransform} for transforming coordinates in the source coordinate reference
 * system to coordinates in the target coordinate reference system. These coordinate
 * systems can be ground or image coordinates. In general mathematics, "transformation"
 * is the general term for mappings between coordinate systems (see tensor analysis).
 * <br><br>
 * For a ground coordinate point, if the transformation depends only on mathematically
 * derived parameters (as in a cartographic projection), then this is an ISO conversion.
 * If the transformation depends on empirically derived parameters (as in datum
 * transformations), then this is an ISO transformation.
 *
 * @version 1.00
 * @author OpenGIS (www.opengis.org)
 * @author Martin Desruisseaux
 *
 * @see org.opengis.ct.CT_CoordinateTransformation
 */
public class CoordinateTransformation extends Info {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -1850470924499685544L;

    /**
     * The source coordinate system.
     */
    private final CoordinateSystem sourceCS;

    /**
     * The destination coordinate system.
     */
    private final CoordinateSystem targetCS;

    /**
     * The transform type.
     */
    private final TransformType type;

    /**
     * The underlying math transform, or <code>null</code> if it
     * doesn't has been constructed yet.   If <code>null</code>,
     * then subclass <strong>must</strong> initialize this field
     * the first time {@link #getMathTransform} is invoked.
     */
    protected MathTransform transform;

    /**
     * The inverse transform. This field
     * will be computed only when needed.
     */
    transient CoordinateTransformation inverse;

    /**
     * Construct a coordinate transformation.
     *
     * @param name      The coordinate transformation name, or <code>null</code>
     *                  for an automatically generated name.
     * @param sourceCS  The source coordinate system.
     * @param targetCS  The destination coordinate system.
     * @param type      The transform type.
     * @param transform The math transform.  This argument is allowed to
     *                  be <code>null</code> only if this constructor is
     *                  invoked from within a subclass constructor. In
     *                  this case, the subclass <strong>must</strong>
     *                  construct a math transform no later than the first
     *                  time {@link #getMathTransform} is invoked.
     */
    public CoordinateTransformation( final String name, final CoordinateSystem sourceCS,
                                    final CoordinateSystem targetCS, final TransformType type,
                                    final MathTransform transform ) {
        super( ( name != null ) ? name : "" );
        this.sourceCS = sourceCS;
        this.targetCS = targetCS;
        this.type = type;
        this.transform = transform;
        ensureNonNull( "sourceCS", sourceCS );
        ensureNonNull( "targetCS", targetCS );
        ensureNonNull( "type", type );
        if ( getClass().equals( CoordinateTransformation.class ) ) {
            ensureNonNull( "transform", transform );
        }
        if ( transform.getDimSource() != sourceCS.getDimension() ) {
            throw new IllegalArgumentException( "sourceCS" );
        }
        if ( transform.getDimTarget() != targetCS.getDimension() ) {
            throw new IllegalArgumentException( "targetCS" );
        }
    }

    /**
     * Gets the name of this coordinate transformation.
     *
     * @param locale The desired locale, or <code>null</code> for the default locale.
     */
    public String getName( final Locale locale ) {
        final String name = super.getName( locale );
        if ( name.length() != 0 )
            return name;
        else if ( transform instanceof AbstractMathTransform )
            return ( (AbstractMathTransform) transform ).getName( locale );
        else
            return sourceCS.getName( locale ) + "\u00A0\u21E8\u00A0" + targetCS.getName( locale );
    }

    /**
     * Gets the source coordinate system.
     *
     * @see org.opengis.ct.CT_CoordinateTransformation#getSourceCS()
     */
    public CoordinateSystem getSourceCS() {
        return sourceCS;
    }

    /**
     * Gets the target coordinate system.
     *
     * @see org.opengis.ct.CT_CoordinateTransformation#getTargetCS()
     */
    public CoordinateSystem getTargetCS() {
        return targetCS;
    }

    /**
     * Gets the semantic type of transform.
     * For example, a datum transformation or a coordinate conversion.
     *
     * @see org.opengis.ct.CT_CoordinateTransformation#getTransformType()
     */
    public TransformType getTransformType() {
        return type;
    }

    /**
     * Gets the math transform. The math transform will transform positions in
     * the source coordinate system into positions in the target coordinate system.
     *
     * @see org.opengis.ct.CT_CoordinateTransformation#getMathTransform()
     */
    public MathTransform getMathTransform() {
        if ( transform != null ) {
            return transform;
        }
        throw new IllegalStateException();
    }

    /**
     * Returns the inverse transform of this object.
     */
    public synchronized CoordinateTransformation inverse()
                            throws NoninvertibleTransformException {
        if ( inverse == null ) {
            inverse = new Inverse( this );
        }
        return inverse;
    }

    /**
     * The inverse coordinate transformation. This class override
     * {@link #getName} in order to delegate part of the call to
     * the underlying direct transformation.
     *
     * @version 1.0
     * @author Martin Desruisseaux
     */
    private static final class Inverse extends CoordinateTransformation {
        /**
         * Construct a coordinate transformation.
         */
        public Inverse( final CoordinateTransformation transform )
                                throws NoninvertibleTransformException {
            super( null, transform.getTargetCS(), transform.getSourceCS(),
                   transform.getTransformType(), transform.getMathTransform().inverse() );
            this.inverse = transform;
        }

        /**
         * Gets the name of this coordinate transformation.
         */
        public String getName( final Locale locale ) {
            return Resources.getResources( locale ).getString( ResourceKeys.INVERSE_$1,
                                                               this.inverse.getName( locale ) );
        }
    }

    /**
     * Returns a hash value for this
     * coordinate transformation.
     */
    public int hashCode() {
        int code = 7851236;
        CoordinateSystem cs;
        if ( ( cs = getSourceCS() ) != null )
            code = code * 37 + cs.hashCode();
        if ( ( cs = getTargetCS() ) != null )
            code = code * 37 + cs.hashCode();
        return code;
    }

    /**
     * Compares the specified object with this coordinate transformation
     * for equality.  The default implementation compare name, transform
     * type, source and target coordinate systems. It doesn't compare the
     * math transform, since it should be equivalents if the above mentionned
     * parameters are equal.
     */
    public boolean equals( final Object object ) {
        if ( object == this )
            return true;
        if ( super.equals( object ) ) {
            final CoordinateTransformation that = (CoordinateTransformation) object;
            return Utilities.equals( this.getTransformType(), that.getTransformType() )
                   && Utilities.equals( this.getSourceCS(), that.getSourceCS() )
                   && Utilities.equals( this.getTargetCS(), that.getTargetCS() );
        }
        return false;
    }

}
