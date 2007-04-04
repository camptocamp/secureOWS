//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/portlet/modules/wfs/actions/portlets/WFSClientPortletPerform_BAK.java,v 1.2 2006/11/27 09:07:52 poth Exp $
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
package org.deegree.portal.portlet.modules.wfs.actions.portlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.jetspeed.portal.Portlet;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.portal.PortalException;
import org.deegree.portal.portlet.modules.actions.IGeoPortalPortletPerform;

/**
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/11/27 09:07:52 $
 *
 * @since 2.0
 */
public class WFSClientPortletPerform_BAK extends IGeoPortalPortletPerform {

    private static final ILogger LOG = LoggerFactory.getLogger( WFSClientPortletPerform_BAK.class );

    protected static final String INIT_TARGETSRS = "TARGETSRS";

    protected static final String INIT_XSLT = "XSLT";

    /**
     * @param request
     * @param portlet
     */
    public WFSClientPortletPerform_BAK( HttpServletRequest request, Portlet portlet,
                                   ServletContext servletContext ) {
        super( request, portlet, servletContext );

    }

    /**
     * 
     * @return
     */
    protected void doGetfeature()
                            throws PortalException, OGCWebServiceException {

        String tmp = (String) parameter.get( "XMLNS" );
        String[] xmlns = StringTools.toArray( tmp, ",", true );

        // featuretypes to query
        tmp = (String) parameter.get( "FEATURETYPES" );
        String[] featureTypes = StringTools.toArray( tmp, ",", true );

        LOG.logDebug( "queried feature types: " + tmp );

        String query = null;
        if ( parameter.get( "QUERYTEMPLATE" ) != null ) {
            query = createQueryFromTemplate();
        } else if ( parameter.get( "FILTER" ) != null ) {
            String filter = (String) parameter.get( "FILTER" );
            query = createQueryFromFilter( featureTypes, xmlns, filter );
        } else {
            String filter = createFilterFromProperties();
            query = createQueryFromFilter( featureTypes, xmlns, filter );
        }

        LOG.logDebug( "Query: \n" + query );

        XMLFragment xml = performQuery( query );
        FeatureCollection fc = null;
        if ( getInitParam( INIT_TARGETSRS ) != null ) {
            try {
                fc = ( (GMLFeatureCollectionDocument) xml ).parse();
                fc = transformGeometries( fc );
            } catch ( XMLParsingException e ) {
                LOG.logError( e.getMessage(), e );
                throw new OGCWebServiceException( "could not parse WFS response as "
                                                  + "FeatureCollection" );
            }
        }
        writeGetFeatureResult( xml, fc );
    }

    /**
     * performs a transaction against a WFS-T or a database. The backend type
     * to be used by a transaction depends on a portlets initParameters. 
     * @throws PortalException 
     */
    public void doTransaction() throws PortalException {
        System.out.println( parameter );
    }

