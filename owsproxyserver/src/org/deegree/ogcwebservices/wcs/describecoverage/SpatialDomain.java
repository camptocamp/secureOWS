package org.deegree.ogcwebservices.wcs.describecoverage;

import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.EnvelopeImpl;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceImpl;
import org.deegree.ogcwebservices.wcs.WCSException;

/**
 * @version $Revision: 1.3 $ * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a> * @author last edited by: $Author: poth $ *  * @version 1.0. $Revision: 1.3 $, $Date: 2006/12/03 21:20:48 $ *  * @since 2.0
 */

public class SpatialDomain implements Cloneable {

    private Envelope[] envelops = null;

    private Object[] grid = new Object[0];

    private Surface[] surface = new Surface[0];

    
    /**
     * @param envelops
     */
    public SpatialDomain(Envelope[] envelops) throws WCSException {
        setEnvelops(envelops);
    }
    
    /**
     * @param envelops
     * @param surface
     */
    public SpatialDomain(Envelope[] envelops, Surface[] surface) throws WCSException {
        setEnvelops(envelops);
        setSurface(surface);
    }
    
    /**
     * @param envelops
     * @param grid
     */
    public SpatialDomain(Envelope[] envelops, Object[] grid) throws WCSException {
        setEnvelops(envelops);
        setGrid(grid);
    }
    
    /**
     * @param envelops
     * @param grid
     */
    public SpatialDomain(Envelope[] envelops, Surface[] surface, Object[] grid) 
                        throws WCSException {
        setEnvelops(envelops);
        setGrid(grid);
        setSurface(surface);
    }

    /**
     * @return Returns the envelops.
     * 
     */
    public Envelope[] getEnvelops() {
        return envelops;
    }

    /**
     * @param envelops The envelops to set.
     * 
     */
    public void setEnvelops(Envelope[] envelops) throws WCSException {
        if (envelops == null) {
            throw new WCSException("At least one envelop must be defined for "
                + "a SpatialDomain!");
        }
        this.envelops = envelops;
    }

    /**
     * @return Returns the grid.
     * 
     */
    public Object[] getGrid() {
        return grid;
    }

    /**
     * @param grid The grid to set.
     * 
     */
    public void setGrid(Object[] grid) {
        if (grid == null) {
            grid = new Object[0];
        }
        this.grid = grid;
    }

    /**
     * @return Returns the surface.
     * 
     */
    public Surface[] getSurface() {
        return surface;
    }

    /**
     * @param surface The surface to set.
     * 
     */
    public void setSurface(Surface[] surface) {
        if (surface == null) {
            surface = new Surface[0];
        }
        this.surface = surface;
    }

    
    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            Envelope[] env = new Envelope[envelops.length];
            for (int i = 0; i < env.length; i++) {
                env[i] = (Envelope)((EnvelopeImpl)envelops[i]).clone();
            }
            
            Surface[] surf = new Surface[surface.length];
            for (int i = 0; i < surf.length; i++) {
                surf[i] = (Surface)((SurfaceImpl)surface[i]).clone();
            }
            return new SpatialDomain( env, surf, grid );
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: SpatialDomain.java,v $
   Revision 1.3  2006/12/03 21:20:48  poth
   code formatting

   Revision 1.2  2005/01/18 22:08:55  poth
   no message

   Revision 1.4  2004/07/14 06:52:48  ap
   no message

   Revision 1.3  2004/06/28 06:26:52  ap
   no message

   Revision 1.2  2004/05/25 07:19:13  ap
   no message

   Revision 1.1  2004/05/24 06:54:39  ap
   no message


********************************************************************** */
