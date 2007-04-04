//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/DescribeRecord.java,v 1.17 2006/10/31 17:12:56 mschneider Exp $
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
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 
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

package org.deegree.ogcwebservices.csw.discovery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.AbstractCSWRequest;
import org.w3c.dom.Element;

/**
 * The mandatory DescribeRecord operation allows a client to discover elements of the information
 * model supported by the target catalogue service. The operation allows some or all of the
 * information model to be described.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.17 $, $Date: 2006/10/31 17:12:56 $
 */
public class DescribeRecord extends AbstractCSWRequest {

    private static final long serialVersionUID = 6554937884331546780L;

    private static final ILogger LOG = LoggerFactory.getLogger( DescribeRecord.class );

    private Map namespaceMappings;

    private String[] typeNames;

    private String outputFormat;

    private URI schemaLanguage;

    /**
     * creates a GetRecords request from the XML fragment passed. The passed element must be valid
     * against the OGC CSW 2.0 GetRecords schema.
     * 
     * @param id
     *            unique ID of the request
     * @param root
     *            root element of the GetRecors request
     * @return
     */
    public static DescribeRecord create( String id, Element root )
                            throws MissingParameterValueException, InvalidParameterValueException,
                            OGCWebServiceException {

        DescribeRecordDocument document = new DescribeRecordDocument();
        document.setRootElement( root );
        DescribeRecord ogcRequest = document.parse( id );

        return ogcRequest;
    }

    /**
     * Creates a new <code>DecribeRecord</code> instance from the values stored in the submitted
     * Map. Keys (parameter names) in the Map must be uppercase.
     * 
     * @TODO evaluate vendorSpecificParameter
     * 
     * @param kvp
     *            Map containing the parameters
     * @exception InvalidParameterValueException
     * @throws MissingParameterValueException 
     */
    public static DescribeRecord create( Map kvp )
                            throws InvalidParameterValueException, MissingParameterValueException {
        LOG.entering();

        String id;
        String version;
        Map vendorSpecificParameter = new HashMap();
        Map namespaceMappings;
        String[] typeNames = new String[0];
        String outputFormat;
        URI schemaLanguage;
       
        // 'ID'-attribute (optional)
        id = getParam( "ID", kvp, "" );

        // 'VERSION'-attribute (mandatory)
        version = getRequiredParam( "VERSION", kvp );

        // 'NAMESPACE'-attribute (optional)
        namespaceMappings = getNSMappings( getParam( "NAMESPACE", kvp, null ) );

        // 'TYPENAME'-attribute (optional)
        String typeNamesString = getParam( "TYPENAME", kvp, null );
        if ( typeNamesString != null ) {
            typeNames = typeNamesString.split( "," );
        }

        // 'OUTPUTFORMAT'-attribute (optional)
        outputFormat = getParam( "OUTPUTFORMAT", kvp, "text/xml" );

        // 'SCHEMALANGUAGE'-attribute (optional)
        String schemaLanguageString = getParam( "SCHEMALANGUAGE", kvp, "XMLSCHEMA" );
        try {
            schemaLanguage = new URI( schemaLanguageString );
        } catch ( URISyntaxException e ) {
            String msg = "Value '" + schemaLanguageString
                         + "' for parameter 'SCHEMALANGUAGE' is invalid. Must "
                         + "denote a valid URI.";
            throw new InvalidParameterValueException( msg );
        }

        LOG.exiting();
        return new DescribeRecord( id, version, vendorSpecificParameter, namespaceMappings,
                                   typeNames, outputFormat, schemaLanguage );
    }

    /**
     * Creates a new <code>DescribeRecord</code> instance.
     * 
     * @param id
     * @param version
     * @param vendorSpecificParameter
     */
    DescribeRecord( String id, String version, Map vendorSpecificParameter ) {
        super( version, id, vendorSpecificParameter );
    }

    /**
     * Creates a new <code>DescribeRecord</code> instance.
     * 
     * @param id
     * @param version
     * @param vendorSpecificParameter
     * @param namespaceMappings
     * @param typeNames
     * @param outputFormat
     * @param schemaLanguage
     */
    DescribeRecord( String id, String version, Map vendorSpecificParameter, Map namespaceMappings,
                    String[] typeNames, String outputFormat, URI schemaLanguage ) {
        this( id, version, vendorSpecificParameter );
        this.namespaceMappings = namespaceMappings;
        this.typeNames = typeNames;
        this.outputFormat = outputFormat;
        this.schemaLanguage = schemaLanguage;
    }

    /**
     * Used to specify namespace(s) and their prefix(es). Format is [prefix:]uri. If prefix is not
     * specified, then this is the default namespace.
     * <p>
     * Zero or one (Optional). Include value for each namespace used by a TypeName. If not included,
     * all qualified names are in the default namespace
     */
    public Map getNamespaces() {
        return this.namespaceMappings;
    }

    /**
     * One or more qualified type names to be described.
     * <p>
     * Zero or one (Optional). Default action is to describe all types known to server.
     * 
     * @uml.property name="typeNames"
     */
    public String[] getTypeNames() {
        return this.typeNames;
    }

    /**
     * A MIME type indicating the format that the output document should have.
     * <p>
     * Zero or one (Optional). Default value is text/xml
     * 
     * @uml.property name="outputFormat"
     */
    public String getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * Default value is 'XMLSCHEMA'.
     * 
     * @uml.property name="schemaLanguage"
     */
    public URI getSchemaLanguage() {
        return this.schemaLanguage;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DescribeRecord.java,v $
 Revision 1.17  2006/10/31 17:12:56  mschneider
 Fixed parsing of TYPENAME parameter.

 Revision 1.16  2006/08/24 06:42:16  poth
 File header corrected

 Revision 1.15  2006/07/10 20:54:31  mschneider
 Fixed footer.

 ********************************************************************** */