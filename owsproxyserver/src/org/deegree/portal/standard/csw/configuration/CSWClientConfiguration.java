//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/configuration/CSWClientConfiguration.java,v 1.12 2006/08/07 09:48:47 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de 

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

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

package org.deegree.portal.standard.csw.configuration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.framework.util.StringTools;
import org.deegree.model.spatialschema.Envelope;
 
/**
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/08/07 09:48:47 $
 * 
 * @since 2.0
 */
public class CSWClientConfiguration {
    
    private int maxRecords = 10;
    
//    private String briefXSL;    
//    private String summaryXSL;    
//    private String fullXSL;
    
    /**
     * key<String> = catalog name;
     * value<String> = catalog address
     */
    private Map<String, String> catalogToUrlMap;

    /**
     * key<String> = catalog name;
     * value<List<String>> = list of supported protocols ( e.g. POST, SOAP ) 
     */
    private Map<String, List<String>> catalogToProtocolMap;

    /**
     * key<String> = catalog name;
     * value<List<String>> = list of supported formats ( e.g. ISO19115, ISO19119, OGCCORE )
     */
    private Map<String, List<String>> catalogToFormatMap;

    /**
     * key<String> = profile name ( e.g. Profile.ISO19115, Profile.OGCCORE, etc. );
     * value<HashMap<String,String>> = key: e.g. brief, summary, full; value: e.g. metaList2html.xsl
     */
    private Map<String, HashMap<String, String>> profilesToXsl; 
    
    private String srs;
    
    private String xPathToDataIdentifier;
    private String xPathToDataTitle;
    private String xPathToDataTitleFull;

    private String xPathToServiceIdentifier;
    private String xPathToServiceTitle;
    private String xPathToServiceOperatesOnTitle;
    private String xPathToServiceAddress;
    private String xPathToServiceType;
    private String xPathToServiceTypeVersion;

    private String[] namespaceBindings;
    
    private String mapContextTemplatePath;
    
    /* ignorig for now
    private HashMap thesauri = null;
    private Download download = null;    
    private CCatalog[] ccatalog = null;
    private int maxInactiveInterval = 0;
    private String[] filterIDs = null;
    private int queryScope = 0;
    private GM_Envelope rootBoundingBox = null;
    private WMSClientConfiguration wmsCConfig = null; // seems to be not needed
    */
    
    public CSWClientConfiguration() {
        catalogToUrlMap = new HashMap<String, String>( 5 );
        profilesToXsl = new HashMap<String, HashMap<String, String>>( 5 );
        catalogToProtocolMap = new HashMap<String, List<String>>( 5 );
        catalogToFormatMap = new HashMap<String, List<String>>( 5 );
    }

    /**
     * @return Returns the names of all served catalogs 
     */
    public String[] getCatalogNames() {
        Set<String> keySet = catalogToUrlMap.keySet();
        return keySet.toArray( new String[ keySet.size() ] );
    }

    /**
     * @return Returns the addresses of all served catalogs 
     */
    public String[] getCatalogServerAddresses() {
        String[] addresses = new String[ catalogToUrlMap.size() ];
        Iterator it = catalogToUrlMap.values().iterator();
        for( int i = 0; i < addresses.length && it.hasNext(); i++ ) {
            addresses[i] = (String)it.next();  
        }
        return addresses;
    }

    /**
     * @param catalogName 
     * @return Returns the address of the submitted catalog, or <tt>null</tt> 
     *         if the map contains no mapping for this key. 
     */
    public String getCatalogServerAddress( String catalogName ) {
        return catalogToUrlMap.get( catalogName );
    }
    
    /**
     * @return Returns the catalogToFormatMap.
     */
    public Map getCatalogToFormatMap() {
        return catalogToFormatMap;
    }
    
    /**
     * @param catalogName The name of the catalog
     * @return  Returns the format types of a given catalog, e.g. "ISO19115" or "ISO19119". 
     *          May be null, if the passed catalogName is unknown.
     */
    public List getCatalogFormats( String catalogName ) {
        return catalogToFormatMap.get( catalogName );
    }
    
    /**
     * @return Returns the catalogToProtocolMap.
     */
    public Map getCatalogToProtocolMap() {
        return catalogToProtocolMap;
    }
    
    /**
     * @param catalogName The name of the catalog
     * @return Returns the protocol type of a given catalog, e.g. "POST" or "SOAP". 
     *         May be null, if the passed catalogName is unknown.
     */
    public List getCatalogProtocols( String catalogName ) {
        return catalogToProtocolMap.get( catalogName );
    }
    
    /**
     * @return Returns the catalogToUrlMap.
     */
    public Map getCatalogToUrlMap() {
        return catalogToUrlMap;
    }    
    
