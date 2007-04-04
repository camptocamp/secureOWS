//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/portal/standard/csw/control/InitCSWModuleListener.java,v 1.5 2006/07/31 09:33:58 mays Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.deegree.enterprise.control.AbstractListener;
import org.deegree.enterprise.control.FormEvent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ParameterList;
import org.deegree.portal.context.GUIArea;
import org.deegree.portal.context.GeneralExtension;
import org.deegree.portal.context.Module;
import org.deegree.portal.context.ViewContext;
import org.deegree.portal.standard.csw.CatalogClientException;
import org.deegree.portal.standard.csw.configuration.CSWClientConfiguration;

/**
 * TODO 
 * - javadoc
 * - except handling 
 * 
 * @author <a href="mailto:mays@lat-lon.de">Judit Mays</a>
 * @author last edited by: $Author: mays $
 * 
 * @version 2.0, $Revision: 1.5 $, $Date: 2006/07/31 09:33:58 $
 * 
 * @since 2.0
 */
public class InitCSWModuleListener extends AbstractListener {

    private static final ILogger LOG = LoggerFactory.getLogger( InitCSWModuleListener.class );
    
    public void actionPerformed( FormEvent event ) {
        LOG.entering();
        
		HttpSession session = ((HttpServletRequest)getRequest()).getSession();
		ViewContext vc = (ViewContext)session.getAttribute( org.deegree.portal.Constants.CURRENTMAPCONTEXT );
		GeneralExtension gen = (GeneralExtension)vc.getGeneral().getExtension();
		
		Module module = null;
		
		try {
		    module = findCswClientModule( gen );
        } catch ( Exception e ) {
            e.printStackTrace();
            gotoErrorPage( "Client error: " + e.getMessage() );
            LOG.exiting();
            return;
        }

        CSWClientConfiguration config = new CSWClientConfiguration();
        
		ParameterList parList = module.getParameter();
		
		try {
            initConfig( config, parList);
        } catch( CatalogClientException e ) {
            e.printStackTrace();
            gotoErrorPage( "Client error: " + e.getMessage() );
            LOG.exiting();
            return;
        }
		
        // srs is available from context
        String srs = "EPSG:4236";
        srs = vc.getGeneral().getBoundingBox()[0].getCoordinateSystem().getName();
        config.setSrs( srs );
        
		session.setAttribute( Constants.CSW_CLIENT_CONFIGURATION, config );
		
        LOG.exiting();
        return;
    }

