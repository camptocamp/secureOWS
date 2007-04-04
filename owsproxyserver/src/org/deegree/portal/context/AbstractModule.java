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




/**
 * describes the common access to modules embedded into the GUI described by 
 * general extension section of a web map context document within the deegree
 * framework.<p/>
 * a module encapsulates GUI elements and/or functions that are used by the
 * GUI. The concrete implementation of the GUI (e.g. as JSP pages) is responsible
 * for enabling the commuication between the different modules of a context.
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 */
public abstract class AbstractModule {
    private ModuleConfiguration moduleConfiguration = null;
    private String name = null;
    private String content = null;
    private boolean hidden = false;

    /**
     * Creates a new AbstractModule object.
     *
     * @param name 
     * @param hidden 
     * @param moduleConfiguration 
     */
    public AbstractModule( String name, String content, boolean hidden, 
                           ModuleConfiguration moduleConfiguration ) {
        setName( name );
        setContent( content );
        setHidden( hidden );
        setModuleConfiguration( moduleConfiguration );
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
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: AbstractModule.java,v $
Revision 1.7  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
