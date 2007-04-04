//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/csw/discovery/GetRecordById.java,v 1.7 2006/06/19 19:26:51 mschneider Exp $
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
package org.deegree.ogcwebservices.csw.discovery;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.csw.AbstractCSWRequest;
import org.deegree.ogcwebservices.csw.NonexistentCollectionException;
import org.deegree.ogcwebservices.csw.NonexistentTypeException;
import org.w3c.dom.Element;

/**
 * The mandatory GetRecordById request retrieves the default representation of 
 * catalogue records using their identifier. The GetRecordById operation is an 
 * implementation of the Present operation from the general model. This operation 
 * presumes that a previous query has been performed in order to obtain the 
 * identifiers that may be used with this operation. For example, records returned 
 * by a GetRecords operation may contain references to other records in the catalogue 
 * that may be retrieved using the GetRecordById operation. This operation is also a 
 * subset of the GetRecords operation, and is included as a convenient short form 
 * for retrieving and linking to records in a catalogue.
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/06/19 19:26:51 $
 *
 * @since 2.0
 */
public class GetRecordById extends AbstractCSWRequest {
    
    private static final long serialVersionUID = -3602776884510160189L;

    private static final ILogger LOG = LoggerFactory.getLogger(GetRecordById.class);
    
    private String[] ids = null;
    private String elementSetName = null;
    
    /**
     * creates a <code>GetRecordById</code> request from the XML fragment 
     * passed. The passed element must be valid against the OGC CSW 2.0 
     * GetRecordById schema.
     * 
     * @param id unique ID of the request
     * @param root root element of the GetRecors request
     * @return
     */
    public static GetRecordById create(String id, Element root) throws MissingParameterValueException,
                                                                    InvalidParameterValueException,
                                                                    OGCWebServiceException {
        LOG.entering();
        
        GetRecordByIdDocument document = new GetRecordByIdDocument();
        document.setRootElement( root );
        GetRecordById ogcRequest = document.parseGetRecordById(id);
        
        LOG.exiting();
        
        return ogcRequest;
    }


    /**
     * Creates a new <code>GetRecordById</code> instance from the values stored
     * in the submitted Map. Keys (parameter names) in the Map must be
     * uppercase.
     *
     * @TODO evaluate vendorSpecificParameter
     * 
     * @param kvp
     *            Map containing the parameters
     * @exception InvalidParameterValueException
     * @exception MissingParameterValueException
     * @exception NonexistentCollectionException
     * @exception NonexistentTypeException
     */
    public static GetRecordById create(Map kvp) {

        String version = (String)kvp.remove( "VERSION" );
        String elementSetName = (String)kvp.remove( "ELEMENTSETNAME" );
        String tmp = (String)kvp.remove( "ID" );
        String[] ids = StringTools.toArray( tmp, ",", true );
        
        return new GetRecordById( "ID" + System.currentTimeMillis(), version, kvp, 
                                  ids, elementSetName );
    }
    
    /**
     * 
     * @param ids identifier of the requested catalogue entries
     * @param elementSetName requested element set (brief|summary|full). Can be 
     *              <code>null</code>; will be treaded as full.
     */
    GetRecordById(String id, String version, Map vendorSpecificParameters,
                  String[] ids, String elementSetName) {
        super( id, version, vendorSpecificParameters );
        this.ids = ids;
        this.elementSetName = elementSetName;
    }

    /**
     * returns the requested element set name. If the returned value 
     * equals <code>null</code> a 'summary' request shall be performed. possible
     * values are:
     * <ul>
     *  <li>brief</li>
     *  <li>summary</li>
     *  <li>full</li>
     * </ul>
     *      
     * @return
     */
    public String getElementSetName() {
        return elementSetName;
    }
    
    public String[] getIds() {
        return ids;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetRecordById.java,v $
Revision 1.7  2006/06/19 19:26:51  mschneider
Refactored due to renaming of CSWRequestBase.

Revision 1.6  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.4  2006/03/16 16:47:45  poth
*** empty log message ***

Revision 1.3  2006/02/26 21:30:42  poth
*** empty log message ***

Revision 1.2  2005/09/27 19:53:18  poth
no message

Revision 1.1  2005/09/09 19:43:46  poth
no message


********************************************************************** */