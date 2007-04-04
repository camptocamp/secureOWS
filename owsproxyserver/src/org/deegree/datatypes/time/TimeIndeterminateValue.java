package org.deegree.datatypes.time;

import java.io.Serializable;

/**
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2005/11/16 13:44:59 $
 *
 * @since 2.0
 */
public class TimeIndeterminateValue implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Comment for <code>AFTER</code>
     */
    public static final String AFTER = "after";
    /**
     * Comment for <code>BEFORE</code>
     */
    public static final String BEFORE = "before";
    /**
     * Comment for <code>NOW</code>
     */
    public static final String NOW = "now";
    /**
     * Comment for <code>UNKNOWN</code>
     */
    public static final String UNKNOWN = "unknown";

    /**
     * Comment for <code>value</code>
     */
    public String value = NOW;

     
    /**
     * default = NOW
     */
    public TimeIndeterminateValue() {
    }

    /**
     * @param value
     */
    public TimeIndeterminateValue(String value) {
        this.value = value;
    }

    /**
     * Compares the specified object with
     * this enum for equality.
     */
    public boolean equals(Object object)
    {
        if (object!=null && getClass().equals(object.getClass()))
        {
            return ((TimeIndeterminateValue) object).value.equals( value );
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final long longCode=value.hashCode();
        return (((int)(longCode >>> 32)) ^ (int)longCode) + 37*super.hashCode();
    }
}

/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: TimeIndeterminateValue.java,v $
   Revision 1.2  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.1.1.1.2.1  2005/11/14 11:34:29  deshmukh
   inserted: serialVersionID

   Revision 1.1.1.1  2005/01/05 10:39:05  poth
   no message

   Revision 1.4  2004/08/16 06:23:33  ap
   no message

   Revision 1.3  2004/07/21 06:16:00  ap
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:01  ap
   no message


********************************************************************** */
