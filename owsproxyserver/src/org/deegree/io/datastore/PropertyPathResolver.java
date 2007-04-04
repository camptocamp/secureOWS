//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/PropertyPathResolver.java,v 1.27 2006/09/26 16:42:12 mschneider Exp $
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
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.io.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcbase.ElementStep;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.ogcbase.PropertyPathStep;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * Helper class that resolves {@link PropertyPath} instances (e.g. PropertyName
 * elements in {@link GetFeature}) against the property structure of {@link MappedFeatureType}s.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.27 $, $Date: 2006/09/26 16:42:12 $
 */
public class PropertyPathResolver {

    private static final ILogger LOG = LoggerFactory.getLogger( PropertyPathResolver.class );

    /**
     * Ensures that the requested property begins with a "feature type step".
     * <p>
     * This is necessary, as section 7.4.2 of the Web Feature Implementation Specification 1.1.0 states:
     * </p>
     * <p>
     * The first step of a relative location path <b>may</b> correspond to the root element of the
     * feature property being referenced <b>or</b> to the root element of the feature type with the
     * next step corresponding to the root element of the feature property being referenced.
     * </p> 
     * 
     * @param ft featureType that the requested properties refer to
     * @param path requested property
     * @return normalized path, i.e. it begins with a feature type step
     */
    public static PropertyPath normalizePropertyPath( MappedFeatureType ft, PropertyPath path ) {
        QualifiedName featureTypeName = ft.getName();
        if ( !featureTypeName.equals( path.getStep( 0 ).getPropertyName() ) ) {
            path.prepend( PropertyPathFactory.createPropertyPathStep( featureTypeName ) );
        }
        return path;
    }

    /**
     * Ensures that all requested properties begin with a feature type step. 
     * <p>     
     * If no properties are specified at all, a single PropertyPath entry is created that selects
     * the whole feature type.
     * </p>
     * 
     * @param ft feature type that the requested properties refer to
     * @param paths requested properties, may not be null
     * @return normalized paths
     */
    public static PropertyPath[] normalizePropertyPaths( MappedFeatureType ft, PropertyPath[] paths ) {
        for ( int i = 0; i < paths.length; i++ ) {
            paths[i] = normalizePropertyPath( ft, paths[i] );
        }
        if ( paths.length == 0 ) {
            QualifiedName featureTypeName = ft.getName();
            paths = new PropertyPath[] { PropertyPathFactory.createPropertyPath( featureTypeName ) };
        }
        return paths;
    }

    /**
     * Determines the properties of the given feature type that have to be fetched based on the requested
     * property paths.
     * <p>
     * Returns a helper <code>Map</code> that associates each (requested) property of the feature
     * with the property paths that request it. Note that the returned helper map may contain more
     * properties than specified. This behaviour is due to section 9.2 of the Web Feature
     * Implementation Specification 1.1.0:
     * </p>
     * <p>
     * In the event that a WFS encounters a query that does not select all mandatory properties of
     * a feature, the WFS will internally augment the property name list to include all mandatory
     * property names.
     * </p>
     * <p>
     * Note that every requested property path must begin with a step that selects the given feature
     * type.
     * </p>
     * 
     * @param ft feature type
     * @param requestedPaths
     * @return <code>Map</code>, key class: <code>MappedPropertyType</code>, value class:
     *         <code>Collection</code> (of <code>PropertyPath</code> instances)
     * @throws PropertyPathResolvingException
     */
    public static Map<MappedPropertyType, Collection<PropertyPath>> determineFetchProperties(
                                                                                             MappedFeatureType ft,
                                                                                             PropertyPath[] requestedPaths )
                            throws PropertyPathResolvingException {

        Map<MappedPropertyType, Collection<PropertyPath>> requestedMap = null;
        requestedMap = determineRequestedProperties( ft, requestedPaths );
        requestedMap = augmentFetchProperties( ft, requestedMap );
        return requestedMap;
    }

    /**
     * Builds a map that associates each requested property of the feature with the property paths
     * that request it. The property path may well select a property of a subfeature, but the key
     * values in the map are always (direct) properties of the given feature type.
     * 
     * @param ft feature type
     * @param requestedPaths
     * @return map that associates each requested property with the property paths that request it
     * @throws PropertyPathResolvingException
     */
    private static Map<MappedPropertyType, Collection<PropertyPath>> determineRequestedProperties(
                                                                                                  MappedFeatureType ft,

                                                                                                  PropertyPath[] requestedPaths )
                            throws PropertyPathResolvingException {

        Map<MappedPropertyType, Collection<PropertyPath>> propertyMap = new LinkedHashMap<MappedPropertyType, Collection<PropertyPath>>();

        for ( int i = 0; i < requestedPaths.length; i++ ) {
            PropertyPath requestedPath = requestedPaths[i];
            if ( requestedPath.getStep( 0 ).getPropertyName().equals( ft.getName() ) ) {
                if ( requestedPath.getSteps() == 1 ) {
                    // path requests the whole feature
                    PropertyType[] allProperties = ft.getProperties();
                    for ( int j = 0; j < allProperties.length; j++ ) {
                        Collection<PropertyPath> paths = propertyMap.get( allProperties[j] );
                        if ( paths == null ) {
                            paths = new ArrayList<PropertyPath>();
                        }
                        PropertyPath newPropertyPath = PropertyPathFactory.createPropertyPath( ft.getName() );
                        newPropertyPath.append( PropertyPathFactory.createPropertyPathStep( allProperties[j].getName() ) );
                        paths.add( newPropertyPath );
                        propertyMap.put( (MappedPropertyType) allProperties[j], paths );
                    }
                } else {
                    // path requests a certain property
                    QualifiedName propertyName = requestedPath.getStep( 1 ).getPropertyName();
                    PropertyType property = ft.getProperty( propertyName );
                    if ( property == null ) {
                        String msg = Messages.getMessage( "DATASTORE_NO_SUCH_PROPERTY2",
                                                          requestedPath, ft.getName(), propertyName );
                        throw new PropertyPathResolvingException( msg );
                    }
                    Collection<PropertyPath> paths = propertyMap.get( property );
                    if ( paths == null ) {
                        paths = new ArrayList<PropertyPath>();
                    }
                    paths.add( requestedPath );
                    propertyMap.put( (MappedPropertyType) property, paths );
                }
            } else {
                String msg = "Internal error in PropertyPathResolver: no property with name '"
                             + requestedPath + "' in feature type '" + ft.getName() + "'.";
                throw new PropertyPathResolvingException( msg );
            }
        }
        return propertyMap;
    }

