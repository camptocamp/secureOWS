package org.deegree.ogcwebservices.wcs.describecoverage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.util.NetWorker;
import org.deegree.model.crs.UnknownCRSException;
import org.xml.sax.SAXException;

/**
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */

public class CoverageDescription {

    private CoverageOffering[] coverageOffering = new CoverageOffering[0];  
 
    private Map map = new HashMap(100);

    private String version = "1.0.0";

    /**
     * creates a <tt>CoverageDescription</tt> from a DOM document assigen by
     * the passed URL
     * 
     * @return created <tt>CoverageDescription</tt>
     * @exception IOException
     * @exception SAXException
     * @exception InvalidCoverageDescriptionExcpetion
     */
    public static CoverageDescription createCoverageDescription(URL url)
            throws IOException, SAXException,
            InvalidCoverageDescriptionExcpetion {
        CoverageDescriptionDocument covDescDoc = new CoverageDescriptionDocument();
        if (url == null) {
            throw new InvalidCoverageDescriptionExcpetion("location URL for "
                    + "a coverage description document is null");
        }
        if (!NetWorker.existsURL(url)) {
            throw new InvalidCoverageDescriptionExcpetion("location URL: "
                    + url + "for a coverage description document doesn't exist");
        }
        covDescDoc.load(url);
        return new CoverageDescription( covDescDoc );
    }

    /**
     * @param covDescDoc
     * @exception InvalidCoverageDescriptionExcpetion
     */
    public CoverageDescription(CoverageDescriptionDocument covDescDoc)
            throws InvalidCoverageDescriptionExcpetion {
        setVersion(covDescDoc.getVersion());
        try {
            setCoverageOfferings(covDescDoc.getCoverageOfferings());
        } catch ( UnknownCRSException e ) {
            throw new InvalidCoverageDescriptionExcpetion( e.getMessage() );
        }
    }

    /**
     * @param coverageOffering
     */
    public CoverageDescription(CoverageOffering[] coverageOffering,
            String version) {
        setVersion(version);
        setCoverageOfferings(coverageOffering);
    }

    /**
     * @return Returns the coverageOffering.
     */
    public CoverageOffering[] getCoverageOfferings() {
        return coverageOffering;
    }

    /**
     * returns a <tt>CoverageOffering</tt> identified by its name. if no
     * <tt>CoverageOffering</tt> is known by a <tt>CoverageDescription</tt>
     * with the passed name, <tt>null</tt> will be returned.
     * 
     * @param name
     * @return
     */
    public CoverageOffering getCoverageOffering(String name) {
        return (CoverageOffering) map.get(name);
    }

    /**
     * @param coverageOffering
     *            The coverageOffering to set.
     */
    public void setCoverageOfferings(CoverageOffering[] coverageOffering) {
        if (coverageOffering == null) {
            coverageOffering = new CoverageOffering[0];
        }
        map.clear();
        for (int i = 0; i < coverageOffering.length; i++) {
            map.put(coverageOffering[i].getName(), coverageOffering[i]);
        }
        this.coverageOffering = coverageOffering;
    }

    /**
     * 
     * @uml.property name="version"
     */
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @uml.property name="version"
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * CoverageDescription.java,v $ Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.11 2004/07/12 11:14:19 ap no message
 * 
 * Revision 1.10 2004/07/12 06:12:11 ap no message
 * 
 * Revision 1.9 2004/07/05 06:15:00 ap no message
 * 
 * Revision 1.8 2004/06/28 06:26:52 ap no message
 * 
 * Revision 1.7 2004/06/25 15:34:52 ap no message
 * 
 * Revision 1.6 2004/05/31 07:37:45 ap no message
 * 
 * Revision 1.5 2004/05/26 15:31:36 ap no message
 * 
 * Revision 1.4 2004/05/26 10:47:46 ap no message
 * 
 * Revision 1.3 2004/05/25 15:13:23 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:39 ap no message
 * 
 *  
 ******************************************************************************/
