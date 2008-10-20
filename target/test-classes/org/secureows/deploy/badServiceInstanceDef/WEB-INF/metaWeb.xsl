<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- XSL stylesheet used to generate web.xml from the service configuration file -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  >
<!--  for debugging  -->
  <xsl:output method="xml"/> 

  <xsl:template match="/">
    <web-app id="owsproxy" version="2.4" xmlns="http://java.sun.com/xml/ns/j2ee" 
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
     xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">

	  <display-name>owsproxyserver</display-name>  
  
      <xsl:apply-templates select="//service"/>

	    <login-config>
	      <auth-method>BASIC</auth-method>
	      <realm-name>Ows Proxy Realm</realm-name>
	    </login-config>
    </web-app>
  </xsl:template>

  <xsl:template match="role">
  	<role-name><xsl:value-of select="."/></role-name>
  </xsl:template>

  <xsl:template match="service">
    
	<filter>
		<filter-name>OWSProxyServletFilter_<xsl:value-of select="@serviceId"/></filter-name>
		<filter-class>
			org.deegree.security.owsproxy.OWSProxyServletFilter
		</filter-class>
		<init-param>
			<param-name>WMS:POLICY</param-name>
			<param-value>/WEB-INF/<xsl:value-of select="@directory"/>/wmsPolicy_<xsl:value-of select="@serviceId"/>.xml</param-value>
		</init-param>
		<init-param>
			<param-name>ALTREQUESTPAGE</param-name>
			<param-value>/accessDenied.html</param-value>
		</init-param>
	</filter>
    
    <!--  TODO OTHERS  -->
	
	<filter-mapping>
		<filter-name>OWSProxyServletFilter_<xsl:value-of select="@serviceId"/></filter-name>
		<servlet-name><xsl:value-of select="@serviceId"/></servlet-name>
	</filter-mapping>
 
    <!--  XXX This could be global if no parameter are used -->
	<filter>
	   <filter-name>OWSLoggerFilter_<xsl:value-of select="@serviceId"/></filter-name>
	   <filter-class>com.camptocamp.owsproxy.OWSLoggerFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>OWSLoggerFilter_<xsl:value-of select="@serviceId"/></filter-name>
		<servlet-name><xsl:value-of select="@serviceId"/></servlet-name>
	</filter-mapping>

	<servlet>
		<description></description>
		<display-name><xsl:value-of select="@serviceDescription"/></display-name>
		<servlet-name><xsl:value-of select="@serviceId"/></servlet-name>
		<servlet-class>com.camptocamp.owsproxy.OWSProxyServlet</servlet-class>

		<init-param>
			<param-name>serviceEndPoint</param-name>
			<param-value><xsl:value-of select="@serviceEndPoint"/></param-value>
		</init-param>
	</servlet>

	<servlet-mapping>
		<servlet-name><xsl:value-of select="@serviceId"/></servlet-name>
		<url-pattern>/<xsl:value-of select="@serviceId"/></url-pattern>
	</servlet-mapping>

    <security-constraint>
      <display-name>Ows Proxy Security Constraint</display-name>

      <web-resource-collection>
         <web-resource-name>Protected Area</web-resource-name>
         
         <url-pattern>/<xsl:value-of select="@serviceId"/></url-pattern>

		 <!-- If you list http methods, only those methods are protected -->
		 <http-method>DELETE</http-method>
         <http-method>GET</http-method>
         <http-method>POST</http-method>
		 <http-method>PUT</http-method>
      </web-resource-collection>

      <auth-constraint>
         <!-- Anyone with one of the listed roles may access this area -->
		 <xsl:apply-templates select="./roles/*"/>
      </auth-constraint>
    </security-constraint>	

  </xsl:template>

</xsl:stylesheet>
