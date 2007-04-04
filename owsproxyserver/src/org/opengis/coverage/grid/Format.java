/*$************************************************************************************************
 **
 ** $Id: Format.java,v 1.2 2006/07/13 06:28:31 poth Exp $
 **
 ** $Source: /home/deegree/jail/deegreerepository/deegree/src/org/opengis/coverage/grid/Attic/Format.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.opengis.coverage.grid;

// OpenGIS dependencies
import org.opengis.parameter.GeneralOperationParameter;


/**
 * A discovery mechanism to determine the formats supported by a {@link GridCoverageExchange}
 * implementation. A <code>GridCoverageExchange</code> implementation can support a number of
 * file format or resources.
 *
 * @UML abstract CV_Format
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 */
public interface Format {
    /**
     * Name of the file format.
     *
     * @return the name of the file format.
     * @UML mandatory name
     */
    String getName();

    /**
     * Description of the file format.
     * If no description, the value will be <code>null</code>.
     *
     * @return the description of the file format.
     * @UML optional description
     */
    String getDescription();

    /**
     * Vendor or agency for the format.
     *
     * @return the vendor or agency for the format.
     * @UML optional vendor
     */
    String getVendor();

    /**
     * Documentation URL for the format.
     *
     * @return the documentation URL for the format.
     * @UML optional docURL
     */
    String getDocURL();

    /**
     * Version number of the format.
     *
     * @return the version number of the format.
     * @UML optional version
     */
    String getVersion();

    /**
     * Retrieve the parameter information for a {@link GridCoverageReader#read read} operation.
     *
     * @UML operation getParameterInfo
     * @UML mandatory numParameters
     */
    GeneralOperationParameter[] getReadParameters();

    /**
     * Retrieve the parameter information for a {@link GridCoverageWriter#write write} operation.
     */
    GeneralOperationParameter[] getWriteParameters();
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: Format.java,v $
Revision 1.2  2006/07/13 06:28:31  poth
comment footer added

********************************************************************** */
