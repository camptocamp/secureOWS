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
package org.deegree.model.coverage.grid;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.opengis.coverage.grid.Format;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.OperationParameter;
import org.opengis.parameter.ParameterNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implementation of @see org.opengis.coverage.grid.GridCoverageWriter for
 * writing a GridCoverage as GML document to a defined destioation
 * 
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/07/05 12:58:09 $
 *
 * @since 2.0
 */
public class GMLGridCoverageWriter extends AbstractGridCoverageWriter {
    
    private static ILogger LOG = LoggerFactory.getLogger( GMLGridCoverageWriter.class ); 
    
    private URL template = 
        GMLGridCoverageWriter.class.getResource( "gml_rectifiedgrid_template.xml" ); 
    
    
    /**
     * 
     * @param destination
     * @param metadata
     * @param subNames
     * @param currentSubname
     * @param format
     */
    public GMLGridCoverageWriter(Object destination, Map metadata, String[] subNames, 
                                 String currentSubname, Format format) {
        super(destination, metadata, subNames, currentSubname, format);
        
    }

    /**
     * disposes all resources assigned to a GMLGridCoverageWriter instance.
     * For most cases this will be IO-resources
     */
    public void dispose() throws IOException {
        
    }

    /**
     * @param coverage
     * @param parameters must contain the servlet URL within the first field;
     *                   all other fields must contain the required parameters
     *                   for a valid GetCoverage request
     */
    public void write(GridCoverage coverage, GeneralParameterValue[] parameters) 
                                                throws InvalidParameterNameException, 
                                                       InvalidParameterValueException, 
                                                       ParameterNotFoundException, IOException {
        XMLFragment xml = new XMLFragment();
        try {            
            xml.load( template );
            Element root = xml.getRootElement();
            NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
            
            String xpath = "gml:rectifiedGridDomain/gml:RectifiedGrid/gml:limits/gml:GridEnvelope/gml:low";
            Element element = (Element)XMLTools.getNode( root, xpath, nsc );
            double x = coverage.getEnvelope().minCP.ord[0];
            double y = coverage.getEnvelope().minCP.ord[1];
            Node node = root.getOwnerDocument().createTextNode( Double.toString( x ) + ' ' + 
                                                                Double.toString( y ) );
            element.appendChild( node );
            
            xpath = "gml:rectifiedGridDomain/gml:RectifiedGrid/gml:limits/gml:GridEnvelope/gml:high";
            element = (Element)XMLTools.getNode( root, xpath, nsc );
            x = coverage.getEnvelope().maxCP.ord[0];
            y = coverage.getEnvelope().maxCP.ord[1];            
            node = root.getOwnerDocument().createTextNode( Double.toString( x ) + ' ' + 
                                                           Double.toString( y ) );
            element.appendChild( node );
            
            xpath = "gml:rectifiedGridDomain/gml:RectifiedGrid/gml:origin/gml:Point";
            element = (Element)XMLTools.getNode( root, xpath, nsc );            
            element.setAttribute( "srsName", coverage.getCoordinateReferenceSystem().getName() );
            
            xpath = "gml:rectifiedGridDomain/gml:RectifiedGrid/gml:origin/gml:Point/gml:pos";
            element = (Element)XMLTools.getNode( root, xpath, nsc );
            x = coverage.getEnvelope().minCP.ord[0];
            y = coverage.getEnvelope().minCP.ord[1];
            node = root.getOwnerDocument().createTextNode( Double.toString( x ) + ' ' + 
                                                           Double.toString( y ) );
            element.appendChild( node );
            
            double[] res = calcGridResolution( coverage, parameters );
                                      
            xpath = "gml:rectifiedGridDomain/gml:RectifiedGrid/gml:offsetVector";
            List list = XMLTools.getNodes( root, xpath, nsc );      
            for (int i = 0; i < list.size(); i++) {
                element = (Element)list.get( i );
                element.setAttribute( "srsName", coverage.getCoordinateReferenceSystem().getName() );
                if ( i == 0 ) {
                    node = root.getOwnerDocument().createTextNode( res[i] + " 0" );
                    element.appendChild( node );
                } else if ( i == 1 ) {
                    node = root.getOwnerDocument().createTextNode( "0 " + res[i] );
                    element.appendChild( node );
                } else if ( i == 2 ) {
                    node = root.getOwnerDocument().createTextNode( "0 0 " + res[i] );
                    element.appendChild( node );
                }
            }   
            
            xpath = "gml:rangeSet/gml:File/gml:fileName";
            element = (Element)XMLTools.getNode( root, xpath, nsc );
            StringBuffer sb = new StringBuffer( 300 );            
            OperationParameter op = (OperationParameter)parameters[0].getDescriptor();
            sb.append( op.getDefaultValue() ).append( '?' );            
            for (int i = 1; i < parameters.length; i++) {
                //OperationParameter
                op = (OperationParameter)parameters[i].getDescriptor();
                sb.append( op.getName( Locale.getDefault() ) );
                sb.append( '=' ).append( op.getDefaultValue() );
                if ( i < parameters.length - 1) {
                    sb.append( '&' );
                }
            }
            node = root.getOwnerDocument().createCDATASection( sb.toString() );
            element.appendChild( node );            
        } catch (XMLParsingException e) {
            LOG.logError( "could not parse GMLGridCoverage response template", e );
            throw new InvalidParameterValueException( "", e.getMessage(), "" );
        } catch (Exception e) {
            LOG.logError( "could not write GMLGridCoverage", e );
            throw new InvalidParameterValueException( "", e.getMessage(), "" );
        }
        xml.write( ((OutputStream)destination) );
        
    }

