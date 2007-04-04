package org.deegree.ogcwebservices.wcs.getcoverage;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.time.TimeSequence;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.wcs.WCSException;

/**
 * @version $Revision: 1.5 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: bezema $ *  * $Revision: 1.5 $, $Date: 2006/11/29 15:58:57 $ */

public class DomainSubset {

    private Code requestSRS = null;
    private SpatialSubset spatialSubset = null;
    private TimeSequence temporalSubset = null;

   
    /**
     * @param requestSRS 
     * @param spatialSubset
     * @throws WCSException 
     */
    public DomainSubset(Code requestSRS, SpatialSubset spatialSubset) throws WCSException {
        this( requestSRS, spatialSubset, null );
    }
    
    /**
     * @param requestSRS 
     * @param temporalSubset
     * @throws WCSException 
     */
    public DomainSubset(Code requestSRS, TimeSequence temporalSubset) throws WCSException {
        this( requestSRS, null, temporalSubset );
    }
    
    /**
     * @param requestSRS 
     * @param spatialSubset
     * @param temporalSubset
     * @throws WCSException if one of the parameters is null
     */
    public DomainSubset(Code requestSRS, SpatialSubset spatialSubset, TimeSequence temporalSubset) 
                        throws WCSException {
        if ( spatialSubset == null && temporalSubset == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new WCSException( "GetCoverage", "at least spatialSubset " +
                    "or temporalSubset must be <> null in DomainSubset", code );
        }
        if ( requestSRS == null ) {
            ExceptionCode code = ExceptionCode.MISSINGPARAMETERVALUE;
            throw new WCSException( "GetCoverage", "'crs/requestSRS' is missing", code );
        }
        this.requestSRS = requestSRS;
        this.spatialSubset = spatialSubset;
        this.temporalSubset = temporalSubset;
    }

    /**
     * @return Returns the spatialSubset.
     * 
     */
    public SpatialSubset getSpatialSubset() {
        return spatialSubset;
    }

    /**
     * @return Returns the temporalSubset.
     */
    public TimeSequence getTemporalSubset() {
        return temporalSubset;
    }

    /**
     * @return Returns the requestSRS.
     */
    public Code getRequestSRS() {
        return requestSRS;
    }
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer(300);
        sb.append("requestSRS=");
        sb.append( requestSRS );
        sb.append(", spatialSubset="  );
        sb.append( spatialSubset );
        return sb.toString();
    }

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DomainSubset.java,v $
Revision 1.5  2006/11/29 15:58:57  bezema
added toString and fixed javadoc and warnings

Revision 1.4  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
