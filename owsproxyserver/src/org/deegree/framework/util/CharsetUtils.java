//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/util/CharsetUtils.java,v 1.1 2006/10/17 20:31:19 poth Exp $
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * 
 *
 * @version $Revision: 1.1 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.1 $, $Date: 2006/10/17 20:31:19 $
 *
 * @since 2.0
 */
public final class CharsetUtils {
    
    private static ILogger LOG = LoggerFactory.getLogger( CharsetUtils.class );

    private static final String DEFAULT_CHARSET = "UTF-8";
    
    private CharsetUtils() {}
    
    /**
     * returns the name of the charset that is passed to the JVM as 
     * system property -DCHARSET=...  If no charset has been defined
     * UTF-8 will be returned as default.
     * @return
     */
    public static String getSystemCharset() {
        String charset = null;
        try {
            charset = System.getProperty( "CHARSET" );
        } catch ( Exception exc ) {
            LOG.logError( "Error retrieving system property CHARSET", exc );
        }
        if ( charset == null ) {
            charset = DEFAULT_CHARSET;
        }
        return charset;
    }

    public static String convertToUnicode(String input, String inCharset) {
        // Create the encoder and decoder for inCharset
        Charset charset = Charset.forName( inCharset );
        CharsetEncoder encoder = charset.newEncoder();
        
        ByteBuffer bbuf = null;
        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            bbuf = encoder.encode( CharBuffer.wrap( input ) );
 
        } catch (CharacterCodingException e) {
            LOG.logError( e.getMessage(), e );
        }
        return bbuf.toString();
    }
    
    public static String convertFromUnicode(String input, String targetCharset) {
        // Create the encoder and decoder for inCharset
        Charset charset = Charset.forName( targetCharset );
        CharsetDecoder decoder = charset.newDecoder();
        
        CharBuffer cbuf = null;
        try {           
            // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a string.
            // The new ByteBuffer is ready to be read.
            cbuf = decoder.decode( ByteBuffer.wrap( input.getBytes() ) );            
        } catch (CharacterCodingException e) {
            LOG.logError( e.getMessage(), e );
        }
        return cbuf.toString();
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: CharsetUtils.java,v $
Revision 1.1  2006/10/17 20:31:19  poth
*** empty log message ***

Revision 1.11  2006/09/23 09:03:00  poth
comment corrected

Revision 1.10  2006/07/23 09:21:36  poth
printstacktrace substitueted by using a Logger

Revision 1.9  2006/07/21 09:33:24  poth
*** empty log message ***

Revision 1.8  2006/04/24 08:03:12  poth
*** empty log message ***

Revision 1.7  2006/04/06 20:25:28  poth
*** empty log message ***

Revision 1.6  2006/04/06 06:54:10  poth
*** empty log message ***

Revision 1.5  2006/03/30 21:20:27  poth
*** empty log message ***

Revision 1.4  2005/03/20 09:57:32  poth
no message

Revision 1.3  2005/03/18 16:31:32  poth
no message

Revision 1.2  2005/02/14 16:02:27  mschneider
Changed default charset to UTF-8.

Revision 1.1  2005/01/27 21:49:17  poth
no message

Revision 1.1  2005/01/24 21:27:40  poth
no message


********************************************************************** */