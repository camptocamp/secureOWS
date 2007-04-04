//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/configuration/WFSConfiguration.java,v 1.22 2006/11/27 09:07:53 poth Exp $
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
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wfs.configuration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.InvalidConfigurationException;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.io.datastore.schema.MappedGMLSchema;
import org.deegree.io.datastore.schema.MappedGMLSchemaDocument;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.capabilities.FilterCapabilities;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wfs.WFService;
import org.deegree.ogcwebservices.wfs.capabilities.FeatureTypeList;
import org.deegree.ogcwebservices.wfs.capabilities.FormatType;
import org.deegree.ogcwebservices.wfs.capabilities.GMLObject;
import org.deegree.ogcwebservices.wfs.capabilities.Operation;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;

/**
 * Represents the configuration for a deegree {@link WFService} instance.
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.22 $, $Date: 2006/11/27 09:07:53 $
 */
public class WFSConfiguration extends WFSCapabilities {

    private static final long serialVersionUID = -8929822028461025018L;

    protected static final ILogger LOG = LoggerFactory.getLogger( WFSConfiguration.class );

    private WFSDeegreeParams deegreeParams;

    private Map<QualifiedName, MappedFeatureType> featureTypeMap = new HashMap<QualifiedName, MappedFeatureType>();

    /**
     * Generates a new <code>WFSConfiguration</code> instance from the given parameters.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param featureTypeList
     * @param servesGMLObjectTypeList
     * @param supportsGMLObjectTypeList
     * @param contents
     *            TODO field not verified! Check spec.
     * @param filterCapabilities
     * @param deegreeParams
     * @throws InvalidConfigurationException
     */
    public WFSConfiguration( String version, String updateSequence,
                            ServiceIdentification serviceIdentification,
                            ServiceProvider serviceProvider, OperationsMetadata operationsMetadata,
                            FeatureTypeList featureTypeList, GMLObject[] servesGMLObjectTypeList,
                            GMLObject[] supportsGMLObjectTypeList, Contents contents,
                            FilterCapabilities filterCapabilities, WFSDeegreeParams deegreeParams )
                            throws InvalidConfigurationException {
        super( version, updateSequence, serviceIdentification, serviceProvider, operationsMetadata,
               featureTypeList, servesGMLObjectTypeList, supportsGMLObjectTypeList, contents,
               filterCapabilities );
        this.deegreeParams = deegreeParams;
        try {
            validateFeatureTypeDefinitions();
        } catch ( InvalidConfigurationException e ) {
            LOG.logError( e.getMessage() );
            throw e;
        }
    }

    /**
     * Returns the deegreeParams.
     * 
     * @return the deegreeParams
     */
    public WFSDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

    /**
     * The deegreeParams to set.
     * 
     * @param deegreeParams
     */
    public void setDeegreeParams( WFSDeegreeParams deegreeParams ) {
        this.deegreeParams = deegreeParams;
    }

    /**
     * Returns a <code>Map</code> of the feature types that this configuration defines.
     * 
     * @return keys: feature type names, values: mapped feature types
     */
    public Map<QualifiedName, MappedFeatureType> getMappedFeatureTypes() {
        return this.featureTypeMap;
    }