    /**
     * Returns an augmented version of the input map that contains additional entries for all
     * mandatory properties of the feature type.
     * 
     * @param ft feature type
     * @param requestedMap
     * @return augmented version of the input map
     * @throws PropertyPathResolvingException
     */
    private static Map<MappedPropertyType, Collection<PropertyPath>> augmentFetchProperties(
                                                                                            MappedFeatureType ft,
                                                                                            Map<MappedPropertyType, Collection<PropertyPath>> requestedMap ) {

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            LOG.logDebug( "Properties to be fetched for feature type '" + ft.getName() + "':" );
        }

        Map<MappedPropertyType, Collection<PropertyPath>> augmentedMap = new LinkedHashMap<MappedPropertyType, Collection<PropertyPath>>();
        PropertyType[] allProperties = ft.getProperties();
        for ( int i = 0; i < allProperties.length; i++ ) {
            MappedPropertyType property = (MappedPropertyType) allProperties[i];
            Collection<PropertyPath> requestingPaths = requestedMap.get( property );
            if ( requestingPaths != null ) {
                LOG.logDebug( "- " + property.getName() );
                augmentedMap.put( property, requestingPaths );
                for ( PropertyPath path : requestingPaths ) {
                    LOG.logDebug( "  - Requested by path: '" + path + "'" );
                }
            } else if ( property.getMinOccurs() > 0 ) {
                LOG.logDebug( "- " + property.getName() + " (augmented)" );
                Collection<PropertyPath> mandatoryPaths = new ArrayList<PropertyPath>();
                List<PropertyPathStep> stepList = new ArrayList<PropertyPathStep>( 2 );
                stepList.add( new ElementStep( ft.getName() ) );
                stepList.add( new ElementStep( property.getName() ) );
                PropertyPath mandatoryPath = new PropertyPath( stepList );
                mandatoryPaths.add( mandatoryPath );
                augmentedMap.put( property, mandatoryPaths );
            }
        }
        return augmentedMap;
    }

    /**
     * Determines the sub property paths that are needed to fetch the given property paths for the
     * also given property.
     * 
     * @param featureType
     * @param propertyPaths
     * @return sub property paths that are needed to fetch the given property paths
     */
    public static PropertyPath[] determineSubPropertyPaths( MappedFeatureType featureType,
                                                           Collection<PropertyPath> propertyPaths ) {
        Collection<PropertyPath> subPropertyPaths = new ArrayList<PropertyPath>();

        Iterator iter = propertyPaths.iterator();
        while ( iter.hasNext() ) {
            PropertyPath path = (PropertyPath) iter.next();
            if ( path.getSteps() > 2 ) {
                subPropertyPaths.add( PropertyPathFactory.createPropertyPath( path, 2,
                                                                              path.getSteps() ) );
            } else {
                PropertyType[] subProperties = featureType.getProperties();
                for ( int i = 0; i < subProperties.length; i++ ) {
                    PropertyPath subPropertyPath = PropertyPathFactory.createPropertyPath( featureType.getName() );
                    subPropertyPath.append( PropertyPathFactory.createPropertyPathStep( subProperties[i].getName() ) );
                    subPropertyPaths.add( subPropertyPath );
                }
            }
        }

        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            LOG.logDebug( "Original property paths:" );
            for ( PropertyPath path : propertyPaths ) {
                LOG.logDebug( "- '" + path + "'" );
            }
            LOG.logDebug( "Sub feature property paths:" );
            for ( PropertyPath path : subPropertyPaths ) {
                LOG.logDebug( "- '" + path + "'" );
            }
        }
        PropertyPath[] subPaths = subPropertyPaths.toArray( new PropertyPath[subPropertyPaths.size()] );
        return subPaths;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: PropertyPathResolver.java,v $
 Revision 1.27  2006/09/26 16:42:12  mschneider
 Javadoc corrections + fixed warnings.

 Revision 1.26  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.25  2006/08/06 20:23:48  poth
 unneccessary type cast removed

 Revision 1.24  2006/08/06 20:21:30  poth
 never thrown exception removed from augmentFetchProperties

 Revision 1.23  2006/06/01 15:23:41  mschneider
 Renamed determineRequestedProperties() to determineFetchProperties(). Changed behaviour: not-requested, but mandatory properties are now augmented to be always fetched.

 Revision 1.22  2006/06/01 13:09:32  mschneider
 Fixed footer.

 ********************************************************************** */