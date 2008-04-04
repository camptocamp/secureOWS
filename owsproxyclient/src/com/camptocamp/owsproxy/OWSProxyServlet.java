package com.camptocamp.owsproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;

public class OWSProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ErrorReporter reporter;
	private boolean firstTime = true;
	private String listenURL;

	private ConnectionParameters connectionParams;

	public OWSProxyServlet(ErrorReporter reporter,
			ConnectionParameters connectionParams) {
		this.reporter = reporter;
		this.connectionParams = connectionParams;
	}

	public void setListenURL(String URL) {
		this.listenURL = URL;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		OWSLogger.DEV.info("server handling: "
				+ Thread.currentThread().getName());

		String serviceEndPoint = connectionParams.server;
		String queryString = request.getQueryString();

		try {
			// XXX what about non self-signed certificates?
			Protocol.registerProtocol("https", new Protocol("https",
					new EasySSLProtocolSocketFactory(), 443));

			HttpClient client = new HttpClient();
			configureProxy(client);

			if (queryString != null) {

				// TODO: only send for GetCapabilities
				if (this.listenURL != null) {
					queryString += "&" + "VENDOR_ONLINE_RESOURCE="
							+ this.listenURL;
				}

				serviceEndPoint += "?" + queryString;
			}

			OWSLogger.DEV.info("End point: " + serviceEndPoint);
			HttpMethod method = new GetMethod(serviceEndPoint);
			// Authentication
			if (connectionParams.username != null
					&& connectionParams.password != null) {
				client.getParams().setAuthenticationPreemptive(true);
				Credentials defaultcreds = new UsernamePasswordCredentials(
						connectionParams.username, connectionParams.password);
				client.getState().setCredentials(AuthScope.ANY, defaultcreds);
			}

			int statusCode = client.executeMethod(method);

			String header = "Content-Type";
			Header contentTypeHeader = method.getResponseHeader(header);
			if (contentTypeHeader != null) {
				response.setHeader(header, contentTypeHeader.getValue());
			}

			// XXX WARNING: Going to buffer response body of large or unknown
			// size. Using getResponseBodyAsStream instead is recommended.
			byte[] responseBody = method.getResponseBody();

			response.getOutputStream().write(responseBody);
			Header[] headers = method.getResponseHeaders();
			for (Header h : headers) {
				// XXX override some headers?
				response.setHeader(h.getName(), h.getValue());
			}

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				write(responseBody);
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

	private void write(byte[] responseBody) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(
				new ByteArrayInputStream(responseBody));
		int c = inputStreamReader.read();
		StringBuilder builder = new StringBuilder();
		while (c != -1) {
			builder.append(c);
			c = inputStreamReader.read();
		}
		OWSLogger.DEV.severe("Response from server for the error is:  \n\n"
				+ builder);
	}

	private void configureProxy(HttpClient client) {
		if (connectionParams.proxyHost != null) {
			client.getHostConfiguration().setProxy(connectionParams.proxyHost,
					connectionParams.proxyPort);
			Credentials defaultcreds = new UsernamePasswordCredentials(
					connectionParams.proxyUsername,
					connectionParams.proxyPassword);
			client.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
		}
	}
}
