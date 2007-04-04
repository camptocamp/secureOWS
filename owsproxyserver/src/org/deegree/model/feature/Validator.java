//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/Validator.java,v 1.5 2006/08/31 15:00:50 mschneider Exp $
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
package org.deegree.model.feature;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.TimeTools;
import org.deegree.model.feature.schema.FeaturePropertyType;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.MultiGeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.feature.schema.SimplePropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.ogcwebservices.OGCWebServiceException;

/**
 * Validator for feature instance (that have been constructed without schema information).
 * <p>
 * Validated features are assigned their respective feature types after succesful validation. 
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.5 $, $Date: 2006/08/31 15:00:50 $
 */
public class Validator {

    private static final ILogger LOG = LoggerFactory.getLogger( Validator.class );    
    
    private Set<Feature> inValidation = new HashSet<Feature> ();
    
    private Map<QualifiedName,FeatureType> ftMap;

    /**
     * Constructs a new instance of <code>Validator</code> that will use the given map to lookup
     * feature types by their names.
     * 
     * @param ftMap
     */
    public Validator( Map<QualifiedName, FeatureType> ftMap ) {
        this.ftMap = ftMap;
    }
    
    /**
     * Validates the given feature instance (and it's subfeatures).
     * <p>
     * The feature instance is then given the corresponding <code>FeatureType</code>. This
     * also applies for it's subfeatures.
     * 
     * @param feature
     *            feature instance to be validated
     * @throws OGCWebServiceException
     */     
    public void validate ( Feature feature ) throws OGCWebServiceException {
       
        if (inValidation.contains(feature)) {
            return;
        }
        inValidation.add (feature);
        
        QualifiedName ftName = feature.getName();
        FeatureType ft = this.ftMap.get( ftName );
        if ( ft == null ) {
            String msg = Messages.format("ERROR_FT_UNKNOWN", ftName);
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        int idx = 0;
        FeatureProperty [] properties = feature.getProperties();
        
        PropertyType [] propertyTypes = ft.getProperties();
        for (int i = 0; i < propertyTypes.length; i++) {
            idx += validateProperties (ftName, propertyTypes [i], properties, idx);
        }
        if (idx != properties.length) {
            String msg = Messages.format ("ERROR_FT_INVALID1", ftName, properties[idx].getName());
            throw new OGCWebServiceException( this.getClass().getName(), msg );
        }

        feature.setFeatureType (ft);
    }

    /**
     * Validates that there is the correct amount of properties with the expected type in the given
     * array of properties. 
     * 
     * @param ftName
     * @param propertyType
     * @param properties
     * @param idx
     * @throws OGCWebServiceException
     */
    private int validateProperties( QualifiedName ftName, PropertyType propertyType,
                                   FeatureProperty[] properties, int idx )
                                                                throws OGCWebServiceException {
        int minOccurs = propertyType.getMinOccurs();
        int maxOccurs = propertyType.getMaxOccurs();
        QualifiedName propertyName = propertyType.getName();
        int count = 0;

        while (idx + count < properties.length) {            
            if ( properties[idx + count].getName().equals( propertyName ) ) {                
                validate( properties[idx + count], propertyType );                
                count++;                
            } else {                
                break;
            }
        }
        if ( count < minOccurs ) {
            if (count == 0) {
                String msg = Messages.format ("ERROR_FT_INVALID2", ftName, propertyName);
                throw new OGCWebServiceException( this.getClass().getName(), msg);                
            } 
            String msg = Messages.format( "ERROR_FT_INVALID3",
                new Object[] { ftName, propertyName, minOccurs, count } );
            throw new OGCWebServiceException( this.getClass().getName(), msg);                
        
        }
        if ( maxOccurs != -1 && count > maxOccurs ) {
            String msg = Messages.format( "ERROR_FT_INVALID4",
                new Object[] { ftName, propertyName, maxOccurs, count } );
            throw new OGCWebServiceException( this.getClass().getName(), msg);            
        }
        return count;
    }

    /**
     * Validates that there is the correct amount of properties with the expected type in the given
     * array of properties. 
     * 
     * @param ftName
     * @param propertyType
     * @param properties
     * @param idx
     * @throws OGCWebServiceException
     */    
    private void validate( FeatureProperty property, PropertyType propertyType )
        throws OGCWebServiceException {

        Object value = property.getValue();
        if ( propertyType instanceof SimplePropertyType ) {
            String s = value.toString();
            if (value instanceof Date) {
                s = TimeTools.getISOFormattedTime((Date) value);
            }
            Object newValue = validateSimpleProperty( (SimplePropertyType) propertyType, s );
            property.setValue( newValue );
        } else if ( propertyType instanceof GeometryPropertyType ) {
            if (!(value instanceof Geometry)) {
                String msg = Messages.format( "ERROR_WRONG_PROPERTY_TYPE", propertyType.getName(),
                    "GeometryProperty", value.getClass().getName() );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
        } else if ( propertyType instanceof FeaturePropertyType ) {
            if (!(value instanceof Feature)) {
                String msg = Messages.format( "ERROR_WRONG_PROPERTY_TYPE", propertyType.getName(),
                    "FeatureProperty", value.getClass().getName() );
                throw new OGCWebServiceException( this.getClass().getName(), msg );
            }
            Feature feature = (Feature) value;
            //FeaturePropertyContent content = (FeaturePropertyContent) propertyType.getContents() [0];
            //MappedFeatureType contentFT = content.getFeatureTypeReference().getFeatureType();
            
            // TODO: check that feature is a correct subsitution for the expected featuretype
            
            validate( feature );
        } else if ( propertyType instanceof MultiGeometryPropertyType ) {
            throw new OGCWebServiceException(
                "Handling of MultiGeometryPropertyTypes not implemented "
                    + "in validateProperty()." );
        } else {
            throw new OGCWebServiceException( "Internal error: Unhandled property type '"
                + propertyType.getClass() + "' encountered while validating property." );
        }
    }

    /**
     * 
     * @param propertyType
     * @param s
     * @return
     * @throws OGCWebServiceException
     */
    private Object validateSimpleProperty( SimplePropertyType propertyType, String s )
        throws OGCWebServiceException {

        int type = propertyType.getType();
        QualifiedName propertyName = propertyType.getName();
        
        Object value = null;
        if ( type == Types.NUMERIC || type == Types.DOUBLE ) {
            try {
                value = new Double( s );
            } catch (NumberFormatException e) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName, "Double" );
                throw new OGCWebServiceException( msg );
            }
        } else if ( type == Types.INTEGER ) {
            try {            
                value = new Integer( s );
            } catch (NumberFormatException e) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName, "Integer" );
                throw new OGCWebServiceException( msg );
            }            
        } else if ( type == Types.DECIMAL || type == Types.FLOAT ) {
            try {
                value = new Float( s );
            } catch (NumberFormatException e) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName, "Float" );
                throw new OGCWebServiceException( msg );
            }            
        } else if ( type == Types.BOOLEAN ) {
            value = new Boolean( s );
        } else if ( type == Types.VARCHAR ) {
            value = s;
        } else if ( type == Types.DATE || type == Types.TIMESTAMP) {
            try {
                value = TimeTools.createCalendar( s ).getTime();
            } catch (NumberFormatException e) {
                String msg = Messages.format( "ERROR_CONVERTING_PROPERTY", s, propertyName, "Date" );
                throw new OGCWebServiceException( msg );
            }            
        } else {
            String typeString = "" + type;
            try {
                typeString = Types.getTypeNameForSQLTypeCode(type);
            } catch (UnknownTypeException e) {
                LOG.logError ("No type name for code: " + type);
            }
            String msg = Messages.format( "ERROR_UNHANDLED_TYPE", "" + typeString );
            LOG.logError( msg );
            throw new OGCWebServiceException( msg );
        }        
        return value;
    }    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Validator.java,v $
Revision 1.5  2006/08/31 15:00:50  mschneider
Javadoc fixes.

Revision 1.4  2006/08/21 15:47:59  mschneider
Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

Revision 1.3  2006/08/07 06:35:09  poth
unneccessary else block removed

Revision 1.2  2006/05/23 16:07:59  mschneider
Improved javadoc.

Revision 1.1  2006/04/10 16:38:52  mschneider
Initial version.

********************************************************************** */
