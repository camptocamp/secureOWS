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

import java.net.URI;
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Address;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.ContactInfo;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.model.metadata.iso19115.Phone;
import org.deegree.model.metadata.iso19115.RoleCode;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.sos.capabilities.CapabilitiesDocument;
import org.deegree.ogcwebservices.sos.capabilities.Platform;
import org.deegree.ogcwebservices.sos.capabilities.SOSCapabilities;
import org.deegree.ogcwebservices.sos.capabilities.Sensor;
import org.deegree.ogcwebservices.sos.describeplatform.DescribePlatformResult;
import org.deegree.ogcwebservices.sos.describeplatform.PlatformDescriptionDocument;
import org.deegree.ogcwebservices.sos.describeplatform.PlatformMetadata;
import org.deegree.ogcwebservices.sos.describesensor.DescribeSensorResult;
import org.deegree.ogcwebservices.sos.describesensor.SensorDescriptionDocument;
import org.deegree.ogcwebservices.sos.describesensor.SensorMetadata;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationDocument;
import org.deegree.ogcwebservices.sos.getobservation.GetObservationResult;
import org.deegree.ogcwebservices.sos.om.Observation;
import org.deegree.ogcwebservices.sos.om.ObservationArray;
import org.deegree.ogcwebservices.sos.sensorml.BasicResponse;
import org.deegree.ogcwebservices.sos.sensorml.Classifier;
import org.deegree.ogcwebservices.sos.sensorml.CoordinateReferenceSystem;
import org.deegree.ogcwebservices.sos.sensorml.Discussion;
import org.deegree.ogcwebservices.sos.sensorml.EngineeringCRS;
import org.deegree.ogcwebservices.sos.sensorml.GeoLocation;
import org.deegree.ogcwebservices.sos.sensorml.GeoPositionModel;
import org.deegree.ogcwebservices.sos.sensorml.GeographicCRS;
import org.deegree.ogcwebservices.sos.sensorml.Identifier;
import org.deegree.ogcwebservices.sos.sensorml.LocationModel;
import org.deegree.ogcwebservices.sos.sensorml.Product;
import org.deegree.ogcwebservices.sos.sensorml.ProjectedCRS;
import org.deegree.ogcwebservices.sos.sensorml.Quantity;
import org.deegree.ogcwebservices.sos.sensorml.Reference;
import org.deegree.ogcwebservices.sos.sensorml.ResponseModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * export data to XML Files
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class XMLFactory extends org.deegree.owscommon.XMLFactory {

	private static final ILogger LOG = LoggerFactory.getLogger(XMLFactory.class);

	protected static final URI SOSNS = CommonNamespaces.SOSNS;
	protected static final URI DEEGREESOS = CommonNamespaces.DEEGREESOS;

	/**
	 * exports the SCS Capabilities
	 * 
	 * @param scsCapabilities
	 * @return @throws
	 *         OGCWebServiceException
	 */
	public static XMLFragment export(SOSCapabilities scsCapabilities)
			throws OGCWebServiceException {

		try {

			CapabilitiesDocument doc = new CapabilitiesDocument();
			
			doc.createEmptyDocument();

			Element root = doc.getRootElement();
			XMLTools.appendNSBinding( root, CommonNamespaces.SOS_PREFIX.toLowerCase() , CommonNamespaces.SOSNS);
			
			//sets the version
			root.setAttribute("version", scsCapabilities.getVersion());

			//sets the updateSsequence
			root.setAttribute("updateSequence", scsCapabilities.getUpdateSequence());

			//appends the ServiceIdentification
			ServiceIdentification serviceIdentification = scsCapabilities
					.getServiceIdentification();
			if (serviceIdentification != null) {
				appendServiceIdentification(root, serviceIdentification);
			}

			//appends the ServiceProvider
			ServiceProvider serviceProvider = scsCapabilities
					.getServiceProvider();
			if (serviceProvider != null) {
				appendServiceProvider(root, serviceProvider);
			}

			//appends the OpertionsMetadata
			OperationsMetadata operationsMetadata = scsCapabilities
					.getOperationsMetadata();
			if (operationsMetadata != null) {
				appendOperationsMetadata(root, operationsMetadata);
			}

			//appends the PlatformList
			ArrayList platformList = scsCapabilities.getPlatformList();
			if (platformList != null) {
				appendPlatformList(root, platformList);
			}

			//appends the SensorList
			ArrayList sensorList = scsCapabilities.getSensorList();
			if (sensorList != null) {
				appendSensorList(root, sensorList);
			}

			return doc;

		} catch (Exception e) {
			LOG.logError( e.getMessage(), e );
			throw new OGCWebServiceException("scs webservice failure");
		}

	}

	/**
	 * 
	 * @param describePlatformResult
	 * @return @throws
	 *         OGCWebServiceException
	 */
	public static XMLFragment export(
			DescribePlatformResult describePlatformResult)
			throws OGCWebServiceException {

		PlatformDescriptionDocument scsDocument = new PlatformDescriptionDocument();

		//erstellt ein leeres Dokument wie in dem Template angegeben
		try {
			scsDocument.createEmptyDocument();

			Element root = scsDocument.getRootElement();
			XMLTools.appendNSBinding( root, CommonNamespaces.GML_PREFIX.toLowerCase() , CommonNamespaces.GMLNS );
			Document doc = root.getOwnerDocument();

			PlatformMetadata[] platformMetadata = describePlatformResult
					.getPlatforms();

			for (int i = 0; i < platformMetadata.length; i++) {

				root.appendChild(appendPlatform(platformMetadata[i], doc));
			}

			return scsDocument;

		} catch (Exception e) {
            LOG.logError( e.getMessage(), e );
			throw new OGCWebServiceException("sos webservice failure");
		}
	}

	/**
	 * 
	 * @param describeSensorResult
	 * @return @throws
	 *         OGCWebServiceException
	 */
	public static XMLFragment export(DescribeSensorResult describeSensorResult)
			throws OGCWebServiceException {

		try {

			SensorDescriptionDocument scsDocument = new SensorDescriptionDocument();

			scsDocument.createEmptyDocument();

			Element root = scsDocument.getRootElement();
			XMLTools.appendNSBinding( root, CommonNamespaces.GML_PREFIX.toLowerCase() , CommonNamespaces.GMLNS );
			
			Document doc = root.getOwnerDocument();

			SensorMetadata[] sensorMetadata = describeSensorResult.getSensors();

			for (int i = 0; i < sensorMetadata.length; i++) {
				root.appendChild(appendSensor(sensorMetadata[i], doc));
			}

			return scsDocument;

		} catch (Exception e) {
			e.printStackTrace();
			throw new OGCWebServiceException("sos webservice failure");
		}
	}

	/**
	 * 
	 * @param getObservationResult
	 * @return @throws
	 *         OGCWebServiceException
	 */
	public static XMLFragment export(GetObservationResult getObservationResult)
			throws OGCWebServiceException {

		GetObservationDocument scsDocument = new GetObservationDocument();

		try {
			scsDocument.createEmptyDocument();

			Element root = scsDocument.getRootElement();
			Document doc = root.getOwnerDocument();

			//TODO add boundedBy
			Element boundedBy = doc.createElement("gml:boundedBy");
			root.appendChild(boundedBy);

			Element leer = doc.createElement("gml:Null");
			boundedBy.appendChild(leer);

			leer.appendChild(doc.createTextNode("unknown"));

			Element observationMembers = doc
					.createElement("om:observationMembers");
			root.appendChild(observationMembers);

			//add observation
			appendObservationArrays(getObservationResult.getObservations(),
					observationMembers, doc);

			return scsDocument;

		} catch (Exception e) {
            LOG.logError( e.getMessage(), e );
			throw new OGCWebServiceException("sos webservice failure");
		}
	}

	/**
	 * appends ObservationArrays
	 * 
	 * @param observationArrays
	 * @param element
	 * @param doc
	 */
	private static void appendObservationArrays(ObservationArray[] observationArrays, 
                                                Element element, Document doc) {
		if (observationArrays == null) {
			return;
		}
		if (element == null) {
			return;
		}
		if (doc == null) {
			return;
		}

		for (int i = 0; i < observationArrays.length; i++) {

			Element observationArray = doc.createElement("om:ObservationArray");
			element.appendChild(observationArray);

			//TODO add boundedBy
			Element boundedBy = doc.createElement("gml:boundedBy");
			observationArray.appendChild(boundedBy);

			Element boundingBox = doc.createElement("gml:boundingBox");
			boundedBy.appendChild(boundingBox);

			Element coordMin = doc.createElement("gml:coord");
			boundingBox.appendChild(coordMin);

			Element minX = doc.createElement("gml:X");
			coordMin.appendChild(minX);

			minX.appendChild(doc.createTextNode(""
					+ observationArrays[i].getBoundedBy().getMin().getX()));

			Element minY = doc.createElement("gml:Y");
			coordMin.appendChild(minY);

			minY.appendChild(doc.createTextNode(""
					+ observationArrays[i].getBoundedBy().getMin().getY()));

			Element coordMax = doc.createElement("gml:coord");
			boundingBox.appendChild(coordMax);

			Element maxX = doc.createElement("gml:X");
			coordMax.appendChild(maxX);

			maxX.appendChild(doc.createTextNode(""
					+ observationArrays[i].getBoundedBy().getMax().getX()));

			Element maxY = doc.createElement("gml:Y");
			coordMax.appendChild(maxY);

			maxY.appendChild(doc.createTextNode(""
					+ observationArrays[i].getBoundedBy().getMax().getY()));

			//TODO add using
			Element using = doc.createElement("om:using");
			observationArray.appendChild(using);

			Element observationMembers = doc
					.createElement("om:observationMembers");
			observationArray.appendChild(observationMembers);

			appendObservations(observationArrays[i].getObservations(),
					observationMembers, doc);

		}

	}

	/**
	 * appends Observations
	 * 
	 * @param observations
	 * @param element
	 * @param doc
	 */
	private static void appendObservations(Observation[] observations,
			Element element, Document doc) {
		if (observations == null) {
			return;
		}
		if (element == null) {
			return;
		}
		if (doc == null) {
			return;
		}

		for (int i = 0; i < observations.length; i++) {

			Element observation = doc.createElement("gml:Observation");
			element.appendChild(observation);

			//add timeStamp
			Element timeStamp = doc.createElement("gml:timeStamp");
			observation.appendChild(timeStamp);

			Element timeInstant = doc.createElement("gml:TimeInstant");
			timeStamp.appendChild(timeInstant);

			Element timePosition = doc.createElement("gml:timePosition");
			timeInstant.appendChild(timePosition);

			timePosition.appendChild( doc.createTextNode( observations[i].getTimeStamp()) );

			//add resultOf
			Element resultOf = doc.createElement("gml:resultOf");
			observation.appendChild(resultOf);

			Element quantityList = doc.createElement("gml:QuantityList");
			resultOf.appendChild(quantityList);

            String ro = observations[i].getResultOf();
            if ( ro != null ) {
                quantityList.appendChild(doc.createTextNode( ro ));
            }

		}

	}

	/**
	 * appends a PlatformList used by getCapabilities
	 * 
	 * @param root
	 * @param platformList
	 *  
	 */
	private static void appendPlatformList(Element root, ArrayList pl) {

		if (pl == null) {
			return;
		}

//        XmlNode nn = new XmlNode(root);
//        nn.appendDomChild(XmlNode.Element, SCSNS, "sos:PlatformList",  null);
        
        Element element = XMLTools.appendElement( root, SOSNS, "sos:PlatformList" );
        
//		XmlNode platformListNode = new XmlNode( nn );

		for (int i = 0; i < pl.size(); i++) {

//            XmlNode n = new XmlNode(platformListNode);
//            n.appendDomChild(XmlNode.Element, SCSNS, "sos:Platform",null);
//			XmlNode platformNode =new XmlNode( n );
            
            Element elem = XMLTools.appendElement( element, SOSNS, "sos:Platform" );
            elem.setAttribute( "Id", ((Platform) pl.get(i)).getId() );

//			platformNode.appendDomChild( XmlNode.Attribute, null, "Id", ""
//					+ ((Platform) pl.get(i)).getId());

//			platformNode.appendDomChild( XmlNode.Attribute, null, "Description",
//					"" + ((Platform) pl.get(i)).getDescription());

            elem.setAttribute( "Description", ((Platform) pl.get(i)).getDescription() );
		}
	}

	/**
	 * appends a sensor list used by getCapabilities
	 * 
	 * @param root
	 * @param sensorList
	 *  
	 */
	private static void appendSensorList(Element root, ArrayList sl) {
		if (sl == null) {
			return;
		}

        Element element = XMLTools.appendElement( root, SOSNS, "sos:SensorList" );

		for (int i = 0; i < sl.size(); i++) {
            Element elem = XMLTools.appendElement( element, SOSNS, "sos:Sensor" );
            elem.setAttribute( "Id", ((Sensor) sl.get(i)).getId() );
            elem.setAttribute( "Description", ((Sensor) sl.get(i)).getDescription() );

		}
	}

	/**
	 * returns a platform element
	 * 
	 * @param actPlatform
	 * @param doc
	 * @return
	 */
	private static Element appendPlatform(PlatformMetadata actPlatform,
			Document doc) {

		if (actPlatform == null) {
			return null;
		}

		Element platform = doc.createElement("sml:Platform");

		appendIdentifiedAs(actPlatform.getIdentifiedAs(), platform, doc);

		appendClassifiedAs(actPlatform.getClassifiedAs(), platform, doc);

		appendAttachedTo(actPlatform, platform, doc);

		appendHasCRS(actPlatform.getHasCRS(), platform, doc);

		appendDescribedBy(actPlatform, platform, doc);

		appendLocatedUsing(actPlatform.getLocatedUsing(), platform, doc);

		appendCarries(actPlatform.getCarries(), platform, doc);

		return platform;
	}

	/**
	 * 
	 * @param actSensor
	 * @param doc
	 * @return
	 */
	private static Element appendSensor(SensorMetadata actSensor, Document doc) {
		if (actSensor == null) {
			return null;
		}

		Element sensor = doc.createElement("sml:Sensor");

		appendIdentifiedAs(actSensor.getIdentifiedAs(), sensor, doc);

		appendClassifiedAs(actSensor.getClassifiedAs(), sensor, doc);

		appendAttachedTo(actSensor, sensor, doc);

		appendHasCRS(actSensor.getHasCRS(), sensor, doc);

		appendDescribedBy(actSensor, sensor, doc);

		appendLocatedUsing(actSensor.getLocatedUsing(), sensor, doc);

		appendMeasures(actSensor.getMeasures(), sensor, doc);

		return sensor;
	}

	/**
	 * appends a IdentifiedAs Node
	 * 
	 * @param actii
	 *            blah blah. Cannot be null.
	 * @param element
	 * @param doc
	 */
	private static void appendIdentifiedAs(Identifier[] actidentifier,
			Element element, Document doc) {
		if (actidentifier == null) {
			return;
		}

		for (int i = 0; i < actidentifier.length; i++) {
			int type = actidentifier[i].getIdentifierType();

			Element identifiedAs = doc.createElement("sml:identifiedAs");
			Element identifier = doc.createElement("sml:Identifier");

			//is required; no check
			identifier.appendChild(doc.createTextNode(actidentifier[i]
					.getIdentifierValue()));

			//is optional; but type must be checked
			if (type == 1)
				identifier.setAttribute("type", "shortName");
			if (type == 2)
				identifier.setAttribute("type", "longName");
			if (type == 3)
				identifier.setAttribute("type", "serialNumber");
			if (type == 4)
				identifier.setAttribute("type", "modelNumber");
			if (type == 5)
				identifier.setAttribute("type", "missionNumber");
			if (type == 6)
				identifier.setAttribute("type", "partNumber");

			//is optional; check
			if (actidentifier[i].getIdentifierCodeSpace() != null)
				identifier.setAttribute("codeSpace", actidentifier[i]
						.getIdentifierCodeSpace());

			identifiedAs.appendChild(identifier);
			element.appendChild(identifiedAs);
		}

	}

	/**
	 * 
	 * @param object
	 * @param doc
	 * @return
	 */
	private static void appendAttachedTo(Object object, Element element,
			Document doc) {

		//optional; check
		if (((ComponentMetadata) object).getAttachedTo() != null) {
			Element attachedTo = doc.createElement("sml:attachedTo");

			Element component = doc.createElement("sml:Component");
			attachedTo.appendChild(component);

			component.appendChild(doc
					.createTextNode(((ComponentMetadata) object)
							.getAttachedTo()));
			element.appendChild(attachedTo);
		}
	}

	/**
	 * 
	 * @param object
	 * @param doc
	 * @return
	 */
	private static void appendClassifiedAs(Classifier[] actClassifier,
			Element element, Document doc) {
		if (actClassifier == null) {
			return;
		}

		for (int i = 0; i < actClassifier.length; i++) {

			Element classifiedAs = doc.createElement("sml:classifiedAs");
			Element classifier = doc.createElement("sml:Classifier");
			classifiedAs.appendChild(classifier);

			//required; no check
			classifier.setAttribute("type", actClassifier[i].getType());

			//required; no check
			classifier.appendChild(doc.createTextNode(actClassifier[i]
					.getValue()));
			//optional; check
			if (actClassifier[i].getCodeSpace() != null) {
				classifier.setAttribute("codeSpace", actClassifier[i]
						.getCodeSpace());
			}

			element.appendChild(classifiedAs);
		}

	}

	private static void appendDerivedFrom(ResponseModel[] responseModels,
			Element element, Document doc) {
		if (responseModels == null) {
			return;
		}

		for (int i = 0; i < responseModels.length; i++) {

			Element derivedFrom = doc.createElement("sml:derivedFrom");
			element.appendChild(derivedFrom);

			Element responseModel = doc.createElement("sml:ResponseModel");
			derivedFrom.appendChild(responseModel);

			// appends identifiedAs
			appendIdentifiedAs(responseModels[i].getIdentifiedAs(),
					responseModel, doc);

			//appends classifiedAs
			appendClassifiedAs(responseModels[i].getClassifiedAs(),
					responseModel, doc);

			//appends description
			appendDescription(responseModels[i].getDescription(),
					responseModel, doc);

			appendUsesParametersFromDerivedFrom(responseModels[i]
					.getUsesParameters(), responseModel, doc);

		}

	}

	/**
	 * appends a hasCRS node
	 * 
	 * @param object
	 * @param element
	 * @param doc
	 */
	private static void appendHasCRS(EngineeringCRS engineeringCRS,
			Element element, Document doc) {
		if (engineeringCRS == null) {
			return;
		}

		Element hasCRS = doc.createElement("sml:hasCRS");
		element.appendChild(hasCRS);
		appendEngineeringCRS(engineeringCRS, hasCRS, doc);

	}

	private static void appendEngineeringCRS(EngineeringCRS engineeringCRS,
			Element element, Document doc) {

		if (engineeringCRS == null) {
			return;
		}

		Element engineeringCRSEle = doc.createElement("gml:EngineeringCRS");
		element.appendChild(engineeringCRSEle);
		Element srsName = doc.createElement("gml:srsName");

		srsName.appendChild(doc.createTextNode(engineeringCRS.getSrsName()));

		engineeringCRSEle.appendChild(srsName);

	}

	private static void appendCoordinateReferenceSystem(
			CoordinateReferenceSystem crs, Element element, Document doc) {

		if (crs == null) {
			return;
		}

		if (crs instanceof GeographicCRS) {
			Element geographicCRS = doc.createElement("gml:GeographicCRS");
			element.appendChild(geographicCRS);
			Element srsName = doc.createElement("gml:srsName");

			srsName.appendChild(doc.createTextNode(crs.getSrsName()));

			geographicCRS.appendChild(srsName);
		}
		if (crs instanceof ProjectedCRS) {
			Element projectedCRS = doc.createElement("gml:ProjectedCRS");
			element.appendChild(projectedCRS);
			Element srsName = doc.createElement("gml:srsName");

			srsName.appendChild(doc.createTextNode(crs.getSrsName()));

			projectedCRS.appendChild(srsName);
		}

	}

	/**
	 * 
	 * @param object
	 * @param doc
	 * @return
	 */
	private static void appendDescribedBy(Object object, Element element,
			Document doc) {

		if (((ComponentMetadata) object).getDescribedBy() != null) {
			Element describedBy = doc.createElement("sml:describedBy");

			Element componentDescription = doc
					.createElement("sml:ComponentDescription");
			describedBy.appendChild(componentDescription);

			if ( ( ( (ComponentMetadata) object).getDescribedBy()).getId() != null) {
				componentDescription.setAttribute("id",
						(((ComponentMetadata) object).getDescribedBy()).getId());
			}

			//append description
			appendDescription( ( ((ComponentMetadata) object).getDescribedBy()).getDescription(),
			                       componentDescription, doc);

			//          append operatedBy
			CitedResponsibleParty[] operatedBy = 
                (((ComponentMetadata) object).getDescribedBy()).getOperatedBy();
			for (int i = 0; i < operatedBy.length; i++) {
				appendOperatedBy(operatedBy[i], componentDescription, doc);
			}

			//          append operatedBy
			CitedResponsibleParty[] manufactedBy = 
                ( ((ComponentMetadata) object).getDescribedBy()).getManufacturedBy();
			for (int i = 0; i < manufactedBy.length; i++) {
				appendManufactedBy(manufactedBy[i], componentDescription, doc);
			}

			//          append operatedBy
			CitedResponsibleParty[] deployedBy = 
                (((ComponentMetadata) object).getDescribedBy()).getDeployedBy();
			for (int i = 0; i < deployedBy.length; i++) {
				appendDeployedBy(deployedBy[i], componentDescription, doc);
			}

			//append reference
			Reference[] reference = (((ComponentMetadata) object).getDescribedBy()).getReference();
			if ((reference != null) && (reference.length > 0)) {
				Element ref = doc.createElement("sml:reference");
				componentDescription.appendChild(ref);

				for (int i = 0; i < reference.length; i++) {
					if (reference[i].isOnLineResource()) {

						appendOnlineResource(reference[i].getOnLineResource(),
								ref, doc);
					}
					if (reference[i].isCitation()) {

					}
				}
			}
			element.appendChild(describedBy);
		}

	}

	private static void appendLocatedUsing(LocationModel[] locatedUsingList,
			Element element, Document doc) {

		if (locatedUsingList == null) {
			return;
		}

		Element locatedUsing = doc.createElement("sml:locatedUsing");
		element.appendChild(locatedUsing);

		//process all locatedUsing objects
		for (int i = 0; i < locatedUsingList.length; i++) {

			// is GeoPositionModel
			if (locatedUsingList[i] instanceof GeoPositionModel) {
				Element geoPositionModel = doc
						.createElement("sml:GeoPositionModel");
				locatedUsing.appendChild(geoPositionModel);

				if (((GeoPositionModel) locatedUsingList[i]).getId() != null) {
					geoPositionModel.setAttribute("id",
							((GeoPositionModel) locatedUsingList[i]).getId());
				}

				appendIdentifiedAs(((GeoPositionModel) locatedUsingList[i])
						.getIdentifiedAs(), geoPositionModel, doc);

				appendClassifiedAs(((GeoPositionModel) locatedUsingList[i])
						.getClassifiedAs(), geoPositionModel, doc);

				appendDescription(((GeoPositionModel) locatedUsingList[i])
						.getDescription(), geoPositionModel, doc);

				//append sourceCRS
				Element sourceCRS = doc.createElement("sml:sourceCRS");
				geoPositionModel.appendChild(sourceCRS);
				appendEngineeringCRS(((GeoPositionModel) locatedUsingList[i])
						.getSourceCRS(), sourceCRS, doc);

				//append referenceCRS
				Element referenceCRS = doc.createElement("sml:referenceCRS");
				geoPositionModel.appendChild(referenceCRS);
				appendCoordinateReferenceSystem(
						((GeoPositionModel) locatedUsingList[i])
								.getReferenceCRS(), referenceCRS, doc);

				//append usesParameters
				appendUsesParametersFromGeoLocation(
						((GeoPositionModel) locatedUsingList[i])
								.getUsesParametersObjects(), geoPositionModel,
						doc);

			}
		}

	}

	private static void appendUsesParametersFromGeoLocation(Object[] objects,
			Element element, Document doc) {

		if (objects == null) {
			return;
		}

		for (int i = 0; i < objects.length; i++) {
			//is GeoLocation
			if (objects[i] instanceof GeoLocation) {
				Element usesParameters = doc
						.createElement("sml:usesParameters");
				element.appendChild(usesParameters);

				Element geoLocation = doc.createElement("sml:GeoLocation");
				usesParameters.appendChild(geoLocation);

				geoLocation.setAttribute("id", ((GeoLocation) objects[i])
						.getId());

				//appends latitude
				Element latitude = doc.createElement("sml:latitude");
				geoLocation.appendChild(latitude);
				appendQuantity(((GeoLocation) objects[i]).getLatitude(),
						latitude, doc);

				//                  appends longitude
				Element longitude = doc.createElement("sml:longitude");
				geoLocation.appendChild(longitude);
				appendQuantity(((GeoLocation) objects[i]).getLongitude(),
						longitude, doc);

				// appends altitude
				if (((GeoLocation) objects[i]).getAltitude() != null) {
					Element altitude = doc.createElement("sml:altitude");
					geoLocation.appendChild(altitude);
					appendQuantity(((GeoLocation) objects[i]).getAltitude(),
							altitude, doc);
				}

				//                  appends trueHeading
				if (((GeoLocation) objects[i]).getTrueHeading() != null) {
					Element trueHeading = doc.createElement("sml:trueHeading");
					geoLocation.appendChild(trueHeading);
					appendQuantity(((GeoLocation) objects[i]).getTrueHeading(),
							trueHeading, doc);
				}

				//                  appends speed
				if (((GeoLocation) objects[i]).getSpeed() != null) {
					Element speed = doc.createElement("sml:speed");
					geoLocation.appendChild(speed);
					appendQuantity(((GeoLocation) objects[i]).getSpeed(),
							speed, doc);
				}

			}

		}
	}

	private static void appendUsesParametersFromDerivedFrom(Object[] objects,
			Element element, Document doc) {
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				//is GeoLocation
				if (objects[i] instanceof BasicResponse) {
					Element usesParameters = doc
							.createElement("sml:usesParameters");
					element.appendChild(usesParameters);

					Element basicResponse = doc
							.createElement("sml:BasicResponse");
					usesParameters.appendChild(basicResponse);

					Element resolution = doc.createElement("sml:resolution");
					basicResponse.appendChild(resolution);

					Element typedQuantity = doc
							.createElement("sml:TypedQuantity");
					resolution.appendChild(typedQuantity);

					typedQuantity.appendChild(doc.createTextNode(""
							+ ((BasicResponse) objects[i]).getResolution()
									.getValue()));

					//sets required attrib type
					typedQuantity.setAttribute("type",
							((BasicResponse) objects[i]).getResolution()
									.getType().toString());

					//sets optional attrib codeSpace
					if (((BasicResponse) objects[i]).getResolution()
							.getCodeSpace() != null) {
						typedQuantity.setAttribute("codeSpace",
								((BasicResponse) objects[i]).getResolution()
										.getCodeSpace().toString());
					}

					//                  sets optional attrib fixed
					if (!((BasicResponse) objects[i]).getResolution().isFixed()) {
						typedQuantity.setAttribute("fixed", "false");
					}

					//                  sets optional attrib uom
					if (((BasicResponse) objects[i]).getResolution().getUom() != null) {
						typedQuantity.setAttribute("uom",
								((BasicResponse) objects[i]).getResolution()
										.getUom().toString());
					}

					//                  sets optional attrib min
					if (!Double.isNaN(((BasicResponse) objects[i])
							.getResolution().getMin())) {
						typedQuantity.setAttribute("min", ""
								+ ((BasicResponse) objects[i]).getResolution()
										.getMin());
					}

					//                  sets optional attrib max
					if (!Double.isNaN(((BasicResponse) objects[i])
							.getResolution().getMax())) {
						typedQuantity.setAttribute("max", ""
								+ ((BasicResponse) objects[i]).getResolution()
										.getMax());
					}

					//sets optional attrib id
					if (((BasicResponse) objects[i]).getResolution().getId() != null) {
						typedQuantity.setAttribute("id",
								((BasicResponse) objects[i]).getResolution()
										.getId());
					}

				}

			}

		}
	}

	private static void appendQuantity(Quantity quantity, Element element,
			Document doc) {

		if (quantity == null) {
			return;
		}

		Element quantityEle = doc.createElement("sml:Quantity");
		element.appendChild(quantityEle);
		quantityEle.appendChild(doc.createTextNode("" + quantity.getValue()));

		//appends uom
		if (quantity.getUom() != null) {
			quantityEle.setAttribute("uom", quantity.getUom().toString());
		}

		//          appends fixed
		if (quantity.isFixed()) {
			quantityEle.setAttribute("fixed", "true");
		}
		if (!quantity.isFixed()) {
			quantityEle.setAttribute("fixed", "false");
		}

		//appends min
		if (!Double.isNaN(quantity.getMin())) {
			quantityEle.setAttribute("min", "" + quantity.getMin());
		}

		//appends max
		if (!Double.isNaN(quantity.getMax())) {
			quantityEle.setAttribute("max", "" + quantity.getMax());
		}

	}

	/**
	 * 
	 * @param description
	 * @param element
	 * @param doc
	 */
	private static void appendDescription(Discussion[] description,
			Element element, Document doc) {

		if (description == null) {
			return;
		}

		for (int i = 0; i < description.length; i++) {

			Element desc = doc.createElement("sml:description");

			desc.appendChild(doc.createTextNode(description[i].getValue()));

			if (description[i].getId() != null) {
				desc.setAttribute("id", description[i].getId());
			}
			if (description[i].getTopic() != null) {
				desc.setAttribute("topic", description[i].getTopic().toString());
			}
			if (description[i].getCodeSpace() != null) {
				desc.setAttribute("codeSpace", description[i].getCodeSpace().toString());

			}

			element.appendChild(desc);
		}

	}

	/**
	 * 
	 * @param onlineResource
	 * @param element
	 * @param doc
	 */
	private static void appendOnlineResource(OnlineResource onlineResource,
			Element element, Document doc) {

		if (onlineResource == null) {
			return;
		}

		Element onlineRes = doc.createElement("iso19115:CI_OnlineResource");

		//append linkage
		Element linkage = doc.createElement("iso19115:linkage");
		linkage.appendChild(doc.createTextNode(onlineResource.getLinkage().getHref().toString()));
		onlineRes.appendChild(linkage);

		//append function
		if (onlineResource.getFunctionCode().getValue() != null) {
			Element function = doc.createElement("iso19115:function");
			function.appendChild(doc.createTextNode(onlineResource.getFunctionCode().getValue()));
			onlineRes.appendChild(function);
		}

		//append protocol
		if (onlineResource.getProtocol() != null) {
			Element protocol = doc.createElement("iso19115:protocol");
			protocol.appendChild(doc.createTextNode(onlineResource.getProtocol()));
			onlineRes.appendChild(protocol);
		}

		//      append applicationProfile
		if (onlineResource.getApplicationProfile() != null) {
			Element applicationProfile = doc.createElement("iso19115:applicationProfile");
			applicationProfile.appendChild(doc.createTextNode(onlineResource.getApplicationProfile()));
			onlineRes.appendChild(applicationProfile);
		}

		//      append name
		if (onlineResource.getOnlineResourceName() != null) {
			Element name = doc.createElement("iso19115:name");
			name.appendChild(doc.createTextNode(onlineResource.getOnlineResourceName()));
			onlineRes.appendChild(name);
		}

		//      append description
		if (onlineResource.getOnlineResourceDescription() != null) {
			Element description = doc.createElement("iso19115:description");
			description.appendChild(doc.createTextNode(onlineResource.getOnlineResourceDescription()));
			onlineRes.appendChild(description);
		}

		element.appendChild(onlineRes);

	}

	private static void appendOperatedBy(
			CitedResponsibleParty responsibleParty, Element element,
			Document doc) {

		if (responsibleParty == null) {
			return;
		}

		Element operatedBy = doc.createElement("sml:operatedBy");
		element.appendChild(operatedBy);
		appendResponsibleParty(responsibleParty, operatedBy, doc);

	}

	private static void appendManufactedBy(
			CitedResponsibleParty responsibleParty, Element element,
			Document doc) {

		if (responsibleParty == null) {
			return;
		}

		Element manufactedBy = doc.createElement("sml:manufactedBy");
		element.appendChild(manufactedBy);
		appendResponsibleParty(responsibleParty, manufactedBy, doc);

	}

	private static void appendDeployedBy(
			CitedResponsibleParty responsibleParty, Element element,
			Document doc) {

		if (responsibleParty == null) {
			return;
		}

		Element deployedBy = doc.createElement("sml:deployedBy");
		element.appendChild(deployedBy);
		appendResponsibleParty(responsibleParty, deployedBy, doc);

	}

	private static void appendContactInfo(ContactInfo contact, Element element,
			Document doc) {

		if (contact == null) {
			return;
		}

		Element cont = doc.createElement("iso19115:contactInfo");
		element.appendChild(cont);

		if (contact.getPhone() != null) {
			appendPhone(contact.getPhone(), cont, doc);
		}

		if (contact.getAddress() != null) {
			appendAddress(contact.getAddress(), cont, doc);
		}

		if (contact.getOnLineResource() != null) {
			appendOnlineResource(contact.getOnLineResource(), cont, doc);
		}

		if (contact.getHoursOfService() != null) {
			Element hours = doc.createElement("iso19115:hoursOfService");
			cont.appendChild(hours);
			hours.appendChild(doc.createTextNode(contact.getHoursOfService()));
		}

		if (contact.getContactInstructions() != null) {
			Element instr = doc.createElement("iso19115:contactInstructions");
			cont.appendChild(instr);
			instr.appendChild(doc.createTextNode(contact
					.getContactInstructions()));
		}

	}

	private static void appendPhone(Phone phone, Element element, Document doc) {

		if (phone == null) {
			return;
		}

		Element phoneEle = doc.createElement("iso19115:phone");
		element.appendChild(phoneEle);

		String[] voice = phone.getVoice();
		if ((voice != null) && (voice.length > 0)) {
			for (int i = 0; i < voice.length; i++) {
				Element actVoice = doc.createElement("iso19115:voice");
				phoneEle.appendChild(actVoice);
				actVoice.appendChild(doc.createTextNode(voice[i]));
			}
		}

		String[] fac = phone.getFacsimile();
		if ((fac != null) && (fac.length > 0)) {
			for (int i = 0; i < fac.length; i++) {
				Element actFac = doc.createElement("iso19115:facsimile");
				phoneEle.appendChild(actFac);
				actFac.appendChild(doc.createTextNode(fac[i]));
			}
		}

	}

	private static void appendAddress(Address address, Element element,
			Document doc) {

		if (address == null) {
			return;
		}

		Element addr = doc.createElement("iso19115:address");
		element.appendChild(addr);

		if (address.getCity() != null) {
			Element city = doc.createElement("iso19115:city");
			addr.appendChild(city);
			city.appendChild(doc.createTextNode(address.getCity()));
		}

		if (address.getAdministrativeArea() != null) {
			Element administrativeArea = doc
					.createElement("iso19115:administrativeArea");
			addr.appendChild(administrativeArea);
			administrativeArea.appendChild(doc.createTextNode(address
					.getAdministrativeArea()));
		}

		if (address.getPostalCode() != null) {
			Element postalCode = doc.createElement("iso19115:postalCode");
			addr.appendChild(postalCode);
			postalCode.appendChild(doc.createTextNode(address.getPostalCode()));
		}

		if (address.getCountry() != null) {
			Element country = doc.createElement("iso19115:country");
			addr.appendChild(country);
			country.appendChild(doc.createTextNode(address.getCountry()));
		}

		String[] deliveryPoints = address.getDeliveryPoint();
		for (int i = 0; i < deliveryPoints.length; i++) {
			Element deliveryPoint = doc.createElement("iso19115:deliveryPoint");
			addr.appendChild(deliveryPoint);
			deliveryPoint.appendChild(doc.createTextNode(deliveryPoints[i]));
		}

		String[] electronicMailAddresses = address.getElectronicMailAddress();
		for (int i = 0; i < electronicMailAddresses.length; i++) {
			Element electronicMailAddress = doc
					.createElement("iso19115:electronicMailAddress");
			addr.appendChild(electronicMailAddress);
			electronicMailAddress.appendChild(doc
					.createTextNode(electronicMailAddresses[i]));
		}

	}

	private static void appendResponsibleParty(
			CitedResponsibleParty responsibleParty, Element element,
			Document doc) {

		if (responsibleParty == null) {
			return;
		}

		Element respParty = doc.createElement("iso19115:CI_ResponsibleParty");
		element.appendChild(respParty);

		//append IndividualName
		if (responsibleParty.getIndividualName() != null) {
			String[] individualNameList = responsibleParty.getIndividualName();
			for (int i = 0; i < individualNameList.length; i++) {
				Element individualName = doc
						.createElement("iso19115:individualName");
				individualName.appendChild(doc
						.createTextNode(individualNameList[i]));
				respParty.appendChild(individualName);
			}
		}

		//      append OrganisationName
		if (responsibleParty.getOrganisationName() != null) {
			String[] organisationNameList = responsibleParty
					.getOrganisationName();
			for (int i = 0; i < organisationNameList.length; i++) {
				Element organisationName = doc
						.createElement("iso19115:organisationName");
				organisationName.appendChild(doc
						.createTextNode(organisationNameList[i]));
				respParty.appendChild(organisationName);
			}
		}

		//      append PositionName
		if (responsibleParty.getPositionName() != null) {
			String[] positionNameList = responsibleParty.getPositionName();
			for (int i = 0; i < positionNameList.length; i++) {
				Element positionName = doc
						.createElement("iso19115:positionName");
				positionName.appendChild(doc
						.createTextNode(positionNameList[i]));
				respParty.appendChild(positionName);
			}
		}

		//      append Role as codelist

		RoleCode[] roleList = responsibleParty.getRoleCode();

		for (int i = 0; i < roleList.length; i++) {
			if (roleList[i].getValue() != null) {
				Element role = doc.createElement("iso19115:role");
				respParty.appendChild(role);
				Element codeList = doc
						.createElement("iso19115:CI_RoleCode_CodeList");
				role.appendChild(codeList);
				codeList
						.appendChild(doc.createTextNode(roleList[i].getValue()));
			}
		}

		//      append contact
		if (responsibleParty.getContactInfo() != null) {
			ContactInfo[] contactInfoList = responsibleParty.getContactInfo();
			for (int i = 0; i < contactInfoList.length; i++) {
				appendContactInfo(contactInfoList[i], respParty, doc);
			}

		}

	}

	private static void appendMeasures(Product[] products, Element element,
			Document doc) {

		if (products == null) {
			return;
		}

		for (int i = 0; i < products.length; i++) {

			Element measures = doc.createElement("sml:measures");
			Element product = doc.createElement("sml:Product");
			if (products[i].getId() != null) {
				product.setAttribute("id", products[i].getId());
			}
			measures.appendChild(product);

			//appends identifiedAs
			appendIdentifiedAs(products[i].getIdentifiedAs(), product, doc);

			//appends classifiedAs
			appendClassifiedAs(products[i].getClassifiedAs(), product, doc);

			//appends description
			appendDescription(products[i].getDescription(), product, doc);

			//appends observable
			if (products[i].getObservable() != null) {
				Element observable = doc.createElement("sml:observable");
				product.appendChild(observable);
				Element phenomenon = doc.createElement("sml:Phenomenon");
				observable.appendChild(phenomenon);

				if (products[i].getObservable().getId() != null) {
					phenomenon.setAttribute("id", products[i].getObservable()
							.getId());
				}

				if (products[i].getObservable().getName() != null) {
					Element name = doc.createElement("sml:name");
					phenomenon.appendChild(name);
					name.appendChild(doc.createTextNode(products[i]
							.getObservable().getName()));
				}

				if (products[i].getObservable().getDefinition() != null) {
					Element description = doc.createElement("sml:description");
					phenomenon.appendChild(description);
					description.appendChild(doc.createTextNode(products[i]
							.getObservable().getDefinition()));
				}
			}

			//appends hasCRS
			if (products[i].getHasCRS() != null) {
				appendHasCRS(products[i].getHasCRS(), product, doc);
			}

			//appends locatedUsing
			if (products[i].getLocatedUsing() != null) {
				appendLocatedUsing(products[i].getLocatedUsing(), product, doc);
			}

			appendDerivedFrom(products[i].getDerivedFrom(), product, doc);

			element.appendChild(measures);
		}

	}

	private static void appendCarries(String[] asset, Element element,
			Document doc) {
		if (asset == null) {
			return;
		}

		for (int i = 0; i < asset.length; i++) {
			Element carries = doc.createElement("sml:carries");
			element.appendChild(carries);
			Element assetEle = doc.createElement("sml:Asset");
			carries.appendChild(assetEle);
			assetEle.appendChild(doc.createTextNode(asset[i]));

		}

	}
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: XMLFactory.java,v $
Revision 1.17  2006/08/24 06:42:16  poth
File header corrected

Revision 1.16  2006/08/07 12:12:30  poth
unneccessary type cast removed / Log statements added

Revision 1.15  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.14  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
