package com.camptocamp.owsproxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.ogcwebservices.wms.operation.GetFeatureInfo;
import org.deegree.security.owsproxy.OWSProxyServletFilter;

/**
 * Implementation class for Proxy OWS Servlet
 * 
 */
public class OWSProxyServlet extends javax.servlet.http.HttpServlet implements
        javax.servlet.Servlet {

    private String SERVICE_END_POINT;

    @Override
    public void init() throws ServletException {
        super.init();
        SERVICE_END_POINT = getInitParameter("serviceEndPoint");
        if (SERVICE_END_POINT == null)
            throw new ServletException("You need to define the serviceEndPoint Parameter in the web.xml file");

    }

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public OWSProxyServlet() {
        super();
    }

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String queryString = request.getQueryString();
        
        
        // Some WMS Client, like ArcMAP, only send the QUERY_LAYERS parameter without a LAYERS parameter.
        // However, some WMS server like MapServer fail in case there is no LAYERS parameter send.
        // Thus, we add a LAYERS parameter in case only a QUERY_LAYERS parameter is available.
        // -> Keep this in sync with the code in org.deegree.ogcwebservices.wms.operation.GetFeatureInfo::create() 
        if (request.getParameter("QUERY_LAYERS") != null && request.getParameter("LAYERS") == null) {
            queryString += "&LAYERS=" + request.getParameter("QUERY_LAYERS"); 
        }

        String endPoint = "" + SERVICE_END_POINT;

        try {
            HttpClient client = new HttpClient();
            if (queryString != null) {
                if (endPoint.indexOf("?") < 0)
                    endPoint += "?";
                endPoint += queryString;
            }
            System.out.println("End point: " + endPoint);
            HttpMethod method = new GetMethod(endPoint);

            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            String header = "Content-Type";
            Header contentTypeHeader = method.getResponseHeader(header);
            if (contentTypeHeader != null) {
                response.setContentType(contentTypeHeader.getValue());
            }

            byte[] responseBody = method.getResponseBody();

            response.getOutputStream().write(responseBody);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }
}