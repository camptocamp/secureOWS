//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/DefaultRequestManager.java,v 1.32 2006/10/17 16:47:38 mays Exp $
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
package org.deegree.ogcwebservices.wmps;

import java.sql.Connection;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.mail.EMailMessage;
import org.deegree.framework.mail.MailHelper;
import org.deegree.framework.mail.MailMessage;
import org.deegree.framework.mail.SendMailException;
import org.deegree.framework.mail.UnknownMimeTypeException;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wmps.configuration.CacheDatabase;
import org.deegree.ogcwebservices.wmps.configuration.WMPSConfiguration;
import org.deegree.ogcwebservices.wmps.operation.PrintMap;
import org.deegree.ogcwebservices.wmps.operation.PrintMapResponse;
import org.deegree.ogcwebservices.wmps.operation.PrintMapResponseDocument;
import org.w3c.dom.Element;

/**
 * Default Handler to save the PrintMap requests to the 'HSQLDB' and send email after processing the
 * request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class DefaultRequestManager implements RequestManager {

    private static final ILogger LOG = LoggerFactory.getLogger( DefaultRequestManager.class );

    protected static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private WMPSConfiguration configuration;

    private final String MIMETYPE = "text/html";

    private PrintMap request;

    /**
     * Creates a new DefaultRequestManager instance.
     * 
     * @param configuration
     * @param request
     *            request to perform
     */
    public DefaultRequestManager( WMPSConfiguration configuration, PrintMap request ) {
        this.configuration = configuration;
        this.request = request;
    }

    /**
     * returns the configuration used by the handler
     * 
     * @return WMPSConfiguration
     */
    public WMPSConfiguration getConfiguration() {
        return this.configuration;

    }

    /**
     * returns the request used by the handler
     * 
     * @return PrintMap request
     */
    public PrintMap getRequest() {
        return this.request;

    }

    /**
     * Opens a connection to a database based on the properties file in the resources directory and
     * saves the current PrintMap request in the table for later access.
     * 
     * @throws OGCWebServiceException
     */
    public synchronized void saveRequestToDB()
                            throws OGCWebServiceException {

        try {
            CacheDatabase cacheDatabase = this.configuration.getDeegreeParams().getCacheDatabase();
            WMPSDatabase dbConnection = new WMPSDatabase( cacheDatabase );
            Connection connection = dbConnection.acquireConnection();
            dbConnection.insertData( connection, this.request );
            dbConnection.releaseConnection( connection );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "Error creating a 'WMPSDatabase' object and setting "
                                              + "up a connection. " + e.getMessage() );
        }

    }

    /**
     * Send an intial response back to the user, depending on whether the request has been
     * successfull saved in the DB or not. The email address from the request is used to send the
     * reply.
     * 
     * @param success
     *            to denote whether the operation was a success or not
     * @return PrintMapResponseDocument
     * @throws OGCWebServiceException
     */
    public PrintMapResponseDocument createInitialResponse( String message )
                            throws OGCWebServiceException {

        // before the print operation is finished stage.
        PrintMapResponse initialResponse = new PrintMapResponse( this.request.getId(),
                                                                 this.request.getEmailAddress(),
                                                                 this.request.getTimestamp(),
                                                                 this.request.getTimestamp(),
                                                                 message, "" );

        PrintMapResponseDocument document;
        try {
            document = XMLFactory.export( initialResponse );

        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = "Error creating a 'PrintMapResponseDocument' object."
                         + " Please check the 'WMPSInitialResponseTemplate' for XML errors. "
                         + e.getMessage();
            throw new OGCWebServiceException( msg );
        }

        return document;
    }

    /**
     * Send an Email to the address provided in the PrintMap request.
     * 
     * @param response
     * @throws OGCWebServiceException
     */
    public void sendEmail( PrintMapResponseDocument response )
                            throws OGCWebServiceException {

        XMLFragment doc = new XMLFragment( response.getRootElement() );
        Element root = doc.getRootElement();
        String id = root.getAttribute( "id" );
        String toEmailAddress = null;
        String timestamp = null;
        String message = null;
        // String processingTime = null;
        try {
            String xPath = "deegreewmps:EmailAddress";
            toEmailAddress = XMLTools.getRequiredNodeAsString( root, xPath, nsContext );
            if ( !isValidEmailAddress( toEmailAddress ) ) {
                throw new PrintMapServiceException( "Incorrect email address '" + toEmailAddress
                                                    + "' in the PrintMap request. Please enter a "
                                                    + "valid email address before trying again. " );
            }
            timestamp = XMLTools.getRequiredNodeAsString( root, "deegreewmps:Timestamp", nsContext );
            message = XMLTools.getRequiredNodeAsString( root, "deegreewmps:Message", nsContext );
            xPath = "deegreewmps:ExpectedProcessingTime";
            // TODO
            // processingTime = XMLTools.getNodeAsString( root, xPath, nsContext, null );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "Error parsing the initial response document to "
                                              + "retrieve the email address and "
                                              + "'PrintMap' request id. " + e.getMessage() );
        }
        String fromEmailAddress = this.configuration.getDeegreeParams().getPrintMapParam().getAdminMailAddress();

        if ( !isValidEmailAddress( fromEmailAddress ) ) {
            throw new PrintMapServiceException( "Incorrect email address '" + fromEmailAddress
                                                + "' in the 'WMPSPrintMap.properties' "
                                                + "file. Please enter a valid email address "
                                                + "before trying again. "  );
        }
        String subject = "PrintMap Notification: " + id + ' ' + timestamp;

        MailMessage email;
        try {
            email = new EMailMessage( fromEmailAddress, toEmailAddress, subject, message,
                                      this.MIMETYPE );
        } catch ( UnknownMimeTypeException e ) {
            throw new OGCWebServiceException( "Unknown mime type set." + e );
        }
        String mailHost = this.configuration.getDeegreeParams().getPrintMapParam().getMailHost();
        try {

            MailHelper.createAndSendMail( email, mailHost );
        } catch ( SendMailException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "Error sending an email notification on '"
                                              + toEmailAddress
                                              + "' for the server configuration '" + mailHost
                                              + "'. " + e.getMessage() );
        }

    }

    /**
     * Check if the email address is valid and has a valid name and domain string.
     * 
     * @param aEmailAddress
     * @return boolean
     */
    private boolean hasNameAndDomain( String aEmailAddress ) {

        String[] tokens = aEmailAddress.split( "@" );
        return tokens.length == 2 && ( ( tokens[0] != null ) || ( tokens[0] != "" ) )
               && ( ( tokens[1] != null ) || ( tokens[1] != "" ) );
    }

    /**
     * Check email add validity.
     * 
     * @param aEmailAddress
     * @return boolean
     */
    private boolean isValidEmailAddress( String aEmailAddress ) {

        String status = "VALID";
        if ( aEmailAddress == null )
            status = "NOTVALID";

        try {
            new InternetAddress( aEmailAddress );
            if ( !hasNameAndDomain( aEmailAddress ) ) {
                status = "NOTVALID";
            }
        } catch ( AddressException ex ) {
            status = "NOTVALID " + ex.getMessage();
        }

        return status.startsWith( "VALID" );
    }

    /**
     * Export the PrintMap service final response to a PrintMapResponseDocument.
     * 
     * @param success
     * @param message
     * @param exception
     * @return PrintMapResponseDocument
     * @throws OGCWebServiceException
     */
    public PrintMapResponseDocument createFinalResponse( String message, String exception )
                            throws OGCWebServiceException {

        PrintMapResponse finalResponse = new PrintMapResponse( this.request.getId(),
                                                               this.request.getEmailAddress(),
                                                               this.request.getTimestamp(),
                                                               this.request.getTimestamp(),
                                                               message, exception );

        PrintMapResponseDocument document;
        try {
            document = XMLFactory.export( finalResponse );

        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            String msg = "Error creating a 'PrintMapResponseDocument' object."
                         + " Please check the 'WMPSInitialResponseTemplate' for XML errors. "
                         + e.getMessage();
            throw new OGCWebServiceException( msg );
        }

        return document;
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: DefaultRequestManager.java,v $
 * Changes to this class. What the people have been up to: Revision 1.32  2006/10/17 16:47:38  mays
 * Changes to this class. What the people have been up to: only throw exception if email address is _not_ valid
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.31  2006/10/13 14:22:40  poth
 * Changes to this class. What the people have been up to: implementation simplified
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.30  2006/10/02 06:30:35  poth
 * Changes to this class. What the people have been up to: bug fixes
 * Changes to this class. What the people have been up to:
 * Revision 1.29  2006/09/15 13:39:17  deshmukh
 * mail link modified to support security servlet feature.
 * Changes to this class. What the people have been up to:
 * Revision 1.28 2006/09/13 09:32:20
 * deshmukh added new exception message
 * Changes to this class. What the people
 * have been up to: Revision 1.27 2006/09/13 07:37:58 deshmukh Changes to this class. What the
 * people have been up to: removed excess debug statements. Changes to this class. What the people
 * have been up to: Revision 1.26 2006/09/04
 * 11:32:25 deshmukh comments added Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.25 2006/08/23 10:21:12 deshmukh Changes to this class. What the people have
 * been up to: detailed response messages in case of exception added. Changes to this class. What
 * the people have been up to: Changes to this this class. What the people have been up to: WMPS has
 * been modified to support the new configuration changes and the excess code not needed has been
 * replaced. Changes to this class. What the
 * people have been up to: Revision 1.23 2006/08/02 06:51:29 deshmukh Changes to this class. What
 * the people have been up to: modification of javadoc Changes to this class. What the people have
 * been up to: Revision 1.22 2006/08/01
 * 14:20:10 deshmukh The wmps configuration
 * has been modified and extended. Also fixed the javadoc. Changes to this class. What the people
 * have been up to: Revision 1.21 2006/08/01
 * 13:41:48 deshmukh The wmps configuration
 * has been modified and extended. Also fixed the javadoc. Changes to this class. What the people
 * have been up to: Revision 1.20 2006/07/31
 * 11:21:06 deshmukh wmps implemention...
 * Revision 1.19 2006/07/12 14:46:16 poth
 * comment footer added
 * 
 **************************************************************************************************/
