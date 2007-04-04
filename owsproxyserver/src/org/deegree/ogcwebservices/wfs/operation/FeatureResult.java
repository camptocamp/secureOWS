//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wfs/operation/FeatureResult.java,v 1.15 2006/10/12 16:24:00 mschneider Exp $
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
package org.deegree.ogcwebservices.wfs.operation;

import org.deegree.model.feature.FeatureCollection;
import org.deegree.ogcwebservices.AbstractOGCWebServiceRequest;
import org.deegree.ogcwebservices.DefaultOGCWebServiceResponse;

/**
 * Represents the response to a {@link GetFeature} request.
 * <p>
 * The response to a {@link GetFeature} request is controlled by the outputFormat attribute. The
 * default value for the outputFormat attribute shall be GML indicating that a WFS must generate a
 * GML document of the result set that conforms to the Geography Markup Language (GML) 3.1.1
 * specification. Vendor specific output formats can also be generated but they must be declared in
 * the capabilities document.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 1.15 $, $Date: 2006/10/12 16:24:00 $
 */
public class FeatureResult extends DefaultOGCWebServiceResponse {

    private FeatureCollection featureCollection;

    /**
     * Creates a new instance of <code>FeatureResult</code>.
     * 
     * @param request
     * @param featureCollection
     */
    public FeatureResult( AbstractOGCWebServiceRequest request, FeatureCollection featureCollection ) {
        super( request );
        this.featureCollection = featureCollection;
    }

    /**
     * Returns the result as a {@link FeatureCollection}.
     * 
     * @return the result as a FeatureCollection
     */
    public Object getResponse() {
        return this.featureCollection;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String ret = this.getClass().getName()
            + ":\n";
        ret = getClass().getName()
            + ":\n";
        ret += ( "response = "
            + featureCollection + "\n" );
        return ret;
    }
}

/* ********************************************************************
 * Changes to this class. What the people haven been up to:
 * 
 * $Log: FeatureResult.java,v $
 * Revision 1.15  2006/10/12 16:24:00  mschneider
 * Javadoc + compiler warning fixes.
 *
 * Revision 1.14  2006/10/09 12:47:11  poth
 * bug fix - extending DefaultOGCWebServiceResponse
 *
 * Revision 1.13  2006/06/07 17:16:12  mschneider
 * Improved javadoc.
 *
 * Revision 1.12  2006/04/06 20:25:28  poth
 * *** empty log message ***
 *
 * Revision 1.11  2006/04/04 20:39:43  poth
 * *** empty log message ***
 *
 * Revision 1.10  2006/03/30 21:20:27  poth
 * *** empty log message ***
 *
 * Revision 1.9  2005/11/22 18:08:04  deshmukh
 * Transaction WFS.. work in progress
 * Revision 1.8 2005/08/26 21:11:29 poth no message
 * 
 * Revision 1.3 2005/04/06 11:50:33 poth no message
 * 
 * Revision 1.2 2005/04/06 10:58:15 poth no message
 * 
 * Revision 1.1 2005/04/05 08:03:28 poth no message
 * 
 * Revision 1.6 2005/03/12 10:45:03 poth no message
 * 
 * Revision 1.5 2005/03/01 16:20:15 poth no message
 * 
 * Revision 1.4 2005/02/22 22:44:15 friebe fix javadoc
 * 
 * Revision 1.3 2005/02/07 07:56:57 poth no message
 * 
 * Revision 1.2 2005/02/03 21:35:08 poth no message
 * 
 * Revision 1.1 2005/01/26 20:10:05 poth no message
 * 
 * Revision 1.2 2005/01/18 22:08:55 poth no message
 * 
 * Revision 1.2 2004/06/07 15:12:04 tf import organised Revision 1.1 2004/06/07 13:38:34 tf code
 * adapted to wfs1 refactoring
 * 
 * Revision 1.4 2004/03/26 11:19:32 poth no message
 * 
 * Revision 1.3 2004/02/09 08:00:21 poth no message
 * 
 * Revision 1.2 2003/04/07 07:26:54 poth no message
 * 
 * Revision 1.1.1.1 2002/09/25 16:01:26 poth no message
 * 
 * Revision 1.8 2002/08/15 10:01:40 ap no message
 * 
 * Revision 1.7 2002/08/09 15:36:30 ap no message
 * 
 * Revision 1.6 2002/07/19 14:48:20 ap no message
 * 
 * Revision 1.5 2002/05/14 14:39:51 ap no message
 * 
 * Revision 1.4 2002/05/13 16:11:02 ap no message
 * 
 * Revision 1.3 2002/04/26 09:05:36 ap no message
 * 
 * Revision 1.1 2002/04/04 16:17:15 ap no message
 * 
 ********************************************************************** */