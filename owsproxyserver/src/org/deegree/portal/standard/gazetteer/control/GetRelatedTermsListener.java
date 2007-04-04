// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/control/GetRelatedTermsListener.java,v 1.8 2006/10/17 20:31:19 poth Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.Debug;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.gazetteer.SI_LocationInstance;
import org.deegree.portal.standard.gazetteer.Constants;
import org.deegree.portal.standard.gazetteer.GazetteerClientException;
import org.deegree.portal.standard.gazetteer.configuration.GazetteerClientConfiguration;
import org.deegree.portal.standard.gazetteer.model.GetTermsResultSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Listener for accessing gazetteer terms related to another term from one or
 * more gazetteers.
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 1.1
 */
public class GetRelatedTermsListener extends AbstractListener {
    
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
            gotoErrorPage( "Invalid WCSCapabilityOperations: " + ex.toString() );
            Debug.debugMethodEnd();
            return;
        }

        String request = null;
        try {
        	request = createRequest( mc );        
        } catch ( Exception ex ) {         
            gotoErrorPage( "Couldn't create Gazetteer WCSCapabilityOperations: " + ex.toString() );
            Debug.debugMethodEnd();
            return;
        }

        HashMap map = null;
        try {
            map = performRequest( request );
        } catch ( Exception ex ) {      
            gotoErrorPage( "Couldn't perform Gazetteer WCSCapabilityOperations: " + ex.toString() );
            Debug.debugMethodEnd();
            return;
        }

        GetTermsResultSet gtrs = null;
        try {
            gtrs = createResultSet( mc, map );
        } catch ( Exception ex ) {
ex.printStackTrace();
            gotoErrorPage( "Couldn't format gazetteer result: " + ex.toString() );
            Debug.debugMethodEnd();
            return;
        }      

        setParamForNextPage( gtrs );
                
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
        if ( struct.getMember( Constants.SOURCETYPE ) == null ) {
        	throw new GazetteerClientException("sourceType parameter must be set " +
                                               "to perform a gazetteer client getTerms" +
                                               "request");
        }
        if ( struct.getMember( Constants.SOURCEVALUE ) == null ) {
            throw new GazetteerClientException("sourceValue parameter must be set " +
                                               "to perform a gazetteer client getTerms" +
                                               "request");
        }
        if ( struct.getMember( Constants.TARGETTYPE ) == null ) {
            throw new GazetteerClientException("targetType parameter must be set " +
                                               "to perform a gazetteer client getTerms" +
                                               "request");
        }
        if ( struct.getMember( Constants.RELATIONTYPE ) == null ) {
            throw new GazetteerClientException("relationType parameter must be set " +
                                               "to perform a gazetteer client getTerms" +
                                               "request");
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
        sb.append( "<wfs:Query typeName='");
        sb.append( struct.getMember( Constants.TARGETTYPE ).getValue() ).append( "'>" );
        sb.append( "<wfs:PropertyName>identifier</wfs:PropertyName>" );
        sb.append( "<wfs:PropertyName>geographicIdentifier</wfs:PropertyName>" );
        sb.append( "<ogc:Filter>" );
        sb.append( "<ogc:PropertyIsEqualTo>" );
        String tmp = (String)struct.getMember( Constants.RELATIONTYPE ).getValue();
        if ( "NT".equals( tmp ) ) {
            sb.append( "<ogc:PropertyName>" )
              .append( "parents/identifier" )
              .append( "</ogc:PropertyName>" );
        } else if ( "BT".equals( tmp ) ) {
            sb.append( "<ogc:PropertyName>" )
            .append( "children/identifier" )
            .append( "</ogc:PropertyName>" );
        } else if ( "RT".equals( tmp ) ) {
        	throw new GazetteerClientException("RT - Related Term is not implemented yet");
        } else {
            throw new GazetteerClientException( "unknown relation type: " + tmp );
        }
        sb.append( "<ogc:Literal>" ).append( struct.getMember( Constants.SOURCEVALUE ).getValue() )
          .append( "</ogc:Literal>" );        
        sb.append( "</ogc:PropertyIsEqualTo>" );
        sb.append( "</ogc:Filter>" );
        sb.append( "</wfs:Query>" );
        sb.append( "</wfs-g:GetFeature>" );
        Debug.debugMethodEnd();
        return sb.toString();
    }
    
    /**
     * performs a GetFeature request against a WFS-G and returns the result
     * encapsulated in <tt>GetTermResultSet</tt> 
     * 
     * @param request request to perform
     * @return result to the passed request
     * @throws GazetteerClientException
     */
    private HashMap performRequest( String request ) throws GazetteerClientException {
        Debug.debugMethodBegin();
        
        HashMap map = new HashMap();
        
        GazetteerClientConfiguration conf = GazetteerClientConfiguration.getInstance();
        String[] gaze = conf.getGazetteerNames();        
        // perform a request for each registered gazetteer
        for (int i = 0; i < gaze.length; i++) {
        	URL url = conf.getGazetteerAddress( gaze[i] );             
            NetWorker nw = new NetWorker( CharsetUtils.getSystemCharset(), url, request);
            InputStreamReader isr = null;
            try {
            	isr = new InputStreamReader( nw.getInputStream(), CharsetUtils.getSystemCharset() );
            } catch(Exception e) {
            	throw new GazetteerClientException( "Couldn't open stream from WFS-G", e );
            }
            try {                          
                Document doc = XMLTools.parse( isr );
                map.put( gaze[i], doc );
            } catch(Exception e) {
                throw new GazetteerClientException( "Couldn't parse result from WFS-G", e );
            }
        }
                
        Debug.debugMethodEnd();
        return map;
    }
    
    /**
     * creates the result object to send to the next page from the parameters
     * contained in the passed <tt>RPCMethodeCall</tt> and the <tt>Document</tt> 
     * array.  
     * 
     * @param mc 
     * @param map
     * @return
     */
    private GetTermsResultSet createResultSet( RPCMethodCall mc, HashMap map ) throws GazetteerClientException {
        Debug.debugMethodBegin();
        
        NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();
        try {
            SI_LocationInstance sil[];
            Iterator iterator = map.keySet().iterator();
            while ( iterator.hasNext() ) {
                Object key = iterator.next();
                Element root = ((Document)map.get( key )).getDocumentElement();
                List list = XMLTools.getNodes( root, "/*/gml:featureMember/child::*", nsContext );
                sil = new SI_LocationInstance[list.size()];            
                for(int i = 0; i < list.size(); i++) {
                    String gi = XMLTools.getNodeAsString( (Node)list.get(i), 
                                            "geographicIdentifier", nsContext, null );
                    String id = XMLTools.getNodeAsString( (Node)list.get(i), "identifier", 
                                                           nsContext, null);
                    Node node = XMLTools.getNode( (Node)list.get(i), "geographicExtent/gml:Polygon", 
                                                  nsContext);
                    Geometry geom = null;
                    if(node != null) {
                        geom = GMLGeometryAdapter.wrap( (Element)node );
                    }
                    sil[i] = new SI_LocationInstance( id, gi, geom );
                }
                map.put( key, sil );
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new GazetteerClientException("Couldn't create location instances", e);
        }
        RPCParameter[] params = mc.getParameters();
        RPCStruct struct = (RPCStruct)params[0].getValue();
        String st = null;
        if ( struct.getMember( Constants.SOURCETYPE ) != null ) {
            st = (String)struct.getMember( Constants.SOURCETYPE ).getValue();
        }
        String sv = null;
        if ( struct.getMember( Constants.SOURCEVALUE ) != null ) {
            sv = (String)struct.getMember( Constants.SOURCEVALUE ).getValue();
        }
        String tt = null;
        if ( struct.getMember( Constants.TARGETTYPE ) != null ) {
            tt = (String)struct.getMember( Constants.TARGETTYPE ).getValue();
        }     
        GetTermsResultSet rs = new GetTermsResultSet( st, sv, tt, map );      
        Debug.debugMethodEnd();
        return rs;
    }
    
    /**
     * sets the parameter as ServletRequest attribute to enable access to
     * the result for the next page
     * 
     * @param o param/result
     */
    private void setParamForNextPage( Object o ) {
        this.getRequest().setAttribute( Constants.TERMSRESULTSET, o );
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: GetRelatedTermsListener.java,v $
   Revision 1.8  2006/10/17 20:31:19  poth
   *** empty log message ***

   Revision 1.7  2006/08/29 19:54:14  poth
   footer corrected

   Revision 1.6  2006/06/19 18:47:22  poth
   *** empty log message ***

   Revision 1.5  2006/06/19 06:39:22  poth
   *** empty log message ***

   Revision 1.4  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:12  poth
   *** empty log message ***

   Revision 1.8  2006/01/19 21:24:49  poth
   *** empty log message ***

   Revision 1.7  2005/12/07 10:52:55  poth
   no message

   Revision 1.6  2005/12/06 13:45:20  poth
   System.out.println substituted by logging api

   Revision 1.5  2005/11/30 13:59:16  poth
   no message

   Revision 1.4  2005/08/30 13:40:03  poth
   no message

   Revision 1.3  2005/03/09 11:55:47  mschneider
   *** empty log message ***

   Revision 1.2  2005/01/06 17:51:46  poth
   no message

   Revision 1.7  2004/06/21 08:05:49  ap
   no message

   Revision 1.6  2004/06/11 08:47:30  ap
   no message

   Revision 1.5  2004/06/08 13:03:20  tf
   refactor to org.deegree.enterprise

   Revision 1.4  2004/06/03 09:02:20  ap
   no message

   Revision 1.3  2004/05/24 15:44:56  ap
   no message

   Revision 1.2  2004/05/24 06:58:47  ap
   no message

   Revision 1.1  2004/05/22 09:55:36  ap
   no message

   Revision 1.5  2004/03/26 16:42:18  poth
   no message

   Revision 1.4  2004/03/26 11:19:28  poth
   no message

   Revision 1.3  2004/03/24 12:36:22  poth
   no message

   Revision 1.2  2004/03/24 08:12:20  poth
   no message

   Revision 1.1  2004/03/16 15:19:47  poth
   no message

   Revision 1.2  2004/03/16 08:07:23  poth
   no message

   Revision 1.1  2004/03/15 07:38:05  poth
   no message



********************************************************************** */
