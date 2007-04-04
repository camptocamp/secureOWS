// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wcs/configuration/ExtensionDocument.java,v 1.3 2006/11/27 09:07:53 poth Exp $
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.ElementList;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.io.IODocument;
import org.deegree.io.JDBCConnection;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.InvalidGMLException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class creates a class representation of the Extension section of 
 * a deegree WCS coverage offering (coverage configuration) element. the 
 * extension section contains
 * informations about data access/sources for different resolutions and
 * ranges.<BR> 
 * an extension section must contain at least one Resolution element but
 * can contains as much as the user may defined. A resoluton contains a
 * access informations for data and the ranges the data are assigned to. 
 * because of this it is possible that more than one Resoultion element
 * with same resolution range but with different other ranges (e.g. time
 * or elevation) 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/11/27 09:07:53 $
 *
 * @since 1.1
 */
public class ExtensionDocument {

    private static final ILogger LOG = LoggerFactory.getLogger( ExtensionDocument.class );

    private static URI GMLNS = CommonNamespaces.GMLNS;

    private static URI DGRNS = CommonNamespaces.DEEGREEWCS;

    private URL systemId = null;

    private Element root = null;

    /**
     * constructing the ExtensionBuilder by passing the root element of
     * a deegree WCS CoverageOffering Extension.
     * 
     * @param root
     */
    public ExtensionDocument( Element root, URL systemId ) {
        this.root = root;
        this.systemId = systemId;
    }

    /**
     * returns the content of the Extension element of te deegree WCS coverage
     * description (configuration document). the extension section contains
     * informations about data access/sources for different resolutions and
     * ranges.<BR> 
     * an extension section must contain at least one Resolution element but
     * can contains as much as the user may defined. A resoluton contains a
     * access informations for data and the ranges the data are assigned to. 
     * because of this it is possible that more than one Resoultion element
     * with same resolution range but with different other ranges (e.g. time
     * or elevation) 
     * @return
     * @throws InvalidCVExtensionException
     * @throws UnknownCVExtensionException
     * @throws InvalidParameterValueException
     * @throws UnknownCRSException 
     */
    public Extension getExtension()
                            throws InvalidCVExtensionException, UnknownCVExtensionException,
                            InvalidParameterValueException, InvalidGMLException, UnknownCRSException {
        Extension extension = null;
        try {
            String type = XMLTools.getRequiredAttrValue( "type", null, root );
            ElementList el = XMLTools.getChildElements( "Resolution", DGRNS, root );
            Resolution[] resolutions = getResolutions( type, el );
            extension = new DefaultExtension( type, resolutions );
        } catch ( XMLParsingException e ) {
            throw new InvalidCVExtensionException( StringTools.stackTraceToString( e ) );
        }
        return extension;
    }

    /**
     * returns the resolutions definitions within the Extension element of the 
     * deegree WCS coverage offering. Each resoultion contains access description
     * for its data and an optional description of the ranges the data are 
     * valid for.
     * 
     * @param type
     * @param el
     * @return
     * @throws XMLParsingException
     * @throws InvalidParameterValueException
     * @throws UnknownCRSException 
     */
    private Resolution[] getResolutions( String type, ElementList el )
                            throws XMLParsingException, InvalidParameterValueException,
                            InvalidGMLException, UnknownCRSException {
        Resolution[] resolutions = new Resolution[el.getLength()];
        for ( int i = 0; i < resolutions.length; i++ ) {
            resolutions[i] = getResolution( type, el.item( i ) );
        }
        return resolutions;
    }

