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
package org.deegree.ogcwebservices.getcapabilities;

import java.net.URL;

import org.deegree.ogcbase.BaseURL;

/**
 * A WFS/WMS/WCS should use one or more <MetadataURL>elements to offer detailed,
 * standardized metadata about the data underneath a particular layer. The
 * <code>MetadataURL</code> element shall not be used to reference metadata in a
 * non-standardized metadata format.
 * <p>
 * The type attribute indicates the standard to which the metadata complies, three
 * types are defined at present (from the WFS 1.1.1 specification):
 * <p>
 * <table border="1">
 * <tr><th>Type value</th><th>Metadata standard</th></tr>
 * <tr><td>'TC211' or 'ISO19115'</td><td>ISO TC211 19115</td></tr>
 * <tr><td>'FGDC'</td><td>FGDC CSDGM</td></tr>
 * <tr><td>'ISO19139'</td><td>ISO 19139</td></tr>
 * </table>
 * 
 * @author <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @version $Revision: 1.12 $, $Date: 2006/07/12 14:46:16 $
 */
public class MetadataURL extends BaseURL {

    private String type = null;

    /**
     * Constructs a new MetadataURL instance.
     */
    public MetadataURL(String type, String format, URL onlineResource) {
        super(format, onlineResource);
        setType(type);
    }

    /**
     * returns the type attribute indicating the standard to which the metadata
     * complies.
     */
    public String getType() {
        return type;
    }

    /**
     * sets the type attribute indicating the standard to which the metadata
     * complies.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String toString() {
        String ret = null;
        ret = "type = " + type + "\n";
        return ret;
    }

  
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MetadataURL.java,v $
Revision 1.12  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
