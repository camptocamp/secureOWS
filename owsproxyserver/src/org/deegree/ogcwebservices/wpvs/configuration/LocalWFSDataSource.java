//$$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/ogcwebservices/wpvs/configuration/LocalWFSDataSource.java,v 1.19 2006/11/27 15:41:13 bezema Exp $$
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

package org.deegree.ogcwebservices.wpvs.configuration;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcwebservices.OGCWebService;
import org.deegree.ogcwebservices.getcapabilities.OGCCapabilities;
import org.deegree.ogcwebservices.wfs.WFServiceFactory;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfiguration;
import org.deegree.ogcwebservices.wfs.configuration.WFSConfigurationDocument;
import org.deegree.ogcwebservices.wpvs.capabilities.OWSCapabilities;
import org.deegree.owscommon.OWSDomainType;


/**
 * This class represents a local WFS dataSource object.
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * @author last edited by: $Author: bezema $
 * 
 * @version 2.0, $Revision: 1.19 $, $Date: 2006/11/27 15:41:13 $
 * 
 * @since 2.0
 */
public class LocalWFSDataSource extends AbstractDataSource {

	private static final ILogger LOG = LoggerFactory.getLogger( LocalWFSDataSource.class );
	
    private final PropertyPath geometryProperty;

	//private final FeatureCollectionAdapter fcAdapter;
	
    /**
     * Creates a new <code>LocalWFSDataSource</code> object from the given parameters.
     * 
     * @param name
     * @param owsCapabilities
     * @param validArea
     * @param minScaleDenominator
     * @param maxScaleDenominator
     * @param geomProperty
     * @param filterCondition a wfs query //TODO give an example
     //* @param adapter the configured adapter which handles the result featurecollections.
     */
    public LocalWFSDataSource( QualifiedName name, OWSCapabilities owsCapabilities, Surface validArea, 
    						   double minScaleDenominator, double maxScaleDenominator, 
                               PropertyPath geomProperty, Filter filterCondition/*, FeatureCollectionAdapter adapter*/ ) {
    	
        super( LOCAL_WFS, name, owsCapabilities, validArea, minScaleDenominator, 
        	   maxScaleDenominator, filterCondition );
        this.geometryProperty = geomProperty;
        //this.fcAdapter = adapter;
    }

    @Override
    public String toString(){
        return super.toString() + '\n' + ' ' + geometryProperty.getAsString();
    }
    
	/**
	 * @return the Filter of the filterCondition.
	 */
	public Filter getFilter() {
		return (Filter)getFilterCondition();
	}

	/**
	 * @return the geometryProperty.
	 */
	public PropertyPath getGeometryProperty() {
		return geometryProperty;
	}

	/** 
     * @see org.deegree.ogcwebservices.wpvs.configuration.AbstractDataSource#getOGCWebService()
     */
    @Override
    protected OGCWebService createOGCWebService() {
        
        try {
            
            WFSConfiguration wfsCapa = null;
            WFSConfigurationDocument wfsDoc = new WFSConfigurationDocument();
            wfsDoc.load( getOwsCapabilities().getOnlineResource() );
            wfsCapa = wfsDoc.getConfiguration();

            return WFServiceFactory.createInstance( wfsCapa );
            
        } catch ( Exception e ) {
        	
        	LOG.logError( "Could not instatiate Datasource: " + toString(), e );
        	return null;
        } 
    }

//	/**
//	 * @return the configured FeatureCollectionAdapter.
//	 */
//	public FeatureCollectionAdapter getFeatureCollectionAdapter() {
//		return fcAdapter;
//	}
    

