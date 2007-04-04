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
package org.deegree.enterprise.control;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.deegree.framework.util.StringTools;
import org.opengis.parameter.ParameterValue;

/**
 * The abstract listener allows the reuse of basic functionality. *  * @author  <a href="mailto:tfriebe@gmx.net">Torsten Friebe</a> * @author  <a href="mailto:poth@lat-lon.de">Andreas Poth</a> *  * @version $Revision: 1.15 $
 */

public abstract class AbstractListener implements WebListener {

    private FormEvent event;
    private Object returnValue;
    private String alternativeDefaultTarget;
    private String alternativeNext;
    private String defaultTarget;
    private String next;
    private List<ParameterValue> params;

    /**
     *
     *
     * @param e 
     */
    public abstract void actionPerformed( FormEvent e );

    /**
     *
     *
     * @param e 
     */
    public final void handle( FormEvent e ) {
        this.event = e;
        this.getNextPageFormRequest();
        this.actionPerformed( e );
        getRequest().setAttribute( "returnValue", getReturnValue() );        
        getRequest().setAttribute( "next", getNextPage() );
    }

    /**
     *
     *
     * @return 
     */
    public ServletRequest getRequest() {
        Object source = this.event.getSource();
        return (ServletRequest)source;
    }
    
    public String getHomePath() {
        String path2Dir =  ((HttpServletRequest)this.getRequest() )
                .getSession( true ).getServletContext().getRealPath("/");
        if ( !path2Dir.startsWith( "/" ) ) {
            path2Dir = '/' + path2Dir;
        }
        return path2Dir;
    }

    /**
     *
     *
     * @param target 
     */
    protected final void setDefaultNextPage( String target ) {
        this.defaultTarget = target;
    }

    /**
     *
     *
     * @param target 
     */
    protected final void setDefaultAlternativeNextPage( String target ) {
        this.alternativeDefaultTarget = target;
    }

    /**
     * Sets the next page for this request.
     */
    public void setNextPage( String target ) {
        this.next = target;
    }

    /**
     *
     *
     * @return 
     */
    public String getNextPage() {
        return ( ( this.next == null ) ? this.defaultTarget : this.next );
    }

    /**
     *
     *
     * @param target 
     */
    public void setAlternativeNextPage( String target ) {
        this.alternativeNext = target;
    }

    /**
     *
     *
     * @return 
     */
    public String getAlternativeNextPage() {
        return ( ( this.alternativeNext == null )
                 ? this.alternativeDefaultTarget : this.alternativeNext );
    }

    /**
     * @return
     * 
     */
    public Object getReturnValue() {
        return this.returnValue;
    }

    /**
     * @param model
     * 
     */
    public void setReturnValue(Object model) {
        this.returnValue = model;
    }

    /**
     *
     */
    private void getNextPageFormRequest() {
        String target = null;
        if ( ( target = this.getRequest().getParameter( "nextPage" ) ) != null ) {
            this.setNextPage( target );
        }
    }

    /**
    *
    */
    protected void gotoErrorPage( String message ) {
        getRequest().setAttribute( "SOURCE", "" + this.getClass().getName() );
        getRequest().setAttribute( "MESSAGE", message );
        setNextPage( "error.jsp" );
    }
    
    /**
     * sets the list of assigned initialization parameters
     * @param params
     */
    void setInitParameterList(List<ParameterValue> params) {
        this.params = params;
    }
    
    /**
     * @see #setInitParameterList(List)
     * @return
     */
    public List<ParameterValue> getInitParameterList() {
        return params;
    }
    
    /**
     * returns a named initi parameter or <code>null</code> if the 
     * parameter is not known
     * @param name
     * @return
     */
    public String getInitParameter(String name) {
        for ( int i = 0; i < params.size(); i++ ) {
            ParameterValue param = params.get( i );
            if ( param.getDescriptor().getName( Locale.getDefault() ).equals( name ) ) {
                return (String)param.getValue();
            }
        }
        return null;
    }
    
    /**
     * transforms the request to a set of name value pairs stored in a HashMap
     */
    protected HashMap toModel() {
        HashMap model = new HashMap();
        ServletRequest req = getRequest();
        Enumeration iterator = req.getParameterNames();

        while ( iterator.hasMoreElements() ) {
            String name = (String)iterator.nextElement();
            String[] value = req.getParameterValues( name );

            int pos = name.indexOf( '@' ) + 1;

            if ( pos < 0 ) {
                pos = 0;
            }

            name = name.substring( pos, name.length() );
            model.put( name.toUpperCase(), StringTools.arrayToString( value, ',' ) );
        }

        return model;
    }
}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractListener.java,v $
Revision 1.15  2006/08/24 16:24:16  poth
initial check in

Revision 1.14  2006/07/12 14:46:17  poth
comment footer added

********************************************************************** */
