/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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
package org.deegree.ogcwebservices.wps.describeprocess;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Code;
import org.deegree.ogcwebservices.MetadataType;
import org.deegree.ogcwebservices.wps.ProcessBrief;

/**
 * 
 * ProcessDescription.java
 * 
 * Created on 09.03.2006. 22:39:07h
 * 
 * Full description of a process.
 * 
 * @author <a href="mailto:christian@kiehle.org">Christian Kiehle</a>
 * @author <a href="mailto:christian.heier@gmx.de">Christian Heier</a>
 * @version 1.0.
 * @since 2.0
 */
public class ProcessDescription extends ProcessBrief {

	/**
	 * 
	 * @param resonsibleClass
	 * @param identifier
	 * @param title
	 * @param _abstract
	 * @param processVersion
	 * @param metadata
	 * @param inputs
	 * @param outputs
	 * @param statusSupported
	 * @param storeSupported
	 */
	public ProcessDescription( String resonsibleClass, Code identifier, String title,
			String _abstract, String processVersion, List<MetadataType> metadata,
			DataInputs inputs, ProcessOutputs outputs, Boolean statusSupported,
			Boolean storeSupported ) {
		super( identifier, title, _abstract, processVersion, metadata );
		this.responsibleClass = resonsibleClass;
		this.dataInputs = inputs;
		this.processOutputs = outputs;
		this.statusSupported = statusSupported;
		this.storeSupported = storeSupported;
	}

	protected String responsibleClass;

	/**
	 * List of the inputs to this process. In almost all cases, at least one
	 * process input is required. However, no process inputs may be identified
	 * when all the inputs are predetermined fixed resources. In this case,
	 * those resources shall be identified in the ows:Abstract element that
	 * describes the process
	 */
	protected DataInputs dataInputs;

	/**
	 * List of outputs which will or can result from executing the process.
	 */
	protected ProcessOutputs processOutputs;

	/**
	 * Indicates if the Execute operation response can be returned quickly with
	 * status information, or will not be returned until process execution is
	 * complete. If "statusSupported" is "true", the Execute operation request
	 * may include "status" equals "true", directing that the Execute operation
	 * response be returned quickly with status information. By default, status
	 * information is not provided for this process, and the Execute operation
	 * response is not returned until process execution is complete.
	 */
	protected Boolean statusSupported;

	/**
	 * Indicates if the ComplexData outputs from this process can be stored by
	 * the WPS server as web-accessible resources. If "storeSupported" is
	 * "true", the Execute operation request may include "store" equals "true",
	 * directing that all ComplexData outputs of the process be stored so that
	 * the client can retrieve them as required. By default for this process,
	 * storage is not supported and all outputs are returned encoded in the
	 * Execute response.
	 */
	protected Boolean storeSupported;

	/**
	 * @return Returns the dataInputs.
	 */
	public DataInputs getDataInputs() {
		return dataInputs;
	}

	/**
	 * @param dataInputs
	 *            The dataInputs to set.
	 */
	public void setDataInputs( DataInputs value ) {
		this.dataInputs = value;
	}

	/**
	 * @return Returns the processOutputs.
	 */
	public ProcessOutputs getProcessOutputs() {
		return processOutputs;
	}

	/**
	 * @param processOutputs
	 *            The processOutputs to set.
	 */
	public void setProcessOutputs( ProcessOutputs value ) {
		this.processOutputs = value;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isStatusSupported() {
		return statusSupported != null;
	}

	/**
	 * @param statusSupported
	 *            The statusSupported to set.
	 */
	public void setStatusSupported( Boolean value ) {
		this.statusSupported = value;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isStoreSupported() {
		return storeSupported != null;
	}

	/**
	 * @param storeSupported
	 *            The storeSupported to set.
	 */
	public void setStoreSupported( Boolean value ) {
		this.storeSupported = value;
	}

	public static class DataInputs {

		/**
		 * 
		 * 
		 * Unordered list of one or more descriptions of the inputs that can be
		 * accepted by this process, including all required and optional inputs.
		 * Where an input is optional because a default value exists, that
		 * default value must be identified in the "ows:Abstract" element for
		 * that input, except in the case of LiteralData, where the default must
		 * be indicated in the corresponding ows:DefaultValue element. Where an
		 * input is optional because it depends on the value(s) of other inputs,
		 * this must be indicated in the ows:Abstract element for that input.
		 * 
		 * 
		 */
		private List<InputDescription> inputDescriptions;

		/**
		 * @return Returns the input.
		 */
		public List<InputDescription> getInputDescriptions() {
			if ( inputDescriptions == null ) {
				inputDescriptions = new ArrayList<InputDescription>();
			}
			return this.inputDescriptions;
		}

		public void setInputDescriptions( List<InputDescription> inputDescriptions ) {
			this.inputDescriptions = inputDescriptions;
		}

	}

	public static class ProcessOutputs {

		/**
		 * Unordered list of one or more descriptions of all the outputs that
		 * can result from executing this process. At least one output is
		 * required from each process.
		 */
		protected List<OutputDescription> output;

		/**
		 * @return Returns the output.
		 */
		public List<OutputDescription> getOutput() {
			if ( output == null ) {
				output = new ArrayList<OutputDescription>();
			}
			return this.output;
		}

	}

	/**
	 * @return Returns the responsibleClass.
	 */
	public String getResponsibleClass() {
		return responsibleClass;
	}

}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ProcessDescription.java,v $
Revision 1.4  2006/11/27 09:07:52  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.3  2006/08/24 06:42:16  poth
File header corrected

Revision 1.2  2006/07/12 14:46:15  poth
comment footer added

********************************************************************** */
