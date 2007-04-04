//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/DeleteFromShoppingCartListener.java,v 1.10 2006/07/31 11:02:44 mays Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2006 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de 

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstraße 19
 53177 Bonn
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

package org.deegree.portal.standard.csw.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.FormEvent;
import org.deegree.enterprise.control.RPCParameter;
import org.deegree.enterprise.control.RPCStruct;
import org.deegree.enterprise.control.RPCWebEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.model.SessionRecord;
import org.deegree.portal.standard.csw.model.ShoppingCart;

/**
 * A <code>${type_name}</code> class.<br/>
 * 
 * The Listener deletes an entry from the selection/shopping cart of a user. 
 * The required information is contained in the RPCWebEvent passed to the actionPerformed method.
 *
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0 $Revision: 1.10 $ $Date: 2006/07/31 11:02:44 $
 * 
 * @since 2.0
 */
public class DeleteFromShoppingCartListener extends AddToShoppingCartListener {
//  extends AddToShoppingCartListener --> SimpleSearchListener --> AbstractListener.
    
    private static final ILogger LOG = LoggerFactory.getLogger( DeleteFromShoppingCartListener.class );
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering();        
        
        RPCWebEvent rpcEvent = (RPCWebEvent)event;
        try {
            validateRequest( rpcEvent );            
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Invalid rpc request: \n" + e.getMessage() );
            LOG.exiting();
            return;
        }

        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession( true );
        ShoppingCart cart = (ShoppingCart)session.getAttribute( Constants.SESSION_SHOPPINGCART );
        if ( cart != null ) {
            
            RPCParameter[] params = rpcEvent.getRPCMethodCall().getParameters();
            
            for (int i = 0; i < params.length; i++) {
                RPCStruct struct = (RPCStruct)params[i].getValue();
                
                String identifier = (String)struct.getMember( Constants.RPC_IDENTIFIER ).getValue();
                String catalog = (String)struct.getMember( RPC_CATALOG ).getValue();
                String title = (String)struct.getMember( RPC_TITLE ).getValue();
                
                cart.remove( new SessionRecord( identifier, catalog, title ) );
            }
            
            // write the shopping cart back to the users session and to the request
            session.setAttribute( Constants.SESSION_SHOPPINGCART, cart );
            getRequest().setAttribute( Constants.SESSION_SHOPPINGCART, cart );
        }
                
        LOG.exiting();
        return;
    }

    protected void validateRequest( RPCWebEvent rpcEvent ) throws CatalogClientException {
        LOG.entering();
        
        RPCParameter[] params = extractRPCParameters( rpcEvent ); 
        
        // there are a varying number of params in this rpcEvent. Check them all !
        for( int i = 0; i < params.length; i++ ) {
            
            RPCStruct struct = extractRPCStruct( rpcEvent, i );
            Object member = extractRPCMember( struct, Constants.RPC_IDENTIFIER );
            if ( member == null ) {
                throw new CatalogClientException( "Identifier must be set in the rpcEvent." );
            }
            member = extractRPCMember( struct, RPC_CATALOG );
            if ( member == null ) {
                throw new CatalogClientException( "Catalog must be set in the rpcEvent." );
            }
            member = extractRPCMember( struct, RPC_TITLE );
            if ( member == null ) {
                throw new CatalogClientException( "Title must be set in the rpcEvent." );
            }    
            
        }
        
        LOG.exiting();
        return;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: DeleteFromShoppingCartListener.java,v $
Revision 1.10  2006/07/31 11:02:44  mays
move constants from class Constants to the classes where they are needed

Revision 1.9  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.8  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */
