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
 53115 Bonn
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
package org.deegree.ogcwebservices.csw.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.DBPoolException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.Record;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * 
 * @version $Revision: 1.18 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.18 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public class CatalogueHarvester extends AbstractHarvester {

    private static final ILogger LOG = LoggerFactory.getLogger( CatalogueHarvester.class );

    private static CatalogueHarvester ch = null;

    private enum HarvestOperation {
        insert, update, delete, nothing
    };

    /**
     * singelton
     * 
     * @return
     */
    public static CatalogueHarvester getInstance() {
        if ( ch == null ) {
            ch = new CatalogueHarvester();
        }
        return ch;
    }

    @Override
    public void run() {
        LOG.logDebug( "starting harvest iteration for CatalogueHarvester." ); 
        try {
            HarvestRepository repository = HarvestRepository.getInstance();

            List<URI> sources = repository.getSources();
            for ( Iterator iter = sources.iterator(); iter.hasNext(); ) {
                URI source = (URI) iter.next();
                try {
                    // determine if source shall be harvested
                    if ( shallHarvest( source, ResourceType.catalogue ) ) {
                        // mark source as currently being harvested
                        inProgress.add( source );
                        HarvestProcessor processor = new HarvestProcessor( this, source );
                        processor.start();
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                    LOG.logError( Messages.format("CatalogueHarvester.exception1", source), e ); 
                    informResponseHandlers( source, e );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( Messages.getString("CatalogueHarvester.exception2"), e ); 
        }

    }

    /**
     * inner class for processing asynchronous harvesting of a catalogue
     * 
     * @version $Revision: 1.18 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version 1.0. $Revision: 1.18 $, $Date: 2006/07/12 14:46:17 $
     * 
     * @since 2.0
     */
    protected class HarvestProcessor extends AbstractHarvestProcessor {

        private Map<String, Record> records = new HashMap( 10000 );

        HarvestProcessor( AbstractHarvester owner, URI source ) {
            super( owner, source );
        }

        @Override
        public void run() {
            records.clear();
            try {
                HarvestRepository repository = HarvestRepository.getInstance();
                int index = 0;
                XMLFragment metaData = null;
                Date harvestingTimestamp = repository.getNextHarvestingTimestamp( source );
                do { 
                    metaData = getNextMetadataRecord( source, index, "dataset" );
                    
                    if ( metaData != null ) {
                        Record record = createOrGetRecord( source, metaData );                        
                        records.put( record.getFileIdentifier(), record );
                        String trans = null;
                        try {
                            HarvestOperation ho = getHarvestOperation( record, metaData );
                            if ( ho == HarvestOperation.insert ) {                        
                                trans = createInsertRequest( metaData );
                            } else if ( ho == HarvestOperation.update ) {              
                                trans = createUpdateRequest( getID( metaData ),
                                                             getIdentifierXPath( metaData ),
                                                             metaData );
                            }
                            if ( ho != HarvestOperation.nothing ) {                   
                                performTransaction( trans );
                                repository.storeRecord( record );
                            } else {
                                LOG.logInfo( "nothing to Harvest" );
                            }
                        } catch ( Exception e ) {
                            LOG.logError( Messages.format("CatalogueHarvester.exception3", index, 
                                                          source), e ); 
                            try {
                                e.printStackTrace();
                                owner.informResponseHandlers( source, e );
                            } catch ( Exception ee ) {
                                ee.printStackTrace();
                            }
                            records.remove( record.getFileIdentifier() );
                        }
                    }
                    index++;
                    if ( index % 1000 == 0 ) {
                        System.gc();
                    }

                } while ( metaData != null );

                // delete all records from the target catalogue and the
                // from harvest cache
                deleteRecordsNoHostedAnymore( source );

                // update timestamps just if transaction has been performed
                // successfully
                writeLastHarvestingTimestamp( source, harvestingTimestamp );
                writeNextHarvestingTimestamp( source, harvestingTimestamp );
                informResponseHandlers( source );
                if ( repository.getHarvestInterval( source ) <= 0 ) {
                    repository.dropRequest( source );
                }
            } catch ( Exception e ) {
                LOG.logError( Messages.format("CatalogueHarvester.exception4", source ), e ); 
                try {
                    e.printStackTrace();
                    owner.informResponseHandlers( source, e );
                } catch ( Exception ee ) {
                    ee.printStackTrace();
                }
            } finally {
                inProgress.remove( source );
            }            
            
        }

        /**
         * returns the XPath the metadata records identifier
         * 
         * @param metaData
         * @return
         */
        private String getIdentifierXPath( XMLFragment metaData ) {
            // default is iso 19115
            String xpath = "iso19115:fileIdentifier/smXML:CharacterString"; 
            if ( metaData != null ) {
                String nspace = metaData.getRootElement().getNamespaceURI();
                nspace = StringTools.replace( nspace, "http://", "", true );
                xpath = Messages.getString( "Identifier_" + nspace );
            }
            return xpath;
        }

        /**
         * returns the XPath the metadata records dateStamp
         * 
         * @param metaData
         * @return
         */
        private String getDateStampXPath( XMLFragment metaData ) {
            String xpath = null;
            if ( metaData != null ) {
                String nspace = metaData.getRootElement().getNamespaceURI();
                nspace = StringTools.replace( nspace, "http://", "", true );
                xpath = Messages.getString( "dateStamp_" + nspace );
            }
            return xpath;
        }

        /**
         * returns the identifier of a metadata record to enable its update and deletion
         * 
         * @param metaData
         * @return
         * @throws XMLParsingException
         */
        private String getID( XMLFragment metaData )
                                throws XMLParsingException {
            String xpath = getIdentifierXPath( metaData );
            String fileIdentifier = XMLTools.getRequiredNodeAsString( metaData.getRootElement(),
                                                                      xpath, nsc );
            return fileIdentifier;
        }

        @Override
        protected StringBuffer createConstraint( String identifier, String xPath ) {
            StringBuffer sb = new StringBuffer( 1000 );
            String s = StringTools.concat( 200, "<csw:Constraint><ogc:Filter>", 
                                           "<ogc:PropertyIsEqualTo>", "<ogc:PropertyName>", xPath,  
                                           "</ogc:PropertyName>", "<ogc:Literal>", identifier,  
                                           "</ogc:Literal>", "</ogc:PropertyIsEqualTo>",  
                                           "</ogc:Filter></csw:Constraint>" ); 
            sb.append( s );
            return sb;
        }

        /**
         * validates if a record stored in the harvester cache if not provided by the harvested
         * catalogue any more; if so the record will be removed from the cache and the harvesting
         * catalogue.
         * 
         * @throws IOException
         * @throws SQLException
         * @throws DBPoolException
         * @throws XMLParsingException
         * @throws SAXException
         * @throws OGCWebServiceException
         * 
         */
        private void deleteRecordsNoHostedAnymore( URI source )
                                throws DBPoolException, SQLException, IOException,
                                OGCWebServiceException, SAXException {
            HarvestRepository repository = HarvestRepository.getInstance();
            List<String> cache = repository.getAllRecords( source );
System.out.println("----- cache ------");            
System.out.println(cache);            
System.out.println("----- records ------");            
System.out.println(records);
            int id = repository.getSourceID( source );
            for ( int i = 0; i < cache.size(); i++ ) {
                String fid = cache.get( i );
                Record record = records.remove( fid );
                if ( record == null ) {
                    repository.dropRecord( repository.new Record( id, null, fid, source ) );
                    String trans = createDeleteRequest( fid, null );
                    performTransaction( trans );
                }
            }
        }

        /**
         * the method tries to read a record from the harvest repository. If the is not already
         * stored in the repository a new record will be created
         * 
         * @param metaData
         * @return
         * @throws XMLParsingException
         * @throws IOException
         * @throws SQLException
         * @throws DBPoolException
         */
        private Record createOrGetRecord( URI source, XMLFragment metaData )
                                throws XMLParsingException, IOException, DBPoolException,
                                SQLException {

            String xpath = getIdentifierXPath( metaData );
            String fileIdentifier = XMLTools.getRequiredNodeAsString( metaData.getRootElement(),
                                                                      xpath, nsc );

            HarvestRepository repository = HarvestRepository.getInstance();
            Record record = repository.getRecordByID( source, fileIdentifier );
            if ( record == null ) {
                xpath = getDateStampXPath( metaData );
                String s = XMLTools.getRequiredNodeAsString( metaData.getRootElement(), xpath, nsc );
                Date date = TimeTools.createCalendar( s ).getTime();
                record = repository.new Record( -1, date, fileIdentifier, source );
            }

            return record;
        }

        /**
         * determines what operation shall be performed on a metadata record read from a remote
         * catalogue
         * 
         * @param metaData
         * @return
         * @throws IOException
         * @throws SQLException
         * @throws DBPoolException
         * @throws XMLParsingException
         */
        private HarvestOperation getHarvestOperation( Record record, XMLFragment metaData )
                                throws XMLParsingException {

            HarvestOperation ho = HarvestOperation.nothing;
            if ( record.getSourceId() < 0 ) {
                ho = HarvestOperation.insert;
            } else {
                String xpath = getDateStampXPath( metaData );
                String s = XMLTools.getRequiredNodeAsString( metaData.getRootElement(), xpath, nsc );
                Date date = TimeTools.createCalendar( s ).getTime();          
                if ( !date.equals( record.getDatestamp() ) ) {
                    ho = HarvestOperation.update;
                }
            }
            return ho;
        }

        /**
         * read
         * 
         * @param source
         * @return
         * @throws IOException
         * @throws HttpException
         * @throws SAXException
         * @throws XMLException
         * @throws XMLParsingException
         */
        private XMLFragment getNextMetadataRecord( URI source, int index, String type )
                                throws IOException, XMLException, SAXException, XMLParsingException {
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw\" " ); 
            sb.append( "service=\"CSW\" version=\"2.0.0\" resultType=\"RESULTS\" " ); 
            sb.append( "outputFormat=\"text/xml\" outputSchema=\"csw:profile\" " ); 
            sb.append( "startPosition='" ).append( index ).append( "' " );  
            sb.append( "maxRecords='1'><csw:Query typeNames='" ); 
            sb.append( type ).append( "'>" ); 
            sb.append( "<csw:ElementSetName>full</csw:ElementSetName>" ); 
            sb.append( "</csw:Query></csw:GetRecords>" ); 

            StringRequestEntity re = new StringRequestEntity( sb.toString() );
            PostMethod post = new PostMethod( source.toASCIIString() );
            post.setRequestEntity( re );
            HttpClient client = new HttpClient();
            client.executeMethod( post );
            InputStream is = post.getResponseBodyAsStream();
            XMLFragment xml = new XMLFragment();
            xml.load( is, source.toURL().toExternalForm() );

            String xpath = "csw:SearchResults/child::*[1]"; 
            Node node = XMLTools.getNode( xml.getRootElement(), xpath, nsc );
            if ( node != null ) {
                xml.setRootElement( (Element) node );
            } else {
                xml = null;
            }

            return xml;
        }
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueHarvester.java,v $
Revision 1.18  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
