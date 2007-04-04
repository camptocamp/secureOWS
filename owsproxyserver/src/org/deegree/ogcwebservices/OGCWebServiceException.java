// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/OGCWebServiceException.java,v 1.9 2006/04/06 20:25:27 poth Exp $
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

import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcbase.OGCException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.9 $, $Date: 2006/04/06 20:25:27 $
 *
 * @since 2.0
 */
public class OGCWebServiceException extends OGCException  {
    
    private String locator = null;
    
    /**
     * creates an OGCWebServiceException from a DOM object as defined in the
     * OGC common implementation specification 
     * @param doc
     */
    public static OGCWebServiceException create(Document doc) {        
        Element root = doc.getDocumentElement();
        return create( root );        
    }
    
    /**
     * creates an OGCWebServiceException from a DOM Element object as defined 
     * in the OGC common implementation specification 
     * @param root
     */
    public static OGCWebServiceException create(Element root) {        
        String me = null;
        String lo = null;
        String code = null;
        
        code = XMLTools.getAttrValue( root, "code" );
        lo = XMLTools.getAttrValue( root, "locator" );
                
        me = XMLTools.getStringValue(root);
        
        ExceptionCode ec = new ExceptionCode( code );
        return new OGCWebServiceException( me, lo, ec );        
    }
    
    /**
     * @param message
     */
    public OGCWebServiceException(String message) {
        super( message );       
    }
    
    
    /**
     * @param locator
     * @param message
     */
    public OGCWebServiceException(String locator, String message) {
        super( message );
        this.locator = locator;        
    }
    
    /**
     * @param locator
     * @param message
     * @param code 
     */
    public OGCWebServiceException(String locator, String message, ExceptionCode code) {
        super( message, code );
        this.locator = locator;        
    }

    /**
     * returns the class/service that has caused the exception
     * 
     */
    public String getLocator() {
        return locator;
    }

    /**
     * sets the class/service that has caused the exception
     * 
     */
    public void setLocator(String locator) {
        this.locator = locator;
    }

    

    
}
/*
 * Changes to this class. What the people haven been up to:
 *
 * $Log: OGCWebServiceException.java,v $
 * Revision 1.9  2006/04/06 20:25:27  poth
 * *** empty log message ***
 *
 * Revision 1.8  2006/04/04 20:39:42  poth
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/30 21:20:26  poth
 * *** empty log message ***
 *
 * Revision 1.6  2005/04/20 20:36:09  poth
 * no message
 *
 * Revision 1.5  2005/03/17 16:53:45  friebe
 * *** empty log message ***
 *
 * Revision 1.4  2005/03/09 11:55:46  mschneider
 * *** empty log message ***
 *
 * Revision 1.3  2005/01/26 20:10:05  poth
 * no message
 *
 * Revision 1.2  2005/01/18 22:08:54  poth
 * no message
 *
 * Revision 1.6  2004/07/12 06:12:11  ap
 * no message
 *
 * Revision 1.5  2004/06/16 09:46:02  ap
 * no message
 *
 * Revision 1.4  2004/06/16 06:35:11  ap
 * no message
 *
 * Revision 1.3  2004/06/09 09:30:42  ap
 * no message
 *
 * Revision 1.2  2004/05/25 07:19:13  ap
 * no message
 *
 * Revision 1.1  2004/05/24 06:54:38  ap
 * no message
 *
 */