    /**
     * creates an instance of  <tt>Resoltuion</tt> from the passed <tt>Element</tt>
     * and the type of the coverage source. Valid values for type are:
     * <ul>
     *  <li>shapeIndexed
     *  <li>nameIndexed
     *  <li>file
     * </ul>
     * if an unknown typed is passed an <tt>InvalidParameterValueException</tt> will
     * be thrown
     * @param type
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private Resolution getResolution( String type, Element element )
                            throws XMLParsingException, InvalidParameterValueException,
                            InvalidGMLException, UnknownCRSException {
        String tmp = XMLTools.getRequiredAttrValue( "min", null, element );
        double min = Double.parseDouble( tmp );
        tmp = XMLTools.getRequiredAttrValue( "max", null, element );
        double max = Double.parseDouble( tmp );
        ElementList el = XMLTools.getChildElements( "Range", DGRNS, element );
        Range[] ranges = getRanges( el );
        Resolution resolution = null;
        if ( type.equals( "shapeIndexed" ) ) {
            // TODO 
            // enable more than one shape
            Element elem = XMLTools.getChildElement( "Shape", DGRNS, element );
            Shape shape = getShape( elem );
            resolution = new ShapeResolution( min, max, ranges, shape );
        } else if ( type.equals( "nameIndexed" ) ) {
            ElementList ell = XMLTools.getChildElements( "Directory", DGRNS, element );
            Directory[] dirs = new Directory[ell.getLength()];
            for ( int i = 0; i < dirs.length; i++ ) {
                dirs[i] = getDirectory( ell.item( i ) );
            }
            resolution = new DirectoryResolution( min, max, ranges, dirs );
        } else if ( type.equals( "file" ) ) {
            ElementList ell = XMLTools.getChildElements( "File", DGRNS, element );
            File[] files = new File[ell.getLength()];
            for ( int i = 0; i < files.length; i++ ) {
                files[i] = getFile( ell.item( i ) );
            }
            resolution = new FileResolution( min, max, ranges, files );
        } else if ( type.equals( "OracleGeoRaster" ) ) {
            resolution = getOracleGeoRasterResolution( element, min, max, ranges );

        } else {
            throw new InvalidParameterValueException( "type: " + type + " not known "
                                                      + "by the deegree WCS" );
        }
        return resolution;
    }

    /**
     * creates a <tt>OracleGeoRasterResolution</tt> object from the passed element 
     * @param element
     * @return
     * @throws XMLParsingException
     */
    private Resolution getOracleGeoRasterResolution( Element element, double min, double max,
                                                    Range[] ranges )
                            throws XMLParsingException {
        Resolution resolution;
        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        String xpath = "deegreewcs:OracleGeoRaster/dgjdbc:JDBCConnection";
        Node node = XMLTools.getRequiredNode( element, xpath, nsc );
        IODocument io = new IODocument( (Element) node );
        JDBCConnection jdbc = io.parseJDBCConnection();
        xpath = "deegreewcs:OracleGeoRaster/deegreewcs:Table/text()";
        String table = XMLTools.getRequiredNodeAsString( element, xpath, nsc );
        xpath = "deegreewcs:OracleGeoRaster/deegreewcs:RDTTable/text()";
        String rdtTable = XMLTools.getRequiredNodeAsString( element, xpath, nsc );
        xpath = "deegreewcs:OracleGeoRaster/deegreewcs:Column/text()";
        String column = XMLTools.getRequiredNodeAsString( element, xpath, nsc );
        xpath = "deegreewcs:OracleGeoRaster/deegreewcs:Identification/text()";
        String identification = XMLTools.getRequiredNodeAsString( element, xpath, nsc );
        xpath = "deegreewcs:OracleGeoRaster/deegreewcs:Level/text()";
        int level = XMLTools.getNodeAsInt( element, xpath, nsc, 1 );
        resolution = new OracleGeoRasterResolution( min, max, ranges, jdbc, table, rdtTable,
                                                    column, identification, level );
        return resolution;
    }

