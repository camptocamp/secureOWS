//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/DescribeFeatureType.java,v 1.32 2006/11/07 11:09:36 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wfs.operation;

import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.KVP2Map;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Element;

/**
 * Represents a <code>DescribeFeatureType</code> request to a web feature service.
 * <p>
 * The function of the DescribeFeatureType interface is to provide a client the means to request a
 * schema definition of any feature type that a particular WFS can service. The description that is
 * generated will define how a WFS expects a client application to express the state of a feature
 * to be created or the new state of a feature to be updated. The result of a DescribeFeatureType
 * request is an XML document, describing one or more feature types serviced by the WFS.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.32 $, $Date: 2006/11/07 11:09:36 $
 */
public class DescribeFeatureType extends AbstractWFSRequest {

    private static final long serialVersionUID = 4403179045869238426L;

    private String outputFormat;

    private QualifiedName[] typeNames;

    /**
     * Creates a new <code>DescribeFeatureType</code> instance.
     * 
     * @param version
     * @param id
     * @param handle
     * @param outputFormat
     * @param typeNames
     */
    DescribeFeatureType( String version, String id, String handle, String outputFormat,
                        QualifiedName[] typeNames ) {
        super( version, id, handle, null );
        this.outputFormat = outputFormat;
        this.typeNames = typeNames;
    }

    /**
     * Creates a <code>DescribeFeatureType</code> instance from a document that contains the DOM
     * representation of the request.
     * 
     * @param id
     * @param root element that contains the DOM representation of the request
     * @return DescribeFeatureType instance
     * @throws OGCWebServiceException
     */
    public static DescribeFeatureType create( String id, Element root )
                            throws OGCWebServiceException {
        DescribeFeatureTypeDocument doc = new DescribeFeatureTypeDocument();
        doc.setRootElement( root );
        DescribeFeatureType request;
        try {
            request = doc.parse( id );
        } catch ( Exception e ) {
            throw new OGCWebServiceException( "DescribeFeatureType", e.getMessage() );
        }
        return request;
    }

    /**
     * Creates a new <code>DescribeFeatureType</code> instance from the given key-value pair encoded
     * request.
     * 
     * @param id request identifier
     * @param request
     * @return new <code>DescribeFeatureType</code> request
     * @throws InvalidParameterValueException 
     * @throws InconsistentRequestException 
     */
    public static DescribeFeatureType create( String id, String request )
                            throws InconsistentRequestException, InvalidParameterValueException {
        Map<String, String> map = KVP2Map.toMap( request );
        map.put( "ID", id );
        return create( map );
    }

    /**
     * Creates a new <code>DescribeFeatureType</code> request from the given map.
     * 
     * @param request
     * @return new <code>DescribeFeatureType</code> request
     * @throws InconsistentRequestException
     * @throws InvalidParameterValueException
     */
    public static DescribeFeatureType create( Map<String, String> request )
                            throws InconsistentRequestException, InvalidParameterValueException {

        checkServiceParameter( request );
        String version = checkVersionParameter( request );
        String outputFormat = getParam( "OUTPUTFORMAT", request, FORMAT_GML3 );
        QualifiedName[] typeNames = extractTypeNames( request );

        // TODO generate unique request id
        String id = null;
        return new DescribeFeatureType( version, id, null, outputFormat, typeNames );
    }

