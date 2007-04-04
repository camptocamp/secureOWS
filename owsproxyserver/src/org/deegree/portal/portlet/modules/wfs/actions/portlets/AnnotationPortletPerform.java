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
package org.deegree.portal.portlet.modules.wfs.actions.portlets;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.portal.Portlet;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.portal.PortalException;

/**
 * 
 * 
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.8 $, $Date: 2006/07/12 14:46:18 $
 *
 * @since 2.0
 */
public class AnnotationPortletPerform extends WFSClientPortletPerform {
    
    private static final ILogger LOG = LoggerFactory.getLogger( AnnotationPortletPerform.class );

    /**
     * 
     * @param request
     * @param portlet
     * @param servletContext
     */
    public AnnotationPortletPerform(HttpServletRequest request, Portlet portlet, 
                                    ServletContext servletContext) {
        super(request, portlet, servletContext);
    }
    
    /**
     * performs a transaction against a WFS-T or a database. The backend type
     * to be used by a transaction depends on a portlets initParameters. 
     * @throws PortalException 
     */
    public void doTransaction() throws PortalException {

        System.out.println( parameter );
        if ( getInitParam( "driver" ) != null ) {
            doDatabaseTransaction();
        } else {
            // handle transaction against a WFS 
            super.doTransaction();
        }
    }

    /**
     * performs a direct transaction into a database
     */
    private void doDatabaseTransaction() throws PortalException {
        
        Connection con =  null;
        LOG.logDebug( "connecting database for insert ... " );
        try {
            Driver drv = (Driver) Class.forName( getInitParam( "driver" ) ).newInstance();
            DriverManager.registerDriver( drv );

            con =  DriverManager.getConnection( getInitParam( "url" ),  getInitParam( "user" ), 
                                                getInitParam( "password" ) );
        } catch (SQLException e) {
            LOG.logError( "could not establish database connection: " +
                          getInitParam( "url" ), e );
            throw new PortalException( "could not establish database connection: " +
                                       getInitParam( "url" ), e ); 
        } catch (Exception e) {
            LOG.logError( "could not initialize driver class: " + 
                                       getInitParam( "driver" ), e );
            throw new PortalException( "could not initialize driver class: " + 
                                       getInitParam( "driver" ), e );       
        }
                
        PreparedStatement stmt = null;
        try {
            if ( "INSERT".equals( parameter.get( "OPERATION" ) ) ) {
                String sql = createGeometryFragment( getInitParam( "SQLInsertTemplate" ) );
                stmt = con.prepareStatement( sql );     
                LOG.logDebug( "perform insert " + sql );
            } else {
                throw new PortalException( "not a supported Operation: " + 
                                           parameter.get( "OPERATION" ) );
            }
            stmt.setDate(1, new Date( System.currentTimeMillis()) );
            String title = (String)parameter.get( "TITLE" );
            if ( title == null ) {
                stmt.setNull( 2, Types.VARCHAR );
            } else {
                stmt.setString( 2, title );
            }
            String annotation = (String)parameter.get( "ANNOTATION" );
            if ( annotation == null ) {
                stmt.setNull( 3, Types.VARCHAR );
            } else {
                stmt.setString( 3, annotation );
            }
            String category = (String)parameter.get( "CATEGORY" );
            if ( category == null ) {
               stmt.setNull( 4, Types.NUMERIC );
            } else {
               stmt.setInt( 4, -1 );
            }
            // annotation location
            stmt.execute();
        } catch (SQLException e) {
            try {
                stmt.close();
            } catch (Exception e1) {}
            try {
                con.close();
            } catch (Exception e1) {}
            LOG.logError( "could not perform insert operation ", e );
            throw new PortalException( "could not perform insert operation ", e );            
        }
        
    }
    
    /**
     * 
     * @return
     */
    private String createGeometryFragment(String sql)  {
        sql = StringTools.replace( sql, "$SRID", getInitParam( "srid" ), false );
        sql = StringTools.replace( sql, "$X", (String)parameter.get( "X" ), false );
        sql = StringTools.replace( sql, "$Y", (String)parameter.get( "Y" ), false );
        return sql;
    }
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AnnotationPortletPerform.java,v $
Revision 1.8  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
