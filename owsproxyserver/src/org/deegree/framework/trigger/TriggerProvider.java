//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/framework/trigger/TriggerProvider.java,v 1.7 2006/10/09 20:04:33 poth Exp $
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
package org.deegree.framework.trigger;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.trigger.TriggerCapabilities.TRIGGER_TYPE;
import org.deegree.framework.util.BootLogger;
import org.deegree.i18n.Messages;

/**
 * 
 * 
 *
 * @version $Revision: 1.7 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 *
 * @version 1.0. $Revision: 1.7 $, $Date: 2006/10/09 20:04:33 $
 *
 * @since 2.0
 */
public class TriggerProvider {
    
    private static Map<String,TriggerProvider>providerMap = new HashMap<String,TriggerProvider>();
    private static TriggerCapabilities triggerCapabilities;
    
    static {
        try {
            URL url = TriggerProvider.class.getResource( "triggerConfiguration.xml" );
            TriggerConfigurationDocument doc = new TriggerConfigurationDocument();
            doc.load( url );
            triggerCapabilities = doc.parseTriggerCapabilities();
            // try reading trigger definitions from root that may overrides
            // default trigger
            url = TriggerProvider.class.getResource( "/triggerConfiguration.xml" );
            try {
                if ( url != null ) {
                    doc = new TriggerConfigurationDocument();
                    doc.load( url );
                    TriggerCapabilities temp = doc.parseTriggerCapabilities();
                    triggerCapabilities.merge( temp );
                }
            } catch ( Exception e ) {
                BootLogger.log( "!!! BOOTLOG: No valid trigger configuration available from root." );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private String className = null;
	
	private TriggerProvider(String className) {
	    this.className = className;
	}

	/**
	 * 
	 * @param clss
	 */
	public static TriggerProvider create(Class clss){
        String s = clss.getName();
        if ( providerMap.get( s ) == null ) {
            providerMap.put( s, new TriggerProvider( s ) ); 
        }
		return providerMap.get( s );
	}

	/**
	 * returns all pre triggers assigend to the calling method
	 */
	public List<Trigger> getPreTrigger() throws TriggerException {
        
        List<Trigger> trigger = new ArrayList(); 
        
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        TriggerCapability tc = 
            triggerCapabilities.getTriggerCapability( className, st[4].getMethodName(),
                                                      TRIGGER_TYPE.PRE );
        if ( tc != null ) {
            appendTrigger( trigger, tc );
            
            List<TriggerCapability>tCaps = tc.getTrigger();
            for ( int i = 0; i < tCaps.size(); i++ ) {
                appendTrigger( trigger, tCaps.get( i ) );
            }
        }
        
		return trigger;
	}
    
    /**
     * returns all post triggers assigend to the calling method
     */
    public List<Trigger> getPostTrigger() throws TriggerException {
        List<Trigger> trigger = new ArrayList(); 

        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        TriggerCapability tc = 
            triggerCapabilities.getTriggerCapability( className, st[4].getMethodName(),
                                                      TRIGGER_TYPE.POST );
        
        if ( tc != null ) {
            appendTrigger( trigger, tc );
            
            List<TriggerCapability>tCaps = tc.getTrigger();
            for ( int i = 0; i < tCaps.size(); i++ ) {
                appendTrigger( trigger, tCaps.get( i ) );
            }
        }

        return trigger;
    }
    
    /**
     * creates a Trigger instance from the passed TriggerCapability and
     * add it to the passed list. If the TriggerCapability contains futher
     * TriggerCapability entries they will also be added within a 
     * recursion
     * 
     * @param trigger
     * @param tc
     * @return
     * @throws TriggerException
     */
    private List<Trigger> appendTrigger( List<Trigger> trigger, TriggerCapability tc )
                            throws TriggerException {
        Class clss = tc.getPerformingClass();
        List<String> paramNames = tc.getInitParameterNames();
        Class[] initClasses = new Class[paramNames.size()];
        Object[] paramVals = new Object[paramNames.size()];
        for ( int i = 0; i < initClasses.length; i++ ) {
            paramVals[i] = tc.getInitParameterValue( paramNames.get( i ) );
            initClasses[i] = paramVals[i].getClass();
        }
        try {
            Constructor cstrtr = clss.getConstructor( initClasses );
            trigger.add( (Trigger)cstrtr.newInstance( paramVals ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new TriggerException( Messages.getMessage( "FRAMEWORK_ERROR_INITIALIZING_TRIGGER", clss ) );
        }
        return trigger;
    }

	/**
	 * performs pre triggers assigend to the calling method
     * @param caller
	 * @param obj
	 * @throws TriggerException 
	 */
	public Object[] doPreTrigger(Object caller, Object... obj) throws TriggerException{
        List<Trigger> list = getPreTrigger();
        for ( int i = 0; i < list.size(); i++ ) {
            obj = list.get( i ).doTrigger( caller, obj );
        }
		return obj;
	}
    
    /**
     * performs post triggers assigend to the calling method
     * @param caller
     * @param obj
     */
    public Object[] doPostTrigger(Object caller, Object... obj){
        List<Trigger> list = getPostTrigger();
        for ( int i = 0; i < list.size(); i++ ) {            
            obj = list.get( i ).doTrigger( caller, obj );
        }
        return obj;
    }

    /**
     * returns the root capabilities
     * @return
     */
	public TriggerCapabilities getCapabilities(){
		return triggerCapabilities;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: TriggerProvider.java,v $
Revision 1.7  2006/10/09 20:04:33  poth
bug fix - null checking when reading configuration from root

Revision 1.6  2006/10/09 14:52:57  poth
enable overload of trigger configuration by adding a configuration document into the root directory

Revision 1.5  2006/09/28 09:45:45  poth
bug fixes

Revision 1.4  2006/09/27 14:23:22  poth
bug fix

Revision 1.3  2006/09/25 06:29:54  poth
static block for reading configuration changed

Revision 1.2  2006/09/23 09:02:39  poth
first working implementation

Revision 1.1  2006/09/14 16:06:05  poth
initial check in


********************************************************************** */