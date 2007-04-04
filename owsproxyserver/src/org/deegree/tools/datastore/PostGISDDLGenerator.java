//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/datastore/PostGISDDLGenerator.java,v 1.16 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.tools.datastore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.model.crs.UnknownCRSException;
import org.xml.sax.SAXException;

/**
 * Generator for PostGIS DDL (CREATE) operations to create PostGIS database schemas from annotated
 * GML schema files.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.16 $, $Date: 2006/11/27 09:07:52 $
 */
public class PostGISDDLGenerator extends DDLGenerator {
   
    /**
     * Generates a new instance of <code>PostGISDDLGenerator</code>, ready to generate DDL for
     * the given schema.
     * 
     * @param schemaURL
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     * @throws XMLParsingException
     * @throws XMLSchemaException
     * @throws UnknownCRSException 
     */
    public PostGISDDLGenerator( URL schemaURL )
        throws MalformedURLException,
            IOException,
            SAXException,
            XMLParsingException,
            XMLSchemaException, UnknownCRSException {
        super( schemaURL );
    }
    
    /**
     * Generates the DDL statements necessary for the creation of the given table definition. This
     * is the PostGIS specific implementation.
     * 
     * @param table
     * @return the DDL statements necessary for the creation of the given table definition
     */    
    @Override
    protected StringBuffer generateCreateStatements( TableDefinition table ) {
        Collection<ColumnDefinition> geometryColumns = new ArrayList<ColumnDefinition> ();
        StringBuffer sb = new StringBuffer( "CREATE TABLE " );
        sb.append( table.getName() );
        sb.append( '(' );
        ColumnDefinition[] columns = table.getColumns();
        boolean needComma = false;
        for (int i = 0; i < columns.length; i++) {
            if (! columns[i].isGeometry() ) {
                if (needComma) {
                    sb.append (',');
                } else {
                    needComma = true;
                }
                sb.append( "\n    " );
                sb.append( columns[i].getName() );
                sb.append( ' ' );
                String typeName;
                try {
                    typeName = Types.getTypeNameForSQLTypeCode(columns[i].getType());
                } catch ( UnknownTypeException e ) {
                    typeName = "" + columns[i].getType();
                }
                sb.append( typeName );
                if ( !columns[i].isNullable() ) {
                    sb.append( " NOT NULL" );
                }
            } else {
                geometryColumns.add(columns [i]);
            }
        }
        ColumnDefinition[] pkColumns = table.getPKColumns();
        if ( pkColumns.length > 0 ) {
            sb.append( ",\n    PRIMARY KEY (" );
            for (int i = 0; i < pkColumns.length; i++) {
                sb.append( pkColumns[i].getName() );
                if ( i != pkColumns.length - 1 ) {
                    sb.append( ',' );
                }
            }
            sb.append (')');
        }
        sb.append( "\n);\n" );

        // build addGeometryStatements
        Iterator iter = geometryColumns.iterator();
        while (iter.hasNext()) {
            ColumnDefinition column = (ColumnDefinition) iter.next ();
            sb.append( "SELECT AddGeometryColumn ('public', '" );
            sb.append( table.getName() );
            sb.append( "', '" );
            sb.append( column.getName() );
            sb.append( "', -1, '" );
            sb.append( column.getType() );
            sb.append ("', '2');\n");
        }
        return sb;
    }

    /**
     * Generates the DDL statements necessary for the removal of the given table definition. This
     * is the PostGIS specific implementation.
     * 
     * @param table
     * @return the DDL statements necessary for the removal of the given table definition
     */    
    @Override
    protected StringBuffer generateDropStatements( TableDefinition table ) {
        StringBuffer sb = new StringBuffer ();
        ColumnDefinition[] columns = table.getColumns();
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].isGeometry()) {
                sb.append( "SELECT DropGeometryColumn ('public','" );
                sb.append( table.getName() );
                sb.append( "', '" );
                sb.append( columns [i].getName() );
                sb.append( "');\n" );
            }
        }
        sb.append ("DROP TABLE ");
        sb.append (table.getName());
        sb.append (" CASCADE;\n");
        return sb;
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: PostGISDDLGenerator.java,v $
 * Revision 1.16  2006/11/27 09:07:52  poth
 * JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 *
 * Revision 1.15  2006/11/23 15:25:03  mschneider
 * Javadoc fixed.
 *
 * Revision 1.14  2006/08/31 14:59:38  mschneider
 * Fixed output of SQL column types. Javadoc fixes.
 *
 * Revision 1.13  2006/08/24 06:43:54  poth
 * File header corrected
 *
 * Revision 1.12  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.11  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.9  2006/01/18 19:20:35  mschneider
 * Adapted to type code for MappingFields (instead of typeName).
 *
 * Revision 1.8  2005/12/22 02:15:46  mschneider
 * Changed srs to -1.
 *
 * Revision 1.7  2005/12/13 23:15:09  mschneider
 * Works again.
 *
 * Revision 1.6  2005/12/12 22:46:32  mschneider
 * Cleanup, javadoc, extraction of messages to ResourceBundle.
 *
 * Revision 1.5  2005/12/12 17:10:09  mschneider
 * Moving common functionality to DDLGenerator.
 *
 * Revision 1.4  2005/12/09 14:52:55  mschneider
 * Added creation of Drop-Statements to remove tables again. Cleaned up output.
 *
 * Revision 1.3 2005/12/08 22:24:24  mschneider
 * Added support for feature type fields, so ambigous foreign keys can be resolved properly.
 * 
 * Revision 1.2 2005/12/08 21:45:47  mschneider
 * Initial implementation.
 **************************************************************************************************/