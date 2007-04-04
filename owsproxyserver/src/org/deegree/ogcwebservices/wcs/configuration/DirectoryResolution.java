// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/DirectoryResolution.java,v 1.6 2006/07/12 14:46:19 poth Exp $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.deegree.model.spatialschema.Envelope;

/**
 * models a <tt>Resolution</tt> by describing the access to the assigned 
 * coverages through named directories containing a well defined collection
 * of coverages.
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/12 14:46:19 $
 *
 * @since 2.0
 */
public class DirectoryResolution extends AbstractResolution {

    private List directories = null;   

    /**
     * @param minScale
     * @param maxScale
     * @param range
     * @throws IllegalArgumentException
     */
    public DirectoryResolution(double minScale, double maxScale, Range[] range,
                                Directory[] directories) 
                               throws IllegalArgumentException {
        super(minScale, maxScale, range);
        setDirectories(directories);
    }

    /**
     * @return Returns the directories.
     * 
     * @uml.property name="directories"
     */
    public Directory[] getDirectories() {
        return (Directory[]) directories.toArray(new Directory[directories
            .size()]);
    }

    
    /**
     * returns the <tt>Directories</tt> of a <tt>Resolution</tt> that intersects
     * with the passed <tt>Envelope</tt> 
     * @return Returns the directories.
     */
    public Directory[] getDirectories(Envelope envelope) {
    	List list = new ArrayList( directories.size() );    	
    	for (Iterator iter = directories.iterator(); iter.hasNext();) {
			Directory dir = (Directory) iter.next();
			if ( dir.getEnvelope().intersects(envelope) ) {
				list.add( dir );
			}
		}
    	Directory[] dirs = new Directory[list.size()];
        return (Directory[])list.toArray( dirs );
    }

    /**
     * @param directories The directories to set.
     */
    public void setDirectories(Directory[] directories) {
        this.directories = Arrays.asList( directories );
    }
    
    /**
     * adds a <tt>Directory</tt> to the <tt>Resolution</tt>
     * @param directory
     */
    public void addDirectory(Directory directory) {
        directories.add( directory );
    }
    
    /**
     * removes a <tt>Directory</tt> from the <tt>Resolution</tt>
     * @param directory
     */
    public void removeDirectory(Directory directory) {
        directories.remove( directory );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DirectoryResolution.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