    /**
     * 
     * @return Returns a List of the service metadata catalogues.
     */
    public List<String> getServiceMetadataCatalogs() {
        List<String> serviceCatalogs = new ArrayList<String>( 10 ); 
        
        Iterator it = catalogToFormatMap.keySet().iterator(); 
        while ( it.hasNext() ) {
            String catalog = (String) it.next();
            List formats = getCatalogFormats( catalog );
            String smf = ServiceMetadataFormats.getString( "CSWClientConfiguration.serviceMetadata" );  
            String[] s = StringTools.toArray( smf, ",", true );
            List<String> list = new ArrayList<String>( Arrays.asList( s ) );
            for (int i = 0; i < formats.size(); i++) {
                if ( list.contains( formats.get( i ) ) ) { 
                    serviceCatalogs.add( catalog ); 
                    break;
                }
            }
        } 

        return serviceCatalogs;
    }

    /**
     * @return Returns an array of IDs that marks UDK objects that are valid for the catalog 
     */
    public String[] getFilterIDs() {
        throw new UnsupportedOperationException( "getFilterIDs() is not implemented yet" ); 
    }
    
    /**
     * @param catalogField 
     * @return Returns the catalog (iso-) elements that shall be targeted by a html form 
     * element
     */
    public String[] getFormElements( String catalogField ) {
        throw new UnsupportedOperationException( "getFormElements( String ) is not implemented yet" ); 
    }
    
    /**
     * @return Returns the mapContextTemplatePath.
     */
    public String getMapContextTemplatePath() {
        return mapContextTemplatePath;
    }
    
    /**
     * @return Returns the maximum number records requested in a GetRecord request. 
     */
    public int getMaxRecords() {
        return maxRecords;
    }
    
    /**
     * @return Returns the maximun time a session will be alive after the last change
     * in seconds
     */
    public int getMaxSessionLifeTime() {
        return 3600;//maxInactiveInterval;
    } 

    /**
     * @return Returns the namespace bindings as String array.
     */
    public String[] getNamespaceBindings() {
        return namespaceBindings;
    }
    
    /**
     * @return Returns the profilesToXsl.
     */
    public Map getProfilesToXsl() {
        return profilesToXsl;
    }

    /**
     * @param profileName
     * @return Returns the profiles to xsl for the passed profile or
     *         <tt>null</tt> if the profile map contains no mapping for this key.
     */
    public HashMap getProfileXSL( String profileName ) {
        return this.profilesToXsl.get( profileName );
    }
   
    /**
     * @return Returns the names of possible protocol types 
     */
    public String[] getProtocolNames() {
        Set<String> keySet = catalogToProtocolMap.keySet(); 
        return keySet.toArray( new String[ keySet.size() ] );
    }   
    
    /**
     * @return Returns the maximum number (iterations) to cascaded catalogs that shall be performed. 
     */
    public int getQueryScope() {
        return 9999;//queryScope;
    }
    
    /**
     * @return Returns the bounding box of the maximum area that shall be searched for (meta)data. 
     *         This parameter will be extracted from the searchmap parameter 
     */
    public Envelope getRootBoundingBox() {
        throw new UnsupportedOperationException( "getRootBoundingBox() is not implemented yet" ); 
//        return rootBoundingBox;
    }
    
    /**
     * @return Returns the srs.
     */
    public String getSrs() {
        return srs;
    }
    
    /**
     * @param thesaurus 
     * @return Returns the address of the submitted thesaurus 
     */
    public URL getThesaurusAddress( String thesaurus ) {
        throw new UnsupportedOperationException( "getThesaurusAddress() is not implemented yet"); 
//        return (URL)thesauri.get( thesaurus );
    }

    /**
     * @return Returns the names of the thesauri known by the client 
     */
    public String[] getThesaurusNames() {
        throw new UnsupportedOperationException( "getThesaurusNames() is not implemented yet" ); 
//        String[] tn = new String[thesauri.size()];
//        return (String[])thesauri.keySet().toArray( tn );
    }
        
    /**
     * @return Returns the xPathToDataIdentifier.
     */
    public String getXPathToDataIdentifier() {
        return xPathToDataIdentifier;
    }

    /**
     * @return Returns the xPathToDataTitle.
     */
    public String getXPathToDataTitle() {
        return xPathToDataTitle;
    }

    /**
     * @return Returns the xPathToDataTitleFull.
     */
    public String getXPathToDataTitleFull() {
        return xPathToDataTitleFull;
    }

    /**
     * @return Returns the xPathToServiceAddress.
     */
    public String getXPathToServiceAddress() {
        return xPathToServiceAddress;
    }

    /**
     * @return Returns the xPathToServiceIdentifier.
     */
    public String getXPathToServiceIdentifier() {
        return xPathToServiceIdentifier;
    }

    /**
     * @return Returns the xPathToServiceOperatesOnTitle.
     */
    public String getXPathToServiceOperatesOnTitle() {
        return xPathToServiceOperatesOnTitle;
    }

