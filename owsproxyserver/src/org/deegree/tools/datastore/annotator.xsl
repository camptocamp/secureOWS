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
<xsl:stylesheet version="2.0" xmlns:deegreewfs="http://www.deegree.org/wfs" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:java="java" xmlns:annotator="org.deegree.tools.datastore.SchemaAnnotator">
	<xsl:output encoding="utf-8"/>
	<xsl:template match="xs:schema">
		<xs:schema>
		    <xsl:attribute name="targetNamespace"><xsl:value-of select="@targetNamespace"/></xsl:attribute>
			<xsl:apply-templates mode="featureType"/>
		</xs:schema>
	</xsl:template>
	<xsl:template match="@*|node()" mode="featureType">
		<xsl:choose>
			<xsl:when test="local-name(.) = 'complexType'">
				<xsl:variable name="TYPE" select="./@name"/>
				<xsl:variable name="TYPE_NAME" select="concat( 'bplan:', ./@name)"/>
				<xsl:variable name="FEATURE_TYPE_NAME" select="/xs:schema/xs:element[@type = $TYPE_NAME]/@name"/>
				<xsl:variable name="TABLE_NAME" select="annotator:getTableName($FEATURE_TYPE_NAME)"/>
				<xs:complexType name="{@name}">
					<xs:annotation>
						<xs:appinfo>
							<deegreewfs:table>
								<xsl:value-of select="$TABLE_NAME"/>
							</deegreewfs:table>
							<deegreewfs:gmlId>
								<xsl:attribute name="prefix"><xsl:value-of select="$TABLE_NAME"/>_</xsl:attribute>
								<deegreewfs:MappingField field="fid" type="INTEGER"/>
							</deegreewfs:gmlId>
						</xs:appinfo>
					</xs:annotation>
					<xsl:apply-templates select="xs:complexContent" mode="property">
						<xsl:with-param name="FEATURE_TYPE_NAME" select="$FEATURE_TYPE_NAME"/>
					</xsl:apply-templates>
				</xs:complexType>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy copy-namespaces="no" exclude-result-prefixes="yes" inherit-namespaces="no" copy-type-annotations="no">
					<xsl:apply-templates select="@*|node()" mode="featureType"/>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="@*|node()" mode="property">
		<xsl:param name="FEATURE_TYPE_NAME" select="."/> 
		<!-- typeName from each complexType -->
		<xsl:choose>
			<xsl:when test="local-name(.) = 'element'">
				<xsl:variable name="FIELD_NAME" select="@name"/>
				<xsl:variable name="COLUMN_NAME" select="annotator:getColumnName($FIELD_NAME)"/>
				<xs:element>
					<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
					<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="@minOccurs">
							<xsl:attribute name="minOccurs"><xsl:value-of select="@minOccurs"/></xsl:attribute>
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="@maxOccurs">
							<xsl:attribute name="maxOccurs"><xsl:value-of select="@maxOccurs"/></xsl:attribute>
						</xsl:when>
					</xsl:choose>
					<xs:annotation>
						<xs:appinfo>
							<xsl:variable name="typBefore">
								<xsl:value-of select="substring-before(@type,':')"/>
							</xsl:variable>
							<xsl:variable name="typAfter">
								<xsl:value-of select="substring-after(@type,':')"/>
							</xsl:variable>
							<xsl:variable name="getFeature">
								<xsl:value-of select="substring-before(@type,':')"/>
							</xsl:variable>
							<deegreewfs:Content>
								<deegreewfs:MappingField>
									<xsl:attribute name="field"><xsl:value-of select="$COLUMN_NAME"/></xsl:attribute>
									<xsl:choose>
										<xsl:when test="@type='xs:string'">
											<xsl:attribute name="type">VARCHAR</xsl:attribute>
										</xsl:when>
										<xsl:when test="@type='xs:decimal'">
											<xsl:attribute name="type">DECIMAL</xsl:attribute>
										</xsl:when>
										<xsl:when test="@type='xs:double'">
											<xsl:attribute name="type">DECIMAL</xsl:attribute>
										</xsl:when>
										<xsl:when test="@type='xs:date'">
											<xsl:attribute name="type">DATE</xsl:attribute>
										</xsl:when>
										<xsl:when test="@type='xs:boolean'">
											<xsl:attribute name="type">BOOLEAN</xsl:attribute>
										</xsl:when>
										<xsl:when test="@type='xs:integer'">
											<xsl:attribute name="type">INTEGER</xsl:attribute>
										</xsl:when>
										<xsl:when test="$typBefore='gml'">
											<xsl:attribute name="type">GEOMETRY</xsl:attribute>
										</xsl:when>
										<xsl:when test="$typBefore='bplan'">
											<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
										</xsl:when>
									</xsl:choose>
								</deegreewfs:MappingField>
							</deegreewfs:Content>
							<xsl:if test="starts-with($typAfter,'Feature')">
								<deegreewfs:Content type="$FEATURE_TYPE_NAME">
									<deegreewfs:Relation>
										<deegreewfs:From>
											<deegreewfs:MappingField>
												<xsl:attribute name="field"><xsl:value-of select="$COLUMN_NAME"/></xsl:attribute>
												<xsl:attribute name="type">VARCHAR</xsl:attribute>											
											</deegreewfs:MappingField> 
										</deegreewfs:From>
										<deegreewfs:To>
											<deegreewfs:MappingField field="fid" type="VARCHAR"/>
										</deegreewfs:To>
									</deegreewfs:Relation>
								</deegreewfs:Content>
								<xsl:attribute name="type">FEATURE</xsl:attribute>
							</xsl:if>
							<xsl:if test="starts-with($typAfter,'Abstract')">
								<deegreewfs:Content type="$FEATURE_TYPE_NAME">
									<deegreewfs:Relation>
										<deegreewfs:From>
											<deegreewfs:MappingField>
												<xsl:attribute name="field"><xsl:value-of select="$COLUMN_NAME"/></xsl:attribute>
												<xsl:attribute name="type">VARCHAR</xsl:attribute>											
											</deegreewfs:MappingField> 
										</deegreewfs:From>
										<deegreewfs:To>
											<deegreewfs:MappingField field="fid" type="VARCHAR"/>
										</deegreewfs:To>
									</deegreewfs:Relation>
								</deegreewfs:Content>
								<xsl:attribute name="type">FEATURE</xsl:attribute>
							</xsl:if>
						</xs:appinfo>
					</xs:annotation>
				</xs:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy copy-namespaces="no" exclude-result-prefixes="yes" inherit-namespaces="no" copy-type-annotations="no">
					<xsl:apply-templates select="@*|node()" mode="property"/>
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
<!-- ==================================================================================
Changes to this class. What the people have been up to:
$Log$
Revision 1.5  2006/11/23 15:24:42  mschneider
Added copying of targetNamespace-attribute.

Revision 1.4  2006/08/29 19:54:14  poth
footer corrected

Revision 1.3  2006/08/06 19:53:18  poth
file header and footer added


 ====================================================================================== -->