    /**
     * creates a <tt>Shape</tt> object from the passed element
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private Shape getShape( Element element ) throws XMLParsingException, UnknownCRSException {
        String tilePoperty = XMLTools.getRequiredAttrValue( "tileProperty", null, element );
        String directoryProperty = XMLTools.getRequiredAttrValue( "directoryProperty", null, element );
        String srsName = XMLTools.getRequiredAttrValue( "srsName", null, element );
        CoordinateSystem crs = CRSFactory.create( srsName );
        String rootFileName = XMLTools.getStringValue( element );
        rootFileName.trim();
        XMLFragment xml = new XMLFragment();
        xml.setRootElement( root );
        xml.setSystemId( systemId );
        java.io.File file = null;
        try {
            URL url = xml.resolve( rootFileName + ".shp" );
            file = new java.io.File( url.toURI() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            e.printStackTrace();
        }
        rootFileName = file.getAbsolutePath();
        rootFileName = rootFileName.substring( 0, rootFileName.lastIndexOf( "." ) );
        return new Shape( crs, rootFileName, tilePoperty, directoryProperty );
    }

    /**
     * creates a <tt>File</tt> object from the passed Element that describes 
     * the extensions and locations of the coverages assigned to a <tt>Resolution</tt>
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private File getFile( Element element )
                            throws XMLParsingException, InvalidGMLException, UnknownCRSException {
        String name = XMLTools.getRequiredStringValue( "Name", DGRNS, element );
        XMLFragment xml = new XMLFragment();
        xml.setRootElement( element );
        xml.setSystemId( systemId );
        try {
            // resolve name if relative
            name = xml.resolve( name ).toExternalForm();
        } catch ( MalformedURLException e ) {
            throw new XMLParsingException( "invalid file name/path: " + name );
        }
        Element elem = XMLTools.getRequiredChildElement( "Envelope", GMLNS, element );
        Envelope envelope = GMLGeometryAdapter.wrapBox( elem );
        String srs = XMLTools.getRequiredAttrValue( "srsName", null, elem );
        if ( srs != null ) {
            String[] tmp = StringTools.toArray( srs, "#", false );
            // just a heuristic because it is not guarranteed that the URL
            // in the srsName attribute can be solved
            if ( srs.toLowerCase().indexOf( "epsg" ) > -1 ) {
                srs = "EPSG:" + tmp[1];
            } else {
                srs = "CRS:" + tmp[1];
            }
            if ( tmp[1].equals( "0" ) )
                srs = null;
        }
        CoordinateSystem crs = CRSFactory.create( srs );
        return new File( crs, name, envelope );
    }

    /**
     * creates a <tt>Directory</tt> object from the passed Elememt that describes 
     * the extensions and locations of the coverages assigned to a <tt>Resolution</tt>
     * @param element
     * @return
     * @throws XMLParsingException
     * @throws UnknownCRSException 
     */
    private Directory getDirectory( Element element )
                            throws XMLParsingException, InvalidGMLException, UnknownCRSException {
        // get valid file extension for this directory
        String temp = XMLTools.getRequiredAttrValue( "extensions", null, element );
        String[] extensions = StringTools.toArray( temp, ",;", true );
        // get the width and height (in pixels) af the tiles in this  directory
        temp = XMLTools.getRequiredAttrValue( "tileWidth", null, element );
        double tileWidth = 0;
        try {
            tileWidth = Double.parseDouble( temp );
        } catch ( Exception e ) {
            throw new XMLParsingException( "tileWidth attribute isn't a number" );
        }
        double tileHeight = 0;
        try {
            tileHeight = Double.parseDouble( temp );
        } catch ( Exception e ) {
            throw new XMLParsingException( "tileHeight attribute isn't a number" );
        }
        // get the directroy name
        String name = XMLTools.getRequiredStringValue( "Name", DGRNS, element );
        XMLFragment xml = new XMLFragment();
        xml.setRootElement( element );
        xml.setSystemId( systemId );
        try {
            // resolve name if relative
            name = xml.resolve( name ).toExternalForm();
        } catch ( MalformedURLException e ) {
            throw new XMLParsingException( "invalid file name/path: " + name );
        }
        // get the bounding envelope of all tiles in the directory
        Element elem = XMLTools.getRequiredChildElement( "Envelope", GMLNS, element );
        Envelope envelope = GMLGeometryAdapter.wrapBox( elem );
        String srs = XMLTools.getRequiredAttrValue( "srsName", null, elem );
        if ( srs != null ) {
            String[] tmp = StringTools.toArray( srs, "#", false );
            // just a heuristic because it is not guarranteed that the URL
            // in the srsName attribute can be solved
            if ( srs.toLowerCase().indexOf( "epsg" ) > -1 ) {
                srs = "EPSG:" + tmp[1];
            } else {
                srs = "CRS:" + tmp[1];
            }
            if ( tmp[1].equals( "0" ) )
                srs = null;
        }
        CoordinateSystem crs = CRSFactory.create( srs );
        return new GridDirectory( name, envelope, crs, extensions, tileWidth, tileHeight );
    }

    /**
     * creates an array of <tt>Ranges</tt> from the passed element list
     * @param el
     * @return created array of <tt>Ranges</tt>
     * @throws XMLParsingException
     */
    private Range[] getRanges( ElementList el )
                            throws XMLParsingException {
        Range[] ranges = new Range[el.getLength()];
        for ( int i = 0; i < ranges.length; i++ ) {
            ranges[i] = getRange( el.item( i ) );
        }
        return ranges;
    }

    /**
     * creates a <tt>Range</tt> object from the passed element
     * @param element
     * @return created <tt>Range</tt>
     * @throws XMLParsingException
     */
    private Range getRange( Element element )
                            throws XMLParsingException {
        String name = XMLTools.getRequiredStringValue( "Name", DGRNS, element );
        ElementList el = XMLTools.getChildElements( "Axis", DGRNS, element );
        Axis[] axis = getAxis( el );
        return new Range( name, axis );
    }

