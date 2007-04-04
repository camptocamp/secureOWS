//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/WPVSDeegreeParams.java,v 1.14 2006/11/28 16:52:58 bezema Exp $$
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

package org.deegree.ogcwebservices.wpvs.configuration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * 
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.14 $, $Date: 2006/11/28 16:52:58 $
 * 
 */
public class WPVSDeegreeParams extends DeegreeParams {

    /**
     * 
     */
    private static final long serialVersionUID = 4480667559177855009L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( WPVSDeegreeParams.class );

    private String copyright; // copyright is either text-string or url-string

    // the configured copyrightImage to paint over the GetView response
    private BufferedImage copyrightImage = null;

    private float viewQuality = 0.95f;

    private int maxLifeTime = 3600;

    private final Map<String, URL> backgroundMap;

    private final boolean isFixedSplitter;

    private final boolean isWatermarked;

    private final int maxViewWidth;

    private final int maxViewHeight;

    private final int maxTextureDim;

    private final boolean requestQualityPreferred;

    private double maximumFarClippingPlane;

    private String defaultSplitter;



    /**
     * 
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param copyright
     * @param watermarked
     * @param maxLifeTime
     * @param viewQuality
     * @param backgroundMap
     *            a <code>Map</code> for background images. This Map contains image names as keys
     *            and image URL as values
     * @param isFixedSplitter
     * @param maxWidth
     * @param maxHeight
     * @param maxTextureDim
     * @param requestQualityPreferred
     * @param maximumFarClippingPlane to which extend the request can set a farclippingplane
     * @param defaultSplitter What kind of splitter to use if the GetView request has no field named "splitter"
     */
    public WPVSDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                             int requestTimeLimit, String characterSet, String copyright,
                             boolean watermarked, int maxLifeTime, float viewQuality,
                             Map<String, URL> backgroundMap, boolean isFixedSplitter, int maxWidth,
                             int maxHeight, int maxTextureDim, boolean requestQualityPreferred,
                             double maximumFarClippingPlane, String defaultSplitter ) {

        super( defaultOnlineResource, cacheSize, requestTimeLimit, characterSet );

        this.copyright = copyright;
        this.maxLifeTime = maxLifeTime;
        this.viewQuality = viewQuality;
        this.backgroundMap = backgroundMap;
        this.isFixedSplitter = isFixedSplitter;
        this.isWatermarked = watermarked;
        this.maxViewWidth = maxWidth;
        this.maxViewHeight = maxHeight;
        this.maxTextureDim = maxTextureDim;
        this.requestQualityPreferred = requestQualityPreferred;
        this.maximumFarClippingPlane = maximumFarClippingPlane;
        this.defaultSplitter = defaultSplitter.toUpperCase();
        if( ! ("QUAD".equals( defaultSplitter ) || "BBOX".equals( defaultSplitter ) ) ) {
            LOG.logWarning("The configured defaultSplitter does not exist, setting to QUAD" );
            this.defaultSplitter = "QUAD";
        }

        try {
            URL url = new URL( this.copyright );
            if ( url != null ) {
                copyrightImage = ImageUtils.loadImage( url.getFile() );
            }
        } catch ( MalformedURLException murle ) {
            // The Copyright is a String.
        } catch ( IOException ioe ) {
            // The Copyright is a String.
        }
    }

    /**
     * @return Returns the copyright.
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * @return Returns the maxLifeTime.
     */
    public int getMaxLifeTime() {
        return maxLifeTime;
    }

    /**
     * @return Returns the viewQuality.
     */
    public float getViewQuality() {
        return viewQuality;
    }

    /**
     * @return the configured different backgroundimages
     */
    public Map getBackgroundMap() {
        return backgroundMap;
    }

    /**
     * @return true if the splitter can change
     */
    public boolean isFixedSplitter() {
        return isFixedSplitter;
    }

    /**
     * @return true if the image should be watermarked (with an image or text)
     */
    public boolean isWatermarked() {
        return isWatermarked;
    }

    /**
     * @return maximum value of the width of a request
     */
    public int getMaxViewWidth() {
        return maxViewWidth;
    }
    /**
     * @return maximum value of the height of a request
     */
    public int getMaxViewHeight() {
        return maxViewHeight;
    }

    /**
     * @return TODO don't no yet
     */
    public int getMaxTextureDim() {
        return maxTextureDim;
    }

    /**
     * @return the requestQualityPreferred value.
     */
    public boolean isRequestQualityPreferred() {
        return requestQualityPreferred;
    }

    /**
     * @return the maximumFarClippingPlane value.
     */
    public double getMaximumFarClippingPlane() {
        return maximumFarClippingPlane;
    }

    /**
     * @return the copyrightImage as a BufferedImage.
     */
    public BufferedImage getCopyrightImage() {
        return copyrightImage;
    }

    /**
     * @return the defaultSplitter value.
     */
    public String getDefaultSplitter() {
        return defaultSplitter;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WPVSDeegreeParams.java,v $
 * Changes to this class. What the people have been up to: Revision 1.14  2006/11/28 16:52:58  bezema
 * Changes to this class. What the people have been up to:  Added support for a default splitter
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.13  2006/11/27 11:33:33  bezema
 * Changes to this class. What the people have been up to: UPdating javadocs and cleaning up
 * Changes to this class. What the people have been up to: Revision
 * 1.12 2006/11/23 11:46:40 bezema The initial version of the new wpvs
 * 
 * Revision 1.11 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.10 2006/07/12 16:59:32 poth required adaptions according to renaming of OnLineResource
 * to OnlineResource
 * 
 * Revision 1.9 2006/04/26 12:13:07 taddei max size and tex config parameters
 * 
 * Revision 1.8 2006/04/06 20:25:24 poth ** empty log message ***
 * 
 * Revision 1.7 2006/03/30 21:20:25 poth ** empty log message ***
 * 
 * Revision 1.6 2006/03/07 08:46:26 taddei added pts list factory
 * 
 * Revision 1.5 2006/02/14 15:14:43 taddei added possibility to choose splitter
 * 
 * Revision 1.4 2005/12/21 13:45:50 taddei added code for background images
 * 
 * Revision 1.3 2005/12/08 16:50:30 mays first implementation of parsing methods for deegreeParams
 * 
 * Revision 1.2 2005/12/01 10:30:14 mays add standard footer to all java classes in wpvs package
 * 
 **************************************************************************************************/
