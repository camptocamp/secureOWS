//$Header$
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

import java.net.URI;
import java.util.List;

import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.Filter;
import org.w3c.dom.Element;

/**
 * An Update object is used to specify values to be used to change existing information in the
 * catalogue. If a complete record instance value is specified then the entire record in the
 * catalogue will be replaced by the value constained in the Update object. If individual record
 * property values are specified in an Update object, then those individual property values of the
 * catalogue record shall be updated.
 * 
 * @version $Revision$
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version 1.0. $Revision$, $Date$
 * 
 * @since 2.0
 */
public class Update extends Operation {

    private URI typeName = null;

    private Filter constraint = null;

    private Element record = null;

    private List<FeatureProperty> recordProperties = null;

    /**
     * 
     * @param handle
     * @param typeName
     * @param constraint
     * @param record
     * @param recordProperties
     */
    public Update( String handle, URI typeName, Filter constraint, Element record,
                  List<FeatureProperty> recordProperties ) {
        super( "Update", handle );

        this.typeName = typeName;
        this.constraint = constraint;
        this.record = record;
        this.recordProperties = recordProperties;
    }

    /**
     * The number of records affected by an update action is determined by the contents of the
     * constraint.
     * 
     * @return
     */
    public Filter getConstraint() {
        return constraint;
    }

    /**
     * complete metadata record to be updated. can be used as an alternative for recordProperties.
     * 
     * @return
     */
    public Element getRecord() {
        return record;
    }

    /**
     * The <csw:RecordProperty> element contains a <csw:Name> element and a <csw:Value> element. The
     * <csw:Name> element is used to specify the name of the record property to be updated. The
     * value of the <csw:Name> element may be a path expression to identify complex properties. The
     * <csw:Value> element contains the value that will be used to update the record in the
     * catalogue.
     * 
     * @return
     */
    public List<FeatureProperty> getRecordProperties() {
        return recordProperties;
    }

    /**
     * The optional typeName attribute may be used to specify the collection name from which records
     * will be updated.
     * 
     * @return
     */
    public URI getTypeName() {
        return typeName;
    }

}
/* ************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log$
 * Revision 1.7  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.6  2006/07/21 15:37:38  poth
 * footer corrected
 *
 * Revision 1.5  2006/04/06 20:25:27  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.4  2006/04/04 20:39:42  poth
 * *** empty log message ***
 * Revision 1.3 2006/03/30 21:20:26
 * poth ** empty log message ***
 * 
 * Revision 1.2 2006/02/20 09:30:18 poth ** empty log message ***
 * 
 * Revision 1.1 2006/02/17 16:28:12 poth ** empty log message ***
 * 
 * Revision 1.1 2006/01/09 07:47:09 ap ** empty log message ***
 * 
 * 
 *********************************************************************************************** */