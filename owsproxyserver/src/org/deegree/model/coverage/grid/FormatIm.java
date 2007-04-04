package org.deegree.model.coverage.grid;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.deegree.datatypes.Code;
import org.opengis.coverage.grid.Format;
import org.opengis.parameter.GeneralOperationParameter;



/**
 *  This interface is a discovery mechanism to determine the formats supported by a
 * {@link org.opengis.coverage.grid.GridCoverageExchange} implementation.
 * A <code>GC_GridCoverageExchange</code> implementation can support a number of
 * file format or resources.
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/03/15 22:20:09 $
 *
 * @since 2.0
 */
public class FormatIm implements Format, Serializable {
    
    private static final long serialVersionUID = 3847909077719638612L;
    
    private String name = null;
    private String description = null;
    private String docURL = null;    
    private String vendor = null;
    private String version = null;
    private List readParameters = null;
    private List writeParameters = null;
    
    /**
     * Initializes a format with a Code containing a code that will be
     * used as format name and a code space (optional) that will be 
     * interpreted as format vendor. 
     * @param code
     */
    public FormatIm(Code code) {
        this.name = code.getCode();
        if ( code.getCodeSpace() != null ) {
            vendor = code.getCodeSpace().toString(); 
        }
    }
    
    /**
     * @param description
     * @param docURL
     * @param name
     * @param vendor
     * @param version
     */
    public FormatIm(String name, String description, String docURL, 
                    String vendor, String version) {
        this.description = description;
        this.docURL = docURL;
        this.name = name;
        this.vendor = vendor;
        this.version = version;
    }
    
    /**
     * @param description
     * @param docURL
     * @param name
     * @param vendor
     * @param version
     * @param readParameters
     * @param writeParameters
     */
    public FormatIm(String name, String description, String docURL, String vendor, 
                    String version, GeneralOperationParameter[] readParameters, 
                    GeneralOperationParameter[] writeParameters) {
        this.description = description;
        this.docURL = docURL;
        this.name = name;
        this.vendor = vendor;
        this.version = version;
        setReadParameters(readParameters);
        setWriteParameters(writeParameters);
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
     * @param docURL The docURL to set.
     * 
     * @uml.property name="docURL"
     */
    public void setDocURL(String docURL) {
        this.docURL = docURL;
    }

    /**
     * @param name The name to set.
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @param readParameters The readParameters to set.
     */
    public void setReadParameters(GeneralOperationParameter[] readParameters) {
        if ( readParameters == null ) readParameters = new GeneralOperationParameter[0];
        this.readParameters = Arrays.asList( readParameters );        
    }
    
    /**
     * @param readParameter
     */
    public void addReadParameter(GeneralOperationParameter readParameter) {        
        this.readParameters.add(readParameter);
    }

    /**
     * @param vendor The vendor to set.
     * 
     * @uml.property name="vendor"
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * @param version The version to set.
     * 
     * @uml.property name="version"
     */
    public void setVersion(String version) {
        this.version = version;
    }


    /**
     * @param writeParameters The writeParameters to set.
     */
    public void setWriteParameters(GeneralOperationParameter[] writeParameters) {
        if ( writeParameters == null ) writeParameters = new GeneralOperationParameter[0];
        this.writeParameters = Arrays.asList( writeParameters );        
    }
    
    /**
     * @param writeParameter
     */
    public void addWriteParameter(GeneralOperationParameter writeParameter) {
        this.readParameters.add(writeParameter);
    }

    /**
     * Name of the file format.
     * 
     * @return the name of the file format.
     * @UML mandatory name
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * Description of the file format.
     * If no description, the value will be <code>null</code>.
     * 
     * @return the description of the file format.
     * @UML optional description
     * 
     * @uml.property name="description"
     */
    public String getDescription() {
        return description;
    }

    /**
     * Vendor or agency for the format.
     * 
     * @return the vendor or agency for the format.
     * @UML optional vendor
     * 
     * @uml.property name="vendor"
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Documentation URL for the format.
     * 
     * @return the documentation URL for the format.
     * @UML optional docURL
     * 
     * @uml.property name="docURL"
     */
    public String getDocURL() {
        return docURL;
    }

    /**
     * Version number of the format.
     * 
     * @return the version number of the format.
     * @UML optional version
     * 
     * @uml.property name="version"
     */
    public String getVersion() {
        return version;
    }

    /**
     * Retrieve the parameter information for a 
     * {@link org.opengis.coverage.grid.GridCoverageReader#read read} operation.
     * 
     * @UML operation getParameterInfo
     * @UML mandatory numParameters
     * 
     * @uml.property name="readParameters"
     */
    public GeneralOperationParameter[] getReadParameters() {
        GeneralOperationParameter[] rp = new GeneralOperationParameter[readParameters
            .size()];
        return (GeneralOperationParameter[]) readParameters.toArray(rp);
    }

    /**
     * Retrieve the parameter information for a 
     * org.opengis.coverage.grid.GridCoverageWriter#write operation.
     * 
     * @uml.property name="writeParameters"
     */
    public GeneralOperationParameter[] getWriteParameters() {
        GeneralOperationParameter[] rp = new GeneralOperationParameter[writeParameters
            .size()];
        return (GeneralOperationParameter[]) writeParameters.toArray(rp);
    }

    
    /**
     * performs a test if the passed Object is equal to this Format. Two
     * Formats are equal if their names ar equal and (if not null) their
     * vendors and versions are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj object to compare
     */
    public boolean equals(Object obj) {
        if ( obj == null || !(obj instanceof Format) ) {
            return false;
        }
        Format other = (Format)obj;
        boolean eq = this.getName().equals( other.getName() );
        if ( getVendor() != null && other.getVendor() != null ) {
            eq = eq && getVendor().equals( other.getVendor() ); 
        } else if ( getVendor() == null && other.getVendor() != null ) {
            return false;
        } else if ( getVendor() != null && other.getVendor() == null ) {
            return false;
        }
        if ( getVersion() != null && other.getVersion() != null ) {
            eq = eq && getVersion().equals( other.getVersion() ); 
        } else if ( getVersion() == null && other.getVersion() != null ) {
            return false;
        } else if ( getVersion() != null && other.getVersion() == null ) {
            return false;
        }
        return eq;
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: FormatIm.java,v $
   Revision 1.3  2006/03/15 22:20:09  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.4  2004/07/22 15:20:41  ap
   no message

   Revision 1.3  2004/07/15 08:18:08  ap
   no message

   Revision 1.2  2004/07/12 06:12:11  ap
   no message

   Revision 1.1  2004/05/25 07:14:01  ap
   no message

   Revision 1.1  2004/05/24 06:51:31  ap
   no message


********************************************************************** */
