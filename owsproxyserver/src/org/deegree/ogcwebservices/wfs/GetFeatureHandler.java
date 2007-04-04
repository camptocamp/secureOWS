//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/GetFeatureHandler.java,v 1.29 2006/11/16 08:53:21 mschneider Exp $
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
 Aennchenstr. 19
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
package org.deegree.ogcwebservices.wfs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.concurrent.ExecutionFinishedEvent;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.MappedFeatureType;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.capabilities.WFSOperationsMetadata;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.operation.FeatureResult;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;
import org.deegree.owscommon.OWSDomainType;

/**
 * Handles {@link GetFeature} requests to the {@link WFService}. Since a {@link GetFeature}
 * request may contain more than one {@link Query}, each {@link Query} is delegated to an own
 * thread.
 * <p>
 * The results of all threads are collected and merged before they are returned to the calling
 * {@link WFService} as a single {@link FeatureCollection}.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.29 $, $Date: 2006/11/16 08:53:21 $
 */
class GetFeatureHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( GetFeatureHandler.class );

    private static final String EPSG_URL = "http://www.opengis.net/gml/srs/epsg.xml#";

    // upper limit for timeout (overrides WFS configuration)
    private static long MAX_TIMEOUT_MILLIS = 10 * 60 * 1000;

    private WFService service;

    private int maxFeatures = -1;

    /**
     * Creates a new instance of <code>GetFeatureHandler</code>. Only called by the
     * <code>WFService</code>.
     * 
     * @param service
     *            associated WFService
     */
    GetFeatureHandler( WFService service ) {
        this.service = service;
        WFSCapabilities capa = service.getCapabilities();
        WFSOperationsMetadata md = (WFSOperationsMetadata) capa.getOperationsMetadata();
        OWSDomainType[] dt = md.getConstraints();
        for ( int i = 0; i < dt.length; i++ ) {
            if ( dt[i].getName().equals( "DefaultMaxFeatures" ) ) {
                try {
                    String tmp = dt[i].getValues()[0];
                    maxFeatures = Integer.parseInt( tmp );
                } catch ( Exception e ) {
                    //e.printStackTrace();
                }
                break;
            }
        }
        LOG.logDebug( "default maxFeatures " + maxFeatures );
    }

    /**
     * Handles a {@link GetFeature} request by delegating the contained {@link Query} objects to
     * different Threads.
     * <p>
     * If at least one query fails an exception will be thrown and all running threads will be
     * stopped.
     * 
     * @param getFeature
     * @return result of the request 
     * @throws OGCWebServiceException
     */
    FeatureResult handleRequest( GetFeature getFeature )
                            throws OGCWebServiceException {
        LOG.entering();

        if ( getFeature.getMaxFeatures() > maxFeatures ) {
            getFeature.setMaxFeatures( maxFeatures );
        }

        LOG.logDebug( "maxFeatures " + getFeature.getMaxFeatures() );

        Query[] queries = getFeature.getQuery();
        List<Callable<FeatureCollection>> queryTasks = new ArrayList<Callable<FeatureCollection>>(
                                                                                                   queries.length );

        for ( Query query : queries ) {
            QualifiedName[] ftNames = query.getTypeNames();
            // TODO joins between feature types
            if ( ftNames.length > 1 ) {
                String msg = "Multiple feature types in a Query (joins over feature types) "
                             + "are not the supported by this WFS implementation.";
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            QualifiedName ftName = ftNames[0];

            MappedFeatureType ft = this.service.getMappedFeatureType( ftName );

            if ( ft == null ) {
                String msg = Messages.getMessage( "WFS_FEATURE_TYPE_UNKNOWN", ftName );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            if ( !ft.isVisible() ) {
                String msg = Messages.getMessage( "WFS_FEATURE_TYPE_INVISIBLE", ftName );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }

            // check and normalized requested SRS
            String srsName = query.getSrsName();
            if ( srsName != null ) {
                WFSFeatureType wfsFT = this.service.getCapabilities().getFeatureTypeList().getFeatureType(
                                                                                                           ftName );
                String normalizedSrsName = normalizeSrsName( srsName );
                query.setSrsName(normalizedSrsName);
                
                if ( !( wfsFT.supportsSrs( normalizedSrsName ) ) ) {
                    String msg = Messages.getMessage( "WFS_FEATURE_TYPE_SRS_UNSUPPORTED", ftName, srsName );
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                }
            }

            QueryTask task = new QueryTask( query, ft );
            queryTasks.add( task );
        }

        WFSConfiguration conf = (WFSConfiguration) service.getCapabilities();
        long timeout = conf.getDeegreeParams().getRequestTimeLimit() * 1000;
        if ( timeout > MAX_TIMEOUT_MILLIS ) {
            // limit max timeout
            timeout = MAX_TIMEOUT_MILLIS;
        }

        List<ExecutionFinishedEvent<FeatureCollection>> finishedEvents = null;
        try {
            finishedEvents = Executor.getInstance().performSynchronously( queryTasks, timeout );
        } catch ( InterruptedException e ) {
            String msg = "Exception occured while waiting for the GetFeature results: "
                         + e.getMessage();
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        // use id of the request as id of the result feature collection
        // to allow identification of the original request that produced
        // the feature collection
        FeatureCollection fc = null;
        if ( getFeature.getResultType() == RESULT_TYPE.RESULTS ) {
            fc = mergeResults( getFeature.getId(), finishedEvents );
        } else {
            fc = mergeHits( getFeature.getId(), finishedEvents );
        }

        FeatureResult fr = new FeatureResult( getFeature, fc );

        LOG.exiting();
        return fr;
    }

    /**
     * Merges the results of the request subparts into one feature collection.
     * 
     * @param fcid
     *            id of the new (result) feature collection
     * @param finishedEvents
     * @return feature collection containing all features from all responses
     * @throws OGCWebServiceException
     */
    private FeatureCollection mergeResults(
                                           String fcid,
                                           List<ExecutionFinishedEvent<FeatureCollection>> finishedEvents )
                            throws OGCWebServiceException {

        FeatureCollection result = null;

        try {
            for ( ExecutionFinishedEvent<FeatureCollection> event : finishedEvents ) {
                if ( result == null ) {
                    result = event.getResult();
                } else {
                    result.addAll( event.getResult() );
                }
            }
        } catch ( CancellationException e ) {
            String msg = Messages.getMessage( "WFS_GET_FEATURE_TIMEOUT" );
            LOG.logInfo( msg );
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        } catch ( Throwable t ) {
            String msg = Messages.getMessage( "WFS_GET_FEATURE_BACKEND", t.getMessage() );
            LOG.logError( msg, t );
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        result.setId( fcid );
        result.setAttribute( "numberOfFeatures", "" + result.size() );
        return result;
    }

    /**
     * Merges the results of the request subparts into one feature collection.
     * <p>
     * This method is used if only the HITS have been requested, i.e. the number of features.
     * 
     * TODO: Do this a better way (maybe change feature model).
     * 
     * @param fcid
     *            id of the new (result) feature collection
     * @param finishedEvents
     * @return empty feature collection with "numberOfFeatures" attribute
     * @throws OGCWebServiceException
     */
    private FeatureCollection mergeHits(
                                        String fcid,
                                        List<ExecutionFinishedEvent<FeatureCollection>> finishedEvents )
                            throws OGCWebServiceException {

        FeatureCollection result = null;
        int numberOfFeatures = 0;

        try {
            for ( ExecutionFinishedEvent<FeatureCollection> event : finishedEvents ) {
                FeatureCollection fc = event.getResult();
                try {
                    numberOfFeatures += Integer.parseInt( ( fc.getAttribute( "numberOfFeatures" ) ) );
                } catch ( NumberFormatException e ) {
                    String msg = "Internal error. Could not parse 'numberOfFeatures' attribute "
                                 + "of sub-result as an integer value.";
                    throw new OGCWebServiceException( this.getClass().getName(), msg );
                }
                if ( result == null ) {
                    result = fc;
                } else {
                    result.addAll( fc );
                }
            }
        } catch ( CancellationException e ) {
            String msg = Messages.getMessage( "WFS_GET_FEATURE_TIMEOUT" );
            LOG.logInfo( msg );
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        } catch ( Throwable t ) {
            String msg = Messages.getMessage( "WFS_GET_FEATURE_BACKEND", t.getMessage() );
            LOG.logError( msg );
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        result.setId( fcid );
        result.setAttribute( "numberOfFeatures", "" + numberOfFeatures );
        return result;
    }

    /**
     * Returns a normalized version of the given srs identifier.
     * <p>
     * Names in the format: <code>http://www.opengis.net/gml/srs/epsg.xml#XYZ</code> are returned
     * as <code>EPSG:XYZ</code>.
     * 
     * @param srsName
     *            name of the srs, <code>EPSG:xyz</code> 
     * @return a normalized version of <code>srsName</code>
     */
    private String normalizeSrsName( String srsName ) {
        String normalizedName = srsName;
        if ( srsName.startsWith( EPSG_URL ) ) {
            String epsgCode = srsName.substring( EPSG_URL.length() );
            normalizedName = "EPSG:" + epsgCode;
        }
        return normalizedName;
    }

    // ///////////////////////////////////////////////////////////////////////////
    //                           inner classes                                  //
    // ///////////////////////////////////////////////////////////////////////////    

    /**
     * Inner class for performing queries on a mapped feature type (datastore).
     */
    private class QueryTask implements Callable<FeatureCollection> {

        private Query query;

        private MappedFeatureType ft;

        QueryTask( Query query, MappedFeatureType ft ) {
            this.query = query;
            this.ft = ft;
        }

        /**
         * Performs the associated {@link Query} and returns the result.
         * 
         * @return resulting feature collection
         * @throws Exception 
         */
        public FeatureCollection call()
                                throws Exception {
            FeatureCollection result = this.ft.performQuery( this.query );
            return result;
        }
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to:
 *  
 * $Log: GetFeatureHandler.java,v $
 * Revision 1.29  2006/11/16 08:53:21  mschneider
 * Merged messages from org.deegree.ogcwebservices.wfs and its subpackages.
 *
 * Revision 1.28  2006/11/09 17:46:15  mschneider
 * Added check for srsName in queries.
 *
 * Revision 1.27  2006/10/17 13:13:13  schmitz
 * Added the last missing type parameters.
 *
 * Revision 1.26  2006/10/09 12:49:02  poth
 * bug fix - setting new ID to merged feature collections
 *
 * Revision 1.25  2006/08/30 18:10:17  mschneider
 * Improved javadoc.
 *
 * Revision 1.24  2006/08/14 16:47:54  mschneider
 * Improved error logging.
 *
 * Revision 1.23  2006/08/14 13:16:32  mschneider
 * Moved magic number (timeout) to constant.
 *
 * Revision 1.22  2006/08/14 13:02:57  mschneider
 * Adapted to use new concurrent API (Executor).
 *
 * Revision 1.21  2006/06/29 11:33:39  poth
 * logging changed
 *
 * Revision 1.20  2006/06/01 15:19:16  mschneider
 * Fixed footer.
 *
 * Revision 1.19  2006/05/29 16:47:08  mays
 * changes by AP in mergeResults()
 *
 * Revision 1.18  2006/05/26 09:44:01  poth
 * *** empty log message ***
 *
 * Revision 1.17  2006/04/06 20:25:21  poth
 * *** empty log message ***
 * 
 * Revision 1.16  2006/03/30 21:20:23  poth
 * *** empty log message ***
 *
 * Revision 1.15  2006/02/23 13:15:40  poth
 * *** empty log message ***
 *
 * Revision 1.14  2006/02/05 21:22:32  mschneider
 * Added handling for invisible feature types.
 * 
 * Revision 1.13  2006/02/02 20:42:54  mschneider
 * Hail to the style guide.
 * 
 * Revision 1.12  2006/01/08 14:09:35  poth
 * *** empty log message ***
 * 
 * Revision 1.11  2005/12/15 08:51:58  poth
 * no message
 * 
 * Revision 1.10  2005/12/13 14:37:55  poth
 * no message
 * 
 * Revision 1.9  2005/12/04 14:45:45  poth
 * no message
 * 
 * Revision 1.8  2005/11/30 07:36:05  deshmukh
 * *** empty log message ***
 * 
 * Revision 1.7 2005/11/17 15:35:07 deshmukh
 * *** empty log message ***
 * 
 * Revision 1.6 2005/11/16 13:44:59 mschneider
 * Merge of wfs development branch.
 * 
 * Revision 1.5.2.1 2005/11/08 15:32:23 mschneider
 * Refactored to new feature type model.
 *
 * Revision 1.5 2005/09/27 19:53:19 poth
 * no message
 * 
 * Revision 1.4 2005/09/02 15:15:45 taddei
 * removed println
 * 
 * Revision 1.3 2005/09/01 13:00:46 mschneider
 * Changes due to correction of "XXXDatstore" names to "XXXDatastore".
 * 
 * Revision 1.2 2005/08/26 21:27:18 pot
 * no message
 * 
 * Revision 1.1 2005/08/26 21:11:29 poth
 * no message
 * 
 * Revision 1.16 2005/08/24 16:09:53 mschneider
 * Renamed GenericName to QualifiedName.
 * 
 * Revision 1.15 2005/08/19 07:45:11 poth
 * no message
 * 
 * Revision 1.14 2005/08/09 15:48:24 poth
 * no message
 * 
 * Revision 1.13 2005/08/02 15:01:21 mschneider
 * Improved exception handling (still not done).
 *
 ************************************************************************************************* */