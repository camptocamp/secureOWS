//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/idgenerator/FeatureIdAssigner.java,v 1.31 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.io.datastore.idgenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.io.datastore.Datastore;
import org.deegree.io.datastore.DatastoreException;
import org.deegree.io.datastore.DatastoreTransaction;
import org.deegree.io.datastore.FeatureId;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.MappedPropertyType;
import org.deegree.io.datastore.schema.MappedSimplePropertyType;
import org.deegree.io.datastore.schema.content.MappingField;
import org.deegree.io.datastore.schema.content.SimpleContent;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert.ID_GEN;

/**
 * Responsible for the assigning of valid {@link FeatureId}s which are a prerequisite to
 * the insertion of features in a {@link Datastore}.
 * <p>
 * Prior to the assigning of new feature ids, "equal" features are looked up in the datastore and
 * their feature ids are used.
 * 
 * @see DatastoreTransaction#performInsert(List)
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.31 $, $Date: 2006/11/27 09:07:53 $
 */
public class FeatureIdAssigner {

    /** if an assigned feature id starts with this, it is already stored */
    public static final String EXIST_PREFIX = "!";

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureIdAssigner.class );

    private Map<String, FeatureId> oldFid2NewFidMap = new HashMap<String, FeatureId>();

    private Set<Feature> reassignedFeatures = new HashSet<Feature>();

    private Set<Feature> storedFeatures = new HashSet<Feature>();

    private ID_GEN idGenMode;

    /**
     * Creates a new <code>FeatureIdAssigner</code> instance that generates new feature ids as
     * specified.
     * 
     * @param idGenMode
     */
    public FeatureIdAssigner( ID_GEN idGenMode ) {
        this.idGenMode = idGenMode;
    }

    /**
     * Assigns valid {@link FeatureId}s to the given feature instance and it's subfeatures.
     * 
     * @param feature
     * @param ta
     * @throws IdGenerationException
     */
    public void assignFID( Feature feature, DatastoreTransaction ta )
                            throws IdGenerationException {

        identifyStoredFeatures( feature, ta, new HashSet<Feature>() );

        switch ( this.idGenMode ) {
        case GENERATE_NEW: {
            assignNewFIDs( feature, null, ta );
            break;
        }
        case REPLACE_DUPLICATE: {
            break;
        }
        case USE_EXISTING: {
            break;
        }
        default: {
            throw new IdGenerationException( "Internal error: Unhandled fid generation mode: "
                                             + this.idGenMode );
        }
        }
    }

    /**
     * TODO mark stored features a better way
     */
    public void markStoredFeatures() {
        // hack: mark stored features (with "!")
        for ( Feature f : this.storedFeatures ) {
            String fid = f.getId();
            if ( !fid.startsWith( EXIST_PREFIX ) ) {
                f.setId( EXIST_PREFIX + fid );
            }
        }
    }

    private String identifyStoredFeatures( Feature feature, DatastoreTransaction ta,
                                          Set<Feature> inProcessing )
                            throws IdGenerationException {

        if ( this.reassignedFeatures.contains( feature ) ) {
            return feature.getId();
        }

        inProcessing.add( feature );

        boolean maybeEqual = true;
        String existingFID = null;

        LOG.logDebug( "Checking for existing feature that equals feature with type: '"
                      + feature.getName() + "' and fid: '" + feature.getId() + "'." );

        // build the comparison operations that are needed to select "equal" feature instances
        List<Operation> compOperations = new ArrayList<Operation>();

        FeatureProperty[] properties = feature.getProperties();
        MappedFeatureType ft = (MappedFeatureType) feature.getFeatureType();

        for ( int i = 0; i < properties.length; i++ ) {
            QualifiedName propertyName = properties[i].getName();
            MappedPropertyType propertyType = (MappedPropertyType) ft.getProperty( propertyName );

            Object propertyValue = properties[i].getValue();
            if ( propertyValue instanceof Feature ) {

                if ( inProcessing.contains( propertyValue ) ) {
                    LOG.logDebug( "Stopping recursion at property with '" + propertyName
                                  + "'. Cycle detected." );
                    continue;
                }

                LOG.logDebug( "Recursing on feature property: " + properties[i].getName() );
                String subFeatureId = identifyStoredFeatures( (Feature) propertyValue, ta,
                                                              inProcessing );
                if ( propertyType.isIdentityPart() ) {
                    if ( subFeatureId == null ) {
                        maybeEqual = false;
                    } else {
                        LOG.logDebug( "Need to check for feature property '" + propertyName
                                      + "' with fid '" + subFeatureId + "'." );

                        // build path that selects subfeature 'gml:id' attribute
                        PropertyPath fidSelectPath = PropertyPathFactory.createPropertyPath( feature.getName() );
                        fidSelectPath.append( PropertyPathFactory.createPropertyPathStep( propertyName ) );
                        fidSelectPath.append( PropertyPathFactory.createPropertyPathStep( ( (Feature) propertyValue ).getName() ) );
                        QualifiedName qn = new QualifiedName( CommonNamespaces.GML_PREFIX, "id",
                                                              CommonNamespaces.GMLNS );
                        fidSelectPath.append( PropertyPathFactory.createAttributePropertyPathStep( qn ) );

                        // hack that remove's the gml id prefix
                        MappedFeatureType subFeatureType = (MappedFeatureType) ( (Feature) propertyValue ).getFeatureType();
                        MappedGMLId gmlId = subFeatureType.getGMLId();
                        String prefix = gmlId.getPrefix();
                        if ( subFeatureId.indexOf( prefix ) != 0 ) {
                            throw new IdGenerationException(
                                                             "Internal error: subfeature id '"
                                                                                     + subFeatureId
                                                                                     + "' does not begin with the expected prefix." );
                        }
                        String plainIdValue = subFeatureId.substring( prefix.length() );

                        PropertyIsCOMPOperation propertyTestOperation = new PropertyIsCOMPOperation(
                                                                                                     OperationDefines.PROPERTYISEQUALTO,
                                                                                                     new PropertyName(
                                                                                                                       fidSelectPath ),
                                                                                                     new Literal(
                                                                                                                  plainIdValue ) );

                        compOperations.add( propertyTestOperation );
                    }
                } else
                    LOG.logDebug( "Skipping property '" + propertyName
                                  + "': not a part of the feature type's identity." );
            } else if ( propertyValue instanceof Geometry ) {

                if ( propertyType.isIdentityPart() ) {
                    throw new IdGenerationException(
                                                     "Check for equal geometry properties "
                                                                             + "is not implemented yet. Do not set "
                                                                             + "identityPart to true for geometry properties." );
                }

            } else {
                if ( propertyType.isIdentityPart() ) {
                    LOG.logDebug( "Need to check for simple property '" + propertyName
                                  + "' with value '" + propertyValue + "'." );

                    String value = propertyValue.toString();
                    if ( propertyValue instanceof Date ) {
                        value = TimeTools.getISOFormattedTime( (Date) propertyValue );
                    }

                    PropertyIsCOMPOperation propertyTestOperation = new PropertyIsCOMPOperation(
                                                                                                 OperationDefines.PROPERTYISEQUALTO,
                                                                                                 new PropertyName(
                                                                                                                   propertyName ),
                                                                                                 new Literal(
                                                                                                              value ) );
                    compOperations.add( propertyTestOperation );
                } else {
                    LOG.logDebug( "Skipping property '" + propertyName
                                  + "': not a part of the feature type's identity." );
                }
            }
        }

        if ( ft.getGMLId().isIdentityPart() ) {
            maybeEqual = false;
            LOG.logDebug( "Skipping check for identical features: feature id is part of "
                          + "the feature identity." );
        }
        if ( maybeEqual ) {
            // build the filter from the comparison operations
            Filter filter = null;
            if ( compOperations.size() == 0 ) {
                // no constraints, so any feature of this type will do
            } else if ( compOperations.size() == 1 ) {
                filter = new ComplexFilter( compOperations.get( 0 ) );
            } else {
                LogicalOperation andOperation = new LogicalOperation( OperationDefines.AND,
                                                                      compOperations );
                filter = new ComplexFilter( andOperation );
            }
            if ( filter != null ) {
                LOG.logDebug( "Performing query with filter: " + filter.toXML() );
            } else {
                LOG.logDebug( "Performing unrestricted query." );
            }
            Query query = Query.create( new PropertyPath[0], null, null, null, null,
                                        new QualifiedName[] { feature.getName() }, null, filter, 1,
                                        0, GetFeature.RESULT_TYPE.RESULTS );

            try {
                FeatureCollection fc = ft.performQuery( query, ta );
                if ( fc.size() > 0 ) {
                    existingFID = fc.getFeature( 0 ).getId();
                    LOG.logDebug( "Found existing + matching feature with fid: '" + existingFID
                                  + "'." );
                } else {
                    LOG.logDebug( "No matching feature found." );
                }
            } catch ( DatastoreException e ) {
                throw new IdGenerationException( "Could not perform query to check for "
                                                 + "existing feature instances: " + e.getMessage(),
                                                 e );
            } catch (UnknownCRSException e) {
                e.printStackTrace();
            }
        }

        if ( existingFID != null ) {
            LOG.logDebug( "Feature '" + feature.getName() + "', FID '" + feature.getId()
                          + "' -> existing FID '" + existingFID + "'" );
            feature.setId( existingFID );
            this.storedFeatures.add( feature );
            this.reassignedFeatures.add( feature );
            changeValueForMappedIDProperties( ft, feature );
        }

        return existingFID;
    }

    /**    
     * TODO: remove parentFID hack
     * @param feature
     * @param parentFID
     * @throws IdGenerationException 
     */
    private void assignNewFIDs( Feature feature, FeatureId parentFID, DatastoreTransaction ta )
                            throws IdGenerationException {

        FeatureId newFid = null;
        MappedFeatureType ft = (MappedFeatureType) feature.getFeatureType();

        if ( this.reassignedFeatures.contains( feature ) ) {
            LOG.logDebug( "Skipping feature with fid '" + feature.getId()
                          + "'. Already reassigned." );
            return;
        }

        this.reassignedFeatures.add( feature );
        String oldFidValue = feature.getId();
        if ( oldFidValue == null || "".equals( oldFidValue ) ) {
            LOG.logDebug( "Feature has no FID. Assigning a new one." );
        } else {
            newFid = this.oldFid2NewFidMap.get( oldFidValue );
        }
        if ( newFid == null ) {
            // TODO remove these hacks
            if ( ft.getGMLId().getIdGenerator() instanceof ParentIDGenerator ) {
                newFid = new FeatureId( ft.getGMLId(), parentFID.getValues() );
            } else {
                newFid = ft.generateFid( ta );
            }
            this.oldFid2NewFidMap.put( oldFidValue, newFid );
        }

        LOG.logDebug( "Feature '" + feature.getName() + "', FID '" + oldFidValue + "' -> new FID '"
                      + newFid + "'" );
        // TODO use FeatureId, not it's String value
        feature.setId( newFid.getAsString() );
        changeValueForMappedIDProperties( ft, feature );

        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            Object propertyValue = properties[i].getValue();
            if ( propertyValue instanceof Feature ) {
                assignNewFIDs( (Feature) propertyValue, newFid, ta );
            }
        }
    }

    /**
     * After reassigning a feature id, this method updates all properties of the feature
     * that are mapped to the same column as the feature id.
     * 
     * TODO: find a better way to do this
     * 
     * @param ft
     * @param feature
     */
    private void changeValueForMappedIDProperties( MappedFeatureType ft, Feature feature ) {
        //  TODO remove this hack as well
        String pkColumn = ft.getGMLId().getIdFields()[0].getField();

        FeatureProperty[] properties = feature.getProperties();
        for ( int i = 0; i < properties.length; i++ ) {
            MappedPropertyType propertyType = (MappedPropertyType) ft.getProperty( properties[i].getName() );
            if ( propertyType instanceof MappedSimplePropertyType ) {
                SimpleContent content = ( (MappedSimplePropertyType) propertyType ).getContent();
                if ( content.isUpdateable() ) {
                    if ( content instanceof MappingField ) {
                        String column = ( (MappingField) content ).getField();
                        if ( column.equalsIgnoreCase( pkColumn ) ) {
                            Object fid = null;
                            try {
                                fid = FeatureId.removeFIDPrefix( feature.getId(), ft.getGMLId() );
                            } catch ( DatastoreException e ) {
                                e.printStackTrace();
                            }
                            properties[i].setValue( fid );
                        }
                    }
                }
            }
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: FeatureIdAssigner.java,v $
 Revision 1.31  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.30  2006/09/28 07:49:46  poth
 Generics introduced for Filter - LogicalOperation and required adaptions

 Revision 1.29  2006/09/26 16:43:22  mschneider
 Javadoc corrections + fixed warnings.

 Revision 1.28  2006/08/29 15:51:50  mschneider
 Changed SimpleContent#isVirtual() to SimpleContent#isUpdateable().

 Revision 1.27  2006/08/23 16:31:42  mschneider
 Added handling of virtual properties.

 Revision 1.26  2006/08/22 18:14:42  mschneider
 Refactored due to cleanup of org.deegree.io.datastore.schema package.

 Revision 1.25  2006/08/21 15:44:50  mschneider
 Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

 Revision 1.24  2006/07/13 07:27:11  poth
 *** empty log message ***

 Revision 1.23  2006/07/10 20:14:39  poth
 code formatting

 Revision 1.22  2006/07/10 08:23:10  poth
 code formatation

 Revision 1.21  2006/06/07 17:12:46  mschneider
 Removed direct call of Query constructor.

 Revision 1.20  2006/05/16 16:19:41  mschneider
 Refactored due to the splitting of org.deegree.ogcwebservices.wfs.operation package.

 Revision 1.19  2006/05/15 10:50:05  mschneider
 Lookup of existing features is done in right transaction context now.

 Revision 1.18  2006/05/12 15:26:05  poth
 *** empty log message ***

 Revision 1.17  2006/05/08 07:44:41  poth
 *** empty log message ***

 Revision 1.16  2006/04/27 09:44:59  poth
 *** empty log message ***

 Revision 1.15  2006/04/19 18:23:03  mschneider
 Fixed problem with identification of existing features when no constraints are given at all (no properties). IdentityPart on the feature id is respected now.

 Revision 1.14  2006/04/18 12:44:33  mschneider
 Improved javadoc.

 Revision 1.13  2006/04/10 16:35:47  mschneider
 Added check for geometry properties that have identityPart set to true.

 Revision 1.12  2006/04/07 17:12:45  mschneider
 Added several hacks to make it work. Needs some serious love, though.

 Revision 1.11  2006/04/06 20:25:32  poth
 *** empty log message ***

 Revision 1.10  2006/04/04 20:39:44  poth
 *** empty log message ***

 Revision 1.9  2006/04/04 17:50:31  mschneider
 Renamed checkForEqualFeature() to checkForStoredFeature().

 Revision 1.8  2006/04/04 10:27:19  mschneider
 More work on checkForEqualFeature(). Not activated yet.

 Revision 1.7  2006/03/30 21:20:29  poth
 *** empty log message ***

 Revision 1.6  2006/03/29 14:54:43  mschneider
 Started to implement check that finds equal feature instances.

 Revision 1.5  2006/03/28 13:35:36  mschneider
 Changed getNewId() so transaction context (DatastoreTransaction) and thus JDBC connection is accessible in the method.

 Revision 1.4  2006/02/24 14:34:46  mschneider
 Added hack to handle properties that are mapped to the same column as the feature id.

 Revision 1.3  2006/02/23 15:28:36  mschneider
 Added ParentIDGenerator.

 Revision 1.2  2006/02/04 20:08:54  mschneider
 Adapted to use FeatureId class.

 Revision 1.1  2006/02/03 18:12:17  mschneider
 Initial version.

 ********************************************************************** */