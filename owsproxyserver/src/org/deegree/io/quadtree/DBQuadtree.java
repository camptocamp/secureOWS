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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.io.DBConnectionPool;
import org.deegree.io.JDBCConnection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.2 $, $Date: 2006/10/30 09:02:38 $
 *
 * @since 2.0
 */
public class DBQuadtree implements Quadtree  {

	private String fk_root;
	private int depth;
    private int id = 0; 
    private String indexName = null; 
    private JDBCConnection jdbc = null;
    private Map nodeCache = new HashMap(10000);
    private double accuracyX = 0.0001;
    private double accuracyY = 0.0001;

    /**
     * initializes a quadtree already existing in a database 
     * @param id  
     * @param indexName this name will be used to create the table 
     *                  that stores the nodes of a specific quadtree
     * @param jdbc description of database connection
     */
	public DBQuadtree(int id, String indexName, JDBCConnection jdbc) throws IndexException {
	    this.id = id;
        this.jdbc = jdbc;
        this.indexName = indexName;
        readRootNodeId();
	}
    
    /**
     * initializes a quadtree already existing in a database 
     * @param id  
     * @param indexName this name will be used to create the table 
     *                  that stores the nodes of a specific quadtree
     * @param jdbc description of database connection
     * @param accuracyX
     * @param accuracyY
     */
    public DBQuadtree(int id, String indexName, JDBCConnection jdbc,
                      double accuracyX, double accuracyY) throws IndexException {
        this.id = id;
        this.jdbc = jdbc;
        this.indexName = indexName;
        this.accuracyX = accuracyX;
        this.accuracyY = accuracyY;
        readRootNodeId();
    }
    
    Node getFromCache(String id) {
        return (Node)nodeCache.get( id );
    }
    
    void addToCache(Node node) {
        nodeCache.put( node.getId(), node );
    }
   
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#insert(java.lang.Object, org.deegree.model.spatialschema.Envelope)
     */
	public void insert(Object item, Envelope envelope) throws IndexException {       
        Node node = new DBNode( fk_root, null, this, indexName, jdbc, 1 );
        node.insert( item, envelope );
    }
    
    
    public void insert( Object item, Point point ) throws IndexException {
        Node node = new DBNode( fk_root, null, this, indexName, jdbc, 1 );
        Envelope envelope = GeometryFactory.createEnvelope( point.getX() - accuracyX, 
                                                            point.getY() - accuracyY,
                                                            point.getX() + accuracyX,
                                                            point.getY() + accuracyY,
                                                            null );
        node.insert( item, envelope );        
    }

    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#query(org.deegree.model.spatialschema.Envelope)
     */
    public List query(Envelope envelope) throws IndexException {
        List visitor = new ArrayList( 1000 );
        DBNode node = new DBNode( fk_root, null, this, indexName, jdbc, 1 );
        envelope = envelope.createIntersection( node.getEnvelope() );
        if ( envelope == null ) {
            return new ArrayList();
        }
        visitor = node.query( envelope, visitor, 1 );
        return visitor;
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#deleteItem(java.lang.Object)
     */
    public void deleteItem(Object item) {
        
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#deleteRange(org.deegree.model.spatialschema.Envelope)
     */
    public void deleteRange(Envelope envelope) {
        
    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#getDepth()
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * reads the root node from the database
     * @return
     */
    private void readRootNodeId() throws IndexException {
        
        Connection con = null;
        DBConnectionPool pool = null;
        try {
            pool = DBConnectionPool.getInstance();
            con = pool.acquireConnection( jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                                     jdbc.getPassword() );
            
            StringBuffer sb = new StringBuffer( 200 );
            sb.append( "Select FK_ROOT, DEPTH from TAB_QUADTREE where ID = ");
            sb.append( id );

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( sb.toString() );
            if ( rs.next() ) {
                fk_root = rs.getString( "FK_ROOT" );       
                depth = rs.getInt( "DEPTH" );
            } else {
                throw new IndexException( "could not read FK_ROOT and DEPTH for " +
                                          "Quadtree with ID" + id );
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            throw new IndexException( "could not load quadtree definition from database", e );
        } finally {
            try {
                pool.releaseConnection( con, jdbc.getDriver(), jdbc.getURL(), jdbc.getUser(), 
                                        jdbc.getPassword() );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }
    
    /* (non-Javadoc)
     * @see org.deegree.io.quadtree.Quadtree#getRootBoundingBox()
     */
    public Envelope getRootBoundingBox() throws IndexException {
        DBNode node = new DBNode( fk_root, null, this, indexName, jdbc, 1 );
        return node.getEnvelope();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DBQuadtree.java,v $
Revision 1.2  2006/10/30 09:02:38  poth
implementation changed for optimized memory management for MemPointQuadtree

Revision 1.1  2006/10/20 07:56:00  poth
core methods extracted to interfaces

Revision 1.7  2006/05/18 14:08:54  poth
file comments completed

Revision 1.6  2006/05/18 14:07:32  poth
*** empty log message ***

Revision 1.5  2006/05/18 14:07:14  poth
not required typecast removed/ file comment footer added


********************************************************************** */