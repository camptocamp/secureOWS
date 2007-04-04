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
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLException;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A Transaction defines an atomic unit of work and is a container for one or more insert, update
 * and/or delete actions.
 * 
 * 
 * @version $Revision: 1.11 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 1.0. $Revision: 1.11 $, $Date: 2006/07/10 20:58:05 $
 * 
 * @since 2.0
 */
public class Transaction extends AbstractOGCWebServiceRequest {

    private static final long serialVersionUID = -4393029325052150570L;

    protected static final ILogger LOG = LoggerFactory.getLogger( Transaction.class );

    private List<Operation> operations = null;

    private boolean verboseResponse = false;

    /**
     * creates a Transaction object from its XML representation defined in OGC CS-W 2.0.0
     * specification
     * 
     * @param transRoot
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws XMLException
     */
    public static final Transaction create( String id, Element transRoot )
                            throws OGCWebServiceException {
        try {            
            TransactionDocument doc = new TransactionDocument( transRoot );
            return doc.parse( id );
        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.logError( "could not parse CS-W Transaction request: ", e );
            throw new OGCWebServiceException( "could not parse CS-W Transaction request: "
                                              + e.getMessage() );
        }
    }

    /**
     * 
     * @param version
     * @param id
     * @param vendorSpecificParameter
     */
    Transaction( String version, String id, Map vendorSpecificParameter,
                List<Operation> operations, boolean verboseResponse ) {
        super( version, id, vendorSpecificParameter );
        this.operations = operations;
        this.verboseResponse = verboseResponse;
    }

    /**
     * returns the name of the service; always CSW
     */
    public String getServiceName() {
        return "CSW";
    }

    /**
     * The verboseResponseattribute is a boolean that may be used by a client to indicate to a
     * server the amount of detail to generate in the rsponse. A value of FALSE means that a CSW
     * should generate a terse or brief transaction response. A value of TRUE, or the absence of the
     * attribute, means that the normal detailed transaction response should be generated.
     * 
     * @return
     */
    public boolean verboseResponse() {
        return verboseResponse;
    }

    /**
     * returns all operations being part of a transaction
     * 
     * @return
     */
    public List<Operation> getOperations() {
        return operations;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: Transaction.java,v $
 * Revision 1.11  2006/07/10 20:58:05  mschneider
 * Renamed parseTransaction() to parse() for unification.
 *
 * Revision 1.10  2006/07/10 15:01:36  poth
 * footer corrected
 *
 * Revision 1.9  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.8  2006/04/04 20:39:43  poth
 * *** empty log message ***
 * Revision 1.7
 * 2006/04/04 10:22:02 poth ** empty log message ***
 * 
 * Revision 1.6 2006/03/30 21:20:26 poth ** empty log message ***
 * 
 * Revision 1.5 2006/03/06 12:41:22 poth ** empty log message ***
 * 
 * Revision 1.4 2006/02/23 17:35:12 poth ** empty log message ***
 * 
 * Revision 1.3 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/17 16:28:12 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 **************************************************************************************************/