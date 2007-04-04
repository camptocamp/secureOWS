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
package org.deegree.ogcwebservices.csw.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.mail.EMailMessage;
import org.deegree.framework.mail.MailHelper;
import org.deegree.framework.mail.MailMessage;
import org.deegree.framework.mail.SendMailException;
import org.deegree.framework.mail.UnknownMimeTypeException;
import org.deegree.framework.trigger.Trigger;
import org.deegree.framework.trigger.TriggerException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.i18n.Messages;
import org.deegree.io.DBConnectionPool;
import org.xml.sax.SAXException;

/**
 * Trigger implementation for synchronizing several CSW instances for
 * incomming Transaction requests
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/11/07 11:04:45 $
 *
 * @since 2.0
 */
public class CSWSychronizationTrigger implements Trigger {
    
    private static final ILogger LOG = LoggerFactory.getLogger( CSWSychronizationTrigger.class );
    
    private String name;
    private URL[] cswAddr;
    private String driver; 
    private String url;
    private String user; 
    private String password;
    private String smtpServer; 
    private String sender;
    private String receiver;
    private int maxRepeat = 0;
    
    /**
     * 
     * @param driver
     * @param url
     * @param user
     * @param password
     * @param smtpServer
     * @param sender
     * @param addresses addresses of all CSW instances to be synchronized
     */
    public CSWSychronizationTrigger(String driver, String url, String user, String password,
                                    String smtpServer, String sender, String receiver, 
                                    Integer maxRepeat, URL address) {
        this.cswAddr = new URL[] { address };
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.smtpServer = smtpServer;
        this.sender = sender;
        this.receiver = receiver;
        this.maxRepeat = maxRepeat;
    }

    /**
     * @param caller
     * @param values
     */
    public Object[] doTrigger( Object caller, Object... values ) {
        
        if ( !(values[0] instanceof TransactionResult) ) {
            return values;
        }

        TransactionResult result = (TransactionResult)values[0];
        Transaction transaction = (Transaction)result.getRequest();
        
        TransactionDocument tDoc = null;
        try {
            tDoc = XMLFactory.export( transaction );
        } catch ( Exception e ) {
            // should not happen because request has been parsed and
            // performed before caling this method
            LOG.logError( e.getMessage(), e );
            throw new TriggerException( e );
        }
                        
        List<URL> errorAddr = new ArrayList<URL>();
        String req = tDoc.getAsString();
        for ( int i = 0; i < cswAddr.length; i++ ) {
            try {            
                String excep = performRequest( req, cswAddr[i] );
                if ( "Exception".equals( excep ) ) {
                    errorAddr.add( cswAddr[i] );
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );                
                errorAddr.add( cswAddr[i] );
            }
        }
        
        try {
            if ( errorAddr.size() > 0 ) {
                handleErrors( errorAddr, tDoc.getAsString() );
            }
        } catch ( Exception e ) {
            // exception will not be forwarded because it does not affect
            // performance of request by the triggering CSW
            LOG.logError( e.getMessage(), e );
        }
        
        return values;
    }

    /**
     * sends a request to the passed url  
     * @param req
     * @param url
     * @return
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     */
    private String performRequest( String req, URL url )
                            throws IOException, HttpException, SAXException {
        StringRequestEntity re = new StringRequestEntity( req );        
        PostMethod post = new PostMethod( url.toExternalForm() );
        post.setRequestEntity( re );
        HttpClient client = new HttpClient();
        client.executeMethod( post );
        InputStream is = post.getResponseBodyAsStream();
        XMLFragment xml = new XMLFragment();
        xml.load( is, url.toExternalForm() );
        String excep = xml.getRootElement().getLocalName();
        return excep;
    }

