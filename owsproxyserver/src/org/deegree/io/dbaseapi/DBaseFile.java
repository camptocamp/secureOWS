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
package org.deegree.io.dbaseapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.util.TimeTools;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.ByteUtils;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * the datatypes of the dBase file and their representation as java types:
 * 
 * dBase-type dBase-type-ID java-type
 * 
 * character "C" String float "F" Float number "N" Double logical "L" String memo "M" String date
 * "D" Date binary "B" ByteArrayOutputStream
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.41 $, $Date: 2006/11/10 23:04:13 $
 */
public class DBaseFile {

    private static final URI DEEGREEAPP = CommonNamespaces.buildNSURI( "http://www.deegree.org/app" );

    private static final String APP_PREFIX = "app";

    private ArrayList colHeader = new ArrayList();

    // representing the datasection of the dBase file
    // only needed for writing a dBase file
    private DBFDataSection dataSection = null;

    // feature type of generated features
    private FeatureType ft;

    // keys: property types, values: column (in dbase file)
    private Map<PropertyType, String> ftMapping = new HashMap<PropertyType, String>( 100 );

    // Hashtable to contain info abouts in the table
    private Map column_info = new HashMap();

    // references to the dbase file
    private RandomAccessFile rafDbf;

    // file suffixes for dbf
    private final String _dbf = ".dbf";

    // represents the dBase file header
    // only needed for writing the dBase file
    private DBFHeader header = null;

    // representing the name of the dBase file
    // only needed for writing the dBase file
    private String fname = null;

    private String ftName = null;

    // number of records in the table
    private double file_numrecs;

    // data start position, and length of the data
    private int file_datalength;

    // data start position, and length of the data
    private int file_datap;

    // flag which indicates if a dBase file should be
    // read or writed.
    // filemode = 0 : read only
    // filemode = 1 : write only
    private int filemode = 0;

    // number of columns
    private int num_fields;

    // current record
    private long record_number = 0;

    // size of the cache used for reading data from the dbase table
    private long cacheSize = 1000000;

    // array containing the data of the cache
    private byte[] dataArray = null;

    // file position the caches starts
    private long startIndex = 0;

    /**
     * constructor<BR>
     * only for reading a dBase file<BR>
     */
    public DBaseFile( String url ) throws IOException {
        fname = url;

        // creates rafDbf
        rafDbf = new RandomAccessFile( url + _dbf, "r" );

        // dataArray = new byte[(int)rafDbf.length()];
        if ( cacheSize > rafDbf.length() ) {
            cacheSize = rafDbf.length();
        }

        dataArray = new byte[(int) cacheSize];
        rafDbf.read( dataArray );
        rafDbf.seek( 0 );

        // initialize dbase file
        initDBaseFile();

        filemode = 0;
    }

    /**
     * constructor<BR>
     * only for writing a dBase file<BR>
     */
    public DBaseFile( String url, FieldDescriptor[] fieldDesc ) throws DBaseException {
        fname = url;

        // create header
        header = new DBFHeader( fieldDesc );

        // create data section
        dataSection = new DBFDataSection( fieldDesc );

        filemode = 1;
    }

    /**
     * 
     */
    public void close() {
        try {
            if ( rafDbf != null ) {
                // just true for reading access
                rafDbf.close();
            }
        } catch ( Exception ex ) {
            // should never happen
            ex.printStackTrace();
        }
    }

