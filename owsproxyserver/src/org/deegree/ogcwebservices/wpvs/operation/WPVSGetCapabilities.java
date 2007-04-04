//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/operation/WPVSGetCapabilities.java,v 1.8 2006/11/23 11:46:40 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs.operation;

import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;

/**
 * ... 
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/11/23 11:46:40 $
 * 
 * @since 2.0
 */
public class WPVSGetCapabilities extends GetCapabilities {

    /**
     * 
     */
    private static final long serialVersionUID = 4226522219910647235L;

    private static final ILogger LOG = LoggerFactory.getLogger( WPVSGetCapabilities.class );
    
    private static final String WPVS_SERVICE_NAME = "WPVS";
    
    /**
     * Creates a <code>WPVSGetCapabilities</code> object.
     * 
     * @param id
     * @param version
     * @param updateSeq
     * @param acceptedVersions
     * @param sections
     * @param acceptedFormats
     * @param vendoreSpec
     * TODO check if all pars are needed for WPV Service
     */
    public WPVSGetCapabilities( String id, String version, String updateSeq, 
                                String[] acceptedVersions, String[] sections, 
                                String[] acceptedFormats, Map<String,String> vendoreSpec) {
        super( id, version, updateSeq, acceptedVersions, sections, acceptedFormats, vendoreSpec);
    }
    
    /**
     * Creates a <code>WPVSGetCapabilites</code> request from a key-value-pairs in
     * <code>paramMap</code>.
     * @param paramMap Map containing te request parameters
     * @return an new instance of a WPVSGetCapabilities request.
     * @throws MissingParameterValueException if there is a parameter missing
     * @throws InvalidParameterValueException if one of the parameters has an invalid value
     */
    public static WPVSGetCapabilities create( Map<String,String> paramMap ) 
    	throws 	MissingParameterValueException,
        		InvalidParameterValueException {
        
        LOG.entering();

        String id = paramMap.remove( "ID" );
        String service = paramMap.remove( "SERVICE" );
        
        if ( !service.equals( WPVS_SERVICE_NAME ) ) {
            throw new MissingParameterValueException( "WPVSGetCapabilities",
                "'service' parameter is missing" );
        }
        if ( !service.equals( WPVS_SERVICE_NAME ) ) {
            throw new InvalidParameterValueException( "WPVSGetCapabilities",
                "service attribute must equal 'WPVS'" );
        }
        
        String updateSeq = paramMap.remove( "UPDATESEQUENCE" );
        String version = paramMap.remove( "VERSION" );
        String tmp = paramMap.remove( "SECTION" );
        
        String[] sections = null;
        if ( tmp != null ) {
            sections = StringTools.toArray( tmp, ",", true );            
        }

        LOG.exiting();

        return new WPVSGetCapabilities( id, service, updateSeq, new String[] { version }, 
            							sections, null, paramMap);
    }

    /** 
     * returns 'WPVS' as service name.
     */
    public String getServiceName() {
        return WPVS_SERVICE_NAME;
    }
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WPVSGetCapabilities.java,v $
Revision 1.8  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.7  2006/10/27 13:28:44  poth
support for vendorspecific parameters added

Revision 1.6  2006/08/24 06:42:16  poth
File header corrected

Revision 1.5  2006/04/06 20:25:31  poth
*** empty log message ***

Revision 1.4  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.3  2006/01/26 14:39:43  taddei
removed println

Revision 1.2  2006/01/18 08:57:14  taddei
*** empty log message ***

Revision 1.1  2005/12/13 14:43:15  taddei
added WPV service, its factory and minimal GetCapabilites


********************************************************************** */