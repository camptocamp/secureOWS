// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcbase/CommonNamespaces.java,v 1.35 2006/10/11 16:05:13 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
 lat/lon Fitzke/Fretter/Poth GbR
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
 lat/lon Fitzke/Fretter/Poth GbR
 Meckenheimer Allee 176
 53115 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Jens Fitzke
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de

 
 ---------------------------------------------------------------------------*/
package org.deegree.ogcbase;

import java.net.URI;
import java.net.URISyntaxException;

import org.deegree.framework.util.BootLogger;
import org.deegree.framework.xml.NamespaceContext;

/**
 * Definitions for all namespaces used within deegree.
 * <p>
 * NOTE: Only use namespaces defined here by referencing them. Don't introduce
 * namespaces in other types.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author <a href="mailto:tfriebe@sf.net">Torsten Friebe</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.35 $, $Date: 2006/10/11 16:05:13 $
 */

// FIXME change ows namespace uri to http://www.opengeospatial.net/ows
// output bound to the current ows namesspace uri (http://www.opengis.net/ows)
// is not valid against up to date ogc schemas!!!!
public class CommonNamespaces {

	// namespaces
	public static final URI XMLNS = buildNSURI( "http://www.w3.org/2000/xmlns/" );

	public static final URI SMLNS = buildNSURI( "http://www.opengis.net/sensorML" );

	public static final URI SOSNS = buildNSURI( "http://www.opengis.net/sos" );

	public static final URI CSWNS = buildNSURI( "http://www.opengis.net/cat/csw" );

	public static final URI GMLNS = buildNSURI( "http://www.opengis.net/gml" );

	public static final URI WFSNS = buildNSURI( "http://www.opengis.net/wfs" );

	public static final URI WFSGNS = buildNSURI( "http://www.opengis.net/wfs-g" );

	public static final URI WCSNS = buildNSURI( "http://www.opengis.net/wcs" );

	public static final URI WMSNS = buildNSURI( "http://www.opengis.net/wms" );

	public static final URI WMPSNS = buildNSURI( "http://www.opengis.net/wmps" );

	public static final URI WPVSNS = buildNSURI( "http://www.opengis.net/wpvs" );

	public static final URI WPSNS = buildNSURI( "http://www.opengeospatial.net/wps" );

	public static final URI OGCNS = buildNSURI( "http://www.opengis.net/ogc" );

	public static final URI OWSNS = buildNSURI( "http://www.opengis.net/ows" );

	public static final URI SLDNS = buildNSURI( "http://www.opengis.net/sld" );

	public static final URI OMNS = buildNSURI( "http://www.opengis.net/om" );

	public static final URI XLNNS = buildNSURI( "http://www.w3.org/1999/xlink" );

	public static final URI CNTXTNS = buildNSURI( "http://www.opengis.net/context" );

	public static final URI DGCNTXTNS = buildNSURI( "http://www.deegree.org/context" );

	public static final URI DEEGREEWFS = buildNSURI( "http://www.deegree.org/wfs" );

	public static final URI DEEGREEWMS = buildNSURI( "http://www.deegree.org/wms" );

	public static final URI DEEGREEWCS = buildNSURI( "http://www.deegree.org/wcs" );

	public static final URI DEEGREECSW = buildNSURI( "http://www.deegree.org/csw" );

	public static final URI DEEGREESOS = buildNSURI( "http://www.deegree.org/sos" );

	public static final URI DEEGREEWAS = buildNSURI( "http://www.deegree.org/was" );

	public static final URI DEEGREEWSS = buildNSURI( "http://www.deegree.org/wss" );

	public static final URI DEEGREEWMPS = buildNSURI( "http://www.deegree.org/wmps" );

	public static final URI DEEGREEWPVS = buildNSURI( "http://www.deegree.org/wpvs" );

	public static final URI DEEGREEWPS = buildNSURI( "http://www.deegree.org/wps" );

