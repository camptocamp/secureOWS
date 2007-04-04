//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/capabilities/CatalogueOperationsMetadata.java,v 1.5 2006/07/12 14:46:16 poth Exp $
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
package org.deegree.ogcwebservices.csw.capabilities;

import java.util.ArrayList;
import java.util.List;

import org.deegree.ogcwebservices.getcapabilities.Operation;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.owscommon.OWSDomainType;

/**
 * Represents the <code>OperationMetadata</code> part in the capabilities * document of an OGC-CSW according to the <code>OpenGIS� * Catalogue Services Specification 2.0</code>. * <p> * In addition to the <code>GetCapabilities</code> operation that all * <code>OWS 0.2</code> compliant services must implement, it may define some * or all of the following operations: <table border="1"> * <tr> * <th>Name</th> * <th>Mandatory?</th> * <th>Function</th> * </tr> * <tr> * <td>DescribeRecord</td> * <td align="center">X</td> * <td>Allows a client to discover elements of the information model supported * by the target catalogue service.</td> * </tr> * <tr> * <td>GetDomain</td> * <td align="center">-</td> * <td>The optional GetDomain operation is used to obtain runtime information * about the range of values of a metadata record element or request parameter. * The runtime range of values for a property or request parameter is typically * much smaller than the value space for that property or parameter based on its * static type definition. For example, a property or request parameter defined * as a 16bit positive integer in a database may have a value space of 65535 * distinct integers but the actual number of distinct values existing in the * database may be much smaller. This type of runtime information about the * range of values of a property or request parameter is useful for generating * user interfaces with meaningful pick lists or for generating query predicates * that have a higher chance of actually identifying a result set. It should be * noted that the GetDomain operation is a �best-effort� operation. That is to * say that a catalogue tries to generate useful information about the specified * request parameter or property if it can. It is entirely possible that a * catalogue may not be able to determine anything about the values of a * property or request parameter in which case an empty response should be * generated.</td> * </tr> * <tr> * <td>GetRecords</td> * <td align="center">X</td> * <td>The primary means of resource discovery in the general model are the two * operations search and present. In the HTTP protocol binding these are * combined in the form of the mandatory GetRecords operation, which does a * search and a piggybacked present.</td> * </tr> * <tr> * <td>GetRecordById</td> * <td align="center">X</td> * <td>The mandatory GetRecordById request retrieves the default representation * of catalogue records using their identifier. The GetRecordById operation is * an implementation of the Present operation from the general model. This * operation presumes that a previous query has been performed in order to * obtain the identifiers that may be used with this operation. For example, * records returned by a GetRecords operation may contain references to other * records in the catalogue that may be retrieved using the GetRecordById * operation. This operation is also a subset of the GetRecords operation, and * is included as a convenient short form for retrieving and linking to records * in a catalogue.</td> * </tr> * <tr> * <td>Transaction</td> * <td align="center">-</td> * <td>The optional Transaction operation defines an interface for creating, * modifying and deleting catalogue records. The specific payload being * manipulated must be defined in a profile.</td> * </tr> * <tr> * <td>Harvest</td> * <td align="center">-</td> * <td>The optional Harvest operation is an operation that "pulls" data into * the catalogue. That is, this operation only references the data to be * inserted or updated in the catalogue, and it is the job of the catalogue * service to resolve the reference, fetch that data, and process it into the * catalogue. The Harvest operation has two modes of operation, controlled by a * flag in the request. The first mode of operation is a synchronous mode in * which the CSW receives a Harvest request from the client, processes it * immediately, and sends the results to the client while the client waits. The * second mode of operation is asynchronous in that the server receives a * Harvest request from the client, and sends the client an immediate * acknowledgement that the request has been successfully received. The server * can then process the Harvest request whenever it likes, taking as much time * as is required and then send the results of the processing to a URI specified * in the original Harvest request. This latter mode of operation is included to * support Harvest requests that could run for a period of time longer than most * HTTP timeout's will allow. Processing a Harvest request means that the CSW * resolves the URI pointing to the metadata resource, parses the resource, and * then creates or modifies metadata records in the catalogue in order to * register the resource. This operation may be performed only once or * periodically depending on how the client invokes the operation.</td> * </tr> * </table> *  * @see org.deegree.ogcwebservices.getcapabilities.OperationsMetadata * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.5 $, $Date: 2006/07/12 14:46:16 $ *  * @since 2.0
 */

