package org.deegree.datatypes.values;

import java.net.URI;

/**
 * 
 *
 * @version $Revision: 1.4 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.4 $, $Date: 2006/07/12 19:37:55 $
 *
 * @since 2.0
 */
public class ValueEnum extends ValueEnumBase implements Cloneable {
   
    private static final long serialVersionUID = 1L;
    private URI type = null;
    private URI semantic = null;    

    /**
     * @param interval
     */
    public ValueEnum(Interval[] interval, TypedLiteral[] singleValue) {
        super(interval, singleValue);
    }

    /**
     * @param interval
     */
    public ValueEnum(Interval[] interval, TypedLiteral[] singleValue, URI type, URI semantic) {
        super(interval, singleValue);
        this.type = type;
        this.semantic = semantic;
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
        TypedLiteral[] singleValue = getSingleValue();
        TypedLiteral[] singleValue_ = new TypedLiteral[singleValue.length];
        for (int i = 0; i < singleValue_.length; i++) {
            singleValue_[i] = (TypedLiteral)singleValue[i].clone();
        }
        
        Interval[] interval = getInterval();
        Interval[] interval_ = new Interval[interval.length];
        for (int i = 0; i < interval_.length; i++) {
            interval_[i] = (Interval)interval[i].clone();
        }
        
        return new ValueEnum(interval_, singleValue_, type, semantic);
        
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ValueEnum.java,v $
   Revision 1.4  2006/07/12 19:37:55  poth
   code formatting

   Revision 1.3  2005/11/16 13:45:00  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/14 11:34:50  deshmukh
   inserted: serialVersionID

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.1  2004/05/25 12:55:02  ap
   no message


********************************************************************** */
