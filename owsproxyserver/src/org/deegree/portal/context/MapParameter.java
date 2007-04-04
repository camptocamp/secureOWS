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

import java.util.ArrayList;




/**
 * encapsulates the part of the general web map context extension parameters
 * that targets the map operation and feature info format options. These are
 * informations about the possible values and the current selected value for
 * each of the encapsulated parameters: <p/>
 * feature info formats<p/>
 * pan factors (% of the map size) <p/>
 * zoom factors (% of the map factors) <p/>
 * minimum displayable scale (WMS scale definition) <p/>
 * maximum displayable scale (WMS scale definition) <p/>
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class MapParameter {
    private ArrayList offeredInfoFormats = new ArrayList();
    private ArrayList offeredPanFactors = new ArrayList();
    private ArrayList offeredZoomFactors = new ArrayList();
    private double maxScale = 0;
    private double minScale = 0;

    /**
     * Creates a new MapParameter object.
     *
     * @param offeredInfoFormats feature info formats
     * @param offeredPanFactors pan factors (% of the map size)
     * @param offeredZoomFactors pan factors (% of the map size)
     * @param minScale minimum displayable scale (WMS scale definition)
     * @param maxScale maximum displayable scale (WMS scale definition)
     */
    public MapParameter( Format[] offeredInfoFormats, MapOperationFactor[] offeredPanFactors, 
                         MapOperationFactor[] offeredZoomFactors, double minScale, double maxScale ) {
        setOfferedInfoFormats( offeredInfoFormats );
        setOfferedPanFactors( offeredPanFactors );
        setOfferedZoomFactors( offeredZoomFactors );
        setMinScale( minScale );
        setMaxScale( maxScale );
    }

    /**
     * sets the offered pan factors (% of the map size) for a map context
     *
     * @param panFactors 
     */
    public void setOfferedPanFactors( MapOperationFactor[] panFactors ) {
        offeredPanFactors.clear();

        if ( panFactors != null ) {
            for ( int i = 0; i < panFactors.length; i++ ) {
                addPanFactor( panFactors[i] );
            }
        }
    }

    /**
     * add a pan factor to a map context
     *
     * @param panFactor 
     */
    public void addPanFactor( MapOperationFactor panFactor ) {
        offeredPanFactors.add( panFactor );
    }

    /**
     * returns the list of pan factors offered by this map context
     */
    public MapOperationFactor[] getOfferedPanFactors() {
        MapOperationFactor[] ms = new MapOperationFactor[0];

        if ( offeredPanFactors.size() == 0 ) {
            ms = null;
        } else {
            ms = new MapOperationFactor[offeredPanFactors.size()];
            ms = (MapOperationFactor[])offeredPanFactors.toArray( ms );
        }

        return ms;
    }

    /**
     * returns the pan factor that is marked as selected. If no pan factor
     * is marked, the first pan factor will be returned.
     */
    public MapOperationFactor getSelectedPanFactor() {
        MapOperationFactor ms = (MapOperationFactor)offeredPanFactors.get( 0 );

        for ( int i = 0; i < offeredPanFactors.size(); i++ ) {
            MapOperationFactor tmp = (MapOperationFactor)offeredPanFactors.get( i );
            if ( tmp.isSelected() ) {
                ms = tmp;
                break;
            }
        }

        return ms;
    }

    /**
     * removes a pan factor from a context
     *
     * @param panFactor 
     */
    public void removePanFactor( MapOperationFactor panFactor ) throws ContextException {
        for (int i = 0; i < offeredPanFactors.size(); i++) {
            MapOperationFactor mof = (MapOperationFactor)offeredPanFactors.get(i);
            if ( mof.getFactor() == panFactor.getFactor() ) {
                if ( mof.isSelected() ) {
                    throw new ContextException( "The PanFactor can't be removed " +
                                                "from the context because it is  the " +
                                                "current one" );
                }
            }
        }
    }

    /**
     * sets the offered zoom factors (% of the map size) for a map context 
     *
     * @param zoomFactors 
     */
    public void setOfferedZoomFactors( MapOperationFactor[] zoomFactors ) {
        offeredZoomFactors.clear();

        if ( zoomFactors != null ) {
            for ( int i = 0; i < zoomFactors.length; i++ ) {
                addZoomFactor( zoomFactors[i] );
            }
        }
    }

    /**
     * adds a zoom factor to a map context 
     *
     * @param zoomFactor 
     */
    public void addZoomFactor( MapOperationFactor zoomFactor ) {
        offeredZoomFactors.add( zoomFactor );
    }

    /**
     * returns the list of zoom factors offered by the map context
     */
    public MapOperationFactor[] getOfferedZoomFactors() {
        MapOperationFactor[] ms = new MapOperationFactor[0];

        if ( offeredZoomFactors.size() == 0 ) {
            ms = null;
        } else {
            ms = new MapOperationFactor[offeredZoomFactors.size()];
            ms = (MapOperationFactor[])offeredZoomFactors.toArray( ms );
        }

        return ms;
    }

    /**
     * returns the zoom factor that is marked as selected. If no zoom factor
     * is marked, the first zoom factor will be returned.
     */
    public MapOperationFactor getSelectedZoomFactor() {
        MapOperationFactor ms = (MapOperationFactor)offeredZoomFactors.get( 0 );

        for ( int i = 0; i < offeredPanFactors.size(); i++ ) {
            MapOperationFactor tmp = (MapOperationFactor)offeredZoomFactors.get( i );

            if ( tmp.isSelected() ) {
                ms = tmp;
                break;
            }
        }

        return ms;
    }

    /**
     * removes a zomm factor from a map context 
     *
     * @param zoomFactor 
     */
    public void removeZoomFactor( MapOperationFactor zoomFactor ) throws ContextException {
        for (int i = 0; i < offeredZoomFactors.size(); i++) {
            MapOperationFactor mof = (MapOperationFactor)offeredZoomFactors.get(i);
            if ( mof.getFactor() == zoomFactor.getFactor() ) {
                if ( mof.isSelected() ) {
                    throw new ContextException( "The ZoomFactor can't be removed " +
                                                "from the context because it is  the " +
                                                "current one" );
                }
            }
        }
    }

    /**
     * sets the info formats offered by a map context
     *
     * @param infoFormats 
     */
    public void setOfferedInfoFormats( Format[] infoFormats ) {
        offeredInfoFormats.clear();

        if ( infoFormats != null ) {
            for ( int i = 0; i < infoFormats.length; i++ ) {
                addInfoFormat( infoFormats[i] );
            }
        }
    }

    /**
     * adds an info format to a map context
     *
     * @param infoFormat 
     */
    public void addInfoFormat( Format infoFormat ) {
        offeredInfoFormats.add( infoFormat );
    }

    /**
     * returns the list of map formats offered by the map context
     */
    public Format[] getOfferedInfoFormats() {
        Format[] ms = new Format[0];

        if ( offeredInfoFormats.size() == 0 ) {
            ms = null;
        } else {
            ms = new Format[offeredInfoFormats.size()];
            ms = (Format[])offeredInfoFormats.toArray( ms );
        }

        return ms;
    }

    /**
     * returns the info format that is marked as selected. If no info format
     * is marked, the first info format will be returned.
     */
    public Format getSelectedInfoFormat() {
        Format ms = (Format)offeredInfoFormats.get( 0 );
        for ( int i = 0; i < offeredInfoFormats.size(); i++ ) {
            Format tmp = (Format)offeredInfoFormats.get( i );

            if ( tmp.isCurrent() ) {
                ms = tmp;
                break;
            }
        }

        return ms;
    }

    /**
     * removes an info format from a map context
     *
     * @param format 
     */
    public void removeInfoFormat( Format format ) throws ContextException {
        for (int i = 0; i < offeredInfoFormats.size(); i++) {
            Format frmt = (Format)offeredInfoFormats.get(i);
            if ( frmt.getName() == format.getName() ) {
                if ( format.isCurrent() ) {
                    throw new ContextException( "The Info Format can't be removed " +
                                                "from the context because it is  the " +
                                                "current one" );
                }
            }
        }
    }

    /**
     * returns the minimum map scale as defined at the OGC WMS specs that is 
     * offered by the map context
     *
     * @return 
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * sets the minimum map scale as defined at the OGC WMS specs that is 
     * offered by the map context
     *
     * @param minScale 
     */
    public void setMinScale( double minScale ) {
        this.minScale = minScale;
    }

    /**
     * returns the maximum map scale as defined at the OGC WMS specs that is 
     * offered by the map context
     *
     * @return 
     */
    public double getMaxScale() {
        return maxScale;
    }

    /**
     * sets the maximum map scale as defined at the OGC WMS specs that is 
     * offered by the map context
     *
     * @param maxScale 
     */
    public void setMaxScale( double maxScale ) {
        this.maxScale = maxScale;
    }

    /**
     *
     *
     * @return 
     */
    public String exportAsXML() {
        return null;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MapParameter.java,v $
Revision 1.6  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