public class CatalogueOperationsMetadata extends OperationsMetadata {

    public static final String DESCRIBE_RECORD_NAME = "DescribeRecord";
    public static final String GET_DOMAIN_NAME = "GetDomain";
    public static final String GET_RECORDS_NAME = "GetRecords";
    public static final String GET_RECORD_BY_ID_NAME = "GetRecordById";
    public static final String TRANSACTION_NAME = "Transaction";
    public static final String HARVEST_NAME = "Harvest";

    private Operation describeRecord;
    private Operation getDomain;
    private Operation getRecords;
    private Operation getRecordById;
    private Operation transaction;
    private Operation harvest;


    /**
     * Constructs a new <code>CatalogOperationsMetadata</code> from the given
     * parameters.
     */
    public CatalogueOperationsMetadata(Operation getCapabilities,
            Operation describeRecord, Operation getDomain,
            Operation getRecords, Operation getRecordById,
            Operation transaction, Operation harvest,
            OWSDomainType [] parameters,
            OWSDomainType [] constraints) {
        super(getCapabilities, parameters, constraints);
        this.describeRecord = describeRecord;
        this.getDomain = getDomain;
        this.getRecords = getRecords;
        this.getRecordById = getRecordById;
        this.transaction = transaction;
        this.harvest = harvest;
    }

    /**
     * Returns all <code>Operations</code>.
     * 
     * @return
     */
    public Operation[] getOperations() {
        List list = new ArrayList();
        list.add( getCapabilitiesOperation );
        list.add( describeRecord );
        list.add( getRecords );        
        if ( getRecordById != null ) {
            list.add( getRecordById );
        }
        if ( getDomain != null ) {
            list.add( getDomain );
        }
        if ( transaction != null ) {
            list.add( transaction );
        }
        if ( harvest != null ) {
            list.add( harvest );
        }
        return (Operation[]) list.toArray( new Operation[ list.size() ] );
    }

    /**
     * @return Returns the describeRecord.
     * 
     * @uml.property name="describeRecord"
     */
    public Operation getDescribeRecord() {
        return describeRecord;
    }

    /**
     * @param describeRecord
     *            The describeRecord to set.
     * 
     * @uml.property name="describeRecord"
     */
    public void setDescribeRecord(Operation describeRecord) {
        this.describeRecord = describeRecord;
    }

    /**
     * @return Returns the getDomain.
     * 
     * @uml.property name="getDomain"
     */
    public Operation getGetDomain() {
        return getDomain;
    }

    /**
     * @param getDomain
     *            The getDomain to set.
     * 
     * @uml.property name="getDomain"
     */
    public void setGetDomain(Operation getDomain) {
        this.getDomain = getDomain;
    }

    /**
     * @return Returns the getRecordById.
     * 
     * @uml.property name="getRecordById"
     */
    public Operation getGetRecordById() {
        return getRecordById;
    }

    /**
     * @param getRecordId
     *            The getRecordId to set.
     * 
     * @uml.property name="getRecordById"
     */
    public void setGetRecordById(Operation getRecordById) {
        this.getRecordById = getRecordById;
    }

    /**
     * @return Returns the getRecords.
     * 
     * @uml.property name="getRecords"
     */
    public Operation getGetRecords() {
        return getRecords;
    }

    /**
     * @param getRecords
     *            The getRecords to set.
     * 
     * @uml.property name="getRecords"
     */
    public void setGetRecords(Operation getRecords) {
        this.getRecords = getRecords;
    }

    /**
     * @return Returns the harvest.
     * 
     * @uml.property name="harvest"
     */
    public Operation getHarvest() {
        return harvest;
    }

    /**
     * @param harvest
     *            The harvest to set.
     * 
     * @uml.property name="harvest"
     */
    public void setHarvest(Operation harvest) {
        this.harvest = harvest;
    }

    /**
     * @return Returns the transaction.
     * 
     * @uml.property name="transaction"
     */
    public Operation getTransaction() {
        return transaction;
    }

    /**
     * @param transaction
     *            The transaction to set.
     * 
     * @uml.property name="transaction"
     */
    public void setTransaction(Operation transaction) {
        this.transaction = transaction;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CatalogueOperationsMetadata.java,v $
Revision 1.5  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