    /**
     * method: initDBaseFile(); inits a DBF file. This is based on Pratap Pereira's Xbase.pm perl
     * module
     * 
     */
    private void initDBaseFile()
                            throws IOException {
        // position the record pointer at 0
        rafDbf.seek( 0 );

        /*
         // read the file type
         file_type = fixByte( rafDbf.readByte() );

         // get the last update date
         file_update_year = fixByte( rafDbf.readByte() );
         file_update_month = fixByte( rafDbf.readByte() );
         file_update_day = fixByte( rafDbf.readByte() );
         */

        fixByte( rafDbf.readByte() );
        fixByte( rafDbf.readByte() );
        fixByte( rafDbf.readByte() );
        fixByte( rafDbf.readByte() );

        // a byte array to hold little-endian long data
        byte[] b = new byte[4];

        // read that baby in...
        rafDbf.readFully( b );

        // convert the byte array into a long (really a double)
        file_numrecs = ByteUtils.readLEInt( b, 0 );

        b = null;

        // a byte array to hold little-endian short data
        b = new byte[2];

        // get the data position (where it starts in the file)
        rafDbf.readFully( b );
        file_datap = ByteUtils.readLEShort( b, 0 );

        // find out the length of the data portion
        rafDbf.readFully( b );
        file_datalength = ByteUtils.readLEShort( b, 0 );

        // calculate the number of fields
        num_fields = ( file_datap - 33 ) / 32;

        // read in the column data
        int locn = 0; // offset of the current column

        // process each field
        for ( int i = 1; i <= num_fields; i++ ) {
            // seek the position of the field definition data.
            // This information appears after the first 32 byte
            // table information, and lives in 32 byte chunks.
            rafDbf.seek( ( ( i - 1 ) * 32 ) + 32 );

            b = null;

            // get the column name into a byte array
            b = new byte[11];
            rafDbf.readFully( b );

            // convert the byte array to a String
            String col_name = new String( b ).trim().toUpperCase();

            // read in the column type
            char[] c = new char[1];
            c[0] = (char) rafDbf.readByte();

            //String ftyp = new String( c );

            // skip four bytes
            rafDbf.skipBytes( 4 );

            // get field length and precision
            short flen = fixByte( rafDbf.readByte() );
            short fdec = fixByte( rafDbf.readByte() );

            // set the field position to the current
            // value of locn
            int fpos = locn;

            // increment locn by the length of this field.
            locn += flen;

            // create a new dbfCol object and assign it the
            // attributes of the current field
            dbfCol column = new dbfCol( col_name );
            column.type = new String( c );
            column.size = flen;
            column.position = fpos + 1;
            column.prec = fdec;

            // to be done: get the name of dbf-table via method in ShapeFile
            column.table = "NOT";

            column_info.put( col_name, column );
            colHeader.add( col_name );
        } // end for

        ft = createCanonicalFeatureType();

    } // end of initDBaseFile

    /**
     * Overrides the default feature type (which is generated from all columns in the dbase file)
     * to allow customized naming and ordering of properties.
     *  
     * @param ft
     * @param ftMapping
     */
    public void setFeatureType( FeatureType ft, Map<PropertyType, String> ftMapping ) {
        this.ft = ft;
        this.ftMapping = ftMapping;
    }

