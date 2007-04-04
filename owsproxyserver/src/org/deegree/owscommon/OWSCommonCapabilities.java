//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon/OWSCommonCapabilities.java,v 1.7 2006/11/07 11:08:38 mschneider Exp $
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

import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;

/**
 * Represents a configuration for an OGC-Webservice according to the OWS Common
 * Implementation Specification 0.2, i.e. it consists of the following parts:
 * <ul>
 * <li>ServiceIdentification (corresponds to and expands the
 * SV_ServiceIdentification class in ISO 19119)
 * <li>ServiceProvider (corresponds to and expands the SV_ServiceProvider class
 * in ISO 19119)
 * <li>OperationsMetadata (contains set of Operation elements that each
 * corresponds to and expand the SV_OperationsMetadata class in ISO 19119)
 * <li>Contents (whenever relevant, contains set of elements that each
 * corresponds to the MD_DataIdentification class in ISO 19119 and 19115)
 * </ul>
 * 
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.7 $, $Date: 2006/11/07 11:08:38 $
 */
public abstract class OWSCommonCapabilities extends OGCCapabilities {

    private ServiceIdentification serviceIdentification;

    private ServiceProvider serviceProvider;

    private OperationsMetadata operationsMetadata;

    private Contents contents;

    /**
     * Constructor to be used from the implementing subclasses.
     * 
     * @param version
     * @param updateSequence
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     */
    protected OWSCommonCapabilities( String version, String updateSequence,
                                    ServiceIdentification serviceIdentification,
                                    ServiceProvider serviceProvider,
                                    OperationsMetadata operationsMetadata, Contents contents ) {
        super( version, updateSequence );
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.contents = contents;
    }

    /**
     * @return Returns the contents.
     */
    public Contents getContents() {
        return contents;
    }

    /**
     * @param contents The contents to set.
     */
    public void setContents( Contents contents ) {
        this.contents = contents;
    }

    /**
     * @return Returns the operationsMetadata.
     */
    public OperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * @param operationsMetadata The operationsMetadata to set.
     */
    public void setOperationsMetadata( OperationsMetadata operationsMetadata ) {
        this.operationsMetadata = operationsMetadata;
    }

    /**
     * @return Returns the serviceIdentification.
     */
    public ServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    /**
     * @param serviceIdentification The serviceIdentification to set.
     */
    public void setServiceIdentification( ServiceIdentification serviceIdentification ) {
        this.serviceIdentification = serviceIdentification;
    }

    /**
     * @return Returns the serviceProvider.
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * @param serviceProvider The serviceProvider to set.
     */
    public void setServiceProvider( ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: OWSCommonCapabilities.java,v $
 Revision 1.7  2006/11/07 11:08:38  mschneider
 Fixed formatting.

 Revision 1.6  2006/07/12 14:46:19  poth
 comment footer added

 ********************************************************************** */