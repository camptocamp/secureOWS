//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/capabilities/Style.java,v 1.9 2006/08/24 06:42:15 poth Exp $
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

package org.deegree.ogcwebservices.wpvs.capabilities;

import org.deegree.model.metadata.iso19115.Keywords;

/**
 * This class represents a style object.
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.9 $, $Date: 2006/08/24 06:42:15 $
 * 
 * @since 2.0
 */
public class Style {

    private String name;
    
	private String title;
	
	private String abstract_;
	
	private Keywords[] keywords;
	
	private Identifier identifier;
	
	private LegendURL[] legendURLs;
	
	private StyleSheetURL styleSheetURL;
	
	private StyleURL styleURL;

	/**
	 * Creates a new style object from the given parameters.
	 * 
	 * @param name
	 * @param title
	 * @param abstract_
	 * @param keywords
	 * @param identifier
	 * @param legendURLs
	 * @param styleSheetURL
	 * @param styleURL
	 */
	public Style( String name, String title, String abstract_, Keywords[] keywords, 
				Identifier identifier, LegendURL[] legendURLs, StyleSheetURL styleSheetURL, 
				StyleURL styleURL ) {
        this.name = name;
		this.title = title;
		this.abstract_ = abstract_;
		this.keywords = keywords;
		this.identifier = identifier;	
		this.legendURLs = legendURLs;
		this.styleSheetURL = styleSheetURL;
		this.styleURL = styleURL;
    }

	/**
	 * @return Returns the abstract_.
	 */
	public String getAbstract() {
		return abstract_;
	}

	/**
	 * @return Returns the identifier.
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * @return Returns the keywords.
	 */
	public Keywords[] getKeywords() {
		return keywords;
	}

	/**
	 * @return Returns the legendURLs.
	 */
	public LegendURL[] getLegendURLs() {
		return legendURLs;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the styleSheetURL.
	 */
	public StyleSheetURL getStyleSheetURL() {
		return styleSheetURL;
	}

	/**
	 * @return Returns the styleURL.
	 */
	public StyleURL getStyleURL() {
		return styleURL;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Style.java,v $
Revision 1.9  2006/08/24 06:42:15  poth
File header corrected

Revision 1.8  2006/04/06 20:25:25  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:26  poth
*** empty log message ***

Revision 1.6  2005/12/05 09:36:38  mays
revision of comments

Revision 1.5  2005/12/02 15:26:59  mays
changed legendURL to array of LegendURLs

Revision 1.4  2005/12/01 10:30:14  mays
add standard footer to all java classes in wpvs package

******************************************************************** */
