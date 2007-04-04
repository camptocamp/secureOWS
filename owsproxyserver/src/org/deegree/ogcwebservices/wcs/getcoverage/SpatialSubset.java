package org.deegree.ogcwebservices.wcs.getcoverage;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wcs.WCSException;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: bezema $ *  * $Revision: 1.3 $, $Date: 2006/11/29 15:58:57 $ */

public class SpatialSubset {

    /**
     * 
     * @uml.property name="envelope"
     * @uml.associationEnd multiplicity="(0 1)"
     */
    private Envelope envelope = null;

    private Object grid = null;
 

    /**
     * @param envelope
     * @param grid
     * @throws WCSException if one of the parameters is null
     */
    public SpatialSubset(Envelope envelope, Object grid) throws WCSException {
        if ( envelope == null) {
            throw new WCSException( "envelope must be <> null for SpatialSubset" );
        }
        if ( grid == null) {
            throw new WCSException( "grid must be <> null for SpatialSubset" );
        }
        this.envelope = envelope;
        this.grid = grid;
    }

    /**
     * @return Returns the envelope.
     * 
     * @uml.property name="envelope"
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * @return Returns the grid.
     * 
     * @uml.property name="grid"
     */
    public Object getGrid() {
        return grid;
    }
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer(300);
        sb.append("envelope=(");
        sb.append( envelope );
        sb.append("), grid=("  );
        sb.append( grid );
        sb.append(')');
        return sb.toString();
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: SpatialSubset.java,v $
   Revision 1.3  2006/11/29 15:58:57  bezema
   added toString and fixed javadoc and warnings

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.3  2004/06/28 06:26:52  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
