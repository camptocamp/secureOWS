/*----------------   FILE HEADER  ------------------------------------------

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.wms.operation.GetLegendGraphic;
import org.deegree.security.UnauthorizedException;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.User;
import org.deegree.security.owsproxy.Condition;
import org.deegree.security.owsproxy.OperationParameter;
import org.deegree.security.owsproxy.Request;
import org.deegree.security.owsrequestvalidator.Messages;
import org.deegree.security.owsrequestvalidator.Policy;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.4 $, $Date: 2006/11/07 09:56:11 $
 * 
 * @since 1.1
 */

class GetLegendGraphicRequestValidator extends AbstractWMSRequestValidator {

    // known condition parameter
    private static final String LAYER = "layers";

    private static final String SLD = "sld";

    private static final String SLD_BODY = "sld_body";

    private static final String INVALIDSLD = Messages
        .getString( "GetLegendGraphicRequestValidator.INVALIDSLD" );

    private static final String INVALIDSLD_BODY = Messages
        .getString( "GetLegendGraphicRequestValidator.INVALIDSLD_BODY" );

    private static final String INVALIDLAYER = Messages
        .getString( "GetLegendGraphicRequestValidator.INVALIDLAYER" );

    private static final String INVALIDSTYLE = Messages
        .getString( "GetLegendGraphicRequestValidator.INVALIDSTYLE" );

    private static FeatureType glgFT = null;

    static {
        if ( glgFT == null ) {
            glgFT = GetLegendGraphicRequestValidator.createFeatureType();
        }
    }

    /**
     * @param policy
     */
    public GetLegendGraphicRequestValidator( Policy policy ) {
        super( policy );
    }

    /**
     * validates the incomming GetLegendGraphic request against the policy assigend to a validator
     * 
     * @param request
     *            request to validate
     * @param user
     *            name of the user who likes to perform the request (can be null)
     */
    public void validateRequest( OGCWebServiceRequest request, User user )
        throws InvalidParameterValueException,
            UnauthorizedException {

        userCoupled = false;
        Request req = policy.getRequest( "WMS", "GetLegendGraphic" );
        // request is valid because no restrictions are made
        if ( req.isAny() )
            return;
        Condition condition = req.getPreConditions();

        GetLegendGraphic wmsreq = (GetLegendGraphic) request;

        validateVersion( condition, wmsreq.getVersion() );
        validateLayer( condition, wmsreq.getLayer(), wmsreq.getStyle() );
        validateExceptions( condition, wmsreq.getExceptions() );
        validateFormat( condition, wmsreq.getFormat() );
        validateMaxWidth( condition, wmsreq.getWidth() );
        validateMaxHeight( condition, wmsreq.getHeight() );
        validateSLD( condition, wmsreq.getSLD() );

        if ( userCoupled ) {
            validateAgainstRightsDB( wmsreq, user );
        }

    }

