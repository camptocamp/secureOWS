/*$************************************************************************************************
 **
 ** $Id: GridAnalysis.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/processing/Attic/GridAnalysis.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.processing;

// OpenGIS direct dependencies
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.pt.PT_Matrix;


/**
 * Performs various analysis operations on a grid coverage.
 * Such processing functionality includes histogram calculation,
 * grid coverage covariance and other statistical measurements.
 *
 * @UML abstract GP_GridAnalysis
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 *
 * @revisit All methods except {@link #getCorrelation} work on a particuler sample dimension.
 *          Why not defines those methods right into {@link org.opengis.coverage.SampleDimension}?
 */
public interface GridAnalysis extends GridCoverage {
    /**
     * Determine the histogram of the grid values for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension to be histogrammed.
     * @param minimumEntryValue Minimum value stored in the first histogram entry.
     * @param maximumEntryValue Maximum value stored in the last histogram entry.
     * @param numberEntries Number of entries in the histogram.
     * @return The histogram of the grid values for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation histogram
     *
     * @see javax.media.jai.Histogram
     */
    int[] getHistogram(int sampleDimension, double minimumEntryValue, double maximumEntryValue, int numberEntries)
            throws InvalidSampleDimensionException;

    /**
     * Determine the minimum grid value for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return the minimum grid value for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation minValue
     */
    double getMinValue(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the maximum grid value for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return the maximum grid value for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation maxValue
     */
    double getMaxValue(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the mean grid value for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return the mean grid value for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation meanValue
     */
    double getMeanValue(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the median grid value for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return the median grid value for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation medianValue
     */
    double getMedianValue(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the mode grid value for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return the mode grid value for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation modeValue
     */
    double getModeValue(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the standard deviation from the mean of the grid values for a sample dimension.
     *
     * @param sampleDimension Index of sample dimension.
     * @return he standard deviation from the mean of the grid values for a sample dimension.
     * @throws InvalidSampleDimensionException if the sample dimension index is out of bounds.
     * @UML operation stdDev
     */
    double getStandardDeviation(int sampleDimension) throws InvalidSampleDimensionException;

    /**
     * Determine the correlation between sample dimensions in the grid.
     *
     * @return the correlation between sample dimensions in the grid.
     * @UML operation correlation
     */
    PT_Matrix getCorrelation();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GridAnalysis.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
