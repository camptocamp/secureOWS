package org.deegree.security.drm.model;

import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.security.GeneralSecurityException;

/**
 * A <code>Right</code> instance encapsulates a <code>SecurableObject</code>, * a <code>RightType</code> and optional constraints which restrict it's * applicability. * <p> * For example, one instance of a <code>RightSet</code> may be the * 'access'-Right to a geographic dataset restricted to a certain area and * weekdays. The situation (requested area and current time) is coded as a * <code>Feature</code> object. *  * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a> * @version $Revision: 1.7 $
 */

public class Right {

    private SecurableObject object;
    private RightType type;
    private Filter constraints;


    /**
     * Creates a new <code>Right</code> -instance (with no constraints).
     * 
     * @param object
     * @param type
     */
    public Right(SecurableObject object, RightType type) {
        this.object = object;
        this.type = type;
    }

    /**
     * Creates a new <code>Right</code> -instance.
     * 
     * @param object
     * @param type
     * @param constraints
     *            null means that no constraints are defined
     */
    public Right(SecurableObject object, RightType type, Filter constraints) {
        this(object, type);
        this.constraints = constraints;
    }

    /**
     * Returns the associated <code>SecurableObject</code>.
     */
    public SecurableObject getSecurableObject() {
        return object;
    }

    /**
     * Returns the associated <code>RightType</code>.
     * 
     */
    public RightType getType() {
        return type;
    }

    /**
     * Returns the restrictions (the parameters) of this <code>Right</code>.
     * 
     * @return null, if no constraints are defined
     * 
     */
    public Filter getConstraints() {
        return constraints;
    }

    /**
     * Generates the disjunctive combination of the instance and the submitted
     * <code>Right</code>, so that the new <code>Right</code> has the
     * permissions of both instances.
     * 
     * @param that
     */
    public Right merge(Right that) throws GeneralSecurityException {
        Right combinedRight = null;

        if (!this.object.equals(that.object)) {
            throw new GeneralSecurityException( "Trying to merge right on securable object '"
                            + this.object.id + "' and on object '" + that.object.id
                            + "', but only rights on the same object may be merged.");
        }
        if (this.type.getID() != that.type.getID()) {
            throw new GeneralSecurityException("Trying to merge right of type '"
                    + this.type.getName() + "' and right of type '"
                    + that.type.getName() + "', but only rights of the same type may be merged.");
        }

        // check if any of the rights has no constraints
        if (this.constraints == null && that.constraints == null) {
            combinedRight = new Right(object, type, null);
        } else if (this.constraints == null && that.constraints != null) {
            combinedRight = new Right(object, type, that.constraints);
        } else if (this.constraints != null && that.constraints == null) {
            combinedRight = new Right(object, type, this.constraints);
        } else if (that.constraints == null) {
            combinedRight = that;
        } else {
            Filter combinedConstraints = new ComplexFilter(
                    (ComplexFilter) this.constraints,
                    (ComplexFilter) that.constraints, OperationDefines.OR);
            combinedRight = new Right(object, type, combinedConstraints);
           
        }
        return combinedRight;
    }

    /**
     * Checks if the <code>Right</code> applies on the given
     * <code>SecurableObject</code> and in a concrete situation (the situation
     * is represented by the given <code>Feature</code>).
     * 
     * @param object
     * @param situation
     * @throws GeneralSecurityException
     */
    public boolean applies(SecurableObject object, Feature situation)
            throws GeneralSecurityException {
        boolean applies = false;
        if (object.equals(this.object)) {
            try {
                if ( constraints != null ) {
                    applies = constraints.evaluate(situation);
                } else {
                    applies = true;
                }
            } catch (FilterEvaluationException e) {
                e.printStackTrace ();
                throw new GeneralSecurityException("Error evaluating parameters (filter expression): "
                                + e.getMessage());
            }
        }
        return applies;
    }

    /**
     * Checks if the <code>Right</code> applies on the given
     * <code>SecurableObject</code> and in unrestricted manner (w/o
     * constraints).
     * 
     * @param object
     * @throws GeneralSecurityException
     */
    public boolean applies(SecurableObject object) {
        boolean applies = false;
        if (object.equals(this.object)) {
            if (constraints == null) {
                applies = true;
            }
        }
        return applies;
    }

    /**
     * Returns a <code>String</code> representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Id: ").append(type.getID()).append(
                ", Name: ").append(type.getName()).append(", ");
        if (constraints != null) {
            sb.append("Constraints: " + constraints.toXML());
        } else {
            sb.append("Constraints: none");
        }
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Right.java,v $
Revision 1.7  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
