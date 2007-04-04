// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/SupportedSRSs.java,v 1.4 2006/04/06 20:25:27 poth Exp $
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
package org.deegree.ogcwebservices;

import org.deegree.datatypes.CodeList;

/**
 * @version $Revision: 1.4 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.4 $, $Date: 2006/04/06 20:25:27 $ *  * @since 2.0
 */

public class SupportedSRSs {

    /**
     * 
     * @uml.property name="requestResponseSRSs"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private CodeList[] requestResponseSRSs = null;

    /**
     * 
     * @uml.property name="requestSRSs"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private CodeList[] requestSRSs = null;

    /**
     * 
     * @uml.property name="responseSRSs"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private CodeList[] responseSRSs = null;

    /**
     * 
     * @uml.property name="nativeSRSs"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    private CodeList[] nativeSRSs = null;

    
   
    /**
     * @param requestResponseSRSs
     * @param requestSRSs
     * @param responseSRSs
     * @param nativeSRSs
     */
    public SupportedSRSs(CodeList[] requestResponseCRSs, CodeList[] requestCRSs, 
                         CodeList[] responseCRSs, CodeList[] nativeCRSs) {
        setRequestSRSs(requestCRSs);
        setResponseSRSs(responseCRSs);
        setRequestResponseSRSs(requestResponseCRSs);
        setNativeSRSs(nativeCRSs);
    }

    /**
     * @return Returns the nativeSRSs.
     * 
     * @uml.property name="nativeSRSs"
     */
    public CodeList[] getNativeSRSs() {
        return nativeSRSs;
    }

    /**
     * @param nativeSRSs The nativeSRSs to set.
     * 
     * @uml.property name="nativeSRSs"
     */
    public void setNativeSRSs(CodeList[] nativeSRSs) {
        if (nativeSRSs == null) {
            nativeSRSs = new CodeList[0];
        }
        this.nativeSRSs = nativeSRSs;
    }

    /**
     * @return Returns the requestSRSs.
     * 
     * @uml.property name="requestSRSs"
     */
    public CodeList[] getRequestSRSs() {
        return requestSRSs;
    }

    /**
     * @param requestSRSs The requestSRSs to set.
     * 
     * @uml.property name="requestSRSs"
     */
    public void setRequestSRSs(CodeList[] requestSRSs) {
        if (requestSRSs == null) {
            requestSRSs = new CodeList[0];
        }
        this.requestSRSs = requestSRSs;
    }

    /**
     * @return Returns the requestResponseSRSs.
     * 
     * @uml.property name="requestResponseSRSs"
     */
    public CodeList[] getRequestResponseSRSs() {
        return requestResponseSRSs;
    }

    /**
     * @param requestResponseSRSs The requestResponseSRSs to set.
     * 
     * @uml.property name="requestResponseSRSs"
     */
    public void setRequestResponseSRSs(CodeList[] requestResponseSRSs) {
        if (requestResponseSRSs == null) {
            requestResponseSRSs = new CodeList[0];
        }
        this.requestResponseSRSs = requestResponseSRSs;
    }

    /**
     * @return Returns the responseSRSs.
     * 
     * @uml.property name="responseSRSs"
     */
    public CodeList[] getResponseSRSs() {
        return responseSRSs;
    }

    /**
     * @param responseSRSs The responseSRSs to set.
     * 
     * @uml.property name="responseSRSs"
     */
    public void setResponseSRSs(CodeList[] responseSRSs) {
        if (responseSRSs == null) {
            responseSRSs = new CodeList[0];
        }
        this.responseSRSs = responseSRSs;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: SupportedSRSs.java,v $
   Revision 1.4  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.1  2005/11/21 15:02:33  deshmukh
   CRS to SRS

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/06/16 09:46:02  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:38  ap
   no message


********************************************************************** */
