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
package org.deegree.ogcwebservices.wmps.configuration;

import java.net.URL;
import java.util.List;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.enterprise.Proxy;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.wms.capabilities.GazetteerParam;

/**
 * WMPS specific deegree parameters container class. 
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class WMPSDeegreeParams extends DeegreeParams {

    private static final long serialVersionUID = 6954454089387042783L;

    private float mapQuality = 0.95f;

    private int maxLifeTime = 3600;

    private int maxMapWidth = 1000;

    private int maxMapHeight = 1000;

    private String copyright = "deegree (c)";

    private GazetteerParam gazetteer;

    private URL schemaLocation;

    private URL dtdLocation;

    private Proxy proxy;

    private boolean antiAliased;

    private int featureInfoRadius = 5;

    private List<String> synchList;

    private CacheDatabase cacheDatabase;

    private PrintMapParam printMapParam;

    /**
     * Create a new WMPSDeegreeParams instance.
     * 
     * @param cacheSize
     * @param maxLifeTime
     * @param requestTimeLimit
     * @param mapQuality
     * @param defaultOnlineResource
     * @param maxMapWidth
     * @param maxMapHeight
     * @param antiAliased
     * @param featureInfoRadius
     *            (default = 5)
     * @param copyRight
     * @param gazetteer
     * @param schemaLocation
     * @param dtdLocation
     * @param proxy
     * @param synchList
     * @param printMapParam
     * @param cacheDatabase
     */
    public WMPSDeegreeParams( int cacheSize, int maxLifeTime, int requestTimeLimit,
                             float mapQuality, OnlineResource defaultOnlineResource,
                             int maxMapWidth, int maxMapHeight, boolean antiAliased,
                             int featureInfoRadius, String copyRight, GazetteerParam gazetteer,
                             URL schemaLocation, URL dtdLocation, Proxy proxy,
                             List<String> synchList, CacheDatabase cacheDatabase,
                             PrintMapParam printMapParam ) {
        
        super( defaultOnlineResource, cacheSize, requestTimeLimit );
        this.maxLifeTime = maxLifeTime;
        this.mapQuality = mapQuality;
        this.maxMapHeight = maxMapHeight;
        this.maxMapWidth = maxMapWidth;
        this.antiAliased = antiAliased;
        this.copyright = copyRight;
        this.gazetteer = gazetteer;
        this.schemaLocation = schemaLocation;
        this.dtdLocation = dtdLocation;
        this.proxy = proxy;
        this.featureInfoRadius = featureInfoRadius;
        this.synchList = synchList;
        this.cacheDatabase = cacheDatabase;
        this.printMapParam = printMapParam;
    }

    /**
     * returns the maximum life time of the internal processes (Threads) of the deegree WMS. default
     * is 3600 seconds. Datasources that are linked to WMS are not targeted by this value.
     * 
     * @return int
     */
    public int getMaxLifeTime() {
        return this.maxLifeTime;
    }

    /**
     * returns a copy right note to draw at the left side of the maps bottom
     * 
     * @return String
     */
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * returns the quality of the map for none loss-less image formats. the value ranges from 0
     * (lowest quality) to 1 (best quality)
     * <p>
     * Default is 0.95
     * 
     * @return float
     */
    public float getMapQuality() {
        return this.mapQuality;
    }

    /**
     * returns the maximum map height that can be requested. If the PrintMap-Parameter 'HEIGHT'
     * extends max map width an exception shall be returned to the client.
     * <p>
     * Default is 1000
     * 
     * @return int
     */
    public int getMaxMapHeight() {
        return this.maxMapHeight;
    }

    /**
     * returns the maximum map width that can be requested. If the PrintMap-Parameter 'WIDTH'
     * extends max map width an exception shall be returned to the client.
     * <p>
     * Default is 1000
     * 
     * @return int
     */
    public int getMaxMapWidth() {
        return this.maxMapWidth;
    }

    /**
     * returns the URL where to access the gazetteer service associated with the WMS
     * 
     * @return GazetteerParam
     */
    public GazetteerParam getGazetteer() {
        return this.gazetteer;
    }

    /**
     * returns the URL where the sxm schema definition of the response to an GetFeatureInfo request
     * is located
     * 
     * @return URL
     */
    public URL getSchemaLocation() {
        return this.schemaLocation;
    }

    /**
     * returns the URL where the DTD defining the OGC WMS capabilities is located
     * 
     * @return URL
     */
    public URL getDTDLocation() {
        return this.dtdLocation;
    }

    /**
     * returns the proxy used with the WMS.
     * 
     * @return Proxy
     */
    public Proxy getProxy() {
        return this.proxy;
    }

    /**
     * returns true if a map shall be rendered with antialising
     * 
     * @return boolean
     */
    public boolean isAntiAliased() {
        return this.antiAliased;
    }

    /**
     * returns the radius (pixel) of the area considered for a feature info request (default = 5px)
     * 
     * @return int
     */
    public int getFeatureInfoRadius() {
        return this.featureInfoRadius;
    }

    /**
     * returns a list of templated for which PrintMap requests shall be handled synchronously
     * 
     * @return List
     */
    public List<String> getSynchronousTemplates() {
        return this.synchList;
    }

    /**
     * @return Returns the cacheDatabase.
     */
    public CacheDatabase getCacheDatabase() {
        return this.cacheDatabase;
    }

    /**
     * @return Returns the printMapParam.
     */
    public PrintMapParam getPrintMapParam() {
        return this.printMapParam;
    }

   
    
    
    
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WMPSDeegreeParams.java,v $
 * Changes to this class. What the people have been up to: Revision 1.17  2006/10/02 06:30:36  poth
 * Changes to this class. What the people have been up to: bug fixes
 * Changes to this class. What the people have been up to:
 * Revision 1.16  2006/08/10 07:11:35  deshmukh
 * WMPS has been modified to support the new configuration changes and the excess code not needed has been replaced.
 * Changes to this class. What the people have been up to:
 * Revision 1.15  2006/07/31 11:21:07  deshmukh
 * wmps implemention...
 * Revision
 * 1.14 2006/07/12 16:59:32 poth required adaptions according to renaming of OnLineResource to
 * OnlineResource
 * 
 * Revision 1.13 2006/07/12 14:46:18 poth comment footer added
 * 
 **************************************************************************************************/