    /**
     * validates if the requested layer is valid against the policy/condition. If the passed user <>
     * null this is checked against the user- and rights-management system/repository
     * 
     * @param condition
     * @param layer
     * @throws InvalidParameterValueException
     */
    private void validateLayer( Condition condition, String layer, String style )
        throws InvalidParameterValueException {

        OperationParameter op = condition.getOperationParameter( LAYER );

        // version is valid because no restrictions are made
        if ( op.isAny() ) {
            return;
        }

        List<String> v = op.getValues();

        // seperate layers from assigned styles
        Map map = new HashMap();
        for (int i = 0; i < v.size(); i++) {
            String[] tmp = StringTools.toArray( v.get( i ), "|", false );
            map.put( tmp[0], tmp[1] );
        }

        String vs = (String) map.get( layer );

        if ( vs == null ) {
            if ( !op.isUserCoupled() ) {
                throw new InvalidParameterValueException( INVALIDLAYER + layer );
            } 
            userCoupled = true;
        } else if ( !style.equalsIgnoreCase( "default" )
            && vs.indexOf( "$any$" ) < 0 && vs.indexOf( style ) < 0 ) {
            if ( !op.isUserCoupled() ) {
                // a style is valid for a layer if it's the default style
                // or the layer accepts any style or a style is explicit defined
                // to be valid
                throw new InvalidParameterValueException( INVALIDSTYLE + layer + ':' + style );
            }
            userCoupled = true;
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

        // version is valid because no restrictions are made
        if ( sldRef == null || op.isAny() )
            return;

        List list = op.getValues();
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
        
        try {
            SLDFactory.createSLD( sldRef );
        } catch ( XMLParsingException e ) {
            String s = org.deegree.i18n.Messages.getMessage( "WMS_SLD_IS_NOT_VALID", sldRef );
            throw new InvalidParameterValueException( s );
        }
    }

    /**
     * validates the passed WMS GetMap request against a User- and Rights-Management DB.
     * 
     * @param wmsreq
     * @throws InvalidParameterValueException
     */
    private void validateAgainstRightsDB( GetLegendGraphic wmsreq, User user )
        throws InvalidParameterValueException,
            UnauthorizedException {

        if ( user == null ) {
            throw new UnauthorizedException( "no access to anonymous user" );
        }

        // create feature that describes the map request
        FeatureProperty[] fps = new FeatureProperty[7];
        fps[0] = FeatureFactory.createFeatureProperty( "version", wmsreq.getVersion() );
        fps[1] = FeatureFactory.createFeatureProperty( "width", new Integer( wmsreq.getWidth() ) );
        fps[2] = FeatureFactory.createFeatureProperty( "height", new Integer( wmsreq.getHeight() ) );
        fps[3] = FeatureFactory.createFeatureProperty( "format", wmsreq.getFormat() );
        fps[4] = FeatureFactory.createFeatureProperty( "exceptions", wmsreq.getExceptions() );
        fps[5] = FeatureFactory.createFeatureProperty( "sld", wmsreq.getSLD() );
        fps[6] = FeatureFactory.createFeatureProperty( "style", wmsreq.getStyle() );
        Feature feature = FeatureFactory.createFeature( "id", glgFT, fps );
        handleUserCoupledRules( user, feature, wmsreq.getLayer(), "Layer",
            RightType.GETLEGENDGRAPHIC );

    }

    /**
     * creates a feature type that matches the parameters of a GetLagendGraphic request
     * 
     * @return created <tt>FeatureType</tt>
     */
    private static FeatureType createFeatureType() {
        PropertyType[] ftps = new PropertyType[7];
        ftps[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "version" ),
            Types.VARCHAR, false );
        ftps[1] = FeatureFactory.createSimplePropertyType( new QualifiedName( "width" ),
            Types.INTEGER, false );
        ftps[2] = FeatureFactory.createSimplePropertyType( new QualifiedName( "height" ),
            Types.INTEGER, false );
        ftps[3] = FeatureFactory.createSimplePropertyType( new QualifiedName( "format" ),
            Types.VARCHAR, false );
        ftps[4] = FeatureFactory.createSimplePropertyType( new QualifiedName( "exceptions" ),
            Types.VARCHAR, false );
        ftps[5] = FeatureFactory.createSimplePropertyType( new QualifiedName( "sld" ),
            Types.VARCHAR, false );
        ftps[6] = FeatureFactory.createSimplePropertyType( new QualifiedName( "style" ),
            Types.VARCHAR, false );

        return FeatureFactory.createFeatureType( "GetLegendGraphic", false, ftps );
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GetLegendGraphicRequestValidator.java,v $
Revision 1.4  2006/11/07 09:56:11  poth
support for GetMap SLD parameter added

Revision 1.3  2006/08/10 07:17:52  poth
bug fix - removing Arrays.asList calls for transforming op.geValues because accoring to refactoring this method it already returns a list

Revision 1.2  2006/08/02 09:45:09  poth
changes required as consequence of changing OperationParameter

Revision 1.1  2006/07/23 08:44:53  poth
refactoring - moved validators assigned to OWS into specialized packages

Revision 1.18  2006/05/25 09:53:30  poth
adapated to changed/simplified policy xml-schema


********************************************************************** */