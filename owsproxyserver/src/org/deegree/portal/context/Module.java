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
package org.deegree.portal.context;

import org.deegree.framework.util.Parameter;
import org.deegree.framework.util.ParameterList;



/**
 * this class encapsulates the basic informations of a module that is part of
 * an area of the GUI. Other classes may extent this class by adding special
 * attributes for more specialazied GUIs.
 *
 * @version $Revision: 1.8 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public class Module  {
    private ModuleConfiguration moduleConfiguration = null;
    private String content = null;
    private String name = null;
    private boolean hidden = false;
    private ParameterList parameterList = null;
    private String type = "content";
    private int width = 0;
    private int height = 0;
    private String[] moduleJSList = new String[0];
    private String scrolling = "auto"; 

    
    /**
     * Creates a new Module_Impl object.
     *
     * @param name name of the module
     * @param content the name of the page/class/file etc. containing the content
     *                of the module
     * @param hidden indicates if the module is visible or not
     * @param moduleConfiguration encapsulates the access to the modules 
     *                configuration (may be <tt>null</tt>)
     */
    public Module( String name, String content, boolean hidden,
            			String type, int width, int height, String scrolling,
						String[] moduleJSList, ModuleConfiguration moduleConfiguration,
                        ParameterList parameterList) {
    	setName( name );
        setContent( content );
        setHidden( hidden );
        setModuleConfiguration( moduleConfiguration );
        setParameter( parameterList );
        setType(type);
        setWidth(width);
        setHeight(height);
        setModuleJSList(moduleJSList);
        setScrolling( scrolling );
    }

    /**
     * returns the name of a module
     *
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of a module
     *
     * @param name 
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * returns the name of the page/class/file etc. containing the content
     * of the module
     */
    public String getContent() {
        return content;
    }

    /**
     * sets the name of the page/class/file etc. containing the content
     * of the module
     *
     * @param content 
     */
    public void setContent( String content ) {
        this.content = content;
    }

    /**
     * returns true if the module is hidden. this will always be the case
     * for modules that just offers functions to the context. visible modules
     * may offere the capability to be turned to visible or not.
     *
     * @return 
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * sets the module to be hidden or visible. modules that only adds functions
     * to a context will ignore this because they are always hidden
     *
     * @param hidden 
     */
    public void setHidden( boolean hidden ) {
        this.hidden = hidden;
    }

    /**
     * returns the a specific confguration for a module. This may be <tt>null</tt>
     * if the module doesn't need to be configured.
     *
     * @return 
     */
    public ModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    /**
     * sets the specific configuration for a module.
     *
     * @param configuration 
     */
    public void setModuleConfiguration( ModuleConfiguration configuration ) {
        this.moduleConfiguration = configuration;
    }
       
    /**
     * returns a list of parameters that will be passed to a class/object etc.
     * that represents a module
     *
     * @return parameters
     */
    public ParameterList getParameter() {
        return parameterList;
    }

    /**
     * sets a list of parameters that will be passed to a class/object etc.
     * that represents a module
     *
     * @param parameterList 
     */
    public void setParameter( ParameterList parameterList ) {
        this.parameterList = parameterList;
    }

    /**
     * adds a parameter to the list of parameters that will be passed to a 
     * class/object etc. that represents a module
     *
     * @param parameter 
     */
    public void addParameter( Parameter parameter ) {
        parameterList.addParameter( parameter );
    }

    /**
     * removes a parameter to the list of parameters that will be passed to a 
     * class/object etc. that represents a module
     *
     * @param name 
     */
    public void removeParameter( String name ) {
        parameterList.removeParameter( name );
    }
    
   
    
    /**
     * @see org.deegree.clients.context.Module#setType(java.lang.String)
     */
    public void setType( String type ){
        if ( type != null ) {
        	this.type = type.toLowerCase();
        }
    }
    
    /**
     * @see org.deegree.clients.context.Module#getType()
     */
    public String getType(){
        return type;
    } 
    
    /**
     * returns the width of the module in the GUI. If '0' will be returned
     * the GUI can set the with like it is best
     * @return
     */
    public int getWidth() {
    	return this.width;
    }
    
    /**
     * sets the desired width of the module in the GUI. If '0' ist passed
     * the GUI can set the with like it is best 
     * @param width desired width of the module
     */
    public void setWidth(int width) {
    	this.width = width;
    }
    
    /**
     * returns the height of the module in the GUI. If '0' will be returned
     * the GUI can set the with like it is best
     * @return
     */
    public int getHeight() {
    	return this.height;
    }
    
    /**
     * sets the desired height of the module in the GUI. If '0' ist passed
     * the GUI can set the with like it is best 
     * @param height desired width of the module
     */
    public void setHeight(int height) {
    	this.height = height;
    }
    
    public String[] getModuleJSList(){
        return moduleJSList;
    }
    public void setModuleJSList(String[] list){
        this.moduleJSList = list;
    }    
    
    /**
     * return true is the module should has scrollbars in the GUI<br>
     * possible values are
     * <UL>
     * 	<li>no
     *  <li>yes
     *  <li>auto
     * </UL>
     * default is auto
     * @return
     */
	public String getScrolling() {
		return scrolling;
	}
	
	/**
     * @see #getScrolling()
     * @param scroll
     */
	public void setScrolling(String scrollable) {
		this.scrolling = scrollable;
	}
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Module.java,v $
Revision 1.8  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
