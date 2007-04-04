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
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * 
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public class CSWProfileHarvester extends AbstractHarvester {

    private static final ILogger LOG = LoggerFactory.getLogger( CSWProfileHarvester.class );

    private static CSWProfileHarvester ch = null;

    /**
     * singelton
     * 
     * @return
     */
    public static CSWProfileHarvester getInstance() {
        if ( ch == null ) {
            ch = new CSWProfileHarvester();
        }
        return ch;
    }

    @Override
    public void run() {
        LOG.logDebug( "starting harvest iteration for CSWProfileHarvester." );
        try {
            HarvestRepository repository = HarvestRepository.getInstance();

            List<URI> sources = repository.getSources();
            for ( Iterator iter = sources.iterator(); iter.hasNext(); ) {
                URI source = (URI) iter.next();
                try {
                    // determine if source shall be harvested
                    if ( shallHarvest( source, ResourceType.csw_profile ) ) {
                        HarvestProcessor processor = new HarvestProcessor( this, source );
                        processor.start();
                    }
                } catch ( Exception e ) {
                    LOG.logError( "Exception harvesting service: " + source, e );
                    informResponseHandlers( source, e );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "generell Exception harvesting services", e );
        }

    }

    /**
     * inner class for processing asynchronous harvesting of a csw:profile metadata document
     * 
     * @version $Revision: 1.11 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version 1.0. $Revision: 1.11 $, $Date: 2006/07/12 14:46:17 $
     * 
     * @since 2.0
     */
    protected class HarvestProcessor extends AbstractHarvestProcessor {

        HarvestProcessor( AbstractHarvester owner, URI source ) {
            super( owner, source );
        }

        @Override
        public void run() {
            try {
                HarvestRepository repository = HarvestRepository.getInstance();
                XMLFragment metaData = accessMetadata( source );
                Date harvestingTimestamp = repository.getNextHarvestingTimestamp( source );
                String trans = null;
                if ( repository.getLastHarvestingTimestamp( source ) == null ) {
                    trans = createInsertRequest( metaData );
                } else {
                    trans = createUpdateRequest( getID( metaData ), getIdentifierXPath( metaData ),
                                                 metaData );
                }
                performTransaction( trans );

                long ts = repository.getHarvestInterval( source );
                if ( ts <= 0 ) {
                    // if the harvest interval is less or equal to 0 the source
                    // shall just be harvested for one time and it will be
                    // removed from harvest cache db
                    informResponseHandlers( source );
                    repository.dropRequest( source );
                } else {
                    // update timestamps just if transaction has been performed
                    // successfully
                    writeLastHarvestingTimestamp( source, harvestingTimestamp );
                    writeNextHarvestingTimestamp( source, harvestingTimestamp );
                    informResponseHandlers( source );
                }

            } catch ( Exception e ) {
                e.printStackTrace();
                LOG.logError( "could not perform harvest operation for source: " + source, e );
                try {
                    owner.informResponseHandlers( source, e );
                } catch ( Exception ee ) {
                    ee.printStackTrace();
                }
            }
        }

        /**
         * returns the XPath the metadata records identifier
         * 
         * @param metaData
         * @return
         */
        private String getIdentifierXPath( XMLFragment metaData ) {
            String xpath = null;
            String nsURI = metaData.getRootElement().getNamespaceURI(); 
            if ( CommonNamespaces.ISO19115NS.toASCIIString().equals( nsURI ) ) {
                xpath = "iso19115:fileIdentifier/smXML:CharacterString";
            } else if ( CommonNamespaces.ISO19115NS.toASCIIString().equals( nsURI ) ) {
                xpath = "iso19115:fileIdentifier/smXML:CharacterString";
            } else {
                xpath = "Identifier";
            }
            return xpath;
        }

        /**
         * returns the identifier of a metadata record to enable its update
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

        /**
         * 
         */
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
         * 
         * @param source
         * @return
         * @throws SAXException
         * @throws IOException
         * @throws MalformedURLException
         */
        private XMLFragment accessMetadata( URI source )
                                throws MalformedURLException, IOException, SAXException {

            XMLFragment xml = new XMLFragment();
            xml.load( source.toURL() );
            return xml;
        }

    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWProfileHarvester.java,v $
Revision 1.11  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
