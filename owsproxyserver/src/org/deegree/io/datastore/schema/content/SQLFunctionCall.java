//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/schema/content/SQLFunctionCall.java,v 1.4 2006/11/09 17:38:00 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
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
package org.deegree.io.datastore.schema.content;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deegree.io.datastore.schema.MappedSimplePropertyType;

/**
 * Content class for {@link MappedSimplePropertyType}s that describes a call to a function provided
 * by an SQL database.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/11/09 17:38:00 $
 */
public class SQLFunctionCall extends FunctionCall {

    private String callString;

    private int typeCode;

    private int[] usedVarNumbers;

    /**
     * Initializes a newly created <code>SQLFunctionCall</code> with the given call string and
     * {@link FunctionParam}s.
     * 
     * @param callString
     *           call string with placeholders ($1,$2,...,$n)
     * @param typeCode
     *           SQL type code of the return value
     * @param params
     *           parameters to be used for the placeholders
     */
    public SQLFunctionCall( String callString, int typeCode, List<FunctionParam> params ) {
        super( params );
        this.callString = callString;
        this.typeCode = typeCode;
        this.usedVarNumbers = extractUsedVars( callString );
    }

    /**
     * Initializes a newly created <code>SQLFunctionCall</code> with the given call string and
     * {@link FunctionParam}s.
     * 
     * @param callString
     *           call string with placeholders ($1,$2,...,$n)
     * @param typeCode
     *           SQL type code of the return value
     * @param params
     *           parameters to be used for the placeholders
     */
    public SQLFunctionCall( String callString, int typeCode, FunctionParam...params ) {
        super( params );
        this.callString = callString;
        this.typeCode = typeCode;
        this.usedVarNumbers = extractUsedVars( callString );
    }    
    
    /**
     * Extracts all variable numbers ('$i') used in the given call string.
     * 
     * @param callString
     * @return all variable numbers used in the given call string
     */
    private int[] extractUsedVars( String callString ) {

        Set<Integer> usedVars = new HashSet<Integer>();

        int foundAt = callString.indexOf( '$' );
        while ( foundAt != -1 ) {
            foundAt++;
            String varNumberString = "";
            while ( foundAt < callString.length() ) {
                char numberChar = callString.charAt( foundAt++ );
                if ( numberChar >= '0' && numberChar <= '9' ) {
                    varNumberString += numberChar;
                } else {
                    break;
                }
            }

            assert varNumberString.length() != 0;

            try {
                int varNo = Integer.parseInt( varNumberString );
                assert varNo != 0;
                usedVars.add( varNo );
            } catch ( NumberFormatException e ) {
                assert false;
            }

            // find next '$' symbol
            foundAt = callString.indexOf( '$', foundAt );
        }

        int[] usedVarInts = new int[usedVars.size()];
        int i = 0;
        for ( Integer pos : usedVars ) {
            usedVarInts[i++] = pos;
        }

        return usedVarInts;
    }

    /**
     * Returns true, because the result of an SQL function call is (in general) suitable as a sort
     * criterion. 
     * 
     * @return true, because the result of an SQL function call is (in general) suitable as a sort
     *         criterion
     */
    public boolean isSortable() {
        return true;
    }

    /**
     * Returns the call string with placeholders ($1,$2,...,$n) for the {@link FunctionParam}s.
     *
     * @return the call string with placeholders ($1,$2,...,$n)
     */
    public String getCall() {
        return this.callString;
    }

    /**
     * Returns the SQL type code of the function call's return value.
     * 
     * @return the SQL type code of the function call's return value.
     */
    public int getTypeCode() {
        return this.typeCode;
    }

    /**
     * Returns all variable numbers used in the call string.
     * 
     * @return all variable numbers used in the call string
     */
    public int[] getUsedVars() {
        return this.usedVarNumbers;
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: SQLFunctionCall.java,v $
 Revision 1.4  2006/11/09 17:38:00  mschneider
 Added constructor that accepts arrays.

 Revision 1.3  2006/09/11 15:04:51  mschneider
 Removed unnecessary whitespace.

 Revision 1.2  2006/09/07 17:04:31  mschneider
 Added extraction of used variables + #getUsedVars().

 Revision 1.1  2006/09/05 17:42:08  mschneider
 Initial version.

 ********************************************************************** */