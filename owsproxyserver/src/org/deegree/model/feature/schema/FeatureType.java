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

package org.deegree.model.feature.schema;

import java.net.URI;

import org.deegree.datatypes.QualifiedName;

/**
 * The FeatureType interface is intended to provide details of the type of a Feature that are
 * described as Feature Schema in the Abstract Specification's Essential Model, specifically the
 * names and types of the properties associated with each instance of a Feature of the given
 * FeatureType.
 * <p>
 * -----------------------------------------------------------------------
 * </p>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version $Revision: 1.5 $ $Date: 2006/08/28 20:15:11 $
 */
public interface FeatureType {

    /**
     * returns the name of the FeatureType
     * 
     * @return name of the FeatureType
     */
    public QualifiedName getName();

    /**
     * Returns whether this feature type is abstract or not.
     * 
     * @return true, if the feature type is abstract, false otherwise
     */
    public boolean isAbstract();

    /**
     * returns the namespace of the feature type (maybe null)
     * 
     * @return
     */
    public URI getNameSpace();

    /**
     * returns the location of the XML schema defintion assigned to a namespace
     * 
     * @return
     */
    public URI getSchemaLocation();

    /**
     * returns the name of the property a the passed index position
     */
    public QualifiedName getPropertyName( int index );

    /**
     * returns the properties of this feature type
     * 
     * @return type properties
     */
    public PropertyType[] getProperties();

    /**
     * returns a property of this feature type identified by its name
     * 
     * @param name
     *            name of the desired property
     * @return one named property
     */
    public PropertyType getProperty( QualifiedName name );

    /**
     * returns the FeatureTypeProperties of type GEOMETRY
     * 
     * @see org.deegree.datatypes.Types
     * @return
     */
    public GeometryPropertyType[] getGeometryProperties();

    /**
     * returns true if the passed FeatureType equals this FeatureType. Two FeatureTypes are equal if
     * they have the same qualified name
     * 
     * @return
     */
    public boolean equals( FeatureType featureType );

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: FeatureType.java,v $
 * Revision 1.5  2006/08/28 20:15:11  poth
 * footer corrected
 *
 * Revision 1.4  2006/04/06 20:25:21  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.3  2006/04/04 20:39:40  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.2  2006/03/30 21:20:23  poth
 * *** empty log message ***
 * Changes to this class. What the people have been up to:
 * Revision 1.1  2006/01/31 16:25:55  mschneider
 * Changes due to refactoring of org.deegree.model.feature package.
 * Changes to this class. What the people have been up to:
 * Revision 1.13  2005/12/20 10:33:32  deshmukh
 * *** empty log message ***
 * Revision 1.12
 * 2005/12/08 20:40:52 mschneider Added 'isAbstract' field to FeatureType.
 * 
 * Revision 1.11 2005/11/16 13:44:59 mschneider Merge of wfs development branch.
 * 
 * Revision 1.10.2.3 2005/11/14 00:57:18 mschneider MappedPropertyType -> PropertyType.
 * 
 * Revision 1.10.2.2 2005/11/09 08:00:50 mschneider More refactoring of 'org.deegree.io.datastore'.
 * 
 * Revision 1.10.2.1 2005/11/07 11:19:09 deshmukh Refactoring of 'createPropertyType()' methods in
 * FeatureFactory.
 * 
 * Revision 1.10 2005/08/30 13:40:03 poth no message
 * 
 * Revision 1.9 2005/08/24 16:09:26 mschneider Renamed GenericName to QualifiedName.
 * 
 * Revision 1.8 2005/07/08 13:24:53 poth no message
 * 
 * Revision 1.7 2005/07/08 08:40:48 poth no message
 * 
 * Revision 1.6 2005/06/06 09:47:46 poth no message
 * 
 * Revision 1.5 2005/02/10 17:17:24 mschneider Corrected usage of XmlNode + XmlDocument.
 * 
 * Revision 1.4 2005/01/19 17:44:35 poth no message
 * 
 * Revision 1.3 2005/01/19 17:22:26 poth no message
 * 
 * Revision 1.2 2005/01/17 22:16:28 poth no message
 * 
 * Revision 1.8 2004/08/06 06:41:51 ap
 * 
 **************************************************************************************************/
