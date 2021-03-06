<?xml version="1.0" encoding="UTF-8"?>
<!--  ======================================================================================

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

@version $Revision$
@author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
@author last edited by: $Author$

@version 1.0. $Revision$, $Date$
                 
====================================================================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:param name="KEEPANNOTATION">false</xsl:param>
	<xsl:template match="xsd:schema">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="@*|node()">
		<xsl:choose>
			<xsl:when test="$KEEPANNOTATION = 'true'">
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="local-name(.) != 'annotation' ">
					<xsl:copy>
						<xsl:apply-templates select="@*|node()"/>
					</xsl:copy>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
<!-- ==================================================================================
Changes to this class. What the people have been up to:
$Log$
Revision 1.3  2006/08/29 19:54:14  poth
footer corrected

Revision 1.2  2006/08/06 19:49:08  poth
file header and footer added


 ====================================================================================== -->