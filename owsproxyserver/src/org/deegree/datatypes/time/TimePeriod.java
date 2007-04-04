package org.deegree.datatypes.time;

import java.io.Serializable;
import java.net.URI;

/**
 * @version $Revision: 1.4 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.4 $, $Date: 2006/02/23 17:35:12 $ *  * @since 2.0
 */

public class TimePeriod implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private TimePosition beginPosition = null;
    private TimePosition endPosition = null;
    private TimeDuration timeResolution = null;

    private URI frame = null;
   
    /**
     * @param beginPosition
     * @param endPosition
     * @param timeResolution
     */
    public TimePeriod(TimePosition beginPosition, TimePosition endPosition, 
                      TimeDuration timeResolution) {
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
        this.timeResolution = timeResolution;
    }
    /**
     * @param beginPosition
     * @param endPosition
     * @param timeResolution
     * @param frame
     */
    public TimePeriod(TimePosition beginPosition, TimePosition endPosition, 
                      TimeDuration timeResolution, URI frame) {
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
        this.timeResolution = timeResolution;
        this.frame = frame;
    }

    /**
     * @return Returns the beginPosition.
     */
    public TimePosition getBeginPosition() {
        return beginPosition;
    }

    /**
     * @param beginPosition The beginPosition to set.
     */
    public void setBeginPosition(TimePosition beginPosition) {
        this.beginPosition = beginPosition;
    }

    /**
     * @return Returns the endPosition.
     */
    public TimePosition getEndPosition() {
        return endPosition;
    }

    /**
     * @param endPosition The endPosition to set.
     */
    public void setEndPosition(TimePosition endPosition) {
        this.endPosition = endPosition;
    }

    /**
     * @return Returns the frame.
     */
    public URI getFrame() {
        return frame;
    }

    /**
     * @param frame The frame to set.
     */
    public void setFrame(URI frame) {
        this.frame = frame;
    }

    /**
     * @return Returns the timeResolution.
     */
    public TimeDuration getTimeResolution() {
        return timeResolution;
    }

    /**
     * @param timeResolution The timeResolution to set.
     */
    public void setTimeResolution(TimeDuration timeResolution) {
        this.timeResolution = timeResolution;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        URI fr = null;
        if ( frame != null ) {
            try {
                fr = new URI( frame.toString() );
            } catch(Exception e) {}
        }
        return new TimePeriod( (TimePosition)beginPosition.clone(), 
                               (TimePosition)endPosition.clone(), 
                               (TimeDuration)timeResolution.clone(), fr );    
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: TimePeriod.java,v $
   Revision 1.4  2006/02/23 17:35:12  poth
   *** empty log message ***

   Revision 1.3  2005/11/16 13:44:59  mschneider
   Merge of wfs development branch.

   Revision 1.2.2.1  2005/11/14 11:34:29  deshmukh
   inserted: serialVersionID

   Revision 1.2  2005/01/18 22:08:54  poth
   no message

   Revision 1.2  2004/08/16 06:23:33  ap
   no message

   Revision 1.1  2004/05/25 12:55:01  ap
   no message


********************************************************************** */
