//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/Lock.java,v 1.8 2006/10/12 16:24:00 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import java.util.ArrayList;

import org.deegree.model.filterencoding.Filter;

/**
 * The purpose of the LockFeature interface is to expose a long term feature locking mechanism to
 * ensure consistency. The lock is considered long term because network latency would make feature
 * locks last relatively longer than native commercial database locks.
 * <p>
 * The LockFeature interface is optional and need only be implemented if the underlying datastore
 * supports (or can be made to support) data locking. In addition, the implementation of locking is
 * completely opaque to the client.
 * 
 * <p>
 * --------------------------------------------------------
 * </p>
 * 
 * @author Andreas Poth <a href="mailto:k.lupp@web.de">Katharina Lupp </a>
 * @version $Revision: 1.8 $ $Date: 2006/10/12 16:24:00 $
 */
public class Lock {

    private ArrayList featureIds = null;

    private Filter filter = null;

    private String handle = null;

    private String lockAction = null;

    private String typeName = null;

    /**
     * default constructor
     */
    Lock() {
        featureIds = new ArrayList();
    }

    /**
     * constructor initializing the class with the <WFSLock>
     */
    Lock( String lockAction, String typeName, String handle, Filter filter, String[] featureIds ) {
        this();
        setLockAction( lockAction );
        setTypeName( typeName );
        setHandle( handle );
        setFilter( filter );
        setFeatureIds( featureIds );
    }

    /**
     * Specify how the lock should be acquired. ALL indicates to try to get all feature locks
     * otherwise fail. SOME indicates to try to get as many feature locks as possible. The default
     * LOCKACTION is ALL.
     * 
     * @uml.property name="lockAction"
     */
    public String getLockAction() {
        return lockAction;
    }

    /**
     * sets <LockAction>
     * 
     * @uml.property name="lockAction"
     */
    public void setLockAction( String lockAction ) {
        this.lockAction = lockAction;
    }

    /**
     * If a filter is not specified, then the optional typeName attribute can be used to specify
     * that all feature instances of a particular feature type should be locked.
     * 
     * @uml.property name="typeName"
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * sets <TypeName>
     * 
     * @uml.property name="typeName"
     */
    public void setTypeName( String typeName ) {
        this.typeName = typeName;
    }

    /**
     * The handle attribute is included to allow a server to associate any text to the response. The
     * purpose of the handle attribute is to provide an error handling mechanism for locating a
     * statement that might fail. Or to identify an InsertResult.
     * 
     * @uml.property name="handle"
     */
    public String getHandle() {
        return handle;
    }

    /**
     * sets the <Handle>
     * 
     * @uml.property name="handle"
     */
    public void setHandle( String handle ) {
        this.handle = handle;
    }

    /**
     * A filter specification describes a set of features to operate upon. The format of the filter
     * is defined in the OGC Filter Encoding Specification. Optional. No default. Prerequisite:
     * TYPENAME
     * 
     * @uml.property name="filter"
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * sets the <Filter>
     * 
     * @uml.property name="filter"
     */
    public void setFilter( Filter filter ) {
        this.filter = filter;
    }

    /**
     * A list of feature identifiers upon which the specified operation shall be applied. Optional.
     * No default.
     * 
     * @uml.property name="featureIds"
     */
    public String[] getFeatureIds() {
        return (String[]) featureIds.toArray( new String[featureIds.size()] );
    }

    /**
     * adds the <FeatureIds>
     */
    public void addFeatureIds( String featureIds ) {
        this.featureIds.add( featureIds );
    }

    /**
     * sets the <FeatureIds>
     */
    public void setFeatureIds( String[] featureIds ) {
        this.featureIds.clear();

        if ( featureIds != null ) {
            for (int i = 0; i < featureIds.length; i++) {
                this.featureIds.add( featureIds[i] );
            }
        }
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: Lock.java,v $
 * Revision 1.8  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.7  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.6  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.4  2005/11/16 13:44:59  mschneider
 * Merge of wfs development branch.
 *
 * Revision 1.3.2.1  2005/11/15 14:36:19  deshmukh
 * QualifiedName modifications
 * Revision 1.3 2005/08/26 21:11:29 poth no message
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.1 2005/02/25 11:19:16 poth no message
 * 
 * Revision 1.2 2005/02/07 07:56:57 poth no message
 * 
 * Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.1 2004/06/07 13:38:34 tf code adapted to wfs1 refactoring Revision 1.3 2004/03/12
 * 15:56:49 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:56 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:25 poth no message
 * 
 * Revision 1.5 2002/08/15 10:01:40 ap no message
 * 
 * Revision 1.4 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 ********************************************************************** */