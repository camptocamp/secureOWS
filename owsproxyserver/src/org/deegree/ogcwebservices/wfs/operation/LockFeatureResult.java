//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/Attic/LockFeatureResult.java,v 1.11 2006/10/12 16:24:00 mschneider Exp $
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

import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;

/**
 * Represents the response to a {@link LockFeature} request.
 * 
 * In response to a LockFeature request, the web feature server shall generate
 * an XML document containing a lock identifier that a client application can
 * reference when operating upon the locked features. The response can also
 * contain optional blocks depending on the value of the lockAction attribute.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a> 
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.11 $
 */
public class LockFeatureResult extends DefaultOGCWebServiceResponse {

    private String[] featuresLocked;

    private String[] featuresNotLocked;

    private String lockId;

    /**
     * Creates a new instance of <code>LockFeatureResult</code>.
     * 
     * @param request
     * @param lockId
     * @param featuresLocked
     * @param featuresNotLocked
     */
    public LockFeatureResult( AbstractOGCWebServiceRequest request, String lockId,
                       String[] featuresLocked, String[] featuresNotLocked ) {
        super( request );
        this.featuresLocked = featuresLocked;
        this.featuresNotLocked = featuresNotLocked;
        this.lockId = lockId;

    }

    /**
     * returns the id of the locking action. the may be used to identify the
     * request that locks a feature.
     * 
     */
    public String getLockId() {
        return lockId;
    }

    /**
     * The method returns the feature identifiers of all the features that were
     * locked by the LockFeature request.
     * 
     */
    public String[] getFeaturesLocked() {
        return featuresLocked;
    }

    /**
     * The method returns the feature identifiers of all the features that could
     * not be locked by the LockFeature request. (possibly because they were
     * already locked by someone else).
     * 
     */
    public String[] getFeaturesNotLocked() {
        return featuresNotLocked;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        String ret = this.getClass().getName() + ":\n";
        ret += ( "lockId: " + lockId + "\n" );
        for ( int i = 0; i < featuresLocked.length; i++ ) {
            ret += ( "featuresLocked: " + featuresLocked[i] + "\n" );
        }
        for ( int i = 0; i < featuresNotLocked.length; i++ ) {
            ret += ( "featuresNotLocked: " + featuresNotLocked[i] + "\n" );
        }
        return ret;
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: LockFeatureResult.java,v $
 * Revision 1.11  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.10  2006/10/09 12:47:11  poth
 * bug fix - extending DefaultOGCWebServiceResponse
 *
 * Revision 1.9  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.6  2005/08/26 21:11:29  poth
 * no message
 *
 * Revision 1.3  2005/04/06 12:02:08  poth
 * no message
 *
 * Revision 1.2  2005/04/06 10:58:15  poth
 * no message
 *
 * Revision 1.1  2005/04/05 08:03:28  poth
 * no message
 *
 * Revision 1.3  2005/03/01 16:20:15  poth
 * no message
 *
 * Revision 1.2  2005/02/07 07:56:57  poth
 * no message
 *
 * Revision 1.1  2005/01/26 20:10:05  poth
 * no message
 *
 * Revision 1.2  2005/01/18 22:08:55  poth
 * no message
 *
 * Revision 1.3  2004/06/21 08:05:49  ap
 * no message
 *
 * Revision 1.2  2004/06/07 15:12:04  tf
 * import organised
 * Revision 1.1 2004/06/07 13:38:34 tf
 * code adapted to wfs1 refactoring
 * 
 * Revision 1.3 2004/03/12 15:56:49 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:55 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:25 poth no message
 * 
 * Revision 1.6 2002/08/15 10:01:40 ap no message
 * 
 * Revision 1.5 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.4 2002/07/04 14:55:07 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 *  
 ********************************************************************** */