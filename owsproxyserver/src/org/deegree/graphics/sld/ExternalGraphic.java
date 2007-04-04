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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.NetWorker;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.Marshallable;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureProperty;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;


/**
 * The ExternalGraphic element allows a reference to be made to an external graphic
 * file with a Web URL. The OnlineResource sub-element gives the URL and the
 * Format sub-element identifies the expected document MIME type of a successful
 * fetch. Knowing the MIME type in advance allows the styler to select the best-
 * supported format from the list of URLs with equivalent content.
 * <p>----------------------------------------------------------------------</p>
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.15 $ $Date: 2006/07/29 08:51:11 $
 */
public class ExternalGraphic implements Marshallable {
  
    private BufferedImage image = null;
    private String format = null;
    private URL onlineResource = null;
    private TranscoderInput input = null;
    
    private ByteArrayOutputStream bos = null;
    private TranscoderOutput output = null;
    private Transcoder trc = null;

    /**
     * Creates a new ExternalGraphic_Impl object.
     *
     * @param format 
     * @param onlineResource 
     */
    ExternalGraphic( String format, URL onlineResource ) {
        setFormat( format );
        setOnlineResource( onlineResource );
    }

    /**
     * the Format sub-element identifies the expected document MIME type of a
     * successful fetch.
     * @return Format of the external graphic
     * 
     * @uml.property name="format"
     */
    public String getFormat() {
        return format;
    }

    /**
     * sets the format (MIME type)
     * @param format Format of the external graphic
     * 
     * @uml.property name="format"
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * The OnlineResource gives the URL of the external graphic
     *  @return URL of the external graphic
     * 
     * @uml.property name="onlineResource"
     */
    public URL getOnlineResource() {
        return onlineResource;
    }

    /**
     * sets the online resource / URL of the external graphic
     * @param onlineResource URL of the external graphic
     * 
     * @uml.property name="onlineResource"
     */
    public void setOnlineResource(URL onlineResource) {

        this.onlineResource = onlineResource;
        String file = onlineResource.getFile();
        int idx = file.indexOf( "$" );
        if ( idx == -1 ) {
            retrieveImage( onlineResource );
        }
    }
    
    /**
     * @param onlineResource
     */
    private void retrieveImage( URL onlineResource ) {
        
        try {
            String t = onlineResource.toExternalForm();
            if ( t.trim().toLowerCase().endsWith( ".svg" ) ) {
                // initialize the the classes required for svg handling
                bos = new ByteArrayOutputStream( 2000 );
                output = new TranscoderOutput( bos );
                // PNGTranscoder is needed to handle transparent parts
                // of a SVG
                trc = new PNGTranscoder();
                try {
                    input = new TranscoderInput( NetWorker.url2String( onlineResource ) );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                InputStream is = onlineResource.openStream();
                MemoryCacheSeekableStream mcss = new MemoryCacheSeekableStream( is );
                RenderedOp rop = JAI.create( "stream", mcss );
                image = rop.getAsBufferedImage();
                mcss.close();
                is.close();
            }
        } catch (IOException e) {
            System.out.println( "Yikes: " + e );
        }
    }

    /**
     * returns the external graphic as an image. this method is not part of the sld specifications
     * but it is added for speed up applications
     * 
     * @return the external graphic as BufferedImage
     */
    public BufferedImage getAsImage( int targetSizeX, int targetSizeY, Feature feature ) {

        if ( ( ( this.input == null ) && ( this.image == null ) ) || feature != null ) {
            URL onlineResource = initializeOnlineResource( feature );
            retrieveImage( onlineResource );
        }

        if ( image != null && image.getWidth() == targetSizeX && image.getHeight() == targetSizeY ) {
            
        } else {
            if ( input != null ) {
                if ( targetSizeX <= 0 )
                    targetSizeX = 0;
                if ( targetSizeY <= 0 )
                    targetSizeY = 0;
    
                trc.addTranscodingHint( PNGTranscoder.KEY_HEIGHT, new Float( targetSizeX ) );
                trc.addTranscodingHint( PNGTranscoder.KEY_WIDTH, new Float( targetSizeY ) );
                try {
                    trc.transcode( input, output );
                    try {
                        bos.flush();
                        bos.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                } catch (TranscoderException e) {
                    e.printStackTrace();
                }
                try {
                    ByteArrayInputStream is = new ByteArrayInputStream( bos.toByteArray() );
                    MemoryCacheSeekableStream mcss = new MemoryCacheSeekableStream( is );
                    RenderedOp rop = JAI.create( "stream", mcss );
                    image = rop.getAsBufferedImage();
                    mcss.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return image;
    }

    /**
     * @param feature
     */
    private URL initializeOnlineResource( Feature feature ) {

        String file = this.onlineResource.getFile();
        String[] tags = StringTools.extractStrings( file, "$", "$" );

        if ( tags != null ) {
            FeatureProperty[] properties = feature.getProperties();        
            for (int i = 0; i < tags.length; i++) {
                String tag = tags[i].substring( 1, tags[i].length() - 1 );
                for (int j = 0; j < properties.length; j++) {
                    FeatureProperty property = properties[j];
                    QualifiedName name = property.getName();                
                    if ( name.getLocalName().equals( tag ) ) {
                        String to = (String)property.getValue();                    
                        String replace = tags[i];
                        file = StringTools.replace( file, replace, to, true );
                    }
                }
            }
        }
        URL onlineResource = null;
        try {
            String protocol = this.onlineResource.getProtocol();
            String host = this.onlineResource.getHost();
            onlineResource = new URL( protocol, host, file );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return onlineResource;
    }
    
     
   /**
    * sets the external graphic as an image.
    * @param image the external graphic as BufferedImage 
    */ 
    public void setAsImage(BufferedImage image) {
        this.image = image;
    }
    
    /**
     * exports the content of the ExternalGraphic as XML formated String
     *
     * @return xml representation of the ExternalGraphic
     */
    public String exportAsXML() {
        
        StringBuffer sb = new StringBuffer(200);
        sb.append( "<ExternalGraphic>" );
        sb.append( "<OnlineResource xmlns:xlink='http://www.w3.org/1999/xlink' ");
        sb.append( "xlink:type='simple' xlink:href='" );
        sb.append( NetWorker.url2String( onlineResource ) + "'/>" );
        sb.append( "<Format>" ).append( format ).append( "</Format>" );
        sb.append( "</ExternalGraphic>" );
        return sb.toString();
    }
    
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ExternalGraphic.java,v $
Revision 1.15  2006/07/29 08:51:11  poth
references to deprecated classes removed

Revision 1.14  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
