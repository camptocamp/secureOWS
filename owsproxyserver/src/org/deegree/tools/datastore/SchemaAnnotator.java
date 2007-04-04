//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/tools/datastore/SchemaAnnotator.java,v 1.9 2006/11/23 15:25:03 mschneider Exp $
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

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.xml.sax.SAXException;

/**
 * Blah blah blah...
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.9 $, $Date: 2006/11/23 15:25:03 $
 */
public class SchemaAnnotator {

    private static final String XSL_FILE = "annotator.xsl";

    private static XSLTDocument xslSheet = new XSLTDocument();

    private static Properties featureTypeMappings = new Properties();

    private static Properties propertyMappings = new Properties();

    static {
        URL sheetURL = SchemaAnnotator.class.getResource( XSL_FILE );
        try {
            xslSheet.load( sheetURL );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static String getColumnName( String columnName ) {
        columnName = columnName.toLowerCase();
        String colName = (String) propertyMappings.get( columnName );
        if ( colName == null ) {
            System.out.println( "No field name -> column mapping for feature type '" + columnName
                                + "'. Using field name as column name." );
            colName = columnName;
        }
        return colName;
    }

    public static String getTableName( String featureTypeName ) {
        featureTypeName = featureTypeName.toLowerCase();
        String tableName = (String) featureTypeMappings.get( featureTypeName );
        if ( tableName == null ) {
            System.out.println( "No feature type -> table mapping for feature type '"
                                + featureTypeName + "'. Using feature type name as table name." );
            tableName = featureTypeName;
        }
        return tableName;
    }

    /**
     * @param args
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    public static void main( String[] args )
                            throws IOException, SAXException, TransformerException {

        if ( args.length != 4 ) {
            System.out.println( "Usage: SchemaAnnotator <input.xsd> <output.xsd> <tableName.properties> <columnName.properties>" );
            System.exit( 0 );
        }

        String tableNames = args [3];
        String columnNames = args [2];
        String outputFile = args [1];
        String inputFile = args [0];
        
        System.out.println( "Loading feature type -> table name translations from file '"
                            + tableNames + "'..." );
        featureTypeMappings.load( new FileInputStream( new URL( tableNames ).getFile() ) );

        System.out.println( "Loading property -> column name translations from file '"
                            + columnNames + "'..." );
        propertyMappings.load( new FileInputStream( new URL( columnNames ).getFile() ) );

        System.out.println( "Loading input schema file '" + inputFile + "'..." );
        XMLFragment inputSchema = new XMLFragment();
        inputSchema.load( new URL( inputFile ) );

        System.out.println( "Adding annotation information..." );
        XMLFragment outputSchema = xslSheet.transform( inputSchema );

        System.out.println( "Writing annotated schema file '" + outputFile + "'..." );
        FileWriter writer = new FileWriter( new URL( outputFile ).getFile() );
        Properties properties = new Properties();

        properties.setProperty( OutputKeys.ENCODING, "UTF-8" );
        outputSchema.write( writer, properties );
        writer.close();
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to:
 * $Log: SchemaAnnotator.java,v $
 * Revision 1.9  2006/11/23 15:25:03  mschneider
 * Javadoc fixed.
 *
 * Revision 1.8  2006/08/29 19:54:14  poth
 * footer corrected
 *
 * Revision 1.7  2006/08/24 06:43:54  poth
 * File header corrected
 *
 * Revision 1.6  2006/04/06 20:25:29  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.3  2006/01/16 20:36:39  poth
 * *** empty log message ***
 *
 * Revision 1.2  2005/12/09 07:53:04  deshmukh
 * *** empty log message ***
 * 
 * Revision 1.1 2005/12/07 16:26:30 mschneider
 * 
 **************************************************************************************************/