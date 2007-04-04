package org.deegree.ogcwebservices.wcs.describecoverage;

import java.util.Map;

import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.MissingParameterValueException;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcs.WCSException;
import org.deegree.ogcwebservices.wcs.WCSExceptionCode;
import org.deegree.ogcwebservices.wcs.WCSRequestBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A DescribeCoverage request lists the coverages to be described, 
 * identified by the Coverage parameter. A request that lists no 
 * coverages shall be interpreted as requesting descriptions of all 
 * coverages that a WCS can serve.
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/08/07 10:04:36 $
 *
 * @since 2.0
 */
public class DescribeCoverage extends WCSRequestBase {
    
    private String[] coverages = null;
    
    /**
     * creates a DescribeCoverage request from its KVP representation
     * @param map request
     * @return created <tt>DescribeCoverage</tt> 
     * @throws OGCWebServiceException will be thrown if something general is wrong
     * @throws WCSException will be thrown if a WCS/DescribeCoverage specific part
     *                      of the request is erroreous  
     */
    public static DescribeCoverage create(Map map) throws OGCWebServiceException, 
                            MissingParameterValueException, InvalidParameterValueException {
        
        String version = (String)map.get( "VERSION");
        if ( version == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new MissingParameterValueException( "DescribeCoverage", 
                                            "'version' is missing", code );
        }
        if ( !version.equals("1.0.0") ) {
            ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
            throw new InvalidParameterValueException( "DescribeCoverage", 
                                                      "'version' <> 1.0.0", code );
        }
        String service = (String)map.get( "SERVICE");
        if ( service == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new MissingParameterValueException( "DescribeCoverage", 
                                            "'service' is missing", code );
        }
        if ( !"WCS".equalsIgnoreCase(service) ) {
            ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
            throw new InvalidParameterValueException( "DescribeCoverage", 
                                                "'service' <> WCS", code );
        }
            
        String[] coverages = new String[0];
        if ( map.get( "COVERAGE" ) != null ) {
            String s = (String)map.get( "COVERAGE" );
            coverages = StringTools.toArray(s, ",", true);
        }
        
        String id = (String)map.get("ID");
        
        return new DescribeCoverage( id, version, coverages );
    }
    
     /**
     * creates a DescribeCoverage request from its KVP representation
     * @param id unique ID of the request
     * @param kvp request
     * @return created <tt>DescribeCoverage</tt> 
     * @throws OGCWebServiceException will be thrown if something general is wrong
     * @throws WCSException will be thrown if a WCS/DescribeCoverage specific part
     *                      of the request is erroreous  
     */
    public static DescribeCoverage createDescribeCoverage(String id, String kvp) 
                            throws OGCWebServiceException, MissingParameterValueException,
                                    InvalidParameterValueException {
        Map map = KVP2Map.toMap( kvp );
        map.put("ID", id);
        return create( map );
    }
    
    /**
     * creates a DescribeCoverage request from its XML representation
     * @param id unique ID of the request
     * @param doc XML representation of the request
     * @return created <tt>DescribeCoverage</tt> 
     * @throws OGCWebServiceException will be thrown if something general is wrong
     * @throws WCSException will be thrown if a WCS/DescribeCoverage specific part
     *                      of the request is erroreous  
     */
    public static DescribeCoverage create(String id, Document doc) 
                            throws OGCWebServiceException, MissingParameterValueException,
                                   InvalidParameterValueException {
                
        String[] coverages = null;
        String version = null;
        try {
            Element root =  XMLTools.getRequiredChildElement( "DescribeCoverage", 
                                                     CommonNamespaces.WCSNS, doc);
         
            version = XMLTools.getAttrValue( root, "version" );
            if ( version == null ) {
                ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
                throw new MissingParameterValueException( "DescribeCoverage", 
                                            "'version' is missing", code );
            }
            if ( !version.equals("1.0.0") ) {
                ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
                throw new InvalidParameterValueException( "DescribeCoverage", 
                                                          "'version' <> 1.0.0", code );
            }
            
            String service = XMLTools.getAttrValue( root, "service" );
            if ( service == null ) {
                ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
                throw new MissingParameterValueException( "DescribeCoverage", 
                                            "'service' is missing", code );
            }
            if ( !"WCS".equalsIgnoreCase(service) ) {
                ExceptionCode code = ExceptionCode.INVALIDPARAMETERVALUE;
                throw new InvalidParameterValueException( "DescribeCoverage", 
                                                        "'service' <> WCS", code );
            }
            
            ElementList el = 
                XMLTools.getChildElements( "Coverage", CommonNamespaces.WCSNS, root );
            coverages = new String[el.getLength()];
            for (int i = 0; i < coverages.length; i++ ) {
                coverages[i] = XMLTools.getStringValue( el.item(i) );
            }
        } catch(XMLParsingException e) {
            ExceptionCode code = WCSExceptionCode.INVALID_FORMAT;
            throw new WCSException( "DescribeCoverage", e.toString(), code );
        }
        
        return new DescribeCoverage( id, version, coverages );
    }
    
    /**
     * @param id unique ID of the request
     * @param version Request protocol version
     * @param coverages list of coverages to describe (identified by 
     *                  their name values in the Capabilities response).
     *                  If <tt>null</tt> or length == 0 all coverages
     *                  of the service instances will be described
     */
    public DescribeCoverage(String id, String version, String[] coverages)  {
        super(id, version);
        this.coverages = coverages;
    }

    /**
     * @return Returns the coverages.
     * 
     */
    public String[] getCoverages() {
        return coverages;
    }

    

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: DescribeCoverage.java,v $
   Revision 1.6  2006/08/07 10:04:36  poth
   never thrown exception removed

   Revision 1.5  2005/09/27 19:53:19  poth
   no message

   Revision 1.4  2005/03/09 11:55:47  mschneider
   *** empty log message ***

   Revision 1.3  2005/02/21 11:24:33  poth
   no message

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.10  2004/07/12 14:13:01  mschneider
   More work on the CatalogConfiguration and capabilities framework.

   Revision 1.9  2004/07/12 11:14:19  ap
   no message

   Revision 1.8  2004/07/12 06:12:11  ap
   no message

   Revision 1.7  2004/06/30 15:16:05  mschneider
   Refactoring of XMLTools.

   Revision 1.6  2004/06/28 15:40:13  mschneider
   Finished the generation of the ServiceIdentification part of the
   Capabilities from DOM, added functionality to the XMLTools helper
   class.

   Revision 1.5  2004/06/28 06:26:52  ap
   no message

   Revision 1.4  2004/06/18 06:18:45  ap
   no message

   Revision 1.3  2004/06/16 11:48:17  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
