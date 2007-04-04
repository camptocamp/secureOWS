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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.sos.ComponentDescriptionDocument;
import org.deegree.ogcwebservices.sos.WFSRequestGenerator;
import org.deegree.ogcwebservices.sos.WFSRequester;
import org.deegree.ogcwebservices.sos.configuration.SOSDeegreeParams;
import org.deegree.ogcwebservices.sos.configuration.SourceServerConfiguration;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * gets the platform metadata from a xsl transformed wfs result
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 */

public class PlatformDescriptionDocument extends ComponentDescriptionDocument {

    private static final String XML_TEMPLATE = "DescribePlatformTemplate.xml";

    /**
     * creates an document from a template file
     */
    public void createEmptyDocument() throws IOException, SAXException {
        URL url = PlatformDescriptionDocument.class.getResource( XML_TEMPLATE );
        if ( url == null ) {
            throw new IOException( "The resource '"
                + XML_TEMPLATE + " could not be found." );
        }
        load( url );
    }

    /**
     * gets the platform descriptions from a wfs and transform it with a xslt script
     * 
     * @param sensors
     * @return
     * @throws InvalidParameterValueException
     * @throws OGCWebServiceException
     * @throws SAXException
     * @throws TransformerException
     */
    public PlatformMetadata[] getPlatform( SOSDeegreeParams deegreeParams, String[] typNames )
        throws OGCWebServiceException {

        try {

            // gets the documents from wfs server
            Document[] docs = getPlatformDescriptionDocuments( deegreeParams, typNames );

            ArrayList platformMetadata = new ArrayList( 1000 );

            for (int d = 0; d < docs.length; d++) {

                if ( docs[d] != null ) {

                    List nl = XMLTools.getNodes( docs[d], "sml:Platforms/sml:Platform", nsContext );

                    // process all platforms in document
                    for (int y = 0; y < nl.size(); y++) {

                        Node platformNode = (Node) nl.get( y );

                        // get identifiedAs
                        List identifierList = XMLTools.getNodes( platformNode, "sml:identifiedAs",
                            nsContext );
                        if ( ( identifierList == null )
                            || ( identifierList.size() <= 0 ) ) {
                            throw new XMLParsingException( "at least one identifiedAs required" );

                        }
                        ArrayList identifiers = new ArrayList( identifierList.size() );
                        for (int i = 0; i < identifierList.size(); i++) {
                            identifiers.add( getIdentifiedAs( (Node) identifierList.get( i ) ) );
                        }

                        // get ClassifiedAs
                        List classifierList = XMLTools.getNodes( platformNode, "sml:classifiedAs",
                            nsContext );
                        ArrayList classifiers = new ArrayList( classifierList.size() );
                        for (int i = 0; i < classifierList.size(); i++) {
                            classifiers.add( getClassifiedAs( (Node) classifierList.get( i ) ) );
                        }

                        // get attachedTo
                        String attachedTo = getAttachedTo( XMLTools.getNode( platformNode,
                            "sml:attachedTo", nsContext ) );

                        // get hasCRS
                        EngineeringCRS hasCRS = getHasCRS( XMLTools.getNode( platformNode,
                            "sml:hasCRS", nsContext ) );

                        // get locatedUsing
                        List locationModelList = XMLTools.getNodes( platformNode,
                            "sml:locatedUsing", nsContext );
                        ArrayList locationModels = new ArrayList( locationModelList.size() );
                        for (int i = 0; i < locationModelList.size(); i++) {
                            locationModels
                                .add( getLocatedUsing( (Node) locationModelList.get( i ) ) );
                        }

                        // get describedBy
                        ComponentDescription describedBy = getDescribedBy( XMLTools.getNode(
                            platformNode, "sml:describedBy", nsContext ) );

                        // get carries
                        List carriesList = XMLTools.getNodes( platformNode, "sml:carries", nsContext );
                        ArrayList carries = new ArrayList( carriesList.size() );
                        for (int i = 0; i < carriesList.size(); i++) {
                            String s = XMLTools.getRequiredNodeAsString(
                                (Node) carriesList.get( i ), "sml:Asset/text()", nsContext );
                            carries.add( s );
                        }

                        Identifier[] ids = new Identifier[identifiers.size()];
                        ids = (Identifier[]) identifiers.toArray( ids );
                        Classifier[] cls = new Classifier[classifiers.size()];
                        cls = (Classifier[]) classifiers.toArray( cls );
                        LocationModel[] lm = new LocationModel[locationModels.size()];
                        lm = (LocationModel[]) locationModels.toArray( lm );
                        String[] crrs = new String[carries.size()];
                        crrs = (String[]) carries.toArray( crrs );
                        PlatformMetadata pmd = new PlatformMetadata( ids, cls, hasCRS, lm,
                            describedBy, attachedTo, crrs );
                        // add act Metadata to ArrayList
                        platformMetadata.add( pmd );
                    }

                }
            }

            // return the Array with Sensormetadata
            PlatformMetadata[] pfmd = new PlatformMetadata[platformMetadata.size()];
            return (PlatformMetadata[]) platformMetadata.toArray( pfmd );

        } catch (Exception e) {
            e.printStackTrace();
            throw new OGCWebServiceException( "sos webservice failure" );
        }
    }

