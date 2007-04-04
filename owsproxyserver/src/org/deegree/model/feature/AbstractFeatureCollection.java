//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/AbstractFeatureCollection.java,v 1.19 2006/10/11 11:22:54 poth Exp $
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
package org.deegree.model.feature;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * @version $Revision: 1.19 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.19 $, $Date: 2006/10/11 11:22:54 $
 * 
 * @since 2.0
 */
public abstract class AbstractFeatureCollection extends AbstractFeature implements
                FeatureCollection {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractFeatureCollection.class );

    /**
     * @param id
     * @param rootfeatureType
     * @param properties
     */
    public AbstractFeatureCollection( String id ) {
        super( id, null );

        PropertyType[] ftp = new PropertyType[1];        
        QualifiedName name = null;
        
        try {
            ftp[0] = FeatureFactory.createPropertyType( new QualifiedName( "features" ),
                                                        Types.FEATURE_ARRAY_PROPERTY_NAME, true );            
            name = new QualifiedName( CommonNamespaces.WFS_PREFIX, "FeatureCollection",  
                                      CommonNamespaces.WFSNS );
        } catch (UnknownTypeException e) {
            LOG.logError( "Unreachable point reached.", e );
        }
        this.featureType = FeatureFactory.createFeatureType( name, false, ftp );

    }

    /**
     * returns a Point with position 0/0 and no CRS
     * @return a geometry
     */
    public Geometry getDefaultGeometryPropertyValue() {
        return GeometryFactory.createPoint( 0, 0, null );
    }

    /**
     * returns the value  of a feature collection geometry properties
     * @return array of all geometry property values
     */
    public Geometry[] getGeometryPropertyValues() {
        return new Geometry[0];
    }

    /**
     * returns all properties of a feature collection
     * @return all properties of a feature
     */
    public FeatureProperty[] getProperties() {
        return new FeatureProperty[0];
    }

    /**
     * returns the properties of a feature collection at the passed 
     * index position
     * @param index
     * @return properties at the passed index position 
     */
    public FeatureProperty[] getProperties( int index ) {
        // TODO
        // a FeatureCollection may also have properties?
        return null;
    }

    /**
     * returns the default property of a feature collection with the
     * passed name
     * @param name
     * @return named default property 
     */
    public FeatureProperty getDefaultProperty( QualifiedName name ) {
        // TODO
        // a FeatureCollection may also have properties?
        return null;
    }

    /**
     * returns the named properties of a feature collection
     * @param name
     * @return named properties
     */
    public FeatureProperty[] getProperties( QualifiedName name ) {
        // TODO
        // a FeatureCollection may also have properties?
        return null;
    }

    /**
     * sets a property of a feature collection.<br>
     * !!! this method is not implemented yet !!!
     * @param property
     */
    public void setProperty( FeatureProperty property ) {
        // TODO
        // a FeatureCollection may also have properties?
    }

    /**
     * @see org.deegree.model.feature.FeatureCollection#addAll(org.deegree.model.feature.Feature[])
     */
    public void addAll( Feature[] feature ) {
        for (int i = 0; i < feature.length; i++) {
            add( feature[i] );
        }
    }

    /**
     * @see org.deegree.model.feature.FeatureCollection#addAll(org.deegree.model.feature.FeatureCollection)
     */
    public void addAll( FeatureCollection fc ) {
        int size = fc.size();
        for (int i = 0; i < size; i++) {
            add( fc.getFeature( i ) );
        }
    }
   
    /**
     * removes a feature identified by its ID from the feature collection. If no feature with the
     * passed ID is available nothing happens and <tt>null</tt> will be returned
     */
    public Feature remove( String id ) {
        Feature feat = getFeature( id );
        return remove( feat );
    }

}

/* **************************************************************************************************
 * Changes to this class. What the people have been up to: 
 * $Log: AbstractFeatureCollection.java,v $
 * Revision 1.19  2006/10/11 11:22:54  poth
 * use generics
 *
 * Revision 1.18  2006/08/07 06:46:25  poth
 * comments added
 *
 * Revision 1.17  2006/06/12 14:45:17  schmitz
 * Removed some cruft (?) and extended the generic sql datastore.
 *
 *  Revision 1.16  2006/04/06 20:25:27  poth
 *  *** empty log message ***
 * 
 *  Revision 1.15  2006/03/30 21:20:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.14  2006/02/02 22:00:24  mschneider
 *  Changed default namespace prefix for feature collections to 'wfs'.
 * 
 *  Revision 1.13  2006/02/01 16:05:26  poth
 *  *** empty log message ***
 * 
 *  Revision 1.11  2006/01/31 16:24:43  mschneider
 *  Changes due to refactoring of org.deegree.model.feature package.
 * 
 *  Revision 1.10  2005/12/08 20:40:52  mschneider
 *  Added 'isAbstract' field to FeatureType.
 * 
 *  Revision 1.9  2005/11/21 18:41:26  mschneider
 *  Added methods for precise modification of the feature's properties.
 * 
 *  Revision 1.8  2005/11/16 13:44:59  mschneider
 *  Merge of wfs development branch.
 * 
 *  Revision 1.7.2.6  2005/11/15 13:36:55  deshmukh
 *  Modified Object to FeatureProperty
 * 
 *  Revision 1.7.2.5  2005/11/14 19:51:41  mschneider
 *  Fixed compilation problems.
 * 
 *  Revision 1.7.2.4  2005/11/14 00:55:52  mschneider
 *  MappedPropertyType -> PropertyType.
 * 
 *  Revision 1.7.2.3  2005/11/09 08:00:50  mschneider
 *  More refactoring of 'org.deegree.io.datastore'.
 * 
 *  Revision 1.7.2.2  2005/11/07 13:09:26  deshmukh
 *  Switched namespace definitions in "CommonNamespaces" to URI.
 * 
 *  Revision 1.7.2.1  2005/11/07 11:19:09  deshmukh
 *  Refactoring of 'createPropertyType()' methods in FeatureFactory.
 * 
 *  Revision 1.7 2005/08/30 13:40:03 poth
 *  no message Changes to this class. What
 * the people have been up to:  Revision 1.6
 * 2005/08/24 16:09:26 mschneider  Renamed
 * GenericName to QualifiedName.  Changes to
 * this class. What the people have been up to: Revision 1.5 2005/07/19 15:05:13 mschneider Changes
 * to this class. What the people have been up to: Changed name of FeatureCollection from
 * 'feature_collection' to 'deegreewfs:FeatureCollection'. Changes to this class. What the people
 * have been up to: Revision 1.4 2005/07/08 12:21:06 poth no message
 * 
 * 
 **************************************************************************************** */
