//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/io/datastore/sql/VirtualContentProvider.java,v 1.4 2006/09/26 13:03:03 mschneider Exp $
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
package org.deegree.io.datastore.sql;

import java.sql.Connection;
import java.util.List;

import org.deegree.datatypes.Types;
import org.deegree.i18n.Messages;
import org.deegree.io.datastore.schema.content.ConstantContent;
import org.deegree.io.datastore.schema.content.FieldContent;
import org.deegree.io.datastore.schema.content.FunctionParam;
import org.deegree.io.datastore.schema.content.SQLFunctionCall;
import org.deegree.io.datastore.schema.content.SpecialContent;
import org.deegree.io.datastore.schema.content.SpecialContent.VARIABLE;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterTools;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * Responsible for determining the value of properties that are mapped to {@link SQLFunctionCall}s.
 * <p>
 * This involves the lookup of the values of variables ({@link SpecialContent} instances).
 * 
 * <table border="1">
 * <tr>
 *   <th>Variable name</th>
 *   <th>Description</th>
 * </tr>
 * <tr>
 *   <td>$QUERY.BBOX</td>
 *   <td>Bounding box of the query (null if it is not present).</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @see SQLFunctionCall
 * @see SpecialContent
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.4 $, $Date: 2006/09/26 13:03:03 $
 */
public class VirtualContentProvider {

    // used in case that no BBOX is available
    private static final Envelope DEFAULT_BBOX = GeometryFactory.createEnvelope( 0.0, 0.0, 0.0,
                                                                                 0.0, null );

    private Filter filter;

    private AbstractSQLDatastore ds;

    private Connection conn;

    /**
     * Creates a new instance of {@link VirtualContentProvider}.
     * 
     * @param filter
     * @param ds
     * @param conn
     */
    VirtualContentProvider( Filter filter, AbstractSQLDatastore ds, Connection conn ) {
        this.filter = filter;
        this.ds = ds;
        this.conn = conn;
    }

    /**
     * Appends a {@link SQLFunctionCall} to the given {@link StatementBuffer}.
     * <p>
     * This includes the correct qualification of all columns that are used as
     * {@link FunctionParam}s. 
     * 
     * @param query
     * @param tableAlias
     * @param call
     */
    public void appendSQLFunctionCall( StatementBuffer query, String tableAlias,
                                      SQLFunctionCall call ) {

        String callString = call.getCall();
        int[] usedVars = call.getUsedVars();
        List<FunctionParam> params = call.getParams();
        for ( int j = 0; j < usedVars.length; j++ ) {
            int varNo = usedVars[j];
            String varString = "\\$" + varNo;
            FunctionParam param = params.get( varNo - 1 );
            if ( param instanceof FieldContent ) {
                String replace = tableAlias + "." + ( (FieldContent) param ).getField().getField();
                callString = callString.replaceAll( varString, replace );
            } else if ( param instanceof ConstantContent ) {
                String replace = ( (ConstantContent) param ).getValue();
                callString = callString.replaceAll( varString, replace );
            } else if ( param instanceof SpecialContent ) {
                appendSpecialContentValue( query, (SpecialContent) param );
                callString = callString.replaceFirst( varString, "?" );
            } else {
                assert false;
            }
        }
        query.append( callString );
    }

    /**
     * Appends the variable from a {@link SpecialContent} property to the given
     * {@link StatementBuffer}.
     * 
     * @param query
     * @param param
     */
    public void appendSpecialContentValue( StatementBuffer query, SpecialContent param ) {

        VARIABLE var = param.getVariable();

        switch ( var ) {
        case QUERY_BBOX: {

            Object bboxDBGeometry = null;

            try {
                Envelope requestBBOX = DEFAULT_BBOX;

                if ( this.filter instanceof ComplexFilter ) {
                    Object[] objects = FilterTools.extractFirstBBOX( (ComplexFilter) filter );
                    if ( objects[0] != null ) {
                        requestBBOX = (Envelope) objects[0];
                    }
                }
                Geometry geometry = GeometryFactory.createSurface( requestBBOX, null );
                bboxDBGeometry = this.ds.convertDeegreeToDBGeometry( geometry, param.getSRS(),
                                                                     this.conn );
            } catch ( Exception e ) {
                String msg = Messages.getMessage( "DATASTORE_EXTRACTBBOX" );
                throw new RuntimeException( msg, e );
            }

            query.addArgument( bboxDBGeometry, Types.OTHER );
            break;
        }
        default: {
            assert false;
        }
        }
    }
}

/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: VirtualContentProvider.java,v $
 Revision 1.4  2006/09/26 13:03:03  mschneider
 Uses internal SRS from SpecialContent now.

 Revision 1.3  2006/09/25 20:30:26  poth
 change required to work with existing csw instances

 Revision 1.2  2006/09/20 11:35:41  mschneider
 Merged datastore related messages with org.deegree.18n.

 Revision 1.1  2006/09/19 14:54:02  mschneider
 Cleaned up handling of VirtualContent, i.e. properties that are mapped to SQLFunctionCalls.

 ********************************************************************** */