    /**
     * @param config
     * @param parList
     * @throws CatalogClientException 
     * @throws CatalogClientException 
     */
    protected void initConfig( CSWClientConfiguration config, ParameterList parList ) 
                                                    throws CatalogClientException {
        
        String value = extractOptionalSingleValue( parList, "maxRecords" );
        if ( value != null ) {
            config.setMaxRecords( Integer.valueOf( value ).intValue() );
        }         
    
        String[][] kvp = null;

        String[] profileNames = extractMandatoryProfileNames( parList ); 
        for( int i = 0; i < profileNames.length; i++ ) {
            HashMap keyToXSL = new HashMap();
            String[] profileValues = extractOptionalMultiValues( parList, profileNames[i] );
            kvp = extractKvpFromParamsList( profileValues );
            for (int j = 0; j < kvp[0].length; j++) {
                keyToXSL.put( kvp[0][j], kvp[1][j] ); //elementSetName=kvp[0][j], xslFile=kvp[1][j]
            }
            config.addProfileXSL( profileNames[i], keyToXSL );
        }
        
		String[] catalogueValues = extractMandatoryMultiValues( parList, "Catalogues" );
		kvp = extractKvpFromParamsList( catalogueValues );
		for (int i = 0; i < kvp[0].length; i++) {
            config.addCatalogueURL( kvp[0][i], kvp[1][i]);
        }
        
        String[] protocolValues = extractMandatoryMultiValues( parList, "Protocols" );
        kvp = extractKvpFromParamsList( protocolValues );
        for (int i = 0; i < kvp[0].length; i++) {
            String[] protocols = kvp[1][i].split( "," );
            List<String> list = new ArrayList<String>( protocols.length );
            for (int j = 0; j < protocols.length; j++) {
                list.add( protocols[j] );
            }
            config.addCatalogueProtocol( kvp[0][i], list);
        }
        
        String[] formatValues = extractMandatoryMultiValues( parList, "Formats" );
        kvp = extractKvpFromParamsList( formatValues );
        for (int i = 0; i < kvp[0].length; i++) {
            String[] formats = kvp[1][i].split( "," );
            List<String> list = new ArrayList<String>( formats.length );
            for (int j = 0; j < formats.length; j++) {
                list.add( formats[j] );
            }
            config.addCatalogueFormat( kvp[0][i], list);
        }

        // path to mapContextTemplate
        config.setMapContextTemplatePath( extractMandatorySingleValue( parList, "mapContextTemplate" ) );
        
        // all namspace bindings
        config.setNamespaceBindings( extractMandatoryMultiValues( parList, "namespaceBindings" ) );
        
        // xPath in data catalog
        config.setXPathToDataIdentifier( extractMandatorySingleValue( parList, "XPathToDataId" ) );
        config.setXPathToDataTitle( extractMandatorySingleValue( parList, "XPathToDataTitle" ) );
        config.setXPathToDataTitleFull( extractMandatorySingleValue( parList, "XPathToDataTitleFull" ) );
        
        // xPath in service catalog
        config.setXPathToServiceIdentifier( extractMandatorySingleValue( parList, "XPathToServiceId" ) );
        config.setXPathToServiceTitle( extractMandatorySingleValue( parList, "XPathToServiceTitle" ) );
        config.setXPathToServiceOperatesOnTitle( extractMandatorySingleValue( parList, "XPathToServiceOperatesOnTitle" ) );
        config.setXPathToServiceAddress( extractMandatorySingleValue( parList, "XPathToServiceAddress" ) );
        config.setXPathToServiceType( extractMandatorySingleValue( parList, "XPathToServiceType" ) );
        config.setXPathToServiceTypeVersion( extractMandatorySingleValue( parList, "XPathToServiceTypeVersion" ) );

        
        /* TODO implement or delete initialBbox
        String initialBbox = (String)parList.getParameter( "InitialBbox" ).getValue();
        initialBbox = initialBbox.substring(1, initialBbox.length() - 1 );
        Envelope env = createBboxFromString( initialBbox );
        */
    }
    
    /**
     * @param paramList
     * @return Returns a String[] containing all profile names from the passed parameter list.
     * @throws CatalogClientException if the mandatory parameter is not part of the parameter list.
     */
    private String[] extractMandatoryProfileNames( ParameterList paramList ) 
                                             throws CatalogClientException {
        
        String[] paramNames = paramList.getParameterNames();
        List<String> profileNames = new ArrayList<String>( paramNames.length );
        
        for( int i = 0; i < paramNames.length; i++ ) {
            if ( paramNames[i].startsWith( "Profiles." )  ) {
                profileNames.add( paramNames[i] ); 
            }
        }
        if ( profileNames.size() < 1 ) {
            throw new CatalogClientException( "The configuration file does not contain any profile " +
                                              "parameter. At least one is mandatory." );
        }

        return (String[])profileNames.toArray( new String[profileNames.size()] );
    }
    
    /**
     * @param paramList
     * @param parameter
     * @return Returns the value for the passed parameter. 
     *         May return null, if the parameter is not part of the passed parameter list.
     */
    private String extractOptionalSingleValue( ParameterList paramList, String parameter ) {
        
        String value = null;
        if ( paramList.getParameter( parameter ) != null ) {
            value = (String)paramList.getParameter( parameter ).getValue();
            
            if ( value.startsWith( "'" ) && value.endsWith( "'" ) ) {
                // strip ' from front and end of string
                value = value.substring( 1, value.length()-1 ); 
            }
        }
        return value;
    }
    
