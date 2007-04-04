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
package org.deegree.tools.shape;

import java.util.Properties;

import org.deegree.io.quadtree.DBQuadtreeManager;
import org.deegree.io.quadtree.DBQuadtreeManagerWithNumberId;

/**
 * 
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/10/20 08:08:14 $
 *
 * @since 2.0
 */
public class GenericSQLShapeImporter {

    private static void validate( Properties map )
                            throws Exception {
        if ( map.get( "-driver" ) == null ) {
            throw new Exception( "-driver must be set" );
        }
        
        if ( map.get( "-url" ) == null ) {
            throw new Exception( "-url must be set" );
        }
        
        if ( map.get( "-user" ) == null ) {
            throw new Exception( "-user must be set" );
        }
        
        if ( map.get( "-password" ) == null ) {
            throw new Exception( "-password must be set" );
        }
        
        if ( map.get( "-indexName" ) == null ) {
            throw new Exception( "-indexName must be set" );
        }
        
        if ( map.get( "-table" ) == null ) {
            throw new Exception( "-table must be set" );
        }
        
        if ( map.get( "-shapeFile" ) == null ) {
            throw new Exception( "-shapeFile must be set" );
        }
        
        if ( map.get( "-maxTreeDepth" ) == null ) {
            map.put( "-maxTreeDepth", 6 );
        }

    }

    private static void printHelp() {        
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_driver" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_url" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_user" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_password" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_indexName" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_table" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_owner" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_shapeFile" ) );        
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_maxTreeDepth" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help_idType" ) );
        System.out.println( GenericSQLShapeImporterMessages.getString( "GenericSQLShapeImporter.help" ) );
    }

    /**
     * @param args
     */
    public static void main( String[] args )
                            throws Exception {

        Properties map = new Properties();
        for ( int i = 0; i < args.length; i += 2 ) {
            System.out.print( args[i] );
            if ( args.length > i+1 ) {
                map.put( args[i], args[i + 1] );
            } else {
                map.put( args[i], null );
            }
            
            System.out.println( ' ' + args[i+1] );
        }

        if ( map.get( "-?" ) != null || map.get( "-h" ) != null ) {
            printHelp();
            return;
        }

        try {
            validate( map );
        } catch ( Exception e ) {
            System.out.println( e.getMessage() );
            System.out.println();
            printHelp();
            return;
        }

        int depth = Integer.parseInt( map.getProperty( "-maxTreeDepth" ) );
        DBQuadtreeManager qtm = null;
        if ( "INTEGER".equals( map.get( "-idType" ) ) ) {
            qtm = new DBQuadtreeManagerWithNumberId( map.getProperty( "-driver" ),
                                                   map.getProperty( "-url" ),
                                                   map.getProperty( "-user" ),
                                                   map.getProperty( "-password" ), "ISO-8859-1",
                                                   map.getProperty( "-indexName" ),
                                                   map.getProperty( "-table" ), "geometry",
                                                   map.getProperty( "-owner" ), depth );
        } else {
            qtm = new DBQuadtreeManager( map.getProperty( "-driver" ),
                                       map.getProperty( "-url" ),
                                       map.getProperty( "-user" ),
                                       map.getProperty( "-password" ), "ISO-8859-1",
                                       map.getProperty( "-indexName" ),
                                       map.getProperty( "-table" ), "geometry",
                                       map.getProperty( "-owner" ), depth );
        }

        qtm.importShape( map.getProperty( "-shapeFile" ) );

        System.exit( 0 );

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GenericSQLShapeImporter.java,v $
Revision 1.6  2006/10/20 08:08:14  poth
required changes resulting from generalizing qudtree implementation

Revision 1.5  2006/07/26 12:45:04  poth
support for alternative object identifier type (integer) -> alternative QuadtreeManager

Revision 1.4  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
