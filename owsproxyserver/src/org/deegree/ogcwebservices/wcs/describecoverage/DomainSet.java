package org.deegree.ogcwebservices.wcs.describecoverage;

import org.deegree.datatypes.time.TimeSequence;
import org.deegree.ogcwebservices.wcs.WCSException;

/**
 * @version $Revision: 1.3 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.3 $, $Date: 2006/12/03 21:20:48 $
 * 
 * @since 2.0
 */

public class DomainSet implements Cloneable {

    private SpatialDomain spatialDomain = null;

    private TimeSequence timeSequence = null;

    /**
     * @param spatialDomain
     */
    public DomainSet( SpatialDomain spatialDomain ) throws WCSException {
        setSpatialDomain( spatialDomain );
    }

    /**
     * @param timeSequence
     */
    public DomainSet( TimeSequence timeSequence ) throws WCSException {
        setTimeSequence( timeSequence );
    }

    /**
     * @param spatialDomain
     * @param timeSequence
     */
    public DomainSet( SpatialDomain spatialDomain, TimeSequence timeSequence ) throws WCSException {
        this.spatialDomain = spatialDomain;
        this.timeSequence = timeSequence;
        if ( this.spatialDomain == null && this.timeSequence == null ) {
            throw new WCSException( "at least spatialDomain or timeSequence must " + "be <> null " );
        }
    }

    /**
     * @return Returns the spatialDomain.
     */
    public SpatialDomain getSpatialDomain() {
        return spatialDomain;
    }

    /**
     * @param spatialDomain The spatialDomain to set.
     */
    public void setSpatialDomain( SpatialDomain spatialDomain )
                            throws WCSException {
        if ( spatialDomain == null && this.timeSequence == null ) {
            throw new WCSException( "spatialDomain must be <> null because timeSequence "
                                    + "is already null" );
        }
        this.spatialDomain = spatialDomain;
    }

    /**
     * @return Returns the timeSequence.
     */
    public TimeSequence getTimeSequence() {
        return timeSequence;
    }

    /**
     * @param timeSequence The timeSequence to set.
     */
    public void setTimeSequence( TimeSequence timeSequence )
                            throws WCSException {
        if ( timeSequence == null && this.spatialDomain == null ) {
            throw new WCSException( "timeSequence must be <> null because spatialDomain "
                                    + "is already null" );
        }
        this.timeSequence = timeSequence;
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        SpatialDomain spatialDomain_ = null;
        if ( spatialDomain != null ) {
            spatialDomain_ = (SpatialDomain) spatialDomain.clone();
        }
        TimeSequence timeSequence_ = null;
        if ( timeSequence != null ) {
            timeSequence_ = (TimeSequence) timeSequence.clone();
        }
        try {
            return new DomainSet( spatialDomain_, timeSequence_ );
        } catch ( Exception e ) {
        }
        return null;
    }

}
/* ********************************************************************
 Changes to this class. What the people have been up to:
 $Log: DomainSet.java,v $
 Revision 1.3  2006/12/03 21:20:48  poth
 code formatting

 Revision 1.2  2005/01/18 22:08:55  poth
 no message

 Revision 1.3  2004/07/12 06:12:11  ap
 no message

 Revision 1.2  2004/05/25 07:19:13  ap
 no message

 Revision 1.1  2004/05/24 06:54:39  ap
 no message


 ********************************************************************** */
