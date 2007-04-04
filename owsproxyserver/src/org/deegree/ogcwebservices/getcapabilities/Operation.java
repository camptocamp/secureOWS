// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/getcapabilities/Operation.java,v 1.10 2006/07/12 14:46:16 poth Exp $
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
package org.deegree.ogcwebservices.getcapabilities;

import java.io.Serializable;

import org.deegree.owscommon.OWSDomainType;

/**
 * Represents the definition of an <code>Operation</code> in the capabilities * document of an OGC-web service according to the <code>OWS Common * Implementation Specification 0.2</code> * (and <code>owsOperationsMetadata.xsd</code>). * <p> * It consists of a mandatory <code>name</code> attribute and the following * elements: <table border="1"> * <tr> * <th>Name</th> * <th>Occurences</th> * <th>Function</th> * </tr> * <tr> * <td>ows:DCP</td> * <td align="center">1-*</td> * <td>Unordered list of Distributed Computing Platforms (DCPs) supported for * this operation. At present, only the HTTP DCP is defined, so this element * will appear only once.</td> * </tr> * <tr> * <td>Parameter</td> * <td align="center">0-*</td> * <td>Optional unordered list of parameter domains that each apply to this * operation which this server implements. If one of these Parameter elements * has the same "name" attribute as a Parameter element in the * OperationsMetadata element, this Parameter element shall override the other * one for this operation. The list of required and optional parameter domain * limitations for this operation shall be specified in the Implementation * Specification for this service.</td> * </tr> * <tr> * <td>ows:Metadata</td> * <td align="center">0-*</td> * <td>Optional unordered list of additional metadata about this operation and * its' implementation. A list of required and optional metadata elements for * this operation should be specified in the Implementation Specification for * this service. (Informative: This metadata might specify the operation request * parameters or provide the XML Schemas for the operation request.)</td> * </tr> * </table> *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.10 $, $Date: 2006/07/12 14:46:16 $ *  * @since 2.0
 */

public class Operation implements Serializable {

    private String name;
    private DCPType[] dcps;
    private OWSDomainType[] parameters;

    
    private Object [] metadata; 

    /**
     * Creates a new <code>Operation</code> instance that has no
     * <code>Parameter</code> information.
     * 
     * @param name
     * @param dcps
     */
    public Operation(String name, DCPType[] dcps) {
        this(name, dcps, new OWSDomainType[0]);
    }

    /**
     * Creates a new <code>Operation</code> instance with
     * <code>Parameter</code> information.
     * 
     * @param name
     * @param dcpTypes
     * @param parameters
     */
    public Operation(String name, DCPType[] dcpTypes,
            OWSDomainType[] parameters) {
        this.name = name;
        this.dcps = dcpTypes;
        this.parameters = parameters;
    }

    /**
     * Returns the name of the <code>Operation</code>.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the <code>Operation</code>.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Returns the <code>DCP</code> definitions for the <code>Operation</code>.
     * 
     * @return
     */
    public DCPType[] getDCPs() {
        return dcps;
    }

    /**
     * Sets the <code>DCP</code> definitions for the <code>Operation</code>.
     * 
     * @param dcpTypes 
     */
    public void setDCPs(DCPType[] dcpTypes) {
        this.dcps = dcpTypes;
    }

    /**
     * Returns the specified <code>Parameter</code> value for the
     * <code>Operation</code>.
     * 
     * @param name
     */
    public OWSDomainType getParameter(String name) {
        for (int i = 0; i < parameters.length; i++) {            
            if (parameters[i].getName ().equals (name)) {
                return parameters[i];
            }
        }
        return null;
    }

    /**
     * Returns all <code>Parameters</code> of the <code>Operation</code>.
     * 
     * @return
     */
    public OWSDomainType[] getParameters() {
        return parameters;
    }

    /**
     * Sets the <code>Parameters</code> of the <code>Operation</code>.
     * 
     * @param parameters
     */
    public void setParameters(OWSDomainType[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @return Returns the metadata.
     */
    public Object[] getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata to set.
     */
    public void setMetadata(Object[] metadata) {
        this.metadata = metadata;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Operation.java,v $
Revision 1.10  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
