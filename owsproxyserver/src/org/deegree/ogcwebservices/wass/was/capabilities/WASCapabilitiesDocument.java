//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wass/was/capabilities/WASCapabilitiesDocument.java,v 1.8 2006/06/27 13:10:47 bezema Exp $
/*----------------    FILE HEADER  ------------------------------------------
 
 This file is part of deegree.
 Copyright (C) 2001-2004 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
 Meckenheimer Allee 176
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

package org.deegree.ogcwebservices.wass.was.capabilities;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wass.common.Messages;
import org.deegree.ogcwebservices.wass.common.OWSCapabilitiesBaseDocument_1_0;
import org.deegree.ogcwebservices.wass.common.OperationsMetadata_1_0;
import org.deegree.ogcwebservices.wass.common.SupportedAuthenticationMethod;
import org.xml.sax.SAXException;

/**
 * Parser for the WAS capabilities according to GDI NRW spec V1.0.
 * 
 * Namespace: http://www.gdi-nrw.org/was
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.8 $, $Date: 2006/06/27 13:10:47 $
 * 
 * @since 2.0
 */
public class WASCapabilitiesDocument extends OWSCapabilitiesBaseDocument_1_0 {

    private static final long serialVersionUID = -6646616562364235109L;

    private static final ILogger LOG = LoggerFactory.getLogger( WASCapabilitiesDocument.class );

    /**
     * This is the XML template used for the GetCapabilities response document.
     */
    public static final String XML_TEMPLATE = "WASCapabilitiesTemplate.xml";

    /**
     * Creates an empty document from default template.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public void createEmptyDocument()
                            throws IOException, SAXException {
        super.createEmptyDocument( XML_TEMPLATE );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.ogcwebservices.getcapabilities.OGCCapabilitiesDocument#parseCapabilities()
     */
    @Override
    public OGCCapabilities parseCapabilities()
                            throws InvalidCapabilitiesException {
        LOG.entering();

        WASCapabilities wasCapabilites = null;
        try {

            ServiceIdentification sf = parseServiceIdentification();
            ServiceProvider sp = parseServiceProvider();
            OperationsMetadata_1_0 om = parseOperationsMetadata();
            String version = parseVersion();
            String updateSequence = parseUpdateSequence();

            ArrayList<SupportedAuthenticationMethod> am = parseSupportedAuthenticationMethods( CommonNamespaces.GDINRWWAS_PREFIX );
            wasCapabilites = new WASCapabilities( version, updateSequence, sf, sp, om, am );

        } catch ( XMLParsingException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidCapabilitiesException(
                                                    Messages.format(
                                                                     "ogcwebservices.wass.ERROR_CAPABILITIES_NOT_PARSED",
                                                                     "WAS" ) );
        } catch ( URISyntaxException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidCapabilitiesException(
                                                    Messages.format(
                                                                     "ogcwebservices.wass.ERROR_URI_NOT_READ",
                                                                     new String[] { "WAS",
                                                                                   "(unknown)" } ) );
        } catch ( MalformedURLException e ) {
            LOG.logError( e.getLocalizedMessage(), e );
            throw new InvalidCapabilitiesException(
                                                    Messages.format(
                                                                     "ogcwebservices.wass.ERROR_URL_NOT_READ",
                                                                     new String[] { "WAS",
                                                                                   "(unknown)" } ) );
        }

        LOG.exiting();
        return wasCapabilites;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WASCapabilitiesDocument.java,v $
 * Changes to this class. What the people have been up to: Revision 1.8  2006/06/27 13:10:47  bezema
 * Changes to this class. What the people have been up to: Finished the last bits of the configuration of the wass.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/06/23 13:53:48  schmitz
 * Changes to this class. What the people have been up to: Externalized all Strings, fixed up some exceptions and messages, reviewed/fixed some code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6 2006/06/23 10:23:50 schmitz
 * Changes to this class. What the people have been up to: Completed the WAS, GetSession and
 * CloseSession work. Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.5 2006/06/09 12:58:32 schmitz Changes to this class.
 * What the people have been up to: Set up some tests for WAS/WSS and the URN class. Changes to this
 * class. What the people have been up to: Commented out some of the deegree param stuff in order
 * for the Changes to this class. What the people have been up to: tests to run. Changes to this
 * class. What the people have been up to: Tests have hardcoded URLs in them, so they won't run just
 * anywhere. Changes to this class. What the people have been up to: Changes to this class. What the
 * people have been up to: Revision 1.4 2006/05/30 08:44:48 bezema Changes to this class. What the
 * people have been up to: Reararranging the layout (again) to use features of OOP. The
 * owscommonDocument is the real baseclass now. Changes to this class. What the people have been up
 * to: Changes to this class. What the people have been up to: Revision 1.3 2006/05/30 07:47:27
 * schmitz Changes to this class. What the people have been up to: Started on the XMLFactory.
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.2 2006/05/29 16:13:00 schmitz Changes to this class. What the people
 * have been up to: Added an XMLFactory to create WAS and WSS GetCapabilities responses. Also added
 * the XML templates for this purpose. Changes to this class. What the people have been up to:
 * Renamed the WASOperationsMetadata to OperationsMetadata_1_0 and made some minor changes to some
 * of the related bean classes. Changes to this class. What the people have been up to: Changes to
 * this class. What the people have been up to: Revision 1.1 2006/05/29 12:00:58 bezema Changes to
 * this class. What the people have been up to: Refactored the security and authentication
 * webservices into one package WASS (Web Authentication -and- Security Services), also created a
 * common package and a saml package which could be updated to work in the future. Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.5 2006/05/26 14:38:32 schmitz Changes to this class. What the people have been up to:
 * Added some KVP constructors to WAS operations. Changes to this class. What the people have been
 * up to: Added some comments, updated the plan. Changes to this class. What the people have been up
 * to: Restructured WAS operations by adding an AbstractRequest base class. Changes to this class.
 * What the people have been up to: Changes to this class. What the people have been up to: Revision
 * 1.4 2006/05/23 15:20:50 bezema Changes to this class. What the people have been up to: Cleaned up
 * the warnings and added some minor methods Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.3 2006/05/22 15:47:05 bezema
 * Changes to this class. What the people have been up to: Cleaning up redundant Documents Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.2 2006/05/19 15:35:35 schmitz Changes to this class. What the people have been
 * up to: Updated the documentation, added the GetCapabilities operation and implemented a rough
 * WAService outline. Fixed some warnings. Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.1 2006/05/15 09:54:16 bezema
 * Changes to this class. What the people have been up to: New approach to the nrw:gdi specs.
 * Including ows_1_0 spec and saml spec Changes to this class. What the people have been up to:
 * 
 **************************************************************************************************/
