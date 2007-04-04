//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/datatypes/xlink/SimpleLink.java,v 1.6 2006/07/12 14:46:19 poth Exp $
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
package org.deegree.datatypes.xlink;

import java.net.URI;

/**
 * Class representation of an <code>xlink:simpleLink</code> according to the
 * <code>xlinks.xsd</code> provided with the
 * <code>OWS Common Specification 0.3</code>.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 */
public class SimpleLink {

    public final static String type = "simple";

    private URI href;

    private URI role;

    private URI arcrole;

    private String title;

    /** valid values: 'new', 'replace', 'embed', 'other' or 'none' */
    private String show;

    /** valid values: 'onLoad', 'onRequest', 'other' or 'none' */
    private String actuate;

    /**
     * Creates a new <code>SimpleLink</code> instance with the minimum of
     * neeeded information.
     * 
     * @param href
     *            may even be null (!)
     */
    public SimpleLink(URI href) {
        this.href = href;
    }

    /**
     * Creates a new <code>SimpleLink</code> instance with the minimum of
     * neeeded information.
     * 
     * @param href
     *            may even be null (!)
     * @param role
     *            may be null
     * @param arcrole
     *            may be null
     * @param title
     *            may be null
     * @param show
     *            valid values: 'new', 'replace', 'embed', 'other', 'none' (or
     *            null)
     * @param actuate
     *            valid values: 'onLoad', 'onRequest', 'other', 'none' (or null)
     */
    public SimpleLink(URI href, URI role, URI arcrole, String title,
            String show, String actuate) {
        this.href = href;
        this.role = role;
        this.arcrole = arcrole;
        this.title = title;
        this.show = show;
        this.actuate = actuate;
    }
    
    
    /**
     * @return Returns the type.
     */
    public static String getType() {
        return type;
    }
    /**
     * @return Returns the actuate.
     */
    public String getActuate() {
        return actuate;
    }
    /**
     * @param actuate The actuate to set.
     */
    public void setActuate(String actuate) {
        this.actuate = actuate;
    }
    /**
     * @return Returns the arcrole.
     */
    public URI getArcrole() {
        return arcrole;
    }
    /**
     * @param arcrole The arcrole to set.
     */
    public void setArcrole(URI arcrole) {
        this.arcrole = arcrole;
    }
    /**
     * @return Returns the href.
     */
    public URI getHref() {
        return href;
    }
    /**
     * @param href The href to set.
     */
    public void setHref(URI href) {
        this.href = href;
    }
    /**
     * @return Returns the role.
     */
    public URI getRole() {
        return role;
    }
    /**
     * @param role The role to set.
     */
    public void setRole(URI role) {
        this.role = role;
    }
    /**
     * @return Returns the show.
     */
    public String getShow() {
        return show;
    }
    /**
     * @param show The show to set.
     */
    public void setShow(String show) {
        this.show = show;
    }
    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SimpleLink.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
