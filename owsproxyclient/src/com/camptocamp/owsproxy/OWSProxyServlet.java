package com.camptocamp.owsproxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.security.UnrecoverableKeyException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import org.apache.commons.httpclient.contrib.ssl.AuthSSLInitializationError;
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
        OWSLogger.DEV.fine("server handling: " + Thread.currentThread().getName()); //$NON-NLS-1$
        String serviceEndPoint = connectionParams.server;
        String queryString = request.getQueryString();

        try {
            configureSSH();

            HttpClient client = new HttpClient();
            configureProxy(client);

            if (queryString != null) {

                // TODO: only send for GetCapabilities
                if (this.listenURL != null) {
                    queryString += "&" + "VENDOR_ONLINE_RESOURCE=" + this.listenURL; //$NON-NLS-1$ //$NON-NLS-2$
                }

                serviceEndPoint += "?" + queryString; //$NON-NLS-1$
            }

            OWSLogger.DEV.info("Request: " + serviceEndPoint); //$NON-NLS-1$
            HttpMethod method = new GetMethod(serviceEndPoint);
            // Authentication
            if (connectionParams.username != null && connectionParams.password != null) {
                client.getParams().setAuthenticationPreemptive(true);
                Credentials defaultcreds = new UsernamePasswordCredentials(connectionParams.username,
                        connectionParams.password);
                client.getState().setCredentials(AuthScope.ANY, defaultcreds);
            }
            int statusCode = client.executeMethod(method);

            String header = "Content-Type"; //$NON-NLS-1$
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
            reporter.reportError(ConnectionStatus.NO_KEYSTORE, e.getMessage());
        } catch (AuthSSLInitializationError e) {
            if( e.getCause() instanceof UnrecoverableKeyException) {
                reporter.reportError(ConnectionStatus.KEYSTORE_PASSWORD, e.getLocalizedMessage());
            }else {
                reporter.reportError(ConnectionStatus.ERROR, e.getLocalizedMessage());
            }
        }catch (Throwable e) {
            reporter.reportError(ConnectionStatus.ERROR, e.getLocalizedMessage());
        }
    }

    private void configureSSH() throws MalformedURLException {
        
        File keystore = new File(connectionParams.keystore);
            if( connectionParams.readonlyKeystore && !keystore.exists() ) {
                throw new NoKeystoreException("Keystore: "+keystore+" does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (!keystore.exists() && !keystore.getAbsolutePath().equals(OWSClient.DEFAULT_SECURITY_SETTINGS.keystore)) {
                reporter.keystoreMissing(keystore);
        }
        AuthSSLProtocolSocketFactory socketFactory;
            socketFactory = new AuthSSLProtocolSocketFactory(keystore, connectionParams.keystorePass, connectionParams.readonlyKeystore, 
                    reporter, connectionParams.sessionCertificates );
        Protocol.registerProtocol("https", new Protocol("https", socketFactory, 443)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private boolean ieBugCausingHeader(Header h) {
        if (h.getName().equals("Cache-Control")) { //$NON-NLS-1$
            return h.getValue().equalsIgnoreCase("no-store") || h.getValue().equalsIgnoreCase("no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return false;
    }

    private void handleError(HttpMethod method, int statusCode, byte[] responseBody) throws IOException {
        System.err.println("Method failed: " + method.getStatusLine()); //$NON-NLS-1$
        write(responseBody);

        switch (statusCode) {
        case HttpStatus.SC_UNAUTHORIZED:
            reporter.reportError(ConnectionStatus.UNAUTHORIZED, "Unauthorized: " + method.getStatusLine()); //$NON-NLS-1$
            break;
        case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
            reporter.reportError(ConnectionStatus.PROXY_AUTH_REQUIRED, "Proxy Authentication Failed: " //$NON-NLS-1$
                    + method.getStatusLine());
            break;

        default:
            reporter.reportError(ConnectionStatus.ERROR, "Method failed: " + method.getStatusLine()); //$NON-NLS-1$
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
            OWSLogger.DEV.finest("Response from server for the error is:  \n\n" + builder); //$NON-NLS-1$
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
