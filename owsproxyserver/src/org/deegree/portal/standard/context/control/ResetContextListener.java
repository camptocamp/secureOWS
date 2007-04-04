/*----------------    FILE HEADER  ------------------------------------------

This file is part of deegree.
Copyright (C) 2001-2006 by:
University of Bonn
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

Klaus Greve
Department of Geography
University of Bonn
Meckenheimer Allee 166
53115 Bonn
Germany
E-Mail: klaus.greve@uni-bonn.de

 ---------------------------------------------------------------------------*/
package org.deegree.portal.standard.context.control;

import java.io.StringWriter;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.util.Debug;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.portal.Constants;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.context.XMLFactory;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.1, $Revision: 1.6 $, $Date: 2006/08/29 19:54:14 $
 * 
 * @since 1.1
 */
public class ResetContextListener extends AbstractListener {
        
    public void actionPerformed(FormEvent event) {

        String xslFilename = "file://" + getHomePath() + ContextSwitchListener.DEFAULT_CTXT2HTML;
        String newHtml;
        try {
            newHtml = doTransformContext(xslFilename);
        } catch (Exception e) {
            gotoErrorPage("<b>Error reseting context: </b>" +  e.getMessage() + "<br/>" +
                          StringTools.stackTraceToString(e)) ;
            return;
        }
        //get the servlet path using the session
        HttpSession session = ( (HttpServletRequest)this.getRequest() ).getSession();
        session.setAttribute(ContextSwitchListener.NEW_CONTEXT_HTML, newHtml);

    }
    
    /**
     * Transforms the context pointed to by <code>context</code> into html
     * using <code>xsltURL</code> (though this is currently fixed; there's 
     * really no need to define one's wn xsl).
     * @param context the context xml
     * @param xsl the transformation xml
     */
    protected String doTransformContext( String xsl ) throws Exception {
        Debug.debugMethodBegin();
        
        HttpSession session = ((HttpServletRequest)getRequest()).getSession( true );
        ViewContext vc = (ViewContext)session.getAttribute( Constants.CURRENTMAPCONTEXT );
        XMLFragment xml = XMLFactory.export(vc);

        XSLTDocument xslt = new XSLTDocument();
        xslt.load( new URL(xsl) );
        xml = xslt.transform( xml );
        StringWriter sw = new StringWriter( 30000 );
        xml.write( sw );
                      
        Debug.debugMethodEnd();
        return sw.toString();
    }
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ResetContextListener.java,v $
Revision 1.6  2006/08/29 19:54:14  poth
footer corrected

Revision 1.5  2006/07/12 14:46:18  poth
comment footer added

********************************************************************** */
