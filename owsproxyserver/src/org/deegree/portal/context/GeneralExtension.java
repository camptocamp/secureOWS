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
 * this class encapsulates the deegree specific extensions of the general 
 * section of a web map context document. this is a description of the GUI 
 * including the used modules (<tt>Frontend</tt>) and the parameters to control 
 * the map view (<tt>Marshallable</tt>).
 *
 * @version $Revision: 1.10 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class GeneralExtension {
    private Frontend frontend = null;
    private MapParameter mapParameter = null;
    private IOSettings iOSettings = null;
    private AuthentificationSettings authSettings = null;
    private boolean transparent = true;
    private String bgColor = "0xFFFFFF";
    private String mode = null;

    /**
     * Creates a new GeneralExtension object.
     *
     * @param frontend 
     * @param mapParameter 
     */
    public GeneralExtension( IOSettings iOSettings, Frontend frontend, 
    					     MapParameter mapParameter, 
                             AuthentificationSettings authSettings,
                             String mode) {
        setFrontend( frontend );
        setMapParameter( mapParameter );
        setIOSettings(iOSettings);
        setAuthentificationSettings(authSettings);
        this.mode = mode;
    }
    
    /**
     * returns true if the maps background should be transparent
     * @return
     */
    public boolean isTransparent() {
        return transparent;
    }
    
    /**
     * @see #isTransparent()
     * @param transparent
     */
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
       
    /**
     * returns the desired background color of the map
     * @return
     */
    public String getBgColor() {
        return bgColor;
    }
    
    /**
     * @see #getBgColor()
     * @param bgColor
     */
    public void setBgColor( String bgColor ) {
        this.bgColor = bgColor;
    }
    
    /**
     * returns the frontend (GUI) description encapsulating objekt
     *
     * @return 
     */
    public Frontend getFrontend() {
        return frontend;
    }

    /**
     * sets the frontend (GUI) description encapsulating objekt
     *
     * @param frontend <tt>Frontend</tt>
     */
    public void setFrontend( Frontend frontend ) {
        this.frontend = frontend;
    }

    /**
     * returns the parameters describing the control options for the map
     *
     * @return <tt>MapParameter</tt> encapsulating several control params
     */
    public MapParameter getMapParameter() {
        return mapParameter;
    }

    /**
     * sets the parameters describing the control options for the map
     *
     * @param mapParameter <tt>MapParameter</tt> encapsulating several control 
     *                     params
     */
    public void setMapParameter( MapParameter mapParameter ) {
        this.mapParameter = mapParameter;
    }
  
	/**
	 * @return Returns the iOSettings.
	 */
	public IOSettings getIOSettings() {
		return iOSettings;
	}

	/**
	 * @param settings The iOSettings to set.
	 */
	public void setIOSettings(IOSettings settings) {
		iOSettings = settings;
	}
    
    public AuthentificationSettings getAuthentificationSettings() {
        return authSettings;
    }
    
    public void setAuthentificationSettings(AuthentificationSettings authSettings) {
        this.authSettings = authSettings;
    }

    /**
     * returns the current mode of a map client using a WMC. A mode defines
     * the action that occurs if a user performs a mouse action on the map
     * @return
     */
    public String getMode() {
        return mode;
    }

    /**
     * @see #getMode()
     * @param mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
	
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GeneralExtension.java,v $
Revision 1.10  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
