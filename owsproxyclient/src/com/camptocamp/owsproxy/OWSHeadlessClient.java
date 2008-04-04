package com.camptocamp.owsproxy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.ConnectionParameters;
import com.camptocamp.owsproxy.parameters.DevLogFileParameter;
import com.camptocamp.owsproxy.parameters.DevLogLevelParameter;
import com.camptocamp.owsproxy.parameters.HelpParameter;
import com.camptocamp.owsproxy.parameters.LogConfigurationParameter;
import com.camptocamp.owsproxy.parameters.Parameter;
import com.camptocamp.owsproxy.parameters.ProxyHostParameter;
import com.camptocamp.owsproxy.parameters.ProxyPortParameter;
import com.camptocamp.owsproxy.parameters.ProxyUserParameter;
import com.camptocamp.owsproxy.parameters.UserLogFileParameter;

/**
 * This is the client for running without a graphical user interface
 * 
 * @author jeichar
 */
public class OWSHeadlessClient implements Observer {

	private static final Parameter[] paramaterOptions = new Parameter[] {
			new ProxyHostParameter(), new ProxyPortParameter(),
			new DevLogLevelParameter(), new DevLogFileParameter(),
			new UserLogFileParameter(), new LogConfigurationParameter(),
			new HelpParameter(), new ProxyUserParameter() };

	ConnectionManager connManager;
	ConnectionParameters params = new ConnectionParameters(null,null,null,null,-1,"","");

	public OWSHeadlessClient() {
		connManager = new ConnectionManager();
		connManager.addObserver(this);
	}

	private void run() {
		connManager.connect(params);
	}

	@Override
	public void update(Observable o, Object arg) {

		OWSLogger.DEV.finer(arg.toString());

		if (!(arg instanceof ConnectionEvent))
			return;
		ConnectionEvent connEvent = (ConnectionEvent) arg;

		switch (connEvent.status) {
		case ERROR:
//			System.exit(0);
			break;

		default:

			String pattern = ResourceBundle.getBundle(
					"owsproxyclient/translations").getString("headlessStatus");
			String message = MessageFormat.format(pattern, connEvent.status);
			OWSLogger.USER.info(message);
			break;
		}
	}

	public static void main(String[] args) {
		try {
			OWSHeadlessClient client = parseProgramArgs(args);
			if (OWSLogger.DEV.isLoggable(Level.FINER)) {
				OWSLogger.enableHttpClientDebug();
			}
			client.run();
		} catch (IllegalArgumentException e) {
			usage(e.getMessage());
		}
	}

	static OWSHeadlessClient parseProgramArgs(String... args2)
			throws IllegalArgumentException {
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
						param.performAction("",
								client);
					}
				}
			}
		}

		if (args.size() != 2) {
			throw new IllegalArgumentException(
					"Two or three arguments are required");
		}
		String usernamePassword = args.get(0);
		client.getParams().username = ProxyUserParameter.parseUsername(usernamePassword);
		client.getParams().password = ProxyUserParameter.parsePassword(usernamePassword);

		client.getParams().server = args.get(1);
		client.params.checkConfiguration();
		return client;
	}

	
	private static void usage(String error) {
		String usage = ResourceBundle.getBundle("owsproxyclient/translations")
				.getString("usage");
		OWSLogger.USER.info(MessageFormat.format(usage, error));
	}

	public ConnectionParameters getParams() {
		return params;
	}

}
