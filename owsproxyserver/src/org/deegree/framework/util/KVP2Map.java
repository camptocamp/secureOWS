// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/util/KVP2Map.java,v 1.7 2006/11/28 16:39:07 bezema Exp $
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
package org.deegree.framework.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * offeres utility method for transformating a key-value-pair
 * encoded request to a <tt>Map</tt>
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: bezema $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/11/28 16:39:07 $
 *
 * @since 2.0
 */
public class KVP2Map {

    /**
     * Transforms a String/KVPs like it is used for HTTP-GET request
     * to a Map.
     * 
     * TODO: Check if the trim () call may cause side effects. It is currently used to
     * eliminate possible new line characters at the end of the string, that occured
     * using the <code>GenericClient</code>. 
     * 
     * @param kvp key-value-pair encoded request
     * @return created Map
     */
    public static Map<String,String> toMap(String kvp) { 
        
        StringTokenizer st = new StringTokenizer( kvp.trim (), "&?" );
        HashMap<String,String> map = new HashMap<String,String>();

        while ( st.hasMoreTokens() ) {
            String s = st.nextToken();
            if ( s != null ) {
                int pos = s.indexOf( '=' );

                if ( pos > -1 ) {
                    String s1 = s.substring( 0, pos );
                    String s2 = s.substring( pos + 1, s.length() );
                    map.put( s1.toUpperCase(), s2 );
                }
            }
        }
        
        return map;
        
    }
    
    /**
     * @param iterator Enumeration containing KVP encoded parameters
     * @return created Map
     */
    public static Map<String,String> toMap(Enumeration iterator) {
        HashMap<String,String> map = new HashMap<String,String>();
        
        while ( iterator.hasMoreElements() ) {
            String s = (String)iterator.nextElement();
            if ( s != null ) {
                int pos = s.indexOf( '=' );

                if ( pos > -1 ) {
                    String s1 = s.substring( 0, pos );
                    String s2 = s.substring( pos + 1, s.length() );
                    map.put( s1.toUpperCase(), s2 );
                }
            }
        }
        
        return map;
    }
    
    /**
     * returns the parameters of a <tt>HttpServletRequest</tt> as <tt>Map</tt>
     * 
     * @param request
     * @return a Map which contains kvp's from the given request
     */
    public static Map<String, String> toMap(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<String, String>();
        
        Enumeration iterator = request.getParameterNames();
        while ( iterator.hasMoreElements() ) {
            String s = (String)iterator.nextElement();
            String val = request.getParameter(s);
            map.put(s.toUpperCase(), val);
        }
        
        return map;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: KVP2Map.java,v $
   Revision 1.7  2006/11/28 16:39:07  bezema
   Added String, String generic to map from toMap

   Revision 1.6  2006/06/06 17:02:28  mschneider
   Removed unnecessary synchronized modifiers. Added usage of Generics for type safety.

   Revision 1.5  2006/04/06 20:25:28  poth
   *** empty log message ***

   Revision 1.4  2006/04/04 20:39:43  poth
   *** empty log message ***

   Revision 1.3  2006/03/30 21:20:27  poth
   *** empty log message ***

   Revision 1.2  2005/02/11 10:43:57  friebe
   fit for java 1.5

   Revision 1.1.1.1  2005/01/05 10:38:33  poth
   no message

   Revision 1.4  2004/08/04 18:49:31  mschneider
   *** empty log message ***

   Revision 1.3  2004/06/17 15:43:12  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:48:47  ap
   no message


********************************************************************** */
