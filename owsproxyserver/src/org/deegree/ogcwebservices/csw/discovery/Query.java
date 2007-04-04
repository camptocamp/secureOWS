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

import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcbase.SortProperty;

/**
 * Main component of a <code>GetRecords</code> request. A * <code>GetRecords</code> request may consist of several <code>Query</code> * elements. *  * @since 2.0 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @version $Revision: 1.9 $ *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> *  * @author last edited by: $Author: poth $ *  * @version 2.0, $Revision: 1.9 $, $Date: 2006/07/11 07:10:11 $
 */

public class Query {

    private String elementSetName;
    private String[] elementNames;
    private Filter constraint;
    private SortProperty[] sortProperties;
    private String[] typeNames;

    /**
     * Creates a new Query instance.
     * 
     * @param elementSetName
     * @param elementNames
     * @param sortProperties
     * @param constraint
     * @param typeName
     */
    Query(String elementSetName, String[] elementNames,
            Filter constraint, SortProperty [] sortProperties, String[] typeNames) {
        this.elementSetName = elementSetName;
        this.elementNames = elementNames;
        this.constraint = constraint;
        this.sortProperties = sortProperties;
        this.typeNames = typeNames;
    }

    /**
     * Zero or one (Optional); If <tt>null</tt> then getElementNames may
     * return a list of requested elements. If both methods returns
     * <tt>null</tt> the default action is to present all metadata elements.
     * <p>
     * The ElementName parameter is used to specify one or more metadata record
     * elements that the query should present in the response to the a
     * GetRecords operation. Well known sets of element may be named, in which
     * case the ElementSetName parameter may be used (e. g.brief, summary or
     * full).
     * <p>
     * If neither parameter is specified, then a CSW shall present all metadata
     * record elements
     */
    public String getElementSetName() {
        return elementSetName;
    }


    /**
     * List of element names returned by a getRecord request.
     */
    public String[] getElementsNames() {
        return elementNames;
    }

    /**
     * Zero or one (Optional); Default action is to execute an unconstrained
     * query
     */
    public Filter getContraint() {
        return this.constraint;
    }

    /**
     * Ordered list of names of metadata elements to use for sorting the
     * response. Format of each list item is metadata_elemen_ name:A indicating
     * an ascending sort or metadata_ element_name:D indicating descending sort
     * <p>
     * The result set may be sorted by specifying one or more metadata record
     * elements upon which to sort.
     * <p>
     * 
     * @todo verify return type URI[] or String
     */
    public SortProperty[] getSortProperties() {
        return this.sortProperties;
    }

    /**
     * The typeName parameter specifies the record type name that defines a set
     * of metadata record element names which will be constrained in the
     * predicate of the query. In addition, all or some of the these names may
     * be specified in the query to define which metadata record elements the
     * query should present in the response to the GetRecords operation.
     */
    public String[] getTypeNames() {
        return this.typeNames;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Query.java,v $
Revision 1.9  2006/07/11 07:10:11  poth
footer added/corrected


********************************************************************** */