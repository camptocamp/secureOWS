// $Header:
// /cvsroot/deegree/src/org/deegree/ogcwebservices/wcs/getcapabilities/WCSRequestValidator.java,v
// 1.6 2004/07/12 11:14:19 ap Exp $
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
package org.deegree.ogcwebservices.wcs.getcapabilities;

import java.net.URI;
import java.net.URL;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.CodeList;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.IGeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.ogcbase.ExceptionCode;
import org.deegree.ogcwebservices.CurrentUpdateSequenceException;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.InvalidUpdateSequenceException;
import org.deegree.ogcwebservices.LonLatEnvelope;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.OGCWebServiceRequest;
import org.deegree.ogcwebservices.SupportedFormats;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.describecoverage.DescribeCoverage;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;

/**
 * @version $Revision: 1.17 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth </a>
 * @author last edited by: $Author: poth $
 * 
 * @version 1.0. $Revision: 1.17 $, $Date: 2006/11/27 09:07:52 $
 * 
 * @since 2.0
 */
public class WCSRequestValidator {
    
    private static final ILogger LOG = LoggerFactory.getLogger( WCSRequestValidator.class );

    /**
     * validates the passed <tt>AbstractOGCWebServiceRequest</tt> which must
     * be a request that is known by a WCS against the passed
     * <tt>WCSCapabilities</tt>
     * 
     * @param capabilities
     * @param request
     * @throws CurrentUpdateSequenceException
     * @throws InvalidUpdateSequenceException
     */
    public static void validate(WCSCapabilities capabilities,
                                    OGCWebServiceRequest request)
                                    throws CurrentUpdateSequenceException,
                                    InvalidUpdateSequenceException, OGCWebServiceException {        
        if ( !request.getVersion().equals(capabilities.getVersion() )) {
            throw new InvalidParameterValueException(request.getVersion() + " is not " +
                    "a valid version for requesting this WCS");
        }
        if (request instanceof WCSGetCapabilities) {
            validate(capabilities, (WCSGetCapabilities) request);
        } else if (request instanceof GetCoverage) {
            validate( capabilities, (GetCoverage)request );
        } else if (request instanceof DescribeCoverage) {
            validate(capabilities, (DescribeCoverage) request);
        } else {
            throw new OGCWebServiceException("Invalid request type: " + request);
        }
    }

    /**
     * validates the passed <tt>WCSGetCapabilities</tt> against the passed
     * <tt>WCSCapabilities</tt>
     * 
     * @param capabilities
     * @param request
     * @throws CurrentUpdateSequenceException
     * @throws InvalidUpdateSequenceException
     */
    private static void validate(WCSCapabilities capabilities,
            WCSGetCapabilities request) throws CurrentUpdateSequenceException,
            InvalidUpdateSequenceException {
        String rUp = request.getUpdateSequence();
        String cUp = capabilities.getUpdateSequence();

        if ((rUp != null) && (cUp != null) && (rUp.compareTo(cUp) == 0)) {
            ExceptionCode code = ExceptionCode.CURRENT_UPDATE_SEQUENCE;
            throw new CurrentUpdateSequenceException("WCS GetCapabilities",
                    "request update sequence: " + rUp
                            + "is equal to capabilities" + " update sequence "
                            + cUp, code);
        }

        if ((rUp != null) && (cUp != null) && (rUp.compareTo(cUp) > 0)) {
            ExceptionCode code = ExceptionCode.INVALID_UPDATESEQUENCE;
            throw new InvalidUpdateSequenceException("WCS GetCapabilities",
                    "request update sequence: " + rUp + " is higher then the "
                            + "capabilities update sequence " + cUp, code);
        }
    }

    /**
     * validates the passed <tt>DescribeCoverage</tt> against the passed
     * <tt>WCSCapabilities</tt>
     * 
     * @param capabilities
     * @param request
     * @throws InvalidParameterValueException
     */
    private static void validate(WCSCapabilities capabilities,
            DescribeCoverage request) throws InvalidParameterValueException {
        String[] coverages = request.getCoverages();
        if (coverages != null) {
            ContentMetadata cm = capabilities.getContentMetadata();
            for (int i = 0; i < coverages.length; i++) {
                if (cm.getCoverageOfferingBrief(coverages[i]) == null) { 
                    throw new InvalidParameterValueException(
                        "Coverage: " + coverages[i] + "is not known by the WCS"); }
            }
        }
    }