    /**
     * @param paramList
     * @param parameter
     * @return Returns a String[] containing all values (separated at ";") for the passed parameter.
     *         May return null, if the parameter is not part of the passed parameter list.
     */
    private String[] extractOptionalMultiValues( ParameterList paramList, String parameter ) {
        
        String multiValues = null;
        if ( paramList.getParameter( parameter ) != null ) {
            multiValues = (String)paramList.getParameter( parameter ).getValue();

            if ( multiValues.startsWith( "'" ) && multiValues.endsWith( "'" ) ) {
                // strip ' from front and end of string
                multiValues = multiValues.substring( 1, multiValues.length()-1 ); 
            }    
        }
        return multiValues == null ? null : multiValues.split( ";" );
    }
    
    /**
     * @param paramList
     * @param parameter
     * @return Returns the single value for the passed parameter.
     * @throws CatalogClientException if the mandatory parameter is not part of the parameter list.
     */
    private String extractMandatorySingleValue( ParameterList paramList, String parameter ) 
                                            throws CatalogClientException {
        String value = null;
        try {
            value = (String)paramList.getParameter( parameter ).getValue();
        } catch (Exception e) {
            throw new CatalogClientException( 
                "The configuration file does not contain the mandatory parameter: "+ parameter );
        }
        if ( value.startsWith( "'" ) && value.endsWith( "'" ) ) {
            value = value.substring( 1, value.length()-1 ); //strip ' from front and end of string    
        }     
        
        return value;
    }

    /**
     * @param paramList
     * @param parameter
     * @return Returns a String[] containing all values (separated by ";") for the passed parameter.
     * @throws CatalogClientException if the mandatory parameter is not part of the parameter list.
     */
    private String[] extractMandatoryMultiValues( ParameterList paramList, String parameter ) 
                                                        throws CatalogClientException {
        String multiValues = null;
        try {
            multiValues = (String)paramList.getParameter( parameter ).getValue();
        } catch (Exception e) {
            throw new CatalogClientException( 
                "The configuration file does not contain the mandatory parameter: "+ parameter );
        }
        if ( multiValues.startsWith( "'" ) && multiValues.endsWith( "'" ) ) {
            // strip ' from front and end of string
            multiValues = multiValues.substring( 1, multiValues.length()-1 ); 
        }
        
        return multiValues.split( ";" );
    }
    
    /**
     * @param values
     * @return Returns the key value pairs for the passed values from the parameter list.
     */
    private String[][] extractKvpFromParamsList( String[] values ) {
        
        String[][] kvp = new String[2][];
        //kvp[0][i] = key[i]
        //kvp[1][i] = value[i]

		kvp[0] = new String[ values.length ];
		kvp[1] = new String[ values.length ];

        //FIXME unsafe! assuming AOK, but should catch exceptions for the split at "|"
		for (int i = 0; i < values.length; i++) {
		    String[] tmpKVP = values[i].split( "\\|" );
		    kvp[0][i] = tmpKVP[0];          
		    kvp[1][i] = tmpKVP[1];          
        }
		
		return kvp;
    }

    /**
     * @param gen
     * @return Returns the csw client module.
     * @throws CatalogClientException
     *             if the csw client module cannot be found.
     */
    protected Module findCswClientModule( GeneralExtension gen )
                            throws CatalogClientException {
        final String moduleName = "CswModule";

        GUIArea guiArea = gen.getFrontend().getNorth();
        Module module = guiArea.getModule( moduleName );

        // there should be a method for getting all gui areas
        if ( module == null ) {
            guiArea = gen.getFrontend().getSouth();
            module = guiArea.getModule( moduleName );
        }
        if ( module == null ) {
            guiArea = gen.getFrontend().getWest();
            module = guiArea.getModule( moduleName );
        }
        if ( module == null ) {
            guiArea = gen.getFrontend().getEast();
            module = guiArea.getModule( moduleName );
        }
        if ( module == null ) {
            guiArea = gen.getFrontend().getCenter();
            module = guiArea.getModule( moduleName );
        }
        if ( module == null ) {
            throw new CatalogClientException( "Could not find CswClient module in initial MapContext",
                                              null );
        }

        return module;
    }
    
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: InitCSWModuleListener.java,v $
Revision 1.5  2006/07/31 09:33:58  mays
move Constants to package control, update imports

Revision 1.4  2006/06/23 13:38:25  mays
add/update csw control files

********************************************************************** */