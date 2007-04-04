package org.deegree.ogcbase;

import org.deegree.ogcwebservices.MetadataLink;

/**
 * @version $Revision: 1.2 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.2 $, $Date: 2005/01/18 22:08:54 $ *  * @since 2.0
 */

public class DescriptionBase implements Cloneable {
        
    private String name = null;
    private String description = null;

    /**
     * 
     * @uml.property name="metadataLink"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private MetadataLink metadataLink = null;

    
    /**
     * just <tt>name</tt> is mandatory
     * 
     * @param name
     */
    public DescriptionBase(String name) throws OGCException {
       setName(name);   
    }
    
    /**
     * @param description
     * @param name
     * @param metadataLink
     */
    public DescriptionBase(String name, String description, MetadataLink metadataLink)
                            throws OGCException {
        this.description = description;
        setName(name);
        this.metadataLink = metadataLink;
    }

    /**
     * @return Returns the description.
     * 
     * @uml.property name="description"
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     * 
     * @uml.property name="description"
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the metadataLink.
     * 
     * @uml.property name="metadataLink"
     */
    public MetadataLink getMetadataLink() {
        return metadataLink;
    }

    /**
     * @param metadataLink The metadataLink to set.
     * 
     * @uml.property name="metadataLink"
     */
    public void setMetadataLink(MetadataLink metadataLink) {
        this.metadataLink = metadataLink;
    }

    /**
     * @return Returns the name.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     * 
     * @uml.property name="name"
     */
    public void setName(String name) throws OGCException {
        if (name == null) {
            throw new OGCException("name must be <> null for DescriptionBase");
        }
        this.name = name;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            MetadataLink ml = null;
            if ( metadataLink != null ) {
                ml = (MetadataLink)metadataLink.clone();
            }
            return new DescriptionBase( name, description, ml );
        } catch(Exception e) {}
        return null;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: DescriptionBase.java,v $
   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:52:07  ap
   no message


********************************************************************** */
