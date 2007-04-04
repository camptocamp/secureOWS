//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wmsconf/styleconf/AddStyleOperation.java,v 1.10 2006/10/17 20:31:19 poth Exp $
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
package org.deegree.conf.services.wmsconf.styleconf;

import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.deegree.conf.services.wmsconf.WMSConfOperation;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * @deprecated
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class AddStyleOperation extends WMSConfOperation {
    
//    private String name;
	private boolean replaceAllMode;
	private String sld;
	
    public static final String REQUEST_NAME = "AddStyle";
   

    private AddStyleOperation(  boolean replaceAllMode, String sld ) {
    	
//    	this.name = name;
    	this.replaceAllMode = replaceAllMode;
    	this.sld = sld;
    }
  
    public static AddStyleOperation create(HttpServletRequest request) throws Exception {

        Map map = KVP2Map.toMap( request );
        
        String nameParameter = (String)map.get( "NAME" );
        String modeParameter = (String)map.get( "MODE" );
        
        String sldParameter = (String)map.get( "SLD" );
        
        checkParameterValidity( nameParameter, modeParameter, sldParameter);
        
        boolean replaceAll = modeParameter.equals( "ReplaceAll" ) ? true : false; 
            
        
        return new AddStyleOperation( replaceAll, sldParameter);

    }

    /** 
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation
     * 											   #performOperation( HttpServletRequest request, 
     * 								   					    		  HttpServletResponse response )
     */
  
    public String performOperation(HttpServletRequest request, HttpServletResponse response)
    	throws Exception {

        URL styleURL = new URL( this.sld );
        
        XMLFragment styleFrag = new XMLFragment( styleURL );
        
        XMLFragment stylesDoc = new XMLFragment( config.getStylesFile().toURL() );
        
        SLDUtils sldUtil = new SLDUtils( stylesDoc );
        sldUtil.addStyle( styleFrag,  this.replaceAllMode );
        
        config.saveStylesFile( stylesDoc );
        
        return "Added style";
    }

	private static void checkParameterValidity(	String name, String mode, String sld )
		throws InvalidParameterValueException {
				    
	    	/*if ( name == null ) { 
	    	    throw new InvalidParameterValueException("The Parameter 'NAME' is mising");
	    	} */
	        
	    	if ( sld == null ) { 
	    	    throw new InvalidParameterValueException("The Parameter 'SLD' is mising");
	    	}
	    	
	}
	
    /**
     * @see org.deegree.conf.services.geodbconf.GeoDbConfOperation#getOperationName()
     */
    public String getOperationName() {
        return REQUEST_NAME;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AddStyleOperation.java,v $
Revision 1.10  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.9  2006/08/24 06:38:30  poth
File header corrected

Revision 1.8  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.7  2006/03/30 21:20:25  poth
*** empty log message ***

Revision 1.6  2005/12/12 09:39:48  taddei
littel refactoring (getOperationName) and chanegs to templates structure

Revision 1.5  2005/12/08 15:32:49  taddei
finished off implementation

Revision 1.4  2005/12/07 16:06:02  taddei
made classes according to WMSConfOperation

Revision 1.3  2005/12/07 14:15:18  ncho
SN
incomplete class

Revision 1.2  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */