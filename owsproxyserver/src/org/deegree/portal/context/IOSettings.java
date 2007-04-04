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
package org.deegree.portal.context;




/**
 * Implements the access to the IO settings of a Web Map Context.
 *
 * @version $Revision: 1.9 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class IOSettings {
	
	private DirectoryAccess downloadDirectory = null;
	private DirectoryAccess sLDDirectory = null;
	private DirectoryAccess printDirectory = null;
    private DirectoryAccess tempDirectory = null;
		
	/**
	 * @param downloadDirectory directory to store temoprary files for 
	 * 							downloading
	 * @param sLDDirectory directory for storing temporary SLD files 
	 * @param printDirectory directory for storing temporary for
	 * 						 print-views
	 */
	IOSettings(DirectoryAccess downloadDirectory, DirectoryAccess sLDDirectory, 
               DirectoryAccess printDirectory, DirectoryAccess tempDirectory) {
		this.downloadDirectory = downloadDirectory;
		this.sLDDirectory = sLDDirectory;
		this.printDirectory = printDirectory;
        this.tempDirectory = tempDirectory;
	}
	/**
	 * @return Returns the downloadDirectory.
	 */
	public DirectoryAccess getDownloadDirectory() {
		return downloadDirectory;
	}

	/**
	 * @param downloadDirectory The downloadDirectory to set.
	 */
	public void setDownloadDirectory(DirectoryAccess downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
	}
 
	/**
	 * @return Returns the printDirectory.
	 */
	public DirectoryAccess getPrintDirectory() {
		return printDirectory;
	}

	/**
	 * @param printDirectory The printDirectory to set.
	 */
	public void setPrintDirectory(DirectoryAccess printDirectory) {
		this.printDirectory = printDirectory;
	}

	/**
	 * @return Returns the sLDDirectory.
	 */
	public DirectoryAccess getSLDDirectory() {
		return sLDDirectory;
	}

	/**
	 * @param directory The sLDDirectory to set.
	 */
	public void setSLDDirectory(DirectoryAccess directory) {
		sLDDirectory = directory;
	}
 
    /**
     * @return Returns the tempDirectory.
     */
    public DirectoryAccess getTempDirectory() {
        return tempDirectory;
    }
     
    /**
     * @param tempDirectory The tempDirectory to set.
     */
    public void setTempDirectory(DirectoryAccess tempDirectory) {
        this.tempDirectory = tempDirectory;
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: IOSettings.java,v $
Revision 1.9  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
