//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/datatypes/Types.java,v 1.30 2006/12/04 18:21:33 mschneider Exp $
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
package org.deegree.datatypes;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.BootLogger;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * General data type constants definition. the type values are the same as in
 * <code>java.sql.Types<code>. Except for several geometry types, 
 * <code>UNKNOWN</code>, <code>FEATURE</code>, <code>FEATURES</code> and 
 * <code>FEATURECOLLECTION</code> that are not known by 
 * <code>java.sql.Types</code>.
 * <p>
 * NOTE: Generally, it would be feasible to extend <code>java.sql.Types</code>,
 * but unfortunately, this is not possible, as it's default constructor is not visible. 
 * </p> 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.30 $, $Date: 2006/12/04 18:21:33 $
 * 
 * @see java.sql.Types
 */
public final class Types {
    
    private static ILogger LOG = LoggerFactory.getLogger( Types.class );

    private static URI GMLNS = CommonNamespaces.GMLNS;

    // generic sql types
    
    public final static int ARRAY = java.sql.Types.ARRAY;    

    public final static int BIGINT = java.sql.Types.BIGINT;    

    public final static int BINARY = java.sql.Types.BINARY;    
    
    public final static int BIT = java.sql.Types.BIT;

    public final static int BLOB = java.sql.Types.BLOB;

    public final static int BOOLEAN = java.sql.Types.BOOLEAN;

    public final static int CHAR = java.sql.Types.CHAR;    
    
    public final static int CLOB = java.sql.Types.CLOB;

    public final static int DATALINK = java.sql.Types.DATALINK;

    public final static int DATE = java.sql.Types.DATE;
    
    public final static int DECIMAL = java.sql.Types.DECIMAL;    
    
    public final static int DISTINCT = java.sql.Types.DISTINCT;    
    
    public final static int DOUBLE = java.sql.Types.DOUBLE;    
        
    public final static int FLOAT = java.sql.Types.FLOAT;    
    
    public final static int INTEGER = java.sql.Types.INTEGER;
    
    public final static int JAVA_OBJECT = java.sql.Types.JAVA_OBJECT;    
    
    public final static int LONGVARBINARY = java.sql.Types.LONGVARBINARY;    
    
    public final static int LONGVARCHAR = java.sql.Types.LONGVARCHAR;    
    
    public final static int NULL = java.sql.Types.NULL;    
    
    public final static int NUMERIC = java.sql.Types.NUMERIC;    
    
    public final static int OTHER = java.sql.Types.OTHER;

    public final static int REAL = java.sql.Types.REAL;    
    
    public final static int REF = java.sql.Types.REF;    
    
    public final static int SMALLINT = java.sql.Types.SMALLINT;
    
    public final static int STRUCT = java.sql.Types.STRUCT;    

    public final static int TIME = java.sql.Types.TIME;    

    public final static int TIMESTAMP = java.sql.Types.TIMESTAMP;    
    
    public final static int TINYINT = java.sql.Types.TINYINT;

    public final static int VARBINARY = java.sql.Types.VARBINARY;

    public final static int VARCHAR = java.sql.Types.VARCHAR;

    // geometry + gml types

    public static final int GEOMETRY = java.sql.Types.VARCHAR + 10000;

    public static final int MULTIGEOMETRY = java.sql.Types.VARCHAR + 10001;

    public static final int FEATURE = java.sql.Types.VARCHAR + 10002;

    public static final int FEATURECOLLECTION = java.sql.Types.VARCHAR + 10004;    
    
    public static final int POINT = java.sql.Types.VARCHAR + 11000;

    public static final int CURVE = java.sql.Types.VARCHAR + 11001;

    public static final int SURFACE = java.sql.Types.VARCHAR + 11002;

    public static final int MULTIPOINT = java.sql.Types.VARCHAR + 11003;

    public static final int MULTICURVE = java.sql.Types.VARCHAR + 11004;

    public static final int MULTISURFACE = java.sql.Types.VARCHAR + 11005;

