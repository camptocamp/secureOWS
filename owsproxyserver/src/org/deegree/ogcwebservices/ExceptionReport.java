// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/ExceptionReport.java,v 1.6 2006/04/25 19:28:52 poth Exp $
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

import java.util.ArrayList;
import java.util.List;

/**
 *<p>Upon receiving an invalid operation request, each OWS shall respond to 
 *the client using an Exception Report message to describe to the client 
 *application and/or its human user the reason(s) that the request is invalid. 
 *Whenever a server detects an exception condition while responding to a valid 
 *operation request, and cannot produce a normal response to that operation, 
 *the server shall also respond to the client using an Exception Report.</p>
 *<p>Each Exception Report shall contain one or more Exception elements, with 
 *each such element signalling detection of an independent error. Each Exception 
 *element shall contain the parameters ExceptionText, exceptionCode and locator.</p>
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/04/25 19:28:52 $
 *
 * @since 2.0
 */
public class ExceptionReport {
    
    private List exceptions = new ArrayList();
    private String version = "1.0.0";
    private String language = "en";

    /**
     * @param exceptions
     */
    public ExceptionReport(OGCWebServiceException[] exceptions) {
        setExceptions(exceptions);
    }
    
    
    /**
     * @param exceptions
     * @param version
     */
    public ExceptionReport(OGCWebServiceException[] exceptions, String version) {
        setExceptions(exceptions);
        setVersion( version );
    }
    
    
    /**
     * @param exceptions
     * @param version
     * @param language
     */
    public ExceptionReport(OGCWebServiceException[] exceptions, String version,
                            String language) {
        setExceptions(exceptions);
        setVersion( version );
        setLanguage( language );
    }

    /**
     * @return Returns the exceptions.
     * 
     * @uml.property name="exceptions"
     */
    public OGCWebServiceException[] getExceptions() {
        OGCWebServiceException[] owse = new OGCWebServiceException[exceptions
            .size()];
        return (OGCWebServiceException[]) exceptions.toArray(owse);
    }


    /**
     * @param exceptions The exceptions to set.
     */
    public void setExceptions(OGCWebServiceException[] exceptions) {        
        this.exceptions.clear();
        for (int i = 0; i < exceptions.length; i++) {
            this.exceptions.add(exceptions[i]);
        }
    }

    /**
     * @return Returns the language.
     * 
     * @uml.property name="language"
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language to set.
     * 
     * @uml.property name="language"
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return Returns the version.
     * 
     * @uml.property name="version"
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     * 
     * @uml.property name="version"
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: ExceptionReport.java,v $
   Revision 1.6  2006/04/25 19:28:52  poth
   *** empty log message ***

   Revision 1.5  2006/04/06 20:25:27  poth
   *** empty log message ***

   Revision 1.4  2006/04/04 20:39:42  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:26  poth
   *** empty log message ***

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.3  2004/06/16 09:49:18  ap
   no message

   Revision 1.2  2004/06/16 09:46:02  ap
   no message


********************************************************************** */