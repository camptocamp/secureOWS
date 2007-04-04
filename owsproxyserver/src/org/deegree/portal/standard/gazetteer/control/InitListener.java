// $Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/gazetteer/control/InitListener.java,v 1.5 2006/08/29 19:54:14 poth Exp $
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
package org.deegree.portal.standard.gazetteer.control;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;



/**
 * Listener to initialize the gazetteer client. At the moment no specific 
 * actions are performed
 *
 * @version $Revision: 1.5 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.5 $, $Date: 2006/08/29 19:54:14 $
 *
 * @since 1.1
 */
public final class InitListener extends AbstractListener {
    
    /**
     * inherited from <tt>AbstractListener</tt> performes the request
     *
     * @param e 
     */
    public void actionPerformed( FormEvent e ) {
    }
    
}
/* ********************************************************************
   Changes to this class. What the people have been up to:
   $Log: InitListener.java,v $
   Revision 1.5  2006/08/29 19:54:14  poth
   footer corrected

   Revision 1.4  2006/04/06 20:25:32  poth
   *** empty log message ***

   Revision 1.3  2006/04/04 20:39:44  poth
   *** empty log message ***

   Revision 1.2  2006/03/30 21:20:29  poth
   *** empty log message ***

   Revision 1.1  2006/02/05 09:30:12  poth
   *** empty log message ***

   Revision 1.1.1.1  2005/01/05 10:30:06  poth
   no message

   Revision 1.2  2004/06/08 13:03:20  tf
   refactor to org.deegree.enterprise

   Revision 1.1  2004/05/22 09:55:36  ap
   no message

   Revision 1.1  2004/03/15 07:38:05  poth
   no message



********************************************************************** */
