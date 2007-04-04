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

import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.filterencoding.Filter;

/**
 * deegree2/
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:mkulbe@lat-lon.de">Matthias Kulbe </a>
 */
public class MeasurementConfiguration {

    private String id = null;

    private String sourceServerId = null;

    private String phenomenon = null;

    private QualifiedName featureTypeName = null;

    private Filter constraint = null;

    private QualifiedName timePropertyName = null;

    private QualifiedName measurandPropertyName = null;

    private String timeResolution = null;

    private String timeResolutionType = null;

    private URL XSLTScriptSource = null;

    /**
     * 
     * @param id
     * @param sourceServerId
     * @param phenomenon
     * @param featureTypeName
     * @param constraint
     * @param timePropertyName
     * @param measurandPropertyName
     * @param timeResolution
     * @param timeResolutionType
     * @param XSLTScriptSource
     */
    public MeasurementConfiguration( String id, String sourceServerId, String phenomenon,
                                    QualifiedName featureTypeName, Filter constraint,
                                    QualifiedName timePropertyName,
                                    QualifiedName measurandPropertyName, String timeResolution,
                                    String timeResolutionType, URL XSLTScriptSource ) {
        this.id = id;
        this.sourceServerId = sourceServerId;
        this.phenomenon = phenomenon;
        this.featureTypeName = featureTypeName;
        this.constraint = constraint;
        this.timePropertyName = timePropertyName;
        this.measurandPropertyName = measurandPropertyName;
        this.timeResolution = timeResolution;
        this.timeResolutionType = timeResolutionType;
        this.XSLTScriptSource = XSLTScriptSource;

    }

    /**
     * 
     * @return
     */
    public String getSourceServerId() {
        return sourceServerId;
    }

    /**
     * 
     * @return
     */
    public QualifiedName getFeatureTypeName() {
        return featureTypeName;
    }

    /**
     * 
     * @return
     */
    public Filter getConstraint() {
        return constraint;
    }

    /**
     * 
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @return
     */
    public QualifiedName getMeasurandPropertyName() {
        return measurandPropertyName;
    }

    /**
     * 
     * @return
     */
    public String getPhenomenon() {
        return phenomenon;
    }

    /**
     * 
     * @return
     */
    public QualifiedName getTimePropertyName() {
        return timePropertyName;
    }

    /**
     * 
     * @return
     */
    public String getTimeResolution() {
        return timeResolution;
    }

    /**
     * 
     * @return
     */
    public String getTimeResolutionType() {
        return timeResolutionType;
    }

    /**
     * 
     * @return
     */
    public URL getXSLTScriptSource() {
        return XSLTScriptSource;
    }

    /**
     * overwrites the equals function. Two instances of <code>MeasurementConfiguration</code> are
     * equal if their IDs are equal
     * 
     * @param obj
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof MeasurementConfiguration ) ) {
            return false;
        }
        if ( this.getId().equals( ( (MeasurementConfiguration) obj ).getId() ) ) {
            return true;
        }
        return false;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: MeasurementConfiguration.java,v $
Revision 1.8  2006/08/24 06:42:16  poth
File header corrected

Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
