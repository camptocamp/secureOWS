
package org.deegree.ogcbase;

import java.io.Serializable;

import org.deegree.ogcwebservices.MetadataLink;

/**
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2005/01/18 22:08:54 $
 *
 * @since 2.0
 */
public class Description extends DescriptionBase implements Cloneable, Serializable {
    
    private String label = null;
    
    /**
     * @param name
     * @param label
     */
    public Description(String name, String label) throws OGCException  {
        super(name);
        setLabel(label);
    }
    
    /**
     * @param description
     * @param name
     * @param metadataLink
     */
    public Description( String name, String label, String description,
                        MetadataLink metadataLink ) throws OGCException  {
        super( name, description, metadataLink);
        setLabel(label); 
    }

    /**
     * @return Returns the label.
     * 
     * @uml.property name="label"
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label to set.
     * 
     * @uml.property name="label"
     */
    public void setLabel(String label) throws OGCException {
        if (label == null) {
            throw new OGCException("label must be <> null for Description");
        }
        this.label = label;
    }

    
    public Object clone() {
        try {
            MetadataLink metadataLink = getMetadataLink();
            if (metadataLink != null) {
                metadataLink = (MetadataLink)metadataLink.clone();
            }
            return new Description( getName(), label, getDescription(), metadataLink );
                        
        } catch(Exception e) {}
        return null;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Description.java,v $
   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/08/16 06:23:33  ap
   no message

   Revision 1.3  2004/07/12 11:14:19  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:52:07  ap
   no message


********************************************************************** */
