// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/enterprise/DeegreeParams.java,v 1.10 2006/07/12 17:00:26 poth Exp $
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
package org.deegree.enterprise;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcwebservices.wms.InvalidFormatException;

/**
 * Base class for the <code>deegreeParams</code> section of configurations for * all deegree web service types. The <code>deegreeParams</code> section * contains deegree specific parameters that are not part of the OGC CSW * capabilities specification. The concrete web service implementations (WMS, * WFS CWS, ...) derive this class and add their specific configuration * parameters. * <p> * The common <code>deegreeParams</code> elements are: <table border="1"> * <tr> * <th>Name</th> * <th>Mandatory</th> * <th>Function</th> * </tr> * <tr> * <td>DefaultOnlineResource</td> * <td align="center">X</td> * <td>The DefaultOnlineResource will be used whenever a required * OnlineResource is not defined.</td> * </tr> * <tr> * <td>CacheSize</td> * <td align="center">-</td> * <td>Amount of Memory to use for caching, default = 100 (MB).</td> * </tr> * <tr> * <td>RequestTimeLimit</td> * <td align="center">-</td> * <td>Maximum amount of time that is allowed for the execution of a request, * defaults to 2 minutes.</td> * </tr> * <tr> * <td>Encoding</td> * <td align="center">-</td> * <td>String encoding, default is UTF-8.</td> * </tr> * </table> *  * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a> * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @author last edited by: $Author: poth $ *  * @version 2.0, $Revision: 1.10 $, $Date: 2006/07/12 17:00:26 $ *  * @since 2.0
 */

public abstract class DeegreeParams implements Serializable {

    private OnlineResource defaultOnlineResource = null;
	private int cacheSize = 100;
	private int requestTimeLimit = 0;
    private Charset characterSet= null;

	/**
	 * Creates a new instance of DeegreeParams with characterSet set to UTF-8.
	 * 
	 * @param defaultOnlineResource
	 * @param cacheSize
	 * @param requestTimeLimit
	 */
	public DeegreeParams(OnlineResource defaultOnlineResource,
			int cacheSize, int requestTimeLimit) {
		this.defaultOnlineResource = defaultOnlineResource;
		this.cacheSize = cacheSize;
		this.requestTimeLimit = requestTimeLimit;
		if( Charset.isSupported("UTF-8") ){//UTF-8 mus be supported
            this.characterSet = Charset.forName("UTF-8");
        }
	}

	/**
	 * Creates a new instance of DeegreeParams.
	 * 
	 * @param defaultOnlineResource
	 * @param cacheSize
	 * @param requestTimeLimit
	 * @param characterSet
	 */
	public DeegreeParams(OnlineResource defaultOnlineResource,
			int cacheSize, int requestTimeLimit, String characterSet) {
		this.defaultOnlineResource = defaultOnlineResource;
		this.cacheSize = cacheSize;
		this.requestTimeLimit = requestTimeLimit;
		if( Charset.isSupported(characterSet) ){
		    this.characterSet = Charset.forName(characterSet);
        }
        else if( Charset.isSupported("UTF-8") ){//UTF-8 mus be supported
            this.characterSet = Charset.forName("UTF-8");
        }
	}

    /**
     * Returns the CacheSize.
     * 
     * @return the size
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Sets the CacheSize.
     * 
     * @param cacheSize
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * Returns the defaultOnlineResource.
     * 
     * @return the URL
     */
    public OnlineResource getDefaultOnlineResource() {
        return defaultOnlineResource;
    }

    /**
     * Sets the defaultOnlineResource.
     * 
     * @param defaultOnlineResource
     */
    public void setDefaultOnlineResource(OnlineResource defaultOnlineResource) {
        this.defaultOnlineResource = defaultOnlineResource;
    }

    /**
     * Returns the requestTimeLimit.
     * 
     * @return the limit
     */
    public int getRequestTimeLimit() {
        return requestTimeLimit;
    }

    /**
     * Sets the requestTimeLimit.
     * 
     * @param requestTimeLimit

     */
    public void setRequestTimeLimit(int requestTimeLimit) {
        this.requestTimeLimit = requestTimeLimit;
    }

    /**
     * Returns the characterSet.
     * 
     * @return the charset
     *    
     */
    public String getCharacterSet() {
        return characterSet.displayName();
    }
    
    /**
     * @return the Charset requested by the deegreeparams.
     */
    public Charset getCharset(){
        return characterSet;
    }

    /**
     * Sets the characterSet.
     * 
     * @param characterSet
     * @throws InvalidFormatException 
     * 
     */
    public void setCharacterSet(String characterSet) throws InvalidFormatException {
        if( Charset.isSupported(characterSet) ){
            this.characterSet = Charset.forName(characterSet);
        }
        else {
            throw new InvalidFormatException("DeegreeParams: The given charset is not supported by the jvm" );
        }
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * DeegreeParams.java,v $ Revision 1.1 2004/06/23 11:55:40 mschneider Changed
 * hierarchy in org.deegree.ogcwebservices.getcapabilities: -
 * OGCCommonCapabilities are derived for Capabilities according to the OGCCommon
 * Implementation Specification 0.2 - OGCStandardCapabilities are derived for
 * Capabilities prior to the OGCCommon Implementation Specification 0.2
 * 
 * Revision 1.3 2004/06/11 08:47:30 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:46:47 ap no message
 * 
 *  
 ******************************************************************************/
