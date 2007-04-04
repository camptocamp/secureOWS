// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/capabilities/GMLObject.java,v 1.6 2006/07/12 14:46:15 poth Exp $
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
package org.deegree.ogcwebservices.wfs.capabilities;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.metadata.iso19115.Keywords;

public class GMLObject {

    private QualifiedName name;

    private String title;

    private String abstract_;

    private Keywords[] keywords;

    private FormatType[] outputFormats;

    public GMLObject(QualifiedName name, String title, String abstract_,
            Keywords[] keywords, FormatType[] outputFormats) {
        this.name = name;
        this.title = title;
        this.abstract_ = abstract_;
        this.keywords = keywords;
        this.outputFormats = outputFormats;
    }

    /**
     * @return Returns the abstract.
     */
    public String getAbstract() {
        return abstract_;
    }

    /**
     * @param abstract_
     *            The abstract to set.
     */
    public void setAbstract(String abstract_) {
        this.abstract_ = abstract_;
    }

    /**
     * @return Returns the keywords.
     */
    public Keywords[] getKeywords() {
        return keywords;
    }

    /**
     * @param keywords
     *            The keywords to set.
     */
    public void setKeywords(Keywords[] keywords) {
        this.keywords = keywords;
    }

    /**
     * @return Returns the name.
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(QualifiedName name) {
        this.name = name;
    }

    /**
     * @return Returns the outputFormats.
     */
    public FormatType[] getOutputFormats() {
        return outputFormats;
    }

    /**
     * @param outputFormats
     *            The outputFormats to set.
     */
    public void setOutputFormats(FormatType[] outputFormats) {
        this.outputFormats = outputFormats;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GMLObject.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
