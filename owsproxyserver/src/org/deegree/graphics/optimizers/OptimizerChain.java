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
package org.deegree.graphics.optimizers;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Allows the chaining of <tt>Optimizer<tt>s. Does implement the
 * <tt>Optimizer<tt>-interface as well. 
 * <p>
 * @author <a href="mailto:mschneider@lat-lon.de">Markus Schneider</a>
 * @version $Revision: 1.6 $ $Date: 2006/07/12 14:46:19 $
 */
public class OptimizerChain extends AbstractOptimizer {

	// stores the Optimizers	
	private ArrayList optimizers = new ArrayList ();
    
    /**
	 * Constructs a new empty <tt>OptimizerChain</tt> 
	 */
	public OptimizerChain () {
    }

	/**
	 * Constructs a new <tt>OptimizerChain</tt> that contains the submitted
	 * <tt>Optimizer</tt> instances.
	 * <p>
	 * @param optimizers
	 */
	public OptimizerChain (AbstractOptimizer [] optimizers) {
		for (int i = 0; i < optimizers.length; i++) {
			this.optimizers.add (optimizers [i]);
		}		
	}

	/**
	 * Appends an <tt>Optimizer</tt> to the end of the processing chain. 
	 * <p>
	 * @param optimizer
	 */
	public void addOptimizer(AbstractOptimizer optimizer) {
		optimizers.add (optimizer);
	}

    /**
     * Performs the optimization. Calls the optimize-method of all contained
     * <tt>Optimizer</tt> instances subsequently.
     * <p>
     * @param g
     */
    public void optimize(Graphics2D g) throws Exception {
    	for (int i = 0; i < optimizers.size(); i++) {
    		((AbstractOptimizer) optimizers.get(i)).optimize(g);
    	}
    }     
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: OptimizerChain.java,v $
Revision 1.6  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
