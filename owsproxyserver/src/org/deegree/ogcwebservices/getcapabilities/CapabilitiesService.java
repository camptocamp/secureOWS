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

import org.deegree.ogcbase.ContactInformation;

/**
 * The interface provides acces to the &lt;CapabilitiesService&gt; element of the  Capabilities XML * providing general metadata for the service as a whole. It shall include a * Name, Title, and Online Resource URL. Optionally, Abstract, Keyword List, * Contact Information, Fees, and Access Constraints may be provided. The meaning * of most of these elements is defined in [ISO 19115]. The CapabilitiesService Name shall * be "ogc:WMS" in the case of a Web Map CapabilitiesService. * <p>----------------------------------------------------------------------</p> * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a> * @version $Revision: 1.10 $
 */

public class CapabilitiesService  {
    
    private ArrayList keywordList = null;
    private ContactInformation contactInformation = null;
    private String abstract_ = null;
    private String accessConstraints = null;
    private String fees = null;
    private String name = null;
    private String title = null;
    private URL onlineResource = null;


    /**
    * constructor initializing the class with the OGCWebServiceCapabilities
    */
    public CapabilitiesService( String name, String title, String abstract_, String[] keywords, 
                         URL onlineResource, ContactInformation contactInformation, String fees, 
                         String accessConstraints ) {
        keywordList = new ArrayList();
        setName( name );
        setTitle( title );
        setAbstract( abstract_ );
        setKeywordList( keywords );
        setOnlineResource( onlineResource );
        setContactInformation( contactInformation );
        setFees( fees );
        setAccessConstraints( accessConstraints );
    }

    /**
     * returns the name of the service. Typically, the Name is a single word used
     * for machine-to-machine communication.
     * @return name of the service
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the service. Typically, the Name is a single word used
     * for machine-to-machine communication.
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the title of the service. The Title is for the benefit of humans.
     * The CapabilitiesService Title is at the discretion of the provider, and should be
     * brief yet descriptive enough to identify this server in a menu with other
     * servers.
     * @see #getName()
     * @return title of the service
     * 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the service. The Title is for the benefit of humans.
     * The CapabilitiesService Title is at the discretion of the provider, and should be
     * brief yet descriptive enough to identify this server in a menu with other
     * servers.
     * @see #getName()
     * 
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * The Abstract element allows a descriptive narrative providing more
     * information about the enclosing object.
     */
    public String getAbstract() {
        return abstract_;
    }

    /**
    * Sets the abstract element
    */
    public void setAbstract( String abstract_ ) {
        this.abstract_ = abstract_;
    }

    /**
     * A list of keywords or keyword phrases should be included to help catalog
     * searching. Currently, no controlled vocabulary has been defined.
     * 
     */
    public String[] getKeywordList() {
        return (String[]) keywordList.toArray(new String[keywordList.size()]);
    }


    /**
    * adds the keywordList
    */
    public void addKeyword( String keyword ) {
        this.keywordList.add( keyword );
    }

    /**
    * sets the keywordList
    */
    public void setKeywordList( String[] keywordList ) {
        this.keywordList.clear();

        if ( keywordList != null ) {
            for ( int i = 0; i < keywordList.length; i++ ) {
                this.keywordList.add( keywordList[i] );
            }
        }
    }

    /**
     * The OnlineResource element within the CapabilitiesService element can be used, for
     * example, to point to the web site of the service provider. There are other
     * OnlineResource elements used for the URL prefix of each supported operation.
     * 
     */
    public URL getOnlineResource() {
        return onlineResource;
    }

    /**
     * sets URL prefix for get HTTP request method.
     * 
     */
    public void setOnlineResource(URL onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * Returns informations who to contact for questions about the service. This
     * method returns <tt>null</tt> if no contact informations are available.
     * 
     */
    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets informations who to contact for questions about the service. This
     * method returns <tt>null</tt> if no contact informations are available.
     * 
     */
    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Returns fees assigned to the service. If no fees defined "none" will be
     * returned.
     * 
     */
    public String getFees() {
        return fees;
    }

    /**
     * Sets fees assigned to the service. If no fees defined "none" will be
     * returned.
     * 
     */
    public void setFees(String fees) {
        this.fees = fees;
    }

    /**
     * Returns access constraints assigned to the service. If no access
     * constraints are defined "none" will be returned.
     * 
     */
    public String getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * Sets access constraints assigned to the service. If no access
     * constraints are defined "none" will be returned.
     * 
     */
    public void setAccessConstraints(String accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

      
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CapabilitiesService.java,v $
Revision 1.10  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
