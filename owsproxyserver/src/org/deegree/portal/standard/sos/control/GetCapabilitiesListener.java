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
package org.deegree.portal.standard.sos.control;

import java.util.HashMap;

import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.framework.util.Debug;
import org.deegree.portal.standard.sos.Constants;
import org.deegree.portal.standard.sos.SOSClientException;
import org.deegree.portal.standard.sos.configuration.SOSClientConfiguration;

/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class GetCapabilitiesListener extends AbstractSOSListener {

    /* (non-Javadoc)
     * @see org.deegree.portal.sos.control.AbstractSOSListener#validateRequest(org.deegree.enterprise.control.RPCMethodCall)
     */
    protected void validateRequest( RPCMethodCall mc ) throws SOSClientException {
        //nothign to do yet; might parse and chec for version
    }

    /* (non-Javadoc)
     * @see org.deegree.portal.sos.control.AbstractSOSListener#createRequest(org.deegree.enterprise.control.RPCMethodCall)
     */
    protected String createRequest( RPCMethodCall mc ) throws SOSClientException {
        StringBuffer sb = new StringBuffer( 1000 );
	    sb.append( "<sos:GetCapabilities ")
	    	.append( "xmlns:sos='http://www.opengis.net/sos' " )
	    	.append( "outputFormat='SensorML' service='SOS' " )
	    //TODO version should be a parameter
	    	.append( "version='0.8.0'>" )
	    	.append( "</sos:GetCapabilities>" );
	    
	    Debug.debugMethodEnd();
	    return sb.toString();	    
    }

    /* (non-Javadoc)
     * @see org.deegree.portal.sos.control.AbstractSOSListener#createData(org.deegree.enterprise.control.RPCMethodCall, java.util.HashMap)
     */
    protected Object createData( RPCMethodCall mc, HashMap map ) throws SOSClientException {
        //FIXME this is all dummy
        SOSClientConfiguration conf = SOSClientConfiguration.getInstance();
        String[] sosResources = conf.getSOSNames(); 
        
        return map.get(sosResources[0]);
    }

    /* (non-Javadoc)
     * @see org.deegree.portal.sos.control.AbstractSOSListener#setNextPageData(java.lang.Object)
     */
    protected void setNextPageData( Object data ) {
        this.getRequest().setAttribute( Constants.SENSORDESCRIPTION, data );

    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetCapabilitiesListener.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