	public static final URI DGJDBC = buildNSURI( "http://www.deegree.org/jdbc" );

	public static final URI DGSECNS = buildNSURI( "http://www.deegree.org/security" );

	public static final URI ISO19112NS = buildNSURI( "http://www.opengis.net/iso19112" );

	public static final URI ISO19115NS = buildNSURI( "http://schemas.opengis.net/iso19115full" );

    public static final URI ISO19115BRIEFNS = buildNSURI( "http://schemas.opengis.net/iso19115brief" );

    public static final URI ISO19119NS = buildNSURI( "http://schemas.opengis.net/iso19119" );
    
    public static final URI DCNS = buildNSURI( "http://purl.org/dc/elements/1.1/" );

	public static final URI GDINRW_WSS = buildNSURI( "http://www.gdi-nrw.org/wss" );

	public static final URI GDINRW_WAS = buildNSURI( "http://www.gdi-nrw.org/was" );

	public static final URI WSSSESSIONNS = buildNSURI( "http://www.gdi-nrw.org/session" );

	public static final URI XSNS = buildNSURI( "http://www.w3.org/2001/XMLSchema" );

    public static final URI XSINS = buildNSURI( "http://www.w3.org/2001/XMLSchema-instance" );    
    
    public static final URI SMXMLNS = buildNSURI( "http://metadata.dgiwg.org/smXML" );
    
   
	// prefixes
	public static final String SML_PREFIX = "sml";

	public static final String SOS_PREFIX = "sos";

	public static final String CSW_PREFIX = "csw";

	public static final String GML_PREFIX = "gml";

	public static final String WFS_PREFIX = "wfs";

	public static final String WFSG_PREFIX = "wfsg";

	public static final String WCS_PREFIX = "wcs";

	public static final String WMS_PREFIX = "wms";

	public static final String WPVS_PREFIX = "wpvs";

	public static final String WMPS_PREFIX = "wmps";

	public static final String WPS_PREFIX = "wps";

	public static final String OGC_PREFIX = "ogc";

	public static final String OWS_PREFIX = "ows";

	public static final String SLD_PREFIX = "sld";

	public static final String XLINK_PREFIX = "xlink";

	public static final String XMLNS_PREFIX = "xmlns";

	public static final String XS_PREFIX = "xs";

    public static final String XSI_PREFIX = "xsi";    

	public static final String CNTXT_PREFIX = "cntxt";

	public static final String DGCNTXT_PREFIX = "dgcntxt";

	public static final String DEEGREEWFS_PREFIX = "deegreewfs";

	public static final String DEEGREEWMS_PREFIX = "deegreewms";

	public static final String DEEGREEWCS_PREFIX = "deegreewcs";

	public static final String DEEGREECSW_PREFIX = "deegreecsw";

	public static final String DEEGREESOS_PREFIX = "deegreesos";

	public static final String DEEGREEWAS_PREFIX = "deegreewas";

	public static final String DEEGREEWSS_PREFIX = "deegreewss";

	public static final String DEEGREEWMPS_PREFIX = "deegreewmps";

	public static final String DEEGREEWPS_PREFIX = "deegreewps";

	public static final String DEEGREEWPVS_PREFIX = "deegreewpvs";

	public static final String DGJDBC_PREFIX = "dgjdbc";

	public static final String DGSEC_PREFIX = "dgsec";

	public static final String ISO19112_PREFIX = "iso19112";

	public static final String ISO19115_PREFIX = "iso19115";
    
    public static final String ISO19115BRIEF_PREFIX = "iso19115brief";
    
    public static final String ISO19119_PREFIX = "iso19119";
    
    public static final String DC_PREFIX = "dc";

	public static final String GDINRWWSS_PREFIX = "wss";

	public static final String GDINRWWAS_PREFIX = "was";

	public static final String WSSSESSION_PREFIX = "wsssession";

	public static final String OMNS_PREFIX = "om";
    
