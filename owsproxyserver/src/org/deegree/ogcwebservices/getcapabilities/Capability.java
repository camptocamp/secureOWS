package org.deegree.ogcwebservices.getcapabilities;

import java.io.Serializable;

import org.deegree.ogcwebservices.ExceptionFormat;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2005/06/14 13:41:15 $ *  * @since 2.0
 */

public class Capability implements Serializable {
    
    private String version = null;
    private String updateSequence = null;
    private OperationsMetadata operations = null;
    private ExceptionFormat exception = null;

    private Object vendorSpecificCapabilities = null;
    
    /**
     * @param exception
     * @param vendorSpecificCapabilities
     */
    public Capability(OperationsMetadata operations, ExceptionFormat exception, 
                      Object vendorSpecificCapabilities) {
        this.operations = operations;
        this.exception = exception;
        this.vendorSpecificCapabilities = vendorSpecificCapabilities;
    }
    /**
     * @param version
     * @param updateSequence
     * @param exception
     * @param vendorSpecificCapabilities
     */
    public Capability(String version, String updateSequence, OperationsMetadata operations, 
                      ExceptionFormat exception, Object vendorSpecificCapabilities) {
        this.version = version;
        this.updateSequence = updateSequence;
        this.operations = operations;
        this.exception = exception;
        this.vendorSpecificCapabilities = vendorSpecificCapabilities;
    }

    /**
     * @return Returns the exception.
     * 
     */
    public ExceptionFormat getException() {
        return exception;
    }

    /**
     * @param exception The exception to set.
     * 
     */
    public void setException(ExceptionFormat exception) {
        this.exception = exception;
    }

    /**
     * @return Returns the request.
     * 
     */
    public OperationsMetadata getOperations() {
        return operations;
    }

    /**
     * @param operations operations supported by a service
     * 
     */
    public void setOperations(OperationsMetadata operations) {
        this.operations = operations;
    }

    /**
     * @return Returns the updateSequence.
     * 
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence The updateSequence to set.
     * 
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @return Returns the vendorSpecificCapabilities.
     * 
     */
    public Object getVendorSpecificCapabilities() {
        return vendorSpecificCapabilities;
    }

    /**
     * @param vendorSpecificCapabilities The vendorSpecificCapabilities to set.
     * 
     */
    public void setVendorSpecificCapabilities(Object vendorSpecificCapabilities) {
        this.vendorSpecificCapabilities = vendorSpecificCapabilities;
    }

    /**
     * @return Returns the version.
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     * 
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: Capability.java,v $
   Revision 1.3  2005/06/14 13:41:15  poth
   no message

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.8  2004/08/16 06:23:33  ap
   no message

   Revision 1.7  2004/07/12 06:12:11  ap
   no message

   Revision 1.6  2004/06/22 13:25:14  ap
   no message

   Revision 1.5  2004/06/14 08:05:58  ap
   no message

   Revision 1.4  2004/06/09 15:30:37  ap
   no message

   Revision 1.3  2004/06/02 14:10:44  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message
********************************************************************** */
