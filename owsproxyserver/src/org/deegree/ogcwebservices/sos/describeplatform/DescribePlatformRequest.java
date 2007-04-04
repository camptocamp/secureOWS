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
 Aennchenstraße 19  
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos.describeplatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 
 * represent a DescribePlatformRequest
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class DescribePlatformRequest extends AbstractOGCWebServiceRequest {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private static final ILogger LOG = LoggerFactory.getLogger( DescribePlatformRequest.class );

    private String[] typeNames = null;

    private String outputFormat = null;

    /**
     * 
     * creates a DescribePlatform Request from a KVP Map
     * 
     * @param id
     * @param map
     * @throws OGCWebServiceException
     * 
     */
    public static DescribePlatformRequest create( Map map ) throws OGCWebServiceException {

        // added by deegree
        String id = (String) map.get( "ID" );

        // optional Parameter
        String version = (String) map.get( "VERSION" );

        // optional Parameter, fixed to "SOS"
        String service = (String) map.get( "SERVICE" );
        if ( ( service != null )
            && ( !service.equals( "SOS" ) ) ) {
            throw new OGCWebServiceException( "service must be 'SOS'" );
        }

        // optional Parameter, fixed to "SensorML"
        String outputFormat = (String) map.get( "OUTPUTFORMAT" );
        if ( ( outputFormat != null )
            && ( !outputFormat.equals( "SensorML" ) ) ) {
            throw new OGCWebServiceException( "outputFormat must be 'SensorML'" );
        }

        // optional and unbounded
        String[] typeNames = null;
        if ( map.get( "TYPENAMES" ) != null ) {
            String tmp = (String) map.get( "TYPENAMES" );
            typeNames = StringTools.toArray( tmp, ",", false );
        }

        return new DescribePlatformRequest( typeNames, "SensorML", version, id, null );

    }

    /**
     * creates a DescribePlatform Request from a XML Document
     * 
     * @param id
     * @param xml
     * @throws OGCWebServiceException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     * 
     */
    public static DescribePlatformRequest create( String id, Document doc )
        throws OGCWebServiceException {

        try {
            // optional Parameter
            String version = XMLTools.getNodeAsString( doc, "/sos:DescribePlatform/@version",
                nsContext, null );

            // optional Parameter, fixed to "SCS"
            String service = XMLTools.getNodeAsString( doc, "/sos:DescribePlatform/@service",
                nsContext, null );
            if ( ( service != null )
                && ( !service.equals( "SOS" ) ) ) {
                throw new OGCWebServiceException( "service must be 'SOS'" );
            }

            // optional Parameter, fixed to "SensorML"
            String outputFormat = XMLTools.getNodeAsString( doc,
                "/sos:DescribePlatform/@outputFormat", nsContext, null );
            if ( ( outputFormat != null )
                && ( !outputFormat.equals( "SensorML" ) ) ) {
                throw new OGCWebServiceException( "outputFormat must be 'SensorML'" );
            }

            // optional and unbounded
            ArrayList al = new ArrayList();
            List nl = XMLTools.getNodes( doc, "/sos:DescribePlatform/sos:TypeName", nsContext );
            for (int i = 0; i < nl.size(); i++) {
                al
                    .add( XMLTools
                        .getRequiredNodeAsString( (Node) nl.get( i ), "text()", nsContext ) );
            }

            String[] types = (String[]) al.toArray( new String[al.size()] );

            return new DescribePlatformRequest( types, "SensorML", version, id, null );

        } catch (Exception e) {
            e.printStackTrace();
            throw new OGCWebServiceException( "scs webservice failure" );
        }

    }

    /**
     * @param id
     * @param version
     * @param outputFormat
     * @param typeNames
     * 
     */
    public static void create( String id, String version, String outputFormat, String[] typeNames ) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param typeNames
     * @param outputFormat
     * @param version
     * @param id
     * @param vendorSpecificParameter
     * @throws InvalidParameterValueException
     */
    private DescribePlatformRequest( String[] typeNames, String outputFormat, String version,
                                    String id, Map vendorSpecificParameter ) {

        super( version, id, vendorSpecificParameter );

        this.typeNames = typeNames;
        this.outputFormat = outputFormat;

        StringBuffer sb = new StringBuffer( 200 );
        sb.append( "create DescribePlatformRequest: service= SCS" );
        sb.append( " version=" ).append( version ).append( " outputFormat=" );
        sb.append( outputFormat ).append( " id=" ).append( id );
        sb.append( " NumberOfTypeNames=" ).append( typeNames.length );
        LOG.logDebug( sb.toString() );

    }

    /**
     * fixed 'SOS'
     */
    public String getServiceName() {
        return "SOS";
    }

    public String[] getTypeNames() {
        return typeNames;
    }

    /**
     * returns the desired output format name
     * 
     * @return
     */
    public String getOutputFormat() {
        return outputFormat;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribePlatformRequest.java,v $
Revision 1.13  2006/08/24 06:42:16  poth
File header corrected

Revision 1.12  2006/08/07 10:07:08  poth
never thrown exception removed

Revision 1.11  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
