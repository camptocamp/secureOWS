//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/conf/services/wmsconf/WMSConfConfiguration.java,v 1.11 2006/08/24 06:38:30 poth Exp $
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
 Aennchenstraße 19
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

package org.deegree.conf.services.wmsconf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.configuration.WMSConfigurationDocument;
import org.xml.sax.SAXException;


/**
 * ...
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class WMSConfConfiguration {
    
    private static WMSConfConfiguration INSTANCE; 
    
    private WMSCapabilitiesDocument wmsCapabilitiesDoc;
    
    private File stylesFile;
    
    public static WMSConfConfiguration getInstance(){
        if( INSTANCE == null ) {
          synchronized( WMSConfConfiguration.class ) {
              if( INSTANCE == null ) {
                  INSTANCE = new WMSConfConfiguration();
              }
         }
       }
       
        return INSTANCE;
    }
    
    private WMSConfConfiguration(){
        // empty
    }
    
    public void initFromUrl( URL configFile ){

        XMLFragment xmlFragment = new XMLFragment();

        try {
            xmlFragment.load( configFile );
            
            String file = XMLTools.getRequiredNodeAsString( 
                                    	    xmlFragment.getRootElement(), 
                                    	    "./WMSCapabilitiesFile", 
                                    	    CommonNamespaces.getNamespaceContext() );
            
            //File dummy = new File( configFile.getFile() );
                        
            File f = new File( file );
            URL url = xmlFragment.resolve( f.toURL().toExternalForm() );
            
            wmsCapabilitiesDoc = new WMSCapabilitiesDocument();
            this.wmsCapabilitiesDoc.load( url );
            
            file = 
	            XMLTools.getRequiredNodeAsString(  xmlFragment.getRootElement(), 
	                							  "./StylesFile", 
	                							  CommonNamespaces.getNamespaceContext() );
            
            f = new File( file );
            url = xmlFragment.resolve( f.toURL().toExternalForm() );
            this.stylesFile = new File( url.getFile() );

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    /**
     * Gets the WMS capabilites document pointed at by this configuration. The capabilities
     * document is actually a deegree WMS configuration document.
     * @return
     */
    public WMSCapabilitiesDocument getWmsCapabilities() {
        return wmsCapabilitiesDoc;
    }

    /**
     * @throws IOException
     * @throws SAXException
     * 
     */
    public synchronized void saveCapabilites() throws IOException, SAXException {

        URL u = this.wmsCapabilitiesDoc.getSystemId();

        // save 
        FileWriter fw = new FileWriter(  this.wmsCapabilitiesDoc.getSystemId().getFile() );
        Writer writer = new BufferedWriter( fw );
        this.wmsCapabilitiesDoc.write(writer);
        writer.close();
        fw.close();
        
        //reload
        this.wmsCapabilitiesDoc = new WMSConfigurationDocument();
        this.wmsCapabilitiesDoc.load( u );

    }
    
    /**
     * @throws IOException
     * @throws SAXException
     * 
     */
    public synchronized void saveStylesFile( XMLFragment stylesDoc ) throws IOException {

        // save 
        FileWriter fw = new FileWriter(  this.getStylesFile() );
        Writer writer = new BufferedWriter( fw );
        stylesDoc.write(writer);
        writer.close();
        fw.close();

    }
    
    /**
     * Gets the default styles document used by deegree WMS.
     * @return
     */    
    public File getStylesFile() {
        return stylesFile;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WMSConfConfiguration.java,v $
Revision 1.11  2006/08/24 06:38:30  poth
File header corrected

Revision 1.10  2006/06/28 20:20:15  poth
some code clean ups

Revision 1.9  2006/04/06 20:25:32  poth
*** empty log message ***

Revision 1.8  2006/03/30 21:20:29  poth
*** empty log message ***

Revision 1.7  2005/12/21 17:30:10  poth
no message

Revision 1.6  2005/12/08 15:31:53  taddei
added reference to styles file and code to save that file

Revision 1.5  2005/12/07 14:47:51  taddei
caps are changed and saved now

Revision 1.4  2005/12/05 13:11:16  taddei
paths are now relative

Revision 1.3  2005/12/01 13:12:07  taddei
added missing headers and footers, changed way how configuration is initialized


********************************************************************** */