    /**
     * Creates a canonical {@link FeatureType} from all fields of the <code>DBaseFile</code>.
     * 
     * @return feature type that contains all fields as property types
     */
    private FeatureType createCanonicalFeatureType() {
        dbfCol column = null;

        PropertyType[] ftp = new PropertyType[colHeader.size() + 1];

        for ( int i = 0; i < colHeader.size(); i++ ) {
            // retrieve the dbfCol object which corresponds // to this column.
            column = (dbfCol) column_info.get( colHeader.get( i ) );

            QualifiedName name = new QualifiedName( APP_PREFIX, column.name, DEEGREEAPP );

            if ( column.type.equalsIgnoreCase( "C" ) ) {
                ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.VARCHAR, true );
            } else if ( column.type.equalsIgnoreCase( "F" ) || column.type.equalsIgnoreCase( "N" ) ) {
                if ( column.prec == 0 ) {
                    if ( column.size < 10 ) {
                        ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.INTEGER, true );
                    } else {
                        ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.BIGINT, true );
                    }
                } else {
                    if ( column.size < 8 ) {
                        ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.FLOAT, true );
                    } else {
                        ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.DOUBLE, true );
                    }
                }
            } else if ( column.type.equalsIgnoreCase( "M" ) ) {
                ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.VARCHAR, true );
            } else if ( column.type.equalsIgnoreCase( "L" ) ) {
                ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.VARCHAR, true );
            } else if ( column.type.equalsIgnoreCase( "D" ) ) {
                ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.VARCHAR, true );
            } else if ( column.type.equalsIgnoreCase( "B" ) ) {
                ftp[i] = FeatureFactory.createSimplePropertyType( name, Types.BLOB, true );
            }

            this.ftMapping.put( ftp[i], column.name );
        }

        int index = fname.lastIndexOf( "/" );
        ftName = fname;
        if ( index >= 0 ) {
            ftName = fname.substring( index + 1 );
        } else {
            index = fname.lastIndexOf( "\\" );
            if ( index >= 0 ) {
                ftName = fname.substring( index + 1 );
            }
        }
        
        QualifiedName featureTypeName = new QualifiedName( APP_PREFIX, ftName, DEEGREEAPP );

        QualifiedName name = new QualifiedName( APP_PREFIX, "GEOM", DEEGREEAPP );
        ftp[ftp.length - 1] = FeatureFactory.createGeometryPropertyType( name,
                                                                         Types.GEOMETRY_PROPERTY_NAME,
                                                                         1, 1 );

        return FeatureFactory.createFeatureType( featureTypeName, false, ftp );
    }

    /**
     * method: getRecordNum()<BR>
     * Get the number of records in the table
     */
    public int getRecordNum()
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        return (int) file_numrecs;
    }

    /**
     * method: goTop()<BR>
     * Position the record pointer at the top of the table.
     */
    public void goTop()
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        record_number = 0;
    }

    /**
     * method: nextRecord()<BR>
     * Advance the record pointer to the next record.
     */
    public boolean nextRecord()
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        if ( record_number < file_numrecs ) {
            record_number++;
            return true;
        }
        return false;

    }

    /**
     * method: getColumn(String col_name)<BR>
     * Retrieve a column's string value from the current row.
     */
    public String getColumn( String col_name )
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        try {
            // retrieve the dbfCol object which corresponds
            // to this column.
            dbfCol column = (dbfCol) column_info.get( col_name );

            // seek the starting offset of the current record,
            // as indicated by record_number
            long pos = file_datap + ( ( record_number - 1 ) * file_datalength );

            // read data from cache if the requested part of the dbase file is
            // within it
            if ( ( pos >= startIndex )
                 && ( ( pos + column.position + column.size ) < ( startIndex + cacheSize ) ) ) {
                pos = pos - startIndex;
            } else {
                // actualize cache starting at the current cursor position
                // if neccesary correct cursor position
                rafDbf.seek( pos );
                rafDbf.read( dataArray );
                startIndex = pos;
                pos = 0;
            }
            int ff = (int) ( pos + column.position );
            return new String( dataArray, ff, column.size ).trim();
        } catch ( Exception e ) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * method: public String[] getProperties()<BR>
     * returns the properties (column headers) of the dBase-file<BR>
     */
    public String[] getProperties()
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        return (String[]) colHeader.toArray( new String[colHeader.size()] );
    }

    /**
     * method: public String[] getDataTypes()<BR>
     * returns the datatype of each column of the database<BR>
     */
    public String[] getDataTypes()
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        String[] datatypes = new String[colHeader.size()];
        dbfCol column;

        for ( int i = 0; i < colHeader.size(); i++ ) {
            // retrieve the dbfCol object which corresponds
            // to this column.
            column = (dbfCol) column_info.get( colHeader.get( i ) );

            datatypes[i] = column.type.trim();
        }

        return datatypes;
    }

    /**
     * method: private boolean contains(String[] container, String element)<BR>
     * retruns true if the container sting array contains element<BR>
     */
    private boolean contains( String[] container, String element ) {
        for ( int i = 0; i < container.length; i++ )

            if ( container[i].equals( element ) ) {
                return true;
            }

        return false;
    }

    /**
     * returns the size of a column
     */
    public int getDataLength( String field )
                            throws DBaseException {
        dbfCol col = (dbfCol) column_info.get( field );
        if ( col == null )
            throw new DBaseException( "Field " + field + " not found" );

        return col.size;
    }

    /**
     * method: public String[] getDataTypes(String[] fields)<BR>
     * returns the datatype of each column of the database specified by fields<BR>
     */
    public String[] getDataTypes( String[] fields )
                            throws DBaseException {
        if ( filemode == 1 ) {
            throw new DBaseException( "class is initialized in write-only mode" );
        }

        ArrayList vec = new ArrayList();
        dbfCol column;

        for ( int i = 0; i < colHeader.size(); i++ ) {
            // check if the current (i'th) column (string) is
            // within the array of specified columns
            if ( contains( fields, (String) colHeader.get( i ) ) ) {
                // retrieve the dbfCol object which corresponds
                // to this column.
                column = (dbfCol) column_info.get( colHeader.get( i ) );

                vec.add( column.type.trim() );
            }
        }

        return (String[]) vec.toArray( new String[vec.size()] );
    }

    /**
     * Returns a row of the dBase file as a {@link Feature} instance.
     * 
     * @param rowNo 
     * @return a row of the dBase file as a Feature instance
     * @throws DBaseException 
     */
    public Feature getFRow( int rowNo )
                            throws DBaseException {

        Map<String, Object> columnValues = getRow( rowNo );

        PropertyType[] propTypes = this.ft.getProperties();
        List<FeatureProperty> props = new ArrayList<FeatureProperty>();

        for ( int i = 0; i < propTypes.length; i++ ) {
            PropertyType pt = propTypes[i];
            if ( pt instanceof GeometryPropertyType ) {
                // insert dummy property for geometry
                FeatureProperty prop = FeatureFactory.createFeatureProperty( pt.getName(), null );
                props.add( prop );
            } else {
                String columnName = this.ftMapping.get( pt );
                Object columnValue = columnValues.get( columnName );
                if ( columnValue != null ) {
                    FeatureProperty prop = FeatureFactory.createFeatureProperty( pt.getName(),
                                                                                 columnValue );
                    props.add( prop );
                }
            }
        }
        FeatureProperty[] fp = props.toArray( new FeatureProperty[props.size()] );
        return FeatureFactory.createFeature( ftName + rowNo, ft, fp );
    }

    private Map<String, Object> getRow( int rowNo )
                            throws DBaseException {

        Map<String, Object> columnValues = new HashMap<String, Object>();

        goTop();
        record_number += rowNo;

        for ( int i = 0; i < colHeader.size(); i++ ) {

            // retrieve the dbfCol object which corresponds to this column.
            dbfCol column = (dbfCol) column_info.get( colHeader.get( i ) );

            String value = getColumn( column.name );
            Object columnValue = value;

            if ( value != null ) {
                // cast the value of the i'th column to corresponding datatype
                if ( column.type.equalsIgnoreCase( "C" ) ) {
                    // nothing to do
                } else if ( column.type.equalsIgnoreCase( "F" )
                            || column.type.equalsIgnoreCase( "N" ) ) {
                    try {
                        if ( column.prec == 0 ) {
                            if ( column.size < 10 ) {
                                columnValue = new Integer( value );
                            } else {
                                columnValue = new Long( value );
                            }
                        } else {
                            if ( column.size < 8 ) {
                                columnValue = new Float( value );
                            } else {
                                columnValue = new Double( value );
                            }
                        }
                    } catch ( Exception ex ) {
                        columnValue = new Double( "0" );
                    }
                } else if ( column.type.equalsIgnoreCase( "M" ) ) {
                    // nothing to do
                } else if ( column.type.equalsIgnoreCase( "L" ) ) {
                    // nothing to do
                } else if ( column.type.equalsIgnoreCase( "D" ) ) {
                    if ( value.equals( "" ) ) {
                        columnValue = null;
                    } else {
                        Object oo = TimeTools.createCalendar(
                                                              value.substring( 0, 4 )
                                                                                      + "-"
                                                                                      + value.substring(
                                                                                                         4,
                                                                                                         6 )
                                                                                      + "-"
                                                                                      + value.substring(
                                                                                                         6,
                                                                                                         8 ) ).getTime();
                        columnValue = oo;
                    }
                } else if ( column.type.equalsIgnoreCase( "B" ) ) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream( 10000 );
                    try {
                        os.write( value.getBytes() );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    columnValue = os;
                }
            } else {
                columnValue = "";
            }
            columnValues.put( column.name, columnValue );
        }

        return columnValues;
    }

    /**
     * method: private fixByte (byte b)<BR>
     * bytes are signed; let's fix them...
     */
    private static short fixByte( byte b ) {
        if ( b < 0 ) {
            return (short) ( b + 256 );
        }

        return b;
    }

    /**
     * method: public void writeAllToFile() creates the dbase file and writes all data to it if the
     * file specified by fname (s.o.) exists it will be deleted!
     */
    public void writeAllToFile()
                            throws IOException, DBaseException {
        if ( filemode == 0 ) {
            throw new DBaseException( "class is initialized in read-only mode" );
        }

        // if a file with the retrieved filename exists, delete it!
        File file = new File( fname + ".dbf" );

        if ( file.exists() ) {
            file.delete();
        }

        // create a new file
        RandomAccessFile rdbf = new RandomAccessFile( fname + ".dbf", "rw" );

        try {
            byte[] b = header.getHeader();
            int nRecords = dataSection.getNoOfRecords();
            // write number of records
            ByteUtils.writeLEInt( b, 4, nRecords );
            // write header to the file
            rdbf.write( b );
            b = dataSection.getDataSection();
            // write datasection to the file
            rdbf.write( b );
        } catch ( IOException e ) {
            throw e;
        } finally {
            rdbf.close();
        }
    }

    /**
     * method: public setRecord(ArrayList recData) writes a data record to byte array representing
     * the data section of the dBase file. The method gets the data type of each field in recData
     * from fieldDesc wich has been set at the constructor.
     */
    public void setRecord( ArrayList recData )
                            throws DBaseException {
        if ( filemode == 0 ) {
            throw new DBaseException( "class is initialized in read-only mode" );
        }

        dataSection.setRecord( recData );
    }

    /**
     * method: public setRecord(int index, ArrayList recData) writes a data record to byte array
     * representing the data section of the dBase file. The method gets the data type of each field
     * in recData from fieldDesc wich has been set at the constructor. index specifies the location
     * of the retrieved record in the datasection. if an invalid index is used an exception will be
     * thrown
     */
    public void setRecord( int index, ArrayList recData )
                            throws DBaseException {
        if ( filemode == 0 ) {
            throw new DBaseException( "class is initialized in read-only mode" );
        }

        dataSection.setRecord( index, recData );
    }
} // end of class DBaseFile

