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
import java.net.URL;

/**
 * Linkage
 * 
 * @author <a href="mailto:schaefer@lat-lon.de">Axel Schaefer </a>
 * @version $Revision: 1.9 $ $Date: 2006/07/12 14:46:18 $
 */
public class Linkage implements Serializable {

    public static final String SIMPLE = "simple";

    private URL href = null;
    private String type = null;

    /** 
     * Creates a default instance of Linkage with default
     * xlink namespace.
     * 
     * @see org.deegree.ogcbase.CommonNamespace#XLNNS 
     */
    public Linkage() {
        this.type = SIMPLE;
    }
    
    /**
     * 
     * @param href
     */
    public Linkage(URL href) {
        this.setHref(href);
    }
    

    /** 
     * Creates a new instance of Linkage
     *  
     */
    public Linkage(URL href, String type) {
        setHref(href);
        setType(type);
    }

    /**
     * use="required"
     * 
     * @return the href-attribute
     * 
     */
    public URL getHref() {
        return href;
    }

    /**
     * @see #getHref()
     * 
     */
    public void setHref(URL href) {
        this.href = href;
    }

    /**
     * fixed="simple"
     * 
     * @return the type-attribute
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * @see #getType()
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

      /**
     * to String method
     */
    public String toString() {
        String ret = null;
        ret = "href = " + href + "\n";
        ret += "type = " + type + "\n";
        return ret;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Linkage.java,v $
Revision 1.9  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
