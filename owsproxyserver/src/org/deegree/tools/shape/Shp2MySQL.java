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
package org.deegree.tools.shape;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.WKTAdapter;

/**
 * 
 *
 * @version $Revision: 1.14 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class Shp2MySQL {
    
    private static final ILogger LOG = LoggerFactory.getLogger( Shp2MySQL.class );
    
    private ArrayList fileList = new ArrayList();
    private String outDir = null;
    
    /**
     * Creates a new Shp2MySQL object.
     *
     * @param file 
     */
    public Shp2MySQL( String file ) {
        fileList.add( file );
        int pos = file.lastIndexOf( '\\' );
        if ( pos < 0 ) {
            pos = file.lastIndexOf( '/' );
        }
        if ( pos < 0 ) {
            outDir = "";
        } else {
            outDir = file.substring( 0, pos );
        }
    }
 
    
    public void run() throws Exception {
        
        for (int i = 0; i < fileList.size(); i++) {
            
            String outFile = (String)fileList.get( i ) + ".sql";
            int pos = outFile.lastIndexOf( '\\' );
            if ( pos < 0 ) {
                pos = outFile.lastIndexOf( '/' );
            }
            if ( pos >= 0 ) {
                outFile = outFile.substring( pos+1 );
            }
            
            BufferedWriter fos = new BufferedWriter( 
                                    new OutputStreamWriter( 
                                        new FileOutputStream( outDir + "/" + outFile ),
                                                              "ISO-8859-1" ) );            
            
            ShapeFile sf = new ShapeFile( (String)fileList.get( i ) );			
            
            // delete table if already exists
            fos.write( "drop table " + sf.getFeatureByRecNo( 1 ).getFeatureType().getName() + ";" );
            fos.newLine();
            
            // get createtable sql statement and write it to the file            
            String createTable = 
                getCreateTableStatement( sf.getFeatureByRecNo( 1 ).getFeatureType() );
            fos.write( createTable );
            fos.newLine();
            
            String tableName = 
                sf.getFeatureByRecNo( 1 ).getFeatureType().getName().getAsString().toUpperCase();
            
            LOG.logInfo( "write to file: " + outDir + "/" + outFile );
            // create an insert statement for each feature conained in 
            // the shapefile
			for (int j = 0; j < sf.getRecordNum(); j++) {
                if ( j % 50 == 0 ) System.out.print(".");
                
                StringBuffer names = new StringBuffer( "(" );
                StringBuffer values = new StringBuffer( " VALUES (" );
                
			    Feature feature = sf.getFeatureByRecNo( j+1 );
                FeatureType ft = feature.getFeatureType();
                PropertyType ftp[] = ft.getProperties();
                boolean gm = false;
                for (int k = 0; k < ftp.length; k++) {
                    Object o = feature.getProperties( ftp[i].getName() )[0];                    
                    if ( o != null ) {           
                        QualifiedName name = ftp[k].getName();
                        String value = null;
                        if ( o instanceof Geometry ) {
                            value = WKTAdapter.export( (Geometry)o ).toString();
                            value = "GeomFromText('" + value + "')";
                            gm = true;
                        } else {
                            value = o.toString();
                        }
                        names.append( name.getAsString() );
                        if ( ftp[k].getType()== Types.VARCHAR || ftp[k].getType()== Types.CHAR ) {
                            value = StringTools.replace( value, "'", "\\'", true );
                            value = StringTools.replace( value, "\"", "\\\"", true );
                            values.append( "'" + value + "'" );
                        } else {
                            values.append( value );
                        }
                        if ( k < ftp.length-1 ) {
                            names.append( "," );
                            values.append( "," );
                        }
                    }                    
                }

                if ( !gm ) {
                    LOG.logInfo( ""+ names );
                    continue;
                }
                names.append( ")" );
                values.append( ")" );
                
                fos.write( "INSERT INTO " + tableName + " " );
                fos.write( names.toString() );
                fos.write( values.toString() + ";" );                
                fos.newLine();
            }
            fos.write( "ALTER TABLE " + tableName +" ADD SPATIAL INDEX(GEOM);" );
            fos.write(  "commit;" );                
            fos.newLine();
            fos.close();
        }
        
        LOG.logInfo( "finished!" );
        
    }
   
    /**
     * creates a create table sql statement from the passed <tt>FeatureType</tt>
     *
     * @param ft feature type
     * @return the created SQL statement
     */
    private String getCreateTableStatement( FeatureType ft ) {
        
        StringBuffer sb = new StringBuffer();
        String name = ft.getName().getAsString();
        
        PropertyType[] ftp = ft.getProperties();
        
        sb.append( "CREATE TABLE " ).append( name ).append( " (");
        for (int i = 0; i < ftp.length; i++) {
            sb.append( ftp[i].getName() ).append( " " );
            int type = ftp[i].getType();
            if ( type == Types.VARCHAR ) {
                sb.append( " VARCHAR(255) " );
            } else if (type == Types.DOUBLE ) {
                sb.append( " DOUBLE(20,8) " );
            } else if (type == Types.INTEGER ) {
                sb.append( " INT(12) " );
            } else if (type == Types.FLOAT ) {
                sb.append( " DOUBLE(20,8) " );
            } else if (type == Types.DATE) {
                sb.append( " Date " );
            } else if (type == Types.GEOMETRY ||
                       type == Types.POINT ||
                       type == Types.CURVE ||
                       type == Types.SURFACE ||
                       type == Types.MULTIPOINT ||
                       type == Types.MULTICURVE ||
                       type == Types.MULTISURFACE ) {
                sb.append( " GEOMETRY NOT NULL" );
            }
            if ( i < ftp.length-1 ) {
                sb.append( "," );
            }
        }
        sb.append( ");" );
        
        return sb.toString();
    }
    
    /**
	 * prints out helping application-information. 
	 * @param n an integer parameter, which determines which help-information 
     *          should be given out. 
	 */
	private static void usage(int n) {
		switch (n) {
			case 0 :
				System.out.println(
					"usage: java -classpath .;deegree.jar de.tools.Shp2MySQL "
                        + "                          [-f shapefile -d sourcedirectory]\n"
						+ "                          [--version] [--help]\n"
						+ "\n"
						+ "arguments:\n"
						+ "    -f shapefile  reads the input shapefile. must be set\n"
                        + "                  if -d is not set.\n"
						+ "    -d inputdir   name of the directory that contains the.\n"
                        + "                  source shapefiles. must be set if -f is\n"
                        + "                  not set.\n"    
						+ "\n"						
						+ "information options:\n"
						+ "    --help      shows this help.\n"
						+ "    --version   shows the version and exits.\n");
				break;
			case 1 :
				System.out.println(
					"Try 'java -classpath .;deegree.jar de.tools.Shp2MySQL --help'\n"+
                    "for more information.");
				break;

			default :
				System.out.println(
					"Unknown usage: \n" +
                    "Try 'java -classpath .;deegree.jar de.tools.Shp2MySQL --help'\n"+
                    "for more information.");
				break;
		}
	}

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) throws Exception {
        
        //args = new String[] { "-f", "C:/Dokumente und Einstellungen/Administrator/Eigene Dateien/geodata/shape/europe/country" };
        
        if ( args == null || args.length < 2 ) {
            usage( 0 );
            System.exit( 1 );
        }
                
        HashMap map = new HashMap();

        for ( int i = 0; i < args.length; i += 2 ) {
            map.put( args[i], args[i + 1] );
        }

        if ( map.get( "--help" ) != null ) {
            usage( 0) ;
            System.exit( 0 );
        }
        
        if ( map.get( "--version" ) != null ) {
            System.out.println("Shp2MySQL version 1.0.0");
            System.exit( 0 );
        }
        
        // one single file shall be transformed
        if ( map.get( "-f" ) != null ) {
            String f = (String)map.get( "-f" );
            if ( f.toUpperCase().endsWith( ".SHP" ) ) {
                f = f.substring( 0, f.length()-4 );
            }
            Shp2MySQL shp = new Shp2MySQL( f );
            shp.run();
        } else {
            // the files of a whole directory shall be transformed
        }
        
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Shp2MySQL.java,v $
Revision 1.14  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
