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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Position;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccess;
import org.deegree.security.drm.model.Right;
import org.deegree.security.drm.model.RightType;
import org.deegree.security.drm.model.Role;
import org.deegree.security.drm.model.SecuredObject;
import org.deegree.security.drm.model.User;

/**
 * This <code>Listener</code> reacts on RPC-EditRole events, extracts the
 * submitted role-id and passes the role + known securable objects on the JSP.
 * <p>
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class InitRightsEditorListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {


        try {
    
            // perform access check
            SecurityAccess access = SecurityHelper.acquireAccess(this);
            SecurityHelper.checkForAdminRole(access);
            User user = access.getUser();

            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            if (params.length != 1) {
                throw new RPCException(
                        "Invalid RPC. Exactly one 'param'-element below 'params' is required.");
            }
            if (params[0].getType() != String.class) {
                throw new RPCException(
                        "Invalid RPC. 'param'-element below 'params' must contain a 'string'.");
            }
            int roleId = -1;
            try {
                roleId = Integer.parseInt((String) params[0].getValue());
            } catch (NumberFormatException e) {
                throw new RPCException(
                        "Invalid RPC. Role must be specified by a valid integer value.");
            }

            // get Role to be edited
            Role role = access.getRoleById(roleId);

            // check if user has the right to update the role
            if (!user.hasRight(access, RightType.UPDATE, role)) {
                throw new GeneralSecurityException(
                        "Sie haben keine Berechtigung zur Modifikation der Rolle '"
                                + role.getName() + "'.");
            }
     
            // fetch all datasets for which access can be granted
            //			SecuredObject[] objects = access.getAllSecuredObjects("dataset");
            //			ArrayList grantable = new ArrayList(objects.length);
            //			Role[] userRoles = user.getRoles(access);
            //			for (int i = 0; i < objects.length; i++) {
            //				if (subadminRole.hasRight(access, RightType.GRANT, objects[i])) {
            //					grantable.add(objects[i]);
            //				}
            //			}

            SecuredObject[] layers = access.getAllSecuredObjects(ClientHelper.TYPE_LAYER);
            SecuredObjectRight[] getMapRights = new SecuredObjectRight[layers.length];
            for (int i = 0; i < layers.length; i++) {
                Right right = role.getRights(access, layers[i]).getRight(
                        layers[i], RightType.GETMAP);
                boolean isAccessible = right != null ? true : false;
                Map constraintsMap = new HashMap();
                if (right != null && right.getConstraints() != null) {
                    constraintsMap = buildConstraintsMap(right.getConstraints());
                }
                getMapRights[i] = new SecuredObjectRight(isAccessible,
                        layers[i], constraintsMap);
            }

            SecuredObject[] featureTypes = 
                access.getAllSecuredObjects(ClientHelper.TYPE_FEATURETYPE);
            SecuredObjectRight[] getFeatureRights = new SecuredObjectRight[featureTypes.length];
            for (int i = 0; i < featureTypes.length; i++) {
                Right right = role.getRights(access, featureTypes[i]).getRight(
                        featureTypes[i], RightType.GETFEATURE);
                boolean isAccessible = right != null ? true : false;
                Map constraints = new HashMap();
                getFeatureRights[i] = new SecuredObjectRight(isAccessible,
                        featureTypes[i], constraints);
            }

            getRequest().setAttribute("ROLE", role);
            getRequest().setAttribute("RIGHTS_GET_MAP", getMapRights);
            getRequest().setAttribute("RIGHTS_GET_FEATURE", getFeatureRights);
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE", "Der Rechteeditor konnte nicht aufgerufen werden, "
                            + "da die Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute( "MESSAGE","Der Rechteeditor konnte nicht aufgerufen werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
            e.printStackTrace();
        }

    }

    /**
     * Reconstructs the constraints map (keys are Strings, values are arrays of
     * Strings) from the given <code>Filter</code> expression. This only works
     * if the expression meets the very format used by the
     * <code>StoreRightsListener</code>.
     * 
     * @param filter
     * @return
     * @throws SecurityException
     */
    private Map buildConstraintsMap(Filter filter) throws SecurityException {
        Map constraintsMap = new HashMap();
        if (filter instanceof ComplexFilter) {
            Operation operation = ((ComplexFilter) filter).getOperation();
            if (operation.getOperatorId() == OperationDefines.AND) {
                LogicalOperation andOperation = (LogicalOperation) operation;
                Iterator it = andOperation.getArguments().iterator();
                while (it.hasNext()) {
                    addConstraintToMap((Operation) it.next(), constraintsMap);
                }
            } else {
                addConstraintToMap(operation, constraintsMap);
            }
        }
        return constraintsMap;
    }

    /**
     * Extracts the constraint in the given <code>Operation</code> and adds it
     * to the also supplied <code>Map</code>. The <code>Operation</code>
     * must be of type OperationDefines.OR (with children that are all of type
     * <code>PropertyIsCOMPOperations</code> or <code>BBOX</code>) or of
     * type <code>PropertyIsCOMPOperation</code>(<code>BBOX</code>), in
     * any other case this method will fail.
     * 
     * @param operation
     * @param map
     * @throws SecurityException
     */
    private void addConstraintToMap(Operation operation, Map map) throws SecurityException {

        PropertyPath constraintName = null;
        String[] parameters = new String[1];

        if (operation instanceof PropertyIsCOMPOperation) {
            PropertyIsCOMPOperation comparison = (PropertyIsCOMPOperation) operation;
            try {
                constraintName = ((PropertyName) comparison.getFirstExpression()).getValue();
                parameters[0] = ((Literal) comparison.getSecondExpression()).getValue();
            } catch (ClassCastException e) {
                throw new SecurityException(
                        "Unable to reconstruct constraint map from stored filter expression. "
                                + "Invalid filter format.");
            }
        } else if ( operation.getOperatorId() == OperationDefines.BBOX ||
                    operation.getOperatorId() == OperationDefines.WITHIN ) {
            constraintName = PropertyPathFactory.createPropertyPath( new QualifiedName( "bbox" ) );
            SpatialOperation spatialOperation = (SpatialOperation) operation;
            Envelope envelope = spatialOperation.getGeometry().getEnvelope();
            try {
                Position max = envelope.getMax();
                Position min = envelope.getMin();
                parameters = new String[4];
                parameters [0] = "" + min.getX();
                parameters [1] = "" + min.getY();
                parameters [2] = "" + max.getX();
                parameters [3] = "" + max.getY();
            } catch (ClassCastException e) {
                throw new SecurityException(
                        "Unable to reconstruct constraint map from stored filter expression. "
                                + "Invalid filter format.");
            }
        } else if (operation.getOperatorId() == OperationDefines.OR) {
            LogicalOperation logical = (LogicalOperation) operation;
            Iterator it = logical.getArguments().iterator();
            ArrayList parameterList = new ArrayList( 10 );
            while (it.hasNext()) {
                try {
                    PropertyIsCOMPOperation argument = (PropertyIsCOMPOperation) it.next();
                    PropertyName propertyName = (PropertyName) argument.getFirstExpression();
                    if ( constraintName != null && 
                          (!constraintName.equals(propertyName.getValue() ) ) ) {
                        throw new SecurityException(
                                "Unable to reconstruct constraint map from stored " +
                                "filter expression. Invalid filter format.");
                    } 
                    constraintName = propertyName.getValue();
                    parameterList.add(((Literal) argument.getSecondExpression()).getValue());
                } catch (ClassCastException e) {
                    throw new SecurityException(
                            "Unable to reconstruct constraint map from stored filter expression. "
                                    + "Invalid filter format.");
                }
            }
            parameters = (String[]) parameterList.toArray( new String[parameterList.size()] );
        } else {
            throw new SecurityException(
                    "Unable to reconstruct constraint map from stored filter expression. "
                            + "Invalid filter format.");
        }
        map.put(constraintName, parameters);
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InitRightsEditorListener.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.4  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
