package org.deegree.ogcwebservices.wcs.describecoverage;

import java.io.Serializable;

import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.SupportedFormats;
import org.deegree.ogcwebservices.SupportedSRSs;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.SupportedInterpolations;
import org.deegree.ogcwebservices.wcs.WCSException;
import org.deegree.ogcwebservices.wcs.configuration.Extension;

/**
 * @version $Revision: 1.6 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.6 $, $Date: 2006/03/03 13:37:42 $ *  * @since 2.0
 */

public class CoverageOffering extends CoverageOfferingBrief 
							  implements Cloneable,Serializable {

    private static final long serialVersionUID = -2280508956895529051L;
    
    private DomainSet domainSet = null;
    private RangeSet rangeSet = null;
    private SupportedSRSs supportedCRSs = null;
    private SupportedFormats supportedFormats = null;
    private SupportedInterpolations supportedInterpolations = new SupportedInterpolations();
    private Extension extension = null;

        
    /**
     * @param name
     * @param label
     * @param description
     * @param metadataLink
     * @param lonLatEnvelope
     * @param keywords
     * @param domainSet
     * @param rangeSet
     * @param supportedCRSs
     * @param supportedFormats
     * @param supportedInterpolations
     * @throws OGCException
     * @throws WCSException
     */
    public CoverageOffering( String name, String label, String description, 
                             MetadataLink metadataLink, LonLatEnvelope lonLatEnvelope,
                             Keywords[] keywords, DomainSet domainSet, RangeSet rangeSet,
                             SupportedSRSs supportedCRSs, SupportedFormats supportedFormats,
                             SupportedInterpolations supportedInterpolations, Extension extension)
                               throws OGCException, WCSException {
        super(name, label, description, metadataLink, lonLatEnvelope, keywords);
        setDomainSet(domainSet);
        setRangeSet(rangeSet);
        setSupportedCRSs(supportedCRSs);
        setSupportedFormats(supportedFormats);
        setSupportedInterpolations(supportedInterpolations);
        setExtension(extension);
    }

    /**
     * @return Returns the domainSet.
     * 
     */
    public DomainSet getDomainSet() {
        return domainSet;
    }

    /**
     * @param domainSet The domainSet to set.
     */
    public void setDomainSet(DomainSet domainSet) throws WCSException {
        if (domainSet == null) {
            throw new WCSException("domainSet must be <> null for CoverageOffering");
        }
        this.domainSet = domainSet;
    }

    /**
     * @return Returns the rangeSet.
     */
    public RangeSet getRangeSet() {
        return rangeSet;
    }

    /**
     * @param rangeSet The rangeSet to set.
     */
    public void setRangeSet(RangeSet rangeSet) throws WCSException {
        if (rangeSet == null) {
            throw new WCSException( "rangeSet must be <> null for CoverageOffering");
        }
        this.rangeSet = rangeSet;
    }

    /**
     * @return Returns the supportedCRSs.
     */
    public SupportedSRSs getSupportedCRSs() {
        return supportedCRSs;
    }

    /**
     * @param supportedCRSs The supportedCRSs to set.
     */
    public void setSupportedCRSs(SupportedSRSs supportedCRSs)
        throws WCSException {
        if (supportedCRSs == null) {
            throw new WCSException("supportedCRSs must be <> null for CoverageOffering");
        }
        this.supportedCRSs = supportedCRSs;
    }

    /**
     * @return Returns the supportedFormats.
     */
    public SupportedFormats getSupportedFormats() {
        return supportedFormats;
    }

    /**
     * @param supportedFormats The supportedFormats to set.
     */
    public void setSupportedFormats(SupportedFormats supportedFormats)
        throws WCSException {
        if (supportedFormats == null) {
            throw new WCSException( "supportedFormatss must be <> null for CoverageOffering");
        }
        this.supportedFormats = supportedFormats;
    }

    /**
     * @return Returns the supportedInterpolations.
     */
    public SupportedInterpolations getSupportedInterpolations() {
        return supportedInterpolations;
    }

    /**
     * If <tt>null</tt> will be passed supportedInterpolations will be
     * set to its default.
     * 
     * @param supportedInterpolations The supportedInterpolations to set.
     */
    public void setSupportedInterpolations(
        SupportedInterpolations supportedInterpolations) {
        if (supportedCRSs != null) {
            this.supportedInterpolations = supportedInterpolations;
        }
    }

    /**
     * @return Returns the extension.
     */
    public Extension getExtension() {
        return extension;
    }

    /**
     * @param extension The extension to set.
     */
    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
       try {
           DomainSet domainSet_ = (DomainSet)domainSet.clone();
           RangeSet rangeSet_ = null;    
           if ( rangeSet != null ) {
               rangeSet_ = (RangeSet)rangeSet.clone();
           }
           
           LonLatEnvelope llenv = (LonLatEnvelope)getLonLatEnvelope().clone();
           
            return new CoverageOffering( getName(), getLabel(), getDescription(),
                                         getMetadataLink(),llenv, getKeywords(),
                                         domainSet_, rangeSet_, supportedCRSs,
                                         supportedFormats, supportedInterpolations,
                                         extension);
       } catch(Exception e) {
           e.printStackTrace();
       }
       return null;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: CoverageOffering.java,v $
   Revision 1.6  2006/03/03 13:37:42  poth
   *** empty log message ***

   Revision 1.5  2006/02/28 17:53:31  poth
   *** empty log message ***

   Revision 1.4  2006/01/16 20:36:39  poth
   *** empty log message ***

   Revision 1.3  2005/11/21 15:04:19  deshmukh
   CRS to SRS

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.8  2004/07/30 06:29:29  ap
   code optimization

   Revision 1.7  2004/07/14 06:52:48  ap
   no message

   Revision 1.6  2004/07/12 06:12:11  ap
   no message

   Revision 1.5  2004/06/28 06:26:52  ap
   no message

   Revision 1.4  2004/06/21 08:05:49  ap
   no message

   Revision 1.3  2004/05/27 12:55:21  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
