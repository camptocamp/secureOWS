package org.deegree.datatypes.time;

import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: mschneider $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2005/11/16 13:44:59 $ *  * @since 2.0
 */

public class TimePosition implements Cloneable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @uml.property name="indeterminatePosition"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private TimeIndeterminateValue indeterminatePosition = null;

    private String calendarEraName = null;
    private URI frame = null; 
    private Calendar time = null;
    
    /**
     * defaults are:
     * <ul>
     *  <li>indeterminatePosition = now</li>
     *  <li>calendarEraName = AC</li>
     *  <li>frame = #ISO-8601</li>
     *  <li>time = new GregorianCalendar()</li>
     * </ul>
     */
    public TimePosition() {
        indeterminatePosition = new TimeIndeterminateValue();
        calendarEraName = "AC";
        try {
            frame = new URI( "#ISO-8601" );
        } catch(Exception e) {
            e.printStackTrace();
        }
        time = new GregorianCalendar();
    }
    
    
    /**
     * defaults are:
     * <ul>
     *  <li>indeterminatePosition = now</li>
     *  <li>calendarEraName = AC</li>
     *  <li>frame = #ISO-8601</li>
     * </ul>
     * @param time
     */
    public TimePosition(Calendar time) {
        this.time = time;
        indeterminatePosition = new TimeIndeterminateValue();
        calendarEraName = "AC";
        try {
            frame = new URI( "#ISO-8601" );
        } catch(Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * @param indeterminatePosition
     * @param calendarEraName
     * @param frame
     * @param time
     */
    public TimePosition(TimeIndeterminateValue indeterminatePosition, String calendarEraName, 
                        URI frame, Calendar time) {
        this.indeterminatePosition = indeterminatePosition;
        this.calendarEraName = calendarEraName;
        this.frame = frame;
        this.time = time;
    }

    /**
     * @return Returns the calendarEraName.
     * 
     * @uml.property name="calendarEraName"
     */
    public String getCalendarEraName() {
        return calendarEraName;
    }

    /**
     * @param calendarEraName The calendarEraName to set.
     * 
     * @uml.property name="calendarEraName"
     */
    public void setCalendarEraName(String calendarEraName) {
        this.calendarEraName = calendarEraName;
    }

    /**
     * @return Returns the frame.
     * 
     * @uml.property name="frame"
     */
    public URI getFrame() {
        return frame;
    }

    /**
     * @param frame The frame to set.
     * 
     * @uml.property name="frame"
     */
    public void setFrame(URI frame) {
        this.frame = frame;
    }

    /**
     * @return Returns the indeterminatePosition.
     * 
     * @uml.property name="indeterminatePosition"
     */
    public TimeIndeterminateValue getIndeterminatePosition() {
        return indeterminatePosition;
    }

    /**
     * @param indeterminatePosition The indeterminatePosition to set.
     * 
     * @uml.property name="indeterminatePosition"
     */
    public void setIndeterminatePosition(
        TimeIndeterminateValue indeterminatePosition) {
        this.indeterminatePosition = indeterminatePosition;
    }

    /**
     * @return Returns the time.
     * 
     * @uml.property name="time"
     */
    public Calendar getTime() {
        return time;
    }

    /**
     * @param time The time to set.
     * 
     * @uml.property name="time"
     */
    public void setTime(Calendar time) {
        this.time = time;
    }

    
    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new TimePosition( indeterminatePosition, calendarEraName, frame, time );
    }
    
}

/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: TimePosition.java,v $
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
