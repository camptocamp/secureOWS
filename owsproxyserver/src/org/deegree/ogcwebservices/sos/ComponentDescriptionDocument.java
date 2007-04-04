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
package org.deegree.ogcwebservices.sos;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.FunctionCode;
import org.deegree.model.metadata.iso19115.Linkage;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.metadata.iso19115.RoleCode;
import org.deegree.ogcbase.OGCDocument;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.sos.sensorml.BasicResponse;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.ComponentDescription;
import org.deegree.ogcwebservices.sos.sensorml.CoordinateReferenceSystem;
import org.deegree.ogcwebservices.sos.sensorml.Discussion;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.GeoLocation;
import org.deegree.ogcwebservices.sos.sensorml.GeoPositionModel;
import org.deegree.ogcwebservices.sos.sensorml.GeographicCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.ogcwebservices.sos.sensorml.ProjectedCRS;
import org.deegree.ogcwebservices.sos.sensorml.Quantity;
import org.deegree.ogcwebservices.sos.sensorml.Reference;
import org.deegree.ogcwebservices.sos.sensorml.ResponseModel;
import org.deegree.ogcwebservices.sos.sensorml.TypedQuantity;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * gets the metadata from a XSL transformed wfs result
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 */

public abstract class ComponentDescriptionDocument extends OGCDocument {

    /**
     * exceptions decleration must be there because this method
     * will be overeritten by extending classes
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument() throws IOException, SAXException {
        // nothing to do
    }

    /**
     * gets the identifiedAs part of a item
     * 
     * @param node
     * @param temp
     * @throws XMLParsingException
     * @throws InvalidParameterValueException
     */
    protected Identifier getIdentifiedAs( Node node ) throws XMLParsingException {

        if ( node != null ) {
            // value is required
            String value = XMLTools.getRequiredNodeAsString( node, "sml:Identifier/text()", nsContext );
            // type is optional
            String type = XMLTools.getNodeAsString( node, "sml:Identifier/@type", nsContext, null );
            // codeSpace is optional
            String codeSpace = XMLTools.getNodeAsString( node, "sml:Identifier/@codespace", nsContext,
                null );

            int typeId = 0;

            // type must compare to one of this values
            if ( type == null ) {
                typeId = 0;
            } else if ( "shortName".equalsIgnoreCase( type ) ) {
                typeId = 1;
            } else if ( "longName".equalsIgnoreCase( type ) ) {
                typeId = 2;
            } else if ( "serialNumber".equalsIgnoreCase( type ) ) {
                typeId = 3;
            } else if ( "modelNumber".equalsIgnoreCase( type ) ) {
                typeId = 4;
            } else if ( "missionNumber".equalsIgnoreCase( type ) ) {
                typeId = 5;
            } else if ( "partNumber".equalsIgnoreCase( type ) ) {
                typeId = 6;
            } else {
                throw new XMLParsingException( type
                    + " is not a valid type for Identifier" );
            }

            return new Identifier( value, typeId, codeSpace );
        }
        return null;
    }

    /**
     * gets the classifiedAs part of a item
     * 
     * @param node
     * @param temp
     * @throws XMLParsingException
     */
    protected Classifier getClassifiedAs( Node node ) throws XMLParsingException {

        if ( node != null ) {
            // value is required
            String value = XMLTools.getRequiredNodeAsString( node, "sml:Classifier/text()", nsContext );
            // type ist required
            String type = XMLTools.getRequiredNodeAsString( node, "sml:Classifier/@type", nsContext );
            // codeSpace is optional
            String codeSpace = XMLTools.getNodeAsString( node, "sml:Classifier/@codespace", nsContext,
                null );

            return new Classifier( value, type, codeSpace );
        }
        return null;

    }

    /**
     * gets the attachedTo part of a Item
     * 
     * @param node
     * @param temp
     * @throws XMLParsingException
     */
    protected String getAttachedTo( Node node ) throws XMLParsingException {
        if ( node != null ) {
            return XMLTools.getNodeAsString( node, "sml:Component/text()", nsContext, null );
        }
        return null;
    }

