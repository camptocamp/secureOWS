package org.deegree.ogcwebservices.wcs;

import java.net.URL;

import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.ogcbase.Description;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.MetadataLink;

/**
 * @version $Revision: 1.4 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.4 $, $Date: 2006/03/03 13:37:42 $ *  * @since 2.0
 */

public class CoverageOfferingBrief extends Description {

    private static final long serialVersionUID = 7109863070752388720L;
    
    private LonLatEnvelope lonLatEnvelope = null;
    private Keywords[] keywords = null;
    private URL configuration = null;
        
  
   
    /**
     * @param name
     * @param label
     * @param description
     * @param metadataLink
     * @param lonLatEnvelope
     * @param keywords
     */
    public CoverageOfferingBrief(String name, String label, String description, 
                                 MetadataLink metadataLink, LonLatEnvelope lonLatEnvelope, 
                                 Keywords[] keywords) throws OGCException, WCSException {
        super(name, label, description, metadataLink);
        setLonLatEnvelope(lonLatEnvelope);
        this.keywords = keywords;
    }
    
    /**
     * @param name
     * @param label
     * @param description
     * @param metadataLink
     * @param lonLatEnvelope
     * @param keywords
     */
    public CoverageOfferingBrief(String name, String label, String description, 
                                 MetadataLink metadataLink, LonLatEnvelope lonLatEnvelope, 
                                 Keywords[] keywords, URL configuration) 
                                            throws OGCException, WCSException {
        super(name, label, description, metadataLink);
        setLonLatEnvelope(lonLatEnvelope);
        this.keywords = keywords;
        this.configuration = configuration;
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
        this.keywords = keywords;
    }

    /**
     * @return Returns the lonLatEnvelope.
     * 
     */
    public LonLatEnvelope getLonLatEnvelope() {        
        return lonLatEnvelope;
    }

    /**
     * @param lonLatEnvelope The lonLatEnvelope to set.
     * 
     */
    public void setLonLatEnvelope(LonLatEnvelope lonLatEnvelope) {
        this.lonLatEnvelope = lonLatEnvelope;
    }

    /**
     * @return Returns the configuration.
     */
    public URL getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration The configuration to set.
     * 
     */
    public void setConfiguration(URL configuration) {
        this.configuration = configuration;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: CoverageOfferingBrief.java,v $
   Revision 1.4  2006/03/03 13:37:42  poth
   *** empty log message ***

   Revision 1.3  2005/03/09 08:44:31  poth
   no message

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.6  2004/08/24 07:31:33  ap
   no message

   Revision 1.5  2004/06/28 06:26:52  ap
   no message

   Revision 1.4  2004/06/21 08:05:49  ap
   no message

   Revision 1.3  2004/06/02 14:09:02  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
