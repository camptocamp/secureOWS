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

@version $Revision: 1.16 $
@author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
@author last edited by: $Author: schmitz $

@version 1.0. $Revision: 1.16 $, $Date: 2006/10/27 09:52:23 $
                 
====================================================================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:deegree="http://www.deegree.org/wms" xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:variable name="DEFAULTONLINERESOURCE">
        <xsl:value-of select="/WMT_MS_Capabilities/deegree:DeegreeParam/deegree:DefaultOnlineResource/@xlink:href"/>
    </xsl:variable>
    <xsl:variable name="VERSION">
        <xsl:choose>
            <xsl:when test="WMT_MS_Capabilities/@version">
                <xsl:value-of select="WMT_MS_Capabilities/@version"/>
            </xsl:when>
            <xsl:otherwise>1.1.1</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:template match="WMT_MS_Capabilities">
        <WMT_MS_Capabilities xmlns:deegree="http://www.deegree.org/wms" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" xmlns:wfs="http://www.opengis.net/wfs" xmlns:ogc="http://www.opengis.net/ogc">
            <xsl:attribute name="version"><xsl:value-of select="$VERSION"/></xsl:attribute>
            <xsl:if test="./@updateSequence">
                <xsl:attribute name="updateSequence"><xsl:value-of select="./@updateSequence"/></xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="deegree:DeegreeParam"/>
            <xsl:choose>
                <xsl:when test="Service">
                    <xsl:apply-templates select="Service"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="Service"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="Capability"/>
        </WMT_MS_Capabilities>
    </xsl:template>
    <xsl:template match="Service" name="Service">
        <Service>
            <xsl:choose>
                <xsl:when test="Name">
                    <xsl:copy-of select="Name"/>
                </xsl:when>
                <xsl:otherwise>
                    <Name>deegree2 WMS</Name>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="Title">
                    <xsl:copy-of select="Title"/>
                </xsl:when>
                <xsl:otherwise>
                    <Title>deegree2 WMS</Title>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="OnlineResource">
                    <xsl:copy-of select="OnlineResource"/>
                </xsl:when>
                <xsl:otherwise>
                    <OnlineResource xlink:type="simple">
                        <xsl:attribute name="version"><xsl:value-of select="$DEFAULTONLINERESOURCE"/></xsl:attribute>
                    </OnlineResource>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="Abstract"/>
            <xsl:copy-of select="KeywordList"/>
            <xsl:copy-of select="ContactInformation"/>
        </Service>
    </xsl:template>
    <xsl:template match="Capability">
        <Capability>
            <xsl:choose>
                <xsl:when test="./Request">
                    <xsl:apply-templates select="Request"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="Request"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="Exception">
                    <xsl:copy-of select="Exception"/>
                </xsl:when>
                <xsl:otherwise>
                    <Exception>
                        <Format>application/vnd.ogc.se_xml</Format>
                        <Format>application/vnd.ogc.se_inimage</Format>
                        <Format>application/vnd.ogc.se_blank</Format>
                    </Exception>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="UserDefinedSymbolization">
                    <xsl:copy-of select="UserDefinedSymbolization"/>
                </xsl:when>
                <xsl:otherwise>
                    <UserDefinedSymbolization SupportSLD="0" UserLayer="0" UserStyle="0" RemoteWFS="0"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="Layer"/>
        </Capability>
    </xsl:template>
    <xsl:template match="Request" name="Request">
        <Request>
            <GetCapabilities>
                <Format>application/vnd.ogc.wms_xml</Format>
                <DCPType>
                    <HTTP>
                        <Get>
                            <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                <xsl:attribute name="xlink:href"><xsl:value-of select="./GetCapabilities/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                            </OnlineResource>
                        </Get>
                        <xsl:copy-of select="./GetCapabilities/DCPType/HTTP/Post"/>
                    </HTTP>
                </DCPType>
            </GetCapabilities>
            <GetMap>
                <xsl:choose>
                    <xsl:when test="./GetMap/Format">
                        <xsl:copy-of select="./GetMap/Format"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <Format>image/png</Format>
                        <Format>image/jpeg</Format>
                    </xsl:otherwise>
                </xsl:choose>
                <DCPType>
                    <HTTP>
                        <Get>
                            <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                <xsl:attribute name="xlink:href"><xsl:value-of select="./GetMap/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                            </OnlineResource>
                        </Get>
                        <xsl:copy-of select="./GetMap/DCPType/HTTP/Post"/>
                    </HTTP>
                </DCPType>
            </GetMap>
            <xsl:if test="./GetFeatureInfo">
                <GetFeatureInfo>
                    <xsl:choose>
                        <xsl:when test="./GetFeatureInfo/Format">
                            <xsl:copy-of select="./GetFeatureInfo/Format"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <Format>application/vnd.ogc.gml</Format>
                            <Format>text/html</Format>
                        </xsl:otherwise>
                    </xsl:choose>
                    <DCPType>
                        <HTTP>
                            <Get>
                                <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                    <xsl:attribute name="xlink:href"><xsl:value-of select="./GetFeatureInfo/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                                </OnlineResource>
                            </Get>
                            <xsl:copy-of select="./GetFeatureInfo/DCPType/HTTP/Post"/>
                        </HTTP>
                    </DCPType>
                </GetFeatureInfo>
            </xsl:if>
            <xsl:if test="./GetLegendGraphic">
                <GetLegendGraphic>
                    <xsl:choose>
                        <xsl:when test="./GetLegendGraphic/Format">
                            <xsl:copy-of select="./GetLegendGraphic/Format"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <Format>image/png</Format>
                            <Format>image/jpeg</Format>
                        </xsl:otherwise>
                    </xsl:choose>
                    <DCPType>
                        <HTTP>
                            <Get>
                                <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                    <xsl:attribute name="xlink:href"><xsl:value-of select="./GetLegendGraphic/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                                </OnlineResource>
                            </Get>
                            <xsl:copy-of select="./GetLegendGraphic/DCPType/HTTP/Post"/>
                        </HTTP>
                    </DCPType>
                </GetLegendGraphic>
            </xsl:if>
            <xsl:if test="./GetStyles">
                <GetStyles>
                    <Format>application/vnd.ogc.wms_xml</Format>
                    <DCPType>
                        <HTTP>
                            <Get>
                                <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                    <xsl:attribute name="xlink:href"><xsl:value-of select="./GetStyles/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                                </OnlineResource>
                            </Get>
                            <xsl:copy-of select="./GetStyles/DCPType/HTTP/Post"/>
                        </HTTP>
                    </DCPType>
                </GetStyles>
            </xsl:if>
            <xsl:if test="./PutStyles">
                <PutStyles>
                    <Format>application/vnd.ogc.wms_xml</Format>
                    <DCPType>
                        <HTTP>
                            <Get>
                                <OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple">
                                    <xsl:attribute name="xlink:href"><xsl:value-of select="./PutStyles/DCPType/HTTP/Get/OnlineResource/@xlink:href"/></xsl:attribute>
                                </OnlineResource>
                            </Get>
                            <xsl:copy-of select="./PutStyles/DCPType/HTTP/Post"/>
                        </HTTP>
                    </DCPType>
                </PutStyles>
            </xsl:if>
        </Request>
    </xsl:template>
    <xsl:template match="Layer">
        <Layer>
            <xsl:if test="./@queryable">
                <xsl:attribute name="queryable"><xsl:value-of select="./@queryable"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./@cascaded">
                <xsl:attribute name="cascaded"><xsl:value-of select="./@cascaded"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./@opaque">
                <xsl:attribute name="opaque"><xsl:value-of select="./@opaque"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./@noSubsets">
                <xsl:attribute name="noSubsets"><xsl:value-of select="./@noSubsets"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./@fixedWidth">
                <xsl:attribute name="fixedWidth"><xsl:value-of select="./@fixedWidth"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="./@fixedHeight">
                <xsl:attribute name="fixedHeight"><xsl:value-of select="./@fixedHeight"/></xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="Name"/>
            <xsl:copy-of select="Title"/>
            <xsl:copy-of select="Abstract"/>
            <xsl:copy-of select="KeywordList"/>
            <xsl:copy-of select="ScaleHint"/>
            <xsl:choose>
                <xsl:when test="./SRS">
                    <xsl:copy-of select="./SRS"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- use EPSG:4326 as default reference system -->
                    <SRS>EPSG:4326</SRS>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="./LatLonBoundingBox">
                    <xsl:copy-of select="./LatLonBoundingBox"/>
                </xsl:when>
                <!--xsl:otherwise>
                    <xsl:if test="../LatLonBoundingBox = '' ">
                        <LatLonBoundingBox minx="-180" miny="-90" maxx="180" maxy="90"/>
                    </xsl:if>
                </xsl:otherwise-->
            </xsl:choose>
            <xsl:copy-of select="BoundingBox"/>
            <xsl:copy-of select="Dimension"/>
            <xsl:copy-of select="Extent"/>
            <xsl:copy-of select="Attribution"/>
            <xsl:copy-of select="AuthorityURL"/>
            <xsl:copy-of select="Identifier"/>
            <xsl:copy-of select="MetadataURL"/>
            <xsl:copy-of select="DataURL"/>
            <xsl:copy-of select="FeatureListURL"/>
            <xsl:if test="Name">
            	<xsl:if test="deegree:DataSource">
            		<!-- just a layer having a requestable name can have a style -->
            		<xsl:choose>
            			<xsl:when test="Style">
            				<xsl:for-each select="Style">
            					<xsl:call-template name="Style">
            						<xsl:with-param name="pStyle"
            							select="." />
            					</xsl:call-template>
            				</xsl:for-each>
            			</xsl:when>
            			<xsl:otherwise>
            				<xsl:call-template name="Style">
            					<xsl:with-param name="pStyle"
            						select="/WMT_MS_Capabilities" />
            				</xsl:call-template>
            			</xsl:otherwise>
            		</xsl:choose>
            	</xsl:if>
            </xsl:if>
            <xsl:apply-templates select="Layer"/>
            <xsl:if test="Name">
                <!-- just a layer having a requestable name can have a datasource -->
                <xsl:choose>
                    <xsl:when test="deegree:DataSource">
                        <xsl:for-each select="deegree:DataSource">
                            <xsl:call-template name="datasource">
                                <xsl:with-param name="pDs" select="."/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:when>
