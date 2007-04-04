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
package org.deegree.ogcwebservices.csw.manager;

import java.net.URI;
import java.util.Map;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.csw.manager.HarvestRepository.ResourceType;

/**
 * returns an concrete instance of
 * 
 * @see org.deegree.ogcwebservices.csw.manager.AbstractHarvester that is responsible for performing
 *      a harvest request against a source type assigned to the request. To decide which concrete
 *      Harvester is required the resourceType and, if neccessary, source parameter of a harvest
 *      request will be examinded.
 * 
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/07/12 14:46:17 $
 * 
 * @since 2.0
 */
public class HarvesterFactory {

    private Map<ResourceType, AbstractHarvester> availableHarvester = null;

    /**
     * list of available Harvester
     * 
     * @param availableHarvester
     */
    HarvesterFactory( Map<ResourceType, AbstractHarvester> availableHarvester ) {
        this.availableHarvester = availableHarvester;
    }

    /**
     * returns an concrete instance of
     * 
     * @see org.deegree.ogcwebservices.csw.manager.AbstractHarvester that is responsible for
     *      performing a harvest request against a resource type assigned to the request. If no
     *      Harvester can be found that can be used to haverst the source defined in a harvest
     *      request an
     * 
     * @param request
     * @return
     */
    AbstractHarvester findHarvester( Harvest request )
                            throws InvalidParameterValueException {
        URI uri = request.getResourceType();
        ResourceType st = null;
        if ( uri == null ) {
            st = evaluateSource( request.getSource() );
        } else {
            String s = uri.toASCIIString();
            if ( s.equals( "csw:profile" ) ) {
                st = ResourceType.csw_profile;
            } else if ( s.equals( "service" ) ) {
                st = ResourceType.service;
            } else if ( s.equals( "FGDC" ) ) {
                st = ResourceType.FGDC;
            } else if ( s.equals( "dublincore" ) ) {
                st = ResourceType.dublincore;
            } else if ( s.equals( "catalogue" ) ) {
                st = ResourceType.catalogue;
            } else {
                String ms = "requested resourceType:" + s + " is not supported by the CS-W";
                throw new InvalidParameterValueException( getClass().getName(), ms );
            }
        }
        return availableHarvester.get( st );
    }

    /**
     * determines the type of a metadata resource by evaluation the root element returned when
     * accessing it.
     * 
     * @param source
     * @return
     * @throws InvalidParameterValueException
     */
    private ResourceType evaluateSource( URI source )
                            throws InvalidParameterValueException {

        ResourceType st = null;
        try {
            XMLFragment xml = new XMLFragment();
            xml.load( source.toURL() );
            String ns = xml.getRootElement().getNamespaceURI();
            String s = xml.getRootElement().getLocalName();
            if ( "MD_Metadata".equals( s ) ) {
                st = ResourceType.csw_profile;
            } else if ( "Record".equals( s ) ) {
                st = ResourceType.dublincore;
            } else if ( "WMT_MS_Capabilities".equals( s ) ) {
                st = ResourceType.service;
            } else if ( "WFS_Capabilities".equals( s ) ) {
                st = ResourceType.service;
            } else if ( "WCS_Capabilities".equals( s ) ) {
                st = ResourceType.service;
            }
            if ( "http://www.opengis.net/cat/csw".equals( ns ) ) {
                st = ResourceType.catalogue;
            }
        } catch ( Exception e ) {
            throw new InvalidParameterValueException( "source: " + source
                                                      + " can not be parsed as XML" );
        }

        return st;
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: HarvesterFactory.java,v $
Revision 1.5  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