/**
 * 
 * 
 * @version $Revision: 1.41 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
class tsColumn {
    public String name = null; // the column's name

    public String table = null; // the table which "owns" the column

    public String type = null; // the column's type

    public int prec = 0; // the column's precision

    public int size = 0; // the column's size

    /**
     * 
     * Constructs a tsColumn object.
     * 
     * @param s
     *            the column name
     */
    tsColumn( String s ) {
        name = s;
    }
} // end of class tsColumn

/**
 * 
 * 
 * @version $Revision: 1.41 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
class dbfCol extends tsColumn {
    int position = 0;

    /**
     * Creates a new dbfCol object.
     * 
     * @param c
     */
    public dbfCol( String c ) {
        super( c );
    }
} // end of class dbfCol

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DBaseFile.java,v $
 Revision 1.41  2006/11/10 23:04:13  poth
 bug fix creating default feature type

 Revision 1.40  2006/08/30 17:02:25  mschneider
 Rewrote handling of mapping to custom feature types (necessary for ShapeDatastore).

 Revision 1.39  2006/08/07 19:44:05  poth
 bug fix - reading dbase header

 Revision 1.38  2006/08/06 20:55:27  poth
 never thrown exception removed

 Revision 1.37  2006/08/06 20:53:07  poth
 never read instance variable removed

 Revision 1.36  2006/08/06 20:52:00  poth
 never read instance variable removed

 Revision 1.35  2006/06/25 20:33:43  poth
 *** empty log message ***

 Revision 1.34  2006/06/01 11:55:19  taddei
 bug fix by ms: createGeometryPropertyType

 Revision 1.33  2006/05/29 16:15:42  poth
 code simplification


 ********************************************************************** */