<!--                      <xsl:otherwise>
                         <xsl:call-template name="datasource">
                            <xsl:with-param name="pDs" select="."/>
                        </xsl:call-template>
                    </xsl:otherwise>-->
                </xsl:choose>
            </xsl:if>
        </Layer>
    </xsl:template>
    <xsl:template match="Style" name="Style">
        <xsl:param name="pStyle"/>
        <xsl:variable name="STYLENAME">
            <xsl:choose>
                <xsl:when test="$pStyle/Name">
                    <xsl:copy-of select="$pStyle/Name"/>
                </xsl:when>
                <xsl:otherwise>
                    <Name>default:<xsl:value-of select="./Name"/>
                    </Name>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <Style>
            <Name>
                <xsl:value-of select="$STYLENAME"/>
            </Name>
            <xsl:choose>
                <xsl:when test="$pStyle/Title">
                    <xsl:copy-of select="$pStyle/Title"/>
                </xsl:when>
                <xsl:otherwise>
                    <Title>
                        <xsl:value-of select="$STYLENAME"/>
                    </Title>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="$pStyle/Abstract"/>
            <xsl:choose>
                <xsl:when test="$pStyle/LegendURL">
                    <xsl:copy-of select="$pStyle/LegendURL"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- use a GetLegendGraphic request as default if no LegendURL has been defined -->
                    <LegendURL width="10" height="10">
                        <Format>image/gif</Format>
                        <OnlineResource xlink:type="simple">
                        	<xsl:choose>
                        		<xsl:when test="boolean(./../Name)">
                            		<xsl:attribute name="xlink:href"><xsl:value-of select="$DEFAULTONLINERESOURCE"/>?request=GetLegendGraphic&amp;version=<xsl:value-of select="$VERSION"/>&amp;width=25&amp;height=25&amp;format=image/jpeg&amp;layer=<xsl:value-of select="./../Name"/>&amp;style=<xsl:value-of select="$STYLENAME"/></xsl:attribute>
                           		</xsl:when>
                           		<xsl:otherwise>
                           			<xsl:attribute name="xlink:href"><xsl:value-of select="$DEFAULTONLINERESOURCE"/>?request=GetLegendGraphic&amp;version=<xsl:value-of select="$VERSION"/>&amp;width=25&amp;height=25&amp;format=image/jpeg&amp;layer=<xsl:value-of select="./Name"/>&amp;style=<xsl:value-of select="$STYLENAME"/></xsl:attribute>
                           		</xsl:otherwise>
                            </xsl:choose>
                        </OnlineResource>
                    </LegendURL>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="$pStyle/StyleSheetURL"/>
            <xsl:copy-of select="$pStyle/StyleURL"/>
            <xsl:copy-of select="$pStyle/deegree:StyleResource"/>
        </Style>
    </xsl:template>
    <xsl:template match="deegree:DataSource" name="datasource">
        <xsl:param name="pDs"/>
        <deegree:DataSource>
            <xsl:if test="$pDs/@queryable">
                <xsl:attribute name="queryable"><xsl:value-of select="$pDs/@queryable"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="$pDs/@failOnException">
                <xsl:attribute name="failOnException"><xsl:value-of select="$pDs/@failOnException"/></xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$pDs/deegree:Name">
                    <xsl:copy-of select="$pDs/deegree:Name"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- use the layers name as default name for the datasource -->
                    <deegree:Name>
                        <xsl:value-of select="./Name"/>
                    </deegree:Name>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$pDs/deegree:Type">
                    <xsl:copy-of select="$pDs/deegree:Type"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- use LOCALWFS as default datasource type -->
                    <deegree:Type>LOCALWFS</deegree:Type>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="$pDs/deegree:GeometryProperty"/>            
            <xsl:choose>
                <xsl:when test="$pDs/deegree:OWSCapabilities">
                    <xsl:copy-of select="$pDs/deegree:OWSCapabilities"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="$pDs/deegree:Type = 'LOCALWCS' ">
                            <!-- if datasource type is LOCALWCS use reference to LOCALWCS_capabilities.xml 
                                   as default -->
                            <deegree:OWSCapabilities>
                                <deegree:OnlineResource xlink:type="simple" xlink:href="LOCALWCS_capabilities.xml"/>
                            </deegree:OWSCapabilities>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- use reference to LOCALWFS_capabilities.xml -->
                            <deegree:OWSCapabilities>
                                <deegree:OnlineResource xlink:type="simple" xlink:href="LOCALWFS_capabilities.xml"/>
                            </deegree:OWSCapabilities>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$pDs/deegree:FilterCondition">
                <xsl:copy-of select="$pDs/deegree:FilterCondition"/>
            </xsl:if>
            <xsl:if test="$pDs/deegree:Type = 'LOCALWCS' or $pDs/deegree:Type = 'REMOTEWCS' ">
                <xsl:copy-of select="$pDs/deegree:TransparentColors"/>
            </xsl:if>
            <xsl:if test="$pDs/deegree:Type = 'REMOTEWMS' ">
                <xsl:copy-of select="$pDs/deegree:TransparentColors"/>
            </xsl:if>
            <xsl:copy-of select="$pDs/deegree:ScaleHint"/>
            <xsl:copy-of select="$pDs/deegree:FeatureInfoTransformation"/>
            <xsl:copy-of select="$pDs/deegree:ValidArea"/>
            <xsl:copy-of select="$pDs/deegree:RequestTimeLimit"/>
        </deegree:DataSource>
    </xsl:template>
</xsl:stylesheet>
<!-- ==================================================================================
Changes to this class. What the people have been up to:
$Log: WMSConfigurationTransform.xsl,v $
Revision 1.16  2006/10/27 09:52:23  schmitz
Brought the WMS up to date regarding 1.1.1 and 1.3.0 conformance.
Fixed a bug while creating the default GetLegendGraphics URLs.

Revision 1.15  2006/10/22 20:32:08  poth
support for vendor specific operation GetScaleBar removed

Revision 1.14  2006/10/20 16:28:19  poth
bug fix - creating default LegendURL

Revision 1.13  2006/10/10 13:46:02  schmitz
Updated the WMS GetMap and GetFeatureInfo requests to handle sublayers of layers as well.

Revision 1.12  2006/08/06 19:51:45  poth
bug fix - reading datastore timelimit from configuration


 ====================================================================================== -->