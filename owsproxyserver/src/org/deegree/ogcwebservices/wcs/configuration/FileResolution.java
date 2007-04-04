// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/FileResolution.java,v 1.8 2006/07/12 14:46:19 poth Exp $
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
package org.deegree.ogcwebservices.wcs.configuration;

import java.util.Arrays;
import java.util.List;

/**
 * models a <tt>Resolution</tt> that describes the access to the coverages using named files (with
 * a defined size if it's a grid coverage).
 * 
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/07/12 14:46:19 $
 * 
 * @since 2.0
 */
public class FileResolution extends AbstractResolution {

    private List files = null;

    /**
     * @param minScale
     * @param maxScale
     * @param range
     * @throws IllegalArgumentException
     */
    public FileResolution( double minScale, double maxScale, Range[] range, File[] files )
        throws IllegalArgumentException {
        super( minScale, maxScale, range );
        setFiles( files );
    }

    /**
     * sets all file description contained in the <tt>Resolution</tt>
     * 
     * @param files
     */
    public void setFiles( File[] files ) {
        this.files = Arrays.asList( files );
    }

    /**
     * returns all file description contained in the <tt>Resolution</tt>
     * 
     * @return
     *  
     * @uml.property name="files"
     */
    public File[] getFiles() {
        return (File[]) files.toArray( new File[files.size()] );
    }

    /**
     * adds a files descrition to the <tt>Resolution</tt>
     * 
     * @param file
     */
    public void addFile( File file ) {
        files.add( file );
    }

    /**
     * removes a files descrition from the <tt>Resolution</tt>
     * 
     * @param file
     */
    public void removeFile( File file ) {
        files.remove( file );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FileResolution.java,v $
Revision 1.8  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
