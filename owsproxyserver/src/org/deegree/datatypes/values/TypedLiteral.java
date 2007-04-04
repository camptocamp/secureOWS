package org.deegree.datatypes.values;

import java.io.Serializable;
import java.net.URI;

/**
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/05/25 14:46:49 $
 *
 * @since 2.0
 */
public class TypedLiteral implements Cloneable, Serializable {
    
    private static final long serialVersionUID = 1L;
    private String value = null;
    private URI type = null;
    /**
     * Identifies the unit of measure of this literal input or output. This unit
     * of measure should be referenced for any numerical value that has units
     * (e.g., "meters", but not a more complete reference system). Shall be a
     * UOM identified in the Process description for this input or output.
     */
    protected URI uom;
    
    /**
     * initializes a <code>TypedLiteral</code> with <code>this.uom = null;</code>
     * @param value
     * @param type
     */
    public TypedLiteral(String value, URI type) {
        this.value = value;
        this.type = type;
    }
    
    /**
     * @param value
     * @param type
     * @param uom units of measure
     */
    public TypedLiteral(String value, URI type, URI uom) {
        this.value = value;
        this.type = type;
        this.uom = uom;
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
     * @return Returns the value.
     * 
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     * 
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * returns the units a <code>TypedLiteral</code> is measured; maybe <code>null</code>  
     * @return
     */
    public URI getUom() {
        return uom;
    }

    public void setUom( URI value ) {
        this.uom = value;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new TypedLiteral( value, type, uom );
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: TypedLiteral.java,v $
   Revision 1.6  2006/05/25 14:46:49  poth
   support for uints of measured added

   Revision 1.5  2006/03/22 13:22:14  poth
   *** empty log message ***

   Revision 1.4  2005/11/16 13:45:00  mschneider
   Merge of wfs development branch.

   Revision 1.3.2.1  2005/11/14 11:34:50  deshmukh
   inserted: serialVersionID

   Revision 1.3  2005/02/25 11:19:16  poth
   no message

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/08/16 06:23:33  ap
   no message

   Revision 1.2  2004/07/09 07:01:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:02  ap
   no message


********************************************************************** */
