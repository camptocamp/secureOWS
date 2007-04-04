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
package org.deegree.graphics.sld;

import java.util.ArrayList;

import org.deegree.framework.xml.Marshallable;


/**
 * StyledLayerDescriptor: This is a sequence of styled layers, represented at
 * the first level by Layer and UserLayer elements. A "version" attribute
 * has been added to allow the formatting of static-file
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a>
 * @version $Revision: 1.12 $ $Date: 2006/11/27 09:07:52 $
 */
public class StyledLayerDescriptor implements  Marshallable {
    private ArrayList layers = null;    
    private String version = null;
    private String abstract_ = null;
    private String name = null;
    private String title = null;
    
    /**
     * @param name
     * @param title
     * @param version
     * @param abstract_
     * @param layers
     */
    StyledLayerDescriptor(String name, String title, String version, 
    						   String abstract_, AbstractLayer[] layers) {
    	this.layers = new ArrayList(layers.length);        
    	setLayers( layers );
    	setVersion( version );
    	setAbstract(abstract_);
    	setName(name);
    	setTitle(title);
    }

    /**
     * constructor initializing the class with the <StyledLayerDescriptor>
     */
    public StyledLayerDescriptor( AbstractLayer[] layers, String version ) {
        this.layers = new ArrayList(layers.length);        
        setLayers( layers );
        setVersion( version );
    }

    /**
     * @return the Layers as Array
     */
    public AbstractLayer[] getLayers() {
        return (AbstractLayer[]) layers.toArray(new AbstractLayer[layers.size()]);
    }


    /**
     * Sets Layers
     * @param layers the Layers as Array
     */
    public void setLayers( AbstractLayer[] layers ) {
        this.layers.clear();

        if ( layers != null ) {
            for ( int i = 0; i < layers.length; i++ ) {
                this.layers.add( layers[i] );
            }
        }
    }

    /**
     * adds the <Layer>
     * @param layer a Layer to add
     */
    public void addLayer( AbstractLayer layer ) {
        layers.add( layer );
    }

    /**
     * removes the <Layer>
     * @param layer a Layer to remove
     */
    public void removeLayer( AbstractLayer layer ) {
        if ( layers.indexOf( layer ) != -1 ) {
            layers.remove( layers.indexOf( layer ) );
        }
    }

    /**
     * A UserLayer can contain one or more UserStyles. A UserLayer may direct the
     * WMS to a specified WFS source of feature data. Multiple feature types can
     * be included in a UserLayer, since this is semantically equivalent to a
     * Layer. All feature types of a UserLayer come from the same WFS. The
     * WFS can be named explicitly with the "wfs" attribute or it can be implied
     * by context.
     * @return the UserLayers as Array
     */
    public UserLayer[] getUserLayers() {
        ArrayList list = new ArrayList( layers.size() );
        for (int i = 0; i < layers.size(); i++) {
            if ( layers.get( i ) instanceof UserLayer ) {
                list.add( layers.get(i) );
            }
        }
        return (UserLayer[])list.toArray( new UserLayer[ list.size() ] );
    }

   /**
     * A NamedLayer uses the "name" attribute to identify a layer known to the
     * WMS and can contain zero or more styles, either NamedStyles or UserStyles.
     * In the absence of any styles the default style for the layer is used.
     * @return the NamedLayers as Array
     */
    public NamedLayer[] getNamedLayers() {
        ArrayList list = new ArrayList( layers.size() );
        for (int i = 0; i < layers.size(); i++) {
            if ( layers.get( i ) instanceof NamedLayer ) {
                list.add( layers.get(i) );
            }
        }
        return (NamedLayer[])list.toArray( new NamedLayer[ list.size() ] );
    }

    /**
     * The version attribute gives the SLD version of an SLD document, to
     * facilitate backward compatibility with static documents stored in various
     * different versions of the SLD spec. The string has the format x.y.z, the
     * same as in other OpenGIS Web Server specs. For example, an SLD document
     * stored according to this spec would have the version string 0.7.2.
     * @return the version of the SLD as String
     * 
     * @uml.property name="version"
     */
    public String getVersion() {
        return version;
    }

    /**
     * sets the <Version>
     * @param version the version of the SLD
     * 
     * @uml.property name="version"
     */
    public void setVersion(String version) {
        this.version = version;
    }


  	/**
	 * @return Returns the abstract_.
	 */
	public String getAbstract() {
		return abstract_;
	}

	/**
	 * @param abstract_ The abstract_ to set.
	 */
	public void setAbstract(String abstract_) {
		this.abstract_ = abstract_;
	}

    /**
     * @return Returns the name.
     * 
     * @uml.property name="name"
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     * 
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the title.
     * 
     * @uml.property name="title"
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set.
     * 
     * @uml.property name="title"
     */
    public void setTitle(String title) {
        this.title = title;
    }

	
	/**
	 * exports the content of the Font as XML formated String
	 *
	 * @return xml representation of the Font
	 */
	public String exportAsXML() {
		

		StringBuffer sb = new StringBuffer( 50000 );
        sb.append( "<?xml version='1.0' encoding='UTF-8'?>");
		sb.append( "<StyledLayerDescriptor version='" + version + "' " );
		sb.append( "xmlns='http://www.opengis.net/sld' " );
		sb.append( "xmlns:gml='http://www.opengis.net/gml' " );
		sb.append( "xmlns:ogc='http://www.opengis.net/ogc' " );
		sb.append( "xmlns:xlink='http://www.w3.org/1999/xlink' " );
		sb.append( "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>" );

		for ( int i = 0; i < layers.size(); i++ ) {
			sb.append( ( (Marshallable)layers.get( i ) ).exportAsXML() );
		}

		sb.append( "</StyledLayerDescriptor>" );

		
		return sb.toString();
	}
	

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: StyledLayerDescriptor.java,v $
Revision 1.12  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.11  2006/09/23 09:03:31  poth
reference to ISO-8859-1 substituted by UFT-8

Revision 1.10  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.9  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
