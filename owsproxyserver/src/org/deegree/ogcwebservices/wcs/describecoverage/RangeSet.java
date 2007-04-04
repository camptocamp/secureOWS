package org.deegree.ogcwebservices.wcs.describecoverage;

import java.net.URI;

import org.deegree.datatypes.values.ValueEnum;
import org.deegree.ogcbase.Description;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.MetadataLink;

/**
 * @version $Revision: 1.2 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.2 $, $Date: 2005/01/18 22:08:55 $ *  * @since 2.0
 */

public class RangeSet extends Description implements Cloneable {
    
    private URI semantic = null;
    private URI refSys = null;
    private String refSysLabel = null;

    /**
     * 
     * @uml.property name="nullValues"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private ValueEnum nullValues = null;

    /**
     * 
     * @uml.property name="axisDescription"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private AxisDescription[] axisDescription = new AxisDescription[0];
   
  
    /**
     * @param name
     * @param label
     * @throws OGCException
     */
    public RangeSet(String name, String label) throws OGCException {
        super(name, label);
    }
    
    /**
     * @param name
     * @param label
     * @param nullValues
     * @param axisDescription
     * @throws OGCException
     */
    public RangeSet(String name, String label, ValueEnum nullValues, 
                    AxisDescription[] axisDescription) throws OGCException {
        super(name, label);
        this.nullValues = nullValues;
        setAxisDescription(axisDescription);
    }
    
    /**
     * @param name
     * @param label
     * @param description
     * @param metadataLink
     * @param semantic
     * @param refSys
     * @param refSysLabel
     * @param nullValues
     * @param axisDescription
     * @throws OGCException
     */
    public RangeSet(
        String name,
        String label,
        String description,
        MetadataLink metadataLink,
        URI semantic,
        URI refSys,
        String refSysLabel,
        ValueEnum nullValues,
        AxisDescription[] axisDescription)
        throws OGCException {
        super(name, label, description, metadataLink);
        this.semantic = semantic;
        this.refSys = refSys;
        this.refSysLabel = refSysLabel;
        this.nullValues = nullValues;
        if ( axisDescription != null ) {
            this.axisDescription = axisDescription;
        }
    }

    /**
     * @return Returns the axisDescription.
     * 
     * @uml.property name="axisDescription"
     */
    public AxisDescription[] getAxisDescription() {
        return axisDescription;
    }

    /**
     * @param axisDescription The axisDescription to set.
     * 
     * @uml.property name="axisDescription"
     */
    public void setAxisDescription(AxisDescription[] axisDescription) {
        if (axisDescription != null) {
            this.axisDescription = axisDescription;
        }
    }

    /**
     * @return Returns the nullValues.
     * 
     * @uml.property name="nullValues"
     */
    public ValueEnum getNullValues() {
        return nullValues;
    }

    /**
     * @param nullValues The nullValues to set.
     * 
     * @uml.property name="nullValues"
     */
    public void setNullValues(ValueEnum nullValues) {
        this.nullValues = nullValues;
    }

    /**
     * @return Returns the refSys.
     * 
     * @uml.property name="refSys"
     */
    public URI getRefSys() {
        return refSys;
    }

    /**
     * @param refSys The refSys to set.
     * 
     * @uml.property name="refSys"
     */
    public void setRefSys(URI refSys) {
        this.refSys = refSys;
    }

    /**
     * @return Returns the refSysLabel.
     * 
     * @uml.property name="refSysLabel"
     */
    public String getRefSysLabel() {
        return refSysLabel;
    }

    /**
     * @param refSysLabel The refSysLabel to set.
     * 
     * @uml.property name="refSysLabel"
     */
    public void setRefSysLabel(String refSysLabel) {
        this.refSysLabel = refSysLabel;
    }

    /**
     * @return Returns the semantic.
     * 
     * @uml.property name="semantic"
     */
    public URI getSemantic() {
        return semantic;
    }

    /**
     * @param semantic The semantic to set.
     * 
     * @uml.property name="semantic"
     */
    public void setSemantic(URI semantic) {
        this.semantic = semantic;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        ValueEnum nullValues_ = null;
        if ( nullValues_!= null ) {
            nullValues_ = (ValueEnum)nullValues.clone();
        }

        AxisDescription[] ad = new AxisDescription[axisDescription.length];
        for (int i = 0; i < ad.length; i++ ) {
            ad[i] = (AxisDescription)axisDescription[i].clone();
        }
        Description des = (Description)super.clone();
        try {
            return new RangeSet( des.getName(), des.getLabel(), des.getDescription(),
                                 des.getMetadataLink(), semantic, refSys, refSysLabel,
                                 nullValues_, ad);
        } catch(Exception e) {}
        return null;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: RangeSet.java,v $
   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.4  2004/07/14 06:52:48  ap
   no message

   Revision 1.3  2004/07/12 06:12:11  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
