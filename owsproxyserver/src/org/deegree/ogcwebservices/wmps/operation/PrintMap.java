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
package org.deegree.ogcwebservices.wmps.operation;

import java.awt.Color;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CharsetUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GMLGeometryAdapter;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.InvalidGMLException;
import org.deegree.ogcwebservices.InconsistentRequestException;
import org.deegree.ogcwebservices.wms.InvalidFormatException;
import org.deegree.ogcwebservices.wms.operation.GetMap;
import org.deegree.ogcwebservices.wms.operation.GetMap.Layer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface describes the access to the parameters of a PrintMap request. It is expected that
 * there are two kinds of request. The first is the 'normal' HTTP GET request with name-value-pair
 * enconding and the second is a HTTP POST request containing a SLD.
 * <p>
 * </p>
 * It is possible to access the values of a HTTP GET request throught its bean accessor methods. The
 * request shall be mapped to a SLD data structure, accessible using the <tt>getSLD()</tt> method.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * @version 2.0
 */
public class PrintMap extends WMPSRequestBase implements Serializable {

    private static final long serialVersionUID = 6898492018448337645L;

    private static final ILogger LOG = LoggerFactory.getLogger( PrintMap.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private List<Layer> layers;

    private String srs;

    private Envelope boundingBox;

    private Point center;

    private int scaleDenominator = -1;

    private boolean transparent;

    private Color bgColor;

    private String title;

    private String copyright;

    private boolean legend;

    private boolean scaleBar;

    private String note;

    private String template;

    private String emailaddress;

    private Timestamp timestamp;

    private TextArea[] textAreas;

    /**
     * Create a new PrintMap instance.
     * 
     * @param id
     * @param version
     * @param layers
     * @param srs
     * @param boundingBox
     * @param center
     * @param scaleDenominator
     * @param transparent
     * @param bgColor
     * @param title
     * @param copyright
     * @param legend
     * @param scaleBar
     * @param note
     * @param template
     * @param emailaddress
     * @param timestamp
     * @param textAreas
     * @param vendorSpecificParameter
     */
    PrintMap( String id, String version, Layer[] layers, String srs, Envelope boundingBox,
             Point center, int scaleDenominator, boolean transparent, Color bgColor, String title,
             String copyright, boolean legend, boolean scaleBar, String note, String template,
             String emailaddress, Timestamp timestamp, TextArea[] textAreas,
             Map vendorSpecificParameter ) {

        super( version, id, vendorSpecificParameter );

        setLayers( layers );
        this.srs = srs;
        this.boundingBox = boundingBox;
        this.center = center;
        this.scaleDenominator = scaleDenominator;
        this.transparent = transparent;
        this.bgColor = bgColor;
        this.title = title;
        this.copyright = copyright;
        this.legend = legend;
        this.scaleBar = scaleBar;
        this.note = note;
        this.template = template;
        this.emailaddress = emailaddress;
        setTimestamp( timestamp );
        this.textAreas = textAreas;
    }

    /**
     * Set the time stamp.
     * 
     * @param timestamp
     */
    private void setTimestamp( Timestamp timestamp ) {

        if ( timestamp != null ) {
            this.timestamp = timestamp;
        } else {
            this.timestamp = setCurrentTime();
        }
    }

    /**
     * Sets the Current System Time where the request was recieved.
     * 
     * @return Date
     */
    private static Timestamp setCurrentTime() {
        long now = System.currentTimeMillis();
        return new Timestamp( now );
    }

    /**
     * The required LAYERS parameter lists the map layer(s) to be returned by this PrintMapRequest
     * request. The value of the LAYERS parameter is a comma-separated list of one or more valid
     * layer names. Allowed layer names are the character data content of any <Layer><Name> element
     * in the Capabilities XML.
     * <p>
     * </p>
     * A WMS shall render the requested layers by drawing the leftmost in the list bottommost, the
     * next one over that, and so on.
     * <p>
     * </p>
     * Each layer is associated to a style. Styles are also is encoded as a comma- seperated list
     * within the PrintMapRequest request.
     * <p>
     * </p>
     * The required STYLES parameter lists the style in which each layer is to be rendered. There is
     * a one-to-one correspondence between the values in the LAYERS parameter and the values in the
     * STYLES parameter. Because of this layer-style combinations are returned coupled within an
     * array of Layer- objects. Each map in the list of LAYERS is drawn using the corresponding
     * style in the same position in the list of STYLES. Each style Name shall be one that was
     * defined in the <Name> element of a <Style> element that is either directly contained within,
     * or inherited by, the associated <Layer> element in Capabilities XML.
     * 
     * @return The required LAYERS
     */
    public Layer[] getLayers() {
        return this.layers.toArray( new Layer[this.layers.size()] );
    }

    /**
     * adds the <Layer>
     * 
     * @param layer
     */
    protected void addLayers( Layer layer ) {
        this.layers.add( layer );
    }

    /**
     * sets the <Layer>
     * 
     * @param layers
     *            a set of layer
     */
    private void setLayers( Layer[] layers ) {
        this.layers = new ArrayList<Layer>( layers.length );
        this.layers.clear();
        if ( layers != null ) {
            this.layers = Arrays.asList( layers );
        }
    }

    /**
     * creates a <tt>PrintMapRequest</tt> from its XML representation as defined in the
     * specification.
     * 
     * @param root
     *            Element
     * @return an instance of <tt>PrintMapRequest</tt>
     * @throws InconsistentRequestException
     * @throws XMLParsingException
     */
    public static PrintMap create( Element root )
                            throws InconsistentRequestException, XMLParsingException {
        LOG.entering();

        LOG.logInfo( "Validating PrintMapRequest request." );
        // Validation
        if ( !root.getLocalName().equals( "PrintMap" ) ) {
            StringBuffer sb = new StringBuffer( 50 );
            sb.append( "Unable to create a 'PrintMapRequest' operation for node '" );
            sb.append( root.getLocalName() + "'. Please check the node to be parsed." );
            throw new InconsistentRequestException( sb.toString() );
        }
        // VERSION
        String version;
        try {
            version = XMLTools.getRequiredAttrValue( "version", null, root );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error parsing required attribute parameter 'Version'. "
                                           + e.getMessage() );
        }

        // LAYERS & STYLES
        List<Layer> layerList = new ArrayList<Layer>();
        List layerElements = null;
        try {
            layerElements = XMLTools.getNodes( root, "deegreewmps:Layers", nsContext );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error parsing required parameter 'Layer(s)'. "
                                           + e.getMessage() );
        }

