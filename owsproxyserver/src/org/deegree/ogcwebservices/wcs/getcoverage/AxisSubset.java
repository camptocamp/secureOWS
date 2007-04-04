package org.deegree.ogcwebservices.wcs.getcoverage;

import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.ValueEnumBase;

/**
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2005/01/18 22:08:55 $
 *
 * @since 2.0
 */
public class AxisSubset extends ValueEnumBase {
    
    private String name = null;
    
    /**
     * @param interval
     * @param name
     * @throws IllegalArgumentException
     */
    public AxisSubset(Interval[] interval, String name) throws IllegalArgumentException {
        super(interval);
        this.name = name;
    }
    
    /**
     * @param singleValue
     * @param name
     * @throws IllegalArgumentException
     */
    public AxisSubset(TypedLiteral[] singleValue, String name) throws IllegalArgumentException {
        super(singleValue);
        this.name = name;
    }
    
    /**
     * @param interval
     * @param singleValue
     * @param name
     */
    public AxisSubset(Interval[] interval, TypedLiteral[] singleValue, String name) 
                      throws IllegalArgumentException {
        super(interval, singleValue);
        this.name = name;
    }

    /**
     * @return Returns the name.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: AxisSubset.java,v $
   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
