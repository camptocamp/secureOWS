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

package org.deegree.ogcwebservices.csw.discovery;

import java.util.HashMap;
import java.util.Map;

import org.deegree.ogcwebservices.csw.AbstractCSWRequest;

/**
 * The optional GetDomain operation is used to obtain runtime information about
 * the range of values of a metadata record element or request parameter. The
 * runtime range of values for a property or request parameter is typically much
 * smaller than the value space for that property or parameter based on its
 * static type definition. For example, a property or request parameter defined
 * as a 16bit positive integer in a database may have a value space of 65535
 * distinct integers but the actual number of distinct values existing in the
 * database may be much smaller.
 * <p>
 * This type of runtime information about the range of values of a property or
 * request parameter is useful for generating user interfaces with meaningful
 * pick lists or for generating query predicates that have a higher chance of
 * actually identifying a result set.
 * <p>
 * It should be noted that the GetDomain operation is a "best-effort" operation.
 * That is to say that a catalogue tries to generate useful information about
 * the specified request parameter or property if it can. It is entirely
 * possible that a catalogue may not be able to determine anything about the
 * values of a property or request parameter in which case an empty response
 * should be generated.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/08/08 09:22:00 $
 */
public class GetDomain extends AbstractCSWRequest {

    public static GetDomain create(Map map) {
        return new GetDomain((String) map.get("ID"), (String) map.get("VERSION"), null);
    }

    /**
     * @param version
     * @param id
     * @param vendorSpecificParameter
     */
    GetDomain(String id, String version, HashMap vendorSpecificParameter) {
        super(version, id, vendorSpecificParameter);
        // TODO Auto-generated constructor stub
    }

    /**
     * Unordered list of names of requested properties, from the information
     * model that the catalogue is using
     * <p>
     * Zero or one; (Conditional)Include when ParameterName not included
     */
    public String[] getPropertyNames() {
        return null;
    }

    /**
     * Unordered list of names of requested parameters, of the form
     * OperationName. ParameterName
     * <p>
     * Zero or one; (Conditional)Include when PropertyName not included
     */
    public String[] ParameterNames() {
        return null;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetDomain.java,v $
Revision 1.9  2006/08/08 09:22:00  poth
uncessary import removed

Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
