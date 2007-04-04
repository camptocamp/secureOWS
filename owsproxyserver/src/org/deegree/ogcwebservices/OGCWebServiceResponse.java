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


/**
 * This is the base interface for all responses to OGC Web Services (OWS)
 * requests. Each class that capsulates a response within an OWS has to implement
 * this interface.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version 2002-03-01
 */
public interface OGCWebServiceResponse {

   /**
    * returns the request that causes the response.
    */
    OGCWebServiceRequest getRequest();
       
   /**
    * returns an XML encoding of the exception that raised. If no exception
    * raised <tt>null</tt> will be returned.
    */
    OGCWebServiceException getException();
    
  

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OGCWebServiceResponse.java,v $
Revision 1.5  2006/07/12 14:46:16  poth
comment footer added

********************************************************************** */
