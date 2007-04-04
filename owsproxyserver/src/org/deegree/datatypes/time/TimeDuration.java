package org.deegree.datatypes.time;

import java.io.Serializable;

/**
 * 
 *
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/06/26 16:16:48 $
 *
 * @since 2.0
 */
public class TimeDuration implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int years = 0;

    private int month = 0;

    private int days = 0;

    private int hours = 0;

    private int minutes = 0;

    private int seconds = 0;

    private int millis = 0;

    /**
     * @param years
     * @param month
     * @param days    
     */
    public TimeDuration( int years, int month, int days ) {
        this.years = years;
        this.month = month;
        this.days = days;
    }

    /**    
     * @param hours
     * @param minutes   
     */
    public TimeDuration( int hours, int minutes ) {
        this.hours = hours;
        this.minutes = minutes;
    }

    /**
     * @param years
     * @param month
     * @param days
     * @param hours
     * @param minutes   
     */
    public TimeDuration( int years, int month, int days, int hours, int minutes ) {
        this.years = years;
        this.month = month;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    /**
     * @param years
     * @param month
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     * @param millis
     */
    public TimeDuration( int years, int month, int days, int hours, int minutes, int seconds,
                        int millis ) {
        this.years = years;
        this.month = month;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }

    /**
     * 
     * @param time String as defined in W3C XSD simple datatypes section,
     *          e.g.: P1Y2M3DT10H30M; P1Y2MT2H; P0Y1347M0D
     * @return
     */
    public static TimeDuration createTimeDuration( String time ) {

        int y = 0;
        int mon = 0;
        int d = 0;
        int h = 0;
        int min = 0;
        int s = 0;

        // remove leading 'P'
        time = time.substring( 1 );
        if ( !time.startsWith( "T" ) ) {
            int pos = time.indexOf( 'Y' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                y = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
            pos = time.indexOf( 'M' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                mon = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
            pos = time.indexOf( 'D' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                d = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
        }

        if ( time.length() > 0 ) {
            // remove leading 'T'
            time = time.substring( 1 );
            int pos = time.indexOf( 'H' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                h = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
            pos = time.indexOf( 'M' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                min = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
            pos = time.indexOf( 'S' );
            if ( pos > -1 ) {
                String tmp = time.substring( 0, pos );
                s = Integer.parseInt( tmp );
                time = time.substring( pos + 1 );
            }
        }

        return new TimeDuration( y, mon, d, h, min, s, 0 );
    }

    /**
     * @return Returns the days.
     * 
     */
    public int getDays() {
        return days;
    }

    /**
     * @param days The days to set.
     * 
     */
    public void setDays( int days ) {
        this.days = days;
    }

    /**
     * @return Returns the hours.
     * 
     */
    public int getHours() {
        return hours;
    }

    /**
     * @param hours The hours to set.
     * 
     */
    public void setHours( int hours ) {
        this.hours = hours;
    }

    /**
     * @return Returns the millis.
     * 
     */
    public int getMillis() {
        return millis;
    }

    /**
     * @param millis The millis to set.
     * 
     */
    public void setMillis( int millis ) {
        this.millis = millis;
    }

    /**
     * @return Returns the minutes.
     * 
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @param minutes The minutes to set.
     * 
     */
    public void setMinutes( int minutes ) {
        this.minutes = minutes;
    }

    /**
     * @return Returns the month.
     * 
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month The month to set.
     * 
     */
    public void setMonth( int month ) {
        this.month = month;
    }

    /**
     * @return Returns the seconds.
     * 
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * @param seconds The seconds to set.
     * 
     */
    public void setSeconds( int seconds ) {
        this.seconds = seconds;
    }

    /**
     * @return Returns the years.
     * 
     */
    public int getYears() {
        return years;
    }

    /**
     * @param years The years to set.
     * 
     */
    public void setYears( int years ) {
        this.years = years;
    }

    /**
     * returns a duration a milli seconds 
     * @return
     */
    public long getAsMilliSeconds() {
        long l = 0;
        l = l + ( years * 31536000000l );
        l = l + ( month * 2592000000l );
        l = l + ( days * 86400000l );
        l = l + ( hours * 3600000l );
        l = l + ( minutes * 60000l );
        l = l + ( seconds * 1000l );
        l = l + millis;
        return l;
    }

    /**
     * return format: P1Y2M3DT10H30M10S
     * 
     * @return
     */
    public String getAsGMLTimeDuration() {
        StringBuffer sb = new StringBuffer( 150 );
        //P1Y2M3DT10H30M
        sb.append( 'P' ).append( years ).append( 'Y' ).append( month ).append( 'M' ).append( days ).append(
                                                                                                            "DT" ).append(
                                                                                                                           hours ).append(
                                                                                                                                           'H' ).append(
                                                                                                                                                         minutes ).append(
                                                                                                                                                                           'M' ).append(
                                                                                                                                                                                         seconds ).append(
                                                                                                                                                                                                           'S' );
        return sb.toString();
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new TimeDuration( years, month, days, hours, minutes, seconds, millis );
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TimeDuration.java,v $
 Revision 1.6  2006/06/26 16:16:48  poth
 bug fix - reading interval just containing 'T' values

 Revision 1.5  2006/05/09 15:51:37  poth
 *** empty log message ***

 Revision 1.4  2006/03/22 17:22:10  poth
 *** empty log message ***

 Revision 1.3  2005/11/16 13:44:59  mschneider
 Merge of wfs development branch.

 Revision 1.2.2.1  2005/11/14 11:34:29  deshmukh
 inserted: serialVersionID

 Revision 1.2  2005/01/18 22:08:54  poth
 no message

 Revision 1.3  2004/08/16 06:23:33  ap
 no message

 Revision 1.2  2004/07/09 07:01:33  ap
 no message

 Revision 1.1  2004/05/25 12:55:01  ap
 no message


 ********************************************************************** */
