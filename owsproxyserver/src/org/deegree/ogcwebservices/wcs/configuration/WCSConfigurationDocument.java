// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/WCSConfigurationDocument.java,v 1.22 2006/11/30 11:26:06 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.wcs.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.metadata.iso19115.Keywords;
import org.deegree.model.metadata.iso19115.OnlineResource;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.OGCException;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.MetadataLink;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilitiesDocument;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @version $Revision: 1.22 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.22 $, $Date: 2006/11/30 11:26:06 $
 */
public class WCSConfigurationDocument extends WCSCapabilitiesDocument {
    
    private static final long serialVersionUID = -5962651588545478145L;

    protected static final ILogger LOG = LoggerFactory.getLogger( WCSConfigurationDocument.class );
    
    private static final String PRE_DEEGREE = CommonNamespaces.DEEGREEWCS_PREFIX+":"; 
    private static final String PRE_WCS = CommonNamespaces.WCS_PREFIX+":"; 

	/**
	 * 
	 */
	public static final String XML_TEMPLATE = "WCSConfigurationTemplate.xml";


	@Override
    public void createEmptyDocument() throws IOException, SAXException {
        URL url = WCSConfigurationDocument.class.getResource(XML_TEMPLATE);
        if (url == null) {
            throw new IOException( "The resource '" + XML_TEMPLATE
                    + " could not be found.");
        }
        load(url);
	}

	/**
	 * creates the deegreeParams section of the WCS capabilities/configuration.
	 * If a 'common' WCS capabilities is encapsulated by the
	 * <tt>WCSCapabilitieDocument</tt> this method returns <tt>null</tt>
	 * 
	 * @return the data 
     * @throws InvalidConfigurationException
	 */
	public WCSDeegreeParams getDeegreeParamsSection()
			throws InvalidConfigurationException {
		try {                             
			Element element = (Element)XMLTools.getRequiredNode( getRootElement(), PRE_DEEGREE+"deegreeParam", nsContext );       
//			if (element == null) {
//				throw new InvalidConfigurationException( "DeegreeParams section is missing" );
//            }
			//OnlineResource defOLR = parseOnLineResource(XMLTools.getRequiredChildElement( "DefaultOnlineResource", DGRNS, element ));
            OnlineResource defOLR = parseOnLineResource((Element)XMLTools.getRequiredNode( element, PRE_DEEGREE + "DefaultOnlineResource", nsContext) );
            //String tmp = XMLTools.getStringValue( "CacheSize", DGRNS, element, "100" );
            int cache = XMLTools.getNodeAsInt( element, PRE_DEEGREE+"CacheSize", nsContext, 100 );
//			int cache = Integer.parseInt(tmp);
//			tmp = XMLTools.getStringValue( "RequestTimeLimit", DGRNS, element, "5" );
//			int timeLimit = Integer.parseInt(tmp);
            int timeLimit = XMLTools.getNodeAsInt( element, PRE_DEEGREE+"RequestTimeLimit", nsContext, 5 );
            Element dataDirs = (Element)XMLTools.getRequiredNode( element, PRE_DEEGREE+"DataDirectoryList", nsContext );
            String[] dirList =  XMLTools.getNodesAsStrings(dataDirs, PRE_DEEGREE+"DataDirectory", nsContext);
            //                                    
            if ( dirList.length > 0 ) {
                StringBuffer sb = new StringBuffer(400);
                sb.append("<DataDirectoryList> evaluation is not suporrted yet, skipping following dirs:\n" );
                for( int i = 0; i < dirList.length; ++i  )
                    sb.append( '\t' + dirList[i] + ((i+1<dirList.length)?'\n':' '));
                LOG.logInfo( sb.toString() );
            }
            WCSDeegreeParams wcsDP = new WCSDeegreeParams(defOLR, cache, timeLimit, dirList );
            LOG.logDebug( "(WCSConfig.getDeegreeParamsSection): " + wcsDP);
			return wcsDP;
		} catch (XMLParsingException e) {
			String s = e.getMessage();
			throw new InvalidConfigurationException(
					"Error while parsing the DeegreeParams "
							+ "Section of the WCS capabilities\n" + s
							+ StringTools.stackTraceToString(e));
		}
	}

//	/**
//	 * returns an array containing a list of default datadirectories parsed by
//	 * the WCS for coverage configurations
//	 * 
//	 * @param element
//	 * @return created array of data directory names
//	 * @throws XMLParsingException
//	 */
//	private String[] getDirectoryList(Element element ) {
//		ElementList el = XMLTools.getChildElements( "DataDirectory", DGRNS, element );
//		String[] dirList = new String[el.getLength()];
//		for (int i = 0; i < dirList.length; i++) {
//			dirList[i] = XMLTools.getStringValue(el.item(i));
//		}
//		return dirList;
//	}