    public static final String SMXML_PREFIX = "smXML";
    
    //gdinrw namespaces
    //gdinrw uris
    public static final URI GDINRW_AUTH = buildNSURI( "http://www.gdi-nrw.org/authentication" );
    public static final String GDINRW_AUTH_PREFIX = "authn";
    public static final URI GDINRW_SESSION = buildNSURI( "http://www.gdi-nrw.org/session" );
    public static final String GDINRW_SESSION_PREFIX = "sessn";
    
    
    //TODO delete duplicate WSSSESSION 
//   public static final URI GDINRW_SESSION = buildNSURI( "http://www.gdi-nrw.org/session" );
//   public static final String GDINRW_SESSION_PREFIX = "";    

	private static NamespaceContext nsContext = null;

	/**
	 * @param namespace
	 * @return Returns the uri for the passed namespace.
	 */
	public static URI buildNSURI( String namespace ) {
		URI uri = null;
		try {
			uri = new URI( namespace );
		} catch ( URISyntaxException e ) {
			BootLogger.logError( "Invalid common namespace URI '" + namespace + "':"
					+ e.getMessage(), e );
		}
		return uri;
	}

	/**
	 * Returns the <code>NamespaceContext</code> for common namespaces known
	 * be deegree.
	 * 
	 * @return the NamespaceContext for all common namespaces
	 */
	public static NamespaceContext getNamespaceContext() {
		if ( nsContext == null ) {
			nsContext = new NamespaceContext();
			nsContext.addNamespace( SML_PREFIX, SMLNS );
			nsContext.addNamespace( SOS_PREFIX, SOSNS );
			nsContext.addNamespace( CSW_PREFIX, CSWNS );
			nsContext.addNamespace( GML_PREFIX, GMLNS );
			nsContext.addNamespace( WFS_PREFIX, WFSNS );
			nsContext.addNamespace( WFSG_PREFIX, WFSGNS );
			nsContext.addNamespace( WCS_PREFIX, WCSNS );
			nsContext.addNamespace( WMS_PREFIX, WMSNS );
			nsContext.addNamespace( WPVS_PREFIX, WMPSNS );
			nsContext.addNamespace( WPVS_PREFIX, WPVSNS );
			nsContext.addNamespace( WPS_PREFIX, WPSNS );
			nsContext.addNamespace( OGC_PREFIX, OGCNS );
			nsContext.addNamespace( OWS_PREFIX, OWSNS );
			nsContext.addNamespace( SLD_PREFIX, SLDNS );
			nsContext.addNamespace( XLINK_PREFIX, XLNNS );
			nsContext.addNamespace( XS_PREFIX, XSNS );
            nsContext.addNamespace( XSI_PREFIX, XSINS );
			nsContext.addNamespace( CNTXT_PREFIX, CNTXTNS );
			nsContext.addNamespace( DGCNTXT_PREFIX, DGCNTXTNS );
			nsContext.addNamespace( DEEGREEWFS_PREFIX, DEEGREEWFS );
			nsContext.addNamespace( DEEGREEWMS_PREFIX, DEEGREEWMS );
			nsContext.addNamespace( DEEGREEWCS_PREFIX, DEEGREEWCS );
			nsContext.addNamespace( DEEGREECSW_PREFIX, DEEGREECSW );
			nsContext.addNamespace( DEEGREESOS_PREFIX, DEEGREESOS );
			nsContext.addNamespace( DEEGREEWAS_PREFIX, DEEGREEWAS );
			nsContext.addNamespace( DEEGREEWSS_PREFIX, DEEGREEWSS );
			nsContext.addNamespace( DEEGREEWPVS_PREFIX, DEEGREEWPVS );
			nsContext.addNamespace( DEEGREEWMPS_PREFIX, DEEGREEWMPS );
			nsContext.addNamespace( DEEGREEWPS_PREFIX, DEEGREEWPS );
			nsContext.addNamespace( DGSEC_PREFIX, DGSECNS );
			nsContext.addNamespace( DGJDBC_PREFIX, DGJDBC );
			nsContext.addNamespace( ISO19112_PREFIX, ISO19112NS );
			nsContext.addNamespace( ISO19115_PREFIX, ISO19115NS );
            nsContext.addNamespace( ISO19115BRIEF_PREFIX, ISO19115BRIEFNS );
            nsContext.addNamespace( ISO19119_PREFIX, ISO19119NS );
            nsContext.addNamespace( DC_PREFIX, DCNS );
			nsContext.addNamespace( WSSSESSION_PREFIX, WSSSESSIONNS );
			nsContext.addNamespace( GDINRWWAS_PREFIX, GDINRW_WAS );
			nsContext.addNamespace( GDINRWWSS_PREFIX, GDINRW_WSS );
			nsContext.addNamespace( OMNS_PREFIX, OMNS );
            nsContext.addNamespace( SMXML_PREFIX, SMXMLNS );
            nsContext.addNamespace( GDINRW_AUTH_PREFIX, GDINRW_AUTH);
		}
		return nsContext;
	}

