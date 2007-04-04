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

package org.deegree.tools.legend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.Encoders;
import org.deegree.graphics.legend.LegendElement;
import org.deegree.graphics.legend.LegendException;
import org.deegree.graphics.legend.LegendFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserLayer;
import org.deegree.graphics.sld.UserStyle;

/**
 * This executable class is an application, which reads out an sld-document,
 * creates the corresponding legend-elements and saves them as an image.
 * The class can be executed from the console. Details of use can be requested
 * with <tt>java LegendElementCreator --help</tt>
 * <pre>
 usage: java LegendConsole [-f sld-file -d directory
                            -g format -c color -w width -h height -t title]
                           [--version] [--help]

 mandatory arguments:
     -f file     reads the SLD inputfile.
     -d outdir   name of the directory to save the results in.

 optional arguments:
     -g format   graphics format of the output (default=png).
                 possible values are: bmp, gif, jpg, png, tif
     -c color    background-color (default=white)
                 possible values are: TRANSPARENT (if supported by the
                 graphics-format '-g'), black, blue, cyan, dark_gray, gray,
                 green, light_gray, magenta, orange, pink, red, white, yellow
                 or the hexadecimal codes (i.e. #ffffff for white).
     -w width    width (in pixel) of the legendsymbol (default=40).
     -h height   height (in pixel) of the legendsymbol (default=40).
     -t title    optional title for the legend-element. (no default).
                 If more than one word, put the title in "quotation marks".

 information options:
     --help      shows this help.
     --version   shows the version and exits.
  </pre>
 * <hr>
 * @version
 * @author <a href="schaefer@lat-lon.de">Axel Schaefer</a>
 */
public class LegendElementCreator {
    
    private static final ILogger LOG = LoggerFactory.getLogger( LegendElementCreator.class );

    String verbose_output = "";
    LecGUI lecgui = null;