    /**
     * ---DO NOT REMOVE ---
     * NOT FUNCTIONAL YET, BUT might be if the WFS uses the new OWSCommon Package.
     * 
     * Retrieves (if it exists) the first value of the requestedParameterName of the Operation defined by it's name. For example one wants to get GetFeature#outputFormat 
     * @param operationName the name of the configured Operation
     * @param requestedParameterName the name of the Parameter.
     * @return <tt>null</tt> - in the future: the Value of the (first) parameter if it exists else <tt>null</tt>.
     */
    @SuppressWarnings("unused")
    public String retrieveConfiguredValueForOperationOfNewOWSCommon( String operationName, String requestedParameterName ){
        String result = null;
/*        if( operationName == null || requestedParameterName == null )return null;
        OGCCapabilities ogcCap = getOGCWebService().getCapabilities();
        List<Operation> operations = ((org.deegree.owscommon_new.OWSCommonCapabilities)ogcCap).getOperationsMetadata().getOperations();

        for( Operation operation : operations ){
            if( operationName.equalsIgnoreCase( operation.getName().getLocalName() ) ){
                QualifiedName outputFormatName = new QualifiedName( operation.getName().getPrefix(), requestedParameterName, operation.getName().getNamespace() );
                Parameter para = operation.getParameter( outputFormatName );
                if( para != null ){
                    if( para instanceof DomainType ){
                        List<TypedLiteral> values = ((DomainType)para).getValues();
                        if( values.size() > 0 ){
                            outputFormat = values.get(0).getValue();
                        } else {
                            outputFormat = ((DomainType)para).getDefaultValue().getValue();
                        }
                    }
                }
            }
        }
        */
        return result;

    }
    
    /**
     * Retrieves (if it exists) the first value of the requestedParameterName of the Operation defined by it's name. For example one wants to get GetFeature#outputFormat 
     * @param operationName the name of the configured Operation
     * @param requestedParameterName the name of the Parameter.
     * @return the Value of the (first) parameter if it exists else <tt>null</tt>.
     */
	public String retrieveConfiguredValueForOperation( String operationName, String requestedParameterName ){
        if( operationName == null || requestedParameterName == null )return null;
        OGCCapabilities ogcCap = getOGCWebService().getCapabilities();
        OWSDomainType[] operations = ((org.deegree.owscommon.OWSCommonCapabilities)ogcCap).getOperationsMetadata().getParameter();
        for( OWSDomainType operation : operations ){
            if( operationName.equalsIgnoreCase( operation.getName() ) ){
                String[] values = operation.getValues();
                if( values!= null && values.length > 0 ){
                    return values[0];
                } 
            }
        }
        return null;
    }
    
    /**
     * returns the (first) value of the configured constraint (given by it's name) for this WFSDataSource.
     * @param constraintName the name of the constraint.
     * @return the value of the Constraint or <tt>null</tt> if no such constraint could be found.
     */
    public String retrieveConfiguredConstraintValue( String constraintName ){
        if( constraintName == null )return null;
        OGCCapabilities ogcCap = getOGCWebService().getCapabilities();
        OWSDomainType[] constraints = ((org.deegree.owscommon.OWSCommonCapabilities)ogcCap).getOperationsMetadata().getConstraints();
        for( OWSDomainType constraint : constraints ){
            if( constraintName.equalsIgnoreCase( constraint.getName() ) ){
                String[] values = constraint.getValues();
                if( values!= null && values.length > 0 ){
                    return values[0];
                } 
            }
        }
        return null;
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: LocalWFSDataSource.java,v $
Revision 1.19  2006/11/27 15:41:13  bezema
Updated the coordinatesystem handling and the featurecollection adapter

Revision 1.18  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.17  2006/11/23 11:46:40  bezema
The initial version of the new wpvs

Revision 1.16  2006/07/20 08:13:05  taddei
use of QualiName for geometry property

Revision 1.15  2006/07/05 15:58:23  poth
bug fix - changed Query to Filter for WFS datasources

Revision 1.14  2006/06/20 07:45:21  taddei
datasources use quali names now

Revision 1.13  2006/04/06 20:25:24  poth
*** empty log message ***

Revision 1.12  2006/04/05 08:54:38  taddei
refactoring: fc adapter

Revision 1.10  2006/03/07 08:46:26  taddei
added pts list factory

Revision 1.9  2006/02/22 13:34:00  taddei
refactoring: added service, createOGCWebService; also better except handling

Revision 1.8  2006/01/18 10:21:07  taddei
putting wfs service to work

Revision 1.7  2006/01/18 08:48:27  taddei
added getOGCWebService()

Revision 1.6  2005/12/23 10:36:03  mays
add toString

Revision 1.5  2005/12/06 12:48:19  mays
move param filterCondition from subclasses to AbstractDataSource

Revision 1.4  2005/12/01 15:54:59  mays
omitted serviceType in call of constructor, replacing it in super() by corresponding field

Revision 1.3  2005/12/01 12:09:42  mays
restructuring of data source classes according to xml schema

Revision 1.2  2005/12/01 10:30:14  mays
add standard footer to all java classes in wpvs package

******************************************************************** */
