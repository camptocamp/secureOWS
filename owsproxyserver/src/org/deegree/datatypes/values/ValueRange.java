package org.deegree.datatypes.values;

import java.io.Serializable;
import java.net.URI;

/**
 * @version $Revision: 1.5 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: mays $ *  * @version 1.0. $Revision: 1.5 $, $Date: 2005/12/20 09:16:04 $ *  * @since 2.0
 */

public class ValueRange implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private TypedLiteral min = null;
    private TypedLiteral max = null;
    private TypedLiteral spacing = null;

    
    private URI type = null;
    private URI semantic = null;
    private boolean atomic = false;

    private Closure closure = new Closure();

   
    /**
     * default:
     * atomic = false
     * closure = closed
     * 
     * @param min
     * @param max
     * @param type
     * @param semantic
     */
    public ValueRange(TypedLiteral min, TypedLiteral max, URI type, URI semantic) {
        this.min = min;
        this.max = max;
        this.type = type;
        this.semantic = semantic;
    }
    
    /**
     * 
     * @param min
     * @param max
     * @param spacing
     */
    public ValueRange(TypedLiteral min, TypedLiteral max, TypedLiteral spacing) {
        this.min = min;
        this.max = max;
        this.spacing = spacing;
    }
    
    /**
     * @param min
     * @param max
     * @param type
     * @param semantic
     * @param atomic
     * @param closure
     */
    public ValueRange(TypedLiteral min, TypedLiteral max, URI type, URI semantic, 
                      boolean atomic, Closure closure) {
        this.min = min;
        this.max = max;
        this.type = type;
        this.semantic = semantic;
        this.atomic = atomic;
        this.closure = closure;
    }
    
    /**
     * @param min
     * @param max
     * @param type
     * @param semantic
     * @param atomic
     * @param closure
     */
    public ValueRange(TypedLiteral min, TypedLiteral max, TypedLiteral spacing, URI type, 
                      URI semantic, boolean atomic, Closure closure) {
        this.min = min;
        this.max = max;
        this.type = type;
        this.semantic = semantic;
        this.atomic = atomic;
        this.closure = closure;
        this.spacing = spacing;
    }

    /**
     * @return Returns the atomic.
     * 
     */
    public boolean isAtomic() {
        return atomic;
    }

    /**
     * @param atomic The atomic to set.
     * 
     */
    public void setAtomic(boolean atomic) {
        this.atomic = atomic;
    }

    /**
     * @return Returns the closure.
     * 
     */
    public Closure getClosure() {
        return closure;
    }

    /**
     * @param closure The closure to set.
     * 
     */
    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    /**
     * @return Returns the max.
     * 
     */
    public TypedLiteral getMax() {
        return max;
    }

    /**
     * @param max The max to set.
     * 
     */
    public void setMax(TypedLiteral max) {
        this.max = max;
    }

    /**
     * @return Returns the min.
     * 
     */
    public TypedLiteral getMin() {
        return min;
    }

    /**
     * @param min The min to set.
     * 
     */
    public void setMin(TypedLiteral min) {
        this.min = min;
    }

    /**
	 * @return Returns the spacing.
	 */
	public TypedLiteral getSpacing() {
		return spacing;
	}

	/**
	 * @param spacing The spacing to set.
	 */
	public void setSpacing(TypedLiteral spacing) {
		this.spacing = spacing;
	}

	/**
     * @return Returns the semantic.
     * 
     */
    public URI getSemantic() {
        return semantic;
    }

    /**
     * @param semantic The semantic to set.
     * 
     */
    public void setSemantic(URI semantic) {
        this.semantic = semantic;
    }

    /**
     * @return Returns the type.
     * 
     */
    public URI getType() {
        return type;
    }

    /**
     * @param type The type to set.
     * 
     */
    public void setType(URI type) {
        this.type = type;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        TypedLiteral min_ = (TypedLiteral)min.clone(); 
        TypedLiteral max_ = (TypedLiteral)max.clone();
        TypedLiteral space_ = (TypedLiteral)spacing.clone(); 
        Closure closure_ = new Closure( closure.value );
        return new ValueRange (min_, max_, space_, type, semantic, atomic, closure_);
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ValueRange.java,v $
   Revision 1.5  2005/12/20 09:16:04  mays
   add getter and setter for TypedLiteral spacing

   Revision 1.4  2005/12/20 09:09:09  poth
   no message

   Revision 1.3  2005/11/16 13:45:00  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/14 11:34:50  deshmukh
   inserted: serialVersionID

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/08/16 06:23:33  ap
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:02  ap
   no message


********************************************************************** */