    /**
     * The <code>WFSConfiguration</code> is processed as follows:
     * <ul>
     * <li>The data directories (as specified in the configuration) are scanned for
     * {@link MappedGMLSchemaDocument}s</li>
     * <li>All {@link MappedFeatureType}s defined in any of the found
     * {@link MappedGMLSchemaDocument}s are extracted, if duplicate feature type definitions
     * occur, an <code>InvalidConfigurationException</code> is thrown.</li>
     * <li>All feature types defined in the FeatureTypeList section of the WFS configuration are
     * checked to have a corresponding {@link MappedFeatureType} definition. If this is not the
     * case (or if the DefaultSRS is not equal to the CRS in the respective datastore definition),
     * an <code>InvalidConfigurationException</code> is thrown.</li>
     * <li>NOTE: the above is not necessary for FeatureTypes that are processed by XSLT-scripts
     * (because they are usually mapped to a different FeatureTypes by XSLT)</li>
     * <li>All feature types that are not defined in the FeatureTypeList section, but have been
     * defined in one of the {@link MappedGMLSchemaDocument}s are added as feature types to
     * the WFS configuration.</li>
     * </ul>
     * </p>
     * 
     * @throws InvalidConfigurationException
     */
    private void validateFeatureTypeDefinitions()
                            throws InvalidConfigurationException {

        // extract the mapped feature types from the given data directory
        this.featureTypeMap = scanForMappedFeatureTypes();
        Map tempFeatureTypeMap = (Map) ( (HashMap) this.featureTypeMap ).clone();

        // check that for each configuration feature type a mapped feature type exists and that
        // their DefaultSRS are identical
        WFSFeatureType[] wfsFTs = getFeatureTypeList().getFeatureTypes();
        for ( int i = 0; i < wfsFTs.length; i++ ) {

            if ( !wfsFTs[i].isVirtual() ) {
                MappedFeatureType mappedFT = this.featureTypeMap.get( wfsFTs[i].getName() );
                if ( mappedFT == null ) {
                    String msg = Messages.getMessage( "WFS_CONF_FT_APP_SCHEMA_MISSING",
                                                      wfsFTs[i].getName() );
                    throw new InvalidConfigurationException( msg );
                }
                if ( !mappedFT.isVisible() ) {
                    String msg = Messages.getMessage( "WFS_CONF_FT_APP_SCHEMA_INVISIBLE",
                                                      wfsFTs[i].getName() );
                    throw new InvalidConfigurationException( msg );
                }
                URI defaultSRS = mappedFT.getGMLSchema().getDefaultSRS();
                if ( !defaultSRS.equals( wfsFTs[i].getDefaultSRS() ) ) {
                    String msg = Messages.getMessage( "WFS_CONF_FT_APP_SCHEMA_WRONG_SRS",
                                                      wfsFTs[i].getName(),
                                                      wfsFTs[i].getDefaultSRS(), defaultSRS );
                    throw new InvalidConfigurationException( msg );
                }
            }

            // remove datastore feature type
            tempFeatureTypeMap.remove( wfsFTs[i].getName() );
        }

        // add all remaining mapped feature types to the WFS configuration
        Iterator it = tempFeatureTypeMap.keySet().iterator();
        while ( it.hasNext() ) {
            QualifiedName featureTypeName = (QualifiedName) it.next();
            MappedFeatureType mappedFT = (MappedFeatureType) tempFeatureTypeMap.get( featureTypeName );
            if ( mappedFT.isVisible() ) {
                try {
                    getFeatureTypeList().addFeatureType( createWFSFeatureType( mappedFT ) );
                } catch ( UnknownCRSException e ) {
                   throw new InvalidConfigurationException( e );
                }
            }
        }
    }

    /**
     * Scans for and extracts the <code>MappedFeatureType</code> s that are located in the data
     * directories of the current WFS configuration.
     * 
     * @return keys: featureTypeNames, values: mapped feature types
     * @throws InvalidConfigurationException
     */
    private Map<QualifiedName, MappedFeatureType> scanForMappedFeatureTypes()
                            throws InvalidConfigurationException {
        List<String> fileNameList = new ArrayList<String>();
        String[] dataDirectories = getDeegreeParams().getDataDirectories();

        for ( int i = 0; i < dataDirectories.length; i++ ) {
            File file = new File( dataDirectories[i] );
            String[] list = file.list( new XSDFileFilter() );
            if ( list != null ) {
                if ( list.length == 0 ) {
                    LOG.logInfo( "Specified datastore directory '" + dataDirectories[i]
                                 + "' does not contain any '.xsd' files." );
                }
                for ( int j = 0; j < list.length; j++ ) {
                    fileNameList.add( dataDirectories[i] + '/' + list[j] );
                }
            } else {
                LOG.logInfo( "Specified datastore directory '" + dataDirectories[i]
                             + "' does not denote a directory." );
            }
        }
        String[] fileNames = fileNameList.toArray( new String[fileNameList.size()] );
        return extractMappedFeatureTypes( fileNames );
    }

