/*$************************************************************************************************
 **
 ** $Id: SampleDimension.java,v 1.4 2006/11/26 18:17:49 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/Attic/SampleDimension.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage;

/**
 * Contains information for an individual sample dimension of {@linkplain Coverage coverage}.
 * This interface is applicable to any coverage type.
 * For {@linkplain org.opengis.coverage.grid.GridCoverage grid coverages},
 * the sample dimension refers to an individual band.
 *
 * @UML abstract CV_SampleDimension
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @revisit {@link #getScale} and {@link #getOffset} are not sufficient; not all relation
 *          are linear (e.g. chlorophyll data from Nasa). We should consider to use the
 *          more general <code>sampleToGeophysics</code> attribute from Geotools instead.
 */
public interface SampleDimension {
    /**
     * Sample dimension title or description.
     * This string may be null or empty if no description is present.
     *
     * @return The sample dimension title or description.
     * @UML mandatory description
     */
    String getDescription();

    /**
     * A code value indicating grid value data type.
     * This will also indicate the number of bits for the data type.
     *
     * @return A code value indicating grid value data type.
     * @UML mandatory sampleDimensionType
     */
    SampleDimensionType getSampleDimensionType();

    /**
     * Sequence of category names for the values contained in a sample dimension.
     * This allows for names to be assigned to numerical values.
     * The first entry in the sequence relates to a cell value of zero.
     * For grid coverages, category names are only valid for a classified grid data.
     *
     * For example:<br>
     *  <UL>
     *    <li>0 Background</li>
     *    <li>1 Water</li>
     *    <li>2 Forest</li>
     *    <li>3 Urban</li>
     *  </UL>
     * Note: If no category names exist, an empty sequence is returned.
     *
     * @return The sequence of category names for the values contained in a sample dimension.
     * @UML mandatory categoryNames
     */
    String[] getCategoryNames();

    /**
     * Color interpretation of the sample dimension.
     * A sample dimension can be an index into a color palette or be a color model
     * component. If the sample dimension is not assigned a color interpretation the
     * value is {@link ColorInterpretation#UNDEFINED UNDEFINED}.
     *
     * @return The color interpretation of the sample dimension.
     * @UML mandatory colorInterpretation
     */
    ColorInterpretation getColorInterpretation();

    /**
     * Indicates the type of color palette entry for sample dimensions which have a
     * palette. If a sample dimension has a palette, the color interpretation must
     * be {@link ColorInterpretation#GRAY_INDEX GRAY_INDEX}
     * or {@link ColorInterpretation#PALETTE_INDEX PALETTE_INDEX}.
     * A palette entry type can be Gray, RGB, CMYK or HLS.
     *
     * @return The type of color palette entry for sample dimensions which have a palette.
     * @UML mandatory paletteInterpretation
     */
    PaletteInterpretation getPaletteInterpretation();

    /**
     * Color palette associated with the sample dimension.
     * A color palette can have any number of colors.
     * See palette interpretation for meaning of the palette entries.
     * If the grid coverage has no color palette, <code>null</code> will be returned.
     *
     * @return The color palette associated with the sample dimension.
     * @UML mandatory palette
     *
     * @see #getPaletteInterpretation
     * @see #getColorInterpretation
     * @see java.awt.image.IndexColorModel
     */
    int[][] getPalette();

    /**
     * Values to indicate no data values for the sample dimension.
     * For low precision sample dimensions, this will often be no data values.
     *
     * @return The values to indicate no data values for the sample dimension.
     * @UML mandatory noDataValue
     *
     * @see #getMinimumValue
     * @see #getMaximumValue
     */
    double[] getNoDataValues();

    /**
     * The minimum value occurring in the sample dimension.
     * If this value is not available, this value can be determined from the
     * {@link org.opengis.coverage.processing.GridAnalysis#getMinValue} operation.
     * This value can be empty if this value is not provided by the implementation.
     *
     * @return The minimum value occurring in the sample dimension.
     * @UML mandatory minimumValue
     *
     * @see #getMaximumValue
     * @see #getNoDataValues
     */
    double getMinimumValue();

    /**
     * The maximum value occurring in the sample dimension.
     * If this value is not available, this value can be determined from the
     * {@link org.opengis.coverage.processing.GridAnalysis#getMaxValue} operation.
     * This value can be empty if this value is not provided by the implementation.
     *
     * @return The maximum value occurring in the sample dimension.
     * @UML mandatory maximumValue
     *
     * @see #getMinimumValue
     * @see #getNoDataValues
     */
    double getMaximumValue();

    /**
     * The unit information for this sample dimension.
     * This interface typically is provided with grid coverages which represent
     * digital elevation data.
     * This value will be <code>null</code> if no unit information is available.
     *
     * @return The unit information for this sample dimension.
     * @UML mandatory units
     */
    String getUnits();

    /**
     * Offset is the value to add to grid values for this sample dimension.
     * This attribute is typically used when the sample dimension represents
     * elevation data. The default for this value is 0.
     *
     * @return The offset is the value to add to grid values for this sample dimension.
     * @UML mandatory offset
     *
     * @see #getScale
     */
    double getOffset();

    /**
     * Scale is the value which is multiplied to grid values for this sample dimension.
     * This attribute is typically used when the sample dimension represents elevation
     * data. The default for this value is 1.
     *
     * @return The scale.
     * @UML mandatory scale
     *
     * @see #getOffset
     */
    double getScale();

    /**
     * The transform which is applied to grid values for this sample dimension.
     * This transform is often defined as
     * <var>y</var> = {@linkplain #getOffset offset} + {@link #getScale scale}&times;<var>x</var> where
     * <var>x</var> is the grid value and <var>y</var> is the geophysics value.
     * However, this transform may also defines more complex relationship, for
     * example a logarithmic one. In order words, this transform is a generalization of
     * {@link #getScale}, {@link #getOffset} and {@link #getNoDataValues} methods.
     *
     * @return The transform from sample to geophysics values, or <code>null</code> if
     *         it doesn't apply.
     *
     * @see #getScale
     * @see #getOffset
     * @see #getNoDataValues
     */
    //MathTransform1D getSampleToGeophysics();
    /**
     * The list of metadata keywords for a sample dimension.
     * If no metadata is available, the sequence will be empty.
     *
     * @return The list of metadata keywords for a sample dimension.
     * @UML mandatory metadataNames
     *
     * @see #getMetadataValue
     * @see javax.media.jai.PropertySource#getPropertyNames()
     */
    String[] getMetaDataNames();

    /**
     * Retrieve the metadata value for a given metadata name.
     *
     * @param  name Metadata keyword for which to retrieve metadata.
     * @return The metadata value for a given metadata name.
     * @throws MetadataNameNotFoundException if there is no value for the specified metadata name.
     * @UML operation getMetadataValue
     *
     * @see #getMetaDataNames
     * @see javax.media.jai.PropertySource#getProperty
     */
    String getMetadataValue( String name )
                            throws MetadataNameNotFoundException;
}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SampleDimension.java,v $
 Revision 1.4  2006/11/26 18:17:49  poth
 unnecessary cast removed / code formatting

 Revision 1.3  2006/07/13 06:28:31  poth
 comment footer added

 ********************************************************************** */
