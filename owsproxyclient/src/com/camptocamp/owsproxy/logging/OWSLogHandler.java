/**
 * 
 */
package com.camptocamp.owsproxy.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A log handler that writes USER log messages to the standard out and a file.
 * @author jeichar
 */
public class OWSLogHandler extends Handler {
	
	/**
	 * This class just allows OWSLogHandler to change the OutputStream of the parent StreamHandler
	 * 
	 * @author jeichar
	 */
	private static class OWSStreamHandler extends StreamHandler{
		public OWSStreamHandler(OutputStream user, OWSLogFormatter logFormatter) {
			super( user, logFormatter );
		}

		@Override
		protected synchronized void setOutputStream(OutputStream out)
				throws SecurityException {
			super.setOutputStream(out);
		}
	}

	private final OWSStreamHandler userlogStream;
	private final OWSStreamHandler devlogStream;
	private OWSStreamHandler userlogConsole;
	// the next two are for testing.  
	private String devLogFile;
	private String userLogFile;
	
	public OWSLogHandler() {
		this( createLogWriter(OWSLogger.USER), System.out,
		createLogWriter(OWSLogger.DEV));
	}

	
	OWSLogHandler(OutputStream user, OutputStream  userConsole, OutputStream  dev) {
		this.userlogStream=new OWSStreamHandler(user, new OWSLogFormatter());
		this.userlogConsole=new OWSStreamHandler(userConsole, new OWSLogFormatter());
		this.devlogStream=new OWSStreamHandler(dev, new OWSLogFormatter());;
	}

	private static OutputStream createLogWriter(OWSLogger logger) {
		String logFile = System.getProperty(logger.systemPropertyKey());
		if( logFile==null ){
			ResourceBundle bundle = ResourceBundle.getBundle("owsproxyclient/translations"); //$NON-NLS-1$
			String pattern = bundle.getString("defaultLog"); //$NON-NLS-1$
			logFile=MessageFormat.format(pattern, logger.name() );
		}
		try {
			return new FileOutputStream(logFile);
		} catch (IOException e) {
			return System.err;
		}
	}

	@Override
	public void close() throws SecurityException {
		userlogStream.close();
		userlogConsole.close();
		devlogStream.close();
	}

	@Override
	public void flush() {
		userlogStream.flush();
		userlogConsole.flush();
		devlogStream.flush();
	}

	@Override
	public void publish(LogRecord record) {
		OWSLogger logger = OWSLogger.lookup(record.getLoggerName());
		if( logger==null ){
			logger=OWSLogger.DEV;
		}
		setLevel(logger.logger().getLevel());
		switch (logger) {
		case USER:
			userlogStream.publish(record);
			userlogConsole.publish(record);
			break;

		default:
			break;
		}
		devlogStream.publish(record);
		flush();
	}
	
	@Override
	public void setEncoding(String encoding) throws SecurityException,
			UnsupportedEncodingException {
		super.setEncoding(encoding);
		userlogConsole.setEncoding(encoding);
		userlogStream.setEncoding(encoding);
		devlogStream.setEncoding(encoding);
	}
	
	@Override
	public void setFilter(Filter newFilter) throws SecurityException {
		super.setFilter(newFilter);
		userlogConsole.setFilter(newFilter);
		userlogStream.setFilter(newFilter);
		devlogStream.setFilter(newFilter);
	}
	
	@Override
	public void setErrorManager(ErrorManager em) {
		super.setErrorManager(em);
		userlogConsole.setErrorManager(em);
		userlogStream.setErrorManager(em);
		devlogStream.setErrorManager(em);
	}
	
	@Override
	public void setFormatter(Formatter newFormatter) throws SecurityException {
		super.setFormatter(newFormatter);
		userlogConsole.setFormatter(newFormatter);
		userlogStream.setFormatter(newFormatter);
		devlogStream.setFormatter(newFormatter);
	}
	
	@Override
	public synchronized void setLevel(Level newLevel) throws SecurityException {
		super.setLevel(newLevel);
		userlogConsole.setLevel(newLevel);
		userlogStream.setLevel(newLevel);
		devlogStream.setLevel(newLevel);
	}


	public String getDevLogFile() {
		return devLogFile;
	}

	public void setDevLogFile(String devLogFile) throws FileNotFoundException {
		this.devLogFile = devLogFile;
		devlogStream.setOutputStream(new FileOutputStream(devLogFile));
	}

	public String getUserLogFile() {
		return userLogFile;
	}

	public void setUserLogFile(String userLogFile) throws FileNotFoundException {
		this.userLogFile = userLogFile;
		userlogStream.setOutputStream(new FileOutputStream(userLogFile));
	}

	

}
