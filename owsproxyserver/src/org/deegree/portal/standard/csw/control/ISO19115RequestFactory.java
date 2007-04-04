//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/ISO19115RequestFactory.java,v 1.18 2006/11/27 09:07:53 poth Exp $
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
 Aennchenstr. 19
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
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyIsNullOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.portal.standard.csw.CatalogClientException;

/**
 * A <code>${type_name}</code> class.<br/>
 * 
 * class for creating a get GetRecord Request against a catalog based on OGC
 * Stateless Web Service Catalog Profil and GDI NRW catalog specifications to
 * access data metadata (ISO 19115).<p>
 * The only public method of the class receives a 'model' represented by a
 * <tt>HashMap</tt> that contains the request parameters as name-value-pairs.
 * The names corresponds to the form-field-names. For common this will be
 * the fields of a HTML-form but it can be any other form (e.g. swing-application)
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.18 $, $Date: 2006/11/27 09:07:53 $
 */
public class ISO19115RequestFactory extends CSWRequestFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( ISO19115RequestFactory.class );

    static final String RPC_SIMPLESEARCH = "RPC_SIMPLESEARCH";

    private static final char WILDCARD = '*';

    private static final String OUTPUTSCHEMA = "csw:profile";

    private RPCStruct struct = null;

    private Properties requestElementsProps = new Properties();

    public ISO19115RequestFactory() {
        try {
            InputStream is = ISO19115RequestFactory.class.getResourceAsStream( "ISO19115requestElements.properties" );
            this.requestElementsProps.load( is );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * creates a GetRecord request that is conform to the OGC Stateless Web
     * Service Catalog Profil and GDI NRW catalog specifications from a RPC struct.     
     *
     * @param struct RPC structure containing the request parameter
     * @return GetFeature request as a string
     * @throws CatalogClientException 
     */
    public String createRequest( RPCStruct struct, String resultType )
                            throws CatalogClientException {

        LOG.entering();

        this.struct = struct;
        boolean isSearchRequest = false;
        boolean isOverviewRequest = false;

        if ( "HITS".equals( resultType ) || "RESULTS".equals( resultType ) ) {
            isSearchRequest = true;
        } else if ( resultType == null ) {
            isOverviewRequest = true;
        }

        InputStream is = null;
        InputStreamReader ireader = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        String request = null;

        if ( isSearchRequest ) {
            is = ISO19115RequestFactory.class.getResourceAsStream( "CSWGetRecordsTemplate.xml" );
        } else if ( isOverviewRequest ) {
            is = ISO19115RequestFactory.class.getResourceAsStream( "CSWGetRecordByIdTemplate.xml" );
        }

        try {
            ireader = new InputStreamReader( is );
            br = new BufferedReader( ireader );
            sb = new StringBuffer( 50000 );

            while ( ( request = br.readLine() ) != null ) {
                sb.append( request );
            }
            request = sb.toString();
            br.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        if ( isSearchRequest ) {
            try {
                request = replaceVarsInSearchRequest( request, resultType );
            } catch ( UnknownCRSException e ) {
                throw new CatalogClientException( e.getMessage(), e );
            }
        } else if ( isOverviewRequest ) {
            request = replaceVarsInOverviewRequest( request );
        }

        LOG.exiting();
        return request;
    }

    /**
     * @param request
     * @param resultType
     * @return Returns the request, where all variables are replaced by values.
     * @throws CatalogClientException
     * @throws UnknownCRSException 
     */
    private String replaceVarsInSearchRequest( String request, String resultType )
                            throws CatalogClientException, UnknownCRSException {
        LOG.entering();

        // replace variables from template

        String filter = createFilterEncoding();
        request = request.replaceFirst( "\\$FILTER", filter );

        request = request.replaceFirst( "\\$OUTPUTSCHEMA", OUTPUTSCHEMA );

        request = request.replaceFirst( "\\$RESULTTYPE", resultType );

        // According to OGC CSW-spec default is 1
        String startPos = "1";
        if ( struct.getMember( RPC_STARTPOSITION ) != null ) {
            startPos = (String) struct.getMember( RPC_STARTPOSITION ).getValue();
        }
        request = request.replaceFirst( "\\$STARTPOSITION", startPos );

        // According to OGC CSW-spec default is 10
        String maxRecords = Integer.toString( config.getMaxRecords() );
        request = request.replaceFirst( "\\$MAXRECORDS", maxRecords );

        String queryType = "csw:dataset"; // dataset, dataseries, service, application
        if ( struct.getMember( RPC_TYPENAME ) != null ) {
            queryType = (String) struct.getMember( RPC_TYPENAME ).getValue();
        }
        request = request.replaceFirst( "\\$TYPENAME", queryType );

        String elementSet = "brief"; // brief, summary, full
        if ( struct.getMember( RPC_ELEMENTSETNAME ) != null ) {
            elementSet = (String) struct.getMember( RPC_ELEMENTSETNAME ).getValue();
        }
        request = request.replaceFirst( "\\$ELEMENTSETNAME", elementSet );

        LOG.exiting();
        return request;
    }

    /**
     * @param request
     * @return Returns the request, where all variables are replaced by values.
     * @throws CatalogClientException
     */
    private String replaceVarsInOverviewRequest( String request )
                            throws CatalogClientException {
        LOG.entering();

        String id;
        if ( struct.getMember( Constants.RPC_IDENTIFIER ) != null ) {
            id = (String) struct.getMember( Constants.RPC_IDENTIFIER ).getValue();
        } else {
            throw new CatalogClientException( "Identifier is not set in RPC request." );
        }
        request = request.replaceFirst( "\\$IDENTIFIER", id );

        request = request.replaceFirst( "\\$OUTPUTSCHEMA", OUTPUTSCHEMA );

        String elementSet = "full"; // brief, summary, full
        if ( struct.getMember( RPC_ELEMENTSETNAME ) != null ) {
            elementSet = (String) struct.getMember( RPC_ELEMENTSETNAME ).getValue();
        }
        request = request.replaceFirst( "\\$ELEMENTSETNAME", elementSet );

        LOG.exiting();
        return request;
    }

    /**
     * takes RequestModel and builds a String result out of it.
     * The result should be OGC FilterEncoding conformant.
     * 
     * @return Returns the fragment for filter encoding.
     * @throws CatalogClientException 
     * @throws UnknownCRSException 
     */
    private String createFilterEncoding()
                            throws CatalogClientException, UnknownCRSException {
        //Debug.level = Debug.ALL;
        LOG.entering();

        StringBuffer sb = new StringBuffer( 2000 );
        int expCounter = 0;

        sb.append( "<csw:Constraint><ogc:Filter>" );

        // build filter encoding structure, handle all known fields sequentially              
        String s = handleFileIdentifier();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleParentIdentifier();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleSimpleSearch();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleTopiccategory();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleKeywords();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleDate();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        s = handleBbox();
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            expCounter++;
            sb.append( s );
        }

        if ( expCounter > 1 ) {
            sb.insert( "<ogc:Constraint><ogc:Filter>".length(), "<ogc:And>" );
            sb.append( "</ogc:And>" );
        }

        sb.append( "</ogc:Filter></csw:Constraint>" );

        LOG.exiting();

        return sb.toString();
    }

    /**
     * Build OGC Filterencoding fragment:
     * use <code>CSWRequestmodel</code> field <b>fileIdentifier</b> to create Comparison Operation.
     * 
     * @return Returns the fragment for fileIdentifier.
     *         May be empty.
     */
    private String handleFileIdentifier() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 1000 );

        String id = null;
        if ( struct.getMember( Constants.RPC_IDENTIFIER ) != null ) {
            id = (String) struct.getMember( Constants.RPC_IDENTIFIER ).getValue();
        }

        if ( ( id != null ) && ( id.trim().length() > 0 ) ) {
            String cf_props = requestElementsProps.getProperty( Constants.CONF_IDENTIFIER );
            String[] cf = cf_props.split( ";" );

            sb = new StringBuffer( 1000 );
            Operation op1 = createOperation( OperationDefines.PROPERTYISEQUALTO, cf[0], id );
            sb.append( op1.toXML() );
        }

        LOG.exiting();
        return sb.toString();
    }

    /**
     * Build OGC Filterencoding fragment:
     * use <code>CSWRequestmodel</code> field <b>parentIdentifier</b> to create Comparison Operation.
     * 
     * @return Returns the fragment for parentIdentifier. 
     *         May be empty.
     */
    private String handleParentIdentifier() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 1000 );
        String id = null;
        if ( struct.getMember( RPC_DATASERIES ) != null ) {
            id = (String) struct.getMember( RPC_DATASERIES ).getValue();
        }

        if ( ( id != null ) && ( id.trim().length() > 0 ) ) {
            String cf_props = requestElementsProps.getProperty( CONF_DATASERIES );
            String[] cf = cf_props.split( ";" );

            sb = new StringBuffer( 1000 );
            Operation op1 = createOperation( OperationDefines.PROPERTYISEQUALTO, cf[0], id );
            sb.append( op1.toXML() );
        }

        LOG.exiting();
        return sb.toString();
    }

    /**
     * Spread <code>CSWRequestmodel</code> field <b>terms</b> to several Comparison Operations 
     * with pre-defined Property names.
     * 
     * @return Returns the fragment for the search string. 
     *         May be empty.
     */
    private String handleSimpleSearch() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 2000 );

        String[] t = null;
        if ( struct.getMember( RPC_SIMPLESEARCH ) != null ) {
            String s = (String) struct.getMember( RPC_SIMPLESEARCH ).getValue();
            t = StringTools.toArray( s, ",;|", true );
        }

        if ( ( t != null ) && ( t.length > 0 ) ) {
            sb.append( "<ogc:Or>" );

            for ( int i = 0; i < t.length; i++ ) {
                // replace invalid chars
                if ( ( t[i] != null ) && ( t[i].length() > 0 ) ) {
                    t[i] = StringTools.replace( t[i], "'", " ", true );
                    t[i] = StringTools.replace( t[i], "\"", " ", true );

                    // determine the way to build FilterEncoding part
                    String cf_props = requestElementsProps.getProperty( Constants.CONF_SIMPLESEARCH );
                    String[] cf = cf_props.split( ";" );

                    for ( int k = 0; k < cf.length; k++ ) {
                        String strOp = t[i];

                        if ( ( strOp != null ) && ( strOp.length() > 0 ) ) {
                            // LOWERCASE SECTION
                            strOp = strOp.substring( 0, 1 ).toLowerCase() + strOp.substring( 1 );

                            Operation op = createOperation( OperationDefines.PROPERTYISLIKE, cf[k],
                                                            strOp );
                            sb.append( op.toXML() );

                            // FIRST LETTER UPPERCASE SECTION
                            strOp = strOp.substring( 0, 1 ).toUpperCase() + strOp.substring( 1 );
                            op = createOperation( OperationDefines.PROPERTYISLIKE, cf[k], strOp );
                            sb.append( op.toXML() );
                        }
                    }
                }
            }
            sb.append( "</ogc:Or>" );
        }

        LOG.exiting();
        return sb.toString();
    }

    /**
     * Builds OGC Filterencoding fragment: 
     * for <code>CSWRequestmodel</code> field <b>topiccategory</b>.
     * 
     * @return Returns the fragment for topiccategory. 
     *         May be null, if no topiccategory is specified.
     */
    private String handleTopiccategory() {
        LOG.entering();

        String tc = null;
        if ( struct.getMember( RPC_TOPICCATEGORY ) != null ) {
            tc = (String) struct.getMember( RPC_TOPICCATEGORY ).getValue();
        }

        if ( tc != null && !tc.startsWith( "..." ) && tc.length() > 0 ) {
            String cf_props = requestElementsProps.getProperty( Constants.CONF_TOPICCATEGORY );
            String[] cf = cf_props.split( ";" );

            Operation op1 = createOperation( OperationDefines.PROPERTYISEQUALTO, cf[0], tc );
            tc = op1.toXML().toString();
        } else {
            tc = null;
        }

        LOG.exiting();
        return tc;
    }

    /**
     * Build OGC Filterencoding fragment:
     * Split <code>CSWRequestmodel</code> field <b>keywords</b> to one Comparison Operation for 
     * each keyword.
     * 
     * @return Returns the fragment for keywords. 
     *         May be empty, if no keywords are specified.
     */
    private String handleKeywords() {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 1000 );
        String[] tc = null;
        if ( struct.getMember( RPC_KEYWORDS ) != null ) {
            String s = (String) struct.getMember( RPC_KEYWORDS ).getValue();
            tc = StringTools.toArray( s, ",;", true );
        }

        if ( ( tc != null ) && ( tc.length > 0 ) ) {
            String cf_props = requestElementsProps.getProperty( Constants.CONF_KEYWORDS );
            String[] cf = cf_props.split( ";" );

            sb = new StringBuffer( 1000 );
            int i = 0;

            for ( i = 0; i < tc.length; i++ ) {
                if ( tc[i].trim().length() > 0 ) {
                    Operation op1 = createOperation( OperationDefines.PROPERTYISEQUALTO, cf[0],
                                                     tc[i] );
                    sb.append( op1.toXML() );
                }
            }

            if ( i > 1 ) {
                sb.insert( 0, "<ogc:Or>" );
                sb.append( "</ogc:Or>" );
            }
        }

        LOG.exiting();
        return sb.toString();
    }

    /**
     * Build OGC Filterencoding fragment:
     * use <code>dateFrom</code> and <code>dateTo</code> to create Comparison Operations.
     * 
     * @return Returns the fragment for dates specified in the <code>RPCStruct</code>. 
     *         May be null, if no dates are specified.
     */
    private String handleDate() {
        LOG.entering();

        String s = null;

        if ( struct.getMember( Constants.RPC_DATEFROM ) == null
             && struct.getMember( Constants.RPC_DATETO ) == null ) {
            LOG.exiting();
            return s;
        }

        // RPC_DATEFROM
        String fy = null;
        String fm = null;
        String fd = null;

        if ( struct.getMember( Constants.RPC_DATEFROM ) != null ) {
            RPCStruct st = (RPCStruct) struct.getMember( Constants.RPC_DATEFROM ).getValue();
            if ( st.getMember( Constants.RPC_YEAR ) != null ) {
                fy = st.getMember( Constants.RPC_YEAR ).getValue().toString();
            }
            if ( st.getMember( Constants.RPC_MONTH ) != null ) {
                fm = st.getMember( Constants.RPC_MONTH ).getValue().toString();
            }
            if ( st.getMember( Constants.RPC_DAY ) != null ) {
                fd = st.getMember( Constants.RPC_DAY ).getValue().toString();
            }
        }

        if ( fy == null ) {
            fy = "0000";
        }
        if ( fm == null ) {
            fm = "1";
        }
        if ( Integer.parseInt( fm ) < 10 ) {
            fm = "0" + Integer.parseInt( fm );
        }
        if ( fd == null ) {
            fd = "1";
        }
        if ( Integer.parseInt( fd ) < 10 ) {
            fd = "0" + Integer.parseInt( fd );
        }
        String df = fy + "-" + fm + "-" + fd;

        //RPC_DATETO
        String ty = null;
        String tm = null;
        String td = null;

        if ( struct.getMember( Constants.RPC_DATETO ) != null ) {
            RPCStruct st = (RPCStruct) struct.getMember( Constants.RPC_DATETO ).getValue();
            if ( st.getMember( Constants.RPC_YEAR ) != null ) {
                ty = st.getMember( Constants.RPC_YEAR ).getValue().toString();
            }
            if ( st.getMember( Constants.RPC_MONTH ) != null ) {
                tm = st.getMember( Constants.RPC_MONTH ).getValue().toString();
            }
            if ( st.getMember( Constants.RPC_DAY ) != null ) {
                td = st.getMember( Constants.RPC_DAY ).getValue().toString();
            }
        }

        if ( ty == null ) {
            ty = "9999";
        }
        if ( tm == null ) {
            tm = "12";
        }
        if ( Integer.parseInt( tm ) < 10 ) {
            tm = "0" + Integer.parseInt( tm );
        }
        if ( td == null ) {
            td = "31";
        }
        if ( Integer.parseInt( td ) < 10 ) {
            td = "0" + Integer.parseInt( td );
        }
        String dt = ty + "-" + tm + "-" + td;

        String date_props = requestElementsProps.getProperty( Constants.CONF_DATE );
        String[] conf_date = date_props.split( ";" );

        if ( ( ty != null ) && ( ty.length() > 0 ) ) {
            StringBuffer sb = new StringBuffer( "<ogc:And>" );

            Operation op1 = null;
            op1 = createOperation( OperationDefines.PROPERTYISGREATERTHANOREQUALTO, conf_date[0],
                                   df );
            sb.append( op1.toXML() );
            op1 = createOperation( OperationDefines.PROPERTYISLESSTHANOREQUALTO, conf_date[0], dt );
            sb.append( op1.toXML() );

            sb.append( "</ogc:And>" );
            s = sb.toString();
        }

        LOG.exiting();
        return s;
    }

    /**
     * Build OGC Filterencoding fragment:
     * use <code>CSWRequestmodel</code> field <b>geographicBox</b> to create Comparison Operation.
     * 
     * @return Returns the fragment for the geographic bounding box.
     *         May be empty, if no bounding box is specified.
     * @throws CatalogClientException 
     * @throws UnknownCRSException 
     */
    private String handleBbox()
                            throws CatalogClientException, UnknownCRSException {
        LOG.entering();

        StringBuffer sb = new StringBuffer( 1000 );
        if ( struct.getMember( Constants.RPC_BBOX ) != null ) {
            RPCStruct bboxStruct = (RPCStruct) struct.getMember( Constants.RPC_BBOX ).getValue();

            Double minx = (Double) bboxStruct.getMember( Constants.RPC_BBOXMINX ).getValue();
            Double miny = (Double) bboxStruct.getMember( Constants.RPC_BBOXMINY ).getValue();
            Double maxx = (Double) bboxStruct.getMember( Constants.RPC_BBOXMAXX ).getValue();
            Double maxy = (Double) bboxStruct.getMember( Constants.RPC_BBOXMAXY ).getValue();

            // FIXME check if srs is correct
            CoordinateSystem srs = CRSFactory.create( config.getSrs() );
            Envelope bbox = GeometryFactory.createEnvelope( minx.doubleValue(), miny.doubleValue(),
                                                            maxx.doubleValue(), maxy.doubleValue(),
                                                            srs );
            try {
                // transform request boundingbox to EPSG:4326 because a ISO 19115
                // compliant catalog must store the bbox of an entry like this
                IGeoTransformer gt = new GeoTransformer( "EPSG:4326" );
                bbox = gt.transform( bbox, config.getSrs() );
            } catch ( Exception e ) {
                throw new CatalogClientException( e.toString() );
            }

            Geometry boxGeom = null;
            try {
                boxGeom = GeometryFactory.createSurface( bbox, srs );
            } catch ( GeometryException e ) {
                e.printStackTrace();
                throw new CatalogClientException( "Cannot create surface from bbox."
                                                  + e.getMessage() );
            }

            String reProps = requestElementsProps.getProperty( Constants.CONF_GEOGRAPHICBOX );
            String[] re = reProps.split( ";" );

            if ( boxGeom != null ) {
                Operation op1 = createOperation( OperationDefines.BBOX, re[0], boxGeom );
                sb.append( op1.toXML() );
            }
        }

        LOG.exiting();
        return sb.toString();
    }

    //    /**
    //     * @param bbox The bounding box to be used as filter condition.
    //     * @return Returns the GML bounding box snippet.
    //     */
    //    private String createGMLBox( Envelope bbox ) {
    //        StringBuffer sb = new StringBuffer( 1000 );
    //
    //        sb.append( "<gml:Box xmlns:gml=\"http://www.opengis.net/gml\" >" );
    //        sb.append( "<gml:coord><gml:X>" );
    //        sb.append( "" + bbox.getMin().getX() );
    //        sb.append( "</gml:X><gml:Y>" );
    //        sb.append( "" + bbox.getMin().getY() );
    //        sb.append( "</gml:Y></gml:coord><gml:coord><gml:X>" );
    //        sb.append( "" + bbox.getMax().getX() );
    //        sb.append( "</gml:X><gml:Y>" );
    //        sb.append( "" + bbox.getMax().getY() );
    //        sb.append( "</gml:Y></gml:coord></gml:Box>" );
    //
    //        return sb.toString();
    //    }

    /**
     * @param opId
     * @param property
     * @param value
     * @return Returns the operation to create.
     */
    private Operation createOperation( int opId, String property, Object value ) {
        LOG.entering();

        Operation op = null;

        switch ( opId ) {
        case OperationDefines.PROPERTYISEQUALTO:
            op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISEQUALTO,
                                              new PropertyName( new QualifiedName( property ) ),
                                              new Literal( (String) value ) );
            break;
        case OperationDefines.PROPERTYISLIKE:

            char wildCard = WILDCARD;
            char singleChar = '?';
            char escapeChar = '/';
            String lit = wildCard + (String) value + wildCard;
            op = new PropertyIsLikeOperation( new PropertyName( new QualifiedName( property ) ),
                                              new Literal( lit ), wildCard, singleChar, escapeChar );
            break;
        case OperationDefines.PROPERTYISLESSTHANOREQUALTO:
            op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHANOREQUALTO,
                                              new PropertyName( new QualifiedName( property ) ),
                                              new Literal( (String) value ) );
            break;
        case OperationDefines.PROPERTYISGREATERTHANOREQUALTO:
            op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISGREATERTHANOREQUALTO,
                                              new PropertyName( new QualifiedName( property ) ),
                                              new Literal( (String) value ) );
            break;
        case OperationDefines.BBOX:
            op = new SpatialOperation( OperationDefines.BBOX,
                                       new PropertyName( new QualifiedName( property ) ),
                                       (Geometry) value );
            break;
        case OperationDefines.PROPERTYISNULL:
            op = new PropertyIsNullOperation( new PropertyName( new QualifiedName( property ) ) );
            break;
        default:
            op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISEQUALTO,
                                              new PropertyName( new QualifiedName( property ) ),
                                              new Literal( (String) value ) );
        }

        LOG.exiting();
        return op;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ISO19115RequestFactory.java,v $
 Revision 1.18  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.17  2006/10/12 13:08:58  mays
 bugfix: according to OGC CSW-spec default value for startPosition is 1.

 Revision 1.16  2006/09/27 16:46:41  poth
 transformation method signature changed

 Revision 1.15  2006/07/31 11:02:44  mays
 move constants from class Constants to the classes where they are needed

 Revision 1.14  2006/07/31 09:33:58  mays
 move Constants to package control, update imports

 Revision 1.13  2006/07/05 10:20:57  mays
 remove sysout

 Revision 1.12  2006/06/30 08:43:19  mays
 clean up code and java doc

 Revision 1.11  2006/06/23 13:38:25  mays
 add/update csw control files

 ********************************************************************** */
