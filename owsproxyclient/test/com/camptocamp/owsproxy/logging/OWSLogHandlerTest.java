package com.camptocamp.owsproxy.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import junit.framework.TestCase;

import com.camptocamp.owsproxy.logging.OWSLogHandler;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class OWSLogHandlerTest extends TestCase {

	private OWSLogHandler handler;
	private ByteOutputStream user;
	private ByteOutputStream dev;
	private ByteOutputStream userConsole;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		user = new ByteOutputStream();
		userConsole = new ByteOutputStream();
		dev = new ByteOutputStream();
		handler = new OWSLogHandler(user, userConsole, dev);
		assertTrue( new String(user.getBytes()).trim().length()==0 );
		assertTrue( new String(userConsole.getBytes()).trim().length()==0 );
		assertTrue( new String(dev.getBytes()).trim().length()==0 );
	}
	
	public void testPublishLogRecordUSER() {
		String message = "message";
		LogRecord record = new LogRecord(Level.INFO, message );
		OWSLogger logger = OWSLogger.USER;
		record.setLoggerName(logger.logger().getName());
		handler.publish(record);

		assertTrue( new String(user.getBytes()).trim().length()>0 );
		assertTrue( new String(userConsole.getBytes()).trim().length()>0 );
		assertTrue( new String(dev.getBytes()).trim().length()>0 );
	}

	public void testPublishLogRecordDEV() {
		String message = "message";
		LogRecord record = new LogRecord(Level.INFO, message );
		OWSLogger logger = OWSLogger.DEV;
		record.setLoggerName(logger.logger().getName());
		handler.publish(record);		
		
		assertTrue( new String(user.getBytes()).trim().length()==0 );
		assertTrue( new String(userConsole.getBytes()).trim().length()==0 );
		assertTrue( new String(dev.getBytes()).trim().length()>0 );
	}
	public void testPublishLogRecordOTHER() {
		String message = "message";
		LogRecord record = new LogRecord(Level.INFO, message );
		record.setLoggerName("com.camptocamp.some.other.logger");
		handler.publish(record);	
		
		assertTrue( new String(user.getBytes()).trim().length()==0 );
		assertTrue( new String(userConsole.getBytes()).trim().length()==0 );
		assertTrue( new String(dev.getBytes()).trim().length()>0 );
	}

}
