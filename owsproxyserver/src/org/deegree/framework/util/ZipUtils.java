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
package org.deegree.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;



/**
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class ZipUtils {
    
    private static ILogger LOG = LoggerFactory.getLogger( ZipUtils.class );
    
    private StringBuffer details = null;
    
    /**
     * packs the passed files into a zip-archive, deletes the files if desired 
     * and returns the name of the archive
     *
     * @param dirName name of the directory where to store the archive
     * @param archiveName desired archive name
     * @param fileNames names of the files to be packed into the zip archive
     * @param deleteFiles if true all files will be deleted after zip-file has been
     *          created
     */
    public String doZip( String dirName, String archiveName, String[] fileNames, boolean deleteFiles ) 
                                        throws FileNotFoundException, IOException {

        byte[] b = new byte[512];

        File file = new File( archiveName );
        String archive = archiveName;
        if ( !file.isAbsolute() ) { 
            archive = dirName + "/" + archiveName;
        }
        ZipOutputStream zout = new ZipOutputStream( new FileOutputStream( archive ) );
        
        details = new StringBuffer();

        for ( int i = 0; i < fileNames.length; i++ ) {            
            file = new File( fileNames[i] );
            if ( !file.isAbsolute() ) {
                fileNames[i] = dirName + "/" + fileNames[i];
            }
            InputStream in = new FileInputStream( fileNames[i] );
            ZipEntry e = new ZipEntry( fileNames[i] );
            zout.putNextEntry( e );

            int len = 0;

            while ( ( len = in.read( b ) ) != -1 ) {
                zout.write( b, 0, len );
            }

            in.close();
            zout.closeEntry();
            details.append( createZipDetails( e ) + "\n" );
        }

        if ( deleteFiles ) {
            for ( int i = 0; i < fileNames.length; i++ ) {
                file = new File( fileNames[i] );
                
                LOG.logInfo( fileNames[i] + " deleted: " + file.delete() );
            }
        }

        zout.close();

        return archive;
    }
    
    /**
     * returns details about the zipping
     */
    public String getZipDetails() {
        return details.toString();
    }
    
    
    /**
     * returns some information about the zip process of the current 
     * <code>ZipEntry</code>.
     */
    private StringBuffer createZipDetails( ZipEntry e ) {
        
        StringBuffer sb = new StringBuffer();

        sb.append( "added " + e.getName() );

        if ( e.getMethod() == ZipEntry.DEFLATED ) {
            long size = e.getSize();

            if ( size > 0 ) {
                long csize = e.getCompressedSize();
                long ratio = ( ( size - csize ) * 100 ) / size;
                sb.append( " (deflated " + ratio + "%)" );
            } else {
                sb.append( " (deflated 0%)" );
            }
        } else {
            sb.append( " (stored 0%)" );
        }
        
        return sb;
    }
        
    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ZipUtils.java,v $
Revision 1.2  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.8  2006/07/29 08:50:23  poth
references to deprecated classes removed

Revision 1.7  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
