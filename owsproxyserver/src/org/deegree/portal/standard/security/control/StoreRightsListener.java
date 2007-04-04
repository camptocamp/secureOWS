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
package org.deegree.portal.standard.security.control;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.model.Right;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.SecuredObject;
import org.deegree.security.drm.model.User;
import org.w3c.dom.Document;

/**
 * This <code>Listener</code> reacts on RPC-StoreRights events.
 * 
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class StoreRightsListener extends AbstractListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( StoreRightsListener.class );

    private static final String MINX = "-180.0";
    private static final String MINY = "-90.0";
    private static final String MAXX = "180.0";
    private static final String MAXY = "90.0";

    public void actionPerformed(FormEvent event) {
        
        // the Role for which the rights are to be set
        int roleId = -1;
        // array of ints, ids of Layers (SecuredObjects) for which
        // the Role has access rights
        int[] layers = null;
        // corresponding maps of key (PropertyName) / value-pairs that
        // constitute access constraints
        Map[] layerConstraints = null;

        // array of ints, ids of FeatureTypes (SecuredObjects) for which
        // the Role has access rights
        int[] featureTypes = null;

        SecurityAccessManager manager = null;
        SecurityTransaction transaction = null;

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            if (params.length != 3) {
                throw new RPCException(
                        "Invalid RPC. Exactly three 'param' elements below 'params' are required.");
            }
            if (!(params[0].getValue() instanceof String)) {
                throw new RPCException(
                        "Invalid RPC. First 'param' must contain a 'string'-value element.");
            }

            // extract role-id
            try {
                roleId = Integer.parseInt((String) params[0].getValue());
            } catch (NumberFormatException e) {
                throw new RPCException(
                        "Invalid RPC. Role must be specified by a valid integer.");
            }

            // extract Layer rights
            if (!(params[1].getValue() instanceof RPCParameter[])) {
                throw new RPCException(
                        "Invalid RPC. Second 'param' must contain an 'array' element.");
            }
            RPCParameter[] layerParams = (RPCParameter[]) params[1].getValue();
            layers = new int[layerParams.length];
            layerConstraints = new Map[layerParams.length];
            for (int i = 0; i < layerParams.length; i++) {
        
                // is the layer access constrained?
                if (layerParams[i].getValue() instanceof RPCParameter[]) {
                    layerConstraints[i] = new HashMap();
                    RPCParameter[] constrainParams = (RPCParameter[]) layerParams[i]
                            .getValue();
                    try {
                        layers[i] = Integer.parseInt((String) constrainParams[0].getValue());
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. Layers must be specified by valid integers (their ids).");
                    }
                    RPCParameter param = constrainParams[1];
                    RPCStruct constraints = (RPCStruct) param.getValue();
                    RPCMember[] members = constraints.getMembers();
                    for (int j = 0; j < members.length; j++) {
                        String propertyName = members[j].getName();
                        Object value = members[j].getValue();
                        if (value instanceof RPCParameter[]) {
                            String[] values = new String[((RPCParameter[]) value).length];
                            for (int k = 0; k < values.length; k++) {
                                values[k] = (String) ((RPCParameter[]) value)[k]
                                        .getValue();
                            }
                            layerConstraints[i].put(propertyName, values);
                        } else if (value instanceof String) {
                            layerConstraints[i].put(propertyName, value);
                        } else {
                            throw new RPCException(
                                    "Invalid RPC. Access constraints either be specified by string- or by array-values.");
                        }
                    }
                } else if (layerParams[i].getValue() instanceof String) {
                    try {
                        layers[i] = Integer.parseInt((String) layerParams[i]
                                .getValue());
                    } catch (NumberFormatException e) {
                        throw new RPCException(
                                "Invalid RPC. Rights must be specified by valid integers.");
                    }
                } else {
                    throw new RPCException(
                            "Invalid RPC. Rights must be specified using string elements or arrays (if constrains are given).");
                }
            }

            // extract FeatureType rights
            if (!(params[2].getValue() instanceof RPCParameter[])) {
                throw new RPCException(
                        "Invalid RPC. Third 'param' must contain an 'array' element.");
            }
            RPCParameter[] featureTypeParams = (RPCParameter[]) params[2]
                    .getValue();
            featureTypes = new int[featureTypeParams.length];
            for (int i = 0; i < featureTypeParams.length; i++) {

                if (!(featureTypeParams[i].getValue() instanceof String)) {
                    throw new RPCException(
                            "Invalid RPC. Rights must be specified in string elements.");
                }
                try {
                    featureTypes[i] = Integer.parseInt((String) featureTypeParams[i].getValue());
                } catch (NumberFormatException e) {
                    throw new RPCException(
                            "Invalid RPC. Rights must be specified by valid integers.");
                }
            }

            transaction = SecurityHelper.acquireTransaction(this);
            SecurityHelper.checkForAdminRole(transaction);

            manager = SecurityAccessManager.getInstance();
            User user = transaction.getUser();
            Role role = transaction.getRoleById(roleId);

            // perform access check
            if (!user.hasRight(transaction, "update", role)) {
                getRequest().setAttribute("SOURCE", this.getClass().getName());
                getRequest().setAttribute( "MESSAGE",
                                "Die Änderungen konnten nicht gespeichert werden, "
                                        + "da Sie keine Berechtigung zur Modifikation der Rolle '"
                                        + role.getName() + "' haben.");
                setNextPage("error.jsp");
                return;
            }

            // set/delete access rights for Layers
            SecuredObject[] presentLayers = transaction.getAllSecuredObjects(ClientHelper.TYPE_LAYER);
            for (int i = 0; i < presentLayers.length; i++) {
                boolean isAccessible = false;
                Map constraintMap = null;
                SecuredObject layer = presentLayers[i];
                for (int j = 0; j < layers.length; j++) {
                    if (layer.getID() == layers[j]) {
                        isAccessible = true;
                        constraintMap = layerConstraints[j];
                        break;
                    }
                }
                if (isAccessible) {
                    Filter filter = null;
                    if (constraintMap != null) {
                        String xml = buildGetMapFilter(constraintMap);
                        if (xml != null) {
                            try {
                                Document doc = XMLTools.parse(new StringReader(
                                        xml));
                                filter = AbstractFilter.buildFromDOM(doc
                                        .getDocumentElement());
                            } catch (Exception e) {
                                throw new GeneralSecurityException(
                                        "Fehler beim Parsen des generierten Filter Encodings: "
                                                + e.getMessage());
                            }
                        }
                        if (filter != null) {
                            LOG.logInfo( "Back to XML: " + filter.toXML() );
                        }
                    }
                    transaction.setRights(layer, role, new Right[] {
                            new Right(layer, RightType.GETMAP, filter),
                            new Right(layer, RightType.GETFEATUREINFO),
                            new Right(layer, RightType.GETLEGENDGRAPHIC) });
                } else {
                    transaction.removeRights(layer, role, new RightType[] {
                            RightType.GETMAP, RightType.GETFEATUREINFO,
                            RightType.GETLEGENDGRAPHIC });
                }
            }

            // set/delete access rights for FeatureTypes
            SecuredObject[] presentFeatureTypes = 
                transaction.getAllSecuredObjects(ClientHelper.TYPE_FEATURETYPE);
            for (int i = 0; i < presentFeatureTypes.length; i++) {
                boolean selected = false;
                SecuredObject featureType = presentFeatureTypes[i];
                for (int j = 0; j < featureTypes.length; j++) {
                    if (featureType.getID() == featureTypes[j]) {
                        selected = true;
                        break;
                    }
                }
                if (selected) {
                    transaction.addRights(featureType, role, new Right[] {
                            new Right(featureType, RightType.GETFEATURE),
                            new Right(featureType,
                                    RightType.DESCRIBEFEATURETYPE) });
                } else {
                    transaction.removeRights(featureType, role,
                            new RightType[] { RightType.GETFEATURE,
                                    RightType.DESCRIBEFEATURETYPE });
                }
            }

            getRequest().setAttribute("MESSAGE",
                            "Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert.");
            manager.commitTransaction(transaction);
            transaction = null;

            getRequest().setAttribute( "MESSAGE",
                    "Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert."
                            + "<br/><br/><p><a href='javascript:editRightsRPC("
                            + role.getID() + ")'>--> zurück "
                            + "zum Rechte-Editor</a></p>");
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } finally {
            if (manager != null && transaction != null) {
                try {
                    manager.abortTransaction(transaction);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Builds a filter encoding-expression as a constraint for GetMap-operations
     * from the values stored in the given <code>Map</code>.
     */
    String buildGetMapFilter(Map constraintMap) throws RPCException {

        int operands = 0;
        StringBuffer sb = new StringBuffer(1000);

        // bbox
        if (constraintMap.get("bbox") != null) {
            operands++;
            String minx = MINX;
            String miny = MINY;
            String maxx = MAXX;
            String maxy = MAXY;
            if (constraintMap.get("bbox") instanceof String) {
                if (!constraintMap.get("bbox").equals("wuppertal")) {
                    throw new RPCException( "Constraint 'bbox' must be specified by a string "
                                    + "value (\"wuppertal\") or a string array "
                                    + "containing exactly 4 coordinates!");
                }
            } else {
                String[] bbox = (String[]) constraintMap.get("bbox");
                if (bbox.length != 4) { 
                    throw new RPCException( "Constraint 'bbox' must be specified by a string "
                                    + "value (\"wuppertal\") or a string array "
                                    + "containing exactly 4 coordinates!");
                }
                minx = bbox[0];
                miny = bbox[1];
                maxx = bbox[2];
                maxy = bbox[3];
            }
            sb.append("<ogc:Within>");
            sb.append("<ogc:PropertyName>bbox</ogc:PropertyName>");
            sb.append("<gml:Box>");
            sb.append("<gml:coordinates>");
            sb.append(minx).append(",").append(miny).append(" ");
            sb.append(maxx).append(",").append(maxy);
            sb.append("</gml:coordinates>");
            sb.append("</gml:Box></ogc:Within>");
        }

        // bgcolor
        String[] bgcolors = (String[]) constraintMap.get("bgcolor");
        if (bgcolors != null && bgcolors.length > 0) {
            operands++;
            if (bgcolors.length > 1) {
                sb.append("<ogc:Or>");
            }
            for (int i = 0; i < bgcolors.length; i++) {
                sb.append("<ogc:PropertyIsEqualTo>");
                sb.append("<ogc:PropertyName>bgcolor</ogc:PropertyName>");
                sb.append("<ogc:Literal><![CDATA[" + bgcolors[i] + "]]></ogc:Literal>");
                sb.append("</ogc:PropertyIsEqualTo>");
            }
            if (bgcolors.length > 1) {
                sb.append("</ogc:Or>");
            }
        }

        // transparent
        String transparent = (String) constraintMap.get("transparent");
        if (transparent != null) {
            operands++;
            sb.append("<ogc:PropertyIsEqualTo>");
            sb.append("<ogc:PropertyName>transparent</ogc:PropertyName>");
            sb.append("<ogc:Literal><![CDATA[" + transparent + "]]></ogc:Literal>");
            sb.append("</ogc:PropertyIsEqualTo>");
        }

        // format
        String[] formats = (String[]) constraintMap.get("format");
        if (formats != null && formats.length > 0) {
            operands++;
            if (formats.length > 1) {
                sb.append("<ogc:Or>");
            }
            for (int i = 0; i < formats.length; i++) {
                sb.append("<ogc:PropertyIsEqualTo>");
                sb.append("<ogc:PropertyName>format</ogc:PropertyName>");
                sb.append("<ogc:Literal><![CDATA[" + formats[i] + "]]></ogc:Literal>");
                sb.append("</ogc:PropertyIsEqualTo>");
            }
            if (formats.length > 1) {
                sb.append("</ogc:Or>");
            }
        }

        // resolution
        String resolution = (String) constraintMap.get("resolution");
        if (resolution != null) {
            operands++;
            sb.append("<ogc:PropertyIsGreaterThanOrEqualTo>");
            sb.append("<ogc:PropertyName>resolution</ogc:PropertyName>");
            sb.append("<ogc:Literal>" + resolution + "</ogc:Literal>");
            sb.append("</ogc:PropertyIsGreaterThanOrEqualTo>");
        }

        // width
        String width = (String) constraintMap.get("width");
        if (width != null) {
            operands++;
            sb.append("<ogc:PropertyIsLessThanOrEqualTo>");
            sb.append("<ogc:PropertyName>width</ogc:PropertyName>");
            sb.append("<ogc:Literal>" + width + "</ogc:Literal>");
            sb.append("</ogc:PropertyIsLessThanOrEqualTo>");
        }

        // height
        String height = (String) constraintMap.get("height");
        if (height != null) {
            operands++;
            sb.append("<ogc:PropertyIsLessThanOrEqualTo>");
            sb.append("<ogc:PropertyName>height</ogc:PropertyName>");
            sb.append("<ogc:Literal>" + height + "</ogc:Literal>");
            sb.append("</ogc:PropertyIsLessThanOrEqualTo>");
        }

        // exceptions
        String[] exceptions = (String[]) constraintMap.get("exceptions");
        if (exceptions != null && exceptions.length > 0) {
            operands++;
            if (exceptions.length > 1) {
                sb.append("<ogc:Or>");
            }
            for (int i = 0; i < exceptions.length; i++) {
                sb.append("<ogc:PropertyIsEqualTo>");
                sb.append("<ogc:PropertyName>exceptions</ogc:PropertyName>");
                sb.append("<ogc:Literal><![CDATA[" + exceptions[i]
                        + "]]></ogc:Literal>");
                sb.append("</ogc:PropertyIsEqualTo>");
            }
            if (exceptions.length > 1) {
                sb.append("</ogc:Or>");
            }
        }

        if (operands == 0) {
            return null;
        } else if (operands >= 2) {
            sb = new StringBuffer(
                    "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">")
                    .append("<ogc:And>").append(sb).append( "</ogc:And></ogc:Filter>");
        } else {
            sb = new StringBuffer(
                    "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">")
                    .append(sb).append("</ogc:Filter>");
        }
        return sb.toString();
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log $
Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
