package org.deegree.ogcwebservices.wcs.getcapabilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2006/08/07 09:45:46 $ *  * @since 2.0
 */

public class ContentMetadata implements Serializable {
    
    private String version = null;
    private String updateSequence = null;

    /**
     * 
     */
    private CoverageOfferingBrief[] coverageOfferingBrief;

    private Map map = new HashMap(100);
    
     /**
     * @param version
     * @param updateSequence
     * @param coverageOfferingBrief
     * @throws OGCException
     */
    public ContentMetadata(String version, String updateSequence, 
                           CoverageOfferingBrief[] coverageOfferingBrief) {

        
        this.version = version;
        this.updateSequence = updateSequence;
        setCoverageOfferingBrief(coverageOfferingBrief);
    }

    /**
     * @return Returns the coverageOfferingBrief.
     * 
     * @uml.property name="coverageOfferingBrief"
     */
    public CoverageOfferingBrief[] getCoverageOfferingBrief() {
        return coverageOfferingBrief;
    }

    
    /**
     * returns the <tt>CoverageOfferingBrief<tt> for the coverage matching
     * the passed name. if no coverage with this name is available <tt>null</tt>
     * will be returned.
     * 
     * @param coverageName
     * @return
     */
    public CoverageOfferingBrief getCoverageOfferingBrief(String coverageName) {        
        return (CoverageOfferingBrief)map.get(coverageName);
    }

    /**
     * @param coverageOfferingBrief The coverageOfferingBrief to set.
     * 
     * @uml.property name="coverageOfferingBrief"
     */
    public void setCoverageOfferingBrief(
        CoverageOfferingBrief[] coverageOfferingBrief) {
        map.clear();
        this.coverageOfferingBrief = new CoverageOfferingBrief[coverageOfferingBrief.length];

        for (int i = 0; i < coverageOfferingBrief.length; i++) {
            this.coverageOfferingBrief[i] = coverageOfferingBrief[i];
            map.put(
                coverageOfferingBrief[i].getName(),
                coverageOfferingBrief[i]);
        }
    }

    /**
     * @return Returns the updateSequence.
     * 
     * @uml.property name="updateSequence"
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence The updateSequence to set.
     * 
     * @uml.property name="updateSequence"
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @return Returns the version.
     * 
     * @uml.property name="version"
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     * 
     * @uml.property name="version"
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ContentMetadata.java,v $
   Revision 1.3  2006/08/07 09:45:46  poth
   never thrown exception removed

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.6  2004/08/16 06:23:33  ap
   no message

   Revision 1.5  2004/06/23 15:32:37  ap
   no message

   Revision 1.4  2004/06/03 11:16:35  ap
   no message

   Revision 1.3  2004/06/02 14:09:02  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