        for ( int i = 0; i < layerElements.size(); i++ ) {
            Element layer = (Element) layerElements.get( i );
            List namedLayers = null;
            try {
                namedLayers = XMLTools.getNodes( layer, "sld:NamedLayer", nsContext );
                layerList = createLayers( namedLayers, layerList );
            } catch ( XMLParsingException e ) {
                throw new XMLParsingException( "Error parsing parameter 'NamedLayer'." );
            }
            List userLayers = null;
            try {
                userLayers = XMLTools.getNodes( layer, "sld:UserLayer", nsContext );
                layerList = createLayers( userLayers, layerList );
            } catch ( XMLParsingException e ) {
                throw new XMLParsingException( "Error parsing  parameter 'UserLayer'." );
            }
            if ( ( layerList == null ) || ( layerList.size() == 0 ) ) {
                throw new InconsistentRequestException( "Atleast one 'NamedLayer' or one "
                                                        + "'UserLayer' has to be specified." );
            }
        }
        Layer[] layers = layerList.toArray( new Layer[layerList.size()] );

        // BBOX
        Element bbox = null;
        String srsName = null;
        Envelope boundingBox = null;
        try {
            bbox = (Element) XMLTools.getNode( root, "gml:Envelope", nsContext );
            if ( bbox != null ) {
                try {
                    srsName = XMLTools.getAttrValue( bbox, "srsName" );
                    boundingBox = GMLGeometryAdapter.wrapBox( bbox );
                } catch ( InvalidGMLException e ) {
                    throw new XMLParsingException( "Error creating a bounding box for the "
                                                   + "'BBOX' parameter." );
                } catch (UnknownCRSException e) {
                    throw new InconsistentRequestException( e.getMessage() );
                }
            }
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error parsing optional parameter 'BoundingBox'. "
                                           + e.getMessage() );
        }

        // Center
        Point center = null;
        try {
            Element centerElement = (Element) XMLTools.getNode( root, "deegreewmps:Center",
                                                                nsContext );
            if ( centerElement != null ) {
                try {
                    srsName = XMLTools.getAttrValue( centerElement, "srsName" );
                    center = (Point) GMLGeometryAdapter.wrap( centerElement );
                } catch ( GeometryException e ) {
                    throw new XMLParsingException( "Error creating a Point for the 'Center' "
                                                   + "parameter. " + e.getMessage() );
                }
            }
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error parsing optional parameter 'Center'. "
                                           + e.getMessage() );
        }

        // ScaleDenominator
        int scaleDenominator = -1;
        try {
            scaleDenominator = XMLTools.getNodeAsInt( root, "deegreewmps:ScaleDenominator",
                                                      nsContext, -1 );
        } catch ( XMLParsingException e ) {
            throw new XMLParsingException( "Error parsing optional parameter 'Center'. "
                                           + e.getMessage() );
        }

        if ( boundingBox == null ) {
            if ( center == null ) {
                throw new InconsistentRequestException( "Both 'BoundingBox' and 'Center' "
                                                        + "are not specified. Either of "
                                                        + "the two must be set. "
                                                        + "Both values cannot be null" );
            }
            if ( scaleDenominator == -1 ) {
                throw new InconsistentRequestException( "Scale Denominator must be "
                                                        + "specified if the Bounding "
                                                        + "Box has not been specified."
                                                        + " Please check the "
                                                        + "'SCALEDENOMINATOR' parameter." );
            }
        }

        // TRANSPARENT
        boolean transparent = XMLTools.getNodeAsBoolean( root, "deegreewmps:Transparent",
                                                         nsContext, false );

        // BGCOLOR
        Color bgColor = null;
        String colorstring = XMLTools.getNodeAsString( root, "deegreewmps:BGColor", nsContext, null );
        if ( colorstring == null ) {
            bgColor = Color.WHITE;
        } else {
            try {
                bgColor = Color.decode( colorstring );
            } catch ( Exception e ) {
                throw new InconsistentRequestException( "Error parsing 'BGCOLOR' "
                                                        + "parameter. The color '" + colorstring
                                                        + "' is not a hexadecimal "
                                                        + "definition of a valid color. "
                                                        + e.getMessage() );
            }
        }

        boolean legend = XMLTools.getNodeAsBoolean( root, "deegreewmps:Legend", nsContext, false );

        boolean scaleBar = XMLTools.getNodeAsBoolean( root, "deegreewmps:ScaleBar", nsContext,
                                                      false );

        String template = XMLTools.getNodeAsString( root, "deegreewmps:Template", nsContext,
                                                    "default" );

        String emailAdd = XMLTools.getNodeAsString( root, "deegreewmps:EMailAddress", nsContext,
                                                    null );

        List list = XMLTools.getNodes( root, "deegreewmps:TextAreas/deegreewmps:TextArea",
                                       nsContext );

        TextArea[] textAreas = null;
        String title = null;
        String copyright = null;
        String note = null;
        if ( list != null ) {
            textAreas = new TextArea[list.size()];
            for ( int i = 0; i < list.size(); i++ ) {
                Node textArea = (Node) list.get( i );
                String name = XMLTools.getRequiredNodeAsString( textArea, "deegreewmps:Name",
                                                                nsContext );
                String value = XMLTools.getRequiredNodeAsString( textArea, "deegreewmps:Text",
                                                                 nsContext );
                if ( name.equalsIgnoreCase( "TITLE" ) ) {
                    title = value;
                }
                if ( name.equalsIgnoreCase( "COPYRIGHT" ) ) {
                    copyright = value;
                }
                if ( name.equalsIgnoreCase( "NOTE" ) ) {
                    note = value;
                }
                textAreas[i] = new TextArea( name, value );

            }
        }
        Map vendorSpecificParameter = getVendorSpecificParameter( root );

        String id = "" + System.currentTimeMillis();

        LOG.logInfo( "Created PrintMap request request with id '" + id + "'." );

        LOG.exiting();

        return new PrintMap( id, version, layers, srsName, boundingBox, center, scaleDenominator,
                             transparent, bgColor, title, copyright, legend, scaleBar, note,
                             template, emailAdd, setCurrentTime(), textAreas,
                             vendorSpecificParameter );

    }

    /**
     * Returns the vendorspecific parameters as a map. Currently handles only the 'session id'
     * 
     * @param root
     * @return Map
     */
    private static Map<String, String> getVendorSpecificParameter( Element root ) {

        LOG.entering();

        Map<String, String> vendorspecific = new HashMap<String, String>();

        String sessionID = XMLTools.getAttrValue( root, "sessionID" );
        LOG.logInfo( "SESSIONID " + sessionID );
        if ( sessionID != null ) {
            LOG.logInfo( "vendor specific parameter 'sessionid' retrieved" );
            vendorspecific.put( "SESSIONID", sessionID );
        }
        LOG.exiting();

        return vendorspecific;
    }

    /**
     * Create Layer objects for each of namedLayer and userLayer.
     * 
     * @param layerNodes
     * @param layers
     * @return List list of layer objects
     * @throws XMLParsingException
     */
    private static List<Layer> createLayers( List layerNodes, List<Layer> layers )
                            throws XMLParsingException {

        LOG.entering();

        if ( layerNodes != null ) {
            for ( int i = 0; i < layerNodes.size(); i++ ) {
                Node layerNode = (Node) layerNodes.get( i );
                try {
                    String layerName = XMLTools.getRequiredNodeAsString( layerNode, "sld:Name",
                                                                         nsContext );
                    String styleName = XMLTools.getRequiredNodeAsString( layerNode,
                                                                         "sld:NamedStyle/sld:Name",
                                                                         nsContext );
                    layers.add( new Layer( layerName, styleName ) );
                } catch ( XMLParsingException e ) {
                    throw new XMLParsingException( "Error creating a Layer from the Node '"
                                                   + layerNode.getNodeName() + "'. "
                                                   + e.getMessage() );
                }
            }
        }

        LOG.exiting();

        return layers;
    }

    /**
     * creates a <tt>PrintMapRequest</tt> from a <tt>HashMap</tt> that contains the request
     * parameters as key-value-pairs. Keys are expected to be in upper case notation.
     * 
     * @param model
     *            <tt>HashMap</tt> containing the request parameters
     * @return an instance of <tt>PrinttMapRequest</tt>
     * @throws InconsistentRequestException
     */
    public static PrintMap create( Map model )
                            throws InconsistentRequestException {
        LOG.entering();

        retrieveRequestParameter( model );

        String version = retrieveVersionParameter( model );

        Layer[] layers = retrieveLayerAndStyleParameters( model );

        String srs = retrieveSRSParameter( model );

        Envelope boundingBox = null;
        Point center = null;
        try {
            boundingBox = retrieveBBOXParameter( model, srs );
            center = retrieveCenterParameter( model, srs );
        } catch ( UnknownCRSException e ) {
            throw new InconsistentRequestException( e.getMessage() );
        }
        

        int scaleDenominator = retrieveScaleDenominatorParameter( model );

        if ( boundingBox == null ) {
            if ( center == null ) {
                throw new InconsistentRequestException( "Both 'BoundingBox' and 'Center' "
                                                        + "are not specified. Either of the 2 "
                                                        + "must be set. "
                                                        + "Both values cannot be null" );
            }
            if ( scaleDenominator == -1 ) {
                throw new InconsistentRequestException( "Scale Denominator must be specified "
                                                        + "if the Bounding Box has not been "
                                                        + "specified. Please check the "
                                                        + "'SCALEDENOMINATOR' parameter." );
            }
        }

        boolean transparent = retrieveTransparentParameter( model );

        Color bgColor = retrieveBGColorParameter( model );

        String title = retrieveTitleParameter( model );

        String copyRightNote = retrieveCopyrightParameter( model );

        boolean legend = retrieveLegendParameter( model );

        boolean scaleBar = retrieveScaleBarParameter( model );

        String note = retrieveNoteParameter( model );

        String template = retrieveTemplateParameter( model );

        String emailaddress = retrieveEmailParameter( model );

        TextArea[] textAreas = retrieveTextAreas( model );

        HashMap vendorSpecificParameter = (HashMap) ( (HashMap) model ).clone();

        String id = "" + System.currentTimeMillis();

        LOG.exiting();

        return new PrintMap( id, version, layers, srs, boundingBox, center, scaleDenominator,
                             transparent, bgColor, title, copyRightNote, legend, scaleBar, note,
                             template, emailaddress, setCurrentTime(), textAreas,
                             vendorSpecificParameter );

    }

    /**
     * Retrieve the Text Areas to be displayed on the PDF output file. Extract the comma seperated
     * list of name, value pairs. The name and value should be seperated with a ':'. E.g.->
     * name:value,name:value,name:value
     * 
     * @param model
     * @return TextArea
     */
    private static TextArea[] retrieveTextAreas( Map model ) {

        LOG.entering();
        List<TextArea> texts = new ArrayList<TextArea>();
        if ( model.containsKey( "TEXTAREAS" ) ) {
            String textstring = (String) model.remove( "TEXTAREAS" );
            if ( textstring != null ) {
                String[] nameValue = StringTools.toArray( textstring, ",", true );
                if ( nameValue != null ) {
                    for ( int i = 0; i < nameValue.length; i++ ) {
                        String tmp = nameValue[i].trim();
                        int idx = tmp.indexOf( ":" );
                        if ( idx != -1 ) {
                            String name = tmp.substring( 0, idx );
                            String value = tmp.substring( idx + 1, tmp.length() );
                            if ( ( name != null ) && ( value != null ) ) {
                                TextArea area = new TextArea( name.toUpperCase(), value );
                                texts.add( area );
                            }
                        }
                    }
                }
            }
        }
        LOG.exiting();
        return texts.toArray( new TextArea[texts.size()] );

    }

    /**
     * Parse 'Template' Parameter.
     * 
     * @param model
     * @return String
     * @throws InconsistentRequestException
     */
    private static String retrieveTemplateParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();
        String templatestring = null;
        if ( model.containsKey( "TEMPLATE" ) ) {
            templatestring = (String) model.remove( "TEMPLATE" );

        }
        if ( templatestring == null ) {
            throw new InconsistentRequestException( "No Template defined. A Template name "
                                                    + "has to be specified "
                                                    + "along with the 'PrintMap' request." );
        }
        LOG.exiting();
        return templatestring;
    }

    /**
     * Retrieve Email parameter
     * 
     * @param model
     * @return String
     * @throws InconsistentRequestException
     */
    private static String retrieveEmailParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();

        String email = null;
        if ( model.containsKey( "EMAIL" ) ) {
            email = (String) model.remove( "EMAIL" );
        }

        if ( email == null ) {
            throw new InconsistentRequestException( "EMail parameter must be set." );
        }
        LOG.exiting();
        return email;
    }

    /**
     * Parse 'Note' Parameter.
     * 
     * @param model
     * @return String
     */
    private static String retrieveNoteParameter( Map model ) {

        LOG.entering();

        String note = null;
        if ( model.containsKey( "NOTE" ) ) {
            note = (String) model.remove( "NOTE" );
        }

        LOG.exiting();
        return note;
    }

    /**
     * Parse 'ScaleBar' Parameter.
     * 
     * @param model
     * @return boolean
     * @throws InconsistentRequestException
     */
    private static boolean retrieveScaleBarParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();
        boolean showScaleBar = false;
        if ( model.containsKey( "SCALEBAR" ) ) {
            String scaleBar = (String) model.remove( "SCALEBAR" );
            if ( scaleBar == null ) {
                showScaleBar = false;
            } else if ( scaleBar.equalsIgnoreCase( "True" ) ) {
                showScaleBar = true;
            } else if ( scaleBar.equalsIgnoreCase( "False" ) ) {
                showScaleBar = false;
            } else {
                throw new InconsistentRequestException( "The 'ScaleBar' parameter can "
                                                        + "only have 'True', 'False' "
                                                        + "values. Here it is '" + scaleBar + "'." );
            }
        }
        LOG.exiting();
        return showScaleBar;
    }

    /**
     * Parse 'Legend' Parameter.
     * 
     * @param model
     * @return boolean
     * @throws InconsistentRequestException
     */
    private static boolean retrieveLegendParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();
        boolean showLegend = false;
        if ( model.containsKey( "LEGEND" ) ) {
            String legend = (String) model.remove( "LEGEND" );
            if ( legend == null ) {
                showLegend = false;
            } else if ( legend.equalsIgnoreCase( "True" ) ) {
                showLegend = true;
            } else if ( legend.equalsIgnoreCase( "False" ) ) {
                showLegend = false;
            } else {
                throw new InconsistentRequestException( "The 'Legend' parameter can "
                                                        + "only have 'True', 'False' values. "
                                                        + "Here it is '" + legend + "'." );
            }
        }
        LOG.exiting();
        return showLegend;

    }

    /**
     * Parse 'Copyright' Parameter.
     * 
     * @param model
     * @return String
     */
    private static String retrieveCopyrightParameter( Map model ) {

        LOG.entering();

        String copyright = null;
        if ( model.containsKey( "COPYRIGHT" ) ) {
            copyright = (String) model.remove( "COPYRIGHT" );
        }
        LOG.exiting();
        return copyright;
    }

    /**
     * Parse 'Title' Parameter.
     * 
     * @param model
     * @return String
     */
    private static String retrieveTitleParameter( Map model ) {

        LOG.entering();

        String title = null;
        if ( model.containsKey( "TITLE" ) ) {
            title = (String) model.remove( "TITLE" );
        }

        LOG.exiting();
        return title;
    }

    /**
     * Parse 'BGColor' Parameter.
     * 
     * @param model
     * @return Color
     * @throws InconsistentRequestException
     */
    private static Color retrieveBGColorParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();
        Color bgColor = Color.WHITE;
        if ( model.containsKey( "BGCOLOR" ) ) {
            String colorstring = (String) model.remove( "BGCOLOR" );
            if ( ( colorstring != null ) || ( colorstring == "" ) ) {
                try {
                    bgColor = Color.decode( colorstring );
                } catch ( Exception e ) {
                    throw new InconsistentRequestException( "Error parsing 'BGCOLOR' parameter. "
                                                            + "The color '" + colorstring
                                                            + "' is not a hexadecimal "
                                                            + "definition of a valid color. "
                                                            + e.getMessage() );
                }
            }
        }
        LOG.exiting();
        return bgColor;
    }

    /**
     * Parse 'Transparent' Parameter.
     * 
     * @param model
     * @return boolean
     * @throws InconsistentRequestException
     */
    private static boolean retrieveTransparentParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();
        boolean isTransparent = false;
        if ( model.containsKey( "TRANSPARENT" ) ) {
            String transparent = (String) model.remove( "TRANSPARENT" );
            if ( transparent == null ) {
                isTransparent = false;
            } else if ( transparent.equalsIgnoreCase( "True" ) ) {
                isTransparent = true;
            } else if ( transparent.equalsIgnoreCase( "False" ) ) {
                isTransparent = false;
            } else {
                throw new InconsistentRequestException(
                                                        "The 'Transparent' parameter can only have "
                                                                                + "'True', 'False' values. Here it has '"
                                                                                + transparent
                                                                                + "'." );
            }
        }
        LOG.exiting();
        return isTransparent;
    }

    /**
     * Parse 'ScaleDenominator' Parameter.
     * 
     * @param model
     * @return String
     */
    private static int retrieveScaleDenominatorParameter( Map model ) {

        LOG.entering();
        int scale = -1;
        if ( model.containsKey( "SCALE" ) ) {
            String value = (String) model.remove( "SCALE" );
            if ( value != null ) {
                scale = Integer.parseInt( value );
            }
        }

        LOG.exiting();
        return scale;

    }

    /**
     * Parse 'Center' Parameter and create a point.
     * 
     * @param model
     * @param srs
     * @return Point to represent the x,y coordinates.
     * @throws InconsistentRequestException
     * @throws UnknownCRSException 
     */
    private static Point retrieveCenterParameter( Map model, String srs )
                            throws InconsistentRequestException, UnknownCRSException {

        LOG.entering();
        Point center = null;
        if ( model.containsKey( "CENTER" ) ) {
            String centerstring = (String) model.remove( "CENTER" );

            String[] values = centerstring.split( "," );
            if ( values.length != 2 ) {
                throw new InconsistentRequestException( "Centre should be defined as a Point "
                                                        + "with 'X' and 'Y' "
                                                        + "values. The current length is '"
                                                        + values.length + "'. It should "
                                                        + "be '2'." );
            }
            double x = -1;
            double y = -1;
            try {
                x = Double.parseDouble( values[0] );
                y = Double.parseDouble( values[1] );
            } catch ( Exception e ) {
                throw new InconsistentRequestException( "Error converting 'X','Y' "
                                                        + "coordinates in the request "
                                                        + "parameter 'CENTER' " + "to double."
                                                        + " Please check the " + "values entered." );
            }

            CoordinateSystem crs = CRSFactory.create( srs );
            center = GeometryFactory.createPoint( x, y, crs );

        }
        LOG.exiting();
        return center;
    }

    /**
     * Parse 'Envelope' Parameter and create an envelope.
     * 
     * @param model
     * @param srs
     * @return Envelope
     * @throws InconsistentRequestException
     * @throws InvalidFormatException
     * @throws UnknownCRSException 
     */
    private static Envelope retrieveBBOXParameter( Map model, String srs )
                            throws InconsistentRequestException, InvalidFormatException, UnknownCRSException {

        LOG.entering();

        Envelope bbox = null;

        if ( model.containsKey( "BBOX" ) ) {
            String boxstring = (String) model.remove( "BBOX" );
            StringTokenizer st = new StringTokenizer( boxstring, "," );
            String s = st.nextToken().replace( ' ', '+' );
            double minx = Double.parseDouble( s );
            s = st.nextToken().replace( ' ', '+' );
            double miny = Double.parseDouble( s );
            s = st.nextToken().replace( ' ', '+' );
            double maxx = Double.parseDouble( s );
            s = st.nextToken().replace( ' ', '+' );
            double maxy = Double.parseDouble( s );

            if ( minx >= maxx ) {
                throw new InvalidFormatException( "minx must be lesser than maxx" );
            }

            if ( miny >= maxy ) {
                throw new InvalidFormatException( "miny must be lesser than maxy" );
            }

            CoordinateSystem crs = CRSFactory.create( srs );
            bbox = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, crs );
        }

        LOG.exiting();
        return bbox;
    }

    /**
     * Parse 'SRS' Parameter.
     * 
     * @param model
     * @return String
     * @throws InconsistentRequestException
     */
    private static String retrieveSRSParameter( Map model )
                            throws InconsistentRequestException {
        LOG.entering();

        String srs = null;
        if ( model.containsKey( "SRS" ) ) {
            srs = (String) model.remove( "SRS" );
        }
        if ( srs == null ) {
            throw new InconsistentRequestException( "SRS-value must be set" );
        }
        LOG.exiting();
        return srs;
    }

    /**
     * Parse 'Layer' and 'Style' Parameter.
     * 
     * @param model
     * @return Layer[]
     * @throws InconsistentRequestException
     */
    private static Layer[] retrieveLayerAndStyleParameters( Map model )
                            throws InconsistentRequestException {

        LOG.entering();

        String layersstring = null;
        if ( model.containsKey( "LAYERS" ) ) {
            layersstring = (String) model.remove( "LAYERS" );
        }
        String stylesstring = null;
        if ( model.containsKey( "STYLES" ) ) {
            stylesstring = (String) model.remove( "STYLES" );
        }

        // normalize styles parameter
        if ( ( stylesstring == null ) || stylesstring.trim().equals( "" ) ) {
            stylesstring = "$DEFAULT";
        }
        if ( stylesstring.startsWith( "," ) ) {
            stylesstring = "$DEFAULT" + stylesstring;
        }
        String tmp = ",$DEFAULT,";
        stylesstring = StringTools.replace( stylesstring, ",,", tmp, true );
        if ( stylesstring.endsWith( "," ) ) {
            stylesstring = stylesstring + "$DEFAULT";
        }
        List<String> layers = new ArrayList<String>();
        List<String> styles = new ArrayList<String>();
        GetMap.Layer[] ls = null;
        if ( ( layersstring != null ) && !layersstring.trim().equals( "" ) ) {
            StringTokenizer st = new StringTokenizer( layersstring, "," );
            int a = 0;
            while ( st.hasMoreTokens() ) {
                String s = st.nextToken();
                layers.add( s );
            }
            st = new StringTokenizer( stylesstring, "," );
            for ( int i = 0; i < layers.size(); i++ ) {
                styles.add( "$DEFAULT" );
            }
            a = 0;
            while ( st.hasMoreTokens() ) {
                String s = st.nextToken();
                styles.set( a++, s );
            }

            // At last, build up the Layer object
            ls = new GetMap.Layer[layers.size()];

            for ( int i = 0; i < layers.size(); i++ ) {
                try {
                    String l = URLDecoder.decode( layers.get( i ),
                                                  CharsetUtils.getSystemCharset() );
                    ls[i] = PrintMap.createLayer( l, styles.get( i ) );
                } catch ( UnsupportedEncodingException e2 ) {
                    e2.printStackTrace();
                }
            }
        }

        if ( ( ls == null || ls.length == 0 ) ) {
            throw new InconsistentRequestException( "No layers defined in PrintMapRequest request" );
        }
        LOG.exiting();
        return ls;
    }

    /**
     * Parse the Request parameter to check if the request is actually a 'PrintMapRequest' request
     * 
     * @param model
     * @throws InconsistentRequestException
     */
    private static void retrieveRequestParameter( Map model )
                            throws InconsistentRequestException {

        LOG.entering();

        String request = null;
        if ( model.containsKey( "REQUEST" ) ) {
            request = (String) model.remove( "REQUEST" );
        } else {
            throw new InconsistentRequestException( "Unable to create a PrintMapRequest "
                                                    + "operation. "
                                                    + "The request parameter is missing." );
        }
        if ( request == null || !( request.equals( "PrintMap" ) ) ) {
            throw new InconsistentRequestException( "Unable to create a PrintMapRequest "
                                                    + "operation for request '" + request + "'." );
        }
        LOG.exiting();
    }

    /**
     * Parse 'Version' Parameter.
     * 
     * @param model
     * @return String version (default=1.0.0)
     */
    private static String retrieveVersionParameter( Map model ) {

        LOG.entering();
        String version = null;
        if ( model.containsKey( "VERSION" ) ) {
            version = (String) model.remove( "VERSION" );
        }
        if ( version == null ) {
            /** default value set as per the WMPS draft specifications. */
            version = "1.0.0";
        }
        LOG.exiting();
        return version;
    }

    /**
     * Get TimeStamp
     * 
     * @return TimeStamp
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    /**
     * Get SRS
     * 
     * @return String
     */
    public String getSRS() {
        return this.srs;
    }

    /**
     * Get bounding box
     * 
     * @return Envelope maybe null
     */
    public Envelope getBBOX() {
        return this.boundingBox;
    }

    /**
     * Get center
     * 
     * @return Point maybe null
     */
    public Point getCenter() {
        return this.center;
    }

    /**
     * Get ScaleDenominator
     * 
     * @return String maybe null
     */
    public int getScaleDenominator() {
        return this.scaleDenominator;
    }

    /**
     * Get Transparency
     * 
     * @return boolean
     */
    public boolean getTransparent() {
        return this.transparent;
    }

    /**
     * Get BGColor
     * 
     * @return Color default is White.
     */
    public Color getBGColor() {
        return this.bgColor;
    }

    /**
     * Get Map Title
     * 
     * @return String maybe null
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Get Copyright
     * 
     * @return String maybe null
     */
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get Legend
     * 
     * @return boolean
     */
    public boolean getLegend() {
        return this.legend;
    }

    /**
     * Get Scale Bar
     * 
     * @return boolean
     */
    public boolean getScaleBar() {
        return this.scaleBar;
    }

    /**
     * Get Note (extra descriptive text)
     * 
     * @return String maybe null
     */
    public String getNote() {
        return this.note;
    }

    /**
     * Get Template
     * 
     * @return String
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * Get Email Address
     * 
     * @return String
     */
    public String getEmailAddress() {
        return this.emailaddress;
    }

    /**
     * Get Text Areas
     * 
     * @return TextArea[]
     */
    public TextArea[] getTextAreas() {
        return this.textAreas;
    }

    /**
     * Retrieve ( if available ) the current text area identified by 'name' from the list of text
     * areas defined. May return null.
     * 
     * @param name
     * @return TextArea
     */
    public TextArea getTextArea( String name ) {

        LOG.entering();

        TextArea textArea = null;
        if ( this.textAreas != null && this.textAreas.length > 0 ) {
            for ( int i = 0; i < this.textAreas.length; i++ ) {
                TextArea tmp = this.textAreas[i];
                if ( tmp.getName().equalsIgnoreCase( name ) ) {
                    textArea = tmp;
                    break;
                }
            }
        }
        LOG.exiting();

        return textArea;
    }

    /**
     * creates a Layer object beacuse of the inner class construct.
     * 
     * @param name
     *            the name of the layer
     * @param style
     *            the corresponding style of the layer
     * @return Layer a layer object constaining name and style
     */
    public static Layer createLayer( String name, String style ) {
        return new Layer( name, style );
    }

    /**
     * Returns a new PrintMap instance.
     * 
     * @param id
     * @param version
     * @param layers
     * @param srs
     * @param bbox
     * @param center
     * @param scaleDenominator
     * @param transparent
     * @param bgColor
     * @param title
     * @param copyright
     * @param legend
     * @param scaleBar
     * @param note
     * @param template
     * @param emailAddress
     * @param timestamp
     * @param textAreas
     * @param vendorSpecificParameters
     * @return PrintMap
     */
    public static PrintMap create( String id, String version, Layer[] layers, String srs,
                                  Envelope bbox, Point center, int scaleDenominator,
                                  boolean transparent, Color bgColor, String title,
                                  String copyright, boolean legend, boolean scaleBar, String note,
                                  String template, String emailAddress, Timestamp timestamp,
                                  TextArea[] textAreas, Map vendorSpecificParameters ) {

        return new PrintMap( id, version, layers, srs, bbox, center, scaleDenominator, transparent,
                             bgColor, title, copyright, legend, scaleBar, note, template,
                             emailAddress, timestamp, textAreas, vendorSpecificParameters );
    }

    /**
     * Overwrite the toString() method to export the current request as a readable statement.
     * Currently only the id, version and layer names will be given out. TODO the rest
     * 
     * @return String
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer( 200 );
        sb.append( "id: " );
        sb.append( getId() );
        sb.append( "\n" );
        sb.append( "version: " );
        sb.append( getVersion() );
        sb.append( "\n" );
        if ( this.layers != null ) {
            sb.append( "layer(s): " );
            for ( int i = 0; i < this.layers.size(); i++ ) {
                sb.append( this.layers.get( i ).getName() );
                sb.append( "\n" );
            }
        }
        return sb.toString();
    }
}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: PrintMap.java,v $
 * Changes to this class. What the people have been up to: Revision 1.30  2006/11/27 09:07:52  poth
 * Changes to this class. What the people have been up to: JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.29  2006/10/17 20:31:19  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.28  2006/09/13 07:37:58  deshmukh
 * Changes to this class. What the people have been up to: removed excess debug statements.
 * Changes to this class. What the people have been up to: Changes to this
 * class. What the people have been up to: Revision 1.27 2006/09/11 08:34:14 deshmukh Changes to
 * this class. What the people have been up to: send document mail link as html. Changes to this
 * class. What the people have been up to: Changes to this class. What the people have been up to:
 * Revision 1.26 2006/09/04 11:32:25 deshmukh Changes to this class. What the people have been up
 * to: comments added Changes to this class. What the people have been up to: Changes to this class.
 * What the people have been up to: Revision 1.25 2006/08/10 07:11:35 deshmukh Changes to this
 * class. What the people have been up to: WMPS has been modified to support the new configuration
 * changes and the excess code not needed has been replaced. Changes to this class. What the people
 * have been up to: Changes to this class. What the people have been up to: Revision 1.24 2006/07/31
 * 11:21:07 deshmukh Changes to this class. What the people have been up to: wmps implemention...
 * Changes to this class. What the people have been up to: Changes to this class. What the people
 * have been up to: Revision 1.23 2006/07/21 14:49:01 deshmukh Changes to this class. What the
 * people have been up to: Added vendor specific parameters for the wmps Changes to this class. What
 * the people have been up to: Revision 1.22 2006/07/20 13:24:12 deshmukh Removed a few floating
 * bugs.
 * 
 * Revision 1.21 2006/07/12 14:46:18 poth comment footer added
 * 
 **************************************************************************************************/