    /**
     * Extracts the {@link MappedFeatureType} s which are defined in the given array of
     * mapped application schema filenames. Files that do not conform to mapped GML Application
     * Schemas definition are omitted, so are multiple definitions of the same feature types.
     * 
     * @param fileNames
     *            fileNames to be scanned
     * @return keys: feature type names, values: mapped feature types
     * @throws InvalidConfigurationException
     */
    private Map<QualifiedName, MappedFeatureType> extractMappedFeatureTypes( String[] fileNames )
                            throws InvalidConfigurationException {

        Map<QualifiedName, MappedFeatureType> featureTypeMap = new HashMap<QualifiedName, MappedFeatureType>(
                                                                                                              fileNames.length );

        for ( int i = 0; i < fileNames.length; i++ ) {
            LOG.logInfo( "Parsing (mapped) GML application schema file '" + fileNames[i] + "'." );
            try {
                URL fileURL = new File( fileNames[i] ).toURL();
                MappedGMLSchemaDocument doc = new MappedGMLSchemaDocument();
                doc.load( fileURL );
                MappedGMLSchema gmlSchema = doc.parseMappedGMLSchema();

                FeatureType[] featureTypes = gmlSchema.getFeatureTypes();
                for ( int j = 0; j < featureTypes.length; j++ ) {
                    MappedFeatureType ft = featureTypeMap.get( featureTypes[j].getName() );
                    if ( ft != null ) {
                        String msg = Messages.getMessage( "WFS_CONF_MULTIPLE_FEATURE_TYPE_DEF",
                                                          featureTypes[j].getName(), fileNames[i] );
                        throw new InvalidConfigurationException( msg );
                    }
                    featureTypeMap.put( featureTypes[j].getName(),
                                        (MappedFeatureType) featureTypes[j] );
                }
            } catch ( IOException e ) {
                String msg = "Error loading '" + fileNames[i] + "': " + e.getMessage();
                throw new InvalidConfigurationException( msg, e );
            } catch ( Exception e ) {
                String msg = "Error parsing '" + fileNames[i] + "': " + e.getMessage();
                throw new InvalidConfigurationException( msg, e );
            }
        }
        return featureTypeMap;
    }

    /**
     * Creates a (minimal) <code>WFSFeatureType</code> from the given
     * <code>MappedFeatureType</code>.
     * 
     * @param rootfeatureType
     * @throws UnknownCRSException 
     */
    private WFSFeatureType createWFSFeatureType( MappedFeatureType mappedFeatureType ) throws UnknownCRSException {
        Operation[] operations = new Operation[1];
        operations[0] = new Operation( Operation.QUERY );
        FormatType[] outputFormats = new FormatType[1];
        // according to WFS 1.1.0 spec text/xml; subtype=gml/3.1.1
        // is the only mandatory format
        outputFormats[0] = new FormatType( null, null, null, "text/xml; subtype=gml/3.1.1" );
        CoordinateSystem crs = CRSFactory.create( "EPSG:4326" );
        Envelope[] wgs84BoundingBoxes = new Envelope[1];
        wgs84BoundingBoxes[0] = GeometryFactory.createEnvelope( -180, -90, 180, 90, crs );
        URI defaultSRS = mappedFeatureType.getGMLSchema().getDefaultSRS();
        WFSFeatureType featureType = new WFSFeatureType( mappedFeatureType.getName(), null, null,
                                                         null, defaultSRS, null, operations,
                                                         outputFormats, wgs84BoundingBoxes, null );
        return featureType;
    }

    static class XSDFileFilter implements FilenameFilter {

        /**
         * Tests if a specified file should be included in a file list.
         * 
         * @param dir
         *            the directory in which the file was found
         * @param name
         *            the name of the file
         * @return <code>true</code> if and only if the name should be included in the file list;
         * <code>false</code> otherwise
         */
        public boolean accept( File dir, String name ) {
            int pos = name.lastIndexOf( "." );
            String ext = name.substring( pos + 1 );
            return ext.toUpperCase().equals( "XSD" );
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSConfiguration.java,v $
 Revision 1.22  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.21  2006/11/15 18:39:16  mschneider
 Fixed reference to Message.

 Revision 1.20  2006/11/02 12:34:10  mschneider
 Fixed NPE in #postprocess() that occured when a feature type definition in the WFS configuration had no DefaultSRS element.

 Revision 1.19  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.18  2006/10/02 16:54:47  mschneider
 Added handling of virtual feature type, i.e. feature types that are handled via XSL-scripts.

 Revision 1.17  2006/08/24 06:42:17  poth
 File header corrected

 Revision 1.16  2006/07/12 14:46:18  poth
 comment footer added

 ********************************************************************** */