    /**
     * writes the result into the forwarded request object
     * @param xml
     * @param fc
     * @throws PortalException
     */
    private void writeGetFeatureResult( XMLFragment xml, FeatureCollection fc )
                            throws PortalException {
        if ( "XML".equals( parameter.get( "RESULTFORMAT" ) ) ) {
            if ( fc != null ) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
                try {
                    new GMLFeatureAdapter().export( fc, bos );
                    xml.load( new ByteArrayInputStream( bos.toByteArray() ),
                              xml.getSystemId().toExternalForm() );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new PortalException( "could not export feature collection as GML", e );
                }
            }
            if ( getInitParam( INIT_XSLT ) != null ) {
                xml = transform( xml );
            }
            request.setAttribute( "RESULT", xml );
        } else {
            if ( fc == null ) {
                try {
                    //xml.prettyPrint( System.out );                    
                    fc = ( (GMLFeatureCollectionDocument) xml ).parse();
                } catch ( Exception e ) {
                    throw new PortalException( "could not parse XML document"
                                               + " as feature collection ", e );
                }
            }
            request.setAttribute( "RESULT", fc );
        }
    }

    /**
     * transforms the result of a WFS request using the XSLT script defined
     * by an init parameter
     * @param xml
     * @return
     * @throws PortalException
     */
    private XMLFragment transform( XMLFragment xml )
                            throws PortalException {
        String xslF = getInitParam( INIT_XSLT );
        File file = new File( xslF );
        if ( !file.isAbsolute() ) {
            file = new File( sc.getRealPath( xslF ) );
        }
        XSLTDocument xslt = new XSLTDocument();
        try {
            xslt.load( file.toURL() );
            xml = xslt.transform( xml );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not transform result of WFS request", e );
        }
        return xml;
    }

    /**
     * transforms the geometry properties of the features contained in the 
     * passed feature collection into the target CRS given by an init parameter 
     * @param fc
     * @return
     * @throws PortalException
     */
    private FeatureCollection transformGeometries( FeatureCollection fc )
                            throws PortalException {
        String cs = getInitParam( INIT_TARGETSRS );
        CoordinateSystem crs;
        try {
            crs = CRSFactory.create( cs );
        } catch ( UnknownCRSException e1 ) {
            throw new PortalException( e1.getMessage(), e1 );
        }
        if ( crs == null ) {
            throw new PortalException( "CRS: " + cs + " is not known by deegree" );
        }
        try {
            IGeoTransformer gt = new GeoTransformer( crs );
            for ( int i = 0; i < fc.size(); i++ ) {
                Feature feature = fc.getFeature( i );
                FeatureType ft = feature.getFeatureType();
                FeatureProperty[] fp = feature.getProperties();
                for ( int j = 0; j < fp.length; j++ ) {
                    if ( ft.getProperty( fp[j].getName() ).getType() == Types.GEOMETRY ) {
                        Geometry geom = (Geometry) fp[j].getValue();
                        if ( !crs.equals( geom.getCoordinateSystem() ) ) {
                            geom = gt.transform( geom );
                            fp[j].setValue( geom );
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not transform geometries to target CRS: " + cs, e );
        }
        return fc;
    }

    /**
     * performs a GetFeature query against a WFS
     * @param query
     * @return
     * @throws OGCWebServiceException
     */
    private GMLFeatureCollectionDocument performQuery( String query )
                            throws OGCWebServiceException {
        // WFS to contact
        String wfs = (String) parameter.get( "WFS" );
        if ( wfs == null ) {
            // if a client does not send the name of the target WFS
            // 'WFS' will be used to get the target WFS address from
            // the portlets init-parameter
            wfs = "WFS";
        }
        String addr = getInitParam( wfs );
        if ( addr == null ) {
            throw new OGCWebServiceException( "WFS: " + wfs + " is not known by the portal" );
        }
        StringRequestEntity re = new StringRequestEntity( query );
        PostMethod post = new PostMethod( addr );
        post.setRequestEntity( re );
        InputStream is = null;
        try {
            HttpClient client = new HttpClient();
            client.executeMethod( post );
            is = post.getResponseBodyAsStream();
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "could not perform query against the WFS: " + wfs );
        }
        GMLFeatureCollectionDocument xml = new GMLFeatureCollectionDocument();
        try {
            xml.load( is, addr );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new OGCWebServiceException( "could not parse response from WFS: " + wfs
                                              + " as XML" );
        }
        return xml;
    }

    /**
     * creates a WFS GetFeature query from a named template and
     * a set of KVP-encoded properties  
     * @return
     */
    private String createQueryFromTemplate()
                            throws PortalException {
        String queryTemplate = (String) parameter.get( "QUERYTEMPLATE" );
        queryTemplate = getInitParam( queryTemplate );
        if ( !( new File( queryTemplate ).isAbsolute() ) ) {
            queryTemplate = sc.getRealPath( queryTemplate );
        }
        StringBuffer template = new StringBuffer( 10000 );
        try {
            BufferedReader br = new BufferedReader( new FileReader( queryTemplate ) );
            String line = null;
            while ( ( line = br.readLine() ) != null ) {
                template.append( line );
            }
            br.close();
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            throw new PortalException( "could not read query template: "
                                       + parameter.get( "TEMPLATE" ) );
        }
        String query = template.toString();
        String tmp = (String) parameter.get( "FILTERPROPERTIES" );
        if ( tmp != null ) {
            String[] properties = StringTools.extractStrings( tmp, "{", "}" );
            for ( int i = 0; i < properties.length; i++ ) {
                String[] kvp = StringTools.toArray( properties[i], "=", false );
                kvp[1] = StringTools.replace( kvp[1], "XXX", "%", true );
                query = StringTools.replace( query, '$' + kvp[0], kvp[1], true );
            }
        }
        return query;
    }

    /**
     * creates a WFS GetFeature query from a OGC filter expression
     * send from a client  
     * 
     * @return
     * @throws PortalException
     */
    private String createQueryFromFilter( String[] featureTypes, String[] xmlns, String filter ) {
        StringBuffer query = new StringBuffer( 20000 );
        String format = "text/xml; subtype=gml/3.1.1";
        int maxFeatures = -1;
        String resultType = "results";
        if ( parameter.get( "OUTPUTFORMAT" ) != null ) {
            format = (String) parameter.get( "OUTPUTFORMAT" );
        }
        if ( parameter.get( "MAXFEATURE" ) != null ) {
            maxFeatures = Integer.parseInt( (String) parameter.get( "MAXFEATURE" ) );
        }
        if ( parameter.get( "RESULTTYPE" ) != null ) {
            resultType = (String) parameter.get( "RESULTTYPE" );
        }
        query.append( "<wfs:GetFeature outputFormat='" ).append( format );
        query.append( "' maxFeatures='" ).append( maxFeatures ).append( "' " );
        query.append( " resultType='" ).append( resultType ).append( "' " );
        for ( int i = 0; i < xmlns.length; i++ ) {
            String[] tmp = StringTools.toArray( xmlns[i], "=", false );
            query.append( "xmlns:" ).append( tmp[0] ).append( "='" );
            query.append( tmp[1] ).append( "' " );
        }
        query.append( "xmlns:wfs='http://www.opengis.net/wfs' " );
        query.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
        query.append( "xmlns:gml='http://www.opengis.net/gml' " );
        query.append( ">" );

        query.append( "<wfs:Query " );
        for ( int i = 0; i < featureTypes.length; i++ ) {
            query.append( "typeName='" ).append( featureTypes[i] );
            if ( i < featureTypes.length - 1 ) {
                query.append( "," );
            }
        }
        query.append( "'>" );
        query.append( filter );
        query.append( "</wfs:Query></wfs:GetFeature>" );

        return query.toString();
    }

    /**
     * creates an OGC FE filter from a set of KVP-encode properties and
     * logical opertaions
     * @return
     */
    private String createFilterFromProperties() {
        String tmp = (String) parameter.get( "FILTERPROPERTIES" );
        if ( tmp != null ) {
            String[] properties = StringTools.extractStrings( tmp, "{", "}" );
            String logOp = (String) parameter.get( "LOGICALOPERATOR" );
            StringBuffer filter = new StringBuffer( 10000 );
            filter.append( "<ogc:Filter>" );
            if ( properties.length > 1 ) {
                filter.append( "<ogc:" ).append( logOp ).append( '>' );
            }
            for ( int i = 0; i < properties.length; i++ ) {
                String[] prop = StringTools.extractStrings( tmp, "[", "]" );
                if ( "!=".equals( prop[1] ) || "NOT LIKE".equals( prop[1] ) ) {
                    filter.append( "<ogc:Not>" );
                }
                if ( "=".equals( prop[1] ) || "!=".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsEqualTo>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsEqualTo>" );
                } else if ( ">=".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsGreaterThanOrEqualTo>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsGreaterThanOrEqualTo>" );
                } else if ( ">".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsGreaterThan>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsGreaterThan>" );
                } else if ( "<=".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsLessThanOrEqualTo>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsLessThanOrEqualTo>" );
                } else if ( "<".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsLessThan>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsLessThan>" );
                } else if ( "LIKE".equals( prop[1] ) || "NOT LIKE".equals( prop[1] ) ) {
                    filter.append( "<ogc:PropertyIsLike wildCard='%' singleChar='#' escape='!'>" );
                    filter.append( "<ogc:PropertyName>" ).append( prop[0] ).append(
                                                                                    "</ogc:PropertyName>" );
                    filter.append( "<ogc:Literal>" ).append( prop[2] ).append( "</ogc:Literal>" );
                    filter.append( "</ogc:PropertyIsLike>" );
                }
                if ( "!=".equals( prop[1] ) || "NOT LIKE".equals( prop[1] ) ) {
                    filter.append( "</ogc:Not>" );
                }
            }
            if ( properties.length > 1 ) {
                filter.append( "</ogc:" ).append( logOp ).append( '>' );
            }
            filter.append( "</ogc:Filter>" );
            return filter.toString();
        }
        return "";
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WFSClientPortletPerform_BAK.java,v $
 Revision 1.2  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.1  2006/09/25 12:47:00  poth
 bug fixes - map scale calculation

 Revision 1.13  2006/08/29 19:54:14  poth
 footer corrected

 Revision 1.12  2006/05/03 20:09:52  poth
 *** empty log message ***

 Revision 1.11  2006/05/01 20:15:27  poth
 *** empty log message ***

 Revision 1.10  2006/04/18 18:22:55  poth
 *** empty log message ***

 Revision 1.9  2006/04/18 18:20:26  poth
 *** empty log message ***

 Revision 1.8  2006/04/06 20:25:30  poth
 *** empty log message ***

 Revision 1.7  2006/03/30 21:20:28  poth
 *** empty log message ***

 Revision 1.6  2006/03/22 21:19:41  poth
 *** empty log message ***

 Revision 1.5  2006/03/06 17:51:46  poth
 *** empty log message ***

 Revision 1.4  2006/03/04 20:36:18  poth
 *** empty log message ***

 Revision 1.3  2006/02/27 16:14:11  poth
 *** empty log message ***

 Revision 1.2  2006/02/23 07:45:24  poth
 *** empty log message ***

 Revision 1.1  2006/02/07 19:52:44  poth
 *** empty log message ***

 Revision 1.1  2006/02/07 13:13:57  poth
 *** empty log message ***

 Revision 1.1  2006/02/05 09:30:11  poth
 *** empty log message ***

 Revision 1.3  2005/10/05 20:45:11  ap
 *** empty log message ***

 Revision 1.2  2005/09/16 09:38:18  ap
 *** empty log message ***

 Revision 1.1  2005/09/16 07:06:30  ap
 *** empty log message ***

 Revision 1.1  2005/09/15 09:45:48  ap
 *** empty log message ***


 ********************************************************************** */