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
package org.deegree.portal.context;

import java.net.URL;

import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.StyledLayerDescriptor;


/**
 * This class encapsulates the descriptions of a SLD element as defined by the
 * OGC Web Map Context specification.
 * <p>
 * The &lt;SLD&gt; element must contain required &lt;Name&gt; and optional &lt;Title&gt; elements 
 * which identify the particular element of a Styled Layer Descriptor to be used 
 * for this style. The &lt;SLD&gt; element must then contain one of three alternative 
 * sources of description of a layer style:</p>
 * <p>
 * 1. an &lt;OnlineResource&gt; element describing a link to the specified SLD document.<p/>
 * &lt;OnlineResource xmlns:xlink="http://www.w3.org/TR/xlink" xlink:type="simple" 
 * xlink:href=�http://example.org/this/is/an/example/link/to/the/sld"&gt;</p>
 * <p>
 * This reference may be to a separately referenced SLD document or to an inline 
 * &lt;StyledLayerDescriptor&gt; in the same context document (which may define the 
 * styles for multiple layers within the Web Map Context)</p>
 * <p>
 * 2. &lt;StyledLayerDescriptor&gt; element containing inline the namedStyle or
 * userStyle named in the enclosing &lt;Style&gt; element</p>
 * <p>
 * 3. &lt;FeatureTypeStyle&gt; element containing inline the specific feature styling 
 * instructions for the enclosing &lt;Style&gt; element
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.8 $
 */
public class SLD  {
    private FeatureTypeStyle featureTypeStyle = null;
    private String name = null;
    private String title = null;
    private StyledLayerDescriptor styledLayerDescriptor = null;
    private URL onlineResource = null;

    /**
     * Creates a new SLD object.
     *
     * @param name name of the SLD
     * @param title title of the SLD
     */
    private SLD( String name, String title ) {
        setName( name );
        setTitle( title );
    }

    /**
     * Creates a new SLD object.
     *
     * @param name name of the SLD
     * @param title title of the SLD
     * @param styledLayerDescriptor complete StyledLayerDescriptor
     *
     * @throws ContextException 
     */
    public SLD( String name, String title, StyledLayerDescriptor styledLayerDescriptor )
        throws ContextException {
        this( name, title );
        setStyledLayerDescriptor( styledLayerDescriptor );
    }

    /**
     * Creates a new SLD object.
     *
     * @param name name of the SLD
     * @param title title of the SLD
     * @param onlineResource online resource where to access the StyledLayerDescriptor
     *
     * @throws ContextException 
     */
    public SLD( String name, String title, URL onlineResource ) throws ContextException {
        this( name, title );
        setOnlineResource( onlineResource );
    }

    /**
     * Creates a new SLD object.
     *
     * @param name name of the SLD
     * @param title title of the SLD
     * @param featureTypeStyle one concrete FeatureTypeStyle as part of a 
     *                         StyledLayerDescriptor
     *
     * @throws ContextException 
     */
    public SLD( String name, String title, FeatureTypeStyle featureTypeStyle )
        throws ContextException {
        this( name, title );
        setFeatureTypeStyle( featureTypeStyle );
    }

    /**
     * name of the SLD
     *
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * title of the SLD
     *
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * describing a link to the specified SLD document.
     *
     * @return 
     */
    public URL getOnlineResource() {
        return onlineResource;
    }

    /**
     * containing inline the specific feature styling instructions for the 
     * enclosing <code>&lt;Style&gt;</code> element
     *
     * @return 
     */
    public FeatureTypeStyle getFeatureTypeStyle() {
        return featureTypeStyle;
    }

    /**
     * inline the namedStyle or userStyle named in the enclosing &lt;Style&gt; element
     *
     * @return 
     */
    public StyledLayerDescriptor getStyledLayerDescriptor() {
        return styledLayerDescriptor;
    }

    /**
     * @see org.deegree.clients.context.SLD#getName()
     *
     * @param name 
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @see org.deegree.clients.context.Server#getTitle()
     *
     * @param title 
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * @see org.deegree.clients.context.Server#getOnlineResource()
     *
     * @param onlineResource 
     *
     * @throws ContextException 
     */
    public void setOnlineResource( URL onlineResource ) throws ContextException {
        if ( onlineResource == null ) {
            throw new ContextException( "onlineResource isn't allowed to be null" );
        }

        this.onlineResource = onlineResource;
    }

    /**
     * @see org.deegree.clients.context.SLD#getFeatureTypeStyle()
     *
     * @param featureTypeStyle 
     *
     * @throws ContextException 
     */
    public void setFeatureTypeStyle( FeatureTypeStyle featureTypeStyle ) throws ContextException {
        if ( featureTypeStyle == null ) {
            throw new ContextException( "featureTypeStyle isn't allowed to be null" );
        }

        this.featureTypeStyle = featureTypeStyle;
    }

    /**
     * @see org.deegree.clients.context.SLD#getStyledLayerDescriptor()
     *
     * @param styledLayerDescriptor 
     *
     * @throws ContextException 
     */
    public void setStyledLayerDescriptor( StyledLayerDescriptor styledLayerDescriptor )
                                  throws ContextException {
        if ( styledLayerDescriptor == null ) {
            throw new ContextException( "onlineResource isn't allowed to be null" );
        }

        this.styledLayerDescriptor = styledLayerDescriptor;
    }
  
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SLD.java,v $
Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
