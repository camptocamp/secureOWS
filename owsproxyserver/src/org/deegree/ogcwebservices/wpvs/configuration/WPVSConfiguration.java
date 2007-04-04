//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/WPVSConfiguration.java,v 1.10 2006/11/27 11:33:33 bezema Exp $$
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

package org.deegree.ogcwebservices.wpvs.configuration;

import org.deegree.ogcwebservices.getcapabilities.Contents;
import org.deegree.ogcwebservices.getcapabilities.OperationsMetadata;
import org.deegree.ogcwebservices.getcapabilities.ServiceIdentification;
import org.deegree.ogcwebservices.getcapabilities.ServiceProvider;
import org.deegree.ogcwebservices.wpvs.capabilities.Dataset;
import org.deegree.ogcwebservices.wpvs.capabilities.WPVSCapabilities;

/**
 * This class represents a <code>WPVSConfiguration</code> object.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.10 $, $Date: 2006/11/27 11:33:33 $
 *
 */
public class WPVSConfiguration extends WPVSCapabilities {

    /**
     * 
     */
    private static final long serialVersionUID = 3699085834869705611L;

    private WPVSDeegreeParams deegreeParams;

    private double smallestMinimalScaleDenomiator;

    /**
     * @param version
     *            the Version of this wpvs
     * @param updateSequence
     *            optional needed for clients who want to do caching (ogc-spec)
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param contents
     * @param dataset
     * @param wpvsParams
     *            deegree specific parameters.
     * @param smallestMinimalScaleDenomiator
     *            of all datasources, it is needed to calculate the smallest resolutionstripe
     *            possible.
     */
    public WPVSConfiguration( String version, String updateSequence,
                             ServiceIdentification serviceIdentification,
                             ServiceProvider serviceProvider,
                             OperationsMetadata operationsMetadata, Contents contents,
                             Dataset dataset, WPVSDeegreeParams wpvsParams,
                             double smallestMinimalScaleDenomiator ) {

        super( version, updateSequence, serviceIdentification, serviceProvider, operationsMetadata,
               contents, dataset );
        this.deegreeParams = wpvsParams;
        this.smallestMinimalScaleDenomiator = smallestMinimalScaleDenomiator;
        if ( Double.isInfinite( smallestMinimalScaleDenomiator )
             || smallestMinimalScaleDenomiator < 0 )
            this.smallestMinimalScaleDenomiator = 1;

 
    }

    /**
     * @return Returns the deegreeParams.
     */
    public WPVSDeegreeParams getDeegreeParams() {
        return deegreeParams;
    }

    /**
     * @param deegreeParams
     *            The deegreeParams to set.
     */
    public void setDeegreeParams( WPVSDeegreeParams deegreeParams ) {
        this.deegreeParams = deegreeParams;
    }

    /**
     * @return the smallestMinimalScaleDenomiator value.
     */
    public double getSmallestMinimalScaleDenomiator() {
        return smallestMinimalScaleDenomiator;
    }
}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: WPVSConfiguration.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/11/27 11:33:33  bezema
 * Changes to this class. What the people have been up to: UPdating javadocs and cleaning up
 * Changes to this class. What the people have been up to: Revision
 * 1.9 2006/08/24 06:42:16 poth File header corrected
 * 
 * Revision 1.8 2006/04/06 20:25:24 poth ** empty log message ***
 * 
 * Revision 1.7 2006/03/30 21:20:25 poth ** empty log message ***
 * 
 * Revision 1.6 2005/12/09 14:08:32 mays clean up
 * 
 * Revision 1.5 2005/12/01 10:30:14 mays add standard footer to all java classes in wpvs package
 * 
 **************************************************************************************************/
