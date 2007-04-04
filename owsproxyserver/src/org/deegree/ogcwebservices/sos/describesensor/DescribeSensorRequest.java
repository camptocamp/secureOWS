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
package org.deegree.ogcwebservices.sos.describesensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * 
 * represents a DescribeSensor Request
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class DescribeSensorRequest extends AbstractOGCWebServiceRequest {

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private String[] typeNames = null;

    private String outputFormat = null;

    /**
     * 
     * creates the Request by using a KVP Map
     * 
     * @param map
     * @return
     * @throws OGCWebServiceException
     */
    public static DescribeSensorRequest create( Map map ) throws OGCWebServiceException {

        // id was set by deegree
        String id = (String) map.get( "ID" );

        // optional Parameter
        String version = (String) map.get( "VERSION" );

        // optional Parameter, is fixed to "SOS"
        String service = (String) map.get( "SERVICE" );
        if ( ( service != null )
            && ( !service.equals( "SOS" ) ) ) {
            throw new OGCWebServiceException( "service must be 'SOS'" );
        }

        // optional Parameter, is fixed to "SensorML"
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

        return new DescribeSensorRequest( typeNames, "SensorML", version, id, null );

    }

    /**
     * creates the Request by using a XML Document
     * 
     * @param id
     * @param xml
     * @throws OGCWebServiceException
     * 
     */
    public static DescribeSensorRequest create( String id, Document doc )
        throws OGCWebServiceException {

        try {
            // optional Prameter
            String version = XMLTools.getNodeAsString( doc, "/sos:DescribeSensor/@version",
                nsContext, null );

            // optional Parameter, is fixed to "SCS"
            String service = XMLTools.getNodeAsString( doc, "/sos:DescribeSensor/@service",
                nsContext, null );
            if ( ( service != null )
                && ( !service.equals( "SOS" ) ) ) {
                throw new OGCWebServiceException( "service must be 'SOS'" );
            }

            // optional Parameter, is fixed to "SensorML"
            String outputFormat = XMLTools.getNodeAsString( doc,
                "/sos:DescribeSensor/@outputFormat", nsContext, null );
            if ( ( outputFormat != null )
                && ( !outputFormat.equals( "SensorML" ) ) ) {
                throw new OGCWebServiceException( "outputFormat must be 'SensorML'" );
            }

            // optional and unbounded
            List nl = XMLTools.getNodes( doc, "/sos:DescribeSensor/sos:TypeName", nsContext );
            ArrayList al = new ArrayList( nl.size() );
            for (int i = 0; i < nl.size(); i++) {
                al
                    .add( XMLTools
                        .getRequiredNodeAsString( (Node) nl.get( i ), "text()", nsContext ) );
            }

            return new DescribeSensorRequest( (String[]) al.toArray( new String[al.size()] ),
                "SensorML", version, id, null );

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
        throw new UnsupportedOperationException( "create( String , String , String , String[] ) not implemented");
    }

    /**
     * @param typeNames
     * @param outputFormat
     * @param version
     * @param id
     * 
     */
    private DescribeSensorRequest( String[] typeNames, String outputFormat, String version,
                                  String id, Map vendorSpecificParameter ) {

        super( version, id, vendorSpecificParameter );

        this.typeNames = typeNames;
        this.outputFormat = outputFormat;

    }

    /**
     * fixed 'SOS'
     */
    public String getServiceName() {
        return "SOS";
    }

    /**
     * 
     * @return
     */
    public String[] getTypeNames() {
        return typeNames;
    }

    /**
     * 
     * @return
     */
    public String getOutputFormat() {
        return outputFormat;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DescribeSensorRequest.java,v $
Revision 1.13  2006/08/24 06:42:17  poth
File header corrected

Revision 1.12  2006/08/07 10:48:57  poth
not used imports removed

Revision 1.11  2006/08/07 10:48:24  poth
never thrown exception removed

Revision 1.10  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
