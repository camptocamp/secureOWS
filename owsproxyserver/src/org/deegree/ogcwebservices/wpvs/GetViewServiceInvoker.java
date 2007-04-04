//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/GetViewServiceInvoker.java,v 1.10 2006/11/27 11:31:50 bezema Exp $
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

package org.deegree.ogcwebservices.wpvs;

import org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource;
import org.deegree.ogcwebservices.wpvs.util.ResolutionStripe;

/**
 * Abstract super class for all invokers. Concrete implementations of this class call specific
 * services in order to request data from them.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * $Revision: 1.10 $, $Date: 2006/11/27 11:31:50 $
 * 
 */
public abstract class GetViewServiceInvoker {

    protected ResolutionStripe resolutionStripe;

    /**
     * @param owner
     *            the ResolutionStripe (part of the users viewing area) which this service will be
     *            called/invoked for.
     */
    public GetViewServiceInvoker( ResolutionStripe owner ) {
        this.resolutionStripe = owner;
    }

    /**
     * The implementation of this method should invoke a webservice specified by the given
     * AbstractDataSource. The Services response to the Request shall be saved in the
     * ResolutionStripe.
     * 
     * @param dataSource
     *            a class containing the necessary data to invoke a service.
     */
    public abstract void invokeService( AbstractDataSource dataSource );

}

/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: GetViewServiceInvoker.java,v $
 * Changes to this class. What the people have been up to: Revision 1.10  2006/11/27 11:31:50  bezema
 * Changes to this class. What the people have been up to: UPdating javadocs and cleaning up
 * Changes to this class. What the people have been up to:
 * Changes to this class. What the people have been up to: Revision 1.9  2006/11/23 11:46:02  bezema
 * Changes to this class. What the people have been up to: The initial version of the new wpvs
 * Changes to this class. What the people have been up to:
 * Revision 1.8 2006/06/20 10:16:01 taddei clean up and javadoc
 * 
 * Revision 1.7 2006/04/06 20:25:30 poth ** empty log message ***
 * 
 * Revision 1.6 2006/03/30 21:20:28 poth ** empty log message ***
 * 
 * Revision 1.5 2006/03/29 15:07:40 taddei sz
 * 
 * Revision 1.4 2006/02/09 15:47:24 taddei bug fixes, refactoring and javadoc
 * 
 * Revision 1.3 2006/01/26 14:42:31 taddei WMS and WFS invokers woring; minor refactoring
 * 
 * Revision 1.2 2006/01/18 08:58:00 taddei implementation (WFS)
 * 
 * Revision 1.1 2005/12/16 15:19:11 taddei added DeafultViewHandler and the Invokers
 * 
 * 
 **************************************************************************************************/
