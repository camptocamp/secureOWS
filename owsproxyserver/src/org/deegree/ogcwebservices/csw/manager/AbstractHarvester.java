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
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.mail.EMailMessage;
import org.deegree.framework.mail.MailHelper;
import org.deegree.framework.mail.MailMessage;
import org.deegree.framework.mail.SendMailException;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.io.DBPoolException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.CSWFactory;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;
import org.xml.sax.SAXException;

/**
 * Abstract super class of all CS-W harvesters. For each kind of source a specialized harvester
 * shall be implemented. A concrete implementation of AbstractHarvester will be called within a
 * timer loop.
 * 
 * 
 * @version $Revision: 1.15 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.15 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public abstract class AbstractHarvester extends TimerTask {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractHarvester.class );

    private boolean stopped = true;

    private Timer timer = null;

    protected List<URI> inProgress = new Vector<URI>();

    protected static NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
    static {
        try {
            nsc.addNamespace( "smXML", new URI( "http://metadata.dgiwg.org/smXML" ) );
            nsc.addNamespace( "iso19119", new URI( "http://schemas.opengis.net/iso19119" ) );
            nsc.addNamespace( "iso19115", new URI( "http://schemas.opengis.net/iso19115full" ) );
        } catch ( URISyntaxException e ) {
            e.printStackTrace();
        }
    }

    /**
     * adds a request to the harvesting process
     * 
     * @param request
     * @throws SQLException
     * @throws DBPoolException
     */
    public void addRequest( Harvest request )
                            throws IOException, DBPoolException, SQLException {
        HarvestRepository.getInstance().storeRequest( request );
    }

    /**
     * returns true if the harvesting process is running
     * 
     * @return
     */
    public boolean isRunning() {
        return !stopped;
    }

    /**
     * removes a request from the harvesting request.
     * <p>
     * <b> !! At the moment the OGC CSW does not know a mechanism/request to stop a cyclic
     * harvesting job, so this method can not be called with a standard OGC OWS request !!</b>
     * </p>
     * 
     * @param request
     * @throws SQLException
     * @throws DBPoolException
     */
    public void removeRequest( Harvest request )
                            throws IOException, DBPoolException, SQLException {
        HarvestRepository.getInstance().dropRequest( request.getSource() );
    }

    /**
     * starts the harvesting process
     * 
     */
    public void startHarvesting() {        
        timer = new Timer();
        timer.schedule( this, 0, 10000 );
        stopped = false;
        LOG.logInfo( "harvesting has been started" );
    }

    /**
     * stops the harvesting process
     * 
     */
    public void stopHarvesting() {
        timer.purge();
        timer.cancel();
        stopped = true;
        LOG.logInfo( "harvesting has been stopped" );
    }

    /**
     * informs all response handlers assigend to a source about successful harvesting of the source
     * 
     * @param source
     * @throws URISyntaxException
     * @throws SQLException
     * @throws DBPoolException
     * @throws MalformedURLException
     */
    protected void informResponseHandlers( URI source )
                            throws SendMailException, IOException, DBPoolException, SQLException,
                            URISyntaxException {

        List<HarvestRepository.ResponseHandler> list = HarvestRepository.getInstance().getResponseHandlers(
                                                                                                            source );

        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            HarvestRepository.ResponseHandler handler = (HarvestRepository.ResponseHandler) iter.next();
            String message = "source: " + source + " has been harvested successfully!";
            sendMessage( handler, message );

        }

    }

    /**
     * returns true if the passed source shall be harvested. this is true if a source has not been
     * harvested before or the next harvesting timestamp has been reached and the source is of type
     * 
     * @see HarvestRepository.ResourceType service
     * 
     * @param source
     * @return
     * @throws DBPoolException
     * @throws SQLException
     */
    protected boolean shallHarvest( URI source, ResourceType targetType )
                            throws IOException, DBPoolException, SQLException {

        if ( inProgress.contains( source ) ) {
            return false;
        }

        HarvestRepository repository = HarvestRepository.getInstance();

        ResourceType st = repository.getSourceType( source );

        if ( !st.equals( targetType ) ) {
            return false;
        }

        Date lastHarvesting = repository.getLastHarvestingTimestamp( source );
        Date nextHarvesting = repository.getNextHarvestingTimestamp( source );

        long tmp = System.currentTimeMillis() - nextHarvesting.getTime();
        return lastHarvesting == null || tmp >= 0;
    }

    /**
     * informs all response handlers assigend to a source about an exception that occurs when
     * harvesting a source
     * 
     * @param source
     * @param e
     * @throws URISyntaxException
     * @throws SQLException
     * @throws DBPoolException
     */
    protected void informResponseHandlers( URI source, Exception e )
                            throws SendMailException, IOException, DBPoolException, SQLException,
                            URISyntaxException {

        List<HarvestRepository.ResponseHandler> list = HarvestRepository.getInstance().getResponseHandlers(
                                                                                                            source );

        for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
            HarvestRepository.ResponseHandler handler = (HarvestRepository.ResponseHandler) iter.next();
            String message = StringTools.concat( 500, "exception occures harvesting source: ",
                                                 source, "; exception: ", e.getMessage() );
            sendMessage( handler, message );

        }

    }

    /**
     * 
     * @param handler
     * @param message
     * @throws SendMailException
     * @throws MalformedURLException
     * @throws IOException
     * @throws HttpException
     */
    private void sendMessage( HarvestRepository.ResponseHandler handler, String message )
                            throws SendMailException {
        if ( handler.isMailAddress() ) {
            String s = handler.getUri().toASCIIString();
            int p = s.indexOf( ":" );
            s = s.substring( p + 1, s.length() );
            LOG.logDebug( StringTools.concat( 200, "informing response handler ", s, "via mail" ) );
            MailMessage mm = new EMailMessage( "info@lat-lon.de", s, "CS-W harvesting", message );
            MailHelper.createAndSendMail( mm, System.getProperty( "mailHost" ) );
        } else {
            LOG.logDebug( StringTools.concat( 200, "informing response handler ", handler.getUri(),
                                              "via HTTP GET" ) );
            HttpClient client = new HttpClient();
            System.out.println( message );

            try {
                GetMethod get = new GetMethod( handler.getUri().toURL().toExternalForm() + 
                                               "?message=" + message );
                client.executeMethod( get );
            } catch ( Exception e ) {
                LOG.logInfo( "could not post message: '" + message + "' to: " + handler.getUri() +
                             "; reason: " + e.getMessage() );
            }

        }
    }

    /**
     * abstract super class for all harvest processores
     * 
     * 
     * @version $Revision: 1.15 $
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version 1.0. $Revision: 1.15 $, $Date: 2006/07/12 14:46:17 $
     * 
     * @since 2.0
     */
    protected abstract class AbstractHarvestProcessor extends Thread {

        protected URI source = null;

        protected AbstractHarvester owner = null;

        protected AbstractHarvestProcessor( AbstractHarvester owner, URI source ) {
            this.owner = owner;
            this.source = source;
        }

        /**
         * performs a transaction for inserting or updating a service meta data record in the
         * catalogue a harvester instance belongs too
         * 
         * @param trans
         * @throws SAXException
         * @throws IOException
         * @throws XMLParsingException
         * @throws OGCWebServiceException
         */
        protected void performTransaction( String trans )
                                throws SAXException, IOException, OGCWebServiceException {

            StringReader sr = new StringReader( trans );
            XMLFragment xml = new XMLFragment();
            xml.load( sr, XMLFragment.DEFAULT_URL );
            Transaction transaction = Transaction.create( "id", xml.getRootElement() );
            CSWFactory.getService().doService( transaction );

        }

        /**
         * creates a CSW Transaction including an Update operation for the passed meta data.
         * 
         * @param metaData
         * @return
         * @throws XMLParsingException
         */
        protected String createUpdateRequest( String identifier, String xpath, XMLFragment metaData ) {

            LOG.logDebug( "creating csw update request" );

            StringBuffer sb = new StringBuffer( 50000 );
            sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            sb.append( "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw\" " );
            sb.append( "xmlns:ogc=\"http://www.opengis.net/ogc\" >" );
            sb.append( "<csw:Update>" );

            String s = metaData.getAsString();
            int p = s.lastIndexOf( "?>" );
            if ( p > -1 ) {
                s = s.substring( p + 2, s.length() );
            }
            sb.append( s );

            sb.append( createConstraint( identifier, xpath ) );

            sb.append( "</csw:Update></csw:Transaction>" );

            return sb.toString();
        }

        /**
         * creates a transaction request including a delete operation to remove the metadata record
         * with the passed fileIdentifier from the catalogue
         * 
         * @param identifier
         * @return
         */
        protected String createDeleteRequest( String identifier, String xpath ) {
            LOG.logDebug( "creating csw delete request" );

            StringBuffer sb = new StringBuffer( 2000 );
            sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            sb.append( "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw\" " );
            sb.append( "xmlns:ogc=\"http://www.opengis.net/ogc\" >" );
            sb.append( "<csw:Delete>" );

            sb.append( createConstraint( identifier, xpath ) );

            sb.append( "</csw:Delete></csw:Transaction>" );

            return sb.toString();
        }

        /**
         * a constraint for delete und update operation depends on concrete metadata format. An
         * implementing class must consider this.
         * 
         * @param fileIdentifier
         *            value to be compared
         * @param xpath
         *            comparable property
         * @return
         */
        protected abstract StringBuffer createConstraint( String fileIdentifier, String xpath );

        /**
         * creates a CSW Transaction including an Update operation for the passed meta data
         * 
         * @param metaData
         * @return
         */
        protected String createInsertRequest( XMLFragment metaData ) {

            LOG.logDebug( "creating csw insert request" );

            StringBuffer sb = new StringBuffer( 50000 );
            sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            sb.append( "<csw:Transaction xmlns:csw=\"http://www.opengis.net/cat/csw\">" );
            sb.append( "<csw:Insert>" );

            String s = metaData.getAsString();
            int p = s.lastIndexOf( "?>" );
            if ( p > -1 ) {
                s = s.substring( p + 2, s.length() );
            }
            sb.append( s );
            sb.append( "</csw:Insert></csw:Transaction>" );

            return sb.toString();
        }

        /**
         * actualizes the source in the repository with timestamp of last harvesting
         * 
         * @param source
         * @param date
         * @throws SQLException
         * @throws DBPoolException
         */
        protected void writeLastHarvestingTimestamp( URI source, Date date )
                                throws IOException, DBPoolException, SQLException {
            HarvestRepository repository = HarvestRepository.getInstance();
            repository.setLastHarvestingTimestamp( source, date );
        }

        /**
         * actualizes the source in the repository with timestamp when next harvesting shall be
         * performed
         * 
         * @param source
         * @param date
         * @throws SQLException
         * @throws DBPoolException
         */
        protected void writeNextHarvestingTimestamp( URI source, Date date )
                                throws IOException, DBPoolException, SQLException {
            HarvestRepository repository = HarvestRepository.getInstance();
            long ts = repository.getHarvestInterval( source );
            date = new Date( ts + date.getTime() );
            repository.setNextHarvestingTimestamp( source, date );
        }

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractHarvester.java,v $
Revision 1.15  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
