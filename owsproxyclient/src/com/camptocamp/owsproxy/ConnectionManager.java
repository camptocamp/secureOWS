package com.camptocamp.owsproxy;

import java.io.IOException;
import java.net.BindException;
import java.util.Observable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;


public class ConnectionManager extends Observable implements ErrorReporter {
	
	private Server server;
	private String listeningAddress;

    // XXX should use config option
    static final int STARTING_PORT = 8888;
    static final String LISTENING_URL = "/";
    // This is only used to test the credentials.
    static final String LISTENING_HOST = "http://localhost:"; 
    
	void connect(ConnectionParameters connectionParams) {
		
        OWSProxyServlet servlet = new OWSProxyServlet(this, connectionParams.copy());

		if (server != null) {
			disconnect();
		}
		
		setChanged();
		notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.CONNECTING));
		
		int port = STARTING_PORT;
		server = new Server(port);

		try {
		    while (true) {
		    	try {
					server.start();
					break;
		    	} catch (BindException be) {
					OWSLogger.DEV.info("Port " + port + " already bound, trying next");
					port += 1;
					server = new Server(port);
				}
			}
		} catch( Exception e) {
			error(e);
			return;
		}

        Context context = new Context(server, LISTENING_URL, Context.SESSIONS);
        context.addServlet(new ServletHolder(servlet), "/*");
        
        OWSLogger.DEV.info("port is " + port);
	    new Thread(new Runnable() {
			public void run() {
		    	try {
					server.join();
				} catch (InterruptedException e) {
					error(e);
				}
			}
	    }).start();
	    
	    OWSLogger.DEV.finer("server starting thread: " + Thread.currentThread().getName());
		
		listeningAddress = LISTENING_HOST + port + LISTENING_URL;
		servlet.setListenURL(listeningAddress);

	    // sleep a while to give time for servlet servet to start up
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { }

        // Creates a dummy request, to check if credentials are correct
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(listeningAddress);
	    try {
            int statusCode = client.executeMethod(get);
            OWSLogger.DEV.finer("status: " + statusCode);
            
	    } catch (IOException e) {
			error(e);
			return;
		}
	    get.releaseConnection();
	}
	
	void error(Throwable e) {
		setChanged();
		notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.ERROR, 
		                "Error during connection", e));
	}
    
    void fireIdleEvent() {
        
        setChanged();
        notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.IDLE, 
                        "Server idle"));
    }
    
	void disconnect() {
	    fireIdleEvent();
        
		if (server == null)
			return;
		try {
			server.stop();
		} catch (Exception e) {
			reportError(ConnectionStatus.ERROR, e.toString());
			e.printStackTrace();
		}
		server = null;
	}

	public void reportError(ConnectionStatus status, String error) {
		setChanged();
		notifyObservers(new ConnectionEvent(status, "Connection error " + error));
	}

	public String getListeningAddress() {
		return listeningAddress;
	}

    public void connected() {
        setChanged();
        notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.RUNNING, 
                "Server listening on " + listeningAddress));
    }
}