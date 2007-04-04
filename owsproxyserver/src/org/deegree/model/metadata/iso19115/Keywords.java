/*
 * ---------------- FILE HEADER ------------------------------------------
 * 
 * This file is part of deegree. Copyright (C) 2001-2006 by: EXSE, Department of
 * Geography, University of Bonn http://www.giub.uni-bonn.de/deegree/ lat/lon
 * Fitzke/Fretter/Poth GbR http://www.lat-lon.de
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact:
 * 
 * Andreas Poth lat/lon GmbH Aennchenstr. 19 53115
 * Bonn Germany E-Mail: poth@lat-lon.de
 * 
 * Prof. Dr. Klaus Greve Department of Geography University of Bonn Meckenheimer Allee 166
 * 53115 Bonn Germany E-Mail: greve@giub.uni-bonn.de
 * 
 * 
 * ---------------------------------------------------------------------------
 */

package org.deegree.model.metadata.iso19115;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Keywords.java *  * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </a> * @version $Revision: 1.8 $ $Date: 2006/08/01 11:46:07 $
 */

public class Keywords implements Serializable {

    private static final long serialVersionUID = -2140118359320160159L;
    private ArrayList keywords;
    private String thesaurusname;
    private TypeCode typecode;

    
    private Keywords() {
        this.keywords = new ArrayList();
    }
    
    public Keywords(String[] keywords) {
        this();
        this.setKeywords(keywords);
    }

    /** Creates a new instance of Keywords */
    public Keywords(String[] keywords, String thesaurusname, TypeCode typecode) {
        this(keywords);
        this.setThesaurusName(thesaurusname);
        this.setTypeCode(typecode);
    }

    /**
     * minOccurs="0" maxOccurs="unbounded"
     * @return
     * 
     * @uml.property name="keywords"
     */
    public String[] getKeywords() {
        return (String[]) keywords.toArray(new String[keywords.size()]);
    }

    /**
     * @see #getKeywords()
     */
    public void addKeyword(String keyword) {
        this.keywords.add(keyword);
    }

    /**
     * @see #getKeywords()
     */
    public void setKeywords(String[] keywords) {
        this.keywords.clear();
        for (int i = 0; i < keywords.length; i++) {
            this.keywords.add(keywords[i]);
        }
    }

    /**
     * minOccurs="0"
     *  
     */
    public String getThesaurusName() {
        return thesaurusname;
    }

    /**
     * @see #getThesaurusName()
     */
    public void setThesaurusName(String thesaurusname) {
        this.thesaurusname = thesaurusname;
    }

    /**
     * minOccurs="0"
     *  
     */
    public TypeCode getTypeCode() {
        return typecode;
    }

    /**
     * @see #getTypeCode()
     */
    public void setTypeCode(TypeCode typecode) {
        this.typecode = typecode;
    }

    /**
     * to String method
     */
    public String toString() {
        String ret = null;
        ret = "keywords = " + keywords + "\n";
        ret += "thesaurusname = " + thesaurusname + "\n";
        ret += "typecode = " + typecode + "\n";
        return ret;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Keywords.java,v $
Revision 1.8  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.

Revision 1.7  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
