//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/ISO19119RequestFactory.java,v 1.11 2006/07/31 11:02:44 mays Exp $
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;

/**
 * A <code>${type_name}</code> class.<br/>
 * TODO class description
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.11 $, $Date: 2006/07/31 11:02:44 $
 * 
 * @since 2.0
 */
public class ISO19119RequestFactory extends CSWRequestFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( ISO19119RequestFactory.class );
    
//    private static final char WILDCARD = '*';
    private static final String OUTPUTSCHEMA = "csw:profile";

    private RPCStruct struct = null;
    private Properties requestElementsProps = new Properties();
    
    public ISO19119RequestFactory() {
        try {
            InputStream is = 
                ISO19119RequestFactory.class.getResourceAsStream("ISO19119requestElements.properties");
            this.requestElementsProps.load( is );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * creates a GetRecord request that is conform to the OGC Stateless Web
     * Service Catalog Profil and GDI NRW catalog specifications from a RPC struct.     
     *
     * @param struct RPC structure containing the request parameter
     * @return GetFeature request as a string
     */
    public String createRequest( RPCStruct struct, String resultType ) {
        LOG.entering();

        this.struct = struct;
        
        InputStream is = null;
        InputStreamReader ireader = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        String request = null;
        
        is = ISO19119RequestFactory.class.getResourceAsStream( "CSWGetRecordsTemplate.xml" );
//		  is = ISO19119RequestFactory.class.getResourceAsStream( "CSWGetRecordByIdTemplate.xml" );
        
        try {
            ireader = new InputStreamReader( is );
            br = new BufferedReader( ireader );
            sb = new StringBuffer( 50000 );
            
            while (( request = br.readLine() ) != null) {
                sb.append( request );
            }
            request = sb.toString();
            br.close();
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        
        request = replaceVarsInSearchRequest( request, resultType );
//            request = replaceVarsInOverviewRequest( request );
        
        LOG.exiting();
        return request;
    }


    private String replaceVarsInSearchRequest( String request, String resultType ) {
        
        String filter = createFilterEncoding();
        request = request.replaceFirst( "\\$FILTER", filter );
      
        request = request.replaceFirst( "\\$OUTPUTSCHEMA", OUTPUTSCHEMA );
        
        request = request.replaceFirst( "\\$RESULTTYPE", resultType );
      
        String startPos = "0";
        if ( struct.getMember( RPC_STARTPOSITION ) != null ) {
            startPos = (String)struct.getMember( RPC_STARTPOSITION ).getValue();
        }
        request = request.replaceFirst( "\\$STARTPOSITION", startPos );
      
        String maxRecords = Integer.toString( config.getMaxRecords() ); // default is 10 (according to spec)
        request = request.replaceFirst( "\\$MAXRECORDS", maxRecords );
      
        String queryType = "csw:service"; // dataset, dataseries, service, application
        if ( struct.getMember( RPC_TYPENAME ) != null ) {
            queryType = (String)struct.getMember( RPC_TYPENAME ).getValue();
        }
        request = request.replaceFirst( "\\$TYPENAME", queryType );
      
        String elementSet = "brief"; // brief, summary, full
        if ( struct.getMember( RPC_ELEMENTSETNAME ) != null ) {
            elementSet = (String)struct.getMember( RPC_ELEMENTSETNAME ).getValue();
        }
        request = request.replaceFirst( "\\$ELEMENTSETNAME", elementSet );
        
        return request;
    }

    private String createFilterEncoding() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 2000 );
        int expCounter = 0;

        sb.append( "<csw:Constraint><ogc:Filter>" );
        
        // build filter encoding structure, handle all known fields sequentially              
        String s = null;
        
        s = handleServiceSearch();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        // NOTE: if some of the methods below are needed, 
        // copy them from ISO19115RequestFactory and adapt them where needed.
        
//      s = handleFileIdentifier();
//      if ( ( s != null ) && ( s.length() > 0 ) ) {
//          expCounter++;
//          sb.append( s );
//      }
      
//      s = handleParentIdentifier();
//      if ( ( s != null ) && ( s.length() > 0 ) ) {
//          expCounter++;
//          sb.append( s );
//      }
        
//        s = handleKeywords();
//        if ( ( s != null ) && ( s.length() > 0 ) ) {
//            expCounter++;
//            sb.append( s );
//        }
        
//        s = handleDate();
//        if ( ( s != null ) && ( s.length() > 0 ) ) {
//            expCounter++;
//            sb.append( s );
//        }
        
//        s = handleBbox();
//        if ( ( s != null ) && ( s.length() > 0 ) ) {
//            expCounter++;
//            sb.append( s );
//        }

        if ( expCounter > 1 ) {
            sb.insert( "<ogc:Constraint><ogc:Filter>".length(), "<ogc:And>" );
            sb.append( "</ogc:And>" );
        }

        sb.append( "</ogc:Filter></csw:Constraint>" );

        LOG.exiting();

        return sb.toString();
    }
    
    /**
     * @return Returns a string containing the service search part of the filter condition.
     */
    private String handleServiceSearch() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 2000 );
        
        String[] t = null;
        if ( struct.getMember( Constants.RPC_SERVICESEARCH ) != null ) {
            String s = (String)struct.getMember( Constants.RPC_SERVICESEARCH ).getValue();
            t = StringTools.toArray( s, "|", true );                
        }
        
        if ( ( t != null ) && ( t.length > 0 ) ) {
//            sb.append( "<ogc:Or>" );
            for ( int i = 0; i < t.length; i++ ) {
                if ( ( t[i] != null ) && ( t[i].length() > 0 ) ) {
                    // replace invalid chars
                    // t[i] = StringExtend.replace( t[i], "'", " ", true );
                    // t[i] = StringExtend.replace( t[i], "\"", " ", true );

                    // determine the way to build FilterEncoding part
                    String cf_props = requestElementsProps.getProperty( Constants.CONF_SERVICESEARCH );
                    String[] cf = cf_props.split(";");
                    
                    for ( int k = 0; k < cf.length; k++ ) {
                        String strOp = t[i];
                        if ( ( strOp != null ) && ( strOp.length() > 0 ) ) {
                            Operation op = 
                                createOperation( OperationDefines.PROPERTYISEQUALTO, cf[k], strOp );
                            sb.append( op.toXML() );
                        }
                    }
                }
            }
//            sb.append( "</ogc:Or>" );
        }

        LOG.exiting();
        return sb.toString();
    }
    
    private Operation createOperation( int opId, String property, Object value ) {
        LOG.entering();

        Operation op = null;

        switch ( opId ) {
            case OperationDefines.PROPERTYISEQUALTO:
                op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISEQUALTO, 
                                                  new PropertyName( new QualifiedName( property ) ), 
                                                  new Literal( (String)value ) );
                break;
                
//            case OperationDefines.PROPERTYISLIKE:
//                char wildCard = WILDCARD;
//                char singleChar = '?';
//                char escapeChar = '/';
//                String lit = wildCard + (String)value + wildCard;
//                op = new PropertyIsLikeOperation( new PropertyName( new QualifiedName( property ) ), 
//                                                  new Literal( lit ), wildCard, singleChar, escapeChar );
//                break;
//            case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
//                op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHANOREQUALTO, 
//                                                  new PropertyName( new QualifiedName( property ) ), 
//                                                  new Literal( (String)value ) );
//                break;
//            case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
//                op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISGREATERTHANOREQUALTO, 
//                                                  new PropertyName( new QualifiedName( property ) ), 
//                                                  new Literal( (String)value ) );
//                break;
//            case OperationDefines.BBOX:
//                op = new SpatialOperation( OperationDefines.BBOX, 
//                                           new PropertyName( new QualifiedName( property ) ), 
//                                           (Geometry)value );
//                break;
//            case OperationDefines.PROPERTYISNULL:
//                op = new PropertyIsNullOperation( new PropertyName( new QualifiedName( property ) ) );
//                break;
    
            default:
                op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISEQUALTO, 
                                                  new PropertyName( new QualifiedName( property ) ), 
                                                  new Literal( (String)value ) );
        }

        LOG.exiting();
        return op;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ISO19119RequestFactory.java,v $
Revision 1.11  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.10  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.9  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
