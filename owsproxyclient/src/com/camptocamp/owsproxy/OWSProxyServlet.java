package com.camptocamp.owsproxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;

public class OWSProxyServlet extends HttpServlet {

    private static final long    serialVersionUID = 1L;

    private ErrorReporter        reporter;
    private boolean              firstTime        = true;
    private String               listenURL;

    private ConnectionParameters connectionParams;
    

    public OWSProxyServlet(ErrorReporter reporter, ConnectionParameters connectionParams) {
        this.reporter = reporter;
        this.connectionParams = connectionParams;
    }

    public void setListenURL(String URL) {
        this.listenURL = URL;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OWSLogger.DEV.fine("server handling: " + Thread.currentThread().getName());
        String serviceEndPoint = connectionParams.server;
        String queryString = request.getQueryString();

        try {
            configureSSH();

            HttpClient client = new HttpClient();
            configureProxy(client);

            if (queryString != null) {

                // TODO: only send for GetCapabilities
                if (this.listenURL != null) {
                    queryString += "&" + "VENDOR_ONLINE_RESOURCE=" + this.listenURL;
                }

                serviceEndPoint += "?" + queryString;
            }

            OWSLogger.DEV.info("Request: " + serviceEndPoint);
            HttpMethod method = new GetMethod(serviceEndPoint);
            // Authentication
            if (connectionParams.username != null && connectionParams.password != null) {
                client.getParams().setAuthenticationPreemptive(true);
                Credentials defaultcreds = new UsernamePasswordCredentials(connectionParams.username,
                        connectionParams.password);
                client.getState().setCredentials(AuthScope.ANY, defaultcreds);
            }
            int statusCode = client.executeMethod(method);

            String header = "Content-Type";
            Header contentTypeHeader = method.getResponseHeader(header);
            if (contentTypeHeader != null) {
                response.setHeader(header, contentTypeHeader.getValue());
            }

            Header[] headers = method.getResponseHeaders();
            for (Header h : headers) {
                // XXX override some headers?
                if (!ieBugCausingHeader(h)) {
                    response.setHeader(h.getName(), h.getValue());
                }
            }

            byte[] cache = new byte[response.getBufferSize()];
            InputStream in = method.getResponseBodyAsStream();
            ServletOutputStream out = response.getOutputStream();
            for (int read = in.read(cache); read > 0; read = in.read(cache)) {
                out.write(cache, 0, read);
            }

            if (statusCode != HttpStatus.SC_OK) {
                handleError(method, statusCode, method.getResponseBody());
                return;
            }

            if (firstTime) {
                firstTime = false;
                reporter.connected();
            }

        } catch (NoKeystoreException e) {
            reporter.reportError(ConnectionStatus.ERROR, "Keystore needs to be correctly configured");
        } catch (Exception e) {
            e.printStackTrace();
            reporter.reportError(ConnectionStatus.ERROR, e.getLocalizedMessage());
        }
    }

    private void configureSSH() throws MalformedURLException {
        
        File keystore;
        if( connectionParams.readonlyKeystore ) {
            keystore = new File(OWSClient.DEFAULT_SECURITY_SETTINGS.keystore);
        } else {
            keystore = new File(connectionParams.keystore);
            if (!keystore.exists() && !keystore.getAbsolutePath().equals(OWSClient.DEFAULT_SECURITY_SETTINGS.keystore)) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            Object[] options = { "Yes", "No" };
                            int result = JOptionPane.showOptionDialog(new JFrame(),
                                    "The defined keystore does not exist\nDo you want to create it?",
                                    "Missing Keystore", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                                    null, options, options[0]);
                            if (result == 1) {
                                throw new RuntimeException("cancel chosen");
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new NoKeystoreException();
                }
            }
        }
        AuthSSLProtocolSocketFactory socketFactory;
            socketFactory = new AuthSSLProtocolSocketFactory(keystore, connectionParams.keystorePass, connectionParams.readonlyKeystore, 
                    connectionParams.noUI, connectionParams.sessionCertificates );
        Protocol.registerProtocol("https", new Protocol("https", socketFactory, 443));
    }

    private boolean ieBugCausingHeader(Header h) {
        if (h.getName().equals("Cache-Control")) {
            return h.getValue().equalsIgnoreCase("no-store") || h.getValue().equalsIgnoreCase("no-cache");
        }

        return false;
    }

    private void handleError(HttpMethod method, int statusCode, byte[] responseBody) throws IOException {
        System.err.println("Method failed: " + method.getStatusLine());
        write(responseBody);

        switch (statusCode) {
        case HttpStatus.SC_UNAUTHORIZED:
            reporter.reportError(ConnectionStatus.UNAUTHORIZED, "Unauthorized: " + method.getStatusLine());
            break;
        case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
            reporter.reportError(ConnectionStatus.PROXY_AUTH_REQUIRED, "Proxy Authentication Failed: "
                    + method.getStatusLine());
            break;

        default:
            reporter.reportError(ConnectionStatus.ERROR, "Method failed: " + method.getStatusLine());
            break;
        }

    }

    private void write(byte[] responseBody) throws IOException {
        if (OWSLogger.DEV.isLoggable(Level.FINEST)) {
            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(responseBody));
            int c = inputStreamReader.read();
            StringBuilder builder = new StringBuilder();
            while (c != -1) {
                builder.append(c);
                c = inputStreamReader.read();
            }
            OWSLogger.DEV.finest("Response from server for the error is:  \n\n" + builder);
        }
    }

    private void configureProxy(HttpClient client) {
        if (connectionParams.proxyHost != null) {
            client.getHostConfiguration().setProxy(connectionParams.proxyHost, connectionParams.proxyPort);
            Credentials defaultcreds = new UsernamePasswordCredentials(connectionParams.proxyUsername,
                    connectionParams.proxyPassword);
            client.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
        }
    }
}
