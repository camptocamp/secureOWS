// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/control/GetTermListListener.java,v 1.5 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.portal.standard.gazetteer.control;

import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.framework.util.Debug;
import org.deegree.portal.standard.gazetteer.Constants;
import org.deegree.portal.standard.gazetteer.GazetteerClientException;

/**
 * 
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 1.1
 */
public class GetTermListListener extends GetRelatedTermsListener {

    
    /**
     * validates the request to be performed.
     *
     * @param mc object containing the request to be performed
     */
    protected void validateRequest( RPCMethodCall mc ) throws GazetteerClientException {        
        RPCParameter[] params = mc.getParameters();
        if ( params == null || params.length != 1 ) {
            throw new GazetteerClientException("one rpc parameter containing a struct "+
                                               "with requiered parameters must be set");
        }
        RPCStruct struct = (RPCStruct)params[0].getValue();
        if ( struct.getMember( Constants.TARGETTYPE ) == null ) {
            throw new GazetteerClientException( "parameter 'targetType' must be set." );
        }
        if ( struct.getMember( Constants.GEOGRAPHICIDENTIFIER ) == null ) {
            throw new GazetteerClientException( "parameter 'geomgraphicIdentifier' must be set." );
        }
        
    }
    
    /**
     * creates a Gazetteer/WFS GetFeature request from the parameters contained
     * in the passed <tt>RPCMethodeCall</tt>.
     * 
     * @param mc
     * @return Gazetteer/WFS GetFeature request 
     */
    protected String createRequest( RPCMethodCall mc ) throws GazetteerClientException {
        Debug.debugMethodBegin();
         
        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct)params[0].getValue();
        
        StringBuffer sb = new StringBuffer(1000);
        sb.append( "<wfs-g:GetFeature outputFormat='GML2' " );
        sb.append( "xmlns:wfs-g='http://www.opengis.net/wfs-g' ");
        sb.append( "xmlns:ogc='http://www.opengis.net/ogc' ");
        sb.append( "xmlns:gml='http://www.opengis.net/gml' ");
        sb.append( "xmlns:wfs='http://www.opengis.net/wfs' ");
        sb.append( "version='1.0.0' service='WFS'>" );
        String s = (String)struct.getMember( Constants.TARGETTYPE ).getValue();
        sb.append( "<wfs:Query typeName='").append( s ).append( "'>" );
        sb.append( "<wfs:PropertyName>identifier</wfs:PropertyName>" );
        sb.append( "<wfs:PropertyName>geographicIdentifier</wfs:PropertyName>" );
        sb.append( "<ogc:Filter>" );
        s = (String)struct.getMember( Constants.GEOGRAPHICIDENTIFIER ).getValue();
        try {
            Integer.parseInt(s);            
            sb.append( "<ogc:PropertyIsEqualTo>");
            sb.append( "<ogc:PropertyName>geographicIdentifier</ogc:PropertyName>");        
            sb.append( "<ogc:Literal>" ).append( s ).append( "</ogc:Literal>" );
            sb.append( "</ogc:PropertyIsEqualTo>");
        } catch (Exception e) {
            sb.append( "<ogc:PropertyIsLike wildCard='*' singleChar='?' escape='/'>");
            sb.append( "<ogc:PropertyName>geographicIdentifier</ogc:PropertyName>");        
            sb.append( "<ogc:Literal>*" ).append( s ).append( "*</ogc:Literal>" );
            sb.append( "</ogc:PropertyIsLike>");
        }
        sb.append( "</ogc:Filter>" );
        sb.append( "</wfs:Query>" );
        sb.append( "</wfs-g:GetFeature>" );
        
        Debug.debugMethodEnd();
        return sb.toString();
    }
    

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GetTermListListener.java,v $
   Revision 1.5  2006/08/29 19:54:14  poth
   footer corrected

   Revision 1.4  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:12  poth
   *** empty log message ***

   Revision 1.2  2006/01/16 20:36:40  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:30:06  poth
   no message

   Revision 1.2  2004/05/24 06:58:47  ap
   no message

   Revision 1.1  2004/05/22 09:55:36  ap
   no message

   Revision 1.3  2004/03/26 16:42:18  poth
   no message

   Revision 1.2  2004/03/26 11:19:28  poth
   no message

   Revision 1.1  2004/03/16 15:19:47  poth
   no message

********************************************************************** */