	public String toString() {
		return nsContext.getURI( WPS_PREFIX ).toString();
	}

}

/* ******************************************************************************
Changes to this class. What the people have been up to:

$Log: CommonNamespaces.java,v $
Revision 1.35  2006/10/11 16:05:13  mschneider
Added "xsi:"-namespace prefix and URI.

Revision 1.34  2006/09/08 08:42:02  schmitz
Updated the WMS to be 1.1.1 conformant once again.
Cleaned up the WMS code.
Added cite WMS test data.

Revision 1.33  2006/08/30 16:58:16  mschneider
Removed DEEGREEAPP (http://www.deegree.org/app) binding. This is *not* a constant binding.

Revision 1.32  2006/05/23 15:23:17  bezema
added GDINRW_SESSION namespace and prefix

Revision 1.31  2006/05/22 15:49:28  bezema
Updated the wss and was prefixes

Revision 1.30  2006/05/16 15:05:34  bezema
added gdi-nrw:authn namespace

Revision 1.29  2006/04/26 14:25:47  mays
added SMXMLNS and ISO19115BRIEFNS; replaced uri for ISO19115NS;
cleaned up the cvs logs in the footer - don't mess'em up
 
Revision 1.21 2006/01/31 17:11:16 deshmukh
Prefix 'app' added

Revision 1.20 2006/01/05 15:18:27 deshmukh
Renamed WPS to WMPS

Revision 1.19 2006/01/05 12:57:11 deshmukh
New Service WMPS added

Revision 1.18 2005/11/25 14:21:36 mays
add new namespaces for WPVS 

Revision 1.17 2005/11/16 13:45:00 mschneider
Merge of wfs development branch. 

Revision 1.16.2.4 2005/11/07 18:28:22 mschneider 
*** empty log message *** 

Revision 1.16.2.3 2005/11/07 15:38:04 mschneider
Refactoring: use NamespaceContext instead of Node for namespace bindings. 

Revision 1.16.2.2 2005/11/07 13:09:26 deshmukh
Switched namespace definitions in "CommonNamespaces" to URI.

Revision 1.16.2.1 2005/10/31 19:17:37 mschneider
Added XSDNS. 
Added method for constructions of URIs.

Revision 1.16 2005/10/07 10:30:41 poth
no message

Revision 1.15 2005/10/05 21:03:55 poth
no message

Revision 1.14 2005/06/08 15:13:55 poth
no message
 
Revision 1.13 2005/06/07 11:54:51 poth
no message

Revision 1.12 2005/04/23 15:32:05 poth
no message

Revision 1.11 2005/04/20 20:36:09 poth
no message

Revision 1.10 2005/04/19 13:23:18 poth
no message

Revision 1.9 2005/04/08 15:57:24 poth
no message

***************************************************************************** */
