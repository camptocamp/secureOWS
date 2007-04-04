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
package org.deegree.security.owsrequestvalidator.wms;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.NamedStyle;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsproxy.UsersOperationParameter;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.9 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 1.1
 */

public class GetMapRequestValidator extends AbstractWMSRequestValidator {
    
    private static double DEFAULT_PIXEL_SIZE = 0.00028;

    // known condition parameter
    private static final String BBOX = "bbox";

    private static final String LAYERS = "layers";

    private static final String BGCOLOR = "bgcolor";

    private static final String TRANSPARENCY = "transparency";

    private static final String RESOLUTION = "resolution";

    private static final String SLD = "sld";

    private static final String SLD_BODY = "sld_body";

    private static final String INVALIDBBOX = Messages
        .getString( "GetMapRequestValidator.INVALIDBBOX" );

    private static final String INVALIDLAYER = Messages
        .getString( "GetMapRequestValidator.INVALIDLAYER" );

    private static final String INVALIDSTYLE = Messages
        .getString( "GetMapRequestValidator.INVALIDSTYLE" );

    private static final String INVALIDBGCOLOR = Messages
        .getString( "GetMapRequestValidator.INVALIDBGCOLOR" );

    private static final String INVALIDTRANSPARENCY = Messages
        .getString( "GetMapRequestValidator.INVALIDTRANSPARENCY" );

    private static final String INVALIDRESOLUTION = Messages
        .getString( "GetMapRequestValidator.INVALIDRESOLUTION" );

    private static final String INVALIDSLD = Messages
        .getString( "GetMapRequestValidator.INVALIDSLD" );

    private static final String INVALIDSLD_BODY = Messages
        .getString( "GetMapRequestValidator.INVALIDSLD_BODY" );

    private static final String MISSINGCRS = Messages
        .getString( "GetMapRequestValidator.MISSINGCRS" );

    private List<String> accessdRes = new ArrayList<String>();

    private static FeatureType mapFT = null;

    private IGeoTransformer gt = null;

    static {
        if ( mapFT == null ) {
            mapFT = GetMapRequestValidator.createFeatureType();
        }
    }

    /**
     * @param policy
     */
    public GetMapRequestValidator( Policy policy ) {
        super( policy );
        try {
            gt = new GeoTransformer("EPSG:4326");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * validates the incomming GetMap request against the policy assigend to a validator
     * 
     * @param request
     *            request to validate
     * @param user
     *            name of the user who likes to perform the request (can be null)
     */
    public void validateRequest( OGCWebServiceRequest request, User user )
        throws InvalidParameterValueException,
            UnauthorizedException {

        accessdRes.clear();
        userCoupled = false;
        Request req = policy.getRequest( "WMS", "GetMap" );
        // request is valid because no restrictions are made
        if ( req.isAny() ) return;
        Condition condition = req.getPreConditions();

        GetMap wmsreq = (GetMap) request;

        validateVersion( condition, wmsreq.getVersion() );
        Envelope env = wmsreq.getBoundingBox();
        /* XXXsyp no proj
        try {
            env = gt.transform( env, wmsreq.getSrs() );
        } catch (Exception e) {
            throw new InvalidParameterValueException( "condition envelope isn't in the right CRS ", e );
        }
        */
        
        
        validateBBOX( condition, env );
        validateLayers( condition, wmsreq.getLayers() );
        validateBGColor( condition, ColorUtils.toHexCode( "0x", wmsreq.getBGColor() ) );
        validateTransparency( condition, wmsreq.getTransparency() );
        validateExceptions( condition, wmsreq.getExceptions() );
        validateFormat( condition, wmsreq.getFormat() );
        validateMaxWidth( condition, wmsreq.getWidth() );
        validateMaxHeight( condition, wmsreq.getHeight() );
        validateResolution( condition, wmsreq );
        validateSLD( condition, wmsreq.getSLD_URL() );
        validateSLD_Body( condition, wmsreq.getStyledLayerDescriptor() );

        if ( userCoupled ) {
            validateAgainstRightsDB( wmsreq, user );
        }

    }

    /**
     * checks if the passed envelope is valid against the maximum bounding box defined in the
     * policy. If <tt>user</ff> != <tt>null</tt> the 
     * maximu valid BBOX will be read from the user/rights repository  
     * @param condition condition containing the definition of the valid BBOX
     * @param envelope
     * @throws InvalidParameterValueException
     */
    private void validateBBOX( Condition condition, Envelope envelope )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( BBOX );

        // version is valid because no restrictions are made
        if ( op.isAny() )
            return;

        String v = op.getFirstAsString();
        String[] d = StringTools.toArray( v, ",", false );
        Envelope env = GeometryFactory.createEnvelope( Double.parseDouble( d[0] ), 
                        Double.parseDouble( d[1] ), Double.parseDouble( d[2] ), 
                        Double.parseDouble( d[3] ), null );

        /* XXXsyp
        try {
            env = gt.transform( env, d[4] );
        } catch (Exception e) {
            throw new InvalidParameterValueException( MISSINGCRS, e );
        }
        */

       if ( !env.contains( envelope ) ) {
            if ( !op.isUserCoupled() ) {
                // if not user coupled the validation has failed
                throw new InvalidParameterValueException( INVALIDBBOX + op.getFirstAsString() );
            }
            userCoupled = true;
            accessdRes.add( "BBOX: " + v );
        }
    }

    /**
     * checks if the passed layres/styles are valid against the layers/styles list defined in the
     * policy. If <tt>user</ff> != <tt>null</tt> the 
     * valid layers/styles will be read from the user/rights repository
     * @param condition condition containing the definition of the valid layers/styles
     * @param layers
     * @throws InvalidParameterValueException
     */
    private void validateLayers( Condition condition, GetMap.Layer[] layers )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( LAYERS );

        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }

