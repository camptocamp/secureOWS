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
 53177 Bonn
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
package org.deegree.ogcwebservices.wpvs.j3d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Material;
import javax.vecmath.Color3f;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.filterencoding.AbstractFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterConstructionException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcbase.PropertyPath;
import org.xml.sax.SAXException;

/**
 * 
 * 
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/11/27 16:57:26 $
 *
 * @since 2.0
 */
public class Object3DFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( Object3DFactory.class );

    private URL xslt = Object3DFactory.class.getResource( "toWPVS.xsl" );

    /**
     * all texture images will be stored on a Map to avoid double loading
     * and creating a BufferedImage from a image source (textureMap property)
     */
    private static Map<String, BufferedImage> textImgMap;

    private static QualifiedName objectID;
    
    private static QualifiedName textMapQn;

    private static QualifiedName textCoordsQn;

    private static QualifiedName shininessQn;

    private static QualifiedName transparencyQn;

    private static QualifiedName ambientintensityQn;

    private static QualifiedName specularcolorQn;

    private static QualifiedName diffusecolorQn;

    private static QualifiedName emissivecolorQn;
    static {
        try {
            if ( textImgMap == null ) {                
                textImgMap = new HashMap<String, BufferedImage>( 200 );
                textMapQn = new QualifiedName( "app", "texturemap",
                                               new URI( "http://www.deegree.org/app" ) );
                textCoordsQn = new QualifiedName( "app", "texturecoordinates",
                                                  new URI( "http://www.deegree.org/app" ) );
                shininessQn = new QualifiedName( "app", "shininess",
                                                 new URI( "http://www.deegree.org/app" ) );
                transparencyQn = new QualifiedName( "app", "transparency",
                                                    new URI( "http://www.deegree.org/app" ) );
                ambientintensityQn = new QualifiedName( "app", "ambientintensity",
                                                        new URI( "http://www.deegree.org/app" ) );
                specularcolorQn = new QualifiedName( "app", "specularcolor",
                                                     new URI( "http://www.deegree.org/app" ) );
                diffusecolorQn = new QualifiedName( "app", "diffusecolor",
                                                    new URI( "http://www.deegree.org/app" ) );
                emissivecolorQn = new QualifiedName( "app", "emissivecolor",
                                                     new URI( "http://www.deegree.org/app" ) );
                objectID = new QualifiedName( "app", "fk_feature",
                                               new URI( "http://www.deegree.org/app" ) );
            }
        } catch ( URISyntaxException e ) {
            BootLogger.logError( e.getMessage(), e );
        }
    }

    /**
     * creates a Surface from the passed feature. It is assumed the feature
     * is simple, contains one surfac/polygon geometry and optional has
     * material and/or texture informations. The GML schema for a valid 
     * feature is defined as:
     * <pre>
     *  <xsd:schema targetNamespace="http://www.deegree.org/app" xmlns:app="http://www.deegree.org/app" xmlns:ogc="http://www.opengis.net/ogc" xmlns:deegreewfs="http://www.deegree.org/wfs" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:gml="http://www.opengis.net/gml" elementFormDefault="qualified" attributeFormDefault="unqualified">
     *      <xsd:import namespace="http://www.opengis.net/gml" schemaLocation="http://schemas.opengis.net/gml/3.1.1/base/feature.xsd"/>
     *       <xsd:import namespace="http://www.opengis.net/gml" schemaLocation="http://schemas.opengis.net/gml/3.1.1/base/geometryAggregates.xsd"/>
     *       <xsd:element name="WPVS" type="app:WPVSType" substitutionGroup="gml:_Feature"/>
     *       <xsd:complexType name="WPVSType">
     *           <xsd:complexContent>
     *               <xsd:extension base="gml:AbstractFeatureType">
     *                   <xsd:sequence>
     *                       <xsd:element name="fk_feature" type="xsd:double"/>
     *                       <xsd:element name="geometry" type="gml:GeometryPropertyType"/>
     *                       <xsd:element name="shininess" type="xsd:double" minOccurs="0"/>
     *                       <xsd:element name="transparency" type="xsd:double" minOccurs="0"/>
     *                       <xsd:element name="ambientintensity" type="xsd:double" minOccurs="0"/>
     *                       <xsd:element name="specularcolor" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="diffusecolor" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="emissivecolor" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="texturemap" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="texturecoordinates" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="texturetype" type="xsd:string" minOccurs="0"/>
     *                       <xsd:element name="repeat" type="xsd:integer" minOccurs="0"/>
     *                   </xsd:sequence>
     *               </xsd:extension>
     *           </xsd:complexContent>
     *       </xsd:complexType>
     *   </xsd:schema>
     * </pre> 
     * @param feature
     * @return a DefaultSurface which is derivtive of a Shape3D.
     */
    public DefaultSurface createSurface( Feature feature ){

        double oId = (Double) feature.getDefaultProperty( objectID ).getValue( -1d );
        
        BufferedImage textImage = null;
        FeatureProperty[] fp = feature.getProperties( textMapQn );
        if ( fp != null && fp.length > 0 ) {
            String tmp = (String) feature.getProperties( textMapQn )[0].getValue();
            if ( ( textImage = textImgMap.get( tmp ) ) == null ) {
                String lt = tmp.toLowerCase();
                try {
                    if ( lt.startsWith( "file:" ) || lt.startsWith( "http:" ) ) {
                        textImage = ImageUtils.loadImage( new URL( tmp ) );
                    } else {
                        textImage = ImageUtils.loadImage( tmp );
                    }
                } catch ( MalformedURLException e ) {
                    e.printStackTrace();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                textImgMap.put( tmp, textImage );
            }
        }
        float[][] textCoords = new float[1][];
        fp = feature.getProperties( textCoordsQn );
        if (  fp != null && fp.length > 0 ) {
            String tmp = (String) feature.getProperties( textCoordsQn )[0].getValue();
            if ( tmp != null ) {
                textCoords[0] = StringTools.toArrayFloat( tmp, ", " );
            }
        }
        double shininess = (Double) feature.getDefaultProperty( shininessQn ).getValue( 1d );
        double transparency = (Double) feature.getDefaultProperty( transparencyQn ).getValue( 0d );
        double ambientintensity = (Double) feature.getDefaultProperty( ambientintensityQn ).getValue(
                                                                                                      1d );
        Color3f ambientcolor = new Color3f( (float) ambientintensity, (float) ambientintensity,
                                            (float) ambientintensity );

        String tmp = (String) feature.getDefaultProperty( specularcolorQn ).getValue( "1 1 1" );
        float[] tmpFl = StringTools.toArrayFloat( tmp.trim(), " " );
        Color3f specularcolor = new Color3f( tmpFl[0], tmpFl[1], tmpFl[2] );

        tmp = (String) feature.getDefaultProperty( diffusecolorQn ).getValue( "1 1 1" );
        tmpFl = StringTools.toArrayFloat( tmp.trim(), " " );
        Color3f diffusecolor = new Color3f( tmpFl[0], tmpFl[1], tmpFl[2] );

        tmp = (String) feature.getDefaultProperty( emissivecolorQn ).getValue( "1 1 1" );
        tmpFl = StringTools.toArrayFloat( tmp.trim(), " " );
        Color3f emissivecolor = new Color3f( tmpFl[0], tmpFl[1], tmpFl[2] );

        Material material = new Material( ambientcolor, emissivecolor, diffusecolor, specularcolor,
                                          (float) shininess );        
        
        Surface geom = (Surface) feature.getDefaultGeometryPropertyValue();
        LOG.logDebug( "3D-Surface: ", geom );

        DefaultSurface surface = null;
        if ( textImage != null ) {            
            surface = new TexturedSurface( feature.getId(),  String.valueOf(oId), geom, material, (float) transparency,
                                           textImage, textCoords );
        } else {
            surface = new ColoredSurface( feature.getId(), String.valueOf(oId), geom, material, (float) transparency );
        }

        surface.compile();
        return surface;
    }

    public Composite3DObject createComposite3DObject( Feature feature ) {
        // TODO
        throw new UnsupportedOperationException();        
    }

    /**
     * creates a Composite3DObject from a FeatureCollection
     * @param fc
     * @return
     * @throws IOException 
     */
    public Composite3DObject createComposite3DObject( FeatureCollection fc )
                            throws IOException {

        List<DefaultSurface> list = new ArrayList<DefaultSurface>( fc.size() );
        for ( int i = 0; i < fc.size(); i++ ) {
            DefaultSurface sur = createSurface( fc.getFeature( i ) );
            list.add( sur );
        }
        return new Composite3DObject( fc.getId(), list );
    }

    /**
     * performs a GetFeature request against the passed URL using the also
     * passed featureType and Filter (least one may be <code>null</code>) 
     * 
     * @param url
     * @param featureType
     * @param filter
     * @return
     * @throws HttpException
     * @throws IOException
     * @throws XMLException
     * @throws SAXException
     */
    public Composite3DObject createComposite3DObject( URL url, QualifiedName featureType,
                                                     Filter filter )
                            throws IOException, XMLException {
        StringBuffer sb = new StringBuffer( 2000 );
        sb.append( "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" );
        sb.append( "<wfs:GetFeature version=\"1.1.0\" outputFormat=\"text/xml; " );
        sb.append( "subtype=gml/3.1.1\" xmlns:wfs=\"http://www.opengis.net/wfs\" " );
        sb.append( "xmlns:" ).append( featureType.getPrefix() ).append( "='" );
        sb.append( featureType.getNamespace().toASCIIString() ).append( "' " );
        sb.append( "><wfs:Query " );
        sb.append( "typeName='" ).append( featureType.getPrefix() ).append( ':' );
        sb.append( featureType.getLocalName() ).append( "'>" );
        if ( filter != null ) {
            sb.append( filter.toXML() );
        }
        sb.append( "</wfs:Query></wfs:GetFeature>" );

        LOG.logDebug( "GetFeature for access 3D objects", sb.toString() );

        PostMethod post = new PostMethod( "http://localhost:8081/wfs/services" );
        StringRequestEntity sre = new StringRequestEntity( sb.toString() );
        post.setRequestEntity( sre );
        HttpClient client = new HttpClient();
        client.executeMethod( post );

        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        try {
            doc.load( post.getResponseBodyAsStream(), url.toExternalForm() );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        }
        return createComposite3DObject( doc );
    }

    /**
     * performs a GetFeature request against the passed URL using the also
     * passed featureType and a @see Filter constructed from the passed
     * envelope
     * 
     * @param url
     * @param featureType
     * @param geomProperty
     * @param envelope
     * @return
     * @throws XMLException
     * @throws IOException
     */
    public Composite3DObject createComposite3DObject( URL url, QualifiedName featureType,
                                                     PropertyPath geomProperty, Envelope envelope )
                            throws XMLException, IOException {
        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"" );
        sb.append( "><ogc:BBOX>" );
        sb.append( "<ogc:PropertyName>" ).append( geomProperty.getAsString() );
        sb.append( "</ogc:PropertyName>" );
        sb.append( GMLGeometryAdapter.exportAsBox( envelope ) );
        sb.append( "</ogc:BBOX></ogc:Filter>" );

        XMLFragment xml = new XMLFragment();
        try {
            xml.load( new StringReader( sb.toString() ), XMLFragment.DEFAULT_URL );
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        }

        Filter filter = null;
        try {
            filter = AbstractFilter.buildFromDOM( xml.getRootElement() );
        } catch ( FilterConstructionException e ) {
            LOG.logError( e.getMessage(), e );
            throw new XMLException( e );
        }

        return createComposite3DObject( url, featureType, filter );
    }

    /**
     * 
     * @param gml
     * @return
     * @throws IOException
     */
    public Composite3DObject createComposite3DObject( GMLFeatureCollectionDocument gml )
                            throws IOException {

        FeatureCollection fc = null;
        try {
            XSLTDocument xsltDoc = new XSLTDocument( xslt );
            XMLFragment xml = xsltDoc.transform( gml );
            gml = new GMLFeatureCollectionDocument();
            gml.setRootElement( xml.getRootElement() );
            fc = gml.parse();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new IOException( "could not load/parse GML feature collection: " + e.getMessage() );
        }

        return createComposite3DObject( fc );
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: Object3DFactory.java,v $
 Revision 1.3  2006/11/27 16:57:26  bezema
 cleaning up the code

 Revision 1.2  2006/11/27 09:07:52  poth
 JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

 Revision 1.1  2006/11/23 11:46:40  bezema
 The initial version of the new wpvs

 Revision 1.1  2006/10/23 09:01:25  ap
 *** empty log message ***


 ********************************************************************** */