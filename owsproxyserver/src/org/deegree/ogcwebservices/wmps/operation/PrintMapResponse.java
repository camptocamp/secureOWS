//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wmps/operation/PrintMapResponse.java,v 1.26 2006/10/02 06:30:35 poth Exp $
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

package org.deegree.ogcwebservices.wmps.operation;

import java.util.Date;

/**
 * PrintMapInitialResponse to inform the user if his request the status of his requst before
 * processing. If the request is (not) successfully recieved an appropriate message will be sent to
 * the user.
 * 
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh</a>
 * 
 * @author last edited by: $Author: poth $
 * 
 * @version 2.0, $Revision: 1.26 $, $Date: 2006/10/02 06:30:35 $
 * 
 * @since 2.0
 */

public class PrintMapResponse {

    private String id;

    private Date timeStamp;

    private Date expectedTime;

    private String emailAddress;

    private String exception;
    
    private String message;

    /**
     * Create an instance of the PrintMapResponse
     * 
     * @param id
     * @param emailAddress
     * @param timeStamp
     * @param expectedTime
     * @param success
     * @param status
     *            0->before print; 1->after print
     * @param mailLink
     * @param exception
     */
    public PrintMapResponse( String id, String emailAddress, Date timeStamp, Date expectedTime,
                             String message, String exception ) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.timeStamp = timeStamp;
        this.expectedTime = expectedTime;
        this.exception = exception;
        this.message = message;

    }

    /**
     * Get PrintMap Request Id
     * 
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get PrintMap request Email Address
     * 
     * @return String
     */
    public String getEmailAddress() {
        return this.emailAddress;

    }

    /**
     * Get PrintMap request TimeStamp
     * 
     * @return Date
     */
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Get Success/Failed Message for this PrintMap request.
     * 
     * @return String
     */
    public String getMessage() {        
        return message;
    }

    /**
     * Get ExpectedTime for the service to process the PrintMap request.
     * 
     * @return Date
     */
    public Date getExpectedTime() {
        return this.expectedTime;
    }

    /**
     * @return Returns the exception.
     */
    public String getException() {
        return this.exception;
    }

}
/***************************************************************************************************
 * Changes to this class. What the people have been up to: $Log: PrintMapResponse.java,v $
 * Changes to this class. What the people have been up to: Revision 1.26  2006/10/02 06:30:35  poth
 * Changes to this class. What the people have been up to: bug fixes
 * Changes to this class. What the people have been up to:
 *  Revision 1.25  2006/09/13 07:37:58  deshmukh
 *  removed excess debug statements.
 *  Changes
 * to this class. What the people have been up to: Revision 1.24 2006/09/11 08:34:14 deshmukh
 *  send document mail link as html. Changes
 * to this class. What the people have been up to: Changes to this class. What the people have been
 * up to: Revision 1.23 2006/09/04 11:32:25 deshmukh Changes to this class. What the people have
 * been up to: comments added  Changes to
 * this class. What the people have been up to: Revision 1.22 2006/08/29 19:54:14 poth Changes to
 * this class. What the people have been up to: footer corrected Changes to this class. What the
 * people have been up to:  Revision 1.21
 * 2006/08/24 06:42:17 poth  File header
 * corrected  Changes to this class. What the
 * people have been up to: Revision 1.20 2006/08/23 10:21:12 deshmukh Changes to this class. What
 * the people have been up to: detailed response messages in case of exception added. Changes to
 * this class. What the people have been up to: Changes to this class. What the people have been up
 * to: Revision 1.19 2006/08/23 09:56:00 taddei Changes to this class. What the people have been up
 * to: i inserted an i  Changes to this
 * class. What the people have been up to: Revision 1.18 2006/08/23 09:47:07 mays Changes to this
 * class. What the people have been up to:
 * 
 * @deshmukh: final response exception message bug fixed Changes to this class. What the people have
 *            been up to:  Revision 1.17
 *            2006/08/10 07:11:35 deshmukh 
 *            WMPS has been modified to support the new configuration changes and the excess code
 *            not needed has been replaced. 
 *             Revision 1.16 2006/08/02
 *            06:51:29 deshmukh  modification
 *            of javadoc  Changes to this
 *            class. What the people have been up to: Revision 1.15 2006/08/01 14:20:10 deshmukh
 *             The wmps configuration has
 *            been modified and extended. Also fixed the javadoc. Changes to this class. What the
 *            people have been up to: 
 *            Revision 1.14 2006/08/01 13:41:48 deshmukh Changes to this class. What the people have
 *            been up to: The wmps configuration has been modified and extended. Also fixed the
 *            javadoc.  Changes to this
 *            class. What the people have been up to: Revision 1.13 2006/07/31 11:21:07 deshmukh
 *             wmps implemention... Changes
 *            to this class. What the people have been up to: Revision 1.12 2006/07/12 14:46:18 poth
 *            comment footer added
 * 
 **************************************************************************************************/