    /**
     * gets the hasCRS part of a Item
     * 
     * @param node
     * @param temp
     * @throws XMLParsingException
     */
    protected EngineeringCRS getHasCRS( Node node ) throws XMLParsingException {
        if ( node != null ) {
            return getEngineeringCRS( node );
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     */
    protected EngineeringCRS getEngineeringCRS( Node node ) throws XMLParsingException {
        if ( node != null ) {
            String srsName = XMLTools.getRequiredNodeAsString( node,
                "gml:EngineeringCRS/gml:srsName/text()", nsContext );
            return new EngineeringCRS( srsName );
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     */
    protected CoordinateReferenceSystem getCoordinateReferenceSystemCRS( Node node )
        throws XMLParsingException {
        if ( node != null ) {
            // is GeographicCRS
            if ( ( XMLTools.getNode( node, "gml:GeographicCRS", nsContext ) ) != null ) {
                String srsName = XMLTools.getNodeAsString( node,
                    "gml:GeographicCRS/gml:srsName/text()", nsContext, null );
                if ( srsName != null ) {
                    return new GeographicCRS( srsName );
                }

            }

            // is ProjectedCRS
            if ( ( XMLTools.getNode( node, "gml:ProjectedCRS", nsContext ) ) != null ) {
                String srsName = XMLTools.getNodeAsString( node,
                    "gml:ProjectedCRS/gml:srsName/text()", nsContext, null );
                if ( srsName != null ) {
                    return new ProjectedCRS( srsName );
                }

            }

        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected Object getUsesParametersFromGeoLocation( Node node )
        throws XMLParsingException,
            URISyntaxException {
        if ( node != null ) {
            if ( ( XMLTools.getNode( node, "sml:GeoLocation", nsContext ) ) != null ) {
                String id = XMLTools.getNodeAsString( node, "sml:GeoLocation/@id", nsContext, null );

                // required
                Quantity latitude = getQuantity( XMLTools.getRequiredNode( node,
                    "sml:GeoLocation/sml:latitude", nsContext ) );
                Quantity longitude = getQuantity( XMLTools.getRequiredNode( node,
                    "sml:GeoLocation/sml:longitude", nsContext ) );

                // optional
                Quantity altitude = getQuantity( XMLTools.getNode( node,
                    "sml:GeoLocation/sml:altitude", nsContext ) );
                Quantity trueHeading = getQuantity( XMLTools.getNode( node,
                    "sml:GeoLocation/sml:trueHeading", nsContext ) );
                Quantity speed = getQuantity( XMLTools.getNode( node, "sml:GeoLocation/sml:speed",
                    nsContext ) );

                return new GeoLocation( id, latitude, longitude, altitude, trueHeading, speed );
            }

        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected Object getUsesParametersFromResponseModel( Node node )
        throws XMLParsingException,
            URISyntaxException {
        if ( node != null ) {
            if ( ( XMLTools.getNode( node, "sml:BasicResponse", nsContext ) ) != null ) {

                // required
                TypedQuantity resolution = getTypedQuantity( XMLTools.getRequiredNode( node,
                    "sml:BasicResponse/sml:resolution", nsContext ) );

                return new BasicResponse( resolution );
            }

            // can add other types, but now not implemented

        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected Quantity getQuantity( Node node ) throws XMLParsingException, URISyntaxException {
        if ( node != null ) {
            String value = XMLTools.getRequiredNodeAsString( node, "sml:Quantity/text()", nsContext );

            if ( value != null ) {
                Quantity temp = new Quantity( Double.parseDouble( value ) );

                // gets the uom parameter
                String paramUom = XMLTools
                    .getNodeAsString( node, "sml:Quantity/@uom", nsContext, null );
                if ( paramUom != null ) {

                    temp.setUom( new URI( paramUom ) );
                }

                // gets the min parameter
                String paramMin = XMLTools
                    .getNodeAsString( node, "sml:Quantity/@min", nsContext, null );
                if ( paramMin != null ) {

                    temp.setMin( Double.parseDouble( paramMin ) );
                }

                // gets the max parameter
                String paramMax = XMLTools
                    .getNodeAsString( node, "sml:Quantity/@max", nsContext, null );
                if ( paramMax != null ) {

                    temp.setMax( Double.parseDouble( paramMax ) );
                }

                // gets the fixed parameter
                String paramFixed = XMLTools.getNodeAsString( node, "sml:Quantity/@fixed", nsContext,
                    null );
                if ( paramFixed != null ) {
                    if ( paramFixed.equalsIgnoreCase( "true" ) ) {

                        temp.setFixed( true );
                    } else if ( paramFixed.equalsIgnoreCase( "false" ) ) {

                        temp.setFixed( false );
                    }
                }
                return temp;
            }

        }

        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected TypedQuantity getTypedQuantity( Node node )
        throws XMLParsingException,
            URISyntaxException {
        if ( node != null ) {

            String value = XMLTools.getRequiredNodeAsString( node, "sml:TypedQuantity/text()",
                nsContext );

            String type = XMLTools
                .getRequiredNodeAsString( node, "sml:TypedQuantity/@type", nsContext );

            TypedQuantity temp = new TypedQuantity( Double.parseDouble( value ), new URI( type ) );

            // gets the uom parameter
            String paramUom = XMLTools.getNodeAsString( node, "sml:TypedQuantity/@uom", nsContext,
                null );
            if ( paramUom != null ) {

                temp.setUom( new URI( paramUom ) );
            }

            // gets the min parameter
            String paramMin = XMLTools.getNodeAsString( node, "sml:TypedQuantity/@min", nsContext,
                null );
            if ( paramMin != null ) {

                temp.setMin( Double.parseDouble( paramMin ) );
            }

            // gets the max parameter
            String paramMax = XMLTools.getNodeAsString( node, "sml:TypedQuantity/@max", nsContext,
                null );
            if ( paramMax != null ) {

                temp.setMax( Double.parseDouble( paramMax ) );
            }

            // gets the fixed parameter
            String paramFixed = XMLTools.getNodeAsString( node, "sml:TypedQuantity/@fixed", nsContext,
                null );
            if ( paramFixed != null ) {
                if ( paramFixed.equalsIgnoreCase( "false" ) ) {

                    temp.setFixed( false );
                }
            }

            // gets the codeSpace parameter
            String codeSpaceAsString = XMLTools.getNodeAsString( node,
                "sml:TypedQuantity/@codeSpace", nsContext, null );
            if ( codeSpaceAsString != null ) {
                URI codeSpace = new URI( codeSpaceAsString );
                temp.setCodeSpace( codeSpace );
            }
            return temp;
        }

        return null;
    }

    /**
     * 
     * @param node
     * @param temp
     * @param nsContext
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    protected ComponentDescription getDescribedBy( Node node )
        throws MalformedURLException,
            XMLParsingException {
        if ( node != null ) {

            boolean create = false;

            // get optional id
            String id = XMLTools.getNodeAsString( node, "sml:ComponentDescription/@id", nsContext,
                null );
            if ( id != null )
                create = true;

            // get optional description
            ArrayList descriptions = new ArrayList();
            List descriptionList = XMLTools.getNodes( node,
                "sml:ComponentDescription/sml:description", nsContext );
            if ( ( descriptionList != null )
                && ( descriptionList.size() > 0 ) )
                create = true;
            for (int i = 0; i < descriptionList.size(); i++) {
                descriptions.add( getDiscussion( (Node) descriptionList.get( i ) ) );
            }

            // get optional reference
            ArrayList references = new ArrayList();
            List referenceList = XMLTools.getNodes( node, "sml:ComponentDescription/sml:reference",
                nsContext );
            if ( ( referenceList != null )
                && ( referenceList.size() > 0 ) ) {
                create = true;

            }
            for (int i = 0; i < referenceList.size(); i++) {
                Reference actreference = getReference( (Node) referenceList.get( i ) );
                if ( actreference != null ) {
                    references.add( actreference );
                }
            }

            // get optional operatedBy
            ArrayList operatedBys = new ArrayList();
            List operatedByList = XMLTools.getNodes( node,
                "sml:ComponentDescription/sml:operatedBy", nsContext );
            if ( ( operatedByList != null )
                && ( operatedByList.size() > 0 ) ) {
                create = true;

            }
            for (int i = 0; i < operatedByList.size(); i++) {
                CitedResponsibleParty actResponsibleParty = getResponsibleParty( (Node) operatedByList
                    .get( i ) );
                if ( actResponsibleParty != null ) {
                    operatedBys.add( actResponsibleParty );
                }
            }

            // get optional manufactedBy
            ArrayList manufactedBys = new ArrayList();
            List manufactedByList = XMLTools.getNodes( node,
                "sml:ComponentDescription/sml:manufactedBy", nsContext );
            if ( ( manufactedByList != null )
                && ( manufactedByList.size() > 0 ) ) {
                create = true;

            }
            for (int i = 0; i < manufactedByList.size(); i++) {
                CitedResponsibleParty actResponsibleParty = getResponsibleParty( (Node) manufactedByList
                    .get( i ) );
                if ( actResponsibleParty != null ) {
                    manufactedBys.add( actResponsibleParty );
                }
            }

            // get optional deployedBys
            ArrayList deployedBys = new ArrayList();
            List deployedByList = XMLTools.getNodes( node,
                "sml:ComponentDescription/sml:deployedBy", nsContext );
            if ( ( deployedByList != null )
                && ( deployedByList.size() > 0 ) ) {
                create = true;

            }
            for (int i = 0; i < deployedByList.size(); i++) {
                CitedResponsibleParty actResponsibleParty = getResponsibleParty( (Node) deployedByList
                    .get( i ) );
                if ( actResponsibleParty != null ) {
                    deployedBys.add( actResponsibleParty );
                }
            }

            if ( create ) {
                return new ComponentDescription( id, ( (CitedResponsibleParty[]) manufactedBys
                    .toArray( new CitedResponsibleParty[manufactedBys.size()] ) ),
                    ( (CitedResponsibleParty[]) deployedBys
                        .toArray( new CitedResponsibleParty[deployedBys.size()] ) ),
                    ( (CitedResponsibleParty[]) operatedBys
                        .toArray( new CitedResponsibleParty[operatedBys.size()] ) ),
                    ( (Discussion[]) descriptions.toArray( new Discussion[descriptions.size()] ) ),
                    ( (Reference[]) references.toArray( new Reference[references.size()] ) ) );
            }

        }
        return null;

    }

    /**
     * 
     * @param node
     * @param temp
     * @param nsContext
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected LocationModel getLocatedUsing( Node node )
        throws XMLParsingException,
            URISyntaxException {

        if ( node != null ) {
            if ( ( XMLTools.getNode( node, "sml:GeoPositionModel", nsContext ) ) != null ) {

                String id = XMLTools.getNodeAsString( node, "sml:GeoPositionModel/@id", nsContext,
                    null );

                // get identifiedAs
                ArrayList identifiers = new ArrayList();
                List identifierList = XMLTools.getNodes( node,
                    "sml:GeoPositionModel/sml:identifiedAs", nsContext );
                for (int i = 0; i < identifierList.size(); i++) {
                    identifiers.add( getIdentifiedAs( (Node) identifierList.get( i ) ) );
                }

                // get ClassifiedAs
                ArrayList classifiers = new ArrayList();
                List classifierList = XMLTools.getNodes( node,
                    "sml:GeoPositionModel/sml:classifiedAs", nsContext );
                for (int i = 0; i < classifierList.size(); i++) {
                    classifiers.add( getClassifiedAs( (Node) classifierList.get( i ) ) );
                }

                // get optional description
                ArrayList descriptions = new ArrayList();
                List descriptionList = XMLTools.getNodes( node,
                    "sml:GeoPositionModel/sml:description", nsContext );
                for (int i = 0; i < descriptionList.size(); i++) {
                    descriptions.add( getDiscussion( (Node) descriptionList.get( i ) ) );
                }

                // get sourceCRS
                Node tNode = XMLTools.getRequiredNode( node, "sml:GeoPositionModel/sml:sourceCRS",
                    nsContext );
                EngineeringCRS sourceCRS = getEngineeringCRS( tNode );

                // get referenceCRS
                tNode = XMLTools.getRequiredNode( node, "sml:GeoPositionModel/sml:referenceCRS",
                    nsContext );
                CoordinateReferenceSystem referenceCRS = getCoordinateReferenceSystemCRS( tNode );

                // get usesParameters
                ArrayList usesParameters = new ArrayList();
                List usesParametersList = XMLTools.getNodes( node,
                    "sml:GeoPositionModel/sml:usesParameters", nsContext );
                if ( ( usesParametersList == null )
                    || ( usesParametersList.size() <= 0 ) ) {
                    throw new XMLParsingException( "at least one usesParameters required" );
                }
                for (int i = 0; i < usesParametersList.size(); i++) {
                    usesParameters.add( getUsesParametersFromGeoLocation( (Node) usesParametersList
                        .get( i ) ) );
                }

                return new GeoPositionModel( id, ( (Identifier[]) identifiers
                    .toArray( new Identifier[identifiers.size()] ) ), ( (Classifier[]) classifiers
                    .toArray( new Classifier[classifiers.size()] ) ), ( (Discussion[]) descriptions
                    .toArray( new Discussion[descriptions.size()] ) ), sourceCRS, referenceCRS,
                    usesParameters.toArray( new Object[usesParameters.size()] ) );
            }

        }
        return null;

    }

    /**
     * 
     * @param node
     * @param temp
     * @param nsContext
     * @throws XMLParsingException
     * @throws URISyntaxException
     */
    protected ResponseModel getDerivedFrom( Node node )
        throws XMLParsingException,
            URISyntaxException {

        if ( node != null ) {

            String id = XMLTools.getNodeAsString( node, "sml:ResponseModel/@id", nsContext, null );

            // get identifiedAs
            ArrayList identifiers = new ArrayList();
            List identifierList = XMLTools.getNodes( node, "sml:ResponseModel/sml:identifiedAs",
                nsContext );
            for (int i = 0; i < identifierList.size(); i++) {
                identifiers.add( getIdentifiedAs( (Node) identifierList.get( i ) ) );
            }

            // get ClassifiedAs
            ArrayList classifiers = new ArrayList();
            List classifierList = XMLTools.getNodes( node, "sml:ResponseModel/sml:classifiedAs",
                nsContext );
            for (int i = 0; i < classifierList.size(); i++) {
                classifiers.add( getClassifiedAs( (Node) classifierList.get( i ) ) );
            }

            // get optional description
            ArrayList descriptions = new ArrayList();
            List descriptionList = XMLTools.getNodes( node, "sml:ResponseModel/sml:description",
                nsContext );
            for (int i = 0; i < descriptionList.size(); i++) {
                descriptions.add( getDiscussion( (Node) descriptionList.get( i ) ) );
            }

            // get usesParameters
            // in spec this parameter is optional, but now is required for set
            // time resolution
            ArrayList usesParameters = new ArrayList();
            List usesParametersList = XMLTools.getNodes( node,
                "sml:ResponseModel/sml:usesParameters", nsContext );
            for (int i = 0; i < usesParametersList.size(); i++) {
                usesParameters.add( getUsesParametersFromResponseModel( (Node) usesParametersList
                    .get( i ) ) );
            }

            // only creats the object if least one value set
            if ( ( id != null )
                || ( identifiers.size() > 0 ) || ( classifiers.size() > 0 )
                || ( descriptions.size() > 0 ) || ( usesParameters.size() > 0 ) ) {
                return new ResponseModel( id, ( (Identifier[]) identifiers
                    .toArray( new Identifier[identifiers.size()] ) ), ( (Classifier[]) classifiers
                    .toArray( new Classifier[classifiers.size()] ) ), ( (Discussion[]) descriptions
                    .toArray( new Discussion[descriptions.size()] ) ), usesParameters
                    .toArray( new Object[usesParameters.size()] ) );
            }
        }
        return null;

    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws MalformedURLException
     * @throws XMLParsingException
     */
    protected Reference getReference( Node node ) throws MalformedURLException, XMLParsingException {

        if ( node != null ) {
            Object getvalue = null;

            getvalue = getOnlineResource( node );

            if ( getvalue != null ) {

                return new Reference( getvalue );
            }
        }
        return null;

    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     */
    protected Discussion getDiscussion( Node node ) throws XMLParsingException {
        if ( node != null ) {
            // required
            String value = XMLTools.getRequiredNodeAsString( node, "text()", nsContext );
            URI topic = null;
            // optional
            String topicString = XMLTools.getNodeAsString( node, "@topic", nsContext, null );
            URI codeSpace = null;
            // optional
            String codeSpaceString = XMLTools.getNodeAsString( node, "@codeSpace", nsContext, null );
            // optional
            String id = XMLTools.getNodeAsString( node, "@id", nsContext, null );

            try {
                if ( topicString != null )
                    topic = new URI( topicString );

                if ( codeSpaceString != null )
                    codeSpace = new URI( codeSpaceString );
            } catch (URISyntaxException e) {
                throw new XMLParsingException( "URISyntaxException: not a valid URI" );
            }

            return new Discussion( value, topic, codeSpace, id );
        }
        return null;

    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    protected OnlineResource getOnlineResource( Node node )
        throws XMLParsingException,
            MalformedURLException {

        if ( node != null ) {
            String function = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:function/text()", nsContext, null );

            String linkageString = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:linkage/text()", nsContext, null );

            String protocol = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:protocol/text()", nsContext, null );

            String applicationProfile = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:applicationProfile/text()", nsContext, null );

            String name = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:name/text()", nsContext, null );

            String description = XMLTools.getNodeAsString( node,
                "iso19115:CI_OnlineResource/iso19115:description/text()", nsContext, null );

            if ( linkageString != null ) {
                return new OnlineResource( applicationProfile, new FunctionCode( function ),
                    new Linkage( new URL( linkageString ) ), description, name, protocol );
            }
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     */
    protected Phone getPhone( Node node ) throws XMLParsingException {

        if ( node != null ) {
            // get voices
            String[] voices = XMLTools.getNodesAsStrings( node,
                "iso19115:phone/iso19115:voice/text()", nsContext );
            // get facsimiles
            String[] facsimiles = XMLTools.getNodesAsStrings( node,
                "iso19115:phone/iso19115:facsimile/text()", nsContext );
            return new Phone( facsimiles, null, null, voices );
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     */
    protected Address getAddress( Node node ) throws XMLParsingException {

        if ( node != null ) {
            String city = XMLTools.getNodeAsString( node, "iso19115:address/iso19115:city/text()",
                nsContext, null );

            String administrativeArea = XMLTools.getNodeAsString( node,
                "iso19115:address/iso19115:administrativeArea/text()", nsContext, null );

            String postalCode = XMLTools.getNodeAsString( node,
                "iso19115:address/iso19115:postalCode/text()", nsContext, null );

            String country = XMLTools.getNodeAsString( node,
                "iso19115:address/iso19115:country/text()", nsContext, null );

            // get deliveryPoints
            String[] deliveryPoints = XMLTools.getNodesAsStrings( node,
                "iso19115:address/iso19115:deliveryPoint/text()", nsContext );
            // get electronicMailAdresses
            String[] electronicMailAdresses = XMLTools.getNodesAsStrings( node,
                "iso19115:address/iso19115:electronicMailAddress/text()", nsContext );
            return new Address( administrativeArea, city, country, deliveryPoints,
                electronicMailAdresses, postalCode );
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws MalformedURLException
     * @throws XMLParsingException
     */
    protected ContactInfo getContactInfo( Node node )
        throws MalformedURLException,
            XMLParsingException {

        if ( node != null ) {
            Phone phone = getPhone( node );
            Address address = getAddress( node );
            OnlineResource onlineResource = getOnlineResource( node );
            String hoursOfService = XMLTools.getNodeAsString( node,
                "iso19115:hoursOfService/text()", nsContext, null );
            String contactInstructions = XMLTools.getNodeAsString( node,
                "iso19115:contactInstructions/text()", nsContext, null );

            return new ContactInfo( address, contactInstructions, hoursOfService, onlineResource,
                phone );
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param nsContext
     * @return
     * @throws XMLParsingException
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    protected CitedResponsibleParty getResponsibleParty( Node node )
        throws MalformedURLException,
            XMLParsingException {

        if ( node != null ) {
            // gets individualNames
            String[] individualNames = XMLTools.getNodesAsStrings( node,
                "iso19115:CI_ResponsibleParty/iso19115:individualName/text()", nsContext );

            // gets organisationNames
            String[] organisationNames = XMLTools.getNodesAsStrings( node,
                "iso19115:CI_ResponsibleParty/iso19115:organisationName/text()", nsContext );

            // gets positionNames
            String[] positionNames = XMLTools.getNodesAsStrings( node,
                "iso19115:CI_ResponsibleParty/iso19115:positionName/text()", nsContext );

            // gets role_CodeList
            ArrayList role_CodeList = new ArrayList();
            List role_CodeListList = XMLTools.getNodes( node,
                "iso19115:CI_ResponsibleParty/iso19115:role/iso19115:CI_RoleCode_CodeList", nsContext );
            for (int i = 0; i < role_CodeListList.size(); i++) {
                role_CodeList.add( new RoleCode( XMLTools.getRequiredNodeAsString(
                    (Node) role_CodeListList.get( i ), "text()", nsContext ) ) );
            }

            // gets contactInfo
            ArrayList contactInfo = new ArrayList();
            List contactInfoList = XMLTools.getNodes( node,
                "iso19115:CI_ResponsibleParty/iso19115:contactInfo", nsContext );
            for (int i = 0; i < contactInfoList.size(); i++) {
                contactInfo.add( getContactInfo( (Node) contactInfoList.get( i ) ) );
            }

            return new CitedResponsibleParty( ( (ContactInfo[]) contactInfo
                .toArray( new ContactInfo[contactInfo.size()] ) ), individualNames,
                organisationNames, positionNames, ( (RoleCode[]) role_CodeList
                    .toArray( new RoleCode[role_CodeList.size()] ) ) );
        }
        return null;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ComponentDescriptionDocument.java,v $
Revision 1.10  2006/10/18 17:00:56  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.9  2006/08/24 06:42:16  poth
File header corrected

Revision 1.8  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.7  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
