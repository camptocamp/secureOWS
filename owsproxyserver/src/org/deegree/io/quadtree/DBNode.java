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
package org.deegree.io.quadtree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * Represents a node of a {@link DBQuadtree}.  Nodes contain items which have a spatial extent
 * corresponding to the node's position in the quadtree.
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/20 07:56:00 $
 *
 * @since 2.0
 */
class DBNode implements Node {
    
    private static ILogger LOG = LoggerFactory.getLogger( DBNode.class );

    private String id = null;
	private int level;
	private String[] fk_subnode = new String[4];
	private Envelope envelope = null;
    private JDBCConnection jdbc = null;
    private DBQuadtree qt = null;
    private String indexName = null;

    /**
     * 
     * @param id
     * @param jdbc
     */
	public DBNode(String id, Envelope env, DBQuadtree qt, String indexName, 
                JDBCConnection jdbc, int level) throws IndexException {
        this.id = id;
        this.envelope = env;
        this.jdbc = jdbc;
        this.qt = qt;
        this.level = level;
        this.indexName = indexName.trim();
        if ( !load() ) {
            create();
        }
	}
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Node#getId()
     */
    public String getId() {
        return id;
    }
    
    Envelope getEnvelope() {
        return envelope;
    }

    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Node#insert(java.lang.Object, org.deegree.model.spatialschema.Envelope)
     */
    public void insert(Object item, Envelope itemEnv) throws IndexException {        
        if ( level != qt.getDepth()  ) {            
            if ( !envelope.intersects( itemEnv ) ) {
            	System.out.println( "node envelope: " + envelope);            	
            	System.out.println( "item envelope: " + itemEnv);
                throw new IndexException( "item envelope does not intersects node envelope" );
            }
            // split the envelope of this node into four equal sized quarters
            Envelope[] envs = split();
            boolean inter = false;
            int k = 0;
            for (int i = 0; i < envs.length; i++) {
                if ( envs[i].intersects( itemEnv ) ) {
                    k++;
                    // check which subnodes are intersected by the
                    // items envelope; just this nodes 
                    // are considered for futher processing
                    if ( fk_subnode[i] == null || fk_subnode[i].trim().length() == 0) {
                        inter = true;
                        fk_subnode[i] = id + '_' + i;
                    }                       
                    Node node = qt.getFromCache( fk_subnode[i] );
                    if ( node == null ) {               
                        node = new DBNode( fk_subnode[i], envs[i], qt, indexName, jdbc, level + 1 );
                        qt.addToCache( node );
                    } 
                    node.insert( item, itemEnv );                    
                }
            }  
            if ( k == 4 ) {
                assigneItem( item );
            }
            qt.addToCache( this );
            if ( inter ) {
                update();     
            }
        }else {
            assigneItem( item );
        }
           
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Node#query(org.deegree.model.spatialschema.Envelope, java.util.List, int)
     */
    public List query(Envelope searchEnv, List visitor, int level) throws IndexException {
        /*      
        if ( level == qt.getDepth() || (searchEnv.getWidth() > envelope.getWidth() || 
             searchEnv.getHeight() > envelope.getHeight()) ) {
            addAssignedItems( visitor );
        } else {
        */
        addAssignedItems( visitor );
        if ( level != qt.getDepth() ) {           
            Envelope[] envs = split();
            for (int i = 0; i < envs.length; i++) {
                if ( fk_subnode[i] != null && envs[i].intersects( searchEnv ) ) {
                    // check which subnodes are intersected by the
                    // items envelope; just this nodes 
                    // are considered for futher processing
                    Node node = new DBNode( fk_subnode[i], envs[i], qt, indexName, jdbc, level + 1 );
                    node.query( searchEnv, visitor, level + 1 );
                }
            }            
        } 
        return visitor;
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Node#deleteItem(java.lang.Object)
     */
    public void deleteItem(Object item) {
        if ( level == qt.getDepth() ) {
            
        } else {
            
        }
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Node#deleteRange(org.deegree.model.spatialschema.Envelope)
     */
    public void deleteRange(Envelope envelope) {
        if ( level == qt.getDepth() ) {
            
        } else {
            
        }
    }
    
    /**
     * load all parameter from of node from the database
     * returns true is a node with current ID is already available 
     * from the database 
     *
     */
    private boolean load() throws IndexException {
        Connection con = null;
        DBConnectionPool pool = null;
        boolean available = true;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 100 );     
            sb.append( "Select * from " ).append( indexName );
            sb.append( " where ID = '").append( id ).append( "'" );
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( sb.toString() );
            if ( rs.next() ) {
                double minx = rs.getFloat( "MINX" );
                double miny = rs.getFloat( "MINY" );
                double maxx = rs.getFloat( "MAXX" );
                double maxy = rs.getFloat( "MAXY" );
                envelope = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, null );
                fk_subnode[0] = rs.getString( "FK_SUBNODE1" );
                fk_subnode[1] = rs.getString( "FK_SUBNODE2" );
                fk_subnode[2] = rs.getString( "FK_SUBNODE3" );
                fk_subnode[3] = rs.getString( "FK_SUBNODE4" );
            } else {
                available = false;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {         
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not load node definition from database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return available;
    }
    
    /**
     * updates the database representation of the current node
     * @throws IndexException
     */
    private void update() throws IndexException {
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "UPDATE ").append( indexName ).append( " set ");
            boolean sub = false;
            for (int i = 0; i < fk_subnode.length; i++) {
                if ( fk_subnode[i] != null ) {
                    sb.append( " FK_SUBNODE").append( i+1 ).append( "='" );
                    sb.append( fk_subnode[i] ).append( "' ,");
                    sub = true;
                }
            }
            if ( sub ) {
                // just execute update if at least one sub node != null
                sb = new StringBuffer( sb.substring( 0, sb.length()-1 ) );
                sb.append( " where ID = '" ).append( id ).append( "'" );
                Statement stmt = con.createStatement();
                stmt.execute( sb.toString() );
                stmt.close();
            }
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not update node definition at database " +
                                      "for node: " + id , e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * creates a new node with current ID and envelope
     * @throws IndexException
     */
    void create() throws IndexException {
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "INSERT INTO " ).append( indexName );
            sb.append( " ( ID, MINX, MINY, MAXX , MAXY ) " );
            sb.append( "VALUES ( ?, ?, ?, ?, ? ) " );
            PreparedStatement stmt = con.prepareStatement( sb.toString() );
            stmt.setString( 1, id );
            stmt.setFloat( 2, (float)envelope.getMin().getX() );
            stmt.setFloat( 3, (float)envelope.getMin().getY() );
            stmt.setFloat( 4, (float)envelope.getMax().getX() );
            stmt.setFloat( 5, (float)envelope.getMax().getY() );
            stmt.execute();            
            stmt.close();
        } catch (Exception e) {    
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * assignes an item to a node by creating a new row in the
     * JT_QTNODE_ITEM table
     * @param Item
     */
    private void assigneItem(Object item) throws IndexException {
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "INSERT INTO " ).append( indexName.trim() ).append( "_ITEM ");
            sb.append( "( FK_QTNODE, FK_ITEM ) " ).append( "VALUES ( ?, ? ) " );
            PreparedStatement stmt = con.prepareStatement( sb.toString() );
            stmt.setString( 1, id );
            if ( item instanceof Integer ) {
                stmt.setInt( 2, (Integer)item );
            } else {
                stmt.setString( 2, item.toString() );
            }            
            stmt.execute();            
            stmt.close();
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * adds all item(IDs) assigned to this node
     * 
     * @param visitor
     * @return
     * @throws IndexException
     */
    private List addAssignedItems(List visitor) throws IndexException {
        
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 100 );
            sb.append( "SELECT DISTINCT FK_ITEM from ").append( indexName ).append( "_ITEM" );
            sb.append( " where " ).append( "FK_QTNODE = '" ).append( id ).append( "'" );
            Statement stmt = con.createStatement();
            ResultSet rs  = stmt.executeQuery( sb.toString() );
            while ( rs.next() ) {
                Object s = rs.getObject( 1 );
                if ( !visitor.contains( s ) ) {
                    visitor.add( s );
                }
            }
            stmt.close();
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new IndexException( "could not create node definition at database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        
        return visitor;
    }
    
    private Envelope[] split() {
        Envelope[] envs = new Envelope[4]; 
        double nW = envelope.getWidth() / 2d;
        double nH = envelope.getHeight() / 2d;
        
        envs[0] = GeometryFactory.createEnvelope( envelope.getMin().getX(), 
                                                  envelope.getMin().getY(),
                                                  envelope.getMin().getX() + nW, 
                                                  envelope.getMin().getY() + nH, null );
        envs[1] = GeometryFactory.createEnvelope( envelope.getMin().getX()+ nW, 
                                                  envelope.getMin().getY(),
                                                  envelope.getMin().getX() + (2*nW), 
                                                  envelope.getMin().getY() + nH, null );
        envs[2] = GeometryFactory.createEnvelope( envelope.getMin().getX() + nW, 
                                                  envelope.getMin().getY() + nH,
                                                  envelope.getMin().getX() + (2*nW), 
                                                  envelope.getMin().getY() + (2*nH), null );
        envs[3] = GeometryFactory.createEnvelope( envelope.getMin().getX(), 
                                                  envelope.getMin().getY() + nH,
                                                  envelope.getMin().getX() + nW,
                                                  envelope.getMin().getY() + (2*nH), null );
        
        return envs;
    }
	
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBNode.java,v $
Revision 1.1  2006/10/20 07:56:00  poth
core methods extracted to interfaces

Revision 1.11  2006/10/13 08:23:28  poth
class defined as package protected

Revision 1.10  2006/07/26 12:43:41  poth
support for alternative object identifier type (integer)

Revision 1.9  2006/06/12 10:59:49  schmitz
Updated the Quadtree framework to work with INGRES database backends.

Revision 1.8  2006/05/18 14:08:54  poth
file comments completed


********************************************************************** */