//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/DetailedSearchListener.java,v 1.12 2006/11/27 09:07:53 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de 

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

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

package org.deegree.portal.standard.csw.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.model.DataSessionRecord;

/**
 * A <code>${type_name}</code> class.<br/>
 * 
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.12 $, $Date: 2006/11/27 09:07:53 $
 * 
 * @since 2.0
 */
public class DetailedSearchListener extends SimpleSearchListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( DetailedSearchListener.class );

    // needs to be public for jsp pages
    public static final String SESSION_DETAILEDSEARCHPARAM = "DETAILEDSEARCHPARAM";
    
    protected Envelope bbox = null;
    
    public void actionPerformed( FormEvent event ) {

        super.actionPerformed( event );
        
        if ( bbox != null ) {
            HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
            
            List dsrList = (ArrayList)session.getAttribute( SESSION_DATARECORDS );
            if ( dsrList != null && dsrList.size() > 0 ) {
                dsrList = addBoundingBox( dsrList, bbox );
            }
            session.setAttribute( SESSION_DATARECORDS, dsrList );
        }
    }
    
    /**
     * validates the request to be performed.
     *
     * @param rpcEvent event object containing the request to be performed
     */
    protected void validateRequest( RPCWebEvent rpcEvent ) throws CatalogClientException {
        LOG.entering();

        // validity check for common search variables
        super.validateRequest( rpcEvent ); 

        // validity check for specific detailed search variables
        RPCStruct struct = extractRPCStruct( rpcEvent, 1 );

        // validity check for date values
        RPCMember dateFromMem = struct.getMember( Constants.RPC_DATEFROM );
        RPCMember dateToMem = struct.getMember( Constants.RPC_DATETO );
        validateDates ( dateFromMem, dateToMem );
        
        // create envelope if a bounding box was part of the request
        if ( struct.getMember( Constants.RPC_BBOX ) != null ) {
            
            RPCStruct bboxStruct = (RPCStruct)extractRPCMember( struct, Constants.RPC_BBOX );
            
            Double minx = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMINX );
            Double miny = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMINY );
            Double maxx = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMAXX );
            Double maxy = (Double)extractRPCMember( bboxStruct, Constants.RPC_BBOXMAXY );
            
            // FIXME check if srs is correct
            CoordinateSystem srs;
            try {
                srs = CRSFactory.create( config.getSrs() );
            } catch ( UnknownCRSException e ) {
                throw new CatalogClientException( e.getMessage(), e );
            }

            bbox = GeometryFactory.createEnvelope( minx.doubleValue(), miny.doubleValue(),
                                                   maxx.doubleValue(), maxy.doubleValue(), srs );
//        } else {
//            bbox = config.getRootBoundingBox();
        }
        
        // write request parameter into session to reconstruct the search form
        RPCParameter[] params = extractRPCParameters( rpcEvent );
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        session.setAttribute( SESSION_DETAILEDSEARCHPARAM, params );

        LOG.exiting(); 
    }

    
    /**
     * Checks, whether day values are smaler than 32, whether month values are smaller than 13 and 
     * whether the year(from) is smaller than year(to).
     * If not, a CatalogClientException is thrown.
     * 
     * @param dateFromMem
     * @param dateToMem
     * @throws CatalogClientException
     */
    private void validateDates( RPCMember dateFromMem, RPCMember dateToMem ) 
        throws CatalogClientException {
    
        Integer fromYear = null;
        if ( dateFromMem != null ) {
            RPCStruct st = (RPCStruct)dateFromMem.getValue();
            if ( st.getMember( Constants.RPC_YEAR ) != null ) {
                try {
                    fromYear = 
                        Integer.valueOf( st.getMember(Constants.RPC_YEAR).getValue().toString());
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe year is not valid. \n" + e.toString() );
                }
            }
            if ( st.getMember( Constants.RPC_MONTH ) != null ) {
                try {
                    Integer fromMonth = 
                        Integer.valueOf( st.getMember(Constants.RPC_MONTH).getValue().toString());
                    if ( fromMonth.intValue() > 12 || fromMonth.intValue() < 1 ) {
                        throw new Exception();       
                    }
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe month is not valid. \n" + e.toString() );
                }
            }                
            if ( st.getMember( Constants.RPC_DAY ) != null ) {
                try {
                    Integer fromDay = 
                        Integer.valueOf( st.getMember( Constants.RPC_DAY ).getValue().toString());
                    if ( fromDay.intValue() > 31 || fromDay.intValue() < 1 ) {
                        throw new Exception();                   
                    }
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe day is not valid. \n" + e.toString() );
                }
            }
        }
      
        Integer toYear = null;
        if ( dateToMem != null ) {
            RPCStruct st = (RPCStruct)dateToMem.getValue();
            if ( st.getMember( Constants.RPC_YEAR ) != null ) {
                try {
                    toYear = 
                        Integer.valueOf( st.getMember(Constants.RPC_YEAR).getValue().toString() );
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe year is not valid. \n" + e.toString() );
                }
            }
            if ( st.getMember( Constants.RPC_MONTH ) != null ) {
                try {
                    Integer toMonth = 
                        Integer.valueOf( st.getMember(Constants.RPC_MONTH).getValue().toString() );
                    if ( toMonth.intValue() > 12 || toMonth.intValue() < 1 ) {
                        throw new Exception();
                    }
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe month is not valid. \n" + e.toString() );
                }
            }                
            if ( st.getMember( Constants.RPC_DAY ) != null ) {
                try {
                    Integer toDay = 
                        Integer.valueOf( st.getMember( Constants.RPC_DAY ).getValue().toString() );
                    if ( toDay.intValue() > 31 || toDay.intValue() < 1 ) {
                        throw new Exception();                    
                    }
                } catch ( Exception e ) {
                    throw new CatalogClientException( "\nThe day is not valid. \n" + e.toString() );
                }
            }
        }
      
        if ( fromYear != null && toYear != null && fromYear.intValue() > toYear.intValue() ) {
            throw new CatalogClientException( "\nThe period from " + fromYear + " to " + toYear 
                                              + " is not valid.\n" );
        }
    }

    
    /**
     * Adds the passed bounding box to each element of the passed List, that does not already have a 
     * bounding box.
     * 
     * @param dataSessionRecList
     * @param boundingBox
     * @return Returns the passed List, with the passed bounding box having been added to all those 
     *         elements, that did not have a bounding box defined.
     */
    private List addBoundingBox( List dataSessionRecList, Envelope boundingBox ) {
        LOG.entering();
        
        for( int i = 0; i < dataSessionRecList.size(); i++ ) {

            if ( ((DataSessionRecord)dataSessionRecList.get(i)).getBoundingBox() == null ) {
               ((DataSessionRecord)dataSessionRecList.get(i)).setBoundingBox( boundingBox );
            }
        }
        
        LOG.exiting(); 
        return dataSessionRecList;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DetailedSearchListener.java,v $
Revision 1.12  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.11  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.10  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.9  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
