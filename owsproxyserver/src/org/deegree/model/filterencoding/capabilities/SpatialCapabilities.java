// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/getcapabilities/Contents.java,v
// 1.1 2004/06/23 11:55:40 mschneider Exp $
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
package org.deegree.model.filterencoding.capabilities;

import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;

/**
 * SpatialCapabilitiesBean
 * 
 * @author <a href="mailto:tfr@users.sourceforge.net">Torsten Friebe </A>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.7 $, $Date: 2006/07/12 14:46:16 $
 * 
 * @since 2.0
 */
public class SpatialCapabilities {

    // keys are Strings (operator names), values are SpatialOperator-instances
    private Map operators = new HashMap();

    private QualifiedName[] geometryOperands;

    /**
     * Creates a new <code>SpatialCapabilities</code> instance that complies
     * to the <code>Filter Encoding Specification 1.0.0</code> (without
     * <code>GeometryOperands</code>).
     * 
     * @param spatialOperators
     */
    public SpatialCapabilities(SpatialOperator[] spatialOperators) {
        setSpatialOperators(spatialOperators);
    }

    /**
     * Creates a new <code>SpatialCapabilities</code> instance that complies
     * to the <code>Filter Encoding Specification 1.1.0</code> (with
     * <code>GeometryOperands</code>).
     * 
     * @param spatialOperators
     * @param geometryOperands
     */
    public SpatialCapabilities(SpatialOperator[] spatialOperators,
            QualifiedName[] geometryOperands) {
        setSpatialOperators(spatialOperators);
        this.geometryOperands = geometryOperands;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree_impl.services.wfs.service.SpatialCapabilities#addSpatialOperator(org.deegree_impl.services.wfs.service.Operator)
     */
    public void addSpatialOperator(SpatialOperator operator) {
        this.operators.put(operator.getName(), operator);

    }

    /**
     * Returns if the given operator is supported.
     * 
     * @param operatorName
     * @return
     */
    public boolean hasOperator(String operatorName) {
        return operators.get(operatorName) != null ? true : false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree_impl.services.wfs.service.SpatialCapabilities#getSpatialOperators()
     */
    public SpatialOperator[] getSpatialOperators() {
        return (SpatialOperator[]) operators.values().toArray(
                new SpatialOperator[this.operators.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree_impl.services.wfs.service.SpatialCapabilities#setSpatialOperators(org.deegree_impl.services.wfs.service.Operator[])
     */
    public void setSpatialOperators(SpatialOperator[] operators) {
        this.operators.clear();
        for (int i = 0; i < operators.length; i++) {
            this.addSpatialOperator(operators[i]);
        }
    }

    /**
     * @return Returns the geometryOperands.
     */
    public QualifiedName[] getGeometryOperands() {
        return geometryOperands;
    }

    /**
     * @param geometryOperands
     *            The geometryOperands to set.
     */
    public void setGeometryOperands(QualifiedName[] geometryOperands) {
        this.geometryOperands = geometryOperands;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: SpatialCapabilities.java,v $
Revision 1.7  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
