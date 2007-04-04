//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/AbstractDataSource.java,v 1.15 2006/11/27 11:32:27 bezema Exp $$
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

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.wpvs.capabilities.OWSCapabilities;

/**
 * 
 * NB. this class is very similar to AbstractDataSource from wms
 * TODO -> re-use ? put into common package?  
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.15 $, $Date: 2006/11/27 11:32:27 $
 * 
 * @since 2.0
 * 
 */
public abstract class AbstractDataSource {

    /**
     * Identifier for a local wcs
     */
    public static final int LOCAL_WCS = 0;
    /**
     * Identifier for a local wfs
     */
    public static final int LOCAL_WFS = 1;
    /**
     * Identifier for a local wms
     */
    public static final int LOCAL_WMS = 2;
    /**
     * Identifier for a remote wfs
     */
    public static final int REMOTE_WFS = 3;
    /**
     * Identifier for a remote wcs
     */
    public static final int REMOTE_WCS = 4;
    /**
     * Identifier for a remote wms
     */
    public static final int REMOTE_WMS = 5;

    protected static final String[] SERVICE_TYPE_TO_NAME = {
    	"LOCAL_WCS", "LOCAL_WFS", "LOCAL_WMS", "REMOTE_WFS", "REMOTE_WCS","REMOTE_WMS" };

    
    private int serviceType ;
    
    private final QualifiedName name;
    
    private OWSCapabilities owsCapabilities;
    
    private Surface validArea;
    
    private double minScaleDenominator;
    
    private double maxScaleDenominator;
    
    private Object filterCondition;
    
    private OGCWebService service;
    
    
    /**
     * TODO pre-conditions. 
     * @param serviceType 
     * @param name 
     * @param owsCapabilities 
     * @param validArea 
     * @param minScaleDenominator 
     * @param maxScaleDenominator 
     * @param filterCondition 
     */
    public AbstractDataSource( int serviceType, QualifiedName name, OWSCapabilities owsCapabilities, 
    						   Surface validArea, double minScaleDenominator, 
    						   double maxScaleDenominator, Object filterCondition ) {
    	
    	setServiceType( serviceType );
        
        if ( name == null ){
            throw new NullPointerException( "QualifiedName cannot be null.");
        }
        this.name = name;
        
        this.owsCapabilities = owsCapabilities;
        this.validArea = validArea;
        
        //TODO min < max?
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
        
        this.filterCondition = filterCondition; 
    }
    
    /**
     * @return Returns the serviceType (WCS, WFS, remote WMS etc...)
     */
    public int getServiceType() {
        return serviceType ;
    }
    
    /**
     * Sets the type of service. A service type means whether the service is a WFS, WCS, remote WMS,
     *  etc.  Allowed values are LOCAL_WCS, LOCAL_WFS, LOCAL_WMS, REMOTE_WFS, REMOTE_WCS or 
     * REMOTE_WMS.
     * 
     * @param serviceType the service type. 
     * @throws IllegalArgumentException if the serviceType is not of know type
     */
    public void setServiceType( int serviceType ) {
        if ( serviceType < LOCAL_WCS || serviceType > REMOTE_WMS ) {
            throw new IllegalArgumentException("serviceType must be one of: " +
            		"LOCAL_WCS, LOCAL_WFS, LOCAL_WMS, REMOTE_WFS, REMOTE_WCS or " +
            		"REMOTE_WMS");
        }
        this.serviceType = serviceType;
    }

	/**
	 * @return Returns the maxScaleDenominator.
	 */
	public double getMaxScaleDenominator() {
		return maxScaleDenominator;
	}

	/**
	 * @return Returns the minScaleDenominator.
	 */
	public double getMinScaleDenominator() {
		return minScaleDenominator;
	}

	/**
	 * @return Returns the name.
	 */
	public QualifiedName getName() {
		return name;
	}

	/**
	 * @return Returns the owsCapabilities.
	 */
	public OWSCapabilities getOwsCapabilities() {
		return owsCapabilities;
	}

	/**
	 * @return Returns the validArea.
	 */
	public Surface getValidArea() {
		return validArea;
	}

	/**
	 * @return Returns the filterCondition.
	 */
	public Object getFilterCondition() {
		return filterCondition;
	}
    
    /**
     * Returns an instance of the <tt>OGCWebService</tt> that represents the
     * datasource. Notice: if more than one layer uses data that are offered by
     * the same OWS, deegree WPVS will use  just one instance for accessing
     * the OWS
     * @return an OGCWebService which represents this datasource
     *  
     */
    public OGCWebService getOGCWebService() {
		if ( this.service == null ){
			this.service = createOGCWebService();
		}
		return this.service;
	}
	
	protected abstract OGCWebService createOGCWebService();
	
	@Override
    public String toString(){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append( "[DataSource: ").append( getName() )
			.append( "\n serviceType: " ).append( SERVICE_TYPE_TO_NAME[ getServiceType() ] )	
			.append( "\n min: " ).append( getMinScaleDenominator() )
			.append( "\n max: " ).append( getMaxScaleDenominator() )
			.append( "\n validArea: " ).append( getValidArea() )
			.append( "\n format: " ).append( getOwsCapabilities().getFormat() )
			.append( "\n onlineResource: " ).append( getOwsCapabilities().getOnlineResource() );
		
		return sb.toString();
	}
	
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractDataSource.java,v $
Revision 1.15  2006/11/27 11:32:27  bezema
UPdating javadocs and cleaning up

Revision 1.14  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.13  2006/08/24 06:42:16  poth
File header corrected

Revision 1.12  2006/07/05 15:58:23  poth
bug fix - changed Query to Filter for WFS datasources

Revision 1.11  2006/06/20 07:45:21  taddei
datasources use quali names now

Revision 1.10  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.9  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.8  2006/03/07 08:46:26  taddei
added pts list factory

Revision 1.7  2006/02/22 13:34:00  taddei
refactoring: added service, createOGCWebService; also better except handling

Revision 1.6  2006/01/18 08:48:27  taddei
added getOGCWebService()

Revision 1.5  2005/12/23 10:36:32  mays
add toString

Revision 1.4  2005/12/06 12:48:19  mays
move param filterCondition from subclasses to AbstractDataSource

Revision 1.3  2005/12/01 12:09:42  mays
restructuring of data source classes according to xml schema

Revision 1.2  2005/12/01 10:30:14  mays
add standard footer to all java classes in wpvs package

******************************************************************** */