    /**
     * @return Returns the xPathToServiceTitle.
     */
    public String getXPathToServiceTitle() {
        return xPathToServiceTitle;
    }

    /**
     * @return Returns the xPathToServiceType.
     */
    public String getXPathToServiceType() {
        return xPathToServiceType;
    }

    /**
     * @return Returns the xPathToServiceTypeVersion.
     */
    public String getXPathToServiceTypeVersion() {
        return xPathToServiceTypeVersion;
    }

    // ###########################################################
    

    /**
     * @param mapContextTemplatePath The mapContextTemplatePath to set.
     */
    public void setMapContextTemplatePath( String mapContextTemplatePath ) {
        this.mapContextTemplatePath = mapContextTemplatePath;
    }
    
    /**
     * @param maxRecords
     */
    public void setMaxRecords( int maxRecords ) {
        this.maxRecords = maxRecords;
    }

    /**
     * @param namespaceBindings The namespaceBindings to set.
     */
    public void setNamespaceBindings( String[] namespaceBindings ) {
        this.namespaceBindings = namespaceBindings;
    }

    /**
     * @param srs The srs to set.
     */
    public void setSrs(String srs) {
        this.srs = srs;        
    }
    
    /**
     * @param pathToIdentifier The xPath to the FileIdentifier to set.
     */
    public void setXPathToDataIdentifier( String pathToIdentifier ) {
        this.xPathToDataIdentifier = pathToIdentifier;
    } 

    /**
     * @param pathToDataTitle The xPathToDataTitle to set.
     */
    public void setXPathToDataTitle( String pathToDataTitle ) {
        xPathToDataTitle = pathToDataTitle;
    }

    /**
     * @param pathToDataTitleFull The xPathToDataTitleFull to set.
     */
    public void setXPathToDataTitleFull( String pathToDataTitleFull ) {
        xPathToDataTitleFull = pathToDataTitleFull;
    }
    
    /**
     * @param pathToServiceAddress The xPathToServiceAddress to set.
     */
    public void setXPathToServiceAddress( String pathToServiceAddress ) {
        xPathToServiceAddress = pathToServiceAddress;
    }

    /**
     * @param pathToServiceIdentifier The xPathToServiceIdentifier to set.
     */
    public void setXPathToServiceIdentifier( String pathToServiceIdentifier ) {
        xPathToServiceIdentifier = pathToServiceIdentifier;
    }

    /**
     * @param pathToServiceOperatesOnTitle The xPathToServiceOperatesOnTitle to set.
     */
    public void setXPathToServiceOperatesOnTitle( String pathToServiceOperatesOnTitle ) {
        xPathToServiceOperatesOnTitle = pathToServiceOperatesOnTitle;
    }

    /**
     * @param pathToServiceTitle The xPathToServiceTitle to set.
     */
    public void setXPathToServiceTitle( String pathToServiceTitle ) {
        xPathToServiceTitle = pathToServiceTitle;
    }

    /**
     * @param pathToServiceType The xPathToServiceType to set.
     */
    public void setXPathToServiceType( String pathToServiceType ) {
        xPathToServiceType = pathToServiceType;
    }

    /**
     * @param pathToServiceTypeVersion The xPathToServiceTypeVersion to set.
     */
    public void setXPathToServiceTypeVersion( String pathToServiceTypeVersion ) {
        xPathToServiceTypeVersion = pathToServiceTypeVersion;
    }

    // ###########################################################
    
    /**
     * @param catalogueName
     * @param formatList
     */
    public void addCatalogueFormat( String catalogueName, List<String> formatList ) {
        this.catalogToFormatMap.put( catalogueName, formatList );
    }
    
    /**
     * @param catalogueName
     * @param protocolList
     */
    public void addCatalogueProtocol( String catalogueName, List<String> protocolList ) {
        this.catalogToProtocolMap.put( catalogueName, protocolList );
    }
    
    /**
     * @param catalogueName
     * @param catURL
     */
    public void addCatalogueURL( String catalogueName, String catURL ) {
        this.catalogToUrlMap.put( catalogueName, catURL );
    }

    /**
     * @param profileName
     *            The name of the profile (ie: Profiles.ISO19115, Profiles.ISO19119, Profiles.OGCCORE)
     * @param elementSetKeyToXSL
     *            HashMap containing the elementset name as key and the xsl file name as value.
     */
    public void addProfileXSL( String profileName, HashMap<String, String> elementSetKeyToXSL ) {
        this.profilesToXsl.put( profileName, elementSetKeyToXSL );
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CSWClientConfiguration.java,v $
Revision 1.12  2006/08/07 09:48:47  poth
unneccessary type cast removed

Revision 1.11  2006/08/07 09:48:14  poth
unneccessary type cast removed

Revision 1.10  2006/07/05 09:48:22  mays
changes for type safety

Revision 1.9  2006/06/23 13:37:06  mays
add/update csw configuration files

********************************************************************** */