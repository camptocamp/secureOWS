// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/control/TakeListener.java,v 1.8 2006/10/17 20:31:19 poth Exp $
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

import java.io.InputStreamReader;
import java.net.URL;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.NetWorker;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.portal.standard.gazetteer.Constants;
import org.deegree.portal.standard.gazetteer.GazetteerClientException;
import org.deegree.portal.standard.gazetteer.configuration.GazetteerClientConfiguration;



/**
 * Listener to take the passed gazetteer term, to access its properties and
 * pass them to the target web resource (nextPage)
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 1.1
 */
public final class TakeListener extends AbstractListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( TakeListener.class );
    
    /**
     * inherited from <tt>AbstractListener</tt> performes the request
     *
     * @param e 
     */
    public void actionPerformed( FormEvent e ) {
        
        RPCWebEvent rpcEvent = (RPCWebEvent)e;
        RPCMethodCall mc = rpcEvent.getRPCMethodCall();
        
        try {
            validateRequest( mc );            
        } catch ( Exception ex ) {
            ex.printStackTrace();
            gotoErrorPage( "Invalid WCSCapabilityOperations: " + ex.toString() );
            
            return;
        }   
        
        String request = null;
        try {
            request = createRequest( mc );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            gotoErrorPage( "Couldn't create Gazetteer WCSCapabilityOperations: " + ex.toString() );
            
            return;
        }
        
        Feature feature = null;
        try {
            feature = performRequest( mc, request );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            gotoErrorPage( "Couldn't perform Gazetteer request: " + ex.toString() );
            
            return;
        }
        
        try {
            this.getRequest().setAttribute( Constants.TERM, feature );
            RPCParameter[] params = mc.getParameters();
            RPCStruct struct = (RPCStruct)params[0].getValue();
            this.getRequest().setAttribute( Constants.TARGETTYPE, 
                                            struct.getMember( Constants.TARGETTYPE ).getValue() );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            gotoErrorPage( "Couldn't return Gazetteer result: " + ex.toString() );
            
        }
        
    }
    
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
        if ( struct.getMember( Constants.GAZETTEER ) == null ) {
            throw new GazetteerClientException( "parameter 'gazetteer' must be set." );
        }
        if ( struct.getMember( Constants.IDENTIFIER ) == null ) {
            throw new GazetteerClientException( "parameter 'identifier' must be set." );
        }
        
    }
    
    /**
     * creates a Gazetteer/WFS GetFeature request from the parameters contained
     * in the passed <tt>RPCMethodeCall</tt>.
     * 
     * @param mc
     * @return Gazetteer/WFS GetFeature request 
     */
    protected String createRequest( RPCMethodCall mc )  {
        
        
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
        sb.append( "<wfs:PropertyName>geographicExtent</wfs:PropertyName>" );
        sb.append( "<ogc:Filter>" );
        sb.append( "<ogc:PropertyIsEqualTo>");
        sb.append( "<ogc:PropertyName>identifier</ogc:PropertyName>");
        s = (String)struct.getMember( Constants.IDENTIFIER ).getValue();
        sb.append( "<ogc:Literal>" ).append( s ).append( "</ogc:Literal>" );
        sb.append( "</ogc:PropertyIsEqualTo>");
        sb.append( "</ogc:Filter>" );
        sb.append( "</wfs:Query>" );
        sb.append( "</wfs-g:GetFeature>" );
        
        LOG.logDebug( sb.toString() );

        return sb.toString();
    }
    
    /**
     * performs a GetFeature request against a WFS-G and returns the result
     * encapsulated in <tt>GetTermResultSet</tt> 
     * 
     * @param mc
     * @param request request to perform
     * @return one feature as result to the passed request
     * @throws GazetteerClientException
     */
    protected Feature performRequest( RPCMethodCall mc , String request ) throws GazetteerClientException {
        
        
        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct)params[0].getValue();
        
        GazetteerClientConfiguration conf = GazetteerClientConfiguration.getInstance();
        
        String gaze = (String)struct.getMember(Constants.GAZETTEER).getValue();
        URL url = conf.getGazetteerAddress( gaze );
        NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), url, request);
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader( nw.getInputStream(), CharsetUtils.getSystemCharset() );
        } catch(Exception e) {
            throw new GazetteerClientException( "Couldn't open stream from WFS-G", e );
        }
        
        FeatureCollection fc = null;
        try {
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
            doc.load( isr, url.toString() );
            fc = doc.parse();
        } catch(Exception e) {
            throw new GazetteerClientException( "Couldn't open stream from WFS-G", e );
        }
        
        if ( fc == null || fc.size() == 0 ) {
            return null;
        }
        return fc.getFeature( 0 );
        
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: TakeListener.java,v $
   Revision 1.8  2006/10/17 20:31:19  poth
   *** empty log message ***

   Revision 1.7  2006/08/29 19:54:14  poth
   footer corrected

   Revision 1.6  2006/08/07 13:55:45  poth
   deprecated method calls removed

   Revision 1.5  2006/06/03 12:16:01  poth
   *** empty log message ***

   Revision 1.4  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:12  poth
   *** empty log message ***

   Revision 1.8  2006/01/20 18:19:32  mschneider
   Adapted to use GMLFeatureCollectionDocument.

   Revision 1.6  2005/12/06 13:45:20  poth
   System.out.println substituted by logging api

   Revision 1.5  2005/08/30 08:25:48  poth
   no message

   Revision 1.4  2005/07/18 07:00:50  poth
   no message

   Revision 1.3  2005/01/19 17:22:26  poth
   no message

   Revision 1.2  2005/01/06 17:51:46  poth
   no message

   Revision 1.7  2004/07/09 06:58:04  ap
   no message

   Revision 1.6  2004/06/28 06:40:04  ap
   no message

   Revision 1.5  2004/06/11 08:47:30  ap
   no message

   Revision 1.4  2004/06/08 13:03:20  tf
   refactor to org.deegree.enterprise

   Revision 1.3  2004/06/03 09:02:20  ap
   no message


********************************************************************** */
