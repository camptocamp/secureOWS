package org.deegree.datatypes.time;

import java.io.Serializable;

import org.deegree.framework.util.StringTools;
import org.deegree.framework.util.TimeTools;
import org.deegree.ogcwebservices.InvalidParameterValueException;

/**
 * @version $Revision: 1.6 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.6 $, $Date: 2006/07/13 13:47:27 $
 * 
 * @since 2.0
 */

public class TimeSequence implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private TimePeriod[] timePeriod = null;

    private TimePosition[] timePosition = null;

    /**
     * @param timePeriod
     */
    public TimeSequence( TimePeriod[] timePeriod ) {
        setTimePeriod( timePeriod );
    }

    /**
     * @param timePosition
     */
    public TimeSequence( TimePosition[] timePosition ) {
        setTimePosition( timePosition );
    }

    /**
     * @param timePeriod
     * @param timePosition
     */
    public TimeSequence( TimePeriod[] timePeriod, TimePosition[] timePosition ) {
        setTimePeriod( timePeriod );
        setTimePosition( timePosition );
    }

    /**
     * format: min/max,res or time1,time2,...,timeN
     * 
     * @param time string representation of a Time dimension 
     * @throws InvalidParameterValueException 
     */
    public TimeSequence( String time ) throws InvalidParameterValueException {
        
        // TODO
        // support parsing more than one period and position

          if ( time.indexOf( '/' ) > 0 ) {
            // TIME= min/max/res,
            String[] tm = StringTools.toArray( time, "/", false );
            if ( tm.length != 3 ) {
                throw new InvalidParameterValueException( "time format ist not correct: " + time );
            }
            TimePosition min = new TimePosition( TimeTools.createCalendar( tm[0] ) );
            TimePosition max = null;
            if ( tm[0].equals( "now" ) ) {
                max = new TimePosition();
            } else {
                max = new TimePosition( TimeTools.createCalendar( tm[1] ) );
            }
            TimeDuration td = TimeDuration.createTimeDuration( tm[2] );
            TimePeriod tper = new TimePeriod( min, max, td );
            this.timePeriod = new TimePeriod[] { tper };
        } else {
            // TIME= time1,time2,...,timeN
            String[] tm = StringTools.toArray( time, ",", false );
            TimePosition[] tp = new TimePosition[tm.length];
            for ( int i = 0; i < tm.length; i++ ) {
                tp[i] = new TimePosition( TimeTools.createCalendar( tm[i] ) );
            }
            this.timePosition = tp;
        }

    }

    /**
     * @return Returns the timePeriod.
     */
    public TimePeriod[] getTimePeriod() {
        return timePeriod;
    }

    /**
     * @param timePeriod The timePeriod to set.
     * 
     */
    public void setTimePeriod( TimePeriod[] timePeriod ) {
        if ( timePeriod == null ) {
            timePeriod = new TimePeriod[0];
        }
        this.timePeriod = timePeriod;
    }

    /**
     * @return Returns the timePosition.
     */
    public TimePosition[] getTimePosition() {
        return timePosition;
    }

    /**
     * @param timePosition The timePosition to set.
     */
    public void setTimePosition( TimePosition[] timePosition ) {
        if ( timePosition == null ) {
            timePosition = new TimePosition[0];
        }
        this.timePosition = timePosition;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        TimePeriod[] timePeriod_ = new TimePeriod[timePeriod.length];
        for ( int i = 0; i < timePeriod_.length; i++ ) {
            timePeriod_[i] = (TimePeriod) timePeriod_[i].clone();
        }
        TimePosition[] timePositions_ = new TimePosition[timePosition.length];
        for ( int i = 0; i < timePeriod_.length; i++ ) {
            timePositions_[i] = (TimePosition) timePositions_[i].clone();
        }
        return new TimeSequence( timePeriod_, timePositions_ );
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: TimeSequence.java,v $
 Revision 1.6  2006/07/13 13:47:27  poth
 constructor added receiving a sequence formatted as string

 Revision 1.5  2006/07/12 14:46:19  poth
 comment footer added

 ********************************************************************** */