    /**
     * returns the resolution of the grid in x- and y- directory
     * @param coverage
     * @param parameters
     * @return
     */
    private double[] calcGridResolution( GridCoverage coverage, GeneralParameterValue[] parameters ) {
        double wx = coverage.getEnvelope().maxCP.ord[0] - coverage.getEnvelope().minCP.ord[0];
        double wy = coverage.getEnvelope().maxCP.ord[1] - coverage.getEnvelope().minCP.ord[1];
        Integer width = (Integer)getNamedParameter( parameters, "width" ).getDefaultValue();
        Integer height = (Integer)getNamedParameter( parameters, "height" ).getDefaultValue();
        double dx = wx / width.doubleValue();
        double dy = wy / height.doubleValue();
        double[] res = new double[] { dx, dy };
        return res;
    }
    
    /**
     * selects the parameter matching the passed name from the passed 
     * array
     * @param parameters
     * @param name
     * @return
     */
    private OperationParameter getNamedParameter(GeneralParameterValue[] parameters, String name) {
        for (int i = 0; i < parameters.length; i++) {
            //OperationParameter
            OperationParameter op = (OperationParameter)parameters[i].getDescriptor();
            if ( op.getName( Locale.getDefault() ).equals( name ) ) {
                return op;
            }
        }
        return null;
    }

    /**
     * 
     * @param coverage
     * @param xAxis
     * @param yAxis
     * @param parameters 
     */
    public void write(GridCoverage coverage, int xAxis, int yAxis, 
                      GeneralParameterValue[] parameters) throws InvalidParameterNameException, 
                                                       InvalidParameterValueException, 
                                                       ParameterNotFoundException, 
                                                       IOException {
        
        
        
    }

}
/* ***************************************************************************
* Changes to this class. What the people have been up to: 
* $Log: GMLGridCoverageWriter.java,v $
* Revision 1.11  2006/07/05 12:58:09  poth
* bug fix - creating offSetVectors for rectified grid corrected
*
* Revision 1.10  2006/06/12 08:09:54  poth
* calculation of rectified grid completed
*
*************************************************************************** */