package org.deegree.ogcwebservices.wcs;

/**
 * 
 *
 * @version $Revision: 1.1.1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1.1.1 $, $Date: 2005/01/05 10:32:18 $
 *
 * @since 2.0
 */
public class InterpolationMethod {

    public static final String NEAREST_NEIGHBOR = "nearest neighbor";
    public static final String BILINEAR = "bilinear";
    public static final String LOST_AREA = "lost area";
    public static final String BARYCENTRIC = "barycentric";
    public static final String NONE = "none";
    
    public String value = NEAREST_NEIGHBOR;
    
    /**
     * default = NEAREST_NEIGHBOR
     */
    public InterpolationMethod() {
    }
    
    /**
     * @param value
     */
    public InterpolationMethod(String value) {
        this.value = value;
    }
    
    /**
     * Compares the specified object with
     * this enum for equality.
     */
    public boolean equals(Object object) {
        if (object!=null && getClass().equals(object.getClass()))
        {
            return ((InterpolationMethod) object).value.equals( value );
        }
        return false;
    }
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: InterpolationMethod.java,v $
   Revision 1.1.1.1  2005/01/05 10:32:18  poth
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */

