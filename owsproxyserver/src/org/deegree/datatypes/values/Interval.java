package org.deegree.datatypes.values;

import java.io.Serializable;
import java.net.URI;

/**
 * @version $Revision: 1.4 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.4 $, $Date: 2006/07/12 19:37:55 $ *  * @since 2.0
 */

public class Interval extends ValueRange implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private TypedLiteral res = null;

    
    
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
    public Interval(TypedLiteral min, TypedLiteral max, URI type, URI semantic, TypedLiteral res) {
        super(min, max, type, semantic);
        this.res = res;
    }

    /**
     * @param min
     * @param max
     * @param type
     * @param semantic
     * @param atomic
     * @param closure
     */
    public Interval(TypedLiteral min, TypedLiteral max, URI type, URI semantic, 
                    boolean atomic, Closure closure, TypedLiteral res) {
        super(min, max, type, semantic, atomic, closure);
        this.res = res;
    }

    /**
     * @return Returns the res.
     * 
     */
    public TypedLiteral getRes() {
        return res;
    }

    /**
     * @param res The res to set.
     * 
     */
    public void setRes(TypedLiteral res) {
        this.res = res;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        ValueRange vr = (ValueRange)super.clone();
        TypedLiteral res_ = (TypedLiteral)res.clone();
        return new Interval( vr.getMin(), vr.getMax(), vr.getType(), vr.getSemantic(), 
                             vr.isAtomic(), vr.getClosure(), res_);
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Interval.java,v $
   Revision 1.4  2006/07/12 19:37:55  poth
   code formatting

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
