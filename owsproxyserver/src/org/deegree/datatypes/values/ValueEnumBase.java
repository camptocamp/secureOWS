package org.deegree.datatypes.values;

import java.io.Serializable;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2006/07/12 19:37:55 $ *  * @since 2.0
 */

public abstract class ValueEnumBase implements Serializable {

    private TypedLiteral[] singleValue = null;
    private Interval[] interval = null;


    /**
     * @param singleValue
     */
    public ValueEnumBase(TypedLiteral[] singleValue) throws IllegalArgumentException {
        setSingleValue(singleValue);
    }
    
    /**
     * @param interval
     */
    public ValueEnumBase(Interval[] interval) throws IllegalArgumentException  {
        setInterval(interval);
    }
    
    /**
     * @param singleValue
     * @param interval
     */
    public ValueEnumBase(Interval[] interval, TypedLiteral[] singleValue) 
                         throws IllegalArgumentException {
        setSingleValue(singleValue);
        setInterval(interval);
    }

    /**
     * @return Returns the interval.
     *    
     */
    public Interval[] getInterval() {
        return interval;
    }

    /**
     * @param interval The interval to set.
     * 
     */
    public void setInterval(Interval[] interval)
        throws IllegalArgumentException {
        if (interval == null && singleValue == null) {
            throw new IllegalArgumentException(
                "at least interval or singleValue must "
                    + "be <> null in ValueEnumBase");
        }
        this.interval = interval;
    }

    /**
     * @return Returns the singleValue.
     * 
     */
    public TypedLiteral[] getSingleValue() {
        return singleValue;
    }

    /**
     * @param singleValue The singleValue to set.
     * 
     */
    public void setSingleValue(TypedLiteral[] singleValue)
        throws IllegalArgumentException {
        if (interval == null && singleValue == null) {
            throw new IllegalArgumentException(
                "at least interval or singleValue must "
                    + "be <> null in ValueEnumBase");
        }
        this.singleValue = singleValue;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ValueEnumBase.java,v $
   Revision 1.3  2006/07/12 19:37:55  poth
   code formatting

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/08/16 06:23:33  ap
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:02  ap
   no message


********************************************************************** */
