//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/StatementBuffer.java,v 1.15 2006/09/26 16:45:45 mschneider Exp $
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
package org.deegree.io.datastore.sql;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * Helper class for the creation and logging of {@link PreparedStatement}s.
 * <p>
 * It allows to concatenate the query step by step and holds the arguments of the query as well.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.15 $, $Date: 2006/09/26 16:45:45 $
 */
public class StatementBuffer {

    protected static final ILogger LOG = LoggerFactory.getLogger( StatementBuffer.class );    
    
    // contains the SQL-query string
    private StringBuffer queryBuffer = new StringBuffer();

    // contains the arguments of the query
    private List<StatementArgument> argumentList = new ArrayList<StatementArgument>();

    /**
     * Appends the given character to the statement.
     * 
     * @param c
     */
    public void append( char c ) {
        this.queryBuffer.append( c );
    }

    /**
     * Appends the given string to the statement.
     * 
     * @param s
     */    
    public void append( String s ) {
        this.queryBuffer.append( s );
    }

    /**
     * Appends the given {@link StringBuffer} to the statement.
     * 
     * @param sb
     */    
    public void append( StringBuffer sb ) {
        this.queryBuffer.append( sb );
    }

    /**
     * Appends the given argument (as the replacement value for the '?' character in the query)
     * to the statement.
     * 
     * @param o
     * @param typeCode
     */
    public void addArgument( Object o, int typeCode ) {
        StatementArgument argument = new StatementArgument (o, typeCode);
        this.argumentList.add( argument );
    }    
    
    /**
     * Returns the query string (without the arguments' values).
     * 
     * @return the query string (without the arguments' values)
     */
    public String getQueryString() {
        return this.queryBuffer.toString();
    }

    /**
     * Returns an {@link Iterator} over the arguments of the query.
     * 
     * @return an Iterator over the arguments of the query
     */
    public Iterator<StatementArgument> getArgumentsIterator() {
        return this.argumentList.iterator();
    }

    @Override
    public String toString() {
        return queryBuffer.toString();
    }
    
    /**
     * Encapsulates an argument value and the SQL type code for the target column.
     * 
     * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
     * @author last edited by: $Author: mschneider $
     * 
     * @version $Revision: 1.15 $, $Date: 2006/09/26 16:45:45 $
     */
    public class StatementArgument {
        
        private Object o;

        private int typeCode;
        
        StatementArgument( Object o, int typeCode ) {
            this.o = o;
            this.typeCode = typeCode;
        }
        
        /**
         * Returns the argument value.
         * 
         * @return the argument value
         */
        public Object getArgument () {
            return this.o;
        }

        /**
         * Returns the SQL type code for the column that is targeted by the argument.
         * 
         * @return the SQL type code for the column that is targeted by the argument
         */
        public int getTypeCode () {
            return this.typeCode;
        }        
    }
}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: StatementBuffer.java,v $
 * Revision 1.15  2006/09/26 16:45:45  mschneider
 * Javadoc corrections + fixed warnings.
 *
 * Revision 1.14  2006/09/19 14:54:02  mschneider
 * Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.
 *
 * Revision 1.13  2006/09/14 00:18:33  mschneider
 * Javadoc fixes.
 *
 * Revision 1.12  2006/09/13 18:23:19  mschneider
 * Improved javadoc.
 *
 * Revision 1.11  2006/06/01 12:16:14  mschneider
 * Fixed header + footer.
 *
 * Revision 1.10  2006/05/26 09:42:41  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 ************************************************************************************************** */