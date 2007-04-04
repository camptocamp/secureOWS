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
package org.deegree.tools.wms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.xml.sax.SAXException;

/**
 * program for creating a Web Map Context document from a 
 * WMS capabilities file 
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/06/07 12:19:38 $
 *
 * @since 2.0
 */
public class WMS2WMC {

    private URL xsl = WMS2WMC.class.getResource( "wms2wmc.xsl" );

    private String inFile;

    private String outFile;

    /**
     * 
     * @param inFile
     * @param outFile
     */
    public WMS2WMC( String inFile, String outFile ) {
        this.inFile = inFile;
        this.outFile = outFile;
    }

    /**
     * does all the work
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    public void run()
                            throws MalformedURLException, IOException, SAXException,
                            TransformerException {
        File in = new File( inFile );
        XMLFragment xml = new XMLFragment( in.toURL() );
        XSLTDocument xslt = new XSLTDocument( xsl );
        xml = xslt.transform( xml );

        FileOutputStream fos = new FileOutputStream( new File( outFile ) );
        xml.write( fos );
        fos.close();
    }

    private static void validate( Properties map )
                            throws Exception {
        if ( map.get( "-inFile" ) == null ) {
            throw new Exception( "-inFile parameter must be set" );
        }
        if ( map.get( "-outFile" ) == null ) {
            throw new Exception( "-outFile parameter must be set" );
        }
    }

    public static void main( String[] args )
                            throws Exception {

        Properties map = new Properties();
        for ( int i = 0; i < args.length; i += 2 ) {
            System.out.println( args[i + 1] );
            map.put( args[i], args[i + 1] );
        }
        try {
            validate( map );
        } catch ( Exception e ) {
            System.out.println( "!!! E R R O R !!!" );
            System.out.println( e.getMessage() );
            return;
        }

        WMS2WMC wms2wmc = new WMS2WMC( map.getProperty( "-inFile" ), map.getProperty( "-outFile" ) );
        wms2wmc.run();

    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: WMS2WMC.java,v $
 Revision 1.1  2006/06/07 12:19:38  poth
 *** empty log message ***


 ********************************************************************** */