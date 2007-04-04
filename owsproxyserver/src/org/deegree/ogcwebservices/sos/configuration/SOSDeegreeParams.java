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
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: fitzke@lat-lon.de

 ---------------------------------------------------------------------------*/
package org.deegree.ogcwebservices.sos.configuration;

import java.util.ArrayList;

import org.deegree.enterprise.DeegreeParams;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.metadata.iso19115.OnlineResource;

/**
 * Describe the SCSDeegreeParams, is a part of the SCSConfiguration
 * 
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 * 
 * @version 1.0
 */

public class SOSDeegreeParams extends DeegreeParams {

    private ArrayList sensorConfigs = null;

    private ArrayList platformConfigs = null;

    private ArrayList sourceServerConfigs = null;

    private int sourceServerTimeLimit = 0;

    private static final ILogger LOG = LoggerFactory.getLogger( SOSDeegreeParams.class );

    /**
     * constructor
     * 
     * @param defaultOnlineResource
     * @param cacheSize
     * @param requestTimeLimit
     * @param characterSet
     * @param sourceServerTimeLimit
     * @param sensorConfigs
     * @param platformConfigs
     * @param sourceServerConfigs
     */
    public SOSDeegreeParams( OnlineResource defaultOnlineResource, int cacheSize,
                            int requestTimeLimit, String characterSet, int sourceServerTimeLimit,
                            SensorConfiguration[] sensorConfigs,
                            PlatformConfiguration[] platformConfigs,
                            SourceServerConfiguration[] sourceServerConfigs ) {

        super( defaultOnlineResource, cacheSize, requestTimeLimit, characterSet );

        this.sourceServerTimeLimit = sourceServerTimeLimit;

        if ( sensorConfigs != null ) {
            this.sensorConfigs = new ArrayList( sensorConfigs.length );
            for (int i = 0; i < sensorConfigs.length; i++) {
                this.sensorConfigs.add( sensorConfigs[i] );
            }
        } else {
            this.sensorConfigs = new ArrayList( 1 );
        }

        if ( platformConfigs != null ) {
            this.platformConfigs = new ArrayList( platformConfigs.length );
            for (int i = 0; i < platformConfigs.length; i++) {
                this.platformConfigs.add( platformConfigs[i] );
            }
        } else {
            this.platformConfigs = new ArrayList( 1 );
        }

        if ( sourceServerConfigs != null ) {
            this.sourceServerConfigs = new ArrayList( sourceServerConfigs.length );
            for (int i = 0; i < sourceServerConfigs.length; i++) {
                this.sourceServerConfigs.add( sourceServerConfigs[i] );
            }
        } else {
            this.sourceServerConfigs = new ArrayList( 1 );
        }

    }

    /**
     * gets the platform configuration by id
     * 
     * @param id
     * @return
     */
    public PlatformConfiguration getPlatformConfiguration( String id ) {
        for (int i = 0; i < platformConfigs.size(); i++) {
            if ( ( (PlatformConfiguration) platformConfigs.get( i ) ).getId().equals( id ) ) {
                return (PlatformConfiguration) platformConfigs.get( i );
            }
        }
        return null;
    }

    /**
     * gets the Platform Configuration by the IdPropertyValue which is set in the configuration
     * 
     * @param id
     * @return
     */
    public PlatformConfiguration getPlatformConfigurationByIdPropertyValue( String id ) {
        for (int i = 0; i < platformConfigs.size(); i++) {
            String s = ( (PlatformConfiguration) platformConfigs.get( i ) ).getIdPropertyValue();
            if ( s.equals( id ) ) {
                return (PlatformConfiguration) platformConfigs.get( i );
            }
        }

        return null;
    }

    /**
     * gets all platform configs
     * 
     * @return
     */
    public PlatformConfiguration[] getPlatformConfigurations() {
        return ( (PlatformConfiguration[]) this.platformConfigs
            .toArray( new PlatformConfiguration[this.platformConfigs.size()] ) );
    }

    /**
     * gets the sensor configuration by id
     * 
     * @param id
     * @return
     */
    public SensorConfiguration getSensorConfiguration( String id ) {
        for (int i = 0; i < sensorConfigs.size(); i++) {
            if ( ( (SensorConfiguration) sensorConfigs.get( i ) ).getId().equals( id ) ) {
                return (SensorConfiguration) sensorConfigs.get( i );
            }

        }

        return null;
    }

    /**
     * gets the Sensor Configuration by the IdPropertyValue which is set in the configuration
     * 
     * @param id
     * @return
     */
    public SensorConfiguration getSensorConfigurationByIdPropertyValue( String id ) {
        for (int i = 0; i < this.sensorConfigs.size(); i++) {
            SensorConfiguration sc = ( (SensorConfiguration) this.sensorConfigs.get( i ) );
            String s = sc.getId();

            if ( s.equals( id ) ) {
                return ( (SensorConfiguration) this.sensorConfigs.get( i ) );
            }
        }

        return null;
    }

    /**
     * gets all sensor configs
     * 
     * @return
     */
    public SensorConfiguration[] getSensorConfigurations() {
        return ( (SensorConfiguration[]) this.sensorConfigs
            .toArray( new SensorConfiguration[this.sensorConfigs.size()] ) );
    }

    public int getSourceServerTimeLimit() {
        return sourceServerTimeLimit;
    }

    /**
     * gets all sourceServer Configurations
     * 
     * @return
     */
    public SourceServerConfiguration[] getSourceServerConfigurations() {
        return ( (SourceServerConfiguration[]) this.sourceServerConfigs
            .toArray( new SourceServerConfiguration[this.sourceServerConfigs.size()] ) );
    }

    /**
     * gets the sourceServer config by the given id
     * 
     * @param id
     * @return
     */
    public SourceServerConfiguration getSourceServerConfiguration( String id ) {
        for (int i = 0; i < this.sourceServerConfigs.size(); i++) {
            if ( ( (SourceServerConfiguration) this.sourceServerConfigs.get( i ) ).getId().equals(
                id ) ) {
                return (SourceServerConfiguration) this.sourceServerConfigs.get( i );
            }
        }
        return null;
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SOSDeegreeParams.java,v $
Revision 1.11  2006/08/24 06:42:17  poth
File header corrected

Revision 1.10  2006/07/12 16:59:32  poth
required adaptions according to renaming of OnLineResource to OnlineResource

Revision 1.9  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
