package org.deegree.ogcwebservices.wcs;

/**
 * @version $Revision: 1.2 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.2 $, $Date: 2005/01/18 22:08:55 $ *  * @since 2.0
 */

public class SupportedInterpolations {

    /**
     * 
     * @uml.property name="interpolationMethod"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private InterpolationMethod[] interpolationMethod = new InterpolationMethod[0];

    /**
     * 
     * @uml.property name="default_"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private InterpolationMethod default_ = new InterpolationMethod();
   
    
    /**
     * default constructor
     */
    public SupportedInterpolations() {
    }
    
    /**
     * @param interpolationMethod
     */
    public SupportedInterpolations(InterpolationMethod[] interpolationMethod) {
        setInterpolationMethod(interpolationMethod);
    }
    
    /**
     * @param interpolationMethod
     * @param default_
     */
    public SupportedInterpolations(InterpolationMethod[] interpolationMethod, 
                                   InterpolationMethod default_) {
        setInterpolationMethod(interpolationMethod);
        this.default_ = default_;
    }
    /**
     * @return Returns the default_.
     */
    public InterpolationMethod getDefault() {
        return default_;
    }

    /**
     * @param default_ The default_ to set.
     */
    public void setDefault(InterpolationMethod default_) {
        this.default_ = default_;
    }

    /**
     * @return Returns the interpolationMethod.
     * 
     * @uml.property name="interpolationMethod"
     */
    public InterpolationMethod[] getInterpolationMethod() {
        return interpolationMethod;
    }

    /**
     * @param interpolationMethod The interpolationMethod to set.
     * 
     * @uml.property name="interpolationMethod"
     */
    public void setInterpolationMethod(InterpolationMethod[] interpolationMethod) {
        if (interpolationMethod == null) {
            interpolationMethod = new InterpolationMethod[0];
        }
        this.interpolationMethod = interpolationMethod;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: SupportedInterpolations.java,v $
   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
