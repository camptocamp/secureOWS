/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

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
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.model.filterencoding;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.w3c.dom.Element;


/**
 * 
 * Encapsulates the information of a <PropertyIsBetween>-element (as defined in
 * Filter DTD).
 * 
 * @version $Revision: 1.10 $
 * @author Markus Schneider
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.10 $, $Date: 2006/08/30 08:37:34 $
 *
 * @since 2.0
 */
public class PropertyIsBetweenOperation extends ComparisonOperation {
    
    private static ILogger LOG = LoggerFactory.getLogger( PropertyIsBetweenOperation.class );

    private PropertyName propertyName;

    private Expression lowerBoundary;

    private Expression upperBoundary;

    public PropertyIsBetweenOperation( PropertyName propertyName, Expression lowerBoundary,
                                      Expression upperBoundary ) {
        super( OperationDefines.PROPERTYISBETWEEN );
        this.propertyName = propertyName;
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
    }

    /**
     * Given a DOM-fragment, a corresponding Operation-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Operation buildFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name equals 'PropertyIsBetween'
        if ( !element.getLocalName().equals( "PropertyIsBetween" ) )
            throw new FilterConstructionException( "Name of element does not equal 'PropertyIsBetween'!" );

        ElementList children = XMLTools.getChildElements( element );
        if ( children.getLength() != 3 )
            throw new FilterConstructionException( "'PropertyIsBetween' requires exactly 3 elements!" );

        PropertyName propertyName = (PropertyName) PropertyName.buildFromDOM( children.item( 0 ) );
        Expression lowerBoundary = buildLowerBoundaryFromDOM( children.item( 1 ) );
        Expression upperBoundary = buildUpperBoundaryFromDOM( children.item( 2 ) );

        return new PropertyIsBetweenOperation( propertyName, lowerBoundary, upperBoundary );
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object (for the
     * LowerBoundary-element) is built. This method recursively calls other
     * buildFromDOM () - methods to validate the structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    private static Expression buildLowerBoundaryFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name equals 'LowerBoundary'
        if ( !element.getLocalName().equals( "LowerBoundary" ) )
            throw new FilterConstructionException( "Name of element does not equal 'LowerBoundary'!" );

        ElementList children = XMLTools.getChildElements( element );
        if ( children.getLength() != 1 )
            throw new FilterConstructionException( "'LowerBoundary' requires exactly 1 element!" );

        return Expression.buildFromDOM( children.item( 0 ) );
    }

    /**
     * Given a DOM-fragment, a corresponding Expression-object (for the
     * UpperBoundary-element) is built. This method recursively calls other
     * buildFromDOM () - methods to validate the structure of the DOM-fragment.
     * 
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    private static Expression buildUpperBoundaryFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name equals 'UpperBoundary'
        if ( !element.getLocalName().equals( "UpperBoundary" ) )
            throw new FilterConstructionException( "Name of element does not equal 'UpperBoundary'!" );

        ElementList children = XMLTools.getChildElements( element );
        if ( children.getLength() != 1 )
            throw new FilterConstructionException( "'UpperBoundary' requires exactly 1 element!" );

        return Expression.buildFromDOM( children.item( 0 ) );
    }

    /**
     * returns the name of the property that shall be compared to the boundaries
     * 
     */
    public PropertyName getPropertyName() {
        return propertyName;
    }

    /**
     * returns the lower boundary of the operation as an <tt>Expression</tt>
     * 
     */
    public Expression getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * returns the upper boundary of the operation as an <tt>Expression</tt>
     * 
     */
    public Expression getUpperBoundary() {
        return upperBoundary;
    }

    /** Produces an indented XML representation of this object. */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer( 500 );
        sb.append( "<ogc:" ).append( getOperatorName() ).append( ">" );
        sb.append( propertyName.toXML() );
        sb.append( "<ogc:LowerBoundary>" );
        sb.append( lowerBoundary.toXML() );
        sb.append( "</ogc:LowerBoundary>" );
        sb.append( "<ogc:UpperBoundary>" );
        sb.append( upperBoundary.toXML() );
        sb.append( "</ogc:UpperBoundary>" );
        sb.append( "</ogc:" ).append( getOperatorName() ).append( ">" );
        return sb;
    }

    /**
     * Calculates the <tt>PropertyIsBetween</tt> -Operation's logical value
     * based on the certain property values of the given <tt>Feature</tt>.
     * TODO: Improve datatype handling.
     * 
     * @param feature
     *            that determines the property values
     * @return true, if the <tt>Operation</tt> evaluates to true, else false
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public boolean evaluate( Feature feature )
                            throws FilterEvaluationException {

        Object lowerValue = lowerBoundary.evaluate( feature );
        if ( lowerValue instanceof String ) {
            lowerValue = Double.parseDouble( (String)lowerValue );
        }
        Object upperValue = upperBoundary.evaluate( feature );
        if ( upperValue instanceof String ) {
            upperValue = Double.parseDouble( (String)lowerValue );
        }
        
        if ( lowerValue == null || upperValue == null ) {
            // this is because datasource may contain null values for properties
            // that shall be applied to a 'is between' operation. This shall not
            // be treaded as an exception.
            return false;
        }
        
        Object thisValue = propertyName.evaluate( feature );
        if ( thisValue instanceof String ) {
            thisValue = Double.parseDouble( (String)lowerValue );
        }

        if ( !( lowerValue instanceof Number && upperValue instanceof Number && 
             thisValue instanceof Number ) ) {
            if ( thisValue == null ) {
               LOG.logInfo( "thisValue == null" );
            } else {
               LOG.logInfo( "thisValue > " + thisValue.getClass().getName() );
            }
            throw new FilterEvaluationException( "PropertyIsBetweenOperation can only be " +
                    "applied to numerical  expressions!" );
        }

        double d1 = ( (Number) lowerValue ).doubleValue();
        double d2 = ( (Number) upperValue ).doubleValue();
        double d3 = ( (Number) thisValue ).doubleValue();
        return d1 <= d3 && d3 <= d2;

    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PropertyIsBetweenOperation.java,v $
Revision 1.10  2006/08/30 08:37:34  poth
behavior changed; if one of the values to be compared is null, false will be returned instead of an exception

Revision 1.9  2006/08/28 20:15:49  poth
heuristic/workaround determining correct data type added

Revision 1.8  2006/07/07 15:10:24  poth
bug fix - check for Number instead of Double


********************************************************************** */