    /**
     * 
     * @param errorAddr
     * @param request
     */
    private void handleErrors( List<URL> errorAddr, String request ) {
        performFormerRequests();
        storeCurrentRequest( errorAddr, request );
        informAdmin( Messages.getMessage( "CSW_ERROR_SYNCHRONIZE_CSW", errorAddr, request ) );
    }

    
    private void performFormerRequests() {
        try {
            DBConnectionPool pool = DBConnectionPool.getInstance();
            Connection con = pool.acquireConnection( driver, url, user, password );
            Statement stmt = con.createStatement();
            List<Fail> failed = new ArrayList<Fail>( 100 );
            ResultSet rs = stmt.executeQuery( "SELECT * FROM FAILEDREQUESTS" );
            // first read all request that failed before from the database
            // to avoid performing transactions on the same table at the
            // same time
            while ( rs.next() ) {
                int id = rs.getInt( "ID" );
                String req = rs.getString( "REQUEST" );
                String cswAddress = rs.getString( "CSWADDRESS" );
                int repeat = rs.getInt( "REPEAT" );
                failed.add( new Fail( id, req, new URL( cswAddress), repeat ) );
            }
            rs.close();
            stmt.close();
            
            for ( int i = 0; i < failed.size(); i++ ) {
                try {                
                    String excep = performRequest( failed.get( i ).request, failed.get( i ).cswAddress );
                    stmt = con.createStatement();
                    if ( !"Exception".equals( excep ) ) {
                        // if request has been performed successfully delete entry 
                        // from the database
                        stmt.execute( "DELETE FROM FAILEDREQUESTS WHERE ID = " + failed.get( i ).id );                    
                    } else {
                        // otherwise increase  counter to indicate how often performing
                        // this request has failed
                        failed.get( i ).repeat++;
                        if ( failed.get( i ).repeat > maxRepeat ) {
                            informAdmin( Messages.getMessage( "CSW_ERROR_EXCEEDING_MAX_REPEAT",
                                                              failed.get( i ).cswAddress,
                                                              failed.get( i ).request, 
                                                              maxRepeat ) );
                            stmt.execute( "DELETE FROM FAILEDREQUESTS WHERE ID = " + failed.get( i ).id );
                        } else {
                            stmt.execute( "UPDATE FAILEDREQUESTS SET REPEAT = " + failed.get( i ).repeat + 
                                          " WHERE ID = " + failed.get( i ).id );
                        }
                    }
                    stmt.close();                
                } catch ( Exception e ) {
                    // just to ensure that if a sql exception occurs other requests
                    // has the chance to be removed from the DB
                    LOG.logError( e.getMessage(), e );
                    informAdmin( Messages.getMessage( "CSW_ERROR_UPDATING_FAILEDREQUESTS", failed.get( i ).id ) );
                }
            }
            
            pool.releaseConnection( con, driver, url, user, password );
            
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new TriggerException( e );
        }
    }

    private void storeCurrentRequest( List<URL> errorAddr, String request ) {
        
        try {
            DBConnectionPool pool = DBConnectionPool.getInstance();
            Connection con = pool.acquireConnection( driver, url, user, password );            
            for ( int i = 0; i < errorAddr.size(); i++ ) {
                PreparedStatement stmt = 
                    con.prepareStatement( "INSERT INTO FAILEDREQUESTS (REQUEST,CSWADDRESS,REPEAT) VALUES (?,?,?)" );
                try {
                    stmt.setString( 1, request );
                    stmt.setString( 2, errorAddr.get( i ).toExternalForm() );
                    stmt.setInt( 3, 1 );
                    stmt.execute();
                } catch ( Exception e ) {
                    // just to ensure that if a sql exception occurs other requests
                    // has the chance to be inserted into the DB
                    LOG.logError( e.getMessage(), e );
                    informAdmin( Messages.getMessage( "CSW_ERROR_INSERTING_INTO_FAILEDREQUESTS", 
                                                      errorAddr.get( i ), request ) );
                }
                stmt.close(); 
            }
            pool.releaseConnection( con, driver, url, user, password );
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new TriggerException( e );
        }
        
    }

    private void informAdmin(String message) {

        String subject = Messages.getMessage( "CSW_SYNCHRONIZE_MAIL_SUBJECT" );
       
        MailMessage email;
        try {
            email = new EMailMessage( sender, receiver, subject, message, "text/html" );
        } catch ( UnknownMimeTypeException e ) {
            LOG.logError( e.getMessage(), e );
            throw new TriggerException( "Unknown mime type set." + e );
        }
        
        try {
            MailHelper.createAndSendMail( email, smtpServer );
        } catch ( SendMailException e ) {
            LOG.logError( e.getMessage(), e );
        }
       
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }
    
    private class Fail {
        public int id = 0; 
        public String request;
        public URL cswAddress;
        public int repeat;
        
        public Fail(int id, String request, URL cswAddress, int repeat) {
            this.id = id;
            this.request = request;
            this.cswAddress = cswAddress;
            this.repeat = repeat;
        }
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWSychronizationTrigger.java,v $
Revision 1.5  2006/11/07 11:04:45  poth
bug fix - checking if incomming valus[0] is an instance of TransactionResult

Revision 1.4  2006/10/18 17:00:56  poth
made DefaultOGCWebServiceResponse base type for all webservice responses

Revision 1.3  2006/10/17 09:32:22  poth
bug fix

Revision 1.2  2006/10/13 18:11:05  poth
implementation completed

Revision 1.1  2006/10/13 14:21:18  poth
initial check in


********************************************************************** */