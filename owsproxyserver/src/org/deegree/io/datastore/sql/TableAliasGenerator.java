//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/TableAliasGenerator.java,v 1.12 2006/09/14 00:18:33 mschneider Exp $
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

/**
 * Responsible for the generation of unique table aliases. This is needed in SQL queries in order to
 * built joins that use the same table more than once.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.12 $, $Date: 2006/09/14 00:18:33 $
 */
public class TableAliasGenerator {

    private static String DEFAULT_PREFIX = "X";

    private String prefix;

    private int currentIdx = 1;

    /**
     * Creates a new <code>TableAliasGenerator</code> instance that uses a default prefix for
     * generated table aliases.
     */
    public TableAliasGenerator() {
        this.prefix = DEFAULT_PREFIX;
    }

    /**
     * Creates a new <code>TableAliasGenerator</code> instance that uses the given prefix for
     * generated table aliases.
     * 
     * @param prefix
     *            prefix for generated table aliases
     */
    public TableAliasGenerator( String prefix ) {
        this.prefix = prefix;
    }

    /**
     * Returns a unique alias.
     * 
     * @return a unique alias
     */
    public String generateUniqueAlias() {
        return this.prefix
            + this.currentIdx++;
    }

    /**
     * Returns the specified number of unique aliases.
     * 
     * @param n
     * @return the specified number of unique aliases
     */
    public String[] generateUniqueAliases( int n ) {
        String[] aliases = new String[n];
        for (int i = 0; i < aliases.length; i++) {
            aliases[i] = generateUniqueAlias();
        }
        return aliases;
    }

    /**
     * Resets the alias sequence.
     */
    public void reset() {
        this.currentIdx = 1;
    }
}
/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: TableAliasGenerator.java,v $
 * Revision 1.12  2006/09/14 00:18:33  mschneider
 * Javadoc fixes.
 *
 * Revision 1.11  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.10  2006/05/26 09:42:41  poth
 * bug fix for supporting numberOfFeatures for returned FeatureCollections / footer correction
 *
 *
 ************************************************************************************************** */