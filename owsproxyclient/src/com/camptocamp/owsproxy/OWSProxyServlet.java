package com.camptocamp.owsproxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;


public class OWSProxyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String server;
    private String username;
    private String password;
    private ErrorReporter reporter;
    private boolean firstTime = true;
    
    public OWSProxyServlet(ErrorReporter reporter, String server, String username, String password) {
        this.reporter = reporter;
        this.server = server;
        this.username = username;
        this.password = password;
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        System.out.println("server handling: " + Thread.currentThread().getName());
        
        String serviceEndPoint = server;
        String queryString = request.getQueryString();
        
        try {
            // XXX what about non self-signed certificates?
            Protocol.registerProtocol("https", 
                    new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
            
            HttpClient client = new HttpClient();
            if (queryString != null) {
                serviceEndPoint += "?" + queryString;
            }
            System.out.println("End point: " + serviceEndPoint);
            HttpMethod method = new GetMethod(serviceEndPoint);
            
            // Authentication
            if (username != null && password != null) {
                client.getParams().setAuthenticationPreemptive(true);
                Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
                client.getState().setCredentials(AuthScope.ANY, defaultcreds);
            }

            int statusCode = client.executeMethod(method);

            String header = "Content-Type";
            Header contentTypeHeader = method.getResponseHeader(header);
            if (contentTypeHeader != null) {
                response.setHeader(header, contentTypeHeader.getValue());
            }

            // XXX WARNING: Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.
            byte[] responseBody = method.getResponseBody();

            response.getOutputStream().write(responseBody);
            Header[] headers = method.getResponseHeaders();
            for (Header h : headers) {
                // XXX override some headers?
                response.setHeader(h.getName(), h.getValue());
            }

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
                if (statusCode == HttpStatus.SC_UNAUTHORIZED)
                    reporter.reportError(ConnectionStatus.UNAUTHORIZED,
                            "Unauthorized: " + method.getStatusLine());
                else
                    reporter.reportError(ConnectionStatus.ERROR,
                            "Method failed: " + method.getStatusLine());
                return;
            }

            if (firstTime) {
                firstTime = false;
                reporter.connected();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            reporter.reportError(ConnectionStatus.ERROR, e.toString());
        }
    }
}
