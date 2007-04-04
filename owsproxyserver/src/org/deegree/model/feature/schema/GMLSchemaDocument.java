//$Header: /home/deegree/jail/deegreerepository/deegree/src/org/deegree/model/feature/schema/GMLSchemaDocument.java,v 1.11 2006/11/27 09:07:53 poth Exp $
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
package org.deegree.model.feature.schema;

import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.ComplexTypeDeclaration;
import org.deegree.framework.xml.schema.ElementDeclaration;
import org.deegree.framework.xml.schema.SimpleTypeDeclaration;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.framework.xml.schema.XSDocument;
import org.deegree.model.crs.UnknownCRSException;

/**
 * Parser for GML Application Schema documents.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author <a href="mailto:deshmukh@lat-lon.de">Anup Deshmukh </a>
 * @author last edited by: $Author: poth $
 * 
 * @version $Revision: 1.11 $, $Date: 2006/11/27 09:07:53 $
 */
public class GMLSchemaDocument extends XSDocument {

    private static final long serialVersionUID = 7298930438304830877L;

    /**
     * Returns the class representation of the underlying GML Schema document.
     * 
     * @return class representation of the underlying GML Schema document
     * @throws XMLParsingException
     * @throws XMLSchemaException
     * @throws UnknownCRSException 
     */
    public GMLSchema parseGMLSchema() throws XMLParsingException, XMLSchemaException, UnknownCRSException {
        SimpleTypeDeclaration[] simpleTypes = extractSimpleTypeDeclarations();
        ComplexTypeDeclaration[] complexTypes = extractComplexTypeDeclarations();
        ElementDeclaration[] elementDeclarations = extractElementDeclarations();
        return new GMLSchema( getTargetNamespace(), simpleTypes, complexTypes, elementDeclarations );
    }
}

/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: GMLSchemaDocument.java,v $
Revision 1.11  2006/11/27 09:07:53  poth
JNI integration of proj4 has been removed. The CRS functionality now will be done by native deegree code.

Revision 1.10  2006/08/29 19:54:14  poth
footer corrected

Revision 1.9  2006/08/29 15:55:05  mschneider
Javadoc fixes.

Revision 1.8  2006/08/24 06:40:27  poth
File header corrected

Revision 1.7  2006/08/22 18:14:42  mschneider
Refactored due to cleanup of org.deegree.io.datastore.schema package.

Revision 1.6  2006/07/12 14:46:14  poth
comment footer added

********************************************************************** */