	/**
	 * creates a <tt>CoverageOfferingBrief</tt> object from the passed element
	 * encapsulating one CoverageOfferingBrief part of the WCS ContentMetadata
	 * section
	 * 
	 * @param element
	 * @return created <tt>CoverageOfferingBrief</tt>
	 * @throws XMLParsingException
	 * @throws OGCWebServiceException
	 * @throws OGCException
	 */
	@Override
    protected CoverageOfferingBrief parseCoverageOfferingBrief(Element element )
			throws XMLParsingException, OGCWebServiceException, OGCException {
	    //Element elem = XMLTools.getChildElement( "metadataLink", WCSNS, element );
        Element elem = (Element)XMLTools.getNode( element,  PRE_WCS+"metadataLink", nsContext );
		MetadataLink mLink = parseMetadataLink(elem);
		String desc = XMLTools.getNodeAsString( element, PRE_WCS+"description", nsContext, null);
		//String name = XMLTools.getRequiredStringValue( "name", WCSNS, element );
        String name = XMLTools.getRequiredNodeAsString( element, PRE_WCS+"name", nsContext );
        //String label = XMLTools.getRequiredStringValue( "label", WCSNS, element );
        String label = XMLTools.getRequiredNodeAsString( element, PRE_WCS+"label", nsContext );
        //elem = XMLTools.getChildElement( "lonLatEnvelope", WCSNS, element );
        elem = (Element)XMLTools.getNode( element, PRE_WCS+"lonLatEnvelope", nsContext  );
		LonLatEnvelope llEnv = parseLonLatEnvelope(elem);
		//ElementList el = XMLTools.getChildElements( "keywords", WCSNS, element );
        List el = XMLTools.getNodes( element, PRE_WCS+"keywords", nsContext );
        Keywords[] keywords = new Keywords[el.size()];
        for( int i = 0; i< el.size(); ++i ){
            keywords[i] = parseKeywords( (Element) el.get(i), WCSNS);
        }
        //Keywords[] keywords = parseKeywords(el, WCSNS);
        String s = XMLTools.getRequiredNodeAsString(element, PRE_DEEGREE+"Configuration", nsContext );
		URL url = null;
//		if (s == null) {
//			throw new XMLParsingException( "<Configuration> element is required "
//							+ "in CoverageOfferingBrief section" );
//		}
		try {
		    url = resolve( s ); 
		} catch (Exception e) {
			throw new XMLParsingException( "<Configuration> element in CoverageOfferingBrief "
							+ "section isn't a valid URL: " + s);
		}

		return new CoverageOfferingBrief(name, label, desc, mLink, llEnv, keywords, url);
	}

}
/*******************************************************************************
 * Changes to this class. What the people have been up to: $Log:
 * WCSConfigurationDocument.java,v $ Revision 1.10 2004/07/06 16:44:25
 * mschneider More work on CatalogConfiguration and
 * CatalogConfigurationDocument. This includes the hierarchy of these classes.
 * 
 * Revision 1.9 2004/07/05 13:42:38 mschneider Changed deegreeParam to
 * deegreeParams wherever it is used.
 * 
 * Revision 1.8 2004/07/05 06:15:00 ap no message
 * 
 * Revision 1.7 2004/07/02 15:36:11 ap no message
 * 
 * Revision 1.6 2004/06/30 15:16:05 mschneider Refactoring of XMLTools.
 * 
 * Revision 1.5 2004/06/08 07:01:51 ap no message
 * 
 * Revision 1.4 2004/05/26 10:16:40 ap no message
 * 
 * Revision 1.3 2004/05/25 14:57:52 ap no message
 * 
 * Revision 1.2 2004/05/25 07:19:13 ap no message
 * 
 * Revision 1.1 2004/05/24 06:54:39 ap no message
 * 
 *  
 ******************************************************************************/
