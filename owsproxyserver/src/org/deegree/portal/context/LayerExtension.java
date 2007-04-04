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
package org.deegree.portal.context;

/**
 * provides additional information about a layer described in a web map context
 * document. Additional description is not requiered so an instance of 
 * <tt>org.deegree_impl.clients.context.Layer</tt> may doesn't provide an
 * instance of this class.
 *
 * @version $Revision: 1.10 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class LayerExtension {

    public static final int NONE = -1;

    public static final int SESSIONID = 0;

    public static final int USERPASSWORD = 1;

    private DataService dataService = null;

    private boolean masterLayer = false;

    private double minScaleHint = 0;

    private double maxScaleHint = 9E99;

    private boolean selectedForQuery = false;

    private int authentication = NONE;

    /**
     * default constructor
     *
     */
    public LayerExtension() {
    }

    /**
     * Creates a new LayerExtension object.
     *
     * @param dataService description of the service/server behind a WMS layer
     * @param masterLayer true if a layer is one of the main layers of an
     *                    application; false if it just provides background or
     *                    additional informations.
     * @param minScaleHint
     * @param maxScaleHint
     * @param selectedForQuery
     * @param authentication
     */
    public LayerExtension( DataService dataService, boolean masterLayer, double minScaleHint,
                          double maxScaleHint, boolean selectedForQuery, int authentication ) {
        setDataService( dataService );
        setMasterLayer( masterLayer );
        setMinScaleHint( minScaleHint );
        setMaxScaleHint( maxScaleHint );
        setSelectedForQuery( selectedForQuery );
        setAuthentication( authentication );
    }

    /**
     * returns a description of the service/server behind a WMS layer. The 
     * returned value will be <tt>null</tt> if the WMS uses an internal mechanism
     * to access a layers data.
     *
     * @return instance of <tt>DataService</tt>
     */
    public DataService getDataService() {
        return this.dataService;
        //return null;
    }

    /**
     * sets a description of the service/server behind a WMS layer. The 
     * returned value will be <tt>null</tt> if the WMS uses an internal mechanism
     * to access a layers data.
     *
     * @param dataService 
     */
    public void setDataService( DataService dataService ) {
        this.dataService = dataService;
    }

    /**
     * returns true if a layer is one of the main layers of an application; 
     * returns false if it just provides background or additional informations.
     *
     * @return 
     */
    public boolean isMasterLayer() {
        return masterLayer;
    }

    /**
     * set to true if a layer is one of the main layers of an application; 
     * set to false if it just provides background or additional informations.
     *
     * @param masterLayer 
     */
    public void setMasterLayer( boolean masterLayer ) {
        this.masterLayer = masterLayer;
    }

    /**
     * returns the maximum sclae the layer is valid
     * @return maximum scale hint
     */
    public double getMaxScaleHint() {
        return maxScaleHint;
    }

    /**
     * sets the maximum scale the layer is valid for
     * @param maxScaleHint
     */
    public void setMaxScaleHint( double maxScaleHint ) {
        this.maxScaleHint = maxScaleHint;
    }

    /**
     * returns the minimum sclae the layer is valid
     * @return minimum scale hint
     */
    public double getMinScaleHint() {
        return minScaleHint;
    }

    /**
     * sets the minimum scale the layer is valid for
     * @param minScaleHint
     */
    public void setMinScaleHint( double minScaleHint ) {
        this.minScaleHint = minScaleHint;
    }

    /**
     * returns true if a layer is currently selected for being active
     * for feature info requests
     * @return
     */
    public boolean isSelectedForQuery() {
        return selectedForQuery;
    }

    /**
     * sets a layer to active for feature info requests
     * @param selectedForFI
     */
    public void setSelectedForQuery( boolean selectedForFI ) {
        this.selectedForQuery = selectedForFI;
    }

    /**
     * returns a code for authentication to be used for service requests
     * @return
     */
    public int getAuthentication() {
        return authentication;
    }

    /**
     * @see #getAuthentication()
     * @param authentication
     */
    public void setAuthentication( int authentication ) {
        this.authentication = authentication;
    }

}/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: LayerExtension.java,v $
 Revision 1.10  2006/08/24 15:05:34  poth
 Web Map Context Factory prepared to use User name/password and/or a sessionID requesting resources (Layer)

 Revision 1.9  2006/07/12 14:46:15  poth
 comment footer added

 ********************************************************************** */
