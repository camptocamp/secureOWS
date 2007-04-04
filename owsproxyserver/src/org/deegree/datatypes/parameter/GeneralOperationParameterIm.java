// $Header:
// /cvsroot/deegree/src/org/deegree/datatypes/parameter/GeneralOperationParameterIm.java,v
// 1.2 2004/08/16 06:23:33 ap Exp $
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
package org.deegree.datatypes.parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralOperationParameter;

/**
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/04/06 20:25:32 $
 * 
 * @since 2.0
 */
public class GeneralOperationParameterIm implements GeneralOperationParameter, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List identifiers = new ArrayList();

    private String name = null;

    private String remarks = null;

    private int maximumOccurs = 0;

    private int minimumOccurs = 0;

    /**
     * @param identifiers
     * @param name
     * @param remarks
     * @param maximumOccurs
     * @param minimumOccurs
     */
    public GeneralOperationParameterIm( Identifier[] identifiers, String name, String remarks,
                                       int maximumOccurs, int minimumOccurs ) {
        setIdentifiers( identifiers );
        this.name = name;
        this.remarks = remarks;
        this.maximumOccurs = maximumOccurs;
        this.minimumOccurs = minimumOccurs;
    }

    /**
     * @return Returns the identifiers.
     * 
     */
    public Identifier[] getIdentifiers() {
        return (Identifier[]) identifiers.toArray( new Identifier[identifiers.size()] );
    }

    /** 
     * @param identifiers
     *            The identifiers to set.
     */
    public void setIdentifiers( Identifier[] identifiers ) {
        this.identifiers.clear();
        for (int i = 0; i < identifiers.length; i++) {
            this.identifiers.add( identifiers[i] );
        }
    }

    /**
     * @return Returns the maximumOccurs.
     * 
     */
    public int getMaximumOccurs() {
        return maximumOccurs;
    }

    /**
     * @param maximumOccurs
     *            The maximumOccurs to set.
     * 
     */
    public void setMaximumOccurs( int maximumOccurs ) {
        this.maximumOccurs = maximumOccurs;
    }

    /**
     * @return Returns the minimumOccurs.
     * 
     */
    public int getMinimumOccurs() {
        return minimumOccurs;
    }

    /**
     * @param minimumOccurs
     *            The minimumOccurs to set.
     * 
     */
    public void setMinimumOccurs( int minimumOccurs ) {
        this.minimumOccurs = minimumOccurs;
    }

    /**
     * @return Returns the name.
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     * 
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return Returns the remarks.
     * 
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            The remarks to set.
     * 
     */
    public void setRemarks( String remarks ) {
        this.remarks = remarks;
    }

    /**
     * @see org.opengis.metadata.Info#getName(java.util.Locale)
     */
    public String getName( Locale locale ) {
        return name;
    }

    /**
     * @see org.opengis.metadata.Info#getRemarks(java.util.Locale)
     */
    public String getRemarks( Locale locale ) {
        return remarks;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GeneralOperationParameterIm.java,v $
 * Changes to this class. What the people have been up to: Revision 1.9  2006/04/06 20:25:32  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.8  2006/04/04 20:39:44  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.7  2006/03/30 21:20:29  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.6  2006/03/01 19:50:38  poth
 * Changes to this class. What the people have been up to: *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.5  2005/11/16 13:44:59  mschneider
 * Changes to this class. What the people have been up to: Merge of wfs development branch.
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.4.2.1  2005/11/14 11:34:03  deshmukh
 * Changes to this class. What the people have been up to: inserted: serialVersionID
 * Changes to this class. What the people have been up to:
 * Revision 1.2 2004/08/16 06:23:33 ap no message
 * 
 * Revision 1.1 2004/05/25 12:55:01 ap no message
 * 
 * 
 **************************************************************************************************/
