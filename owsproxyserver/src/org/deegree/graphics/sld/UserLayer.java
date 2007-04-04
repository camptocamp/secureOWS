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


import org.deegree.framework.xml.Marshallable;

/**
 * In addition to using named layers, it is also useful to be able to define * custom user-defined layers for rendering.<p></p> * Since a layer is defined as a collection of potentially mixed-type features, * the UserLayer element must provide the means to identify the features to be * used. All features to be rendered are assumed to be fetched from a Web Feature * Server (WFS) or a Web Coverage CapabilitiesService (WCS, in which case the term features * is used loosely). *  *  * @author <a href="mailto:k.lupp@web.de">Katharina Lupp</a> * @version $Revision: 1.9 $ $Date: 2006/07/29 08:51:12 $
 */

public class UserLayer extends AbstractLayer implements Marshallable {

    /**
     * 
     * @uml.property name="remoteOWS"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private RemoteOWS remoteOWS = null;


    /**
    * constructor initializing the class with the &lt;UserLayer&gt;
    */
    UserLayer( String name, LayerFeatureConstraints layerFeatureConstraints, 
                    AbstractStyle[] userStyles, RemoteOWS remoteOWS ) {
        super( name, layerFeatureConstraints, userStyles );
        setRemoteOWS( remoteOWS );
    }

    /**
     * the method returns a remote web service that enables the access to data
     * that are not stored on same server as the WMS.
     * @return the RemoteOWS
     * 
     * @uml.property name="remoteOWS"
     */
    public RemoteOWS getRemoteOWS() {
        return remoteOWS;
    }

    /**
     * sets the &lt;RemoteOWS&gt;
     * @param remoteOWS the RemoteOWS
     * 
     * @uml.property name="remoteOWS"
     */
    public void setRemoteOWS(RemoteOWS remoteOWS) {
        this.remoteOWS = remoteOWS;
    }

    /**
     *
     *
     * @return 
     */
    public String toString() {
        String ret = getClass().getName() + "\n";
        ret = "remoteOWS = " + remoteOWS + "\n";

        return ret;
    }
    
    /**
     * exports the content of the UserLayer as XML formated String
     *
     * @return xml representation of the UserLayer
     */
    public String exportAsXML() {
        
        
        StringBuffer sb = new StringBuffer(5000);
        sb.append( "<UserLayer>" );
        sb.append( "<Name>" ).append( name ).append( "</Name>" );
        if ( remoteOWS != null ) {
            sb.append( ((Marshallable)remoteOWS).exportAsXML() );
        }
        
        sb.append( ((Marshallable)layerFeatureConstraints).exportAsXML() );
       
        for (int i = 0; i < styles.size(); i++) {
            sb.append( ((Marshallable)styles.get(i)).exportAsXML() );
        }
        sb.append( "</UserLayer>" );
        
        
        return sb.toString();
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: UserLayer.java,v $
Revision 1.9  2006/07/29 08:51:12  poth
references to deprecated classes removed

Revision 1.8  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
