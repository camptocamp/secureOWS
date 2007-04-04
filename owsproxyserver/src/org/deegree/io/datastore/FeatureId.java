//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/FeatureId.java,v 1.20 2006/09/20 11:35:41 mschneider Exp $
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

package org.deegree.io.datastore;

import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.idgenerator.IdGenerationException;
import org.deegree.io.datastore.schema.MappedGMLId;
import org.deegree.io.datastore.schema.content.MappingField;

/**
 * Used to identify persistent (stored) feature instances.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.20 $, $Date: 2006/09/20 11:35:41 $
 */
public class FeatureId {

    private MappedGMLId fidDefinition;

    private Object[] values;

    /**
     * Creates a new instance of <code>FeatureId</code> from the given parameters.
     * 
     * @param fidDefinition
     *            cannot be null
     * @param values
     */
    public FeatureId( MappedGMLId fidDefinition, Object[] values ) {
        this.fidDefinition = fidDefinition;
        this.values = values;
    }

    /**
     * Creates a new instance of <code>FeatureId</code> from the given parameters.
     * 
     * @param fidDefinition
     *            cannot be null
     * @param fid
     *            cannot be null
     * @throws IdGenerationException
     */
    public FeatureId( MappedGMLId fidDefinition, String fid ) throws IdGenerationException {
        this.fidDefinition = fidDefinition;
        this.values = new Object [1];
        this.values [0] = removeFIDPrefix(fid, fidDefinition);
    }    

    /**
     * Return the underlying {@link MappedGMLId}.
     * 
     * @return MappedGMLId
     */
    public MappedGMLId getFidDefinition() {
        return this.fidDefinition;
    }    
    
    /**
     * Returns the number of components that the key consists of.
     * 
     * @return the number of components that the key consists of
     */
    public int getLength() {
        return this.values.length;
    }

    /**
     * Returns all column values of the key.
     * 
     * @return all column values of the key
     */
    public Object[] getValues() {
        return this.values;
    }

    /**
     * Returns a certain column value of the key.
     * 
     * @param i
     *            requested column 
     * @return the requested column value of the key
     */    
    public Object getValue( int i ) {
        return this.values[i];
    }

    /**
     * Returns the canonical textual representation, i.e. the key components, separated by the
     * separator defined in the associated {@link MappedGMLId}. 
     * 
     * @return the canonical textual representation
     */
    public String getAsString() {
        StringBuffer sb = new StringBuffer( fidDefinition.getPrefix() );
        for (int i = 0; i < this.values.length; i++) {
            sb.append( values[i] );
            if ( i != this.values.length - 1 ) {
                sb.append( fidDefinition.getSeparator() );
            }
        }
        return sb.toString();
    }

    /**
     * Returns a hash code value for the object.
     * 
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hashCode = fidDefinition.hashCode();
        for (int i = 0; i < this.values.length; i++) {
            hashCode += this.values[i].toString().hashCode();
        }
        return hashCode;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj
     *            Indicates whether some other object is "equal to" this one.
     * 
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null
            || !( obj instanceof FeatureId ) ) {
            return false;
        }
        FeatureId that = (FeatureId) obj;
        if ( this.fidDefinition != that.fidDefinition ) {
            return false;
        }
        if ( this.values == null
            && that.values == null ) {
            return true;
        }
        if ( ( this.values != null && that.values == null )
            || ( this.values == null && that.values != null )
            || ( this.values.length != that.values.length ) ) {
            return false;
        }
        for (int i = 0; i < this.values.length; i++) {
            if ( ( this.values[i] != null && that.values[i] == null )
                || ( this.values[i] == null && that.values[i] != null ) ) {
                return false;
            }
            if ( this.values[i] != null
                && that.values[i] != null ) {
                if ( !this.values[i].equals( that.values[i] ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "fid=" );
        sb.append( getAsString() );
        sb.append( ", " );
        MappingField[] fidFields = fidDefinition.getIdFields();
        for (int i = 0; i < fidFields.length; i++) {
            sb.append( fidFields[i].getField() );
            sb.append( "=" );
            sb.append( values[i] );
            if ( i != fidFields.length - 1 ) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }

    /**
     * Removes the prefix from the given feature id.
     * <p>
     * The prefix is taken from the given gml:id mapping.
     * 
     * @param id
     *            feature id (including prefix).
     * @param idMapping
     *            target gml:id mapping (where the fid will be stored)
     * @return feature id (without prefix) as an object of the right type (matching the table column).
     * @throws IdGenerationException
     *             if the given fid does not begin with the expected prefix from the gml:id mapping
     */
    public static Object removeFIDPrefix( String id, MappedGMLId idMapping ) throws IdGenerationException {
        Object fidObject = null;
        String plainIdValue = id;
        String prefix = idMapping.getPrefix();
        if ( prefix != null && prefix.length() > 0 ) {
            if ( !id.startsWith( prefix ) ) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_ID_NO_PREFIX", id, prefix );
                throw new IdGenerationException( msg );
            }
            plainIdValue = id.substring( prefix.length() );
        }

        if (idMapping.getIdFields().length > 1) {
            String msg = "Compound feature ids not supported in FeatureId.removeFIDPrefix().";
            throw new IdGenerationException( msg );
        }
        
        int sqlTypeCode = idMapping.getIdFields()[0].getType();
        switch (sqlTypeCode) {
        case Types.NUMERIC:
        case Types.DOUBLE: {
            try {
                fidObject = Double.parseDouble (plainIdValue);
            } catch (NumberFormatException e) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_ID_CONVERT", plainIdValue, "Double" );
                throw new IdGenerationException( msg );
            }
            break;
        }
        case Types.FLOAT: {
            try {
                fidObject = Float.parseFloat (plainIdValue);
            } catch (NumberFormatException e) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_ID_CONVERT", plainIdValue, "Float" );
                throw new IdGenerationException( msg );
            }
            break;
        }        
        case Types.INTEGER: {
            try {
                fidObject = Integer.parseInt (plainIdValue);
            } catch (NumberFormatException e) {
                String msg = Messages.getMessage( "DATASTORE_FEATURE_ID_CONVERT", plainIdValue, "Integer" );
                throw new IdGenerationException( msg );
            }
            break;
        }
        case Types.VARCHAR: {
            fidObject = plainIdValue;
            break;
        }        
        default: {
            String msg = null;
            try {
                msg = Messages.getMessage( "DATASTORE_FEATURE_ID_CONVERT", plainIdValue, Types
                    .getTypeNameForSQLTypeCode( sqlTypeCode ) );
            } catch (UnknownTypeException e) {
                throw new IdGenerationException( e.getMessage() );
            }
            throw new IdGenerationException( msg );
        }
        }        
        return fidObject;
    }    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: FeatureId.java,v $
Revision 1.20  2006/09/20 11:35:41  mschneider
Merged datastore related messages with org.deegree.18n.

Revision 1.19  2006/08/28 16:44:25  mschneider
Javadoc fixes.

Revision 1.18  2006/08/24 06:40:05  poth
File header corrected

Revision 1.17  2006/08/21 16:42:01  mschneider
Refactored due to cleanup (and splitting) of org.deegree.io.datastore.schema package.

Revision 1.16  2006/06/01 12:15:25  mschneider
Fixed header + footer.

********************************************************************** */