package org.deegree.security.drm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deegree.model.feature.Feature;
import org.deegree.security.GeneralSecurityException;

/**
 * A <code>RightSet</code> encapsulates a number of <code>Right</code>
 * objects. This are grouped by the <code>SecurableObject</code> for which
 * they apply to support an efficient implementation of the merge()-operation.
 * The merge()-operation results in a <code>RightSet</code> that contains the
 * logical rights of boths sets, but only one <code>Right</code> object of
 * each <code>RightType</code> (and <code>SecurableObject</code>). This is
 * accomplished by merging the constraints of the <code>Rights</code> of the
 * same type (and object).
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.5 $
 */
public class RightSet {

    // keys are SecurableObjects (for which the rights are defined), values
    // are Maps (keys are RightTypes, values are Rights)
    private Map secObjects = new HashMap();

    RightSet() {
    }

    public RightSet(Right[] rights) {
        for (int i = 0; i < rights.length; i++) {
            Map rightMap = (Map) secObjects.get(rights[i].getSecurableObject());
            if (rightMap == null) {
                rightMap = new HashMap();
            }
            rightMap.put(rights[i].getType(), rights[i]);
            secObjects.put(rights[i].getSecurableObject(), rightMap);
        }
    }

    /**
     * Checks if the <code>RightSet</code> contains the permissions for a
     * <code>SecurableObject</code> and a concrete situation (the situation is
     * represented by the given <code>Feature</code>).
     * 
     * @param object
     * @param type
     * @param situation
     * @throws GeneralSecurityException
     */
    public boolean applies(SecurableObject object, RightType type,
            Feature situation) throws GeneralSecurityException {
        boolean applies = false;
        Map rightMap = (Map) secObjects.get(object);
        if (rightMap != null) {
            Right right = (Right) rightMap.get(type);
            if (right != null) {
                applies = right.applies(object, situation);
            }
        }
        return applies;
    }

    /**
     * Checks if the <code>RightSet</code> contains the (unrestricted)
     * permissions for a <code>SecurableObject</code> and a certain type of
     * right.
     * 
     * @param object
     * @param type
     * @throws GeneralSecurityException
     */
    public boolean applies(SecurableObject object, RightType type) {
        boolean applies = false;
        Map rightMap = (Map) secObjects.get(object);
        if (rightMap != null) {
            Right right = (Right) rightMap.get(type);
            if (right != null) {
                applies = right.applies(object);
            }

        }
        return applies;
    }

    /**
     * Returns the <code>Right</code> of the specified <code>RightType</code>
     * that this <code>RightSet</code> defines on the specified
     * <code>SecurableObject</code>.
     */
    public Right getRight(SecurableObject secObject, RightType type) {
        Right right = null;
        if (secObjects.get(secObject) != null) {
            right = (Right) ((Map) secObjects.get(secObject)).get(type);
        }
        return right;
    }

    /**
     * Returns the encapulated <code>Rights</code> (for one
     * <code>SecurableObject</code>) as an one-dimensional array.
     */
    public Right[] toArray(SecurableObject secObject) {
        Right[] rights = new Right[0];
        Map rightMap = (Map) secObjects.get(secObject);
        if (rightMap != null) {
            rights = (Right[]) rightMap.values().toArray(
                    new Right[rightMap.size()]);
        }
        return rights;
    }

    /**
     * Returns the encapulated <code>Rights</code> as a two-dimensional array:
     * <ul>
     * <li>first index: runs the different <code>SecurableObjects</code>
     * <li>second index: runs the different <code>Rights</code>
     * </ul>
     */
    public Right[][] toArray2() {
        ArrayList secObjectList = new ArrayList();
        Iterator it = secObjects.values().iterator();
        while (it.hasNext()) {
            Map rightMap = (Map) it.next();
            Right[] rights = (Right[]) rightMap.values().toArray(
                    new Right[rightMap.size()]);
            secObjectList.add(rights);
        }
        return (Right[][]) secObjectList
                .toArray(new Right[secObjectList.size()][]);
    }

    /**
     * Produces the logical disjunction of two <code>RightSets</code>.
     * 
     * @param that
     * @return
     */
    public RightSet merge(RightSet that) {

        ArrayList mergedRights = new ArrayList(20);
        Iterator secObjectsIt = this.secObjects.keySet().iterator();

        // add all rights from 'this' (and merge them with corresponding right
        // from 'that')
        while (secObjectsIt.hasNext()) {
            SecurableObject secObject = (SecurableObject) secObjectsIt.next();
            Map thisRightMap = (Map) this.secObjects.get(secObject);
            Map thatRightMap = (Map) that.secObjects.get(secObject);
            Iterator rightIt = (thisRightMap).keySet().iterator();
            while (rightIt.hasNext()) {
                RightType type = (RightType) rightIt.next();
                Right mergedRight = (Right) thisRightMap.get(type);

                // find corresponding Right (if any) in the other RightSet
                if (thatRightMap != null && thatRightMap.get(type) != null) {
                    try {
                        mergedRight = mergedRight.merge((Right) thatRightMap.get(type));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
                mergedRights.add(mergedRight);
            }
        }

        // add role rights from 'that'
        secObjectsIt = that.secObjects.keySet().iterator();
        while (secObjectsIt.hasNext()) {
            SecurableObject secObject = (SecurableObject) secObjectsIt.next();
            Map thisRightMap = (Map) this.secObjects.get(secObject);
            Map thatRightMap = (Map) that.secObjects.get(secObject);

            Iterator it = thatRightMap.keySet().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                RightType type = (RightType) o;
                // find corresponding Right (if none, add)
                if (thisRightMap == null || thisRightMap.get(type) == null) {
                    mergedRights.add( thatRightMap.get( type ) );
                }
            }
        }
        return new RightSet((Right[]) mergedRights.toArray(new Right[mergedRights.size()]));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("RightSet:");
        Iterator it = secObjects.keySet().iterator();
        while (it.hasNext()) {
            SecurableObject secObject = (SecurableObject) it.next();
            sb.append("on SecurableObject ").append(secObject).append("\n");
            Map rightMap = (Map) secObjects.get(secObject);
            Iterator rights = rightMap.keySet().iterator();
            while (rights.hasNext()) {
                RightType rightType = (RightType) rights.next();
                sb.append("- Right ").append(rightMap.get(rightType)).append(
                        "\n");
            }
        }
        return sb.toString();
    }
    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: RightSet.java,v $
Revision 1.5  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