    /**
     * creates an array of <tt>Axis</tt> objects from the passed element list 
     * @param el
     * @return created array of <tt>Axis</tt>
     * @throws XMLParsingException
     */
    private Axis[] getAxis( ElementList el )
                            throws XMLParsingException {
        Axis[] axis = new Axis[el.getLength()];
        for ( int i = 0; i < axis.length; i++ ) {
            axis[i] = getAxis( el.item( i ) );
        }
        return axis;
    }

    /**
     * creates an <tt>Axis</tt> object from the passed element. The 
     * <tt>Interval</tt> included in the <tt>Axis</tt> doesn't have a
     * resolution because it isn't required.
     * @param element
     * @return created <tt>Axis</tt>
     * @throws XMLParsingException
     */
    private Axis getAxis( Element element )
                            throws XMLParsingException {
        try {
            String name = XMLTools.getRequiredStringValue( "Name", DGRNS, element );
            Element elem = XMLTools.getRequiredChildElement( "Interval", DGRNS, element );
            String tmp = XMLTools.getRequiredStringValue( "min", DGRNS, elem );
            TypedLiteral min = new TypedLiteral( tmp, new URI( "xs:double" ) );
            tmp = XMLTools.getRequiredStringValue( "max", DGRNS, elem );
            TypedLiteral max = new TypedLiteral( tmp, new URI( "xs:double" ) );
            Interval interval = new Interval( min, max, null, null, null );
            return new Axis( name, interval );
        } catch ( URISyntaxException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLParsingException( e.getMessage() );
        }
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: ExtensionDocument.java,v $
 Revision 1.3  2006/11/27 09:07:53  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.2  2006/11/23 08:36:19  bezema
 a trim of the rootfilename sometimes cures the endline failure

 Revision 1.1  2006/07/13 13:31:19  poth
 class rename from ExtensionBuilder to ExtensionDocument

 Revision 1.18  2006/05/19 12:34:52  poth
 todo note added

 Revision 1.17  2006/05/12 15:26:05  poth
 *** empty log message ***

 Revision 1.16  2006/05/01 20:15:26  poth
 *** empty log message ***

 Revision 1.15  2006/04/06 20:25:27  poth
 *** empty log message ***

 Revision 1.14  2006/04/04 20:39:42  poth
 *** empty log message ***

 Revision 1.13  2006/03/30 21:20:26  poth
 *** empty log message ***

 Revision 1.12  2006/03/14 08:42:52  poth
 *** empty log message ***

 Revision 1.11  2006/03/07 21:40:20  poth
 *** empty log message ***

 Revision 1.10  2006/03/02 21:39:38  poth
 *** empty log message ***

 Revision 1.9  2006/02/28 09:45:33  poth
 *** empty log message ***

 Revision 1.8  2006/02/06 15:54:33  poth
 *** empty log message ***

 Revision 1.7  2006/02/06 11:08:24  poth
 *** empty log message ***

 Revision 1.6  2006/02/05 20:33:09  poth
 *** empty log message ***

 Revision 1.5  2005/11/16 13:45:00  mschneider
 Merge of wfs development branch.

 Revision 1.4.2.1  2005/11/07 13:09:26  deshmukh
 Switched namespace definitions in "CommonNamespaces" to URI.

 Revision 1.4  2005/09/27 19:53:18  poth
 no message

 Revision 1.3  2005/03/09 11:55:47  mschneider
 *** empty log message ***

 Revision 1.2  2005/01/16 22:30:33  poth
 no message

 Revision 1.10  2004/08/12 10:39:44  ap
 no message

 Revision 1.9  2004/08/09 06:43:50  ap
 no message

 Revision 1.8  2004/07/19 06:20:01  ap
 no message

 Revision 1.7  2004/07/13 06:13:07  ap
 no message

 Revision 1.6  2004/07/12 13:03:21  mschneider
 More work on the CatalogConfiguration and capabilities framework.

 Revision 1.5  2004/07/12 06:12:11  ap
 no message

 Revision 1.4  2004/06/30 15:16:05  mschneider
 Refactoring of XMLTools.

 Revision 1.3  2004/06/28 06:26:52  ap
 no message

 Revision 1.2  2004/05/28 06:02:57  ap
 no message


 ********************************************************************** */
