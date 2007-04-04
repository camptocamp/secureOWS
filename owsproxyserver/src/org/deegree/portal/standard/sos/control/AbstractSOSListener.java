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

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.Debug;
import org.deegree.framework.xml.XMLTools;
import org.deegree.portal.standard.sos.SOSClientException;
import org.deegree.portal.standard.sos.configuration.SOSClientConfiguration;
import org.w3c.dom.Document;

/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public abstract class AbstractSOSListener extends AbstractListener{

    //TODO javadoc
    protected abstract void validateRequest( RPCMethodCall mc ) throws SOSClientException;
    
//  TODO javadoc
    protected abstract String createRequest( RPCMethodCall mc ) throws SOSClientException;

    //  TODO javadoc
    protected abstract Object createData( RPCMethodCall mc, HashMap map ) throws SOSClientException;
    
    /**
	 * sets the parameter as ServletRequest attribute to enable access to
	 * the result for the next page
	 * 
	 * @param o param/result
	 */
    protected abstract void setNextPageData( Object data );
    
	public void actionPerformed(FormEvent e) {

	    RPCWebEvent rpcEvent = (RPCWebEvent)e;
	    RPCMethodCall mc = rpcEvent.getRPCMethodCall();
	    
	    try {
	        validateRequest( mc );            
	    } catch ( Exception ex ) {         
	        gotoErrorPage( "Invalid Sensor Observation Service DescribePlatform request: " 
	                	   + ex.toString() );
	        Debug.debugMethodEnd();
	        return;
	    }
	
	    String request = null;
	    try {
	    	request = createRequest( mc );
	    } catch ( Exception ex ) {    
	        gotoErrorPage( "Couldn't create Sensor Observation Service DescribePlatform request: " 
	                	   + ex.toString() );
	        Debug.debugMethodEnd();
	        return;
	    }
	
	    HashMap map = null;
	    try {
	        map = performRequest( request );
	    } catch ( Exception ex ) {
	        gotoErrorPage( "Couldn't perform Sensor Observation Service DescribePlatform request: " 
	                	   + ex.toString() );
	        Debug.debugMethodEnd();
	        return;
	    }
	
	    Object data = null;
	    try {
	        data = createData( mc, map );
	    } catch ( Exception ex ) {
	        gotoErrorPage( "Couldn't format Sensor Observation Service result: " + ex.toString() );
	        Debug.debugMethodEnd();
	        return;
	    }      
	
//	    setParamForNextPage( key, data );
	    setNextPageData( data );        
	}

    
    // FIXME make return type a "Map"
	protected HashMap performRequest( String request ) throws SOSClientException {
	    Debug.debugMethodBegin();

	    // FIXME or I'm not sure whether we should return a hash map here. Doc will do too
	    HashMap map = new HashMap();
	    
	    SOSClientConfiguration conf = SOSClientConfiguration.getInstance();
	    String[] sosResources = conf.getSOSNames();        
	    // perform a request for each registered sensor observation service
	    for (int i = 0; i < sosResources.length; i++) {
	    	URL url = conf.getSOSAddress( sosResources[i] );  
	    	
			try {
			
		    	HttpClient httpclient = new HttpClient();
	            httpclient.setTimeout( 30000 );
	            
	            PostMethod httppost = new PostMethod( url.toString() );
	            httppost.setRequestBody( request );
	            httpclient.executeMethod(httppost);
	            
	            java.io.InputStream is = httppost.getResponseBodyAsStream();
	            InputStreamReader inputStreamRdr = new InputStreamReader( is );
	            Document doc = XMLTools.parse( inputStreamRdr );
	            	            
	            map.put( sosResources[i], doc );
	            
	            
			} catch (Exception e) {
			    throw new SOSClientException( "Couldn't connect to " +
					  "Observation Service at " + url.toString(), e );
			}
	    }
	            
	    Debug.debugMethodEnd();
	    return map;
	}
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractSOSListener.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