    /**
     * Returns the value of the outputFormat attribute.
     * <p>
     * The outputFormat attribute, is used to indicate the schema description language that should
     * be used to describe a feature schema. The only mandated format is XML-Schema denoted by the
     * XMLSCHEMA element; other vendor specific formats specified in the capabilities document are
     * also possible.
     * 
     * @return the value of the outputFormat attribute.
     */
    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * Returns the names of the feature types for which the schema is requested.
     * <p>
     * @return the names of the feature types for which the schema is requested.
     */
    public QualifiedName[] getTypeNames() {
        return typeNames;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String ret = this.getClass().getName() + ":\n";
        ret += ( outputFormat + "\n" );
        if ( typeNames != null ) {
            for ( int i = 0; i < typeNames.length; i++ ) {
                ret += ( typeNames[i] + "\n" );
            }
        }
        return ret;
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: DescribeFeatureType.java,v $
 * Revision 1.32  2006/11/07 11:09:36  mschneider
 * Added exceptions in case anything other than the 1.1.0 format is requested.
 *
 * Revision 1.31  2006/10/11 18:02:31  mschneider
 * Fixed NPE in #toString().
 *
 * Revision 1.30  2006/10/06 14:16:09  mschneider
 * Removed BOM (Byte Order Mark).
 *
 * Revision 1.29  2006/10/05 10:09:55  mschneider
 * Javadoc fixes.
 *
 * Revision 1.28  2006/08/07 10:05:33  poth
 * never thrown exception removed
 *
 * Revision 1.27  2006/08/07 10:04:58  poth
 * not used imports removed
 *
 * Revision 1.26  2006/07/05 23:24:46  mschneider
 * Uses extractTypeNames() from superclass now.
 *
 * Revision 1.25  2006/07/04 14:48:11  mschneider
 * Moved check of SERVICE parameter to superclass method.
 *
 * Revision 1.24  2006/06/07 17:14:47  mschneider
 * Improved javadoc.
 *
 * Revision 1.23  2006/06/06 17:05:41  mschneider
 * Initial version. Outfactored parser.
 *
 * Revision 1.22  2006/05/16 16:26:08  mschneider
 * Renamed WFSRequestBase to AbstractWFSRequest.
 *
 * Revision 1.21  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.20  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.19  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.18  2005/11/22 18:08:04  deshmukh
 * Transaction WFS.. work in progress
 * Revision 1.17 2005/11/21 15:13:29 deshmukh Check for mandatory
 * version attribute Revision 1.16 2005/11/17 13:07:37 deshmukh changes in Exception catching
 * 
 * Revision 1.15 2005/11/16 13:44:59 mschneider Merge of wfs development branch.
 * 
 * Revision 1.14.2.6 2005/11/16 07:26:06 deshmukh Corrected error in method Revision 1.14.2.5
 * 2005/11/15 14:36:19 deshmukh QualifiedName modifications Revision 1.14.2.4 2005/11/15 13:36:55
 * deshmukh Modified Object to FeatureProperty Revision 1.14.2.3 2005/11/08 16:33:18 deshmukh
 * typeName changed from String[] to QualifiedName[] Revision 1.14.2.2 2005/11/07 16:45:08 deshmukh
 * NodeList to List Revision 1.14.2.1 2005/11/07 15:38:04 mschneider Refactoring: use
 * NamespaceContext instead of Node for namespace bindings.
 * 
 * Revision 1.14 2005/08/26 21:11:29 poth no message
 * 
 * Revision 1.2 2005/07/22 15:17:38 mschneider Added constants for output formats. Revision 1.1
 * 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.12 2005/03/09 11:55:47 mschneider *** empty log message ***
 * 
 * Revision 1.11 2005/03/01 16:20:15 poth no message
 * 
 * Revision 1.10 2005/03/01 14:39:08 mschneider *** empty log message ***
 * 
 * Revision 1.9 2005/02/28 13:03:21 poth no message
 * 
 * Revision 1.8 2005/02/28 07:43:40 poth no message
 * 
 * Revision 1.7 2005/02/26 16:57:29 poth no message
 * 
 * Revision 1.6 2005/02/21 13:53:48 poth no message
 * 
 * Revision 1.5 2005/02/21 11:24:33 poth no message
 * 
 * Revision 1.4 2005/02/18 20:54:18 poth no message
 * 
 * Revision 1.3 2005/02/07 07:56:57 poth no message
 * 
 * Revision 1.2 2005/02/03 21:35:08 poth no message
 * 
 * Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.3 2004/08/24 11:48:26 tf no message Revision 1.2 2004/06/16 09:13:37 ap no message
 * 
 * Revision 1.1 2004/06/07 13:38:34 tf code adapted to wfs1 refactoring Revision 1.4 2004/03/12
 * 15:56:49 poth no message
 * 
 * Revision 1.3 2003/11/11 17:12:56 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:53 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:26 poth no message
 * 
 * Revision 1.9 2002/08/15 10:01:40 ap no message
 * 
 * Revision 1.8 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.7 2002/08/05 16:11:30 ap no message
 * 
 * Revision 1.6 2002/05/14 14:39:51 ap no message
 * 
 * Revision 1.5 2002/05/13 16:11:02 ap no message
 * 
 * Revision 1.4 2002/05/06 07:56:41 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 ********************************************************************** */