    /**
     * requests all servers which serves one of the requested platforms and returns the transformed
     * result docs
     * 
     * @param deegreeParams
     * @param typNames
     * @return
     * @throws IOException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParsingException
     * @throws TransformerException
     */
    private Document[] getPlatformDescriptionDocuments( SOSDeegreeParams dp, String[] typNames )
        throws IOException,
            SAXException,
            XMLParsingException,
            TransformerException,
            OGCWebServiceException {

        ArrayList resultDocuments = new ArrayList();

        Hashtable servers = new Hashtable();

        for (int t = 0; t < typNames.length; t++) {
            String sourceServerId = dp.getPlatformConfiguration( typNames[t] ).getSourceServerId();

            if ( servers.containsKey( sourceServerId ) ) {
                ( (ArrayList) servers.get( sourceServerId ) ).add( typNames[t] );
            } else {
                ArrayList temp = new ArrayList();
                temp.add( typNames[t] );
                servers.put( sourceServerId, temp );
            }

        }

        String[] keySet = (String[]) servers.keySet().toArray( new String[servers.keySet().size()] );

        // request all servers from servers hashtable
        for (int i = 0; i < keySet.length; i++) {

            List ids = (ArrayList) servers.get( keySet[i] );

            String[] idProps = new String[ids.size()];
            for (int a = 0; a < ids.size(); a++) {
                idProps[a] = dp.getPlatformConfiguration( (String) ids.get( a ) )
                    .getIdPropertyValue();
            }

            QualifiedName pdft = dp.getSourceServerConfiguration( keySet[i] )
                .getPlatformDescriptionFeatureType();
            QualifiedName pdid = dp.getSourceServerConfiguration( keySet[i] )
                .getPlatformDescriptionIdPropertyName();
            Document request = WFSRequestGenerator.createIsLikeOperationWFSRequest( idProps, pdft,
                pdid );

            SourceServerConfiguration ssc = dp.getSourceServerConfiguration( keySet[i] );

            Document resultDoc = null;
            try {
                resultDoc = WFSRequester.sendWFSrequest( request, ssc.getDataService() );
            } catch (Exception e) {
                throw new OGCWebServiceException( this.getClass().getName(),
                    "could not get platform data from WFS "
                        + StringTools.stackTraceToString( e ) );
            }
            if ( resultDoc != null ) {
                URL pdxs = dp.getSourceServerConfiguration( keySet[i] )
                    .getPlatformDescriptionXSLTScriptSource();
                XSLTDocument sheet = new XSLTDocument();
                sheet.load( pdxs );
                XMLFragment input = new XMLFragment();
                input.setRootElement( resultDoc.getDocumentElement() );
                XMLFragment result = sheet.transform( input );
                resultDocuments.add( result.getRootElement().getOwnerDocument() );
            }

        }

        return ( (Document[]) resultDocuments.toArray( new Document[resultDocuments.size()] ) );
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: PlatformDescriptionDocument.java,v $
Revision 1.19  2006/10/18 17:00:56  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.18  2006/08/24 06:42:16  poth
File header corrected

Revision 1.17  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
