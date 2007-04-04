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
 53115 Bonn
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

import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.w3c.dom.Element;

/**
 * Encapsulates the information of a logical_ops entity (as defined in the
 * Filter DTD).
 * 
 * @version $Revision: 1.10 $
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.10 $, $Date: 2006/09/28 07:49:46 $
 *
 * @since 2.0
 */
public class LogicalOperation extends AbstractOperation {

    /** Arguments of the Operation. */
    private List<Operation> arguments;

    /**
     * Constructs a new LogicalOperation.
     * 
     * @see OperationDefines
     * 
     * @param operatorId
     * @param arguments
     */
    public LogicalOperation( int operatorId, List<Operation> arguments ) {
        super( operatorId );
        this.arguments = arguments;
    }

    /**
     * Returns the arguments of the operation. These are <tt>OperationsMetadata</tt>
     * as well. 
     */
    public List<Operation> getArguments() {
        return arguments;
    }

    /**
     * Given a DOM-fragment, a corresponding Operation-object is built. This
     * method recursively calls other buildFromDOM () - methods to validate the
     * structure of the DOM-fragment.
     * 
     * @param element
     * @return opertation
     * @throws FilterConstructionException
     *             if the structure of the DOM-fragment is invalid
     */
    public static Operation buildFromDOM( Element element )
                            throws FilterConstructionException {

        // check if root element's name is a known operator
        String name = element.getLocalName();
        int operatorId = OperationDefines.getIdByName( name );
        List<Operation> arguments = new ArrayList<Operation>();

        switch ( operatorId ) {
            case OperationDefines.AND:
            case OperationDefines.OR: {
                ElementList children = XMLTools.getChildElements( element );
                if ( children.getLength() < 2 )
                    throw new FilterConstructionException( "'" + name
                                                           + "' requires at least 2 elements!" );
                for ( int i = 0; i < children.getLength(); i++ ) {
                    Element child = children.item( i );
                    Operation childOperation = AbstractOperation.buildFromDOM( child );
                    arguments.add( childOperation );
                }
                break;
            }
            case OperationDefines.NOT: {
                ElementList children = XMLTools.getChildElements( element );
                if ( children.getLength() != 1 )
                    throw new FilterConstructionException( "'" + name + "' requires exactly 1 element!" );
                Element child = children.item( 0 );
                Operation childOperation = AbstractOperation.buildFromDOM( child );
                arguments.add( childOperation );
                break;
            }
            default: {
                throw new FilterConstructionException( "'" + name + "' is not a logical operator!" );
            }
        }
        return new LogicalOperation( operatorId, arguments );
    }

    /**
     * Produces an indented XML representation of this object. 
     * @return XML representation of opertation
     */
    public StringBuffer toXML() {
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<ogc:" ).append( getOperatorName() ).append( ">" );

        for ( int i = 0; i < arguments.size(); i++ ) {
            sb.append( arguments.get( i ).toXML() );
        }

        sb.append( "</ogc:" ).append( getOperatorName() ).append( ">" );
        return sb;
    }

    /**
     * Calculates the <tt>LogicalOperation</tt>'s logical value based on the
     * certain property values of the given <tt>Feature</tt>.
     * 
     * @param feature
     *            that determines the property values
     * @return true, if the <tt>LogicalOperation</tt> evaluates to true, else
     *         false
     * @throws FilterEvaluationException
     *             if the evaluation fails
     */
    public boolean evaluate( Feature feature )
                            throws FilterEvaluationException {
        switch ( getOperatorId() ) {
            case OperationDefines.AND: {
                for ( int i = 0; i < arguments.size(); i++ ) {
                    if ( !arguments.get( i ).evaluate( feature ) )
                        return false;
                }
                return true;
            }
            case OperationDefines.OR: {
                for ( int i = 0; i < arguments.size(); i++ ) {
                    if ( arguments.get( i ).evaluate( feature ) )
                        return true;
                }
                return false;
            }
            case OperationDefines.NOT: {
                return !arguments.get( 0 ).evaluate( feature );
            }
            default: {
                throw new FilterEvaluationException( "Unknown LogicalOperation encountered: '"
                                                     + getOperatorName() + "'" );
            }
        }
    }
}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: LogicalOperation.java,v $
 Revision 1.10  2006/09/28 07:49:46  poth
 Generics introduced for Filter - LogicalOperation and required adaptions

 Revision 1.9  2006/07/12 14:46:14  poth
 comment footer added

 ********************************************************************** */