    /**
     * 
     */
    public LegendElementCreator(String sldfile,String directory, String format,
                                Color color, int width, int height,String title,
                                LecGUI lec) throws LegendException {

        this.lecgui = lec;

        StringBuffer sb = new StringBuffer(100);

        // read out the SLD
        HashMap stylemap = null;
        try {
            stylemap = loadSLD(sldfile);
        } catch (IOException ioe) {
            throw new LegendException(
                "An error (IOException) occured in processing the SLD-File:\n"
                    + sldfile + "\n" + ioe);
        } catch (XMLParsingException xmlpe) {
            throw new LegendException("An error (XMLParsingException) occured in parsing " +
                    "the SLD-File:\n" + sldfile + "\n" + xmlpe.getMessage());
        }

        // output
        LegendFactory lf = new LegendFactory();
        LegendElement le = null;
        BufferedImage buffi = null;

        Iterator iterator = stylemap.entrySet().iterator();
        String filename = null;
        AbstractStyle style = null;
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            Map.Entry entry = (Map.Entry) iterator.next();
            filename = ((String) entry.getKey()).replace(':', '_');
            style = (AbstractStyle) entry.getValue();

            try {
                le = lf.createLegendElement(style, width, height, title);
                buffi = le.exportAsImage( MimeTypeMapper.toMimeType( format ));
                saveImage(buffi, directory, filename, format, color);
                sb.append(
                    "- Image "
                        + filename
                        + "."
                        + format
                        + " in "
                        + directory
                        + " saved.\n");
            } catch (LegendException lex) {
                throw new LegendException(
                    "An error (LegendException) occured during the creating\n"
                        + "of the LegendElement "
                        + filename
                        + ":\n"
                        + lex);
            } catch (IOException ioex) {
                throw new LegendException(
                    "An error (IOException) occured during the creating/saving\n"
                        + "of the output-image "
                        + filename
                        + ":\n"
                        + ioex);
            } catch (Exception ex) {
                throw new LegendException(
                    "A general error (Exception) occured during the creating/saving\n"
                        + "of the output-image "
                        + filename
                        + ":\n"
                        + ex);
            }

        }
        setVerboseOutput(sb.toString());
    }

    /**
     * 
     * @return
     */
    public String getVerboseOutput() {
        return this.verbose_output;
    }

    public void setVerboseOutput(String vo) {
        if (vo != null) {
            this.verbose_output = vo;
        }
    }

    /**
     * loads the sld-document, parses it an returns a HashMap containing the
     * different styles. 
     * @param sldFile the file containing the StyledLayerDescriptor
     * @return HashMap containing the styles of the SLD.
     * @throws IOException if the SLD-document cant be read/found in the filesystem
     * @throws XMLParsingException if an error occurs during the parsing of the sld-document
     */
    static private HashMap loadSLD(String sldFile) throws IOException, XMLParsingException {
        AbstractStyle[] styles = null;

        File file = new File( sldFile );
        StyledLayerDescriptor sld = SLDFactory.createSLD( file.toURL() );
        
        HashMap map = new HashMap();

        // NAMED LAYER
        NamedLayer[] namedlayers = sld.getNamedLayers();
        for (int i = 0; i < namedlayers.length; i++) {
            styles = namedlayers[i].getStyles();
            for (int j = 0; j < styles.length; j++) {
                if (styles[j] instanceof UserStyle) {
                    map.put(styles[j].getName(), styles[j]);
                }
            }
        }

        // USER LAYER
        UserLayer[] userLayers = sld.getUserLayers();
        for (int k = 0; k < userLayers.length; k++) {
            styles = userLayers[k].getStyles();
            for (int l = 0; l < styles.length; l++) {
                if (styles[l] instanceof UserStyle) {
                    map.put(styles[l].getName(), styles[l]);
                }
            }
        }
        return map;
    }

    /**
     * saves the resulting buffered Image from org.deegree.graphics.legend as
     * an image.
     * @param bi the BufferedImage from org.deegree.graphics.legend.*
     * @param outdir the output-directory (application-parameter)
     * @param filename the output-filename (from the styles of the SLD)
     * @param graphicsformat the output-graphicsformat (application-parameter)
     * @throws IOException if saving fails.
     * @throws Exception if the graphic-encoder can't be found.
     */
    private void saveImage(
        BufferedImage bi,
        String outdir,
        String filename,
        String graphicsformat,
        Color color)
        throws LegendException, IOException, Exception {

        File file = new File(outdir, filename + "." + graphicsformat);
        FileOutputStream fos = new FileOutputStream(file);

        // PNG
        if (graphicsformat.equalsIgnoreCase("PNG")) {

            BufferedImage outbi =
                new BufferedImage(
                    bi.getWidth(),
                    bi.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = outbi.getGraphics();
            g.drawImage(bi, 0, 0, color, null);
            Encoders.encodePng(fos, outbi);
            // BMP
        } else if (graphicsformat.equalsIgnoreCase("BMP")) {
            BufferedImage outbi =
                new BufferedImage(
                    bi.getWidth(),
                    bi.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);

            Graphics g = outbi.getGraphics();
            // transparency 
            if (color == null) {
                this.lecgui.addDebugInformation(
                    "BMP-NOTIFY:\n"
                        + "Requested transparency (transp.) isn't available for BMP-images.\n"
                        + "Using default background color WHITE.\n");
                color = Color.WHITE;
            }
            g.drawImage(bi, 0, 0, color, null);
            Encoders.encodeBmp(fos, outbi);
            // GIF
        } else if (graphicsformat.equalsIgnoreCase("GIF")) {
            BufferedImage outbi =
                new BufferedImage(
                    bi.getWidth(),
                    bi.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = outbi.getGraphics();
            g.drawImage(bi, 0, 0, color, null);
            Encoders.encodeGif(fos, outbi);
            // JPEG
        } else if (
            graphicsformat.equalsIgnoreCase("JPEG")
                || graphicsformat.equalsIgnoreCase("JPG")) {
            BufferedImage outbi =
                new BufferedImage(
                    bi.getWidth(),
                    bi.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = outbi.getGraphics();

            // transparency
            if (color == null) {
                this.lecgui.addDebugInformation(
                    "JPEG-NOTIFY:\n"
                        + "Requested transparency (transp.) isn't available for JPG-images.\n"
                        + "Using default background color WHITE.\n");
                color = Color.WHITE;
            }

            g.drawImage(bi, 0, 0, color, null);
            Encoders.encodeJpeg(fos, outbi, 1f);
            // TIFF
        } else if (
            graphicsformat.equalsIgnoreCase("TIFF")
                || graphicsformat.equalsIgnoreCase("TIF")) {
            BufferedImage outbi =
                new BufferedImage(
                    bi.getWidth(),
                    bi.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);
            Graphics g = outbi.getGraphics();
            g.drawImage(bi, 0, 0, color, null);
            Encoders.encodeTiff(fos, outbi);
        } else {
            throw new Exception(
                "Can't save output image because no graphic-encoder found for:\n"
                    + "filetype: '"
                    + graphicsformat
                    + "' for file: '"
                    + file
                    + "'");
        }
        LOG.logInfo( "-- " + file + " saved." );
    }

    /**
     * main-method for testing purposes
     */
    public static void main(String[] args) {

        String sldfile = "E:\\java\\source\\deegree\\style1.xml";
        String directory = "E:\\java\\source\\deegree\\output";
        String format = "PNG";
        Color color = Color.WHITE;
        int width = 40;
        int height = 40;
        String title = "Mein Titel Platzhalter Texttexttext";
        LecGUI lec = null;

        try {
            new LegendElementCreator( sldfile, directory, format, color, width,
                                      height, title, lec );
        } catch (LegendException e) {
            e.printStackTrace();
        }

        LOG.logInfo( "...finished" );
    }

}

/*
*****************************************************************************
   Changes to this class. What the people have been up to:
   $Log: LegendElementCreator.java,v $
   Revision 1.9  2006/07/14 13:48:03  taddei
   fix for mime type not being equal to img extension

   Revision 1.8  2006/04/06 20:25:24  poth
   *** empty log message ***

   Revision 1.7  2006/04/04 20:39:41  poth
   *** empty log message ***

   Revision 1.6  2006/03/30 21:20:25  poth
   *** empty log message ***

   Revision 1.5  2006/03/02 12:58:38  poth
   *** empty log message ***

   Revision 1.4  2005/12/06 13:45:20  poth
   System.out.println substituted by logging api

   Revision 1.3  2005/03/09 11:55:47  mschneider
   *** empty log message ***

   Revision 1.2  2005/02/18 20:54:18  poth
   no message

   Revision 1.2  2004/06/24 06:22:40  ap
   no message

   Revision 1.1  2004/06/14 08:01:26  ap
   no message

   Revision 1.1  2004/05/24 07:11:45  ap
   no message

   Revision 1.10  2004/04/07 10:58:29  axel_schaefer
   bugfix



**************************************************************************** */