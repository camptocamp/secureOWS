//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/owscommon_new/DomainType.java,v 1.2 2006/08/24 06:43:04 poth Exp $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:
 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de
 Jens Fitzke
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.owscommon_new;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.Interval;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.datatypes.values.Values;
import org.deegree.model.crs.CoordinateSystem;

/**
 * <code>DomainType</code> is describes the domain of a parameter according to the OWS common
 * specification 1.0.0. It also implements quite a few extensions.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.2 $, $Date: 2006/08/24 06:43:04 $
 * 
 * @since 2.0
 */

public class DomainType extends Parameter {

    private QualifiedName name = null;
    
    private List<TypedLiteral> values = null;
    
    private List<Interval> ranges = null;
    
    private TypedLiteral defaultValue = null;
    
    private boolean anyValueAllowed = false;
    
    private String meaning = null;
    
    private boolean noValuesAllowed = false;
    
    private CoordinateSystem referenceSystem = null;
    
    private QualifiedName unitOfMeasure = null;

    private Values valueList = null;
    
    private Object metadata = null;
    
    /**
     * Standard constructor that initializes all encapsulated data.
     * 
     * @param optional
     * @param repeatable
     * @param description
     * @param direction
     * @param name
     * @param values
     * @param ranges
     * @param defaultValue
     * @param anyValueAllowed
     * @param meaning
     * @param noValuesAllowed
     * @param referenceSystem
     * @param unitOfMeasure 
     * @param valueList
     * @param metadata
     */
    public DomainType( boolean optional, boolean repeatable, String description, int direction,
                       QualifiedName name, List<TypedLiteral> values, List<Interval> ranges,
                       TypedLiteral defaultValue, boolean anyValueAllowed, String meaning,
                       boolean noValuesAllowed, CoordinateSystem referenceSystem,
                       QualifiedName unitOfMeasure, Values valueList, Object metadata ) {
        super( optional, repeatable, description, direction );
        
        this.name = name;
        this.values = values;
        this.ranges = ranges;
        this.defaultValue = defaultValue;
        this.anyValueAllowed = anyValueAllowed;
        this.meaning = meaning;
        this.noValuesAllowed = noValuesAllowed;
        this.referenceSystem = referenceSystem;
        this.unitOfMeasure = unitOfMeasure;
        this.valueList = valueList;
        this.metadata = metadata;
    }
    
    /**
     * @return Returns whether any value is allowed.
     */
    public boolean isAnyValueAllowed() {
        return anyValueAllowed;
    }

    /**
     * @return Returns the defaultValue.
     */
    public TypedLiteral getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return Returns the meaning.
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * @return Returns the metadata.
     */
    public Object getMetadata() {
        return metadata;
    }

    /**
     * @return Returns the name.
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * @return Returns the noValuesAllowed.
     */
    public boolean areNoValuesAllowed() {
        return noValuesAllowed;
    }

    /**
     * @return Returns the ranges.
     */
    public List<Interval> getRanges() {
        return ranges;
    }

    /**
     * @return Returns the referenceSystem.
     */
    public CoordinateSystem getReferenceSystem() {
        return referenceSystem;
    }

    /**
     * @return Returns the unitOfMeasure.
     */
    public QualifiedName getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * @return Returns the valueList.
     */
    public Values getValueList() {
        return valueList;
    }

    /**
     * @return Returns the values.
     */
    public List<TypedLiteral> getValues() {
        return values;
    }
    
}


/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DomainType.java,v $
Revision 1.2  2006/08/24 06:43:04  poth
File header corrected

Revision 1.1  2006/08/23 07:10:22  schmitz
Renamed the owscommon_neu package to owscommon_new.

Revision 1.3  2006/08/08 10:21:52  schmitz
Parser is finished, as well as the iso XMLFactory.

Revision 1.2  2006/08/04 15:16:26  schmitz
Half the OWS common 1.0.0 parser is finished. Data classes should be complete.

Revision 1.1  2006/08/01 11:46:07  schmitz
Added data classes for the new OWS common capabilities framework
according to the OWS 1.0.0 common specification.
Added name to service identification.



********************************************************************** */