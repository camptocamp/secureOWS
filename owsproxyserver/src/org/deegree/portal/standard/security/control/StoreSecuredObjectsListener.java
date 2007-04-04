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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCException;
import org.deegree.enterprise.control.RPCMember;
import org.deegree.enterprise.control.RPCMethodCall;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.util.Debug;
import org.deegree.security.GeneralSecurityException;
import org.deegree.security.drm.SecurityAccessManager;
import org.deegree.security.drm.SecurityTransaction;
import org.deegree.security.drm.model.SecuredObject;

/**
 * This <code>Listener</code> reacts on 'storeSecuredObjects' events, extracts
 * the contained Layer/FeatureType definitions and updates the
 * <code>SecurityManager</code> accordingly.
 * 
 * Access constraints:
 * <ul>
 * <li>only users that have the 'SEC_ADMIN'-role are allowed
 * </ul>
 * 
 * @author <a href="mschneider@lat-lon.de">Markus Schneider </a>
 */
public class StoreSecuredObjectsListener extends AbstractListener {

    public void actionPerformed(FormEvent event) {
        Debug.debugMethodBegin();

        // keys are Strings (types), values are ArrayLists (which contain
        // Strings)
        Map newObjectTypes = new HashMap();
        // keys are Strings (types), values are ArrayLists (which contain
        // Integers)
        Map oldObjectTypes = new HashMap();

        SecurityAccessManager manager = null;
        SecurityTransaction transaction = null;

        try {
            RPCWebEvent ev = (RPCWebEvent) event;
            RPCMethodCall rpcCall = ev.getRPCMethodCall();
            RPCParameter[] params = rpcCall.getParameters();

            for (int i = 0; i < params.length; i++) {
                if (!(params[0].getValue() instanceof RPCStruct)) {
                    throw new RPCException(
                            "Invalid RPC. Param elements must contain 'struct'-values.");
                }
                RPCStruct struct = (RPCStruct) params[i].getValue();

                // extract details of one SecuredObject
                RPCMember idRPC = struct.getMember("id");
                RPCMember nameRPC = struct.getMember("name");
                RPCMember typeRPC = struct.getMember("type");

                int id;
                String name = null;
                String type = null;

                // extract id
                if (idRPC == null) {
                    throw new RPCException(
                            "Invalid RPC. Every object must have an 'id'.");
                }
                if (!(idRPC.getValue() instanceof String)) {
                    throw new RPCException(
                            "Invalid RPC. 'id' members must be 'string'-values.");
                }
                try {
                    id = Integer.parseInt(((String) idRPC.getValue()));
                } catch (NumberFormatException e) {
                    throw new RPCException(
                            "Invalid RPC. 'id' members must be valid integer values.");
                }
                // extract name
                if (nameRPC != null) {
                    if (!(nameRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'name' members must be 'string'-values.");
                    } 
                    name = (String) nameRPC.getValue();                    
                }
                // extract type
                if (typeRPC != null) {
                    if (!(typeRPC.getValue() instanceof String)) {
                        throw new RPCException(
                                "Invalid RPC. 'type' members must be 'string'-values.");
                    } 
                    type = (String) typeRPC.getValue();
                    
                }
                if (name == null) {
                    throw new GeneralSecurityException(
                            "Every SecuredObject must have a name.");
                }
                if (type == null) {
                    throw new GeneralSecurityException(
                            "Every SecuredObject must have a type.");
                }

                // new or existing SecuredObject?
                if (id == -1) {
                    ArrayList list = (ArrayList) newObjectTypes.get(type);
                    if (list == null) {
                        list = new ArrayList(20);
                        newObjectTypes.put(type, list);
                    }
                    list.add(name);
                } else {
                    ArrayList list = (ArrayList) oldObjectTypes.get(type);
                    if (list == null) {
                        list = new ArrayList(20);
                        oldObjectTypes.put(type, list);
                    }
                    list.add(new Integer(id));
                }
            }

            // get Transaction and perform access check
            manager = SecurityAccessManager.getInstance();
            transaction = SecurityHelper.acquireTransaction(this);
            SecurityHelper.checkForAdminRole (transaction);

            // remove deleted Layers
            SecuredObject[] obsoleteLayers = getObjectsToDelete(
                    (ArrayList) oldObjectTypes.get(ClientHelper.TYPE_LAYER), transaction
                            .getAllSecuredObjects(ClientHelper.TYPE_LAYER));
            for (int i = 0; i < obsoleteLayers.length; i++) {
                transaction.deregisterSecuredObject(obsoleteLayers[i]);
            }

            // register new Layers
            ArrayList newLayerList = (ArrayList) newObjectTypes.get(ClientHelper.TYPE_LAYER);
            if (newLayerList != null) {
                Iterator it = newLayerList.iterator();
                while (it.hasNext()) {
                    String name = (String) it.next ();
                    transaction.registerSecuredObject(ClientHelper.TYPE_LAYER, name, name);
                }
            }

            // remove deleted FeatureTypes 
            SecuredObject[] obsoleteFeatureTypes = getObjectsToDelete(
                    (ArrayList) oldObjectTypes.get(ClientHelper.TYPE_FEATURETYPE), transaction
                            .getAllSecuredObjects(ClientHelper.TYPE_FEATURETYPE));
            for (int i = 0; i < obsoleteFeatureTypes.length; i++) {
                transaction.deregisterSecuredObject(obsoleteFeatureTypes[i]);
            }

            // register new FeatureTypes
            ArrayList newFeatureTypeList = (ArrayList) newObjectTypes.get(ClientHelper.TYPE_FEATURETYPE);
            if (newFeatureTypeList != null) {
                Iterator it = newFeatureTypeList.iterator();
                while (it.hasNext()) {
                    String name = (String) it.next ();
                    transaction.registerSecuredObject(ClientHelper.TYPE_FEATURETYPE, name, name);
                }
            }            
            
            manager.commitTransaction(transaction);
            transaction = null;

            getRequest()
                    .setAttribute(
                            "MESSAGE",
                            "Ihre Änderungen wurden erfolgreich in der Datenbank gespeichert.<BR/>"
                                    + "<BR/><p><a href='javascript:initSecuredObjectsEditor()'>--> zurück zum"
                                    + " Layer-/FeatureType-Editor</a></p>");
        } catch (RPCException e) {
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute(
                    "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da Ihre Anfrage fehlerhaft war.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            getRequest().setAttribute("SOURCE", this.getClass().getName());
            getRequest().setAttribute(
                    "MESSAGE",
                    "Ihre Änderungen konnten nicht in der Datenbank gespeichert werden, "
                            + "da ein Fehler aufgetreten ist.<br><br>"
                            + "Die Fehlermeldung lautet: <code>"
                            + e.getMessage() + "</code>");
            setNextPage("error.jsp");
        } finally {
            if (manager != null && transaction != null) {
                try {
                    manager.abortTransaction(transaction);
                } catch (GeneralSecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Debug.debugMethodEnd();
    }

    private SecuredObject[] getObjectsToDelete(ArrayList remainingObjects,
            SecuredObject[] presentObjects) {
        Set lookup = new HashSet();
        ArrayList deleteList = new ArrayList(10);
        if (remainingObjects != null) {
            lookup = new HashSet(remainingObjects);
        }
        for (int i = 0; i < presentObjects.length; i++) {
            if (!lookup.contains(new Integer(presentObjects[i].getID()))) {
                deleteList.add(presentObjects[i]);
            }
        }
        return (SecuredObject[]) deleteList
                .toArray(new SecuredObject[deleteList.size()]);
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: StoreSecuredObjectsListener.java,v $
Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/07/13 08:10:56  poth
file header added / references to Debug.XXXX removed

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
