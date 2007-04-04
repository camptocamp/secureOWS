package org.deegree.datatypes.values;

import java.net.URI;

/**
 * @version $Revision: 1.4 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.4 $, $Date: 2006/05/15 06:55:37 $ *  * @since 2.0
 */

public class Values extends ValueEnum implements Cloneable {

    private static final long serialVersionUID = 1L;
    private TypedLiteral default_ = null;
    
    /**
     * @param singleValue
     * @param default_
     */
    public Values(Interval[] interval, TypedLiteral[] singleValue, TypedLiteral default_) {
        super(interval,singleValue);
        this.default_ = default_;
    }    
    
    /**
     * @param singleValue
     * @param type
     * @param semantic
     * @param default_
     */
    public Values(Interval[] interval, TypedLiteral[] singleValue, URI type, 
                  URI semantic, TypedLiteral default_) {
        super( interval, singleValue, type, semantic);
        this.default_ = default_;
    }
   
    
    /**
     * @return Returns the default_.
     */
    public TypedLiteral getDefault() {
        return default_;
    }

    /**
     * @param default_ The default_ to set.
     */
    public void setDefault(TypedLiteral default_) {
        this.default_ = default_;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        ValueEnum ve = (ValueEnum)super.clone();
        TypedLiteral default__ = null;
        if ( default_ != null ) {
            default__ = (TypedLiteral) default_.clone();
        }
        return new Values( ve.getInterval(), ve.getSingleValue(), ve.getType(), 
                           ve.getSemantic(), default__ );
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Values.java,v $
   Revision 1.4  2006/05/15 06:55:37  poth
   *** empty log message ***

   Revision 1.3  2005/11/16 13:45:00  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/14 11:34:50  deshmukh
   inserted: serialVersionID

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:02  ap
   no message


********************************************************************** */
