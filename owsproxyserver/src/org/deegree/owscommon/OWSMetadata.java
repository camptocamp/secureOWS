//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon/OWSMetadata.java,v 1.5 2006/08/24 06:42:48 poth Exp $
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

package org.deegree.owscommon;

import java.net.URI;

import org.deegree.datatypes.xlink.SimpleLink;

/**
 * Class representation of an <code>ows:Metadata</code> -Element as defined in
 * <code>owsOperationsMetadata.xsd</code> from the
 * <code>OWS Common Implementation
 * Specification 0.3</code>.
 * <p>
 * This element either references or contains more metadata about the element
 * that includes this element. Either at least one of the attributes in
 * xlink:simpleLink or a substitute for the _MetaData element shall be included,
 * but not both. An Implementation Specification can restrict the contents of
 * this element to always be a reference or always contain metadata.
 * (Informative: This element was adapted from the metaDataProperty element in
 * GML 3.0.)
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/08/24 06:42:48 $
 * 
 * @since 2.0
 */
public class OWSMetadata  {

	private URI about;
    // an ows:Metadata - Element has the same attributes as a SimpleLink
    private SimpleLink link;
    private String name;
    

    public OWSMetadata ( URI about, SimpleLink link, String name ) {
        this.about = about;
		this.link = link;
		this.name = name;
    }
    

	/**
	 * @return Returns the about.
	 */
	public URI getAbout() {
		return about;
	}

	/**
	 * @return Returns the link.
	 */
	public SimpleLink getLink() {
		return link;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSMetadata.java,v $
Revision 1.5  2006/08/24 06:42:48  poth
File header corrected

Revision 1.4  2006/04/06 20:25:31  poth
*** empty log message ***

Revision 1.3  2006/03/30 21:20:28  poth
*** empty log message ***

Revision 1.2  2005/12/20 10:01:50  mays
change Object metadata to String name

******************************************************************** */
