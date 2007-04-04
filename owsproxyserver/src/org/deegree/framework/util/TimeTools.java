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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * The <code>TimeTools</code> class can be used to format Strings to timecodes and get Calenadars
 * of a given Timecode.
 * 
 * <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.20 $, $Date: 2006/09/18 12:37:39 $
 * 
 * @since 2.0
 */

public class TimeTools {

    /**
     * A final Year representation
     */
    public static final int YEAR = 0;

    /**
     * A final Month representation
     */
    public static final int MONTH = 1;

    /**
     * A final Day representation
     */
    public static final int DAY = 2;

    /**
     * A final Hour representation
     */
    public static final int HOUR = 3;

    /**
     * A final Minute representation
     */
    public static final int MINUTE = 4;

    /**
     * A final Second representation
     */
    public static final int SECOND = 5;

    private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss",
                                                                Locale.GERMANY );

    /**
     * @return the current timestamp in ISO format
     */
    public static String getISOFormattedTime() {
        return getISOFormattedTime( new Date( System.currentTimeMillis() ) );
    }

    /**
     * returns the date calendar in ISO format
     * 
     * @param date
     * @return
     */
    public static String getISOFormattedTime( Date date ) {
        return sdf.format( date ).replace( ' ', 'T' );
    }

    /**
     * @param date
     *            the date object to get the time values of
     * @param locale
     *            the locale to convert to
     * @return the date calendar in ISO format considering the passed locale
     */
    public static String getISOFormattedTime( Date date, Locale locale ) {
        SimpleDateFormat sdf_ = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", locale );
        return sdf_.format( date ).replace( ' ', 'T' );
    }

    /**
     * 
     * @param cal
     *            a Calendar to get the timevalues of
     * @return the passed calendar in ISO format
     */
    public static String getISOFormattedTime( Calendar cal ) {
        return getISOFormattedTime( cal.getTime() );
    }

    /**
     * returns a part of the submitted iso-formatted timestamp. possible values
     * 
     * @param value
     *            <ul>
     *            <li>YEAR
     *            <li>MONTH
     *            <li>DAY
     *            <li>HOUR
     *            <li>MINUTE
     *            <li>SECOND
     *            </ul>
     * @param isoTimestamp
     *            an ISO timestamp-> year-mon-dayThours:min:sec
     * @return the timevalue of the given value
     */
    private static int get( int value, String[] isoTimestamp ) {

        for ( int i = 0; i < isoTimestamp.length; i++ ) {
            Integer.parseInt( isoTimestamp[i] );
        }
        if ( value > isoTimestamp.length - 1 ) {
            return 0;
        }
        return Integer.parseInt( isoTimestamp[value] );
    }

    /**
     * 
     * @param isoDate
     *            an ISO timestamp-> year-mon-dayThours:min:sec
     * @return an instance of a <tt>GregorianCalendar</tt> from an ISO timestamp
     */
    public static GregorianCalendar createCalendar( String isoDate ) {
        String[] tmp = StringTools.toArray( isoDate.trim(), "-:T.", false );
        int y = TimeTools.get( TimeTools.YEAR, tmp );
        int m = TimeTools.get( TimeTools.MONTH, tmp );
        int d = TimeTools.get( TimeTools.DAY, tmp );
        int h = TimeTools.get( TimeTools.HOUR, tmp );
        int min = TimeTools.get( TimeTools.MINUTE, tmp );
        int sec = TimeTools.get( TimeTools.SECOND, tmp );
        return new GregorianCalendar( y, m - 1, d, h, min, sec );
    }

}/***********************************************************************************************
     * Changes to this class. What the people have been up to: $Log: TimeTools.java,v $
     * Changes to this class. What the people have been up to: Revision 1.20  2006/09/18 12:37:39  bezema
     * Changes to this class. What the people have been up to: added documentation
     * Changes to this class. What the people have been up to: Revision
     * 1.19 2006/07/12 14:46:17 poth comment footer added
     * 
     **********************************************************************************************/
