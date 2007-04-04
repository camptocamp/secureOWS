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
package org.deegree.ogcwebservices.getcapabilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The HTTP-Protocol, which extends the super-interface Protocol.java. It
 * defines the GetOnlineResource and the getPostOnlineResource methods.
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.7 $ $Date: 2006/07/12 14:46:16 $
 */
public class HTTP extends Protocol {

    private List getOnlineResource = null;

    private List postOnlineResource = null;

    /**
     * default constructor
     */
    public HTTP() {
        getOnlineResource = new ArrayList();
        postOnlineResource = new ArrayList();
    }

    /**
     * constructor initializing the class with get and post URL
     */
    public HTTP(URL[] getOnlineResource, URL[] postOnlineResource) {
        this();

        setGetOnlineResources(getOnlineResource);
        setPostOnlineResources(postOnlineResource);
    }

    /**
     * Return the list of online resources for the HTTP GET request. An Online
     * Resource URL intended for HTTP GET requests, is a URL prefix to which
     * additional parameters must be appended in order to construct a valid
     * Operation request. A URL prefix is defined as an opaque string including
     * the protocol, hostname, optional port number, path, a question mark '?',
     * and, optionally, one or more server-specific parameters ending in an
     * ampersand '&'.
     *  
     */
    public URL[] getGetOnlineResources() {
        return (URL[]) getOnlineResource.toArray(new URL[getOnlineResource
                .size()]);
    }

    /**
     * adds a get-online resource to the HTTP protocol class
     */
    public void addGetOnlineResource(URL getOnlineResource) {
        this.getOnlineResource.add(getOnlineResource);
    }

    /**
     *  
     */
    public void setGetOnlineResources(URL[] getOnlineResource) {
        this.getOnlineResource.clear();

        if (getOnlineResource != null) {
            for (int i = 0; i < getOnlineResource.length; i++) {
                addGetOnlineResource(getOnlineResource[i]);
            }
        }
    }

    /**
     * Return the list of online resources for the HTTP GET request. An Online
     * Resource URL intended for HTTP POST requests is a complete and valid URL
     * to which Clients transmit encoded requests in the body of the POST
     * document.
     */
    public URL[] getPostOnlineResources() {
        return (URL[]) postOnlineResource.toArray(new URL[postOnlineResource.size()]);
    }

    /**
     * sets URL prefix for post HTTP request method.
     */
    public void setPostOnlineResources(URL[] postOnlineResource) {
        this.postOnlineResource.clear();

        if (postOnlineResource != null) {
            for (int i = 0; i < postOnlineResource.length; i++) {
                addPostOnlineResource(postOnlineResource[i]);
            }
        }
    }

    /**
     * adds a post-online resource to the HTTP protocol class
     */
    public void addPostOnlineResource(URL postOnlineResource) {
        this.postOnlineResource.add(postOnlineResource);
    }

  
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: HTTP.java,v $
Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