        List<String> v = op.getValues();
        // seperate layers from assigned styles
        Map map = new HashMap();
        for (int i = 0; i < v.size(); i++) {
            String[] tmp = StringTools.toArray( v.get( i ), "|", false );
            // XXXsyp: allow lack of "|$any" in layer name
            String value = "$any$";
            if (tmp.length == 2)
                value = tmp[1];                
            map.put( tmp[0], value );
        }
        
        for (int i = 0; i < layers.length; i++) {
            String style = layers[i].getStyleName();
            String vs = (String) map.get( layers[i].getName() );
            if ( vs == null ) {
                if ( !op.isUserCoupled() ) {
                    throw new InvalidParameterValueException( INVALIDLAYER + layers[i].getName() );
                } 
                accessdRes.add( "Layers: " + layers[i].getName() );
                userCoupled = true;
            } else if ( !style.equalsIgnoreCase( "default" )
                && vs.indexOf( "$any$" ) < 0 && vs.indexOf( style ) < 0 ) {
                // a style is valid for a layer if it's the default style
                // or the layer accepts any style or a style is explicit defined
                // to be valid
                if ( !op.isUserCoupled() ) {
                    throw new InvalidParameterValueException( INVALIDSTYLE
                        + layers[i].getName() + ':' + style );
                } 
                userCoupled = true;
                accessdRes.add( "Styles: " + style );
            }
        }
      
    }

    /**
     * checks if the passed bgcolor is valid against the bgcolor(s) defined in the policy. If
     * <tt>user</ff> != <tt>null</tt> the valid bgcolors will be read from 
     * the user/rights repository
     * @param condition condition containing the definition of the valid bgcolors
     * @param bgcolor
     * @throws InvalidParameterValueException
     */
    private void validateBGColor( Condition condition, String bgcolor )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( BGCOLOR );

        // version is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List list = op.getValues();

        if ( !list.contains( bgcolor ) ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDBGCOLOR
                    + bgcolor );
            } 
            accessdRes.add( "BGCOLOR" + bgcolor );     
            userCoupled = true;
        }

    }

    /**
     * checks if the passed transparency is valid against the transparency defined in the policy. If
     * <tt>user</ff> != <tt>null</tt> the valid transparency will be  
     * read from the user/rights repository
     * @param condition condition containing the definition of the valid transparency
     * @param transparency
     * @throws InvalidParameterValueException
     */
    private void validateTransparency( Condition condition, boolean transparency )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( TRANSPARENCY );

        // version is valid because no restrictions are made
        if ( op.isAny() )
            return;

        List<String> v = op.getValues();
        String s = "" + transparency;
        if ( !v.get(0).equals( s ) && !v.get(v.size() - 1).equals( s ) ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDTRANSPARENCY
                    + transparency );
            } 
            userCoupled = true;
            accessdRes.add( "Transparency: " + transparency );
        }

    }

    /**
     * checks if the requested map area/size is valid against the minimum resolution defined in the
     * policy. If <tt>user</ff> != <tt>null</tt> the valid  
     * resolution will be read from the user/rights repository
     * @param condition condition containing the definition of the valid resolution
     * @param resolution
     * @throws InvalidParameterValueException
     */
    private void validateResolution( Condition condition, GetMap gmr )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( RESOLUTION );

        // version is valid because no restrictions are made
        if ( op.isAny() )
            return;

        double scale = 0;
        try {
            scale = calcScale( gmr );
        } catch (Exception e) {
            throw new InvalidParameterValueException( StringTools.stackTraceToString( e ) );
        }
        double compareRes = 0;
        compareRes = op.getFirstAsDouble();
        if ( scale < compareRes ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDRESOLUTION + scale );
            }
            userCoupled = true;
            accessdRes.add( "resolution: " + scale );
        }
    }

    /**
     * checks if the passed reference to a SLD document is valid against the defined in the policy.
     * If <tt>user</ff> != <tt>null</tt> the valid  
     * sld reference addresses will be read from the user/rights repository
     * @param condition condition containing the definition of the valid sldRef
     * @param sldRef
     * @throws InvalidParameterValueException
     */
    private void validateSLD( Condition condition, URL sldRef )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( SLD );
        OperationParameter gmop = condition.getOperationParameter( LAYERS );

        // version is valid because no restrictions are made
        if ( sldRef == null || op.isAny() ) {
            return;
        }

        // validate reference base of the SLD
        List<String> list = op.getValues();
        String port = null;
        if ( sldRef.getPort() != -1 ) {
            port = ":" + sldRef.getPort();
        } else {
            port = ":80";
        }
        String addr = sldRef.getProtocol() + "://" + sldRef.getHost() + port;
        if ( !list.contains( addr ) ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDSLD + sldRef );
            }   
            userCoupled = true;
        }    
        
        // validate referenced dacument to be a valid SLD
        StyledLayerDescriptor sld = null;
        try {
            sld = SLDFactory.createSLD( sldRef );
        } catch ( XMLParsingException e ) {
            String s = org.deegree.i18n.Messages.getMessage( "WMS_SLD_IS_NOT_VALID", sldRef );
            throw new InvalidParameterValueException( s );
        }
        
        // validate NamedLayers referenced by the SLD
        NamedLayer[] nl = sld.getNamedLayers();
        List<String> v = gmop.getValues();
        // seperate layers from assigned styles
        Map map = new HashMap();
        for (int i = 0; i < v.size(); i++) {
            String[] tmp = StringTools.toArray( v.get( i ), "|", false );
            map.put( tmp[0], tmp[1] );
        }
        if ( !userCoupled ) {
            for ( int i = 0; i < nl.length; i++ ) {
                AbstractStyle st =  nl[i].getStyles()[0];
                String style = null;
                if ( st instanceof NamedStyle ) {
                    style = ((NamedStyle) st).getName();
                } else {
                    // use default as name if a UserStyle is defined
                    // to ensure that the style will be accepted by
                    // the validator
                    style = "default";
                }
                String vs = (String) map.get( nl[i].getName() );
                if ( vs == null ) {
                    if ( !op.isUserCoupled() ) {
                        throw new InvalidParameterValueException( INVALIDLAYER + nl[i].getName() );
                    } 
                    accessdRes.add( "Layers: " + nl[i].getName() );
                    userCoupled = true;
                } else if ( !style.equalsIgnoreCase( "default" )
                    && vs.indexOf( "$any$" ) < 0 && vs.indexOf( style ) < 0 ) {
                    // a style is valid for a layer if it's the default style
                    // or the layer accepts any style or a style is explicit defined
                    // to be valid
                    if ( !op.isUserCoupled() ) {
                        throw new InvalidParameterValueException( INVALIDSTYLE + nl[i].getName() + 
                                                                  ':' + style );
                    } 
                    userCoupled = true;
                    accessdRes.add( "Styles: " + style );
                }
            }
        }
        
    }

    /**
     * checks if the passed user is allowed to perform a GetMap request containing a SLD_BODY
     * parameter.
     * 
     * @param condition
     *            condition containing when SLD_BODY is valid or nots
     * @param sld_body
     * @throws InvalidParameterValueException
     */
    private void validateSLD_Body( Condition condition, StyledLayerDescriptor sld_body )
        throws InvalidParameterValueException {

        /*
         * 
         * the problem is that sld_body never is null because it always will contain the requested
         * layers and styles
         * 
         * OperationParameter op = condition.getOperationParameter( SLD_BODY );
         *  // version is valid because no restrictions are made if ( sld_body == null ||op.isAny() )
         * return;
         *  // at the moment it is just evaluated if the user is allowed // to perform a SLD request
         * or not. no content validation will // be made boolean isAllowed = false; if (
         * op.isUserCoupled() ) { //TODO // get comparator list from security registry } if
         * (!isAllowed ) { throw new InvalidParameterValueException( INVALIDSLD_BODY ); }
         */
    }

    /**
     * validates the passed WMS GetMap request against a User- and Rights-Management DB.
     * 
     * @param wmsreq
     * @param user
     * @throws InvalidParameterValueException
     */
    private void validateAgainstRightsDB( GetMap wmsreq, User user )
        throws InvalidParameterValueException,
            UnauthorizedException {

        if ( user == null ) {
            StringBuffer sb = new StringBuffer( 1000 );
            sb.append( ' ' );
            for (int i = 0; i < accessdRes.size(); i++) {
                sb.append( accessdRes.get( i ) ).append( "; " );
            }
            throw new UnauthorizedException( Messages.format( "RequestValidator.NOACCESS", sb ) );
        }

        Double scale = null;
        try {
            scale = new Double( calcScale( wmsreq ) );
        } catch (Exception e) {
            throw new InvalidParameterValueException( e );
        }

        // create feature that describes the map request
        FeatureProperty[] fps = new FeatureProperty[11];
        fps[0] = FeatureFactory.createFeatureProperty( "version", wmsreq.getVersion() );
        fps[1] = FeatureFactory.createFeatureProperty( "width", new Integer( wmsreq.getWidth() ) );
        fps[2] = FeatureFactory.createFeatureProperty( "height", new Integer( wmsreq.getHeight() ) );
        Envelope env = wmsreq.getBoundingBox();
        try {
            env = gt.transform( env, wmsreq.getSrs()  );
        } catch (Exception e) {
            throw new InvalidParameterValueException( "A:condition envelope isn't in "
                + "the right CRS ", e );
        }
        Object geom = null;
        try {
            geom = GeometryFactory.createSurface( env, null );
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        fps[3] = FeatureFactory.createFeatureProperty( "GEOM", geom );
        fps[4] = FeatureFactory.createFeatureProperty( "format", wmsreq.getFormat() );
        fps[5] = FeatureFactory.createFeatureProperty( "bgcolor", 
                                                       ColorUtils.toHexCode( "0x", wmsreq.getBGColor() ) );
        fps[6] = FeatureFactory.createFeatureProperty( "transparent", ""
            + wmsreq.getTransparency() );
        fps[7] = FeatureFactory.createFeatureProperty( "exceptions", wmsreq.getExceptions() );
        fps[8] = FeatureFactory.createFeatureProperty( "resolution", scale );
        fps[9] = FeatureFactory.createFeatureProperty( "sld", wmsreq.getSLD_URL() );

        GetMap.Layer[] layers = wmsreq.getLayers();
        for (int i = 0; i < layers.length; i++) {
            fps[10] = FeatureFactory.createFeatureProperty( "style", layers[i].getStyleName() );
            Feature feature = FeatureFactory.createFeature( "id", mapFT, fps );
            handleUserCoupledRules( user, feature, layers[i].getName(), "Layer", 
                                    RightType.GETMAP );
        }

    }

    /**
     * calculates the map scale as defined in the OGC WMS 1.1.1 specifications
     * 
     * @return scale of the map
     */
    private double calcScale( GetMap request ) throws Exception {
        
        

        Envelope bbox = request.getBoundingBox();
        
        CoordinateSystem crs = CRSFactory.create( request.getSrs() );
        return MapUtils.calcScale( request.getWidth(), request.getHeight(), bbox, crs, DEFAULT_PIXEL_SIZE );
/*
        if ( !request.getSrs().equalsIgnoreCase( "EPSG:4326" ) ) {
            // transform the bounding box of the request to EPSG:4326
            bbox = gt.transformEnvelope( bbox, request.getSrs());
        }

        double dx = bbox.getWidth() / request.getWidth();
        double dy = bbox.getHeight() / request.getHeight();

        // create a box on the central map pixel to determine its size in meter
        Position min = GeometryFactory.createPosition( bbox.getMin().getX()
            + dx * ( request.getWidth() / 2d - 1d ), bbox.getMin().getY()
            + dy * ( request.getHeight() / 2d - 1d ) );
        Position max = GeometryFactory.createPosition( bbox.getMin().getX()
            + dx * ( request.getWidth() / 2d ), bbox.getMin().getY()
            + dy * ( request.getHeight() / 2d ) );

        double sc = calcDistance( min.getY(), min.getX(), max.getY(), max.getX() );

        return sc;
*/        
    }

    private static FeatureType createFeatureType() {
        PropertyType[] ftps = new PropertyType[11];
        ftps[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "version" ),
            Types.VARCHAR, false );
        ftps[1] = FeatureFactory.createSimplePropertyType( new QualifiedName( "width" ),
            Types.INTEGER, false );
        ftps[2] = FeatureFactory.createSimplePropertyType( new QualifiedName( "height" ),
            Types.INTEGER, false );
        ftps[3] = FeatureFactory.createSimplePropertyType( new QualifiedName( "GEOM" ),
            Types.GEOMETRY, false );
        ftps[4] = FeatureFactory.createSimplePropertyType( new QualifiedName( "format" ),
            Types.VARCHAR, false );
        ftps[5] = FeatureFactory.createSimplePropertyType( new QualifiedName( "bgcolor" ),
            Types.VARCHAR, false );
        ftps[6] = FeatureFactory.createSimplePropertyType( new QualifiedName( "transparent" ),
            Types.VARCHAR, false );
        ftps[7] = FeatureFactory.createSimplePropertyType( new QualifiedName( "exceptions" ),
            Types.VARCHAR, false );
        ftps[8] = FeatureFactory.createSimplePropertyType( new QualifiedName( "resolution" ),
            Types.DOUBLE, false );
        ftps[9] = FeatureFactory.createSimplePropertyType( new QualifiedName( "sld" ),
            Types.VARCHAR, false );
        ftps[10] = FeatureFactory.createSimplePropertyType( new QualifiedName( "style" ),
            Types.VARCHAR, false );

        return FeatureFactory.createFeatureType( "GetMap", false, ftps );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetMapRequestValidator.java,v $
Revision 1.9  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.8  2006/11/07 09:56:11  poth
support for GetMap SLD parameter added

Revision 1.7  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.6  2006/09/27 16:46:41  poth
transformation method signature changed

Revision 1.5  2006/09/25 12:47:00  poth
bug fixes - map scale calculation

Revision 1.4  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.3  2006/08/02 14:16:16  poth
message for throwing an UnauthrizedException corrected

Revision 1.2  2006/08/02 09:45:09  poth
changes required as consequence of changing OperationParameter

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.27  2006/06/07 12:19:38  poth
*** empty log message ***

Revision 1.26  2006/06/05 09:59:25  poth
method for conversation from java.awt.Color to its Hex code representation centralized in framework.ColorUtil

Revision 1.25  2006/05/25 09:53:31  poth
adapated to changed/simplified policy xml-schema


********************************************************************** */