    /**
     * validates the passed <tt>GetCoverage</tt> against the passed
     * <tt>WCSCapabilities</tt>
     * 
     * @param capabilities
     * @param request
     * @throws InvalidParameterValueException
     */
    private static void validate(WCSCapabilities capabilities,
            GetCoverage request) throws InvalidParameterValueException {
        String coverage = request.getSourceCoverage();
        ContentMetadata cm = capabilities.getContentMetadata();
        // is coverage known by the WCS?
        CoverageOfferingBrief cob = cm.getCoverageOfferingBrief(coverage);
        if (cob == null) { throw new InvalidParameterValueException(
                "Coverage: " + coverage + " is not known by the WCS"); }

        URL url = cob.getConfiguration();
        CoverageDescription cd = null;
        try {
            cd = CoverageDescription.createCoverageDescription(url);
        } catch (Exception e) {
            LOG.logError( e.getMessage(), e );
            throw new InvalidParameterValueException( e.getMessage() );
        }
        CoverageOffering co = cd.getCoverageOffering(coverage);
        if (co == null ) {
            throw new InvalidParameterValueException("no coverage descrition " +
                    "available for requested coverage: " + coverage);
        }
        // validate requested format
        String format = request.getOutput().getFormat().getCode();
        SupportedFormats sf = co.getSupportedFormats();
        CodeList[] codeList = sf.getFormats();        
        if (!validate(codeList, null, format)) { 
            throw new InvalidParameterValueException( "requested format: " + format
                        + " is not known by the WCS for coverage:" + coverage); }
        // validate requested response CRS
        String crs = request.getOutput().getCrs().getCode();
        URI codeSpace = request.getOutput().getCrs().getCodeSpace();        
        String space = null;
        if ( codeSpace != null ) {
        	space = codeSpace.toString(); 
        }

        CodeList[] rrcrs = co.getSupportedCRSs().getRequestResponseSRSs();
        CodeList[] rescrs = co.getSupportedCRSs().getResponseSRSs();        
        if (!validate(rrcrs, space, crs) && !validate(rescrs, space, crs)) { 
            throw new InvalidParameterValueException(
                "requested response CRS: " + crs
                        + " is not known by the WCS " + "for coverage:"
                        + coverage); }
        // validate requested CRS
        crs = request.getDomainSubset().getRequestSRS().getCode();
        codeSpace = request.getDomainSubset().getRequestSRS().getCodeSpace();
        if ( codeSpace != null ) {
        	space = codeSpace.toString(); 
        }
        CodeList[] reqcrs = co.getSupportedCRSs().getRequestSRSs();
        
        if (!validate(rrcrs, space, crs) && !validate(reqcrs, space, crs)) { 
        	throw new InvalidParameterValueException(
                "requested request CRS: " + crs
                        + " is not known by the WCS for coverage:" + coverage); }
        // validate requested envelope
        Envelope envelope = request.getDomainSubset().getSpatialSubset().getEnvelope();
        LonLatEnvelope llEnv = cob.getLonLatEnvelope();

        try {
            if ( !intersects(envelope, request.getDomainSubset().getRequestSRS(), llEnv) ) { 
            	throw new InvalidParameterValueException(
                    "requested BBOX: doesn't intersect "
                            + " the area of the requested coverage: "
                            + coverage); 
            }
        } catch ( UnknownCRSException e ) {
            throw new InvalidParameterValueException( e );
        }
        
    }

    /**
     * returns true if the passed <tt>CodeList</tt> s contains the also passed
     * codeSpace-value combination. Otherwise false will be returned
     * 
     * @param codeList
     * @param codeSpace
     * @param value
     * @return
     */
    private static boolean validate(CodeList[] codeList, String codeSpace,
            String value) {
        for (int i = 0; i < codeList.length; i++) {            	
            if (codeList[i].validate(codeSpace, value)) { return true; }            
        }
        return false;
    }

    private static boolean intersects(Envelope envelope, Code reqCRS,
            						  LonLatEnvelope llEnv) throws UnknownCRSException {
        Envelope latlonEnv = 
        	GeometryFactory.createEnvelope(llEnv.getMin().getX(), 
        								   llEnv.getMin().getY(), 
										   llEnv.getMax().getX(), 
										   llEnv.getMax().getY(),
                                           CRSFactory.create("EPSG:4326") );
        try {
            if ( !"EPSG:4326".equals( reqCRS.getCode() ) ) {
                IGeoTransformer gt = new GeoTransformer("EPSG:4326");
                String crs = reqCRS.getCode();
                envelope = gt.transform(envelope, crs );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return envelope.intersects(latlonEnv);
    }

}/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: WCSRequestValidator.java,v $
Revision 1.17  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.16  2006/11/23 09:23:43  bezema
added a EPSG:4326 to the longlatenvellope

Revision 1.15  2006/09/27 16:46:41  poth
transformation method signature changed

Revision 1.14  2006/08/07 13:37:59  poth
bug fix - checking for intersection between requested box and a coverages envelope

Revision 1.13  2006/07/28 08:01:27  schmitz
Updated the WMS for 1.1.1 compliance.
Fixed some documentation.

Revision 1.12  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
