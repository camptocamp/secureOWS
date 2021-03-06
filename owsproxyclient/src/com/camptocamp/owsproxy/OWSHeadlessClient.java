package com.camptocamp.owsproxy;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.camptocamp.owsproxy.ConnectionEvent.ConnectionStatus;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;
import com.camptocamp.owsproxy.parameters.DevLogFileParameter;
import com.camptocamp.owsproxy.parameters.DevLogLevelParameter;
import com.camptocamp.owsproxy.parameters.HelpParameter;
import com.camptocamp.owsproxy.parameters.KeystoreParameter;
import com.camptocamp.owsproxy.parameters.LogConfigurationParameter;
import com.camptocamp.owsproxy.parameters.Parameter;
import com.camptocamp.owsproxy.parameters.ProxyHostParameter;
import com.camptocamp.owsproxy.parameters.ProxyPortParameter;
import com.camptocamp.owsproxy.parameters.ProxyUserParameter;
import com.camptocamp.owsproxy.parameters.UserLogFileParameter;

/**
 * This is the client for running without a graphical user interface.
 * 
 * @author jeichar
 */
public class OWSHeadlessClient implements Observer {

	private static final Parameter[] paramaterOptions = new Parameter[] {
			new ProxyHostParameter(), new ProxyPortParameter(),
			new DevLogLevelParameter(), new DevLogFileParameter(),
			new UserLogFileParameter(), new LogConfigurationParameter(),
			new HelpParameter(), new ProxyUserParameter(), new KeystoreParameter() };

	private Collection<X509Certificate> sessionCertificates = new HashSet<X509Certificate>();

	ConnectionManager connManager;
	ConnectionParameters params = new ConnectionParameters(null, null, null,
			null, -1, "", "", OWSClient.DEFAULT_SECURITY_SETTINGS.keystore, "", false, sessionCertificates); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private ConnectionStatus state = ConnectionStatus.IDLE;
	
	public OWSHeadlessClient() {
		connManager = new ConnectionManager();
		connManager.addObserver(this);
	}

	private void run() {
		connManager.connect(params);
	}

	public void update(Observable o, Object arg) {

		OWSLogger.DEV.fine(arg.toString());

		if (!(arg instanceof ConnectionEvent))
			return;
		ConnectionEvent connEvent = (ConnectionEvent) arg;
		switch (connEvent.status) {
        case ERROR: {
            OWSLogger.USER.warning(Translations.getString("OWSHeadlessClient.Error", //$NON-NLS-1$
                    connEvent.message));
            if( state == ConnectionStatus.CONNECTING) {
                System.exit(0);
            }
            break;
        }      
        case KEYSTORE_PASSWORD: {
            OWSLogger.USER.severe(Translations.getString("OWSHeadlessClient.Error", //$NON-NLS-1$
                    connEvent.message));
            if( state == ConnectionStatus.CONNECTING) {
                System.exit(0);
            }
            break;
        }
        case NO_KEYSTORE: {
            OWSLogger.USER.severe(Translations.getString("OWSHeadlessClient.Error", //$NON-NLS-1$
                    connEvent.message));
            if( state == ConnectionStatus.CONNECTING) {
                System.exit(0);
            }
            break;
        }
		case UNAUTHORIZED: {
			OWSLogger.USER
					.severe(Translations.getString("OWSHeadlessClient.Unauthorized")); //$NON-NLS-1$
			System.exit(0);
			break;
		}
		case PROXY_AUTH_REQUIRED: {
			OWSLogger.USER
					.severe(Translations.getString("OWSHeadlessClient.Prox_Unauth")); //$NON-NLS-1$
			System.exit(0);
			break;
		}
		case RUNNING: {
			OWSLogger.USER.info(Translations.getString("HeadlessRunning", connManager.getListeningAddress())); //$NON-NLS-1$
			break;
		}
		default: {
			String message = Translations.getString("OWSHeadlessClient.headlessStatus", connEvent.status); //$NON-NLS-1$
			OWSLogger.USER.info(message);
			break;
		}
		}
		state = connEvent.status;
	}

	public static void main(String[] args) {
		try {
			OWSHeadlessClient client = parseProgramArgs(args);
			if (OWSLogger.DEV.isLoggable(Level.FINEST)) {
				OWSLogger.enableHttpClientDebug();
			}
			client.run();
		} catch (IllegalArgumentException e) {
			usage(e.getMessage());
		}
	}

	static OWSHeadlessClient parseProgramArgs(String... args2)
			throws IllegalArgumentException {
		if( args2.length==0){
			throw new IllegalArgumentException(""); //$NON-NLS-1$
		}
		
		OWSHeadlessClient client = new OWSHeadlessClient();

		List<String> args = new ArrayList<String>(Arrays.asList(args2));

		for (ListIterator<String> iter = args.listIterator(); iter.hasNext();) {
			String arg = iter.next();
			for (Parameter param : paramaterOptions) {
				if (param.match(arg)) {
					iter.remove();
					if (iter.hasNext()) {
						boolean argUsed = param.performAction(iter.next(),
								client);
						if (argUsed) {
							iter.remove();
						} else {
							iter.previous();
						}
					} else {
						param.performAction("", client); //$NON-NLS-1$
					}
				}
			}
		}

		if (args.size() != 2) {
			throw new IllegalArgumentException(
					Translations.getString("OWSHeadlessClient.wrongParams")); //$NON-NLS-1$
		}
		String usernamePassword = args.get(0);
		client.getParams().username = ProxyUserParameter
				.parseUsername(usernamePassword);
		client.getParams().password = ProxyUserParameter
				.parsePassword(usernamePassword);

		client.getParams().server = args.get(1);
		client.params.checkConfiguration();
		return client;
	}

	private static void usage(String error) {
		String usage = Translations.getString("OWSHeadlessClient.usage", error); //$NON-NLS-1$
		OWSLogger.USER.info(usage);
	}

	public ConnectionParameters getParams() {
		return params;
	}

}
