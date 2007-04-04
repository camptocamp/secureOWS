//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon/OWSDomainType.java,v 1.6 2006/07/12 14:46:19 poth Exp $
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
package org.deegree.owscommon;

/**
 * Class representation of the type <code>ows:DomainType</code> defined in
 * <code>owsOperationsMetadata.xsd</code> from the
 * <code>OWS Common Implementation
 * Specification 0.3</code>.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 */
public class OWSDomainType {
    
    private String name;
    private String [] values;
    private OWSMetadata [] metadata;

    /**
     * 
     * @param name
     * @param metadata
     */
    public OWSDomainType (String name, OWSMetadata [] metadata) {
        this.name = name;
        this.metadata = metadata;
    }    
    
    /**
     * 
     * @param name
     * @param values
     * @param metadata
     */
    public OWSDomainType (String name, String [] values, OWSMetadata [] metadata) {
        this.name = name;
        this.values = values;
        this.metadata = metadata;
    }
    
    /**
     * @return Returns the metadata.
     */
    public OWSMetadata[] getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata to set.
     */
    public void setMetadata(OWSMetadata[] metadata) {
        this.metadata = metadata;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the values.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * @param values The values to set.
     */
    public void setValues(String[] values) {
        this.values = values;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OWSDomainType.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