    public static final int ENVELOPE = java.sql.Types.VARCHAR + 11006;

    public static final QualifiedName GEOMETRY_PROPERTY_NAME = new QualifiedName(
        "GeometryPropertyType", GMLNS );

    public static final QualifiedName MULTI_GEOMETRY_PROPERTY_NAME = new QualifiedName(
        "MultiGeometryPropertyType", GMLNS );

    public static final QualifiedName FEATURE_PROPERTY_NAME = new QualifiedName(
        "FeaturePropertyType", GMLNS );

    // TODO check if this is really needed
    public static final QualifiedName FEATURE_ARRAY_PROPERTY_NAME = new QualifiedName(
        "FeatureArrayPropertyType", GMLNS );

    // key instances: Integer, value instances: String
    private static Map typeNameMap = new HashMap ();

    // key instances: String, value instances: Integer    
    private static Map typeCodeMap = new HashMap ();    

    static {
        try {
            Field[] fields = java.sql.Types.class.getFields();
            for (int i = 0; i < fields.length; i++) {
                String typeName = fields [i].getName();
                Integer typeCode = (Integer) fields[i].get( null );
                typeNameMap.put (typeCode, typeName);
                typeCodeMap.put (typeName, typeCode);
            }
        } catch (Exception e) {
            BootLogger.logError("Error populating sql type code maps: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the generic sql type code for the given type name.
     * 
     * @param typeName
     * @return the generic sql type code for the given type name.
     * @throws UnknownTypeException
     *             if the type name is not an sql type name
     * @see java.sql.Types
     */
    public static int getTypeCodeForSQLType (String typeName) throws UnknownTypeException {
        Integer typeCode = (Integer) typeCodeMap.get(typeName);
        if (typeCode == null) {
            throw new UnknownTypeException ("Type name '" + typeName 
                + "' does not denote an sql type.");
        }
        return typeCode.intValue();
    }

    /**
     * Returns the generic sql type name for the given type code.
     * 
     * @param typeName
     * @return the generic sql type name for the given type code.
     * @throws UnknownTypeException
     *             if the type code is not an sql type code
     * @see java.sql.Types
     */    
    public static String getTypeNameForSQLTypeCode (int typeCode) throws UnknownTypeException {
        String typeName = (String) typeNameMap.get(new Integer (typeCode));
        if (typeName == null) {
            throw new UnknownTypeException ("Type code '" + typeCode 
                + "' does not denote an sql type.");
        }
        return typeName;
    }    
    
    /**
     * mapping between GML-typenames and java-classnames for GML-geometry types
     * 
     * @param gmlTypeName
     *            the name of the GML type name
     * @return the internal type
     * @throws UnknownTypeException
     *             if the given name cannot be mapped to a known type.
     */
    public static int getJavaTypeForGMLType( String gmlTypeName ) throws UnknownTypeException {
        if ( "GeometryPropertyType".equals( gmlTypeName ) )
            return Types.GEOMETRY;

        if ( "PointPropertyType".equals( gmlTypeName ) )
            // return Types.POINT;
            return Types.GEOMETRY;

        if ( "MultiPointPropertyType".equals( gmlTypeName ) )
            // return Types.MULTIPOINT;
            return Types.GEOMETRY;

        if ( "PolygonPropertyType".equals( gmlTypeName ) )
            // return Types.SURFACE;
            return Types.GEOMETRY;

        if ( "MultiPolygonPropertyType".equals( gmlTypeName ) )
            // return Types.MULTISURFACE;
            return Types.GEOMETRY;

        if ( "LineStringPropertyType".equals( gmlTypeName ) )
            // return Types.CURVE;
            return Types.GEOMETRY;

        if ( "MultiLineStringPropertyType".equals( gmlTypeName ) )
            // return Types.MULTICURVE;
            return Types.GEOMETRY;
        
        if ( "CurvePropertyType".equals( gmlTypeName ) )
            // return Types.POINT;
            return Types.GEOMETRY;

        if ( "MultiCurvePropertyType".equals( gmlTypeName ) )
            // return Types.POINT;
            return Types.GEOMETRY;        

        if ( "SurfacePropertyType".equals( gmlTypeName ) )
            // return Types.POINT;
            return Types.GEOMETRY;

        if ( "MultiSurfacePropertyType".equals( gmlTypeName ) )
            // return Types.POINT;
            return Types.GEOMETRY;        
        
        throw new UnknownTypeException( "Unsupported Type: '"
            + gmlTypeName + "'" );
    }

    /**
     * mapping between xml-typenames and java-classnames for XMLSCHEMA-simple types
     * 
     * @param schemaTypeName
     *            of the XML schema type
     * @return the internal type
     * @throws UnknownTypeException
     *             if the given name cannot be mapped to a known type.
     * @todo TODO map them all over registry
     */
    public static int getJavaTypeForXSDType( String schemaTypeName ) throws UnknownTypeException {

        if ( "integer".equals( schemaTypeName ) )
            return Types.INTEGER;

        if ( "string".equals( schemaTypeName ) )
            return Types.VARCHAR;

        if ( "date".equals( schemaTypeName ) )
            return Types.DATE;

        if ( "boolean".equals( schemaTypeName ) )
            return Types.BOOLEAN;

        if ( "float".equals( schemaTypeName ) )
            return Types.FLOAT;

        if ( "double".equals( schemaTypeName ) )
            return Types.DOUBLE;

        if ( "decimal".equals( schemaTypeName ) )
            return Types.DECIMAL;

        if ( "dateTime".equals( schemaTypeName ) )
            return Types.TIMESTAMP;

        if ( "time".equals( schemaTypeName ) )
            return Types.TIME;

        if ( "date".equals( schemaTypeName ) )
            return Types.DATE;

        if ( "anyURI".equals( schemaTypeName ) )
            return Types.VARCHAR;        
        
        throw new UnknownTypeException( "Unsupported Type:"
            + schemaTypeName );
    }   

    /**
     * 
     * @param type
     *            SQL datatype code
     * @param precision
     *            precision (just used for type NUMERIC)
     * @return
     */
    public static String getXSDTypeForSQLType( int type, int precision ) {
        String s = null;

        switch (type) {
        case Types.VARCHAR:
        case Types.CHAR:            
            s = "string";
            break;
        case Types.NUMERIC: {
            if ( precision <= 1 ) {
                s = "integer";
                break;
            } 
            s = "double";
            break;            
        }
        case Types.DECIMAL:
            s = "decimal";
            break;
        case Types.DOUBLE:
        case Types.REAL:
            s = "double";
            break;
        case Types.FLOAT:        
            s = "float";
            break;
        case Types.INTEGER:
        case Types.SMALLINT:            
            s = "integer";
            break;
        case Types.TIMESTAMP:
        case Types.TIME:
        case Types.DATE:
            s = "dateTime";
            break;
        case Types.CLOB:
            s = "string";
            break;
        case Types.BIT:
        case Types.BOOLEAN:
            s = "boolean";
            break;
        case Types.GEOMETRY:
        case Types.OTHER:
        case Types.STRUCT:
            s = "gml:GeometryPropertyType";
            break;
        case Types.FEATURE:
            s = "gml:FeaturePropertyType";
            break;            
        default:
            LOG.logWarning( "could not determine XSDType for SQLType; using 'XXX': " + type );
            s = "code: " + type;
        }
        return s;
    }

}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Types.java,v $
Revision 1.30  2006/12/04 18:21:33  mschneider
Some more hacks for missing gml types.

Revision 1.29  2006/11/23 15:22:38  mschneider
Added "anyURI" in #getJavaTypeForXSDType( String ).

Revision 1.28  2006/09/15 19:22:26  poth
bug fix - support for REAL added

Revision 1.27  2006/08/21 15:41:31  mschneider
Removed unused FeatureArrayPropertyType.

Revision 1.26  2006/07/12 14:40:03  poth
*** empty log message ***

********************************************************************** */