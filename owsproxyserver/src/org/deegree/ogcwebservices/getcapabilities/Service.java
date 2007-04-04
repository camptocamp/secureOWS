package org.deegree.ogcwebservices.getcapabilities;

import org.deegree.datatypes.CodeList;
import org.deegree.model.metadata.iso19115.CitedResponsibleParty;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.ogcbase.Description;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2005/04/20 20:36:09 $ *  * @since 2.0
 */

public class Service extends Description {

    private Keywords[] keywords = new Keywords[0];
    private CitedResponsibleParty citedResponsibleParty = null;
    private CodeList fees = null;
    private CodeList[] accessConstraints = new CodeList[0];
    private String version = null;
    private String updateSequence = null;
    
    /**
     * @param name
     * @param label
     * @param fees
     * @param accessConstraints
     */
    public Service(String name, String label, CodeList fees, CodeList[] accessConstraints) 
                   throws OGCException, OGCWebServiceException {
        super(name, label);
        setFees(fees);
        this.accessConstraints = accessConstraints;
    }
    
    /**
     * @param name
     * @param label
     * @param description
     * @param citedResponsibleParty
     * @param fees
     * @param accessConstraints
     * @throws OGCException
     * @throws OGCWebServiceException
     */
    public Service(
        String name,
        String label,
        String description,        
        CitedResponsibleParty citedResponsibleParty,
        CodeList fees,
        CodeList[] accessConstraints) throws OGCException, OGCWebServiceException {
        super( name, label, description, null);
        this.citedResponsibleParty = citedResponsibleParty;
        setFees(fees);
        setAccessConstraints(accessConstraints);
    }

    /**
     * @param description
     * @param name
     * @param metadataLink
     * @param label
     * @param keywords
     * @param citedResponsibleParty
     * @param fees
     * @param accessConstraints
     * @param version
     * @param updateSequence
     */
    public Service(
        String description,
        String name,
        MetadataLink metadataLink,
        String label,
        Keywords[] keywords,
        CitedResponsibleParty citedResponsibleParty,
        CodeList fees,
        CodeList[] accessConstraints,
        String version,
        String updateSequence) throws OGCException, OGCWebServiceException  {
        super(name, label, description, metadataLink);
        setKeywords(keywords);
        this.citedResponsibleParty = citedResponsibleParty;
        setFees(fees);
        setAccessConstraints(accessConstraints);
        this.version = version;
        this.updateSequence = updateSequence;
    }

    /**
     * @return Returns the accessConstraints.
     * 
     */
    public CodeList[] getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * @param accessConstraints The accessConstraints to set.
     * 
     */
    public void setAccessConstraints(CodeList[] accessConstraints) {
        if (accessConstraints == null) {
            accessConstraints = new CodeList[0];
        }
        this.accessConstraints = accessConstraints;
    }

    /**
     * @return Returns the citedResponsibleParty.
     * 
     */
    public CitedResponsibleParty getCitedResponsibleParty() {
        return citedResponsibleParty;
    }

    /**
     * @param citedResponsibleParty The citedResponsibleParty to set.
     * 
     */
    public void setCitedResponsibleParty(
        CitedResponsibleParty citedResponsibleParty) {
        this.citedResponsibleParty = citedResponsibleParty;
    }

    /**
     * @return Returns the fees.
     * 
     */
    public CodeList getFees() {
        return fees;
    }

    /**
     * @param fees The fees to set.
     * 
     */
    public void setFees(CodeList fees) throws OGCWebServiceException {
        if (fees == null) {
            throw new OGCWebServiceException("fees must be <> null for Service");
        }
        this.fees = fees;
    }

    /**
     * @return Returns the keywords.
     * 
     */
    public Keywords[] getKeywords() {
        return keywords;
    }

    /**
     * @param keywords The keywords to set.
     * 
     */
    public void setKeywords(Keywords[] keywords) {
        if (keywords == null) {
            keywords = new Keywords[0];
        }
        this.keywords = keywords;
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
   $Log: Service.java,v $
   Revision 1.3  2005/04/20 20:36:09  poth
   no message

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.5  2004/07/12 06:12:11  ap
   no message

   Revision 1.4  2004/06/28 06:27:05  ap
   no message

   Revision 1.3  2004/06/21 06:44:57  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
