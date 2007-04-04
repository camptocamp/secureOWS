//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/transaction/Native.java,v 1.3 2006/10/12 16:24:00 mschneider Exp $
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
 Aennchenstraße 19
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
package org.deegree.ogcwebservices.wfs.operation.transaction;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.w3c.dom.Element;

/**
 * Represents a <code>Native</code> operation as a part of a {@link Transaction} request.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.3 $, $Date: 2006/10/12 16:24:00 $
 */
public class Native extends TransactionOperation {

    private Element vendorSpecificData;

    private String vendorId;

    private boolean safeToIgnore;

    /**
     * Creates a new <code>Native</code> instance.
     * 
     * @param handle
     *            optional identifier for the operation (for error messsages)
     * @param vendorSpecificData
     *            vendor specific information (as a DOM element)
     * @param vendorId
     *            vendor identifier
     * @param safeToIgnore
     *            true, if the operation may be ignored without problems, false if the surrounding
     *            request depends on it (and must fail if the native operation cannot be executed)
     */
    public Native( String handle, Element vendorSpecificData, String vendorId, boolean safeToIgnore ) {
        super( handle );
        this.vendorSpecificData = vendorSpecificData;
        this.vendorId = vendorId;
        this.safeToIgnore = safeToIgnore;
    }

    /**
     * Returns the vendor specific data that describes the operation to be performed.
     * 
     * @return the vendor specific data that describes the operation to be performed.
     */
    public Element getVendorSpecificData() {
        return this.vendorSpecificData;
    }

    /**
     * Returns the vendor identifier.
     * 
     * @return the vendor identifier.
     */
    public String getVendorId() {
        return this.vendorId;
    }

    /**
     * Returns whether the surrounding transaction request must fail if the operation can
     * not be executed.
     * 
     * @return true, if the operation may be ignored safely, false otherwise.
     */
    public boolean isSafeToIgnore() {
        return this.safeToIgnore;
    }

    /**
     * Returns the names of the feature types that are affected by the operation.
     * 
     * @return the names of the affected feature types.
     */
    @Override
    public List<QualifiedName> getAffectedFeatureTypes() {
        throw new UnsupportedOperationException( "getAffectFeatureTypes() is not supported "
                                                 + "for Native operations." );
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Native.java,v $
 Revision 1.3  2006/10/12 16:24:00  mschneider
 Javadoc + compiler warning fixes.

 Revision 1.2  2006/09/14 00:01:20  mschneider
 Little corrections + javadoc fixes.

 Revision 1.1  2006/05/16 16:25:30  mschneider
 Moved transaction related classes from org.deegree.ogcwebservices.wfs.operation to org.deegree.ogcwebservices.wfs.operation.transaction.

 ********************************************************************** */