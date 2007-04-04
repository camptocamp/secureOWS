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
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;
import org.xml.sax.SAXException;

/**
 * <p>
 * Concrete implementation of
 * 
 * @see org.deegree.ogcwebservices.csw.manager.AbstractHarvester for harvesting service metadata
 *      from OGC web services. To enable this capabilities documents of the OWS will be accessed and
 *      transformed into a valid format that will be understood by the underlying catalogue. To
 *      enable a lot of flexibility a XSLT read from resource bundle (harvestservice.xsl) script
 *      will be used to perform the required transformation.
 *      </p>
 *      <p>
 *      A valid harvest SOURCE for a service must be a complete GetCapabilities request; the
 *      RESOURCETYPE must be 'service'. Example:
 *      </p>
 *      <p>
 *      ...?request=Harvest&version=2.0.0&source=[http://MyServer:8080/deegree?
 *      service=WFS&version=1.1.0&request=GetCapabilities]&resourceType=service&
 *      resourceFormat=text/xml&responseHandler=mailto:info@lat-lon.de&harvestInterval=P2W
 *      </p>
 *      <p>
 *      value in brackets [..] must be URL encoded and send without brackets!
 *      </p>
 *      <p>
 *      This is not absolutly compliant to OGc CSW 2.0.0 specification but Harvest definition as
 *      available from the spec is to limited because it just targets single metadata documents.
 *      </p>
 * 
 * @version $Revision: 1.13 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.13 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public class ServiceHarvester extends AbstractHarvester {

    private static final ILogger LOG = LoggerFactory.getLogger( ServiceHarvester.class );

    private static final URL xslt = ServiceHarvester.class.getResource( "harvestservice.xsl" );

    private static ServiceHarvester sh = null;

    /**
     * singelton
     * 
     * @return
     */
    public static ServiceHarvester getInstance() {
        if ( sh == null ) {
            sh = new ServiceHarvester();
        }
        return sh;
    }

    @Override
    public void run() {
        LOG.logDebug( "starting harvest iteration for ServiceHarvester." );
        try {
            HarvestRepository repository = HarvestRepository.getInstance();

            List<URI> sources = repository.getSources();
            for ( Iterator iter = sources.iterator(); iter.hasNext(); ) {
                URI source = (URI) iter.next();
                try {
                    // determine if source shall be harvested
                    if ( shallHarvest( source, ResourceType.service ) ) {
                        inProgress.add( source );
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
     * inner class for processing asynchronous harvesting of a service
     * 
     * @version $Revision: 1.13 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version 1.0. $Revision: 1.13 $, $Date: 2006/07/12 14:46:17 $
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
                XMLFragment capabilities = accessSourceCapabilities( source );
                Date harvestingTimestamp = repository.getNextHarvestingTimestamp( source );
                XMLFragment metaData = transformCapabilities( capabilities );
                String trans = null;
                if ( repository.getLastHarvestingTimestamp( source ) == null ) {
                    trans = createInsertRequest( metaData );
                } else {
                    trans = createUpdateRequest( getID( metaData ),
                                                 "smXML:fileIdentifier/smXML:CharacterString",
                                                 metaData );
                }
                performTransaction( trans );
                // update timestamps just if transaction has been performed
                // successfully
                writeLastHarvestingTimestamp( source, harvestingTimestamp );
                writeNextHarvestingTimestamp( source, harvestingTimestamp );
                informResponseHandlers( source );
            } catch ( Exception e ) {
                LOG.logError( "could not perform harvest operation for source: " + source, e );
                try {
                    owner.informResponseHandlers( source, e );
                } catch ( Exception ee ) {
                    ee.printStackTrace();
                }
            }
        }

        private String getID( XMLFragment metaData )
                                throws XMLParsingException {
            String xpath = "smXML:fileIdentifier/smXML:CharacterString";
            String fileIdentifier = XMLTools.getRequiredNodeAsString( metaData.getRootElement(),
                                                                      xpath, nsc );
            return fileIdentifier;
        }

        @Override
        protected StringBuffer createConstraint( String fileIdentifier, String xPath ) {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * transforms a OWS capabilities document into the desired target format
         * 
         * @param xml
         * @return
         * @throws IOException
         * @throws SAXException
         * @throws TransformerException
         */
        private XMLFragment transformCapabilities( XMLFragment xml )
                                throws IOException, SAXException, TransformerException {

            XSLTDocument xsltDoc = new XSLTDocument();
            xsltDoc.load( xslt );

            return xsltDoc.transform( xml );
        }

        /**
         * returns the capabilities of
         * 
         * @param source
         * @return
         * @throws IOException
         * @throws SAXException
         */
        private XMLFragment accessSourceCapabilities( URI source )
                                throws IOException, SAXException {

            URL url = source.toURL();
            XMLFragment xml = new XMLFragment();
            xml.load( url );
            return xml;
        }

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ServiceHarvester.java,v $
Revision 1.13  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
