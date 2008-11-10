package com.camptocamp.owsproxy;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.Observable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import owsproxyclient.ExamineCertPanel.AddCert;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;


public class ConnectionManager extends Observable implements ErrorReporter {
	
	private Server server;
	private String listeningAddress;

    // XXX should use config option
    static final int STARTING_PORT = 8888;
    static final String LISTENING_URL = "/"; //$NON-NLS-1$
    // This is only used to test the credentials.
    static final String LISTENING_HOST = "http://localhost:";  //$NON-NLS-1$
    
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
					OWSLogger.DEV.info("Port " + port + " already bound, trying next"); //$NON-NLS-1$ //$NON-NLS-2$
					port += 1;
					server = new Server(port);
				}
			}
		} catch( Exception e) {
			error(e);
			return;
		}

        Context context = new Context(server, LISTENING_URL, Context.SESSIONS);
        context.addServlet(new ServletHolder(servlet), "/*"); //$NON-NLS-1$
        
        OWSLogger.DEV.info("port is " + port); //$NON-NLS-1$
	    new Thread(new Runnable() {
			public void run() {
		    	try {
					server.join();
				} catch (InterruptedException e) {
					error(e);
				}
			}
	    }).start();
	    
	    OWSLogger.DEV.finer("server starting thread: " + Thread.currentThread().getName()); //$NON-NLS-1$
		
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
            OWSLogger.DEV.finer("status: " + statusCode); //$NON-NLS-1$
            
	    } catch (IOException e) {
			error(e);
			return;
		}
	    get.releaseConnection();
	}
	
	void error(Throwable e) {
		setChanged();
		notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.ERROR, 
		                Translations.getString("ConnectionManager.connectionError"), e)); //$NON-NLS-1$
	}
    
    void fireIdleEvent() {
        
        setChanged();
        notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.IDLE, 
                        Translations.getString("ConnectionManager.IdleServer"))); //$NON-NLS-1$
    }
    
	void disconnect() {
	    fireIdleEvent();
        
		if (server == null)
			return;
		try {
			server.stop();
		} catch (Exception e) {
			reportError(ConnectionStatus.ERROR, e.toString());
		}
		server = null;
	}

	public void reportError(ConnectionStatus status, String error) {
		setChanged();
		notifyObservers(new ConnectionEvent(status, Translations.getString("ConnectionManager.ConnectionError") + error)); //$NON-NLS-1$
	}

	public String getListeningAddress() {
		return listeningAddress;
	}

    public void connected() {
        setChanged();
        notifyObservers(new ConnectionEvent(ConnectionEvent.ConnectionStatus.RUNNING, 
                "Server listening on " + listeningAddress)); //$NON-NLS-1$
    }

    public AddCert certificateValidationFailure(boolean readonlyKeystore, String errorMessage, String certificateInformation) {
        return AddCert.NEVER;
    }

    public void keystoreMissing(File keystore) {
        throw new NoKeystoreException("keystore: "+keystore+" is missing XXX"); //$NON-NLS-2$
    }
}